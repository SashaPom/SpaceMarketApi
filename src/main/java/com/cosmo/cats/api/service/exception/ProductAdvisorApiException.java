package com.cosmo.cats.api.service.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ProductAdvisorApiException extends RuntimeException{
  private final HttpStatus status;

  public ProductAdvisorApiException(String message, HttpStatus status) {
    super(message);
    this.status = status;
  }
}
