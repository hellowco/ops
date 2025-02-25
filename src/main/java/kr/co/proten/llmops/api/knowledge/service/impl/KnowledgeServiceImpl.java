package kr.co.proten.llmops.api.knowledge.service.impl;

import kr.co.proten.llmops.api.knowledge.dto.KnowledgeDTO;
import kr.co.proten.llmops.api.knowledge.entity.Knowledge;
import kr.co.proten.llmops.api.knowledge.repository.OpenSearchKnowledgeRepository;
import kr.co.proten.llmops.api.knowledge.service.KnowledgeService;
import kr.co.proten.llmops.core.exception.IndexCreationException;
import kr.co.proten.llmops.core.exception.IndexDeleteException;
import kr.co.proten.llmops.core.helpers.UUIDGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class KnowledgeServiceImpl implements KnowledgeService {

    public static final String KNOWLEDGE_METADATA = "knowledge_metadata";

    @Value("${opensearch.index.prefix}")
    String osPrefix;

    private final OpenSearchKnowledgeRepository openSearchKnowledgeRepository;

    public KnowledgeServiceImpl(OpenSearchKnowledgeRepository openSearchKnowledgeRepository) {
        this.openSearchKnowledgeRepository = openSearchKnowledgeRepository;
    }

    @Override
    public String createIndex(String indexName, int dimension) {
        //특수문자 제거
        indexName = indexName.replaceAll("[^a-zA-Z0-9]", "_");

        final String newIndexName = osPrefix + indexName + "_" + dimension;

        if(openSearchKnowledgeRepository.createIndex(newIndexName, dimension)){
            return newIndexName;
        } else {
            throw new IndexCreationException("Index creation failed.");
        }
    }

    @Override
    public boolean deleteIndex(String indexName) {
        // 메타데이터 인덱스 이름 생성
//        final String metaIndexName = indexName + "_metadata";

        try {
            openSearchKnowledgeRepository.deleteIndex(indexName);
//            openSearchKnowledgeRepository.deleteIndex(metaIndexName);
        } catch (Exception e) {
            throw new IndexDeleteException("Error while deleting index");
        }

        return true;
    }

    @Override
    public Map<String, Object> getKnowledgeList() {
        Map<String, Object> result = new HashMap<>();

        try {
            List<Knowledge> entities = openSearchKnowledgeRepository.findAllKnowledge(KNOWLEDGE_METADATA);


            List<KnowledgeDTO> knowledgeDTOList = entities.stream()
                    .map(this::toDTO)
                    .toList();

            // 결과 반환
            result.put("status", "success");
            result.put("message", "지식 리스트 반환 성공");
            result.put("response", knowledgeDTOList);
            return result;
        } catch (IOException e) {
            throw new RuntimeException("Error fetching knowledge list", e);
        }
    }

    @Override
    public Map<String, Object> createKnowledge(String modelName,String knowledgeName,String description) {
        Map<String, Object> result = new HashMap<>();

        Knowledge entity = Knowledge.builder()
                .id(UUIDGenerator.generateUUID())
                .modelName(modelName)
                .knowledgeName(knowledgeName)
                .description(description)
                .build();

        try {
            String response_id = openSearchKnowledgeRepository.saveKnowledge(KNOWLEDGE_METADATA, entity);
            result.put("status", "success");
            result.put("message", "지식 생성 성공");
            result.put("response", response_id);
            return result;
        } catch (IOException e) {
            throw new RuntimeException("Error creating knowledge", e);
        }
    }

    @Override
    public Map<String, Object> updateKnowledge(String id, String description) {
        Map<String, Object> result = new HashMap<>();

        try {
            // ID로 문서 조회
            Knowledge entity = openSearchKnowledgeRepository.findById(KNOWLEDGE_METADATA, id);

            if (entity == null) {
                result.put("status", "error");
                result.put("message", "해당 ID의 문서를 찾을 수 없습니다.");
                return result;
            }

            // description 업데이트
            entity.setDescription(description);

            // 업데이트된 문서 저장
            String res = openSearchKnowledgeRepository.updateKnowledge(KNOWLEDGE_METADATA, id, entity);

            result.put("status", "success");
            result.put("message", "지식 수정 성공");
            result.put("response", res);
            return result;
        } catch (Exception e) {
            throw new RuntimeException("Error updating knowledge", e);
        }
    }

    @Override
    public Map<String, Object> deleteKnowledge(String id) {
        Map<String, Object> result = new HashMap<>();

        try {
            String res = openSearchKnowledgeRepository.deleteKnowledge(KNOWLEDGE_METADATA, id);

            result.put("status", "success");
            result.put("message", "지식 삭제 성공");
            result.put("response", res);
            return result;
        } catch (IOException e) {
            throw new RuntimeException("Error deleting knowledge", e);
        }
    }

    private KnowledgeDTO toDTO(Knowledge entity) {
        return KnowledgeDTO.builder()
                .id(entity.getId())
                .modelName(entity.getModelName())
                .knowledgeName(entity.getKnowledgeName())
                .description(entity.getDescription())
                .build();
    }
}
