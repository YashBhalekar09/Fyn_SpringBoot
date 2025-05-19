package com.jwt;

public class AuthenticationResponse {
	
	private final String jwtToken;

	public String getJwtToken() {
		return jwtToken;
	}

	public AuthenticationResponse(String jwtToken) {
		super();
		this.jwtToken = jwtToken;
	}

	
}
