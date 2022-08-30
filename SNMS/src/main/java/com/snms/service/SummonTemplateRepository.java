package com.snms.service;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.snms.entity.SummonStatus;
import com.snms.entity.SummonTemplate;

@Repository("SummonTempRepo")
public interface SummonTemplateRepository extends JpaRepository<SummonTemplate, Integer>{
	
	@Transactional
	@Query("select max(srNo) from SummonTemplate s where s.summonStatus = ?1")
	public Long findMaxSrNo(SummonStatus summonStatus);
	
	
	@Transactional
	@Query("select s from SummonTemplate s where s.srNo = (SELECT max(t.srNo) FROM SummonTemplate t where t.summonStatus =?1)  and s.summonStatus = ?1")
	public SummonTemplate findBySummon(SummonStatus summonStatus);
	

}
