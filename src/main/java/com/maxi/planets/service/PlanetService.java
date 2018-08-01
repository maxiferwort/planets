package com.maxi.planets.service;

import static com.maxi.planets.util.PlanetUtils.format;
import static java.lang.Math.abs;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.toRadians;
import static java.math.BigDecimal.valueOf;

import com.maxi.planets.persistence.model.Civilization;
import com.maxi.planets.persistence.model.Planet;
import com.maxi.planets.persistence.repository.PlanetRepository;
import java.math.BigDecimal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PlanetService {

  public static final BigDecimal VALUE_OF_360 = BigDecimal.valueOf(360);

  public static final double ERROR_TOLERANCE = 0.1;
  @Autowired
  private PlanetRepository planetRepository;

  public double[] calculateFlatPosition(Planet planet, Long day) {
    BigDecimal circlePos = calculateCirclePosition(planet, day);
    double[] point = new double[2];
    point[0] = format(
        cos(toRadians(circlePos.doubleValue())) * planet.getSunDistance().doubleValue());
    point[1] = format(
        sin(toRadians(circlePos.doubleValue())) * planet.getSunDistance().doubleValue());
    return point;
  }

  public BigDecimal calculateCirclePosition(Planet planet, Long day) {
    BigDecimal pos = planet.getVelocity().multiply(valueOf(day))
        .add(planet.getInitialPosition());
    pos = pos.remainder(VALUE_OF_360);
    if (pos.doubleValue() < 0) {
      pos = VALUE_OF_360.add(pos);
    }
    return pos;
  }

  private boolean sunInsideOrAligned(long day) {
    Planet planetFerengi = planetRepository.findByCivilization(Civilization.FERENGIES);
    Planet planetVulcano = planetRepository.findByCivilization(Civilization.VULCANOS);
    Planet planetBetasoide = planetRepository.findByCivilization(Civilization.BETASOIDES);

    double[] pFerencies = calculateFlatPosition(planetFerengi, day);
    double[] pVulcanos = calculateFlatPosition(planetVulcano, day);
    double[] pBetasoides = calculateFlatPosition(planetBetasoide, day);

    double x = 0, y = 0;
    double x1 = pFerencies[0], y1 = pFerencies[1];
    double x2 = pVulcanos[0], y2 = pVulcanos[1];
    double x3 = pBetasoides[0], y3 = pBetasoides[1];

    double ABC = abs(x1 * (y2 - y3) + x2 * (y3 - y1) + x3 * (y1 - y2));
    double ABP = abs(x1 * (y2 - y) + x2 * (y - y1) + x * (y1 - y2));
    double APC = abs(x1 * (y - y3) + x * (y3 - y1) + x3 * (y1 - y));
    double PBC = abs(x * (y2 - y3) + x2 * (y3 - y) + x3 * (y - y2));

    return ABP + APC + PBC == ABC;
  }

  public boolean arePlanetsAligned(Long day) {
    Planet planetFerengi = planetRepository.findByCivilization(Civilization.FERENGIES);
    Planet planetVulcano = planetRepository.findByCivilization(Civilization.VULCANOS);
    Planet planetBetasoide = planetRepository.findByCivilization(Civilization.BETASOIDES);

    double[] pFerencies = calculateFlatPosition(planetFerengi, day);
    double[] pVulcanos = calculateFlatPosition(planetVulcano, day);
    double[] pBetasoides = calculateFlatPosition(planetBetasoide, day);
    double[] vectorBV = new double[2];
    vectorBV[0] = pVulcanos[0] - pBetasoides[0];
    vectorBV[1] = pVulcanos[1] - pBetasoides[1];
    double[] vectorBF = new double[2];
    vectorBF[0] = pFerencies[0] - pVulcanos[0];
    vectorBF[1] = pFerencies[1] - pVulcanos[1];
    if (vectorBF[1] == 0 && vectorBV[1] == 0) {
      return true;
    } else if (vectorBF[1] == 0 || vectorBV[1] == 0) {
      return false;
    } else {
      return abs((vectorBF[0] / vectorBF[1]) - (vectorBV[0] / vectorBV[1])) < ERROR_TOLERANCE;
    }
  }

  private double calculateNorm(double[] vector) {
    return Math.sqrt(vector[0] * vector[0] + vector[1] * vector[1]);
  }

  public double perimeterSize(long day) {
    if (arePlanetsAligned(day)) {
      return 0;
    }
    Planet planetFerengi = planetRepository.findByCivilization(Civilization.FERENGIES);
    Planet planetVulcano = planetRepository.findByCivilization(Civilization.VULCANOS);
    Planet planetBetasoide = planetRepository.findByCivilization(Civilization.BETASOIDES);

    double[] pFerencies = calculateFlatPosition(planetFerengi, day);
    double[] pVulcanos = calculateFlatPosition(planetVulcano, day);
    double[] pBetasoides = calculateFlatPosition(planetBetasoide, day);
    double[] vectorVF = new double[2];
    vectorVF[0] = abs(pFerencies[0] - pVulcanos[0]);
    vectorVF[1] = abs(pFerencies[1] - pVulcanos[1]);
    double[] vectorBV = new double[2];
    vectorBV[0] = abs(pVulcanos[0] - pBetasoides[0]);
    vectorBV[1] = abs(pVulcanos[1] - pBetasoides[1]);
    double[] vectorBF = new double[2];
    vectorBF[0] = abs(pBetasoides[0] - pFerencies[0]);
    vectorBF[1] = abs(pBetasoides[1] - pFerencies[1]);
    return calculateNorm(vectorBV) + calculateNorm(vectorVF) + calculateNorm(vectorBF);
  }

  public boolean isSunInTheMiddle(long day) {
    if (arePlanetsAligned(day)) {
      return false;
    }
    return sunInsideOrAligned(day);
  }

  public boolean sunAndPlanetsAligned(long day) {
    return arePlanetsAligned(day) && sunInsideOrAligned(day);
  }

  public BigDecimal getCirclePosition(Civilization civilization, Long day) {
    Planet planet = planetRepository.findByCivilization(civilization);
    return calculateCirclePosition(planet, day);
  }

  public double[] getFlatPosition(Civilization civilization, Long day) {
    Planet planet = planetRepository.findByCivilization(civilization);
    return calculateFlatPosition(planet, day);
  }

  public Planet findPlanet(Civilization civilization, Long dayNumber) {
    Planet planet = planetRepository.findByCivilization(civilization);
    double[] flatPosition = calculateFlatPosition(planet, dayNumber);
    planet.setX(flatPosition[0]).setY(flatPosition[1]);
    return planet;
  }

  public void updatePlanet(Civilization civilization, Double initialPosition) {
    Planet planet = planetRepository.findByCivilization(civilization);
    planet.setInitialPosition(BigDecimal.valueOf(initialPosition));
    planetRepository.save(planet);
  }
}
