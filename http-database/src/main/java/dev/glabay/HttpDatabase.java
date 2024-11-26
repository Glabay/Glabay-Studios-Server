package dev.glabay;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@SpringBootApplication
public class HttpDatabase {

    public static void main(String[] args) {
        SpringApplication.run(HttpDatabase.class, args);
    }

    @Bean
    RestClient restClient() {
        return RestClient.builder()
            .requestFactory(new JdkClientHttpRequestFactory())
            .build();
    }
}
