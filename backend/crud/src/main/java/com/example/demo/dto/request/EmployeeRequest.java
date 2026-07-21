package com.example.demo.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record EmployeeRequest(
		Integer emplId,
		String empName,
		@Email(message = "Should be a valid email.") 
		@NotNull(message = "Employee email can't be null") 
		String empEmail,
		@NotNull(message = "Employee Address can't be null") 
		String empAddress, 
		String companyName,
		@NotNull(message = "Password can't be null") 
		String password,
		@Min(message = "Salary must be positive only.", value = 0) 
		Integer empSalary, 
		@NotNull 
		String role) {

}

//public record EmployeeRequest(String empoyeeName, String employeeEmail, String empAddress, String companyName,
//		String password, Integer employeeSalary, String role) {
//}