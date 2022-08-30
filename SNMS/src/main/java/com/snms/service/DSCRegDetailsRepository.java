package com.snms.service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.snms.entity.DSCRegistration;

@Repository
public interface DSCRegDetailsRepository extends JpaRepository<DSCRegistration, Long>{

}
