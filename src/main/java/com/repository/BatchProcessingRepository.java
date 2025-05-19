package com.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.entity.BatchQueue;

public interface BatchProcessingRepository extends JpaRepository<BatchQueue, Integer>{

	public List<BatchQueue> findByIsProcess(String isProcess);
}
