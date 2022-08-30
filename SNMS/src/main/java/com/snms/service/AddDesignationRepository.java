package com.snms.service;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.snms.entity.AddDesignation;

//@Repository
public interface AddDesignationRepository extends JpaRepository<AddDesignation, Long> {
	@Query("SELECT s from AddDesignation s where s.type = ?1 or s.type = ?2 ")
	List<AddDesignation> findDesignationBytype(String string, String string2);

	List<AddDesignation> findDesignationBytype(String string, String string2, Sort by);
}
