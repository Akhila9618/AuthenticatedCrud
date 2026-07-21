package com.example.demo.security;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.demo.utility.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// extends OncePerRequestFilter means  Execute this filter exactly once per request.
@Component
public class JwtFilter extends OncePerRequestFilter {

//	this is another component which validates token
	@Autowired
	JwtUtil jwtUtil;

	@Autowired
	private ObjectMapper objectMapper;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		String authHeader = request.getHeader("Authorization");
		if (authHeader != null && authHeader.startsWith("Bearer ")) {
//			token extraction
			String token = authHeader.substring(7);
			try {

				String role = jwtUtil.extractRole(token);
				List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role));
				// utility validates token and return username if token is expired then throws
				// exception
				String userName = jwtUtil.extractUserName(token);
				UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userName,
						null, authorities);

				SecurityContextHolder.getContext().setAuthentication(authentication);
			} catch (ExpiredJwtException e) {

				ApiResponse<Object> responseObj = new ApiResponse<>("FAILED", "JWT Expired. Please login again", null);

				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

				response.setContentType("application/json");

				response.getWriter().write(objectMapper.writeValueAsString(responseObj));

				return;
			}

			catch (SignatureException e) {
				ApiResponse<Object> responseObj = new ApiResponse<>("FAILED", "Signature error", null);

				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

				response.setContentType("application/json");

				response.getWriter().write(objectMapper.writeValueAsString(responseObj));
				return;
			}

			catch (MalformedJwtException e) {
				ApiResponse<Object> responseObj = new ApiResponse<>("FAILED", "Malformed token.Please check once.",
						null);

				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

				response.setContentType("application/json");

				response.getWriter().write(objectMapper.writeValueAsString(responseObj));
				return;
			}

			catch (Exception e) {
				e.printStackTrace();
				response.setStatus(401);
				response.getWriter().write(e.getMessage());
				return;
			}
		}
//		this line indicates my work is done other can process  without this line controller won't executed
		filterChain.doFilter(request, response);
	}

}
