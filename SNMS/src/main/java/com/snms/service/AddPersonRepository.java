package com.snms.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.snms.entity.CaseDetails;
import com.snms.entity.PersonDetails;
@Repository
public interface AddPersonRepository extends JpaRepository<PersonDetails, Integer>{

	PersonDetails findAllByPanNumber(String panNumber);

	List<PersonDetails> findAllByCompanypersonCaseDetails(Optional<CaseDetails> findById);

	PersonDetails findAllByPersonID(int personID);

	PersonDetails findAllByVoterId(String voterId);
	PersonDetails findAllByPassportNumber(String passportNUmber);

	PersonDetails findAllByEmail(String email);

	List<PersonDetails> findAllByCompanypersonCaseDetails(Optional<CaseDetails> findById, Sort by);

	PersonDetails findAllByPanNumberAndIsApproved(String panNumber, boolean b);


}
