package com.InsuranceProposerCrud.controller;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.InsuranceProposerCrud.JWT.AuthService;
import com.InsuranceProposerCrud.JWT.AuthenticationRequest;
import com.InsuranceProposerCrud.JWT.AuthenticationResponse;
import com.InsuranceProposerCrud.JWT.UserService;
import com.InsuranceProposerCrud.response.ResponseHandler;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;
    
    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseHandler register(@RequestBody AuthenticationRequest request) {
    	ResponseHandler handler=new ResponseHandler();
    	try {
    		String register = userService.registerUser(request);
    		handler.setData(register);
    		handler.setMessage("success");
    		handler.setStatus(true);
		} catch (Exception e) {
			handler.setData(new ArrayList<>());
    		handler.setMessage("failed");
    		handler.setStatus(false);
		} 
        
        return handler;
    }
    

    @PostMapping("/login")
    public ResponseHandler login(@RequestBody Map<String, String> request) {
        ResponseHandler response = new ResponseHandler();
        try {
            String username = request.get("username");
            String password = request.get("password");
            
            String token = authService.loginAndGenerateToken(username, password);
            
            AuthenticationResponse authResponse = new AuthenticationResponse(token);
            
            response.setStatus(true);
            response.setMessage("Login successful");
            response.setData(authResponse);
            
        } catch (BadCredentialsException | UsernameNotFoundException e) {
            response.setStatus(false);
            response.setMessage("Invalid username or password");
            
        } catch (Exception e) {
            response.setStatus(false);
            response.setMessage("Something went wrong");
        }

        return response;
    }

}
