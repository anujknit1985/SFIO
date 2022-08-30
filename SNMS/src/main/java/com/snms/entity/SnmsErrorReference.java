package com.snms.entity;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.snms.utils.SnmsConstant;

@Entity
@Table(name="snms_error_reference",schema="public")
public class SnmsErrorReference extends PersistenceEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String errorCode;
	@Column(columnDefinition = "text")
	private String errorMessage;
	private String errorTime = new SimpleDateFormat(SnmsConstant.DATE_FORMAT).format(new Date());

	////////////////////////////////////////////////////////////////////////
	// getter METHODS
	////////////////////////////////////////////////////////////////////////

	public Long getId() {
		return id;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public String getErrorTime() {
		return errorTime;
	}
	////////////////////////////////////////////////////////////////////////
	// setter METHODS
	////////////////////////////////////////////////////////////////////////

	public void setId(Long id) {
		this.id = id;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public void setErrorTime(String errorTime) {
		this.errorTime = errorTime;
	}

}
