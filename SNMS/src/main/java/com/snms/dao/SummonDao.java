package com.snms.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.apache.log4j.Logger;
import org.owasp.esapi.ESAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import org.thymeleaf.util.StringUtils;

import com.snms.dto.SummonReportDTO;
import com.snms.entity.AppUser;
import com.snms.entity.CaseDetails;
import com.snms.entity.CompanySummon;
import com.snms.entity.InitiateNoticeDraft;
import com.snms.entity.InitiateSummonDraft;
import com.snms.entity.NoticeStatus;
import com.snms.entity.PersonDATA;
import com.snms.entity.SummonDetails;
import com.snms.entity.SummonStatus;

import com.snms.entity.SummonType;
import com.snms.entity.UnitDetails;
import com.snms.entity.UserDetails;
import com.snms.entity.personcompanyApproval;
import com.snms.service.PersoncompanyApprovalRepository;
import com.snms.service.SummonRepository;
import com.snms.service.UserDeatilsRepository;
import com.snms.utils.SnmsException;
import com.snms.utils.SummonPDF;
import com.snms.validation.SNMSValidator;

import bsh.ParseException;

@Repository
@Transactional
public class SummonDao {
	@Autowired
	private SummonRepository summonRepo;
	private static final Logger logger = Logger.getLogger(SummonDao.class);
	@Autowired
	private EntityManager entityManager;
	@Autowired
	private AppRoleDAO roleDAO;
	@Autowired
	PersoncompanyApprovalRepository pcaRepo;
	@Autowired
	UserDeatilsRepository userdtlRepo;

	public Page<SummonStatus> summonlistAll(int pageNo, int pageSize, String sortField, String sortDirection,
			AppUser userDetails) {
		String role = roleDAO.getRoleName(userDetails.getUserId());
		Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending()
				: Sort.by(sortField).descending();

		Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
		if (role.equals("ROLE_DIRECTOR")) {
			return this.summonRepo.findAll(pageable);
		} else {
			return this.summonRepo.findAllByAppUser(pageable, userDetails);
		}
	}

	@SuppressWarnings("unchecked")
	public SummonDetails findByCaseId(Long caseId) {
		SNMSValidator snmsvalid = new SNMSValidator();

		// caseId= (long) 0;

		try {

			String sql = "Select e from " + SummonDetails.class.getName() + " e " //
					+ " Where e.caseId = :caseId";

			Query query = entityManager.createQuery(sql, SummonDetails.class);
			if (snmsvalid.getValidInteger(caseId))
				query.setParameter("caseId", caseId);
			else {
				return null;
			}
			List<SummonDetails> summonlist = query.getResultList();
			if (!summonlist.isEmpty())
				return summonlist.get(0);
			else
				return null;

			// return (SummonDetails) query.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	@SuppressWarnings({ "unchecked" })
	public List<Object[]> findListByUserId(Long userId) {
		SNMSValidator snmsvalid = new SNMSValidator();
		List<Object[]> ListByUserId;

		try {
			String sql = "select cd.id,cd.case_id,cd.case_title from investigation.summon_details sum,investigation.case_details cd,investigation.inspector i , investigation.office_order o where cd.id=sum.case_id and cd.id=i.case_details_id   and  i.is_active = true and i.app_user_user_id=:app_user_user_id and cd.id=o.case_details_id and o.is_signed=1";
			Query query = entityManager.createNativeQuery(sql);
			// gouthami 15/09/2020
			if (snmsvalid.getValidInteger(userId)) {
				query.setParameter("app_user_user_id", userId);
			} else {
				return null;
			}
			ListByUserId = query.getResultList();
			if (!ListByUserId.isEmpty())
				return ListByUserId;
			else
				return ListByUserId;

		} catch (NoResultException e) {
			return null;
		}
	}

	public void deleteSummonType(int editDeleteId) {
		Query query = entityManager.createNativeQuery("delete from investigation.Summon_Type where id= :editDeleteId");
		query.setParameter("editDeleteId", editDeleteId).executeUpdate();
	}

	@SuppressWarnings("unchecked")
	public List<Object[]> findListByIdCompanyType(Long caseId, String company, String type) {
		SNMSValidator snmsvalid = new SNMSValidator();

		if (snmsvalid.getValidInteger(caseId) || snmsvalid.getvalidCompany(company, true)
				|| snmsvalid.getValidInteger(Long.parseLong(type)))

			try {

				String sql = "select st.type,cm.company_name,st.reg_no,st.auditor_name,st.email,st.vendor_name,st.vendor_relation,st.vendor_email,st.dir_name,st.name,st.din,st.designation,st.off_email,st.isdirector,st.id,st.member_ship_num,st.member_name,member_email from investigation.summon_type st,investigation.company_summon cm,investigation.summon_details sd where sd.id=cm.entity_company_summon and cm.id=st.entity_summontype and sd.case_id=:caseId and cm.company_name=:company and st.type=:type";
				Query query = entityManager.createNativeQuery(sql);
				query.setParameter("caseId", caseId);
				query.setParameter("company", company);
				query.setParameter("type", type);
				return (List<Object[]>) query.getResultList();
			} catch (NoResultException e) {
				return null;
			}

		else
			return null;
	}

	// Added BY Gouthami
	@SuppressWarnings("unchecked")
	public List<Object[]> findListByIdCompanyTypeNew(Long caseId, String company, int type)
			throws SnmsException, IllegalAccessException {

		SNMSValidator snmsvalid = new SNMSValidator();

		try {

			String sql = "select st.individual_type_individual_id,cm.company_name,st.registration_no,st.name,st.email,st.relationwithcompany,st.designation,st.is_cin,st.name_Company ,st.id from investigation.Summon_Type st,investigation.company_summon cm,investigation.summon_details sd where sd.id=cm.entity_company_summon and cm.id=st.entity_summontype and sd.case_id=:caseId and cm.company_name=:company and st.individual_type_individual_id=:individual_type_individual_id";
			Query query = entityManager.createNativeQuery(sql);
			if (snmsvalid.getValidInteger(caseId) && snmsvalid.getvalidCompany(company, true)
					&& snmsvalid.getValidInteger(type)) {
				query.setParameter("caseId", caseId);
				query.setParameter("company", company);
				query.setParameter("individual_type_individual_id", type);
			} else {

				throw new IllegalAccessException("demo");
			}
			return (List<Object[]>) query.getResultList();
		} catch (NoResultException e) {

			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public Object[] getCompanyByName(String companyName) {
		SNMSValidator snmsvalid = new SNMSValidator();

		try {
			String sql = "select cin,address,email,company_type_company_type_id from investigation.company_summon where entity_company_summon is not null and company_name=:company_name";
			Query query = entityManager.createNativeQuery(sql);
			if (snmsvalid.getvalidCompany(companyName, true))
				query.setParameter("company_name", companyName);
			else
				throw new IllegalAccessException("Invalid Company Name");
			List<Object[]> listCompany = query.getResultList();
			if (listCompany.size() > 0)
				return listCompany.get(0);
			else
				return null;
		} catch (NoResultException | IllegalAccessException e) {
			List<Object[]> list = null;
			list.add(e.getStackTrace());

			return list.get(0);
		}
	}

	@SuppressWarnings("unchecked")
	public InitiateSummonDraft findSummonDraftByCaseDetails(CaseDetails caseDetails, SummonType summonType) {
		try {
			String sql = "Select i from " + InitiateSummonDraft.class.getName() + " i " //
					+ " Where i.isFinal =false and i.caseDetails=:caseDetails and i.summonType=:summonType ";
			Query query = entityManager.createQuery(sql, InitiateSummonDraft.class);
			if (caseDetails != null) {
				query.setParameter("caseDetails", caseDetails);
			} else {
				query.setParameter("caseDetails", new CaseDetails());
			}
			if (summonType != null) {
				query.setParameter("summonType", summonType);
			} else {
				query.setParameter("summonType", new SummonType());
			}

			List<InitiateSummonDraft> summonlist = query.getResultList();
			if (!summonlist.isEmpty())
				return summonlist.get(summonlist.size() - 1);
			else
				return null;

			// return (SummonDetails) query.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	// change BY gouthami
	@SuppressWarnings("unchecked")
	public InitiateNoticeDraft findNoticeDraftByCaseDetails(CaseDetails caseDetails, SummonType summonType) {
		try {
			String sql = "Select i from " + InitiateNoticeDraft.class.getName() + " i " //
					+ " Where i.isFinal =false and i.caseDetails=:caseDetails and i.summonType=:summonType";

			Query query = entityManager.createQuery(sql, InitiateNoticeDraft.class);
			if (caseDetails != null) {
				query.setParameter("caseDetails", caseDetails);
			} else {
				query.setParameter("caseDetails", new CaseDetails());
			}
			if (summonType != null) {
				query.setParameter("summonType", summonType);
			} else {
				query.setParameter("summonType", new SummonType());
			}
			List<InitiateNoticeDraft> summonlist = query.getResultList();
			if (!summonlist.isEmpty())
				return summonlist.get(summonlist.size() - 1);
			else
				return null;

			// return (SummonDetails) query.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}
	@SuppressWarnings("unchecked")
	public InitiateNoticeDraft findNoticeDraftByCaseDetails1(CaseDetails caseDetails, SummonType summonType) {
		try {
			String sql = "Select i from " + InitiateNoticeDraft.class.getName() + " i " //
					+ " Where i.isFinal =true and i.caseDetails=:caseDetails and i.summonType=:summonType";

			Query query = entityManager.createQuery(sql, InitiateNoticeDraft.class);
			if (caseDetails != null) {
				query.setParameter("caseDetails", caseDetails);
			} else {
				query.setParameter("caseDetails", new CaseDetails());
			}
			if (summonType != null) {
				query.setParameter("summonType", summonType);
			} else {
				query.setParameter("summonType", new SummonType());
			}
			List<InitiateNoticeDraft> summonlist = query.getResultList();
			if (!summonlist.isEmpty())
				return summonlist.get(summonlist.size() - 1);
			else
				return null;

			// return (SummonDetails) query.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}
	// Gouthami

	@SuppressWarnings("unchecked")
	public InitiateNoticeDraft findNoticeDraftByCaseDetailsNew(CaseDetails caseDetails, SummonType summonType) {
		try {
			String sql = "Select i from " + InitiateNoticeDraft.class.getName() + " i " //
					+ " Where i.isFinal =false and i.caseDetails=:caseDetails and i.summonType=:summonType";

			Query query = entityManager.createQuery(sql, InitiateNoticeDraft.class);
			if (caseDetails != null) {
				query.setParameter("caseDetails", caseDetails);
			} else {
				query.setParameter("caseDetails", new CaseDetails());
			}
			if (summonType != null) {
				query.setParameter("summonType", summonType);
			} else {
				query.setParameter("summonType", new SummonType());
			}
			List<InitiateNoticeDraft> summonlist = query.getResultList();
			if (!summonlist.isEmpty())
				return summonlist.get(summonlist.size() - 1);
			else
				return null;

			// return (SummonDetails) query.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	
	@SuppressWarnings("unchecked")
	public Object[] getCompanyByCin(String cinNum) {
		try {
			String sql = "select companynameincaps,cin from public.cdm_master where cin=:cin";
			Query query = entityManager.createNativeQuery(sql);
			// gouthami 15/09/2020
			query.setParameter("cin", cinNum);
			List<Object[]> listCompany = query.getResultList();
			if (listCompany.size() > 0)
				return listCompany.get(0);
			else
				return null;
		} catch (NoResultException e) {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public Object[] getIcaiDataByMemberNo(String memberNo) {
		try {
			String sql = "select name,official_email_address,official_mobile_number,official_correspondence_address from public.icsi where membership_number=:membership_number and "
					+ " premembno=:premembno ";
			Query query = entityManager.createNativeQuery(sql);
			// gouthami 15/09/2020
			query.setParameter("membership_number", memberNo.substring(1));
			query.setParameter("premembno", memberNo.charAt(0));

			List<Object[]> listCompany = query.getResultList();
			if (listCompany.size() > 0)
				return listCompany.get(0);
			else
				return null;
		} catch (NoResultException e) {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public CompanySummon findCompanyById(int id) {
		try {
			String sql = "Select e from " + CompanySummon.class.getName() + " e " //
					+ " Where e.id = :id";

			Query query = entityManager.createQuery(sql, CompanySummon.class);
			query.setParameter("id", id);
			List<CompanySummon> summonlist = query.getResultList();
			if (!summonlist.isEmpty())
				return summonlist.get(0);
			else
				return null;

			// return (SummonDetails) query.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	/*
	 * @SuppressWarnings("unchecked") public List<Object[]>
	 * findSummonsByApproveSenstive(AppUser appUser) { try { String sql =
	 * "select st.summon_no,st.date_of_appear,st.created_date,st.id from investigation.inspector i,investigation.summon_status st where i.isio=true and i.case_details_id=st.case_details_id and i.app_user_user_id=:appUser and st.is_signed=:isSigned and st.aprroval_stage2=true"
	 * ; Query query = entityManager.createNativeQuery(sql);
	 * query.setParameter("appUser", appUser); query.setParameter("isSigned", 0);
	 * return (List<Object[]>) query.getResultList(); } catch (NoResultException e)
	 * { return null; } }
	 */

	@SuppressWarnings("unchecked")
	public List<Object[]> findSummonsByApproveSenstive() {
		try {
			String sql = "select distinct st.summon_no,st.date_of_appear,st.created_date,st.id,cd.case_title,st.summon_din from investigation.inspector i,investigation.summon_status st,investigation.case_details cd where cd.id=st.case_details_id and i.case_details_id=st.case_details_id and st.is_signed=:isSigned and st.aprroval_stage2=true and st.aprroval_stage1=true";
			Query query = entityManager.createNativeQuery(sql);
			// query.setParameter("appUser", appUser);
			query.setParameter("isSigned", 0);
			return (List<Object[]>) query.getResultList();
		} catch (NoResultException e) {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public List<Object[]> findAllSignedSummons() {
		try {
			String sql = "select cd.case_id,cd.case_title,o.id,o.order_signed_date,o.summon_no from investigation.summon_status o,investigation.case_details cd where cd.id=o.case_details_id and o.is_signed=1";
			Query query = this.entityManager.createNativeQuery(sql);
			return (List<Object[]>) query.getResultList();
		} catch (NoResultException e) {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public List<Object[]> findAllSignedSummonsOrders() {
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
					+ "   where   o.is_signed=1";
			Query query = this.entityManager.createNativeQuery(sql);

			return (List<Object[]>) query.getResultList();
		} catch (NoResultException e) {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public List<Object[]> findSignedSummons(AppUser userId) {
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
					+ "   where   i.app_user_user_id=:userId and o.is_signed=1";
			Query query = this.entityManager.createNativeQuery(sql);
			if (userId != null) {
				query.setParameter("userId", userId.getUserId());
			} else {
				query.setParameter("userId", new AppUser().getUserId());
			}

			return (List<Object[]>) query.getResultList();
		} catch (NoResultException e) {
			return null;
		}
	}

	/*
	 * @SuppressWarnings("unchecked") public List<Object[]>
	 * findSignedSummons(AppUser userId) { try { String sql =
	 * "select distinct cd.case_id,cd.case_title,o.id,o.order_signed_date,o.summon_no from investigation.summon_status o,investigation.case_details cd,investigation.inspector i where cd.id=o.case_details_id and i.app_user_user_id=:userId and o.is_signed=1"
	 * ; Query query = this.entityManager.createNativeQuery(sql);
	 * query.setParameter("userId", userId); return (List<Object[]>)
	 * query.getResultList(); } catch (NoResultException e) { return null; } }
	 */

	@SuppressWarnings("unchecked")
	public String findCompanyNameById(int id) {
		try {
			// String sql = "select cs.company_name from investigation.company_summon
			// cs,investigation.summon_type st where cs.id=st.entity_summontype and
			// st.id="+id;
			String sql = "select cs.company_name from investigation.company_summon cs,investigation.summon_type st where cs.id=st.entity_summontype and st.id=:id";

			Query query = entityManager.createNativeQuery(sql);
			// gouthami 15/09/2020
			query.setParameter("id", id);
			List<String> company = query.getResultList();
			if (company.size() > 0)
				return company.get(0);
			else
				return null;
		} catch (NoResultException e) {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public List<Object[]> getIndividualType(int compId) {
		try {

			String sql = "select st.auditor_name,st.vendor_name,st.dir_name,st.name,st.id,st.member_name from investigation.summon_type st where st.entity_summontype=:compId";
			Query query = entityManager.createNativeQuery(sql);
			query.setParameter("compId", compId);
			return (List<Object[]>) query.getResultList();
		} catch (NoResultException e) {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public List<NoticeStatus> getNoticeBySummonType(SummonType summonType) {
		try {
			String sql = "Select e from " + NoticeStatus.class.getName() + " e " //
					+ " Where e.summonType = :summonType";

			Query query = entityManager.createQuery(sql, NoticeStatus.class);

			if (summonType != null) {
				query.setParameter("summonType", summonType);
			} else {
				query.setParameter("summonType", new SummonType());
			}
			List<NoticeStatus> listNotice = query.getResultList();
			if (!listNotice.isEmpty())
				return listNotice;
			else
				return null;

			// return (SummonDetails) query.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	/*
	 * public List<SummonStatus> getSummonBySmnTypId(SummonType summonType) { try {
	 * String sql = "Select e from " + SummonStatus.class.getName() + " e " // +
	 * " Where e.summonType = :summonType";
	 * 
	 * Query query = entityManager.createQuery(sql, SummonStatus.class);
	 * query.setParameter("summonType", summonType); List<SummonStatus>
	 * listSummon=query.getResultList(); if(!listSummon.isEmpty()) return
	 * listSummon; else return null;
	 * 
	 * //return (SummonDetails) query.getSingleResult(); } catch (NoResultException
	 * e) { return null; } }
	 */

	@SuppressWarnings("unchecked")
	public List<InitiateSummonDraft> getSummonBySmnTypId(SummonType summonType) {
		try {
			String sql = "Select e from " + InitiateSummonDraft.class.getName() + " e " //
					+ " Where e.summonType = :summonType order by createdDate desc";

			Query query = entityManager.createQuery(sql, InitiateSummonDraft.class);

			if (summonType != null) {
				query.setParameter("summonType", summonType);
			} else {
				query.setParameter("summonType", new SummonType());
			}
			query.setMaxResults(1);
			List<InitiateSummonDraft> listSummon = query.getResultList();
			if (!listSummon.isEmpty())
				return listSummon;
			else
				return null;

			// return (SummonDetails) query.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public List<Object[]> getDraftedSummon(AppUser appUser) {
		try {

			String sql = "select cd.id,cd.case_id,cd.case_title,isd.summon_type_id from investigation.initiate_summon_draft isd,investigation.inspector ins,investigation.case_details cd where cd.id=isd.case_details_id and isd.case_details_id=ins.case_details_id and ins.app_user_user_id=:userId and isd.summon_no not in "
					+ " (select ss.summon_no from investigation.summon_status ss ) order by created_date desc";
			Query query = entityManager.createNativeQuery(sql);
			if (appUser != null) {
				query.setParameter("userId", appUser.getUserId());
			} else {
				query.setParameter("userId", new AppUser().getUserId());
			}
			return (List<Object[]>) query.getResultList();
		} catch (NoResultException e) {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public List<Object[]> getDraftedNotice(AppUser appUser) {
		try {

			String sql = "select cd.id,cd.case_id,cd.case_title,ind.summon_type_id from investigation.initiate_notice_draft ind,investigation.inspector ins,investigation.case_details cd where cd.id=ind.case_details_id and ind.case_details_id=ins.case_details_id and ins.app_user_user_id=:userId and ind.summon_no not in "
					+ " (select ns.summon_no from investigation.notice_status ns ) order by created_date desc";
			Query query = entityManager.createNativeQuery(sql);
			if (appUser != null) {
				query.setParameter("userId", appUser.getUserId());
			} else {
				query.setParameter("userId", new AppUser().getUserId());
			}
			return (List<Object[]>) query.getResultList();
		} catch (NoResultException e) {
			return null;
		}
	}

	public String findCompanyNameBySummonTypeId(int sumtypeId) {
		try {
			sumtypeId = 0;
			SNMSValidator snmsvalid = new SNMSValidator();
			String sql = "select cs.company_name from investigation.summon_type st,investigation.company_summon cs where cs.id=st.entity_summontype and st.id=:sumtypeId";
			Query query = entityManager.createNativeQuery(sql);
			if (snmsvalid.getValidInteger(sumtypeId)) {
				query.setParameter("sumtypeId", sumtypeId);
				return (String) query.getResultList().get(0);
			} else {
				return null;
			}

		} catch (NoResultException e) {
			return null;
		}

	}

	@SuppressWarnings("unchecked")
	public List<Object[]> findSummonsByUserId(AppUser userDetails) {
		try {
			String sql = "select distinct st.summon_no,st.date_of_appear,st.created_date,st.id,cd.case_title,st.summon_din from investigation.inspector i,investigation.summon_status st,investigation.case_details cd where cd.id=st.case_details_id and i.case_details_id=st.case_details_id and st.is_signed=:isSigned and st.aprroval_stage2=true and i.app_user_user_id=:userId";
			Query query = entityManager.createNativeQuery(sql);
			// query.setParameter("appUser", appUser);
			query.setParameter("isSigned", 0);
			if (userDetails != null) {
				query.setParameter("userId", userDetails.getUserId());
			} else {
				query.setParameter("userId", new AppUser().getUserId());
			}
			List<Object[]> SummonsByUserIdList = query.getResultList();
			if (!SummonsByUserIdList.isEmpty())
				return SummonsByUserIdList;
			else
				return null;
		} catch (NoResultException e) {
			return null;
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked", "unused" })
	public List<SummonStatus> findSummonByUserId(AppUser userDetails) {
		try {

			String role = roleDAO.getRoleName(userDetails.getUserId());
			List listId;
			if (role.equals("ROLE_DIRECTOR")) {
				String nativesql = "select ss.id from investigation.summon_status ss,investigation.inspector ins where ss.case_details_id=ins.case_details_id ";
				Query nativequery = entityManager.createNativeQuery(nativesql);

				listId = nativequery.getResultList();
			} else {
				String nativesql = "select ss.id from investigation.summon_status ss,investigation.inspector ins where ss.case_details_id=ins.case_details_id and ins.app_user_user_id=:userId  and ins.is_active = true ";
				Query nativequery = entityManager.createNativeQuery(nativesql);
				if (userDetails != null) {
					nativequery.setParameter("userId", userDetails.getUserId());
				} else {
					nativequery.setParameter("userId", new AppUser().getUserId());
				}
				listId = nativequery.getResultList();
			}

			if (listId.isEmpty())
				return new ArrayList<SummonStatus>();

			String sql = "Select e from " + SummonStatus.class.getName() + " e " //
					+ " Where e.id in (" + StringUtils.join(listId, ',') + ")";

			Query query = entityManager.createQuery(sql, SummonStatus.class);
//	            query.setParameter("listId", StringUtils.join(listId, ','));
			List<SummonStatus> listSummon = query.getResultList();
			if (!listSummon.isEmpty())
				return listSummon;
			else
				return null;

		} catch (NoResultException e) {
			return null;
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<SummonStatus> findSummonsByApprove() {
		try {
			String nativesql = "select distinct st.id from investigation.inspector i,investigation.summon_status st,investigation.case_details cd where cd.id=st.case_details_id and i.case_details_id=st.case_details_id and st.is_signed= 0 and st.aprroval_stage2=true";
			Query nativequery = entityManager.createNativeQuery(nativesql);
			// query.setParameter("appUser", appUser);
			// nativequery.setParameter("isSigned", 0);
			List listId = nativequery.getResultList();
			if (listId.isEmpty())
				return new ArrayList<SummonStatus>();

			String sql = "Select e from " + SummonStatus.class.getName() + " e " //
					+ " Where e.id in (" + StringUtils.join(listId, ',') + ")";

			Query query = entityManager.createQuery(sql, SummonStatus.class);
//		            query.setParameter("listId", StringUtils.join(listId, ','));
			List<SummonStatus> listSummon = query.getResultList();
			if (!listSummon.isEmpty())
				return listSummon;
			else
				return null;
		} catch (NoResultException e) {
			return null;
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked", "unused" })
	public List<NoticeStatus> findNoticeByUserId(AppUser userDetails) {
		try {

			String role = roleDAO.getRoleName(userDetails.getUserId());
			List listId;
			if (role.equals("ROLE_DIRECTOR")) {
				String nativesql = "select ss.id from investigation.notice_status ss,investigation.inspector ins where ss.case_details_id=ins.case_details_id ";
				Query nativequery = entityManager.createNativeQuery(nativesql);

				listId = nativequery.getResultList();
			} else {
				String nativesql = "select ss.id from investigation.notice_status ss,investigation.inspector ins where ss.case_details_id=ins.case_details_id and ins.app_user_user_id=:userId   and ins.is_active = true ";
				Query nativequery = entityManager.createNativeQuery(nativesql);
				if (userDetails != null) {
					nativequery.setParameter("userId", userDetails.getUserId());

				} else {
					nativequery.setParameter("userId", new AppUser().getUserId());

				}
				listId = nativequery.getResultList();
			}

			// String nativesql = " select ss.id from investigation.summon_status
			// ss,investigation.inspector ins where ss.case_details_id=ins.case_details_id
			// and ins.app_user_user_id=:userId";

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

	@SuppressWarnings({ "rawtypes", "unchecked", "unused" })
	public List<personcompanyApproval> findPersonStatusByUserId(AppUser userDetails) {
		try {

			String role = roleDAO.getRoleName(userDetails.getUserId());
			List listId;
			if (role.equals("ROLE_DIRECTOR")) {
				String nativesql = "select ss.id from gams.personcompany_approval ss,investigation.inspector ins ,gams.relation_person_company pc  where pc.case_details_id=ins.case_details_id ";
				Query nativequery = entityManager.createNativeQuery(nativesql);

				listId = nativequery.getResultList();
			} else if (role.equals("ROLE_PROSECUTION_ADMIN")) {
				String nativesql = "select approval_status_id from gams.personcompany_approval ss, gams.relation_person_company pc ,investigation.case_details cd  where pc.case_details_id=cd.id and ss.id= pc.rpc_id and cd.case_stage = 2 ";
				Query nativequery = entityManager.createNativeQuery(nativesql);

				listId = nativequery.getResultList();
			}

			else if (role.equals("ROLE_PROSECUTION")) {
				String nativesql = "select approval_status_id from gams.personcompany_approval ss, gams.relation_person_company pc \r\n"
						+ " where  ss.created_by_id=:userId and pc.rpc_id =ss.id";
				Query nativequery = entityManager.createNativeQuery(nativesql);
				if (userDetails != null) {
					nativequery.setParameter("userId", userDetails.getUserId());

				} else {
					nativequery.setParameter("userId", new AppUser().getUserId());

				}
				listId = nativequery.getResultList();
			} else {
				String nativesql = "select approval_status_id from gams.personcompany_approval ss,investigation.inspector ins , gams.relation_person_company pc \r\n"
						+ " where pc.case_details_id=ins.case_details_id and ins.app_user_user_id=:userId and pc.rpc_id =ss.id \r\n"
						+ "and ins.is_active = true ";
				Query nativequery = entityManager.createNativeQuery(nativesql);
				if (userDetails != null) {
					nativequery.setParameter("userId", userDetails.getUserId());

				} else {
					nativequery.setParameter("userId", new AppUser().getUserId());

				}
				listId = nativequery.getResultList();
			}

			// String nativesql = " select ss.id from investigation.summon_status
			// ss,investigation.inspector ins where ss.case_details_id=ins.case_details_id
			// and ins.app_user_user_id=:userId";

			if (listId.isEmpty())
				return new ArrayList<personcompanyApproval>();
			String sql = "Select e from " + personcompanyApproval.class.getName() + " e " //
					+ " Where e.id in (" + StringUtils.join(listId, ',') + ")";

			Query query = entityManager.createQuery(sql, personcompanyApproval.class);
//	            query.setParameter("listId", StringUtils.join(listId, ','));
			List<personcompanyApproval> listPerson = query.getResultList();
			if (!listPerson.isEmpty())
				return listPerson;
			else
				return null;

		} catch (NoResultException e) {
			return null;
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<SummonStatus> findAllSummonByUserId(AppUser userDetails) {
		try {
			String nativesql = "select distinct st.id from investigation.inspector i,investigation.summon_status st,investigation.case_details cd where cd.id=st.case_details_id and i.case_details_id=st.case_details_id and st.is_signed=:isSigned and st.aprroval_stage2=true and i.app_user_user_id=:userId  and  i.is_active = true";

			Query nativequery = entityManager.createNativeQuery(nativesql);
			nativequery.setParameter("isSigned", 0);
			if (userDetails != null) {
				nativequery.setParameter("userId", userDetails.getUserId());
			} else {
				nativequery.setParameter("userId", new AppUser().getUserId());
			}
			List listId = nativequery.getResultList();
			if (listId.isEmpty())
				return new ArrayList<SummonStatus>();
			String sql = "Select e from " + SummonStatus.class.getName() + " e " //
					+ " Where e.id in (" + StringUtils.join(listId, ',') + ") Order by e.approvalDate desc";

			Query query = entityManager.createQuery(sql, SummonStatus.class);
//	            query.setParameter("listId", StringUtils.join(listId, ','));
			List<SummonStatus> listSummon = query.getResultList();
			if (!listSummon.isEmpty())
				return listSummon;
			else
				return null;

		} catch (NoResultException e) {
			return null;
		}
	}

	@SuppressWarnings({ "rawtypes", "unused" })
	public List<SummonStatus> findSummon_signed(AppUser userDetails) {
		try {

			String role = roleDAO.getRoleName(userDetails.getUserId());
			List listId;
			if (role.equals("ROLE_DIRECTOR")) {
				String nativesql = "select ss.id from investigation.summon_status ss,investigation.inspector ins where ss.case_details_id=ins.case_details_id and ss.is_signed=1 ";
				Query nativequery = entityManager.createNativeQuery(nativesql);

				listId = nativequery.getResultList();
			} else {
				String nativesql = "select ss.id from investigation.summon_status ss,investigation.inspector ins where ss.case_details_id=ins.case_details_id and ins.app_user_user_id=:userId and ss.is_signed=1  and ins.is_active = true";
				Query nativequery = entityManager.createNativeQuery(nativesql);
				if (userDetails != null) {
					nativequery.setParameter("userId", userDetails.getUserId());
				} else {
					nativequery.setParameter("userId", new AppUser().getUserId());
				}

				listId = nativequery.getResultList();
			}

			if (listId.isEmpty())
				return new ArrayList<SummonStatus>();

			String sql = "Select e from " + SummonStatus.class.getName() + " e " //
					+ " Where e.id in (" + StringUtils.join(listId, ',') + ") order by e.id desc";

			Query query = entityManager.createQuery(sql, SummonStatus.class);
//	            query.setParameter("listId", StringUtils.join(listId, ','));
			@SuppressWarnings("unchecked")
			List<SummonStatus> listSummon = query.getResultList();
			if (!listSummon.isEmpty())
				return listSummon;
			else
				return null;

		} catch (NoResultException e) {
			return null;
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked", "unused" })
	public List<NoticeStatus> findNotice_signed(AppUser userDetails) {
		try {

			String role = roleDAO.getRoleName(userDetails.getUserId());
			List listId;
			if (role.equals("ROLE_DIRECTOR")) {
				String nativesql = "select ss.id from investigation.notice_status ss,investigation.inspector ins where ss.case_details_id=ins.case_details_id and ss.is_signed=1 and ss.aprroval_stage2=true and ss.aprroval_stage1=true";
				Query nativequery = entityManager.createNativeQuery(nativesql);

				listId = nativequery.getResultList();
			} else {
				String nativesql = "select ss.id from investigation.notice_status ss,investigation.inspector ins where ss.case_details_id=ins.case_details_id and ins.app_user_user_id=:userId and ss.is_signed=1   and ins.is_active = true";
				Query nativequery = entityManager.createNativeQuery(nativesql);
				if (userDetails != null) {
					nativequery.setParameter("userId", userDetails.getUserId());
				} else {
					nativequery.setParameter("userId", new AppUser().getUserId());
				}
				listId = nativequery.getResultList();
			}

			if (listId.isEmpty())
				return new ArrayList<NoticeStatus>();

			String sql = "Select e from " + NoticeStatus.class.getName() + " e " //
					+ " Where e.id in (" + StringUtils.join(listId, ',') + ") order by e.id desc";

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

	@SuppressWarnings("unchecked")
	public NoticeStatus findNotice_byDIN(String DIN) {
		try {

			String sql = "Select e from " + NoticeStatus.class.getName() + " e " + " where e.noticeDin =:noticeDin ";

			Query query = entityManager.createQuery(sql, NoticeStatus.class);
			query.setParameter("noticeDin", DIN);
			List<NoticeStatus> listSummon = query.getResultList();
			if (!listSummon.isEmpty())
				return listSummon.get(0);
			else
				return null;

		} catch (NoResultException e) {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public SummonStatus findSummon_byDIN(String DIN) {
		try {

			String sql = "Select e from " + SummonStatus.class.getName() + " e " + " where e.summonDin =: summonDin";

			Query query = entityManager.createQuery(sql, SummonStatus.class);
			query.setParameter("summonDin", DIN);
			List<SummonStatus> listSummon = query.getResultList();
			if (!listSummon.isEmpty())
				return listSummon.get(0);
			else
				return null;

		} catch (NoResultException e) {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public SummonStatus getSummonStatusBysummonId(SummonType summonType) {
		try {
			String sql = "Select e from " + SummonStatus.class.getName() + " e " //
					+ " Where e.summonType = :summonType";

			Query query = entityManager.createQuery(sql, SummonStatus.class);
			if (summonType != null) {
				query.setParameter("summonType", summonType);
			} else {
				query.setParameter("summonType", new SummonType());
			}
			List<SummonStatus> listSummon = query.getResultList();
			if (!listSummon.isEmpty())
				return listSummon.get(0);
			else
				return null;

			// return (SummonDetails) query.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	public int findByName(String firstname) {
		try {
			/*
			 * String sql = "Select e from " + SummonType.class.getName() + " e " // +
			 * "where e.Name = :Name";
			 */

			String sql = "SELECT  e.entity_summontype from  investigation.summon_type e where  e.name like'%"
					+ firstname + "'";
			Query query = entityManager.createNativeQuery(sql);

			List<Integer> listSummon = query.getResultList();
			if (!listSummon.isEmpty())
				return listSummon.get(0);
			else
				return 0;

			// return (SummonDetails) query.getSingleResult();
		} catch (NoResultException e) {
			return 0;
		}
	}

	public List<Object[]> findByPanOrPassport(String panNumber, String passportNumber, String aadharNo) {

		String sql = "";
		if (!panNumber.equalsIgnoreCase("") && passportNumber.equalsIgnoreCase("") && aadharNo.equalsIgnoreCase("")) {

			try {
				sql = "select  cm.company_name , cm.cin ,cd.case_id ,it.individual_name  ,sd.id ,sd.name from  investigation.summon_type sd \r\n"
						+ "INNER JOIN investigation.company_summon cm\r\n" + "ON sd.entity_summontype = cm.id  \r\n"
						+ "INNER JOIN investigation.individual_type it\r\n"
						+ "On it.individual_id = sd.individual_type_individual_id\r\n"
						+ "INNER JOIN    investigation.summon_details st\r\n"
						+ "on  cm.entity_company_summon = st.id \r\n"
						+ "INNER JOIN    investigation.case_details cd\r\n" + "on  st.case_id = cd.id \r\n"
						+ "where  sd.pan_Number =:pan_Number  ";

				Query nativequery = entityManager.createNativeQuery(sql);
				nativequery.setParameter("pan_Number", panNumber);
				/*
				 * nativequery.setParameter("passportNumber", passportNumber);
				 * nativequery.setParameter("aadharNo", aadharNo);
				 */

				return (List<Object[]>) nativequery.getResultList();
			}

			catch (NoResultException e) {
				return null;
			}
		} else if (!panNumber.equalsIgnoreCase("") && !passportNumber.equalsIgnoreCase("")
				&& aadharNo.equalsIgnoreCase("")) {
			try {
				sql = "select  cm.company_name , cm.cin ,cd.case_id ,it.individual_name  ,sd.id from  investigation.summon_type sd \r\n"
						+ "INNER JOIN investigation.company_summon cm\r\n" + "ON sd.entity_summontype = cm.id  \r\n"
						+ "INNER JOIN investigation.individual_type it\r\n"
						+ "On it.individual_id = sd.individual_type_individual_id\r\n"
						+ "INNER JOIN    investigation.summon_details st\r\n"
						+ "on  cm.entity_company_summon = st.id \r\n"
						+ "INNER JOIN    investigation.case_details cd\r\n" + "on  st.case_id = cd.id \r\n"
						+ "where  sd.pan_Number =:pan_Number or sd.passport =:passportNumber ";

				Query nativequery = entityManager.createNativeQuery(sql);
				nativequery.setParameter("pan_Number", panNumber);
				nativequery.setParameter("passportNumber", passportNumber);

				return (List<Object[]>) nativequery.getResultList();
			}

			catch (NoResultException e) {
				return null;
			}
		} else if (!panNumber.equalsIgnoreCase("") && !aadharNo.equalsIgnoreCase("")
				&& passportNumber.equalsIgnoreCase("")) {
			try {
				sql = "select  cm.company_name , cm.cin ,cd.case_id ,it.individual_name  ,sd.id from  investigation.summon_type sd \r\n"
						+ "INNER JOIN investigation.company_summon cm\r\n" + "ON sd.entity_summontype = cm.id  \r\n"
						+ "INNER JOIN investigation.individual_type it\r\n"
						+ "On it.individual_id = sd.individual_type_individual_id\r\n"
						+ "INNER JOIN    investigation.summon_details st\r\n"
						+ "on  cm.entity_company_summon = st.id \r\n"
						+ "INNER JOIN    investigation.case_details cd\r\n" + "on  st.case_id = cd.id \r\n"
						+ "where  sd.pan_Number =:pan_Number or sd.aadhar_number =:aadharNo ";

				Query nativequery = entityManager.createNativeQuery(sql);
				nativequery.setParameter("pan_Number", panNumber);
				nativequery.setParameter("aadharNo", aadharNo);

				return (List<Object[]>) nativequery.getResultList();
			}

			catch (NoResultException e) {
				return null;
			}
		} else if (!passportNumber.equalsIgnoreCase("") && panNumber.equalsIgnoreCase("")
				&& aadharNo.equalsIgnoreCase("")) {
			try {
				sql = "select  cm.company_name , cm.cin ,cd.case_id ,it.individual_name  ,sd.id from  investigation.summon_type sd \r\n"
						+ "INNER JOIN investigation.company_summon cm\r\n" + "ON sd.entity_summontype = cm.id  \r\n"
						+ "INNER JOIN investigation.individual_type it\r\n"
						+ "On it.individual_id = sd.individual_type_individual_id\r\n"
						+ "INNER JOIN    investigation.summon_details st\r\n"
						+ "on  cm.entity_company_summon = st.id \r\n"
						+ "INNER JOIN    investigation.case_details cd\r\n" + "on  st.case_id = cd.id \r\n"
						+ "where  sd.passport =:passport ";

				Query nativequery = entityManager.createNativeQuery(sql);
				nativequery.setParameter("passport", passportNumber);

				return (List<Object[]>) nativequery.getResultList();
			}

			catch (NoResultException e) {
				return null;
			}
		} else if (passportNumber.equalsIgnoreCase("") && panNumber.equalsIgnoreCase("")
				&& !aadharNo.equalsIgnoreCase("")) {
			try {
				sql = "select  cm.company_name , cm.cin ,cd.case_id ,it.individual_name  ,sd.id from  investigation.summon_type sd \r\n"
						+ "INNER JOIN investigation.company_summon cm\r\n" + "ON sd.entity_summontype = cm.id  \r\n"
						+ "INNER JOIN investigation.individual_type it\r\n"
						+ "On it.individual_id = sd.individual_type_individual_id\r\n"
						+ "INNER JOIN    investigation.summon_details st\r\n"
						+ "on  cm.entity_company_summon = st.id \r\n"
						+ "INNER JOIN    investigation.case_details cd\r\n" + "on  st.case_id = cd.id \r\n"
						+ "where   sd.aadhar_number =:aadharNo ";

				Query nativequery = entityManager.createNativeQuery(sql);

				nativequery.setParameter("aadharNo", aadharNo);

				return (List<Object[]>) nativequery.getResultList();
			}

			catch (NoResultException e) {
				return null;
			}
		}

		else if (!passportNumber.equalsIgnoreCase("") && panNumber.equalsIgnoreCase("")
				&& !aadharNo.equalsIgnoreCase("")) {
			try {
				sql = "select  cm.company_name , cm.cin ,cd.case_id ,it.individual_name  ,sd.id from  investigation.summon_type sd \r\n"
						+ "INNER JOIN investigation.company_summon cm\r\n" + "ON sd.entity_summontype = cm.id  \r\n"
						+ "INNER JOIN investigation.individual_type it\r\n"
						+ "On it.individual_id = sd.individual_type_individual_id\r\n"
						+ "INNER JOIN    investigation.summon_details st\r\n"
						+ "on  cm.entity_company_summon = st.id \r\n"
						+ "INNER JOIN    investigation.case_details cd\r\n" + "on  st.case_id = cd.id \r\n"
						+ "where  sd.passport =:passport or sd.aadhar_number =:aadharNo ";

				Query nativequery = entityManager.createNativeQuery(sql);
				nativequery.setParameter("passport", passportNumber);
				nativequery.setParameter("aadharNo", aadharNo);

				return (List<Object[]>) nativequery.getResultList();
			}

			catch (NoResultException e) {
				return null;
			}

		} else {
			try {
				sql = "select  cm.company_name , cm.cin ,cd.case_id ,it.individual_name  ,sd.id from  investigation.summon_type sd \r\n"
						+ "INNER JOIN investigation.company_summon cm\r\n" + "ON sd.entity_summontype = cm.id  \r\n"
						+ "INNER JOIN investigation.individual_type it\r\n"
						+ "On it.individual_id = sd.individual_type_individual_id\r\n"
						+ "INNER JOIN    investigation.summon_details st\r\n"
						+ "on  cm.entity_company_summon = st.id \r\n"
						+ "INNER JOIN    investigation.case_details cd\r\n" + "on  st.case_id = cd.id \r\n"
						+ "where  sd.pan_Number =:pan_Number or sd.passport =:passportNumber or sd.aadhar_number =:aadharNo ";

				Query nativequery = entityManager.createNativeQuery(sql);
				nativequery.setParameter("pan_Number", panNumber);
				nativequery.setParameter("passportNumber", passportNumber);
				nativequery.setParameter("aadharNo", aadharNo);

				return (List<Object[]>) nativequery.getResultList();
			}

			catch (NoResultException e) {
				return null;
			}
		}

	}

	public List<Object[]> findListByIdCompany(Long caseId, String company) {
		SNMSValidator snmsvalid = new SNMSValidator();

		try {

			String sql = "select st.name,st.email,st.passport,st.pan_number,it.individual_name, st.id,st.dob,st.address from investigation.Summon_Type st,investigation.company_summon cm,investigation.summon_details sd,investigation.individual_type it  where sd.id=cm.entity_company_summon and cm.id=st.entity_summontype and sd.case_id=:caseId and cm.company_name=:company and it.individual_id = st.individual_type_individual_id";
			Query query = entityManager.createNativeQuery(sql);
			if (snmsvalid.getValidInteger(caseId) && snmsvalid.getvalidCompany(company, true)) {
				query.setParameter("caseId", caseId);
				query.setParameter("company", company);

			} else {

				throw new IllegalAccessException("demo");
			}
			return (List<Object[]>) query.getResultList();
		} catch (NoResultException | IllegalAccessException e) {

			return null;
		}
	}

	public List<SummonStatus> findSummonsByUnit(Long unit_unit_id, int status, int app_user_user_id, String startDate,
			String endDate) {
		try {
			if (app_user_user_id == 0) {
				String nativesql1 = "SELECT ud.id FROM authentication.user_details ud where ud.unit_unit_id=:unit_unit_id ";
				Query nativequery1 = entityManager.createNativeQuery(nativesql1);
				nativequery1.setParameter("unit_unit_id", unit_unit_id);
				List listuseridId = nativequery1.getResultList();
				String nativesql = null;
				if (status == 1) {
					nativesql = "select distinct st.id from investigation.inspector i,investigation.summon_status st,investigation.case_details cd where st.app_user_user_id in ("
							+ StringUtils.join(listuseridId, ',')
							+ ") and  cd.id=st.case_details_id and i.case_details_id=st.case_details_id ";

				}
				if (status == 2) {
					nativesql = "select distinct st.id from investigation.inspector i,investigation.summon_status st,investigation.case_details cd where st.app_user_user_id in ("
							+ StringUtils.join(listuseridId, ',')
							+ ") and  cd.id=st.case_details_id and i.case_details_id=st.case_details_id and st.is_signed= 0 and st.aprroval_stage2=true and st.aprroval_stage1=true";

				}
				if (status == 3) {
					nativesql = "select distinct st.id from investigation.inspector i,investigation.summon_status st,investigation.case_details cd where st.app_user_user_id in ("
							+ StringUtils.join(listuseridId, ',')
							+ ") and  cd.id=st.case_details_id and i.case_details_id=st.case_details_id  and st.is_signed=1  and i.is_active = true";

				}
				if (status == 4) {
					nativesql = "select distinct st.id from investigation.inspector i,investigation.summon_status st,investigation.case_details cd where st.app_user_user_id in ("
							+ StringUtils.join(listuseridId, ',')
							+ ") and  cd.id=st.case_details_id and i.case_details_id=st.case_details_id and st.is_signed= 0 and st.aprroval_stage1=false";

				}
				Query nativequery = entityManager.createNativeQuery(nativesql);
				// query.setParameter("appUser", appUser);
				// nativequery.setParameter("isSigned", 0);
				List listId = nativequery.getResultList();
				if (listId.isEmpty())
					return new ArrayList<SummonStatus>();

				String sql = "Select e from " + SummonStatus.class.getName() + " e " //
						+ " Where e.id in (" + StringUtils.join(listId, ',') + ")";

				Query query = entityManager.createQuery(sql, SummonStatus.class);
//		            query.setParameter("listId", StringUtils.join(listId, ','));
				List<SummonStatus> listSummon = query.getResultList();
				if (!listSummon.isEmpty())
					return listSummon;
				else
					return null;
			} else {
				String nativesql = null;
				if (status == 2) {
					nativesql = "select distinct st.id from investigation.inspector i,investigation.summon_status st,investigation.case_details cd where st.app_user_user_id =:app_user_user_id and  cd.id=st.case_details_id and i.case_details_id=st.case_details_id and st.is_signed= 0 and st.aprroval_stage2=true";
				}
				if (status == 3) {
					nativesql = "select distinct st.id from investigation.inspector i,investigation.summon_status st,investigation.case_details cd where st.app_user_user_id =:app_user_user_id and  cd.id=st.case_details_id and i.case_details_id=st.case_details_id and st.is_signed=1";
				}
				if (status == 4) {
					nativesql = "select distinct st.id from investigation.inspector i,investigation.summon_status st,investigation.case_details cd where st.app_user_user_id =:app_user_user_id and  cd.id=st.case_details_id and i.case_details_id=st.case_details_id and st.is_signed= 0 and st.aprroval_stage1=false";
				} else {
					nativesql = "select distinct st.id from investigation.inspector i,investigation.summon_status st,investigation.case_details cd where st.app_user_user_id =:app_user_user_id and  cd.id=st.case_details_id and i.case_details_id=st.case_details_id ";

				}
				Query nativequery = entityManager.createNativeQuery(nativesql);
				nativequery.setParameter("app_user_user_id", app_user_user_id);
				// nativequery.setParameter("isSigned", 0);
				List listId = nativequery.getResultList();
				if (listId.isEmpty())
					return new ArrayList<SummonStatus>();

				String sql = null;
				Query query;
				if (startDate == "" || endDate == "") {
					sql = "Select e from " + SummonStatus.class.getName() + " e " //
							+ " Where e.id in (" + StringUtils.join(listId, ',') + ")";
					query = entityManager.createQuery(sql, SummonStatus.class);
				} else {
					sql = "Select e from " + SummonStatus.class.getName() + " e " //
							+ " Where e.id in (" + StringUtils.join(listId, ',') + ") and date(e.createdDate) BETWEEN '"
							+ startDate + "' AND '" + endDate + "'";

					query = entityManager.createQuery(sql, SummonStatus.class);
				}
//		            query.setParameter("listId", StringUtils.join(listId, ','));
				List<SummonStatus> listSummon = query.getResultList();
				if (!listSummon.isEmpty())
					return listSummon;
				else
					return null;
			}
		} catch (NoResultException e) {
			return null;
		}
	}

	public List<SummonStatus> findSummon_Peding(AppUser userDetails) {
		List listId;
		try {
			String nativesql = "select ss.id from investigation.summon_status ss,investigation.inspector ins where ss.case_details_id=ins.case_details_id and ss.aprroval_stage1=false";
			Query nativequery = entityManager.createNativeQuery(nativesql);

			listId = nativequery.getResultList();
			if (listId.isEmpty())
				return new ArrayList<SummonStatus>();

			String sql = "Select e from " + SummonStatus.class.getName() + " e " //
					+ " Where e.id in (" + StringUtils.join(listId, ',') + ")";

			Query query = entityManager.createQuery(sql, SummonStatus.class);
//           query.setParameter("listId", StringUtils.join(listId, ','));
			@SuppressWarnings("unchecked")
			List<SummonStatus> listSummon = query.getResultList();
			if (!listSummon.isEmpty())
				return listSummon;
			else
				return null;

		} catch (NoResultException e) {
			return null;
		}
	}

	public List<NoticeStatus> findNotice_Peding(AppUser userDetails) {
		try {
			String nativesql = "select distinct st.id from investigation.inspector i,investigation.notice_status st,investigation.case_details cd where cd.id=st.case_details_id and i.case_details_id=st.case_details_id and st.is_signed= 0 and st.aprroval_stage1=false";
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

	public List<NoticeStatus> findNoticeByUnit(Long unit_unit_id, int status, int app_user_user_id, String startDate,
			String endDate) {
		try {
			if (app_user_user_id == 0) {
				String nativesql1 = "SELECT ud.id FROM authentication.user_details ud where ud.unit_unit_id=:unit_unit_id ";
				Query nativequery1 = entityManager.createNativeQuery(nativesql1);
				nativequery1.setParameter("unit_unit_id", unit_unit_id);
				List listuseridId = nativequery1.getResultList();
				String nativesql = null;
				if (status == 2) {
					nativesql = "select distinct st.id from investigation.inspector i,investigation.notice_status st,investigation.case_details cd where st.app_user_user_id in ("
							+ StringUtils.join(listuseridId, ',')
							+ ") and  cd.id=st.case_details_id and i.case_details_id=st.case_details_id and st.is_signed= 0 and st.aprroval_stage2=true";

				}
				if (status == 3) {
					nativesql = "select distinct st.id from investigation.inspector i,investigation.notice_status st,investigation.case_details cd where st.app_user_user_id in ("
							+ StringUtils.join(listuseridId, ',')
							+ ") and  cd.id=st.case_details_id and i.case_details_id=st.case_details_id  and st.is_signed=1  and i.is_active = true";

				}
				if (status == 4) {
					nativesql = "select distinct st.id from investigation.inspector i,investigation.notice_status st,investigation.case_details cd where st.app_user_user_id in ("
							+ StringUtils.join(listuseridId, ',')
							+ ") and  cd.id=st.case_details_id and i.case_details_id=st.case_details_id and st.is_signed= 0 and st.aprroval_stage1=false";

				}
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
							+ " Where e.id in (" + StringUtils.join(listId, ',') + ") and date(e.createdDate) BETWEEN '"
							+ startDate + "' AND '" + endDate + "'";

					query = entityManager.createQuery(sql, NoticeStatus.class);
				}
//		            query.setParameter("listId", StringUtils.join(listId, ','));
				List<NoticeStatus> listNotice = query.getResultList();
				if (!listNotice.isEmpty())
					return listNotice;
				else
					return null;
			} else {
				String nativesql = null;
				if (status == 2) {
					nativesql = "select distinct st.id from investigation.inspector i,investigation.notice_status st,investigation.case_details cd where st.app_user_user_id =:app_user_user_id and  cd.id=st.case_details_id and i.case_details_id=st.case_details_id and st.is_signed= 0 and st.aprroval_stage2=true";
				}
				if (status == 3) {
					nativesql = "select distinct st.id from investigation.inspector i,investigation.notice_status st,investigation.case_details cd where st.app_user_user_id =:app_user_user_id and  cd.id=st.case_details_id and i.case_details_id=st.case_details_id and st.is_signed=1";
				}
				if (status == 4) {
					nativesql = "select distinct st.id from investigation.inspector i,investigation.notice_status st,investigation.case_details cd where st.app_user_user_id =:app_user_user_id and  cd.id=st.case_details_id and i.case_details_id=st.case_details_id and st.is_signed= 0 and st.aprroval_stage1=false";
				} else {
					nativesql = "select distinct st.id from investigation.inspector i,investigation.notice_status st,investigation.case_details cd where st.app_user_user_id =:app_user_user_id and  cd.id=st.case_details_id and i.case_details_id=st.case_details_id";
				}
				Query nativequery = entityManager.createNativeQuery(nativesql);
				nativequery.setParameter("app_user_user_id", app_user_user_id);
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
							+ " Where e.id in (" + StringUtils.join(listId, ',') + ") and date(e.createdDate) BETWEEN '"
							+ startDate + "' AND '" + endDate + "'";

					query = entityManager.createQuery(sql, NoticeStatus.class);
				}
//		            query.setParameter("listId", StringUtils.join(listId, ','));
				List<NoticeStatus> listNotice = query.getResultList();
				if (!listNotice.isEmpty())
					return listNotice;
				else
					return null;
			}
		} catch (NoResultException e) {
			return null;
		}
	}

	public List<SummonStatus> findSummonsByApprove(String startDate, String endDate) throws ParseException {
		try {

			String nativesql = "select distinct st.id from investigation.inspector i,investigation.summon_status st,investigation.case_details cd where cd.id=st.case_details_id and i.case_details_id=st.case_details_id and st.is_signed= 0 and st.aprroval_stage2=true";
			Query nativequery = entityManager.createNativeQuery(nativesql);
			// query.setParameter("appUser", appUser);
			// nativequery.setParameter("isSigned", 0);
			List listId = nativequery.getResultList();
			if (listId.isEmpty())
				return new ArrayList<SummonStatus>();
			String sql = null;
			Query query;
			if (startDate == "" || endDate == "") {
				sql = "Select e from " + SummonStatus.class.getName() + " e " //
						+ " Where e.id in (" + StringUtils.join(listId, ',') + ")";
				query = entityManager.createQuery(sql, SummonStatus.class);
			} else {
				/*
				 * SimpleDateFormat format = new SimpleDateFormat("YYYY-MM-DD"); Date frmDate =
				 * format.parse(startDate); Date enDate = format.parse(endDate);
				 */
				sql = "Select e from " + SummonStatus.class.getName() + " e " //
						+ " Where e.id in (" + StringUtils.join(listId, ',') + ") and date(e.approvalDate) BETWEEN '"
						+ startDate + "' AND '" + endDate + "'";

				query = entityManager.createQuery(sql, SummonStatus.class);
				// query.setParameter("frmDate", startDate);
				// query.setParameter("enDate", endDate);
			}

//		            query.setParameter("listId", StringUtils.join(listId, ','));
			List<SummonStatus> listSummon = query.getResultList();
			if (!listSummon.isEmpty())
				return listSummon;
			else
				return null;
		} catch (NoResultException e) {
			return null;
		}
	}

	@SuppressWarnings("unused")
	public List<SummonStatus> findSummon_signed(AppUser userDetails, String startDate, String endDate) {
		try {

			String role = roleDAO.getRoleName(userDetails.getUserId());
			List listId;
			if (role.equals("ROLE_DIRECTOR")) {
				String nativesql = "select ss.id from investigation.summon_status ss,investigation.inspector ins where ss.case_details_id=ins.case_details_id and ss.is_signed=1 ";
				Query nativequery = entityManager.createNativeQuery(nativesql);

				listId = nativequery.getResultList();
			} else {
				String nativesql = "select ss.id from investigation.summon_status ss,investigation.inspector ins where ss.case_details_id=ins.case_details_id and ins.app_user_user_id=:userId and ss.is_signed=1  and ins.is_active = true";
				Query nativequery = entityManager.createNativeQuery(nativesql);
				if (userDetails != null) {
					nativequery.setParameter("userId", userDetails.getUserId());
				} else {
					nativequery.setParameter("userId", new AppUser().getUserId());
				}

				listId = nativequery.getResultList();
			}

			if (listId.isEmpty())
				return new ArrayList<SummonStatus>();

			String sql = null;
			Query query;
			if (startDate == "" || endDate == "") {
				sql = "Select e from " + SummonStatus.class.getName() + " e " //
						+ " Where e.id in (" + StringUtils.join(listId, ',') + ")";
				query = entityManager.createQuery(sql, SummonStatus.class);
			} else {
				sql = "Select e from " + SummonStatus.class.getName() + " e " //
						+ " Where e.id in (" + StringUtils.join(listId, ',') + ") and date(e.orderSignedDate) BETWEEN '"
						+ startDate + "' AND '" + endDate + "'";

				query = entityManager.createQuery(sql, SummonStatus.class);
			}

			query = entityManager.createQuery(sql, SummonStatus.class);
//	            query.setParameter("listId", StringUtils.join(listId, ','));
			@SuppressWarnings("unchecked")
			List<SummonStatus> listSummon = query.getResultList();
			if (!listSummon.isEmpty())
				return listSummon;
			else
				return null;

		} catch (NoResultException e) {
			return null;
		}
	}

	public List<SummonStatus> findSummon_Peding(AppUser userDetails, String startDate, String endDate) {
		List listId;
		try {
			String nativesql = "select ss.id from investigation.summon_status ss,investigation.inspector ins where ss.case_details_id=ins.case_details_id and ss.aprroval_stage1=false";
			Query nativequery = entityManager.createNativeQuery(nativesql);

			listId = nativequery.getResultList();
			if (listId.isEmpty())
				return new ArrayList<SummonStatus>();

			String sql = null;
			Query query;
			if (startDate == "" || endDate == "") {
				sql = "Select e from " + SummonStatus.class.getName() + " e " //
						+ " Where e.id in (" + StringUtils.join(listId, ',') + ")";
				query = entityManager.createQuery(sql, SummonStatus.class);
			} else {
				sql = "Select e from " + SummonStatus.class.getName() + " e " //
						+ " Where e.id in (" + StringUtils.join(listId, ',') + ") and date(e.createdDate) BETWEEN '"
						+ startDate + "' AND '" + endDate + "'";

				query = entityManager.createQuery(sql, SummonStatus.class);
			}

			// query = entityManager.createQuery(sql, SummonStatus.class);
//           query.setParameter("listId", StringUtils.join(listId, ','));
			@SuppressWarnings("unchecked")
			List<SummonStatus> listSummon = query.getResultList();
			if (!listSummon.isEmpty())
				return listSummon;
			else
				return null;

		} catch (NoResultException e) {
			return null;
		}
	}

	public List<SummonStatus> findSummonsByAll(String startDate, String endDate) {
		try {

			// String role = roleDAO.getRoleName(userDetails.getUserId());
			List listId;

			String nativesql = "select ss.id from investigation.summon_status ss,investigation.inspector ins where ss.case_details_id=ins.case_details_id ";
			Query nativequery = entityManager.createNativeQuery(nativesql);

			listId = nativequery.getResultList();

			if (listId.isEmpty())
				return new ArrayList<SummonStatus>();

			String sql = null;
			Query query;
			if (startDate == "" || endDate == "") {
				sql = "Select e from " + SummonStatus.class.getName() + " e " //
						+ " Where e.id in (" + StringUtils.join(listId, ',') + ")";
				query = entityManager.createQuery(sql, SummonStatus.class);
			} else {
				sql = "Select e from " + SummonStatus.class.getName() + " e " //
						+ " Where e.id in (" + StringUtils.join(listId, ',') + ") and date(e.createdDate) BETWEEN '"
						+ startDate + "' AND '" + endDate + "'";

				query = entityManager.createQuery(sql, SummonStatus.class);
			}

			// query = entityManager.createQuery(sql, SummonStatus.class);
//	            query.setParameter("listId", StringUtils.join(listId, ','));
			List<SummonStatus> listSummon = query.getResultList();
			if (!listSummon.isEmpty())
				return listSummon;
			else
				return null;

		} catch (NoResultException e) {
			return null;
		}
	}

	@SuppressWarnings("unused")
	public List<NoticeStatus> findNotice_signed(AppUser userDetails, String startDate, String endDate) {
		try {

			String role = roleDAO.getRoleName(userDetails.getUserId());
			List listId;
			if (role.equals("ROLE_DIRECTOR")) {
				String nativesql = "select ss.id from investigation.notice_status ss,investigation.inspector ins where ss.case_details_id=ins.case_details_id and ss.is_signed=1 ";
				Query nativequery = entityManager.createNativeQuery(nativesql);

				listId = nativequery.getResultList();
			} else {
				String nativesql = "select ss.id from investigation.notice_status ss,investigation.inspector ins where ss.case_details_id=ins.case_details_id and ins.app_user_user_id=:userId and ss.is_signed=1   and ins.is_active = true";
				Query nativequery = entityManager.createNativeQuery(nativesql);
				if (userDetails != null) {
					nativequery.setParameter("userId", userDetails.getUserId());
				} else {
					nativequery.setParameter("userId", new AppUser().getUserId());
				}
				listId = nativequery.getResultList();
			}

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
						+ " Where e.id in (" + StringUtils.join(listId, ',') + ") and date(e.orderSignedDate) BETWEEN '"
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

	public List<NoticeStatus> findNotice_Peding(AppUser userDetails, String startDate, String endDate) {
		try {
			String nativesql = "select distinct st.id from investigation.inspector i,investigation.notice_status st,investigation.case_details cd where cd.id=st.case_details_id and i.case_details_id=st.case_details_id and st.is_signed= 0 and st.aprroval_stage1=false";
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

	public List<UserDetails> findInspectorLIst() {
		try {
			String nativesql = "select user_id from  authentication.user_role  where role_id = 2";
			Query nativequery = entityManager.createNativeQuery(nativesql);
			// query.setParameter("appUser", appUser);
			// nativequery.setParameter("isSigned", 0);
			List listId = nativequery.getResultList();
			if (listId.isEmpty())
				return new ArrayList<UserDetails>();

			String sql = "Select e from " + UserDetails.class.getName() + " e " //
					+ " Where e.id in (" + StringUtils.join(listId, ',') + ") order by e.firstName";

			Query query = entityManager.createQuery(sql, UserDetails.class);
			List<UserDetails> listinsp = query.getResultList();
			if (!listinsp.isEmpty())
				return listinsp;
			else
				return null;
		} catch (NoResultException e) {
			return null;
		}
	}

	public List<SummonStatus> findSummonsByInspector(int inspList, int status, int inspList2, String startDate,
			String endDate) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<NoticeStatus> findNoticeByUnits(long unit_unit_id, int status) {
		List listId = null;
		try {

			String nativesql1 = "SELECT ud.id FROM authentication.user_details ud where ud.unit_unit_id=:unit_unit_id ";
			Query nativequery1 = entityManager.createNativeQuery(nativesql1);
			nativequery1.setParameter("unit_unit_id", unit_unit_id);
			List listuseridId = nativequery1.getResultList();
			String nativesql = null;
			if (!listuseridId.isEmpty()) {
				if (status == 1) {
					nativesql = "select distinct st.id from investigation.inspector i,investigation.notice_status st,investigation.case_details cd where st.app_user_user_id in ("
							+ StringUtils.join(listuseridId, ',')
							+ ") and  cd.id=st.case_details_id and i.case_details_id=st.case_details_id and st.is_rejected=false ";
				}
				if (status == 2) {
					nativesql = "select distinct st.id from investigation.inspector i,investigation.notice_status st,investigation.case_details cd where st.app_user_user_id in ("
							+ StringUtils.join(listuseridId, ',')
							+ ") and  cd.id=st.case_details_id and i.case_details_id=st.case_details_id and st.is_signed= 0 and st.aprroval_stage2=true and st.aprroval_stage1=true and st.is_physicallysent=false";

				}
				if (status == 3) {
					nativesql = "select distinct st.id from investigation.inspector i,investigation.notice_status st,investigation.case_details cd where st.app_user_user_id in ("
							+ StringUtils.join(listuseridId, ',')
							+ ") and  cd.id=st.case_details_id and i.case_details_id=st.case_details_id  and st.is_signed=1 and st.is_physicallysent=false and st.aprroval_stage2=true and st.aprroval_stage1=true and i.is_active = true";

				}
				if (status == 4) {
					nativesql = "select distinct st.id from investigation.inspector i,investigation.notice_status st,investigation.case_details cd where st.app_user_user_id in ("
							+ StringUtils.join(listuseridId, ',')
							+ ") and  cd.id=st.case_details_id and i.case_details_id=st.case_details_id and st.is_signed= 0 and st.aprroval_stage1=false and st.aprroval_stage2=false and st.is_physicallysent=false and st.is_rejected=false";

				} else if (status == 5) {
					nativesql = "select distinct st.id from investigation.inspector i,investigation.notice_status st,investigation.case_details cd where st.app_user_user_id in ("
							+ StringUtils.join(listuseridId, ',')
							+ ") and  cd.id=st.case_details_id and i.case_details_id=st.case_details_id and  TO_DATE(to_char(created_date,'YYYY-MM-DD'),'YYYY-MM-DD') < to_timestamp('2021-09-01 15:32:50.984', 'YYYY-MM-DD') and st.aprroval_stage2=true and st.is_signed=0   and st.is_physicallysent=false";

				} else if (status == 6) {
					nativesql = "select distinct st.id from investigation.inspector i,investigation.notice_status st,investigation.case_details cd where st.app_user_user_id in ("
							+ StringUtils.join(listuseridId, ',')
							+ ") and  cd.id=st.case_details_id and i.case_details_id=st.case_details_id and st.is_physicallysent=true and st.is_signed=1 and isverified=true  and  TO_DATE(to_char(st.created_date,'YYYY-MM-DD'),'YYYY-MM-DD') < to_timestamp('2021-09-01 15:32:50.984', 'YYYY-MM-DD')";
				}

				else if (status == 7) {
					nativesql = "select distinct st.id from investigation.inspector i,investigation.notice_status st,investigation.case_details cd where st.app_user_user_id in ("
							+ StringUtils.join(listuseridId, ',')
							+ ") and  cd.id=st.case_details_id and i.case_details_id=st.case_details_id and st.is_physicallysent=true   and  TO_DATE(to_char(st.created_date,'YYYY-MM-DD'),'YYYY-MM-DD') < to_timestamp('2021-09-01 15:32:50.984', 'YYYY-MM-DD') and st.aprroval_stage2=true and st.aprroval_stage1=false and st.is_physicallysent=true and st.is_rejected = false ";
				}

				else if (status == 8) {
					nativesql = "select distinct st.id from investigation.inspector i,investigation.notice_status st,investigation.case_details cd where st.app_user_user_id in ("
							+ StringUtils.join(listuseridId, ',')
							+ ") and  cd.id=st.case_details_id and i.case_details_id=st.case_details_id and st.is_physicallysent=true   and  TO_DATE(to_char(st.created_date,'YYYY-MM-DD'),'YYYY-MM-DD') < to_timestamp('2021-09-01 15:32:50.984', 'YYYY-MM-DD') and st.aprroval_stage2=true and st.aprroval_stage1=false and st.is_physicallysent=true and st.is_rejected = true ";
				}

				Query nativequery = entityManager.createNativeQuery(nativesql);
				// query.setParameter("appUser", appUser);
				// nativequery.setParameter("isSigned", 0);
				listId = nativequery.getResultList();

				if (listId.isEmpty())
					return new ArrayList<NoticeStatus>();
			}

			if (listuseridId.isEmpty())
				return new ArrayList<NoticeStatus>();

			String sql = null;
			Query query;

			sql = "Select e from " + NoticeStatus.class.getName() + " e " //
					+ " Where e.id in (" + StringUtils.join(listId, ',') + ")";
			query = entityManager.createQuery(sql, NoticeStatus.class);
			List<NoticeStatus> listNotice = query.getResultList();
			if (!listNotice.isEmpty())
				return listNotice;
			else {
				return new ArrayList<NoticeStatus>();
			}
		} catch (NoResultException e) {
			return new ArrayList<NoticeStatus>();
		}
	}

	public List<SummonStatus> findSummonByUnits(Long unit_unit_id, int status) {
		List listId;
		try {
			String nativesql1 = "SELECT ud.id FROM authentication.user_details ud where ud.unit_unit_id=:unit_unit_id ";
			Query nativequery1 = entityManager.createNativeQuery(nativesql1);
			nativequery1.setParameter("unit_unit_id", unit_unit_id);
			List listuseridId = nativequery1.getResultList();
			String nativesql = null;
			if (!listuseridId.isEmpty()) {
				// all summon
				if (status == 1) {
					nativesql = "select distinct st.id from investigation.inspector i,investigation.summon_status st,investigation.case_details cd where st.app_user_user_id in ("
							+ StringUtils.join(listuseridId, ',')
							+ ") and  cd.id=st.case_details_id and i.case_details_id=st.case_details_id and st.is_rejected=false ";

				}
				// approvedSummonUnit
				if (status == 2) {
					nativesql = "select distinct st.id from investigation.inspector i,investigation.summon_status st,investigation.case_details cd where st.app_user_user_id in ("
							+ StringUtils.join(listuseridId, ',')
							+ ") and  cd.id=st.case_details_id and i.case_details_id=st.case_details_id and st.is_signed= 0 and st.aprroval_stage2=true and st.aprroval_stage1=true and st.is_physicallysent=false";

				}
				// EsignedSummonUnit
				if (status == 3) {
					nativesql = "select distinct st.id from investigation.inspector i,investigation.summon_status st,investigation.case_details cd where st.app_user_user_id in ("
							+ StringUtils.join(listuseridId, ',')
							+ ") and  cd.id=st.case_details_id and i.case_details_id=st.case_details_id  and st.is_signed=1   and st.is_offline=false and st.is_physicallysent =false  and st.aprroval_stage2=true and st.aprroval_stage1=true  ";

				}
				// PendingSummonUnit
				if (status == 4) {
					nativesql = "select distinct st.id from investigation.inspector i,investigation.summon_status st,investigation.case_details cd where st.app_user_user_id in ("
							+ StringUtils.join(listuseridId, ',')
							+ ") and  cd.id=st.case_details_id and i.case_details_id=st.case_details_id and st.is_signed= 0 and st.aprroval_stage1=false and st.aprroval_stage2=false and st.is_rejected=false";

				}
				// Pendingphysummon
				else if (status == 5) {
					nativesql = "select distinct st.id from investigation.inspector i,investigation.summon_status st,investigation.case_details cd where st.app_user_user_id in ("
							+ StringUtils.join(listuseridId, ',')
							+ ") and  cd.id=st.case_details_id and i.case_details_id=st.case_details_id and  TO_DATE(to_char(created_date,'YYYY-MM-DD'),'YYYY-MM-DD') < to_timestamp('2021-09-01 15:32:50.984', 'YYYY-MM-DD') and st.aprroval_stage2=true  and st.is_signed=0  ";

				} else if (status == 6) {
					nativesql = "select distinct st.id from investigation.inspector i,investigation.summon_status st,investigation.case_details cd where st.app_user_user_id in ("
							+ StringUtils.join(listuseridId, ',')
							+ ") and  cd.id=st.case_details_id and i.case_details_id=st.case_details_id and st.is_physicallysent=true   and  TO_DATE(to_char(st.created_date,'YYYY-MM-DD'),'YYYY-MM-DD') < to_timestamp('2021-09-01 15:32:50.984', 'YYYY-MM-DD') and st.aprroval_stage2=true and st.aprroval_stage1=false and st.is_physicallysent=true and st.is_rejected = false ";
				} else if (status == 7) {
					nativesql = "select distinct st.id from investigation.inspector i,investigation.summon_status st,investigation.case_details cd where st.app_user_user_id in ("
							+ StringUtils.join(listuseridId, ',')
							+ ") and  cd.id=st.case_details_id and i.case_details_id=st.case_details_id and st.is_signed= 0 and st.aprroval_stage2=false and st.aprroval_stage1=true and st.is_sensitive='Y'and st.is_physicallysent=false";

				} else if (status == 8) {
					nativesql = "select distinct st.id from investigation.inspector i,investigation.summon_status st,investigation.case_details cd where st.app_user_user_id in ("
							+ StringUtils.join(listuseridId, ',')
							+ ") and  cd.id=st.case_details_id and i.case_details_id=st.case_details_id and st.is_signed=1 and st.aprroval_stage2=true and st.aprroval_stage1=true";

				}

				Query nativequery = entityManager.createNativeQuery(nativesql);
				listId = nativequery.getResultList();
				if (listId.isEmpty())
					return new ArrayList<SummonStatus>();
			} else {
				return new ArrayList<SummonStatus>();
			}
			String sql = "Select e from " + SummonStatus.class.getName() + " e " //
					+ " Where e.id in (" + StringUtils.join(listId, ',') + ")";

			Query query = entityManager.createQuery(sql, SummonStatus.class);
//		            query.setParameter("listId", StringUtils.join(listId, ','));
			List<SummonStatus> listSummon = query.getResultList();
			if (!listSummon.isEmpty())
				return listSummon;
			else
				return new ArrayList<SummonStatus>();

		} catch (NoResultException e) {
			return new ArrayList<SummonStatus>();
		}
	}

	public List<SummonStatus> findOfflineSummonByUnits(Long unit_unit_id) {
		List listId;
		try {
			String nativesql1 = "SELECT ud.id FROM authentication.user_details ud where ud.unit_unit_id=:unit_unit_id ";
			Query nativequery1 = entityManager.createNativeQuery(nativesql1);
			nativequery1.setParameter("unit_unit_id", unit_unit_id);
			List listuseridId = nativequery1.getResultList();
			String nativesql = null;
			if (!listuseridId.isEmpty()) {

				nativesql = "select distinct st.id from investigation.inspector i,investigation.summon_status st,investigation.case_details cd where st.app_user_user_id in ("
						+ StringUtils.join(listuseridId, ',')
						+ ") and  cd.id=st.case_details_id and i.case_details_id=st.case_details_id and st.is_offline=true and st.aprroval_stage2=true and st.aprroval_stage1=true and st.is_signed=1 and is_physicallysent=false and st.is_rejected=false";

				Query nativequery = entityManager.createNativeQuery(nativesql);
				listId = nativequery.getResultList();
				if (listId.isEmpty())
					return new ArrayList<SummonStatus>();
			} else {
				return new ArrayList<SummonStatus>();
			}
			String sql = "Select e from " + SummonStatus.class.getName() + " e " //
					+ " Where e.id in (" + StringUtils.join(listId, ',') + ")";

			Query query = entityManager.createQuery(sql, SummonStatus.class);
//		            query.setParameter("listId", StringUtils.join(listId, ','));
			List<SummonStatus> listSummon = query.getResultList();
			if (!listSummon.isEmpty())
				return listSummon;
			else
				return new ArrayList<SummonStatus>();

		} catch (NoResultException e) {
			return new ArrayList<SummonStatus>();
		}

	}

	public List<SummonStatus> findSummonByStatus(AppUser userDetails, int status) {
		String nativesql = null;
		Query nativequery = null;
		List listId;
		try {
			if (status == 1) {
				nativesql = "select ss.id from investigation.summon_status ss,investigation.inspector ins where ss.case_details_id=ins.case_details_id and ss.app_user_user_id=:app_user_user_id ";
				nativequery = entityManager.createNativeQuery(nativesql);
				if (userDetails != null) {
					nativequery.setParameter("app_user_user_id", userDetails.getUserId());
				} else {
					nativequery.setParameter("app_user_user_id", new AppUser().getUserId());
				}
			} else if (status == 2) {
				nativesql = "select distinct st.id from investigation.inspector i,investigation.summon_status st,investigation.case_details cd where cd.id=st.case_details_id and i.case_details_id=st.case_details_id and st.is_signed= 0 and st.aprroval_stage2=true  and st.app_user_user_id=:app_user_user_id ";
				nativequery = entityManager.createNativeQuery(nativesql);
				if (userDetails != null) {
					nativequery.setParameter("app_user_user_id", userDetails.getUserId());
				} else {
					nativequery.setParameter("app_user_user_id", new AppUser().getUserId());
				}
			} else if (status == 3) {
				nativesql = "select ss.id from investigation.summon_status ss,investigation.inspector ins where ss.case_details_id=ins.case_details_id and ss.app_user_user_id=:app_user_user_id and ss.is_signed=1  and ins.is_active = true";
				nativequery = entityManager.createNativeQuery(nativesql);
				if (userDetails != null) {
					nativequery.setParameter("app_user_user_id", userDetails.getUserId());
				} else {
					nativequery.setParameter("app_user_user_id", new AppUser().getUserId());
				}
			} else if (status == 4) {
				nativesql = "select distinct st.id from investigation.inspector i,investigation.summon_status st,investigation.case_details cd where st.app_user_user_id =:app_user_user_id"

						+ " and  cd.id=st.case_details_id and i.case_details_id=st.case_details_id and st.is_signed= 0 and st.aprroval_stage1=false";
				nativequery = entityManager.createNativeQuery(nativesql);
				if (userDetails != null) {
					nativequery.setParameter("app_user_user_id", userDetails.getUserId());
				} else {
					nativequery.setParameter("app_user_user_id", new AppUser().getUserId());
				}
			}
			// Query nativequery = entityManager.createNativeQuery(nativesql);
			listId = nativequery.getResultList();
			if (listId.isEmpty())
				return new ArrayList<SummonStatus>();

			String sql = "Select e from " + SummonStatus.class.getName() + " e " //
					+ " Where e.id in (" + StringUtils.join(listId, ',') + ")";

			Query query = entityManager.createQuery(sql, SummonStatus.class);
//	            query.setParameter("listId", StringUtils.join(listId, ','));
			List<SummonStatus> listSummon = query.getResultList();
			if (!listSummon.isEmpty())
				return listSummon;
			else
				return new ArrayList<SummonStatus>();
		} catch (NoResultException e) {
			return new ArrayList<SummonStatus>();
		}

	}

	public List<NoticeStatus> findNoticeByStatus(AppUser userDetails, int status) {

		String nativesql = null;
		Query nativequery = null;
		List listId;
		try {

			if (status == 1) {
				nativesql = "select distinct st.id from investigation.inspector i,investigation.notice_status st,investigation.case_details cd where st.app_user_user_id =:app_user_user_id"
						+ " and  cd.id=st.case_details_id and i.case_details_id=st.case_details_id";
				nativequery = entityManager.createNativeQuery(nativesql);
				if (userDetails != null) {
					nativequery.setParameter("app_user_user_id", userDetails.getUserId());
				} else {
					nativequery.setParameter("app_user_user_id", new AppUser().getUserId());
				}

			}
			if (status == 2) {
				nativesql = "select distinct st.id from investigation.inspector i,investigation.notice_status st,investigation.case_details cd where st.app_user_user_id =:app_user_user_id"
						+ " and  cd.id=st.case_details_id and i.case_details_id=st.case_details_id and st.is_signed= 0 and st.aprroval_stage2=true";
				nativequery = entityManager.createNativeQuery(nativesql);
				if (userDetails != null) {
					nativequery.setParameter("app_user_user_id", userDetails.getUserId());
				} else {
					nativequery.setParameter("app_user_user_id", new AppUser().getUserId());
				}
			}
			if (status == 3) {
				nativesql = "select distinct st.id from investigation.inspector i,investigation.notice_status st,investigation.case_details cd where st.app_user_user_id =:app_user_user_id"

						+ " and  cd.id=st.case_details_id and i.case_details_id=st.case_details_id  and st.is_signed=1  and i.is_active = true";
				nativequery = entityManager.createNativeQuery(nativesql);
				if (userDetails != null) {
					nativequery.setParameter("app_user_user_id", userDetails.getUserId());
				} else {
					nativequery.setParameter("app_user_user_id", new AppUser().getUserId());
				}
			}
			if (status == 4) {
				nativesql = "select distinct st.id from investigation.inspector i,investigation.notice_status st,investigation.case_details cd where st.app_user_user_id =:app_user_user_id"

						+ " and  cd.id=st.case_details_id and i.case_details_id=st.case_details_id and st.is_signed= 0 and st.aprroval_stage1=false";
				nativequery = entityManager.createNativeQuery(nativesql);
				if (userDetails != null) {
					nativequery.setParameter("app_user_user_id", userDetails.getUserId());
				} else {
					nativequery.setParameter("app_user_user_id", new AppUser().getUserId());
				}
			}
			// Query nativequery = entityManager.createNativeQuery(nativesql);
			listId = nativequery.getResultList();

			if (listId.isEmpty())
				return new ArrayList<NoticeStatus>();

			String sql = null;
			Query query;

			sql = "Select e from " + NoticeStatus.class.getName() + " e " //
					+ " Where e.id in (" + StringUtils.join(listId, ',') + ")";
			query = entityManager.createQuery(sql, NoticeStatus.class);
			List<NoticeStatus> listNotice = query.getResultList();
			if (!listNotice.isEmpty())
				return listNotice;
			else {
				return new ArrayList<NoticeStatus>();
			}
		} catch (NoResultException e) {
			return new ArrayList<NoticeStatus>();
		}
	}

	public List<SummonStatus> findSummonAddl(AppUser userDetails, int status) {
		List listId;
		List listuseridId = null;
		try {
			String nativesql1 = "SELECT ins.case_details_id FROM investigation.inspector ins  where ins.app_user_user_id=:app_user_user_id  and ins.is_ado = true";
			Query nativequery1 = entityManager.createNativeQuery(nativesql1);
			nativequery1.setParameter("app_user_user_id", userDetails.getUserId());
			List listCaseId = nativequery1.getResultList();
			String nativesql = null;
			if (!listCaseId.isEmpty()) {

				nativesql = "select distinct ins.app_user_user_id FROM investigation.inspector ins  where ins.case_details_id in ("
						+ StringUtils.join(listCaseId, ',') + ") ";

				Query nativequery = entityManager.createNativeQuery(nativesql);
				listuseridId = nativequery.getResultList();
				if (listuseridId.isEmpty())
					return new ArrayList<SummonStatus>();
			}

			if (status == 1) {
				nativesql = "select distinct st.id from investigation.inspector i,investigation.summon_status st,investigation.case_details cd where st.app_user_user_id in ("
						+ StringUtils.join(listuseridId, ',')
						+ ") and  cd.id=st.case_details_id and i.case_details_id=st.case_details_id ";

			}
			if (status == 2) {
				nativesql = "select distinct st.id from investigation.inspector i,investigation.summon_status st,investigation.case_details cd where st.app_user_user_id in ("
						+ StringUtils.join(listuseridId, ',')
						+ ") and  cd.id=st.case_details_id and i.case_details_id=st.case_details_id and st.is_signed= 0 and st.aprroval_stage2=true";
			}
			if (status == 3) {
				nativesql = "select distinct st.id from investigation.inspector i,investigation.summon_status st,investigation.case_details cd where st.app_user_user_id in ("
						+ StringUtils.join(listuseridId, ',')
						+ ") and  cd.id=st.case_details_id and i.case_details_id=st.case_details_id  and st.is_signed=1  and st.aprroval_stage2=true and i.is_active = true";
			}
			if (status == 4) {
				nativesql = "select distinct st.id from investigation.inspector i,investigation.summon_status st,investigation.case_details cd where st.app_user_user_id in ("
						+ StringUtils.join(listuseridId, ',')
						+ ") and  cd.id=st.case_details_id and i.case_details_id=st.case_details_id and st.is_signed= 0 and st.aprroval_stage1=false";
			}
			Query nativequery = entityManager.createNativeQuery(nativesql);
			listId = nativequery.getResultList();
			if (listId.isEmpty()) {
				return new ArrayList<SummonStatus>();
			}

			String sql = "Select e from " + SummonStatus.class.getName() + " e  Where e.id in ("
					+ StringUtils.join(listId, ',') + ")";

			Query query = entityManager.createQuery(sql, SummonStatus.class); //
			// query.setParameter("listId", StringUtils.join(listId, ','));
			List<SummonStatus> listSummon = query.getResultList();
			if (!listSummon.isEmpty())
				return listSummon;
			else
				return new ArrayList<SummonStatus>();

		} catch (NoResultException e) {
			return new ArrayList<SummonStatus>();
		}

	}

	public List<NoticeStatus> findNoticeByStatus(AppUser userDetails, int status, CaseDetails caseDetails) {
		String nativesql = null;
		Query nativequery = null;
		List listId;
		try {

			if (status == 1) {
				nativesql = "select distinct st.id from investigation.inspector i,investigation.notice_status st,investigation.case_details cd where st.app_user_user_id =:app_user_user_id"
						+ " and  cd.id=st.case_details_id and i.case_details_id=st.case_details_id and st.is_rejected=false and i.isio=true ";
				nativequery = entityManager.createNativeQuery(nativesql);
				if (userDetails != null) {
					nativequery.setParameter("app_user_user_id", userDetails.getUserId());
				} else {
					nativequery.setParameter("app_user_user_id", new AppUser().getUserId());
				}

			}
			if (status == 2) {
				nativesql = "select distinct st.id from investigation.inspector i,investigation.notice_status st,investigation.case_details cd where st.app_user_user_id =:app_user_user_id"
						+ " and  cd.id=st.case_details_id and i.case_details_id=st.case_details_id and st.is_signed= 0 and st.aprroval_stage2=true and st.aprroval_stage1=true and st.is_rejected=false and st.is_physicallysent=false and i.isio=true";
				nativequery = entityManager.createNativeQuery(nativesql);
				if (userDetails != null) {
					nativequery.setParameter("app_user_user_id", userDetails.getUserId());
				} else {
					nativequery.setParameter("app_user_user_id", new AppUser().getUserId());
				}
			}
			if (status == 3) {
				nativesql = "select distinct st.id from investigation.inspector i,investigation.notice_status st,investigation.case_details cd where st.app_user_user_id =:app_user_user_id"

						+ " and  cd.id=st.case_details_id and i.case_details_id=st.case_details_id  and st.is_signed=1 and st.is_physicallysent=false and st.aprroval_stage2=true and st.aprroval_stage1=true and i.is_active = true and i.isio=true ";
				nativequery = entityManager.createNativeQuery(nativesql);
				if (userDetails != null) {
					nativequery.setParameter("app_user_user_id", userDetails.getUserId());
				} else {
					nativequery.setParameter("app_user_user_id", new AppUser().getUserId());
				}
			}
			if (status == 4) {
				nativesql = "select distinct st.id from investigation.inspector i,investigation.notice_status st,investigation.case_details cd where st.app_user_user_id =:app_user_user_id"

						+ " and  cd.id=st.case_details_id and i.case_details_id=st.case_details_id and st.is_signed= 0 and st.aprroval_stage1=false and st.aprroval_stage2=false and st.is_physicallysent=false and st.is_rejected=false and i.isio=true";
				nativequery = entityManager.createNativeQuery(nativesql);
				if (userDetails != null) {
					nativequery.setParameter("app_user_user_id", userDetails.getUserId());
				} else {
					nativequery.setParameter("app_user_user_id", new AppUser().getUserId());
				}
			}
			if (status == 5) {
				nativesql = "select distinct st.id from investigation.inspector i,investigation.notice_status st,investigation.case_details cd where st.app_user_user_id =:app_user_user_id"

						+ " and  cd.id=st.case_details_id and i.case_details_id=st.case_details_id and  TO_DATE(to_char(created_date,'YYYY-MM-DD'),'YYYY-MM-DD') < to_timestamp('2021-09-01 15:32:50.984', 'YYYY-MM-DD') and  st.aprroval_stage1=true and st.aprroval_stage2=true and st.is_signed=0  and st.is_rejected=false and st.is_physicallysent=false and i.isio=true";
				nativequery = entityManager.createNativeQuery(nativesql);
				if (userDetails != null) {
					nativequery.setParameter("app_user_user_id", userDetails.getUserId());
				} else {
					nativequery.setParameter("app_user_user_id", new AppUser().getUserId());
				}
			}
			if (status == 6) {
				nativesql = "select distinct st.id from investigation.inspector i,investigation.notice_status st,investigation.case_details cd where st.app_user_user_id =:app_user_user_id"

						+ " and  cd.id=st.case_details_id and i.case_details_id=st.case_details_id and st.is_physicallysent=true and i.isio=true and st.is_signed=1 and isverified=true  and  TO_DATE(to_char(st.created_date,'YYYY-MM-DD'),'YYYY-MM-DD') < to_timestamp('2021-09-01 15:32:50.984', 'YYYY-MM-DD')";
				nativequery = entityManager.createNativeQuery(nativesql);
				if (userDetails != null) {
					nativequery.setParameter("app_user_user_id", userDetails.getUserId());
				} else {
					nativequery.setParameter("app_user_user_id", new AppUser().getUserId());
				}
			}
			// Query nativequery = entityManager.createNativeQuery(nativesql);
			listId = nativequery.getResultList();

			if (listId.isEmpty())
				return new ArrayList<NoticeStatus>();

			String sql = null;
			Query query;

			sql = "Select e from " + NoticeStatus.class.getName() + " e " //
					+ " Where e.id in (" + StringUtils.join(listId, ',') + ") and caseDetails=:caseDetails";
			query = entityManager.createQuery(sql, NoticeStatus.class);
			query.setParameter("caseDetails", caseDetails);
			List<NoticeStatus> listNotice = query.getResultList();
			if (!listNotice.isEmpty())
				return listNotice;
			else {
				return new ArrayList<NoticeStatus>();
			}
		} catch (NoResultException e) {
			return new ArrayList<NoticeStatus>();
		}
	}

	public List<SummonStatus> findSummonByStatus(AppUser userDetails, int status, CaseDetails caseDetails) {
		String nativesql = null;
		Query nativequery = null;
		List listId;
		try {
			// allSummonUnit
			if (status == 1) {
				nativesql = "select ss.id from investigation.summon_status ss,investigation.inspector ins where ss.case_details_id=ins.case_details_id  and ins.isio=true and ss.app_user_user_id=:app_user_user_id and  ss.is_rejected=false ";
				nativequery = entityManager.createNativeQuery(nativesql);
				if (userDetails != null) {
					nativequery.setParameter("app_user_user_id", userDetails.getUserId());
				} else {
					nativequery.setParameter("app_user_user_id", new AppUser().getUserId());
				}

			}
			// approvedSummon
			else if (status == 2) {
				nativesql = "select distinct st.id from investigation.inspector i,investigation.summon_status st,investigation.case_details cd where cd.id=st.case_details_id and i.isio=true and i.case_details_id=st.case_details_id and st.is_signed= 0 and st.aprroval_stage2=true and st.aprroval_stage1=true and st.is_physicallysent=false   and st.app_user_user_id=:app_user_user_id ";
				nativequery = entityManager.createNativeQuery(nativesql);
				if (userDetails != null) {
					nativequery.setParameter("app_user_user_id", userDetails.getUserId());
				} else {
					nativequery.setParameter("app_user_user_id", new AppUser().getUserId());
				}
			}
			// EsignedSummon
			else if (status == 3) {
				nativesql = "select ss.id from investigation.summon_status ss,investigation.inspector ins where ss.case_details_id=ins.case_details_id  and ins.isio=true and ss.app_user_user_id=:app_user_user_id and ss.is_signed=1  and ins.is_active = true and ss.is_offline=false and ss.is_physicallysent =false  and ss.aprroval_stage2=true and ss.aprroval_stage1=true ";
				nativequery = entityManager.createNativeQuery(nativesql);
				if (userDetails != null) {
					nativequery.setParameter("app_user_user_id", userDetails.getUserId());
				} else {
					nativequery.setParameter("app_user_user_id", new AppUser().getUserId());
				}
			}
			// PendingSummon
			else if (status == 4) {
				nativesql = "select distinct st.id from investigation.inspector i,investigation.summon_status st,investigation.case_details cd where st.app_user_user_id =:app_user_user_id"

						+ " and  cd.id=st.case_details_id and i.case_details_id=st.case_details_id and st.is_signed= 0  and i.isio=true and st.aprroval_stage1=false and st.aprroval_stage2=false and st.is_physicallysent =false and st.is_rejected=false";
				nativequery = entityManager.createNativeQuery(nativesql);
				if (userDetails != null) {
					nativequery.setParameter("app_user_user_id", userDetails.getUserId());
				} else {
					nativequery.setParameter("app_user_user_id", new AppUser().getUserId());
				}
			}
			// Query nativequery = entityManager.createNativeQuery(nativesql);
			listId = nativequery.getResultList();
			if (listId.isEmpty())
				return new ArrayList<SummonStatus>();

			String sql = "Select e from " + SummonStatus.class.getName() + " e " //
					+ " Where e.id in (" + StringUtils.join(listId, ',')
					+ ") and caseDetails=:caseDetails order by e.id desc";

			Query query = entityManager.createQuery(sql, SummonStatus.class);
			query.setParameter("caseDetails", caseDetails);
//	            query.setParameter("listId", StringUtils.join(listId, ','));
			List<SummonStatus> listSummon = query.getResultList();
			if (!listSummon.isEmpty())
				return listSummon;
			else
				return new ArrayList<SummonStatus>();
		} catch (NoResultException e) {
			return new ArrayList<SummonStatus>();
		}
	}

	public List<SummonStatus> findOfflineSummonByUsers(AppUser userDetails, CaseDetails caseDetails) {
		List listId;
		try {
			String nativesql = null;

			nativesql = "select distinct st.id from investigation.inspector i,investigation.summon_status st,"
					+ "investigation.case_details cd where st.app_user_user_id=:app_user_user_id and "
					+ " st.is_offline=true and st.is_signed=1 and is_physicallysent=false";

			Query nativequery = entityManager.createNativeQuery(nativesql);

			nativequery.setParameter("app_user_user_id", userDetails);
			listId = nativequery.getResultList();
			if (listId.isEmpty())
				return new ArrayList<SummonStatus>();

			String sql = "Select e from " + SummonStatus.class.getName() + " e " //
					+ " Where e.id in (" + StringUtils.join(listId, ',')
					+ ")  and caseDetails=:caseDetails order by e.id desc";

			Query query = entityManager.createQuery(sql, SummonStatus.class);
			query.setParameter("caseDetails", caseDetails);
//		            query.setParameter("listId", StringUtils.join(listId, ','));
			List<SummonStatus> listSummon = query.getResultList();
			if (!listSummon.isEmpty())
				return listSummon;
			else
				return new ArrayList<SummonStatus>();

		} catch (NoResultException e) {
			return new ArrayList<SummonStatus>();
		}
	}

	public List<SummonStatus> findPhySummon(AppUser userDetails, boolean approveStage1, boolean Physummon) {
		try {

			String role = roleDAO.getRoleName(userDetails.getUserId());
			List listId;

			// String nativesql = "select ss.id from investigation.summon_status
			// ss,investigation.inspector ins where ss.case_details_id=ins.case_details_id
			// and ins.app_user_user_id=:userId and ss.is_signed=0 and ins.is_active = true
			// and ss.is_physicallysent =true and ss.aprroval_stage1=false and
			// ss.is_rejected=false order by ss.id desc";
			String nativesql = "select  ss.id from investigation.summon_status ss, investigation.case_details c, \r\n"
					+ "investigation.inspector i where i.case_details_id=c.id and( i.app_user_user_id=:userId and i.is_ado=true  \r\n"
					+ " or (i.app_user_user_id in (select  regular_id_id from authentication.link_officer lo where lo.user_details_id=:user_details_id and   lo.is_active = true and CURRENT_DATE between lo.from_date and lo.to_date)))\r\n"
					+ "and ss.case_details_id = c.id and ss.aprroval_stage1=false and  ss.is_rejected = false and ss.is_physicallysent = true  ORDER BY ss.id DESC\r\n";

			Query nativequery = entityManager.createNativeQuery(nativesql);
			if (userDetails != null) {
				nativequery.setParameter("userId", userDetails.getUserId());
			} else {
				nativequery.setParameter("userId", new AppUser().getUserId());
			}
			nativequery.setParameter("user_details_id", userDetails.getUserId());
			listId = nativequery.getResultList();

			if (listId.isEmpty())
				return new ArrayList<SummonStatus>();

			String sql = "Select e from " + SummonStatus.class.getName() + " e " //
					+ " Where e.id in (" + StringUtils.join(listId, ',') + ") order by e.id desc";

			Query query = entityManager.createQuery(sql, SummonStatus.class);
//	            query.setParameter("listId", StringUtils.join(listId, ','));
			@SuppressWarnings("unchecked")
			List<SummonStatus> listSummon = query.getResultList();
			if (!listSummon.isEmpty())
				return listSummon;
			else
				return null;

		} catch (NoResultException e) {
			return null;
		}

	}

	public List<SummonStatus> findPhyReviewSummon(AppUser userDetails, boolean approveStage1, boolean Physummon) {
		try {

			String role = roleDAO.getRoleName(userDetails.getUserId());
			List listId;

			String nativesql = "select ss.id from investigation.summon_status ss,investigation.inspector ins where ss.case_details_id=ins.case_details_id and ins.app_user_user_id=:userId and ss.is_signed=0  and ins.is_active = true and ss.is_physicallysent =true and ss.aprroval_stage1=false and ss.is_rejected = true ";
			Query nativequery = entityManager.createNativeQuery(nativesql);
			if (userDetails != null) {
				nativequery.setParameter("userId", userDetails.getUserId());
			} else {
				nativequery.setParameter("userId", new AppUser().getUserId());
			}

			listId = nativequery.getResultList();

			if (listId.isEmpty())
				return new ArrayList<SummonStatus>();

			String sql = "Select e from " + SummonStatus.class.getName() + " e " //
					+ " Where e.id in (" + StringUtils.join(listId, ',') + ") order by e.id desc";

			Query query = entityManager.createQuery(sql, SummonStatus.class);
//	            query.setParameter("listId", StringUtils.join(listId, ','));
			@SuppressWarnings("unchecked")
			List<SummonStatus> listSummon = query.getResultList();
			if (!listSummon.isEmpty())
				return listSummon;
			else
				return null;

		} catch (NoResultException e) {
			return null;
		}

	}

	public List<SummonStatus> findPhySummonList(AppUser userDetails, boolean b, boolean c) {
		try {

			String role = roleDAO.getRoleName(userDetails.getUserId());

			List listId;
			List caseID;

			String nativesql1 = "select distinct i.case_details_id from investigation.inspector i,investigation.summon_status st,investigation.case_details cd where cd.id=st.case_details_id and i.case_details_id=st.case_details_id and st.is_signed= 0 and st.aprroval_stage2=true  and i.app_user_user_id=:app_user_user_id ";
			Query nativequery1 = entityManager.createNativeQuery(nativesql1);
			if (userDetails != null) {
				nativequery1.setParameter("app_user_user_id", userDetails.getUserId());
			} else {
				nativequery1.setParameter("app_user_user_id", new AppUser().getUserId());
			}

			caseID = nativequery1.getResultList();

			if (caseID.isEmpty())
				return new ArrayList<SummonStatus>();

			String nativesql = "select ss.id from investigation.summon_status ss,investigation.inspector ins where ss.case_details_id=ins.case_details_id and ins.app_user_user_id=:userId  and ins.is_active = true and ss.is_physicallysent =true and ss.case_details_id in("
					+ StringUtils.join(caseID, ',') + ") ORDER BY  ss.id desc";
			Query nativequery = entityManager.createNativeQuery(nativesql);
			if (userDetails != null) {
				nativequery.setParameter("userId", userDetails.getUserId());
			} else {
				nativequery.setParameter("userId", new AppUser().getUserId());
			}

			listId = nativequery.getResultList();

			if (listId.isEmpty())
				return new ArrayList<SummonStatus>();

			String sql = "Select e from " + SummonStatus.class.getName() + " e " //
					+ " Where e.id in (" + StringUtils.join(listId, ',') + ") order by e.id desc";

			Query query = entityManager.createQuery(sql, SummonStatus.class);
//	            query.setParameter("listId", StringUtils.join(listId, ','));
			@SuppressWarnings("unchecked")
			List<SummonStatus> listSummon = query.getResultList();
			if (!listSummon.isEmpty())
				return listSummon;
			else
				return null;

		} catch (NoResultException e) {
			return null;
		}

	}

	public List<SummonStatus> findPhyReviewSummonByUnits(Long unit_unit_id) {
		List listId;
		try {
			String nativesql1 = "SELECT ud.id FROM authentication.user_details ud where ud.unit_unit_id=:unit_unit_id ";
			Query nativequery1 = entityManager.createNativeQuery(nativesql1);
			nativequery1.setParameter("unit_unit_id", unit_unit_id);
			List listuseridId = nativequery1.getResultList();
			String nativesql = null;
			if (!listuseridId.isEmpty()) {

				nativesql = "select distinct st.id from investigation.inspector i,investigation.summon_status st,investigation.case_details cd where st.app_user_user_id in ("
						+ StringUtils.join(listuseridId, ',')
						+ ") and  cd.id=st.case_details_id and i.case_details_id=st.case_details_id and st.is_physicallysent=true and st.is_signed=1 and st.is_rejected=false";

				Query nativequery = entityManager.createNativeQuery(nativesql);
				listId = nativequery.getResultList();
				if (listId.isEmpty())
					return new ArrayList<SummonStatus>();
			} else {
				return new ArrayList<SummonStatus>();
			}
			String sql = "Select e from " + SummonStatus.class.getName() + " e " //
					+ " Where e.id in (" + StringUtils.join(listId, ',') + ") order by e.id desc";

			Query query = entityManager.createQuery(sql, SummonStatus.class);
//		            query.setParameter("listId", StringUtils.join(listId, ','));
			List<SummonStatus> listSummon = query.getResultList();
			if (!listSummon.isEmpty())
				return listSummon;
			else
				return new ArrayList<SummonStatus>();

		} catch (NoResultException e) {
			return new ArrayList<SummonStatus>();
		}

	}

	public SummonStatus findAllBySummonDin(String dinNumber, AppUser userDetails) {
		// TODO Auto-generated method stub
		return null;
	}

	public Boolean finduserPresentisPresent(CaseDetails caseDetails, AppUser userDetails) {
		String sql = "SELECT e.id from investigation.inspector e  where case_details_id=:case_details_id  and app_user_user_id =:app_user_user_id and is_active = true";
		Query nativequery = entityManager.createNativeQuery(sql);

		nativequery.setParameter("case_details_id", caseDetails.getId());
		nativequery.setParameter("app_user_user_id", userDetails.getUserId());

		List<Object[]> id = nativequery.getResultList();
		if (id.isEmpty()) {
			return false;
		} else {
			return true;
		}
	}

	public List<Object[]> findSummonsPhysically() {
		try {
			String sql = "select cd.case_id,cd.case_title,o.id,o.order_signed_date,o.summon_no from investigation.summon_status o,investigation.case_details cd where cd.id=o.case_details_id and o.is_signed=0 and o.is_physicallysent=true";
			Query query = this.entityManager.createNativeQuery(sql);
			return (List<Object[]>) query.getResultList();
		} catch (NoResultException e) {
			return null;
		}
	}

	public List<Object[]> findNoticePhysically() {
		try {
			String sql = "select distinct st.summon_no,st.date_of_appear,st.created_date,st.id,cd.case_title,st.notice_din from investigation.inspector i,investigation.notice_status st,investigation.case_details cd where cd.id=st.case_details_id and i.case_details_id=st.case_details_id and st.is_signed=:isSigned and st.aprroval_stage2=true and st.is_physicallysent=true";
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

	public List<SummonStatus> findSummonsPhysicallysend() {
		try {
			String nativesql = "select distinct st.id from investigation.inspector i,investigation.summon_status st,investigation.case_details cd where cd.id=st.case_details_id and i.case_details_id=st.case_details_id and st.is_signed= 0 and st.is_physicallysent=true";
			Query nativequery = entityManager.createNativeQuery(nativesql);
			// query.setParameter("appUser", appUser);
			// nativequery.setParameter("isSigned", 0);
			List listId = nativequery.getResultList();
			if (listId.isEmpty())
				return new ArrayList<SummonStatus>();

			String sql = "Select e from " + SummonStatus.class.getName() + " e " //
					+ " Where e.id in (" + StringUtils.join(listId, ',') + ")";

			Query query = entityManager.createQuery(sql, SummonStatus.class);
//		            query.setParameter("listId", StringUtils.join(listId, ','));
			List<SummonStatus> listSummon = query.getResultList();
			if (!listSummon.isEmpty())
				return listSummon;
			else
				return null;
		} catch (NoResultException e) {
			return null;
		}
	}

	public List<SummonStatus> findSummonsPedingPhysicallysend() {
		try {
			String nativesql = "select st.id from investigation.summon_status st \r\n"
					+ "where TO_DATE(to_char(created_date,'YYYY-MM-DD'),'YYYY-MM-DD') < to_timestamp('2021-09-01 15:32:50.984', 'YYYY-MM-DD') and  aprroval_stage1=true and aprroval_stage2=true and is_signed=0  and is_rejected=false\r\n"
					+ "and is_physicallysent=false";
			Query nativequery = entityManager.createNativeQuery(nativesql);
			// query.setParameter("appUser", appUser);
			// nativequery.setParameter("isSigned", 0);
			List listId = nativequery.getResultList();
			if (listId.isEmpty())
				return new ArrayList<SummonStatus>();

			String sql = "Select e from " + SummonStatus.class.getName() + " e " //
					+ " Where e.id in (" + StringUtils.join(listId, ',') + ")";

			Query query = entityManager.createQuery(sql, SummonStatus.class);
//		            query.setParameter("listId", StringUtils.join(listId, ','));
			List<SummonStatus> listSummon = query.getResultList();
			if (!listSummon.isEmpty())
				return listSummon;
			else
				return null;
		} catch (NoResultException e) {
			return null;
		}
	}

	public List<NoticeStatus> findPendingNoticeAddl(AppUser userDetails) {
		List listId;
		List caseIdlst = null;
		List listuseridId = null;

		try {

			String nativesql = "select ins.case_details_id FROM investigation.inspector ins where  ins.app_user_user_id=:app_user_user_id and ins.is_ado=true and ins.is_active =true ";

			Query nativequery = entityManager.createNativeQuery(nativesql);
			nativequery.setParameter("app_user_user_id", userDetails.getUserId());
			caseIdlst = nativequery.getResultList();
			if (caseIdlst.isEmpty())
				return new ArrayList<NoticeStatus>();
			String nativesql1 = "select distinct ins.app_user_user_id FROM investigation.inspector ins  where ins.case_details_id in ("
					+ StringUtils.join(caseIdlst, ',') + ") ";

			Query nativequery1 = entityManager.createNativeQuery(nativesql1);

			listuseridId = nativequery1.getResultList();
			if (listuseridId.isEmpty())
				return new ArrayList<NoticeStatus>();
			String nativesql2 = "select distinct st.id from investigation.inspector i,investigation.notice_status st,investigation.case_details cd where st.app_user_user_id in ("
					+ StringUtils.join(listuseridId, ',')
					+ ") and  cd.id=st.case_details_id  and st.case_details_id in (" + StringUtils.join(caseIdlst, ',')
					+ ")   and  TO_DATE(to_char(created_date,'YYYY-MM-DD'),'YYYY-MM-DD') < to_timestamp('2021-09-01 15:32:50.984', 'YYYY-MM-DD')  and st.aprroval_stage2=true and st.aprroval_stage1=true and st.is_signed=0 and st.is_rejected=false";

			Query nativequery2 = entityManager.createNativeQuery(nativesql2);

			listId = nativequery2.getResultList();
			if (listId.isEmpty()) {
				return new ArrayList<NoticeStatus>();
			}
			String sql = "Select e from " + NoticeStatus.class.getName() + " e  Where e.id in ("
					+ StringUtils.join(listId, ',') + ")";

			Query query = entityManager.createQuery(sql, NoticeStatus.class); //
			// query.setParameter("listId", StringUtils.join(listId, ','));
			List<NoticeStatus> listSummon = query.getResultList();
			if (!listSummon.isEmpty())
				return listSummon;
			else
				return new ArrayList<NoticeStatus>();

		} catch (NoResultException e) {
			return new ArrayList<NoticeStatus>();
		}

	}

	public List<SummonStatus> findPendingSummonAddl(AppUser userDetails) {
		List listId;
		List caseIdlst = null;
		List listuseridId = null;

		try {

			String nativesql = "select ins.case_details_id FROM investigation.inspector ins where  ins.app_user_user_id=:app_user_user_id and ins.is_ado=true and ins.is_active =true ";

			Query nativequery = entityManager.createNativeQuery(nativesql);
			nativequery.setParameter("app_user_user_id", userDetails.getUserId());
			caseIdlst = nativequery.getResultList();
			if (caseIdlst.isEmpty())
				return new ArrayList<SummonStatus>();
			String nativesql1 = "select distinct ins.app_user_user_id FROM investigation.inspector ins  where ins.case_details_id in ("
					+ StringUtils.join(caseIdlst, ',') + ") ";

			Query nativequery1 = entityManager.createNativeQuery(nativesql1);

			listuseridId = nativequery1.getResultList();
			if (listuseridId.isEmpty())
				return new ArrayList<SummonStatus>();
			String nativesql2 = "select distinct st.id from investigation.inspector i,investigation.summon_status st,investigation.case_details cd where st.app_user_user_id in ("
					+ StringUtils.join(listuseridId, ',')
					+ ") and  cd.id=st.case_details_id  and st.case_details_id in (" + StringUtils.join(caseIdlst, ',')
					+ ")   and  TO_DATE(to_char(created_date,'YYYY-MM-DD'),'YYYY-MM-DD') < to_timestamp('2021-09-01 15:32:50.984', 'YYYY-MM-DD')  and st.aprroval_stage2=true and st.aprroval_stage1=true and st.is_signed=0 and st.is_rejected=false";

			Query nativequery2 = entityManager.createNativeQuery(nativesql2);

			listId = nativequery2.getResultList();
			if (listId.isEmpty()) {
				return new ArrayList<SummonStatus>();
			}
			String sql = "Select e from " + SummonStatus.class.getName() + " e  Where e.id in ("
					+ StringUtils.join(listId, ',') + ")";

			Query query = entityManager.createQuery(sql, SummonStatus.class); //
			// query.setParameter("listId", StringUtils.join(listId, ','));
			List<SummonStatus> listSummon = query.getResultList();
			if (!listSummon.isEmpty())
				return listSummon;
			else
				return new ArrayList<SummonStatus>();

		} catch (NoResultException e) {
			return new ArrayList<SummonStatus>();
		}

	}

	public List<NoticeStatus> findNoticeAddl(AppUser userDetails, int status, CaseDetails caseDetails) {
		List listId;
		List listuseridId = null;
		try {

			String nativesql = "select distinct ins.app_user_user_id FROM investigation.inspector ins  where ins.case_details_id=:case_details_id ";

			Query nativequery = entityManager.createNativeQuery(nativesql);
			nativequery.setParameter("case_details_id", caseDetails.getId());
			listuseridId = nativequery.getResultList();
			if (listuseridId.isEmpty())
				return new ArrayList<NoticeStatus>();

			if (status == 1) {
				nativesql = "select distinct st.id from investigation.inspector i,investigation.notice_status st,investigation.case_details cd where st.app_user_user_id in ("
						+ StringUtils.join(listuseridId, ',')
						+ ") and  cd.id=st.case_details_id and st.case_details_id=:case_details_id  and st.is_rejected=false";

			}
			if (status == 2) {
				nativesql = "select distinct st.id from investigation.inspector i,investigation.notice_status st,investigation.case_details cd where st.app_user_user_id in ("
						+ StringUtils.join(listuseridId, ',')
						+ ") and  cd.id=st.case_details_id and st.case_details_id=:case_details_id and st.is_signed= 0 and st.aprroval_stage2=true  and st.is_rejected=false";
			}
			if (status == 3) {
				nativesql = "select distinct st.id from investigation.inspector i,investigation.notice_status st,investigation.case_details cd where st.app_user_user_id in ("
						+ StringUtils.join(listuseridId, ',')
						+ ") and  cd.id=st.case_details_id and st.case_details_id=:case_details_id  and st.is_signed=1  and st.aprroval_stage2=true and st.aprroval_stage1=true and st.is_physicallysent=false";
			}
			if (status == 4) {
				nativesql = "select distinct st.id from investigation.inspector i,investigation.notice_status st,investigation.case_details cd where st.app_user_user_id in ("
						+ StringUtils.join(listuseridId, ',')
						+ ") and  cd.id=st.case_details_id and st.case_details_id=:case_details_id and st.is_signed= 0 and st.aprroval_stage1=false and st.aprroval_stage2=false and st.is_rejected=false and st.is_physicallysent=false";
			}
			if (status == 5) {
				nativesql = "select distinct st.id from investigation.inspector i,investigation.notice_status st,investigation.case_details cd where st.app_user_user_id in ("
						+ StringUtils.join(listuseridId, ',')
						+ ") and  cd.id=st.case_details_id and st.case_details_id=:case_details_id and  TO_DATE(to_char(created_date,'YYYY-MM-DD'),'YYYY-MM-DD') < to_timestamp('2021-09-01 15:32:50.984', 'YYYY-MM-DD')  and st.aprroval_stage2=true and st.aprroval_stage1=true and st.is_signed=0 and st.is_rejected=false";
			}
			if (status == 6) {
				nativesql = "select distinct st.id from investigation.inspector i,investigation.notice_status st,investigation.case_details cd where st.app_user_user_id in ("
						+ StringUtils.join(listuseridId, ',')
						+ ") and  cd.id=st.case_details_id and st.case_details_id=:case_details_id and st.is_physicallysent=true  and st.is_signed=1 and isverified=true  and  TO_DATE(to_char(st.created_date,'YYYY-MM-DD'),'YYYY-MM-DD') < to_timestamp('2021-09-01 15:32:50.984', 'YYYY-MM-DD')";
			}
			Query nativequery1 = entityManager.createNativeQuery(nativesql);
			nativequery1.setParameter("case_details_id", caseDetails.getId());
			listId = nativequery1.getResultList();
			if (listId.isEmpty()) {
				return new ArrayList<NoticeStatus>();
			}

			String sql = "Select e from " + NoticeStatus.class.getName() + " e  Where e.id in ("
					+ StringUtils.join(listId, ',') + ")";

			Query query = entityManager.createQuery(sql, NoticeStatus.class); //
			// query.setParameter("listId", StringUtils.join(listId, ','));
			List<NoticeStatus> listSummon = query.getResultList();
			if (!listSummon.isEmpty())
				return listSummon;
			else
				return new ArrayList<NoticeStatus>();

		} catch (NoResultException e) {
			return new ArrayList<NoticeStatus>();
		}

	}

	public List<SummonStatus> findSummonByStatusAddl(AppUser userDetails, int status, CaseDetails caseDetails) {
		List listId;
		List listuseridId = null;
		try {

			String nativesql = "select distinct ins.app_user_user_id FROM investigation.inspector ins  where ins.case_details_id=:case_details_id";

			Query nativequery = entityManager.createNativeQuery(nativesql);
			nativequery.setParameter("case_details_id", caseDetails.getId());
			listuseridId = nativequery.getResultList();
			if (listuseridId.isEmpty())
				return new ArrayList<SummonStatus>();

			// allSumon
			if (status == 1) {
				nativesql = "select distinct st.id from investigation.inspector i,investigation.summon_status st,investigation.case_details cd where st.app_user_user_id in ("
						+ StringUtils.join(listuseridId, ',')
						+ ") and  cd.id=st.case_details_id and st.case_details_id=:case_details_id  and st.is_rejected = false  ";

			}
			// approvedSummon
			if (status == 2) {
				nativesql = "select distinct st.id from investigation.inspector i,investigation.summon_status st,investigation.case_details cd where st.app_user_user_id in ("
						+ StringUtils.join(listuseridId, ',')
						+ ") and  cd.id=st.case_details_id and st.case_details_id=:case_details_id and st.is_signed= 0 and st.aprroval_stage2=true and st.aprroval_stage1=true  and st.is_rejected = false  and st.is_physicallysent=false  ";
			}
			// EsignedSummon
			if (status == 3) {
				nativesql = "select distinct st.id from investigation.inspector i,investigation.summon_status st,investigation.case_details cd where st.app_user_user_id in ("
						+ StringUtils.join(listuseridId, ',')
						+ ") and  cd.id=st.case_details_id and st.case_details_id=:case_details_id  and st.is_signed=1  and st.aprroval_stage2=true and st.is_rejected = false and  st.is_physicallysent=false  ";
			}
			// PendingSummon
			if (status == 4) {
				nativesql = "select distinct st.id from investigation.inspector i,investigation.summon_status st,investigation.case_details cd where st.app_user_user_id in ("
						+ StringUtils.join(listuseridId, ',')
						+ ") and  cd.id=st.case_details_id and st.case_details_id=:case_details_id and st.is_signed= 0 and st.aprroval_stage1=false and st.aprroval_stage2=false and st.is_physicallysent=false and st.is_rejected=false";
			}

			if (status == 5) {
				nativesql = "select distinct st.id from investigation.inspector i,investigation.summon_status st,investigation.case_details cd where st.app_user_user_id in ("
						+ StringUtils.join(listuseridId, ',')
						+ ") and  cd.id=st.case_details_id and st.case_details_id=:case_details_id and  TO_DATE(to_char(created_date,'YYYY-MM-DD'),'YYYY-MM-DD') < to_timestamp('2021-09-01 15:32:50.984', 'YYYY-MM-DD') and   st.aprroval_stage1=true and st.aprroval_stage2=true and st.is_signed=0  and st.is_rejected=false and st.is_physicallysent=false ";
			}
			if (status == 6) {
				nativesql = "select distinct st.id from investigation.inspector i,investigation.summon_status st,investigation.case_details cd where st.app_user_user_id in ("
						+ StringUtils.join(listuseridId, ',')
						+ ") and  cd.id=st.case_details_id and st.case_details_id=:case_details_id and st.is_physicallysent=true  and st.is_signed=1 and isverified=true  and  TO_DATE(to_char(st.created_date,'YYYY-MM-DD'),'YYYY-MM-DD') < to_timestamp('2021-09-01 15:32:50.984', 'YYYY-MM-DD')";
			}
			Query nativequery1 = entityManager.createNativeQuery(nativesql);
			nativequery1.setParameter("case_details_id", caseDetails.getId());
			listId = nativequery1.getResultList();
			if (listId.isEmpty()) {
				return new ArrayList<SummonStatus>();
			}

			String sql = "Select e from " + SummonStatus.class.getName() + " e  Where e.id in ("
					+ StringUtils.join(listId, ',') + ")";

			Query query = entityManager.createQuery(sql, SummonStatus.class); //
			// query.setParameter("listId", StringUtils.join(listId, ','));
			List<SummonStatus> listSummon = query.getResultList();
			if (!listSummon.isEmpty())
				return listSummon;
			else
				return new ArrayList<SummonStatus>();

		} catch (NoResultException e) {
			return new ArrayList<SummonStatus>();
		}
	}

	public List<SummonStatus> findOfflineSummonByAddl(AppUser userDetails, CaseDetails caseDetails) {
		List listId;
		try {

			String nativesql = "select distinct ins.app_user_user_id FROM investigation.inspector ins  where ins.case_details_id=:case_details_id ";

			Query nativequery = entityManager.createNativeQuery(nativesql);
			nativequery.setParameter("case_details_id", caseDetails.getId());
			listId = nativequery.getResultList();
			if (listId.isEmpty())
				return new ArrayList<SummonStatus>();

			String sql = "Select e from " + SummonStatus.class.getName() + " e " //
					+ " Where e.appUser in (" + StringUtils.join(listId, ',')
					+ ")  and caseDetails=:caseDetails and isOffline = true  and isSigned=1   and isRejected = false and Is_physicallysent =false order by e.id desc ";

			Query query = entityManager.createQuery(sql, SummonStatus.class);
			query.setParameter("caseDetails", caseDetails);
//		            query.setParameter("listId", StringUtils.join(listId, ','));
			List<SummonStatus> listSummon = query.getResultList();
			if (!listSummon.isEmpty())
				return listSummon;
			else
				return new ArrayList<SummonStatus>();

		} catch (NoResultException e) {
			return new ArrayList<SummonStatus>();
		}
	}

	public List<SummonStatus> physummonYetToUploadByUnits(Long unit_unit_id) {
		List listId;
		try {
			String nativesql1 = "SELECT ud.id FROM authentication.user_details ud where ud.unit_unit_id=:unit_unit_id ";
			Query nativequery1 = entityManager.createNativeQuery(nativesql1);
			nativequery1.setParameter("unit_unit_id", unit_unit_id);
			List listuseridId = nativequery1.getResultList();
			String nativesql = null;
			if (!listuseridId.isEmpty()) {

				nativesql = "select distinct st.id from investigation.inspector i,investigation.summon_status st,investigation.case_details cd where st.app_user_user_id in ("
						+ StringUtils.join(listuseridId, ',')
						+ ") and  cd.id=st.case_details_id and i.case_details_id=st.case_details_id and  TO_DATE(to_char(created_date,'YYYY-MM-DD'),'YYYY-MM-DD') < to_timestamp('2021-09-01 15:32:50.984', 'YYYY-MM-DD') and  st.aprroval_stage1=true and st.aprroval_stage2=true and st.is_signed=0  and st.is_rejected=false and st.is_physicallysent=false";

				Query nativequery = entityManager.createNativeQuery(nativesql);
				listId = nativequery.getResultList();
				if (listId.isEmpty())
					return new ArrayList<SummonStatus>();
			} else {
				return new ArrayList<SummonStatus>();
			}
			String sql = "Select e from " + SummonStatus.class.getName() + " e " //
					+ " Where e.id in (" + StringUtils.join(listId, ',') + ") order by e.id desc";

			Query query = entityManager.createQuery(sql, SummonStatus.class);
//		            query.setParameter("listId", StringUtils.join(listId, ','));
			List<SummonStatus> listSummon = query.getResultList();
			if (!listSummon.isEmpty())
				return listSummon;
			else
				return new ArrayList<SummonStatus>();

		} catch (NoResultException e) {
			return new ArrayList<SummonStatus>();
		}

	}

	public List<SummonStatus> findPhyReviewSummonByUsers(AppUser userDetails, CaseDetails caseDetails) {

		List listId;
		try {
			String nativesql = null;

			nativesql = "select distinct st.id from investigation.inspector i,investigation.summon_status st,"
					+ "investigation.case_details cd where st.app_user_user_id=:app_user_user_id and "
					+ "st.is_physicallysent=true and st.is_signed=1 and isverified=true and  st.is_rejected=false ";

			Query nativequery = entityManager.createNativeQuery(nativesql);

			nativequery.setParameter("app_user_user_id", userDetails);
			listId = nativequery.getResultList();
			if (listId.isEmpty())
				return new ArrayList<SummonStatus>();

			String sql = "Select e from " + SummonStatus.class.getName() + " e " //
					+ " Where e.id in (" + StringUtils.join(listId, ',')
					+ ")  and caseDetails=:caseDetails order by e.id desc";

			Query query = entityManager.createQuery(sql, SummonStatus.class);
			query.setParameter("caseDetails", caseDetails);
//		            query.setParameter("listId", StringUtils.join(listId, ','));
			List<SummonStatus> listSummon = query.getResultList();
			if (!listSummon.isEmpty())
				return listSummon;
			else
				return new ArrayList<SummonStatus>();

		} catch (NoResultException e) {
			return new ArrayList<SummonStatus>();
		}

	}

	public List<SummonStatus> physummonYetToUploadByUser(AppUser userDetails, CaseDetails caseDetails) {

		List listId;
		try {
			String nativesql = null;

			nativesql = "select distinct st.id from investigation.inspector i,investigation.summon_status st,"
					+ "investigation.case_details cd where st.app_user_user_id=:app_user_user_id and "
					+ "TO_DATE(to_char(created_date,'YYYY-MM-DD'),'YYYY-MM-DD') < to_timestamp('2021-09-01 15:32:50.984', 'YYYY-MM-DD') and   st.aprroval_stage2=true and st.is_signed=0 ";

			Query nativequery = entityManager.createNativeQuery(nativesql);

			nativequery.setParameter("app_user_user_id", userDetails);
			listId = nativequery.getResultList();
			if (listId.isEmpty())
				return new ArrayList<SummonStatus>();

			String sql = "Select e from " + SummonStatus.class.getName() + " e " //
					+ " Where e.id in (" + StringUtils.join(listId, ',')
					+ ")  and caseDetails=:caseDetails order by e.id desc";

			Query query = entityManager.createQuery(sql, SummonStatus.class);
			query.setParameter("caseDetails", caseDetails);
//		            query.setParameter("listId", StringUtils.join(listId, ','));
			List<SummonStatus> listSummon = query.getResultList();
			if (!listSummon.isEmpty())
				return listSummon;
			else
				return new ArrayList<SummonStatus>();

		} catch (NoResultException e) {
			return new ArrayList<SummonStatus>();
		}

	}

	public List<Object[]> findAllBYUnit() {
		String s = "::::";
		try {
			String nativesql = "select unit_id , unit_name from (SELECT split_part(unit_name" + s + "TEXT,'-', 1) y,"
					+ "split_part(unit_name" + s + "TEXT,'-', 2) m,cast(coalesce(nullif( split_part(unit_name" + s
					+ "TEXT,'-', 2),''),'0') as Integer) as "
					+ "result,unit_id , unit_name FROM authentication.unit_details ORDER BY result  ASC  ) temp";

			Query nativequery = entityManager.createNativeQuery(nativesql);
			return (List<Object[]>) nativequery.getResultList();
		} catch (NoResultException e) {
			return null;
		}
	}

	public List<personcompanyApproval> findPersonStatusByUserId(int page_id, int pageSize, String sortField,
			String sortDirection, AppUser userDetails) {
		try {

			String role = roleDAO.getRoleName(userDetails.getUserId());
			List listId;
			if (role.equals("ROLE_DIRECTOR")) {
				String nativesql = "select ss.id from gams.personcompany_approval ss,investigation.inspector ins ,gams.relation_person_company pc  where pc.case_details_id=ins.case_details_id ";
				Query nativequery = entityManager.createNativeQuery(nativesql);

				listId = nativequery.getResultList();
			} else if (role.equals("ROLE_PROSECUTION_ADMIN")) {
				String nativesql = "select approval_status_id from gams.personcompany_approval ss, gams.relation_person_company pc ,investigation.case_details cd  where pc.case_details_id=cd.id and ss.id= pc.rpc_id and cd.case_stage = 2 ";
				Query nativequery = entityManager.createNativeQuery(nativesql);

				listId = nativequery.getResultList();
			}

			else if (role.equals("ROLE_PROSECUTION")) {
				String nativesql = "select approval_status_id from gams.personcompany_approval ss, gams.relation_person_company pc \r\n"
						+ " where  ss.created_by_id=:userId and pc.rpc_id =ss.id";
				Query nativequery = entityManager.createNativeQuery(nativesql);
				if (userDetails != null) {
					nativequery.setParameter("userId", userDetails.getUserId());

				} else {
					nativequery.setParameter("userId", new AppUser().getUserId());

				}
				listId = nativequery.getResultList();
			} else {
				String nativesql = "select approval_status_id from gams.personcompany_approval ss,investigation.inspector ins , gams.relation_person_company pc \r\n"
						+ " where pc.case_details_id=ins.case_details_id and ins.app_user_user_id=:userId and pc.rpc_id =ss.id \r\n"
						+ "and ins.is_active = true ";
				Query nativequery = entityManager.createNativeQuery(nativesql);
				if (userDetails != null) {
					nativequery.setParameter("userId", userDetails.getUserId());

				} else {
					nativequery.setParameter("userId", new AppUser().getUserId());

				}
				listId = nativequery.getResultList();
			}

			if (listId.isEmpty())
				return (List<personcompanyApproval>) new ArrayList<personcompanyApproval>();

			String sql1 = "select approval_status_id from  gams.personcompany_approval e where e.approval_status_id in ("
					+ StringUtils.join(listId, ',') + ") order by approval_status_id desc  LIMIT " + pageSize
					+ " OFFSET " + page_id;
			Query nativequery = entityManager.createNativeQuery(sql1);
			List listId1 = nativequery.getResultList();

			if (listId1.isEmpty())
				return (List<personcompanyApproval>) new ArrayList<personcompanyApproval>();

			String sql = "Select e from " + personcompanyApproval.class.getName() + " e " //
					+ " Where e.id in (" + StringUtils.join(listId1, ',') + ")";

			Query query = entityManager.createQuery(sql, personcompanyApproval.class);
//	            query.setParameter("listId", StringUtils.join(listId, ','));
			List<personcompanyApproval> listPerson = query.getResultList();
			if (!listPerson.isEmpty())
				return (List<personcompanyApproval>) listPerson;
			else
				return null;

		} catch (NoResultException e) {
			return null;
		}
	}

	public Page<personcompanyApproval> PersonlistAll(int pageNo, int pageSize, String sortField, String sortDirection,
			AppUser userDetails) {
		String role = roleDAO.getRoleName(userDetails.getUserId());
		Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending()
				: Sort.by(sortField).descending();
		UserDetails userdt = userdtlRepo.findById(userDetails.getUserId()).get();
		Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
		if (role.equals("ROLE_DIRECTOR")) {
			return this.pcaRepo.findAll(pageable);
		}
		if (role.equals("ROLE_PROSECUTION")) {
			return this.pcaRepo.findAllByCreatedBy(pageable, userdt);
		} else {
			return this.pcaRepo.findAllByCreatedBy(pageable, userdt);
		}
	}

	public Page<SummonStatus> summonlistAsIO(int pageNo, int pageSize, String sortField, String sortDirection,
			AppUser userDetails) {
		Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending()
				: Sort.by(sortField).descending();

		Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);

		return this.summonRepo.findAllByAppUser(pageable, userDetails);

	}

	public List<SummonStatus> findSummon_signed1(int pageNo, int pageSize, String sortField, String sortDirection,
			AppUser userDetails, SummonReportDTO summonStatusmodel) {
		try {

			String role = roleDAO.getRoleName(userDetails.getUserId());
			List listId = null;

			if (summonStatusmodel.getIsPhysically_signed() == null) {
				if (role.equals("ROLE_DIRECTOR")) {
					String nativesql = "select ss.id from investigation.summon_status ss,investigation.inspector ins where ss.case_details_id=ins.case_details_id and ss.is_signed=1 ";
					Query nativequery = entityManager.createNativeQuery(nativesql);

					listId = nativequery.getResultList();
				} else {
					String nativesql = "select ss.id from investigation.summon_status ss,investigation.inspector ins where ss.case_details_id=ins.case_details_id and ins.app_user_user_id=:userId and ss.is_signed=1  and ins.is_active = true";
					Query nativequery = entityManager.createNativeQuery(nativesql);
					if (userDetails != null) {
						nativequery.setParameter("userId", userDetails.getUserId());
					} else {
						nativequery.setParameter("userId", new AppUser().getUserId());
					}

					listId = nativequery.getResultList();
				}
			} 
			
	else if(summonStatusmodel.getType()!=0 && summonStatusmodel.getIsPhysically_signed()==null) {
				
				if(summonStatusmodel.getType()==1) {
					if (role.equals("ROLE_DIRECTOR")) {
						String nativesql = "select ss.id from investigation.summon_status ss,investigation.inspector ins , investigation.summon_type st where ss.case_details_id=ins.case_details_id and ss.is_signed=1 and ss.aprroval_stage2=true and ss.aprroval_stage1=true and st.id=ss.summon_type_id and st.name=:name";
						Query nativequery = entityManager.createNativeQuery(nativesql);
						nativequery.setParameter("name", summonStatusmodel.getValue());
						listId = nativequery.getResultList();
					} else {
						String nativesql = "select ss.id from investigation.summon_status ss,investigation.inspector ins, investigation.summon_type st where ss.case_details_id=ins.case_details_id and ins.app_user_user_id=:userId and ss.is_signed=1   and ins.is_active = true and st.id=ss.summon_type_id and st.name=:name";
						Query nativequery = entityManager.createNativeQuery(nativesql);
						if (userDetails != null) {
							nativequery.setParameter("name", summonStatusmodel.getValue());
							nativequery.setParameter("userId", userDetails.getUserId());
						} else {
							nativequery.setParameter("userId", new AppUser().getUserId());
						}
						listId = nativequery.getResultList();
					}					
				}
				
				
				if(summonStatusmodel.getType()==2) {
					
					if (role.equals("ROLE_DIRECTOR")) {
						String nativesql = "select ss.id from investigation.summon_status  ss,investigation.inspector ins , investigation.summon_type st where ss.case_details_id=ins.case_details_id and ss.is_signed=1 and ss.aprroval_stage2=true and ss.aprroval_stage1=true and st.id=ss.summon_type_id and ss.summon_din like'%" + summonStatusmodel.getValue() +"%'";
						Query nativequery = entityManager.createNativeQuery(nativesql);                                           
						//nativequery.setParameter("notice_din", noticeStatusmodel.getValue());																																																										
							
						listId = nativequery.getResultList();
					} else {
						String nativesql = "select ss.id from investigation.summon_status  ss,investigation.inspector ins, investigation.summon_type st where ss.case_details_id=ins.case_details_id and ins.app_user_user_id=:userId and ss.is_signed=1   and ins.is_active = true and st.id=ss.summon_type_id and ss.summon_din like'%" + summonStatusmodel.getValue() +"%'";
						Query nativequery = entityManager.createNativeQuery(nativesql);
						if (userDetails != null) {
						//	nativequery.setParameter("notice_din", noticeStatusmodel.getValue());
							nativequery.setParameter("userId", userDetails.getUserId());
						} else {
							nativequery.setParameter("userId", new AppUser().getUserId());
						}
						listId = nativequery.getResultList();
					}					
				}
				
			}
			
			else if (summonStatusmodel.getIsPhysically_signed().equalsIgnoreCase("true")) {
				if(summonStatusmodel.getType()==0) {
				if (role.equals("ROLE_DIRECTOR")) {
					String nativesql = "select ss.id from investigation.summon_status ss,investigation.inspector ins where ss.case_details_id=ins.case_details_id and ss.is_signed=1  and ss.is_physicallysent=true";
					Query nativequery = entityManager.createNativeQuery(nativesql);

					listId = nativequery.getResultList();
				} else {
					String nativesql = "select ss.id from investigation.summon_status ss,investigation.inspector ins where ss.case_details_id=ins.case_details_id and ins.app_user_user_id=:userId and ss.is_signed=1  and ins.is_active = true and ss.is_physicallysent=true";
					Query nativequery = entityManager.createNativeQuery(nativesql);
					if (userDetails != null) {
						nativequery.setParameter("userId", userDetails.getUserId());
					} else {
						nativequery.setParameter("userId", new AppUser().getUserId());
					}

					listId = nativequery.getResultList();
				}
			} 
				
				if(summonStatusmodel.getType()!=0) {
					if(summonStatusmodel.getType()==1) {
						if (role.equals("ROLE_DIRECTOR")) {
							String nativesql = "select ss.id from investigation.summon_status ss,investigation.inspector ins , investigation.summon_type st where ss.case_details_id=ins.case_details_id and ss.is_signed=1 and ss.aprroval_stage2=true and ss.aprroval_stage1=true and st.id=ss.summon_type_id and st.name=:name  and ss.is_physicallysent=true";
							Query nativequery = entityManager.createNativeQuery(nativesql);
							nativequery.setParameter("name", summonStatusmodel.getValue());
							listId = nativequery.getResultList();
						} else {
							String nativesql = "select ss.id from investigation.summon_status ss,investigation.inspector ins, investigation.summon_type st where ss.case_details_id=ins.case_details_id and ins.app_user_user_id=:userId and ss.is_signed=1   and ins.is_active = true and st.id=ss.summon_type_id and st.name=:name and ss.is_physicallysent=true";
							Query nativequery = entityManager.createNativeQuery(nativesql);
							if (userDetails != null) {
								nativequery.setParameter("name", summonStatusmodel.getValue());
								nativequery.setParameter("userId", userDetails.getUserId());
							} else {
								nativequery.setParameter("userId", new AppUser().getUserId());
							}
							listId = nativequery.getResultList();
						}					
					}
					
					
					if(summonStatusmodel.getType()==2) {
						
						if (role.equals("ROLE_DIRECTOR")) {
							String nativesql = "select ss.id from investigation.summon_status ss,investigation.inspector ins , investigation.summon_type st where ss.case_details_id=ins.case_details_id and ss.is_signed=1 and ss.aprroval_stage2=true and ss.aprroval_stage1=true and st.id=ss.summon_type_id and ss.is_physicallysent=true and ss.summon_din like'%" + summonStatusmodel.getValue() +"%'";
							Query nativequery = entityManager.createNativeQuery(nativesql);                                           
							//nativequery.setParameter("notice_din", noticeStatusmodel.getValue());																																																										
								
							listId = nativequery.getResultList();
						} else {
							String nativesql = "select ss.id from investigation.summon_status ss,investigation.inspector ins, investigation.summon_type st where ss.case_details_id=ins.case_details_id and ins.app_user_user_id=:userId and ss.is_signed=1   and ins.is_active = true and st.id=ss.summon_type_id and ss.is_physicallysent=true and ss.summon_din like'%" + summonStatusmodel.getValue() +"%'";
							Query nativequery = entityManager.createNativeQuery(nativesql);
							if (userDetails != null) {
							//	nativequery.setParameter("notice_din", noticeStatusmodel.getValue());
								nativequery.setParameter("userId", userDetails.getUserId());
							} else {
								nativequery.setParameter("userId", new AppUser().getUserId());
							}
							listId = nativequery.getResultList();
						}					
					}
					
			}
			}else {
				if(summonStatusmodel.getType()==0) {
					if (role.equals("ROLE_DIRECTOR")) {
						String nativesql = "select ss.id from investigation.summon_status ss,investigation.inspector ins where ss.case_details_id=ins.case_details_id and ss.is_signed=1 and ss.aprroval_stage2=true and ss.aprroval_stage1=true and ss.is_physicallysent=false";
						Query nativequery = entityManager.createNativeQuery(nativesql);

						listId = nativequery.getResultList();
					} else {
						String nativesql = "select ss.id from investigation.summon_status ss,investigation.inspector ins where ss.case_details_id=ins.case_details_id and ins.app_user_user_id=:userId and ss.is_signed=1   and ins.is_active = true and ss.is_physicallysent=false";
						Query nativequery = entityManager.createNativeQuery(nativesql);
						if (userDetails != null) {
							nativequery.setParameter("userId", userDetails.getUserId());
						} else {
							nativequery.setParameter("userId", new AppUser().getUserId());
						}
						listId = nativequery.getResultList();
					}
					}
					
					if(summonStatusmodel.getType()!=0) {
						if(summonStatusmodel.getType()==1) {
							if (role.equals("ROLE_DIRECTOR")) {
								String nativesql = "select ss.id from investigation.summon_status ss,investigation.inspector ins , investigation.summon_type st where ss.case_details_id=ins.case_details_id and ss.is_signed=1 and ss.aprroval_stage2=true and ss.aprroval_stage1=true and st.id=ss.summon_type_id and st.name=:name   ss.is_physicallysent=false";
								Query nativequery = entityManager.createNativeQuery(nativesql);
								nativequery.setParameter("name", summonStatusmodel.getValue());
								listId = nativequery.getResultList();
							} else {
								String nativesql = "select ss.id from investigation.summon_status ss,investigation.inspector ins, investigation.summon_type st where ss.case_details_id=ins.case_details_id and ins.app_user_user_id=:userId and ss.is_signed=1   and ins.is_active = true and st.id=ss.summon_type_id and st.name=:name and  ss.is_physicallysent=false";
								Query nativequery = entityManager.createNativeQuery(nativesql);
								if (userDetails != null) {
									nativequery.setParameter("name", summonStatusmodel.getValue());
									nativequery.setParameter("userId", userDetails.getUserId());
								} else {
									nativequery.setParameter("userId", new AppUser().getUserId());
								}
								listId = nativequery.getResultList();
							}					
						}
						
						
						if(summonStatusmodel.getType()==2) {
							
							if (role.equals("ROLE_DIRECTOR")) {
								String nativesql = "select ss.id from investigation.summon_status ss,investigation.inspector ins , investigation.summon_type st where ss.case_details_id=ins.case_details_id and ss.is_signed=1 and ss.aprroval_stage2=true and ss.aprroval_stage1=true and st.id=ss.summon_type_id and ss.is_physicallysent=false and ss.summon_din like'%" + summonStatusmodel.getValue() +"%'";
								Query nativequery = entityManager.createNativeQuery(nativesql);                                           
								//nativequery.setParameter("notice_din", noticeStatusmodel.getValue());																																																										
									
								listId = nativequery.getResultList();
							} else {
								String nativesql = "select ss.id from investigation.summon_status ss,investigation.inspector ins, investigation.summon_type st where ss.case_details_id=ins.case_details_id and ins.app_user_user_id=:userId and ss.is_signed=1   and ins.is_active = true and st.id=ss.summon_type_id and ss.is_physicallysent=false and ss.summon_din like'%" + summonStatusmodel.getValue() +"%'";
								Query nativequery = entityManager.createNativeQuery(nativesql);
								if (userDetails != null) {
								//	nativequery.setParameter("notice_din", noticeStatusmodel.getValue());
									nativequery.setParameter("userId", userDetails.getUserId());
								} else {
									nativequery.setParameter("userId", new AppUser().getUserId());
								}
								listId = nativequery.getResultList();
							}					
						}
						
				}
			}
			if (listId.isEmpty())
				return new ArrayList<SummonStatus>();

			Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending()
					: Sort.by(sortField).descending();
			Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
			int offset = (int) pageable.getOffset();
			int number = pageable.getPageNumber();
			int size = pageable.getPageSize();

			/*
			 * SummonStatus ss = new SummonStatus(); ss.setIds(listId); Page<SummonStatus>
			 * listSummon1 = summonRepo.findAllByIdsIn(pageable, ss.getIds());
			 */

			// Page<SummonStatus> listSummon1 = summonRepo.findAllByIdIn(pageable,
			// StringUtils.join(listId, ','));
			if (listId.isEmpty())
				return new ArrayList<SummonStatus>();

			String sql = "Select e.id from investigation.summon_status  e " //
					+ " Where e.id in (" + StringUtils.join(listId, ',') + ") ORDER BY e.id  desc OFFSET " + offset
					+ " LIMIT " + size + "";

			Query query = entityManager.createNativeQuery(sql);
//	            query.setParameter("listId", StringUtils.join(listId, ','));

			List listId1 = query.getResultList();

			if (listId1.isEmpty())
				return new ArrayList<SummonStatus>();

			String sql2 = "Select e from " + SummonStatus.class.getName() + " e " //
					+ " Where e.id in (" + StringUtils.join(listId1, ',') + ") order by e.id  desc";

			Query query2 = entityManager.createQuery(sql2, SummonStatus.class);
//	            query.setParameter("listId", StringUtils.join(listId, ','));
			@SuppressWarnings("unchecked")
			List<SummonStatus> listSummon = query2.getResultList();
			if (!listSummon.isEmpty())
				return listSummon;
			else
				return null;

		} catch (NoResultException e) {
			return null;
		}
	}

	public List<NoticeStatus> findNotice_signed1(int pageNo, int pageSize, String sortField, String sortDirection,
			AppUser userDetails, SummonReportDTO noticeStatusmodel) {
		try {

			String role = roleDAO.getRoleName(userDetails.getUserId());
			List listId = null;
			if(noticeStatusmodel.getIsPhysically_signed()==null && noticeStatusmodel.getType()==0) {
			if (role.equals("ROLE_DIRECTOR")) {
				String nativesql = "select ss.id from investigation.notice_status ss,investigation.inspector ins where ss.case_details_id=ins.case_details_id and ss.is_signed=1 and ss.aprroval_stage2=true and ss.aprroval_stage1=true";
				Query nativequery = entityManager.createNativeQuery(nativesql);

				listId = nativequery.getResultList();
			} else {
				String nativesql = "select ss.id from investigation.notice_status ss,investigation.inspector ins where ss.case_details_id=ins.case_details_id and ins.app_user_user_id=:userId and ss.is_signed=1   and ins.is_active = true";
				Query nativequery = entityManager.createNativeQuery(nativesql);
				if (userDetails != null) {
					nativequery.setParameter("userId", userDetails.getUserId());
				} else {
					nativequery.setParameter("userId", new AppUser().getUserId());
				}
				listId = nativequery.getResultList();
			}
			}
			
			
			else if(noticeStatusmodel.getType()!=0 && noticeStatusmodel.getIsPhysically_signed()==null) {
				
				if(noticeStatusmodel.getType()==1) {
					if (role.equals("ROLE_DIRECTOR")) {
						String nativesql = "select ss.id from investigation.notice_status ss,investigation.inspector ins , investigation.summon_type st where ss.case_details_id=ins.case_details_id and ss.is_signed=1 and ss.aprroval_stage2=true and ss.aprroval_stage1=true and st.id=ss.summon_type_id and st.name=:name";
						Query nativequery = entityManager.createNativeQuery(nativesql);
						nativequery.setParameter("name", noticeStatusmodel.getValue());
						listId = nativequery.getResultList();
					} else {
						String nativesql = "select ss.id from investigation.notice_status ss,investigation.inspector ins, investigation.summon_type st where ss.case_details_id=ins.case_details_id and ins.app_user_user_id=:userId and ss.is_signed=1   and ins.is_active = true and st.id=ss.summon_type_id and st.name=:name";
						Query nativequery = entityManager.createNativeQuery(nativesql);
						if (userDetails != null) {
							nativequery.setParameter("name", noticeStatusmodel.getValue());
							nativequery.setParameter("userId", userDetails.getUserId());
						} else {
							nativequery.setParameter("userId", new AppUser().getUserId());
						}
						listId = nativequery.getResultList();
					}					
				}
				
				
				if(noticeStatusmodel.getType()==2) {
					
					if (role.equals("ROLE_DIRECTOR")) {
						String nativesql = "select ss.id from investigation.notice_status ss,investigation.inspector ins , investigation.summon_type st where ss.case_details_id=ins.case_details_id and ss.is_signed=1 and ss.aprroval_stage2=true and ss.aprroval_stage1=true and st.id=ss.summon_type_id and ss.notice_din like'%" + noticeStatusmodel.getValue() +"%'";
						Query nativequery = entityManager.createNativeQuery(nativesql);                                           
						//nativequery.setParameter("notice_din", noticeStatusmodel.getValue());																																																										
							
						listId = nativequery.getResultList();
					} else {
						String nativesql = "select ss.id from investigation.notice_status ss,investigation.inspector ins, investigation.summon_type st where ss.case_details_id=ins.case_details_id and ins.app_user_user_id=:userId and ss.is_signed=1   and ins.is_active = true and st.id=ss.summon_type_id and ss.notice_din like'%" + noticeStatusmodel.getValue() +"%'";
						Query nativequery = entityManager.createNativeQuery(nativesql);
						if (userDetails != null) {
						//	nativequery.setParameter("notice_din", noticeStatusmodel.getValue());
							nativequery.setParameter("userId", userDetails.getUserId());
						} else {
							nativequery.setParameter("userId", new AppUser().getUserId());
						}
						listId = nativequery.getResultList();
					}					
				}
				
			}
			else if(noticeStatusmodel.getIsPhysically_signed().equalsIgnoreCase("true")) {
				if(noticeStatusmodel.getType()==0) {
				if (role.equals("ROLE_DIRECTOR")) {
					String nativesql = "select ss.id from investigation.notice_status ss,investigation.inspector ins where ss.case_details_id=ins.case_details_id and ss.is_signed=1 and ss.aprroval_stage2=true and ss.aprroval_stage1=true and ss.is_physicallysent=true";
					Query nativequery = entityManager.createNativeQuery(nativesql);

					listId = nativequery.getResultList();
				} else {
					String nativesql = "select ss.id from investigation.notice_status ss,investigation.inspector ins where ss.case_details_id=ins.case_details_id and ins.app_user_user_id=:userId and ss.is_signed=1   and ins.is_active = true and ss.is_physicallysent=true";
					Query nativequery = entityManager.createNativeQuery(nativesql);
					if (userDetails != null) {
						nativequery.setParameter("userId", userDetails.getUserId());
					} else {
						nativequery.setParameter("userId", new AppUser().getUserId());
					}
					listId = nativequery.getResultList();
				}				
				}
				
				if(noticeStatusmodel.getType()!=0) {
					if(noticeStatusmodel.getType()==1) {
						if (role.equals("ROLE_DIRECTOR")) {
							String nativesql = "select ss.id from investigation.notice_status ss,investigation.inspector ins , investigation.summon_type st where ss.case_details_id=ins.case_details_id and ss.is_signed=1 and ss.aprroval_stage2=true and ss.aprroval_stage1=true and st.id=ss.summon_type_id and st.name=:name  and ss.is_physicallysent=true";
							Query nativequery = entityManager.createNativeQuery(nativesql);
							nativequery.setParameter("name", noticeStatusmodel.getValue());
							listId = nativequery.getResultList();
						} else {
							String nativesql = "select ss.id from investigation.notice_status ss,investigation.inspector ins, investigation.summon_type st where ss.case_details_id=ins.case_details_id and ins.app_user_user_id=:userId and ss.is_signed=1   and ins.is_active = true and st.id=ss.summon_type_id and st.name=:name and ss.is_physicallysent=true";
							Query nativequery = entityManager.createNativeQuery(nativesql);
							if (userDetails != null) {
								nativequery.setParameter("name", noticeStatusmodel.getValue());
								nativequery.setParameter("userId", userDetails.getUserId());
							} else {
								nativequery.setParameter("userId", new AppUser().getUserId());
							}
							listId = nativequery.getResultList();
						}					
					}
					
					
					if(noticeStatusmodel.getType()==2) {
						
						if (role.equals("ROLE_DIRECTOR")) {
							String nativesql = "select ss.id from investigation.notice_status ss,investigation.inspector ins , investigation.summon_type st where ss.case_details_id=ins.case_details_id and ss.is_signed=1 and ss.aprroval_stage2=true and ss.aprroval_stage1=true and st.id=ss.summon_type_id and ss.is_physicallysent=true and ss.notice_din like'%" + noticeStatusmodel.getValue() +"%'";
							Query nativequery = entityManager.createNativeQuery(nativesql);                                           
							//nativequery.setParameter("notice_din", noticeStatusmodel.getValue());																																																										
								
							listId = nativequery.getResultList();
						} else {
							String nativesql = "select ss.id from investigation.notice_status ss,investigation.inspector ins, investigation.summon_type st where ss.case_details_id=ins.case_details_id and ins.app_user_user_id=:userId and ss.is_signed=1   and ins.is_active = true and st.id=ss.summon_type_id and ss.is_physicallysent=true and ss.notice_din like'%" + noticeStatusmodel.getValue() +"%'";
							Query nativequery = entityManager.createNativeQuery(nativesql);
							if (userDetails != null) {
							//	nativequery.setParameter("notice_din", noticeStatusmodel.getValue());
								nativequery.setParameter("userId", userDetails.getUserId());
							} else {
								nativequery.setParameter("userId", new AppUser().getUserId());
							}
							listId = nativequery.getResultList();
						}					
					}
					
			}
			}
			
			else {
				if(noticeStatusmodel.getType()==0) {
				if (role.equals("ROLE_DIRECTOR")) {
					String nativesql = "select ss.id from investigation.notice_status ss,investigation.inspector ins where ss.case_details_id=ins.case_details_id and ss.is_signed=1 and ss.aprroval_stage2=true and ss.aprroval_stage1=true and ss.is_physicallysent=false";
					Query nativequery = entityManager.createNativeQuery(nativesql);

					listId = nativequery.getResultList();
				} else {
					String nativesql = "select ss.id from investigation.notice_status ss,investigation.inspector ins where ss.case_details_id=ins.case_details_id and ins.app_user_user_id=:userId and ss.is_signed=1   and ins.is_active = true and ss.is_physicallysent=false";
					Query nativequery = entityManager.createNativeQuery(nativesql);
					if (userDetails != null) {
						nativequery.setParameter("userId", userDetails.getUserId());
					} else {
						nativequery.setParameter("userId", new AppUser().getUserId());
					}
					listId = nativequery.getResultList();
				}
				}
				
				if(noticeStatusmodel.getType()!=0) {
					if(noticeStatusmodel.getType()==1) {
						if (role.equals("ROLE_DIRECTOR")) {
							String nativesql = "select ss.id from investigation.notice_status ss,investigation.inspector ins , investigation.summon_type st where ss.case_details_id=ins.case_details_id and ss.is_signed=1 and ss.aprroval_stage2=true and ss.aprroval_stage1=true and st.id=ss.summon_type_id and st.name=:name   ss.is_physicallysent=false";
							Query nativequery = entityManager.createNativeQuery(nativesql);
							nativequery.setParameter("name", noticeStatusmodel.getValue());
							listId = nativequery.getResultList();
						} else {
							String nativesql = "select ss.id from investigation.notice_status ss,investigation.inspector ins, investigation.summon_type st where ss.case_details_id=ins.case_details_id and ins.app_user_user_id=:userId and ss.is_signed=1   and ins.is_active = true and st.id=ss.summon_type_id and st.name=:name and  ss.is_physicallysent=false";
							Query nativequery = entityManager.createNativeQuery(nativesql);
							if (userDetails != null) {
								nativequery.setParameter("name", noticeStatusmodel.getValue());
								nativequery.setParameter("userId", userDetails.getUserId());
							} else {
								nativequery.setParameter("userId", new AppUser().getUserId());
							}
							listId = nativequery.getResultList();
						}					
					}
					
					
					if(noticeStatusmodel.getType()==2) {
						
						if (role.equals("ROLE_DIRECTOR")) {
							String nativesql = "select ss.id from investigation.notice_status ss,investigation.inspector ins , investigation.summon_type st where ss.case_details_id=ins.case_details_id and ss.is_signed=1 and ss.aprroval_stage2=true and ss.aprroval_stage1=true and st.id=ss.summon_type_id and ss.is_physicallysent=false and ss.notice_din like'%" + noticeStatusmodel.getValue() +"%'";
							Query nativequery = entityManager.createNativeQuery(nativesql);                                           
							//nativequery.setParameter("notice_din", noticeStatusmodel.getValue());																																																										
								
							listId = nativequery.getResultList();
						} else {
							String nativesql = "select ss.id from investigation.notice_status ss,investigation.inspector ins, investigation.summon_type st where ss.case_details_id=ins.case_details_id and ins.app_user_user_id=:userId and ss.is_signed=1   and ins.is_active = true and st.id=ss.summon_type_id and ss.is_physicallysent=false and ss.notice_din like'%" + noticeStatusmodel.getValue() +"%'";
							Query nativequery = entityManager.createNativeQuery(nativesql);
							if (userDetails != null) {
							//	nativequery.setParameter("notice_din", noticeStatusmodel.getValue());
								nativequery.setParameter("userId", userDetails.getUserId());
							} else {
								nativequery.setParameter("userId", new AppUser().getUserId());
							}
							listId = nativequery.getResultList();
						}					
					}
					
			}
			}
			if (listId.isEmpty())
				return new ArrayList<NoticeStatus>();

			Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending() :
				Sort.by(sortField).descending();
			Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
			int offset = (int) pageable.getOffset();
			int number  =  pageable.getPageNumber();
			int  size =                pageable.getPageSize();
			
			
			String sql = "Select e.id from investigation.notice_status  e " //
					+ " Where e.id in (" + StringUtils.join(listId, ',') + ") ORDER BY e.id desc OFFSET "+offset+" LIMIT "+size+"";

			Query query = entityManager.createNativeQuery(sql);
//	            query.setParameter("listId", StringUtils.join(listId, ','));
			
		        List  listId1 = query.getResultList();
			
			
			if (listId1.isEmpty())
				return  new ArrayList<NoticeStatus>();
			
			
			
			String sql1 = "Select e from " + NoticeStatus.class.getName() + " e " //
					+ " Where e.id in (" + StringUtils.join(listId1, ',') + ") order by e.orderSignedDate desc";

			Query query1 = entityManager.createQuery(sql1, NoticeStatus.class);
//	            query.setParameter("listId", StringUtils.join(listId, ','));
			List<NoticeStatus> listSummon = query1.getResultList();
			if (!listSummon.isEmpty())
				return listSummon;
			else
				return null;

		} catch (NoResultException e) {
			return null;
		}
	}

	public List<SummonStatus> findSummon_signeds(AppUser userDetails, SummonReportDTO summonStatusmodel) {
		try {

			String role = roleDAO.getRoleName(userDetails.getUserId());
			List listId;

			if (summonStatusmodel.getIsPhysically_signed() == null) {
				if (role.equals("ROLE_DIRECTOR")) {
					String nativesql = "select ss.id from investigation.summon_status ss,investigation.inspector ins where ss.case_details_id=ins.case_details_id and ss.is_signed=1 ";
					Query nativequery = entityManager.createNativeQuery(nativesql);

					listId = nativequery.getResultList();
				} else {
					String nativesql = "select ss.id from investigation.summon_status ss,investigation.inspector ins where ss.case_details_id=ins.case_details_id and ins.app_user_user_id=:userId and ss.is_signed=1  and ins.is_active = true";
					Query nativequery = entityManager.createNativeQuery(nativesql);
					if (userDetails != null) {
						nativequery.setParameter("userId", userDetails.getUserId());
					} else {
						nativequery.setParameter("userId", new AppUser().getUserId());
					}

					listId = nativequery.getResultList();
				}
			} else if (summonStatusmodel.getIsPhysically_signed().equalsIgnoreCase("true")) {
				if (role.equals("ROLE_DIRECTOR")) {
					String nativesql = "select ss.id from investigation.summon_status ss,investigation.inspector ins where ss.case_details_id=ins.case_details_id and ss.is_signed=1  and ss.is_physicallysent=true";
					Query nativequery = entityManager.createNativeQuery(nativesql);

					listId = nativequery.getResultList();
				} else {
					String nativesql = "select ss.id from investigation.summon_status ss,investigation.inspector ins where ss.case_details_id=ins.case_details_id and ins.app_user_user_id=:userId and ss.is_signed=1  and ins.is_active = true and ss.is_physicallysent=true";
					Query nativequery = entityManager.createNativeQuery(nativesql);
					if (userDetails != null) {
						nativequery.setParameter("userId", userDetails.getUserId());
					} else {
						nativequery.setParameter("userId", new AppUser().getUserId());
					}

					listId = nativequery.getResultList();
				}
			} else {
				if (role.equals("ROLE_DIRECTOR")) {
					String nativesql = "select ss.id from investigation.summon_status ss,investigation.inspector ins where ss.case_details_id=ins.case_details_id and ss.is_signed=1  and ss.is_physicallysent=false";
					Query nativequery = entityManager.createNativeQuery(nativesql);

					listId = nativequery.getResultList();
				} else {
					String nativesql = "select ss.id from investigation.summon_status ss,investigation.inspector ins where ss.case_details_id=ins.case_details_id and ins.app_user_user_id=:userId and ss.is_signed=1  and ins.is_active = true and ss.is_physicallysent=false";
					Query nativequery = entityManager.createNativeQuery(nativesql);
					if (userDetails != null) {
						nativequery.setParameter("userId", userDetails.getUserId());
					} else {
						nativequery.setParameter("userId", new AppUser().getUserId());
					}

					listId = nativequery.getResultList();
				}
			}
			if (listId.isEmpty())
				return new ArrayList<SummonStatus>();

			/*
			 * SummonStatus ss = new SummonStatus(); ss.setIds(listId); Page<SummonStatus>
			 * listSummon1 = summonRepo.findAllByIdsIn(pageable, ss.getIds());
			 */

			// Page<SummonStatus> listSummon1 = summonRepo.findAllByIdIn(pageable,
			// StringUtils.join(listId, ','));
			if (listId.isEmpty())
				return new ArrayList<SummonStatus>();

			String sql2 = "Select e from " + SummonStatus.class.getName() + " e " //
					+ " Where e.id in (" + StringUtils.join(listId, ',') + ") order by e.id desc";

			Query query2 = entityManager.createQuery(sql2, SummonStatus.class);
//	            query.setParameter("listId", StringUtils.join(listId, ','));
			@SuppressWarnings("unchecked")
			List<SummonStatus> listSummon = query2.getResultList();
			if (!listSummon.isEmpty())
				return listSummon;
			else
				return null;

		} catch (NoResultException e) {
			return null;
		}
	}

	public List<NoticeStatus> findNotice_signeds(AppUser userDetails, SummonReportDTO noticeStatusmodel) {
		try {

			String role = roleDAO.getRoleName(userDetails.getUserId());
			List listId = null;
			if (noticeStatusmodel.getIsPhysically_signed() == null) {
				if (role.equals("ROLE_DIRECTOR")) {
					String nativesql = "select ss.id from investigation.notice_status ss,investigation.inspector ins where ss.case_details_id=ins.case_details_id and ss.is_signed=1 and ss.aprroval_stage2=true and ss.aprroval_stage1=true";
					Query nativequery = entityManager.createNativeQuery(nativesql);

					listId = nativequery.getResultList();
				} else {
					String nativesql = "select ss.id from investigation.notice_status ss,investigation.inspector ins where ss.case_details_id=ins.case_details_id and ins.app_user_user_id=:userId and ss.is_signed=1   and ins.is_active = true";
					Query nativequery = entityManager.createNativeQuery(nativesql);
					if (userDetails != null) {
						nativequery.setParameter("userId", userDetails.getUserId());
					} else {
						nativequery.setParameter("userId", new AppUser().getUserId());
					}
					listId = nativequery.getResultList();
				}
			}

			else if (noticeStatusmodel.getIsPhysically_signed().equalsIgnoreCase("true")) {
				if (role.equals("ROLE_DIRECTOR")) {
					String nativesql = "select ss.id from investigation.notice_status ss,investigation.inspector ins where ss.case_details_id=ins.case_details_id and ss.is_signed=1 and ss.aprroval_stage2=true and ss.aprroval_stage1=true and ss.is_physicallysent=true";
					Query nativequery = entityManager.createNativeQuery(nativesql);

					listId = nativequery.getResultList();
				} else {
					String nativesql = "select ss.id from investigation.notice_status ss,investigation.inspector ins where ss.case_details_id=ins.case_details_id and ins.app_user_user_id=:userId and ss.is_signed=1   and ins.is_active = true and ss.is_physicallysent=true";
					Query nativequery = entityManager.createNativeQuery(nativesql);
					if (userDetails != null) {
						nativequery.setParameter("userId", userDetails.getUserId());
					} else {
						nativequery.setParameter("userId", new AppUser().getUserId());
					}
					listId = nativequery.getResultList();
				}
			} else {
				if (role.equals("ROLE_DIRECTOR")) {
					String nativesql = "select ss.id from investigation.notice_status ss,investigation.inspector ins where ss.case_details_id=ins.case_details_id and ss.is_signed=1 and ss.aprroval_stage2=true and ss.aprroval_stage1=true and ss.is_physicallysent=false";
					Query nativequery = entityManager.createNativeQuery(nativesql);

					listId = nativequery.getResultList();
				} else {
					String nativesql = "select ss.id from investigation.notice_status ss,investigation.inspector ins where ss.case_details_id=ins.case_details_id and ins.app_user_user_id=:userId and ss.is_signed=1   and ins.is_active = true and ss.is_physicallysent=false";
					Query nativequery = entityManager.createNativeQuery(nativesql);
					if (userDetails != null) {
						nativequery.setParameter("userId", userDetails.getUserId());
					} else {
						nativequery.setParameter("userId", new AppUser().getUserId());
					}
					listId = nativequery.getResultList();
				}
			}
			if (listId.isEmpty())
				return new ArrayList<NoticeStatus>();

			String sql1 = "Select e from " + NoticeStatus.class.getName() + " e " //
					+ " Where e.id in (" + StringUtils.join(listId, ',') + ") order by e.id desc";

			Query query1 = entityManager.createQuery(sql1, NoticeStatus.class);
//		            query.setParameter("listId", StringUtils.join(listId, ','));
			List<NoticeStatus> listSummon = query1.getResultList();
			if (!listSummon.isEmpty())
				return listSummon;
			else
				return null;

		} catch (NoResultException e) {
			return null;
		}
	}

	public int findBySummonTypeId(int id) {
		int companyId;
		try {
			String sql = "SELECT   e.entity_summontype from investigation.summon_type e  where id=:id";
			Query query = entityManager.createNativeQuery(sql);
			query.setParameter("id", id);
			List<Integer> listSummon = query.getResultList();
			return listSummon.get(0);
		} catch (NoResultException e) {
			return 0;
		}
	}

	public void updateCompany(int compId, int summonId) {
		Query query = entityManager.createNativeQuery(
				"update  investigation.Summon_Type  set  entity_summontype =:compId where id=:summonId");
		query.setParameter("compId", compId);
		query.setParameter("summonId", summonId);
		query.executeUpdate();

	}

	public InitiateSummonDraft findSummonDraftByCaseDetails1(CaseDetails caseDetails, SummonType summonType) {
		try {
			String sql = "Select i from " + InitiateSummonDraft.class.getName() + " i " //
					+ " Where i.isFinal =true and i.caseDetails=:caseDetails and i.summonType=:summonType ";
			Query query = entityManager.createQuery(sql, InitiateSummonDraft.class);
			if (caseDetails != null) {
				query.setParameter("caseDetails", caseDetails);
			} else {
				query.setParameter("caseDetails", new CaseDetails());
			}
			if (summonType != null) {
				query.setParameter("summonType", summonType);
			} else {
				query.setParameter("summonType", new SummonType());
			}

			List<InitiateSummonDraft> summonlist = query.getResultList();
			
			
			
			if (!summonlist.isEmpty())
				return summonlist.get(summonlist.size() - 1);
			else
				return null;

			// return (SummonDetails) query.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}

	}

}
