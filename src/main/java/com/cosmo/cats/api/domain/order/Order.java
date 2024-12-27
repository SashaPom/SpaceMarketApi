package com.cosmo.cats.api.domain.order;

import java.math.BigDecimal;
import java.util.List;
import lombok.Builder;
import lombok.Value;

@Value
@Builder(toBuilder = true)
public class Order {
  Long id;
  String cartId;
  BigDecimal totalPrice;
  List<OrderEntry> entries;
}
