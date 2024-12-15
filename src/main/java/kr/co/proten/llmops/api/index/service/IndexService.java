package kr.co.proten.llmops.api.index.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface IndexService {
    boolean createIndexWithMapping(String indexName) throws IOException;
    boolean deleteIndex(String indexName) throws IOException;
}
