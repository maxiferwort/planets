package com.maxi.planets.persistence.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
@Entity
@Table(name = "days")
public @Data
class Day {

  @Id
  private Long day;

  private String weather;

  private Boolean optimalPressionAndTemperature;

  private Boolean intensityPeak;

  private Double perimeter;

  private boolean sunInTheMiddle;

  @Transient
  private List<Planet> planets = new ArrayList<>();

  public Long getDay() {
    return day;
  }

}
