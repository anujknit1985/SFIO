package com.snms.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@Table(name = "investigation_status", schema = "gams")
public class InvestigationStatus {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "Investigation_status_id  ", columnDefinition = "serial")
	private Long id;
	@Column(name = "Investigation_status")
	private String statusName;
	@Column(name = "status_type")
	private String type;
    @Transient
    private Boolean editstatus;
}
