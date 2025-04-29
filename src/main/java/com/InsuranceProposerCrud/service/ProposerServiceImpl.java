package com.InsuranceProposerCrud.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.InsuranceProposerCrud.entity.Nominee;
import com.InsuranceProposerCrud.entity.Proposer;
import com.InsuranceProposerCrud.entity.ProposerPagination;
import com.InsuranceProposerCrud.entity.ProposerSearchFilter;
import com.InsuranceProposerCrud.enumclasses.Gender;
import com.InsuranceProposerCrud.enumclasses.ProposerTitle;
import com.InsuranceProposerCrud.repository.NomineeRepository;
import com.InsuranceProposerCrud.repository.ProposerRepository;
import com.InsuranceProposerCrud.request.NomineeDto;
import com.InsuranceProposerCrud.request.RequestDto;
import com.InsuranceProposerCrud.request.ResponseDto;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;

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
	public List<RequestDto> listAllProposers() {
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
	public List<Proposer> allProposer(ProposerPagination pagination) {

		CriteriaBuilder cb = entityManager.getCriteriaBuilder();

		CriteriaQuery<Proposer> cq = cb.createQuery(Proposer.class);

		Root<Proposer> root = cq.from(Proposer.class);

		// cq.where(cb.equal(root.get("status"), "y"));

		List<Predicate> predicates = new ArrayList<>();

//		if (pagination.getFilters() == null || pagination.getFilters().isEmpty()) {
//		    predicates.add(cb.equal(cb.lower(root.get("status")), "y"));
//		}
		ProposerSearchFilter filter = pagination.getSearchFilters();

		if (pagination.getSearchFilters() != null) {

			if (filter.getFirstName() != null && !filter.getFirstName().isEmpty()) {
				predicates
						.add(cb.like(cb.lower(root.get("firstName")), "%" + filter.getFirstName().toLowerCase() + "%"));
			}

			if (filter.getLastName() != null && !filter.getLastName().isEmpty()) {
				predicates.add(cb.like(cb.lower(root.get("lastName")), "%" + filter.getLastName().toLowerCase() + "%"));
			}

			if (filter.getEmail() != null && !filter.getEmail().isEmpty()) {
				if (!filter.getEmail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
					throw new IllegalArgumentException("Invalid email format.");
				}

				predicates.add(cb.like(cb.lower(root.get("email")), "%" + filter.getEmail().toLowerCase() + "%"));
			}

			if (filter.getStatus() != null && !filter.getStatus().isEmpty()) {
				if (!filter.getStatus().equalsIgnoreCase("y") && !filter.getStatus().equalsIgnoreCase("n")) {
					throw new IllegalArgumentException("Invalid status value. Allowed only 'y' or 'n'");
				}
				predicates.add(cb.equal(cb.lower(root.get("status")), filter.getStatus().toLowerCase()));
			} else {
				predicates.add(cb.equal(cb.lower(root.get("status")), "y"));
			}
		}

		cq.where(cb.and(predicates.toArray(new Predicate[0])));

		String sortBy = pagination.getSortBy();
		if (sortBy == null || sortBy.trim().isEmpty()) {
			sortBy = "proposerId";
		}

		String sortOrder = pagination.getSortOrder();
		if (sortOrder == null || sortOrder.trim().isEmpty()) {
			sortOrder = "desc";
		}

		if (sortOrder.equalsIgnoreCase("desc")) {
			cq.orderBy(cb.desc(root.get(sortBy)));
		} else {
			cq.orderBy(cb.asc(root.get(sortBy)));
		}

		TypedQuery<Proposer> query = entityManager.createQuery(cq);

		int page = pagination.getPage();
		int size = pagination.getSize();

		if (page == 0 && size == 0) {
			return query.getResultList();

		} else if (page <= 0 || size <= 0) {
			throw new IllegalArgumentException("Page number and size must be greater than 0.");
		} else {
			query.setFirstResult((page - 1) * size);
			query.setMaxResults(size);
			return query.getResultList();
		}
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
				// nomDto.setNomineeId(nomEntity.getNomineeId());
				nomDto.setIsUpdate(false);
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

		if (!opt.isPresent()) {
			throw new NoSuchElementException("Proposer not found with ID: " + proposerId);
		}

		Proposer existing = opt.get();
		List<String> errors = new ArrayList<>();

		// Validations
		if (requestDto.getEmail() != null) {
			Optional<Proposer> emailCheck = proposerRepo.findByEmail(requestDto.getEmail());
			if (!requestDto.getEmail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
				errors.add("Invalid email format");
			}
			if (emailCheck.isPresent() && !emailCheck.get().getProposerId().equals(proposerId)) {
				errors.add("Email already exists");
			}
		}

		if (requestDto.getPanNumber() != null) {
			Optional<Proposer> panCheck = proposerRepo.findByPanNumber(requestDto.getPanNumber());
			if (!requestDto.getPanNumber().matches("^[A-Z]{5}[0-9]{4}[A-Z]$")) {
				errors.add("Invalid PAN format");
			}
			if (panCheck.isPresent() && !panCheck.get().getProposerId().equals(proposerId)) {
				errors.add("PAN number already exists");
			}
		}

		if (requestDto.getAadharNo() != null) {
			Optional<Proposer> aadharCheck = proposerRepo.findByAadharNo(requestDto.getAadharNo());
			if (String.valueOf(requestDto.getAadharNo()).length() != 12) {
				errors.add("Aadhar number must be 12 digits");
			}
			if (aadharCheck.isPresent() && !aadharCheck.get().getProposerId().equals(proposerId)) {
				errors.add("Aadhar number already exists");
			}
		}

		if (requestDto.getMobileNo() != null) {
			Optional<Proposer> mobileCheck = proposerRepo.findByMobileNo(requestDto.getMobileNo());
			if (String.valueOf(requestDto.getMobileNo()).length() != 10) {
				errors.add("Mobile number must be 10 digits");
			}
			if (mobileCheck.isPresent() && !mobileCheck.get().getProposerId().equals(proposerId)) {
				errors.add("Mobile number already exists");
			}
		}

		if (requestDto.getPincode() != null && String.valueOf(requestDto.getPincode()).length() != 6) {
			errors.add("Pincode must be 6 digits");
		}

		if (!errors.isEmpty()) {
			throw new IllegalArgumentException(String.join("; ", errors));
		}

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

		Proposer proposerDetails = proposerRepo.save(existing);

		// Handling Nominee
		NomineeDto nomineeDto = requestDto.getNomineeDetails();
		Optional<Nominee> nomineeOpt = nomineeRepo.findByProposerIdAndStatus(proposerId, "y");

		if (requestDto.getDoYouWantToAddNominee() != null
				&& requestDto.getDoYouWantToAddNominee().equalsIgnoreCase("y")) {
			if (nomineeOpt.isPresent()) {
				Nominee oldNominee = nomineeOpt.get();
				oldNominee.setStatus("n");
				nomineeRepo.save(oldNominee);
			}

			Nominee newNominee = new Nominee();
			newNominee.setProposerId(proposerDetails.getProposerId());
			newNominee.setNomineeDOB(nomineeDto.getNomineeDOB());
			newNominee.setNomineeGender(nomineeDto.getNomineeGender());
			newNominee.setNomineeFirstName(nomineeDto.getNomineeFirstName());
			newNominee.setNomineeLastName(nomineeDto.getNomineeLastName());
			newNominee.setRelationWithNominee(nomineeDto.getRelationWithNominee());

			nomineeRepo.save(newNominee);
		} else if (requestDto.getDoYouWantUpdateNominee() != null
				&& requestDto.getDoYouWantUpdateNominee().equalsIgnoreCase("y")) {
			if (nomineeOpt.isPresent()) {
				Nominee existingNominee = nomineeOpt.get();
				existingNominee.setNomineeDOB(nomineeDto.getNomineeDOB());
				existingNominee.setNomineeGender(nomineeDto.getNomineeGender());
				existingNominee.setNomineeFirstName(nomineeDto.getNomineeFirstName());
				existingNominee.setNomineeLastName(nomineeDto.getNomineeLastName());
				existingNominee.setRelationWithNominee(nomineeDto.getRelationWithNominee());

				nomineeRepo.save(existingNominee);
			} else {
				throw new NoSuchElementException("No existing nominee found to update.");
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

	@Override
	public List<Proposer> fetchAllProposerByStringBuilder(ProposerPagination pagination) {

		StringBuilder sb = new StringBuilder("SELECT p FROM Proposer p WHERE p.status='y'");

		Map<String, Object> params = new HashMap<>();

		ProposerSearchFilter filter = pagination.getSearchFilters();

		// Add dynamic filters
		if (filter.getFirstName() != null && !filter.getFirstName().isEmpty()) {
			sb.append(" AND LOWER(p.firstName) LIKE :firstName");
			params.put("firstName", "%" + filter.getFirstName().toLowerCase() + "%");
		}

		if (filter.getLastName() != null && !filter.getLastName().isEmpty()) {
			sb.append(" AND LOWER(p.lastName) LIKE :lastName");
			params.put("lastName", "%" + filter.getLastName().toLowerCase() + "%");
		}

		if (filter.getEmail() != null && !filter.getEmail().isEmpty()) {
			sb.append("AND LOWER(p.email) LIKE :email");
			params.put("email", "%" + filter.getEmail().toLowerCase() + "%");
		}

		if (filter.getMobileNo() != null) {
			sb.append("AND LOWER(p.mobileNo) LIKE :mobileNo");
			params.put("mobileNo", "%" + filter.getMobileNo() + "%");
		}

		if (filter.getStatus() != null && !filter.getStatus().isEmpty()) {
			sb.append("AND LOWER(sb.status) LIKE :status");
			params.put("status", "%" + filter.getStatus().toLowerCase() + "%");
		}

		String sortBy = pagination.getSortBy();
		String sortOrder = pagination.getSortOrder();

		if (!sortOrder.equalsIgnoreCase("asc") && !sortOrder.equalsIgnoreCase("desc")) {
			sortOrder = "desc";
		}

		if (sortBy == null || sortBy.trim().isEmpty()) {
			sortBy = "proposerId";
		}

		if (sortOrder == null || sortOrder.trim().isEmpty()) {
			sortOrder = "desc";
		}

		sb.append(" ORDER BY p.").append(sortBy).append(" ").append(sortOrder); // ORDER BY p.proposerId desc

		TypedQuery<Proposer> query = entityManager.createQuery(sb.toString(), Proposer.class);

		for (Map.Entry<String, Object> entry : params.entrySet()) {
			query.setParameter(entry.getKey(), entry.getValue());
		}

		int page = pagination.getPage();
		int size = pagination.getSize();

		if (page == 0 && size == 0) {
			return query.getResultList(); // return all records
		} else {
			query.setFirstResult((page - 1) * size);
			query.setMaxResults(size);
		}

		return query.getResultList();
	}

	@Override
	public List<Proposer> fetchAllProposersWithNomineesByJoin(ProposerPagination pagination) {
		// Create CriteriaBuilder and CriteriaQuery
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Proposer> criteriaQuery = criteriaBuilder.createQuery(Proposer.class);

		// Define the root of the query (Proposer)
		Root<Proposer> proposerRoot = criteriaQuery.from(Proposer.class);

		// Join Proposer to Nominee (assuming Nominee has the proposerId as the foreign
		// key)
		Join<Proposer, Nominee> nomineeJoin = proposerRoot.join("nomineeDetails", JoinType.LEFT); // LEFT JOIN if not
																									// every Proposer
																									// has a Nominee

		// List to store predicates (conditions for the query)
		List<Predicate> predicates = new ArrayList<>();

		// Get filters from pagination
		ProposerSearchFilter filter = pagination.getSearchFilters();

		// Add dynamic filtering conditions
		if (filter.getFirstName() != null && !filter.getFirstName().isEmpty()) {
			Predicate firstNamePredicate = criteriaBuilder.like(criteriaBuilder.lower(proposerRoot.get("firstName")),
					"%" + filter.getFirstName().toLowerCase() + "%");
			predicates.add(firstNamePredicate);
		}

		if (filter.getLastName() != null && !filter.getLastName().isEmpty()) {
			Predicate lastNamePredicate = criteriaBuilder.like(criteriaBuilder.lower(proposerRoot.get("lastName")),
					"%" + filter.getLastName().toLowerCase() + "%");
			predicates.add(lastNamePredicate);
		}

		if (filter.getEmail() != null && !filter.getEmail().isEmpty()) {
			Predicate emailPredicate = criteriaBuilder.like(criteriaBuilder.lower(proposerRoot.get("email")),
					"%" + filter.getEmail().toLowerCase() + "%");
			predicates.add(emailPredicate);
		}

		if (filter.getMobileNo() != null) {
			Predicate mobileNoPredicate = criteriaBuilder.like(criteriaBuilder.lower(proposerRoot.get("mobileNo")),
					"%" + filter.getMobileNo() + "%");
			predicates.add(mobileNoPredicate);
		}

		if (filter.getStatus() != null && !filter.getStatus().isEmpty()) {
			Predicate statusPredicate = criteriaBuilder.equal(proposerRoot.get("status"), filter.getStatus());
			predicates.add(statusPredicate);
		}

		// Combine all predicates using AND logic
		criteriaQuery.where(criteriaBuilder.and(predicates.toArray(new Predicate[0])));

		// Add sorting logic (based on sortBy and sortOrder)
		String sortBy = pagination.getSortBy();
		String sortOrder = pagination.getSortOrder();
		if (sortBy != null && !sortBy.isEmpty()) {
			if (sortOrder.equalsIgnoreCase("desc")) {
				criteriaQuery.orderBy(criteriaBuilder.desc(proposerRoot.get(sortBy)));
			} else {
				criteriaQuery.orderBy(criteriaBuilder.asc(proposerRoot.get(sortBy)));
			}
		}

		// Create the query and set pagination (page and size)
		TypedQuery<Proposer> query = entityManager.createQuery(criteriaQuery);

		int page = pagination.getPage();
		int size = pagination.getSize();
		if (page > 0 && size > 0) {
			query.setFirstResult((page - 1) * size); // Set starting record
			query.setMaxResults(size); // Set number of records per page
		}

		// Execute the query and get results
		return query.getResultList(); // Return the Proposer list with NomineeDetails
	}

	@Override
	public String exportProposersToExcel() throws FileNotFoundException, IOException {

		//List<Proposer> allProposers = proposerRepo.findAllByStatus("y");

		XSSFWorkbook workbook = new XSSFWorkbook();

		XSSFSheet sheet = workbook.createSheet("Proposer_Data");

		XSSFRow headerRow = sheet.createRow(0);

//		headerRow.createCell(0).setCellValue("Proposer ID");
//		headerRow.createCell(1).setCellValue("Title");
//		headerRow.createCell(2).setCellValue("First Name");
//		headerRow.createCell(3).setCellValue("Middle Name");
//		headerRow.createCell(4).setCellValue("Last Name");
//		headerRow.createCell(5).setCellValue("Gender");
//		headerRow.createCell(6).setCellValue("Date of Birth");
//		headerRow.createCell(7).setCellValue("PAN Number");
//		headerRow.createCell(8).setCellValue("Aadhar Number");
//		headerRow.createCell(9).setCellValue("Status");
//		headerRow.createCell(10).setCellValue("Email");
//		headerRow.createCell(11).setCellValue("Mobile No");
//		headerRow.createCell(12).setCellValue("Alternate Mobile No");
//		headerRow.createCell(13).setCellValue("Address Line 1");
//		headerRow.createCell(14).setCellValue("Address Line 2");
//		headerRow.createCell(15).setCellValue("Address Line 3");
//		headerRow.createCell(16).setCellValue("Pincode");
//		headerRow.createCell(17).setCellValue("City");
//		headerRow.createCell(18).setCellValue("State");

		String[] headers = { "proposer_id", "proposer_title", "first_name", "middle_name", "last_name", "gender",
				"date_of_birth", "pan_number", "aadhar_number", "active_status", "email", "mobile_no",
				"alternate_mobile_number", "addressLine1", "addressLine2", "addressLine3", "pincode", "city", "state" };

		for (int i = 0; i < headers.length; i++) {
			headerRow.createCell(i).setCellValue(headers[i]);
		}

//		int rowIndex = 1;
//
//		for (Proposer proposer : allProposers) {
//			
//			Row row = sheet.createRow(rowIndex++);
//			
//			row.createCell(0).setCellValue(proposer.getProposerId());
//			row.createCell(1).setCellValue(proposer.getProposerTitle().toString());
//			row.createCell(2).setCellValue(proposer.getFirstName());
//			row.createCell(3).setCellValue(proposer.getMiddleName());
//			row.createCell(4).setCellValue(proposer.getLastName());
//			row.createCell(5).setCellValue(proposer.getGender().toString());
//			row.createCell(6).setCellValue(proposer.getDateOfBirth().toString());
//			row.createCell(7).setCellValue(proposer.getPanNumber());
//			row.createCell(8).setCellValue(proposer.getAadharNo());
//			row.createCell(9).setCellValue(proposer.getStatus());
//			row.createCell(10).setCellValue(proposer.getEmail());
//			row.createCell(11).setCellValue(proposer.getMobileNo());
//			row.createCell(12).setCellValue(proposer.getAlternateMobNo());
//			row.createCell(13).setCellValue(proposer.getAddressLine1());
//			row.createCell(14).setCellValue(proposer.getAddressLine2());
//			row.createCell(15).setCellValue(proposer.getAddressLine3());
//			row.createCell(16).setCellValue(proposer.getPincode());
//			row.createCell(17).setCellValue(proposer.getCity());
//			row.createCell(18).setCellValue(proposer.getState());
//		}

//		try(FileOutputStream fileOutput=new FileOutputStream(filePath))
//		{
//			workbook.write(fileOutput);
//		}

		String SystemPath = "C:/ExcelFiles";
		new File(SystemPath).mkdirs();

//	    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
//	    String formattedDateTime = LocalDateTime.now().format(formatter);

		String fileName = "Proposer_Data_" + UUID.randomUUID().toString().substring(0, 4) + ".xlsx";
		String filepath = SystemPath + "/" + fileName;

		try (FileOutputStream fileOut = new FileOutputStream(filepath)) {
			workbook.write(fileOut);
		}
		workbook.close();

		return filepath;

//		ServletOutputStream output=response.getOutputStream();
//		workbook.write(output);
//		workbook.close();
//		output.close();
	}

	@Override
	public void importFromExcel(MultipartFile file) throws IOException {
		List<Proposer> proposer = new ArrayList<>();
		
		XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());

		XSSFSheet sheet = workbook.getSheet("Proposr_Data");
		
		for (int i = 1; i <= sheet.getLastRowNum(); i++) {

			Row row = sheet.getRow(i);

			if (row == null)
				continue;

			Proposer p = new Proposer();
		
			p.setProposerTitle(ProposerTitle.valueOf(row.getCell(1).getStringCellValue()));
			p.setFirstName(row.getCell(2).getStringCellValue());
			p.setMiddleName(row.getCell(3).getStringCellValue());
			p.setLastName(row.getCell(4).getStringCellValue());
			p.setGender(Gender.valueOf(row.getCell(5).getStringCellValue()));
			p.setDateOfBirth((Date)row.getCell(6).getDateCellValue());
			p.setPanNumber(row.getCell(7).getStringCellValue());
			p.setAadharNo((long) row.getCell(8).getNumericCellValue());
			p.setStatus(row.getCell(9).getStringCellValue());
			p.setEmail(row.getCell(10).getStringCellValue());
			p.setMobileNo((long) row.getCell(11).getNumericCellValue());
			p.setAlternateMobNo((long) row.getCell(12).getNumericCellValue());
			p.setAddressLine1(row.getCell(13).getStringCellValue());
			p.setAddressLine2(row.getCell(14).getStringCellValue());
			p.setAddressLine3(row.getCell(15).getStringCellValue());
			p.setPincode((long) row.getCell(16).getNumericCellValue());
			p.setCity(row.getCell(17).getStringCellValue());
			p.setState(row.getCell(18).getStringCellValue());

			proposer.add(p);
		}
		workbook.close();
		proposerRepo.saveAll(proposer);
	}
	
}
