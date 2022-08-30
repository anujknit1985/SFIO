package com.snms.dao;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.transaction.Transactional;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.thymeleaf.util.StringUtils;

import com.snms.dto.gamsPersonDto;
import com.snms.entity.AppUser;
import com.snms.entity.PersonDetails;
import com.snms.entity.RelationpersonCompany;
import com.snms.entity.SummonStatus;
import com.snms.entity.UserDetails;
import com.snms.entity.personcompanyApproval;
import com.snms.service.UserDetailsRepository;
import com.snms.service.UserDetailsServiceImpl;

import java.util.ArrayList;
import java.util.List;

@Repository
@Transactional
public class gamsDao {

	private static final Logger logger = Logger.getLogger(gamsDao.class);
	@Autowired
	private EntityManager entityManager;
	@Autowired
	private UserDetailsRepository userDetailsRepo;
	@Autowired
	private UserDetailsServiceImpl userDetailsService;

	public List<Object[]> ApprovedpersonDetails() {
		try {

			String sql = "select temp.*,stat.*,case when temp.status_investigation_status_id in (3,2) then 'Adverse' else 'Hold' end from \r\n"
					+ "(select distinct  CONCAT(pd.full_name) AS full_name ,max(pa.status_investigation_status_id) as status_investigation_status_id  , (pd.pan_number),(pd.person_id),(pd.passport_number),(pd.is_gep_updated),(pd.gepupdated_date),(pd.is_dummy)\r\n"
					+ "		  from gams.personcompany_approval pa inner join  gams.relation_person_company pc on  pa.id = pc.rpc_id and pa.approved_status=2 and pa.is_approved_stage2=true			  \r\n"
					+ "		   and pc.status_investigation_status_id in (1,2,3) inner join  gams.person_details pd	on pd.person_id = pc.person_id\r\n"

					+ "		  group by pd.full_name, pd.pan_number ,pd.person_id ,pd.passport_number\r\n"
					+ " ) temp \r\n"
					+ "inner join gams.investigation_status stat on stat.investigation_status_id=temp.status_investigation_status_id ";

			Query query = entityManager.createNativeQuery(sql);

			return (List<Object[]>) query.getResultList();
		}

		catch (NoResultException e) {
			return null;
		}
	}

	public List<Object[]> ApprovedpersonDetailsBY(String passNum, String panNumber) {

		String sql = "";
		if (!panNumber.equalsIgnoreCase("") && !passNum.equalsIgnoreCase("")) {
			try {

				sql = "select temp.*,stat.*,case when temp.status_investigation_status_id in (3,2) then 'Adverse' else 'hold' end from \r\n"
						+ "(select distinct  CONCAT(pd.full_name) AS full_name ,max(pa.status_investigation_status_id) as status_investigation_status_id  , (pd.pan_number),(pd.person_id),(pd.passport_number)\r\n"
						+ "		  from gams.personcompany_approval pa inner join  gams.relation_person_company pc on  pa.id = pc.rpc_id and pa.approved_status=2 and pa.is_approved_stage2=true			  \r\n"
						+ "		   and pc.status_investigation_status_id in (1,2,3) inner join  gams.person_details pd	on pd.person_id = pc.rpc_id and pd.pan_number=:panNUmber and pd.passport_number=:passNum\r\n"
						+ "		  group by pd.full_name, pd.pan_number ,pd.person_id ,pd.passport_number\r\n"
						+ " ) temp \r\n"
						+ "inner join gams.investigation_status stat on stat.investigation_status_id=temp.status_investigation_status_id ";

				Query query = entityManager.createNativeQuery(sql);
				query.setParameter("panNUmber", panNumber);
				query.setParameter("passNum", passNum);
				return (List<Object[]>) query.getResultList();
			}

			catch (NoResultException e) {
				return null;
			}
		}

		else if (!panNumber.equalsIgnoreCase("") && passNum.equalsIgnoreCase("")) {
			try {

				sql = "select temp.*,stat.*,case when temp.status_investigation_status_id in (3,2) then 'Adverse' else 'hold' end from \r\n"
						+ "(select distinct  CONCAT(pd.full_name) AS full_name ,max(pa.status_investigation_status_id) as status_investigation_status_id  , (pd.pan_number),(pd.person_id),(pd.passport_number)\r\n"
						+ "		  from gams.personcompany_approval pa inner join  gams.relation_person_company pc on  pa.id = pc.rpc_id and pa.approved_status=2 and pa.is_approved_stage2=true			  \r\n"
						+ "		   and pc.status_investigation_status_id in (1,2,3) inner join  gams.person_details pd	on pd.person_id = pc.rpc_id and pd.pan_number=:panNUmber\r\n"
						+ "		  group by pd.full_name, pd.pan_number ,pd.person_id ,pd.passport_number\r\n"
						+ " ) temp \r\n"
						+ "inner join gams.investigation_status stat on stat.investigation_status_id=temp.status_investigation_status_id ";

				Query query = entityManager.createNativeQuery(sql);
				query.setParameter("panNUmber", panNumber);
				return (List<Object[]>) query.getResultList();
			}

			catch (NoResultException e) {
				return null;
			}
		} else {
			try {

				sql = "select temp.*,stat.*,case when temp.status_investigation_status_id in (3,2) then 'Adverse' else 'hold' end from \r\n"
						+ "(select distinct  CONCAT(pd.full_name) AS full_name ,max(pa.status_investigation_status_id) as status_investigation_status_id  , (pd.pan_number),(pd.person_id),(pd.passport_number)\r\n"
						+ "		  from gams.personcompany_approval pa inner join  gams.relation_person_company pc on  pa.id = pc.rpc_id and pa.approved_status=2 and pa.is_approved_stage2=true			  \r\n"
						+ "		   and pc.status_investigation_status_id in (1,2,3) inner join  gams.person_details pd	on pd.person_id = pc.rpc_id and pd.passport_number=:passNum\r\n"
						+ "		  group by pd.full_name, pd.pan_number ,pd.person_id ,pd.passport_number\r\n"
						+ " ) temp \r\n"
						+ "inner join gams.investigation_status stat on stat.investigation_status_id=temp.status_investigation_status_id ";

				Query query = entityManager.createNativeQuery(sql);

				query.setParameter("passNum", passNum);
				return (List<Object[]>) query.getResultList();
			}

			catch (NoResultException e) {
				return null;
			}
		}

	}

	public personcompanyApproval findAllByRpc(RelationpersonCompany rpc) {
		try {

			String sql = "Select e from " + personcompanyApproval.class.getName() + " e " //
					+ " Where e.rpc = :rpc";

			Query query = entityManager.createQuery(sql, personcompanyApproval.class);

			query.setParameter("rpc", rpc);
			List<personcompanyApproval> pcaprov = query.getResultList();

			return pcaprov.get(0);

			// return (SummonDetails) query.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}

	}

	public List<Object[]> findAllByCaseStage(int case_stage, Sort by) {
		try {

			// String sql = "select e.id ,e.case_id, e.case_title from
			// investigation.case_details as e "+ //
			// " Where e.case_stage = :case_stage";

			String sql = "select cd.id ,cd.case_Id , cd.case_title  , c.name,o.aprroval_stage2  from investigation.case_details cd inner join investigation.company c\r\n"
					+ "on cd.id = c.case_details_id inner join investigation.office_order o\r\n"
					+ " on o.case_details_id=cd.id\r\n"
					+ "  where o.aprroval_stage2=true and c.is_active =true and cd.case_stage=:case_stage \r\n"
					+ "  order by case_title, c.name";
			Query query = entityManager.createNativeQuery(sql);
			query.setParameter("case_stage", case_stage);
			return (List<Object[]>) query.getResultList();

			// return (SummonDetails) query.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}

	}

	public List<Object[]> personListUnderCompAndCase(long caseId, String compName) {
		List company_Summon;
		String sql = "select cs.id  from investigation.company_summon  cs  , investigation.summon_details sd where  sd.case_id =:case_id  and sd.id = cs.entity_company_summon and cs.company_name=:company_name ";

		Query query = this.entityManager.createNativeQuery(sql);
		query.setParameter("case_id", caseId);
		query.setParameter("company_name", compName);

		company_Summon = query.getResultList();
		if (company_Summon.isEmpty())
			return new ArrayList<Object[]>();

		try {

			String sqlquerry = "select pd.full_name ,pd.gender ,pd.relation_name ,pd.address , pd.dob,pd.email, pd.pan_number ,pd.passport_number,pd.primary_mobile, d.designation  ,s.investigation_status"
					+ " from gams.relation_person_company pc ,gams.person_details pd  ,authentication.designation d  , gams.investigation_status s where pc.company_summon_id in ("
					+ StringUtils.join(company_Summon, ',')
					+ ") and pc.person_id = pd.person_id   and d.id=pc.designation_id and s.investigation_status_id = pc.status_investigation_status_id  ;\r\n";
			Query query1 = entityManager.createNativeQuery(sqlquerry);

			return (List<Object[]>) query1.getResultList();

			// return (SummonDetails) query.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}

	}

	public List<Object[]> findAllByCaseCUI() {
		try {

			// String sql = "select e.id ,e.case_id, e.case_title from
			// investigation.case_details as e "+ //
			// " Where e.case_stage = :case_stage";

			String sql = "select cd.id ,cd.case_Id , cd.case_title  , c.name,o.aprroval_stage2  from investigation.case_details cd inner join investigation.company c\r\n"
					+ "on cd.id = c.case_details_id inner join investigation.office_order o\r\n"
					+ " on o.case_details_id=cd.id\r\n" + "  where o.aprroval_stage2=true and c.is_active =true  \r\n"
					+ "  order by case_title, c.name";
			Query query = entityManager.createNativeQuery(sql);

			return (List<Object[]>) query.getResultList();

			// return (SummonDetails) query.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}

	}

	public PersonDetails findAllByEmail(String email) {
		try {

			String sql = "Select e from " + PersonDetails.class.getName() + " e " //
					+ " Where e.email = :email";

			Query query = entityManager.createQuery(sql, PersonDetails.class);

			query.setParameter("email", email);
			return (PersonDetails) query.getSingleResult();

			// return (SummonDetails) query.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}

	}

	public List<personcompanyApproval> personApproveCount(Long unit_unit_id) {
		List listId = null;

		List<gamsPersonDto> stdList = new ArrayList<gamsPersonDto>();
		try {

			String nativesql1 = "SELECT  ud.id FROM authentication.user_details ud where ud.unit_unit_id=:unit_unit_id ";

			// String nativesql1 = "SELECT CONCAT(ud.first_name ,' ',ud.middle_name, ' ',
			// last_name) AS full_name FROM authentication.user_details ud where
			// ud.unit_unit_id=:unit_unit_id ";
			Query nativequery1 = entityManager.createNativeQuery(nativesql1);
			nativequery1.setParameter("unit_unit_id", unit_unit_id);
			List listuseridId = nativequery1.getResultList();
			// List<UserDetails> listuseridId =nativequery1.getResultList();
			int count = 1;

			/*
			 * for ( List listuserid :listuseridId ) {
			 * 
			 * stdList.add(new gamsPersonDto(userDetailsService.getFullName(user)));
			 * count++; }
			 */
			String nativesql = null;
			if (listuseridId.isEmpty() || listuseridId == null)
				return new ArrayList<personcompanyApproval>();

			String sql = "Select e from " + personcompanyApproval.class.getName() + " e " //
					+ " Where e.createdBy in (" + StringUtils.join(listuseridId, ',')
					+ ") and  e.approved_status=2 and e.isApproved_stage2=true	";

			Query query = entityManager.createQuery(sql, personcompanyApproval.class);
			List<personcompanyApproval> listSummon = query.getResultList();
			
		
			if (!listSummon.isEmpty())
				return listSummon;
			else
				return new ArrayList<personcompanyApproval>();

		} catch (NoResultException e) {
			return new ArrayList<personcompanyApproval>();
		}
	}

	public List<personcompanyApproval> personCreatedCount(Long unit_unit_id) {
		List listId = null;

		List<gamsPersonDto> stdList = new ArrayList<gamsPersonDto>();
		try {

			String nativesql1 = "SELECT  ud.id FROM authentication.user_details ud where ud.unit_unit_id=:unit_unit_id ";

			// String nativesql1 = "SELECT CONCAT(ud.first_name ,' ',ud.middle_name, ' ',
			// last_name) AS full_name FROM authentication.user_details ud where
			// ud.unit_unit_id=:unit_unit_id ";
			Query nativequery1 = entityManager.createNativeQuery(nativesql1);
			nativequery1.setParameter("unit_unit_id", unit_unit_id);
			List listuseridId = nativequery1.getResultList();
			// List<UserDetails> listuseridId =nativequery1.getResultList();
			int count = 1;

			/*
			 * for ( List listuserid :listuseridId ) {
			 * 
			 * stdList.add(new gamsPersonDto(userDetailsService.getFullName(user)));
			 * count++; }
			 */
			String nativesql = null;
			if (listuseridId.isEmpty() || listuseridId == null)
				return new ArrayList<personcompanyApproval>();

			String sql = "Select e from " + personcompanyApproval.class.getName() + " e " //
					+ " Where e.createdBy in (" + StringUtils.join(listuseridId, ',') + ")";

			Query query = entityManager.createQuery(sql, personcompanyApproval.class);
			List<personcompanyApproval> listSummon = query.getResultList();
			
			
			int personCount = 0;
			
			List<personcompanyApproval> listPerson1 = new ArrayList<personcompanyApproval>();
			for(personcompanyApproval listPerson : listSummon) {
				if(listSummon.get(personCount).getApproved_status()==1 && listSummon.get(personCount).getIsApproved_stage2()==true) {
					//listSummon.remove(listPerson.getId()-1);
					
				}else {
					listPerson1.add(new personcompanyApproval(listPerson.getId()));
				}
				personCount++;
			}
		
			if (!listPerson1.isEmpty())
				return listPerson1;
			else
				return new ArrayList<personcompanyApproval>();

		} catch (NoResultException e) {
			return new ArrayList<personcompanyApproval>();
		}
	}

	
	
	
	public List<RelationpersonCompany> TotalpersonCount(Long unit_unit_id) {
		List listId = null;

		List<gamsPersonDto> stdList = new ArrayList<gamsPersonDto>();
		try {

			String nativesql1 = "SELECT  ud.id FROM authentication.user_details ud where ud.unit_unit_id=:unit_unit_id ";

			// String nativesql1 = "SELECT CONCAT(ud.first_name ,' ',ud.middle_name, ' ',
			// last_name) AS full_name FROM authentication.user_details ud where
			// ud.unit_unit_id=:unit_unit_id ";
			Query nativequery1 = entityManager.createNativeQuery(nativesql1);
			nativequery1.setParameter("unit_unit_id", unit_unit_id);
			List listuseridId = nativequery1.getResultList();
			// List<UserDetails> listuseridId =nativequery1.getResultList();
			int count = 1;

			/*
			 * for ( List listuserid :listuseridId ) {
			 * 
			 * stdList.add(new gamsPersonDto(userDetailsService.getFullName(user)));
			 * count++; }
			 */
			String nativesql = null;
			if (listuseridId.isEmpty() || listuseridId == null)
				return new ArrayList<RelationpersonCompany>();

				String sql = "select  id from gams.personcompany_approval as e where e.created_by_id in ("+StringUtils.join(listuseridId, ",")+")";
				
				Query query = entityManager.createNativeQuery(sql);
				
				List listrpcidId = query.getResultList();
				
				if (listrpcidId.isEmpty() || listrpcidId == null)
					return new ArrayList<RelationpersonCompany>();
				
			
			  String sql1 = "Select e from " + RelationpersonCompany.class.getName() + " e "
			   + " Where e.id in (" + StringUtils.join(listrpcidId, ',') + ") ";
			 

			Query query1 = entityManager.createQuery(sql1, RelationpersonCompany.class);
		    List<RelationpersonCompany> listSummon = query.getResultList();
			if (!listSummon.isEmpty())
				return listSummon;
			else
				return new ArrayList<RelationpersonCompany>();

		} catch (NoResultException e) {
			return new ArrayList<RelationpersonCompany>();
		}
	}

	
	public List<personcompanyApproval> personApproveCountByDate(Long unit_unit_id, String startDate, String endDate) {
		List listId = null;

		List<gamsPersonDto> stdList = new ArrayList<gamsPersonDto>();
		try {

			String nativesql1 = "SELECT  ud.id FROM authentication.user_details ud where ud.unit_unit_id=:unit_unit_id ";

			// String nativesql1 = "SELECT CONCAT(ud.first_name ,' ',ud.middle_name, ' ',
			// last_name) AS full_name FROM authentication.user_details ud where
			// ud.unit_unit_id=:unit_unit_id ";
			Query nativequery1 = entityManager.createNativeQuery(nativesql1);
			nativequery1.setParameter("unit_unit_id", unit_unit_id);
			List listuseridId = nativequery1.getResultList();
			// List<UserDetails> listuseridId =nativequery1.getResultList();
			int count = 1;

			/*
			 * for ( List listuserid :listuseridId ) {
			 * 
			 * stdList.add(new gamsPersonDto(userDetailsService.getFullName(user)));
			 * count++; }
			 */
			String nativesql = null;
			if (listuseridId.isEmpty() || listuseridId == null)
				return new ArrayList<personcompanyApproval>();

			String sql = "Select e from " + personcompanyApproval.class.getName() + " e " //
					+ " Where e.createdBy in (" + StringUtils.join(listuseridId, ',')
					+ ") and  e.approved_status=2 and e.isApproved_stage2=true  and date(e.approvedDate) BETWEEN ' "
					+ startDate + "' AND '" + endDate + "'";
			Query query = entityManager.createQuery(sql, personcompanyApproval.class);
			List<personcompanyApproval> listSummon = query.getResultList();
			if (!listSummon.isEmpty())
				return listSummon;
			else
				return new ArrayList<personcompanyApproval>();

		} catch (NoResultException e) {
			return new ArrayList<personcompanyApproval>();
		}
	}

	public List<personcompanyApproval> personCreatedCountByDate(Long unitId, String startDate, String endDate) {
		List listId = null;

		List<gamsPersonDto> stdList = new ArrayList<gamsPersonDto>();
		try {

			String nativesql1 = "SELECT  ud.id FROM authentication.user_details ud where ud.unit_unit_id=:unit_unit_id ";

			// String nativesql1 = "SELECT CONCAT(ud.first_name ,' ',ud.middle_name, ' ',
			// last_name) AS full_name FROM authentication.user_details ud where
			// ud.unit_unit_id=:unit_unit_id ";
			Query nativequery1 = entityManager.createNativeQuery(nativesql1);
			nativequery1.setParameter("unit_unit_id", unitId);
			List listuseridId = nativequery1.getResultList();
			// List<UserDetails> listuseridId =nativequery1.getResultList();
			int count = 1;

			/*
			 * for ( List listuserid :listuseridId ) {
			 * 
			 * stdList.add(new gamsPersonDto(userDetailsService.getFullName(user)));
			 * count++; }
			 */
			String nativesql = null;
			if (listuseridId.isEmpty() || listuseridId == null)
				return new ArrayList<personcompanyApproval>();

			String sql = "Select e from " + personcompanyApproval.class.getName() + " e " //
					+ " Where e.createdBy in (" + StringUtils.join(listuseridId, ',')
					+ ") and  e.approved_status=2 and e.isApproved_stage2=true  and date(e.createdDate) BETWEEN ' "
					+ startDate + "' AND '" + endDate + "'";
			Query query = entityManager.createQuery(sql, personcompanyApproval.class);
			List<personcompanyApproval> listSummon = query.getResultList();
			if (!listSummon.isEmpty())
				return listSummon;
			else
				return new ArrayList<personcompanyApproval>();

		} catch (NoResultException e) {
			return new ArrayList<personcompanyApproval>();
		}
	}

	public List<personcompanyApproval> personPendingCount(Long unit_unit_id) {
		List listId = null;

		List<gamsPersonDto> stdList = new ArrayList<gamsPersonDto>();
		try {

			String nativesql1 = "SELECT  ud.id FROM authentication.user_details ud where ud.unit_unit_id=:unit_unit_id ";

			// String nativesql1 = "SELECT CONCAT(ud.first_name ,' ',ud.middle_name, ' ',
			// last_name) AS full_name FROM authentication.user_details ud where
			// ud.unit_unit_id=:unit_unit_id ";
			Query nativequery1 = entityManager.createNativeQuery(nativesql1);
			nativequery1.setParameter("unit_unit_id", unit_unit_id);
			List listuseridId = nativequery1.getResultList();
			// List<UserDetails> listuseridId =nativequery1.getResultList();
			int count = 1;

			/*
			 * for ( List listuserid :listuseridId ) {
			 * 
			 * stdList.add(new gamsPersonDto(userDetailsService.getFullName(user)));
			 * count++; }
			 */
			String nativesql = null;
			if (listuseridId.isEmpty() || listuseridId == null)
				return new ArrayList<personcompanyApproval>();

			String sql = "Select e from " + personcompanyApproval.class.getName() + " e " //
					+ " Where e.createdBy in (" + StringUtils.join(listuseridId, ',')
					+ ") and  e.approved_status=1 and e.isApproved_stage2=false and e.rejected=false";

			Query query = entityManager.createQuery(sql, personcompanyApproval.class);
			List<personcompanyApproval> listSummon = query.getResultList();
			if (!listSummon.isEmpty())
				return listSummon;
			else
				return new ArrayList<personcompanyApproval>();

		} catch (NoResultException e) {
			return new ArrayList<personcompanyApproval>();
		}
	}

	public List<personcompanyApproval> personReviewCount(Long unit_unit_id) {
		List listId = null;

		List<gamsPersonDto> stdList = new ArrayList<gamsPersonDto>();
		try {

			String nativesql1 = "SELECT  ud.id FROM authentication.user_details ud where ud.unit_unit_id=:unit_unit_id ";

			// String nativesql1 = "SELECT CONCAT(ud.first_name ,' ',ud.middle_name, ' ',
			// last_name) AS full_name FROM authentication.user_details ud where
			// ud.unit_unit_id=:unit_unit_id ";
			Query nativequery1 = entityManager.createNativeQuery(nativesql1);
			nativequery1.setParameter("unit_unit_id", unit_unit_id);
			List listuseridId = nativequery1.getResultList();
			// List<UserDetails> listuseridId =nativequery1.getResultList();
			int count = 1;

			/*
			 * for ( List listuserid :listuseridId ) {
			 * 
			 * stdList.add(new gamsPersonDto(userDetailsService.getFullName(user)));
			 * count++; }
			 */
			String nativesql = null;
			if (listuseridId.isEmpty() || listuseridId == null)
				return new ArrayList<personcompanyApproval>();

			String sql = "Select e from " + personcompanyApproval.class.getName() + " e " //
					+ " Where e.createdBy in (" + StringUtils.join(listuseridId, ',')
					+ ") and  e.approved_status=1 and e.isApproved_stage2=false and e.rejected=true";

			Query query = entityManager.createQuery(sql, personcompanyApproval.class);
			List<personcompanyApproval> listSummon = query.getResultList();
			if (!listSummon.isEmpty())
				return listSummon;
			else
				return new ArrayList<personcompanyApproval>();

		} catch (NoResultException e) {
			return new ArrayList<personcompanyApproval>();
		}
	}

	public List<personcompanyApproval> personPendingCountByDate(Long unit_unit_id, String startDate, String endDate) {
		List listId = null;

		List<gamsPersonDto> stdList = new ArrayList<gamsPersonDto>();
		try {

			String nativesql1 = "SELECT  ud.id FROM authentication.user_details ud where ud.unit_unit_id=:unit_unit_id ";

			// String nativesql1 = "SELECT CONCAT(ud.first_name ,' ',ud.middle_name, ' ',
			// last_name) AS full_name FROM authentication.user_details ud where
			// ud.unit_unit_id=:unit_unit_id ";
			Query nativequery1 = entityManager.createNativeQuery(nativesql1);
			nativequery1.setParameter("unit_unit_id", unit_unit_id);
			List listuseridId = nativequery1.getResultList();
			// List<UserDetails> listuseridId =nativequery1.getResultList();
			int count = 1;

			/*
			 * for ( List listuserid :listuseridId ) {
			 * 
			 * stdList.add(new gamsPersonDto(userDetailsService.getFullName(user)));
			 * count++; }
			 */
			String nativesql = null;
			if (listuseridId.isEmpty() || listuseridId == null)
				return new ArrayList<personcompanyApproval>();

			String sql = "Select e from " + personcompanyApproval.class.getName() + " e " //
					+ " Where e.createdBy in (" + StringUtils.join(listuseridId, ',')
					+ ") and e.approved_status=1 and e.isApproved_stage2=false and e.rejected=false and date(e.approvedDate) BETWEEN ' "
					+ startDate + "' AND '" + endDate + "'";
			Query query = entityManager.createQuery(sql, personcompanyApproval.class);
			List<personcompanyApproval> listSummon = query.getResultList();
			if (!listSummon.isEmpty())
				return listSummon;
			else
				return new ArrayList<personcompanyApproval>();

		} catch (NoResultException e) {
			return new ArrayList<personcompanyApproval>();
		}

	}

	public List<personcompanyApproval> personReviewCountByDate(Long unit_unit_id, String startDate, String endDate) {
		List listId = null;

		List<gamsPersonDto> stdList = new ArrayList<gamsPersonDto>();
		try {

			String nativesql1 = "SELECT  ud.id FROM authentication.user_details ud where ud.unit_unit_id=:unit_unit_id ";
			Query nativequery1 = entityManager.createNativeQuery(nativesql1);
			nativequery1.setParameter("unit_unit_id", unit_unit_id);
			List listuseridId = nativequery1.getResultList();
			int count = 1;

			String nativesql = null;
			if (listuseridId.isEmpty() || listuseridId == null)
				return new ArrayList<personcompanyApproval>();

			String sql = "Select e from " + personcompanyApproval.class.getName() + " e " //
					+ " Where e.createdBy in (" + StringUtils.join(listuseridId, ',')
					+ ") and e.approved_status=1 and e.isApproved_stage2=false and e.rejected=true  and date(e.approvedDate) BETWEEN ' "
					+ startDate + "' AND '" + endDate + "'";
			Query query = entityManager.createQuery(sql, personcompanyApproval.class);
			List<personcompanyApproval> listSummon = query.getResultList();
			if (!listSummon.isEmpty())
				return listSummon;
			else
				return new ArrayList<personcompanyApproval>();

		} catch (NoResultException e) {
			return new ArrayList<personcompanyApproval>();
		}

	}

	public List<personcompanyApproval> personRejectPersonCount(Long unit_unit_id) {
		List listId = null;

		List<gamsPersonDto> stdList = new ArrayList<gamsPersonDto>();
		try {

			String nativesql1 = "SELECT  ud.id FROM authentication.user_details ud where ud.unit_unit_id=:unit_unit_id ";

			// String nativesql1 = "SELECT CONCAT(ud.first_name ,' ',ud.middle_name, ' ',
			// last_name) AS full_name FROM authentication.user_details ud where
			// ud.unit_unit_id=:unit_unit_id ";
			Query nativequery1 = entityManager.createNativeQuery(nativesql1);
			nativequery1.setParameter("unit_unit_id", unit_unit_id);
			List listuseridId = nativequery1.getResultList();
			// List<UserDetails> listuseridId =nativequery1.getResultList();
			int count = 1;

			/*
			 * for ( List listuserid :listuseridId ) {
			 * 
			 * stdList.add(new gamsPersonDto(userDetailsService.getFullName(user)));
			 * count++; }
			 */
			String nativesql = null;
			if (listuseridId.isEmpty() || listuseridId == null)
				return new ArrayList<personcompanyApproval>();

			String sql = "Select e from " + personcompanyApproval.class.getName() + " e " //
					+ " Where e.createdBy in (" + StringUtils.join(listuseridId, ',')
					+ ") and  e.approved_status=1 and e.isApproved_stage2=false and e.isDeleted=true";

			Query query = entityManager.createQuery(sql, personcompanyApproval.class);
			List<personcompanyApproval> listSummon = query.getResultList();
			if (!listSummon.isEmpty())
				return listSummon;
			else
				return new ArrayList<personcompanyApproval>();

		} catch (NoResultException e) {
			return new ArrayList<personcompanyApproval>();
		}
	}

	public List<personcompanyApproval> personRejectPersonCount(Long unit_unit_id, String startDate, String endDate) {
		List listId = null;

		List<gamsPersonDto> stdList = new ArrayList<gamsPersonDto>();
		try {

			String nativesql1 = "SELECT  ud.id FROM authentication.user_details ud where ud.unit_unit_id=:unit_unit_id ";
			Query nativequery1 = entityManager.createNativeQuery(nativesql1);
			nativequery1.setParameter("unit_unit_id", unit_unit_id);
			List listuseridId = nativequery1.getResultList();
			int count = 1;

			String nativesql = null;
			if (listuseridId.isEmpty() || listuseridId == null)
				return new ArrayList<personcompanyApproval>();

			String sql = "Select e from " + personcompanyApproval.class.getName() + " e " //
					+ " Where e.createdBy in (" + StringUtils.join(listuseridId, ',')
					+ ") and e.approved_status=1 and e.isApproved_stage2=false and e.isDeleted=true  and date(e.approvedDate) BETWEEN ' "
					+ startDate + "' AND '" + endDate + "'";
			Query query = entityManager.createQuery(sql, personcompanyApproval.class);
			List<personcompanyApproval> listSummon = query.getResultList();
			if (!listSummon.isEmpty())
				return listSummon;
			else
				return new ArrayList<personcompanyApproval>();

		} catch (NoResultException e) {
			return new ArrayList<personcompanyApproval>();
		}

	}
}