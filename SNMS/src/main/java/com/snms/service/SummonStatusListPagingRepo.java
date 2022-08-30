package com.snms.service;

import org.springframework.data.domain.Page;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.snms.entity.SummonStatus;


public interface SummonStatusListPagingRepo extends PagingAndSortingRepository<SummonStatus, Long> {

	

}
