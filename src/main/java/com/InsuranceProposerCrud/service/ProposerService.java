package com.InsuranceProposerCrud.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.multipart.MultipartFile;

import com.InsuranceProposerCrud.dto.RequestDto;
import com.InsuranceProposerCrud.dto.ResponseDto;
import com.InsuranceProposerCrud.entity.Proposer;
import com.InsuranceProposerCrud.entity.ProposerSearchRequest;

import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;

public interface ProposerService {

	//public List<RequestDto> allProposer();
	
	//List<RequestDto> allProposer(int page, int size);

	List<Proposer> allProposer(ProposerSearchRequest pagination);
	
	List<Proposer> fetchAllProposerByStringBuilder(ProposerSearchRequest pagination);
	
	public ResponseDto proposerFindById(Integer proposerId);
	
	public String deleteProposer(Integer proposerId);
	
	public String updateProposer(Integer proposerId,RequestDto requestDto);
	
	public Optional<Proposer> findByEmail(String email);
	
	public String saveProposer(RequestDto requestDto);

	Optional<Proposer> proposerUpdateByIdAndStatus(Integer proposerId, String status);

	public List<RequestDto> listAllProposers();
	
	public List<Proposer> fetchAllProposersWithNomineesByJoin(ProposerSearchRequest pagination);

	public String  exportProposersToExcel() throws FileNotFoundException, IOException;
	
	public String importFromExcel(MultipartFile file) throws IOException;
	
	public String batchProcessing(MultipartFile file) throws IOException;
	
	void getDBDataToExcel(OutputStream out) throws IOException;

	public void processExcelBatch() throws IOException ;

}
