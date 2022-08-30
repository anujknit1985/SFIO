package com.snms.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
@Entity
@Data
@Table(name = "country", schema = "public")
public class Country {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	private String country_name;
	
}
