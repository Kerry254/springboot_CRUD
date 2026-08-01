package com.example.springboot_backend;

import com.example.springboot_backend.model.Employee;
import com.example.springboot_backend.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringbootBackendApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(SpringbootBackendApplication.class, args);
	}

	@Autowired
	 private EmployeeRepository employeeRepository;

	@Override
	public void run(String... args) throws Exception {
		Employee employee = new Employee();
		employee.setFirstName("Victor");
		employee.setLastName("Kerry");
		employee.setEmailId("kerry@gmail.com");
	employeeRepository.save(employee);

		Employee employee1 = new Employee();
		employee1.setFirstName("Jane");
		employee1.setLastName("Terry");
		employee1.setEmailId("Terry@gmail.com");
		employeeRepository.save(employee1);

		Employee employee2 = new Employee();
		employee2.setFirstName("Job");
		employee2.setLastName("Kawau");
		employee2.setEmailId("job@gmail.com");
		employeeRepository.save(employee2);

		Employee employee3 = new Employee();
		employee3.setFirstName("Allison");
		employee3.setLastName("Scott");
		employee3.setEmailId("scott@gmail.com");
		employeeRepository.save(employee3);
	}
}
