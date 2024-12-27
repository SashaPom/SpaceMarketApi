package com.cosmo.cats.api.service.exception;

import java.util.UUID;

public class OrderNotFoundException extends NotFoundException{
    public final static String ORDER_NOT_FOUND = "Order with this cartId %s not found";

    public OrderNotFoundException(UUID cartId){
        super(String.format(ORDER_NOT_FOUND, cartId));
        DOMAIN = "Order";
    }
}
