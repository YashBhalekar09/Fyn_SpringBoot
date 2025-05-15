package com.InsuranceProposerCrud.repository;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.InsuranceProposerCrud.JWT.JWTUtils;
import com.InsuranceProposerCrud.entity.UserJWT;

@Service
public class UserService {
	@Autowired
	private UserJWTRepository userJwtRepo;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private JWTUtils jwtUtils;

	public UserService(UserJWTRepository userJwtRepo, PasswordEncoder passwordEncoder,
			AuthenticationManager authenticationManager, JWTUtils jwtUtils) {
		super();
		this.userJwtRepo = userJwtRepo;
		this.passwordEncoder = passwordEncoder;
		this.authenticationManager = authenticationManager;
		this.jwtUtils = jwtUtils;
	}

	public String register(String username, String password) {
		if (username == null || password == null) {
			throw new IllegalArgumentException("Username and password must not be null");
		}

		UserJWT user = new UserJWT();
		user.setUsername(username);
		user.setPassword(passwordEncoder.encode(password));
		userJwtRepo.save(user);

		return "User registered successfully!";
	}

	public String login(String username, String password) {
		// Authenticate the user
		authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
		final UserDetails userDetails = new org.springframework.security.core.userdetails.User(username, password,
				new ArrayList<>());

		return jwtUtils.generateToken(userDetails);
	}
}
