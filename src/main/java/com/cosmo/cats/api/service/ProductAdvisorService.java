package com.cosmo.cats.api.service;

import com.cosmo.cats.api.domain.product.Product;
import com.cosmo.cats.api.dto.product.advisor.ProductAdvisorResponseDto;

public interface ProductAdvisorService {

  ProductAdvisorResponseDto getProductPriceAdvice(Product product);
}
