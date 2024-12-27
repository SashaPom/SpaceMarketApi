package com.cosmo.cats.api.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import com.cosmo.cats.api.data.ProductRepository;
import com.cosmo.cats.api.domain.category.Category;
import com.cosmo.cats.api.domain.product.Product;
import com.cosmo.cats.api.service.ProductService;
import com.cosmo.cats.api.service.exception.DuplicateProductNameException;
import com.cosmo.cats.api.service.exception.ProductNotFoundException;
import com.cosmo.cats.api.service.impl.ProductServiceImpl;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest(classes = {ProductServiceImpl.class})
public class ProductServiceTest {
    private final List<Product> MOCK_PRODUCTS = buildAllProductsMock();
    private final Product MOCK_PRODUCT = buildProductCreation();
    private final Product MOCK_PRODUCT_UPDATED = buildProductCreation().toBuilder().id(1L)
            .category(Category.builder().id(2L).name("Space item").build()).build();
    @MockBean
    ProductRepository productRepository;
    @Autowired
    ProductService productService;
    @Captor
    ArgumentCaptor<Long> idCaptor;
    @Captor
    ArgumentCaptor<Product> productArgumentCaptor;

    private static Stream<Integer> provideId() {
        return Stream.of(0, 1, 2);
    }

    @Test
    void shouldReturnAllProducts() {
        when(productRepository.getAll()).thenReturn(MOCK_PRODUCTS);
        var result = productService.getProducts();

        assertEquals(3, result.size());
    }

    @ParameterizedTest
    @MethodSource("provideId")
    void shouldReturnProductById(Integer id) {
        when(productRepository.getById(idCaptor.capture())).thenAnswer(
                inv -> Optional.of(MOCK_PRODUCTS.get(id)));

        var actualId = id + 1L;
        var result = productService.getProduct(actualId);
        assertNotNull(result.getCategory());
        assertEquals(actualId, result.getId());
        assertEquals("Star Mock" + actualId, result.getName());
    }

    @Test
    void shouldThrowProductNotFoundExceptionWhenIdIsNonExistent() {
        when(productRepository.getById(idCaptor.capture())).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> productService.getProduct(4L));
    }

    @Test
    void shouldCreateProductSuccessfully() {
        when(productRepository.getAll()).thenReturn(MOCK_PRODUCTS);
        when(productRepository.addProduct(productArgumentCaptor.capture())).thenAnswer(
                inv -> inv.getArgument(0));

        var result = productService.createProduct(MOCK_PRODUCT, 2L);

        assertNotNull(result.getCategory());
        assertEquals(2L, result.getCategory().getId());
        assertEquals(4L, result.getId());
    }

    @Test
    void shouldThrowDuplicateProductNameExceptionWhenCreatingWithExistingName() {
        when(productRepository.getAll()).thenReturn(
                List.of(MOCK_PRODUCT.toBuilder().id(1L).build()));

        assertThrows(DuplicateProductNameException.class,
                () -> productService.createProduct(MOCK_PRODUCT, 2L));
    }

    @Test
    void shouldUpdateProductSuccessfully() {
        when(productRepository.getAll()).thenReturn(MOCK_PRODUCTS);
        when(productRepository.update(idCaptor.capture(),
                productArgumentCaptor.capture())).thenAnswer(inv ->
                Optional.of(inv.getArgument(1)));
        when(productRepository.getById(idCaptor.capture())).thenAnswer(
                inv -> Optional.of(MOCK_PRODUCTS.get((int) (long) inv.getArgument(0))));

        var result = productService.updateProduct(1L, MOCK_PRODUCT, 2L);

        assertEquals(MOCK_PRODUCT_UPDATED.getId(), result.getId());
        assertEquals(MOCK_PRODUCT_UPDATED.getCategory(), result.getCategory());
    }

    @Test
    void shouldUpdateProductWithNewIdWhenProductIdIsNonExistent() {
        when(productRepository.getAll()).thenReturn(MOCK_PRODUCTS);
        when(productRepository.addProduct(productArgumentCaptor.capture())).thenAnswer(
                inv -> inv.getArgument(0));

        var result = productService.updateProduct(7L, MOCK_PRODUCT, 2L);

        assertEquals(7L, result.getId());
        assertEquals(2L, result.getCategory().getId());
        assertEquals(MOCK_PRODUCT.getName(), result.getName());
    }

    @Test
    void shouldThrowDuplicateProductNameExceptionWhenUpdatingWithExistingName() {
        when(productRepository.getAll()).thenReturn(MOCK_PRODUCTS);
        when(productRepository.getById(anyLong())).thenReturn(Optional.of(MOCK_PRODUCTS.get(0)));

        assertThrows(DuplicateProductNameException.class,
                () -> productService.updateProduct(1L, MOCK_PRODUCTS.get(1), 2L));
    }


    private Product buildProductCreation() {
        return Product.builder().name("NonExistent name").price(BigDecimal.valueOf(1223))
                .stockQuantity(10).description("Description").build();
    }

    private List<Product> buildAllProductsMock() {
        return List.of(
                Product.builder()
                        .id(1L)
                        .name("Star Mock1")
                        .description("Description mock1")
                        .price(BigDecimal.valueOf(199.99))
                        .stockQuantity(10)
                        .category(Category.builder().id(1L).name("Mock1 category").build())
                        .build(),
                Product.builder()
                        .id(2L)
                        .name("Star Mock2")
                        .description("Description mock2")
                        .price(BigDecimal.valueOf(299.99))
                        .stockQuantity(20)
                        .category(Category.builder().id(2L).name("Mock2 category").build())
                        .build(),
                Product.builder()
                        .id(3L)
                        .name("Star Mock3")
                        .description("Description mock3")
                        .price(BigDecimal.valueOf(399.99))
                        .stockQuantity(30)
                        .category(Category.builder().id(3L).name("Mock3 category").build())
                        .build()
        );
    }
}
