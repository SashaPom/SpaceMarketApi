package com.cosmo.cats.api.web.mapper;

import com.cosmo.cats.api.domain.order.Order;
import com.cosmo.cats.api.domain.order.OrderContext;
import com.cosmo.cats.api.repository.entity.OrderEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderDtoMapper {
    @Mapping(target = "entries", source = "orderEntries")
    Order toOrder(OrderEntity orderEntity);
    @Mapping(target = "orderEntries", source = "entries")
    OrderEntity toOrderEntity(Order order);
    OrderContext toOrderContext(Order order);
}
