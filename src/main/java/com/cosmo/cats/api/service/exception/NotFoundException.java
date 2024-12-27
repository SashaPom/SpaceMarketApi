package com.cosmo.cats.api.service.exception;

public abstract class NotFoundException extends RuntimeException{
    public String DOMAIN;
    public NotFoundException(String message){
        super(message);
    }
}
