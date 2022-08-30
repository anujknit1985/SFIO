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

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data 
@Table(name = "personcompanyApproval", schema = "gams")
public class personcompanyApproval {
	public personcompanyApproval(int id) {
		this.id = id ;
		// TODO Auto-generated constructor stub
	}
	public personcompanyApproval() {
		// TODO Auto-generated constructor stub
	}
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "Approval_status_id", columnDefinition = "serial")
	private int id;
	 @ManyToOne(fetch = FetchType.LAZY)
		@PrimaryKeyJoinColumn(name = "Investigation_status_id")
	    private InvestigationStatus status ;
	    
		private Boolean isApproved_stage2;
		
		private int approved_status;
		@Column(name = "createdDate")
		@Temporal(TemporalType.TIMESTAMP)
		
		private Date createdDate;
		
		@ManyToOne(fetch = FetchType.LAZY)
		@PrimaryKeyJoinColumn(name = "createdBy")
		private UserDetails createdBy;
		
		private String approvedBY;
		
		@Column(name = "approvedDate")
		@Temporal(TemporalType.TIMESTAMP)
		
		private Date approvedDate;
	
		
		private String remark;
		
		@Transient
		private String rejectRemark;
		
		private Boolean rejected=false;
		private Boolean isDeleted = false;
		@OneToOne(cascade=CascadeType.ALL)
	    @JoinColumn(name="id")
		private RelationpersonCompany rpc;
		
		/*
		 * @ManyToOne(fetch = FetchType.LAZY)
		 * 
		 * @PrimaryKeyJoinColumn(name="id") private RelationpersonCompany pcr;
		 */
		
		
}
