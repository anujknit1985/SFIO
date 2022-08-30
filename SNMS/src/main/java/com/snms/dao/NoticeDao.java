package com.snms.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.thymeleaf.util.StringUtils;

import com.snms.entity.AppUser;
import com.snms.entity.NoticeStatus;
import com.snms.entity.SummonStatus;
import com.snms.entity.SummonType;
import com.snms.service.NoticeRepository;

@Repository
@Transactional
public class NoticeDao {

	@Autowired
	private EntityManager entityManager;
	@Autowired
	private AppRoleDAO roleDAO;
	
	@Autowired
	private NoticeRepository noticeRepo;
	/*
	 * @SuppressWarnings("unchecked") public List<Object[]>
	 * findNoticeApproveByUser(AppUser appUser) { try { String sql =
	 * "select st.summon_no,st.date_of_appear,st.created_date,st.id from investigation.inspector i,investigation.notice_status st where i.isio=true and i.case_details_id=st.case_details_id and i.app_user_user_id=:appUser and st.is_signed=:isSigned and st.aprroval_stage2=true"
	 * ; Query query = entityManager.createNativeQuery(sql);
	 * query.setParameter("appUser", appUser); query.setParameter("isSigned", 0);
	 * return (List<Object[]>) query.getResultList(); } catch (NoResultException e)
	 * { return null; } }
	 */

	@SuppressWarnings("unchecked")
	public List<Object[]> findNoticeApproveByUser() {
		try {
			String sql = "select distinct st.summon_no,st.date_of_appear,st.created_date,st.id,cd.case_title,st.notice_din from investigation.inspector i,investigation.notice_status st,investigation.case_details cd where cd.id=st.case_details_id and i.case_details_id=st.case_details_id and st.is_signed=:isSigned and st.aprroval_stage2=true and st.aprroval_stage1=true";
			Query query = entityManager.createNativeQuery(sql);
			// query.setParameter("appUser", appUser);
			query.setParameter("isSigned", 0);

			List<Object[]> NoticeList = query.getResultList();
			if (!NoticeList.isEmpty()) {
				return NoticeList;
			} else {
				return NoticeList;
			}

		} catch (NoResultException e) {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public List<Object[]> findAllSignedNotices() {
		try {
			String sql = "select cd.case_id,cd.case_title,o.id,o.order_signed_date,o.summon_no from investigation.notice_status o,investigation.case_details cd where cd.id=o.case_details_id and o.is_signed=1";
			Query query = this.entityManager.createNativeQuery(sql);
			return (List<Object[]>) query.getResultList();
		} catch (NoResultException e) {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public List<Object[]> findSignedNotices(AppUser userid) {
		try {
			String sql = "select distinct cd.case_id,cd.case_title,o.id,o.order_signed_date,o.summon_no from investigation.notice_status o,investigation.case_details cd,investigation.inspector i where o.case_details_id =i.case_details_id and cd.id=o.case_details_id and i.app_user_user_id=:userId and o.is_signed=1";
			Query query = this.entityManager.createNativeQuery(sql);
			query.setParameter("userId", userid.getUserId());
			return (List<Object[]>) query.getResultList();
		} catch (NoResultException e) {
			return null;
		}
	}

	public List<Object[]> findNoticeByUser(AppUser userDetails) {
		try {
			String sql = "select distinct st.summon_no,st.date_of_appear,st.created_date,st.id,cd.case_title,st.notice_din from investigation.inspector i,investigation.notice_status st,investigation.case_details cd where cd.id=st.case_details_id and i.case_details_id=st.case_details_id and st.is_signed=:isSigned and st.aprroval_stage2=true and i.app_user_user_id=:userId";
			Query query = entityManager.createNativeQuery(sql);
			// query.setParameter("appUser", appUser);
			query.setParameter("isSigned", 0);
			if (userDetails != null) {
				query.setParameter("userId", userDetails.getUserId());
			} else {
				query.setParameter("userId", new AppUser().getUserId());
			}

			return (List<Object[]>) query.getResultList();
		} catch (NoResultException e) {
			return null;
		}
	}

	public List<Object[]> findNoticeApproveByUserNew(AppUser userDetails) {
		try {
			// String sql = "select
			// cd.case_id,cd.case_title,o.id,o.order_signed_date,o.summon_no from
			// investigation.summon_status o,investigation.case_details
			// cd,investigation.inspector i where cd.id=o.case_details_id and
			// i.app_user_user_id=:userId and o.is_signed=1";
			// String sql= "select distinct
			// cd.case_id,cd.case_title,o.id,o.order_signed_date,o.summon_no ,
			// com.company_name,
			// tot.name,tot.reg_no,tot.designation,tot.phone,tot.email,tot.address,tot.type
			// from investigation.case_details cd inner join investigation.inspector i on
			// cd.id =i.case_details_id inner join investigation.summon_status o on
			// o.case_details_id =i.case_details_id inner join public.getdata_total() tot on
			// o.summon_type_id=tot.id inner join investigation.company_summon com on com.id
			// =tot.entity_summontype where i.app_user_user_id=:userId and o.is_signed=1";
			// edit by gouthami

			String sql = "select distinct cd.case_id,cd.case_title,o.id,o.order_signed_date,o.summon_no ,\r\n"
					+ " com.company_name, tot.name,tot.registration_no,tot.designation,tot.mobile_no,tot.email,tot.address,tot.individual_type_individual_id\r\n"
					+ " from investigation.case_details cd   inner join investigation.inspector i\r\n"
					+ "  on cd.id =i.case_details_id  inner join  investigation.summon_status o \r\n"
					+ " on o.case_details_id =i.case_details_id\r\n"
					+ "  inner join investigation.summon_type tot on o.summon_type_id=tot.id inner join investigation.company_summon com on com.id =tot.entity_summontype\r\n"
					+ "   where   i.app_user_user_id=:userId and o.is_signed=0";
			Query query = this.entityManager.createNativeQuery(sql);
			if (userDetails != null) {
				query.setParameter("userId", userDetails.getUserId());
			} else {
				query.setParameter("userId", new AppUser().getUserId());
			}

			return (List<Object[]>) query.getResultList();
		} catch (NoResultException e) {
			return null;
		}
	}

	public List<NoticeStatus> findNoticeByUserId(AppUser userDetails) {
		try {
			String nativesql = "select distinct st.id from investigation.inspector i,investigation.notice_status st,investigation.case_details cd where cd.id=st.case_details_id and i.case_details_id=st.case_details_id and st.is_signed=:isSigned and st.aprroval_stage2=true and i.app_user_user_id=:userId and  i.is_active = true";

			Query nativequery = entityManager.createNativeQuery(nativesql);
			nativequery.setParameter("isSigned", 0);
			if (userDetails != null) {
				nativequery.setParameter("userId", userDetails.getUserId());
			} else {
				nativequery.setParameter("userId", new AppUser().getUserId());
			}
			List listId = nativequery.getResultList();
			if (listId.isEmpty())
				return new ArrayList<NoticeStatus>();
			String sql = "Select e from " + NoticeStatus.class.getName() + " e " //
					+ " Where e.id in (" + StringUtils.join(listId, ',') + ")";

			Query query = entityManager.createQuery(sql, NoticeStatus.class);
//	            query.setParameter("listId", StringUtils.join(listId, ','));
			List<NoticeStatus> listSummon = query.getResultList();
			if (!listSummon.isEmpty())
				return listSummon;
			else
				return null;

		} catch (NoResultException e) {
			return null;
		}
	}

	public List<NoticeStatus> findNoticeByApprove() {
		try {
			String nativesql = "select distinct st.id from investigation.inspector i,investigation.notice_status st,investigation.case_details cd where cd.id=st.case_details_id and i.case_details_id=st.case_details_id and st.is_signed= 0 and st.aprroval_stage2=true and st.aprroval_stage1=true";
			Query nativequery = entityManager.createNativeQuery(nativesql);
			// query.setParameter("appUser", appUser);
			// nativequery.setParameter("isSigned", 0);
			List listId = nativequery.getResultList();
			if (listId.isEmpty())
				return new ArrayList<NoticeStatus>();

			String sql = "Select e from " + NoticeStatus.class.getName() + " e " //
					+ " Where e.id in (" + StringUtils.join(listId, ',') + ")";

			Query query = entityManager.createQuery(sql, NoticeStatus.class);
//		            query.setParameter("listId", StringUtils.join(listId, ','));
			List<NoticeStatus> listSummon = query.getResultList();
			if (!listSummon.isEmpty())
				return listSummon;
			else
				return null;
		} catch (NoResultException e) {
			return null;
		}
	}

	public NoticeStatus getNoticeStatusBysummonId(SummonType summonType) {
		try {
			String sql = "Select e from " + NoticeStatus.class.getName() + " e " //
					+ " Where e.summonType = :summonType";

			Query query = entityManager.createQuery(sql, NoticeStatus.class);
			if (summonType != null) {
				query.setParameter("summonType", summonType);
			} else {
				query.setParameter("summonType", new SummonType());
			}
			List<NoticeStatus> listSummon = query.getResultList();
			if (!listSummon.isEmpty())
				return listSummon.get(0);
			else
				return null;

			// return (SummonDetails) query.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	public List<NoticeStatus> findNoticeByApprove(String startDate, String endDate) {
		try {
			String nativesql = "select distinct st.id from investigation.inspector i,investigation.notice_status st,investigation.case_details cd where cd.id=st.case_details_id and i.case_details_id=st.case_details_id and st.is_signed= 0 and st.aprroval_stage2=true";
			Query nativequery = entityManager.createNativeQuery(nativesql);
			// query.setParameter("appUser", appUser);
			// nativequery.setParameter("isSigned", 0);
			List listId = nativequery.getResultList();
			if (listId.isEmpty())
				return new ArrayList<NoticeStatus>();
			String sql = null;
			Query query;
			if (startDate == "" || endDate == "") {
				sql = "Select e from " + NoticeStatus.class.getName() + " e " //
						+ " Where e.id in (" + StringUtils.join(listId, ',') + ")";
				query = entityManager.createQuery(sql, NoticeStatus.class);
			} else {

				sql = "Select e from " + NoticeStatus.class.getName() + " e " //
						+ " Where e.id in (" + StringUtils.join(listId, ',') + ") and date(e.approvalDate) BETWEEN '"
						+ startDate + "' AND '" + endDate + "'";

				query = entityManager.createQuery(sql, NoticeStatus.class);
			}

//		            query.setParameter("listId", StringUtils.join(listId, ','));
			List<NoticeStatus> listSummon = query.getResultList();
			if (!listSummon.isEmpty())
				return listSummon;
			else
				return null;
		} catch (NoResultException e) {
			return null;
		}
	}

	public List<NoticeStatus> findNoticeByAll(String startDate, String endDate) {
		try {

			List listId;

			String nativesql = "select ss.id from investigation.notice_status ss,investigation.inspector ins where ss.case_details_id=ins.case_details_id ";
			Query nativequery = entityManager.createNativeQuery(nativesql);

			listId = nativequery.getResultList();

			if (listId.isEmpty())
				return new ArrayList<NoticeStatus>();
			String sql = null;
			Query query;
			if (startDate == "" || endDate == "") {
				sql = "Select e from " + NoticeStatus.class.getName() + " e " //
						+ " Where e.id in (" + StringUtils.join(listId, ',') + ")";
				query = entityManager.createQuery(sql, NoticeStatus.class);
			} else {

				sql = "Select e from " + NoticeStatus.class.getName() + " e " //
						+ " Where e.id in (" + StringUtils.join(listId, ',') + ") and date(e.createdDate) BETWEEN '"
						+ startDate + "' AND '" + endDate + "'";

				query = entityManager.createQuery(sql, NoticeStatus.class);
			}
			List<NoticeStatus> listSummon = query.getResultList();
			if (!listSummon.isEmpty())
				return listSummon;
			else
				return null;

		} catch (NoResultException e) {
			return null;
		}
	}

	@SuppressWarnings("unused")
	public List<NoticeStatus> findPhyNotice(AppUser userDetails, boolean approveStage1, boolean Phynotice) {
		try {

			String role = roleDAO.getRoleName(userDetails.getUserId());
			List listId;
			
			//	String nativesql = "select ss.id from investigation.notice_status ss,investigation.inspector ins where ss.case_details_id=ins.case_details_id and ins.app_user_user_id=:userId and ss.is_signed=0  and ins.is_active = true and ss.is_physicallysent =true and ss.aprroval_stage1=false and  ss.is_rejected = false";
				
			String nativesql = "select ns.id from investigation.notice_status ns, investigation.case_details c, \r\n" + 
					"investigation.inspector i where i.case_details_id=c.id and( i.app_user_user_id=:userId and i.is_ado=true  \r\n" + 
					" or (i.app_user_user_id in (select  regular_id_id from authentication.link_officer lo where lo.user_details_id=:user_details_id and   lo.is_active = true and CURRENT_DATE between lo.from_date and lo.to_date)))\r\n" + 
					"and ns.case_details_id = c.id and ns.aprroval_stage1=false and ns.is_physicallysent =true and ns.is_rejected = false ORDER BY ns.id DESC" ;
					
			Query nativequery = entityManager.createNativeQuery(nativesql);
				if (userDetails != null) {
					nativequery.setParameter("userId", userDetails.getUserId());
				} else {
					nativequery.setParameter("userId", new AppUser().getUserId());
				}
                    nativequery.setParameter("user_details_id", userDetails.getUserId())   ;
				listId = nativequery.getResultList();
			

			if (listId.isEmpty())
				return new ArrayList<NoticeStatus>();


			String sql = "Select e from " + NoticeStatus.class.getName() + " e " //
					+ " Where e.id in (" + StringUtils.join(listId, ',') + ") order by e.id desc";

			Query query = entityManager.createQuery(sql, NoticeStatus.class);
//	            query.setParameter("listId", StringUtils.join(listId, ','));
			@SuppressWarnings("unchecked")
			List<NoticeStatus> listSummon = query.getResultList();
			if (!listSummon.isEmpty())
				return listSummon;
			else
				return null;

		} catch (NoResultException e) {
			return null;
		}
	}

	@SuppressWarnings("unused")
	public List<NoticeStatus> findPhyReviewNotice(AppUser userDetails, boolean approveStage1, boolean Phynotice) {
		try {

			String role = roleDAO.getRoleName(userDetails.getUserId());
			List listId;
			
				String nativesql = "select ss.id from investigation.notice_status ss,investigation.inspector ins where ss.case_details_id=ins.case_details_id and ins.app_user_user_id=:userId and ss.is_signed=0  and ins.is_active = true and ss.is_physicallysent =true and ss.aprroval_stage1=false and  ss.is_rejected = true";
				Query nativequery = entityManager.createNativeQuery(nativesql);
				if (userDetails != null) {
					nativequery.setParameter("userId", userDetails.getUserId());
				} else {
					nativequery.setParameter("userId", new AppUser().getUserId());
				}

				listId = nativequery.getResultList();
			

			if (listId.isEmpty())
				return new ArrayList<NoticeStatus>();

			String sql = "Select e from " + NoticeStatus.class.getName() + " e " //
					+ " Where e.id in (" + StringUtils.join(listId, ',') + ")";

			Query query = entityManager.createQuery(sql, NoticeStatus.class);
//	            query.setParameter("listId", StringUtils.join(listId, ','));
			@SuppressWarnings("unchecked")
			List<NoticeStatus> listSummon = query.getResultList();
			if (!listSummon.isEmpty())
				return listSummon;
			else
				return null;

		} catch (NoResultException e) {
			return null;
		}
	}

	public List<NoticeStatus> findPhyNoticeList(AppUser userDetails, boolean b, boolean c) {
		try {

			String role = roleDAO.getRoleName(userDetails.getUserId());
			List listId;
			
				String nativesql = "select ss.id from investigation.notice_status ss,investigation.inspector ins where ss.case_details_id=ins.case_details_id and ins.app_user_user_id=:userId  and ins.is_active = true and ss.is_physicallysent =true";
				Query nativequery = entityManager.createNativeQuery(nativesql);
				if (userDetails != null) {
					nativequery.setParameter("userId", userDetails.getUserId());
				} else {
					nativequery.setParameter("userId", new AppUser().getUserId());
				}

				listId = nativequery.getResultList();
			

			if (listId.isEmpty())
				return new ArrayList<NoticeStatus>();

			String sql = "Select e from " + NoticeStatus.class.getName() + " e " //
					+ " Where e.id in (" + StringUtils.join(listId, ',') + ")";

			Query query = entityManager.createQuery(sql, NoticeStatus.class);
//	            query.setParameter("listId", StringUtils.join(listId, ','));
			@SuppressWarnings("unchecked")
			List<NoticeStatus> listSummon = query.getResultList();
			if (!listSummon.isEmpty())
				return listSummon;
			else
				return null;

		} catch (NoResultException e) {
			return null;
		}	}

	public List<NoticeStatus> findNoticePhysicallysend() {
		try {
			String nativesql = "select distinct st.id from investigation.inspector i,investigation.notice_status st,investigation.case_details cd where cd.id=st.case_details_id and i.case_details_id=st.case_details_id and st.is_signed= 0 and st.is_physicallysent=true";
			Query nativequery = entityManager.createNativeQuery(nativesql);
			// query.setParameter("appUser", appUser);
			// nativequery.setParameter("isSigned", 0);
			List listId = nativequery.getResultList();
			if (listId.isEmpty())
				return new ArrayList<NoticeStatus>();

			String sql = "Select e from " + NoticeStatus.class.getName() + " e " //
					+ " Where e.id in (" + StringUtils.join(listId, ',') + ")";

			Query query = entityManager.createQuery(sql, NoticeStatus.class);
//		            query.setParameter("listId", StringUtils.join(listId, ','));
			List<NoticeStatus> listSummon = query.getResultList();
			if (!listSummon.isEmpty())
				return listSummon;
			else
				return null;
		} catch (NoResultException e) {
			return null;
		}
	}

	public List<NoticeStatus> findNoticePedingPhysicallysend() {
		try {
			String nativesql = "select st.id from investigation.notice_status st \r\n" + 
					"where TO_DATE(to_char(created_date,'YYYY-MM-DD'),'YYYY-MM-DD') < to_timestamp('2021-09-01 15:32:50.984', 'YYYY-MM-DD') and  aprroval_stage1=true and aprroval_stage2=true and is_signed=0  and is_rejected=false  and is_physicallysent=false \r\n " + 
					" and is_physicallysent=false ";	
			Query nativequery = entityManager.createNativeQuery(nativesql);
			// query.setParameter("appUser", appUser);
			// nativequery.setParameter("isSigned", 0);
			List listId = nativequery.getResultList();
			if (listId.isEmpty())
				return new ArrayList<NoticeStatus>();

			String sql = "Select e from " + NoticeStatus.class.getName() + " e " //
					+ " Where e.id in (" + StringUtils.join(listId, ',') + ")";

			Query query = entityManager.createQuery(sql, NoticeStatus.class);
//		            query.setParameter("listId", StringUtils.join(listId, ','));
			List<NoticeStatus> listSummon = query.getResultList();
			if (!listSummon.isEmpty())
				return listSummon;
			else
				return null;
		} catch (NoResultException e) {
			return null;
		}
	}

	public Page<NoticeStatus> noticelistAll(int pageNo, int pageSize, String sortField, String sortDirection,AppUser userDetails) {
		Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending() :
			Sort.by(sortField).descending();
		String role = roleDAO.getRoleName(userDetails.getUserId());
		Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
		if (role.equals("ROLE_DIRECTOR")) {
			return this.noticeRepo.findAll(pageable);
		}
		else {
		return this.noticeRepo.findAllByAppUser(pageable,userDetails);
		}
	}
	
	
}
