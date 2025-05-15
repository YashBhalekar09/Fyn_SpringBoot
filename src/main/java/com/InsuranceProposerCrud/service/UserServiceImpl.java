package com.InsuranceProposerCrud.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.InsuranceProposerCrud.dto.AddressDto;
import com.InsuranceProposerCrud.dto.UserDto;
import com.InsuranceProposerCrud.dto.UserUpdateDto;
import com.InsuranceProposerCrud.entity.Address;
import com.InsuranceProposerCrud.entity.ProposerSearchRequest;
import com.InsuranceProposerCrud.entity.ProposerSearchFilter;
import com.InsuranceProposerCrud.entity.User;
import com.InsuranceProposerCrud.entity.UserPagination;
import com.InsuranceProposerCrud.entity.UserSerchFilter;
import com.InsuranceProposerCrud.repository.AddressRepository;
import com.InsuranceProposerCrud.repository.UserRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private AddressRepository addressRepo;

	@Autowired
	private EntityManager entityManager;

	@Override
	public String addUser(UserDto userDto) {

		User user = new User();
		user.setFirstName(userDto.getFirstName());
		user.setLastName(userDto.getLastName());
		user.setStatus("y");

		List<Address> addresses = new ArrayList<>();

		List<AddressDto> addresses2 = userDto.getAddresses();

		for (AddressDto addressDto : addresses2) {
			Address address = new Address();
			address.setStreet(addressDto.getStreet());
			address.setCity(addressDto.getCity());
			address.setState(addressDto.getState());
			address.setStatus("y");
			// Set the back-reference from Address to User
			address.setUsers(user);

			addresses.add(address);
		}

		user.setAddresses(addresses);

		userRepo.save(user);

		return "User Saved Successfully!!";
	}

//	@Override
//	public String updateUser(Integer userId, UserDto userDto) {
//
//		Optional<User> optionalUser = userRepo.findById(userId);
//
//		if (optionalUser.isPresent()) {
//			User existingUser = optionalUser.get();
//
//			existingUser.setFirstName(userDto.getFirstName());
//			existingUser.setLastName(userDto.getLastName());
//			
//			
//		    Map<Integer, Address> existingMap = new HashMap<>();
//		    for (Address addr : existingUser.getAddresses()) {
//		        existingMap.put(addr.getAddressId(), addr);
//		    }
//
//			List<AddressDto> updatedAddressDtos = userDto.getAddresses();
//
//			List<Address> existingAddresses = existingUser.getAddresses();
//
////	        for(int i=0;i<updatedAddressDtos.size();i++) {
////	        	AddressDto dto =updatedAddressDtos.get(i);
////	        	
////	        	if(i<existingAddresses.size()) {
////	        		Address existingadd =existingAddresses.get(i);
////	        		
////	        		existingadd.setCity(dto.getCity());
////	        		existingadd.setState(dto.getState());
////	        		existingadd.setStreet(dto.getStreet());
////	        	}
////	        	else {
////	        		Address add=new Address();
////	        		
////	        		add.setCity(dto.getCity()); 
////	        		add.setCity(dto.getCity());
////	        		add.setState(dto.getState());
////	        		add.setUsers(existingUser);
////	        		existingAddresses.add(add);
////	        	}
////	        }
//
//			for (int i = 0; i < updatedAddressDtos.size(); i++) {
//
//				AddressDto dto = updatedAddressDtos.get(i);
//
//				if (i < existingAddresses.size()) {
//					// Update existing address
//					Address existingAddress = existingAddresses.get(i);
//					existingAddress.setStreet(dto.getStreet());
//					existingAddress.setCity(dto.getCity());
//					existingAddress.setState(dto.getState());
//				} else {
//					// Handle case where new address is added
//					Address newAddress = new Address();
//					newAddress.setStreet(dto.getStreet());
//					newAddress.setCity(dto.getCity());
//					newAddress.setState(dto.getState());
//					newAddress.setUsers(existingUser);
//					existingAddresses.add(newAddress);
//				}
//			}
//
//			userRepo.save(existingUser);
//			
//			return "User and address updated successfully";
//			
//		} else {
//			return "User not found";
//		}
//	}

	@Override
	public String updateUser(Integer userId, UserUpdateDto userUpdateDto) {
		Optional<User> optionalUser = userRepo.findById(userId);

		if (optionalUser.isPresent()) {
			User existingUser = optionalUser.get();

			if (!optionalUser.isPresent()) {
				throw new NoSuchElementException("User not found with ID: " + userId);
			}

			existingUser.setFirstName(userUpdateDto.getFirstName());
			existingUser.setLastName(userUpdateDto.getLastName());

			userRepo.save(existingUser);
			return "User updated successfully!";
		} else {
			return "User not found";
		}
	}

	@Override
	public String updateAddress(Integer addressId, AddressDto addressDto) {
		Optional<Address> addId = addressRepo.findById(addressId);
		if (addId.isPresent()) {
			Address add = addId.get();
			if (!addId.isPresent()) {
				throw new NoSuchElementException("Address not found with ID: " + addressId);
			}

			add.setCity(addressDto.getCity());
			add.setState(addressDto.getState());
			add.setStreet(addressDto.getStreet());

			addressRepo.save(add);
			return "Address Update Successfully!";
		} else {
			return "AddressId is invalid";
		}
	}

	@Override
	public String deleteUser(Integer userId) {
		Optional<User> opt = userRepo.findById(userId);

		if (opt.isPresent()) {
			User user = opt.get();

			if ("y".equals(user.getStatus())) {
				user.setStatus("n");

				List<Address> addresses = user.getAddresses();
				if ("n".equals(user.getStatus())) {
					for (Address address : addresses) {
						address.setStatus("n");
					}
				}
				userRepo.save(user);
				return "User deleted!";
			} else {
				return "User already inactive!";
			}

		} else {
			return "User not found!";
		}

	}

	@Override
	public List<User> getAllUsers(UserPagination pagination) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<User> cq = cb.createQuery(User.class);
		Root<User> root = cq.from(User.class);

		List<Predicate> predicates = new ArrayList<>();

		predicates.add(cb.equal(root.get("status"), "y"));

		UserSerchFilter userSearchFilter = pagination.getSearchFilters();

		String firstName = userSearchFilter.getFirstName();
		String lastName = userSearchFilter.getLastName();

		if (userSearchFilter != null) {

			if (firstName != null && !firstName.isEmpty()) {
				predicates.add(cb.like(cb.lower(root.get("firstName")), "%" + firstName.toLowerCase() + "%"));
			}

			if (lastName != null && !lastName.isEmpty()) {
				predicates.add(cb.like(cb.lower(root.get("lastName")), "%" + lastName.toLowerCase() + "%"));
			}

		}

		cq.where(cb.and(predicates.toArray(new Predicate[0])));

		String sortBy = pagination.getSortBy();

		if (sortBy == null || sortBy.isEmpty()) {
			sortBy = "userId";
		}

		String sortOrder = pagination.getSortOrder();
		if (sortOrder == null || sortOrder.isEmpty()) {
			sortOrder = "desc";
		}

		if (sortOrder.equalsIgnoreCase("desc")) {
			cq.orderBy(cb.desc(root.get(sortBy)));
		} else {
			cq.orderBy(cb.asc(root.get(sortBy)));
		}

		int page = pagination.getPage();
		int size = pagination.getSize();

		TypedQuery<User> query = entityManager.createQuery(cq);

		int startIndex = ((page - 1) * size);
		int endIndex = startIndex + size;

		if (page >= 0 && size > 0) {

			query.setFirstResult(startIndex);
			query.setMaxResults(size);

		} else if (page == 0 && size > 0 || page > 0 && size == 0) {

			throw new IllegalArgumentException("page can't be zero");
		}
		return query.getResultList();

	}

	@Override
	public String deleteParticularAddress(Integer addressId) {
		Optional<Address> opt = addressRepo.findById(addressId);
		if (opt.isPresent()) {
			Address address = opt.get();
			address.setStatus("n");
			addressRepo.save(address); 
			return "Address deleted.";
		} else {
			return "Address not found.";
		}
	}

//	@Override
//	public List<User> allUsers() {
//		List<User> allUsers = userRepo.findAll();
//		return allUsers;
//	}

}
