package com.cosmo.cats.api.service.impl;

import static org.springframework.http.MediaType.APPLICATION_JSON;

import com.cosmo.cats.api.domain.product.Product;
import com.cosmo.cats.api.dto.product.advisor.ProductAdvisorRequestDto;
import com.cosmo.cats.api.dto.product.advisor.ProductAdvisorResponseDto;
import com.cosmo.cats.api.service.ProductAdvisorService;
import com.cosmo.cats.api.service.ProductServiceMapper;
import com.cosmo.cats.api.service.exception.ProductAdvisorApiException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class ProductAdvisorServiceImpl implements ProductAdvisorService {

    private final ProductServiceMapper productServiceMapper;
    private final RestClient advisorClient;
    private final String productAdvisorUrl;

    public ProductAdvisorServiceImpl(ProductServiceMapper productServiceMapper,
                                     @Qualifier("advisorClient") RestClient restClient,
                                     @Value("${application.price-advisor-service.advisor}")
                                     String productAdvisorUrl) {
        this.productServiceMapper = productServiceMapper;
        this.advisorClient = restClient;
        this.productAdvisorUrl = productAdvisorUrl;
    }

    @Override
    public ProductAdvisorResponseDto getProductPriceAdvice(Product product) {
        ProductAdvisorRequestDto requestDto =
                productServiceMapper.toProductAdvisorRequestDto(product);

        return advisorClient.post()
                .uri(productAdvisorUrl)
                .body(requestDto)
                .contentType(APPLICATION_JSON)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (response, request) -> {
                    throw new ProductAdvisorApiException("Error while getting price advice",
                            HttpStatus.SERVICE_UNAVAILABLE);
                })
                .body(ProductAdvisorResponseDto.class);
    }
}
