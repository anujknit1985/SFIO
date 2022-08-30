package com.snms.entity;

import java.util.ArrayList;
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
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import lombok.Data;

@Entity
@Data
@Table(name = "CompanySummon", schema = "investigation")
public class CompanySummon {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private int id;
	private String cin;
	private String companyName;
	private String address;
	private String email;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@PrimaryKeyJoinColumn(name = "company_type_id")
    private CompanyType companyType;
	
	@OneToMany(cascade=CascadeType.ALL)
	@LazyCollection(LazyCollectionOption.FALSE)
    
	@JoinColumn(name="ENTITY_summontype")
	private List<SummonType> summonType=new ArrayList<SummonType>();
	
	//private List<SummonType> summonType=new ArrayList<SummonType>();
	
	/*@ManyToOne
    @JoinColumn(name = "ENTITY_companySummon")
    private SummonDetails summonDetails;*/

	public CompanySummon(String cin, String companyName, String address, String email) {
		super();
		this.cin = cin;
		this.companyName = companyName;
		this.address = address;
		this.email = email;
	}

	public CompanySummon() {
		super();
	}

	public CompanySummon(String cin, String companyName, String address, String email, CompanyType companyType) {
		super();
		this.cin = cin;
		this.companyName= companyName;
		this.address=address;
		this.email=email;
		this.companyType=companyType;
	}

	
}
