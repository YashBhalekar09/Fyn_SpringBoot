package com.InsuranceProposerCrud.JWT;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.InsuranceProposerCrud.entity.UserJWT;
import com.InsuranceProposerCrud.repository.UserJWTRepository;

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

//	public String register(String username, String password) {
//		if (username == null || password == null) {
//			throw new IllegalArgumentException("Username and password must not be null");
//		}
//		
//		UserJWT user = new UserJWT();
//		user.setUsername(username);
//		user.setPassword(passwordEncoder.encode(password));
//		userJwtRepo.save(user);
//		return "User registered successfully!";
//	}
//	
	
	public String registerUser(AuthenticationRequest request) {
		
		if (request.getUsername() == null || request.getPassword() == null) {
			throw new IllegalArgumentException("Username and password must not be null");
		}  
		
        if (userJwtRepo.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        if (userJwtRepo.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        UserJWT user = new UserJWT();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword())); // Secure password
        //here add extra claim
        user.setEmail(request.getEmail());
        user.setFullName(request.getFullName());
        user.setAddress(request.getAddress());

        userJwtRepo.save(user);

        return "User registered successfully!";
    }
    

//	public String login(String username, String password) {
//		// Authenticate the user
//		authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
//		final UserDetails userDetails = new org.springframework.security.core.userdetails.User(username, password,
//				new ArrayList<>());
//
//		return jwtUtils.generateToken(userDetails);
//	}
	
	public String login(String username, String password) {
	 
	    authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));

	   
	    UserJWT user = userJwtRepo.findByUsername(username)
	            .orElseThrow(() -> new RuntimeException("User not found"));

	    UserDetails userDetails = new org.springframework.security.core.userdetails.User(
	            user.getUsername(), user.getPassword(), new ArrayList<>()
	    );

	    //extra claims add
	    Map<String, Object> extraClaims = new HashMap<>();
	    extraClaims.put("email", user.getEmail());
	    extraClaims.put("fullName", user.getFullName());
	    extraClaims.put("address",user.getAddress());
	    
	    // Generate token with custom claims
	    return jwtUtils.generateToken(extraClaims, userDetails);
	}

}
