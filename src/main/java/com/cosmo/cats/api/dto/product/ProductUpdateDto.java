package com.cosmo.cats.api.dto.product;

import com.cosmo.cats.api.dto.validation.CosmicWordCheck;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import lombok.Builder;
import lombok.Value;

@Value
@Builder(toBuilder = true)
public class ProductUpdateDto {
    @NotNull(message = "Name cannot be null")
    @Size(min = 3, max = 100, message = "Name must be between 3 and 100 characters")
    @CosmicWordCheck
    String name;
    @Size(max = 255, message = "Description cannot exceed 255 characters")
    @NotNull(message = "description cannot be null")
    @NotBlank(message = "description cannot be blank")
    String description;
    @NotNull(message = "Price cannot be null")
    @DecimalMin(value = "0.0", inclusive = false)
    @Digits(integer = 5, fraction = 2)
    BigDecimal price;
    @NotNull(message = "Stock quantity cannot be null")
    @Positive(message = "Stock quantity must be greater than 0")
    Integer stockQuantity;
}
