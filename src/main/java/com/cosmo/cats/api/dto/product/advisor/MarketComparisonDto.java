package com.cosmo.cats.api.dto.product.advisor;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class MarketComparisonDto {
  String market;
  BigDecimal price;
  BigDecimal priceDifference;
}
