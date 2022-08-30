package com.snms.dao;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.snms.entity.DSCRegistration;

@Repository
@Transactional
public class DSCDao {
	
	@Autowired
    private EntityManager entityManager;
 
    public DSCRegistration findUserId(Long userid) {
        try {
            String sql = "Select e from " + DSCRegistration.class.getName() + " e " //
                    + " Where e.userid = :userid AND e.regStatus = :regStatus";
 
            Query query = entityManager.createQuery(sql, DSCRegistration.class);
            query.setParameter("userid", userid);
            query.setParameter("regStatus", 1);
            return (DSCRegistration) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

}
