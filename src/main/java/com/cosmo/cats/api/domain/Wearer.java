package com.cosmo.cats.api.domain;

import lombok.Getter;

@Getter
public enum Wearer {
  CATS("cats-products"),
  KITTIES("kitty-products");

  private final String wearerName;

  Wearer(String wearerName) {
    this.wearerName = wearerName;
  }
}
