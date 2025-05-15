package com.InsuranceProposerCrud.service;


import java.util.List;

import com.InsuranceProposerCrud.dto.AddressDto;
import com.InsuranceProposerCrud.dto.UserDto;
import com.InsuranceProposerCrud.dto.UserUpdateDto;
import com.InsuranceProposerCrud.entity.User;
import com.InsuranceProposerCrud.entity.UserPagination;

public interface UserService {

	public String addUser(UserDto userDto);
	
	public String updateAddress(Integer addressId, AddressDto addressDto);

	public String updateUser(Integer userId, UserUpdateDto userUpdateDto);
	
	public String deleteUser(Integer userId);
	
	public String deleteParticularAddress(Integer addressId);
	
	//public List<User> allUsers();

	public List<User> getAllUsers(UserPagination pagination);
}
