package com.maxi.planets.service;


import static com.maxi.planets.TestUtils.nextBigDecimal;
import static com.maxi.planets.persistence.model.Civilization.BETASOIDES;
import static com.maxi.planets.persistence.model.Civilization.FERENGIES;
import static com.maxi.planets.persistence.model.Civilization.VULCANOS;
import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.ZERO;
import static java.math.BigDecimal.valueOf;
import static org.apache.commons.lang3.RandomUtils.nextLong;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import com.maxi.planets.persistence.model.Civilization;
import com.maxi.planets.persistence.model.Planet;
import com.maxi.planets.persistence.repository.PlanetRepository;
import java.math.BigDecimal;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import org.junit.Test;

public class PlanetServiceTest {

  @Tested
  private PlanetService planetService;

  @Injectable
  private PlanetRepository planetRepository;


  @Test
  public void testPositionDayZero() throws Exception {
    final Planet ferengies = getPlanetFerengies();
    new Expectations() {{
      planetRepository.findByCivilization(withInstanceOf(Civilization.class));
      result = ferengies;
    }};
    assertThat(planetService.getCirclePosition(FERENGIES, 0l), is(ZERO));
  }

  @Test
  public void testPositionAfterOneDay() {
    final Planet ferengies = getPlanetFerengies();
    new Expectations() {{
      planetRepository.findByCivilization(withInstanceOf(Civilization.class));
      result = ferengies;
    }};

    assertThat(planetService.getCirclePosition(FERENGIES, 1l), is(valueOf(1)));
  }

  @Test
  public void testPositionAfterOneDayWithInitialPosition() {
    BigDecimal position = getRandomDecimal();
    final Planet ferengies = getPlanetFerengies().setInitialPosition(position);
    new Expectations() {{
      planetRepository.findByCivilization(withInstanceOf(Civilization.class));
      result = ferengies;
    }};
    assertThat(planetService.getCirclePosition(FERENGIES, 1l),
        is(position.add(ONE).remainder(valueOf(360l))));
  }

  @Test
  public void testPositionAfter100DaysWithInitialPosition() {
    final Planet ferengies = getPlanetFerengies().setInitialPosition(valueOf(361));
    new Expectations() {{
      planetRepository.findByCivilization(withInstanceOf(Civilization.class));
      result = ferengies;
    }};
    assertThat(planetService.getCirclePosition(FERENGIES, 100l),
        is(valueOf(101)));
  }

  @Test
  public void testPositionAfter72DaysVulcano() {
    final Planet vulcanos = getVulcanos();
    new Expectations() {{
      planetRepository.findByCivilization(withInstanceOf(Civilization.class));
      result = vulcanos;
    }};
    assertThat(planetService.getCirclePosition(VULCANOS, 72l), is(ZERO));
  }

  @Test
  public void testPositionAfter71DaysVulcano() {
    final Planet vulcanos = getVulcanos();
    new Expectations() {{
      planetRepository.findByCivilization(withInstanceOf(Civilization.class));
      result = vulcanos;
    }};
    assertThat(planetService.getCirclePosition(VULCANOS, 71l), is(valueOf(5)));
  }

  @Test
  public void testPositionAlwasysLessThan360() {
    for (int i = 0; i < 100; i++) {
      BigDecimal ferengi = getRandomDecimal();
      BigDecimal betasoide = getRandomDecimal();
      BigDecimal vulcano = getRandomDecimal();
      expectPlanets(ferengi, vulcano, betasoide);
      assertTrue(
          planetService.getCirclePosition(BETASOIDES, nextLong(0, 10000)).doubleValue()
              < 360d);
      assertTrue(
          planetService.getCirclePosition(BETASOIDES, nextLong(0, 10000)).doubleValue() > 0);
      assertTrue(
          planetService.getCirclePosition(FERENGIES, nextLong(0, 10000)).doubleValue() < 360d);
      assertTrue(
          planetService.getCirclePosition(FERENGIES, nextLong(0, 10000)).doubleValue() > 0);
      assertTrue(
          planetService.getCirclePosition(VULCANOS, nextLong(0, 10000)).doubleValue() < 360d);
      assertTrue(
          planetService.getCirclePosition(VULCANOS, nextLong(0, 10000)).doubleValue() > 0);
    }
  }

  @Test
  public void testAreAlignedAtBeginning() {
    expectPlanets(ZERO, ZERO, ZERO);
    assertTrue(planetService.arePlanetsAligned(0l));
  }


  private void expectPlanets(BigDecimal ferengies, BigDecimal vulcanos, BigDecimal betasoides) {
    new Expectations() {{
      planetRepository.findByCivilization(FERENGIES);
      result = getPlanetFerengies().setInitialPosition(ferengies);

      planetRepository.findByCivilization(VULCANOS);
      result = getVulcanos().setInitialPosition(vulcanos);

      planetRepository.findByCivilization(BETASOIDES);
      result = getBetasoides().setInitialPosition(betasoides);
    }};
  }

  @Test
  public void testAreNotAlignedFirstDay() {
    expectPlanets(ZERO, ZERO, ZERO);
    assertFalse(planetService.arePlanetsAligned(1l));
  }

  @Test
  public void testAreAlignedAtMiddle() {
    expectPlanets(ZERO, ZERO, ZERO);
    assertTrue(planetService.arePlanetsAligned(180l));
  }

  @Test
  public void testAreAlignedWithInitialPositionSameDegree() {
    BigDecimal rdn = getRandomDecimal();
    expectPlanets(rdn, rdn, rdn);
    assertTrue(planetService.arePlanetsAligned(0l));
  }

  @Test
  public void testAreAlignedWithInitialPositionDegreeOpposite() {
    expectPlanets(ZERO, valueOf(180), valueOf(180));
    assertTrue(planetService.arePlanetsAligned(0l));
  }

  @Test
  public void testAreAligned() {
    expectPlanets(ONE, ONE, valueOf(181l));
    assertTrue(planetService.arePlanetsAligned(0l));
  }

  @Test
  public void testAlignedOverYAxis() {
    expectPlanets(BigDecimal.valueOf(90), BigDecimal.valueOf(90), BigDecimal.valueOf(270));
    assertTrue(planetService.arePlanetsAligned(0l));
  }

  @Test
  public void testAlignedOverCrossAxis() {
    expectPlanets(BigDecimal.valueOf(120), BigDecimal.valueOf(120), BigDecimal.valueOf(300));
    assertTrue(planetService.arePlanetsAligned(0l));
  }

  @Test
  public void testAlignedOverYAxisDefaultX() {
    expectPlanets(BigDecimal.valueOf(30), BigDecimal.valueOf(Math.toDegrees(Math.asin(0.25))),
        BigDecimal.valueOf(Math.toDegrees(Math.asin(0.125))));
    assertTrue(planetService.arePlanetsAligned(0l));
  }

  @Test
  public void testIsSunInTheMiddleXAxis() {
    expectPlanets(ZERO, ZERO, ZERO);
    assertFalse(planetService.isSunInTheMiddle(0l));
  }

  @Test
  public void testIsSunInTheMiddle() {
    expectPlanets(ZERO, BigDecimal.valueOf(90), BigDecimal.valueOf(180));
    assertTrue(planetService.isSunInTheMiddle(0l));
  }

  @Test
  public void testPerimeterSizeAligned() {
    expectPlanets(ZERO, BigDecimal.ZERO, BigDecimal.valueOf(180));
    assertThat(planetService.perimeterSize(0l), is(0d));
  }

  @Test
  public void testPerimeterSizeNotAligned() {
    expectPlanets(ZERO, BigDecimal.valueOf(90), BigDecimal.valueOf(180));
    assertThat(planetService.perimeterSize(0l), is(5854.101966249685));
  }


  private Planet getVulcanos() {
    return new Planet().setInitialPosition(ZERO)
        .setVelocity(valueOf(-5)).setSunDistance(valueOf(1000));
  }

  private Planet getBetasoides() {
    return new Planet().setInitialPosition(ZERO)
        .setVelocity(valueOf(3)).setSunDistance(valueOf(2000));
  }

  private BigDecimal getRandomDecimal() {
    return nextBigDecimal(0, 1000);
  }

  private Planet getPlanetFerengies() {
    return new Planet().setInitialPosition(ZERO)
        .setVelocity(valueOf(1l)).setSunDistance(valueOf(500));
  }

}
