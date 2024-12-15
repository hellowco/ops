package kr.co.proten.llmops.api.document.repository;

import kr.co.proten.llmops.api.document.entity.Document;
import kr.co.proten.llmops.api.document.entity.Metadata;

import java.util.List;

public interface DocumentRepository {


    boolean saveDocument(String indexName, List<Document> documents, Metadata metadata);
//    List<Document> findByMetadata(Metadata metadata);
}