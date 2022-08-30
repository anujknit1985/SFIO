package com.snms.dao;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.snms.entity.AuditTrail;
import com.snms.entity.NoticeStatus;
import com.snms.entity.SummonType;

@Repository
@javax.transaction.Transactional
public class AuditBeanDaoImpl implements AuditBeanDao {
	@Autowired
	private EntityManager entityManager;
	public void saveAuditTrail(AuditTrail auditTrail) {
		entityManager.persist(auditTrail);
	}
	@Override
	public List<AuditTrail> findAllByActionDate(Date date1) {
		try {
            String sql = "Select e from " + AuditTrail.class.getName() + " e " //
                    + " Where Date(e.actionDate) = :actionDate";
 
            Query query = entityManager.createQuery(sql, AuditTrail.class);


            if(date1 != null) {
               			query.setParameter("actionDate", date1);
               		}
               		
            List<AuditTrail> listAuditTrail=query.getResultList();
            if(!listAuditTrail.isEmpty())
            	return listAuditTrail;
            else
            	return null;
            
            //return (SummonDetails) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
	}
	@Override
	public List<AuditTrail> findAllByActionDateBetween(String date1, String date2) {
		try {
            String sql = "Select e from " + AuditTrail.class.getName() + " e " 
                    + " Where Date(e.actionDate)  BETWEEN '"+ date1 +"' AND '" + date2 + "'";
 
          Query query = entityManager.createQuery(sql, AuditTrail.class);         
            
            String sql1 = "SELECT at.*,concat (ud.first_name ,' ', ud.middle_name,' ',ud.last_name) as name FROM public.audit_trail at inner join authentication.app_user  app_user\r\n" + 
            		"on at.actor_id =app_user.user_id inner join authentication.user_details ud on app_user.user_id= ud.app_user_user_id Where Date(at.action_Date)  BETWEEN to_date(:date1, 'YYYY MM DD') and to_date(:date2, 'YYYY MM DD')  order by at.id desc";
			Query query1 = entityManager.createNativeQuery(sql1);
		   query1.setParameter("date1", date1);
		   query1.setParameter("date2", date2);

			List<Object[]> NoticeList = query1.getResultList();
			
			 List<AuditTrail> listAuditTrail=new ArrayList<AuditTrail>();  
		for(	Object[] object  : NoticeList)
		{
			
			
	            AuditTrail objAuditTrail =new AuditTrail();
	            
	            objAuditTrail.setActorIP((object[2].toString()));
	            objAuditTrail.setActorName((object[9].toString()));
	            objAuditTrail.setUrl((object[8].toString()));
	            if (object[6] != null)
	            objAuditTrail.setOperationDesc((object[6].toString()));
	            
	            if (object[1] != null)
	            	System.out.println(object[1].toString());
	            
	            
	            SimpleDateFormat dateParser = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	            
	                try {
	                    Date date = dateParser.parse(object[1].toString());
	                    System.out.println(date);
	                    objAuditTrail.setActionDate(date);

	                 //   SimpleDateFormat dateFormatter = new SimpleDateFormat("MM/dd/yy");
	                    //System.out.println(dateFormatter.format(date));

	                }  catch (java.text.ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	            
	            listAuditTrail.add(objAuditTrail);
	        }
			
			 // List<AuditTrail> listAuditTrail=query.getResultList();
	            if(!listAuditTrail.isEmpty())
	            	return listAuditTrail;
	            else
	            	return null;
		
            
            //return (SummonDetails) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
	}

}
