package com.snms.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Data
@Table(name = "ICAI", schema = "public")
public class ICAI 

{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	private String name_of_firm;
	private String category;
	private String registartion_no;
	private String email;
	private String telephone_no;
	private String address_line_1;
	private String address_line_2;
	private String address_line_3;
	private String address_line_4;
	private String city_name;
	private String pin_code;
	private String type;
	private String no_of_partner;
	private String member_name;
	private String membership_no;
	
	
}
