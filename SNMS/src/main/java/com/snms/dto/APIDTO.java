package com.snms.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
public class APIDTO {
	
	private String caseNo;
	private String date;
	
	
	
	private Long summonId;
	private String caseStrId;
	private Long caseId;
	private String Name;
	private String summonNo;
	private String dateOfAppear;
	private String isSensitive;	
	private int summontypeid;
	private String summonDin;
	private byte [] data;
	public String getCaseNo() {
		return caseNo;
	}
	public void setCaseNo(String caseNo) {
		this.caseNo = caseNo;
	}
	public byte[] getData() {
		return data;
	}
	public void setData(byte[] data) {
		this.data = data;
	}
	
	

}
