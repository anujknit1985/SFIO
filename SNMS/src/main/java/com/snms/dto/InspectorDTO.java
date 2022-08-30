package com.snms.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor 
public class InspectorDTO {
	private int srNo;
	private String inspName;
	private boolean isIO;
	private boolean isAdo;

}
