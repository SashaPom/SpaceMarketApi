package com.cosmo.cats.api.service;

import com.cosmo.cats.api.domain.order.Order;
import com.cosmo.cats.api.dto.order.OrderRequestEntry;
import java.util.List;
import java.util.Optional;

public interface OrderService {
    Order addToOrder(Optional<String> cartId, List<OrderRequestEntry> orderRequestEntryList);
}
