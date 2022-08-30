package com.snms.entity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;

public class CaseInspectors {
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id",columnDefinition = "serial")
	private Long id;

	@ManyToOne(cascade = CascadeType.ALL)
	@PrimaryKeyJoinColumn(name = "id")
	private CaseDetails caseDetails;
	
//	@ManyToOne(cascade = CascadeType.ALL)
//	@PrimaryKeyJoinColumn(name = "id")
//	private CaseDetails caseDetails;

}
