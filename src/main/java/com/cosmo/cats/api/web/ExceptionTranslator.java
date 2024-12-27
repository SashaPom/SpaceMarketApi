package com.cosmo.cats.api.web;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

import com.cosmo.cats.api.service.exception.DuplicateProductNameException;
import com.cosmo.cats.api.service.exception.ProductAdvisorApiException;
import com.cosmo.cats.api.service.exception.ProductNotFoundException;
import com.cosmo.cats.api.util.InvalidatedParams;
import java.net.URI;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ExceptionTranslator extends ResponseEntityExceptionHandler {

  @ExceptionHandler(ProductNotFoundException.class)
  ProblemDetail handleProductNotFoundException(ProductNotFoundException ex) {
    ProblemDetail problemDetail =
        ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
    problemDetail.setType(URI.create("product-not-found"));
    problemDetail.setTitle("Product not found");
    return problemDetail;
  }

  @ExceptionHandler(DuplicateProductNameException.class)
  ProblemDetail handleDuplicateProductNameException(DuplicateProductNameException ex) {
    ProblemDetail problemDetail =
        ProblemDetail.forStatusAndDetail(BAD_REQUEST, ex.getMessage());
    problemDetail.setType(URI.create("this-name-exists"));
    problemDetail.setTitle("Duplicate name");
    return problemDetail;
  }

  @ExceptionHandler(ProductAdvisorApiException.class)
  ProblemDetail handleProductAdvisorApiException(ProductAdvisorApiException ex) {
    ProblemDetail problemDetail =
        ProblemDetail.forStatusAndDetail(ex.getStatus(), ex.getMessage());
    problemDetail.setType(URI.create("price-advisor-error"));
    problemDetail.setTitle("Could not get price advice");
    return problemDetail;
  }

  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(
      MethodArgumentNotValidException ex,
      HttpHeaders headers, HttpStatusCode status, WebRequest request) {
    List<FieldError> errors = ex.getBindingResult().getFieldErrors();
    List<InvalidatedParams> validationResponse = errors.stream()
        .map(err -> InvalidatedParams.builder()
            .cause(err.getDefaultMessage())
            .attribute(err.getField())
            .build()
        ).toList();

    ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(BAD_REQUEST,
        "Request validation failed");
    problemDetail.setType(URI.create("validation-failed"));
    problemDetail.setTitle("Validation Failed");
    problemDetail.setProperty("invalidParams", validationResponse);
    return ResponseEntity.status(BAD_REQUEST).body(problemDetail);
  }
}