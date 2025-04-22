package com.InsuranceProposerCrud.service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.InsuranceProposerCrud.entity.Nominee;
import com.InsuranceProposerCrud.entity.Proposer;
import com.InsuranceProposerCrud.entity.ProposerPagination;
import com.InsuranceProposerCrud.repository.NomineeRepository;
import com.InsuranceProposerCrud.repository.ProposerRepository;
import com.InsuranceProposerCrud.request.NomineeDto;
import com.InsuranceProposerCrud.request.RequestDto;
import com.InsuranceProposerCrud.request.ResponseDto;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@Service
public class ProposerServiceImpl implements ProposerService {

	@Autowired
	private ProposerRepository proposerRepo;

	@Autowired
	private NomineeRepository nomineeRepo;

	@Autowired
	private EntityManager entityManager;

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

		NomineeDto nomineeDto = requestDto.getNomineeDetails();
		List<Nominee> nomineeEnitity = new ArrayList<>();

		Nominee nominee = new Nominee();
		nominee.setNomineeFirstName(nomineeDto.getNomineeFirstName());
		nominee.setNomineeLastName(nomineeDto.getNomineeLastName());
		nominee.setNomineeDOB(nomineeDto.getNomineeDOB());
		nominee.setNomineeGender(nomineeDto.getNomineeGender());
		nominee.setRelationWithNominee(nomineeDto.getRelationWithNominee());
		nominee.setProposerId(proposerId);

		nomineeEnitity.add(nominee);

		nomineeRepo.saveAll(nomineeEnitity);
		return "Proposer Succesfully Saved along with Nominee's Details...!";
	}

	@Override
	public List<Proposer> allProposer(ProposerPagination pagination) {

		CriteriaBuilder cb = entityManager.getCriteriaBuilder();

		CriteriaQuery<Proposer> cq = cb.createQuery(Proposer.class);

		Root<Proposer> root = cq.from(Proposer.class);

		cq.where(cb.equal(root.get("status"), "y"));

		String sortBy = pagination.getSortBy();
		if (sortBy == null || sortBy.trim().isEmpty()) {
			sortBy = "proposerId";
		}

		String sortOrder = pagination.getSortOrder();
		if (sortOrder == null || sortOrder.trim().isEmpty()) {
			sortOrder = "asc";
		}

		// Apply sorting to the query
		if (sortOrder.equalsIgnoreCase("desc")) {
			cq.orderBy(cb.desc(root.get(sortBy)));
		} else {
			cq.orderBy(cb.asc(root.get(sortBy)));
		}

		int page = pagination.getPage();
		int size = pagination.getSize();

		TypedQuery<Proposer> query = entityManager.createQuery(cq);

		if (page == 0 && size == 0) {
			return query.getResultList();
		}

		query.setFirstResult(page * size); // start from this record
		query.setMaxResults(size); // max records to return

		List<Proposer> proposerList = query.getResultList();

		return proposerList;
	}

	@Override
	public ResponseDto proposerFindById(Integer proposerId) {
		Optional<Proposer> proposerOpt = proposerRepo.findById(proposerId);
		ResponseDto dto = new ResponseDto();

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
		// Optional<Proposer> opt = proposerRepo.findByProposerIdAndStatus(proposerId,
		// "y");

		Optional<Proposer> opt = proposerRepo.findByProposerIdAndStatus(proposerId, "y");

		if (opt.isPresent()) {
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
			Proposer proposerDetails = proposerRepo.save(existing);

//		NomineeDto dto = requestDto.getNomineeDetails();
////		List<Nominee> updatedNominees = new ArrayList<>();
//
//		// Get existing nominees for the proposer
//		Optional<Nominee> byProposerId = nomineeRepo.findByProposerId(proposerId);
//
//		if (!byProposerId.isPresent()) {
//			throw new IllegalArgumentException("Invalid Id");
//		}
//// Update each existing nominee with data from nomineeDtos	
//		Nominee nominee2 = byProposerId.get();
//		nominee2.setNomineeLastName(dto.getNomineeLastName());
//		nominee2.setNomineeDOB(dto.getNomineeDOB());
//		nominee2.setNomineeGender(dto.getNomineeGender());
//		nominee2.setRelationWithNominee(dto.getRelationWithNominee());
//		
//
//		if("y".equalsIgnoreCase(nominee2.getStatus()) ) {
//			nominee2.setStatus("y");
//		}
//	
//		nomineeRepo.save(nominee2);

			NomineeDto nomineeDto = requestDto.getNomineeDetails();

			Optional<Nominee> optional = nomineeRepo.findByProposerIdAndStatus(proposerId, "y");

			if (requestDto.getDoYouWantToAddNominee().equalsIgnoreCase("n")) {
				if (requestDto.getDoYouWantUpdateNominee().equalsIgnoreCase("y" + "")) {

					if (optional.isPresent()) {
						Nominee Existingnominiee = optional.get();

						// Existingnominiee.setAddress(nomineeDto.getnAddress());
						Existingnominiee.setNomineeDOB(nomineeDto.getNomineeDOB());
						Existingnominiee.setNomineeGender(nomineeDto.getNomineeGender());
						// Existingnominiee.setMobileNo(nomineeDto.getMobileNo());
						Existingnominiee.setNomineeFirstName(nomineeDto.getNomineeFirstName());
						Existingnominiee.setNomineeLastName(nomineeDto.getNomineeLastName());
						Existingnominiee.setRelationWithNominee(nomineeDto.getRelationWithNominee());

						nomineeRepo.save(Existingnominiee);
					}
				}

			}

			if (requestDto.getDoYouWantToAddNominee().equalsIgnoreCase("y")) {

				Nominee existingNominee = optional.get();

				existingNominee.setStatus("n");

				Nominee newNominee = new Nominee();

				NomineeDto nomineeDtoNew = requestDto.getNomineeDetails();

				newNominee.setProposerId(proposerDetails.getProposerId());

				newNominee.setNomineeDOB(nomineeDtoNew.getNomineeDOB());
				newNominee.setNomineeGender(nomineeDtoNew.getNomineeGender());

				newNominee.setNomineeFirstName(nomineeDtoNew.getNomineeFirstName());
				newNominee.setNomineeLastName(nomineeDtoNew.getNomineeLastName());
				newNominee.setRelationWithNominee(nomineeDtoNew.getRelationWithNominee());

				nomineeRepo.save(newNominee);

			} else {
				throw new NoSuchElementException("Proposer not found with ID: " + proposerId);
			}
		}
		return "Proposer updated successfully!";
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
