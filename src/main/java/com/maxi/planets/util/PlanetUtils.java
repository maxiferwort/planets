package com.maxi.planets.util;

import static java.math.BigDecimal.valueOf;

import java.math.BigDecimal;

public class PlanetUtils {

  public static final BigDecimal VALUE_OF_360 = valueOf(360l);

  public static double format(double value) {
    return (double) Math.round(value * 1000000)
        / 1000000;
  }
}
