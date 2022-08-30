package com.snms.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.snms.entity.ICAI;
import com.snms.utils.SnmsException;

@Repository
@Transactional
public class AjaxDao {

	@Autowired
    private EntityManager entityManager;
 
    @SuppressWarnings("unchecked")
	public ICAI findByRegNo(String registartion_no) throws SnmsException,Exception{
        try {
            String sql = "Select e from " + ICAI.class.getName() + " e " //
                    + " Where e.registartion_no = :registartion_no";
 
            Query query = entityManager.createQuery(sql, ICAI.class);
            query.setParameter("registartion_no", registartion_no);
            List<ICAI> icai=query.getResultList();
            if(!icai.isEmpty())
            	return icai.get(0);
            else
            	return null;
            //return (ICAI) query.getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }
}
