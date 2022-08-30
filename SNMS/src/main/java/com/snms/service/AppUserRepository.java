package com.snms.service;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.snms.entity.AppUser;

@Repository
public interface AppUserRepository extends JpaRepository<AppUser, Long> {



	Optional<AppUser> findByUserId(Long UserId);

}
