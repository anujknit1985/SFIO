//package com.snms.entity;
//
//import java.util.Date;
//
//import javax.persistence.Column;
//import javax.persistence.Entity;
//import javax.persistence.GeneratedValue;
//import javax.persistence.Id;
//import javax.persistence.Table;
//import javax.persistence.Temporal;
//import javax.persistence.TemporalType;
//import javax.validation.constraints.NotNull;
//import javax.validation.constraints.Size;
//
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//
////@Entity
//@Data @NoArgsConstructor
//@Table(name = "caseDetails", schema = "investigation")
//public class InitiateCase {
//	
//	@Id
//	@GeneratedValue
//	@Column(name = "id", nullable = false)
//	private Long id;
//	
//	@Column(name = "caseId", length = 70, nullable = false)
//	private String caseId;
//	
//	@Column(name = "fromYear", nullable = false)
//	private int fromYear;
//	
//	@Column(name = "toYear", nullable = false)
//	private int toYear;
//	
//	@Column(name = "caseName", length = 200, nullable = false)
//	@Size(min=10, max=195)
//	private String caseName;
//	
//	@Column(name = "caseSummary", length = 500, nullable = false)
//	private String caseSummary;
//	
//	@Column(name = "mcaDoc", length = 70, nullable = false)
//	@NotNull
//	private String mcaDoc;
//	
//	@Column(name = "sfioDoc", length = 70, nullable = false)
//	@NotNull
//	private String sfioDoc;
//	
//	@Column(name = "uploadedDate")
//	@Temporal(TemporalType.TIMESTAMP)
//    private Date uploadedDate;
//
//}
