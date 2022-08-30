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
import javax.validation.constraints.Size;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data @NoArgsConstructor
@Table(name = "App_Role", schema = "authentication", uniqueConstraints = {
		@UniqueConstraint(name = "APP_ROLE_UK", columnNames = "Role_Name") })
public class AppRole {

	public AppRole(Long roleId) {
		this.roleId = roleId;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "Role_Id",columnDefinition = "serial")
	private Long roleId;

	@Column(name = "Role_Name", length = 30, nullable = false)
	@NotNull
	@Pattern(regexp="^[a-zA-Z_]{2,40}",message="Role name must be in alphanumeric with length ranging 2-40")
	private String roleName;
	
}
