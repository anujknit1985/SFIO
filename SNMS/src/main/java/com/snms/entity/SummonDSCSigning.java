package com.snms.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name="SummonDSCSigningDeatils",schema = "public")
public class SummonDSCSigning {

	@Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="dsc_id")
	private Long id;
	private String serialNo;
	@Column(columnDefinition="text")
	private String issuedTo;
	@Column(columnDefinition="text")
	private String issuedBy;
	private Date validity;
	private String status;
	@Column(columnDefinition="text")
	private String mac_Address;
	@Column(columnDefinition="text")
	private String ip_Address;
	@Column(columnDefinition="text")
	private String aliasName;
	@Column(columnDefinition="text")
	private String cdpPoint;
	@Column(columnDefinition="text")
	private String signingXml;
	private Long app_Id;
	private Long summonId;
	
    public SummonDSCSigning(String serialNo, String issuedTo, String issuedBy, Date validity, String status,
			String mac_Address, String ip_Address, String aliasName, String cdpPoint, String signingXml, Long app_Id,
			Long summonId) {
		super();
		this.serialNo = serialNo;
		this.issuedTo = issuedTo;
		this.issuedBy = issuedBy;
		this.validity = validity;
		this.status = status;
		this.mac_Address = mac_Address;
		this.ip_Address = ip_Address;
		this.aliasName = aliasName;
		this.cdpPoint = cdpPoint;
		this.signingXml = signingXml;
		this.app_Id = app_Id;
		this.summonId = summonId;
	}
}
