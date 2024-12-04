package kr.co.proten.llmops.api.index.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface IndexService {
    Map<String, Object> uploadFile(MultipartFile file) throws Exception;
}
