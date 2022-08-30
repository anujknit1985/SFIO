package com.snms.entity;


import java.util.Date;
import javax.persistence.Transient;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.snms.dto.StatusDTO;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data @NoArgsConstructor
@Table(name = "UnitDetails", schema = "authentication", uniqueConstraints = {
		@UniqueConstraint(name = "Unit_Details_UK", columnNames = "unitName") })
public class UnitDetails {
	
	public UnitDetails(Long unitId) {
		this.unitId = unitId;
	}

	public UnitDetails(Long unitId2, String unitName2, String location2, String address2, String telephoneNo2,
			String faxNo2, String eMail2, Date createdDate2) {
		this.unitId = unitId2;
		this.unitName = unitName2;
		this.location =location2;
		this.address = address2;
		this.telephoneNo = telephoneNo2;
		this.faxNo = faxNo2;
		this.eMail = eMail2;
		this.createdDate = createdDate2;
	}

	public UnitDetails(long unitId, String unitName) {
		this.unitId = unitId;
		this.unitName = unitName;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "unitId", columnDefinition = "serial")
	private Long unitId;

	@Column(name = "unitName", length = 20, nullable = false)
	private String unitName;
	
	@Column(name = "location", length = 70, nullable = false)
	private String location;
	
	@Column(name = "address", length = 200, nullable = false)
	private String address;
	
	@Column(name = "address_h", length = 200, nullable = false)
    private String address_hi;
	
	@Column(name = "telephoneNo", length = 30, nullable = false)
	private String telephoneNo;
	
	@Column(name = "faxNo", length = 30, nullable = false)
	private String faxNo;
	
	@Column(name = "eMail", length = 50, nullable = false)
	private String eMail;
	
	@Column(name = "createdDate")
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdDate;
	
	@Transient
	private Boolean editUnit;
	 @Transient
	    private StatusDTO stdDTo;
		
	 
	 @Column(name = "updatedDate")
		@Temporal(TemporalType.TIMESTAMP)
		private Date updatedDate;
   

	
//	@ManyToOne(fetch = FetchType.LAZY)
//	@PrimaryKeyJoinColumn(name = "createdBy")
//	private AppUser createdBy;

}
