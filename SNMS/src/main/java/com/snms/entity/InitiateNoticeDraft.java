package com.snms.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "InitiateNoticeDraft", schema = "investigation")
public class InitiateNoticeDraft {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", columnDefinition = "serial")
	private Long id;

	@ManyToOne
	@PrimaryKeyJoinColumn(name = "id")
	private CaseDetails caseDetails;

	@Lob
	private String para1;
	@Lob
	private String para2;
	@Lob
	private String para3;
	@Lob
	private String para4;
	@Lob
	private String para5;

	@Lob
	private String para1h;
	@Lob
	private String para2h;
	@Lob
	private String para3h;
	@Lob
	private String para4h;
	@Lob
	private String para5h;

	private Boolean isFinal;

	@ManyToOne(fetch = FetchType.LAZY)
	@PrimaryKeyJoinColumn(name = "userId")
	private AppUser appUser;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@PrimaryKeyJoinColumn(name = "id")
	private SummonType summonType;

	@Column(name = "createdDate")
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdDate;
	
	private String dateOfAppear;
	
	private String summonNo;
/*
	public InitiateNoticeDraft(CaseDetails caseDetails, String para1, String para2, String para3, String para4,
			String para5, Boolean isFinal, AppUser appUser, Date createdDate,SummonType summonType,String dateOfAppear,String summonNo) {
		super();
		this.caseDetails = caseDetails;
		this.para1 = para1;
		this.para2 = para2;
		this.para3 = para3;
		this.para4 = para4;
		this.para5 = para5;
		this.isFinal = isFinal;
		this.appUser = appUser;
		this.createdDate = createdDate;
		this.summonType=summonType;
		this.dateOfAppear = dateOfAppear;
		this.summonNo = summonNo;
	}*/
	
	public InitiateNoticeDraft(CaseDetails caseDetails, String para1, String para2, String para3, String para4,
			String para5, Boolean isFinal, AppUser appUser, Date createdDate,SummonType summonType,String dateOfAppear,String summonNo) {
		super();
		this.caseDetails = caseDetails;
		this.para1 = para1;
		this.para2 = para2;
		this.para3 = para3;
		this.para4 = para4;
		this.para5 = para5;
		this.isFinal = isFinal;
		this.appUser = appUser;
		this.createdDate = createdDate;
		this.summonType=summonType;
		this.dateOfAppear = dateOfAppear;
		this.summonNo = summonNo;
	}
	
	public InitiateNoticeDraft(CaseDetails caseDetails, String para1, String para2, String para3, String para4,
			String para5, String para1h, String para2h, String para3h, String para4h, String para5h, Boolean isFinal,
			AppUser appUser, Date createdDate,SummonType summonType,String dateOfAppear,String summonNo) {
				super();
				
				
				this.caseDetails = caseDetails;
				this.para1 = para1;
				this.para2 = para2;
				this.para3 = para3;
				this.para4 = para4;
				this.para5 = para5;
				
				this.para1h = para1h;
				this.para2h = para2h;
				this.para3h = para3h;
				this.para4h = para4h;
				this.para5h = para5h;
				this.isFinal = isFinal;
				this.appUser = appUser;
				this.createdDate = createdDate;
				this.summonType=summonType;
				this.dateOfAppear = dateOfAppear;
				this.summonNo = summonNo;
	}
}
