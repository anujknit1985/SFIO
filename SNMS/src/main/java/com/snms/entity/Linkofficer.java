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
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@Table(name = "Link_Officer", schema = "authentication")
public class Linkofficer {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(columnDefinition = "serial")
	private Long  Id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@PrimaryKeyJoinColumn(name = "unit")
	private UnitDetails unitId;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@PrimaryKeyJoinColumn(name = "LinkOfficerunit")
	private UnitDetails LinkunitId;
	
	
	@ManyToOne
	@PrimaryKeyJoinColumn(name = "LinkOfficerId")
	private UserDetails userDetails;
	
	@ManyToOne
	@PrimaryKeyJoinColumn(name = "regularkOfficerId")
	private UserDetails regularId;
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(pattern = "dd/MM/yyyy") 
	private Date fromDate ;
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(pattern = "dd/MM/yyyy") 
	private Date toDate ;
	private Boolean isActive;

    @Transient
    private String fromDate1;
    @Transient
    private String toDate1;
}
