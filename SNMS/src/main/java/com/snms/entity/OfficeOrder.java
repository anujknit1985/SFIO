package com.snms.entity;

import java.util.Date;

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

import org.springframework.format.annotation.DateTimeFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "OfficeOrder", schema = "investigation")
public class OfficeOrder {

	public OfficeOrder(Long id) {
		this.id = id;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", columnDefinition = "serial")
	private Long id;

	@ManyToOne
	@PrimaryKeyJoinColumn(name = "id")
	private CaseDetails caseDetails;

	private Boolean aprrovalStage1;

	private Boolean aprrovalStage2;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@PrimaryKeyJoinColumn(name = "userId")
	private AppUser appUser;
	
	@Column(name = "createdDate")
	@Temporal(TemporalType.TIMESTAMP)
	//@DateTimeFormat(pattern = "dd-MMM-yyyy HH:mm:ss A")
	private Date createdDate;
	
	private String ooDin;

	public OfficeOrder(CaseDetails caseDetails, Boolean aprrovalStage1, Boolean aprrovalStage2, AppUser appUser,
			Date createdDate) {
		super();
		this.caseDetails = caseDetails;
		this.aprrovalStage1 = aprrovalStage1;
		this.aprrovalStage2 = aprrovalStage2;
		this.appUser = appUser;
		this.createdDate = createdDate;
	}
	
	
	private String unsignFile;
	private String signFile;
	
	@Column(name = "orderSignedDate")
	@Temporal(TemporalType.TIMESTAMP)
	private Date orderSignedDate;
	
	private int isSigned;
	
	@Column(name = "approvalDate")
	@Temporal(TemporalType.TIMESTAMP)
	//@DateTimeFormat(pattern = "dd-MMM-yyyy HH:mm:ss A")
	private Date approvalDate;
	
	@Column(name = "is_dsc")
	private int isDSC;
	
	private String txnId;
	

}
