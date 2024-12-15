package kr.co.proten.llmops.api.document.service.impl;

import kr.co.proten.llmops.api.document.dto.DocumentDTO;
import kr.co.proten.llmops.api.document.dto.MetadataDTO;
import kr.co.proten.llmops.api.document.dto.ProcessingResult;
import kr.co.proten.llmops.api.document.entity.Document;
import kr.co.proten.llmops.api.document.entity.Metadata;
import kr.co.proten.llmops.api.document.repository.opensearch.OpenSearchDocumentRepository;
import kr.co.proten.llmops.api.document.service.strategy.embedding.EmbeddingProcessor;
import kr.co.proten.llmops.api.document.service.strategy.chunk.ChunkProcessor;
import kr.co.proten.llmops.api.document.service.DocumentService;
import kr.co.proten.llmops.api.document.service.factory.ChunkProcessorRegistry;
import kr.co.proten.llmops.api.document.service.factory.ChunkProcessorFactory;
import kr.co.proten.llmops.api.document.service.factory.EmbeddingProcessorFactory;
import kr.co.proten.llmops.api.document.util.DocumentBuilder;
import kr.co.proten.llmops.core.helpers.FileValidator;
import kr.co.proten.llmops.core.helpers.TextExtractor;
import kr.co.proten.llmops.api.document.service.storage.FileStorageService;
import kr.co.proten.llmops.core.exception.FileStorageException;
import kr.co.proten.llmops.core.exception.UnsupportedModelException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static kr.co.proten.llmops.core.helpers.FileUtil.getExtension;
import static kr.co.proten.llmops.core.helpers.MappingLoader.convertToMap;


@Service
public class DocumentServiceImpl implements DocumentService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final long PARALLEL_THRESHOLD;

    private final FileValidator fileValidator;
    private final FileStorageService fileStorageService;
    private final TextExtractor textExtractor;
    private final ChunkProcessorFactory chunkProcessorFactory;
    private final EmbeddingProcessorFactory embeddingProcessorFactory;
    private final ChunkProcessorRegistry chunkProcessorRegistry;
    private final OpenSearchDocumentRepository openSearchDocumentRepository;

    @Value("${file.chunk.size:10}")
    private long fileChunkSize;

    @Value("${file.upload.path:D:/llmops/uploads}")
    private String uploadPath;

    @Value("${file.save.path:D:/llmops/saves}")
    private String savePath;

    @Autowired
    public DocumentServiceImpl(FileValidator fileValidator, FileStorageService fileStorageService, TextExtractor textExtractor, ChunkProcessorFactory chunkProcessorFactory, EmbeddingProcessorFactory embeddingProcessorFactory, ChunkProcessorRegistry chunkProcessorRegistry, OpenSearchDocumentRepository openSearchDocumentRepository) {
        this.fileValidator = fileValidator;
        this.fileStorageService = fileStorageService;
        this.textExtractor = textExtractor;
        this.chunkProcessorFactory = chunkProcessorFactory;
        this.embeddingProcessorFactory = embeddingProcessorFactory;
        this.chunkProcessorRegistry = chunkProcessorRegistry;
        this.openSearchDocumentRepository = openSearchDocumentRepository;
        PARALLEL_THRESHOLD = 1024 * 1024 * fileChunkSize; // filChunkSize는 MB단위
    }

    @Override
    public Map<String, Object> uploadFile(MultipartFile file) throws Exception{
        Map<String, Object> result = new HashMap<>();
        try {
            // 파일 검증
            fileValidator.validate(file);

            // 파일 저장
            File savedFile = fileStorageService.saveFile(file, uploadPath);

            // 텍스트 추출
            String extractedText = textExtractor.extractText(savedFile, savePath);

            // 결과 반환
            result.put("status", "success");
            result.put("message", "파일 업로드 및 텍스트 추출 성공");
            result.put("response", extractedText);
        } catch (IOException e) {
            throw new FileStorageException("파일 저장 중 오류가 발생했습니다.", e);
        }
        return result;
    }

    @Override
    public Map<String, Object> uploadDocument(String indexName, String fileName, int chunkSize, int overlapSize, String modelType, List<String> processingKeys) {
        Map<String, Object> result = new HashMap<>();

        String fileType = getExtension(fileName);

        // 1. 청크 서비스 선택
        ChunkProcessor chunkProcessor = chunkProcessorFactory.getChunkService(fileType)
                .orElseThrow(() -> new UnsupportedOperationException("지원하지 않는 파일 타입: " + fileType));

        // 2. 임베딩 서비스 선택
        EmbeddingProcessor embeddingProcessor = embeddingProcessorFactory.getEmbeddingService(modelType)
                .orElseThrow(() -> new UnsupportedOperationException("지원하지 않는 임베딩 형식: " + modelType));

        ProcessingResult processingResult = null;
        try {
            // 3. 파일 내용 읽기
            log.info("파일 읽기 시작: {}", fileName);
            String fileContent = chunkProcessor.readFileContent(savePath + "/" + fileName);

            // 4. 사용자 요청에 따른 전처리 함수 가져오기
            log.info("파일 전처리 시작: {}", processingKeys);
            List<Function<String, String>> processingFunctions = chunkProcessorRegistry.getProcessors(processingKeys);

            // 5. 파일 전처리 수행 (파일 크기에 따라 병렬/단일 처리 결정)
            log.info("파일 전처리 시작 \n 파일 크기: {}", fileContent.length());
            Stream<String> stream = (fileContent.length() > PARALLEL_THRESHOLD)
                    ? Stream.of(fileContent).parallel()
                    : Stream.of(fileContent);

            for (Function<String, String> function : processingFunctions) {
                stream = stream.map(function); // 각 전처리 함수를 병렬/단일 스트림에 적용
            }
            fileContent = stream.collect(Collectors.joining("\n"));

            // 6. 청크 생성
            log.info("파일 청크 생성 시작 \n 청크 크기: {}, 오버랩 크기: {}", chunkSize, overlapSize);
            List<String> chunks = chunkProcessor.createChunks(fileContent, chunkSize, overlapSize);
            log.info("{} 파일이 {}로 청크됨.", fileName, chunks.size());

            // 7. 병렬로 임베딩 처리
            log.info("파일 임베딩 시작");
            List<List<Double>> embeddings = chunks.parallelStream()
                    .map(embeddingProcessor::embed)
                    .collect(Collectors.toList());
            log.info("파일 임베딩 완료");
            log.info("{}", embeddings.get(0));

            // 문서 아이디 생성 및 문서 배열 생성
            String docId = "doc_" + UUID.randomUUID();
            //문서 내용 빌더
            DocumentBuilder builder = new DocumentBuilder();
            List<Document> documents = builder.createDocuments(
                    indexName,
                    docId,
                    chunks,
                    embeddings
            );

            //메타데이터 빌더 (추후 추가)
            // extract Metadata from file
            // for now hard coding
            Metadata metadata = builder.buildMetadata(
                    indexName,
                    docId
            );

            //저장
            if (!openSearchDocumentRepository.saveDocument(indexName, documents, metadata)) {
                throw new IOException("Failed to process file: " + fileName);
            } else {
                // 결과 반환
                result.put("status", "success");
                result.put("message", "청크 및 임베딩 성공!");
                result.put("response", docId);
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to process file: " + fileName, e);
        } catch (Exception e) {
            throw new UnsupportedModelException("Embedding processing failed: " + e.getMessage());
        }

        return result;
    }

    @Override
    public Map<String, Object> getDocument(String index, String docId) throws Exception{
        Map<String, Object> result = new HashMap<>();
        List<Document> documents= openSearchDocumentRepository.getDocByDocId(index, docId);

        List<DocumentDTO> documentDTOList = documents.stream()
                .map(this::convertToDTO) // 변환 메서드 호출
                .toList();

        // 결과 반환
        result.put("status", "success");
        result.put("message", "파일 청크 리스트 가져오기 성공");
        result.put("response", documentDTOList);
        return result;
    }

    private DocumentDTO convertToDTO(Document document) {
        // Document를 DocumentDTO로 변환 (예시)
        return new DocumentDTO(
                document.getId(),
                document.getDocId(),
                document.getIndex(),
                document.isActive(),
                document.getContent(),
                document.getContentVec(),
                document.getPage()
        );
    }

    public Map<String, Object> updateDocument(String index, MetadataDTO metadataDTO){
        Map<String, Object> result = new HashMap<>();
        String res = openSearchDocumentRepository.updateDocMetadataByDocId(index, metadataDTO.getDocId(), convertToMap(MetadataDTO.toMetadata(metadataDTO)));

        // 결과 반환
        result.put("status", "success");
        result.put("message", "파일 삭제 성공");
        result.put("response", res);
        return result;
    }


    @Override
    public Map<String, Object> deleteDocument(String index, String docId){
        Map<String, Object> result = new HashMap<>();
        openSearchDocumentRepository.deleteDocByDocId(index, docId);

        // 결과 반환
        result.put("status", "success");
        result.put("message", "파일 삭제 성공");
        return result;
    }

}

/*
1. getDocByDocId
java
코드 복사
List<Map<String, Object>> docs = getDocByDocId("metadata_index", "12345");
docs.forEach(System.out::println);
2. deleteDocByDocId
java
코드 복사
deleteDocByDocId("metadata_index", "12345");
3. updateDocByDocId
java
코드 복사
Map<String, Object> updatedFields = Map.of(
    "key1", "newValue1",
    "key2", "newValue2"
);
updateDocByDocId("metadata_index", "12345", updatedFields);
 */
