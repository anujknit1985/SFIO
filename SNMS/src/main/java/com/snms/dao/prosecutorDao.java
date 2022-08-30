package com.snms.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.snms.entity.RelationpersonCompany;

@Repository
@Transactional
public class prosecutorDao {
	@Autowired
	private EntityManager entityManager;
	
	public List<Object[]> findCases(){
		try {
			//String sql = "select cd.case_id,cd.case_title,cd.id from investigation.inspector i,investigation.case_details cd,investigation.office_order o where cd.id=i.case_details_id and cd.id=o.case_details_id and o.is_signed=1 and i.app_user_user_id=:userid";
			String sql = "SELECT distinct cd.case_id,cd.case_title,cd.id,cd.mca_order_file,cd.court_order_file,cd.radio_value FROM   gams.person_details pd\r\n" + 
					"INNER JOIN  gams.relation_person_company pc\r\n" + 
					"ON pd.person_id = pc.rpc_id \r\n" + 
					"INNER JOIN   gams.personcompany_approval pa\r\n" + 
					"ON pa.id = pc.rpc_id and pa.approved_status=2 and pa.is_approved_stage2=true and pa.status_investigation_status_id=6\r\n" + 
					"INNER JOIN investigation.case_details cd\r\n" + 
					"ON  pc.case_details_id = cd.id";
			Query query = this.entityManager.createNativeQuery(sql);
			
			return (List<Object[]>) query.getResultList();
		} catch (NoResultException e) {
			return null;
		}
	}

	public List<Object[]> findAllCases() {
		try {
			//String sql = "select cd.case_id,cd.case_title,cd.id from investigation.inspector i,investigation.case_details cd,investigation.office_order o where cd.id=i.case_details_id and cd.id=o.case_details_id and o.is_signed=1 and i.app_user_user_id=:userid";
			String sql = "select distinct cd.case_id,cd.case_title,cd.id,cd.mca_order_file,cd.court_order_file,cd.radio_value from \r\n" + 
					"     investigation.case_details cd , investigation.office_order o  where  cd.case_stage=2 and   cd.id=o.case_details_id and o.is_signed=1 ";
			Query query = this.entityManager.createNativeQuery(sql);
			
			return (List<Object[]>) query.getResultList();
		} catch (NoResultException e) {
			return null;
		}
	}

	public List<RelationpersonCompany> findByPersonDetails(String panNumber,  String passportNumber) {
		List listId = null;
		List<RelationpersonCompany> listcompany = null;
		// only panNumber
		if (!panNumber.equalsIgnoreCase("")  && passportNumber.equalsIgnoreCase("")) {

			String nativesql = "select distinct p.person_id from gams.person_details  p where p.pan_number =:pan_number";
			Query nativequery = entityManager.createNativeQuery(nativesql);
			nativequery.setParameter("pan_number", panNumber);
			listId = nativequery.getResultList();
		}
	
		if (!passportNumber.equalsIgnoreCase("") && panNumber.equalsIgnoreCase("") ) {

			String nativesql = "select distinct p.person_id from gams.person_details  p where "
					+ "p.passport_number =:passport_number";
			Query nativequery = entityManager.createNativeQuery(nativesql);
			nativequery.setParameter("passport_number", passportNumber);
			listId = nativequery.getResultList();
		}
// passportNumber and panNumner
		if (!passportNumber.equalsIgnoreCase("") && !panNumber.equalsIgnoreCase("") ) {

			String nativesql = "select distinct p.person_id from gams.person_details  p where "
					+ "p.passport_number =:passport_number or p.pan_number =:pan_number ";
			Query nativequery = entityManager.createNativeQuery(nativesql);
			nativequery.setParameter("passport_number", passportNumber);
			nativequery.setParameter("pan_number", panNumber);

			listId = nativequery.getResultList();
		}


//passportNUmber panNumber 
		if (!passportNumber.equalsIgnoreCase("") && !panNumber.equalsIgnoreCase("") ) {

			String nativesql = "select distinct p.person_id from gams.person_details  p where "
					+ "p.passport_number =:passport_number or p.pan_number =:pan_number";
			Query nativequery = entityManager.createNativeQuery(nativesql);
			nativequery.setParameter("passport_number", passportNumber);
			
			nativequery.setParameter("pan_number", panNumber);

			listId = nativequery.getResultList();
		}

		if ( listId ==null || listId.isEmpty())
			return new ArrayList<RelationpersonCompany>();
         
		try {
			String sql = "Select e from " + RelationpersonCompany.class.getName() + " e " //
					+ " Where e.personDetails in (" + StringUtils.join(listId, ',') + ")";

			Query query = entityManager.createQuery(sql, RelationpersonCompany.class);
			listcompany = query.getResultList();
			if (listcompany.isEmpty())
				return listcompany;
			else
				return listcompany;

		} catch (NoResultException e) {
			return listcompany;
		}
	}
}
