package com.cosmo.cats.api.config;

import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.ClientHttpRequestFactories;
import org.springframework.boot.web.client.ClientHttpRequestFactorySettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfiguration {

  private final int responseTimeout;

  public RestClientConfiguration(
      @Value("${application.rest-client.response-timeout}") int responseTimeout) {
    this.responseTimeout = responseTimeout;
  }

  private static ClientHttpRequestFactory getClientHttpRequestFactory(int responseTimeout) {
    ClientHttpRequestFactorySettings settings = ClientHttpRequestFactorySettings.DEFAULTS
        .withReadTimeout(Duration.ofMillis(responseTimeout));
    return ClientHttpRequestFactories.get(settings);
  }

  @Bean("advisorClient")
  public RestClient restClient() {
    return RestClient.builder()
        .requestFactory(getClientHttpRequestFactory(responseTimeout))
        .build();
  }
}
