package com.snms.dto;

public class DscRegForm 
{
	private long id;
	private boolean check;
	private String serialNo;
	private String issuedBy;
	private String issuedTo;
	private String validity;
	private String serverDate;
	private String successXml;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public boolean isCheck() {
		return check;
	}
	public void setCheck(boolean check) {
		this.check = check;
	}
	public String getSerialNo() {
		return serialNo;
	}
	public void setSerialNo(String serialNo) {
		this.serialNo = serialNo;
	}
	public String getIssuedBy() {
		return issuedBy;
	}
	public void setIssuedBy(String issuedBy) {
		this.issuedBy = issuedBy;
	}
	public String getIssuedTo() {
		return issuedTo;
	}
	public void setIssuedTo(String issuedTo) {
		this.issuedTo = issuedTo;
	}
	public String getValidity() {
		return validity;
	}
	public void setValidity(String validity) {
		this.validity = validity;
	}
	public String getServerDate() {
		return serverDate;
	}
	public void setServerDate(String serverDate) {
		this.serverDate = serverDate;
	}
	public String getSuccessXml() {
		return successXml;
	}
	public void setSuccessXml(String successXml) {
		this.successXml = successXml;
	}
	
	
}
