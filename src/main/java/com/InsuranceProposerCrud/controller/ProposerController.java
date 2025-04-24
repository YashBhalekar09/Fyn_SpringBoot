package com.InsuranceProposerCrud.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.InsuranceProposerCrud.entity.Proposer;
import com.InsuranceProposerCrud.entity.ProposerPagination;
import com.InsuranceProposerCrud.entity.ProposerSearchFilter;
import com.InsuranceProposerCrud.request.RequestDto;
import com.InsuranceProposerCrud.request.ResponseDto;
import com.InsuranceProposerCrud.response.ResponseHandler;
import com.InsuranceProposerCrud.service.ProposerService;
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

	@PostMapping("/allProposer")
	public ResponseHandler allProposer(@RequestBody ProposerPagination pagination) {
		ResponseHandler response = new ResponseHandler();

		List<RequestDto> getAllCount = proposerService.listAllProposers();
		int count = getAllCount.size();

		try {
			List<Proposer> allProposer = proposerService.allProposer(pagination);

			response.setData(allProposer);
			response.setStatus(true);
			response.setMessage("Success");

			if (pagination.getSearchFilters() != null) {
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
		try {
			ResponseDto data = proposerService.proposerFindById(id);

			if (data == null || data.getFirstName() == null) {
				response.setData(new ArrayList<>());
				response.setStatus(false);
				response.setMessage("Proposer not found");
			} else {
				response.setData(data);
				response.setStatus(true);
				response.setMessage("success");
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
	public ResponseEntity<Optional<Proposer>> findByEmail(@PathVariable String email) {
		Optional<Proposer> prop = proposerService.findByEmail(email);
		if (prop.isPresent()) {
			return new ResponseEntity<Optional<Proposer>>(prop, HttpStatus.OK);
		}
		return new ResponseEntity<>(prop, HttpStatus.NOT_FOUND);
	}

}
