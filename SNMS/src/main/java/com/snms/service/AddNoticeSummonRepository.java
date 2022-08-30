package com.snms.service;

import javax.validation.Valid;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.snms.entity.SummonDetails;

@Repository
public interface AddNoticeSummonRepository extends JpaRepository<SummonDetails, Long> {

	@Valid
	SummonDetails findAllByCaseId(Long caseId);
	
	
}
