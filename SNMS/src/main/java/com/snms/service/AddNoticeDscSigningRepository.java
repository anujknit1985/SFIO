package com.snms.service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.snms.entity.NoticeDSCSigning;
import com.snms.entity.SummonDSCSigning;
@Repository
public interface AddNoticeDscSigningRepository extends JpaRepository<NoticeDSCSigning, Long> {

}
