package com.snms.service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.snms.entity.FinancialMaster;

public interface FinancialYearRepository extends JpaRepository<FinancialMaster, Long> {
	
	@Modifying
	@Transactional
	@Query("select fm from FinancialMaster fm where fm.isActive=true")
	public List<FinancialMaster> findActiveFY();
	
	@Modifying
	@Transactional
	@Query("update FinancialMaster fm set fm.isActive = false where fm.isActive = true")
	public int deactivateFinancialYear();
	
	@Modifying
	@Transactional
	@Query(value="ALTER SEQUENCE investigation.office_order_din_seq RESTART",nativeQuery=true)
	public  void resetOfficeOrderDinSeq();
	
	@Modifying
	@Transactional
	@Query(value="ALTER SEQUENCE investigation.summon_din_seq RESTART",nativeQuery=true)
	public  void resetSummonDinSeq();
	
	@Modifying
	@Transactional
	@Query(value="ALTER SEQUENCE investigation.notice_din_seq RESTART",nativeQuery=true)
	public void resetNoticeDinSeq();
}


