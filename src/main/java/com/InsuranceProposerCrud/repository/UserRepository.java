package com.InsuranceProposerCrud.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.InsuranceProposerCrud.entity.User;

public interface UserRepository extends JpaRepository<User, Integer>{

}
