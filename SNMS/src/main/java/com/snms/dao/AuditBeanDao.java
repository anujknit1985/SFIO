package com.snms.dao;

import java.util.Date;
import java.util.List;

import com.snms.entity.AuditTrail;

public interface AuditBeanDao {

	void saveAuditTrail(AuditTrail auditTrail);

	List<AuditTrail> findAllByActionDate(Date date1);

	List<AuditTrail> findAllByActionDateBetween(String dateStr, String toDateStr);
	

}
