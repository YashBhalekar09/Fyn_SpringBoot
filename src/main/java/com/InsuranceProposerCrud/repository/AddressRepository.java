package com.InsuranceProposerCrud.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.InsuranceProposerCrud.entity.Address;

public interface AddressRepository extends JpaRepository<Address, Integer>{

	

}
