package com.snms.service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.snms.entity.AppRole;

@Repository
public interface AppRoleRepository extends JpaRepository<AppRole, Long>{

	
} 
