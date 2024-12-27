package com.cosmo.cats.api.web.mapper;

import com.cosmo.cats.api.domain.Wearer;
import com.cosmo.cats.api.domain.product.Product;
import com.cosmo.cats.api.dto.product.ProductCreationDto;
import com.cosmo.cats.api.dto.product.ProductDto;
import com.cosmo.cats.api.dto.product.ProductUpdateDto;
import com.cosmo.cats.api.repository.entity.ProductEntity;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface ProductDtoMapper {

    List<ProductDto> toProductDto(List<Product> products);

    ProductDto toProductDto(Product product);
    @Mapping(target = "wearer", source = "wearer", qualifiedByName = "mapWearer")
    Product toProduct(ProductCreationDto productDto);

    @Named("mapWearer")
    default Wearer mapWearer(String wearer) {
        try {
            return Wearer.valueOf(wearer.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid wearer value: " + wearer);
        }
    }
    @Mapping(target = "wearer", source = "wearer", qualifiedByName = "mapWearer")
    Product toProduct(ProductUpdateDto productDto);
    List<Product> toProductList(List<ProductEntity> productEntityList);
    Product toProductFromEntity(ProductEntity productEntity);
    ProductEntity toProductEntity(Product product);

}
