package com.snms.dto;

import java.util.List;

import com.snms.entity.NoticeStatus;
import com.snms.entity.UnitDetails;

import lombok.Data;

@Data
public class StatusDTO {

	
	
	
	/*
	 * public StatusDTO(int count, Long unitId, String unitName, int allNoticeUnit1,
	 * int approvedNoticeUnit1, int EsignedNoticeUnit1, int PendingNoticeUnit1 ,int
	 * allSummonUnit,int approvedSummonUnit1,int EsignedSummonUnit1,int
	 * PendingSummonUnit1 ) { this.srNo = count; this.unitId = unitId; this.unitName
	 * = unitName; this.allNoticeUnit1= allNoticeUnit1; this.approvedNoticeUnit1
	 * =approvedNoticeUnit1; this.EsignedNoticeUnit1 = EsignedNoticeUnit1;
	 * this.PendingNoticeUnit1 = PendingNoticeUnit1; this.allSummonUnit=
	 * allSummonUnit; this.approvedSummonUnit1=approvedSummonUnit1;
	 * this.EsignedSummonUnit1= EsignedSummonUnit1;
	 * this.PendingSummonUnit1=PendingSummonUnit1;
	 * 
	 * }
	 */

 public StatusDTO() {
		// TODO Auto-generated constructor stub
	}

public StatusDTO(int srNo, Long unitId, String unitName, int approvedSummonUnit1, int EsignedSummonUnit1, int offlineSummon) {
	this.srNo = srNo;
	this.unitId = unitId;
	this.unitName = unitName;
	this.approvedSummonUnit1=approvedSummonUnit1;
	this.EsignedSummonUnit1= EsignedSummonUnit1;
	this.offlineSummon = offlineSummon;

}

/*
 * public StatusDTO(int count, Long unitId, String unitName, int allNoticeUnit1,
 * int approvedNoticeUnit1, int EsignedNoticeUnit1, int PendingNoticeUnit1 ,int
 * allSummonUnit,int approvedSummonUnit1,int EsignedSummonUnit1,int
 * PendingSummonUnit1 ,int offlineSummon) { this.srNo = count; this.unitId =
 * unitId; this.unitName = unitName; this.allNoticeUnit1= allNoticeUnit1;
 * this.approvedNoticeUnit1 =approvedNoticeUnit1; this.EsignedNoticeUnit1 =
 * EsignedNoticeUnit1; this.PendingNoticeUnit1 = PendingNoticeUnit1;
 * this.allSummonUnit= allSummonUnit;
 * this.approvedSummonUnit1=approvedSummonUnit1; this.EsignedSummonUnit1=
 * EsignedSummonUnit1; this.PendingSummonUnit1=PendingSummonUnit1;
 * this.offlineSummon = offlineSummon; }
 */

/*
 * public StatusDTO(int count, Long unitId, String unitName, int allNoticeUnit1,
 * int approvedNoticeUnit1, int EsignedNoticeUnit1, int PendingNoticeUnit1 ,int
 * allSummonUnit,int approvedSummonUnit1,int EsignedSummonUnit1,int
 * PendingSummonUnit1 ,int offlineSummon,int physummon) { this.srNo = count;
 * this.unitId = unitId; this.unitName = unitName; this.allNoticeUnit1=
 * allNoticeUnit1; this.approvedNoticeUnit1 =approvedNoticeUnit1;
 * this.EsignedNoticeUnit1 = EsignedNoticeUnit1; this.PendingNoticeUnit1 =
 * PendingNoticeUnit1; this.allSummonUnit= allSummonUnit;
 * this.approvedSummonUnit1=approvedSummonUnit1; this.EsignedSummonUnit1=
 * EsignedSummonUnit1; this.PendingSummonUnit1=PendingSummonUnit1;
 * this.offlineSummon = offlineSummon; this.physummon = physummon; }
 */

public StatusDTO(int count, int allNoticeUnit1, int approvedNoticeUnit1, int EsignedNoticeUnit1, int PendingNoticeUnit1 ,int allSummonUnit,int approvedSummonUnit1,int EsignedSummonUnit1,int PendingSummonUnit1 ) {
	this.srNo = count;
	/*
	 * this.unitId = unitId; this.unitName = unitName;
	 */
	this.allNoticeUnit1= allNoticeUnit1;
	this.approvedNoticeUnit1 =approvedNoticeUnit1;
	this.EsignedNoticeUnit1 = EsignedNoticeUnit1;
	this.PendingNoticeUnit1 = PendingNoticeUnit1;
	this.allSummonUnit= allSummonUnit;
	this.approvedSummonUnit1=approvedSummonUnit1;
	this.EsignedSummonUnit1= EsignedSummonUnit1;
	this.PendingSummonUnit1=PendingSummonUnit1;
	//this.offlineSummon = offlineSummon;
}

public StatusDTO(int count, String caseId, String caseTittle,  int allNoticeUnit1, int approvedNoticeUnit1, int EsignedNoticeUnit1, int PendingNoticeUnit1 ,int allSummonUnit,int approvedSummonUnit1,int EsignedSummonUnit1,int PendingSummonUnit1 ,int offlineSummon) {
	this.srNo = count;
	this.caseId = caseId;
	this.caseTittle = caseTittle;
	this.allNoticeUnit1= allNoticeUnit1;
	this.approvedNoticeUnit1 =approvedNoticeUnit1;
	this.EsignedNoticeUnit1 = EsignedNoticeUnit1;
	this.PendingNoticeUnit1 = PendingNoticeUnit1;
	this.allSummonUnit= allSummonUnit;
	this.approvedSummonUnit1=approvedSummonUnit1;
	this.EsignedSummonUnit1= EsignedSummonUnit1;
	this.PendingSummonUnit1=PendingSummonUnit1;
	this.offlineSummon = offlineSummon;
}

public StatusDTO(int count, String caseId2, String caseTitle, int allNoticeUnit1, int approvedNoticeUnit1, int EsignedNoticeUnit1, int PendingNoticeUnit1, int allSummonUnit,
		int approvedSummonUnit1, int EsignedSummonUnit1, int PendingSummonUnit1) {
	this.srNo = count;
	this.caseId = caseId2;
	this.caseTittle = caseTitle;
	this.allNoticeUnit1= allNoticeUnit1;
	this.approvedNoticeUnit1 =approvedNoticeUnit1;
	this.EsignedNoticeUnit1 = EsignedNoticeUnit1;
	this.PendingNoticeUnit1 = PendingNoticeUnit1;
	this.allSummonUnit= allSummonUnit;
	this.approvedSummonUnit1=approvedSummonUnit1;
	this.EsignedSummonUnit1= EsignedSummonUnit1;
	this.PendingSummonUnit1=PendingSummonUnit1;
	
}

public StatusDTO(int srNo, Long unitId, String unitName, int approvedSummonUnit1, int EsignedSummonUnit1, int offlineSummon, int physummon) {
	this.srNo = srNo;
	this.unitId = unitId;
	this.unitName = unitName;
	this.approvedSummonUnit1=approvedSummonUnit1;
	this.EsignedSummonUnit1= EsignedSummonUnit1;
	this.offlineSummon = offlineSummon;
	this.physummon = physummon;
}

public StatusDTO(int count, Long unitId ,String unitName2, int allSummonUnit, int approvedSummonUnit1, 
		int EsignedSummonUnit1, int PendingSummonUnit1,int offlineSummon, int pendingPhySummon,
		int physummon , int phySummonYetApprove , int summonSensitive , int totalSigned) {
	this.srNo= count;
	this.unitId = unitId;
	this.unitName=unitName2;
	this.allSummonUnit=allSummonUnit;
	this.approvedSummonUnit1=approvedSummonUnit1;
	this.EsignedSummonUnit1=EsignedSummonUnit1;
	this.PendingSummonUnit1 = PendingSummonUnit1;
	this.offlineSummon = offlineSummon;
	this.pendingPhySummon=pendingPhySummon;
	this.physummon = physummon;
	this.phySummonYetApprove =phySummonYetApprove;
	this.summonSensitive = summonSensitive;
	this.totalSigned = totalSigned;
}

public StatusDTO(int count, Long unitId, String unitName, int allNoticeUnit1, int approvedNoticeUnit1, int EsignedNoticeUnit1, int PendingNoticeUnit1 ,int pendingPysicallySendNOtice, int approvePhyNotice ,int allSummonUnit,int approvedSummonUnit1,int EsignedSummonUnit1,int PendingSummonUnit1 ,int offlineSummon,int physummon) {
	this.srNo = count;
	this.unitId = unitId;
	this.unitName = unitName;
	this.allNoticeUnit1= allNoticeUnit1;
	this.approvedNoticeUnit1 =approvedNoticeUnit1;
	this.EsignedNoticeUnit1 = EsignedNoticeUnit1;
	this.PendingNoticeUnit1 = PendingNoticeUnit1;
	this.pendingPysicallySendNOtice =pendingPysicallySendNOtice;
	this.phynotice = approvePhyNotice;
	this.allSummonUnit= allSummonUnit;
	this.approvedSummonUnit1=approvedSummonUnit1;
	this.EsignedSummonUnit1= EsignedSummonUnit1;
	this.PendingSummonUnit1=PendingSummonUnit1;
	this.offlineSummon = offlineSummon;
	this.physummon = physummon;
}

public StatusDTO(int count, Long unitId, String unitName, int allNoticeUnit1, int approvedNoticeUnit1, int EsignedNoticeUnit1, int PendingNoticeUnit1 ,int pendingPysicallySendNOtice, int approvePhyNotice ,int allSummonUnit,int approvedSummonUnit1,int EsignedSummonUnit1,int PendingSummonUnit1 ,int offlineSummon,int physummon,int pendingPhySummon) {
	this.srNo = count;
	this.unitId = unitId;
	this.unitName = unitName;
	this.allNoticeUnit1= allNoticeUnit1;
	this.approvedNoticeUnit1 =approvedNoticeUnit1;
	this.EsignedNoticeUnit1 = EsignedNoticeUnit1;
	this.PendingNoticeUnit1 = PendingNoticeUnit1;
	this.pendingPysicallySendNOtice =pendingPysicallySendNOtice;
	this.phynotice = approvePhyNotice;
	this.allSummonUnit= allSummonUnit;
	this.approvedSummonUnit1=approvedSummonUnit1;
	this.EsignedSummonUnit1= EsignedSummonUnit1;
	this.PendingSummonUnit1=PendingSummonUnit1;
	this.offlineSummon = offlineSummon;
	this.physummon = physummon;
	this.pendingPhySummon=pendingPhySummon;
}

public StatusDTO(int srNo, String caseId, String caseTitle, int allSummonUnit, int approvedSummonUnit1, int EsignedSummonUnit1, int PendingSummonUnit1, int offlineSummon,
		int physummon, int pendingPhySummon) {
	this.srNo = srNo;
	this.caseId= caseId;
	this.caseTittle=caseTitle;
	this.allSummonUnit = allSummonUnit;
	this.approvedSummonUnit1 = approvedSummonUnit1;
	this.EsignedSummonUnit1 = EsignedSummonUnit1;
	this.PendingSummonUnit1 = PendingSummonUnit1;
	this.offlineSummon = offlineSummon;
	this.physummon = physummon;
	this.pendingPhySummon = pendingPhySummon;
}

public StatusDTO(int srNo, String caseId, String caseTitle, int allNoticeUnit1, int approvedNoticeUnit1, int EsignedNoticeUnit1, int PendingNoticeUnit1, int pendingPysicallySendNOtice,
		int phynotice) {

      this.srNo = srNo;
      this.caseId=caseId;
      this.caseTittle=caseTitle;
      this.allNoticeUnit1 = allNoticeUnit1;
      this.approvedNoticeUnit1 = approvedNoticeUnit1;
      this.EsignedNoticeUnit1 = EsignedNoticeUnit1;
      this.PendingNoticeUnit1 = PendingNoticeUnit1;
      this.pendingPysicallySendNOtice=pendingPysicallySendNOtice;
      this.phynotice = phynotice;
      
      
}



/*
 * public StatusDTO(int srNo, Long unitId, String unitName, int allSummonUnit,
 * int approvedSummonUnit1, int EsignedSummonUnit1, int PendingSummonUnit1, int
 * offlineSummon, int physummon, int pendingPhySummon) { this.srNo = srNo;
 * this.unitId = unitId; this.unitName = unitName; this.allSummonUnit =
 * allSummonUnit; this.approvedSummonUnit1 = approvedSummonUnit1;
 * this.EsignedSummonUnit1 = EsignedSummonUnit1; this.PendingSummonUnit1 =
 * PendingSummonUnit1; this.offlineSummon = offlineSummon; this.physummon =
 * physummon; this.pendingPhySummon = pendingPhySummon;
 * 
 * }
 */



public StatusDTO(int count, Long unitId, String unitName, int allNoticeUnit1, int approvedNoticeUnit1, int EsignedNoticeUnit1, int PendingNoticeUnit1 
		,int pendingPysicallySendNOtice, int approvePhyNotice , int phynoticeYetApprove  ) {
	 this.srNo = count;
	 this.unitId = unitId;
	 this.unitName = unitName;
	 this.allNoticeUnit1 = allNoticeUnit1;
	 this.approvedNoticeUnit1= approvedNoticeUnit1;
	 this.EsignedNoticeUnit1 = EsignedNoticeUnit1;
	 this.PendingNoticeUnit1 = PendingNoticeUnit1;
	 this.pendingPysicallySendNOtice =pendingPysicallySendNOtice;
	 this.phynotice =approvePhyNotice;
	 this.phynoticeYetApprove =phynoticeYetApprove;
	 
}



private int srNo;
	private  int allNoticeUnit1;
	  private int approvedNoticeUnit1;
	  private int EsignedNoticeUnit1;
	  private int PendingNoticeUnit1;
	  private  int allSummonUnit;
	  private int approvedSummonUnit1;
	  private int EsignedSummonUnit1;
	  private int PendingSummonUnit1; 
      private int offlineSummon;
	  private String caseId;
	  private String caseTittle;
	  private int physummon;
      private int pendingPhySummon; 
      private int pendingPysicallySendNOtice;
      private int phynotice;
      private int phynoticeYetApprove;
      private int phySendReview;
      private int phySummonYetApprove;
      private int summonSensitive;
  private UnitDetails unit;
  
  private Long unitId;
  private String unitName;
  private int totalSigned;

	/*
	 * private NoticeStatus allNoticeUnit1; private NoticeStatus
	 * approvedNoticeUnit1; private NoticeStatus EsignedNoticeUnit1; private
	 * NoticeStatus PendingNoticeUnit1;
	 */
  
	
}
