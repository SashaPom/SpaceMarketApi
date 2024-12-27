package com.cosmo.cats.api.web.mapper;

import com.cosmo.cats.api.domain.product.Product;
import com.cosmo.cats.api.dto.product.ProductCreationDto;
import com.cosmo.cats.api.dto.product.ProductDto;
import com.cosmo.cats.api.dto.product.ProductUpdateDto;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductDtoMapper {

    List<ProductDto> toProductDto(List<Product> products);

    ProductDto toProductDto(Product product);

    Product toProduct(ProductCreationDto productDto);
    Product toProduct(ProductUpdateDto productDto);

}
