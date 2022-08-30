package com.snms.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
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
import com.fasterxml.jackson.annotation.JsonIgnore;

import ch.qos.logback.core.status.Status;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.springframework.format.annotation.DateTimeFormat;

import lombok.Data;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Data

@Table(name = "Relation_personCompany", schema = "gams")
public class RelationpersonCompany {
	public RelationpersonCompany(CaseDetails caseDetails2, AddDesignation designation2, Date dateAppointment2,
			Date dateCessation2, String din2, String frn2, String iec2, int summon_type_id2,
			CompanySummon companySummon) {
		this.caseDetails = caseDetails2;
		this.designation = designation2;
		this.dateAppointment = dateAppointment2;
		this.dateCessation = dateCessation2;
		this.din = din2;
		this.frn = frn2;
		this.Iec = iec2;
		this.summon_type_id = summon_type_id2;
		this.companySummon = companySummon;

	}

	public RelationpersonCompany() {
		// TODO Auto-generated constructor stub
	}

	public RelationpersonCompany(CaseDetails caseDetails2, AddDesignation designation2, Date dateAppointment2,
			Date dateCessation2, String din2, String frn2, String iec2, int summon_type_id2,
			CompanySummon companySummon, String personremark) {
		this.caseDetails = caseDetails2;
		this.designation = designation2;
		this.dateAppointment = dateAppointment2;
		this.dateCessation = dateCessation2;
		this.din = din2;
		this.frn = frn2;
		this.Iec = iec2;
		this.summon_type_id = summon_type_id2;
		this.companySummon = companySummon;
		this.personRemark = personremark;
	}

	public RelationpersonCompany(int id, PersonDetails personDetails, AddDesignation designation) {
		this.id = id;
		this.personDetails = personDetails;
		this.designation = designation;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "RPC_ID", columnDefinition = "serial")
	private int id;

	@ManyToOne(fetch = FetchType.LAZY)
	@PrimaryKeyJoinColumn(name = "companySummon")
	private CompanySummon companySummon;

	@ManyToOne(fetch = FetchType.LAZY)
	@PrimaryKeyJoinColumn(name = "caseDetails")
	 @JsonIgnore
	private CaseDetails caseDetails;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "person_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
	private PersonDetails personDetails;

	private int summon_type_id;

	@ManyToOne(fetch = FetchType.LAZY)
	@PrimaryKeyJoinColumn(name = "designation_id")
	private AddDesignation designation;
	private Boolean isDeleted = false;

	@Temporal(TemporalType.DATE)
	@DateTimeFormat(pattern = "dd/MM/yyyy")
	private Date dateAppointment;

	@Temporal(TemporalType.DATE)
	@DateTimeFormat(pattern = "dd/MM/yyyy")
	private Date dateCessation;
	@Column(name = "FRN")
	private String frn;
	@Column(name = "IEC")
	private String Iec;
	@Column(name = "DIN")
	private String din;

	private Boolean isApproved = false;
	private Boolean isApprovedstage2 = false;
	
	@Column(name = "person_Remark")
	private String  personRemark;
	
	private String remark;
	private Boolean rejected=false;
	 @ManyToOne(fetch = FetchType.LAZY)
	@PrimaryKeyJoinColumn(name = "Investigation_status_id")
	private InvestigationStatus status;

	 @Transient
	private personcompanyApproval pca;

	 
	/*
	 * @OneToMany(cascade=CascadeType.ALL)
	 * 
	 * @LazyCollection(LazyCollectionOption.FALSE)
	 * 
	 * @JoinColumn(name="id") private List<personcompanyApproval> pac=new
	 * ArrayList<personcompanyApproval>();
	 */

	/*
	 * @ManyToOne(fetch = FetchType.LAZY)
	 * 
	 * @PrimaryKeyJoinColumn(name = "persondetails") private PersonDetails
	 * persondetails;
	 */

}
