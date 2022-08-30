package com.snms.entity;

import java.text.SimpleDateFormat;
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

import lombok.Data;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Data
@Table(name = "PersonDetails", schema = "gams")
public class PersonDetails {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "person_id", columnDefinition = "serial")
	private int personID;

	@Column(name = "FullName", length = 60, nullable = false)
	private String fullName;

	@Column(name = "Passport_Number", length = 10)
	private String passportNumber;
	@Column(name = "Pan_Number", length = 10)
	private String panNumber;

	@Temporal(TemporalType.DATE)
	@DateTimeFormat(pattern = "dd/MM/yyyy")
	private Date dob;
	@Column(name = "email", length = 100, nullable = false)
	private String email;
	@Column(name = "Gender")
	private String Gender;

	@Column(name = "Relation")
	private String Relation;

	private String RelationName;
	@Column(name = "Address")
	private String address;
	@Column(name = "primaryMobile", length = 12, nullable = false)
	private String primaryMobile;
	@Column(name = "alternateNo")
	private String alternateNo;

	private Boolean isApproved;
	//in Gep Portel
	@Column(name = "isGepUpdated")
	private Boolean isUpdated = false;
	@Temporal(TemporalType.TIMESTAMP)
	private Date GepupdatedDate; 
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(pattern = "dd/MM/yyyy")
	private Date passportDate;

	@Column(name = "createdDate")
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdDate;

	@ManyToOne(fetch = FetchType.LAZY)
	@PrimaryKeyJoinColumn(name = "createdBy")
	private UserDetails createdBy;

	@Temporal(TemporalType.TIMESTAMP)
	private Date updatedDate;
	
	
	@SuppressWarnings("deprecation")
	@Transient 
	private Date checkDate = new Date(2000, 11, 21);

	@ManyToOne(fetch = FetchType.LAZY)
	@PrimaryKeyJoinColumn(name = "updatedBy")
	private UserDetails updatedBy;

	@ManyToOne(fetch = FetchType.LAZY)
	@PrimaryKeyJoinColumn(name="countryId")
	private Country country;
	@Column(name = "Voter_Id_Number")
	private String voterId;
	@Transient
	private int personId;
	@Transient
	private String isApass;
	@Transient
	private String isAadhar;
	@Transient
	private String isAvoter;
	@Transient
	private String designationName;
	
	
	//for Pan Number
	private Boolean isDummy;
		
	@Transient
	private String isDummyValue;
	
	
	@Transient
	private Boolean  isNew;
	
	
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "personDetails")
	private List<RelationpersonCompany> companyperson = new ArrayList<RelationpersonCompany>();

	@Transient
	private RelationpersonCompany compDto = new RelationpersonCompany();

}
