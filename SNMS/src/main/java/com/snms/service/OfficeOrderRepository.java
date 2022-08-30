package com.snms.service;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;
import javax.validation.Valid;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.snms.entity.CaseDetails;
import com.snms.entity.OfficeOrder;

@Repository
public interface OfficeOrderRepository extends JpaRepository<OfficeOrder, Long>{
	
	@Modifying
	@Transactional
	@Query("update OfficeOrder o set o.aprrovalStage1 = true where o.caseDetails = ?1")
	public int approveInfoByIdStage1(CaseDetails caseDetails);
	
	@Modifying
	@Transactional
	@Query("update OfficeOrder o set o.aprrovalStage2 = true, oo_din=?2, approval_date=current_timestamp where o.caseDetails = ?1")
	public int approveInfoByIdStage2(CaseDetails caseDetails, String ooDIN);	
	
	public Optional<OfficeOrder> findById(Long id);
	
	public List<OfficeOrder> findByCaseDetails(CaseDetails caseDetails,Sort sort);

	@Modifying
	@Transactional
	@Query("update OfficeOrder o set o.aprrovalStage1 = true, o.aprrovalStage2 = true,approval_date=current_timestamp, oo_din=?2, is_signed=1 where o.caseDetails = ?1")
	public void approveLegacyOrderById(CaseDetails caseDetails, String ooDIN);
	
	@Transactional
	@Query("select max(id) from OfficeOrder")
	public Long findMaxid();
	
	@Transactional
	@Query(value="select nextval('investigation.office_order_din_seq')",nativeQuery=true)
	public Long getOfficeOrderDinSequence();
	
	@Transactional
	@Query(value="select nextval('investigation.esign_txn_seq')",nativeQuery=true)
	public Long getEsignTxnSequence();

	public OfficeOrder findAllByCaseDetails(@Valid CaseDetails caseDetails);

	public List<OfficeOrder> findAllByAprrovalStage1(boolean b);

	public List<OfficeOrder> findAllByAprrovalStage1AndAprrovalStage2(boolean b, boolean c);
}
