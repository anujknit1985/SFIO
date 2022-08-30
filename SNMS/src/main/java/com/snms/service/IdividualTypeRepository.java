package com.snms.service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.snms.entity.IndividualType;
@Repository
public interface IdividualTypeRepository extends JpaRepository<IndividualType, Long> {

}
