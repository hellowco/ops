package kr.co.proten.llmops.api.index.service.impl;

import kr.co.proten.llmops.api.index.dto.ProcessingResult;
import kr.co.proten.llmops.api.index.service.EmbeddingProcessor;
import kr.co.proten.llmops.api.index.service.FileChunkProcessor;
import kr.co.proten.llmops.api.index.service.IndexService;
import kr.co.proten.llmops.api.index.service.factory.ChunkProcessorRegistry;
import kr.co.proten.llmops.api.index.service.factory.ChunkServiceFactory;
import kr.co.proten.llmops.api.index.service.factory.EmbedServiceFactory;
import kr.co.proten.llmops.api.index.service.helper.FileValidator;
import kr.co.proten.llmops.api.index.service.helper.TextExtractor;
import kr.co.proten.llmops.api.index.service.storage.FileStorageService;
import kr.co.proten.llmops.global.exception.FileStorageException;
import kr.co.proten.llmops.global.exception.UnsupportedModelException;
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

import static kr.co.proten.llmops.global.common.utils.FileUtil.getExtension;

@Service
public class IndexServiceImpl implements IndexService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final long PARALLEL_THRESHOLD;

    private final FileValidator fileValidator;
    private final FileStorageService fileStorageService;
    private final TextExtractor textExtractor;
    private final ChunkServiceFactory chunkServiceFactory;
    private final EmbedServiceFactory embedServiceFactory;
    private final ChunkProcessorRegistry chunkProcessorRegistry;

    @Value("${file.chunk.size:10}")
    private long fileChunkSize;

    @Value("${file.upload.path:D:/llmops/uploads}")
    private String uploadPath;

    @Value("${file.save.path:D:/llmops/saves}")
    private String savePath;

    @Autowired
    public IndexServiceImpl(FileValidator fileValidator, FileStorageService fileStorageService, TextExtractor textExtractor, ChunkServiceFactory chunkServiceFactory, EmbedServiceFactory embedServiceFactory, ChunkProcessorRegistry chunkProcessorRegistry) {
        this.fileValidator = fileValidator;
        this.fileStorageService = fileStorageService;
        this.textExtractor = textExtractor;
        this.chunkServiceFactory = chunkServiceFactory;
        this.embedServiceFactory = embedServiceFactory;
        this.chunkProcessorRegistry = chunkProcessorRegistry;
        PARALLEL_THRESHOLD = 1024 * 1024 * fileChunkSize;
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
            result.put("extractedText", extractedText);
        } catch (IOException e) {
            throw new FileStorageException("파일 저장 중 오류가 발생했습니다.", e);
        }
        return result;
    }

    @Override
    public Map<String, Object> indexChunkFile(String fileName, int chunkSize, int overlapSize, String modelType, List<String> processingKeys) {
        Map<String, Object> result = new HashMap<>();

        String fileType = getExtension(fileName);

        // 1. 청크 서비스 선택
        FileChunkProcessor chunkProcessor = chunkServiceFactory.getFileChunkService(fileType)
                .orElseThrow(() -> new UnsupportedOperationException("지원하지 않는 파일 타입: " + fileType));

        // 2. 임베딩 서비스 선택
        EmbeddingProcessor embeddingProcessor = embedServiceFactory.getEmbeddingService(modelType)
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

            processingResult = ProcessingResult.of(chunks, embeddings);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to process file: " + fileName, e);
        } catch (Exception e) {
            processingResult = ProcessingResult.of(Collections.emptyList(), null);
            throw new UnsupportedModelException("Embedding processing failed: " + e.getMessage());
        } finally {
            // 결과 반환
            result.put("status", "success");
            if (processingResult != null && processingResult.getEmbeddings() != null) {
                result.put("message", "청크 및 임베딩 성공!");
                result.put("chunkList", processingResult.getChunks());
            } else if (processingResult != null) {
                result.put("message", "청크 성공 및 임베딩 실패!");
                result.put("extractedText", processingResult.getChunks());
            } else {
                result.put("message", "청크 및 임베딩 실패!");
                result.put("extractedText", "");
            }
        }
        return result;
    }
}
