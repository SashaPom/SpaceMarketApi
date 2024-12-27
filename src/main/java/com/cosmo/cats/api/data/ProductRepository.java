package com.cosmo.cats.api.data;

import com.cosmo.cats.api.domain.product.Product;
import java.util.List;
import java.util.Optional;

public interface ProductRepository {
    Optional<Product> getById(Long id);

    List<Product> getAll();

    Optional<Product> update(Long id, Product updatedProduct);

    void delete(Long id);

    Product addProduct(Product product);

    void resetRepository();
}
