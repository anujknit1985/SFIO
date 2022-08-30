package com.snms.service;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.snms.entity.CaseDetails;
import com.snms.entity.Company;

@Repository
public interface AddCompanyRepository extends JpaRepository<Company, Long> {

	List<Company> findByCaseDetailsAndIsActive(CaseDetails caseDtl, boolean b);

}
