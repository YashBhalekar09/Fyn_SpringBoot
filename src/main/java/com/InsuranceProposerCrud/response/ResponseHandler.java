package com.InsuranceProposerCrud.response;

import java.util.List;

public class ResponseHandler {
	
	private Object Data;
	private boolean status;
	private String message;
	private Object totalRecord;
	
	private List<String> errors; //for listing errors
	
	public Object getData() {
		return Data;
	}
	public void setData(Object data) {
		Data = data;
	}
	public boolean isStatus() {
		return status;
	}
	public void setStatus(boolean status) {
		this.status = status;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public Object getTotalRecord() {
		return totalRecord;
	}
	public void setTotalRecord(Object totalRecord) {
		this.totalRecord = totalRecord;
	}
	
	public List<String> getErrors() {
		return errors;
	}
	public void setErrors(List<String> errors) {
		this.errors = errors;
	}

}
