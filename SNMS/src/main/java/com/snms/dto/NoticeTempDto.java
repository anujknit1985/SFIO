package com.snms.dto;

import java.util.Date;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor
public class NoticeTempDto {

	private String caseNo;
	private String date;
	
	private String para1;
	private String para2;
	private String para3;
	private String para4;
	private String para5;
	
	private String para1h;
	private String para2h;
	private String para3h;
	private String para4h;
	private String para5h;
	
//	private String para6;
	
	private Long noticeId;
	private String caseStrId;
	private Long caseId;
	private String Io;

	private List<String> companylist;
	private String company;
	private String companyAddress;
	private String designation;
	private String designation_hi;
	private String name;
	private String email;
	private String summonNo;
	private String dateOfAppear;
	private String isSensitive;
	private int summontypeid;
	private String noticeDin;
	private Date approvalDate;
	
	private String IODesignation;
	
	private String comapnyPara;
	private String officeAddress;
	public NoticeTempDto(String caseNo, String date, String para1, String para2, String para3, String para4,
			String para5, Long noticeId, String caseStrId, Long caseId, String io,String summonNo,String dateOfAppear,int summontypeid,String isSensitive,String Designation) {
		super();
		this.caseNo = caseNo;
		this.date = date;
		this.para1 = para1;
		this.para2 = para2;
		this.para3 = para3;
		this.para4 = para4;
		this.para5 = para5;
		this.noticeId = noticeId;
		this.caseStrId = caseStrId;
		this.caseId = caseId;
		this.Io = io;
		this.summonNo=summonNo;
		this.dateOfAppear=dateOfAppear;
		this.summontypeid = summontypeid;
		this.isSensitive = isSensitive;
		this.designation = Designation;
		
	}
	
	
	public NoticeTempDto(String caseNo, String date, String para1, String para2, String para3, String para4,
			String para5, String para1h, String para2h, String para3h, String para4h,
			String para5h, Long noticeId, String caseStrId, Long caseId, String io,String summonNo,String dateOfAppear,int summontypeid,String isSensitive,String Designation,String designation_hi) {
		super();
		this.caseNo = caseNo;
		this.date = date;
		this.para1 = para1;
		this.para2 = para2;
		this.para3 = para3;
		this.para4 = para4;
		this.para5 = para5;
		this.para1h = para1h;
		this.para2h = para2h;
		this.para3h = para3h;
		this.para4h = para4h;
		this.para5h = para5h;
		this.noticeId = noticeId;
		this.caseStrId = caseStrId;
		this.caseId = caseId;
		this.Io = io;
		this.summonNo=summonNo;
		this.dateOfAppear=dateOfAppear;
		this.summontypeid = summontypeid;
		this.isSensitive = isSensitive;
		this.designation = Designation;
		this.designation_hi=designation_hi;
		
	}
	public NoticeTempDto(String caseNo, String date, String para1, String para2, String para3, String para4,
			String para5, String io, List<String> companylist,
			String company, String companyAddress,String designation, String name,String email) {
		super();
		this.caseNo = caseNo;
		this.date = date;
		this.para1 = para1;
		this.para2 = para2;
		this.para3 = para3;
		this.para4 = para4;
		this.para5 = para5;
		Io = io;
		this.companylist = companylist;
		this.company = company;
		this.companyAddress = companyAddress;
		this.designation=designation;
		this.name=name;
		this.email=email;
	}
	
	public NoticeTempDto(String caseNo, String date, String para1, String para2, String para3, String para4,
			String para5, String io, List<String> companylist,
			String company, String companyAddress,String designation, String name,String email,Date approvalDate,String IODesignation) {
		super();
		this.caseNo = caseNo;
		this.date = date;
		this.para1 = para1;
		this.para2 = para2;
		this.para3 = para3;
		this.para4 = para4;
		this.para5 = para5;
		Io = io;
		this.companylist = companylist;
		this.company = company;
		this.companyAddress = companyAddress;
		this.designation=designation;
		this.name=name;
		this.email=email;
		this.approvalDate = approvalDate;
		this.IODesignation=IODesignation;
	}
	public NoticeTempDto(String caseNo, String date, String para1, String para2, String para3,
			String para4, String para5, String io, String comapnyPara, String company, String companyAddress,
			String designation, String name, String email, String officeAddress ,String IODesignation) {
		
		
		super();
		this.caseNo = caseNo;
		this.date = date;
		this.para1 = para1;
		this.para2 = para2;
		this.para3 = para3;
		this.para4 = para4;
		this.para5 = para5;
		Io = io;
		this.comapnyPara = comapnyPara;
		this.company = company;
		this.companyAddress = companyAddress;
		this.designation=designation;
		this.name=name;
		this.email=email;
		this.officeAddress =officeAddress;
		this.IODesignation = IODesignation;
		
		// TODO Auto-generated constructor stub
	}
	
	
}
