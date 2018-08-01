package com.maxi.planets.persistence.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.math.BigDecimal;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
@Entity
@Table(name = "planets")
public @Data
class Planet {

  @Id
  @Enumerated(EnumType.STRING)
  private Civilization civilization;

  private BigDecimal velocity;

  private BigDecimal sunDistance;

  private BigDecimal initialPosition;

  @Transient
  private Double x;

  @Transient
  private Double y;

  @Transient
  private BigDecimal degrees;

}
