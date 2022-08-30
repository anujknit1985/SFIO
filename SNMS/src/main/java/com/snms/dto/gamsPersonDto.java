package com.snms.dto;

import lombok.Data;

@Data
public class gamsPersonDto {
	 public gamsPersonDto(int count, Long unitId, String unitName, int approvedPersonCount) {
		this.srNo =count;
		this.unitId = unitId;
		this.unitName = unitName;
		this.approvedPersonCount = approvedPersonCount;
	}
	 public gamsPersonDto(String fullname) {
		 this.fullname = fullname;
		// TODO Auto-generated constructor stub
	}
	 
	 public gamsPersonDto(int srNo, Long unitId, String unitName, int approvedPersonCount,int createPersonCount) {
			this.srNo =srNo;
			this.unitId = unitId;
			this.unitName = unitName;
			this.approvedPersonCount = approvedPersonCount;
			this.createPersonCount = createPersonCount;
		}
	 
	 public gamsPersonDto(int srNo, Long unitId, String unitName, int approvedPersonCount,int createPersonCount ,int pendingPerson, int rejectPerson , int deletePerson) {
			this.srNo =srNo;
			this.unitId = unitId;
			this.unitName = unitName;
			this.approvedPersonCount = approvedPersonCount;
			this.createPersonCount = createPersonCount;
			this.pendingPerson = pendingPerson;
			this.rejectPerson = rejectPerson;
			this.deletePerson = deletePerson;
		}
	private int srNo;
	private Long unitId;
	 private String unitName;
	 private int approvedPersonCount;
	 private int createPersonCount;
	 private int pendingPerson;
	 private int rejectPerson;
	 private int deletePerson;
	 private String fullname;
}
