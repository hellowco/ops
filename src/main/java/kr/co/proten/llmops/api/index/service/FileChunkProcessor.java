package kr.co.proten.llmops.api.index.service;

import java.io.IOException;
import java.util.List;

public interface FileChunkProcessor {
    String getServiceType(); // 서비스 타입 반환
    String readFileContent(String fileName) throws IOException; // 파일 읽기
    List<String> createChunks(String fileContent, int chunkSize, int overlap); // 청크 생성
}