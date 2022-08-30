package com.snms.dto;

import java.util.Date;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor 
public class OfficeOrderDto {
	
	private String orderDate;
	private String oderId;
	private List<String> inspectors;
	private String io;
	private List<String> company;
	private String director;
	private String clause;
	private String mcaOrderNo;
	
	private String para1;
	private String para2;
	private String para3;
	private String para4;
	private String para5;
	private Date approvalDate; 
	
	public OfficeOrderDto(String orderDate, String oderId, List<String> inspectors, String io, List<String> company,
			String director, String para1, String para2, String para3, String para4, String para5) {
		super();
		this.orderDate = orderDate;
		this.oderId = oderId;
		this.inspectors = inspectors;
		this.io = io;
		this.company = company;
		this.director = director;
		this.para1 = para1;
		this.para2 = para2;
		this.para3 = para3;
		this.para4 = para4;
		this.para5 = para5;
	}
	
	public OfficeOrderDto(String orderDate, String oderId, List<String> inspectors, String io, List<String> company,
			String director, String para1, String para2, String para3, String para4, String para5,Date approvalDate) {
		super();
		this.orderDate = orderDate;
		this.oderId = oderId;
		this.inspectors = inspectors;
		this.io = io;
		this.company = company;
		this.director = director;
		this.para1 = para1;
		this.para2 = para2;
		this.para3 = para3;
		this.para4 = para4;
		this.para5 = para5;
		this.approvalDate = approvalDate;
	}

	public OfficeOrderDto(String orderDate, String oderId, List<String> inspectors, String io, List<String> company,
			String director,String clause,String mcaOrderNo) {
		super();
		this.orderDate = orderDate;
		this.oderId = oderId;
		this.inspectors = inspectors;
		this.io = io;
		this.company = company;
		this.director = director;
		this.clause = clause;
		this.mcaOrderNo=mcaOrderNo;
	}

}
