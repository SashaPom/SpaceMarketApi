package com.cosmo.cats.api.domain.category;

import lombok.Builder;
import lombok.Value;

@Value
@Builder(toBuilder = true)
public class Category {
  Long id;
  String name;
}
