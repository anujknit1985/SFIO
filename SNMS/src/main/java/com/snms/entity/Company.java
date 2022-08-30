package com.snms.entity;



import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.Valid;

import org.jetbrains.annotations.Nullable;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor @AllArgsConstructor
@Table(name = "Company", schema = "investigation")
public class Company {

	public Company( @Valid CaseDetails caseDetails, String name , Boolean isActive) {
		
		this.caseDetails = caseDetails;
		this.name = name;
		this.isActive = isActive;
	}


	public Company(long id, @Valid CaseDetails caseDetails, String name ,Boolean isActive) {
		this.caseDetails = caseDetails;
		this.name = name;
		this.id = id;
		this.isActive = isActive;
	}


	public Company(@Valid CaseDetails caseDetails, String name, boolean isActive , Date Created_date, UserDetails createdBy, Long section,
			Long approved_status) {
	
		this.caseDetails = caseDetails;
		this.name = name;
		this.isActive = isActive;
		this.Created_date = Created_date;
		this.createdBy = createdBy;
		this.section = section;
		this.approved_status = approved_status;
		
	}


	public Company(long compid2, @Valid CaseDetails caseDetails, String name, boolean isActive , Date Created_date, UserDetails createdBy, Long section,
			Long approved_status) {
		this.id = compid2;
		this.caseDetails = caseDetails;
		this.name = name;
		this.isActive = isActive;
		this.Created_date = Created_date;
		this.createdBy = createdBy;
		this.section = section;
		this.approved_status = approved_status;
	}


	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id",columnDefinition = "serial")
	private Long id;

	@ManyToOne
	@PrimaryKeyJoinColumn(name = "id")
	private CaseDetails caseDetails;
	
	private Boolean isActive;
	private String name;
	@ManyToOne(fetch = FetchType.LAZY)
	@PrimaryKeyJoinColumn(name = "createdBy")
	private UserDetails createdBy;
	@Temporal(TemporalType.TIMESTAMP)
	private Date Created_date;
	
	
	
	 @Column(nullable = true)
	private Long approvedBY;
	 @Column(nullable = true)
	private Long approved_status;
    private String companyOrderFile;
	@Temporal(TemporalType.TIMESTAMP)
	private Date  approval_date;
	@Column(nullable = true)
	private Long section;
	
	private String remark;

	@Transient
	private Long compId;

	

}
