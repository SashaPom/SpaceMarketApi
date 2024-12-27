package com.cosmo.cats.api.dto.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class CosmicWordValidator implements ConstraintValidator<CosmicWordCheck, String> {
  private static final List<String> COSMIC_TERMS = Arrays.asList("star", "galaxy", "comet");
  private static final String COSMIC_TERMS_REGEX = ".*\\b(" + String.join("|", COSMIC_TERMS) + ")\\b.*";
  private static final Pattern pattern = Pattern.compile(COSMIC_TERMS_REGEX, Pattern.CASE_INSENSITIVE);
  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    return pattern.matcher(value).matches();
  }
}