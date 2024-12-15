package kr.co.proten.llmops.api.document.service;

import kr.co.proten.llmops.api.document.dto.MetadataDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface DocumentService {
    Map<String, Object> uploadFile(MultipartFile file) throws Exception;

    Map<String, Object> uploadDocument(String targetIndex, String fileName, int chunkSize, int overlapSize, String modelType, List<String> processingKeys) throws Exception;

    Map<String, Object> getDocument(String index, String docId) throws Exception;

    Map<String, Object> updateDocument(String index, MetadataDTO metadataDTO);

    Map<String, Object> deleteDocument(String index, String docId);
}
