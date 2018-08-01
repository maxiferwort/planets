package com.maxi.planets.persistence.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.List;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public @Data
class WeatherReport {

  private Long cantidadDiasSequias;
  private Long cantidadDiasLluvias;
  private List<Long> diasPicoMaximoLuvias;
  private List<Long> diasCondicionesOptimasPresionTemperatura;

}
