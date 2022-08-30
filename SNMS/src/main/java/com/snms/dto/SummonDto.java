package com.snms.dto;

import java.util.Date;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor 
public class SummonDto {
	
	private String orderDate;
	private String oderId;
	//private List<String> inspectors;
	private String io;
	
	//private List<String> company;
	
	private String companyName;
	private String companyAddress;
	
	private String designation;
	private String companyDir;
	private String reportingDate;
	private int size;
	private int remainInsSize;
	private String mcaOrderNo;
	
	private String para1;
	private String para2;
	private String para3;
	private String para4;
	private String para5;
	private String para6;
	private Date approvalDate;
	
	private String officeAddress;
	private String comapnyPara;
	
	private String issueDate;
	private String summonDin;
	private String IODesignation;
	private boolean isOffline;
	public SummonDto(String orderDate, String oderId, String io, String companyName, String companyAddress,
			String designation, String companyDir,String para1,String para2,String para3,String para4, String para5, String para6 ,String officeAddress, String comapnyPara ,String iodesignation) {
		super();
		this.orderDate = orderDate;
		this.oderId = oderId;
		this.io = io;
		this.companyName = companyName;
		this.companyAddress = companyAddress;
		this.designation = designation;
		this.companyDir = companyDir;
		this.para1 = para1;
		this.para2 = para2;
		this.para3 = para3;
		this.para4 = para4;
		this.para5 = para5;
		this.para6 = para6;
		this.officeAddress = officeAddress ;
		this.comapnyPara = comapnyPara;
		this.IODesignation = iodesignation;
		
	}
	
	public SummonDto(String orderDate, String oderId, String io, String companyName, String companyAddress,
			String designation, String companyDir,String para1,String para2,String para3,String para4, String para5, String para6,Date approvalDate, String IODesignation) {
		super();
		this.orderDate = orderDate;
		this.oderId = oderId;
		this.io = io;
		this.companyName = companyName;
		this.companyAddress = companyAddress;
		this.designation = designation;
		this.companyDir = companyDir;
		this.para1 = para1;
		this.para2 = para2;
		this.para3 = para3;
		this.para4 = para4;
		this.para5 = para5;
		this.para6 = para6;
		this.approvalDate = approvalDate;
		this.IODesignation = IODesignation;
		
	}

	public SummonDto(String orderDate, String oderId, String io, String companyName, String companyAddress,
			String designation, String companyDir, String reportingDate,int size,int remainInsSize,String mcaOrderNo) {
		super();
		this.orderDate = orderDate;
		this.oderId = oderId;
		this.io = io;
		this.companyName = companyName;
		this.companyAddress = companyAddress;
		this.designation = designation;
		this.companyDir = companyDir;
		this.reportingDate=reportingDate;
		this.size = size;
		this.remainInsSize=remainInsSize;
		this.mcaOrderNo=mcaOrderNo;
	}

	public SummonDto(String orderDate, String oderId, String io, String companyName, String companyAddress,
			String designation, String companyDir, String reportingDate,int size,int remainInsSize,String mcaOrderNo, String officeAddress, String comapnyPara, String IODesignation) {
				super();
				this.orderDate = orderDate;
				this.oderId = oderId;
				this.io = io;
				this.companyName = companyName;
				this.companyAddress = companyAddress;
				this.designation = designation;
				this.companyDir = companyDir;
				this.reportingDate=reportingDate;
				this.size = size;
				this.remainInsSize=remainInsSize;
				this.mcaOrderNo=mcaOrderNo;
				this.officeAddress =officeAddress;
				this.comapnyPara =comapnyPara;
				this.IODesignation = IODesignation;
	}

	public SummonDto(String orderDate, String oderId, String io, String companyName, String companyAddress,
			String designation, String companyDir, String para1, String para2, String para3, String officeAddress,
			String comapnyPara , String IODesignation) {
		super();
		this.orderDate = orderDate;
		this.oderId = oderId;
		this.io = io;
		this.companyName = companyName;
		this.companyAddress = companyAddress;
		this.designation = designation;
		this.companyDir = companyDir;
		this.para1 = para1;
		this.para2 = para2;
		this.para3 = para3;
		
		this.officeAddress = officeAddress ;
		this.comapnyPara = comapnyPara;
		this.IODesignation = IODesignation;
	}

	public SummonDto(String orderDate, String oderId, String io, String companyName, String summonDin,
			String issueDate, String companyAddress, String designation, String companyDir, String reportingDate,
			int size, int remainInsSize, String mcaOrderNo, String officeAddress, String comapnyPara ,String IODesignation) {
		super();
		this.orderDate = orderDate;
		this.oderId = oderId;
		this.io = io;
		this.companyName = companyName;
		this.summonDin = summonDin;
		this.issueDate = issueDate;
		this.companyAddress = companyAddress;
		this.designation = designation;
		this.companyDir = companyDir;
		this.reportingDate=reportingDate;
		this.size = size;
		this.remainInsSize=remainInsSize;
		this.mcaOrderNo=mcaOrderNo;
		this.officeAddress =officeAddress;
		this.comapnyPara =comapnyPara;
		this.IODesignation = IODesignation;
	}

	

	
}
