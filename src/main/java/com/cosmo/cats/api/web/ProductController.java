package com.cosmo.cats.api.web;


import com.cosmo.cats.api.domain.Wearer;
import com.cosmo.cats.api.domain.product.Product;
import com.cosmo.cats.api.dto.product.ProductCreationDto;
import com.cosmo.cats.api.dto.product.ProductDto;
import com.cosmo.cats.api.dto.product.ProductUpdateDto;
import com.cosmo.cats.api.dto.product.advisor.ProductAdvisorResponseDto;
import com.cosmo.cats.api.repository.projection.ProductProjection;
import com.cosmo.cats.api.service.ProductAdvisorService;
import com.cosmo.cats.api.service.ProductService;
import com.cosmo.cats.api.web.mapper.ProductDtoMapper;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/products")
public class ProductController {

    private final ProductService productService;
    private final ProductDtoMapper productDtoMapper;
    private final ProductAdvisorService productAdvisorService;

    @GetMapping
    public ResponseEntity<List<ProductDto>> getProducts() {
        return ResponseEntity.ok(productDtoMapper.toProductDto(productService.getProducts()));
    }

    @GetMapping("/analyze")
    public ResponseEntity<List<ProductProjection>> getMostOrderedProducts() {
        return ResponseEntity.ok(productService.getMostOrderedProducts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProduct(@PathVariable Long id) {
        return ResponseEntity.ok(productDtoMapper.toProductDto(productService.getProduct(id)));
    }

    @GetMapping("/{id}/price-advisor")
    public ResponseEntity<ProductAdvisorResponseDto> getProductWithPriceAdvisor(
            @PathVariable Long id) {
        Product product = productService.getProduct(id);
        return ResponseEntity.ok(productAdvisorService.getProductPriceAdvice(product));
    }

    @PostMapping("/category/{id}")
    @PreAuthorize("hasRole('CAT_MODERATOR')")
    public ResponseEntity<ProductDto> createProduct(
            @RequestBody @Valid ProductCreationDto productDto,
            @PathVariable Long id) {
        return new ResponseEntity<>(productDtoMapper.toProductDto(
                productService.createProduct(productDtoMapper.toProduct(productDto), id)),
                HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('CAT_MODERATOR')")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/category/{categoryId}")
    @PreAuthorize("hasRole('CAT_MODERATOR')")
    public ResponseEntity<ProductDto> updateProduct(
            @PathVariable Long id, @PathVariable Long categoryId,
            @RequestBody @Valid ProductUpdateDto productDto) {
        return ResponseEntity.ok(productDtoMapper.toProductDto(
                productService.updateProduct(id, productDtoMapper.toProduct(productDto),
                        categoryId)));
    }

    @GetMapping("/wearer/{wearer}")
    public ResponseEntity<List<ProductDto>> getProductsByWearer(@PathVariable String wearer) {
        return ResponseEntity.ok(productService.getProductsByWearer(Wearer.valueOf(wearer)).stream()
                .map(productDtoMapper::toProductDto).toList());
    }
}