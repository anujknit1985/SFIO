package com.snms.dto;

import lombok.Data;

@Data
public class pendingPersonDto {
	public pendingPersonDto(String caseStrId, String caseTitle, String fullName,String panNumber ,int personapprovid  ) {
		super();
		this.caseStrId = caseStrId;
		this.caseTitle = caseTitle;
		this.fullName = fullName;
		this.panNumber= panNumber;
		this.personapprovid=personapprovid;
	}
	private String caseStrId;
	private String caseTitle;
	private String fullName;
	private String panNumber;
	private int personapprovid;
}
