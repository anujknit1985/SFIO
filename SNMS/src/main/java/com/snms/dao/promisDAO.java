package com.snms.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.transaction.Transactional;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.thymeleaf.util.StringUtils;

import com.snms.entity.CaseDetails;
import com.snms.entity.InitiatorOfficeOrderDraft;
import com.snms.entity.NoticeStatus;
import com.snms.entity.RelationpersonCompany;
import com.snms.entity.personcompanyApproval;

@Repository
@Transactional
public class promisDAO {
	private static final Logger logger = Logger.getLogger(promisDAO.class);
	@Autowired
	private EntityManager entityManager;
	public CaseDetails findAllByMcaOrderNo(String mcaNo) {
		Query query=null;
		String sql = "Select e from " + CaseDetails.class.getName() + " e " //
				+ " Where  e.mcaOrderNo=:mcaOrderNo";
		
		 query= entityManager.createQuery(sql, CaseDetails.class);
        query.setParameter("mcaOrderNo", mcaNo);
        List<CaseDetails> cadtl = query.getResultList();
        if(!cadtl.isEmpty())
        	return cadtl.get(cadtl.size()-1);
        else
        	return null;
	}
	public List<Object[]> findCompListByCaseID(Long invCaseId) {
		Query query=null;
		String sql = "SELECT  cs.company_name , cs.cin,cs.id   from investigation.summon_details  sd  ,  investigation.company_summon  cs  where  sd.case_id =:case_id  and sd.id = cs.entity_company_summon  ";
		
		 query= entityManager.createNativeQuery(sql);
        query.setParameter("case_id", invCaseId);
        List<Object[]> complst = query.getResultList();
        if(!complst.isEmpty())
        	return complst ;
        else
        	return null;
	}
	public List<RelationpersonCompany> findAllByPersonCaseDetails(CaseDetails caseDtl) {
		
		List listpersonId = null;
		
		String nativesql = "Select pc.rpc_id from  gams.relation_person_company pc  where pc.case_details_id =:case_details_id";
		
		Query nativequery = entityManager.createNativeQuery(nativesql);
		nativequery.setParameter("case_details_id", caseDtl.getId());
		listpersonId = nativequery.getResultList();
		if (listpersonId.isEmpty())
			return new ArrayList<RelationpersonCompany>();

		/*
		 * String sql = "Select e  from " + RelationpersonCompany.class.getName() +
		 * " e " // + "Where e.id in (" + StringUtils.join(listpersonId, ',') +
		 * ") order by e.id desc";
		 */
		String sql = "Select e  from " + RelationpersonCompany.class.getName() + " e " //
				+ "Where e.id ="+ 17 +" order by e.id desc";
		Query query= entityManager.createQuery(sql, RelationpersonCompany.class);
        //query.setParameter("caseDetails", caseDtl);
        List<RelationpersonCompany> cadtl = query.getResultList();
       
        if(!cadtl.isEmpty())
        	return cadtl ;
        else
        	return null;
	}
	public List<Object[]> findAllByPersonCaseDetails1(CaseDetails caseDtl) {
		List<Object[]> listpersonId = null;
		
		String sql =  "SELECT  pc.rpc_id , pd.person_id , pd.full_name , pd.pan_number , pd.gender ,pd.email ,pd.primary_mobile ,d.designation ,cs.company_name,cs.cin ,cs.id  from gams.relation_person_company pc , gams.person_details pd  ,authentication.designation d, " + 
				"  investigation.company_summon  cs where pc.case_details_id =:case_details and  pc.person_id=pd.person_id and d.id=pc.designation_id and cs.id=pc.company_summon_id" ;
			
		
		Query nativequery = entityManager.createNativeQuery(sql);
		nativequery.setParameter("case_details", caseDtl);
		listpersonId = nativequery.getResultList();
		return listpersonId;
	}
	public CaseDetails findAllByCaseTitle(String compName) {
		Query query=null;
		String sql = "Select e from " + CaseDetails.class.getName() + " e " //
				+ " Where  e.caseTitle=:caseTitle";
		
		 query= entityManager.createQuery(sql, CaseDetails.class);
        query.setParameter("caseTitle", compName);
        List<CaseDetails> cadtl = query.getResultList();
        if(!cadtl.isEmpty())
        	return cadtl.get(cadtl.size()-1);
        else
        	return null;
	}
}
