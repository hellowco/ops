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

    /**
     * 텍스트를 지정된 크기로 청크 단위로 나누는 메서드
     * @param text 원본 텍스트
     * @param chunkSize 청크 크기
     * @param overlapSize 겹치는 크기
     * @return 청크 리스트
     */
    @Override
    public List<String> chunkText(String text, int chunkSize, int overlapSize) {
        if (overlapSize >= chunkSize) {
            throw new IllegalArgumentException("Overlap 사이즈는 Chunk 사이즈보다 작아야 합니다.");
        }

        log.info("Creating chunks from file content");

        List<String> chunks = new ArrayList<>();
        int start = 0;
        int length = text.length();

        while (start < length) {
            int end = Math.min(start + chunkSize, length);
            chunks.add(text.substring(start, end));
            start += (chunkSize - overlapSize); // 겹치는 부분만큼 이동
        }

        return chunks;
    }

    /**
     * Sentence Window 기반으로 텍스트를 청크로 나누는 메서드
     * @param text 원본 텍스트
     * @param maxChunkSize 최대 청크 크기
     * @return 문장 기반 청크 리스트
     */
    public List<String> chunkBySentenceWindow(String text, int maxChunkSize) {
        List<String> chunks = new ArrayList<>();
        StringBuilder chunkBuilder = new StringBuilder();
        int currentSize = 0;

        // 정규식을 이용해 문장을 구분
        String[] sentences = text.split("(?<=[.!?,])(?=\\s)");

        for (String sentence : sentences) {
            String trimmedSentence = sentence.trim();

            // 청크에 문장을 추가해도 최대 크기를 초과하지 않는 경우
            if (currentSize + trimmedSentence.length() <= maxChunkSize) {
                chunkBuilder.append(trimmedSentence).append(" ");
                currentSize += trimmedSentence.length();
            } else {
                // 현재 청크를 저장하고 새 청크를 시작
                chunks.add(chunkBuilder.toString().trim());
                chunkBuilder.setLength(0); // 초기화
                chunkBuilder.append(trimmedSentence).append(" ");
                currentSize = trimmedSentence.length();
            }
        }

        // 마지막 청크를 추가
        if (!chunkBuilder.isEmpty()) {
            chunks.add(chunkBuilder.toString().trim());
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