package com.cosmo.cats.api.service.exception;

public class DuplicateProductNameException extends RuntimeException{
  public static final String PRODUCT_WITH_NAME_EXIST_MESSAGE = "Product with name %s already exists";

  public DuplicateProductNameException(String productName) {
    super(String.format(PRODUCT_WITH_NAME_EXIST_MESSAGE, productName));
  }
}
