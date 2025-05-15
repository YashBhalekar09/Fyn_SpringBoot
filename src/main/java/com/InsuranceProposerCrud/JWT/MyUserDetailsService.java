package com.InsuranceProposerCrud.JWT;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.InsuranceProposerCrud.entity.UserJWT;
import com.InsuranceProposerCrud.repository.UserJWTRepository;

@Service
public class MyUserDetailsService implements UserDetailsService {

	@Autowired
	private UserJWTRepository jwtRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		UserJWT user = jwtRepository.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("User not found"));

		return new org.springframework.security.core.userdetails.User
				(user.getUsername(), user.getPassword(),new ArrayList<>());
	}

}
