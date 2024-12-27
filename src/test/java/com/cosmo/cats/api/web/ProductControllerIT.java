package com.cosmo.cats.api.web;


import static com.cosmo.cats.api.domain.Wearer.CATS;
import static com.cosmo.cats.api.domain.Wearer.KITTIES;
import static com.cosmo.cats.api.service.exception.DuplicateProductNameException.PRODUCT_WITH_NAME_EXIST_MESSAGE;
import static com.cosmo.cats.api.service.exception.ProductNotFoundException.PRODUCT_NOT_FOUND_ID;
import static com.cosmo.cats.api.util.SecurityUtil.PRODUCT_API_KEY_HEADER;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.cosmo.cats.api.AbstractIt;
import com.cosmo.cats.api.annotation.DisableFeatureToggle;
import com.cosmo.cats.api.annotation.EnableFeatureToggle;
import com.cosmo.cats.api.dto.product.ProductCreationDto;
import com.cosmo.cats.api.dto.product.ProductUpdateDto;
import com.cosmo.cats.api.dto.product.advisor.MarketComparisonDto;
import com.cosmo.cats.api.dto.product.advisor.ProductAdvisorResponseDto;
import com.cosmo.cats.api.featuretoggle.FeatureToggleExtension;
import com.cosmo.cats.api.repository.OrderRepository;
import com.cosmo.cats.api.repository.ProductRepository;
import com.cosmo.cats.api.service.ProductAdvisorService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import java.math.BigDecimal;
import java.net.URI;
import java.util.List;
import java.util.stream.Stream;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;


@AutoConfigureMockMvc
@ExtendWith(FeatureToggleExtension.class)
public class ProductControllerIT extends AbstractIt {

  private final String URL = "/api/v1/products";
  private final ProductCreationDto PRODUCT_CREATION = buildProductCreationDto("Star mock");
  private final ProductUpdateDto PRODUCT_UPDATE = buildProductUpdateDto("Cat Star Toy");
  private final ProductAdvisorResponseDto PRODUCT_ADVISOR_RESPONSE =
      buildProductAdvisorResponseDto();

  @Autowired
  MockMvc mockMvc;
  @Autowired
  ProductRepository productRepository;
  @MockBean
  JwtDecoder jwtDecoder;
  @Autowired
  OrderRepository orderRepository;
  @Autowired
  ObjectMapper objectMapper;
  @SpyBean
  ProductAdvisorService productAdvisorService;

  private static ProductCreationDto buildProductCreationDto(String name) {
    return ProductCreationDto.builder().name(name).description("Mock description").price(
        BigDecimal.valueOf(777)).stockQuantity(10).wearer("CATS").build();
  }

  private static ProductUpdateDto buildProductUpdateDto(String name) {
    return ProductUpdateDto.builder().name(name).description("Update description").price(
        BigDecimal.valueOf(126)).stockQuantity(12).wearer("CATS").build();
  }

  private static Stream<ProductCreationDto> buildUnValidProductCreationDto() {
    return Stream.of(
        buildProductCreationDto(""),
        buildProductCreationDto("Name without required words"),
        buildProductCreationDto(null),
        buildProductCreationDto("galaxy mock").toBuilder().price(BigDecimal.valueOf(0.002))
            .build(),
        buildProductCreationDto("galaxy").toBuilder().stockQuantity(-2).build());
  }

  private static Stream<ProductUpdateDto> buildUnValidProductUpdateDto() {
    return Stream.of(
        buildProductUpdateDto(""),
        buildProductUpdateDto("Name without required words"),
        buildProductUpdateDto(null),
        buildProductUpdateDto("galaxy mock").toBuilder().price(BigDecimal.valueOf(0.002))
            .build(),
        buildProductUpdateDto("galaxy").toBuilder().stockQuantity(-2).build());
  }

  @AfterEach
  void setUp() {
    orderRepository.deleteAll();
    productRepository.deleteAll();
    reset(productAdvisorService);
  }
  @BeforeEach
  void setUpMock() {
    Jwt mockJwt = Jwt.withTokenValue("dummy-token")
        .header("alg", "none")
        .claim("access", "ProductApi")
        .claim("authorities", List.of())
        .build();
    when(jwtDecoder.decode(anyString())).thenReturn(mockJwt);
  }
  @Test
  @SneakyThrows
  @WithMockUser
  @Sql({"/sql/products-create.sql", "/sql/order-create.sql", "/sql/order-entry-create.sql"})
  public void shouldAnalyzeProducts(){
    mockMvc.perform(get(URL + "/analyze")
            .header(PRODUCT_API_KEY_HEADER, "Bearer dummy-token")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .with(csrf()))
            .andExpectAll(status().isOk(),
                    jsonPath("$.length()").value(2),
                    jsonPath("$[0].name").value("Cat Star Scratcher"));
  }

  @Test
  @SneakyThrows
  @DisableFeatureToggle(CATS)
  @WithMockUser
  void shouldGet404FeatureDisabled() {
    mockMvc.perform(get("/api/v1/products/wearer/{wearer}", CATS)
        .header(PRODUCT_API_KEY_HEADER, "Bearer dummy-token")
        .with(csrf())).andExpect(status().isNotFound());
  }

  @Test
  @SneakyThrows
  @EnableFeatureToggle(KITTIES)
  @WithMockUser
  void shouldGet200() {
    mockMvc.perform(get("/api/v1/products/wearer/{wearer}", KITTIES)
        .header(PRODUCT_API_KEY_HEADER, "Bearer dummy-token")
        .contentType(MediaType.APPLICATION_JSON)
        .with(csrf())).andExpect(status().isOk());
  }

  @Test
  @SneakyThrows
  @Sql("/sql/products-create.sql")
  @WithMockUser
  void shouldReturnAllProducts() {
    mockMvc.perform(get(URL)
        .header(PRODUCT_API_KEY_HEADER, "Bearer dummy-token")
        .with(csrf())).andExpectAll(
        status().isOk(),
        jsonPath("$.length()").value(6)
    );
  }

  @Test
  @SneakyThrows
  @WithMockUser
  @Sql("/sql/products-create.sql")
  void shouldReturnProductById() {
    var product = productRepository.findByNameIgnoreCase("Cat Star Toy").get();
    var id = product.getId();
    mockMvc.perform(get(URL + "/{id}", id).with(csrf())
            .header(PRODUCT_API_KEY_HEADER, "Bearer dummy-token"))
        .andExpectAll(status().isOk(),
            jsonPath("$.category.id").value(1),
            jsonPath("$.name").value("Cat Star Toy"));
  }

  @Test
  @SneakyThrows
  @WithMockUser
  void shouldThrowProductNotFoundException() {
    ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND,
        String.format(PRODUCT_NOT_FOUND_ID, "999"));
    problemDetail.setType(URI.create("product-not-found"));
    problemDetail.setTitle("Product not found");

    mockMvc.perform(get(URL + "/{id}", 999L).with(csrf())
        .header(PRODUCT_API_KEY_HEADER, "Bearer dummy-token")).andExpectAll(
        status().isNotFound(),
        content().json(objectMapper.writeValueAsString(problemDetail)));
  }

  @Test
  @SneakyThrows
  @WithMockUser(roles = "CAT_MODERATOR")
  @Sql("/sql/products-create.sql")
  void shouldDeleteProduct() {
    mockMvc.perform(delete(URL + "/{id}", 2L).with(csrf())
            .header(PRODUCT_API_KEY_HEADER, "Bearer dummy-token"))
        .andExpect(status().isNoContent());

    var length = productRepository.findAll().size();

    assertThat(length).isEqualTo(5);
  }

  @Test
  @SneakyThrows
  @WithMockUser(roles = "CAT_MODERATOR")
  @Sql("/sql/products-create.sql")
  void shouldCreateProduct() {
    var result = mockMvc.perform(
        post(URL + "/category/{id}", 2L).with(csrf())
            .header(PRODUCT_API_KEY_HEADER, "Bearer dummy-token")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(PRODUCT_CREATION)));

    result.andExpectAll(status().isCreated(),
        jsonPath("$.category").isNotEmpty());
    var length = productRepository.findAll().size();
    assertThat(length).isEqualTo(7);
  }

  @ParameterizedTest
  @MethodSource(value = "buildUnValidProductCreationDto")
  @SneakyThrows
  @WithMockUser
  void shouldThrowMethodArgumentNotValidException(ProductCreationDto productCreationDto) {
    mockMvc.perform(post(URL + "/category/{id}", 2L).with(csrf())
            .header(PRODUCT_API_KEY_HEADER, "Bearer dummy-token")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(
                productCreationDto)))
        .andExpectAll(status().isBadRequest(),
            jsonPath("$.invalidParams").isNotEmpty());

  }

  @Test
  @SneakyThrows
  @WithMockUser(roles = "CAT_MODERATOR")
  @Sql("/sql/products-create.sql")
  void shouldThrowDuplicateProductNameException() {
    ProblemDetail problemDetail =
        ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, String.format(
            PRODUCT_WITH_NAME_EXIST_MESSAGE, "Cat Star Toy"
        ));
    problemDetail.setType(URI.create("this-name-exists"));
    problemDetail.setTitle("Duplicate name");

    mockMvc.perform(post(URL + "/category/{id}", 2L).with(csrf())
            .header(PRODUCT_API_KEY_HEADER, "Bearer dummy-token")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(
                buildProductCreationDto("Cat Star Toy"))))
        .andExpectAll(status().isBadRequest(),
            content().json(objectMapper.writeValueAsString(problemDetail)));
  }

  @Test
  @SneakyThrows
  @WithMockUser(roles = "CAT_MODERATOR")
  @Sql("/sql/products-create.sql")
  void shouldUpdateProduct() {
    var product = productRepository.findByNameIgnoreCase("Cat Star Toy").get();
    var id = product.getId();
    mockMvc.perform(put(URL + "/{id}/category/{categoryId}", id, 2).with(csrf())
            .header(PRODUCT_API_KEY_HEADER, "Bearer dummy-token")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(PRODUCT_UPDATE)))
        .andExpectAll(status().isOk(),
            content().json(objectMapper.writeValueAsString(PRODUCT_UPDATE)));
  }

  @ParameterizedTest
  @MethodSource(value = "buildUnValidProductUpdateDto")
  @SneakyThrows
  @WithMockUser
  void shouldThrowUnValidArgumentsExceptionUpdate(ProductUpdateDto productUpdateDto) {
    mockMvc.perform(post(URL + "/category/{id}", 2L).with(csrf())
            .header(PRODUCT_API_KEY_HEADER, "Bearer dummy-token")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(
                productUpdateDto)))
        .andExpectAll(status().isBadRequest(),
            jsonPath("$.invalidParams").isNotEmpty());
  }

  @Test
  @SneakyThrows
  @WithMockUser
  @Sql("/sql/products-create.sql")
  void shouldComparePrices() {
    var product = productRepository.findByNameIgnoreCase("Cat Star Toy").get();
    var id = product.getId();
    stubFor(WireMock.post("/api/v1/price-comparison")
        .willReturn(aResponse().withStatus(200)
            .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
            .withBody(objectMapper.writeValueAsBytes(PRODUCT_ADVISOR_RESPONSE))));

    mockMvc.perform(get(URL + "/{id}/price-advisor", id).with(csrf())
        .header(PRODUCT_API_KEY_HEADER, "Bearer dummy-token")).andExpectAll(status().isOk(),
        content().json(objectMapper.writeValueAsString(PRODUCT_ADVISOR_RESPONSE)));
  }

  @Test
  @SneakyThrows
  @WithMockUser
  @Sql("/sql/products-create.sql")
  void shouldThrowProductAdvisorApiException() {
    var product = productRepository.findByNameIgnoreCase("Cat Star Toy").get();
    var id = product.getId();
    ProblemDetail problemDetail =
        ProblemDetail.forStatusAndDetail(HttpStatus.SERVICE_UNAVAILABLE,
            "Error while getting price advice");
    problemDetail.setType(URI.create("price-advisor-error"));
    problemDetail.setTitle("Could not get price advice");

    stubFor(WireMock.post("/api/v1/price-comparison")
        .willReturn(aResponse().withStatus(500)));

    mockMvc.perform(get(URL + "/{id}/price-advisor", id).with(csrf())
            .header(PRODUCT_API_KEY_HEADER, "Bearer dummy-token"))
        .andExpectAll(status().isServiceUnavailable(),
            content().json(objectMapper.writeValueAsString(problemDetail)));
  }


  private ProductAdvisorResponseDto buildProductAdvisorResponseDto() {
    return ProductAdvisorResponseDto.builder()
        .originalMarketPrice(BigDecimal.valueOf(100))
        .comparisons(
            List.of(
                MarketComparisonDto.builder()
                    .market("EU")
                    .price(BigDecimal.valueOf(102.50))
                    .priceDifference(BigDecimal.valueOf(2.50))
                    .build(),
                MarketComparisonDto.builder()
                    .market("US")
                    .price(BigDecimal.valueOf(95.00))
                    .priceDifference(BigDecimal.valueOf(-5.10))
                    .build())
        ).build();
  }
}
