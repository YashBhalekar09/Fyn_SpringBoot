package com.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.entity.ProposersError;

public interface ProposerErrorRepository extends JpaRepository<ProposersError, Integer>{
	List<ProposersError> findByBatchId(Integer batchId);

}
