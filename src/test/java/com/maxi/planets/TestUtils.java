package com.maxi.planets;

import static org.apache.commons.lang3.RandomUtils.nextDouble;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class TestUtils {

  public static BigDecimal nextBigDecimal(double start, double end) {
    return BigDecimal.valueOf(nextDouble(start, end))
        .setScale(3, RoundingMode.HALF_UP);
  }

}
