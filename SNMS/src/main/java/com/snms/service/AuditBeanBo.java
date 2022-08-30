package com.snms.service;


public interface AuditBeanBo
{

    void setAuditBean() throws  Exception;
    public void setAuditBean(int actorId, String actorName)
            throws  Exception;
    public void setAuditBean(int domId, String targetedUser, String opFlag)
            throws  Exception;
    public void setAuditBean(int actorId, String actorName, String domainName,
            int domainId, String operationType, String operationDesc,
            String targetedUser, String opFlag) throws  Exception;
    public void setAuditBean(String domainName, String operationType,
            String operationDesc, String opTime) throws  Exception;
    public void save() throws  Exception;
    public void setAuditBean(int domId, String loginuser, int loginUId,
            String targetedUser, String opFlag) throws  Exception;

}
