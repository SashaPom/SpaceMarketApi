package com.cosmo.cats.api.service;

import com.cosmo.cats.api.domain.Wearer;
import com.cosmo.cats.api.domain.product.Product;
import com.cosmo.cats.api.repository.projection.ProductProjection;
import java.util.List;

public interface ProductService {
    List<Product> getProducts();

    Product getProduct(Long id);

    Product createProduct(Product product, Long categoryId);

    Product updateProduct(Long id, Product product, Long categoryId);

    void deleteProduct(Long id);

    List<Product> getProductsByWearer(Wearer wearer);

    List<ProductProjection> getMostOrderedProducts();
}
