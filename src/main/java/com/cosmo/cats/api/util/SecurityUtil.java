package com.cosmo.cats.api.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class SecurityUtil {
  public static final String ROLE_CLAIMS_HEADER = "X-Roles-Claims";
  public static final String PRODUCT_API_KEY_HEADER = "X-Product-Api-Key";
}
