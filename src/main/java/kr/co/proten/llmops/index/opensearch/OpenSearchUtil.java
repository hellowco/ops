package kr.co.proten.llmops.index.opensearch;

import com.google.gson.Gson;
import kr.co.proten.llmops.global.common.utils.StringUtil;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.opensearch.action.bulk.BulkItemResponse;
import org.opensearch.action.bulk.BulkRequest;
import org.opensearch.action.bulk.BulkResponse;
import org.opensearch.action.delete.DeleteRequest;
import org.opensearch.action.index.IndexRequest;
import org.opensearch.action.update.UpdateRequest;
import org.opensearch.client.RequestOptions;
import org.opensearch.client.RestClient;
import org.opensearch.client.RestClientBuilder;
import org.opensearch.client.RestHighLevelClient;
import org.opensearch.common.unit.TimeValue;
import org.opensearch.common.xcontent.XContentType;
import org.opensearch.index.query.QueryBuilders;
import org.opensearch.index.reindex.BulkByScrollResponse;
import org.opensearch.index.reindex.DeleteByQueryRequest;
import org.opensearch.search.Scroll;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OpenSearchUtil {
	private static final Logger logger = LoggerFactory.getLogger(OpenSearchUtil.class);
	private RestHighLevelClient client = null;
	private String osIp = "";
	private int osPort = 19200;
	private int timeouts = 30000;

	final Scroll scroll = new Scroll(TimeValue.timeValueMinutes(10));

	Map<String, Integer> indexList = new HashMap<String, Integer>();
	public boolean isConnected = false;
	String errorMsg = "";

	public OpenSearchUtil(String ip, int port) {
		this.osIp = ip;
		this.osPort = port;
	}

	public OpenSearchUtil(String ip, int port, int timeout) {
		this.osIp = ip;
		this.osPort = port;
		this.timeouts = timeout;
	}

	public OpenSearchUtil getClone() {
		OpenSearchUtil cloneUtil = new OpenSearchUtil(osIp, osPort, timeouts);

		try {
			cloneUtil.connection();

			return cloneUtil;
		} catch (Exception e) {
			logger.error("Exception OpenSearch " + e.getMessage(), e);
		}

		return null;
	}

	public void connection() throws Exception {
		if (osIp.equals("")) {
			throw new Exception("OpenSearch Ip is null");
		}

		final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
		credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials("admin", "proten1!"));

		client = new RestHighLevelClient(RestClient.builder(new HttpHost(osIp, osPort, "https"))
				.setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {
					@Override
					public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpClientBuilder) {
						return httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider)
								.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE);
					}
				}));

		try {
			client.ping(RequestOptions.DEFAULT);
		} catch (Exception ex) {
			throw new Exception(
					" OpenSearchUtil connection Fail [ ip : " + osIp + " |  port : " + osPort + "]" + ex.toString());
		}
	}

	public void close() {
		try {
			client.close();
		} catch (IOException e) {
			logger.error("Exception OpenSearchUtil " + e.getMessage(), e);
		}
	}

	public static boolean testConn(String strUrl) {
		logger.debug("OpenSearchUtil Url : " + strUrl);

		HttpURLConnection urlConn = null;

		try {
			URL url = new URL(strUrl);
			urlConn = (HttpURLConnection) url.openConnection();
			urlConn.connect();

			if (HttpURLConnection.HTTP_OK == urlConn.getResponseCode()) {
				return true;
			}

			logger.debug("OpenSearch Connection Ok");
		} catch (Exception e) {
			logger.error("Exception OpenSearchUtil " + e.getMessage(), e);

			return false;
		} finally {
			if (urlConn != null) {
				urlConn.disconnect();
			}
		}

		return false;
	}

	public int[] bulk(String indexName, String indexType, List<Map<String, Object>> mapList) {
		int retCount[] = new int[4];

		for (int idx = 0; idx < retCount.length; idx++) {
			retCount[idx] = 0;
		}

		BulkRequest request = new BulkRequest();
		for (Map<String, Object> mapData : mapList) {
			for (Map.Entry<String, Object> elem : mapData.entrySet()) {
				logger.debug("bulk : " + elem.getKey() + "/" + elem.getValue().toString());
			}

			String _index = indexName;
			String _action = StringUtil.checkNull(mapData.get("_action"));
			String _id = StringUtil.checkNull(mapData.get("_id"));

			mapData.remove("_id");
			mapData.remove("_action");

			logger.debug("action : " + _action + "/" + _index + "/" + _id);

			if ("index".equals(_action)) {
				request.add(new IndexRequest(_index).id(_id).source(mapData, XContentType.JSON));
				new Gson();
			} else if ("update".equals(_action)) {
				UpdateRequest update = new UpdateRequest(_index, _id);
				update.doc(mapData, XContentType.JSON);
				update.docAsUpsert(true);

				request.add(update);
			} else if ("delete".equals(_action)) {
				request.add(new DeleteRequest(_index, _id));
			}
		}

		try {
			BulkResponse bulkResponse = client.bulk(request, RequestOptions.DEFAULT);

			logger.debug("\nPOST \n" + request.getDescription());

			if (bulkResponse.hasFailures()) {
				for (BulkItemResponse bulkItemResponse : bulkResponse) {
					if (bulkItemResponse.isFailed()) {
						BulkItemResponse.Failure failure = bulkItemResponse.getFailure();

						logger.error("[bulk]" + failure.toString());

						retCount[3]++;
					}

					bulkItemResponse.getResponse();

					switch (bulkItemResponse.getOpType()) {
					case CREATE:
						retCount[0]++;

						break;
					case UPDATE:
						retCount[1]++;

						break;
					case DELETE:
						retCount[2]++;
					}
				}
			}
			
			Thread.sleep(1000); // 19.6.10 opensearch Interval : async, sync, 싱크
		} catch (Exception e) {
			logger.error("Exception OpenSearch " + e.getMessage(), e);
		}

		return retCount;
	}

	public int[] bulkMap(String indexName, String indexType, String _action, Map<String, Object> mapData)
			throws Exception {
		int retCount[] = new int[] { 0, 0, 0, 0 };

		BulkRequest request = new BulkRequest();

		String _index = indexName;
		String _id = "";

		if (mapData.containsKey("_id")) {
			_id = StringUtil.checkNull(mapData.get("_id"));
			mapData.remove("_id");
		}

		if ("".equals(_id) && mapData.containsKey("id")) {
			_id = StringUtil.checkNull(mapData.get("id"));
		}

		Gson gson = new Gson();
		String mapContents = gson.toJson(mapData);

		logger.debug("action : " + _action + "/" + _index + "/" + _id);
		logger.debug("map : " + mapContents);

		_id = StringUtil.replace(_id, "\"", "");

		if ("index".equals(_action)) {
			request.add(new IndexRequest(_index).id(_id).source(mapContents, XContentType.JSON));
		} else if ("update".equals(_action)) {
			UpdateRequest update = new UpdateRequest(_index, _id);
			update.doc(mapContents, XContentType.JSON);
			update.docAsUpsert(true);

			request.add(update);
		} else if ("delete".equals(_action)) {
			request.add(new DeleteRequest(_index, _id));
		} else {
			logger.error("ERROR " + mapData.toString());
		}

		try {
			BulkResponse bulkResponse = client.bulk(request, RequestOptions.DEFAULT);

			if (bulkResponse.hasFailures()) {
				for (BulkItemResponse bulkItemResponse : bulkResponse) {
					if (bulkItemResponse.isFailed()) {
						BulkItemResponse.Failure failure = bulkItemResponse.getFailure();

						logger.error("[bulkMap]" + failure.toString());

						retCount[3]++;
					}
				}
			} else {
				for (BulkItemResponse bulkItemResponse : bulkResponse) {
					if (bulkItemResponse.isFailed()) {
						BulkItemResponse.Failure failure = bulkItemResponse.getFailure();

						logger.error("[bulkMap]" + failure.toString());

						retCount[3]++;
					}

					switch (bulkItemResponse.getOpType()) {
					case INDEX:
						retCount[0]++;

						break;
					case CREATE:
						retCount[0]++;

						break;
					case UPDATE:
						retCount[1]++;

						break;
					case DELETE:
						retCount[2]++;
					}
				}
			}
		} catch (Exception e) {
			throw new Exception("[OpenSearchUtil:bulkMap (index : " + indexName + ")]" + e.toString());
		}

		return retCount;
	}

	public int[] bulkMap2(String indexName, String indexType, String _action, List<Map<String, Object>> mapData)
			throws Exception {
		int retCount[] = new int[] { 0, 0, 0, 0 };

		BulkRequest request = new BulkRequest();

		String _index = indexName;
		String _id = "";

		logger.info("mapData size: {}", mapData.size());

		for (Map<String, Object> mData : mapData) {
			if (mData.containsKey("_id")) {
				_id = StringUtil.checkNull(mData.get("_id"));
				mapData.remove("_id");
			}

			if (mData.containsKey("id")) {
				_id = StringUtil.checkNull(mData.get("id"));
			}

			Gson gson = new Gson();
			String mapContents = gson.toJson(mData);

			logger.debug("action : " + _action + "/" + _index + "/" + _id);
			logger.debug("map : " + mapContents);

			_id = StringUtil.replace(_id, "\"", "");

			if ("index".equals(_action)) {
				request.add(new IndexRequest(_index).id(_id).source(mapContents, XContentType.JSON));
			} else if ("update".equals(_action)) {
				UpdateRequest update = new UpdateRequest(_index, _id);
				update.doc(mapContents, XContentType.JSON);
				update.docAsUpsert(true);

				request.add(update);
			} else if ("delete".equals(_action)) {
				request.add(new DeleteRequest(_index, _id));
			} else {
				logger.error("ERROR " + mapData.toString());
			}
		}

		try {
			BulkResponse bulkResponse = client.bulk(request, RequestOptions.DEFAULT);

			if (bulkResponse.hasFailures()) {
				for (BulkItemResponse bulkItemResponse : bulkResponse) {
					if (bulkItemResponse.isFailed()) {
						BulkItemResponse.Failure failure = bulkItemResponse.getFailure();

						logger.error("[bulkMap]" + failure.toString());

						retCount[3]++;
					}
				}
			} else {
				for (BulkItemResponse bulkItemResponse : bulkResponse) {
					if (bulkItemResponse.isFailed()) {
						BulkItemResponse.Failure failure = bulkItemResponse.getFailure();

						logger.error("[bulkMap]" + failure.toString());

						retCount[3]++;
					}

					switch (bulkItemResponse.getOpType()) {
						case INDEX:
							retCount[0]++;

							break;
						case CREATE:
							retCount[0]++;

							break;
						case UPDATE:
							retCount[1]++;

							break;
						case DELETE:
							retCount[2]++;
					}
				}
			}
		} catch (Exception e) {
			throw new Exception("[OpenSearchUtil:bulkMap (index : " + indexName + ")]" + e.toString());
		}

		return retCount;
	}


	public int deleteByQuery(String index, String field, String value) throws IOException {
		int deletedCount = 0;

		// DeleteByQueryRequest 생성
		DeleteByQueryRequest request = new DeleteByQueryRequest(index);
		request.setQuery(QueryBuilders.matchQuery(field, value));
		logger.info(request.toString());

		try {
			// Delete By Query 실행
			BulkByScrollResponse bulkResponse = client.deleteByQuery(request, RequestOptions.DEFAULT);
			
			deletedCount = (int) bulkResponse.getDeleted();
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		} finally {
			// 클라이언트 종료
			client.close();
		}

		return deletedCount;
	}
}