package com.snms.service;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.snms.entity.SummonType;

public interface SummonTypeNew extends JpaRepository<SummonType, Integer> {

	

}
