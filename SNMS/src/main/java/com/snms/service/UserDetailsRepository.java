package com.snms.service;

import java.util.List;

import org.springframework.data.domain.Sort.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Repository;

import com.snms.entity.AddDesignation;
import com.snms.entity.AppRole;
import com.snms.entity.AppUser;
import com.snms.entity.UserDetails;
@Repository
public interface UserDetailsRepository extends JpaRepository<UserDetails, Long>{
	
	public UserDetails findByDesignation(AddDesignation designation);

	public UserDetails findAllByEmail(String username);

	public UserDetails findAllByUserId(AppUser userDetails);

	


	
	

}
