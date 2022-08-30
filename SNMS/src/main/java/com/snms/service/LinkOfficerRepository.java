package com.snms.service;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.snms.entity.Linkofficer;
import com.snms.entity.UnitDetails;
import com.snms.entity.UserDetails;

@Repository
public interface LinkOfficerRepository extends JpaRepository<Linkofficer, Long> {

	List<Linkofficer> findAllByUnitId(UnitDetails unitId);

	List<Linkofficer> findAllByRegularId(UserDetails regularId);

	Linkofficer findAllByRegularIdAndIsActive(UserDetails userdet, boolean b);

	List<Linkofficer> findAllByUserDetails(UserDetails userdetail);

	List<Linkofficer> findAllByUserDetailsAndIsActive(UserDetails userdetail, boolean b);

	



}
