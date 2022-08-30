package com.snms.service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.snms.entity.OfficeOrderFixedTemp;

@Repository
public interface OfficeOrderFixedTempReopsitory extends JpaRepository<OfficeOrderFixedTemp, Integer>{

}
