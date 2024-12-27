package com.cosmo.cats.api.dto.order;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderRequestEntry {
    @NotBlank
    String productName;
    @Min(value = 0)
    int amount;
}
