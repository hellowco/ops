package kr.co.proten.llmops.api.search.util;

import kr.co.proten.llmops.api.document.dto.DocumentDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
public class RRFMerger {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private static final int RRF_CONSTANT = 60;

    public List<DocumentDTO> merge(
            List<DocumentDTO> keywordResults,
            List<DocumentDTO> vectorResults,
            double keywordWeight,
            double vectorWeight,
            int pageSize
    ) {
        // 최종 점수 맵 (문서 ID -> 점수)
        Map<String, Double> scoreMap = new HashMap<>();

        // 키워드 결과에 점수 부여
        for (int i = 0; i < keywordResults.size(); i++) {
            DocumentDTO doc = keywordResults.get(i);
            double rrfScore = 1.0 / (RRF_CONSTANT + i + 1); // RRF 점수 계산
            scoreMap.compute(doc.id(), (key, oldScore) ->
                    (oldScore == null ? 0.0 : oldScore) + keywordWeight * rrfScore
            );
        }

        // 벡터 결과에 점수 부여
        for (int i = 0; i < vectorResults.size(); i++) {
            DocumentDTO doc = vectorResults.get(i);
            double rrfScore = 1.0 / (RRF_CONSTANT + i + 1); // RRF 점수 계산
            scoreMap.compute(doc.id(), (key, oldScore) ->
                    (oldScore == null ? 0.0 : oldScore) + vectorWeight * rrfScore
            );
        }

        // 상위 pageSize개의 문서를 선택
        return scoreMap.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .limit(pageSize) // pageSize 제한
                .map(entry -> {
                    String id = entry.getKey();
                    double finalScore = entry.getValue() * 100;
                    log.debug("{} : {}", id, finalScore);
                    DocumentDTO originalDoc = findDocument(id, keywordResults, vectorResults);

                    if (originalDoc != null) {
                        return DocumentDTO.builder()
                                .id(originalDoc.id())
                                .docId(originalDoc.docId())
                                .chunkId(originalDoc.chunkId())
                                .index(originalDoc.index())
                                .isActive(originalDoc.isActive())
                                .content(originalDoc.content())
                                .page(originalDoc.page())
                                .score(finalScore)
                                .build();
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .toList();
    }

    // 문서 ID로 DocumentDTO 찾기 (키워드 및 벡터 결과 리스트에서 검색)
    private DocumentDTO findDocument(String id, List<DocumentDTO> keywordResults, List<DocumentDTO> vectorResults) {
        return keywordResults.stream()
                .filter(doc -> doc.id().equals(id))
                .findFirst()
                .orElseGet(() -> vectorResults.stream()
                        .filter(doc -> doc.id().equals(id))
                        .findFirst()
                        .orElse(null)
                );
    }
}
