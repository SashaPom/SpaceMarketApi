package com.cosmo.cats.api.util;

import lombok.Builder;

@Builder
public record InvalidatedParams(String cause, String attribute) {}
