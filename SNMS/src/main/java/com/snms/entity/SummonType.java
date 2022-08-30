package com.snms.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Past;

import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;

import lombok.Data;

@Entity
@Data
@Table(name = "SummonType", schema = "investigation")
public class SummonType {
	
	

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private int id;
	
	private String  name;
	private String nationalId;
	private String nationality;
	@Length(min = 3, max = 255)
	private String address;
	@Column(length = 50)
	private String email;

	@DateTimeFormat(pattern = "dd/MM/yyyy")
	private Date offJoinDate;
	private String mobileNo;
	
	private String placeof_issued;
	@Column (length = 1)
	private String sex;
	//private String Company_name;
	@Column(length=50)
	private String relationwithcompany;
	@Column(length=25)
    private String registration_no;
    private String designation;
 
    @DateTimeFormat(pattern = "dd/MM/yyyy")
	private Date dob;
  
	@DateTimeFormat(pattern = "dd/MM/yyyy")
	private Date issueDate;
	@Column (length = 1)
	private String isCin;    
    @ManyToOne(fetch = FetchType.LAZY)
	@PrimaryKeyJoinColumn(name = "Type")
    
	private IndividualType  individualType;
    @Column(length=100)
    private String NameCompany;
    
    @Transient
	private Long IndividualId;
    private String passport;
	private String panNumber;
	private String aadharNumber;
	
	//Director 
    @Transient
    private  String  dirName;
    @Transient
    private String  dirReg_no;
    @Transient
    private String  dirAddr;
    @Transient

    @DateTimeFormat(pattern = "dd/MM/yyyy")
    @Past
    private Date diroffJoinDate;
    @Transient
    private String dirEmail;
    @Transient
    private String dirMobile;
    @Transient
    private String dirpassport;
    @Transient
	private String dirpanNumber;
    @Transient
    private String diraadharNumber;
	
	
	
  //FormarDirector
    @Transient
    private  String  fdirName;
    @Transient
    private String  fdirReg_no;
    @Transient
    private String  fdirAddr;
    @Transient

    @DateTimeFormat(pattern = "dd/MM/yyyy")
    @Past
    private Date fdiroffJoinDate;
    @Transient
    private String fdirEmail;
    @Transient
    private String fdirMobile;
    
    @Transient
    private String fdirpassport;
    @Transient
	private String fdirpanNumber;
    @Transient
    private String fdiraadharNumber;
	
    //emploayee //formaremplaoyee
    
    @Transient
    private String empName;
    @Transient
    private String empdesgi;
    @Transient
    private String empsex;
    @Transient
    private String empNation;
    @Transient
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    @Past
    private Date empDob;
    @Transient
    private String empnatid;
    @Transient
    private String empPassport;
    
    @Transient
	private String empPanNumber;
    @Transient
    private String empissuplace;
    @Transient
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    @Past
    private Date empPassDate;
    @Transient
    @Past
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private Date empoffdate;
    @Transient
    private String empAddress;
    @Transient
    private String empMobile;
    @Transient
    private String empemail;
    
    @Transient
    private String empaadharNumber;
	
    //formalEmployee
    
    @Transient
    private String fempName;
    @Transient
    private String fempdesgi;
    @Transient
    private String fempsex;
    @Transient
    private String fempNation;
    @Transient
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    @Past
    private Date fempDob;
    @Transient
    private String fempnatid;
    @Transient
    private String fempPassport;
    
    @Transient
	private String fempPanNumber;
    @Transient
    private String fempissuplace;
    @Transient
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    @Past
    private Date fempPassDate;
    @Transient
    @Past
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private Date fempoffdate;
    @Transient
    private String fempAddress;
    @Transient
    private String fempMobile;
    @Transient
    private String fempemail;
    
    @Transient
    private String fempaadharNumber;
	
    //Adgent 
    
    @Transient
    private String agentName;
    @Transient
    private String agentcompany;
    @Transient
    private String agentEmail;
    @Transient
    private String agentMobile;
    @Transient
    private String agentAddress;
    @Transient
    private String agentRegno;
    @Transient
    private String isACin;    
    @Transient
    private String agentpassport;
    @Transient
	private String agentpanNumber;
    @Transient
    private String agentaadharNumber;
	
	
    @Transient
    private String fagentName;
    @Transient
    private String fagentcompany;
    @Transient
    private String fagentEmail;
    @Transient
    private String fagentMobile;
    @Transient
    private String fagentAddress;
    @Transient
    private String fagentRegno;
    @Transient
    private String fisACin;    
    @Transient
    private String fagentpassport;
    @Transient
	private String fagentpanNumber;
    @Transient
    private String fagentaadharNumber;
	  
    
    
    //others
    @Transient
    private String othName;
    @Transient
    private String othRegno;
    @Transient
    private String othRelation;
    @Transient
    private String othEmail;
    @Transient
    private String othMobile;
    @Transient
    private String othAddress;
    
    @Transient
  
    private String isOCin;    
	
    @Transient
    private String othpassport;
    @Transient
	private String othpanNumber;
	
    @Transient
    private String othaadharNumber;
	
    public SummonType(int summontypeid) {
    	this.id = summontypeid;
	}




	public SummonType() {
		super();
	}



	 @Transient
	    private int editDeleteId;
	 @Transient
	    private int compId;
	 @Transient
	    private int dataId;

}
