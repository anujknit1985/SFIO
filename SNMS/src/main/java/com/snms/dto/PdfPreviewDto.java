package com.snms.dto;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class PdfPreviewDto {
	 
	private Long id;
	private String company;
	private String type;
	private int sumtypeId;
	private String appearDate;
	private String summonNo;
	
	private String IssueDate;
	private MultipartFile SummonFile;
	private String summonDin;
	
	
	public PdfPreviewDto(Long id, String company, String type) {
		super();
		this.id = id;
		this.company = company;
		this.type = type;
	}
	public PdfPreviewDto(Long id, String company, String type, int sumtypeId,String appearDate,String summonNo) {
		super();
		this.id = id;
		this.company = company;
		this.type = type;
		this.sumtypeId = sumtypeId;
		this.appearDate=appearDate;
		this.summonNo = summonNo;
		
		
	}
	public PdfPreviewDto(Long id, String company, String type, int sumtypeId, String appearDate,
			String summonNo ,String IssueDate, String summonDin) {
		super();
		this.id = id;
		this.company = company;
		this.type = type;
		this.sumtypeId = sumtypeId;
		this.appearDate=appearDate;
		this.summonNo = summonNo;
		this.summonDin = summonDin;
		this.IssueDate = IssueDate;
		
	}
	
}
