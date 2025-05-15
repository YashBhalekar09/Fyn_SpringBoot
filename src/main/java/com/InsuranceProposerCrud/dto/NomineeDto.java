package com.InsuranceProposerCrud.dto;

import java.util.Date;

import com.InsuranceProposerCrud.enums.Gender;


public class NomineeDto {
	private String nomineeFirstName;
	private String nomineeLastName;
	private Gender nomineeGender;
	private Date nomineeDOB;
	private String relationWithNominee;
	//private Integer nomineeId;
	
	private Boolean isUpdate; // optional flag

	public Boolean getIsUpdate() {
		return isUpdate;
	}

	public void setIsUpdate(Boolean isUpdate) {
		this.isUpdate = isUpdate;
	}


	private Integer proposerId; // foreign key

	public Integer getProposerId() {
		return proposerId;
	}

	public void setProposerId(Integer proposerId) {
		this.proposerId = proposerId;
	}

	public String getRelationWithNominee() {
		return relationWithNominee;
	}

	public void setRelationWithNominee(String relationWithNominee) {
		this.relationWithNominee = relationWithNominee;
	}


	public String getNomineeFirstName() {
		return nomineeFirstName;
	}

	public void setNomineeFirstName(String nomineeFirstName) {
		this.nomineeFirstName = nomineeFirstName;
	}

	public String getNomineeLastName() {
		return nomineeLastName;
	}

	public void setNomineeLastName(String nomineeLastName) {
		this.nomineeLastName = nomineeLastName;
	}

	public Gender getNomineeGender() {
		return nomineeGender;
	}

	public void setNomineeGender(Gender nomineeGender) {
		this.nomineeGender = nomineeGender;
	}

	public Date getNomineeDOB() {
		return nomineeDOB;
	}

	public void setNomineeDOB(Date nomineeDOB) {
		this.nomineeDOB = nomineeDOB;
	}

	
}
