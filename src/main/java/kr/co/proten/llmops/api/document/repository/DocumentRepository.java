package kr.co.proten.llmops.api.document.repository;

import kr.co.proten.llmops.api.document.entity.Document;
import kr.co.proten.llmops.api.document.entity.Metadata;

import java.util.List;
import java.util.Map;

public interface DocumentRepository {

    boolean saveDocument(String indexName, List<Document> documents, Metadata metadata);
    List<Document> getDocByDocId(String indexName, String knowledgeName, String docId, int pageNo, int pageSize);
    Metadata getDocMetadataByDocId(String indexName, String knowledgeName, String docId);
    String updateDocMetadataByDocId(String indexName, String docId, Map<String, Object> updatedFields);
    void deleteDocByDocId(String indexName, String knowledgeName, String docId);
}