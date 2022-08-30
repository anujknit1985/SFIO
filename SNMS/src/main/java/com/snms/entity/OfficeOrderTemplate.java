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

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "OfficeOrderTemplate", schema = "investigation")
public class OfficeOrderTemplate {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", columnDefinition = "serial")
	private Long id;
	
	@ManyToOne
	@PrimaryKeyJoinColumn(name = "id")
	private OfficeOrder officeOrder;
	
	private Long srNo;
	
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
	
	private Boolean isFinal;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@PrimaryKeyJoinColumn(name = "userId")
	private AppUser appUser;
	
	@Column(name = "createdDate")
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdDate;

	public OfficeOrderTemplate(OfficeOrder officeOrder, Long srNo, String para1, String para2, String para3,
			String para4, String para5, Boolean isFinal, AppUser appUser, Date createdDate) {
		super();
		this.officeOrder = officeOrder;
		this.srNo = srNo;
		this.para1 = para1;
		this.para2 = para2;
		this.para3 = para3;
		this.para4 = para4;
		this.para5 = para5;
		this.isFinal = isFinal;
		this.appUser = appUser;
		this.createdDate = createdDate;
	}
	
	
	
}
