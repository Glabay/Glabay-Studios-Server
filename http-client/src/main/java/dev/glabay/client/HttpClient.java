package dev.glabay.client;

import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

/**
 * @author Glabay | Glabay-Studios
 * @project Glabay-Studios-Server
 * @social Discord: Glabay
 * @since 2024-11-26
 */
@Service
public abstract class HttpClient {

    private final RestClient restClient;

    public HttpClient() {
        restClient = RestClient.builder()
            .requestFactory(new JdkClientHttpRequestFactory())
            .build();
    }
    public RestClient getRestClient() {
        return restClient;
    }
}
