package com.example.demo.service;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.example.demo.dto.db.EmployeeModel;
import com.example.demo.dto.response.MenuResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.annotation.PostConstruct;

@Service
public class JsonDataReadingService {
	@Autowired
	private ObjectMapper objectMapper;
	
	private List<EmployeeModel> employees;
	private List<MenuResponse> menus;
	
	@PostConstruct
	public void loadData() throws IOException {
		employees = readJson("employees.json",new TypeReference<List<EmployeeModel>>() {});
		menus = readJson("menus.json" , new TypeReference<List<MenuResponse>>() {});
	}
	
	public <T> List<T> readJson(String fileName,TypeReference<List<T>> type) throws IOException{
		File file = new ClassPathResource("data/"+ fileName).getFile();
		return objectMapper.readValue(file,type);
		
	}
	
	public Optional<EmployeeModel> findEmployee(String userName) {

        return employees.stream()
                .filter(emp -> emp.getEmpName().equals(userName))
                .findFirst();
    }
	public Optional<MenuResponse> findMenus(String role) {

        return menus.stream()
                .filter(menu -> menu.getRole().equals(role))
                .findFirst();
    }
	
	public List<EmployeeModel> getAllEmployees(){
		return employees;
	}


}
