package com.cosmo.cats.api.service.impl;

import com.cosmo.cats.api.data.ProductRepository;
import com.cosmo.cats.api.domain.category.Category;
import com.cosmo.cats.api.domain.product.Product;
import com.cosmo.cats.api.service.ProductService;
import com.cosmo.cats.api.service.exception.DuplicateProductNameException;
import com.cosmo.cats.api.service.exception.ProductNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Override
    public List<Product> getProducts() {
        return productRepository.getAll();
    }

    @Override
    public Product getProduct(Long id) {
        return productRepository.getById(id).orElseThrow(() -> new ProductNotFoundException(id));
    }

    @Override
    public Product createProduct(Product product, Long categoryId) {
        Product newProduct = buildProduct(product, categoryId, setId());
        return productRepository.addProduct(newProduct);
    }

    @Override
    public Product updateProduct(Long id, Product updatedProduct, Long categoryId) {
        if (!existById(id)) {
            return productRepository.addProduct(buildProduct(updatedProduct, categoryId, id));
        }
        Product existingProduct = getProduct(id);
        if (existByName(updatedProduct.getName())
                && !updatedProduct.getName().equals(existingProduct.getName())) {
            throw new DuplicateProductNameException(updatedProduct.getName());
        }
        Product productWithUpdates = updatedProduct.toBuilder()
                .category(Category.builder().id(categoryId).name("Space item").build())
                .id(id)
                .build();
        productRepository.update(id, productWithUpdates);
        return productWithUpdates;
    }

    @Override
    public void deleteProduct(Long id) {
        productRepository.delete(id);
    }

    private boolean existByName(String productName) {
        return productRepository.getAll().stream()
                .anyMatch(product -> product.getName().equals(productName));
    }

    private boolean existById(Long productId) {
        return productRepository.getAll().stream()
                .anyMatch(product -> product.getId().equals(productId));
    }

    private long setId() {
        long id = productRepository.getAll().size() + 1;
        return existById(id) ? id + 1 : id;
    }

    private Product buildProduct(Product product, Long categoryId, Long id) {
        if (existByName(product.getName())) {
            throw new DuplicateProductNameException(product.getName());
        }
        return product.toBuilder()
                .category(Category.builder().id(categoryId).name("Space item").build())
                .id(id)
                .build();
    }

}