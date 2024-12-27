package com.cosmo.cats.api.service.impl;

import com.cosmo.cats.api.domain.Wearer;
import com.cosmo.cats.api.domain.category.Category;
import com.cosmo.cats.api.domain.product.Product;
import com.cosmo.cats.api.repository.ProductRepository;
import com.cosmo.cats.api.repository.entity.ProductEntity;
import com.cosmo.cats.api.repository.projection.ProductProjection;
import com.cosmo.cats.api.service.ProductService;
import com.cosmo.cats.api.service.exception.DuplicateProductNameException;
import com.cosmo.cats.api.service.exception.ProductNotFoundException;
import com.cosmo.cats.api.web.mapper.ProductDtoMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductDtoMapper productMapper;

    @Override
    public List<Product> getProducts() {
        return productMapper.toProductList(productRepository.findAll());
    }

    @Override
    public List<ProductProjection> getMostOrderedProducts() {
        return productRepository.findMostFrequentlyOrderedProduct();
    }

    @Override
    public Product getProduct(Long id) {
        return productMapper.toProductFromEntity(
                productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException(id)));
    }

    @Override
    @Transactional
    public Product createProduct(Product product, Long categoryId) {
        ProductEntity newProduct =
                productMapper.toProductEntity(buildProduct(product, categoryId, setId()));
        return productMapper.toProductFromEntity(productRepository.save(newProduct));
    }

    @Transactional
    @Override
    public Product updateProduct(Long id, Product updatedProduct, Long categoryId) {
        if (!existById(id)) {
            var savedProduct = productRepository.save(
                    productMapper.toProductEntity(buildProduct(updatedProduct, categoryId, id)));
            return productMapper.toProductFromEntity(savedProduct);
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
        productRepository.save(productMapper.toProductEntity(productWithUpdates));
        return productWithUpdates;
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    @Override
    public List<Product> getProductsByWearer(Wearer wearer) {
        return getProducts().stream().filter(product -> product.getWearer() == wearer).toList();
    }

    private boolean existByName(String productName) {
        return productRepository.findAll().stream()
                .anyMatch(product -> product.getName().equals(productName));
    }

    private boolean existById(Long productId) {
        return productRepository.findAll().stream()
                .anyMatch(product -> product.getId().equals(productId));
    }

    private long setId() {
        long id = productRepository.findAll().size() + 1;
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