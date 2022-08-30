package com.snms.service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.snms.entity.SummonDSCSigning;
@Repository
public interface AddSummonDscSigningRepository extends JpaRepository<SummonDSCSigning, Long> {

}
