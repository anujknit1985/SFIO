package com.snms.entity;


import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.validator.constraints.Length;
import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "SummonStatus", schema = "investigation")
public class SummonStatus {

	public SummonStatus(Long id) {
		this.id = id;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", columnDefinition = "serial")
	private Long id;

	@ManyToOne
	@PrimaryKeyJoinColumn(name = "id")
	private CaseDetails caseDetails;
	@ManyToOne
	@PrimaryKeyJoinColumn(name = "id")
	//private SummonType summonType;
      private SummonType summonType;
	private Boolean aprrovalStage1;
	private Boolean aprrovalStage2;
	@ManyToOne(fetch = FetchType.LAZY)
	@PrimaryKeyJoinColumn(name = "userId")
	private AppUser appUser;
    @Column(name = "createdDate")
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdDate;
	private String summonNo;
	private String dateOfAppear;
	private String isSensitive;
	  @Transient
	  private String DinNumber;
	  @Transient
	  private String Type;
	  
	/*
	 * public SummonStatus(CaseDetails caseDetails, Boolean aprrovalStage1, Boolean
	 * aprrovalStage2, AppUser appUser, Date createdDate,String summonNo,String
	 * dateOfAppear,String isSensitive,SummonType summonType) { super();
	 * this.caseDetails = caseDetails; this.aprrovalStage1 = aprrovalStage1;
	 * this.aprrovalStage2 = aprrovalStage2; this.appUser = appUser;
	 * this.createdDate = createdDate; this.summonNo = summonNo; this.dateOfAppear =
	 * dateOfAppear; this.isSensitive = isSensitive; this.summonType = summonType; }
	 */
	
	public SummonStatus(CaseDetails caseDetails, Boolean aprrovalStage1, Boolean aprrovalStage2, AppUser appUser,
			Date createdDate,String summonNo,String dateOfAppear,SummonType summonType) {
		super();
		this.caseDetails = caseDetails;
		this.aprrovalStage1 = aprrovalStage1;
		this.aprrovalStage2 = aprrovalStage2;
		this.appUser = appUser;
		this.createdDate = createdDate;
		this.summonNo = summonNo;
		this.dateOfAppear = dateOfAppear; 
		//this.isSensitive = isSensitive;
		this.summonType = summonType;
	}
	
	public SummonStatus(CaseDetails caseDetails, boolean aprrovalStage1, boolean aprrovalStage2, AppUser appUser, Date createdDate,
			String summonNo, String dateOfAppear, SummonType summonType, String dateOfIssue, String summonDin,
			boolean isOffline, String unsignFile) {
		super();
		this.caseDetails = caseDetails;
		this.aprrovalStage1 = aprrovalStage1;
		this.aprrovalStage2 = aprrovalStage2;
		this.appUser = appUser;
		this.createdDate = createdDate;
		this.summonNo = summonNo;
		this.dateOfAppear = dateOfAppear; 
		//this.isSensitive = isSensitive;
		this.summonType = summonType;
		this.issueDate= dateOfIssue;
		this.summonDin = summonDin;
		this.isOffline = isOffline;
		this.unsignFile = unsignFile;
	     }
	public SummonStatus(Long id, CaseDetails caseDetails, SummonType summonType, Boolean aprrovalStage1,
			Boolean aprrovalStage2, AppUser appUser, Date approvalDate, String dateOfAppear, String isSensitive,
			String summonDin, String remark, String summonNo, String uName, Date createdDate) {
	  this.id = id;
	  this.caseDetails = caseDetails;
	  this.summonType= summonType;
	  this.aprrovalStage1 = aprrovalStage1;
	  this.aprrovalStage2 = aprrovalStage2;
	  this.appUser = appUser;
	  this.approvalDate= approvalDate;
	  this.dateOfAppear = dateOfAppear;
	  this.isSensitive = isSensitive;
	  this.summonDin = summonDin;
	  this.remark = remark;
	  this.summonNo = summonNo;
	  this.uName = uName;
	  this.createdDate = createdDate;
	}

	public SummonStatus(Long id, CaseDetails caseDetails, SummonType summonType, Boolean aprrovalStage1,
			Boolean aprrovalStage2, String issuedBY, Date createdDate,  Date orderSignedDate,String summonNo, String dateOfAppear,
			String isSensitive, String unsignFile, String signFile, int isSigned, String summonDin,
			Date approvalDate, int isDSC, Boolean isOffline, String issueDate, Boolean isRejected,
			Boolean is_physicallysent, String remark, Long verify_id, Long approval_Id) {
		this.id = id;
		this.caseDetails = caseDetails;
		this.summonType = summonType;
		this.aprrovalStage1 = aprrovalStage1;
		this.aprrovalStage2 = aprrovalStage2;
		this.uName = issuedBY;
		this.createdDate = createdDate;
		this.orderSignedDate= orderSignedDate;
		this.summonNo = summonNo;
		this.dateOfAppear = dateOfAppear;
		this.isSensitive = isSensitive;
		this.unsignFile = unsignFile;
		this.signFile = signFile;
		this.isSigned = isSigned;
		this.summonDin = summonDin;
		this.approvalDate = approvalDate;
		this.isDSC = isDSC;
		this.isOffline = isOffline;
		this.issueDate = issueDate;
		this.isRejected = isRejected;
		this.Is_physicallysent = is_physicallysent;
		this.remark = remark;
		this.Verify_id = verify_id;
		this.approval_Id= approval_Id;
	}

	private String unsignFile;
	private String signFile;
    @Column(name = "orderSignedDate")
	@Temporal(TemporalType.TIMESTAMP)
	private Date orderSignedDate;
    private int isSigned;
	private String summonDin;
	@Column(name = "approvalDate")
	@Temporal(TemporalType.TIMESTAMP)
	//@DateTimeFormat(pattern = "dd-MMM-yyyy HH:mm:ss A")
	private Date approvalDate;
	@Column(name = "is_dsc")
	private int isDSC;
	private String txnId;
    private Boolean isOffline = false ;
 	private String issueDate;
    private Boolean isRejected=false;
    private Boolean Is_physicallysent=false;
	/* private Boolean Is */
    @Transient
    private MultipartFile summonPhysicallyFile;
	@Length(min = 5 ,max = 50)
	private String remark;
	
	private Boolean isverified=false;
	@Column(name = "Verify_id")
	private Long  Verify_id;
	private Long approval_Id;
	@Transient
	private String uName;
	
	@Transient
	private String na;
	@Transient
	private  List<Long> ids;
	/*
	 * @Transient private String issuedBy;
	 */
	/*
	 * @ManyToOne(fetch = FetchType.LAZY)
	 * 
	 * @PrimaryKeyJoinColumn(name = "userId") private AppUser appUserApprovedId;
	 */
    
}
