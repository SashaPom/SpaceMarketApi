package com.cosmo.cats.api.web;

import static java.net.URI.create;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.ProblemDetail.forStatusAndDetail;

import com.cosmo.cats.api.service.exception.DuplicateProductNameException;
import com.cosmo.cats.api.service.exception.FeatureIsDisabledException;
import com.cosmo.cats.api.service.exception.NotFoundException;
import com.cosmo.cats.api.service.exception.ProductAdvisorApiException;
import com.cosmo.cats.api.util.InvalidatedParams;
import java.util.List;
import org.springframework.http.HttpHeaders;
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

    @ExceptionHandler(NotFoundException.class)
    ProblemDetail handleProductNotFoundException(NotFoundException ex) {
        ProblemDetail problemDetail =
                forStatusAndDetail(NOT_FOUND, ex.getMessage());
        problemDetail.setType(create(String.format("%s-not-found", ex.DOMAIN.toLowerCase())));
        problemDetail.setTitle(String.format("%s not found", ex.DOMAIN));
        return problemDetail;
    }

    @ExceptionHandler(DuplicateProductNameException.class)
    ProblemDetail handleDuplicateProductNameException(DuplicateProductNameException ex) {
        ProblemDetail problemDetail =
                forStatusAndDetail(BAD_REQUEST, ex.getMessage());
        problemDetail.setType(create("this-name-exists"));
        problemDetail.setTitle("Duplicate name");
        return problemDetail;
    }

    @ExceptionHandler(ProductAdvisorApiException.class)
    ProblemDetail handleProductAdvisorApiException(ProductAdvisorApiException ex) {
        ProblemDetail problemDetail =
                forStatusAndDetail(ex.getStatus(), ex.getMessage());
        problemDetail.setType(create("price-advisor-error"));
        problemDetail.setTitle("Could not get price advice");
        return problemDetail;
    }

    @ExceptionHandler(FeatureIsDisabledException.class)
    ProblemDetail handleFeatureToggleNotEnabledException(FeatureIsDisabledException ex) {
        ProblemDetail problemDetail = forStatusAndDetail(NOT_FOUND, ex.getMessage());
        problemDetail.setType(create("feature-disabled"));
        problemDetail.setTitle("Feature is disabled");
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

        ProblemDetail problemDetail = forStatusAndDetail(BAD_REQUEST,
                "Request validation failed");
        problemDetail.setType(create("validation-failed"));
        problemDetail.setTitle("Validation Failed");
        problemDetail.setProperty("invalidParams", validationResponse);
        return ResponseEntity.status(BAD_REQUEST).body(problemDetail);
    }
}