package com.maxi.planets.controller;


import static com.google.common.base.Preconditions.checkArgument;

import com.maxi.planets.persistence.model.Day;
import com.maxi.planets.persistence.model.WeatherReport;
import com.maxi.planets.service.DayService;
import com.maxi.planets.service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController("/weather")
public class WeatherController {

  @Autowired
  private DayService dayService;

  @Autowired
  private JobService jobService;


  @GetMapping
  public Day getDay(@RequestParam Long day) {
    checkArgument(day >= 0, "Day number must be ge 0");
    Day dayO = dayService.getDay(day);
    return dayO;
  }

  @PostMapping("/weather/generate")
  public void runJobSync(@RequestParam(required = false, defaultValue = "0") Double ferengies,
      @RequestParam(required = false, defaultValue = "0") Double vulcanos,
      @RequestParam(required = false, defaultValue = "0") Double betasoides,
      @RequestParam(required = false, defaultValue = "0") Long start,
      @RequestParam(required = false, defaultValue = "360") Long end) throws Exception {
    checkArgument(ferengies >= 0 && ferengies < 360,
        "Initial degrees position for ferengies must be ge 0 and less than 360");
    checkArgument(betasoides >= 0 && betasoides < 360,
        "Initial degrees position for betasoides must be ge 0 and less than 360");
    checkArgument(vulcanos >= 0 && vulcanos < 360,
        "Initial degrees position for vulcanos must be ge 0 and less than 360");
    jobService.runYearJob(ferengies, vulcanos, betasoides, start, end);
  }

  @GetMapping("/weather/report")
  public WeatherReport findWeatherReport(
      @RequestParam(required = false, defaultValue = "0") Long start,
      @RequestParam(required = false, defaultValue = "3600") Long end) {
    return dayService.findWeatherReport(start, end);
  }

}
