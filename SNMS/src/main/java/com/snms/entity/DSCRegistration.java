package com.snms.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Data
//@NoArgsConstructor
@Table(name="dsc_reg",schema = "public")
public class DSCRegistration {

	@Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="dsc_id")
	private Long id;
    
    private String serialNo;
	private String aliasName;

	@Column(columnDefinition="text",name="issuedto")
	private String issuedTo;
	@Column(columnDefinition="text",name="issuedby")
	private String issuedBy;
	
	@Column(name="validity")
	private Date validity;
	
	@Column(name="status")
	private String status;
	@Column(name="userid")
	private Long userid;
	@Column(columnDefinition="text")
	private String mac_Address;
	@Column(columnDefinition="text")
	private String ip_Address;
	@Column(columnDefinition="text")
	private String cdpPoint;
	@Column(columnDefinition="text",name="dsc_xmlresponse")
	private String signXml;	
	@Column(columnDefinition="text")
	private String revocation;
	
	@Column(name="isactive")
	private int regStatus;
	
	
	
	
	/**************************Constructors***************/
	public DSCRegistration(){}
	public DSCRegistration(String serialNo, String aliasName, String issuedTo,
			String issuedBy,Date validity,String status, Long userid,
			String mac_Address, String ip_Address, String cdpPoint, String signXml, String revocation, int regStatus) {
		super();
		this.serialNo = serialNo;
		this.aliasName = aliasName;
		this.issuedTo = issuedTo;
		this.issuedBy = issuedBy;
		this.validity = validity;
		this.status = status;
		this.userid = userid;
		this.mac_Address = mac_Address;
		this.ip_Address = ip_Address;
		this.cdpPoint = cdpPoint;
		this.signXml = signXml;
		this.revocation = revocation;
		this.regStatus = regStatus;
	}

	
	
}
