package com.maxi.planets.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.maxi.planets.persistence.model.Day;
import java.util.ArrayList;
import java.util.List;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class WeatherControllerTest {

  private ObjectMapper objectMapper = new ObjectMapper();

  @Autowired
  private MockMvc mockMvc;

  //tests exceptions

  //each resource

  @Test
  @Ignore
  public void testPerimiterOverTheTime() throws Exception {
    List<Day> dayList = new ArrayList<>();
    for (int i = 0; i < 360; i++) {
      Day day = requestDay(i);
      dayList.add(day);
    }
    dayList.stream().forEach(day -> System.out.println(day.getPerimeter()));
  }

  private Day requestDay(int i) throws Exception {
    String dayString = mockMvc
        .perform(get("/weather?day=" + i))
        .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
    return objectMapper.readValue(dayString, Day.class);
  }

}
