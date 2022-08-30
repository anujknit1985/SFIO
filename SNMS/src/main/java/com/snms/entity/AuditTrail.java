package com.snms.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "AUDIT_TRAIL",schema="public")
public class AuditTrail {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private int id;

	@Column(name = "ACTION_DATE")
	private Date actionDate;
	
	@Column(name = "ACTOR_ID")
	private int actorId;
	
	@Column(name = "ACTOR_NAME")
	private String actorName;
	
	@Column(name = "ACTOR_IP")
	private String actorIP;
	
	@Column(name = "URL")
	private String url;
	
	@Column(name = "DOMAIN")
	private String domain;
	
	@Column(name = "OPERATION_TYPE")
	private String operationType;
	
	@Column(name = "OPERATION_DESC")
	private String operationDesc;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public Date getActionDate() {
		return actionDate;
	}
	public void setActionDate(Date actionDate) {
		this.actionDate = actionDate;
	}
	public int getActorId() {
		return actorId;
	}
	public void setActorId(int actorId) {
		this.actorId = actorId;
	}
	public String getActorName() {
		return actorName;
	}
	public void setActorName(String actorName) {
		this.actorName = actorName;
	}
	public String getActorIP() {
		return actorIP;
	}
	public void setActorIP(String actorIP) {
		this.actorIP = actorIP;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getDomain() {
		return domain;
	}
	public void setDomain(String domain) {
		this.domain = domain;
	}
	public String getOperationType() {
		return operationType;
	}
	public void setOperationType(String operationType) {
		this.operationType = operationType;
	}
	public String getOperationDesc() {
		return operationDesc;
	}
	public void setOperationDesc(String operationDesc) {
		this.operationDesc = operationDesc;
	}
}
