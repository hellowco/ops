package kr.co.proten.llmops.api.index.service.impl;

import kr.co.proten.llmops.api.index.repository.OpenSearchIndexRepository;
import kr.co.proten.llmops.api.index.service.IndexService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;

import static kr.co.proten.llmops.core.helpers.MappingLoader.loadMappingFromResources;

@Service
public class IndexServiceImpl implements IndexService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final OpenSearchIndexRepository openSearchIndexRepository;

    public IndexServiceImpl(OpenSearchIndexRepository openSearchIndexRepository) {
        this.openSearchIndexRepository = openSearchIndexRepository;
    }


    @Override
    public boolean createIndexWithMapping(String indexName) throws IOException {
        Map<String, Object> contentMapping = loadMappingFromResources("mappings/ContentMapping.json");
        Map<String, Object> metadataMapping = loadMappingFromResources("mappings/MetadataMapping.json");

        // 메타데이터 인덱스 이름 생성
        final String metaIndexName = indexName + "_metadata";

        try {
            openSearchIndexRepository.createIndex(indexName, contentMapping);
            openSearchIndexRepository.createIndex(metaIndexName, metadataMapping);
        } catch (IOException e) {
            openSearchIndexRepository.deleteIndex(indexName);
            openSearchIndexRepository.deleteIndex(metaIndexName);
            throw new IOException("Error while creating index", e);
        }

        return true;
    }

    @Override
    public boolean deleteIndex(String indexName) throws IOException {
        // 메타데이터 인덱스 이름 생성
        final String metaIndexName = indexName + "_metadata";

        try {
            openSearchIndexRepository.deleteIndex(indexName);
            openSearchIndexRepository.deleteIndex(metaIndexName);
        } catch (IOException e) {
            throw new IOException("Error while deleting index", e);
        }
        return true;
    }
}
