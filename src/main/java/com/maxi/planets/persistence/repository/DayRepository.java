package com.maxi.planets.persistence.repository;

import com.maxi.planets.persistence.model.Day;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DayRepository extends JpaRepository<Day, Long> {

  Optional<Day> findByDay(Long day);

  @Query("SELECT max(d.perimeter) FROM Day d WHERE d.weather like 'Lluvia' and d.sunInTheMiddle = TRUE ")
  Double findMaxPerimeter();

  @Query("SELECT d FROM Day d WHERE d.perimeter = :perimeter and d.weather like 'Lluvia' and d.sunInTheMiddle = TRUE ")
  List<Day> findByPerimeter(@Param("perimeter") Double perimeter);

  @Query("Select count(d.day) from Day d where d.day >= :start and d.day <= :end and d.weather like :weather")
  Long countByWeatherBetweenDays(@Param("start") Long start, @Param("end") Long end,
      @Param("weather") String weather);

  @Query("SELECT max(d.perimeter) FROM Day d WHERE d.weather like 'Lluvia' and d.day >= :start and d.day <= :end and d.sunInTheMiddle = TRUE ")
  Double findMaxPerimeter(@Param("start") Long start, @Param("end") Long end);

  @Query("SELECT d FROM Day d WHERE d.perimeter = :perimeter and d.weather like 'Lluvia' and d.day >= :start and d.day <= :end and d.sunInTheMiddle = TRUE ")
  List<Day> findByPerimeterAndDay(@Param("start") Long start, @Param("end") Long end,
      @Param("perimeter") Double perimeter);

  @Query("SELECT d from Day d where d.day>= :start and d.day<= :end and d.optimalPressionAndTemperature = :optimalPressionAndTemperature")
  List<Day> findByOptimalPressionAndTemperature(@Param("start") Long start, @Param("end") Long end,
      @Param("optimalPressionAndTemperature") Boolean optimalPressionAndTemperature);
}
