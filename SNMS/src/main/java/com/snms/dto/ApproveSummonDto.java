package com.snms.dto;

import java.util.Date;

import lombok.Data;

@Data
public class ApproveSummonDto {

	private String summonNo;
	private String dateOfAppear;
	private Date createdDate;
	private Long id;
	private String case_title;
	private Object din;
	
	private String CompanyName;
	private String Name ;
	private String IndividualId;
	private String Designation;
	private String Registration_no;
	
	

	public ApproveSummonDto(String summonNo, String dateOfAppear, Long id,String case_title,Object din) {
		super();
		this.summonNo = summonNo;
		this.dateOfAppear = dateOfAppear;
		this.id = id;
		this.case_title = case_title;
		this.din = din;
	}



	public ApproveSummonDto(String case_title, String summonNo, String companyName,
			Object din,  String name, String individualId, String designation,
			String registration_no) {
		super();
		this.summonNo = summonNo;
		this.dateOfAppear = dateOfAppear;
		this.createdDate = createdDate;
		this.id = id;
		this.case_title = case_title;
		this.din = din;
		CompanyName = companyName;
		Name = name;
		IndividualId = individualId;
		Designation = designation;
		Registration_no = registration_no;
	}
	
	
}
