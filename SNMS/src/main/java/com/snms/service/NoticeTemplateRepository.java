package com.snms.service;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.snms.entity.NoticeStatus;
import com.snms.entity.NoticeTemplate;
import com.snms.entity.OfficeOrder;
import com.snms.entity.OfficeOrderTemplate;

@Repository("NoticeTempRepo")
public interface NoticeTemplateRepository extends JpaRepository<NoticeTemplate, Long>{
	
	@Transactional
	@Query("select max(srNo) from NoticeTemplate n where n.noticeStatus = ?1")
	public Long findMaxSrNo(NoticeStatus noticeStatus);
	
	
	@Transactional
	@Query("select n from NoticeTemplate n where n.srNo = (SELECT max(t.srNo) FROM NoticeTemplate t where t.noticeStatus =?1)  and n.noticeStatus = ?1")
	public NoticeTemplate findByNotice(NoticeStatus noticeStatus);
	

}
