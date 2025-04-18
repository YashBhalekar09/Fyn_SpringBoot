package com.InsuranceProposerCrud.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.InsuranceProposerCrud.entity.Nominee;
import com.InsuranceProposerCrud.entity.Proposer;
import com.InsuranceProposerCrud.repository.NomineeRepository;
import com.InsuranceProposerCrud.repository.ProposerRepository;
import com.InsuranceProposerCrud.request.NomineeDto;
import com.InsuranceProposerCrud.request.RequestDto;

@Service
public class ProposerServiceImpl implements ProposerService {

	@Autowired
	private ProposerRepository proposerRepo;

	@Autowired
	private NomineeRepository nomineeRepo;

	@Override
	public String saveProposer(RequestDto requestDto) {
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

		if (proposerRepo.findByEmail(requestDto.getEmail()).isPresent())
			errors.add("Email already exists");

		if (proposerRepo.findByPanNumber(requestDto.getPanNumber()).isPresent())
			errors.add("PAN number already exists");

		if (proposerRepo.findByAadharNo(requestDto.getAadharNo()).isPresent())
			errors.add("Aadhar number already exists");

		if (!errors.isEmpty()) {
			throw new IllegalArgumentException(String.join("; ", errors));
		}

		Proposer proposer = new Proposer();
		proposer.setProposerTitle(requestDto.getProposerTitle());
		proposer.setFirstName(requestDto.getFirstName());
		proposer.setMiddleName(requestDto.getMiddleName());
		proposer.setLastName(requestDto.getLastName());
		proposer.setGender(requestDto.getGender());
		proposer.setDateOfBirth(requestDto.getDateOfBirth());
		proposer.setPanNumber(requestDto.getPanNumber());
		proposer.setAadharNo(requestDto.getAadharNo());
		proposer.setEmail(requestDto.getEmail());
		proposer.setMobileNo(requestDto.getMobileNo());
		proposer.setAlternateMobNo(requestDto.getAlternateMobNo());
		proposer.setAddressLine1(requestDto.getAddressLine1());
		proposer.setAddressLine2(requestDto.getAddressLine2());
		proposer.setAddressLine3(requestDto.getAddressLine3());
		proposer.setPincode(requestDto.getPincode());
		proposer.setCity(requestDto.getCity());
		proposer.setState(requestDto.getState());

		Proposer details = proposerRepo.save(proposer);

		Integer proposerId = details.getProposerId();// fetch proposer id

		List<NomineeDto> nomineeDetails = requestDto.getNomineeDetails();
		List<Nominee> nomineeEnitity = new ArrayList<>();

		for (NomineeDto nomineeDto : nomineeDetails) {

			Nominee nominee = new Nominee();
			nominee.setNomineeFirstName(nomineeDto.getNomineeFirstName());
			nominee.setNomineeLastName(nomineeDto.getNomineeLastName());
			nominee.setNomineeDOB(nomineeDto.getNomineeDOB());
			nominee.setNomineeGender(nomineeDto.getNomineeGender());
			nominee.setRelationWithNominee(nomineeDto.getRelationWithNominee());
			nominee.setProposerId(proposerId);

			nomineeEnitity.add(nominee);

		}

		nomineeRepo.saveAll(nomineeEnitity);

		return "Proposer Succesfully Saved along with Nominee's Details...!";
	}

	@Override
	public List<RequestDto> allProposer() {
		List<Proposer> entityList = proposerRepo.findByStatus("y");
		List<RequestDto> listDto = new ArrayList<>();

		for (Proposer proposer : entityList) {
			RequestDto reqDto = new RequestDto();
			reqDto.setProposerTitle(proposer.getProposerTitle());
			reqDto.setFirstName(proposer.getFirstName());
			reqDto.setMiddleName(proposer.getMiddleName());
			reqDto.setLastName(proposer.getLastName());
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
	public RequestDto proposerFindById(Integer proposerId) {
		Optional<Proposer> proposerOpt = proposerRepo.findById(proposerId);
		RequestDto dto = new RequestDto();

		if (proposerOpt.isPresent()) {
			Proposer details = proposerOpt.get();

			dto.setFirstName(details.getFirstName());
			dto.setMiddleName(details.getMiddleName());
			dto.setLastName(details.getLastName());
			dto.setAddressLine1(details.getAddressLine1());
			dto.setAddressLine2(details.getAddressLine2());
			dto.setAddressLine3(details.getAddressLine3());
			dto.setAlternateMobNo(details.getAlternateMobNo());
			dto.setCity(details.getCity());
			dto.setEmail(details.getEmail());
			dto.setMobileNo(details.getMobileNo());
			dto.setPincode(details.getPincode());
			dto.setProposerTitle(details.getProposerTitle());
			dto.setState(details.getState());
			dto.setGender(details.getGender());
			dto.setDateOfBirth(details.getDateOfBirth());
			dto.setAadharNo(details.getAadharNo());
			dto.setPanNumber(details.getPanNumber());
			dto.setStatus(details.getStatus());

			List<Nominee> nomineeEntity = nomineeRepo.getByProposerId(proposerId);
			List<NomineeDto> nomineeDtoList = new ArrayList<>();

			for (Nominee nomEntity : nomineeEntity) {
				NomineeDto nomDto = new NomineeDto();
				nomDto.setNomineeFirstName(nomEntity.getNomineeFirstName());
				nomDto.setNomineeLastName(nomEntity.getNomineeLastName());
				nomDto.setNomineeGender(nomEntity.getNomineeGender());
				nomDto.setRelationWithNominee(nomEntity.getRelationWithNominee());
				nomDto.setNomineeDOB(nomEntity.getNomineeDOB());
				nomDto.setProposerId(proposerId);

				nomineeDtoList.add(nomDto);
			}

			dto.setNomineeDetails(nomineeDtoList);
		}

		return dto;
	}

	@Override
	public String deleteProposer(Integer proposerId) {
		Optional<Proposer> proposerOpt = proposerRepo.findById(proposerId);
		if (proposerOpt.isPresent()) {
			Proposer proposer = proposerOpt.get();
			if ("y".equals(proposer.getStatus())) {
				proposer.setStatus("n");
				proposerRepo.save(proposer);

				List<Nominee> nomineeList = nomineeRepo.getByProposerId(proposerId);
				for (Nominee nominee : nomineeList) {
					if ("n".equals(proposer.getStatus())) {
						nominee.setStatus("n");
					}
				}
				nomineeRepo.saveAll(nomineeList);
				return "Proposer deleted";
			} else {
				return "Proposer already inactive";
			}
		} else {
			return "Proposer not found";
		}
	}

	@Override
	public String updateProposer(Integer proposerId, RequestDto requestDto) {
		Optional<Proposer> opt = proposerRepo.findByProposerIdAndStatus(proposerId, "y");
		if (opt.isEmpty())
			return "Proposer not found";

		Proposer existing = opt.get();
		List<String> errors = new ArrayList<>();

		// Validation
		Optional<Proposer> emailCheck = proposerRepo.findByEmail(requestDto.getEmail());
		if (requestDto.getEmail() != null && !requestDto.getEmail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"))
			errors.add("Invalid email format");
		if (emailCheck.isPresent() && !emailCheck.get().getProposerId().equals(proposerId))
			errors.add("Email already exists");

		Optional<Proposer> panCheck = proposerRepo.findByPanNumber(requestDto.getPanNumber());
		if (requestDto.getPanNumber() != null && !requestDto.getPanNumber().matches("^[A-Z]{5}[0-9]{4}[A-Z]$"))
			errors.add("Invalid PAN format");
		if (panCheck.isPresent() && !panCheck.get().getProposerId().equals(proposerId))
			errors.add("PAN number already exists");

		Optional<Proposer> aadharCheck = proposerRepo.findByAadharNo(requestDto.getAadharNo());
		if (requestDto.getAadharNo() != null && String.valueOf(requestDto.getAadharNo()).length() != 12)
			errors.add("Aadhar number must be 12 digits");
		if (aadharCheck.isPresent() && !aadharCheck.get().getProposerId().equals(proposerId))
			errors.add("Aadhar number already exists");

		Optional<Proposer> mobileCheck = proposerRepo.findByMobileNo(requestDto.getMobileNo());
		if (requestDto.getMobileNo() != null && String.valueOf(requestDto.getMobileNo()).length() != 10)
			errors.add("Mobile number must be 10 digits");
		if (mobileCheck.isPresent() && !mobileCheck.get().getProposerId().equals(proposerId))
			errors.add("Mobile number already exists");

		if (requestDto.getPincode() != null && String.valueOf(requestDto.getPincode()).length() != 6)
			errors.add("Pincode must be 6 digits");

		if (!errors.isEmpty())
			throw new IllegalArgumentException(String.join("; ", errors));

		// Update proposer fields
		if (requestDto.getProposerTitle() != null)
			existing.setProposerTitle(requestDto.getProposerTitle());
		if (requestDto.getFirstName() != null)
			existing.setFirstName(requestDto.getFirstName());
		if (requestDto.getMiddleName() != null)
			existing.setMiddleName(requestDto.getMiddleName());
		if (requestDto.getLastName() != null)
			existing.setLastName(requestDto.getLastName());
		if (requestDto.getGender() != null)
			existing.setGender(requestDto.getGender());
		if (requestDto.getDateOfBirth() != null)
			existing.setDateOfBirth(requestDto.getDateOfBirth());
		if (requestDto.getPanNumber() != null)
			existing.setPanNumber(requestDto.getPanNumber());
		if (requestDto.getAadharNo() != null)
			existing.setAadharNo(requestDto.getAadharNo());
		if (requestDto.getEmail() != null)
			existing.setEmail(requestDto.getEmail());
		if (requestDto.getMobileNo() != null)
			existing.setMobileNo(requestDto.getMobileNo());
		if (requestDto.getAlternateMobNo() != null)
			existing.setAlternateMobNo(requestDto.getAlternateMobNo());
		if (requestDto.getAddressLine1() != null)
			existing.setAddressLine1(requestDto.getAddressLine1());
		if (requestDto.getAddressLine2() != null)
			existing.setAddressLine2(requestDto.getAddressLine2());
		if (requestDto.getAddressLine3() != null)
			existing.setAddressLine3(requestDto.getAddressLine3());
		if (requestDto.getCity() != null)
			existing.setCity(requestDto.getCity());
		if (requestDto.getState() != null)
			existing.setState(requestDto.getState());
		if (requestDto.getPincode() != null)
			existing.setPincode(requestDto.getPincode());

		// Save proposer
		proposerRepo.save(existing);

		List<NomineeDto> nomineeDto1 = requestDto.getNomineeDetails();
		List<Nominee> nomineesList = new ArrayList<>();

		Optional<List<Nominee>> opt1 = nomineeRepo.findByProposerId(proposerId);

		if (opt.isPresent()) {
			List<Nominee> existingNominee = opt1.get();

			for (int i = 0; i < existingNominee.size(); i++) {
				for (NomineeDto nomDto : nomineeDto1) {
					existingNominee.get(i).setNomineeFirstName(nomDto.getNomineeFirstName());
					existingNominee.get(i).setNomineeLastName(nomDto.getNomineeLastName());
					existingNominee.get(i).setNomineeDOB(nomDto.getNomineeDOB());
					existingNominee.get(i).setNomineeGender(nomDto.getNomineeGender());
					existingNominee.get(i).setRelationWithNominee(nomDto.getRelationWithNominee());

					if (i == 0) {
						break;
					}
				}

				nomineesList.add(existingNominee.get(i));

			}

			nomineeRepo.saveAll(nomineesList);

		}

		return "Proposer and Nominee(s) updated successfully";
	}

	@Override
	public Optional<Proposer> findByEmail(String email) {
		return proposerRepo.findByEmail(email);
	}

	@Override
	public Optional<Proposer> proposerUpdateByIdAndStatus(Integer proposerId, String status) {
		return Optional.empty();
	}
}
