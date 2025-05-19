package com.controller;

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

import com.dto.RequestDto;
import com.dto.ResponseDto;
import com.entity.Proposer;
import com.entity.ProposerSearchRequest;
import com.response.ResponseHandler;
import com.service.ProposerService;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/proposer")
public class ProposerController {

	@Autowired
	private ProposerService proposerService;

	@PostMapping("/add")
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

	//all_proposer_by_criteriaBuilder
	@PostMapping("/listing")
	public ResponseHandler allProposerByCriteriaBuilder(@RequestBody ProposerSearchRequest pagination) {
		ResponseHandler response = new ResponseHandler();

		List<RequestDto> getAllCount = proposerService.listAllProposers();
		int count = getAllCount.size();

		try {
			List<Proposer> allProposer = proposerService.allProposer(pagination);

			response.setData(allProposer);
			response.setStatus(true);
			response.setMessage("Success");
			response.setTotalRecord(count);


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

	//all_proposer_by_stringBuilder
	@PostMapping("/listings")
	public ResponseHandler allProposerByStringBuilder(@RequestBody ProposerSearchRequest pagination) {
		ResponseHandler response = new ResponseHandler();

		try {
			List<Proposer> pagedData = proposerService.allProposer(pagination);
			response.setData(pagedData);
			response.setStatus(true);
			response.setMessage("Success");
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

	@GetMapping("/list")
	public ResponseHandler proposers() {
		ResponseHandler response = new ResponseHandler();
		try {
			List<RequestDto> reqDto = proposerService.listAllProposers();
			if(reqDto.isEmpty()) {
		        throw new RuntimeException("No proposers found");
		    }
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

	@GetMapping("/list/{id}")
	public ResponseHandler getProposerWithNominees(@PathVariable Integer id) {
		ResponseHandler response = new ResponseHandler();
		
		try {
			ResponseDto data = proposerService.proposerFindById(id);
			int count = (data != null) ? 1 : 0;

			if (data == null || data.getFirstName() == null) {
				response.setData(new ArrayList<>());
				response.setStatus(false);
				response.setMessage("Proposer not found");
				response.setTotalRecord(0);
				//response.setErrors(errors);
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

	@DeleteMapping("/delete/{proposerId}")
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

	@PutMapping("/update/{proposerId}")
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

	@GetMapping("/list_email/{email}")
	public ResponseHandler findByEmail(@PathVariable String email) {
		Optional<Proposer> prop = proposerService.findByEmail(email);
		
		
		ResponseHandler response = new ResponseHandler();
		if (prop.isPresent()) {
			response.setData(prop);
			response.setStatus(true);
			response.setMessage("Success");
			

		} else {
			response.setData(new ArrayList<>());
			response.setStatus(false);
			response.setMessage("failed");
			

		}
		return response;
	}

	@PostMapping("/list_join")
	public ResponseHandler allProposers(@RequestBody ProposerSearchRequest pagination) {
		ResponseHandler response = new ResponseHandler();

		try {
			List<Proposer> proposers = proposerService.fetchAllProposersWithNomineesByJoin(pagination);
			
			List<RequestDto> totalRecords = proposerService.listAllProposers();

			response.setData(proposers);
			response.setTotalRecord(totalRecords); 
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

	@GetMapping("/file_export")
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

	@PostMapping(value = "/file_import", consumes = "multipart/form-data")
	public ResponseHandler importDataToDB(@RequestParam("file") MultipartFile file) {
		ResponseHandler handler = new ResponseHandler();
		try {
			String batchProcessing = proposerService.batchProcessing(file);
			handler.setData(batchProcessing);
			handler.setStatus(true);
			handler.setMessage("Success");
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

	@GetMapping("/list_excel")
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



