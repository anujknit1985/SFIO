package com.snms.service;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.snms.entity.AppUser;
import com.snms.entity.CaseDetails;
import com.snms.entity.Inspector;

@Repository
public interface InspectorRepository extends JpaRepository<Inspector, Long>{

	Inspector findAllByCaseDetailsAndAppUser(CaseDetails caseDetails, AppUser userDetails);

	List<Inspector> findAllByAppUser(AppUser appUser);

	List<Inspector> findAllByAppUserAndIsAdoAndIsActive(AppUser userDetails, boolean b, boolean c);

	List<Inspector> findAllByAppUserAndIsActive(AppUser userDetails, boolean b);

	List<Inspector> findAllByAppUserAndIsActiveAndIsIO(AppUser userDetails, boolean b, boolean c);

	List<Inspector> findAllByAppUserAndIsActiveAndIsAdo(AppUser userDetails, boolean b, boolean c);

	List<Inspector> findAllByAppUserAndIsAdo(AppUser userDetails, boolean c);

	Inspector findByCaseDetailsAndIsActiveAndIsAdo(CaseDetails caseDetails1, boolean b, boolean c);


	
	
}
