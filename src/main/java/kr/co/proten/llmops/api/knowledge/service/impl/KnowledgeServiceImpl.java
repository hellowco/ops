package kr.co.proten.llmops.api.knowledge.service.impl;

import kr.co.proten.llmops.api.knowledge.dto.KnowledgeDTO;
import kr.co.proten.llmops.api.knowledge.entity.Knowledge;
import kr.co.proten.llmops.api.knowledge.repository.OpenSearchKnowledgeRepository;
import kr.co.proten.llmops.api.knowledge.service.KnowledgeService;
import kr.co.proten.llmops.core.helpers.UUIDGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static kr.co.proten.llmops.core.helpers.MappingLoader.loadMappingFromResources;

@Service
public class KnowledgeServiceImpl implements KnowledgeService {

    public static final String KNOWLEDGE_METADATA = "knowledge_metadata";
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final OpenSearchKnowledgeRepository openSearchKnowledgeRepository;

    public KnowledgeServiceImpl(OpenSearchKnowledgeRepository openSearchKnowledgeRepository) {
        this.openSearchKnowledgeRepository = openSearchKnowledgeRepository;
    }

    @Override
    public boolean createIndexWithMapping(String indexName) throws IOException {
        Map<String, Object> contentMapping = loadMappingFromResources("mappings/ContentMapping.json");
        Map<String, Object> metadataMapping = loadMappingFromResources("mappings/MetadataMapping.json");

        // 메타데이터 인덱스 이름 생성
        final String metaIndexName = indexName + "_metadata";

        try {
            openSearchKnowledgeRepository.createIndex(indexName, contentMapping);
            openSearchKnowledgeRepository.createIndex(metaIndexName, metadataMapping);
        } catch (Exception e) {
            openSearchKnowledgeRepository.deleteIndex(indexName);
            openSearchKnowledgeRepository.deleteIndex(metaIndexName);
            throw new IOException("Error while creating index", e);
        }

        return true;
    }

    @Override
    public boolean deleteIndex(String indexName) throws IOException {
        // 메타데이터 인덱스 이름 생성
        final String metaIndexName = indexName + "_metadata";

        try {
            openSearchKnowledgeRepository.deleteIndex(indexName);
            openSearchKnowledgeRepository.deleteIndex(metaIndexName);
        } catch (Exception e) {
            throw new IOException("Error while deleting index", e);
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

        //TODO:: modelName에 해당하는 인덱스가 오픈서치에 없으면 생성해야함.
        // 현재 인덱스 mapping되는데 settings가 안되어서 구현 추후로 미룸
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

    private Knowledge toEntity(KnowledgeDTO dto) {
        return Knowledge.builder()
                .id(dto.id())
                .modelName(dto.modelName())
                .knowledgeName(dto.knowledgeName())
                .description(dto.description())
                .build();
    }
}
