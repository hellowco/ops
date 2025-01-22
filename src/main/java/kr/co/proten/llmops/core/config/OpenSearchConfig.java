package kr.co.proten.llmops.core.config;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.ssl.SSLContextBuilder;
import org.opensearch.client.RestClient;
import org.opensearch.client.RestClientBuilder;
import org.opensearch.client.json.jackson.JacksonJsonpMapper;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.transport.rest_client.RestClientTransport;

import javax.net.ssl.SSLContext;
import java.util.List;

public class OpenSearchConfig {

    private OpenSearchConfig() {}

    public static OpenSearchClient createConnection(String[] searchServers, String userName, String password) {
        try {
            // 사용자 인증 정보 설정
            final BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
            credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(userName, password));

            // SSLContext 설정
            SSLContext sslContext = SSLContextBuilder.create().loadTrustMaterial((chain, authType) -> true).build();

            // 서버 리스트 생성
            List<String> serverList = List.of(searchServers);
            RestClientBuilder builder = RestClient.builder(serverList.stream()
                    .map(server -> {
                        String[] parts = server.split(":");
                        return new HttpHost(parts[0], Integer.parseInt(parts[1]), "https");
                    }).toArray(HttpHost[]::new)
            );

            // RestClient 및 OpenSearchClient 생성
            RestClient restClient = builder.setHttpClientConfigCallback(httpClientBuilder -> httpClientBuilder
                    .setDefaultCredentialsProvider(credentialsProvider)
                    .setSSLContext(sslContext)
                    .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
            ).build();

            return new OpenSearchClient(new RestClientTransport(restClient, new JacksonJsonpMapper()));

        } catch (Exception e) {
            throw new RuntimeException("Error creating OpenSearch connection", e);
        }
    }

    public static void closeConnection(OpenSearchClient client) {
        try {
            if (client != null) {
                client._transport().close();
            }
        } catch (Exception e) {
            throw new RuntimeException("Error closing OpenSearch connection", e);
        }
    }
}
