package com.snms.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;


import com.snms.entity.AppUser;
import com.snms.entity.CaseDetails;
import com.snms.entity.RelationpersonCompany;
import com.snms.entity.UserDetails;
import com.snms.entity.personcompanyApproval;

public interface PersoncompanyApprovalRepository extends JpaRepository<personcompanyApproval, Integer> {

	personcompanyApproval findAllById(int id);

  List<personcompanyApproval> findAllByRpc(RelationpersonCompany rpc);

List<personcompanyApproval> findAllByRpc(RelationpersonCompany comp, Sort by);





Page<personcompanyApproval> findAllByCreatedBy(Pageable pageable, UserDetails userdt);

	
 //personcompanyApproval    findAllByRpc(RelationpersonCompany rpc);
//	List<personcompanyApproval> findAllByRpcAndApproved_statusAndIsApproved_stage2(RelationpersonCompany rpc, int i, boolean b);

}
