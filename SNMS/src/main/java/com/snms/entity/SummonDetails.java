package com.snms.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data 
@Table(name = "SummonDetails", schema = "investigation")
public class SummonDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private int id;
	
	private Long caseId;
	
	private Long userid;
	
	private Date createdDate;
	private Date updatedDate;
	
	@OneToMany(cascade=CascadeType.ALL)
	@LazyCollection(LazyCollectionOption.FALSE)
    @JoinColumn(name="ENTITY_companySummon")
//	@OneToMany(mappedBy = "summonDetails", cascade = CascadeType.ALL)
	private List<CompanySummon> companySummon=new ArrayList<CompanySummon>();
	
	@Transient
	private CompanySummon companySummonsDto=new CompanySummon();
	
	
	 // @Transient
	 // private SummonType summonTypeDto=new SummonType();
	 
	
	@Transient
	private SummonType summonTypenewDto=new SummonType();
	
	
	@Transient
	private PersonDetails personDetails=new PersonDetails();
	
	@Transient 
	private boolean edit = false ;
	
	@Transient
	private boolean editDisable =  false;
	
	@Transient
	private boolean editperson =  false;
	
	@Transient
	private Date validDate;
}
