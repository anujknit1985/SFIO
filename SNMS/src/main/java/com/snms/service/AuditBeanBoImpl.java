package com.snms.service;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.snms.dao.AuditBeanDao;
import com.snms.dto.AuditBean;
import com.snms.entity.AuditTrail;

@Service("auditBeanBo")
public class AuditBeanBoImpl implements AuditBeanBo
{

    private AuditBean auditBean = new AuditBean();
 /*   private static final Logger logger    = Logger
            .getLogger(AuditBeanBoImpl.class);*/
    @Autowired
    private AuditBeanDao  auditBeanDao;
    
    /**
     * Method for set variable of AuditBean object
     */
    @Override
    public void setAuditBean() throws  Exception
    {
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder
                .currentRequestAttributes();
        String actorIp="";
        actorIp = attr.getRequest().getHeader("X-Forwarded-For");
        if(null==actorIp)
        actorIp = attr.getRequest().getRemoteAddr();
        
//        String actorIp = attr.getRequest().getRemoteAddr();
        SimpleDateFormat sdf = new SimpleDateFormat(
                "dd/MM/YYYY hh:mm:ss");
        String date = sdf.format(new Date());
        String path = attr.getRequest().getRequestURL().toString();
       /* String url = path
                .substring(path
                        .lastIndexOf(attr.getRequest().getServletContext()
                                .getContextPath())
                        + attr.getRequest().getServletContext().getContextPath()
                                .length());*/
        
        String url = path.substring(path.lastIndexOf("/"));
        auditBean.setActorIp(actorIp);
        auditBean.setDate(date);
        auditBean.setUrl(url);
    }

    /**
     * Method for set variable of AuditBean object
     * 
     * @param actorId - int variable for login user id
     * @param actorName - String variable for login user name
     */
    @Override
    public void setAuditBean(int actorId, String actorName)
            throws  Exception
    {
        setAuditBean();
        auditBean.setActorId(actorId);
        auditBean.setActorName(actorName);
    }

    /**
     * Method for set variable of AuditBean object
     * 
     * @param domId - int variable of domain Id
     * @param targetedUser - String variable for targeted user name
     * @param opFlag - String variable of opFlag(true or false)
     */
    @Override
    public void setAuditBean(int domId, String targetedUser, String opFlag)
            throws  Exception
    {
        setAuditBean();
        auditBean.setDomainId(domId);
        auditBean.setTargetedUser(targetedUser);
        auditBean.setOpStatus(opFlag);
    }

    /**
     * Method for set variable of AuditBean object
     * 
     * @param actorId - int variable for login user id
     * @param actorName - String variable for login user name
     * @param doaminName - String variable for doamin name
     * @param domId - int variable of domain Id
     * @param operationType - String variable for operationType
     * @param operationDesc - String variable for operationDesc
     * @param targetedUser - String variable for targeted user name
     * @param opFlag - String variable of opFlag(true or false)
     */
    @Override
    public void setAuditBean(int actorId, String actorName, String domainName,
            int domainId, String operationType, String operationDesc,
            String targetedUser, String opFlag) throws  Exception
    {

        setAuditBean();
        auditBean.setActorId(actorId);
        auditBean.setActorName(actorName);
        auditBean.setDomainName(domainName);
        auditBean.setDomainId(domainId);
        auditBean.setOperationType(operationType);
        auditBean.setOperationDesc(operationDesc);
        auditBean.setTargetedUser(targetedUser);
        auditBean.setOpStatus(opFlag);
    }

    /**
     * Method for set variable of AuditBean object
     * 
     * @param doaminName - String variable for doamin name
     * @param operationType - String variable for operationType
     * @param operationDesc - String variable for operationDesc
     * @param opTime - Time taken in a operation
     */
    @Override
    public void setAuditBean(String domainName, String operationType,
            String operationDesc, String opTime) throws  Exception
    {
        setAuditBean();
        auditBean.setDomainName(domainName);
        auditBean.setOperationType(operationType);
        auditBean.setOperationDesc(operationDesc);
        auditBean.setOpTime(opTime);
    }

    /**
     * Method for save AuditBean object
     */
    @Override
    public void save() throws  Exception
    {
        if ("true".equals(auditBean.getOpStatus())){
//            auditBean.saveAudit();
        	AuditTrail auditTrail = auditBean.getAuditBeanDetaild();
        auditBeanDao.saveAuditTrail(auditTrail);
        }
    }

    /**
     * Method for set variable of AuditBean object
     * 
     * @param domId - int variable of domain Id
     * @param targetedUser - String variable for targeted user name
     * @param opFlag - String variable of opFlag(true or false)
     */

    @Override
    public void setAuditBean(int domId, String loginuser, int loginUId,
            String targetedUser, String opFlag) throws  Exception
    {
        setAuditBean();
        auditBean.setDomainId(domId);
        auditBean.setActorId(loginUId);
        auditBean.setActorName(loginuser);
        auditBean.setTargetedUser(targetedUser);
        auditBean.setOpStatus(opFlag);
    }
}
