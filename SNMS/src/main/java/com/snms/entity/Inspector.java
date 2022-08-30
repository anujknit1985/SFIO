package com.snms.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.Valid;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
@Entity
@Table(name = "Inspector", schema = "investigation")
public class Inspector {
	
	public Inspector(int srno, String name, String designation, int i, Long userId, boolean isIO,int caseAssigned) {
		
		this.srno = srno;
		this.name = name;
		this.designation = designation;
		this.userId = userId;
		this.isIO = isIO;
		this.caseAssigned = caseAssigned;
	}

	public Inspector(CaseDetails caseDetails, AppUser appUser, boolean isIO) {
		
		this.caseDetails = caseDetails;
		this.appUser = appUser;
		this.isIO = isIO;
	}


	public Inspector(long inspectorid, @Valid CaseDetails caseDetails, AppUser appUser, boolean isIO) {
		
		this.id = inspectorid;
		this.caseDetails = caseDetails;
		this.appUser = appUser;
		this.isIO = isIO;
	}


	/*
	 * public Inspector(long inspectorid, @Valid CaseDetails caseDetails, AppUser
	 * appUser, boolean isActive) {
	 * 
	 * this.id = inspectorid; this.caseDetails = caseDetails; this.appUser =
	 * appUser; this.isActive = isActive; // TODO Auto-generated constructor stub }
	 */

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id",columnDefinition = "serial")
	
	private Long id;
	@Transient
	private int srno;
	@Transient
	private String name;
	@Transient
	private String designation;
	@Transient
	private int caseAssigned;
	@Transient
	private Long userId;
	
	@ManyToOne
	@PrimaryKeyJoinColumn(name = "id")
	private CaseDetails caseDetails;
	
	@ManyToOne
	@PrimaryKeyJoinColumn(name = "userId")
	private AppUser appUser;
	
	
	private Boolean isIO;
	
	private Boolean isAdo;
	
	private Boolean isActive;
	
	
}
