package com.cosmo.cats.api.web;

import com.cosmo.cats.api.domain.order.Order;
import com.cosmo.cats.api.domain.order.OrderContext;
import com.cosmo.cats.api.dto.order.OrderRequestEntry;
import com.cosmo.cats.api.service.OrderService;
import com.cosmo.cats.api.web.mapper.OrderDtoMapper;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private final OrderDtoMapper orderMapper;

    @PostMapping(value = {"", "/{cartId}"})
    public ResponseEntity<OrderContext> addProductToAnOrder(@PathVariable(required = false) String cartId,
                                                            @RequestBody
                                                     @Valid List<OrderRequestEntry> productList) {
        var order = orderService.addToOrder(Optional.ofNullable(cartId), productList);
        return ResponseEntity.ok(orderMapper.toOrderContext(order));
    }
}
