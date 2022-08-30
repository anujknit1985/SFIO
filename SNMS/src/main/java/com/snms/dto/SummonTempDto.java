package com.snms.dto;

import javax.persistence.Lob;
import javax.persistence.Transient;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class SummonTempDto {
	
	private String caseNo;
	private String date;
	
	private String para1;
	private String para2;
	private String para3;
	private String para4;
	private String para5;
	private String para6;
	
	
	private String para1h;
	private String para2h;
	private String para3h;
	private String para4h;
	private String para5h;
	private String para6h;
	
	private Long summonId;
	private String caseStrId;
	private Long caseId;
	private String directorName;
	private String summonNo;
	private String dateOfAppear;
	private String isSensitive;	
	private int summontypeid;
	private String summonDin;
	private String dateOfIssue;
	private MultipartFile summonOfflineFile;
	private  String isOfline;
	private String designation ; 
	private String designation_Hi ; 
	  @Lob @Transient private String para3_1;
	  @Lob @Transient private String para3_1h;
		 
	
	public SummonTempDto(String caseNo, String date, String para1, String para2, String para3, String para4,
			String para5, String para6, long summonId, String caseStrId, Long caseId, String directorName, String summonNo,
			String dateOfAppear, String isSensitive, int summontypeid, String summonDin ,String Designation) {
		super();
		this.caseNo = caseNo;
		this.date = date;
		this.para1 = para1;
		this.para2 = para2;
		this.para3 = para3;
		this.para4 = para4;
		this.para5 = para5;
		this.para6 = para6;
		this.summonId= summonId;
		this.caseStrId = caseStrId;
		this.caseId = caseId;
		this.directorName = directorName;
		this.summonNo = summonNo;
		this.dateOfAppear = dateOfAppear;
		this.isSensitive = isSensitive;
		this.summontypeid = summontypeid;
		this.summonDin = summonDin;
		this.designation = Designation;
		
		// TODO Auto-generated constructor stub
	}
	
	public SummonTempDto(String caseNo, String date, String para1, String para2, String para3, String para4,
			String para5, String para6, 
			String para1h, String para2h, String para3h, String para4h,
			String para5h, String para6h,
			long summonId, String caseStrId, Long caseId, String directorName, String summonNo,
			String dateOfAppear, String isSensitive, int summontypeid, String summonDin ,String Designation,String designation_Hi) {
		super();
		this.caseNo = caseNo;
		this.date = date;
		this.para1 = para1;
		this.para2 = para2;
		this.para3 = para3;
		this.para4 = para4;
		this.para5 = para5;
		this.para6 = para6;
		this.para1h= para1h;
		this.para2h = para2h;
		this.para3h= para3h;
		this.para4h = para4h;
		this.para5h = para5h;
		this.para6h= para6h;
		this.summonId= summonId;
		this.caseStrId = caseStrId;
		this.caseId = caseId;
		this.directorName = directorName;
		this.summonNo = summonNo;
		this.dateOfAppear = dateOfAppear;
		this.isSensitive = isSensitive;
		this.summontypeid = summontypeid;
		this.summonDin = summonDin;
		this.designation = Designation;
		this.designation_Hi=designation_Hi;
		
		// TODO Auto-generated constructor stub
	}

	public SummonTempDto(String caseNo, String date, String para1, String para2, String para3, long summonId,
			String caseStrId, Long caseId, String directorName, String summonNo, String dateOfAppear, int summontypeid,
			String dateofIssue, String summonDin,String isOfline ,String Designation) {
		
		
		super();
		this.caseNo = caseNo;
		this.date = date;
		this.para1 = para1;
		this.para2 = para2;
		this.para3 = para3;
		
		this.summonId= summonId;
		this.caseStrId = caseStrId;
		this.caseId = caseId;
		this.directorName = directorName;
		this.summonNo = summonNo;
		this.dateOfAppear = dateOfAppear;
		
		this.summontypeid = summontypeid;
		this.dateOfIssue = dateofIssue;
		this.summonDin = summonDin;
		this.isOfline = isOfline;
		this.designation = Designation;
	}

	public SummonTempDto(String caseNo, String date, String para1, String para2, String para3, String para3_1,
			
			 String para1h, String para2h, String para3h, String para3_1h,
			long summonId, String caseStrId, Long caseId, String directorName, String summonNo, String dateOfAppear, int summontypeid,
			String dateofIssue, String summonDin, String isOfline, String Designation) {
		super();
		this.caseNo = caseNo;
		this.date = date;
		this.para1 = para1;
		this.para2 = para2;
		this.para3 = para3;
		this.para3_1 = para3_1;
		
		this.para1h= para1h;
		this.para2h=para2h;
		this.para3h = para3h;
		this.para3_1h=para3_1h;
		
		this.summonId= summonId;
		this.caseStrId = caseStrId;
		this.caseId = caseId;
		this.directorName = directorName;
		this.summonNo = summonNo;
		this.dateOfAppear = dateOfAppear;
		
		this.summontypeid = summontypeid;
		this.dateOfIssue = dateofIssue;
		this.summonDin = summonDin;
		this.isOfline = isOfline;
		this.designation = Designation;
	}



	
}
