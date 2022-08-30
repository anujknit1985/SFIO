package com.snms.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class StatusMaster {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long  id ;
	
	private String  Functionality;
	private  Long   Status ;

	private String  StatusDesc;
	
}
