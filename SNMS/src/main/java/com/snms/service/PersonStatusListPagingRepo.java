package com.snms.service;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.snms.entity.personcompanyApproval;

public interface PersonStatusListPagingRepo extends PagingAndSortingRepository<personcompanyApproval,Integer> {

}
