package kr.co.proten.llmops.index.support;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Service 공통
 */
@Service("GenericService")
public class GenericService {
    public final static String TYPE 						= "";  							// type 기본 doc임
    public final static String ASC  						= "asc";
    public final static String DESC 						= "desc";

    public final static String NEWLINE = "\n";
    public final static String SEPERATOR = "/";
    
    @Value("${search.ip}") public String searchIP;
    @Value("${search.port}") public int searchPort;
}