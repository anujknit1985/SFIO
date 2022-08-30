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
import javax.persistence.Transient;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "InitiateSummonDraft", schema = "investigation")
public class InitiateSummonDraft {

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
	private String para6;

	private Boolean isFinal;

	
	  @Lob @Transient private String para3_1;
	  
	  @Lob @Transient private String para3_1h;
	 
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

	public InitiateSummonDraft(CaseDetails caseDetails, String para1, String para2, String para3, String para4,
			String para5,String para6, Boolean isFinal, AppUser appUser, Date createdDate,SummonType summonType,String dateOfAppear,String summonNo) {
		super();
		this.caseDetails = caseDetails;
		this.para1 = para1;
		this.para2 = para2;
		this.para3 = para3;
		this.para4 = para4;
		this.para5 = para5;
		this.para6 = para6;
		this.isFinal = isFinal;
		this.appUser = appUser;
		this.createdDate = createdDate;
		this.summonType=summonType;
		this.dateOfAppear = dateOfAppear;
		this.summonNo = summonNo;
	}

	public InitiateSummonDraft(CaseDetails caseDetails, String para1, String para2, String para3, String para4,
			String para5,String para6,
			 String para1h, String para2h, String para3h, String para4h,
				String para5h,String para6h,
			Boolean isFinal, AppUser appUser, Date createdDate,SummonType summonType,String dateOfAppear,String summonNo) {
		super();
		this.caseDetails = caseDetails;
		this.para1 = para1;
		this.para2 = para2;
		this.para3 = para3;
		this.para4 = para4;
		this.para5 = para5;
		this.para6 = para6;
		this.para1_h = para1h;
		this.para2_h = para2h;
		this.para3_h = para3h;
		this.para4_h = para4h;
		this.para5_h = para5h;
		this.para6_h = para6h;
		
		this.isFinal = isFinal;
		this.appUser = appUser;
		this.createdDate = createdDate;
		this.summonType=summonType;
		this.dateOfAppear = dateOfAppear;
		this.summonNo = summonNo;
	}


	@Lob
	private String para1_h;
	@Lob
	private String para2_h;
	@Lob
	private String para3_h;
	@Lob
	private String para4_h;
	@Lob
	private String para5_h;
	@Lob
	private String para6_h;
}
