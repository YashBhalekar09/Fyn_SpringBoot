package com.InsuranceProposerCrud.dto;

import java.util.Date;
import java.util.List;

import com.InsuranceProposerCrud.enums.Gender;
import com.InsuranceProposerCrud.enums.ProposerTitle;

public class ResponseDto {
	private ProposerTitle proposerTitle;
	
	private Gender gender;
	
	private Date dateOfBirth;
	
	private String panNumber;
	
	private Long aadharNo;
	
	private String status;
	
	private String email;
	
	private Long mobileNo;
	
	private Long alternateMobNo;
	
	private String addressLine1;
	
	private String addressLine2;
	
	private String addressLine3;
	
	private Long pincode;
	
	private String city;
	
	private String state;

	private String firstName;

	private String middleName;

	private String lastName;

	private List<NomineeDto> nomineeDetails;

	public List<NomineeDto> getNomineeDetails() {
		return nomineeDetails;
	}

	public void setNomineeDetails(List<NomineeDto> nomineeDetails) {
		this.nomineeDetails = nomineeDetails;
	}

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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
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
