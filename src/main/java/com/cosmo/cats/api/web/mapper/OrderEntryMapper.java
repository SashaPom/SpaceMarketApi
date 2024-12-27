package com.cosmo.cats.api.web.mapper;

import com.cosmo.cats.api.domain.order.OrderEntry;
import com.cosmo.cats.api.repository.entity.OrderEntity;
import com.cosmo.cats.api.repository.entity.OrderEntryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderEntryMapper {
    @Mapping(target = "order", source = "order")
    OrderEntryEntity toOrderEntryEntity(OrderEntry orderEntry, OrderEntity order);
}
