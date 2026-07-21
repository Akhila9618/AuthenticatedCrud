package com.example.demo.configuration;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.example.demo.security.JwtFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfiguration {

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Autowired
	private JwtFilter jwtFilter;

	@Bean
	public SecurityFilterChain security(HttpSecurity http) throws Exception {

//		CSRF (Cross-Site Request Forgery)
		return http
				// this is used because spring security overrides CORS settings.
				.cors(cors -> {
				}).csrf(csrf -> csrf.disable())
				// used to create sessions for every user and later server forgets user
				// this is stateless
				.sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

				.authorizeHttpRequests(auth -> auth.requestMatchers("/auth/login", "/auth/generate-token").permitAll()

						.anyRequest().authenticated())
				// indicates where to include jwtFilter in the built in sequential pipeline
				.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)

				.build();
	}

//	@Bean
//	public CorsConfigurationSource corsConfigurationSource() {
//		CorsConfiguration corsConfig = new CorsConfiguration();
//		corsConfig.setAllowedOriginPatterns(List.of("*"));
//		corsConfig.setAllowedMethods(List.of("*"));
//		corsConfig.setAllowedHeaders(List.of("*"));
//		corsConfig.setAllowCredentials(false);
//		
//		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//		source.registerCorsConfiguration("/**", corsConfig);
//		return source;
//	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOriginPatterns(List.of("*"));
		configuration.setAllowedMethods(List.of("*"));
		configuration.setAllowedHeaders(List.of("*"));
		configuration.setAllowCredentials(false);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

		source.registerCorsConfiguration("/**", configuration);
		return source;
	}

}
