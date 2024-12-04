package kr.co.proten.llmops.index.provsearch;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties("search.index.sets")
@Data
public class ProSearchIndexProperties {
	private List<String> indexName;
	private List<String> aliasName;
	private List<String> sort;
	private List<String> searchField;
	private List<String> includeField;
	private List<String> excludeField;
	private List<String> highlightField;
	private List<String> nestHighlightField;
	private List<String> filterQuery;
	private List<String> resultField;
	private List<String> viewIndexName;			
}