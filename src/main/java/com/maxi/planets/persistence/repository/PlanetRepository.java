package com.maxi.planets.persistence.repository;

import com.maxi.planets.persistence.model.Civilization;
import com.maxi.planets.persistence.model.Planet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlanetRepository extends JpaRepository<Planet, Long> {

  Planet findByCivilization(Civilization ferengies);

}
