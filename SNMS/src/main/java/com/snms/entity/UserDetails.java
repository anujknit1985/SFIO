package com.snms.entity;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.springframework.format.annotation.DateTimeFormat;

import com.sun.istack.Nullable;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@Table(name = "UserDetails", schema = "authentication", uniqueConstraints = {
		@UniqueConstraint(columnNames = { "primaryMobile", "email", "sfioEmpId" }) })
public class UserDetails {

	public UserDetails(Long i) {
	}

	public UserDetails(Long id, String fullName) {
		this.id = id;
		this.fullName =fullName;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id",columnDefinition = "serial")
	private Long id;

	@OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(unique = true)
	private AppUser userId;

	@Column(name = "firstName", length = 40, nullable = false)
	private String firstName;

	@Column(name = "middleName", length = 40, nullable = true)
	private String middleName;

	@Column(name = "lastName", length = 40, nullable = false)
	private String lastName;

	@Column(name = "fullName", length = 120)
	private String fullName;

	@Column(name = "salutation", length = 3, nullable = false)
	private String salutation;

	@Column(name = "sfioEmpId", length = 30, nullable = false)
	private String sfioEmpId;

	@Column(name = "primaryMobile", length = 12, nullable = false)
	private String primaryMobile;
	
	@Column(name = "alternateNo", length = 12, nullable = false)
	private String alternateNo;

	@Column(name = "email", length = 100, nullable = false)
	private String email;
	@ManyToOne(fetch = FetchType.LAZY)
	@PrimaryKeyJoinColumn(name = "designation")
	private AddDesignation designation;
	
	@Transient
	private String 	userType;
	@Transient
	//@NotNull
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(pattern = "dd/MM/yyyy")
	private Date uiDob;
	
	@Transient
	private String fullName1;
	
	@Transient
	private Long unitId;
	
	@Transient
	private Long roleId;
	
	@Transient
	private Long designationId;
	
	@Transient
	//@NotNull
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(pattern = "dd/MM/yyyy")
	private Date uiJoiningDate;

//	@Column(name = "location", length = 50, nullable = false)
//	@NotNull
//	private String location;

	@ManyToOne(fetch = FetchType.LAZY)
	@PrimaryKeyJoinColumn(name = "unit")
	private UnitDetails unit;
	
	@Column(name = "dob")
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(pattern = "dd/MM/yyyy")
	private Date dob;
	
	@Column(name = "joiningDate")
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(pattern = "dd/MM/yyyy")
	private Date joiningDate;
	
	@Column(name = "createdDate")
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdDate;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@PrimaryKeyJoinColumn(name = "createdBy")
	private AppUser createdBy;

}
