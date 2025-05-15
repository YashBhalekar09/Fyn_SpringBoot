package com.InsuranceProposerCrud.entity;

public class UserPagination {
	
	private Integer page;
	private Integer size;
	private String sortBy;
	private String sortOrder;
	
	private UserSerchFilter searchFilters;

	public Integer getPage() {
		return page;
	}

	public void setPage(Integer page) {
		this.page = page;
	}

	public Integer getSize() {
		return size;
	}

	public void setSize(Integer size) {
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

	public UserSerchFilter getSearchFilters() {
		return searchFilters;
	}

	public void setSearchFilters(UserSerchFilter searchFilters) {
		this.searchFilters = searchFilters;
	}

}
