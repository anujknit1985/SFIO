package com.snms.service;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.snms.entity.CaseDetails;

@Repository("caseDetailsRepository")
public interface CaseDetailsRepository extends JpaRepository<CaseDetails, Long> {

	public Optional<CaseDetails> findById(Long id);
	
	//public List<CaseDetails> findByCaseId(String caseId);
	
	@Transactional
	@Query("select max(id) from CaseDetails")
	public Long findMaxid();

	//public List<CaseDetails> findAllByCaseStage(int i);

	public List<CaseDetails> findAllByCaseStage(int i, Sort by);
	
	
	List<Object[]> findAllByCaseStage(int i);
	/*@Transactional
	@Query("select c from CaseDetails c where c.caseTitle=?1")
	public CaseDetails uniqueCaseTile(String caseTitle);
	*/

	public CaseDetails findAllByMcaOrderNo(String mcaNo);

	public List<Object[]> findAllByMcaOrderNoStartsWith(String mcaNo);

	public CaseDetails findAllById(Long invCaseId);
	
}
