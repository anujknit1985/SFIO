package com.snms.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.transaction.Transactional;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.snms.entity.AppUser;
import com.snms.entity.CaseDetails;
import com.snms.entity.FinancialMaster;
import com.snms.entity.InitiateSummonDraft;
import com.snms.entity.InitiatorOfficeOrderDraft;
import com.snms.entity.OfficeOrder;
import com.snms.entity.UnitDetails;
import com.snms.entity.UserDetails;
import com.snms.utils.NoticePdf;
import com.snms.validation.SNMSValidator;

@Repository("officeOrderDao")
@Transactional
public class OfficeOrderDao {
	 private static final Logger logger = Logger.getLogger(OfficeOrderDao.class);
		
	@Autowired
    private EntityManager entityManager;
	
	SNMSValidator snmsValid ;
	@SuppressWarnings("unchecked")
	public CaseDetails uniqueCaseCheck(String caseTitle,String mcaOrderNo,
			String courtOrderNo,int operationType){
		String sql="";
		Query query=null;
		try {
			if(operationType==1)
			{
				sql= "Select i from " + CaseDetails.class.getName() + " i " //
	                + " Where i.caseTitle=:caseTitle";
				query= entityManager.createQuery(sql, CaseDetails.class);
		        query.setParameter("caseTitle", caseTitle);
			}
			else if(operationType==2)
			{
				sql= "Select i from " + CaseDetails.class.getName() + " i " //
	                + " Where i.mcaOrderNo=:mcaOrderNo";
				query= entityManager.createQuery(sql, CaseDetails.class);
		        query.setParameter("mcaOrderNo", mcaOrderNo);
			}
			if(operationType==3)
			{
				sql= "Select i from " + CaseDetails.class.getName() + " i " //
	                + " Where i.courtOrderNo=:courtOrderNo";
				query= entityManager.createQuery(sql, CaseDetails.class);
		        query.setParameter("courtOrderNo", courtOrderNo);
			}
			if(operationType==4)
			{
				sql= "Select i from " + CaseDetails.class.getName() + " i " //
	                + " Where i.caseId=:caseId";
				query= entityManager.createQuery(sql, CaseDetails.class);
		        query.setParameter("caseId", courtOrderNo);
			}
			List<CaseDetails> caseList=query.getResultList();
	        if(!caseList.isEmpty())
	        	return caseList.get(0);
	        else
	        	return null;
	    } catch (NoResultException e) {
	        return null;
	    }
	}
	
	@SuppressWarnings("unchecked")
	public InitiatorOfficeOrderDraft findDraftByCaseDetails(CaseDetails caseDetails){
		try {
	        String sql = "Select i from " + InitiatorOfficeOrderDraft.class.getName() + " i " //
	                + " Where i.isFinal =false and i.caseDetails=:caseDetails";
	        Query query = entityManager.createQuery(sql, InitiatorOfficeOrderDraft.class);
	        if(caseDetails != null) {
				query.setParameter("caseDetails", caseDetails);
			}
			else {
				query.setParameter("caseDetails", new CaseDetails());
			}
	        List<InitiatorOfficeOrderDraft> summonlist=query.getResultList();
	        if(!summonlist.isEmpty())
	        	return summonlist.get(summonlist.size()-1);
	        else
	        	return null;
	        
	        //return (SummonDetails) query.getSingleResult();
	    } catch (NoResultException e) {
	        return null;
	    }
	}
	
	@SuppressWarnings("unchecked")
	public List<Object[]> findOfficeOrderByDirApprove() {
		try {
			  String sql = "select cd.case_id,cd.case_title,o.id,o.oo_din ,cd.radio_value from investigation.office_order o,investigation.case_details cd where o.aprroval_stage2=true and cd.id=o.case_details_id and o.is_signed=0 order by o.id desc";
			Query query = this.entityManager.createNativeQuery(sql);
			
			
			List<Object[]>OfficeOrderlist=query.getResultList();
	            if(!OfficeOrderlist.isEmpty())
	            	return OfficeOrderlist;
	            else
	            	return OfficeOrderlist;
		
		} catch (NoResultException e) {
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<Object[]> findLegacyOrderByAdoApprove() {
		try {
			  String sql = "select cd.case_id,cd.case_title,o.id,o.oo_din ,cd.radio_value from investigation.office_order o,investigation.case_details cd where o.aprroval_stage2=true and cd.id=o.case_details_id and o.is_signed=1 and (cd.radio_value='Legacy' or  cd.radio_value='Prosecution') order by o.id desc";
			Query query = this.entityManager.createNativeQuery(sql);
			
			List<Object[]>  findLegacyOrderlist = query.getResultList();
			
			
			return (List<Object[]>) query.getResultList();
		} catch (NoResultException e) {
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<Object[]> findOfficeLegacyOrderByAdoApprove() {
		try {
			  String sql = "select cd.case_id,cd.case_title,o.id,o.oo_din ,cd.radio_value from investigation.office_order o,investigation.case_details cd where o.aprroval_stage2=true and cd.id=o.case_details_id and o.is_signed=1 and (cd.radio_value='Legacy' or cd.radio_value='Prosecution') order by o.id desc";
			Query query = this.entityManager.createNativeQuery(sql);
			return (List<Object[]>) query.getResultList();
		} catch (NoResultException e) {
			return null;
		}
	}
	/*@SuppressWarnings("unchecked")
	public List<Object[]> findOrderTemplateByCaseId(){
		try {
			String sql="select * from investigation.office_order o,investigation.office_order_template ot where case_details_id=:caseId and o.id=ot.office_order_id and ot.app_user_user_id=5";
	        Query query = entityManager.createNamedQuery(sql);
	        query.setParameter("caseId", caseId);
	       return (List<Object[]>) query.getResultList();
	        
	    } catch (NoResultException e) {
	        return null;
	    }
	}*/
	
	@SuppressWarnings({ "unchecked", "unused" })
	public List<Object[]> findSignedOfficeOrder(AppUser userId) {
		try {
			  String sql = "select cd.case_id,cd.case_title,o.id,o.order_signed_date,o.sign_file from investigation.office_order o,investigation.case_details cd,investigation.inspector i where cd.id=o.case_details_id and i.app_user_user_id=:userId and o.is_signed=1";
			Query query = this.entityManager.createNativeQuery(sql);
			
			//gouthami 15/09/2020
		     snmsValid =  new SNMSValidator();
		     if(!snmsValid.getValidInteger(userId.getUserId())) {
		    	 return null;
		     }else {
		    	  if(userId != null) {
		            	query.setParameter("userId", userId.getUserId());
		                  }else {
		                	  query.setParameter("userId", new AppUser().getUserId());
		                  }
			return (List<Object[]>) query.getResultList();
		     }
			
			
		} catch (NoResultException e) {
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<Object[]> findAllSignedOfficeOrder() {
		try {
			  String sql = "select cd.case_id,cd.case_title,o.id,o.order_signed_date,o.sign_file,cd.legacy_order_file,cd.id as caseid,cd.radio_value , o.oo_din  from investigation.office_order o,investigation.case_details cd where cd.id=o.case_details_id and o.is_signed=1 and (cd.radio_value='MCA')  ";
			Query query = this.entityManager.createNativeQuery(sql);
			return (List<Object[]>) query.getResultList();
		} catch (NoResultException e) {
			return null;
		}
	}

	public boolean isUniqueFinYear(String fromY, String toY) {
		String sql="";
		Query query=null;
		try {
				sql= "Select i from " + FinancialMaster.class.getName() + " i " //
	                + " Where i.fromY=:fromY and i.toY=:toY";
				query= entityManager.createQuery(sql, FinancialMaster.class);
		        query.setParameter("fromY", fromY);
		        query.setParameter("toY", toY);
		        List<FinancialMaster> finList=query.getResultList();
		        if(finList.size()>0)
		        	return false;
		        else
		        	return true;
		} catch (Exception e) {
	      logger.info(e.getMessage());
	    }
		return false;
	}

	@SuppressWarnings("unchecked")
	public List<OfficeOrder> findOrderByAdoApprove(long caseid) {
		SNMSValidator snmsvalid = new SNMSValidator();
		
		try {
			  String sql = "select  * from investigation.office_order  where aprroval_stage1=true and case_details_id =:case_details_id";
			  Query nativequery = entityManager.createNativeQuery(sql);
	            
	          
	            if(snmsvalid.getValidInteger(caseid)) {
	            nativequery.setParameter("case_details_id", caseid);
	            List<OfficeOrder> listOffice = nativequery.getResultList();
	            if(!listOffice.isEmpty())
	            	return listOffice;
	            else
	            
	            	return null;
	            }else
	            	return null;
			//return (List <OfficeOrder>) nativequery.getResultList();
		} catch (NoResultException e) {
			return null;
		}
	}
	
}
