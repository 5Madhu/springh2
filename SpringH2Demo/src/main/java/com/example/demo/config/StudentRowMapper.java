package com.example.demo.config;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import com.example.demo.model.Student;


public class StudentRowMapper implements RowMapper<Student> {

	@Override
	public Student mapRow(ResultSet rs, int rowNum) throws SQLException {
		Student student = new Student();
		student.setStudentId(rs.getInt("STUDENT_ID"));
		student.setAge(rs.getInt("AGE"));
		student.setFirstName(rs.getString("FIRST_NAME"));
		student.setLastName(rs.getString("LAST_NAME"));
		student.setStream(rs.getString("STREAM"));
		return student;
	}

}
