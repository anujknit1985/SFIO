package com.snms.entity;


import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.validation.Valid;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@Table(name = "inspectorHistory", schema = "investigation")
public class inspectorHistory {
	



	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", columnDefinition = "serial")
	private long id ;
	
	@ManyToOne
	@PrimaryKeyJoinColumn(name = "id")
	private CaseDetails caseDetails;
	
	@ManyToOne
	@PrimaryKeyJoinColumn(name = "userId")
	private AppUser appUser;
	
	private Date ioActiveDateFrom;
	private Date ioActiveDateTo;
	
	private String directorOrderFrom;
	
	private String directorOrderTo;
	
	public inspectorHistory(@Valid CaseDetails caseDetails) {
		
		this.caseDetails = caseDetails;
		// TODO Auto-generated constructor stub
	}

	public inspectorHistory(@Valid CaseDetails caseDetails, AppUser appUser) {
		this.caseDetails = caseDetails;
		this.appUser = appUser;
	}

	public inspectorHistory(@Valid CaseDetails caseDetails, AppUser appUser, String directorOrderTo) {
		this.caseDetails = caseDetails;
		this.appUser = appUser;
		this.directorOrderTo = directorOrderTo;
	}
	
	public inspectorHistory(@Valid CaseDetails caseDetails, AppUser appUser, String directorOrderTo ,String directorOrderFrom, Date ioActiveDateFrom,Date ioActiveDateTo) {
		this.caseDetails = caseDetails;
		this.appUser = appUser;
		this.directorOrderTo = directorOrderTo;
		this.directorOrderFrom = directorOrderFrom;
		this.ioActiveDateFrom = ioActiveDateFrom;
		this.ioActiveDateTo = ioActiveDateTo;
		
		
	}

	public inspectorHistory(long id, @Valid CaseDetails caseDetails, AppUser appUser, String directorOrderTo,
			String directorOrderFrom, Date ioActiveDateFrom, Date ioActiveDateTo) {
		
		
			
			this.id = id;
			this.caseDetails = caseDetails;
			this.appUser = appUser;
			this.directorOrderTo = directorOrderTo;
			this.directorOrderFrom = directorOrderFrom;
			this.ioActiveDateFrom = ioActiveDateFrom;
			this.ioActiveDateTo = ioActiveDateTo;

	}

	
	


}
