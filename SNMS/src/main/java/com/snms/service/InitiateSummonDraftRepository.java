package com.snms.service;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.snms.entity.CaseDetails;
import com.snms.entity.InitiateSummonDraft;

import com.snms.entity.SummonType;

@Repository("initiatesummonrepo")
//@Transactional
public interface InitiateSummonDraftRepository extends JpaRepository<InitiateSummonDraft, Long>{

	@Modifying	
	@Transactional
	@Query("update InitiateSummonDraft isd set isd.isFinal = true where isd.caseDetails = ?1 and isd.summonType=?2")
	public int updateSummonDraftFinal(CaseDetails caseDetails, SummonType summonType);
//BY gouthami
	
	//@Query("select i from InitiateSummonDraft i ORDER BY id DESC LIMIT 1 where i.isFinal =false and i.caseDetails = ?1")
	//public InitiateSummonDraft findSummonDraftByCaseDetails(CaseDetails caseDetails);

	
}
