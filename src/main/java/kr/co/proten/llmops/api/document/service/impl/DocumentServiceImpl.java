package kr.co.proten.llmops.api.document.service.impl;

import kr.co.proten.llmops.api.document.dto.DocumentDTO;
import kr.co.proten.llmops.api.document.dto.MetadataDTO;
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
import java.util.function.UnaryOperator;
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
    public Map<String, Object> uploadDocument(String indexName, String knowledgeName, String fileName, int chunkSize, int overlapSize, String modelType, List<String> processingKeys) {
        Map<String, Object> result = new HashMap<>();

        String fileType = getExtension(fileName);

        // 1. 청크 서비스 선택
        ChunkProcessor chunkProcessor = chunkProcessorFactory.getChunkService(fileType)
                .orElseThrow(() -> new UnsupportedOperationException("지원하지 않는 파일 타입: " + fileType));

        // 2. 임베딩 서비스 선택
        EmbeddingProcessor embeddingProcessor = embeddingProcessorFactory.getEmbeddingService(modelType)
                .orElseThrow(() -> new UnsupportedOperationException("지원하지 않는 임베딩 형식: " + modelType));

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
            // 청크 방식 선택 된거 사용할 수 있도록 바꿔야할 듯
            List<String> chunks = chunkProcessor.chunkText(fileContent, chunkSize, overlapSize);
            log.info("{} 파일이 {}로 청크됨.", fileName, chunks.size());

            // 7. 병렬로 임베딩 처리
            log.info("파일 임베딩 시작");
            List<List<Double>> embeddings = chunks.parallelStream()
                    .map(embeddingProcessor::embed)
                    .toList();
            log.info("파일 임베딩 완료");
            log.info("{}", embeddings.get(0));

            // 문서 아이디 생성 및 문서 배열 생성
            String docId = "doc_" + UUID.randomUUID();
            //문서 내용 빌더
            DocumentBuilder builder = new DocumentBuilder();
            List<Document> documents = builder.createDocuments(
                    knowledgeName,
                    docId,
                    chunks,
                    embeddings
            );

            // TODO:: 메타데이터 빌더 (추후 수정 필요)
            // TODO:: 파일 경로, pdf 관련 수정 필요
            Metadata metadata = builder.buildMetadata(
                    knowledgeName,
                    docId,
                    fileName,
                    fileName,
                    chunkSize,
                    chunks.size()
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
    public Map<String, Object> getDocumentList(String index, String knowledgeName, int pageNo, int pageSize) throws Exception {
        Map<String, Object> result = new HashMap<>();
        // Validate page params
        int adjustedPageNo = (pageNo <= 1) ? 0 : pageNo - 1;
        int adjustedPageSize = Math.max(pageSize, 1);

        List<Metadata> metadataList= openSearchDocumentRepository.getDocumentList(index, knowledgeName, adjustedPageNo, adjustedPageSize);

        List<MetadataDTO> metadataDTOList = (metadataList == null || metadataList.isEmpty())
                ? Collections.emptyList()
                : metadataList.stream()
                .map(MetadataDTO::of)
                .toList();

        log.info("converted documents: {}", metadataDTOList);

        // 결과 반환
        if(!metadataDTOList.isEmpty()){
            result.put("status", "success");
            result.put("message", "파일 데이터 리스트 가져오기 성공");
            result.put("response", metadataDTOList);
        } else {
            result.put("status", "failed");
            result.put("message", "파일 데이터 리스트 가져오기 실패");
            result.put("response", metadataDTOList);
        }

        return result;
    }

    @Override
    public Map<String, Object> getDocument(String index, String knowledgeName, String docId, int pageNo, int pageSize) throws Exception{
        Map<String, Object> result = new HashMap<>();
        // Validate page params
        int adjustedPageNo = (pageNo <= 1) ? 0 : pageNo - 1;
        int adjustedPageSize = Math.max(pageSize, 1);

        List<Document> documents= openSearchDocumentRepository.getDocByDocId(index, knowledgeName, docId, adjustedPageNo, adjustedPageSize);

        List<DocumentDTO> documentDTOList = (documents == null || documents.isEmpty())
                ? Collections.emptyList()
                : documents.stream()
                .map(this::convertToDTO)
                .toList();

        log.info("converted documents: {}", documentDTOList);

        // 결과 반환
        if(!documentDTOList.isEmpty()){
            result.put("status", "success");
            result.put("message", "파일 데이터 리스트 가져오기 성공");
            result.put("response", documentDTOList);
        } else {
            result.put("status", "failed");
            result.put("message", "파일 데이터 리스트 가져오기 실패");
            result.put("response", documentDTOList);
        }

        return result;
    }

    @Override
    public Map<String, Object> getDocumentMetadata(String index, String knowledgeName, String docId) throws Exception{
        Map<String, Object> result = new HashMap<>();

        MetadataDTO metadataDTO = MetadataDTO.of(openSearchDocumentRepository.getDocMetadataByDocId(index, knowledgeName, docId));

        log.info("Metadata of document: {}", metadataDTO);

        // 결과 반환
        if(metadataDTO != null){
            result.put("status", "success");
            result.put("message", "파일 메타데이터 가져오기 성공");
            result.put("response", metadataDTO);
        } else {
            result.put("status", "failed");
            result.put("message", "파일 메타데이터 가져오기 실패");
            result.put("response", null);
        }

        return result;
    }

    @Override
    public Map<String, Object> updateDocument(String indexName, String knowledgeName, String docId, Object object){
        Map<String, Object> result = new HashMap<>();
        Metadata metadata = openSearchDocumentRepository.getDocMetadataByDocId(indexName, knowledgeName, docId);

        updateMetadataFields(metadata, object);

        String updateResponse = openSearchDocumentRepository.updateDocMetadataByDocId(indexName, docId, convertToMap(metadata));

        // 결과 반환
        result.put("status", "success");
        result.put("message", "파일 수정 성공");
        result.put("response", updateResponse);
        return result;
    }

    @Override
    public Map<String, Object> deleteDocument(String index, String knowledgeName, String docId){
        Map<String, Object> result = new HashMap<>();
        openSearchDocumentRepository.deleteDocByDocId(index, knowledgeName, docId);

        // 결과 반환
        result.put("status", "success");
        result.put("message", "파일 삭제 성공");
        return result;
    }

    private DocumentDTO convertToDTO(Document document) {
        // Document를 DocumentDTO로 변환 (예시)
        return DocumentDTO.builder()
                .id(document.getId())
                .docId(document.getDocId())
                .chunkId(document.getChunkId())
                .index(document.getIndex())
                .isActive(document.isActive())
                .content(document.getContent())
                .page(document.getPage())
                .score(document.getScore())
                .build();
    }

    /**
     * 메타데이터 필드를 업데이트하는 메서드.
     *
     * @param metadata 수정할 메타데이터 객체
     * @param object   업데이트할 값 (boolean 또는 String)
     */
    private void updateMetadataFields(Metadata metadata, Object object) {
        if (object instanceof Boolean bool) {
            metadata.setActive(bool);
        } else if (object instanceof String str) {
            metadata.setDescription(str);
        } else {
            throw new IllegalArgumentException("Unsupported object type for metadata update: " + object.getClass().getName());
        }
    }
}