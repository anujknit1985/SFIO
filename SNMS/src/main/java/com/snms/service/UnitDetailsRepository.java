package com.snms.service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import com.snms.entity.UnitDetails;

@Service
public interface UnitDetailsRepository extends JpaRepository<UnitDetails, Long> {


}
