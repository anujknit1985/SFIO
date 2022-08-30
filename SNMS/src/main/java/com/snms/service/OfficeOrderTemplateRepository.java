package com.snms.service;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.snms.entity.OfficeOrder;
import com.snms.entity.OfficeOrderTemplate;

@Repository("officeOrderTempRepo")
public interface OfficeOrderTemplateRepository extends JpaRepository<OfficeOrderTemplate, Long>{
	
	@Transactional
	@Query("select max(srNo) from OfficeOrderTemplate o where o.officeOrder = ?1")
	public Long findMaxSrNo(OfficeOrder officeOrder);
	
	
	@Transactional
	@Query("select o from OfficeOrderTemplate o where o.srNo = (SELECT max(t.srNo) FROM OfficeOrderTemplate t where t.officeOrder =?1)  and o.officeOrder = ?1")
	public OfficeOrderTemplate findByOfficeOrder(OfficeOrder officeOrder);
	

}
