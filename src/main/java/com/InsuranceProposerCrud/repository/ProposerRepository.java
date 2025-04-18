package com.InsuranceProposerCrud.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.InsuranceProposerCrud.entity.Proposer;

public interface ProposerRepository extends JpaRepository<Proposer, Integer>{
	public List<Proposer> findByStatus(String status);
	
	public Optional<Proposer> findByEmail(String email);
	
	public Optional<Proposer> findByProposerIdAndStatus(Integer proposerId,String status);
	

	Optional<Proposer> findByPanNumber(String panNumber);
	Optional<Proposer> findByAadharNo(Long aadharNo);
	Optional<Proposer> findByMobileNo(Long mobileNo);

}

