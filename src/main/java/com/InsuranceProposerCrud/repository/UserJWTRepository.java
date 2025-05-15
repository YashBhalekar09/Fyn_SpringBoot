package com.InsuranceProposerCrud.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.InsuranceProposerCrud.entity.UserJWT;

public interface UserJWTRepository extends JpaRepository<UserJWT, Integer>{

	Optional<UserJWT> findByUsername(String username);
}
