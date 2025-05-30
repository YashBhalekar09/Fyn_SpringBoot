package com.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.dto.NomineeDto;
import com.dto.RequestDto;
import com.dto.ResponseDto;
import com.entity.BatchQueue;
import com.entity.Nominee;
import com.entity.Proposer;
import com.entity.ProposerSearchFilter;
import com.entity.ProposerSearchRequest;
import com.entity.ProposersError;
import com.enums.Gender;
import com.enums.ProposerTitle;
import com.repository.BatchProcessingRepository;
import com.repository.NomineeRepository;
import com.repository.ProposerErrorRepository;
import com.repository.ProposerRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@Service
public class ProposerServiceImpl implements ProposerService {

	@Autowired
	private ProposerRepository proposerRepo;

	@Autowired
	private NomineeRepository nomineeRepo;

	@Autowired
	private ProposerErrorRepository errorRepo;

	@Autowired
	private EntityManager entityManager;

	@Autowired
	private BatchProcessingRepository batchQueueRepo;

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
	public List<Proposer> allProposer(ProposerSearchRequest pagination) {

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
		ResponseDto responseDto = new ResponseDto();

		if (proposerOpt.isPresent()) {
			Proposer proposer = proposerOpt.get();

			responseDto.setFirstName(proposer.getFirstName());
			responseDto.setMiddleName(proposer.getMiddleName());
			responseDto.setLastName(proposer.getLastName());
			responseDto.setAddressLine1(proposer.getAddressLine1());
			responseDto.setAddressLine2(proposer.getAddressLine2());
			responseDto.setAddressLine3(proposer.getAddressLine3());
			responseDto.setAlternateMobNo(proposer.getAlternateMobNo());
			responseDto.setCity(proposer.getCity());
			responseDto.setEmail(proposer.getEmail());
			responseDto.setMobileNo(proposer.getMobileNo());
			responseDto.setPincode(proposer.getPincode());
			responseDto.setProposerTitle(proposer.getProposerTitle());
			responseDto.setState(proposer.getState());
			responseDto.setGender(proposer.getGender());
			responseDto.setDateOfBirth(proposer.getDateOfBirth());
			responseDto.setAadharNo(proposer.getAadharNo());
			responseDto.setPanNumber(proposer.getPanNumber());
			responseDto.setStatus(proposer.getStatus());

			List<Nominee> nomineeEntity = nomineeRepo.getByProposerId(proposerId);
			List<NomineeDto> nomineeDtoList = new ArrayList<>();

			for (Nominee nomEntity : nomineeEntity) {
				NomineeDto nomineeDto = new NomineeDto();
				nomineeDto.setNomineeFirstName(nomEntity.getNomineeFirstName());
				nomineeDto.setNomineeLastName(nomEntity.getNomineeLastName());
				nomineeDto.setNomineeGender(nomEntity.getNomineeGender());
				nomineeDto.setRelationWithNominee(nomEntity.getRelationWithNominee());
				nomineeDto.setNomineeDOB(nomEntity.getNomineeDOB());
				// nomDto.setNomineeId(nomEntity.getNomineeId());
				nomineeDto.setIsUpdate(false);
				nomineeDto.setProposerId(proposerId);

				nomineeDtoList.add(nomineeDto);
			}

			responseDto.setNomineeDetails(nomineeDtoList);
		}

		return responseDto;
	}

	@Override
	public String deleteProposer(Integer proposerId) {
		Optional<Proposer> optional = proposerRepo.findById(proposerId);
		if (optional.isPresent()) {
			Proposer proposer = optional.get();
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

		Optional<Proposer> optional = proposerRepo.findByProposerIdAndStatus(proposerId, "y");

		if (!optional.isPresent()) {
			throw new NoSuchElementException("Proposer not found with ID: " + proposerId);
		}

		Proposer existing = optional.get();
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
	public List<Proposer> fetchAllProposerByStringBuilder(ProposerSearchRequest pagination) {

		StringBuilder stringBuilder = new StringBuilder("SELECT p FROM Proposer p WHERE p.status='y'");

		Map<String, Object> params = new HashMap<>();

		ProposerSearchFilter filter = pagination.getSearchFilters();

		// Add dynamic filters
		if (filter.getFirstName() != null && !filter.getFirstName().isEmpty()) {
			stringBuilder.append(" AND LOWER(p.firstName) LIKE :firstName");
			params.put("firstName", "%" + filter.getFirstName().toLowerCase() + "%");
		}

		if (filter.getLastName() != null && !filter.getLastName().isEmpty()) {
			stringBuilder.append(" AND LOWER(p.lastName) LIKE :lastName");
			params.put("lastName", "%" + filter.getLastName().toLowerCase() + "%");
		}

		if (filter.getEmail() != null && !filter.getEmail().isEmpty()) {
			stringBuilder.append("AND LOWER(p.email) LIKE :email");
			params.put("email", "%" + filter.getEmail().toLowerCase() + "%");
		}

		if (filter.getMobileNo() != null) {
			stringBuilder.append("AND LOWER(p.mobileNo) LIKE :mobileNo");
			params.put("mobileNo", "%" + filter.getMobileNo() + "%");
		}

		if (filter.getStatus() != null && !filter.getStatus().isEmpty()) {
			stringBuilder.append("AND LOWER(sb.status) LIKE :status");
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

		stringBuilder.append(" ORDER BY p.").append(sortBy).append(" ").append(sortOrder); // ORDER BY p.proposerId desc

		TypedQuery<Proposer> query = entityManager.createQuery(stringBuilder.toString(), Proposer.class);

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
	public List<Proposer> fetchAllProposersWithNomineesByJoin(ProposerSearchRequest pagination) {
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

		XSSFWorkbook workbook = new XSSFWorkbook();

		XSSFSheet sheet = workbook.createSheet("Proposer_Data");

		XSSFRow headerRow = sheet.createRow(0);

		String[] headers = { "proposer_id", "proposer_title", "first_name", "middle_name", "last_name", "gender",
				"date_of_birth", "pan_number", "aadhar_number", "active_status", "email", "mobile_no",
				"alternate_mobile_number", "addressLine1", "addressLine2", "addressLine3", "pincode", "city", "state" };

		for (int i = 0; i < headers.length; i++) {
			headerRow.createCell(i).setCellValue(headers[i]);
		}

		String SystemPath = "C:/ExcelFiles";
		new File(SystemPath).mkdirs();

		String fileName = "Proposer_Data_" + UUID.randomUUID().toString().substring(0, 4) + ".xlsx";
		String filepath = SystemPath + "/" + fileName;

		try (FileOutputStream fileOut = new FileOutputStream(filepath)) {
			workbook.write(fileOut);
		}
		workbook.close();

		return filepath;
	}

	@Override
	public String importFromExcel(MultipartFile file) throws IOException {

		Workbook workbook = new XSSFWorkbook(file.getInputStream());
		Sheet sheet = workbook.getSheetAt(0);

		Row newRow = sheet.getRow(0);
		
		int lastColumn = newRow.getLastCellNum();
		
		newRow.createCell(lastColumn).setCellValue("Status");
		newRow.createCell(lastColumn + 1).setCellValue("Error Message");

		List<String> responseErrors = new ArrayList<>();
		List<ProposersError> currentFileErrors = new ArrayList<>();

		for (int i = 1; i <= sheet.getLastRowNum(); i++) {

			Row row = sheet.getRow(i);
			if (row == null)
				continue;

			List<String> errorFields = new ArrayList<>();
			List<String> fieldNames = new ArrayList<>();

			Cell title = row.getCell(1);
			if (title == null || title.getStringCellValue().isEmpty()) {
				errorFields.add("Title is missing");
				fieldNames.add("Title");
			}

			Cell firstName = row.getCell(2);
			if (firstName == null || firstName.getStringCellValue().isEmpty()) {
				errorFields.add("First Name is missing");
				fieldNames.add("First Name");
			}

			Cell middleName = row.getCell(3);
			if (middleName == null || middleName.getStringCellValue().isEmpty()) {
				errorFields.add("Middle Name is missing");
				fieldNames.add("Middle Name");
			}

			Cell lastName = row.getCell(4);
			if (lastName == null || lastName.getStringCellValue().isEmpty()) {
				errorFields.add("Last Name is missing");
				fieldNames.add("Last Name");
			}

			Cell gender = row.getCell(5);
			if (gender == null || gender.getStringCellValue().isEmpty()) {
				errorFields.add("Gender is missing");
				fieldNames.add("Gender");
			}

			Cell dob = row.getCell(6);
			if (dob == null || dob.getDateCellValue() == null) {
				errorFields.add("Date of Birth is missing");
				fieldNames.add("Date of Birth");
			}

			Cell panNumber = row.getCell(7);
			if (panNumber == null || panNumber.getCellType() != CellType.STRING) {
				errorFields.add("Pan Number is missing");
				fieldNames.add("Pan Number");
			} else {
				String pan = panNumber.getStringCellValue().trim().toUpperCase();
				if (!pan.matches("[A-Z]{5}[0-9]{4}[A-Z]{1}")) {
					errorFields.add("Pan Number is invalid (e.g., ABCDE1234F)");
					fieldNames.add("Pan Number");
				} else if (proposerRepo.existsByPanNumber(pan)) {
					errorFields.add("Pan number already exists in database");
					fieldNames.add("Pan Number");
				}
			}

			Cell aadharNumber = row.getCell(8);
			if (aadharNumber == null || aadharNumber.getCellType() != CellType.NUMERIC) {
				errorFields.add("Aadhar Number is missing or invalid");
				fieldNames.add("Aadhar Number");
			} else {
				long aadhar = (long) aadharNumber.getNumericCellValue();
				String aadharStr = String.valueOf(aadhar);
				if (aadharStr.length() != 12) {
					errorFields.add("Aadhar Number is invalid (must be 12 digits)");
					fieldNames.add("Aadhar Number");
				} else if (proposerRepo.existsByAadharNo(Long.parseLong(aadharStr))) {
					errorFields.add("Aadhar number already exists in database");
					fieldNames.add("Aadhar Number");
				}
			}

			Cell status = row.getCell(9);
			if (status == null || status.getStringCellValue().isEmpty()) {
				errorFields.add("Status is missing");
				fieldNames.add("Status");
			}

			Cell email = row.getCell(10);
			if (email == null || email.getCellType() != CellType.STRING
					|| email.getStringCellValue().trim().isEmpty()) {
				errorFields.add("Email Id is missing");
				fieldNames.add("Email Id");
			} else {
				String emailStr = email.getStringCellValue().trim();
				if (!emailStr.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$")) {
					errorFields.add("Email Id is invalid");
					fieldNames.add("Email Id");
				} else if (proposerRepo.existsByEmail(emailStr)) {
					errorFields.add("Email Id already exists in database");
					fieldNames.add("Email Id");
				}
			}

			Cell mobileNumber = row.getCell(11);
			if (mobileNumber == null || mobileNumber.getCellType() != CellType.NUMERIC) {
				errorFields.add("Mobile Number is missing or not numeric");
				fieldNames.add("Mobile Number");
			} else {
				long mobile = (long) mobileNumber.getNumericCellValue();
				String mobileStr = String.valueOf(mobile);
				if (mobileStr.length() != 10 || !mobileStr.matches("[6-9][0-9]{9}")) {
					errorFields.add("Mobile Number is invalid (must be 10 digits)");
					fieldNames.add("Mobile Number");
				} else if (proposerRepo.existsByMobileNo(Long.parseLong(mobileStr))) {
					errorFields.add("Mobile number already exists in database");
					fieldNames.add("Mobile Number");
				}
			}

			Cell altMobileNumber = row.getCell(12);
			if (altMobileNumber == null || altMobileNumber.getNumericCellValue() == 0) {
				errorFields.add("Alternate Mobile Number is missing");
				fieldNames.add("Alternate Mobile Number");
			}

			Cell address1 = row.getCell(13);
			if (address1 == null || address1.getStringCellValue().isEmpty()) {
				errorFields.add("Address1 is missing");
				fieldNames.add("Address1");
			}

			Cell address2 = row.getCell(14);
			if (address2 == null || address2.getStringCellValue().isEmpty()) {
				errorFields.add("Address2 is missing");
				fieldNames.add("Address2");
			}

			Cell address3 = row.getCell(15);
			if (address3 == null || address3.getStringCellValue().isEmpty()) {
				errorFields.add("Address3 is missing");
				fieldNames.add("Address3");
			}

			Cell pincode = row.getCell(16);
			if (pincode == null || pincode.getNumericCellValue() == 0) {
				errorFields.add("Pincode is missing");
				fieldNames.add("Pincode");
			} else if (String.valueOf((long) pincode.getNumericCellValue()).length() != 6) {
				errorFields.add("Pincode is invalid");
				fieldNames.add("Pincode");
			}

			Cell city = row.getCell(17);
			if (city == null || city.getStringCellValue().isEmpty()) {
				errorFields.add("City is missing");
				fieldNames.add("City");
			}

			Cell state = row.getCell(18);
			if (state == null || state.getStringCellValue().isEmpty()) {
				errorFields.add("State is missing");
				fieldNames.add("State");
			}

			Cell errorstatus = row.createCell(lastColumn); // Status
			Cell errorMessage = row.createCell(lastColumn + 1); // Error

			if (!errorFields.isEmpty()) {
				for (int j = 0; j < errorFields.size(); j++) {
					ProposersError error = new ProposersError();

//					 error.setErrorField(String.join(", ", fieldNames));
//					    error.setErrorMessage(String.join("; ", errorFields));
					error.setErrorMessage(errorFields.get(j));
					error.setErrorField(fieldNames.get(j));
					error.setStatus("Failed");
					error.setRowNumber(i);
					currentFileErrors.add(error);
					errorRepo.save(error);
				}
				errorstatus.setCellValue("failed");
				errorMessage.setCellValue(String.join(", ", errorFields));

				responseErrors.add("Row " + i + ": " + String.join(", ", errorFields));
			} else {
				// Save proposer
				Proposer proposer = new Proposer();
				proposer.setProposerTitle(ProposerTitle.valueOf(title.getStringCellValue().trim()));
				proposer.setFirstName(firstName.getStringCellValue().trim());
				proposer.setMiddleName(middleName.getStringCellValue().trim());
				proposer.setLastName(lastName.getStringCellValue().trim());
				proposer.setGender(Gender.valueOf(gender.getStringCellValue().trim()));
				proposer.setDateOfBirth(new java.sql.Date(dob.getDateCellValue().getTime()));
				proposer.setPanNumber(panNumber.getStringCellValue().trim());
				proposer.setAadharNo((long) aadharNumber.getNumericCellValue());
				proposer.setStatus(status.getStringCellValue());
				proposer.setEmail(email.getStringCellValue().trim());
				proposer.setMobileNo((long) mobileNumber.getNumericCellValue());
				proposer.setAlternateMobNo((long) altMobileNumber.getNumericCellValue());
				proposer.setAddressLine1(address1.getStringCellValue().trim());
				proposer.setAddressLine2(address2.getStringCellValue().trim());
				proposer.setAddressLine3(address3.getStringCellValue().trim());
				proposer.setPincode((long) pincode.getNumericCellValue());
				proposer.setCity(city.getStringCellValue().trim());
				proposer.setState(state.getStringCellValue().trim());

				Proposer saved = proposerRepo.save(proposer);

				ProposersError successEntry = new ProposersError();
				successEntry.setErrorMessage("Proposer saved into DB, with ID: " + saved.getProposerId());
				successEntry.setErrorField("Save");
//				successEntry.setErrorField(String.join(", ", fieldNames));
//				successEntry.setErrorMessage(String.join("; ", errorFields));
				successEntry.setStatus("Success");
				successEntry.setRowNumber(i);
				currentFileErrors.add(successEntry);
				errorRepo.save(successEntry);

				errorstatus.setCellValue("Success");
				errorMessage.setCellValue("Saved successfully");
			}
		}
		String errorPath = "C:/ErrorFiles/";
		new File(errorPath).mkdirs();
		String errorFileName = "ValidationErrors_" + UUID.randomUUID().toString().substring(0, 4) + ".xlsx";

		FileOutputStream errorOut = new FileOutputStream(errorPath + errorFileName);
		workbook.write(errorOut);
		errorOut.close();
		workbook.close();

		// Create separate error Excel if needed
		if (!currentFileErrors.isEmpty()) {
			XSSFWorkbook errorWorkbook = new XSSFWorkbook();
			XSSFSheet errorSheet = errorWorkbook.createSheet("Error_sheet");

			Row header = errorSheet.createRow(0);
			String[] headers = { "Error Field", "Error Message", "Error Status", "Row Number" };
			for (int i = 0; i < headers.length; i++) {
				header.createCell(i).setCellValue(headers[i]);
			}

			int rowIndex = 1;
			for (ProposersError err : currentFileErrors) {
				Row errRow = errorSheet.createRow(rowIndex++);
				errRow.createCell(0).setCellValue(err.getErrorField());
				errRow.createCell(1).setCellValue(err.getErrorMessage());
				errRow.createCell(2).setCellValue(err.getStatus());
				errRow.createCell(3).setCellValue(err.getRowNumber());
			}

//	        String errorPath = "C:/ErrorFiles/";
//	        new File(errorPath).mkdirs();
//	        String errorFileName = "ValidationErrors_" + UUID.randomUUID().toString().substring(0, 4) + ".xlsx";
//
//	        FileOutputStream errorOut = new FileOutputStream(errorPath + errorFileName);
//	        errorWorkbook.write(errorOut);
//	        errorOut.close();
//	        errorWorkbook.close();

			throw new RuntimeException("Validation failed. See error file: " + errorPath + errorFileName);
		}

		if (!responseErrors.isEmpty()) {
			throw new RuntimeException("Validation failed: " + String.join(", ", responseErrors));
		}

		return "All proposers saved successfully";
	}

	@Override
	public String batchProcessing(MultipartFile file) throws IOException {

		Workbook workbook = new XSSFWorkbook(file.getInputStream());
		Sheet sheet = workbook.getSheetAt(0);

		// new logic
		int totalRows = sheet.getLastRowNum();
		//int batchSize = 3;

		if (totalRows > 5) {

			String filePath = "C:/batchuploads/";
			new File(filePath).mkdirs();

			String fileName = "QueuedFile_" + UUID.randomUUID().toString().substring(0, 4) + ".xlsx";
			String fullFilePath = filePath + fileName;

			try (FileOutputStream out = new FileOutputStream(fullFilePath)) {
				workbook.write(out);
			}
			
			BatchQueue queue = new BatchQueue();
			queue.setFilePath(fullFilePath);
			queue.setRowCount(totalRows);
			queue.setRowRead(0);
			queue.setStatus("n");
			queue.setIsProcess("n");
			//queue.setLastProcessCount(batchSize);
			batchQueueRepo.save(queue);

			workbook.close();
			return "File queued for batch processing.";
		}
		// end

		List<String> responseErrors = new ArrayList<>();
		List<ProposersError> currentFileErrors = new ArrayList<>();

		for (int i = 1; i <= sheet.getLastRowNum(); i++) {

			Row row = sheet.getRow(i);
			if (row == null)
				continue;

			List<String> errorFields = new ArrayList<>();
			List<String> fieldNames = new ArrayList<>();

			Cell title = row.getCell(1);
			if (title == null || title.getStringCellValue().isEmpty()) {
				errorFields.add("Title is missing");
				fieldNames.add("Title");
			}

			Cell firstName = row.getCell(2);
			if (firstName == null || firstName.getStringCellValue().isEmpty()) {
				errorFields.add("First Name is missing");
				fieldNames.add("First Name");
			}

			Cell middleName = row.getCell(3);
			if (middleName == null || middleName.getStringCellValue().isEmpty()) {
				errorFields.add("Middle Name is missing");
				fieldNames.add("Middle Name");
			}

			Cell lastName = row.getCell(4);
			if (lastName == null || lastName.getStringCellValue().isEmpty()) {
				errorFields.add("Last Name is missing");
				fieldNames.add("Last Name");
			}

			Cell gender = row.getCell(5);
			if (gender == null || gender.getStringCellValue().isEmpty()) {
				errorFields.add("Gender is missing");
				fieldNames.add("Gender");
			}

			Cell dob = row.getCell(6);
			if (dob == null || dob.getDateCellValue() == null) {
				errorFields.add("Date of Birth is missing");
				fieldNames.add("Date of Birth");
			}

			Cell panNumber = row.getCell(7);
			if (panNumber == null || panNumber.getCellType() != CellType.STRING) {
				errorFields.add("Pan Number is missing");
				fieldNames.add("Pan Number");
			} else {
				String pan = panNumber.getStringCellValue().trim().toUpperCase();
				if (!pan.matches("[A-Z]{5}[0-9]{4}[A-Z]{1}")) {
					errorFields.add("Pan Number is invalid (e.g., ABCDE1234F)");
					fieldNames.add("Pan Number");
				} else if (proposerRepo.existsByPanNumber(pan)) {
					errorFields.add("Pan number already exists in database");
					fieldNames.add("Pan Number");
				}
			}

			Cell aadharNumber = row.getCell(8);
			if (aadharNumber == null || aadharNumber.getCellType() != CellType.NUMERIC) {
				errorFields.add("Aadhar Number is missing or invalid");
				fieldNames.add("Aadhar Number");
			} else {
				long aadhar = (long) aadharNumber.getNumericCellValue();
				String aadharStr = String.valueOf(aadhar);
				if (aadharStr.length() != 12) {
					errorFields.add("Aadhar Number is invalid (must be 12 digits)");
					fieldNames.add("Aadhar Number");
				} else if (proposerRepo.existsByAadharNo(Long.parseLong(aadharStr))) {
					errorFields.add("Aadhar number already exists in database");
					fieldNames.add("Aadhar Number");
				}
			}

			Cell status = row.getCell(9);
			if (status == null || status.getStringCellValue().isEmpty()) {
				errorFields.add("Status is missing");
				fieldNames.add("Status");
			}

			Cell email = row.getCell(10);
			if (email == null || email.getCellType() != CellType.STRING
					|| email.getStringCellValue().trim().isEmpty()) {
				errorFields.add("Email Id is missing");
				fieldNames.add("Email Id");
			} else {
				String emailStr = email.getStringCellValue().trim();
				if (!emailStr.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$")) {
					errorFields.add("Email Id is invalid");
					fieldNames.add("Email Id");
				} else if (proposerRepo.existsByEmail(emailStr)) {
					errorFields.add("Email Id already exists in database");
					fieldNames.add("Email Id");
				}
			}

			Cell mobileNumber = row.getCell(11);
			if (mobileNumber == null || mobileNumber.getCellType() != CellType.NUMERIC) {
				errorFields.add("Mobile Number is missing or not numeric");
				fieldNames.add("Mobile Number");
			} else {
				long mobile = (long) mobileNumber.getNumericCellValue();
				String mobileStr = String.valueOf(mobile);
				if (mobileStr.length() != 10 || !mobileStr.matches("[6-9][0-9]{9}")) {
					errorFields.add("Mobile Number is invalid (must be 10 digits)");
					fieldNames.add("Mobile Number");
				} else if (proposerRepo.existsByMobileNo(Long.parseLong(mobileStr))) {
					errorFields.add("Mobile number already exists in database");
					fieldNames.add("Mobile Number");
				}
			}

			Cell altMobileNumber = row.getCell(12);
			if (altMobileNumber == null || altMobileNumber.getNumericCellValue() == 0) {
				errorFields.add("Alternate Mobile Number is missing");
				fieldNames.add("Alternate Mobile Number");
			}

			Cell address1 = row.getCell(13);
			if (address1 == null || address1.getStringCellValue().isEmpty()) {
				errorFields.add("Address1 is missing");
				fieldNames.add("Address1");
			}

			Cell address2 = row.getCell(14);
			if (address2 == null || address2.getStringCellValue().isEmpty()) {
				errorFields.add("Address2 is missing");
				fieldNames.add("Address2");
			}

			Cell address3 = row.getCell(15);
			if (address3 == null || address3.getStringCellValue().isEmpty()) {
				errorFields.add("Address3 is missing");
				fieldNames.add("Address3");
			}

			Cell pincode = row.getCell(16);
			if (pincode == null || pincode.getNumericCellValue() == 0) {
				errorFields.add("Pincode is missing");
				fieldNames.add("Pincode");
			} else if (String.valueOf((long) pincode.getNumericCellValue()).length() != 6) {
				errorFields.add("Pincode is invalid");
				fieldNames.add("Pincode");
			}

			Cell city = row.getCell(17);
			if (city == null || city.getStringCellValue().isEmpty()) {
				errorFields.add("City is missing");
				fieldNames.add("City");
			}

			Cell state = row.getCell(18);
			if (state == null || state.getStringCellValue().isEmpty()) {
				errorFields.add("State is missing");
				fieldNames.add("State");
			}

			if (!errorFields.isEmpty()) {
				for (int j = 0; j < errorFields.size(); j++) {
					ProposersError error = new ProposersError();
					error.setErrorMessage(errorFields.get(j));
					error.setErrorField(fieldNames.get(j));
					error.setStatus("Failed");
					error.setRowNumber(i);
					currentFileErrors.add(error);
					errorRepo.save(error);
				}

				responseErrors.add("Row " + i + ": " + String.join(", ", errorFields));
			} else {
				// Save proposer
				Proposer proposer = new Proposer();
				proposer.setProposerTitle(ProposerTitle.valueOf(title.getStringCellValue().trim()));
				proposer.setFirstName(firstName.getStringCellValue().trim());
				proposer.setMiddleName(middleName.getStringCellValue().trim());
				proposer.setLastName(lastName.getStringCellValue().trim());
				proposer.setGender(Gender.valueOf(gender.getStringCellValue().trim()));
				proposer.setDateOfBirth(new java.sql.Date(dob.getDateCellValue().getTime()));
				proposer.setPanNumber(panNumber.getStringCellValue().trim());
				proposer.setAadharNo((long) aadharNumber.getNumericCellValue());
				proposer.setStatus(status.getStringCellValue());
				proposer.setEmail(email.getStringCellValue().trim());
				proposer.setMobileNo((long) mobileNumber.getNumericCellValue());
				proposer.setAlternateMobNo((long) altMobileNumber.getNumericCellValue());
				proposer.setAddressLine1(address1.getStringCellValue().trim());
				proposer.setAddressLine2(address2.getStringCellValue().trim());
				proposer.setAddressLine3(address3.getStringCellValue().trim());
				proposer.setPincode((long) pincode.getNumericCellValue());
				proposer.setCity(city.getStringCellValue().trim());
				proposer.setState(state.getStringCellValue().trim());

				Proposer saved = proposerRepo.save(proposer);

				ProposersError successEntry = new ProposersError();
				successEntry.setErrorMessage("Proposer saved into DB, with ID: " + saved.getProposerId());
				successEntry.setErrorField("Save");
				successEntry.setStatus("Success");
				successEntry.setRowNumber(i);
				currentFileErrors.add(successEntry);
				errorRepo.save(successEntry);
			}
		}
		String errorPath = "C:/ErrorFiles/";
		new File(errorPath).mkdirs();
		String errorFileName = "ValidationErrors_" + UUID.randomUUID().toString().substring(0, 4) + ".xlsx";

		FileOutputStream errorOut = new FileOutputStream(errorPath + errorFileName);
		workbook.write(errorOut);
		errorOut.close();
		workbook.close();

		// Create separate error Excel if needed
		if (!currentFileErrors.isEmpty()) {
			XSSFWorkbook errorWorkbook = new XSSFWorkbook();
			XSSFSheet errorSheet = errorWorkbook.createSheet("Error_sheet");

			Row header = errorSheet.createRow(0);
			String[] headers = { "Error Field", "Error Message", "Error Status", "Row Number" };
			for (int i = 0; i < headers.length; i++) {
				header.createCell(i).setCellValue(headers[i]);
			}

			int rowIndex = 1;
			for (ProposersError err : currentFileErrors) {
				Row errRow = errorSheet.createRow(rowIndex++);
				errRow.createCell(0).setCellValue(err.getErrorField());
				errRow.createCell(1).setCellValue(err.getErrorMessage());
				errRow.createCell(2).setCellValue(err.getStatus());
				errRow.createCell(3).setCellValue(err.getRowNumber());
			}

//	        String errorPath = "C:/ErrorFiles/";
//	        new File(errorPath).mkdirs();
//	        String errorFileName = "ValidationErrors_" + UUID.randomUUID().toString().substring(0, 4) + ".xlsx";
//
//	        FileOutputStream errorOut = new FileOutputStream(errorPath + errorFileName);
//	        errorWorkbook.write(errorOut);
//	        errorOut.close();
//	        errorWorkbook.close();

			throw new RuntimeException("Validation failed. See error file: " + errorPath + errorFileName);
		}

		if (!responseErrors.isEmpty()) {
			throw new RuntimeException("Validation failed: " + String.join(", ", responseErrors));
		}

		return "All proposers saved successfully";
	}

	@Override
	@Scheduled(fixedRate = 3000) // every 3 seconds
	public void processExcelBatch() throws IOException {

		List<BatchQueue> batchQueues = batchQueueRepo.findByIsProcess("n");

		for (BatchQueue batchQueue : batchQueues) {

			batchQueueRepo.save(batchQueue);

			try {
				FileInputStream fis=new FileInputStream(batchQueue.getFilePath());
				Workbook workbook = new XSSFWorkbook(fis);

				Sheet sheet = workbook.getSheetAt(0);
				int rowStart = batchQueue.getRowRead() + 1;
				System.out.println(rowStart);
				int totalRows = batchQueue.getRowCount();
				int batchSize = 3;
				
				Row headerRow = sheet.getRow(0);
				
				if (rowStart == 1) {
					int lastCol = headerRow.getLastCellNum();

					headerRow.createCell(lastCol).setCellValue("Error Message");

					headerRow.createCell(lastCol + 1).setCellValue("Error Status");
				}

				List<ProposersError> currentFileErrors = new ArrayList<>();
				List<String> responseErrors = new ArrayList<>();

				for (int i = rowStart; i <= totalRows && i < rowStart + batchSize; i++) {
					Row row = sheet.getRow(i);
					if (row == null)
						continue;

					List<String> errorFields = new ArrayList<>();
					List<String> fieldNames = new ArrayList<>();

					Cell title = row.getCell(1);
					if (title == null || title.getStringCellValue().isEmpty()) {
						errorFields.add("Title is missing");
						fieldNames.add("Title");
					}

					Cell firstName = row.getCell(2);
					if (firstName == null || firstName.getStringCellValue().isEmpty()) {
						errorFields.add("First Name is missing");
						fieldNames.add("First Name");
					}

					Cell middleName = row.getCell(3);
					if (middleName == null || middleName.getStringCellValue().isEmpty()) {
						errorFields.add("Middle Name is missing");
						fieldNames.add("Middle Name");
					}

					Cell lastName = row.getCell(4);
					if (lastName == null || lastName.getStringCellValue().isEmpty()) {
						errorFields.add("Last Name is missing");
						fieldNames.add("Last Name");
					}

					Cell gender = row.getCell(5);
					if (gender == null || gender.getStringCellValue().isEmpty()) {
						errorFields.add("Gender is missing");
						fieldNames.add("Gender");
					}

					Cell dob = row.getCell(6);
					if (dob == null || dob.getDateCellValue() == null) {
						errorFields.add("Date of Birth is missing");
						fieldNames.add("Date of Birth");
					}

					Cell panNumber = row.getCell(7);
					if (panNumber == null || panNumber.getCellType() != CellType.STRING) {
						errorFields.add("Pan Number is missing");
						fieldNames.add("Pan Number");
					} else {
						String pan = panNumber.getStringCellValue().trim().toUpperCase();
						if (!pan.matches("[A-Z]{5}[0-9]{4}[A-Z]{1}")) {
							errorFields.add("Pan Number is invalid (e.g., ABCDE1234F)");
							fieldNames.add("Pan Number");
						} else if (proposerRepo.existsByPanNumber(pan)) {
							errorFields.add("Pan number already exists in database");
							fieldNames.add("Pan Number");
						}
					}

					Cell aadharNumber = row.getCell(8);
					if (aadharNumber == null || aadharNumber.getCellType() != CellType.NUMERIC) {
						errorFields.add("Aadhar Number is missing or invalid");
						fieldNames.add("Aadhar Number");
					} else {
						long aadhar = (long) aadharNumber.getNumericCellValue();
						String aadharStr = String.valueOf(aadhar);
						if (aadharStr.length() != 12) {
							errorFields.add("Aadhar Number must be 12 digits");
							fieldNames.add("Aadhar Number");
						} else if (proposerRepo.existsByAadharNo(aadhar)) {
							errorFields.add("Aadhar number already exists in database");
							fieldNames.add("Aadhar Number");
						}
					}

					Cell status = row.getCell(9);
					if (status == null || status.getStringCellValue().isEmpty()) {
						errorFields.add("Status is missing");
						fieldNames.add("Status");
					}

					Cell email = row.getCell(10);
					if (email == null || email.getStringCellValue().trim().isEmpty()) {
						errorFields.add("Email Id is missing");
						fieldNames.add("Email Id");
					} else {
						String emailStr = email.getStringCellValue().trim();
						if (!emailStr.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$")) {
							errorFields.add("Email Id is invalid");
							fieldNames.add("Email Id");
						} else if (proposerRepo.existsByEmail(emailStr)) {
							errorFields.add("Email Id already exists in database");
							fieldNames.add("Email Id");
						}
					}

					Cell mobileNumber = row.getCell(11);
					if (mobileNumber == null || mobileNumber.getCellType() != CellType.NUMERIC) {
						errorFields.add("Mobile Number is missing or not numeric");
						fieldNames.add("Mobile Number");
					} else {
						long mobile = (long) mobileNumber.getNumericCellValue();
						String mobileStr = String.valueOf(mobile);
						if (!mobileStr.matches("[6-9][0-9]{9}")) {
							errorFields.add("Mobile Number is invalid (must start with 6-9)");
							fieldNames.add("Mobile Number");
						} else if (proposerRepo.existsByMobileNo(mobile)) {
							errorFields.add("Mobile number already exists in database");
							fieldNames.add("Mobile Number");
						}
					}

					Cell alternateMobile = row.getCell(12);
					if (alternateMobile == null || alternateMobile.getNumericCellValue() == 0) {
						errorFields.add("Alternate Mobile Number is missing");
						fieldNames.add("Alternate Mobile Number");
					}

					Cell address1 = row.getCell(13);
					if (address1 == null || address1.getStringCellValue().isEmpty()) {
						errorFields.add("Address1 is missing");
						fieldNames.add("Address1");
					}

					Cell address2 = row.getCell(14);
					if (address2 == null || address2.getStringCellValue().isEmpty()) {
						errorFields.add("Address2 is missing");
						fieldNames.add("Address2");
					}

					Cell address3 = row.getCell(15);
					if (address3 == null || address3.getStringCellValue().isEmpty()) {
						errorFields.add("Address3 is missing");
						fieldNames.add("Address3");
					}

					Cell pincode = row.getCell(16);
					if (pincode == null || pincode.getNumericCellValue() == 0) {
						errorFields.add("Pincode is missing");
						fieldNames.add("Pincode");
					} else if (String.valueOf((long) pincode.getNumericCellValue()).length() != 6) {
						errorFields.add("Pincode must be 6 digits");
						fieldNames.add("Pincode");
					}

					Cell city = row.getCell(17);
					if (city == null || city.getStringCellValue().isEmpty()) {
						errorFields.add("City is missing");
						fieldNames.add("City");
					}

					Cell state = row.getCell(18);
					if (state == null || state.getStringCellValue().isEmpty()) {
						errorFields.add("State is missing");
						fieldNames.add("State");
					}

					if (!errorFields.isEmpty()) {
						for (int j = 0; j < errorFields.size(); j++) {
							ProposersError error = new ProposersError();
							error.setBatchId(batchQueue.getQueid());
							error.setErrorMessage(errorFields.get(j));
							error.setErrorField(fieldNames.get(j));
							error.setStatus("Failed");
							error.setRowNumber(i);

							currentFileErrors.add(error);
							errorRepo.save(error);
						}
						Cell errorMessage = row.createCell(headerRow.getLastCellNum()-2);
						Cell errorstatus = row.createCell(headerRow.getLastCellNum() - 1);

						errorMessage.setCellValue(String.join(", ", errorFields));
						errorstatus.setCellValue("failed");
						responseErrors.add("Row " + i + ": " + String.join(", ", errorFields));
					} else {
						// SAVE PROPOSER
						Proposer proposer = new Proposer();
						proposer.setProposerTitle(ProposerTitle.valueOf(title.getStringCellValue().trim()));
						proposer.setFirstName(firstName.getStringCellValue().trim());
						proposer.setMiddleName(middleName.getStringCellValue().trim());
						proposer.setLastName(lastName.getStringCellValue().trim());
						proposer.setGender(Gender.valueOf(gender.getStringCellValue().trim()));
						proposer.setDateOfBirth(new java.sql.Date(dob.getDateCellValue().getTime()));
						proposer.setPanNumber(panNumber.getStringCellValue().trim());
						proposer.setAadharNo((long) aadharNumber.getNumericCellValue());
						proposer.setStatus(status.getStringCellValue().trim());
						proposer.setEmail(email.getStringCellValue().trim());
						proposer.setMobileNo((long) mobileNumber.getNumericCellValue());
						proposer.setAlternateMobNo((long) alternateMobile.getNumericCellValue());
						proposer.setAddressLine1(address1.getStringCellValue().trim());
						proposer.setAddressLine2(address2.getStringCellValue().trim());
						proposer.setAddressLine3(address3.getStringCellValue().trim());
						proposer.setPincode((long) pincode.getNumericCellValue());
						proposer.setCity(city.getStringCellValue().trim());
						proposer.setState(state.getStringCellValue().trim());

						Proposer saved = proposerRepo.save(proposer);
						
						ProposersError success = new ProposersError();
						
						success.setBatchId(batchQueue.getQueid());
						success.setRowNumber(i);
						success.setStatus("Success");
						success.setErrorField("Save");
						success.setErrorMessage("Saved with ID: " + saved.getProposerId());
						currentFileErrors.add(success);
						errorRepo.save(success);
						
						Cell errorMessage = row.createCell(headerRow.getLastCellNum()-2);
						Cell errorstatus = row.createCell(headerRow.getLastCellNum() - 1);

						errorMessage.setCellValue(saved.getProposerId().toString());
						errorstatus.setCellValue("success");

					}
					batchQueue.setRowRead(i);

				}

				if (batchQueue.getRowRead() >= totalRows) {
					batchQueue.setIsProcess("y");
					batchQueue.setStatus("y");

	//				Workbook resultWorkbook = new XSSFWorkbook();
	//				Sheet resultSheet = resultWorkbook.createSheet("Response Excel");
//
//					Row row = sheet.getRow(0);
//					Row resultHeader = resultSheet.createRow(0);
//					int colCount = row.getLastCellNum();
//
//					for (int i = 0; i < colCount; i++) {
//						resultHeader.createCell(i).setCellValue(row.getCell(i).toString());
//					}
//
//					resultHeader.createCell(colCount).setCellValue("Status");
//					resultHeader.createCell(colCount + 1).setCellValue("Error Message");
//
//					// Fetch all errors for this batch
//					List<ProposersError> allErrors = errorRepo.findByBatchId(batchQueue.getQueid());
//
//					int rowIndex = 1;
//					for (ProposersError error : allErrors) {
//						int errorRowNum = error.getRowNumber();
//						Row originalRow = sheet.getRow(errorRowNum);
//						if (originalRow == null)
//							continue;
//
//						Row resultRow = resultSheet.createRow(rowIndex++);
//
//						for (int i = 0; i < colCount; i++) {
//							Cell originalCell = originalRow.getCell(i);
//							if (originalCell != null) {
//								resultRow.createCell(i).setCellValue(originalCell.toString());
//							}
//						}
//
//						// Add status and error message
//						resultRow.createCell(colCount).setCellValue(error.getStatus());
//						resultRow.createCell(colCount + 1).setCellValue(error.getErrorMessage());
//					}

					// Save result file
					
				}
				String resultPath = batchQueue.getFilePath();
				try (FileOutputStream fos = new FileOutputStream(resultPath)) {
					workbook.write(fos);
				}
				batchQueue.setFilePath(resultPath);
				workbook.close();
				batchQueueRepo.save(batchQueue);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void getDBDataToExcel(OutputStream outputStream) throws IOException {

		List<Proposer> allProposers = proposerRepo.findAllByStatus("y");

		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("ProposerDB_Data");
		XSSFRow headerRow = sheet.createRow(0);

		String[] headers = { "proposer_id", "proposer_title", "first_name", "middle_name", "last_name", "gender",
				"date_of_birth", "pan_number", "aadhar_number", "active_status", "email", "mobile_no",
				"alternate_mobile_number", "addressLine1", "addressLine2", "addressLine3", "pincode", "city", "state" };

		for (int i = 0; i < headers.length; i++) {
			headerRow.createCell(i).setCellValue(headers[i]);
		}

		int rowIndex = 1;
		for (Proposer proposer : allProposers) {
			Row row = sheet.createRow(rowIndex++);
			row.createCell(0).setCellValue(proposer.getProposerId());
			row.createCell(1).setCellValue(proposer.getProposerTitle().toString());
			row.createCell(2).setCellValue(proposer.getFirstName());
			row.createCell(3).setCellValue(proposer.getMiddleName());
			row.createCell(4).setCellValue(proposer.getLastName());
			row.createCell(5).setCellValue(proposer.getGender().toString());
			row.createCell(6).setCellValue(proposer.getDateOfBirth().toString());
			row.createCell(7).setCellValue(proposer.getPanNumber());
			row.createCell(8).setCellValue(proposer.getAadharNo());
			row.createCell(9).setCellValue(proposer.getStatus());
			row.createCell(10).setCellValue(proposer.getEmail());
			row.createCell(11).setCellValue(proposer.getMobileNo());
			row.createCell(12).setCellValue(proposer.getAlternateMobNo());
			row.createCell(13).setCellValue(proposer.getAddressLine1());
			row.createCell(14).setCellValue(proposer.getAddressLine2());
			row.createCell(15).setCellValue(proposer.getAddressLine3());
			row.createCell(16).setCellValue(proposer.getPincode());
			row.createCell(17).setCellValue(proposer.getCity());
			row.createCell(18).setCellValue(proposer.getState());
		}

		workbook.write(outputStream);
		workbook.close();
	}

}
