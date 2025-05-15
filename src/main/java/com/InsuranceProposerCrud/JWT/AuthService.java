package com.InsuranceProposerCrud.JWT;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private MyUserDetailsService userDetailsService;

    @Autowired
    private JWTUtils jwtUtils;

    public String loginAndGenerateToken(String username, String password) {
        
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password));

       
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        
        return jwtUtils.generateToken(userDetails);
    }
}

