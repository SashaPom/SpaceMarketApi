package com.cosmo.cats.api.dto.product.advisor;

import java.math.BigDecimal;
import java.util.List;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class ProductAdvisorResponseDto {
  BigDecimal originalMarketPrice;
  List<MarketComparisonDto> comparisons;
}
