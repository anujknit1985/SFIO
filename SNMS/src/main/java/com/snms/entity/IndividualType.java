package com.snms.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import lombok.Data;
import lombok.NoArgsConstructor;


	@Entity
	@Data @NoArgsConstructor
	@Table(name = "Individual_Type", schema = "investigation", uniqueConstraints = {
			@UniqueConstraint(name = "Individual_Type_UK", columnNames = "Individual_Name") })
	public class IndividualType {
		@Id
		@GeneratedValue(strategy = GenerationType.IDENTITY)
		@Column(name = "Individual_Id",columnDefinition = "serial")
		private Long individualId;

		public IndividualType(Long individualId) {
			
			this.individualId = individualId;
		}

		@Column(name = "Individual_name", length = 30, nullable = false)
		@NotNull
		@Pattern(regexp="^[a-zA-Z_]{2,40}",message="Individual Type name must be in alphanumeric with length ranging 2-40")
		private String individualName;
	
}
