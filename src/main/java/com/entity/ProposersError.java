package com.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "proposer_error_tbl")
public class ProposersError {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "error_id")
	private Integer errorId;

	@Column(name = "error_field")
	private String errorField;

	@Column(name = "error_message")
	private String errorMessage;

	@Column(name = "error_status")
	private String status;
	
	@Column(name = "row_number")
	private Integer rowNumber;
	
	 @Column(name = "batch_id")
	    private Integer batchId; 

	
	public Integer getBatchId() {
		return batchId;
	}

	public void setBatchId(Integer batchId) {
		this.batchId = batchId;
	}

	public Integer getRowNumber() {
		return rowNumber;
	}

	public void setRowNumber(Integer rowNumber) {
		this.rowNumber = rowNumber;
	}

	public Integer getErrorId() {
		return errorId;
	}

	public void setErrorId(Integer errorId) {
		this.errorId = errorId;
	}

	public String getErrorField() {
		return errorField;
	}

	public void setErrorField(String errorField) {
		this.errorField = errorField;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}
