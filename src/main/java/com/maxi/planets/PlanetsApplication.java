package com.maxi.planets;

import com.maxi.planets.batch.JobCompletionNotificationListener;
import com.maxi.planets.persistence.model.Day;
import com.maxi.planets.service.DayService;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.DefaultBatchConfigurer;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.support.IteratorItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.batch.BatchAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication(exclude = {BatchAutoConfiguration.class})
@EnableBatchProcessing
@EnableTransactionManagement
@EnableAsync
public class PlanetsApplication extends DefaultBatchConfigurer {

  @Autowired
  public JobBuilderFactory jobBuilderFactory;
  @Autowired
  public StepBuilderFactory stepBuilderFactory;
  @Autowired
  private EntityManagerFactory entityManagerFactory;
  @Autowired
  private Job job;
  @Autowired
  private DayService dayService;

	public static void main(String[] args) {
		SpringApplication.run(PlanetsApplication.class, args);
	}

  @Override
  public void setDataSource(DataSource dataSource) {

  }

  @Bean
  @StepScope
  public IteratorItemReader<Long> reader(@Value("#{jobParameters['start']}") Long start,
      @Value("#{jobParameters['end']}") Long end) {
    List<Long> days = new ArrayList<>();
    for (long i = start; i < end * 1; i++) {
      days.add(i);
    }
    return new IteratorItemReader<Long>(days);
  }

  @Bean
  @StepScope
  public ItemProcessor<Long, Day> processor() {
    return new ItemProcessor<Long, Day>() {
      @Override
      public Day process(Long day) throws Exception {
        return dayService.createDay(day);
      }
    };
  }

  @Bean
  @StepScope
  public JpaItemWriter<Day> dayJpaItemWriter() {
    JpaItemWriter<Day> jpaItemWriter = new JpaItemWriter<Day>();
    jpaItemWriter.setEntityManagerFactory(entityManagerFactory);
    return jpaItemWriter;
  }

  @Bean
  public PlatformTransactionManager transactionManager() {
    JpaTransactionManager transactionManager
        = new JpaTransactionManager();
    transactionManager.setEntityManagerFactory(
        entityManagerFactory);
    return transactionManager;
  }

  @Bean
  public Job importUserJob(JobCompletionNotificationListener listener, Step step1) {
    return jobBuilderFactory.get("generateYearJob")
        .incrementer(new RunIdIncrementer())
        .listener(listener)
        .flow(step1)
        .end()
        .build();
  }

  @Bean
  public Step step1(JpaItemWriter<Day> writer) {
    return stepBuilderFactory.get("step1")
        .<Long, Day>chunk(10)
        .reader(reader(null, null))
        .processor(processor())
        .writer(writer)
        .transactionManager(transactionManager())
        .build();
  }

}
