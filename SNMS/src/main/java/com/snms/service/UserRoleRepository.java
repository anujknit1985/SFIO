package com.snms.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Repository;

import com.snms.entity.AppUser;
import com.snms.entity.UserRole;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Long>{



	

	List<UserRole> findByAppRole(int i);

	

	

}
