package com.snms.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@Table(name = "CaseDetails", schema = "investigation")
public class CaseDetails {

	public CaseDetails(long id) {
		this.id = id;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", columnDefinition = "serial")
	private Long id;

	@Column(name = "caseId", length = 100, nullable = false)
	//@Size(min = 10,max = 25)
	@NotNull
	private String caseId;

	@Column(name = "radioValue", length = 11, nullable = false)
	//@Size(min = 3,max = 5)
	@NotNull
	private String radioValue;
	
	@Column(name = "caseTitle", length = 100, nullable = false)
	//@Size(min = 10,max = 25)
	@NotNull
	private String caseTitle;

	private String mcaOrderNo;
	@Transient
	private String mcaDate;
	@Transient
	private String mcaSubDate;
	private String courtOrderNo;
	@Transient
	private String courtDate;

	@Temporal(TemporalType.DATE)
	private Date mcaOrderDate;
	@Temporal(TemporalType.DATE)
	private Date courtOrderDate;
	@Temporal(TemporalType.DATE)
	private Date mcaSubmissionDate;
	@Transient
	private String companyName[];
	
	@Transient
	private String inspectors[];

	@Transient
	private Long unitId;
	
	@ManyToOne
	@PrimaryKeyJoinColumn(name = "unit")
	private UnitDetails unit;

	@Transient
	private ArrayList<Object> companies;

	@Transient
	private ArrayList<Object> inspectorList;

	private String mcaOrderFile;

	private String courtOrderFile;
	
	private String legacyOrderFile;
	
	private String financeYear;
	
	private int caseStage=1;
	
	private String remark;
	
	@Column(nullable = true)
	private Long updateByID;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date updatedDate;

	
	@Transient
	private MultipartFile mcaFile;

	@Transient
	private MultipartFile courtFile;
	
	@Transient
	private MultipartFile officeOrderFile;
	
	@Transient
	private String chooseIo;
	
	@Transient
	private String chooseAdo;
	
	@Transient
	private String section;
	
	
	
	@Transient 
	private MultipartFile addIoOrder[];
	
	@Transient 
	private MultipartFile deleteIoOrder[];
	
	@Transient
	private String chooseIOFile[];
	
	
	@Transient
	private String deleteIO[];
	
	
	@Transient
	private String deleteIOfile[];
	
	@Transient
	private String deletid;
	
	
	@Transient
	private String deleteIOid[];
	
	@Transient 
	private String  newIOorder[];
	
	
	@Transient
	private Boolean editLagacy;
	
	@Transient
	private String deleteCompId[];
   
	@Transient
	private String caseStatus;
	
	@Transient
	private String fileError;
	
	@Transient
	private String compRemark;
    
	@Transient
	private String caseRemark;
	
	
	@Transient
	private  String  upCompName;
	@Transient 
	private MultipartFile companyOrder;
	@Transient
	private Long upCompId;
}
