package com.cosmo.cats.api.service;

import com.cosmo.cats.api.domain.product.Product;
import com.cosmo.cats.api.dto.product.advisor.ProductAdvisorRequestDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductServiceMapper {

  ProductAdvisorRequestDto toProductAdvisorRequestDto(Product product);
}
