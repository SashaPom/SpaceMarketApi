package com.cosmo.cats.api.domain.product;

import com.cosmo.cats.api.domain.category.Category;
import java.math.BigDecimal;
import lombok.Builder;
import lombok.Setter;
import lombok.Value;


@Value
@Builder(toBuilder = true)
public class Product {
    Long id;
    String name;
    String description;
    BigDecimal price;
    Integer stockQuantity;
    Category category;
}
