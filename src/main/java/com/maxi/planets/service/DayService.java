package com.maxi.planets.service;

import static com.maxi.planets.persistence.model.Civilization.BETASOIDES;
import static com.maxi.planets.persistence.model.Civilization.FERENGIES;
import static com.maxi.planets.persistence.model.Civilization.VULCANOS;

import com.google.common.collect.Lists;
import com.maxi.planets.persistence.model.Day;
import com.maxi.planets.persistence.model.Planet;
import com.maxi.planets.persistence.model.WeatherReport;
import com.maxi.planets.persistence.repository.DayRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class DayService {

  public static final String LLUVIA = "Lluvia";
  public static final String SEQUIA = "Sequia";
  public static final String NORMAL = "Normal";
  @Autowired
  private DayRepository dayRepository;

  @Autowired
  private PlanetService planetService;


  public Day getDay(Long dayNumber) {
    Planet ferengies = planetService.findPlanet(FERENGIES, dayNumber);
    Planet vulcanos = planetService.findPlanet(VULCANOS, dayNumber);
    Planet betasoides = planetService.findPlanet(BETASOIDES, dayNumber);

    Day day = dayRepository.findByDay(dayNumber).orElseThrow(() ->
        new ResourceNotFoundException("Day not found: " + dayNumber));
    day.setPlanets(Lists.newArrayList(ferengies, vulcanos, betasoides));
    return day;
  }

  public Day createDay(Long dayNbr) {
    Day day = new Day().setWeather(NORMAL).setIntensityPeak(false)
        .setOptimalPressionAndTemperature(false)
        .setPerimeter(planetService.perimeterSize(dayNbr))
        .setDay(dayNbr).setSunInTheMiddle(planetService.isSunInTheMiddle(dayNbr));
    if (planetService.sunAndPlanetsAligned(dayNbr)) {
      day.setWeather(SEQUIA);
    } else {
      if (planetService.arePlanetsAligned(dayNbr)) {
        day.setOptimalPressionAndTemperature(true);
      } else {
        if (planetService.isSunInTheMiddle(dayNbr)) {
          day.setWeather(LLUVIA);
        }
      }
    }
    return day;
  }

  public void saveDay(Day day) {
    dayRepository.save(day);
  }

  public WeatherReport findWeatherReport(Long start, Long end) {
    Long lluvias = dayRepository.countByWeatherBetweenDays(start, end, LLUVIA);
    Long sequias = dayRepository.countByWeatherBetweenDays(start, end, SEQUIA);
    Double maxPerimeter = dayRepository.findMaxPerimeter(start, end);
    List<Long> diasPicoIntensidad = dayRepository.findByPerimeterAndDay(start, end, maxPerimeter)
        .stream().map(Day::getDay).collect(Collectors.toList());
    List<Long> diasOptimosPresionTemp = dayRepository
        .findByOptimalPressionAndTemperature(start, end, true).stream()
        .map(Day::getDay).collect(Collectors.toList());
    return new WeatherReport().setCantidadDiasLluvias(lluvias).setCantidadDiasSequias(sequias)
        .setDiasCondicionesOptimasPresionTemperatura(diasOptimosPresionTemp)
        .setDiasPicoMaximoLuvias(diasPicoIntensidad);
  }
}
