package kr.co.proten.llmops.api.index.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface IndexService {
    Map<String, Object> uploadFile(MultipartFile file) throws Exception;

    Map<String, Object> indexChunkFile(String fileName, int chunkSize, int overlapSize, String modelType, List<String> processingKeys) throws Exception;
}
