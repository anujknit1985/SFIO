package com.snms.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data @NoArgsConstructor @AllArgsConstructor 
public class InspectorEditDTO {
	private List<InspectorDetailDTO> inspctorList;
	
}
