package kr.co.proten.llmops.api.document.service;

import kr.co.proten.llmops.api.document.dto.MetadataDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface DocumentService {
    Map<String, Object> uploadFile(MultipartFile file) throws Exception;

    Map<String, Object> uploadDocument(String targetIndex, String knowledgeName, String fileName, int chunkSize, int overlapSize, String modelType, List<String> processingKeys) throws Exception;

    Map<String, Object> getDocumentList(String index, String knowledgeName, int pageNo, int pageSize) throws Exception;

    Map<String, Object> getDocument(String index, String knowledgeName, String docId, int pageNo, int pageSize) throws Exception;

    Map<String, Object> getDocumentMetadata(String index, String knowledgeName, String docId) throws Exception;

    Map<String, Object> updateDocument(String indexName, String knowledgeName, String docId, Object object);

    Map<String, Object> deleteDocument(String index, String knowledgeName, String docId);
}
