package com.service;


import java.util.List;

import com.dto.AddressDto;
import com.dto.UserDto;
import com.dto.UserUpdateDto;
import com.entity.User;
import com.entity.UserPagination;

public interface UserService {

	public String addUser(UserDto userDto);
	
	public String updateAddress(Integer addressId, AddressDto addressDto);

	public String updateUser(Integer userId, UserUpdateDto userUpdateDto);
	
	public String deleteUser(Integer userId);
	
	public String deleteParticularAddress(Integer addressId);
	
	//public List<User> allUsers();

	public List<User> getAllUsers(UserPagination pagination);
}
