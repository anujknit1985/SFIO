package com.snms.service;

import org.springframework.data.jpa.repository.JpaRepository;

import com.snms.entity.CompanySummon;

public interface CompanySummonRepository extends JpaRepository<CompanySummon, Integer> {

	CompanySummon findAllById(int i);

}
