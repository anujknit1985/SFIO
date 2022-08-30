package com.snms.dto;

import lombok.Data;

@Data 
public class PendingForApprovalDTO {
	
	private Long officeOrderId;
	private String caseStrId;
	private String caseTitle;
	private Long caseId;
	private Object mcaOrderFile;
	private Object courtOrderFile;
	private Long summonTypeId;
	private String caseType;
	private String radiovalue;
	public PendingForApprovalDTO(Long officeOrderId, String caseStrId, String caseTitle, Long caseId ,String radiovalue) {
		super();
		this.officeOrderId = officeOrderId;
		this.caseStrId = caseStrId;
		this.caseTitle = caseTitle;
		this.caseId = caseId;
		this.radiovalue = radiovalue;
	}
	public PendingForApprovalDTO(String caseStrId, String caseTitle, Long caseId) {
		super();
		this.caseStrId = caseStrId;
		this.caseTitle = caseTitle;
		this.caseId = caseId;
	}
	public PendingForApprovalDTO() {
		super();
	} 
	
	public PendingForApprovalDTO(String caseStrId, String caseTitle, Long caseId,
			Object mcaOrderFile,Object courtOrderFile,String caseType) {
		super();
		this.caseStrId = caseStrId;
		this.caseTitle = caseTitle;
		this.caseId = caseId;
		this.mcaOrderFile = mcaOrderFile;
		this.courtOrderFile = courtOrderFile;
		this.caseType = caseType;
	}
	public PendingForApprovalDTO(Long officeOrderId, String caseStrId, String caseTitle, Long caseId) {
		super();
		this.officeOrderId = officeOrderId;
		this.caseStrId = caseStrId;
		this.caseTitle = caseTitle;
		this.caseId = caseId;
	}
	
}
