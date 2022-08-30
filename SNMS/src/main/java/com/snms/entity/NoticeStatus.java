package com.snms.entity;

import java.util.Date;
import java.util.List;

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

import org.hibernate.validator.constraints.Length;
import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "NoticeStatus", schema = "investigation")
public class NoticeStatus {

	public NoticeStatus(Long id) {
		this.id = id;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@ManyToOne
	@PrimaryKeyJoinColumn(name = "id")
	private CaseDetails caseDetails;
	
	@ManyToOne
	@PrimaryKeyJoinColumn(name = "id")
	//private SummonType summonType;
     
	//Added BY gouthami
	private SummonType summonType;
	private Boolean aprrovalStage1;

	private Boolean aprrovalStage2;
	
	@ManyToOne(fetch = FetchType.LAZY,cascade=CascadeType.ALL)
	@PrimaryKeyJoinColumn(name = "userId")
	private AppUser appUser;
	
	@Column(name = "createdDate")
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdDate;
	
	private String summonNo;
	private String dateOfAppear;
	private String isSensitive;

	/*
	public NoticeStatus(CaseDetails caseDetails, Boolean aprrovalStage1, Boolean aprrovalStage2, AppUser appUser,
			Date createdDate,String summonNo,String dateOfAppear,String isSensitive,SummonType summonType) {
		super();
		this.caseDetails = caseDetails;
		this.aprrovalStage1 = aprrovalStage1;
		this.aprrovalStage2 = aprrovalStage2;
		this.appUser = appUser;
		this.createdDate = createdDate;
		this.summonNo = summonNo;
		this.dateOfAppear = dateOfAppear; 
		this.isSensitive = isSensitive;
		this.summonType = summonType;
	}*/
	
	public NoticeStatus(CaseDetails caseDetails, Boolean aprrovalStage1, Boolean aprrovalStage2, AppUser appUser,
			Date createdDate,String summonNo,String dateOfAppear,String isSensitive,SummonType summonType) {
		super();
		this.caseDetails = caseDetails;
		this.aprrovalStage1 = aprrovalStage1;
		this.aprrovalStage2 = aprrovalStage2;
		this.appUser = appUser;
		this.createdDate = createdDate;
		this.summonNo = summonNo;
		this.dateOfAppear = dateOfAppear; 
		this.isSensitive = isSensitive;
		this.summonType = summonType;
	}
	public NoticeStatus(Long id2, CaseDetails caseDetails2, SummonType summonType2, Boolean aprrovalStage1,
			Boolean aprrovalStage2, AppUser appUser, Date approvalDate, String dateOfAppear, String isSensitive,
			String noticeDin, String remark, String summonNo, String UName , Date createdDate) {
		this.id = id2;
		this.caseDetails = caseDetails2;
		this.summonType = summonType2;
		this.aprrovalStage1 = aprrovalStage1;
		this.aprrovalStage2 = aprrovalStage2;
		this.appUser = appUser;
		this.approvalDate = approvalDate;
		this.dateOfAppear= dateOfAppear;
		this.isSensitive = isSensitive;
		this.noticeDin = noticeDin;
		this.remark = remark ;
		this.summonNo = summonNo;
		this.uName = UName;
		this.createdDate = createdDate;
	}

	public NoticeStatus(Long id, CaseDetails caseDetails, SummonType summonType, Boolean aprrovalStage1,
			Boolean aprrovalStage2, String issuedBY, Date createdDate, Date orderSignedDate, String summonNo,
			String dateOfAppear, String isSensitive, String unsignFile, String signFile, int isSigned,
			String noticeDin, Date approvalDate, int isDSC ,Boolean isRejected,
			Boolean is_physicallysent, String remark, Long verify_id, Long approval_Id) {
		
		this.id = id;
		this.caseDetails = caseDetails;
		this.summonType = summonType;
		this.aprrovalStage1 = aprrovalStage1;
		this.aprrovalStage2 = aprrovalStage2;
		this.uName = issuedBY;
		this.createdDate = createdDate;
		this.orderSignedDate = orderSignedDate;
		this.summonNo = summonNo;
		this.dateOfAppear = dateOfAppear;
		this.isSensitive = isSensitive;
		this.unsignFile = unsignFile;
		this.signFile = signFile;
		this.isSigned = isSigned;
		this.noticeDin = noticeDin;
		this.approvalDate = approvalDate;
		this.isDSC = isDSC;
       this.isRejected = isRejected;
       this.is_physicallysent = is_physicallysent;
       this.remark = remark;
       this.Verify_id = verify_id;
       this.approval_Id = approval_Id;
	}

	private String unsignFile;
	private String signFile;
	
	@Column(name = "orderSignedDate")
	@Temporal(TemporalType.TIMESTAMP)
	private Date orderSignedDate;
	private int isSigned;
	private String noticeDin;
	@Column(name = "approvalDate")
	@Temporal(TemporalType.TIMESTAMP)
	//@DateTimeFormat(pattern = "dd-MMM-yyyy HH:mm:ss A")
	private Date approvalDate;
	@Column(name = "is_dsc")
	private int isDSC;
	private String txnId;
	private Boolean isRejected = false;
	private Boolean is_physicallysent = false;
	@Transient
	private List < UserDetails> userdetails;
	
	@Transient
    private MultipartFile noticePhysicallyFile;
	
	@Length(min = 5 ,max = 50)
	private String remark;
	private Boolean isverified=false;
	@Column(name = "Verify_id")
	private Long  Verify_id;
	private Long approval_Id;
	@Transient
	private String uName;
}
