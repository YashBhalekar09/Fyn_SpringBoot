package com.InsuranceProposerCrud.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.InsuranceProposerCrud.entity.BatchQueue;

public interface BatchProcessingRepository extends JpaRepository<BatchQueue, Integer>{

	public List<BatchQueue> findByIsProcess(String isProcess);
}
