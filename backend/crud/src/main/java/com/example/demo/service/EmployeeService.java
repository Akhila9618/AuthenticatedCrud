package com.example.demo.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.dto.db.EmployeeModel;
import com.example.demo.dto.request.EmployeeRequest;
import com.example.demo.dto.response.EmployeeResponse;
import com.example.demo.repository.EmployeeRepository;
import com.example.demo.utility.ApiResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class EmployeeService {
	@Autowired
	private EmployeeRepository employeeRepository;

//	@Autowired
//	private JsonDataReadingService jsonDataReader;

	@Value("${folderPath}")
	private String folderPath;

	private final PasswordEncoder passwordEncoder;

	public EmployeeService(PasswordEncoder passwordEncoder) {
		this.passwordEncoder = passwordEncoder;
	}

	public ApiResponse<Map<String, Object>> fetchAllEmployees() {
		log.info("[EmployeeService] [fetchAllEmployees] Started.");
		ApiResponse<Map<String, Object>> response = new ApiResponse<>();
		Map<String, Object> finalData = new HashMap<>();

		log.info("[EmployeeService] [fetchAllEmployees] Fetching employees from database.");
//		List<EmployeeModel> empList = jsonDataReader.getAllEmployees();
//		from repository it is returning entity not DTO;
		List<EmployeeModel> employees = employeeRepository.findAll();
		log.info("[EmployeeService] [fetchAllEmployees] Retrieved {} employee records.", employees.size());
		List<EmployeeResponse> empList = employees.stream()
				.map(emp -> new EmployeeResponse(emp.getEmplId(), emp.getEmpName(), emp.getEmpEmail(),
						emp.getEmpAddress(), emp.getCompanyName(), emp.getEmpSalary(), emp.getRole()))
				.toList();

		List<String> rolesList = employeeRepository.distinctRoles();
		log.info("[EmployeeService] [fetchAllEmployees] Retrieved {} distinct roles.", rolesList.size());
		finalData.put("empList", empList);
		finalData.put("rolesList", rolesList);

		if (empList.isEmpty()) {
			log.warn("[EmployeeService] [fetchAllEmployees] No employee records found.");
			response.setMessage("No employees data found");
			response.setStatus("success");
			response.setData(finalData);
		} else {
			log.info("[EmployeeService] [fetchAllEmployees] Employee data prepared successfully.");
			response.setMessage("Employee data fetched successfully");
			response.setStatus("success");
			response.setData(finalData);
		}
		log.info("[EmployeeService] [fetchAllEmployees] Completed successfully.");
		return response;

//		return employeeRepository.findAll();

	}

	public ApiResponse<Map<String, String>> saveEmployee(EmployeeRequest newUser) {
		log.info("Inside method saveEmployee");
		ApiResponse<Map<String, String>> response = new ApiResponse<>();
		try {

			EmployeeModel newEmp = new EmployeeModel();
			log.info("emp" + newEmp);

			newEmp.setEmpEmail(newUser.empEmail());
			if (newUser.companyName() != null) {
				newEmp.setCompanyName(newUser.companyName());
			} else {
				log.info("[EmployeeService] [saveEmployee] Company name not provided. Using default value.");
				newEmp.setCompanyName("psx");
			}
			newEmp.setEmpAddress(newUser.empAddress());

			if (newUser.empName() != null) {

				newEmp.setEmpName(newUser.empName());
			} else {
				String email = newUser.empEmail();
				log.info("[EmployeeService] [saveEmployee] Employee name not provided. Generating from email.");

				if (email != null && email.contains("@")) {
					newEmp.setEmpName(email.substring(0, email.indexOf("@")));
				} else {
					log.warn("[EmployeeService] [saveEmployee] Invalid email received: {}", email);
					throw new IllegalArgumentException("Invalid email address");
				}
			}
			if (newUser.empSalary() != null) {
				newEmp.setEmpSalary(newUser.empSalary());
			} else {
				log.info("[EmployeeService] [saveEmployee] Salary not provided. Using default salary.");
				newEmp.setEmpSalary(1000);
			}

			newEmp.setPassword(passwordEncoder.encode(newUser.password()));
			newEmp.setRole(newUser.role());
			System.out.println(newEmp);

			log.info("[EmployeeService] [saveEmployee] Saving employee to database.");
			employeeRepository.save(newEmp);
			log.info("[EmployeeService] [saveEmployee] Employee saved successfully with ID: {}", newEmp.getEmplId());

//			String userName = newEmp.getEmpName();
//			createUserFolder(userName);

			response.setStatus("success");
			response.setMessage("User Data Saved Successfully");

		} catch (Exception e) {
			response.setStatus("error");
			response.setMessage("Error while saving" + e.getMessage());
		}
		return response;
	}

	public void createUserFolder(String userName) throws IOException {
		System.out.println("Folder Path : " + folderPath);
		Path path = Paths.get(folderPath, userName);
		System.out.println("Creating Folder : " + path.toAbsolutePath());
		Files.createDirectories(path);
		System.out.println("Folder Created Successfully");
	}

	public ApiResponse<EmployeeResponse> updateEmployee(EmployeeRequest emp) {
		log.info("[EmployeeService] [updateEmployee] Started for Employee ID: {}", emp.empName());
		ApiResponse<EmployeeResponse> response = new ApiResponse<>();
		try {
			log.info("[EmployeeService] [updateEmployee] Fetching employee from database.");
			Optional<EmployeeModel> matchedEmp = employeeRepository.findById(emp.emplId());
			if (matchedEmp.isPresent()) {
				EmployeeModel existingEmp = matchedEmp.get();
				log.info("[EmployeeService] [updateEmployee] Employee found. Updating details for ID: {}",
						emp.empName());

				existingEmp.setEmpName(emp.empName());
				existingEmp.setEmpSalary(emp.empSalary());
				existingEmp.setEmpAddress(emp.empAddress());
				existingEmp.setCompanyName(emp.companyName());
				existingEmp.setEmpEmail(emp.empEmail());
				existingEmp.setRole(emp.role());

				employeeRepository.save(existingEmp);
				log.info("[EmployeeService] [updateEmployee] Employee updated successfully for ID: {}", emp.empName());

				response.setStatus("success");
				response.setMessage("Updated Successfully");
			} else {
				log.warn("[EmployeeService] [updateEmployee] Employee not found with ID: {}", emp.empName());
				response.setStatus("success");
				response.setMessage("Employee Not found with" + emp.emplId());
			}

		} catch (Exception e) {
			log.error("[EmployeeService] [updateEmployee] Exception occurred while updating employee ID: {}",
					emp.empName(), e);

			response.setMessage("An error occured");
			response.setStatus("error");
		}
		return response;

	}

//	

	public ApiResponse<Map<String, String>> deleteEmployee(Integer empId) {
		log.info("[EmployeeService] [deleteEmployee] Started for Employee ID: {}", empId);
		ApiResponse<Map<String, String>> response = new ApiResponse<>();
		try {
			log.info("[EmployeeService] [deleteEmployee] Fetching employee from database.");

			Optional<EmployeeModel> matchedEmp = employeeRepository.findById(empId);
			if (matchedEmp.isPresent()) {
				log.info("[EmployeeService] [deleteEmployee] Employee found. Deleting Employee ID: {}", empId);
				employeeRepository.deleteById(empId);
				log.info("[EmployeeService] [deleteEmployee] Database record deleted successfully.");

//				deleteFolder(matchedEmp.get().getEmpName());
				response.setMessage("Employee deleted successfully");
				response.setStatus("success");
			} else {
				log.warn("[EmployeeService] [deleteEmployee] Employee not found with ID: {}", empId);
				response.setMessage("Employee Not found with" + empId);
				response.setStatus("success");
			}

		} catch (Exception e) {
			log.error("[EmployeeService] [deleteEmployee] Exception occurred while deleting Employee ID: {}", empId, e);

			response.setMessage("Error while deleting");
			response.setStatus("error");
		}
		return response;
	}

//	private void deleteFolder(String empName) throws IOException {
//		System.out.println("Folder Path : " + folderPath);
//		String completePath = folderPath.concat("/" + empName);
//		Path folderPath = Paths.get(completePath);
//		if (Files.exists(folderPath)) {
//			Files.walkFileTree(folderPath, new SimpleFileVisitor<Path>() {
//
//				@Override
//				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
//					Files.delete(file); // Delete individual files
//					return FileVisitResult.CONTINUE;
//				}
//
//				@Override
//				public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
//					Files.delete(dir); // Delete empty sub directory or root folder
//					return FileVisitResult.CONTINUE;
//				}
//			});
//		} else {
//			System.out.println(completePath + "Folder not found.");
//		}
//
//	}

}
