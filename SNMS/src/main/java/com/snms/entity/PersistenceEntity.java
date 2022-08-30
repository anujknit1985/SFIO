package com.snms.entity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

@MappedSuperclass
public class PersistenceEntity
{

    private Date   createdDate;
    @Column(length = 50)
    private String createdBy;
    private Date   modifiedDate;
    @Column(length = 50)
    private String modifiedBy;
    @Transient
    private String createdStrDate;
    @Transient
    private String modifiedStrDate;
   
    ////////////////////////////////////////////////////////////////////////
    // getter METHODS
    ////////////////////////////////////////////////////////////////////////

    public Date getCreatedDate()
    {
        return createdDate;
    }
    public String getCreatedBy()
    {
        return createdBy;
    }
    public Date getModifiedDate()
    {
        return modifiedDate;
    }
    public String getModifiedBy()
    {
        return modifiedBy;
    }
    public String getCreatedStrDate()
    {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
        if (getCreatedDate() != null)
        {
            String dtFormat = sdf.format(getCreatedDate());
            return dtFormat;
        }
        return "";
    }
    public String getModifiedStrDate()
    {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        if (getModifiedDate() != null)
        {

            String mdDtFormat = sdf.format(getModifiedDate());
            return mdDtFormat;
        }
        return "";
    }

    ////////////////////////////////////////////////////////////////////////
    // setter METHODS
    ////////////////////////////////////////////////////////////////////////

    public void setCreatedDate(Date createdDate) throws ParseException
    {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        this.createdDate = sdf.parse(sdf.format(createdDate));
    }
    public void setCreatedBy(String createdBy)
    {
        this.createdBy = createdBy;
    }
    public void setModifiedDate(Date modifiedDate)
    {
        this.modifiedDate = modifiedDate;
    }
    public void setModifiedBy(String modifiedBy)
    {
        this.modifiedBy = modifiedBy;
    }
    public void setCreatedStrDate(String createdStrDate)
    {
        this.createdStrDate = createdStrDate;
    }
    public void setModifiedStrDate(String modifiedStrDate)
    {
        this.modifiedStrDate = modifiedStrDate;
    }

}
