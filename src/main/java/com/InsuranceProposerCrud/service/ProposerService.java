package com.InsuranceProposerCrud.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.server.ServletServerHttpResponse;

import com.InsuranceProposerCrud.entity.Proposer;
import com.InsuranceProposerCrud.entity.ProposerPagination;
import com.InsuranceProposerCrud.request.RequestDto;
import com.InsuranceProposerCrud.request.ResponseDto;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletResponse;

public interface ProposerService {

	
	//public List<RequestDto> allProposer();
	
	//List<RequestDto> allProposer(int page, int size);

	List<Proposer> allProposer(ProposerPagination pagination);
	
	List<Proposer> fetchAllProposerByStringBuilder(ProposerPagination pagination);
	
	public ResponseDto proposerFindById(Integer proposerId);
	
	public String deleteProposer(Integer proposerId);
	
	public String updateProposer(Integer proposerId,RequestDto requestDto);
	
	public Optional<Proposer> findByEmail(String email);
	
	public String saveProposer(RequestDto requestDto);

	Optional<Proposer> proposerUpdateByIdAndStatus(Integer proposerId, String status);

	public List<RequestDto> listAllProposers();
	
	public List<Proposer> fetchAllProposersWithNomineesByJoin(ProposerPagination pagination);

	public void exportProposersToExcel(HttpServletResponse response) throws ServletException, IOException;


}
