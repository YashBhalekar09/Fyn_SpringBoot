package com.InsuranceProposerCrud.entity;

import java.util.Date;

import com.InsuranceProposerCrud.enumclasses.Gender;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "nominee_tbl")
public class Nominee {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "nominee_id")
	private Integer nomineeId;

	@Column(name = "nominee_firstName")
	private String nomineeFirstName;

	@Column(name = "nominee_lastName")
	private String nomineeLastName;

	@Column(name = "nominee_gender")
	private Gender nomineeGender;
	
	@Column(name = "nominee_dob")
	private Date nomineeDOB;

	@Column(name="nominee_relation")
	private String relationWithNominee;
	
	@Column(name="active_status")
	private String status = "y"; // default to active
	

	@Column(name="proposer_id")
	private Integer proposerId; // foreign key

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getRelationWithNominee() {
		return relationWithNominee;
	}

	public void setRelationWithNominee(String relationWithNominee) {
		this.relationWithNominee = relationWithNominee;
	}


	public Integer getNomineeId() {
		return nomineeId;
	}

	public void setNomineeId(Integer nomineeId) {
		this.nomineeId = nomineeId;
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

	public Integer getProposerId() {
		return proposerId;
	}

	public void setProposerId(Integer proposerId) {
		this.proposerId = proposerId;
	}

}
