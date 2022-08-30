package com.snms.validation;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.multipart.MultipartFile;

import com.snms.entity.CaseDetails;
import com.snms.entity.Company;
import com.snms.entity.FinancialMaster;
import com.snms.entity.Linkofficer;
import com.snms.entity.UnitDetails;
import com.snms.entity.UserDetails;

public class UserValidation {

	public void validateUserRegComplete(UserDetails userRegistration, Errors errors,boolean isUniqueUser,boolean isUniquePrimaryMob,boolean isUniqueAlternateMob) {
		
		String salutation = userRegistration.getSalutation();
		String email = userRegistration.getEmail();
		String primaryMobile = userRegistration.getPrimaryMobile();
		String alternateNo = userRegistration.getAlternateNo();

		String firstName = userRegistration.getFirstName();
		String middleName = userRegistration.getMiddleName();
		String lastName = userRegistration.getLastName();
		
		Date uiDob = userRegistration.getUiDob();
		String sfioEmpId = userRegistration.getSfioEmpId();
		Long designationId = userRegistration.getDesignationId();
		Date uiJoiningDate = userRegistration.getUiJoiningDate();
		Long unitId = userRegistration.getUnitId();
		
		Long roleId = userRegistration.getRoleId();

		SNMSValidator snmsVal = new SNMSValidator();

		snmsVal.isvalidSalutation("salutation", salutation, errors, "errmsg.salutation", true);
		snmsVal.isvalidPersonName("firstName", firstName, errors, "errmsg.fnames", true);
		snmsVal.isvalidPersonName("middleName", middleName, errors, "errmsg.mnames", false);
		snmsVal.isvalidPersonName("lastName", lastName, errors, "errmsg.lnames", false);
		snmsVal.isValidMobile("primaryMobile", primaryMobile, errors);
		if(!snmsVal.isBlank("alternateNo", alternateNo))
			snmsVal.isValidMobile("alternateNo", alternateNo, errors);
		snmsVal.isValidEmail("email", email, errors,true);
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		if (uiDob != null && !uiDob.equals("")) {
			boolean isValid = snmsVal.validateDateFormat(sdf.format(uiDob));
			if (!isValid) {
				errors.rejectValue("uiDob", "errmsg.toDate");
			}
		} else if (uiDob == null || uiDob.equals("")) {
			errors.rejectValue("uiDob", "errmsg.required");
		}
		snmsVal.isvalidRegnum("sfioEmpId", sfioEmpId, errors, "errmsg.engage.reg", false);
		
		if (uiJoiningDate != null && !uiJoiningDate.equals("")) {
			boolean isValid = snmsVal.validateDateFormat(sdf.format(uiJoiningDate));
			if (!isValid) {
				errors.rejectValue("uiJoiningDate", "errmsg.toDate");
			}
		} else if (uiJoiningDate == null || uiJoiningDate.equals("")) {
			errors.rejectValue("uiJoiningDate", "errmsg.required");
		}
		
		if (!isUniqueUser)
			errors.rejectValue("email", "errmsg.uniqueUser");
		
		if (!isUniquePrimaryMob)
			errors.rejectValue("primaryMobile", "errmsg.uniqueMobile");
		
		if (!isUniqueAlternateMob)
			errors.rejectValue("alternateNo", "errmsg.uniqueMobile");
		
		snmsVal.isValidDropDown("designation",Integer.parseInt(designationId.toString()),errors);
		snmsVal.isValidDropDown("unit",Integer.parseInt(unitId.toString()),errors);
		snmsVal.isValidDropDown("roleId",Integer.parseInt(roleId.toString()),errors);
	}
	
	public void validateUnitDetails(UnitDetails unitDetails, Errors errors) {
		
		 String unitName = unitDetails.getUnitName();
		 String location = unitDetails.getLocation();
		 String address =  unitDetails.getAddress();
		 String addresshi =  unitDetails.getAddress_hi();
		 String telephoneNo = unitDetails.getTelephoneNo();
		 String faxNo = unitDetails.getFaxNo();
		 String eMail = unitDetails.getEMail();
		 
		 SNMSValidator snmsVal = new SNMSValidator();
		 
		 snmsVal.isvalidUnit("unitName", unitName, errors, "errmsg.unitname", true);
		 snmsVal.isvalidLocation("location", location, errors, "errmsg.location", true);
		 snmsVal.isvalidUserAddress("address", address, errors, "errmsg.fname", true);
		 
		 if(addresshi==null || addresshi.equalsIgnoreCase("")|| addresshi.isEmpty()) {
			 snmsVal.isvalidUserAddress("address_hi", address, errors, "errmsg.required", true);
					 
		 }
		 snmsVal.isValidFirmPhone("telephoneNo", telephoneNo, errors);
		 snmsVal.isValidFax("faxNo", faxNo, errors);
		 snmsVal.isValidEmail("eMail", eMail, errors,false);
	}

	public void validateCaseDetails(@Valid CaseDetails caseDetails, BindingResult errors) throws JSONException, IOException {
		 String caseId = caseDetails.getCaseId();
		 String radioValue = caseDetails.getRadioValue().replace(",", "");
		 String caseTitle = caseDetails.getCaseTitle();
		 String mcaOrderNo = caseDetails.getMcaOrderNo();
		 String mcaDate = caseDetails.getMcaDate();
		 String courtOrderNo = caseDetails.getCourtOrderNo();
		 String courtDate = caseDetails.getCourtDate();
//		 Date mcaOrderDate;
//		 Date courtOrderDate;
		 Long unitId = caseDetails.getUnitId();
//		 ArrayList<Object> companies = caseDetails.getCompanies();
		 String companyName[] = caseDetails.getCompanyName() ;
		 String inspectors[] = caseDetails.getInspectors();
//		 String mcaOrderFile;
//		 String courtOrderFile;
		 MultipartFile mcaOrderFile=caseDetails.getMcaFile();
		 MultipartFile courtOrderFile=caseDetails.getCourtFile();
		 String chooseIo=caseDetails.getChooseIo();
		 String chooseAdo=caseDetails.getChooseAdo();
		 
		 SNMSValidator snmsVal = new SNMSValidator();
		 snmsVal.isvalidCaseId("caseId", caseId, errors, true);
		 snmsVal.isvalidName("caseTitle", caseTitle, errors, "errmsg.casetitle", true);
		 if(radioValue.equals("MCA") || radioValue.equals("Court") || radioValue.equals("Both")){
		 if(radioValue.equals("MCA") || radioValue.equals("Both")){
			 snmsVal.isvalidorder("mcaOrderNo", mcaOrderNo, errors, "errmsg.mcaorderno", true);
			 if (mcaDate != null && !mcaDate.equals("")) {
					boolean isValid = snmsVal.validateDateFormat(mcaDate);
					if (!isValid) {
						errors.rejectValue("mcaDate", "errmsg.toDate");
					}
				} else if (mcaDate == null || mcaDate.equals("")) {
					errors.rejectValue("mcaDate", "errmsg.required");
				}
			 
			 if(mcaOrderFile!=null) {
			 if(mcaOrderFile.getSize() > 0 || !mcaOrderFile.isEmpty())
			 {
				 snmsVal.isValidFile(mcaOrderFile, errors, true,"mcaFile");
				 if(!snmsVal.isValidFileName(mcaOrderFile.getOriginalFilename()))
						errors.rejectValue("mcaFile", "errmsg.filename");
				 if(!snmsVal.isValidFileTikka(mcaOrderFile.getOriginalFilename(), mcaOrderFile))
						errors.rejectValue("mcaFile", "errmsg.maliciousdata");
			 }
		 }
			 else {
					errors.rejectValue("mcaFile", "errmsg.required");
					 
			 }
		 }
		 if(radioValue.equals("Court") || radioValue.equals("Both")){
			 snmsVal.isvalidorder("courtOrderNo", courtOrderNo, errors, "errmsg.courtOrderNo", true);
			 if (courtDate != null && !courtDate.equals("")) {
					boolean isValid = snmsVal.validateDateFormat(courtDate);
					if (!isValid) {
						errors.rejectValue("courtDate", "errmsg.toDate");
					}
				} else if (courtDate == null || courtDate.equals("")) {
					errors.rejectValue("courtDate", "errmsg.required");
				}
			 if(courtOrderFile!=null) {
			 if(courtOrderFile.getSize() > 0 || !courtOrderFile.isEmpty())
			 {
				 snmsVal.isValidFile(courtOrderFile, errors, true,"courtFile");
				 if(!snmsVal.isValidFileName(courtOrderFile.getOriginalFilename()))
						errors.rejectValue("courtFile", "errmsg.filename");
				 if(!snmsVal.isValidFileTikka(courtOrderFile.getOriginalFilename(), courtOrderFile))
						errors.rejectValue("courtFile", "errmsg.maliciousdata");
			 }
		 }
			 else {
					errors.rejectValue("courtFile", "errmsg.required");
					 
			 }
		 }
		 }else{
			 errors.reject("radioValue", "errmsg.radioValue");
		 }
		 int length = 0;
		 if(null!=companyName && !companyName.equals("")){
				for(int i = 0; i < companyName.length ; i++){
					  snmsVal.isvalidCompany("companyName", companyName[i], errors, "errmsg.company", true); ;
					  length++;
				}
				}
		 if(length == 0)
			 errors.rejectValue("companyName", "errmsg.required");
		 
		 length = 0;
		 if(null!=inspectors && !inspectors.equals("")){
				for(int i = 0; i < inspectors.length ; i++){
					  snmsVal.isValidNumeric("inspectors", inspectors[i], errors, "errmsg.inspectors", true); ;
					  length++;
				}
				}
		 if(length == 0)
			 errors.rejectValue("inspectors", "errmsg.required");
		 
		 snmsVal.isvalidRadio("chooseIo", chooseIo, errors);
		 snmsVal.isvalidRadio("chooseAdo", chooseAdo, errors);
		 snmsVal.isValidDropDown("unitId",Integer.parseInt(unitId.toString()),errors);
			 
	}

	public void validateLegacyDetails(@Valid CaseDetails caseDetails, BindingResult errors) throws IOException {

		String caseId = caseDetails.getCaseId();
//		 String radioValue = caseDetails.getRadioValue().replace(",", "");
		String financeYear = caseDetails.getFinanceYear();
		 String caseTitle = caseDetails.getCaseTitle();
		 String mcaOrderNo = caseDetails.getMcaOrderNo();
		 String mcaDate = caseDetails.getMcaDate();
		 String courtOrderNo = caseDetails.getCourtOrderNo();
		 String courtDate = caseDetails.getCourtDate();
//		 Date mcaOrderDate;
//		 Date courtOrderDate;
		 Long unitId = caseDetails.getUnitId();
//		 ArrayList<Object> companies = caseDetails.getCompanies();
		 String companyName[] = caseDetails.getCompanyName() ;
		 String inspectors[] = caseDetails.getInspectors();
//		 String mcaOrderFile;
//		 String courtOrderFile;
		 MultipartFile mcaOrderFile=caseDetails.getMcaFile();
		 MultipartFile officeOrderFile=caseDetails.getOfficeOrderFile();
		 String chooseIo=caseDetails.getChooseIo();
		 String chooseAdo=caseDetails.getChooseAdo();
		 
		 Boolean editCase =  caseDetails.getEditLagacy();
		 
		 SNMSValidator snmsVal = new SNMSValidator();
		 snmsVal.isvalidCaseId("caseId", caseId, errors, true);
		 snmsVal.isvalidFinanceYear("financeYear", financeYear, errors, "errmsg.financeYear", true);
		 
		 snmsVal.isvalidName("caseTitle", caseTitle, errors, "errmsg.casetitle", true);
		 snmsVal.isvalidorder("mcaOrderNo", mcaOrderNo, errors, "errmsg.mcaorderno", true);
			 if (mcaDate != null && !mcaDate.equals("")) {
					boolean isValid = snmsVal.validateDateFormat(mcaDate);
					if (!isValid) {
						errors.rejectValue("mcaDate", "errmsg.toDate");
					}
				}else{
					errors.rejectValue("mcaDate", "errmsg.required");
				}
			 
			 if(mcaOrderFile!=null) {
			 if(mcaOrderFile.getSize() > 0 || !mcaOrderFile.isEmpty())
			 {
				 snmsVal.isValidFile(mcaOrderFile, errors, true,"mcaFile");
				 if(!snmsVal.isValidFileName(mcaOrderFile.getOriginalFilename()))
						errors.rejectValue("mcaFile", "errmsg.filename");
				 if(!snmsVal.isValidFileTikka(mcaOrderFile.getOriginalFilename(), mcaOrderFile))
						errors.rejectValue("mcaFile", "errmsg.maliciousdata");
			 }}else {
				 errors.rejectValue("mcaFile", "errmsg.required");
			 }
		if(caseDetails.getId()==null) {
			if(officeOrderFile!=null) {
			 if(officeOrderFile.getSize() > 0 || !officeOrderFile.isEmpty())
			 {
				 snmsVal.isValidFile(officeOrderFile, errors, true,"officeOrderFile");
				 if(!snmsVal.isValidFileName(officeOrderFile.getOriginalFilename()))
						errors.rejectValue("officeOrderFile", "errmsg.filename");
				 if(!snmsVal.isValidFileTikka(officeOrderFile.getOriginalFilename(), officeOrderFile))
						errors.rejectValue("officeOrderFile", "errmsg.maliciousdata");
			 }else{
				 errors.rejectValue("officeOrderFile", "errmsg.required"); 
			 }
			}else {
				 errors.rejectValue("officeOrderFile", "errmsg.required");
			}
		}else {
			if(officeOrderFile.getSize() > 0 || !officeOrderFile.isEmpty())
			 {
				 snmsVal.isValidFile(officeOrderFile, errors, true,"officeOrderFile");
				 if(!snmsVal.isValidFileName(officeOrderFile.getOriginalFilename()))
						errors.rejectValue("officeOrderFile", "errmsg.filename");
				 if(!snmsVal.isValidFileTikka(officeOrderFile.getOriginalFilename(), officeOrderFile))
						errors.rejectValue("officeOrderFile", "errmsg.maliciousdata");
			 }
		}
		 
		 int length = 0;
		 if(null!=companyName && !companyName.equals("")){
				for(int i = 0; i < companyName.length ; i++){
					  snmsVal.isvalidCompany("companyName", companyName[i], errors, "errmsg.company", true); ;
					  length++;
				}
				}
		 if(length == 0)
			 errors.rejectValue("companyName", "errmsg.required");
		 
		 length = 0;
		 if(null!=inspectors && !inspectors.equals("")){
				for(int i = 0; i < inspectors.length ; i++){
					  snmsVal.isValidNumeric("inspectors", inspectors[i], errors, "errmsg.inspectors", true); ;
					  length++;
				}
				}
		 if(length == 0)
			 errors.rejectValue("inspectors", "errmsg.required");
		 
		 snmsVal.isvalidRadio("chooseIo", chooseIo, errors);
		 snmsVal.isvalidRadio("chooseAdo", chooseAdo, errors);
		 snmsVal.isValidDropDown("unitId",Integer.parseInt(unitId.toString()),errors);
		
	}

	
	public void validateproLegacyDetails(@Valid CaseDetails caseDetails, BindingResult errors) throws IOException {

		String caseId = caseDetails.getCaseId();

		String financeYear = caseDetails.getFinanceYear();
		 String caseTitle = caseDetails.getCaseTitle();
		 String mcaOrderNo = caseDetails.getMcaOrderNo();
		 String mcaDate = caseDetails.getMcaDate();
		 String courtOrderNo = caseDetails.getCourtOrderNo();
		 String courtDate = caseDetails.getCourtDate();
		// Long unitId = caseDetails.getUnitId();
		 String companyName[] = caseDetails.getCompanyName() ;
		 MultipartFile mcaOrderFile=caseDetails.getMcaFile();
		 MultipartFile officeOrderFile=caseDetails.getOfficeOrderFile();
		 
		 Boolean editCase =  caseDetails.getEditLagacy();
		 
		 SNMSValidator snmsVal = new SNMSValidator();
		 snmsVal.isvalidCaseId("caseId", caseId, errors, true);
		 snmsVal.isvalidFinanceYear("financeYear", financeYear, errors, "errmsg.financeYear", true);
		 
		 snmsVal.isvalidName("caseTitle", caseTitle, errors, "errmsg.casetitle", true);
		 snmsVal.isvalidorder("mcaOrderNo", mcaOrderNo, errors, "errmsg.mcaorderno", true);
			 if (mcaDate != null && !mcaDate.equals("")) {
					boolean isValid = snmsVal.validateDateFormat(mcaDate);
					if (!isValid) {
						errors.rejectValue("mcaDate", "errmsg.toDate");
					}
				}else{
					errors.rejectValue("mcaDate", "errmsg.required");
				}
			 if(mcaOrderFile!=null) {
			 if(mcaOrderFile.getSize() > 0 || !mcaOrderFile.isEmpty())
			 {
				 snmsVal.isValidFile(mcaOrderFile, errors, true,"mcaFile");
				 if(!snmsVal.isValidFileName(mcaOrderFile.getOriginalFilename()))
						errors.rejectValue("mcaFile", "errmsg.filename");
				 if(!snmsVal.isValidFileTikka(mcaOrderFile.getOriginalFilename(), mcaOrderFile))
						errors.rejectValue("mcaFile", "errmsg.maliciousdata");
			 }}else{
				 errors.rejectValue("mcaFile", "errmsg.required");
			 }
		if(caseDetails.getId()==null) {
			if(officeOrderFile!=null) {
			 if(officeOrderFile.getSize() > 0 || !officeOrderFile.isEmpty())
			 {
				 snmsVal.isValidFile(officeOrderFile, errors, true,"officeOrderFile");
				 if(!snmsVal.isValidFileName(officeOrderFile.getOriginalFilename()))
						errors.rejectValue("officeOrderFile", "errmsg.filename");
				 if(!snmsVal.isValidFileTikka(officeOrderFile.getOriginalFilename(), officeOrderFile))
						errors.rejectValue("officeOrderFile", "errmsg.maliciousdata");
			 }else{
				 errors.rejectValue("officeOrderFile", "errmsg.required"); 
			 }}else {
				 errors.rejectValue("officeOrderFile", "errmsg.required"); 	
				}
		}else {
			
			if(officeOrderFile.getSize() > 0 || !officeOrderFile.isEmpty())
			 {
				 snmsVal.isValidFile(officeOrderFile, errors, true,"officeOrderFile");
				 if(!snmsVal.isValidFileName(officeOrderFile.getOriginalFilename()))
						errors.rejectValue("officeOrderFile", "errmsg.filename");
				 if(!snmsVal.isValidFileTikka(officeOrderFile.getOriginalFilename(), officeOrderFile))
						errors.rejectValue("officeOrderFile", "errmsg.maliciousdata");
			 }
		}
		
		
		 int length = 0;
		 if(null!=companyName && !companyName.equals("")){
				for(int i = 0; i < companyName.length ; i++){
					  snmsVal.isvalidCompany("companyName", companyName[i], errors, "errmsg.company", true); ;
					  length++;
				}
				}
		 if(length == 0)
			 errors.rejectValue("companyName", "errmsg.required");
		 
		 //snmsVal.isValidDropDown("unitId",Integer.parseInt(unitId.toString()),errors);
		
	}

	public void validateFinanceDetails(@Valid FinancialMaster financialDetails, BindingResult errors) {
		SNMSValidator snmsVal = new SNMSValidator();
		snmsVal.isBlank("fromY", financialDetails.getFromY(),errors);
		snmsVal.isBlank("toY", financialDetails.getToY(),errors);
		snmsVal.isvalidFinanceYear("fromY", financialDetails.getFromY()+"-"+financialDetails.getToY(), errors, "errmsg.financeYear", true);
	}

	
	
	public void validateIOfile(@Valid CaseDetails caseDetails, BindingResult errors) throws IOException {
		 MultipartFile ioDeleteOrderFile[]=caseDetails.getDeleteIoOrder();
		 SNMSValidator snmsVal = new SNMSValidator();
		 if(caseDetails.getDeleteIoOrder() != null) {
				if(ioDeleteOrderFile.length >0) {
			 for(int j=0;j<ioDeleteOrderFile.length;j++) {
				 if(ioDeleteOrderFile[j].getSize() > 0 || !ioDeleteOrderFile[j].isEmpty())
				 {
					 snmsVal.isValidFile(ioDeleteOrderFile[j], errors, true,"mcaFile");
					 if(!snmsVal.isValidFileName(ioDeleteOrderFile[j].getOriginalFilename()))
							errors.rejectValue("deleteIOfile", "errmsg.filename");
					 if(!snmsVal.isValidFileTikka(ioDeleteOrderFile[j].getOriginalFilename(), ioDeleteOrderFile[j]))
							errors.rejectValue("deleteIOfile", "errmsg.maliciousdata");
				 }else{
					 errors.rejectValue("deleteIOfile", "errmsg.required");
				 }
				 }
				}
			}
	}
	
	public void validateIODetails(@Valid CaseDetails caseDetails, BindingResult errors) throws IOException {
		
		 String companyName[] = caseDetails.getCompanyName() ;
		 String inspectors[] = caseDetails.getInspectors();
		 
		 String chooseIo=caseDetails.getChooseIo();
		 String chooseAdo=caseDetails.getChooseAdo();
		 SNMSValidator snmsVal = new SNMSValidator();
		 MultipartFile ioOrderFile[]=caseDetails.getAddIoOrder();
		 MultipartFile ioDeleteOrderFile[]=caseDetails.getDeleteIoOrder();
			if(caseDetails.getAddIoOrder() != null) {
			if(ioOrderFile.length >0) {
		 for(int i=0;i<ioOrderFile.length;i++) {
		 if(ioOrderFile[i].getSize() > 0 || !ioOrderFile[i].isEmpty())
		 {
			 snmsVal.isValidFile(ioOrderFile[i], errors, true,"mcaFile");
			 if(!snmsVal.isValidFileName(ioOrderFile[i].getOriginalFilename()))
					errors.rejectValue("addIoOrder", "errmsg.filename");
			 if(!snmsVal.isValidFileTikka(ioOrderFile[i].getOriginalFilename(), ioOrderFile[i]))
					errors.rejectValue("addIoOrder", "errmsg.maliciousdata");
		 }else{
			 errors.rejectValue("addIoOrder", "errmsg.required");
		 }
		 }
		 
		 
	
			}
			}
			
			if(caseDetails.getDeleteIoOrder() != null) {
				if(ioDeleteOrderFile.length >0) {
			 for(int j=0;j<ioDeleteOrderFile.length;j++) {
				 if(ioDeleteOrderFile[j].getSize() > 0 || !ioDeleteOrderFile[j].isEmpty())
				 {
					 snmsVal.isValidFile(ioDeleteOrderFile[j], errors, true,"mcaFile");
					 if(!snmsVal.isValidFileName(ioDeleteOrderFile[j].getOriginalFilename()))
							errors.rejectValue("inspectors", "errmsg.filename");
					 if(!snmsVal.isValidFileTikka(ioDeleteOrderFile[j].getOriginalFilename(), ioDeleteOrderFile[j]))
							errors.rejectValue("inspectors", "errmsg.maliciousdata");
				 }else{
					 errors.rejectValue("inspectors", "errmsg.required");
				 }
				 }
				}
			}
		 int length = 0;
		
		 if(null!=inspectors && !inspectors.equals("")){
				for(int i = 0; i < inspectors.length ; i++){
					  snmsVal.isValidNumeric("inspectors", inspectors[i], errors, "errmsg.inspectors", true); ;
					  length++;
				}
				}
				/*
				 * if(length == 0) errors.rejectValue("inspectors", "errmsg.required");
				 */
		
		if(length == 0)
			 errors.rejectValue("inspectors", "errmsg.required");
		
		length = 0;
		 if(null!=companyName && !companyName.equals("")){
				for(int i = 0; i < companyName.length ; i++){
					  snmsVal.isvalidCompany("companyName", companyName[i], errors, "errmsg.company", true); ;
					  length++;
				}
				}
		 if(length == 0)
			 errors.rejectValue("companyName", "errmsg.required");
		 
		 snmsVal.isvalidRadio("chooseIo", chooseIo, errors);
		 snmsVal.isvalidRadio("chooseAdo", chooseAdo, errors);
		
	}

	public void validLinkofficer(@Valid Linkofficer linkofficer, BindingResult errors) {
		
		
		  Date fromDate = linkofficer.getFromDate(); 
		  Date toDate =   linkofficer.getToDate();
		  SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		  SNMSValidator snmsVal = new SNMSValidator(); 
		  if (fromDate != null && !fromDate.equals(""))
		  { 
			  boolean isValid = snmsVal.validateDateFormat(sdf.format(fromDate)); 
			  if (!isValid) {
		  errors.rejectValue("fromDate", "errmsg.toDate"); 
		  } 
		}else if (fromDate ==null || fromDate.equals("")) { 
			errors.rejectValue("fromDate",   "errmsg.required"); }
		  
		  if (toDate != null && !toDate.equals("")) { 
			  boolean isValid = snmsVal.validateDateFormat(sdf.format(toDate)); 
			  if (!isValid) {
				  errors.rejectValue("toDate", "errmsg.toDate"); 
				  } } 
		  else if (toDate == null || toDate.equals("")) {
			  errors.rejectValue("toDate", "errmsg.required"); }
		 
	}

	public void validateCompanyDetails(@Valid CaseDetails caseDetails, BindingResult errors) {
		 String companyName[] = caseDetails.getCompanyName() ;
		 SNMSValidator snmsVal = new SNMSValidator();
		 int length = 0;
		 if(null!=companyName && !companyName.equals("")){
				for(int i = 0; i < companyName.length ; i++){
					  snmsVal.isvalidCompany("companyName", companyName[i], errors, "errmsg.company", true); ;
					  length++;
				}
				}
		 if(length == 0)
			 errors.rejectValue("companyName", "errmsg.required");
	}

	public void validatecaseDetails(@Valid CaseDetails caseDetails, BindingResult errors) {
		 String caseId = caseDetails.getCaseId();
		 String remark = caseDetails.getRemark();
		 String caseTitle = caseDetails.getCaseTitle();
		 
		 SNMSValidator snmsVal = new SNMSValidator();
		 snmsVal.isvalidCaseId("caseId", caseId, errors, true);
		 snmsVal.isvalidName("caseTitle", caseTitle, errors, "errmsg.casetitle", true);
		
	}



}
	/*public void validateCaseDetails(@Valid CaseDetails caseDetails, BindingResult errors) throws JSONException {
		 String caseId = caseDetails.getCaseId();
		 String radioValue = caseDetails.getRadioValue();
		 String caseTitle = caseDetails.getCaseTitle();
		 String mcaOrderNo = caseDetails.getMcaOrderNo();
		 String mcaDate = caseDetails.getMcaDate();
		 String courtOrderNo = caseDetails.getCourtOrderNo();
		 String courtDate = caseDetails.getCourtDate();
//		 Date mcaOrderDate;
//		 Date courtOrderDate;
		 Long unitId = caseDetails.getUnitId();
		 ArrayList<Object> companies = caseDetails.getCompanies();
		 ArrayList<Object> inspectorList = caseDetails.getInspectorList();
//		 String mcaOrderFile;
//		 String courtOrderFile;
		 
		 SNMSValidator snmsVal = new SNMSValidator();
		 snmsVal.isvalidCaseId("caseId", caseId, errors, true);
		 snmsVal.isvalidName("caseTitle", caseTitle, errors, "errmsg.casetitle", true);
		 if(!radioValue.equals("MCA") && !radioValue.equals("Court") && !radioValue.equals("Both")){
			 errors.reject("radioValue", "errmsg.radioValue");
		 }else if(radioValue.equals("MCA") || radioValue.equals("BOTH")){
			 snmsVal.isvalidName("mcaOrderNo", mcaOrderNo, errors, "errmsg.mcaorderno", true);
			 if (mcaDate != null && !mcaDate.equals("")) {
					boolean isValid = snmsVal.validateDateFormat(mcaDate);
					if (!isValid) {
						errors.rejectValue("mcaDate", "errmsg.toDate");
					}
				} else if (mcaDate == null || mcaDate.equals("")) {
					errors.rejectValue("mcaDate", "errmsg.required");
				}	
		 }
		 else if(radioValue.equals("Court") || radioValue.equals("BOTH")){
			 snmsVal.isvalidName("courtOrderNo", courtOrderNo, errors, "errmsg.courtOrderNo", true);
			 if (courtDate != null && !courtDate.equals("")) {
					boolean isValid = snmsVal.validateDateFormat(courtDate);
					if (!isValid) {
						errors.rejectValue("courtDate", "errmsg.toDate");
					}
				} else if (courtDate == null || courtDate.equals("")) {
					errors.rejectValue("courtDate", "errmsg.required");
				}	
		 }
		 JSONArray companyArray = new JSONArray(companies);

			List<Company> companyList = new ArrayList<Company>();
			int length = 0; 
			for (int i = 0; i < companyArray.length(); i++) {
				length++;
				JSONObject object = companyArray.getJSONObject(i);
//				Company company = new Company(caseDetails, object.getString("Company Name"));
				snmsVal.isvalidCompany("companyName", object.getString("Company Name"), errors, "errmsg.companyName", false);
			}
			if(length==0){
				errors.rejectValue("companyName", "errmsg.required");
			}
			 
	}*/


