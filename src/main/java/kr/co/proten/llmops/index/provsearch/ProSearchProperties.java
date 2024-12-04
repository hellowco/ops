package kr.co.proten.llmops.index.provsearch;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Data
@Component
public class ProSearchProperties {
	@Value("${search.highlight.size}")
	public int highlightSize;	
	
	@Value("${search.special.char.comma}")
	public String specialCharComma;
	
	@Value("${search.special.char.slssh}")
	public String specialCharSlssh;
	
	@Value("${search.special.char.caret}")
	public String specialCharCaret;
	
	@Value("${search.special.char.dot}")
	public String specialCharDot;
	
	@Value("${search.special.char.colon}")
	public String specialCharColon;
	
	@Value("${search.token.key}")
	public String tokenKey;
	
	@Value("${search.use.data.encrypt}")
	public Boolean useDataEncrypt;
	
	@Value("${search.priority.sort}")
	public String [] prioritySort;
	
	@Value("${search.default.index.name}")
	public int defaultIndexName;
	
	@Value("${search.default.alias.name}")
	public int defaultAliasName;
	
	@Value("${search.default.sort.field}")
	public int defaultSortField;
	
	@Value("${search.default.search.field}")
	public int defaultSearchField;	
	
	@Value("${search.default.exclude.field}")
	public int defaultExcludeField;	
	
	@Value("${search.default.highlight.field}")
	public int defaultHighlightField;	
	
	@Value("${search.default.filter.query}")
	public int defaultFilterGuery;	
	
	@Value("${search.default.index.view.name}")
	public int defaultIndexViewName;

	@Value("${search.ip}")
	public String searchIP;

	@Value("${search.port}")
	public String searchPort;

	@Value("${search.manager.servers}")
	public String managerServers; 

	@Value("${search.highlight.size}")
	public int HIGHLIGHT_SIZE;

	@Value("${search.username}")
	public String username;

	@Value("${search.password}")
	public String password;
}
