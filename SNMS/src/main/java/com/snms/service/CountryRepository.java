package com.snms.service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.snms.entity.Country;

@Repository
public interface CountryRepository extends JpaRepository<Country, Long>{

}
