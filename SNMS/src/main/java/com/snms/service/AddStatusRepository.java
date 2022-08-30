package com.snms.service;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.snms.entity.InvestigationStatus;

public interface AddStatusRepository extends JpaRepository<InvestigationStatus, Long> {

	List<InvestigationStatus> findAllBytype(String string);

}
