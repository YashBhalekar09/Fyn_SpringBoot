package com.InsuranceProposerCrud.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="batch_queue")
public class BatchQueue {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="que_id")
	private Integer queid;
	@Column(name="file_path")
	private String filePath;
	@Column(name="is_process")
	private String isProcess;
	@Column(name="row_count")
	private Integer rowCount;
	@Column(name="row_read")
	private Integer rowRead;
	@Column(name="status")
	private String status;
	@Column(name="last_process_count")
	private Integer lastProcessCount;
	
	public Integer getQueid() {
		return queid;
	}
	public void setQueid(Integer queid) {
		this.queid = queid;
	}
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	public String getIsProcess() {
		return isProcess;
	}
	public void setIsProcess(String isProcess) {
		this.isProcess = isProcess;
	}
	public Integer getRowCount() {
		return rowCount;
	}
	public void setRowCount(Integer rowCount) {
		this.rowCount = rowCount;
	}
	public Integer getRowRead() {
		return rowRead;
	}
	public void setRowRead(Integer rowRead) {
		this.rowRead = rowRead;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Integer getLastProcessCount() {
		return lastProcessCount;
	}
	public void setLastProcessCount(Integer lastProcessCount) {
		this.lastProcessCount = lastProcessCount;
	}
	

}
