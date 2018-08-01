package com.maxi.planets.persistence.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import lombok.Data;


@JsonIgnoreProperties(ignoreUnknown = true)
public @Data
class ErrorResponse implements Serializable {

  private String message;
  private Integer code;

}