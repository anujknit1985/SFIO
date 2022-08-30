package com.snms.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.transaction.Transactional;
import org.thymeleaf.util.StringUtils;
import org.owasp.esapi.ESAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.snms.dto.InspectorDTO;
import com.snms.entity.AppUser;
import com.snms.entity.CaseDetails;
import com.snms.entity.Company;
import com.snms.entity.Inspector;
import com.snms.entity.OfficeOrder;
import com.snms.entity.RelationpersonCompany;
import com.snms.entity.UnitDetails;
import com.snms.entity.UserDetails;
import com.snms.entity.UserRole;
import com.snms.entity.personcompanyApproval;
import com.snms.service.PersoncompanyApprovalRepository;
import com.snms.service.UserManagementCustom;
import com.snms.validation.SNMSValidator;

@Repository
@Transactional
public class UserDetailsDao implements UserManagementCustom {

	@Autowired
	private EntityManager entityManager;

	@SuppressWarnings("unchecked")
	@Override
	public List<UserDetails> findByUnit(UnitDetails unitDetails) {
		try {
			// String sql = "Select * from " + UserDetails.class + " e Where e.unit =
			// "+UnitDetails.class;

			String sql = "Select e from " + UserDetails.class.getName() + " e " //
					+ " where e.designation.id !=1 and e.unit = : unitDetails order by e.designation.order";

			Query query = entityManager.createQuery(sql, UserDetails.class);
			query.setParameter("unitDetails", unitDetails);
			List<UserDetails> unitList = query.getResultList();
			if (unitList != null)
				return unitList;
			else
				return unitList;
		} catch (NoResultException e) {
			return null;
		}
	}

	/*
	 * @SuppressWarnings("unchecked")
	 * 
	 * @Override public List<Object[]> findByCase(Long caseId) { try { String sql =
	 * "select ud.salutation,ud.full_name,d.designation,i.isio from authentication.user_details ud,investigation.inspector i,"
	 * +
	 * "investigation.case_details cd,authentication.designation d where cd.id=i.case_details_id and "
	 * +
	 * "i.app_user_user_id = ud.id and d.id=ud.designation_id and i.case_details_id="
	 * +caseId; Query query = this.entityManager.createNativeQuery(sql); return
	 * (List<Object[]>) query.getResultList(); } catch (NoResultException e) {
	 * return null; } }
	 */

	@SuppressWarnings("unchecked")
	@Override
	public List<Object[]> findByCase(Long caseId) {
		try {
			String sql = "select ud.salutation,ud.first_name || ' ' || ud.middle_name || ' ' || ud.last_name,d.designation,i.isio,i.is_ado ,ud.id from authentication.user_details ud,investigation.inspector i,"
					+ "investigation.case_details cd,authentication.designation d where cd.id=i.case_details_id and "
					+ "i.app_user_user_id = ud.id and d.id=ud.designation_id  and  is_active = true and i.case_details_id=:case_details_id";
			Query query = this.entityManager.createNativeQuery(sql);
			// gouthami 15/09/2020
			query.setParameter("case_details_id", caseId);
			return (List<Object[]>) query.getResultList();
		} catch (NoResultException e) {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Company> findByAllCompByCaseId(CaseDetails caseDetails) {
		String sql = "Select e from " + Company.class.getName() + " e " //
				+ " where e.caseDetails = : caseDetails ";

		Query query = entityManager.createQuery(sql, Company.class);

		if (caseDetails != null) {
			query.setParameter("caseDetails", caseDetails);
		} else {
			query.setParameter("caseDetails", new CaseDetails());
		}
		return (List<Company>) query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Company> findByCaseId(CaseDetails caseDetails) {
		// SNMSValidator snmsvalid = new SNMSValidator()

		String sql = "Select e from " + Company.class.getName() + " e " //
				+ " where e.caseDetails = : caseDetails and is_active = true ";

		Query query = entityManager.createQuery(sql, Company.class);
		if (caseDetails != null) {
			query.setParameter("caseDetails", caseDetails);
		} else {
			query.setParameter("caseDetails", new CaseDetails());
		}

		// gouthami 15/09/2020

		List<Company> compList = query.getResultList();
		if (!compList.isEmpty())
			return compList;
		else
			return compList;
	}

	@Override
	public List<Object[]> findOfficeOrderPendingForApproval(int stage) {
		try {
			String sql = null;
			if (stage == 1) {
				sql = "select o.id,c.case_id,c.case_title,c.id caseid ,c.radio_value from investigation.office_order o, investigation.case_details c "
						+ "where o.case_details_id = c.id and c.case_stage=1 and o.aprroval_stage1=false ORDER BY o.id DESC";
			} else if (stage == 2) {
				sql = "select o.id,c.case_id,c.case_title,c.id caseid ,c.radio_value from investigation.office_order o, investigation.case_details c "
						+ "where o.case_details_id = c.id and o.aprroval_stage1=true and o.aprroval_stage2=false ORDER BY o.id DESC";
			} else if (stage == 3) {
				sql = "select o.id,c.case_id,c.case_title,c.id caseid,c.radio_value from investigation.office_order o, investigation.case_details c "
						+ "where o.case_details_id = c.id and c.case_stage=2 and o.aprroval_stage1=false ORDER BY o.id DESC";
			}

			Query query = this.entityManager.createNativeQuery(sql);

			return (List<Object[]>) query.getResultList();
		} catch (NoResultException e) {
			return null;
		}
	}

	@Override
	public List<Object[]> findCaseByUserId(Long userId) {
		try {
			// String sql = "select cd.case_id,cd.case_title,cd.id from
			// investigation.inspector i,investigation.case_details
			// cd,investigation.office_order o where cd.id=i.case_details_id and
			// cd.id=o.case_details_id and o.is_signed=1 and i.app_user_user_id=:userid";
			String sql = "select distinct cd.case_id,cd.case_title,cd.id,cd.mca_order_file,cd.court_order_file,cd.radio_value from investigation.inspector i,investigation.case_details cd,investigation.office_order o where cd.id=i.case_details_id and cd.id=o.case_details_id and o.is_signed=1 and i.app_user_user_id=:userid and i.is_active=true";
			Query query = this.entityManager.createNativeQuery(sql);
			query.setParameter("userid", userId);
			return (List<Object[]>) query.getResultList();
		} catch (NoResultException e) {
			return null;
		}
	}                                                                                                                                                                    

	@SuppressWarnings("unchecked")
	@Override
	public Object[] findIoByCaseId(Long caseId) {
		try {
			String sql = "select ud.first_name || ' ' || ud.middle_name || ' ' || ud.last_name from investigation.inspector i,authentication.user_details ud,investigation.case_details cd where cd.id=i.case_details_id and i.case_details_id=:caseId and ud.app_user_user_id=i.app_user_user_id and i.isio=true";
			Query query = this.entityManager.createNativeQuery(sql);
			query.setParameter("caseId", caseId);
			Object[] object = (Object[]) query.getSingleResult();
			return object;
		} catch (NoResultException e) {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Object[]> findNoticePendingForApproval(int stage, Long user_id) {
		try {
			String sql = null;
			if (stage == 1) {
				/*
				 * sql=
				 * "select ns.id,c.case_id,c.case_title,c.id caseid from investigation.notice_status ns, investigation.case_details c "
				 * +
				 * "where ns.case_details_id = c.id and ns.aprroval_stage1=false ORDER BY ns.id DESC"
				 * ;
				 */
/*
				sql = "select ns.id,c.case_id,c.case_title,c.id caseid from investigation.notice_status ns, investigation.case_details c, "
						+ "investigation.inspector i where i.case_details_id=c.id and i.app_user_user_id=:user_id and i.is_ado=true "
						+ "and ns.case_details_id = c.id and ns.aprroval_stage1=false and  ns.is_rejected = false ORDER BY ns.id DESC"; */
				
				sql = "select ns.id,c.case_id,c.case_title,c.id caseid from investigation.notice_status ns, investigation.case_details c, \r\n" + 
						"investigation.inspector i where i.case_details_id=c.id and( i.app_user_user_id=:app_user_user_id and i.is_ado=true  \r\n" + 
						" or (i.app_user_user_id in (select  regular_id_id from authentication.link_officer lo where lo.user_details_id=:user_details_id and   lo.is_active = true and CURRENT_DATE between lo.from_date and lo.to_date)))\r\n" + 
						"and ns.case_details_id = c.id and ns.aprroval_stage1=false and ns.aprroval_stage2=false and  ns.is_rejected = false     ORDER BY ns.id DESC";
				Query query = this.entityManager.createNativeQuery(sql);
				query.setParameter("app_user_user_id", user_id);
				query.setParameter("user_details_id", user_id);
				// gouthami 15/09/2020
				List<Object[]> NoticePendingList1 = query.getResultList();
				if (!NoticePendingList1.isEmpty())
					return NoticePendingList1;
				else
					return NoticePendingList1;
			}

			else if (stage == 2) {
				sql = "select ns.id,c.case_id,c.case_title,c.id caseid from investigation.notice_status ns, investigation.case_details c "
						+ "where ns.case_details_id = c.id and ns.aprroval_stage1=true and ns.aprroval_stage2=false and ns.is_sensitive='Y' ORDER BY ns.id DESC";
			}

			// gouthami 15/09/2020

			Query query = this.entityManager.createNativeQuery(sql);
			List<Object[]> NoticePendingList = query.getResultList();
			if (!NoticePendingList.isEmpty())
				return NoticePendingList;
			else
				return NoticePendingList;
		} catch (NoResultException e) {
			return null;
		}
	}

	@Override
	public List<Object[]> findSummonPendingForApproval(int stage, Long user_id) {
		try {
			String sql = null;
			if (stage == 1) {
				/*
				 * sql=
				 * "select ss.id,c.case_id,c.case_title,c.id caseid from investigation.summon_status ss, investigation.case_details c "
				 * +
				 * "where ss.case_details_id = c.id and ss.aprroval_stage1=false ORDER BY ss.id DESC"
				 * ;
				 */

			/*
				sql = "select ss.id,c.case_id,c.case_title,c.id caseid from investigation.summon_status ss, investigation.case_details c, "
						+ "investigation.inspector i where i.case_details_id=c.id and i.app_user_user_id=:user_id and i.is_ado=true "
						+ "and ss.case_details_id = c.id and ss.aprroval_stage1=false and ss.is_rejected = false ORDER BY ss.id DESC";
*/
				
				sql = "select ss.id,c.case_id,c.case_title,c.id caseid from investigation.summon_status ss, investigation.case_details c, \r\n" + 
						"investigation.inspector i where i.case_details_id=c.id and( i.app_user_user_id=:user_id and i.is_ado=true  \r\n" + 
						" or (i.app_user_user_id in (select  regular_id_id from authentication.link_officer lo where lo.user_details_id=:user_details_id and   lo.is_active = true and CURRENT_DATE between lo.from_date and lo.to_date)))\r\n" + 
						"and ss.case_details_id = c.id and ss.aprroval_stage1=false and ss.aprroval_stage2=false and  ss.is_rejected = false and ss.is_physicallysent = false ORDER BY ss.id DESC";
				/*
				 * sql=
				 * "select ss.id,c.case_id,c.case_title,c.id caseid from investigation.summon_status ss,investigation.summon_template st, investigation.case_details c, "
				 * +
				 * "investigation.inspector i where ss.id=st.summon_status_id and st.is_final=true and i.case_details_id=c.id and i.app_user_user_id=:user_id and i.is_ado=true "
				 * +
				 * "and ss.case_details_id = c.id and ss.aprroval_stage1=false ORDER BY ss.id DESC"
				 * ;
				 */

				Query query = this.entityManager.createNativeQuery(sql);
				query.setParameter("user_id", user_id);
				query.setParameter("user_details_id", user_id);
				
				return (List<Object[]>) query.getResultList();

			} else if (stage == 2) {
				sql = "select ss.id,c.case_id,c.case_title,c.id caseid from investigation.summon_status ss, investigation.case_details c "
						+ "where ss.case_details_id = c.id and ss.aprroval_stage1=true and ss.aprroval_stage2=false and ss.is_sensitive='Y' ORDER BY ss.id DESC";
			}

			Query query = this.entityManager.createNativeQuery(sql);
			return (List<Object[]>) query.getResultList();
		} catch (NoResultException e) {
			return null;
		}
	}

	@Override
	public List<Object[]> getIoAdoByCaseId(Long caseId) {
		try {
			String sql = "select app_user_user_id,isio,is_ado from investigation.inspector where case_details_id=:caseId and (isio=true or is_ado=true )";
			Query query = this.entityManager.createNativeQuery(sql);
			query.setParameter("caseId", caseId);
			List<Object[]> lstInsp = query.getResultList();
			return lstInsp;
		} catch (NoResultException e) {
			return null;
		}
	}

	@Override
	public List<Object[]> getInspectorList(Long caseId) {
		try {
			String sql = "select app_user_user_id,isio,is_ado from investigation.inspector where case_details_id=:caseId ";
			Query query = this.entityManager.createNativeQuery(sql);
			query.setParameter("caseId", caseId);
			List<Object[]> lstInsp = query.getResultList();
			return lstInsp;
		} catch (NoResultException e) {
			return null;
		}
	}
	/*
	 * @Override public List<Object[]> findNoticePendingForApproval(int stage) { try
	 * { String sql = null; if(stage==1) { sql=
	 * "select ns.id,c.case_id,c.case_title,c.id caseid from investigation.notice_status ns, investigation.case_details c "
	 * +
	 * "where ns.case_details_id = c.id and ns.aprroval_stage1=false ORDER BY ns.id DESC"
	 * ; }else if(stage==2){ sql=
	 * "select ns.id,c.case_id,c.case_title,c.id caseid from investigation.notice_status ns, investigation.case_details c "
	 * +
	 * "where ns.case_details_id = c.id and ns.aprroval_stage1=true and ns.aprroval_stage2=false and ns.is_sensitive='Y' ORDER BY ns.id DESC"
	 * ; }
	 * 
	 * 
	 * Query query = this.entityManager.createNativeQuery(sql);
	 * 
	 * return (List<Object[]>) query.getResultList(); } catch (NoResultException e)
	 * { return null; } }
	 * 
	 * 
	 * @Override public List<Object[]> findSummonPendingForApproval(int stage) { try
	 * { String sql = null; if(stage==1) { sql=
	 * "select ss.id,c.case_id,c.case_title,c.id caseid from investigation.summon_status ss, investigation.case_details c "
	 * +
	 * "where ss.case_details_id = c.id and ss.aprroval_stage1=false ORDER BY ss.id DESC"
	 * ; }else if(stage==2){ sql=
	 * "select ss.id,c.case_id,c.case_title,c.id caseid from investigation.summon_status ss, investigation.case_details c "
	 * +
	 * "where ss.case_details_id = c.id and ss.aprroval_stage1=true and ss.aprroval_stage2=false and ss.is_sensitive='Y' ORDER BY ss.id DESC"
	 * ; }
	 * 
	 * 
	 * Query query = this.entityManager.createNativeQuery(sql);
	 * 
	 * return (List<Object[]>) query.getResultList(); } catch (NoResultException e)
	 * { return null; } }
	 */

	/*
	 * @Override public List<UserDetails> findByRole(int roleUser) { try { // String
	 * sql = "Select * from " + UserDetails.class + " e Where e.unit = //
	 * "+UnitDetails.class;
	 * 
	 * String sql = "Select e from " + UserDetails.class.getName() + " e " // +
	 * " where e.designation.id !=1 and e.userId in ("+"Select r.appUser from " +
	 * UserRole.class.getName() +" r " +"where r.appRole="+roleUser
	 * +") order by e.designation.order";
	 * 
	 * Query query = entityManager.createQuery(sql, UserDetails.class); //
	 * query.setParameter("unitDetails", unitDetails); return (List<UserDetails>)
	 * query.getResultList(); } catch (NoResultException e) { return null; } }
	 */

	@Override
	public int getCasesAssigned(Long userId) {
		try {
			// String sql = "Select * from " + UserDetails.class + " e Where e.unit =
			// "+UnitDetails.class;

			String sql = "select count(i) from investigation.inspector i where i.app_user_user_id=:userId";

			Query query = entityManager.createNativeQuery(sql);
			query.setParameter("userId", userId);
			return Integer.parseInt(query.getResultList().get(0).toString());
		} catch (NoResultException e) {
			return 0;
		}
	}

	@Override
	public boolean isAdo(Long userId) {
		try {
			String sql = "";
			sql = "select cd.case_id,cd.case_title,cd.id from investigation.inspector i,investigation.case_details cd,investigation.office_order o where cd.id=i.case_details_id and cd.id=o.case_details_id and o.is_signed=1 and i.app_user_user_id=:userid and i.is_ado=true";
			Query query = this.entityManager.createNativeQuery(sql);
			query.setParameter("userid", userId);
			List<Object[]> list = query.getResultList();
			if (!list.isEmpty())
				return true;
			else
				return false;
		} catch (NoResultException e) {
			return false;
		}
	}
	/*
	 * @Override public List<Object[]> findByCase(Long caseId) { try { String sql =
	 * "select ud.salutation,ud.full_name,d.designation,i.isio from authentication.user_details ud,investigation.inspector i,"
	 * +
	 * "investigation.case_details cd,authentication.designation d where cd.id=i.case_details_id and "
	 * +
	 * "i.app_user_user_id = ud.app_user_user_id and d.id=ud.designation_id and i.case_details_id="
	 * +caseId; Query query = this.entityManager.createNativeQuery(sql); return
	 * (List<Object[]>) query.getResultList(); } catch (NoResultException e) {
	 * return null; } }
	 */

	@Override
	public List<UserDetails> findByRole(int roleUser, UnitDetails unitDetails) {
		try {
			// String sql = "Select * from " + UserDetails.class + " e Where e.unit =
			// "+UnitDetails.class;

			String sql = "Select e from " + UserDetails.class.getName() + " e " //
					+ " where e.designation.id !=1 and e.unit = : unitDetails and e.userId in ("
					+ "Select r.appUser from " + UserRole.class.getName() + " r " + "where r.appRole=" + roleUser
					+ ") order by e.firstName";

			Query query = entityManager.createQuery(sql, UserDetails.class);
			if (unitDetails != null) {
				query.setParameter("unitDetails", unitDetails);
			} else {
				query.setParameter("unitDetails", new UnitDetails());
			}

			return (List<UserDetails>) query.getResultList();
		} catch (NoResultException e) {
			return null;
		}
	}

	public UserDetails findUserDetailsMobile(String mobile) {
		try {
			String sql = "Select e from " + UserDetails.class.getName() + " e " //
					+ " Where e.primaryMobile = :primaryMobile OR e.alternateNo = :alternateNo AND e.alternateNo!=''";
			Query query = entityManager.createQuery(sql, UserDetails.class);
			query.setParameter("primaryMobile", mobile);
			query.setParameter("alternateNo", mobile);
			return (UserDetails) query.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Object[]> findByCaseId(Long caseId) {
		try {
			String sql = "select ud.salutation,ud.first_name || ' ' || ud.middle_name || ' ' || ud.last_name,d.designation,i.isio,i.is_ado ,i.app_user_user_id ,i.id from authentication.user_details ud,investigation.inspector i,"
					+ "investigation.case_details cd,authentication.designation d where cd.id=i.case_details_id and "
					+ "i.app_user_user_id = ud.id and d.id=ud.designation_id and  is_active = true and i.case_details_id="
					+ caseId;
			Query query = this.entityManager.createNativeQuery(sql);
			return (List<Object[]>) query.getResultList();
		} catch (NoResultException e) {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Object[]> findDelInpBYCaseId(Long caseId) {
		try {
			String sql = "select ud.salutation,ud.first_name || ' ' || ud.middle_name || ' ' || ud.last_name,d.designation,i.isio,i.is_ado ,i.app_user_user_id ,i.id ,ih.io_active_date_from , ih.io_active_date_to from authentication.user_details ud,investigation.inspector i,"
					+ "investigation.case_details cd,authentication.designation d,investigation.inspector_history ih  where cd.id=i.case_details_id and "
					+ "i.app_user_user_id = ud.id and d.id=ud.designation_id and  ih.case_details_id=i.case_details_id and is_active = false and i.app_user_user_id = ih.app_user_user_id and ih.director_order_from   IS NULL and i.case_details_id="
					+ caseId;
			Query query = this.entityManager.createNativeQuery(sql);
			return (List<Object[]>) query.getResultList();
		} catch (NoResultException e) {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Object[]> findAllInspByCaseId(Long caseId) {
		try {
			String sql = "select ud.salutation,ud.first_name || ' ' || ud.middle_name || ' ' || ud.last_name,d.designation,i.isio,i.is_ado ,i.app_user_user_id ,i.id from authentication.user_details ud,investigation.inspector i,"
					+ "investigation.case_details cd,authentication.designation d where cd.id=i.case_details_id and "
					+ "i.app_user_user_id = ud.id and d.id=ud.designation_id  and i.case_details_id=:case_details_id";
			Query query = this.entityManager.createNativeQuery(sql);
			query.setParameter("case_details_id", caseId);
			return (List<Object[]>) query.getResultList();
		} catch (NoResultException e) {
			return null;
		}
	}

	@Override
	public List<Inspector> findByCaseandUserId(Long id, Long userId) {
		return null;

	}

	@Override
	public List<Object[]> findpersonPendingForApproval(int i, Long userId) {
		try {
			String sql = "select c.case_id,c.case_title,pd.full_name ,pd.pan_number ,pa.id  from gams.personcompany_approval pa, gams.relation_person_company pc,\r\n"
					+ "investigation.case_details c, gams.person_details pd, investigation.inspector i where i.case_details_id=c.id and i.app_user_user_id=:user_id\r\n"
					+ "and i.is_ado=true and pc.case_details_id = c.id and  pa.id = pc.rpc_id and pa.approved_status=1 and pa.is_approved_stage2=false and pd.person_id = pc.rpc_id";
			Query query = this.entityManager.createNativeQuery(sql);
			query.setParameter("user_id", userId);
			// gouthami 15/09/2020
			List<Object[]> personPendingList1 = query.getResultList();
			if (!personPendingList1.isEmpty())
				return personPendingList1;
			else
				return personPendingList1;
		} catch (NoResultException e) {
			return null;
		}
	}

	/*
	 * public List<RelationpersonCompany> findpersonPendingListForApproval(int i,
	 * Long userId, int caseStage) { List<RelationpersonCompany> listperson = null;
	 * List personPendingList1;
	 * 
	 * String sql =
	 * "select  rpc_id from gams.personcompany_approval pa, gams.relation_person_company pc,\r\n"
	 * +
	 * "investigation.case_details c, gams.person_details pd, investigation.inspector i where i.case_details_id=c.id and i.app_user_user_id=:user_id\r\n"
	 * +
	 * "and i.is_ado=true and pc.case_details_id = c.id and  pa.id = pc.rpc_id and pa.approved_status=1 and pa.is_approved_stage2=false and c.case_stage=:case_stage"
	 * ; Query query = this.entityManager.createNativeQuery(sql);
	 * query.setParameter("user_id", userId); query.setParameter("case_stage",
	 * caseStage);
	 * 
	 * personPendingList1 = query.getResultList(); if (personPendingList1.isEmpty())
	 * return new ArrayList<RelationpersonCompany>();
	 * 
	 * try { String sqlquerry = "Select e from " +
	 * RelationpersonCompany.class.getName() + " e " // + " Where e.id in (" +
	 * StringUtils.join(personPendingList1, ',') + ") ";
	 * 
	 * Query query1 = entityManager.createQuery(sqlquerry,
	 * RelationpersonCompany.class);
	 * 
	 * listperson = query1.getResultList(); if (listperson.isEmpty()) return
	 * listperson; else return listperson;
	 * 
	 * } catch (NoResultException e) { return listperson; }
	 * 
	 * }
	 * 
	 */	
	
	
	
	public List<personcompanyApproval> findpersonPendingListForApproval(int i, Long userId, int caseStage) {
		List<personcompanyApproval> listperson = null;
		List personPendingList1;

	/*	String sql = "select  pa.approval_status_id from gams.personcompany_approval pa, gams.relation_person_company pc,\r\n"
				+ "investigation.case_details c, gams.person_details pd, investigation.inspector i where i.case_details_id=c.id and i.app_user_user_id=:user_id\r\n"
				+ "and i.is_ado=true and pc.case_details_id = c.id and  pa.id = pc.rpc_id and pa.approved_status=1 and pa.rejected = false and pa.is_approved_stage2=false and c.case_stage=:case_stage";
	*/
		String sql = "select  pa.approval_status_id from gams.personcompany_approval pa, gams.relation_person_company pc,\r\n" + 
				"investigation.case_details c, gams.person_details pd, investigation.inspector i where i.case_details_id=c.id and( i.app_user_user_id=:user_id and i.is_ado=true  \r\n" + 
				" or (i.app_user_user_id in (select  regular_id_id from authentication.link_officer lo where lo.user_details_id=:user_details_id and   lo.is_active = true and CURRENT_DATE between lo.from_date and lo.to_date))) and pc.case_details_id = c.id and  pa.id = pc.rpc_id and pa.approved_status=1 and pa.rejected = false and pa.is_deleted=false and pa.is_approved_stage2=false and c.case_stage=:case_stage\r\n"; 
				
		Query query = this.entityManager.createNativeQuery(sql);
		query.setParameter("user_id", userId);
		query.setParameter("user_details_id", userId);
		query.setParameter("case_stage", caseStage);

		personPendingList1 = query.getResultList();
		if (personPendingList1.isEmpty())
			return new ArrayList<personcompanyApproval>();

		try {
			String sqlquerry = "Select e from " + personcompanyApproval.class.getName() + " e " //
					+ " Where e.id in (" + StringUtils.join(personPendingList1, ',') + ") order By e.id DESC";

			Query query1 = entityManager.createQuery(sqlquerry, personcompanyApproval.class);

			listperson = query1.getResultList();
			if (listperson.isEmpty())
				return listperson;
			else
				return listperson;

		} catch (NoResultException e) {
			return listperson;
		}

	}


	public List<personcompanyApproval> findpersonApprovedListForApproval(int i, Long userId, int caseStage) {
		List<personcompanyApproval> listperson = null;
		List personPendingList1;

		String sql = "select  pa.approval_status_id from gams.personcompany_approval pa, gams.relation_person_company pc,\r\n"
				+ "investigation.case_details c, gams.person_details pd, investigation.inspector i where i.case_details_id=c.id and i.app_user_user_id=:user_id\r\n"
				+ "and i.is_ado=true and pc.case_details_id = c.id and  pa.id = pc.rpc_id and pa.approved_status=2  and pa.rejected = false and pa.is_approved_stage2=true and c.case_stage=:case_stage";
		Query query = this.entityManager.createNativeQuery(sql);
		query.setParameter("user_id", userId);
		query.setParameter("case_stage", caseStage);

		personPendingList1 = query.getResultList();
		if (personPendingList1.isEmpty())
			return new ArrayList<personcompanyApproval>();

		try {
			String sqlquerry = "Select e from " + personcompanyApproval.class.getName() + " e " //
					+ " Where e.id in (" + StringUtils.join(personPendingList1, ',') + ")  ";

			Query query1 = entityManager.createQuery(sqlquerry, personcompanyApproval.class);

			listperson = query1.getResultList();
			if (listperson.isEmpty())
				return listperson;
			else
				return listperson;

		} catch (NoResultException e) {
			return listperson;
		}

	}


	
	@Override
	public List<Object[]> findCaseByUserId(Long userId, Boolean isADO) {
		try {
			// String sql = "select cd.case_id,cd.case_title,cd.id from
			// investigation.inspector i,investigation.case_details
			// cd,investigation.office_order o where cd.id=i.case_details_id and
			// cd.id=o.case_details_id and o.is_signed=1 and i.app_user_user_id=:userid";
			String sql = "select distinct cd.case_id,cd.case_title,cd.id,cd.mca_order_file,cd.court_order_file,cd.radio_value from investigation.inspector i,investigation.case_details cd,investigation.office_order o where cd.id=i.case_details_id and cd.id=o.case_details_id and o.is_signed=1 and i.app_user_user_id=:userid and i.is_active=true and i.is_ado=true and cd.case_stage=1";
			Query query = this.entityManager.createNativeQuery(sql);
			query.setParameter("userid", userId);
			return (List<Object[]>) query.getResultList();
		} catch (NoResultException e) {
			return null;
		}
	}

	@Override
	public List<Object[]> findCaseByUserIdbystage(Long userId) {
		try {
			// String sql = "select cd.case_id,cd.case_title,cd.id from
			// investigation.inspector i,investigation.case_details
			// cd,investigation.office_order o where cd.id=i.case_details_id and
			// cd.id=o.case_details_id and o.is_signed=1 and i.app_user_user_id=:userid";
			String sql = "select distinct cd.case_id,cd.case_title,cd.id,cd.mca_order_file,cd.court_order_file,cd.radio_value from investigation.inspector i,investigation.case_details cd,investigation.office_order o where cd.id=i.case_details_id and cd.id=o.case_details_id and o.is_signed=1 and i.app_user_user_id=:userid and i.is_active=true and cd.case_stage=1";
			Query query = this.entityManager.createNativeQuery(sql);
			query.setParameter("userid", userId);
			return (List<Object[]>) query.getResultList();
		} catch (NoResultException e) {
			return null;
		}
	}

	/*
	 * @Override public List<RelationpersonCompany>
	 * findpersonPendingListForProApproval(int i, Long userId, int caseStage) {
	 * List<RelationpersonCompany> listperson = null; List personPendingList1;
	 * 
	 * String sql =
	 * "select  rpc_id from gams.personcompany_approval pa, gams.relation_person_company pc,\r\n"
	 * + "investigation.case_details c, gams.person_details pd where  \r\n" +
	 * "pc.case_details_id = c.id and  pa.id = pc.rpc_id and pa.approved_status=1 and pa.is_approved_stage2=false  and pc.is_approvedstage2=false and c.case_stage=:case_stage"
	 * ; Query query = this.entityManager.createNativeQuery(sql); //
	 * query.setParameter("user_id", userId); query.setParameter("case_stage",
	 * caseStage);
	 * 
	 * personPendingList1 = query.getResultList(); if (personPendingList1.isEmpty())
	 * return new ArrayList<RelationpersonCompany>();
	 * 
	 * try { String sqlquerry = "Select e from " +
	 * RelationpersonCompany.class.getName() + " e " // + " Where e.id in (" +
	 * StringUtils.join(personPendingList1, ',') + ") ";
	 * 
	 * Query query1 = entityManager.createQuery(sqlquerry,
	 * RelationpersonCompany.class);
	 * 
	 * listperson = query1.getResultList(); if (listperson.isEmpty()) return
	 * listperson; else return listperson;
	 * 
	 * } catch (NoResultException e) { return listperson; }
	 * 
	 * }
	 */
	@Override
	public List<personcompanyApproval> findpersonPendingListForProApproval(int i, Long userId, int caseStage) {
		List<personcompanyApproval> listperson = null;
		List personPendingList1;

		String sql = "select  pa.approval_status_id from gams.personcompany_approval pa, gams.relation_person_company pc,\r\n"
				+ "investigation.case_details c, gams.person_details pd where  \r\n"
				+ "pc.case_details_id = c.id and  pa.id = pc.rpc_id and pa.approved_status=1 and pa.is_approved_stage2=false  and pc.is_approvedstage2=false and c.case_stage=:case_stage and pc.rejected = false";
		Query query = this.entityManager.createNativeQuery(sql);
		// query.setParameter("user_id", userId);
		query.setParameter("case_stage", caseStage);

		personPendingList1 = query.getResultList();
		if (personPendingList1.isEmpty())
			return new ArrayList<personcompanyApproval>();

		try {
			String sqlquerry = "Select e from " + personcompanyApproval.class.getName() + " e " //
					+ " Where e.id in (" + StringUtils.join(personPendingList1, ',') + ") ";

			Query query1 = entityManager.createQuery(sqlquerry, personcompanyApproval.class);

			listperson = query1.getResultList();
			if (listperson.isEmpty())
				return listperson;
			else
				return listperson;

		} catch (NoResultException e) {
			return listperson;
		}

	}

	public List<RelationpersonCompany> findTotalpersonListForProApproval(int i, Long userId, int caseStage) {
		List<RelationpersonCompany> listperson = null;
		List personPendingList1;

		String sql = "select  rpc_id from gams.personcompany_approval pa, gams.relation_person_company pc,\r\n"
				+ "investigation.case_details c, gams.person_details pd where  \r\n"
				+ "pc.case_details_id = c.id and  pa.id = pc.rpc_id and c.case_stage=:case_stage";
		Query query = this.entityManager.createNativeQuery(sql);
		// query.setParameter("user_id", userId);
		query.setParameter("case_stage", caseStage);

		personPendingList1 = query.getResultList();
		if (personPendingList1.isEmpty())
			return new ArrayList<RelationpersonCompany>();

		try {
			String sqlquerry = "Select e from " + RelationpersonCompany.class.getName() + " e " //
					+ " Where e.id in (" + StringUtils.join(personPendingList1, ',') + ") ";

			Query query1 = entityManager.createQuery(sqlquerry, RelationpersonCompany.class);

			listperson = query1.getResultList();
			if (listperson.isEmpty())
				return listperson;
			else
				return listperson;

		} catch (NoResultException e) {
			return listperson;
		}

	}

	public List<RelationpersonCompany> findpersonAorrovedListForProApproval(int i, Long userId, int caseStage) {
		List<RelationpersonCompany> listperson = null;
		List personPendingList1;

		String sql = "select  rpc_id from gams.personcompany_approval pa, gams.relation_person_company pc,\r\n"
				+ "investigation.case_details c, gams.person_details pd where  \r\n"
				+ "pc.case_details_id = c.id and  pa.id = pc.rpc_id and pa.approved_status=2 and pa.is_approved_stage2=true  and c.case_stage=:case_stage";
		Query query = this.entityManager.createNativeQuery(sql);
		// query.setParameter("user_id", userId);
		query.setParameter("case_stage", caseStage);

		personPendingList1 = query.getResultList();
		if (personPendingList1.isEmpty())
			return new ArrayList<RelationpersonCompany>();

		try {
			String sqlquerry = "Select e from " + RelationpersonCompany.class.getName() + " e " //
					+ " Where e.id in (" + StringUtils.join(personPendingList1, ',') + ") ";

			Query query1 = entityManager.createQuery(sqlquerry, RelationpersonCompany.class);

			listperson = query1.getResultList();
			if (listperson.isEmpty())
				return listperson;
			else
				return listperson;

		} catch (NoResultException e) {
			return listperson;
		}

	}

	@Override
	public List<RelationpersonCompany> findApprovePersonListByUserID(int stage,Long user_id,int caseStage) {
		List<RelationpersonCompany> listperson = null;
		List personPendingList1;

		String sql = "select  rpc_id from gams.personcompany_approval pa, gams.relation_person_company pc,\r\n"
				+ "investigation.case_details c, gams.person_details pd where  \r\n"
				+ "pc.case_details_id = c.id and  pa.id = pc.rpc_id and pa.approved_status=2 and pa.is_approved_stage2=true and pa.created_by_id=:user_id  and c.case_stage=:case_stage";
		Query query = this.entityManager.createNativeQuery(sql);
		query.setParameter("user_id", user_id);
		query.setParameter("case_stage", caseStage);

		personPendingList1 = query.getResultList();
		if (personPendingList1.isEmpty())
			return new ArrayList<RelationpersonCompany>();

		try {
			String sqlquerry = "Select e from " + RelationpersonCompany.class.getName() + " e " //
					+ " Where e.id in (" + StringUtils.join(personPendingList1, ',') + ") ";

			Query query1 = entityManager.createQuery(sqlquerry, RelationpersonCompany.class);

			listperson = query1.getResultList();
			if (listperson.isEmpty())
				return listperson;
			else
				return listperson;

		} catch (NoResultException e) {
			return listperson;
		}
	}

	@Override
	public List<RelationpersonCompany> findpersonPendingListByUserID(int stage,Long user_id,int caseStage) {
		List<RelationpersonCompany> listperson = null;
		List personPendingList1;

		String sql = "select  rpc_id from gams.personcompany_approval pa, gams.relation_person_company pc,\r\n"
				+ "investigation.case_details c, gams.person_details pd where  \r\n"
				+ "pc.case_details_id = c.id and  pa.id = pc.rpc_id and pa.approved_status=1 and pa.is_approved_stage2=false and pc.is_deleted = false and pc.rejected = false  and pa.created_by_id=:user_id and pc.is_approvedstage2=false and c.case_stage=:case_stage";
		Query query = this.entityManager.createNativeQuery(sql);
		query.setParameter("user_id", user_id);
		query.setParameter("case_stage", caseStage);

		personPendingList1 = query.getResultList();
		if (personPendingList1.isEmpty())
			return new ArrayList<RelationpersonCompany>();

		try {
			String sqlquerry = "Select e from " + RelationpersonCompany.class.getName() + " e " //
					+ " Where e.id in (" + StringUtils.join(personPendingList1, ',') + ") ";

			Query query1 = entityManager.createQuery(sqlquerry, RelationpersonCompany.class);

			listperson = query1.getResultList();
			if (listperson.isEmpty())
				return listperson;
			else
				return listperson;

		} catch (NoResultException e) {
			return listperson;
		}
	}

	@Override
	public List<OfficeOrder> findAllByDirAprrovalStage1(boolean aprrovalStage1, int caseStage) {
		List CasePendingList1;
		List<OfficeOrder> listcase = null;
		String sql = "select cd.id  from investigation.case_details cd where cd.case_stage=:case_stage ";
		Query query = this.entityManager.createNativeQuery(sql);
		
		query.setParameter("case_stage", caseStage);

		CasePendingList1 = query.getResultList();
		if (CasePendingList1.isEmpty())
			return new ArrayList<OfficeOrder>();

		try {
			String sqlquerry = "Select e from " + OfficeOrder.class.getName() + " e " //
					+ " Where e.caseDetails in (" + StringUtils.join(CasePendingList1, ',') + ") and e.aprrovalStage1 =: aprrovalStage1 ";

			Query query1 = entityManager.createQuery(sqlquerry, OfficeOrder.class);
			query1.setParameter("aprrovalStage1", aprrovalStage1);
			listcase = query1.getResultList();
			if (listcase.isEmpty())
				return listcase;
			else
				return listcase;

		} catch (NoResultException e) {
			return listcase;
		}

		
	}
	@Override
	public List<OfficeOrder> findAllByDirAprrovalStage1(boolean aprrovalStage1) {
		List CasePendingList1;
		List<OfficeOrder> listcase = null;
		String sql = "select cd.id  from investigation.case_details cd ";
		Query query = this.entityManager.createNativeQuery(sql);
		
	

		CasePendingList1 = query.getResultList();
		if (CasePendingList1.isEmpty())
			return new ArrayList<OfficeOrder>();

		try {
			String sqlquerry = "Select e from " + OfficeOrder.class.getName() + " e " //
					+ " Where e.caseDetails in (" + StringUtils.join(CasePendingList1, ',') + ") and e.aprrovalStage1 =: aprrovalStage1 ";

			Query query1 = entityManager.createQuery(sqlquerry, OfficeOrder.class);
			query1.setParameter("aprrovalStage1", aprrovalStage1);
			listcase = query1.getResultList();
			if (listcase.isEmpty())
				return listcase;
			else
				return listcase;

		} catch (NoResultException e) {
			return listcase;
		}

		
	}
	@Override
	public List<OfficeOrder> findAllByAdoAprrovalStage1(boolean b, int caseStage) {
		List CasePendingList1;
		List<OfficeOrder> listcase = null;
		String sql = "select cd.id  from investigation.case_details cd where cd.case_stage=:case_stage ";
		Query query = this.entityManager.createNativeQuery(sql);
		
		query.setParameter("case_stage", caseStage);

		CasePendingList1 = query.getResultList();
		if (CasePendingList1.isEmpty())
			return new ArrayList<OfficeOrder>();

		try {
			String sqlquerry = "Select e from " + OfficeOrder.class.getName() + " e " //
					+ " Where e.caseDetails in (" + StringUtils.join(CasePendingList1, ',') + ") ";

			Query query1 = entityManager.createQuery(sqlquerry, OfficeOrder.class);

			listcase = query1.getResultList();
			if (listcase.isEmpty())
				return listcase;
			else
				return listcase;

		} catch (NoResultException e) {
			return listcase;
		}

	}

	@Override
	public List<Object[]> findPhySummonPendingForApproval(int i, Long userId, boolean isPhysicalOffline) {
		try {
			String sql = null;
		
			
			sql = "select DISTINCT ss.id,c.case_id,c.case_title,c.id caseid from investigation.summon_status ss, investigation.case_details c, \r\n" + 
					"investigation.inspector i where i.case_details_id=c.id and( i.app_user_user_id=:userId and i.is_ado=true  \r\n" + 
					" or (i.app_user_user_id in (select  regular_id_id from authentication.link_officer lo where lo.user_details_id=:user_details_id and   lo.is_active = true and CURRENT_DATE between lo.from_date and lo.to_date)))\r\n" + 
					"and ss.case_details_id = c.id and ss.aprroval_stage1=false and  ss.is_rejected = false and ss.is_physicallysent = true  ORDER BY ss.id DESC\r\n";
					
			
			/*
			 * sql =
			 * "select ss.id,c.case_id,c.case_title,c.id caseid from investigation.summon_status ss, investigation.case_details c, "
			 * +
			 * "investigation.inspector i where i.case_details_id=c.id and i.app_user_user_id=:user_id and i.is_ado=true "
			 * +
			 * "and ss.case_details_id = c.id and ss.aprroval_stage1=false and ss.is_rejected = false and ss.is_physicallysent = true ORDER BY ss.id DESC"
			 * ;
			 */	Query query = this.entityManager.createNativeQuery(sql);
				query.setParameter("userId", userId);
				query.setParameter("user_details_id", userId);
				return (List<Object[]>) query.getResultList();

		} catch (NoResultException e) {
			return null;
		}

	}

	@Override
	public List<Object[]> findPhyNoticePendingForApproval(int i, Long userId, boolean b) {
		try {
			String sql = null;
		

				sql = "select  ns.id,c.case_id,c.case_title,c.id caseid from investigation.notice_status ns, investigation.case_details c, \r\n" + 
						"investigation.inspector i where i.case_details_id=c.id and( i.app_user_user_id=:userId and i.is_ado=true  \r\n" + 
						" or (i.app_user_user_id in (select  regular_id_id from authentication.link_officer lo where lo.user_details_id=:user_details_id and   lo.is_active = true and CURRENT_DATE between lo.from_date and lo.to_date)))\r\n" + 
						"and ns.case_details_id = c.id and ns.aprroval_stage1=false and  ns.is_rejected = false and ns.is_physicallysent = true  ORDER BY ns.id DESC\r\n";
				Query query = this.entityManager.createNativeQuery(sql);
				query.setParameter("userId",  userId);
				query.setParameter("user_details_id",  userId);
				// gouthami 15/09/2020
				List<Object[]> NoticePendingList1 = query.getResultList();
				if (!NoticePendingList1.isEmpty())
					return NoticePendingList1;
				else
					return NoticePendingList1;
            } catch (NoResultException e) {
			return null;
		}
	}
	
	
	@Override
	public List<Object[]> findPhyNoticeSendBackforApproval(int i, Long userId, boolean b) {
		try {
			String sql = null;
		

				sql = "select ns.id,c.case_id,c.case_title,c.id caseid from investigation.notice_status ns, investigation.case_details c, "
						+ "investigation.inspector i where i.case_details_id=c.id and i.app_user_user_id=:user_id and i.is_ado=true "
						+ "and ns.case_details_id = c.id and ns.aprroval_stage1=false and  ns.is_rejected = true and ns.is_physicallysent = true ORDER BY ns.id DESC";
				Query query = this.entityManager.createNativeQuery(sql);
				query.setParameter("user_id",  userId);
				// gouthami 15/09/2020
				List<Object[]> NoticePendingList1 = query.getResultList();
				if (!NoticePendingList1.isEmpty())
					return NoticePendingList1;
				else
					return NoticePendingList1;
            } catch (NoResultException e) {
			return null;
		}
	}

	@Override
	public List<Object[]> findPhySummonSendBackforApproval(int i, Long userId, boolean b) {
		try {
			String sql = null;
		
				sql = "select ss.id,c.case_id,c.case_title,c.id caseid from investigation.summon_status ss, investigation.case_details c, "
						+ "investigation.inspector i where i.case_details_id=c.id and i.app_user_user_id=:user_id and i.is_ado=true "
						+ "and ss.case_details_id = c.id and ss.aprroval_stage1=false and ss.is_rejected = true and ss.is_physicallysent = true ORDER BY ss.id DESC";
            	Query query = this.entityManager.createNativeQuery(sql);
				query.setParameter("user_id", userId);
				return (List<Object[]>) query.getResultList();

		} catch (NoResultException e) {
			return null;
		}
	}

	@Override
	public List<UserDetails> findByRole(int roleUser) {
		try {
			// String sql = "Select * from " + UserDetails.class + " e Where e.unit =
			// "+UnitDetails.class;

			String sql = "Select e from " + UserDetails.class.getName() + " e " //
					+ " where e.designation.id !=1  and e.userId in ("
					+ "Select r.appUser from " + UserRole.class.getName() + " r " + "where r.appRole=" + roleUser
					+ ") order by e.firstName";

			Query query = entityManager.createQuery(sql, UserDetails.class);
			

			return (List<UserDetails>) query.getResultList();
		} catch (NoResultException e) {
			return null;
		}
	}

	@Override
	public List<Object[]> findOfficeOrderPendingForApproval() {
		try {
			String sql = null;
			
				sql = "select o.id ,c.case_id,c.case_title  ,c.id as caseID ,c.radio_value from investigation.office_order o, investigation.case_details c "
						+ "where o.case_details_id = c.id  and o.aprroval_stage1=false ORDER BY o.id DESC";
			

			Query query = entityManager.createNativeQuery(sql);
           
			List<Object[]> caseList =  query.getResultList();
			return caseList;
		} catch (NoResultException e) {
			return null;
		}

	}

	@Override
	public List<OfficeOrder> findAllByAdoAprroval(boolean b) {
		List CasePendingList1;
		List<OfficeOrder> listcase = null;
		String sql = "select cd.id  from investigation.case_details cd ";
		Query query = this.entityManager.createNativeQuery(sql);
		
		//query.setParameter("case_stage", caseStage);

		CasePendingList1 = query.getResultList();
		if (CasePendingList1.isEmpty())
			return new ArrayList<OfficeOrder>();

		try {
			String sqlquerry = "Select e from " + OfficeOrder.class.getName() + " e " //
					+ " Where e.caseDetails in (" + StringUtils.join(CasePendingList1, ',') + ")  order by id desc";

			Query query1 = entityManager.createQuery(sqlquerry, OfficeOrder.class);

			listcase = query1.getResultList();
			if (listcase.isEmpty())
				return listcase;
			else
				return listcase;

		} catch (NoResultException e) {
			return listcase;
		}
	}

	@Override
	public List<CaseDetails> findPeningForfarwarding() {
		List CasePendingList1;
		List<CaseDetails> listcase = null;
		String sql = "select oo.case_details_id  from investigation.office_order oo ";
		Query query = this.entityManager.createNativeQuery(sql);
		
		//query.setParameter("case_stage", caseStage);

		CasePendingList1 = query.getResultList();
		if (CasePendingList1.isEmpty())
			return new ArrayList<CaseDetails>();

		try {
			String sqlquerry = "Select e from " + CaseDetails.class.getName() + " e " //
					+ " Where e.id NOT IN (" + StringUtils.join(CasePendingList1, ',') + ")  order by id desc";

			Query query1 = entityManager.createQuery(sqlquerry, CaseDetails.class);

			listcase = query1.getResultList();
			if (listcase.isEmpty())
				return listcase;
			else
				return listcase;

		} catch (NoResultException e) {
			return listcase;
		}
	}

	@Override
	public List<Company> findCompanyPendingForApproval(int caseStage, Long userId, boolean is_active,
			long approved_status) {
		
		List compPendingList1;
		List<Company> listcomp = null;
		String sql = "select  comp.id from \r\n" + 
				"investigation.case_details c, investigation.company comp , investigation.inspector i where i.case_details_id=c.id and( i.app_user_user_id=:user_id and i.is_ado=true  \r\n" + 
				" or (i.app_user_user_id in (select  regular_id_id from authentication.link_officer lo where lo.user_details_id=:user_details_id and   lo.is_active = true and CURRENT_DATE between lo.from_date and lo.to_date))) and c.case_stage=:case_stage and c.id = comp.case_details_id and comp.is_active =false and comp.approved_status = 1\r\n"; 
				
		Query query = this.entityManager.createNativeQuery(sql);
		query.setParameter("user_id", userId);
		query.setParameter("user_details_id", userId);
		query.setParameter("case_stage", caseStage);

		compPendingList1 = query.getResultList();
		if (compPendingList1.isEmpty())
			return new ArrayList<Company>();

		try {
			String sqlquerry = "Select e from " + Company.class.getName() + " e " //
					+ " Where e.id in (" + StringUtils.join(compPendingList1, ',') + ") order By e.id DESC";

			Query query1 = entityManager.createQuery(sqlquerry, Company.class);

			listcomp = query1.getResultList();
			if (listcomp.isEmpty())
				return listcomp;
			else
				return listcomp;

		} catch (NoResultException e) {
			return listcomp;
		}
	}

	@Override
	public List<CaseDetails> findCompanyByCasePendingForApproval(int caseStage, Long userId, boolean b, long l) {
		List compPendingList1;
		List<CaseDetails> listcomp = null;
		String sql = "select  c.id from \r\n" + 
				"investigation.case_details c, investigation.company comp , investigation.inspector i where i.case_details_id=c.id and( i.app_user_user_id=:user_id and i.is_ado=true  \r\n" + 
				" or (i.app_user_user_id in (select  regular_id_id from authentication.link_officer lo where lo.user_details_id=:user_details_id and   lo.is_active = true and CURRENT_DATE between lo.from_date and lo.to_date))) and c.case_stage=:case_stage and c.id = comp.case_details_id and comp.is_active =false and comp.approved_status = 1\r\n"; 
				
		Query query = this.entityManager.createNativeQuery(sql);
		query.setParameter("user_id", userId);
		query.setParameter("user_details_id", userId);
		query.setParameter("case_stage", caseStage);

		compPendingList1 = query.getResultList();
		if (compPendingList1.isEmpty())
			return new ArrayList<CaseDetails>();

		try {
			String sqlquerry = "Select e from " + CaseDetails.class.getName() + " e " //
					+ " Where e.id in (" + StringUtils.join(compPendingList1, ',') + ") order By e.id DESC";

			Query query1 = entityManager.createQuery(sqlquerry, CaseDetails.class);

			listcomp = query1.getResultList();
			if (listcomp.isEmpty())
				return listcomp;
			else
				return listcomp;

		} catch (NoResultException e) {
			return listcomp;
		}
	}

	@Override
	public List<Company> findByPendingCompByCaseId(CaseDetails caseDetails) {
		
		
		
		String sql = "Select e from " + Company.class.getName() + " e " //
				+ " where e.caseDetails = : caseDetails and approved_status = 1 and isActive = false ";

		Query query = entityManager.createQuery(sql, Company.class);

		if (caseDetails != null) {
			query.setParameter("caseDetails", caseDetails);
		} else {
			query.setParameter("caseDetails", new CaseDetails());
		}
		return (List<Company>) query.getResultList();
	}

	@Override
	public List<CaseDetails> findCompanyByCasePendingForApprovalByADO(boolean is_active, long approved_status) {
		List compPendingList1;
		List<CaseDetails> listcomp = null;
		String sql = "select  c.id from \r\n" + 
				"investigation.case_details c, investigation.company comp , investigation.inspector i where i.case_details_id=c.id and(  i.is_ado=true  \r\n" + 
				" or (i.app_user_user_id in (select  regular_id_id from authentication.link_officer lo where    lo.is_active = true and CURRENT_DATE between lo.from_date and lo.to_date))) and c.id = comp.case_details_id and comp.is_active =false and comp.approved_status = 4\r\n"; 
				
		Query query = this.entityManager.createNativeQuery(sql);
		
		compPendingList1 = query.getResultList();
		if (compPendingList1.isEmpty())
			return new ArrayList<CaseDetails>();

		try {
			String sqlquerry = "Select e from " + CaseDetails.class.getName() + " e " //
					+ " Where e.id in (" + StringUtils.join(compPendingList1, ',') + ") order By e.id DESC";

			Query query1 = entityManager.createQuery(sqlquerry, CaseDetails.class);

			listcomp = query1.getResultList();
			if (listcomp.isEmpty())
				return listcomp;
			else
				return listcomp;

		} catch (NoResultException e) {
			return listcomp;
		}
	}

	@Override
	public List<Company> findByPendingCompByCaseId(CaseDetails caseDetails, String appRoleName) {
		String sql ; 
		
		if (appRoleName.equals("ROLE_ADMIN_OFFICER")) {
			sql = "Select e from " + Company.class.getName() + " e " //
					+ " where e.caseDetails = : caseDetails and approved_status = 4 and isActive = false ";
			
		}else {
		sql = "Select e from " + Company.class.getName() + " e " //
				+ " where e.caseDetails = : caseDetails and approved_status = 1 and isActive = false ";
		 }
		Query query = entityManager.createQuery(sql, Company.class);

		if (caseDetails != null) {
			query.setParameter("caseDetails", caseDetails);
		} else {
			query.setParameter("caseDetails", new CaseDetails());
		}
		return (List<Company>) query.getResultList();
	}


}
