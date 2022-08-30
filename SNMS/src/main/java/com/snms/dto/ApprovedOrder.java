package com.snms.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data @AllArgsConstructor
public class ApprovedOrder {

	private String caseStringId;
	private String CaseTitle;
	private Long orderid;
	private Object oo_din;
	private String radioValue;
	
}
