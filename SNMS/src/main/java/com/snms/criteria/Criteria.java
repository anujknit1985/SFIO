package com.snms.criteria;

import java.util.List;

import com.snms.entity.NoticeStatus;

public interface Criteria {
	  public List<NoticeStatus> meetCriteria(List<NoticeStatus> noticeStatus);
}
