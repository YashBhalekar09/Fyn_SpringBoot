package com.jwt;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.entity.UserJWT;
import com.repository.UserJWTRepository;


@Service
public class AuthService {

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private MyUserDetailsService userDetailsService;

	@Autowired
	private JWTUtils jwtUtils;

	@Autowired
	private UserJWTRepository userJwtRepo;

	public String loginAndGenerateToken(String username, String password) {

		authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));

		UserDetails userDetails = userDetailsService.loadUserByUsername(username);

		UserJWT user = userJwtRepo.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));

		// Prepare custom claims
		Map<String, Object> extraClaims = new HashMap<>();
		extraClaims.put("email", user.getEmail());
		extraClaims.put("fullName", user.getFullName());
		extraClaims.put("address", user.getAddress());
		// Generate JWT token with custom claims
		return jwtUtils.generateToken(extraClaims, userDetails);

	}
}
