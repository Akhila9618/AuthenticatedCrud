package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.dto.Menu;
import com.example.demo.dto.SubMenu;
import com.example.demo.dto.db.EmployeeModel;
import com.example.demo.dto.db.MenuEntity;
import com.example.demo.dto.db.Roles;
import com.example.demo.dto.request.LoginRequest;
import com.example.demo.dto.response.LoginResponse;
import com.example.demo.repository.EmployeeRepository;
import com.example.demo.repository.RoleRepository;
import com.example.demo.security.JwtUtil;
import com.example.demo.utility.ApiResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AuthService {
	@Autowired
	private EmployeeRepository employeeRepository;
	private final PasswordEncoder passwordEncoder;

	@Autowired
	private JwtUtil jwtUtil;

	@Autowired
	private RoleRepository roleRepository;

	public AuthService(PasswordEncoder passwordEncoder) {
		this.passwordEncoder = passwordEncoder;
	}

	public ApiResponse<LoginResponse> executeLogin(LoginRequest loginCredentials) {
		log.info("[AuthService] [executeLogin] Login attempt initiated for user: {}", loginCredentials.getUserName());
		ApiResponse<LoginResponse> response = new ApiResponse<>();
		LoginResponse loginResponseData = new LoginResponse();
//		to user data based on loginerequest.username
		EmployeeModel emp = employeeRepository.findByEmpName(loginCredentials.getUserName()).orElseThrow(() -> {
			log.warn("[AuthService] [executeLogin] User not found: {}", loginCredentials.getUserName());
			return new UsernameNotFoundException("User not found");
		});
		log.info("[AuthService] [executeLogin] User found with Employee ID: {}", emp.getEmpName());

		if (loginCredentials.getPassword() == null || loginCredentials.getPassword().isBlank()) {
			log.warn("[AuthService] [executeLogin] Empty password provided for user: {}",
					loginCredentials.getUserName());
			response.setStatus("failed");
			response.setMessage("password is required");
			response.setData(null);
		} else {
			boolean valid = false;
			if (loginCredentials.getUserName().equalsIgnoreCase("defaultuser")) {

				log.info("[AuthService] [executeLogin] Authenticating default user.");
				valid = loginCredentials.getPassword().equalsIgnoreCase("defaultpassword");
			} else {
				log.info("[AuthService] [executeLogin] Validating password for user: {}",
						loginCredentials.getUserName());
				valid = passwordEncoder.matches(loginCredentials.getPassword(), emp.getPassword());
			}

			log.info("Input Password : {}", loginCredentials.getPassword());

			log.info("DB Password : {}", emp.getPassword());
			if (valid) {
				log.info("[AuthService] [executeLogin] Login successful for user: {}", loginCredentials.getUserName());
				Roles role = roleRepository.findByRoleName(emp.getRole());
				log.info("[AuthService] [executeLogin] Role assigned: {}", role.getRoleName());
				List<Menu> menus = convertTableDataToMenuDto(role.getMenus());
//				String token = jwtUtil.generateToken(loginCredentials, emp.getRole());
				String token = "";

				loginResponseData.setEmployeeId(emp.getEmplId());
				loginResponseData.setMenusData(menus);
				loginResponseData.setRole(role.getRoleName());
				loginResponseData.setToken(token);
				loginResponseData.setUserName(emp.getEmpName());

				response.setMessage("Login Successful");
				response.setStatus("success");
				response.setData(loginResponseData);
				log.info("[AuthService] [executeLogin] Response prepared successfully for user: {}",
						loginCredentials.getUserName());
			} else {
				log.warn("[AuthService] [executeLogin] Invalid password provided for user: {}",
						loginCredentials.getUserName());
				response.setStatus("failed");
				response.setMessage("Invalid Password");
				response.setData(null);
			}
		}
		log.info("[AuthService] [executeLogin] Completed for user: {}", loginCredentials.getUserName());
		return response;
	}

	private List<Menu> convertTableDataToMenuDto(List<MenuEntity> menus) {
		log.info("[AuthService] [convertTableDataToMenuDto] Started. Number of menus received: {}", menus.size());

		List<Menu> menuList = menus.stream().map(menuItem -> {
			Menu menu = new Menu();
			menu.setMenuName(menuItem.getMenuName());
			menu.setIcon(menuItem.getIcon());
			List<SubMenu> subMenus = menuItem.getSubPages().stream().map(subMenuItem -> {
				SubMenu subMenu = new SubMenu();
				subMenu.setPageName(subMenuItem.getPageName());

				subMenu.setRoutingUrl(subMenuItem.getRoutingUrl());

				return subMenu;
			}).toList();
			menu.setSubPages(subMenus);
			return menu;

		}).toList();
		log.info("[AuthService] [convertTableDataToMenuDto] Completed successfully. Total menus converted: {}",
				menuList.size());
		return menuList;
	}

	public String generateToken(LoginRequest loginCredentials) {
		log.info("[AuthService] [generateToken] Started for user: {}", loginCredentials.getUserName());
		EmployeeModel emp = employeeRepository.findByEmpName(loginCredentials.getUserName()).orElseThrow(() -> {
			log.warn("[AuthService] [generateToken] User not found: {}", loginCredentials.getUserName());

			return new UsernameNotFoundException("User not found");
		});
		String token = "";
		token = jwtUtil.generateToken(loginCredentials, emp.getRole());
		log.info("[AuthService] [generateToken] JWT generated successfully for user: {}",
				loginCredentials.getUserName());
		return token;

	}
}
