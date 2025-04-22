package com.InsuranceProposerCrud.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import com.InsuranceProposerCrud.entity.Proposer;
import com.InsuranceProposerCrud.entity.ProposerPagination;
import com.InsuranceProposerCrud.request.RequestDto;
import com.InsuranceProposerCrud.request.ResponseDto;

public interface ProposerService {

	
	//public List<RequestDto> allProposer();
	
	//List<RequestDto> allProposer(int page, int size);

	List<Proposer> allProposer(ProposerPagination pagination);
	
	public ResponseDto proposerFindById(Integer proposerId);
	
	public String deleteProposer(Integer proposerId);
	
	public String updateProposer(Integer proposerId,RequestDto requestDto);
	
	public Optional<Proposer> findByEmail(String email);
	
	public String saveProposer(RequestDto requestDto);

	Optional<Proposer> proposerUpdateByIdAndStatus(Integer proposerId, String status);




}
