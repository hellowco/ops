package kr.co.proten.llmops.api.document.service.strategy.chunk;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Component
public class TextChunkProcessor implements ChunkProcessor {
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    public static final String CHUNK_TYPE = "txt";

    @Override
    public String getServiceType() { return CHUNK_TYPE; }

    @Override
    public List<String> createChunks(String fileContent, int chunkSize, int overlap) {
        if (overlap >= chunkSize) {
            throw new IllegalArgumentException("Overlap 사이즈는 Chunk 사이즈보다 작아야 합니다.");
        }

        log.info("Creating chunks from file content");

        List<String> chunks = new ArrayList<>();
        List<String> buffer = new ArrayList<>();

        // 파일 내용을 줄 단위로 분리
        String[] lines = fileContent.split("\n");

        for (String line : lines) {
            buffer.add(line.trim()); // 각 줄을 트리밍하여 추가
            while (buffer.size() >= chunkSize) {
                // 청크를 문자열로 생성
                String chunk = String.join("\n", buffer.subList(0, chunkSize));
                chunks.add(chunk);

                // 겹치는 데이터 유지
                buffer = new ArrayList<>(buffer.subList(chunkSize - overlap, buffer.size()));
            }
        }

        // 남은 데이터 처리
        if (!buffer.isEmpty()) {
            chunks.add(String.join("\n", buffer));
        }

        return chunks;
    }

    @Override
    public String readFileContent(String fileName) throws IOException {
        log.info("Reading file content: {}", fileName);

        StringBuilder content = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line.trim()).append("\n"); // 라인을 트리밍하고 줄 바꿈 추가
            }
        } catch (FileNotFoundException e) {
            throw new IOException("파일이 존재하지 않습니다: " + fileName, e);
        } catch (IOException e) {
            throw new IOException("파일 읽기 오류: " + fileName, e);
        }

        return content.toString(); // 최종 문자열 반환
    }
}