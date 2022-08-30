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
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.snms.entity.AppUser;
import com.snms.entity.CaseDetails;
import com.snms.entity.SummonDetails;
import com.snms.entity.SummonStatus;
import com.snms.entity.SummonType;


@Repository
public interface SummonRepository extends JpaRepository<SummonStatus, Long>{
	
	@Modifying	
	@Transactional
	@Query("update SummonStatus s set s.aprrovalStage1 = true,is_sensitive=?3 where s.caseDetails = ?1 and s.id=?2")
	public int approveInfoByIdStage1(CaseDetails caseDetails,Long summonId,String isSensitive);
	
	@Modifying	
	@Transactional
	@Query("update SummonStatus s set s.aprrovalStage1 = true,s.aprrovalStage2 = true,summon_din= ?3, approval_date=current_timestamp ,is_sensitive=?4,approval_id=?5  where s.caseDetails = ?1 and s.id=?2")
	public int approveInfoByIdStage1Final(CaseDetails caseDetails,Long summonId, String summonDIN,String isSensitive,int approval_id);
	
	@Modifying
	@Transactional
	@Query("update SummonStatus s set s.aprrovalStage2 = true,summon_din= ?3, approval_date=current_timestamp where s.caseDetails = ?1 and s.id=?2")
	public int approveInfoByIdStage2(CaseDetails caseDetails,Long SummonId,String summonDin);	
	
	
	@Modifying	
	@Transactional
	@Query("update SummonStatus s set s.isRejected= true  where s.caseDetails = ?1 and s.id=?2")
	public int approveInfoByIdReject(CaseDetails caseDetails,Long summonId);
	
	public Optional<SummonStatus> findById(Long id);
	
	public List<SummonStatus> findByCaseDetails(CaseDetails caseDetails,Sort sort);
	
	public Optional<SummonStatus> findBySummonType(SummonType summonType);
	
	/*@Transactional
	@Query("select max(id) from SummonStatus")
	public Long findMaxId();*/
	
	@Transactional
	@Query(value="select nextval('investigation.summon_num_seq')",nativeQuery=true)
	public Long findMaxId();
	
	@Transactional
	@Query(value="select nextval('investigation.summon_din_seq')",nativeQuery=true)
	public Long getSummonDinSequence();

	@Transactional
	@Query("select s from SummonStatus s where summon_type_id =?1 and summonNo=?2")
	public Optional<SummonStatus> findBySummonTypeAndSummonNo(SummonType summonType,String summonNo);
	
	
	@Transactional
	@Query("select s from SummonStatus s where summon_type_id =?1 and summonNo=?2 and is_rejected =?3")
	public Optional<SummonStatus> findBySummonTypeAndSummonNoDraft(SummonType summonType,String summonNo , Boolean reject);
	//and is_rejected = false

	@Transactional
	@Query("select max(id) from SummonStatus")
	public Long findMaxid();

	public SummonStatus findAllBySummonNo(String summonNo);

	
	
	  
	  @Transactional
	  @Query("update SummonStatus s set is_sensitive=?3  where s.caseDetails = ?1 and s.id=?2")
	       public void sendBackToReview(CaseDetails caseDetails, Long summonId,String isSensitive);

	public SummonStatus findAllBySummonDin(String dinNumber);

	public Page<SummonStatus> findAllByAppUser(Pageable pageable, AppUser userDetails);

	

	 @Query("SELECT e FROM SummonStatus e WHERE id IN :id") 
     public Page<SummonStatus> findAllByIdsIn(Pageable pageable,  @Param("id")  List<Long> id);

	
	

	

	/*
	 * public Page<SummonStatus> findAllByApproval_Id(Pageable pageable, Long
	 * userId);
	 */
	 
	
	
}
