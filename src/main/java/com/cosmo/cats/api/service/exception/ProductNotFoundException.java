package com.cosmo.cats.api.service.exception;

public class ProductNotFoundException extends NotFoundException{
  public static final String PRODUCT_NOT_FOUND_ID = "Product with id %s not found";
  private static final String PRODUCT_NOT_FOUND_NAME = "Product with this name %s not found";

  public ProductNotFoundException(Long productId) {
    super(String.format(PRODUCT_NOT_FOUND_ID, productId));
    DOMAIN = "Product";
  }
  public ProductNotFoundException(String name){
    super(String.format(PRODUCT_NOT_FOUND_NAME, name));
    DOMAIN = "Product";
  }
}
