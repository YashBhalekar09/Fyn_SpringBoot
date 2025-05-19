package com.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dto.AddressDto;
import com.dto.UserDto;
import com.dto.UserUpdateDto;
import com.entity.User;
import com.entity.UserPagination;
import com.response.ResponseHandler;
import com.service.UserService;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserService userService;
	

	@PostMapping("/add")
	public ResponseHandler addUser(@RequestBody UserDto userdto) {
		ResponseHandler response = new ResponseHandler();

		try {
			String user = userService.addUser(userdto);
			response.setData(user);
			response.setStatus(true);
			response.setMessage("success");
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			response.setData(new ArrayList<>());
			response.setStatus(false);
			response.setMessage("failed: " + e.getMessage());

		} catch (Exception e) {
			e.printStackTrace();
			response.setData(new ArrayList<>());
			response.setStatus(false);
			response.setMessage(e.getMessage());
		}
		return response;
	}

	@PutMapping("/update_user/{userId}")
	public ResponseHandler updateUser(@PathVariable Integer userId, @RequestBody UserUpdateDto userUpdateDto) {
		ResponseHandler response = new ResponseHandler();

		try {
			String updateUser = userService.updateUser(userId, userUpdateDto);
			response.setData(updateUser);
			response.setStatus(true);
			response.setMessage("success");
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			response.setData(new ArrayList<>());
			response.setStatus(false);
			response.setMessage("failed: " + e.getMessage());

		} catch (Exception e) {
			e.printStackTrace();
			response.setData(new ArrayList<>());
			response.setStatus(false);
			response.setMessage(e.getMessage());
		}
		return response;
	}

	@PutMapping("/update_address/{addressId}")
	public ResponseHandler updateAddress(@PathVariable Integer addressId, @RequestBody AddressDto addressDto) {
		ResponseHandler response = new ResponseHandler();

		try {
			String updateAddress = userService.updateAddress(addressId, addressDto);
			response.setData(updateAddress);
			response.setStatus(true);
			response.setMessage("success");

		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			response.setData(new ArrayList<>());
			response.setStatus(false);
			response.setMessage("failed: " + e.getMessage());

		} catch (Exception e) {
			e.printStackTrace();
			response.setData(new ArrayList<>());
			response.setStatus(false);
			response.setMessage(e.getMessage());
		}
		return response;
	}

	@PatchMapping("/delete/{userId}")
	public ResponseHandler deleteUser(@PathVariable Integer userId) {
		ResponseHandler response = new ResponseHandler();

		try {
			String deleteUser = userService.deleteUser(userId);
			response.setData(deleteUser);
			response.setStatus(true);
			response.setMessage("success");
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			response.setData(new ArrayList<>());
			response.setStatus(false);
			response.setMessage("failed: " + e.getMessage());

		} catch (Exception e) {
			e.printStackTrace();
			response.setData(new ArrayList<>());
			response.setStatus(false);
			response.setMessage(e.getMessage());
		}
		return response;

	}
	
	@PatchMapping("/delete_address/{addressId}")
	public ResponseHandler deleteParticularAddress(@PathVariable Integer addressId) {
		ResponseHandler response = new ResponseHandler();

		try {
			String deleteParticularAddress = userService.deleteParticularAddress(addressId);
			response.setData(deleteParticularAddress);
			response.setStatus(true);
			response.setMessage("success");
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			response.setData(new ArrayList<>());
			response.setStatus(false);
			response.setMessage("failed: " + e.getMessage());

		} catch (Exception e) {
			e.printStackTrace();
			response.setData(new ArrayList<>());
			response.setStatus(false);
			response.setMessage(e.getMessage());
		}
		return response;

	}

	@PostMapping("/listing")
	public ResponseHandler allUsers(@RequestBody UserPagination pagination) {
		ResponseHandler response = new ResponseHandler();
		try {
			List<User> allUsers = userService.getAllUsers(pagination);
			response.setData(allUsers);
			response.setStatus(true);
			response.setMessage("success");
			response.setTotalRecord(allUsers.size());
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			response.setData(new ArrayList<>());
			response.setStatus(false);
			response.setMessage("failed: " + e.getMessage());

		} catch (Exception e) {
			e.printStackTrace();
			response.setData(new ArrayList<>());
			response.setStatus(false);
			response.setMessage(e.getMessage());
		}
		return response;
	}

}
