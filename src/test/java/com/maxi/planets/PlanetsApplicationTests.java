package com.maxi.planets;

import static org.assertj.core.util.Lists.newArrayList;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.maxi.planets.persistence.model.Day;
import com.maxi.planets.persistence.model.WeatherReport;
import com.maxi.planets.service.JobService;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class PlanetsApplicationTests {

	private ObjectMapper objectMapper = new ObjectMapper();

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private JobService jobService;

	@Before
	public void construct()
			throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
		jobService.runYearJob(0d, 0d, 0d);
	}

	@Test
	public void testDiasLluvia() throws Exception {
		WeatherReport weatherReport = getWeatherReport();
		List<Day> days = collectDays();
		List<Day> diasLluvia = days.stream().filter(day -> day.getWeather().equals("Lluvia")).collect(
				Collectors.toList());
		diasLluvia.stream().forEach(day -> assertTrue(day.isSunInTheMiddle()));
		assertTrue(diasLluvia.size() == weatherReport.getCantidadDiasLluvias().intValue());
	}

	@Test
	public void testOptimalPressionAndTemperature() throws Exception {
		WeatherReport weatherReport = getWeatherReport();
		List<Day> days = collectDays();
		List<Long> diasCOPT = days.stream().filter(day -> day.getOptimalPressionAndTemperature())
				.map(Day::getDay).collect(Collectors.toList());
		assertTrue(diasCOPT.containsAll(weatherReport.getDiasCondicionesOptimasPresionTemperatura()));
		assertTrue(
				diasCOPT.size() == weatherReport.getDiasCondicionesOptimasPresionTemperatura().size());
	}

	@Test
	public void testDiasPicoMaximoLuvias() throws Exception {
		WeatherReport weatherReport = getWeatherReport();
		List<Day> days = collectDays();
		List<Day> diasPML = days.stream()
				.filter(day -> day.getIntensityPeak() && day.getWeather().equals("Lluvia")).collect(
						Collectors.toList());
		List<Long> diasPMLlong = diasPML.stream().map(Day::getDay).collect(Collectors.toList());
		assertTrue(diasPMLlong.containsAll(weatherReport.getDiasPicoMaximoLuvias()));
		assertTrue(
				diasPMLlong.size() == weatherReport.getDiasPicoMaximoLuvias().size());
		Day maxPer = requestDay(diasPMLlong.get(0).intValue());
		diasPML.stream()
				.forEach(day -> assertTrue(day.getPerimeter().equals(maxPer.getPerimeter())));
	}

	@Test
	public void testDiasSequias() throws Exception {
		WeatherReport weatherReport = getWeatherReport();
		List<Day> days = collectDays();
		List<Day> diasSequia = days.stream().filter(day -> day.getWeather().equals("Sequia")).collect(
				Collectors.toList());
		diasSequia.stream().forEach(day -> assertTrue(day.getPerimeter() == 0d));
		assertTrue(diasSequia.size() == weatherReport.getCantidadDiasSequias().intValue());

	}

	private WeatherReport getWeatherReport() throws Exception {
		String weather = mockMvc
				.perform(get("/weather/report"))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
		return objectMapper.readValue(weather, WeatherReport.class);
	}


	private Day requestDay(int i) throws Exception {
		String dayString = mockMvc
				.perform(get("/weather?day=" + i))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
		return objectMapper.readValue(dayString, Day.class);
	}

	private List<Day> collectDays() throws Exception {
		List<Day> days = newArrayList();
		for (int i = 0; i < 360l; i++) {
			Day day = requestDay(i);
			days.add(day);
		}
		return days;
	}

}
