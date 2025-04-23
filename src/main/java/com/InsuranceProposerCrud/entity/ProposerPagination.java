package com.InsuranceProposerCrud.entity;

import java.util.List;

public class ProposerPagination {

	private Integer page;
	private Integer size;
	private String sortBy;
	private String sortOrder;
	
	private List<ProposerSearchFilter> searchFilters;

	public List<ProposerSearchFilter> getSearchFilters() {
		return searchFilters;
	}

	public void setSearchFilters(List<ProposerSearchFilter> searchFilters) {
		this.searchFilters = searchFilters;
	}

	public void setPage(Integer page) {
		this.page = page;
	}

	public void setSize(Integer size) {
		this.size = size;
	}

	public ProposerPagination(Integer page, Integer size, String sortBy, String sortOrder) {
		super();
		this.page = page;
		this.size = size;
		this.sortBy = sortBy;
		this.sortOrder = sortOrder;
	}
	
	public ProposerPagination(Integer page, Integer size, String sortBy, String sortOrder,
			List<ProposerSearchFilter> filters) {
		super();
		this.page = page;
		this.size = size;
		this.sortBy = sortBy;
		this.sortOrder = sortOrder;
		
	}

	public ProposerPagination() {
		super();
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public String getSortBy() {
		return sortBy;
	}

	public void setSortBy(String sortBy) {
		this.sortBy = sortBy;
	}

	public String getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(String sortOrder) {
		this.sortOrder = sortOrder;
	}
}
