package com.cosmo.cats.api.service.exception;

public class FeatureIsDisabledException extends RuntimeException {
    public static final String FEATURE_IS_DISABLED = "The feature %s a not available at the moment";

    public FeatureIsDisabledException(String message) {
        super(String.format(FEATURE_IS_DISABLED, message));
    }
}
