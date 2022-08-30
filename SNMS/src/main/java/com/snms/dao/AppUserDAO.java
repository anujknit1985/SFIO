package com.snms.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.snms.entity.AppUser;
import com.snms.entity.UserDetails;
import com.snms.entity.UserRole;
import com.snms.utils.SnmsException;
 
@Repository
@Transactional
public class AppUserDAO {
 
    @Autowired
    private EntityManager entityManager;
 
    public AppUser findUserAccount(String userName) {
        try {
            String sql = "Select e from " + AppUser.class.getName() + " e " //
                    + " Where e.userName = :userName ";
 
            Query query = entityManager.createQuery(sql, AppUser.class);
            query.setParameter("userName", userName);
 
            return (AppUser) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

	public com.snms.entity.UserDetails findUserDetails(AppUser appUser) {
		 try {
	            String sql = "Select e from " + UserDetails.class.getName() + " e " //
	                    + " Where e.userId = :userId ";
	 
	            Query query = entityManager.createQuery(sql, UserDetails.class);
	            query.setParameter("userId", appUser);
	 
	            return (UserDetails) query.getSingleResult();
	        } catch (NoResultException e) {
	            return null;
	        }
	}
	
	
	 public UserRole getRoleId(Long userId)  {
	        String sql = "Select  ur  from " + UserRole.class.getName() + " ur " //
	                + " where ur.appUser.userId = :userId ";
	 
	        Query query = this.entityManager.createQuery(sql, UserRole.class);
	        query.setParameter("userId", userId);
	        return (UserRole) query.getResultList().get(0);
	    }

	 
}
