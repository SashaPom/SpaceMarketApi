package com.cosmo.cats.api.web;

import static com.cosmo.cats.api.service.exception.OrderNotFoundException.ORDER_NOT_FOUND;
import static com.cosmo.cats.api.service.exception.ProductNotFoundException.PRODUCT_NOT_FOUND_ID;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.cosmo.cats.api.AbstractIt;
import com.cosmo.cats.api.dto.order.OrderRequestEntry;
import com.cosmo.cats.api.featuretoggle.FeatureToggleExtension;
import com.cosmo.cats.api.repository.OrderRepository;
import com.cosmo.cats.api.repository.ProductRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.util.List;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

@AutoConfigureMockMvc
@ExtendWith(FeatureToggleExtension.class)
public class OrderControllerIT extends AbstractIt {
    private final String URL = "/api/v1/orders";
    private final List<OrderRequestEntry> ORDER_ENTRY_REQUEST_LIST = getOrderRequestEntryList();
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    ObjectMapper objectMapper;

    @AfterEach
    public void cleanAll() {
        orderRepository.deleteAll();
        productRepository.deleteAll();
    }

    @Test
    @SneakyThrows
    @Sql("/sql/products-create.sql")
    public void shouldAddToOrder() {
        mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ORDER_ENTRY_REQUEST_LIST)))
                .andExpectAll(status().isOk());
    }

    @Test
    @SneakyThrows
    @Sql("/sql/products-create.sql")
    public void shouldThrowOrderNotFoundException() {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND,
                String.format(ORDER_NOT_FOUND, "205c43d0-e6c1-4a78-b7b7-3dfdfdff67ea"));
        problemDetail.setType(URI.create("order-not-found"));
        problemDetail.setTitle("Order not found");

        mockMvc.perform(post(URL + "/205c43d0-e6c1-4a78-b7b7-3dfdfdff67ea").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ORDER_ENTRY_REQUEST_LIST))).andExpectAll(status().isNotFound());
    }


    private List<OrderRequestEntry> getOrderRequestEntryList() {
        return List.of(
                OrderRequestEntry.builder().productName("Cat Star Scratcher").amount(2).build(),
                OrderRequestEntry.builder().productName("Cat Star Toy").amount(3).build(),
                OrderRequestEntry.builder().productName("Kitty Star Treats").amount(1).build()
        );
    }
}
