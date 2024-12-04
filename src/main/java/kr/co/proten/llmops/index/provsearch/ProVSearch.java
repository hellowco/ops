package kr.co.proten.llmops.index.provsearch;

import lombok.Getter;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.opensearch.action.search.MultiSearchRequest;
import org.opensearch.action.search.MultiSearchResponse;
import org.opensearch.action.search.SearchRequest;
import org.opensearch.action.search.SearchResponse;
import org.opensearch.client.RequestOptions;
import org.opensearch.client.RestClient;
import org.opensearch.client.RestHighLevelClient;
import org.opensearch.common.geo.GeoDistance;
import org.opensearch.common.unit.DistanceUnit;
import org.opensearch.core.common.text.Text;
import org.opensearch.index.query.*;
import org.opensearch.search.SearchHit;
import org.opensearch.search.builder.SearchSourceBuilder;
import org.opensearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.opensearch.search.fetch.subphase.highlight.HighlightField;
import org.opensearch.search.sort.FieldSortBuilder;
import org.opensearch.search.sort.GeoDistanceSortBuilder;
import org.opensearch.search.sort.ScoreSortBuilder;
import org.opensearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.*;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.*;

public class ProVSearch {
	private static final Logger logger = LoggerFactory.getLogger(ProVSearch.class);
	private final ProSearchProperties proSearchProperties;

	private StringBuilder debugMsgBuffer;
	private StringBuilder errorMsgBuffer;
	private StringBuilder warnMsgBuffer;

	private final boolean isDebug;
	private String query = "";
	private String vecQuery = "";

	@Getter
	private final static MultiSearchResponse multiSearchResponse = null;

	private final static int totalListCount = 1000; // 리스트 경우, 가져오는 사이즈 지정

	public RestHighLevelClient client = null;
	public Map<String, Integer> multiSearchIndex;
	Map<String, Object> searchMap;

	private final String searchServer;

	/**
	 * ProSearch 생성자를 생성한다.
	 *
	 * @param isDebug :: Debug 모드로 값을 화면에서 확인할 경우 사용한다. Default : False
	 */
	public ProVSearch(boolean isDebug, String[] esIndex, ProSearchProperties proSearchProperties,
                      ProSearchIndexProperties proSearchIndexProperties) {
		this.isDebug = isDebug;
		this.debugMsgBuffer = new StringBuilder();
		this.errorMsgBuffer = new StringBuilder();
		this.warnMsgBuffer = new StringBuilder();
		this.multiSearchIndex = new HashMap<>();
		this.searchMap = new HashMap<>();
		this.proSearchProperties = proSearchProperties;
		this.searchServer = proSearchProperties.getSearchIP() + ":" + proSearchProperties.getSearchPort();

		for (int idx = 0; idx < esIndex.length; idx++) {
			if (!proSearchIndexProperties.getSort().get(idx).trim().isEmpty()) {
				this.setSortField(esIndex[idx], proSearchIndexProperties.getSort().get(idx).trim());
			}

			if (!proSearchIndexProperties.getSearchField().get(idx).trim().isEmpty()) {
				this.setSearchField(esIndex[idx], proSearchIndexProperties.getSearchField().get(idx).trim());
			}

			if (!proSearchIndexProperties.getIncludeField().get(idx).trim().isEmpty()) {
				this.setIncludeField(esIndex[idx], proSearchIndexProperties.getIncludeField().get(idx).trim());
			}

			if (!proSearchIndexProperties.getExcludeField().get(idx).trim().isEmpty()) {
				this.setExcludeField(esIndex[idx], proSearchIndexProperties.getExcludeField().get(idx).trim());
			}

			if (!proSearchIndexProperties.getHighlightField().get(idx).trim().isEmpty()) {
				this.setHighlightField(esIndex[idx], proSearchIndexProperties.getHighlightField().get(idx).trim());
			}

			if (!proSearchIndexProperties.getFilterQuery().get(idx).trim().isEmpty()) {
				this.setFilterQuery(esIndex[idx], proSearchIndexProperties.getFilterQuery().get(idx).trim());
			}
		}
	}

	/**
	 * 검색엔진서버에 접속한다
	 *
	 * @return
	 */
	public synchronized boolean connection() {
		String[] servers = ProUtils.split(searchServer, proSearchProperties.getSpecialCharComma());
		boolean isConnection = false;
		List<HttpHost> hostList = new ArrayList<>();

		for (String server : servers) {
			String host = ProUtils.split(server, ":")[0];
			String sport = ProUtils.split(server, ":")[1];
			int port = ProUtils.parseInt(sport, 19200);
			hostList.add(new HttpHost(host, port, "https"));
		}

		final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
		credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials("admin", "proten1!"));

		try {
			client = new RestHighLevelClient(
					RestClient.builder(hostList.toArray(new HttpHost[hostList.size()]))
							.setHttpClientConfigCallback(httpClientBuilder -> httpClientBuilder
									.setDefaultCredentialsProvider(credentialsProvider)
									.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)));
		} catch (Exception e) {
			appendERROR("OpenSearch Connection Error: " + e.getMessage());

			logger.error("OpenSearch Connection Error: {}", e.getMessage());
		}

		if (client != null) {
			isConnection = true;
		}

		appendDebug("OpenSearch Connection URL : " + searchServer);
		appendDebug("OpenSearch Connection : " + isConnection);

		return isConnection;
	}

	/**
	 * 검색엔진서버에 접속을 종료한다.
	 *
	 * @return
	 */
	public synchronized void closeConnection() {
		try {
			if (client != null) {
				client.close();
				client = null;
			}
		} catch (IOException e) {
			appendERROR(e.getMessage());
		}
	}

	/**
	 * 설정된 값으로 조건을 조합하여 msearch 에 검색을 요청한다.
	 *
	 * @param query   ::: 검색어
	 * @param esIndex ::: index List
	 * @return
	 */
	public MultiSearchResponse doSearch(String query, String[] esIndex) {
		return doSearch(query, esIndex, "TOTAL", "", 0);
	}

	public MultiSearchResponse doVSearch(String query, String vQuery, String[] esIndex, int knnK) {
		return doVSearch(query, vQuery, esIndex, "TOTAL", "", knnK);
	}

	/**
	 * 설정된 값으로 조건을 조합하여 msearch 에 검색을 요청한다.
	 *
	 * @param query   ::: 검색어
	 * @param esIndex ::: index List
	 * @param addInfo ::: 사용자 정보
	 * @return
	 */
	public MultiSearchResponse doSearch(String query, String[] esIndex, String service, String addInfo, int knnK) {
		this.query = query;
		this.vecQuery = "";

		MultiSearchRequest multiSearchRequest = new MultiSearchRequest();

		for (int idx = 0; idx < esIndex.length; idx++) {
			SearchRequest searchRequest = searchRequest(esIndex[idx], knnK);

			multiSearchRequest.add(searchRequest);
			multiSearchIndex.put(esIndex[idx], idx);
		}

		MultiSearchResponse multiSearchResponse = msearch(multiSearchRequest);

		logger.debug("multiSearchResponse: {}", multiSearchResponse);

		return multiSearchResponse;
	}

	/**
	 * 설정된 값으로 조건을 조합하여 msearch 에 검색을 요청한다.
	 *
	 * @param query   ::: 검색어
	 * @param esIndex ::: index List
	 * @param addInfo ::: 사용자 정보
	 * @return
	 */
	public MultiSearchResponse doVSearch(String query, String vecQuery, String[] esIndex, String service,
										 String addInfo, int knnK) {
		this.query = query;
		this.vecQuery = vecQuery;

		MultiSearchRequest multiSearchRequest = new MultiSearchRequest();

		for (int idx = 0; idx < esIndex.length; idx++) {
			SearchRequest searchRequest = searchRequest(esIndex[idx], knnK);

			logger.debug("searchRequest: {}", searchRequest.toString());

			multiSearchRequest.add(searchRequest);
			multiSearchIndex.put(esIndex[idx], idx);
		}

		MultiSearchResponse multiSearchResponse = msearch(multiSearchRequest);

		logger.debug("searchResponse: {}", multiSearchResponse);

		return multiSearchResponse;
	}

	/**
	 * 필드값에 설정된 값을 리턴한다.
	 *
	 * @param indexName
	 * @param type
	 * @return
	 */
	public String getMapValue(String indexName, String type) {
		return ProUtils.nvl(searchMap.get(indexName + "@" + type), "");
	}

	public SearchRequest searchRequest(String indexName, int knnK) {
		if (!"".equals(indexName)) {
			SearchSourceBuilder searchBuilder = new SearchSourceBuilder();
			searchBuilder.trackTotalHits(true);

			String includeField = getMapValue(indexName, "INCLUDE");
			String excludeField = getMapValue(indexName, "EXCLUDE");

			if (!"".equals(includeField) || !"".equals(excludeField)) {
				String[] includeFields = includeField.split(proSearchProperties.getSpecialCharComma());
				String[] excludeFields = excludeField.split(proSearchProperties.getSpecialCharComma());

				searchBuilder.fetchSource(includeFields, excludeFields);

				appendDebug("INCLUDE_FIELD : " + includeField);
				appendDebug("EXCLUDE_FIELD : " + excludeField);
			}

			String[] searchFields = null;
			if(indexName.split("_")[1].equals("file")){ //index 명이 file 로 끝나면 orgFile과 content 검색
				searchFields = new String[]{"orgFileName", "content"};
			} else {
				searchFields = getMapValue(indexName, "SEARCH").split(proSearchProperties.getSpecialCharComma());
			}

			BoolQueryBuilder boolQuery = new BoolQueryBuilder();
			BoolQueryBuilder searchQuery = new BoolQueryBuilder();
			{
				if (query.isEmpty()) {
					searchQuery.must(QueryBuilders.matchAllQuery());
				} else {
					List<QueryBuilder> listQuery = new ArrayList<>();
					QueryBuilder common;

					if (!vecQuery.isEmpty()) { // 벡터 값이 있는 경우
						common = getSimpleQuery(searchFields, query, 0); // simple_query_string 생성
						String knnQuery = getKNNQuery("content_vec", vecQuery, knnK); // 벡터 쿼리 생성
						QueryBuilder vQuery = QueryBuilders.wrapperQuery("{" + knnQuery + "}"); // 벡터쿼리 wrapperQuery로 생성

						listQuery.add(vQuery); // 벡터쿼리 추가
					} else { // 벡터 값이 없는 경우
						common = getSimpleQuery(searchFields, query, 0);
					}

					if (common != null) {
						listQuery.add(common); // simple_query_string 추가
					}

					for (QueryBuilder qBuild : listQuery) {
						searchQuery.should(qBuild);
					}
				}
			}

			boolQuery.must(searchQuery);

			String default_string_query = getMapValue(indexName, "QUERY_STRING");

			if (!"".equals(default_string_query)) {
				if (default_string_query.contains(",")) {
					String[] strs = ProUtils.split(default_string_query, proSearchProperties.getSpecialCharComma());

					for (String str : strs) {
						QueryBuilder common = getTermQuery(str);

						if (common != null) {
							boolQuery.must(common);
						}
					}
				} else {
					QueryBuilder common = getQueryString(default_string_query);

					if (common != null) {
						boolQuery.must(common);
					}
				}
			}

			BoolQueryBuilder boolFilterQuery = null;
			String default_filter_query = getMapValue(indexName, "FILTER_QUERY");

			if (!"".equals(default_filter_query)) {
				QueryBuilder filter = getQueryString(default_filter_query);

				if (filter != null) {
					boolFilterQuery = new BoolQueryBuilder();

					boolFilterQuery.must(filter);
				}
			}

			String default_not_query = getMapValue(indexName, "NOT_QUERY");

			if (!"".equals(default_not_query)) {
				QueryBuilder common = getQueryString(searchFields, default_not_query); // index의 선언한 sfield 를 대상으로 조회한다.

				if (common != null) {
					if (boolFilterQuery == null) {
						boolFilterQuery = new BoolQueryBuilder();
					}

					boolFilterQuery.mustNot(common);
				}
			}

			if (boolFilterQuery != null) {
				boolQuery.filter(boolFilterQuery);
			}

			searchBuilder.query(boolQuery);

			/* page setting */
			String[] arrPageField = getMapValue(indexName, "PAGE").split(proSearchProperties.getSpecialCharComma());

			if (arrPageField.length == 2) {
				int from = ProUtils.parseInt(arrPageField[0], 0);
				int size = ProUtils.parseInt(arrPageField[1], 0);

				String isFile = indexName.split("_")[1];
				if(isFile.equals("file") || isFile.equals("page")){
					searchBuilder.from(from);
					searchBuilder.size(totalListCount);
					searchBuilder.explain(false);
				} else {
					searchBuilder.from(from);
					searchBuilder.size(size);
					searchBuilder.explain(false);
				}
			} else {
				searchBuilder.from(0);
				searchBuilder.size(10);
				searchBuilder.explain(false);
			}

			/* sort field setting */
			String[] sortFields = getMapValue(indexName, "SORT").split(proSearchProperties.getSpecialCharComma());

			if (sortFields.length == 1 && "".equals(sortFields[0])) {
				searchBuilder.sort(new ScoreSortBuilder().order(SortOrder.DESC)); // <1>
			} else {
				for (String sortField : sortFields) {
					String[] arr = ProUtils.split(sortField, proSearchProperties.getSpecialCharSlssh());
					String _sort = arr[0];
					String _order = "";

					if (arr.length == 1) {
						_order = "DESC";
					} else {
						_order = arr[1];
					}

					if ("SCORE".equals(_sort) || "RANK".equals(_sort)) {
						searchBuilder.sort(new ScoreSortBuilder().order(SortOrder.DESC)); // <1>
					} else if ("GEO".equals(_sort) && arr.length > 3) {
						double lon = Double.parseDouble(arr[2]);
						double lat = Double.parseDouble(arr[3]);

						GeoDistanceSortBuilder geoSort = new GeoDistanceSortBuilder(arr[1], lon, lat);
						geoSort.unit(DistanceUnit.METERS);
						geoSort.geoDistance(GeoDistance.PLANE);

						searchBuilder.sort(geoSort);
					} else {
						if ("ASC".equals(_order)) {
							searchBuilder.sort(new FieldSortBuilder(_sort).order(SortOrder.ASC)); // <2>
						} else {
							searchBuilder.sort(new FieldSortBuilder(_sort).order(SortOrder.DESC)); // <2>
						}
					}
				}
			}

			/* highlight field setting */
			String strHighlightFields = getMapValue(indexName, "HIGHLIGHT");

			if (strHighlightFields != null && !strHighlightFields.isEmpty()) {
				HighlightBuilder highlighter = getHighlightList(strHighlightFields);
				searchBuilder.highlighter(highlighter);
			}

			SearchRequest searchRequest = new SearchRequest();
			searchRequest.indices(indexName).source(searchBuilder);

			logger.info("GET {}/_search {}", searchRequest.indices()[0], searchRequest.source().toString());

			appendDebug("GET " + searchRequest.indices()[0] + "/_search " + searchRequest.source().toString() + "");

			return searchRequest;
		}

		return null;
	}

	/**
	 * knn 쿼리로 만들어서 반환
	 *
	 * @param vQuery ::: query의 vector 값
	 * @param k      ::: knn에서의 k값
	 * @return
	 */
	private String getKNNQuery(String field, String vQuery, int k) {
		return String.format("\"knn\":{\"%s\": {\"vector\": [%s],\"k\": %d}}", field, vQuery, k);
	}

	/**
	 * simple Query 를 설정한다. Field 에 Comma 를 기준으로 여러 필드들을 설정한다. ( String Array 기준 )
	 * SLASH 를 기준으로 Field 의 가중치를 설정한다.
	 *
	 * @param fields ::: 필드명/가중치
	 * @param query  ::: 검색어
	 * @param andor  ::: 0=OR ::: 1=AND
	 * @return
	 */
	public QueryBuilder getSimpleQuery(String[] fields, String query, int andor) {
		Map<String, Float> fieldInfo = new HashMap<String, Float>();

		for (String field : fields) {
			if (field.contains(proSearchProperties.getSpecialCharSlssh())) {
				String[] _field = ProUtils.split(field, proSearchProperties.getSpecialCharSlssh());
				float boost = 1.0f;

				if (andor == 0 || andor == 2) {
					boost = 10.0f;
				} else {
					try {
						boost = Float.parseFloat(_field[1]);
					} catch (Exception ex) {
						boost = 1.0f;
					}
				}

				fieldInfo.put(_field[0], boost);
			} else {
				if (andor == 0 || andor == 2) {
					fieldInfo.put(field, 10.0f);
				} else {
					fieldInfo.put(field, 1.0f);
				}
			}
		}

		Operator op = Operator.AND;

		if (andor == 0) {
			op = Operator.OR;
		}

		return QueryBuilders.simpleQueryStringQuery(query).fields(fieldInfo).defaultOperator(op);
	}

	public QueryBuilder getQueryString(String query) {
		return QueryBuilders.queryStringQuery(query).defaultOperator(Operator.OR);
	}

	public QueryBuilder getQueryString(String[] fields, String query) {
		Map<String, Float> fieldInfo = new HashMap<String, Float>();

		for (String field : fields) {
			if (field.contains(proSearchProperties.getSpecialCharSlssh())) {
				String[] _field = ProUtils.split(field, proSearchProperties.getSpecialCharSlssh());
				float boost = 1.0f;

				try {
					boost = Float.parseFloat(_field[1]);
				} catch (Exception ex) {
					boost = 1.0f;
				}

				fieldInfo.put(_field[0], 10.0f);
			} else {
				fieldInfo.put(field, 1.0f);
			}
		}

		return QueryBuilders.queryStringQuery(query).fields(fieldInfo).boost(10.0f);
	}

	public QueryBuilder getTermQuery(String str) {
		String[] arQuery = ProUtils.split(str, proSearchProperties.getSpecialCharColon());

		if (arQuery.length == 1) {
			appendERROR("[getTermQuery] Invalid value [ " + str
					+ " ]. Input the value [ fieldname:value1,value2,value3 ] ");

			return null;
		}

		String field = arQuery[0];
		String query = arQuery[1];
		String[] arQuerys = ProUtils.split(query, " ");

		if (query.trim().isEmpty()) {
			return QueryBuilders.termsQuery(field, "");
		}

		Set<String> filter = new HashSet<String>();

		for (String _q : arQuerys) {
			if (_q != null && !_q.isEmpty()) {
				filter.add(_q);
			}
		}

		return QueryBuilders.termsQuery(field, filter);
	}

	/**
	 * highlight 관련 설정을 진행한다.
	 *
	 * @param searchField
	 * @return
	 */
	public HighlightBuilder getHighlightList(String searchField) {
		return getHighlightList(searchField, "experimental", "scan");
	}

	/**
	 * highlight 관련 설정을 진행한다.
	 *
	 * @param searchField
	 * @return
	 */
	public HighlightBuilder getHighlightList(String searchField, String highlighterType, String fragmenter) {
		String[] searchFields = ProUtils.split(searchField, proSearchProperties.getSpecialCharComma());

		return getHighlightList(searchFields, highlighterType, fragmenter);
	}

	/**
	 * Highlight 관련 설정을 진행한다.
	 *
	 * @param searchField     ::: 필드명
	 * @param highlighterType ::: 하이라이트 타입
	 * @param fragmenter      ::: 분석방식
	 * @return
	 */
	public HighlightBuilder getHighlightList(String[] searchField, String highlighterType, String fragmenter) {
		HighlightBuilder highlightBuilder = new HighlightBuilder();

		for (String sfield : searchField) {
			int maxSize = proSearchProperties.HIGHLIGHT_SIZE;

			if (sfield.contains(proSearchProperties.getSpecialCharSlssh())) {
				String[] _sInfo = ProUtils.split(sfield, proSearchProperties.getSpecialCharSlssh());
				sfield = _sInfo[0];
				maxSize = ProUtils.parseInt(_sInfo[1], maxSize);
			}

			HighlightBuilder.Field highlighField = new HighlightBuilder.Field(sfield);
			highlighField.fragmentSize(maxSize);
			highlighField.noMatchSize(maxSize);
			highlightBuilder.field(highlighField);
		}

		highlightBuilder.preTags("<em>");
		highlightBuilder.postTags("</em>");

		highlightBuilder.highlighterType(highlighterType);

		highlightBuilder.fragmenter(fragmenter);

		highlightBuilder.order(HighlightBuilder.Order.SCORE);

		return highlightBuilder;
	}

	/**
	 * single index search ;
	 *
	 * @param searchRequest
	 * @return
	 */

	public SearchResponse search(SearchRequest searchRequest) {
		SearchResponse searchResponse = null;

		try {
			searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
		} catch (IOException e) {
			appendERROR("[search]" + e.getMessage());
		}

		return searchResponse;
	}

	/**
	 * Multi index , type search ;;
	 *
	 * @param searchRequest
	 * @return
	 */
	public MultiSearchResponse msearch(MultiSearchRequest searchRequest) {
		MultiSearchResponse searchResponse = null;

		try {
			if (client == null) {
				connection();
			}

			searchResponse = client.msearch(searchRequest, RequestOptions.DEFAULT);

			for (MultiSearchResponse.Item item : searchResponse.getResponses()) {
				if (item.isFailure()) {
					logger.error(item.getFailureMessage());

					return null;
				}
			}
		} catch (Exception e) {
			appendERROR("[msearch]" + e.getMessage());

			logger.error("[msearch]" + e.getMessage());
		} finally {
			closeConnection();
		}

		return searchResponse;
	}

	/**
	 * 필드 Data 를 가져온다.
	 *
	 * @param hit
	 * @param fieldName
	 * @param def
	 * @param isHighlight
	 * @return
	 */
	public String getFieldData(SearchHit hit, String fieldName, Object def, boolean isHighlight) {
		Map<String, Object> map = hit.getSourceAsMap();
		String _ret = null;

		if (isHighlight) {
			Map<String, HighlightField> _highMap = hit.getHighlightFields();
			HighlightField hlObj = _highMap.get(fieldName);

			if (hlObj != null) {
				Text[] objss = hlObj.getFragments();

				if (objss != null && objss.length > 0) {
					_ret = objss[0].toString();
				} else {
					_ret = String.valueOf(map.getOrDefault(fieldName, def));
				}
			} else {
				_ret = String.valueOf(map.getOrDefault(fieldName, def));
			}
		} else {
			_ret = String.valueOf(map.getOrDefault(fieldName, def));
		}

		return _ret;
	}

	/**
	 * 경고 메시지를 버퍼에 저장한다.
	 *
	 * @param msg 경고 메시지
	 */
	public void appendWARN(String msg) {
		if (isDebug && !msg.isEmpty()) {
			warnMsgBuffer.append("[WARN]").append(msg).append("\n");
		}
	}

	/**
	 * 경고 메시지를 버퍼에 저장한다.
	 *
	 * @param msg 경고 메시지
	 */
	public void appendDebug(String msg) {
		if (isDebug && !msg.isEmpty()) {
			debugMsgBuffer.append("[DEBUG] ").append(msg).append("\n");
		}
	}

	/**
	 * 에러 메시지를 버퍼에 저장한다.
	 *
	 * @param msg 에러 메시지
	 */
	public void appendERROR(String msg) {
		if (!msg.isEmpty()) {
			errorMsgBuffer.append("[ERROR] ").append(msg).append("\n");
		}
	}

	/**
	 * 디버그 정보를 화면에 출력할 경우 메시지를 반환한다.
	 *
	 * @return 디버그 정보 반환
	 */
	public String printDebugView() {
		StringBuilder outBuffer = new StringBuilder();

		if (!errorMsgBuffer.toString().isEmpty()) {
			outBuffer.append(errorMsgBuffer.toString());
		}

		if (!warnMsgBuffer.toString().isEmpty()) {
			outBuffer.append(warnMsgBuffer.toString());
		}

		if (!debugMsgBuffer.toString().isEmpty()) {
			outBuffer.append(debugMsgBuffer.toString());
		}

		return outBuffer.toString();
	}

	public void setQueryLog(String query, String index, double took, long total, String userId) {
		try {
			query = URLEncoder.encode(query, StandardCharsets.UTF_8);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}

		String url = "https://" + searchServer + "/_pro10-querylog";
		String params = "?query=" + query + "&service=" + index + "&cnt=" + total + "&took=" + took + "&addinfo="
				+ userId;

		callPluginAPI2(url + params, "");
	}

	/**
	 * SSL 오류 방지
	 */
	private static void disableSslVerification() {
		try {
			// Create a trust manager that does not validate certificate chains
			TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}

				public void checkClientTrusted(X509Certificate[] certs, String authType) {
				}

				public void checkServerTrusted(X509Certificate[] certs, String authType) {
				}
			} };

			// Install the all-trusting trust manager
			SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(null, trustAllCerts, new SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

			// Create all-trusting host name verifier
			HostnameVerifier allHostsValid = new HostnameVerifier() {
				public boolean verify(String hostname, SSLSession session) {
					return true;
				}
			};

			// Install the all-trusting host verifier
			HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
		} catch (NoSuchAlgorithmException | KeyManagementException e) {
			logger.error("disableSslVerification failed \n {}", e.getMessage());
		}
	}

	public String callPluginAPI2(String url, String params) {
		disableSslVerification();

		StringBuilder receiveMsg = new StringBuilder();
		HttpURLConnection uc = null;

		try {
			URL servletUrl = new URL(url);

			uc = (HttpURLConnection) servletUrl.openConnection();
			uc.setRequestProperty("Content-type", "application/x-www-form-urlencoded");
			uc.setRequestMethod("POST");
			uc.setDoOutput(true);
			uc.setDoInput(true);
			uc.setUseCaches(false);
			uc.setDefaultUseCaches(false);

			String userpass = proSearchProperties.getUsername() + ":" + proSearchProperties.getPassword();
			String basicAuth = "Basic :" + new String(Base64.getEncoder().encode(userpass.getBytes()));

			uc.setRequestProperty("Authorization", basicAuth);

			DataOutputStream dos = new DataOutputStream(uc.getOutputStream());
			dos.write(params.getBytes());
			dos.flush();
			dos.close();

			if (uc.getResponseCode() == HttpURLConnection.HTTP_OK) {
				String currLine = "";

				BufferedReader in = new BufferedReader(new InputStreamReader(uc.getInputStream(), StandardCharsets.UTF_8));

				while ((currLine = in.readLine()) != null) {
					receiveMsg.append(currLine).append("\r\n");
				}

				in.close();
			} else {
				return receiveMsg.toString();
			}
		} catch (Exception ex) {
			logger.error(ex.getMessage());
		} finally {
			uc.disconnect();
		}

		return receiveMsg.toString();
	}

	/**
	 * multiSearchRequest를 초기화 한다.
	 *
	 * @return
	 */
	public synchronized void clear() {
		try {
			this.debugMsgBuffer = new StringBuilder();
			this.errorMsgBuffer = new StringBuilder();
			this.warnMsgBuffer = new StringBuilder();
		} catch (Exception e) {
			logger.error(e.getMessage());

			appendERROR(e.getMessage());
		}
	}

	private void setMapValue(String index, String keySuffix, String value) {
		if (value != null && !value.trim().isEmpty()) {
			searchMap.put(index + "@" + keySuffix, value);
		}
	}

	public void setSearchField(String index, String value) {
		setMapValue(index, "SEARCH", value);
	}

	public void setIncludeField(String index, String value) {
		setMapValue(index, "INCLUDE", value);
	}

	public void setExcludeField(String index, String value) {
		setMapValue(index, "EXCLUDE", value);
	}

	public void setHighlightField(String index, String value) {
		setMapValue(index, "HIGHLIGHT", value);
	}

	public void setFilterQuery(String index, String value) {
		setMapValue(index, "FILTER_QUERY", value);
	}

	public void setQueryString(String index, String value) {
		setMapValue(index, "QUERY_STRING", value);
	}

	public void setPage(String index, String value) {
		setMapValue(index, "PAGE", value);
	}

	public void setSortField(String index, String value) {
		setMapValue(index, "SORT", value);
	}

	public void setNotQueryString(String index, String value) {
		setMapValue(index, "NOT_QUERY", value);
	}
}