package com.cosmo.cats.api.repository;

import com.cosmo.cats.api.repository.entity.ProductEntity;
import com.cosmo.cats.api.repository.projection.ProductProjection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Long> {
    Optional<ProductEntity> findByNameIgnoreCase(String name);

    @Query("""
                SELECT p FROM ProductEntity p
                JOIN OrderEntryEntity oe ON p.id = oe.product.id
                GROUP BY p.id
                ORDER BY SUM(oe.quantity) DESC
            """)
    List<ProductProjection> findMostFrequentlyOrderedProduct();
}
