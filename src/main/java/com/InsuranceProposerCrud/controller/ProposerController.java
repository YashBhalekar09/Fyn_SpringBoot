package com.InsuranceProposerCrud.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.InsuranceProposerCrud.entity.Proposer;
import com.InsuranceProposerCrud.entity.ProposerPagination;
import com.InsuranceProposerCrud.request.RequestDto;
import com.InsuranceProposerCrud.request.ResponseDto;
import com.InsuranceProposerCrud.response.ResponseHandler;
import com.InsuranceProposerCrud.service.ProposerService;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/proposer")
public class ProposerController {

	@Autowired
	private ProposerService proposerService;

	@PostMapping("/saveProposer")
	public ResponseHandler saveProposer(@RequestBody RequestDto requestDto) {
		ResponseHandler response = new ResponseHandler();

		try {
			String savedProposer = proposerService.saveProposer(requestDto);

			response.setData(savedProposer);
			response.setStatus(true);
			response.setMessage("success");

		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			response.setData(new ArrayList<>());
			response.setStatus(false);
			response.setMessage("Validation failed: " + e.getMessage());

		} catch (Exception e) {
			e.printStackTrace();

			response.setData(new ArrayList<>());
			response.setStatus(false);
			response.setMessage(e.getMessage());
		}

		return response;
	}

	@PostMapping("/all_proposer_by_criteriaBuilder")
	public ResponseHandler allProposerByCriteriaBuilder(@RequestBody ProposerPagination pagination) {
		ResponseHandler response = new ResponseHandler();

		List<RequestDto> getAllCount = proposerService.listAllProposers();
		int count = getAllCount.size();

		try {
			List<Proposer> allProposer = proposerService.allProposer(pagination);

			response.setData(allProposer);
			response.setStatus(true);
			response.setMessage("Success");

			if (pagination.getSearchFilters() != null || pagination.getSearchFilters().getFirstName() != null
					|| pagination.getSearchFilters().getLastName() != null
					|| pagination.getSearchFilters().getEmail() != null
					|| pagination.getSearchFilters().getMobileNo() != null
					|| pagination.getSearchFilters().getStatus() != null) {
				response.setTotalRecord(allProposer.size());
			} else {
				response.setTotalRecord(count);
			}

		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			response.setData(new ArrayList<>());
			response.setStatus(false);
			response.setMessage(e.getMessage());

		} catch (Exception e) {
			e.printStackTrace();
			response.setData(new ArrayList<>());
			response.setStatus(false);
			response.setMessage(e.getMessage());

		}

		return response;
	}

	@PostMapping("/all_proposer_by_stringBuilder")
	public ResponseHandler allProposerByStringBuilder(@RequestBody ProposerPagination pagination) {
		ResponseHandler response = new ResponseHandler();

		try {

			List<Proposer> pagedData = proposerService.allProposer(pagination);
			response.setData(pagedData);
			response.setStatus(true);
			response.setMessage("Success");

			if (pagination.getSearchFilters() != null) {
				pagination.setPage(0);
				pagination.setSize(0);
				response.setTotalRecord(proposerService.allProposer(pagination).size());
			} else {
				response.setTotalRecord(proposerService.listAllProposers().size());
			}

		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			response.setData(new ArrayList<>());
			response.setStatus(false);
			response.setMessage(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			response.setData(new ArrayList<>());
			response.setStatus(false);
			response.setMessage(e.getMessage());
		}

		return response;
	}

	@GetMapping("/listAllProposers")
	public ResponseHandler proposers() {
		ResponseHandler response = new ResponseHandler();
		try {
			List<RequestDto> reqDto = proposerService.listAllProposers();

			response.setData(reqDto);
			response.setStatus(true);
			response.setMessage("Success");
			response.setTotalRecord(reqDto.size());
		} catch (Exception e) {
			e.printStackTrace();
			response.setData(new ArrayList<>());
			response.setStatus(false);
			response.setMessage("Failed to fetch proposers");

		}
		return response;
	}

	@GetMapping("/viewProposer/{id}")
	public ResponseHandler getProposerWithNominees(@PathVariable Integer id) {
		ResponseHandler response = new ResponseHandler();
		List<String> errors = new ArrayList<>();
		errors.add("Proposer not found for the given ID " + id);
		try {
			ResponseDto data = proposerService.proposerFindById(id);
			int count = (data != null) ? 1 : 0;

			if (data == null || data.getFirstName() == null) {
				response.setData(new ArrayList<>());
				response.setStatus(false);
				response.setMessage("Proposer not found");
				response.setTotalRecord(0);
				response.setErrors(errors);
			} else {
				response.setData(data);
				response.setStatus(true);
				response.setMessage("success");
				response.setTotalRecord(count);
			}
		} catch (Exception e) {
			response.setData(new ArrayList<>());
			response.setStatus(false);
			response.setMessage("failed");
		}
		return response;
	}

	@DeleteMapping("/deleteProposer/{proposerId}")
	public ResponseHandler deleteProposer(@PathVariable Integer proposerId) {

		ResponseHandler response = new ResponseHandler();
		try {

			String data = proposerService.deleteProposer(proposerId);
			response.setData(data);
			response.setStatus(true);
			response.setMessage("success");

		} catch (Exception e) {
			response.setData(new ArrayList<>());
			response.setStatus(false);
			response.setMessage("failed");
		}
		return response;
	}

	@PutMapping("/updateProposer/{proposerId}")
	public ResponseHandler updateProposer(@PathVariable Integer proposerId, @RequestBody RequestDto requestDto) {

		ResponseHandler response = new ResponseHandler();
		try {
			String result = proposerService.updateProposer(proposerId, requestDto);
			response.setStatus(true);
			response.setMessage(result);
			response.setData(result);
		} catch (IllegalArgumentException e) {
			response.setStatus(false);
			response.setMessage("Validation failed");
			response.setErrors(List.of(e.getMessage().split("; ")));
			response.setData(List.of());
		}

		return response;

	}

	@GetMapping("/getByEmail/{email}")
	public ResponseHandler findByEmail(@PathVariable String email) {
		Optional<Proposer> prop = proposerService.findByEmail(email);
		Optional<Proposer> getAllCount = proposerService.findByEmail(email);
		int count = getAllCount.isPresent() ? 1 : 0;
		ResponseHandler response = new ResponseHandler();
		if (prop.isPresent()) {
			response.setData(prop);
			response.setStatus(true);
			response.setMessage("Success");
			response.setTotalRecord(count);

		} else {
			response.setData(new ArrayList<>());
			response.setStatus(false);
			response.setMessage("failed");
			response.setTotalRecord(count);

		}
		return response;
	}

	@PostMapping("/fetchProposerByJoin")
	public ResponseHandler allProposers(@RequestBody ProposerPagination pagination) {
		ResponseHandler response = new ResponseHandler();

		try {
			// Call the service to fetch the proposers based on the pagination filters
			List<Proposer> proposers = proposerService.fetchAllProposersWithNomineesByJoin(pagination);

			// Calculate the total number of records for pagination
			List<RequestDto> totalRecords = proposerService.listAllProposers();

			response.setData(proposers);
			response.setTotalRecord(totalRecords); // Setting total count of records
			response.setStatus(true);
			response.setMessage("Proposers fetched successfully");
		} catch (Exception e) {
			e.printStackTrace();
			response.setData(new ArrayList<>());
			response.setStatus(false);
			response.setMessage("Error: " + e.getMessage());
		}

		return response;
	}

	@GetMapping("/excelFileExport")
	public ResponseHandler exportProposersToExcel() {
		ResponseHandler handler = new ResponseHandler();
		try {
			String filePath = proposerService.exportProposersToExcel();
			handler.setData(filePath);
			handler.setStatus(true);
			handler.setMessage("File generated successfully");

		} catch (IOException e) {
			handler.setStatus(false);
			handler.setMessage("Failed: " + e.getMessage());

		}
		return handler;
	}

	@PostMapping(value = "/excelFileImport", consumes = "multipart/form-data")
	public ResponseHandler importDataToDB(@RequestParam("file") MultipartFile file) {
		ResponseHandler handler = new ResponseHandler();
		try {
			proposerService.importFromExcel(file);
			handler.setStatus(true);
			handler.setMessage("File imported successfully");
		} catch (RuntimeException e) {

			handler.setStatus(false);
			handler.setMessage("Validation error in Excel file");
			handler.setErrors(List.of(e.getMessage())); 

		} catch (IOException e) {
			// If there is an IOException (e.g., file issues)
			handler.setStatus(false);
			handler.setMessage("Failed to import file: " + e.getMessage());

		 }
		return handler;
	}

	@GetMapping("/getDBDataToExcel")
	public void getDBDataToExcel(HttpServletResponse response) {

		try {
			response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
			response.setHeader("Content-Disposition", "attachment; filename=Proposer_Data.xlsx");

			proposerService.getDBDataToExcel(response.getOutputStream());

		} catch (IOException e) {
			e.printStackTrace();
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}
}

//@Override
//public void importFromExcel(MultipartFile file) throws IOException {
//	List<Proposer> newProposer = new ArrayList<>();
//	List<String> responseErrors = new ArrayList<>();
//	List<ProposersError> dbErrors = new ArrayList<>();
//
//	XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());
//	XSSFSheet sheet = workbook.getSheet("Proposer_Data");
//
//	for (int i = 1; i <= sheet.getLastRowNum(); i++) {
//		Row row = sheet.getRow(i);
//
//		if (row == null)
//			continue;
//
//		Proposer proposers = new Proposer();
//
//		String title = row.getCell(1).getStringCellValue().trim();
//		if (title.isEmpty()) {
//			ProposersError newError = new ProposersError();
//
//			newError.setErrorField("title");
//			newError.setErrorMessage("Invalid or missing title at row " + (i + 1));
//			newError.setStatus("Failed");
//			dbErrors.add(newError);
//			responseErrors.add("Title is missing at row " + (i + 1));
//		} else {
//			proposers.setProposerTitle(ProposerTitle.valueOf(title));
//		}
//
//		String firstName = row.getCell(2).getStringCellValue().trim();
//		if (firstName.isEmpty()) {
//			ProposersError newError = new ProposersError();
//
//			newError.setErrorField("firstName");
//			newError.setErrorMessage("Invalid or missing firstName at row " + (i + 1));
//			newError.setStatus("Failed");
//			dbErrors.add(newError);
//			responseErrors.add("First name is missing at row " + (i + 1));
//		} else {
//			proposers.setFirstName(firstName);
//		}
//
//		String middleName = row.getCell(3).getStringCellValue().trim();
//		if (middleName.isEmpty()) {
//			ProposersError newError = new ProposersError();
//
//			newError.setErrorField("middleName");
//			newError.setErrorMessage("Invalid or missing middleName at row " + (i + 1));
//			newError.setStatus("Failed");
//			dbErrors.add(newError);
//			responseErrors.add("Middle name is missing at row " + (i + 1));
//		} else {
//			proposers.setMiddleName(middleName);
//		}
//
//		String lastName = row.getCell(4).getStringCellValue().trim();
//		if (lastName.isEmpty()) {
//			ProposersError newError = new ProposersError();
//
//			newError.setErrorField("lastName");
//			newError.setErrorMessage("Invalid or missing lastName at row " + (i + 1));
//			newError.setStatus("Failed");
//			dbErrors.add(newError);
//			responseErrors.add("Last name is missing at row " + (i + 1));
//		} else {
//			proposers.setLastName(lastName);
//		}
//
//		String gender = row.getCell(5).getStringCellValue().trim();
//		if (gender.isEmpty()) {
//			ProposersError newError = new ProposersError();
//
//			newError.setErrorField("gender");
//			newError.setErrorMessage("Invalid or missing gender at row " + (i + 1));
//			newError.setStatus("Failed");
//			dbErrors.add(newError);
//			responseErrors.add("Gender is missing at row " + (i + 1));
//		} else {
//			proposers.setGender(Gender.valueOf(gender));
//		}
//
//		if (row.getCell(6) == null || row.getCell(6).getDateCellValue() == null) {
//			ProposersError newError = new ProposersError();
//
//			newError.setErrorField("DATE");
//			newError.setErrorMessage("Invalid or missing date at row " + (i + 1));
//			newError.setStatus("Failed");
//			dbErrors.add(newError);
//			responseErrors.add("Date of birth is missing at row " + (i + 1));
//		} else {
//			proposers.setDateOfBirth(row.getCell(6).getDateCellValue());
//		}
//
//		String pan = row.getCell(7).getStringCellValue().trim();
//		if (pan.isEmpty()) {
//			ProposersError newError = new ProposersError();
//
//			newError.setErrorField("pan");
//			newError.setErrorMessage("Invalid or missing pan at row " + (i + 1));
//			newError.setStatus("Failed");
//			dbErrors.add(newError);
//			responseErrors.add("PAN number is missing at row " + (i + 1));
//		} else {
//			proposers.setPanNumber(pan);
//		}
//
//		long aadhar = (long) row.getCell(8).getNumericCellValue();
//		if (String.valueOf(aadhar).length() != 12) {
//			ProposersError newError = new ProposersError();
//
//			newError.setErrorField("aadhar");
//			newError.setErrorMessage("Invalid or missing aadhar at row " + (i + 1));
//			newError.setStatus("Failed");
//			dbErrors.add(newError);
//			responseErrors.add("Invalid Aadhar number at row " + (i + 1));
//		} else {
//			proposers.setAadharNo(aadhar);
//		}
//
//		String status = row.getCell(9).getStringCellValue().trim();
//		if (status.isEmpty()) {
//			ProposersError newError = new ProposersError();
//
//			newError.setErrorField("status");
//			newError.setErrorMessage("Invalid or missing status at row " + (i + 1));
//			newError.setStatus("Failed");
//			dbErrors.add(newError);
//			responseErrors.add("Status is missing at row " + (i + 1));
//		} else {
//			proposers.setStatus(status);
//		}
//
//		String email = row.getCell(10).getStringCellValue().trim();
//		if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
//			ProposersError newError = new ProposersError();
//
//			newError.setErrorField("email");
//			newError.setErrorMessage("Invalid or missing email at row " + (i + 1));
//			newError.setStatus("Failed");
//			dbErrors.add(newError);
//			responseErrors.add("Invalid email at row " + (i + 1));
//		} else {
//			proposers.setEmail(email);
//		}
//
//		long mobileNo = (long) row.getCell(11).getNumericCellValue();
//		if (String.valueOf(mobileNo).length() != 10) {
//			ProposersError newError = new ProposersError();
//
//			newError.setErrorField("mobileNo");
//			newError.setErrorMessage("Invalid or missing mobileNo at row " + (i + 1));
//			newError.setStatus("Failed");
//			dbErrors.add(newError);
//			responseErrors.add("Invalid mobile number at row " + (i + 1));
//		} else {
//			proposers.setMobileNo(mobileNo);
//		}
//
//		long alternateMobNo = (long) row.getCell(12).getNumericCellValue();
//		if (String.valueOf(alternateMobNo).length() != 10) {
//			ProposersError newError = new ProposersError();
//
//			newError.setErrorField("alternateMobNo");
//			newError.setErrorMessage("Invalid or missing alternateMobNo at row " + (i + 1));
//			newError.setStatus("Failed");
//			dbErrors.add(newError);
//			responseErrors.add("Invalid alternate number at row " + (i + 1));
//		} else {
//			proposers.setAlternateMobNo(alternateMobNo);
//		}
//
//		String address1 = row.getCell(13).getStringCellValue().trim();
//		if (address1.isEmpty()) {
//			ProposersError newError = new ProposersError();
//
//			newError.setErrorField("address1");
//			newError.setErrorMessage("Invalid or missing address1 at row " + (i + 1));
//			newError.setStatus("Failed");
//			dbErrors.add(newError);
//			responseErrors.add("Address Line 1 is missing at row " + (i + 1));
//		} else {
//			proposers.setAddressLine1(address1);
//		}
//
//		String address2 = row.getCell(14).getStringCellValue().trim();
//		if (address2.isEmpty()) {
//			ProposersError newError = new ProposersError();
//
//			newError.setErrorField("address2");
//			newError.setErrorMessage("Invalid or missing address2 at row " + (i + 1));
//			newError.setStatus("Failed");
//			dbErrors.add(newError);
//			responseErrors.add("Address Line 2 is missing at row " + (i + 1));
//		} else {
//			proposers.setAddressLine2(address2);
//		}
//
//		String address3 = row.getCell(15).getStringCellValue().trim();
//		if (address3.isEmpty()) {
//			ProposersError newError = new ProposersError();
//
//			newError.setErrorField("address3");
//			newError.setErrorMessage("Invalid or missing address3 at row " + (i + 1));
//			newError.setStatus("Failed");
//			dbErrors.add(newError);
//			responseErrors.add("Address Line 3 is missing at row " + (i + 1));
//		} else {
//			proposers.setAddressLine3(address3);
//		}
//
//		long pincode = (long) row.getCell(16).getNumericCellValue();
//		if (String.valueOf(pincode).length() != 6) {
//			ProposersError newError = new ProposersError();
//
//			newError.setErrorField("pincode");
//			newError.setErrorMessage("Invalid or missing pincode at row " + (i + 1));
//			newError.setStatus("Failed");
//			dbErrors.add(newError);
//			responseErrors.add("Invalid pincode at row " + (i + 1));
//		} else {
//			proposers.setPincode(pincode);
//		}
//
//		String city = row.getCell(17).getStringCellValue().trim();
//		if (city.isEmpty()) {
//			ProposersError newError = new ProposersError();
//
//			newError.setErrorField("city");
//			newError.setErrorMessage("Invalid or missing city at row " + (i + 1));
//			newError.setStatus("Failed");
//			dbErrors.add(newError);
//			responseErrors.add("City is missing at row " + (i + 1));
//		} else {
//			proposers.setCity(city);
//		}
//
//		String state = row.getCell(18).getStringCellValue().trim();
//		if (state.isEmpty()) {
//			ProposersError newError = new ProposersError();
//
//			newError.setErrorField("state");
//			newError.setErrorMessage("Invalid or missing state at row " + (i + 1));
//			newError.setStatus("Failed");
//			dbErrors.add(newError);
//			responseErrors.add("State is missing at row " + (i + 1));
//		} else {
//			proposers.setState(state);
//		}
//
//		if (responseErrors.isEmpty()) {
//			newProposer.add(proposers);
//		}
//	}
//	workbook.close();
//
//	if (!responseErrors.isEmpty()) {
//		errorRepo.saveAll(dbErrors);
//		//throw new RuntimeException("Validation failed: " + String.join(", ", responseErrors));
//	}
//	  
//	proposerRepo.saveAll(newProposer);
//}

