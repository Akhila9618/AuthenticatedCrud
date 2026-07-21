package com.example.demo.service;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.dto.db.EmployeeModel;
import com.example.demo.dto.request.EmployeeRequest;
import com.example.demo.dto.request.LoginRequest;
import com.example.demo.dto.response.EmployeeResponse;
import com.example.demo.dto.response.LoginResponse;
import com.example.demo.dto.response.MenuResponse;
import com.example.demo.repository.EmployeeRepository;
import com.example.demo.utility.ApiResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class EmployeeService {
	@Autowired
	private EmployeeRepository employeeRepository;

	@Autowired
	private JsonDataReadingService jsonDataReader;

	@Value("${folderPath}")
	private String folderPath;

	private final PasswordEncoder passwordEncoder;

	public EmployeeService(PasswordEncoder passwordEncoder) {
		this.passwordEncoder = passwordEncoder;
	}

	public ApiResponse<Map<String, Object>> fetchAllEmployees() {
		log.info("FetchAllemployees started ...");
		ApiResponse<Map<String, Object>> response = new ApiResponse<>();
		Map<String, Object> finalData = new HashMap<>();

//		List<EmployeeModel> empList = jsonDataReader.getAllEmployees();
//		from repository it is returning entity not DTO;
		List<EmployeeModel> employees = employeeRepository.findAll();
		List<EmployeeResponse> empList = employees.stream()
				.map(emp -> new EmployeeResponse(emp.getEmplId(), emp.getEmpName(), emp.getEmpEmail(),
						emp.getEmpAddress(), emp.getCompanyName(), emp.getEmpSalary(), emp.getRole()))
				.toList();

		List<String> rolesList = employeeRepository.distinctRoles();

		finalData.put("empList", empList);
		finalData.put("rolesList", rolesList);

		if (empList.isEmpty()) {
			response.setMessage("No employees data found");
			response.setStatus("success");
			response.setData(finalData);
		} else {
			response.setMessage("Employee data fetched successfully");
			response.setStatus("success");
			response.setData(finalData);
		}
		return response;

//		return employeeRepository.findAll();

	}

	public ApiResponse<Map<String,String>> saveEmployee(EmployeeRequest newUser) {
		log.info("Inside method saveEmployee");
		ApiResponse<Map<String,String>> response = new ApiResponse<>();
		try {

			EmployeeModel newEmp = new EmployeeModel();
			log.info("emp" + newEmp);

			newEmp.setEmpEmail(newUser.empEmail());
			if (newUser.companyName() != null) {
				newEmp.setCompanyName(newUser.companyName());
			} else {
				newEmp.setCompanyName("psx");
			}
			newEmp.setEmpAddress(newUser.empAddress());

			if (newUser.empName() != null) {
				newEmp.setEmpName(newUser.empName());
			} else {
				String email = newUser.empEmail();

				if (email != null && email.contains("@")) {
					newEmp.setEmpName(email.substring(0, email.indexOf("@")));
				} else {
					throw new IllegalArgumentException("Invalid email address");
				}
			}
			if (newUser.empSalary() != null) {
				newEmp.setEmpSalary(newUser.empSalary());
			} else {
				newEmp.setEmpSalary(1000);
			}

			newEmp.setPassword(passwordEncoder.encode(newUser.password()));
			newEmp.setRole(newUser.role());
			System.out.println(newEmp);

			employeeRepository.save(newEmp);

			String userName = newEmp.getEmpName();
			createUserFolder(userName);

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

	public ApiResponse<EmployeeResponse> fetchEmployee(EmployeeRequest emp) {
		ApiResponse<EmployeeResponse> response = new ApiResponse<>();
		System.out.println("inside...." + emp);
		try {

			Optional<EmployeeModel> matchedEmp = employeeRepository.findById(emp.emplId());
			if (matchedEmp.isPresent()) {
				EmployeeModel existingEmp = matchedEmp.get();

				existingEmp.setEmpName(emp.empName());
				existingEmp.setEmpSalary(emp.empSalary());
				existingEmp.setEmpAddress(emp.empAddress());
				existingEmp.setCompanyName(emp.companyName());
				existingEmp.setEmpEmail(emp.empEmail());
				existingEmp.setRole(emp.role());

				employeeRepository.save(existingEmp);

				response.setStatus("success");
				response.setMessage("Updated Successfully");
			} else {
				response.setStatus("success");
				response.setMessage("Employee Not found with" + emp.emplId());
			}

		} catch (Exception e) {
			response.setMessage("An error occured");
			response.setStatus("error");
		}
		return response;

	}

//	

	public ApiResponse<LoginResponse> validateLogin(LoginRequest userCredentials) {
		ApiResponse<LoginResponse> response = new ApiResponse<>();
		try {
//			this is to fetch user from table
			if (userCredentials.getUserName() == null || userCredentials.getUserName().isBlank()) {
				throw new IllegalArgumentException("Username is required");
			}
			Optional<EmployeeModel> employee = employeeRepository.findByEmpName(userCredentials.getUserName());
			log.info("emplooyee:::"+employee);

//			to find user form the employee.json
//			Optional<EmployeeModel> employee = jsonDataReader.findEmployee(userCredentials.getUserName());
			if (employee.isEmpty()) {
				response.setMessage("User Not found");
				response.setStatus("error");

			} else {
				EmployeeModel user = employee.get();

				// no need to check username because queried by using username only
				if (!Objects.equals(userCredentials.getPassword(), user.getPassword())) {
					response.setMessage("Invalid user credentials");
					response.setStatus("error");
				} else {
//					to get associated menu respone from menu.json
					Optional<MenuResponse> menuResponse = jsonDataReader.findMenus(user.getRole());
					System.out.print(menuResponse);

					LoginResponse loginResponse = new LoginResponse();
					loginResponse.setEmployeeId(user.getEmplId());
					loginResponse.setUserName(user.getEmpName());
					loginResponse.setRole(user.getRole());

					loginResponse.setToken(null);

					if (menuResponse.isPresent()) {
						loginResponse.setMenusData(menuResponse.get().getMenus());
					}

//					 menuResponse.ifPresent(menu ->
//	                    loginResponse.setMenusData(
//	                            menu.getMenus()));

					response.setStatus("success");
					response.setMessage("User is valid");
					response.setData(loginResponse);

				}

			}

		} catch (Exception e) {
			e.printStackTrace();
			response.setStatus("error");
			response.setMessage("Something went wrong");
		}
		return response;
	}

	public ApiResponse<Map<String, String>> deleteEmployee(Integer empId) {
		ApiResponse<Map<String, String>> response = new ApiResponse<>();
		try {
			Optional<EmployeeModel> matchedEmp = employeeRepository.findById(empId);
			if (matchedEmp.isPresent()) {
				employeeRepository.deleteById(empId);
				deleteFolder(matchedEmp.get().getEmpName());
				response.setMessage("Employee deleted successfully");
				response.setStatus("success");
			} else {
				response.setMessage("Employee Not found with" + empId);
				response.setStatus("success");
			}

		} catch (Exception e) {
			response.setMessage("Error while deleting");
			response.setStatus("error");
		}
		return response;
	}

	private void deleteFolder(String empName) throws IOException {
		System.out.println("Folder Path : " + folderPath);
		String completePath = folderPath.concat("/" + empName);
		Path folderPath = Paths.get(completePath);
		if (Files.exists(folderPath)) {
			Files.walkFileTree(folderPath, new SimpleFileVisitor<Path>() {

				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
					Files.delete(file); // Delete individual files
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
					Files.delete(dir); // Delete empty sub directory or root folder
					return FileVisitResult.CONTINUE;
				}
			});
		} else {
			System.out.println(completePath + "Folder not found.");
		}

	}

}
