package com.snms.service;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.snms.entity.AppUser;
import com.snms.entity.CaseDetails;
import com.snms.entity.NoticeStatus;
import com.snms.entity.OfficeOrder;

import com.snms.entity.SummonType;

@Repository
public interface NoticeRepository extends JpaRepository<NoticeStatus, Long>{
	
	@Modifying
	@Transactional
	@Query("update NoticeStatus n set n.aprrovalStage1 = true where n.caseDetails = ?1 and n.id=?2")
	public int approveInfoByIdStage1(CaseDetails caseDetails,Long noticeId);
	
	@Modifying
	@Transactional
	@Query("update NoticeStatus n set n.aprrovalStage1 = true,n.aprrovalStage2 = true,notice_din= ?3, approval_date=current_timestamp,approval_id= ?4 where n.caseDetails = ?1 and n.id=?2")
	public int approveInfoByIdStage1Final(CaseDetails caseDetails,Long noticeId, String noticeDIN,int approvalid);
	
	@Modifying
	@Transactional
	@Query("update NoticeStatus n set n.aprrovalStage2 = true,notice_din= ?3, approval_date=current_timestamp where n.caseDetails = ?1 and n.id=?2")
	public int approveInfoByIdStage2(CaseDetails caseDetails,Long noticeId,String noticeDIN);	
	
	public Optional<NoticeStatus> findById(Long id);
	
	public List<NoticeStatus> findByCaseDetails(CaseDetails caseDetails,Sort sort);
	
	/*@Transactional
	@Query("select max(id) from NoticeStatus")
	public Long findMaxId();*/
	
	@Transactional
	@Query(value="select nextval('investigation.notice_num_seq')",nativeQuery=true)
	public Long findMaxId();
	
	@Transactional
	@Query(value="select nextval('investigation.notice_din_seq')",nativeQuery=true)
	public Long getNoticeDinSequence();

	//public Optional<NoticeStatus> findBySummonType(SummonType summonType);

	//changeBY gouthami 
	
	public Optional<NoticeStatus> findBySummonType(SummonType summonType);
	@Transactional
	@Query("select n from NoticeStatus n where summonType=?1 and summonNo=?2")
	public Optional<NoticeStatus> findBySummonTypeAndSummonNo(SummonType summonType, String summonNo);
	
	@Modifying	
	@Transactional
	@Query("update NoticeStatus n set n.isRejected= true   where n.caseDetails = ?1 and n.id=?2")
	public int approveInfoByIdReject(CaseDetails caseDetails, Long noticeDIN);

	public NoticeStatus findAllBySummonNo(String summonNo);

	public NoticeStatus findAllByNoticeDin(String dinNumber);

	public Page<NoticeStatus> findAllByAppUser(Pageable pageable, AppUser userDetails);
}
