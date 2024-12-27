package com.cosmo.cats.api.repository.projection;

import com.cosmo.cats.api.domain.Wearer;

public interface ProductProjection {

  String getName();

  String getDescription();

  Wearer getWearer();
  Double getPrice();
}