package com.snms.entity;

import java.util.Date;

import javax.persistence.CascadeType;
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
@Table(name = "SummonTemplate", schema = "investigation")
public class SummonTemplate {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", columnDefinition = "serial")
	private Long id;

	@ManyToOne
	@PrimaryKeyJoinColumn(name = "id")
	private SummonStatus summonStatus;
	
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
	@Lob
	private String para6;
        
	@Lob
	private String para6H;
	@Lob
	private String para5H;
	@Lob
	private String para4H;
	@Lob
	private String para3H;
	@Lob
	private String para2H;
	@Lob
	private String para1H;

	private Boolean isFinal;

	@ManyToOne(fetch = FetchType.LAZY)
	@PrimaryKeyJoinColumn(name = "userId")
	private AppUser appUser;

	@Column(name = "createdDate")
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdDate;

	public SummonTemplate(SummonStatus summonStatus, Long srNo, String para1, String para2, String para3, String para4,
			String para5,String para6, Boolean isFinal, AppUser appUser, Date createdDate) {
		super();
		this.summonStatus = summonStatus;
		this.srNo = srNo;
		this.para1 = para1;
		this.para2 = para2;
		this.para3 = para3;
		this.para4 = para4;
		this.para5 = para5;
		this.para6 = para6;
		this.isFinal = isFinal;
		this.appUser = appUser;
		this.createdDate = createdDate;
	}
	
	public SummonTemplate(SummonStatus summonStatus, Long srNo, String para1, String para2, String para3,
			boolean isFinal, AppUser appUser, Date createdDate) {
		
		
		super();
		this.summonStatus = summonStatus;
		this.srNo = srNo;
		this.para1 = para1;
		this.para2 = para2;
		this.para3 = para3;
	
		this.isFinal = isFinal;
		this.appUser = appUser;
		this.createdDate = createdDate;
	}

	public SummonTemplate(SummonStatus summonStatus, Long srNo, String para1, String para2, String para3, String para4,
			String para5,String para6, String para1h, String para2h, String para3h, String para4h,
			String para5h, String para6h, Boolean isFinal, AppUser appUser, Date createdDate) {
		
		
		super();
		this.summonStatus = summonStatus;
		this.srNo = srNo;
		this.para1 = para1;
		this.para2 = para2;
		this.para3 = para3;
		this.para4 = para4;
		this.para5 = para5;
		this.para6 = para6;
		this.para1H = para1h;
		this.para2H=para2h;
		this.para3H=para3h;
		this.para4H=para4;
		this.para5H=para5h;
		this.para6H=para6h;
		this.isFinal = isFinal;
		this.appUser = appUser;
		this.createdDate = createdDate;
	}

	public SummonTemplate(SummonStatus summonStatus, Long srNo, String para1, String para2, String para3,
			String para1h, String para2h, String para3h, AppUser userDetails, Date date) {
		this.summonStatus = summonStatus;
		this.srNo = srNo;
		this.para1 = para1;
		this.para2 = para2;
		this.para3 = para3;
		this.para1H = para1h;
		this.para2H=para2h;
		this.para3H=para3h;
		this.appUser = userDetails;
		
		this.createdDate = date;
	}
	
}
