package com.snms.service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.snms.entity.UserDetails;

@Repository
public interface UserDeatilsRepository extends JpaRepository<UserDetails, Long> {

}
