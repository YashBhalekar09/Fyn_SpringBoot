package com.InsuranceProposerCrud.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.InsuranceProposerCrud.entity.Proposer;
import com.InsuranceProposerCrud.repository.ProposerRepository;
import com.InsuranceProposerCrud.request.RequestDto;

@Service
public class ProposerServiceImpl implements ProposerService {

	@Autowired
	private ProposerRepository proposerRepo;

	@Override
	public String saveProposer(RequestDto requestDto) {

	    // Custom Validation
	    List<String> errors = new ArrayList<>();

	    if (requestDto.getProposerTitle() == null)
	        errors.add("Proposer title is required");

	    if (requestDto.getGender() == null)
	        errors.add("Gender is required");

	    if (requestDto.getDateOfBirth() == null)
	        errors.add("Date of Birth is required");

	    if (requestDto.getPanNumber() == null || !requestDto.getPanNumber().matches("[A-Z]{5}[0-9]{4}[A-Z]{1}"))
	        errors.add("Invalid PAN format");

	    if (requestDto.getAadharNo() == null || String.valueOf(requestDto.getAadharNo()).length() != 12)
	        errors.add("Aadhar number must be 12 digits");

	    if (requestDto.getEmail() == null || !requestDto.getEmail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"))
	        errors.add("Invalid email");

	    if (requestDto.getMobileNo() == null || String.valueOf(requestDto.getMobileNo()).length() != 10)
	        errors.add("Mobile number must be 10 digits");

	    if (requestDto.getAddressLine1() == null || requestDto.getAddressLine1().trim().isEmpty())
	        errors.add("Address Line 1 is required");

	    if (requestDto.getPincode() == null || String.valueOf(requestDto.getPincode()).length() != 6)
	        errors.add("Pincode must be 6 digits");

	    if (requestDto.getCity() == null || requestDto.getCity().trim().isEmpty())
	        errors.add("City is required");

	    if (requestDto.getState() == null || requestDto.getState().trim().isEmpty())
	        errors.add("State is required");

	    if (!errors.isEmpty()) {
	        throw new IllegalArgumentException(String.join("; ", errors));
	      
	    }

	    Proposer existing = new Proposer();
	    existing.setProposerTitle(requestDto.getProposerTitle());
	    existing.setGender(requestDto.getGender());
	    existing.setDateOfBirth(requestDto.getDateOfBirth());
	    existing.setPanNumber(requestDto.getPanNumber());
	    existing.setAadharNo(requestDto.getAadharNo());
	    existing.setEmail(requestDto.getEmail());
	    existing.setMobileNo(requestDto.getMobileNo());
	    existing.setAlternateMobNo(requestDto.getAlternateMobNo());
	    existing.setAddressLine1(requestDto.getAddressLine1());
	    existing.setAddressLine2(requestDto.getAddressLine2());
	    existing.setAddressLine3(requestDto.getAddressLine3());
	    existing.setPincode(requestDto.getPincode());
	    existing.setCity(requestDto.getCity());
	    existing.setState(requestDto.getState());

	    proposerRepo.save(existing);
	    return "Proposer saved successfully";
	}


	@Override
	public List<RequestDto> allProposer() {
		List<Proposer> entityList= proposerRepo.findByStatus("y");
		
		List<RequestDto> listDto=new ArrayList<>();
		RequestDto reqDto=new RequestDto();
		
		for (Proposer proposer : entityList) {
			reqDto.setProposerTitle(proposer.getProposerTitle());
		
			reqDto.setGender(proposer.getGender());
			reqDto.setDateOfBirth(proposer.getDateOfBirth());
			reqDto.setPanNumber(proposer.getPanNumber());
			reqDto.setAadharNo(proposer.getAadharNo());
			reqDto.setEmail(proposer.getEmail());
			reqDto.setMobileNo(proposer.getMobileNo());
			reqDto.setAlternateMobNo(proposer.getAlternateMobNo());
			reqDto.setAddressLine1(proposer.getAddressLine1());
			reqDto.setAddressLine2(proposer.getAddressLine2());
			reqDto.setAddressLine3(proposer.getAddressLine3());
			reqDto.setPincode(proposer.getPincode());
			reqDto.setCity(proposer.getCity());
			reqDto.setState(proposer.getState());
			reqDto.setStatus(proposer.getStatus());
			listDto.add(reqDto);
		}
		return listDto;
	}

	@Override
	public Optional<Proposer> proposerFindById(Integer proposerId) {
		return proposerRepo.findById(proposerId);
	}

	@Override
	public String deleteProposer(Integer proposerId) {
	   
	    Optional<Proposer> proposerOpt = proposerRepo.findById(proposerId);
	    
	    if (proposerOpt.isPresent()) {
	        Proposer proposer = proposerOpt.get();
	      if ("y".equals(proposer.getStatus())) {
	            proposer.setStatus("n");
	            proposerRepo.save(proposer);  
	            return "Proposer status updated";
	        } else {
	            return "Proposer not found.";
	        }
	    } else {
	        return "Proposer not found.";
	    }
	}

	@Override
	public String updateProposer(Integer proposerId, RequestDto requestDto) {
		Optional<Proposer> opt = proposerRepo.findByProposerIdAndStatus(proposerId,"y");
		if (opt.isPresent()) {
			
			Proposer existing =opt.get();
			existing.setProposerTitle(requestDto.getProposerTitle());
			existing.setGender(requestDto.getGender());
			existing.setDateOfBirth(requestDto.getDateOfBirth());
			existing.setPanNumber(requestDto.getPanNumber());
			existing.setAadharNo(requestDto.getAadharNo());
			existing.setEmail(requestDto.getEmail());
			existing.setMobileNo(requestDto.getMobileNo());
			existing.setAlternateMobNo(requestDto.getAlternateMobNo());
			existing.setAddressLine1(requestDto.getAddressLine1());
			existing.setAddressLine2(requestDto.getAddressLine2());
			existing.setAddressLine3(requestDto.getAddressLine3());
			existing.setPincode(requestDto.getPincode());
			existing.setCity(requestDto.getCity());
			existing.setState(requestDto.getState());

			proposerRepo.save(existing);
			return "Proposer updated successfully!!";
		}

		return "Proposer not found";
	}

	@Override
	public Optional<Proposer> findByEmail(String email) {
		
		return proposerRepo.findByEmail(email);
	}

	@Override
	public Optional<Proposer> proposerUpdateByIdAndStatus(Integer proposerId, String status) {
		
		return null;
	}

}
