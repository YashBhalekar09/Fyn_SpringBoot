package com.InsuranceProposerCrud.entity;

import java.util.Date;


import com.InsuranceProposerCrud.enumclasses.Gender;
import com.InsuranceProposerCrud.enumclasses.ProposerTitle;
import com.InsuranceProposerCrud.request.NomineeDto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "proposer_tbl")
public class Proposer {

	// personal details
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="proposer_id")
	private Integer proposerId;

	@Enumerated(EnumType.STRING)
	@Column(name="proposer_title")
	private ProposerTitle proposerTitle;

	@Column(name="first_name")
	private String firstName;
	@Column(name="middle_name")
	private String middleName;
	@Column(name="last_name")
	private String lastName;
	

	@Enumerated(EnumType.STRING)
	@Column(name="gender")
	private Gender gender;
	@Column(name="date_of_birth")
	private Date dateOfBirth;
	@Column(name="pan_number")
	private String panNumber;
	@Column(name="aadhar_number")
	private Long aadharNo;
	@Column(name="active_status")
	private String status="y";

	// Contact Details
	@Column(name="email")
	private String email;
	@Column(name="mobile_no")
	private Long mobileNo;
	@Column(name="alternate_mobile_number")
	private Long alternateMobNo;

	// Address details
	@Column(name="addressLine1")
	private String addressLine1;
	@Column(name="addressLine2")
	private String addressLine2;
	@Column(name="addressLine3")
	private String addressLine3;
	@Column(name="pincode")
	private Long pincode;
	@Column(name="city")
	private String city;
	@Column(name="state")
	private String state;
	
	
	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	


	public Proposer(Integer proposerId, ProposerTitle proposerTitle, Gender gender, Date dateOfBirth, String panNumber,
			Long aadharNo, String status, String email, Long mobileNo, Long alternateMobNo, String addressLine1,
			String addressLine2, String addressLine3, Long pincode, String city, String state) {
		super();
		this.proposerId = proposerId;
		this.proposerTitle = proposerTitle;
		this.gender = gender;
		this.dateOfBirth = dateOfBirth;
		this.panNumber = panNumber;
		this.aadharNo = aadharNo;
		this.status = status;
		this.email = email;
		this.mobileNo = mobileNo;
		this.alternateMobNo = alternateMobNo;
		this.addressLine1 = addressLine1;
		this.addressLine2 = addressLine2;
		this.addressLine3 = addressLine3;
		this.pincode = pincode;
		this.city = city;
		this.state = state;
	}

	public Proposer() {
		super();
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Integer getProposerId() {
		return proposerId;
	}

	public void setProposerId(Integer proposerId) {
		this.proposerId = proposerId;
	}

	public ProposerTitle getProposerTitle() {
		return proposerTitle;
	}

	public void setProposerTitle(ProposerTitle proposerTitle) {
		this.proposerTitle = proposerTitle;
	}

	public Gender getGender() {
		return gender;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}

	public Date getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(Date dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public String getPanNumber() {
		return panNumber;
	}

	public void setPanNumber(String panNumber) {
		this.panNumber = panNumber;
	}

	public Long getAadharNo() {
		return aadharNo;
	}

	public void setAadharNo(Long aadharNo) {
		this.aadharNo = aadharNo;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Long getMobileNo() {
		return mobileNo;
	}

	public void setMobileNo(Long mobileNo) {
		this.mobileNo = mobileNo;
	}

	public Long getAlternateMobNo() {
		return alternateMobNo;
	}

	public void setAlternateMobNo(Long alternateMobNo) {
		this.alternateMobNo = alternateMobNo;
	}

	public String getAddressLine1() {
		return addressLine1;
	}

	public void setAddressLine1(String addressLine1) {
		this.addressLine1 = addressLine1;
	}

	public String getAddressLine2() {
		return addressLine2;
	}

	public void setAddressLine2(String addressLine2) {
		this.addressLine2 = addressLine2;
	}

	public String getAddressLine3() {
		return addressLine3;
	}

	public void setAddressLine3(String addressLine3) {
		this.addressLine3 = addressLine3;
	}

	public Long getPincode() {
		return pincode;
	}

	public void setPincode(Long pincode) {
		this.pincode = pincode;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	

}
