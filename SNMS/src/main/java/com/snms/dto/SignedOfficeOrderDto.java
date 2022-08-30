package com.snms.dto;

import lombok.Data;

@Data
public class SignedOfficeOrderDto {
	
	private String caseStringId;
	private String caseTitle;
	private Long orderid;
	private String signDate;
	private String summonNo;
	private String companyName;
	private String indivi_name;
	private Long id;
	private String oo_din;
	
	private String order_type;
	public SignedOfficeOrderDto(String caseStringId, String caseTitle, Long orderid, String signDate, String filename) {
		super();
		this.caseStringId = caseStringId;
		this.caseTitle = caseTitle;
		this.orderid = orderid;
		this.signDate = signDate;
		
	}
	
	public SignedOfficeOrderDto(String caseStringId, String caseTitle, Long orderid, String signDate, String filename,Long id,String order_type) {
		super();
		this.caseStringId = caseStringId;
		this.caseTitle = caseTitle;
		this.orderid = orderid;
		this.signDate = signDate;
		this.id = id;
		this.order_type=order_type;
	}
	public SignedOfficeOrderDto(String caseStringId, String caseTitle, Long orderid, String signDate, String filename,Long id,String order_type,String oo_din) {
		super();
		this.caseStringId = caseStringId;
		this.caseTitle = caseTitle;
		this.orderid = orderid;
		this.signDate = signDate;
		this.id = id;
		this.order_type=order_type;
		this.oo_din = oo_din;
	}
	public SignedOfficeOrderDto(String caseStringId, String caseTitle, Long orderid, String signDate, String filename, String companyName, String name)
	{
		super();
		this.caseStringId = caseStringId;
		this.caseTitle = caseTitle;
		this.orderid = orderid;
		this.signDate = signDate;
		this.companyName=companyName;
		this.indivi_name=name;
	}
	public SignedOfficeOrderDto(String caseStringId, String caseTitle, Long orderid, String signDate, String filename,
			String summonNo) {
		super();
		this.caseStringId = caseStringId;
		this.caseTitle = caseTitle;
		this.orderid = orderid;
		this.signDate = signDate;
		this.summonNo = summonNo;
	}
	
	
}
