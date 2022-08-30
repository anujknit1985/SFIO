package com.snms.dto;

import java.util.Date;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data 
public class InspectorDetailDTO {
	public InspectorDetailDTO(int srNo, String inspName, Boolean isIO, Boolean isAdo, String Designation,
			int caseAssign, int userID) {
		 this.srNo =srNo;
		 this.inspName =inspName;
		 this.isIO =isIO;
		 this.isAdo=isAdo;
		 this.Designation=Designation;
		 this.caseAssign =caseAssign;
		 this.userID = userID;
	}
	public InspectorDetailDTO(int srNo, String inspName, Boolean isIO, Boolean isAdo, String Designation,
			int caseAssign, int userID, String  fromDate ,String toDate) {
		this.srNo =srNo;
		 this.inspName =inspName;
		 this.isIO =isIO;
		 this.isAdo=isAdo;
		 this.Designation=Designation;
		 this.caseAssign =caseAssign;
		 this.userID = userID;
		 this.fromDate=fromDate;
		 this.toDate =toDate;
	}
	private int srNo;
	private String inspName;
	private boolean isIO;
	private boolean isAdo;
	private String Designation;
	private int caseAssign;
	private int userID;
	
	private String fromDate;
	
	private String toDate;
	

}
