package com.snms.dto;

import java.util.Date;
import java.util.List;

import lombok.Data;

@Data 
public class NoticeDto {
	
	private String orderDate;
	private String oderId;
	private String io;
	private List<String> companylist;
	private String company;
	private String companyAddress;
	private String reportingDate;
	private int size;
	private String designation;
	private String companyDir;
	private String para1;
	private String para2;
	private String para3;
	private String para4;
	private String para5;
	private String companyPara;
	private Date approvalDate;
	private String officeAddress;
	private String IODesignation;
	
	public NoticeDto(String orderDate, String oderId, String io, List<String> companylist, String company,
			String companyAddress, String reportingDate, int size, String para1, String para2, String para3,
			String para4, String para5, String IODesignation) {
		super();
		this.orderDate = orderDate;
		this.oderId = oderId;
		this.io = io;
		this.companylist = companylist;
		this.company = company;
		this.companyAddress = companyAddress;
		this.reportingDate = reportingDate;
		this.size = size;
		this.para1 = para1;
		this.para2 = para2;
		this.para3 = para3;
		this.para4 = para4;
		this.para5 = para5;
		this.IODesignation = IODesignation;
	}
	public NoticeDto(String caseId, String currentDate, String para12, String para22, String para32, String para42,
			String para52, long l, String caseId2, Long id, String string, List<String> companyNames, String company2,
			String companyAdd) {
		// TODO Auto-generated constructor stub
	}
	public NoticeDto(String orderDate, String oderId, String io, List<String> companylist, String company,
			String companyAddress, String reportingDate, int size,String companyDir,String designation,String companyPara, String officeAddresss ,String IODesignation ) {
		this.orderDate = orderDate;
		this.oderId = oderId;
		this.io = io;
		this.companylist = companylist;
		this.company = company;
		this.companyAddress = companyAddress;
		this.reportingDate = reportingDate;
		this.size = size;
		this.companyDir = companyDir;
		this.designation = designation;
		this.companyPara=companyPara;
		this.officeAddress = officeAddresss;
		this.IODesignation = IODesignation;
	}
	
	

}
