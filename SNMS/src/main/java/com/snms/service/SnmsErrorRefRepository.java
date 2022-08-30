package com.snms.service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.snms.entity.SnmsErrorReference;

@Repository
public interface SnmsErrorRefRepository extends JpaRepository<SnmsErrorReference, Long>{

}
