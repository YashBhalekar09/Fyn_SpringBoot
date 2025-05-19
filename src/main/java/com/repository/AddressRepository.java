package com.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.entity.Address;

public interface AddressRepository extends JpaRepository<Address, Integer>{

	

}
