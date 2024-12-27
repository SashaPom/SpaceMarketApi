package com.cosmo.cats.api.service.impl;

import com.cosmo.cats.api.domain.order.Order;
import com.cosmo.cats.api.domain.order.OrderEntry;
import com.cosmo.cats.api.dto.order.OrderRequestEntry;
import com.cosmo.cats.api.repository.OrderRepository;
import com.cosmo.cats.api.repository.ProductRepository;
import com.cosmo.cats.api.repository.entity.OrderEntryEntity;
import com.cosmo.cats.api.repository.entity.ProductEntity;
import com.cosmo.cats.api.service.OrderService;
import com.cosmo.cats.api.service.exception.OrderNotFoundException;
import com.cosmo.cats.api.service.exception.ProductNotFoundException;
import com.cosmo.cats.api.web.mapper.OrderDtoMapper;
import com.cosmo.cats.api.web.mapper.OrderEntryMapper;
import com.cosmo.cats.api.web.mapper.ProductDtoMapper;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final OrderDtoMapper orderMapper;
    private final ProductDtoMapper productMapper;
    private final OrderEntryMapper orderEntryMapper;

    @Override
    @Transactional
    public Order addToOrder(Optional<String> cartId,
                            List<OrderRequestEntry> orderRequestEntryList) {
        Order order;
        if (cartId.isEmpty()) {
            order = Order.builder().cartId(UUID.randomUUID()).totalPrice(
                    BigDecimal.valueOf(0)).entries(new ArrayList<>()).build();
        } else {
            order = orderMapper.toOrder(
                    orderRepository.findByNaturalId(UUID.fromString(cartId.get())).orElseThrow(
                            () -> new OrderNotFoundException(UUID.fromString(cartId.get()))));
        }
        var orderEntryList = getOrderEntryList(orderRequestEntryList);

        var newOrderEntries = mergeEntries(order.getEntries(), orderEntryList);
        var totalPrice = calculateTotalPrice(newOrderEntries);

        var orderEntity = orderMapper.toOrderEntity(order);
        List<OrderEntryEntity> orderEntryEntities = newOrderEntries.stream()
                .map((entry) -> orderEntryMapper.toOrderEntryEntity(entry, orderEntity)).toList();
        orderEntity.setOrderEntries(orderEntryEntities);
        orderEntity.setTotalPrice(totalPrice);
        orderRepository.save(orderEntity);
        return orderMapper.toOrder(orderEntity);
    }

    private BigDecimal calculateTotalPrice(List<OrderEntry> orderEntries) {
        return orderEntries.stream()
                .map(orderEntry -> {
                    BigDecimal price = orderEntry.getProduct().getPrice();
                    Integer quantity = orderEntry.getQuantity();
                    return price.multiply(BigDecimal.valueOf(quantity));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private List<OrderEntry> getOrderEntryList(List<OrderRequestEntry> orderRequestEntryList) {

        return orderRequestEntryList.stream()
                .map(orderRequestEntry -> {
                    ProductEntity productEntity = productRepository
                            .findByNameIgnoreCase(orderRequestEntry.getProductName())
                            .orElseThrow(() -> new ProductNotFoundException(
                                    orderRequestEntry.getProductName()
                            ));
                    var product = productMapper.toProductFromEntity(productEntity);
                    return OrderEntry.builder()
                            .product(product)
                            .quantity(orderRequestEntry.getAmount())
                            .build();
                })
                .collect(Collectors.toList());
    }

    private List<OrderEntry> mergeEntries(List<OrderEntry> existedEntries,
                                          List<OrderEntry> newOrderEntries) {
        Map<String, OrderEntry> entryMap = existedEntries.stream()
                .collect(Collectors.toMap(e -> e.getProduct().getName(), e -> e));

        for (OrderEntry newEntry : newOrderEntries) {
            String productName = newEntry.getProduct().getName();

            if (entryMap.containsKey(productName)) {
                OrderEntry existingEntry = entryMap.get(productName);
                OrderEntry updatedEntry = existingEntry.toBuilder()
                        .quantity(existingEntry.getQuantity() + newEntry.getQuantity())
                        .build();
                entryMap.put(productName, updatedEntry);
            } else {
                entryMap.put(productName, newEntry);
            }
        }

        return new ArrayList<>(entryMap.values());
    }
}
