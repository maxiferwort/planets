package com.maxi.planets.batch;

import com.google.common.base.Preconditions;
import com.maxi.planets.persistence.model.Civilization;
import com.maxi.planets.persistence.model.Day;
import com.maxi.planets.persistence.repository.DayRepository;
import com.maxi.planets.service.PlanetService;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JobCompletionNotificationListener extends JobExecutionListenerSupport {

  private static final Logger log = LoggerFactory
      .getLogger(JobCompletionNotificationListener.class);

  @Autowired
  private DayRepository dayRepository;

  @Autowired
  private PlanetService planetService;

  @Override
  public void beforeJob(JobExecution jobExecution) {
    planetService.updatePlanet(Civilization.FERENGIES,
        jobExecution.getJobParameters().getDouble(Civilization.FERENGIES.name()));
    planetService.updatePlanet(Civilization.VULCANOS,
        jobExecution.getJobParameters().getDouble(Civilization.VULCANOS.name()));
    planetService.updatePlanet(Civilization.BETASOIDES,
        jobExecution.getJobParameters().getDouble(Civilization.BETASOIDES.name()));
  }

  @Override
  public void afterJob(JobExecution jobExecution) {
    if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
      log.info("JOB FINISHED Update intensity peak");
      Double maxPerimeter = dayRepository.findMaxPerimeter();
      List<Day> dayMaxPer = dayRepository.findByPerimeter(maxPerimeter);
      Preconditions.checkArgument(!dayMaxPer.isEmpty(), "Days with max perimeter cannot be empty");
      dayMaxPer.stream().forEach(day -> day.setIntensityPeak(true));
      dayRepository.saveAll(dayMaxPer);
    }
  }
}

