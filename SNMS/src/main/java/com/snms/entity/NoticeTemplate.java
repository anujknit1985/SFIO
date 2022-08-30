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
@Table(name = "NoticeTemplate", schema = "investigation")
public class NoticeTemplate {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@ManyToOne
	@PrimaryKeyJoinColumn(name = "id")
	private NoticeStatus noticeStatus;
	
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
	private String para1_hi;
	@Lob
	private String para2_hi;
	@Lob
	private String para3_hi;
	@Lob
	private String para4_hi;
	@Lob
	private String para5_hi;

	
	
	private Boolean isFinal;

	@ManyToOne(fetch = FetchType.LAZY,cascade=CascadeType.ALL)
	@PrimaryKeyJoinColumn(name = "userId")
	private AppUser appUser;

	@Column(name = "createdDate")
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdDate;

	public NoticeTemplate(NoticeStatus noticeStatus, Long srNo, String para1, String para2, String para3, String para4,
			String para5, Boolean isFinal, AppUser appUser, Date createdDate) {
		super();
		this.noticeStatus = noticeStatus;
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

	

	public NoticeTemplate(NoticeStatus noticeStatus, Long srNo, String para1, String para2, String para3, String para4,
			String para5, String para1h, String para2h, String para3h, String para4h, String para5h,
			 Boolean isFinal, AppUser appUser, Date createdDate) {
		super();
		this.noticeStatus = noticeStatus;
		this.srNo = srNo;
		this.para1 = para1;
		this.para2 = para2;
		this.para3 = para3;
		this.para4 = para4;
		this.para5 = para5;
		this.para1_hi = para1h;
		this.para2_hi  = para2h;
		this.para3_hi = para3h;
		this.para4_hi = para4h;
		this.para5_hi = para5h;
		this.isFinal = isFinal;
		this.appUser = appUser;
		this.createdDate = createdDate;
	}
	
	
}
