package com.snms.service;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.snms.entity.inspectorHistory;

@Repository
public interface InspectorHistoryRepository extends JpaRepository<inspectorHistory, Long> {


	@Transactional
	@Query("select max(id) from inspectorHistory")
	public Long findMaxid();
}
