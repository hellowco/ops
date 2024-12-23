package kr.co.proten.llmops.api.knowledge.service;

import java.io.IOException;

public interface IndexService {
    boolean createIndexWithMapping(String indexName) throws IOException;
    boolean deleteIndex(String indexName) throws IOException;
}
