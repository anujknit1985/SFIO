package com.snms.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.snms.entity.Company;
import com.snms.entity.Inspector;
import com.snms.entity.inspectorHistory;


@Repository
@Transactional
public class InspectorDao {

	@Autowired
    private EntityManager entityManager;

	

	public Inspector findAllByCaseIdAndUserId(int caseId, int userid) {
		 try {
	            String sql = "Select e from " + Inspector.class.getName() + " e " //
	                    + " Where case_details_id = :caseId and app_user_user_id =:userId";
	 
	            Query query = entityManager.createQuery(sql, Inspector.class);
	            query.setParameter("caseId", caseId);
	            query.setParameter("userId", userid);
	            List<Inspector> Inpectorlist=query.getResultList();
	            if(!Inpectorlist.isEmpty())
	            	return Inpectorlist.get(0);
	            else
	            	return null;
	            
	            //return (SummonDetails) query.getSingleResult();
	        } catch (NoResultException e) {
	            return null;
	        }
	}
	
	
	public inspectorHistory findAllByInspHisCaseIdAndUserId(int caseId, int userid) {
		 try {
	            String sql = "Select e from " + inspectorHistory.class.getName() + " e " //
	                    + " Where case_details_id = :caseId and app_user_user_id =:userId ";
	 
	            Query query = entityManager.createQuery(sql, inspectorHistory.class);
	            query.setParameter("caseId", caseId);
	            query.setParameter("userId", userid);
	            @SuppressWarnings("unchecked")
				List<inspectorHistory> Inpectorlist=query.getResultList();
			
	            if( Inpectorlist.size()!=0 )
	            	return Inpectorlist.get(0);
	            else
	            	return null;
	            
	            //return (SummonDetails) query.getSingleResult();
	        } catch (NoResultException e) {
	            return null;
	        }
	}
	
	
	public Company findComapnyNameByCaseIdAndUserId(int caseId, String compidName) {
		 try {
	            String sql = "Select e from " + Company.class.getName() + " e " //
	                    + " Where  name =:name and case_details_id ="+caseId;
	 
	            Query query = entityManager.createQuery(sql, Company.class);
	           
	            query.setParameter("name", compidName);
	            List<Company> Inpectorlist=query.getResultList();
	            if(!Inpectorlist.isEmpty())
	            	return Inpectorlist.get(0);
	            else
	            	return null;
	            
	            //return (SummonDetails) query.getSingleResult();
	        } catch (NoResultException e) {
	            return null;
	        }
	}

}
