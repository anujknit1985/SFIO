package com.snms.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Data;

@Data
public class PersonDATA {


public PersonDATA(String companyName, String cin, String case_id, String individual_name ,int summonType_id ,String name) {
		this.companyName = companyName;
		this.cin = cin ;
		this.case_id = case_id;
		this.individual_name = individual_name;
		this.summonType_id = summonType_id;
		this.name = name;
	}

/*
select 
st.registration_no,
st.name,
st.email,
st.passport,
st.pan_number,
st.dob,
st.mobile_no,
it.individual_name
*/ 
public PersonDATA(String name,String email,String passport,String pan,
		String individual_name,int summonType_id) {
	this.name = name;
	this.email=email;
	this.passportNumber= passport;
	this.panNumber=pan;
	
	
	this.individual_name = individual_name;
	this.summonType_id = summonType_id;
	
}
private String companyName;
private String cin ;
private String case_id;
private String individual_name;
private int  summonType_id;
private String name;
private String passportNumber;
private String panNumber;
private Date dob;
private String email;
private String Gender;
private String fatherName;
private String address;
private String primaryMobile;
private String alternateNo;
private Date passportDate;
private String din;

private int personID;

private String status;


public PersonDATA(String name,String passport,String pan,
		String status,int personID) {
	this.name = name;
	this.passportNumber= passport;
	this.panNumber=pan;
	this.status = status;
	
	
}



}
