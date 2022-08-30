package com.snms.service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.snms.entity.FinancialMaster;

@Repository
public interface AddFinancialMasterRepo extends JpaRepository<FinancialMaster, Long>{

}
