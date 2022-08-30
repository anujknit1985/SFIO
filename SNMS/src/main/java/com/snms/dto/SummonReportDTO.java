package com.snms.dto;

import java.util.Date;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor 
public class SummonReportDTO {
	
	private String isPhysically_signed;
	private int type;
	private String value;

}
