package com.snms.service;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.snms.entity.CaseDetails;
import com.snms.entity.CompanySummon;
import com.snms.entity.PersonDetails;
import com.snms.entity.RelationpersonCompany;

@Repository
public interface RelationpersonCompanyRepository extends JpaRepository<RelationpersonCompany, Integer> {

	RelationpersonCompany findAllById(int id);

	List<RelationpersonCompany> findAllByPersonDetailsAndIsApprovedAndIsApprovedstage2(PersonDetails pd, boolean b,
			boolean c);

	List<RelationpersonCompany> findAllByPersonDetailsAndIsApprovedAndIsApprovedstage2(PersonDetails pd, boolean b,
			boolean c, Sort by);

	
	
	

	RelationpersonCompany findAllByPersonDetailsAndCompanySummonAndCaseDetails(PersonDetails persondetail,
			CompanySummon compsumm, CaseDetails cd);

	List<RelationpersonCompany> findAllByPersonDetails(PersonDetails personList);

	List<RelationpersonCompany> findAllByCaseDetails(CaseDetails caseDtl);



}
