package com.snms.dto;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.snms.entity.AddDesignation;
import com.snms.entity.UserDetails;
import com.snms.service.AddDesignationRepository;
import com.snms.utils.SnmsException;

@Service
@Transactional
public class DesignationDaoImpl{
	@Autowired
	private EntityManager entityManager;
	@Autowired
	private AddDesignationRepository repo;
	public void save(AddDesignation des) throws SnmsException,Exception
	{
		
		 try {
			 repo.save(des);
		    } catch (Exception er) {
		      SnmsException dex = new SnmsException(er.getMessage(), er.getCause());
		      dex.setERROR_CODE("001");
		      if (des == null)
		        dex.setParameter(new String[] { "Nill designation" });
		      else
		        dex.setParameter(new String[] {des + "" });
		      throw dex;
		    }
		
	}
	public List<AddDesignation> findDesignationBytype(AddDesignation addDesignation) {
		try {
			// String sql = "Select * from " + UserDetails.class + " e Where e.unit =
			// "+UnitDetails.class;

			String sql = "Select e from " + AddDesignation.class.getName() + " e " //
					+ " where e.type= 'C' or  e.type='S' order by e.designation";

			Query query = entityManager.createQuery(sql, AddDesignation.class);
			
			List<AddDesignation> designationList = query.getResultList();
			if (designationList != null)
				return designationList;
			else
				return designationList;
		} catch (NoResultException e) {
			return null;
		}
	}
	public List<AddDesignation> findDesignationByUsertype(AddDesignation addDesignation) {
		try {
			// String sql = "Select * from " + UserDetails.class + " e Where e.unit =
			// "+UnitDetails.class;

			String sql = "Select e from " + AddDesignation.class.getName() + " e " //
					+ " where e.type= 'U' or  e.type='S' order by e.designation";

			Query query = entityManager.createQuery(sql, AddDesignation.class);
			
			List<AddDesignation> designationList = query.getResultList();
			if (designationList != null)
				return designationList;
			else
				return designationList;
		} catch (NoResultException e) {
			return null;
		}
	}
}
