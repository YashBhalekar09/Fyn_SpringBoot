package com.InsuranceProposerCrud.service;

import java.util.List;
import java.util.Optional;
import com.InsuranceProposerCrud.entity.Proposer;
import com.InsuranceProposerCrud.request.RequestDto;

public interface ProposerService {

	
	public List<RequestDto> allProposer();
	
	public RequestDto proposerFindById(Integer proposerId);
	
	public String deleteProposer(Integer proposerId);
	
	public String updateProposer(Integer proposerId,RequestDto requestDto);
	
	public Optional<Proposer> findByEmail(String email);
	
	public String saveProposer(RequestDto requestDto);

	Optional<Proposer> proposerUpdateByIdAndStatus(Integer proposerId, String status);
}
