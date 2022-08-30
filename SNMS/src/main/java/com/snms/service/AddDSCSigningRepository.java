package com.snms.service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.snms.entity.DSCSigning;

@Repository
public interface AddDSCSigningRepository extends JpaRepository<DSCSigning, Long>{

}
