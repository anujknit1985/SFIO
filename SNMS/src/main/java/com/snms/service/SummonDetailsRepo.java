package com.snms.service;

import org.springframework.data.jpa.repository.JpaRepository;


import com.snms.entity.CompanySummon;
import com.snms.entity.SummonDetails;

public interface SummonDetailsRepo extends JpaRepository<SummonDetails, Integer> {

	SummonDetails findAllByCompanySummon(CompanySummon companysummon);

	SummonDetails findAllByCaseId(Long caseId);

}
