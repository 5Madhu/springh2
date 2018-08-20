package com.example.demo.config;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.xml.StaxEventItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.oxm.xstream.XStreamMarshaller;
import com.example.demo.model.Student;

import com.example.demo.processor.StudentItemProcessor;

@Configuration
@EnableBatchProcessing
public class BatchConfig {
	@Autowired
	JdbcTemplate jdbcTemplate;

	@Autowired
	private JobBuilderFactory jobBuilderFactory;

	@Autowired
	private StepBuilderFactory stepBuilderFactory;

	@Autowired
	private DataSource dataSource;

	/*
	 * public List<Student> findAll() { return
	 * jdbcTemplate.query("select * from STUDENT", new StudentRowMapper()); }
	 */

	@Bean
	public JdbcCursorItemReader<Student> reader() {
		JdbcCursorItemReader<Student> cursorItemReader = new JdbcCursorItemReader<>();
		cursorItemReader.setDataSource(dataSource);
		cursorItemReader.setSql("SELECT STUDENT_ID, AGE, FIRST_NAME, LAST_NAME, STREAM FROM STUDENT");
		cursorItemReader.setRowMapper(new StudentRowMapper());
		return cursorItemReader;
	}

	@Bean
	public StudentItemProcessor processor() {
		return new StudentItemProcessor();
	}

	
	public StaxEventItemWriter<Student> writer() {
		StaxEventItemWriter<Student> writer = new StaxEventItemWriter<Student>();
		writer.setResource(new ClassPathResource("student.xml"));

		Map<String, String> aliasesMap = new HashMap<String, String>();
		aliasesMap.put("student", "com.example.demo.model.Student");
		XStreamMarshaller marshaller = new XStreamMarshaller();
		marshaller.setAliases(aliasesMap);
		writer.setMarshaller(marshaller);
		writer.setRootTagName("students");
		writer.setOverwriteOutput(true);
		return writer;
	}

	
	public Step step1() {
		return stepBuilderFactory.get("step1").<Student, Student>chunk(100).reader(reader()).processor(processor())
				.writer(writer()).build();
	}

	
	public Job exportStudentJob() {
		return jobBuilderFactory.get("exportStudentJob").incrementer(new RunIdIncrementer()).flow(step1()).end()
				.build();
	}

}
