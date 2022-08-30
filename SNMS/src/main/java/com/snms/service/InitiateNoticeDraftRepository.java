package com.snms.service;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.snms.entity.CaseDetails;
import com.snms.entity.InitiateNoticeDraft;

import com.snms.entity.SummonType;
@Repository("initiateNoticeDarftRepo")
public interface InitiateNoticeDraftRepository extends JpaRepository<InitiateNoticeDraft, Long>{

	@Modifying	
	@Transactional
	@Query("update InitiateNoticeDraft ind set ind.isFinal = true where ind.caseDetails = ?1 and ind.summonType=?2")
	public int updateNoticeDraftFinal(CaseDetails caseDetails, SummonType summonType);
	//change BY gouthami
	/*@Transactional
	@Query("select i from InitiateNoticeDraft i where i.isFinal =false and i.caseDetails = ?1")
	public InitiateNoticeDraft findNoticeDraftByCaseDetails(CaseDetails caseDetails);
*/
}
