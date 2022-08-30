package com.snms.dto;

import java.util.List;

import com.snms.entity.Company;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor 
public class InspectorListDTO {
	
	public InspectorListDTO(List<Company> companyList) {
		this.companyList = companyList;
	}
	private List<InspectorDTO> inspctorList;
	private List<InspectorDTO> copyToList;
	private String ioName;
	private List<Company> companyList;
	

}
