package com.InsuranceProposerCrud.repository;




import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.InsuranceProposerCrud.entity.Nominee;

@Repository
public interface NomineeRepository extends JpaRepository<Nominee, Integer> {
	
    List<Nominee> getByProposerId(Integer proposerId);
    
    Optional<Nominee> findByProposerId(Integer proposerId); // renamed to avoid clash

	Optional<Nominee> findByProposerIdAndStatus(Integer proposerId, String status);
}

