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
import org.springframework.web.bind.annotation.RestController;

import com.InsuranceProposerCrud.entity.Proposer;
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
	        response.setMessage("Something went wrong: " + e.getMessage());
	    }

	    return response;
	}
 

	@GetMapping("/allProposer")
	public ResponseHandler allProposer() {
		ResponseHandler response = new ResponseHandler();
		try {
			List<RequestDto> reqDto = proposerService.allProposer();
			response.setData(reqDto);
			response.setStatus(true);
			response.setMessage("success");
		} catch (Exception e) {
			response.setData(new ArrayList<>());
			response.setStatus(false);
			response.setMessage("failed");
		}
		return response;
	}

	@GetMapping("/allProposer/{id}")
	public ResponseEntity<ResponseDto> getProposerWithNominees(@PathVariable Integer id) {
	    ResponseDto proposerFindById = proposerService.proposerFindById(id);
	    return ResponseEntity.ok(proposerFindById);
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
