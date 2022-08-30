package com.snms.dto;

import java.util.Date;

import com.snms.entity.AuditTrail;

public class AuditBean
{
//    private static final Logger auditLog = Logger.getLogger("auditLogger");

    private int                 actorId;
    private String              actorName;
    private String              actorIp;
    private String              domainName;
    private int                 domainId;
    private String              operationType;
    private String              operationDesc;
    private String              url;
    private String              date;
    private String              targetedUser;
    private String              opStatus;
    private String              opTime;
    private String              userAgent;
    private String              referer;

    ////////////////////////////////////////////////////////////////////////
    // getter METHODS
    ////////////////////////////////////////////////////////////////////////

    public String getUserAgent()
    {
        return userAgent;
    }
    public void setUserAgent(String userAgent)
    {
        this.userAgent = userAgent;
    }
    public String getReferer()
    {
        return referer;
    }
    public void setReferer(String referer)
    {
        this.referer = referer;
    }
    public int getActorId()
    {
        return actorId;
    }
    public String getActorName()
    {
        return actorName;
    }
    public String getActorIp()
    {
        return actorIp;
    }
    public String getDomainName()
    {
        return domainName;
    }
    public int getDomainId()
    {
        return domainId;
    }
    public String getOperationType()
    {
        return operationType;
    }
    public String getOperationDesc()
    {
        return operationDesc;
    }
    public String getUrl()
    {
        return url;
    }
    public String getDate()
    {
        return date;
    }
    public String getTargetedUser()
    {
        return targetedUser;
    }
    public String getOpStatus()
    {
        return opStatus;
    }
    public String getOpTime()
    {
        return opTime;
    }

    ////////////////////////////////////////////////////////////////////////
    // setter METHODS
    ////////////////////////////////////////////////////////////////////////

    public void setActorId(int actorId)
    {
        this.actorId = actorId;
    }
    public void setActorName(String actorName)
    {
        this.actorName = actorName;
    }
    public void setActorIp(String actorIp)
    {
        this.actorIp = actorIp;
    }
    public void setDomainName(String domainName)
    {
        this.domainName = domainName;
    }
    public void setDomainId(int domainId)
    {
        this.domainId = domainId;
    }
    public void setOperationType(String operationType)
    {
        this.operationType = operationType;
    }
    public void setOperationDesc(String operationDesc)
    {
        this.operationDesc = operationDesc;
    }
    public void setUrl(String url)
    {
        this.url = url;
    }
    public void setDate(String date)
    {
        this.date = date;
    }
    public void setTargetedUser(String targetedUser)
    {
        this.targetedUser = targetedUser;
    }
    public void setOpStatus(String opStatus)
    {
        this.opStatus = opStatus;
    }
    public void setOpTime(String opTime)
    {
        this.opTime = opTime;
    }

    ////////////////////////////////////////////////////////////////////////
    // operation METHODS
    ////////////////////////////////////////////////////////////////////////

    /**
     * Method for saving log details in log file
     */
    public void saveAudit()
    {

        /*auditLog.info("[DATE:" + date + "]," + "[ACTOR_ID:" + actorId + "],"
                + "[ACTOR_NAME:" + actorName + "]," + "[ACTOR_IP:" + actorIp
                + "]," + "[SCREEN:" + url + "]," + "[DOMAIN:" + domainName
                + "]," + "[DOMAIN_ID:" + domainId + "]," + "[OPERATION_TYPE:"
                + operationType + "]," + "[OPERATION_DESC:" + operationDesc
                + "]," + "[OPERATION_TIME:" + opTime + "]," + "[TARGETED_USER:"
                + targetedUser + "]," + "[USERAGENT:" + userAgent + "],"
                + "[REFERER:" + referer + "]");*/
    }
	public AuditTrail getAuditBeanDetaild() {
		AuditTrail auditTrail = new AuditTrail();
		auditTrail.setActorId(actorId);
		auditTrail.setActorName(actorName);
		auditTrail.setActorIP(actorIp);
		auditTrail.setDomain(domainName);
		auditTrail.setActionDate(new Date());
		auditTrail.setUrl(url);
		auditTrail.setOperationType(operationType);
		auditTrail.setOperationDesc(operationDesc);
		return auditTrail;
	}
}
