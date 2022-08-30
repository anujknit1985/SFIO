package com.snms.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Data
@NoArgsConstructor
@Table(name = "Designation", schema = "authentication", uniqueConstraints = {
		@UniqueConstraint(name = "DESIGNATION_UK", columnNames = "designation") })


public class AddDesignation {
	public AddDesignation(Long id) {
		this.id = id;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", columnDefinition = "serial")
	private Long id;

	@Column(name = "designation", length = 40, nullable = false)
	@NotNull
//	@Size(min = 5, max = 39)
	@Pattern(regexp="^[a-zA-Z ]{2,39}",message="Designation must be in alphanumeric with length ranging 2-40")
	private String designation;
	
	private String type;
	@Pattern(regexp="^[a-zA-Z ]{2,39}",message="Designation must be in alphanumeric with length ranging 2-40")
	private String description;

	@Column(name = "desig_order", length = 2, nullable = true)
	private Integer order;
     
	@Column(name = "createdDate")
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdDate;
	
	
	@Column(name = "updatedDate")
	@Temporal(TemporalType.TIMESTAMP)
	private Date updatedDate;
	
	@Transient
	private Boolean editdesgi;

}
