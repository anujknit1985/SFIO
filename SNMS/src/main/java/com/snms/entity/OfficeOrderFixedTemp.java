package com.snms.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@Table(name = "OfficeOrderFixedTemp", schema = "investigation")
public class OfficeOrderFixedTemp {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id",columnDefinition = "serial")
	private int id;
	
	private String director;
	
	@Lob
	private String para1;
	@Lob
	private String para2;
	@Lob
	private String para3;
	@Lob
	private String para4;
	@Lob
	private String para4_1;
	@Lob
	private String para5;
	
	
	public OfficeOrderFixedTemp(String director, String para1, String para2, String para3, String para4, String para4_1,
			String para5) {
		super();
		this.director = director;
		this.para1 = para1;
		this.para2 = para2;
		this.para3 = para3;
		this.para4 = para4;
		this.para4_1 = para4_1;
		this.para5 = para5;
	}

}
