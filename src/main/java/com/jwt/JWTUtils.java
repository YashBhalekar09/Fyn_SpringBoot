package com.jwt;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.entity.UserJWT;
import com.repository.UserJWTRepository;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Service
public class JWTUtils {
	
	@Autowired
	private UserJWTRepository userRepo;

	private static final String SECRET = "my_very_secure_and_long_jwt_secret_key!";
	private static final SecretKey SECRET_KEY = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));

	private String createToken(Map<String, Object> claims, String subject) {
		return Jwts.builder().setClaims(claims)
				.setSubject(subject)
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + 86400000)) // 1 day
				.signWith(SECRET_KEY, SignatureAlgorithm.HS256)
				.compact();
	}

	public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
		return createToken(extraClaims, userDetails.getUsername());
	}

	private Claims extractAllClaims(String token) {
		return Jwts.parserBuilder().setSigningKey(SECRET_KEY).build().parseClaimsJws(token).getBody();
	}

	public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = extractAllClaims(token);
		return claimsResolver.apply(claims);
	}

	public String extractUsername(String token) {
		return extractClaim(token, Claims::getSubject);
	}

	public Date extractExpiration(String token) {
		return extractClaim(token, Claims::getExpiration);
	}

	private Boolean isTokenExpired(String token) {
		return extractExpiration(token).before(new Date());
	}

	public Boolean validateToken(String token, UserDetails userDetails) {
	    try {
	        final String username = extractUsername(token);
	        
	        final String email = extractEmail(token);
	        
	        final String address=extractAddress(token);
	        
	       Optional<UserJWT> user = userRepo.findByEmailAndAddress(email,address);

	       if (username.equals(userDetails.getUsername())) {
		        if (email.equals(user.get().getEmail()) && !isTokenExpired(token)) {
		            return true;
		        }
		        
		    }
		    return false;
//	        return username.equals(userDetails.getUsername()) &&
//	               !isTokenExpired(token);
	    } catch (JwtException e) {
	        return false;
	    }
	}

	// to extract custom fields
	public String extractEmail(String token) {
		return extractAllClaims(token).get("email", String.class);
	}

	public String extractFullName(String token) {
		return extractAllClaims(token).get("fullName", String.class);
	}
	
	public String extractAddress(String token) {
		return extractAllClaims(token).get("address", String.class);
	}

}
