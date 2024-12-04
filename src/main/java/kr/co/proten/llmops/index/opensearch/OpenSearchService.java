package kr.co.proten.llmops.index.opensearch;

;
import kr.co.proten.llmops.index.support.GenericService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OpenSearchService extends GenericService {
	private static final Logger logger = LoggerFactory.getLogger(OpenSearchService.class);
	
	/**
	 * 엘라스틱 서치 데이터를 insert/update 한다. value : _action
	 */
	public int[] saveDataByOpenSearchMap(String _index, String _type, String _action, Map<String, Object> requestMap) {
		int[] retInt = new int[4];
		OpenSearchUtil util = new OpenSearchUtil(searchIP, searchPort);
		
		try {
			util.connection();
			
			retInt = util.bulkMap(_index, _type, _action, requestMap);
		} catch (Exception e) {
			retInt = new int[] { -1, -1, -1, -1 };
			
			logger.error("Exception OpenSearch " + e.getMessage(), e);
		} finally {
			util.close();
		}

		return retInt;
	}

	/**
	 * 엘라스틱 서치 데이터를 insert/update 한다. value : _action
	 */
	public int[] saveDataByOpenSearchMapBulk(String _index, String _type, String _action, List<Map<String, Object>> requestMap) {
		int[] retInt = new int[4];
		OpenSearchUtil util = new OpenSearchUtil(searchIP, searchPort);

		try {
			util.connection();

			retInt = util.bulkMap2(_index, _type, _action, requestMap);
		} catch (Exception e) {
			retInt = new int[] { -1, -1, -1, -1 };

			logger.error("Exception OpenSearch " + e.getMessage(), e);
		} finally {
			util.close();
		}

		return retInt;
	}
	
	/**
	 * 엘라스틱 서치 데이터를 삭제한다. (String id) value : _action
	 */
	public void deleteDataByOpenSearchByString(String _index, String _action, String _type, List<String> array) {
		OpenSearchUtil util = new OpenSearchUtil(searchIP, searchPort);
		
		try {
			List<Map<String, Object>> mapList = new ArrayList<Map<String, Object>>();

			for (String id : array) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("_id", id);
				map.put("_action", _action);
				
				mapList.add(map);
			}
			
			util.connection();
			util.bulk(_index, _type, mapList);
		} catch (Exception e) {
			logger.error("Exception OpenSearch " + e.getMessage(), e);
		} finally {
			util.close();
		}
	}

	public int deleteByQuery(String index, String field, String value) throws IOException {
        OpenSearchUtil util = new OpenSearchUtil(searchIP, searchPort);
        int result;
        
        try {
            util.connection();
            
            result = util.deleteByQuery(index, field, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return result;
    }
}