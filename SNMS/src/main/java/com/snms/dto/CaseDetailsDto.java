package com.snms.dto;

import java.util.ArrayList;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.snms.entity.UnitDetails;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CaseDetailsDto {

	public CaseDetailsDto(long id) {
		this.id = id;
	}

	private Long id;

	@Size(min = 10,max = 25)
	@NotNull
	private String caseId;

	@Size(min = 3,max = 5)
	@NotNull
	private String radioValue;
	
	@Size(min = 10,max = 25)
	@NotNull
	
	private String caseTitle;

	private String mcaOrderNo;
	
	private String mcaDate;

	private String courtOrderNo;
	private String courtDate;


	private Long unitId;
	
	private UnitDetails unit;

	private ArrayList<Object> companies;

	private ArrayList<Object> inspectorList;

	private String mcaOrderFile;

	private String courtOrderFile;

}
