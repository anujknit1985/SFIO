package com.snms.service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.snms.entity.InitiatorOfficeOrderDraft;

@Repository("initiatorOffOrdRepo")
public interface InitiatorOfficeOrderDraftRepository extends JpaRepository<InitiatorOfficeOrderDraft, Long> {
	
	/*@Transactional
	@Query("select i from InitiatorOfficeOrderDraft i where i.isFinal =false and i.caseDetails = ?1")
	public InitiatorOfficeOrderDraft findDraftByCaseDetails(CaseDetails caseDetails);*/
	

}
