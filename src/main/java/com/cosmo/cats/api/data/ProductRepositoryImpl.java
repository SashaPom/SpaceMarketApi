package com.cosmo.cats.api.data;

import com.cosmo.cats.api.domain.category.Category;
import com.cosmo.cats.api.domain.product.Product;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class ProductRepositoryImpl implements ProductRepository {
    private final List<Product> products = new ArrayList<>(buildAllProductsMock());


    @Override
    public Optional<Product> getById(Long id) {
        return products.stream()
                .filter(product -> product.getId().equals(id))
                .findFirst();
    }

    @Override
    public List<Product> getAll() {
        return products;
    }

    @Override
    public Optional<Product> update(Long id, Product updatedProduct) {
        delete(id);
        products.add(updatedProduct);
        return Optional.of(updatedProduct);
    }

    @Override
    public void delete(Long id) {
        var toBeDeleted = products.stream().filter(temp -> temp.getId().equals(id)).findFirst();
        if (toBeDeleted.isEmpty()) {
            return;
        }
        products.remove(toBeDeleted.get());
    }

    @Override
    public Product addProduct(Product product) {
        products.add(product);
        return product;
    }
    public void resetRepository() {
        products.clear();
        products.addAll(buildAllProductsMock());
    }

    private List<Product> buildAllProductsMock() {
        return List.of(
                Product.builder()
                        .id(1L)
                        .name("Star Helmet")
                        .description("A durable helmet for intergalactic travel.")
                        .price(BigDecimal.valueOf(299.99))
                        .stockQuantity(50)
                        .category(Category.builder().id(1L).name("Space Gear").build())
                        .build(),
                Product.builder()
                        .id(2L)
                        .name("Anti-Gravity Boots")
                        .description("Experience weightlessness on any surface.")
                        .price(BigDecimal.valueOf(199.99))
                        .stockQuantity(30)
                        .category(Category.builder().id(2L).name("Space Wear").build())
                        .build(),
                Product.builder()
                        .id(3L)
                        .name("Star Map")
                        .description("A holographic map of the known universe.")
                        .price(BigDecimal.valueOf(149.99))
                        .stockQuantity(100)
                        .category(Category.builder().id(3L).name("Space Tools").build())
                        .build()
        );
    }
}
