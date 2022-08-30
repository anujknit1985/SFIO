package com.snms.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class OfficeOrderTempDto {
	
	private String caseNo;
	private String date;
	
//	private String OrderNo;
//	private Date orderDate;
	
	private String para1;
	private String para2;
	private String para3;
	private String para4;
	private String para5;
	
	private Long officeOrderId;
	private String caseStrId;
	private Long caseId;
	
	private String directorName;
	private String clause;
}
