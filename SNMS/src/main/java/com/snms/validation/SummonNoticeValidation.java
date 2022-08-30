package com.snms.validation;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Column;

import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.multipart.MultipartFile;

import com.snms.entity.NoticeStatus;
import com.snms.entity.SummonDetails;
import com.snms.entity.SummonStatus;
import com.snms.utils.Utils;

public class SummonNoticeValidation {

	public void validateCompleteCompany(SummonDetails summonDetails, Errors errors) {
		String name = summonDetails.getCompanySummonsDto().getCompanyName();
		String cin = summonDetails.getCompanySummonsDto().getCin();
		String address = summonDetails.getCompanySummonsDto().getAddress();
		String email = summonDetails.getCompanySummonsDto().getEmail();
		
		int compType = summonDetails.getCompanySummonsDto().getCompanyType().getId();
		
		SfioValidator sfioValidator = new SfioValidator();
		sfioValidator.isValidDropDown("companySummonsDto.companyName", name, errors);
		//sfioValidator.isvalidCIN("companySummonsDto.cin", cin, errors, true);
		sfioValidator.isvalidUserAddress("companySummonsDto.address", address, errors, "errmsg.address", true);
		sfioValidator.isValidEmail("companySummonsDto.email", email, errors);
		if(compType == 3) {
			sfioValidator.isvalidLLPIN("companydetDto.cin", cin, errors, true);
		 }else {
			 sfioValidator.isvalidCIN("companydetDto.cin", cin, errors, true);
		 }
	}

	public void validateCompleteCompanysummon(SummonDetails summonDetails, Errors errors) {
		String name = summonDetails.getCompanySummonsDto().getCompanyName();
		String cin = summonDetails.getCompanySummonsDto().getCin();
		String address = summonDetails.getCompanySummonsDto().getAddress();
		String email = summonDetails.getCompanySummonsDto().getEmail();
		
		//int compType = summonDetails.getCompanySummonsDto().getCompanyType().getId();
		
		SfioValidator sfioValidator = new SfioValidator();
		sfioValidator.isValidDropDown("companySummonsDto.companyName", name, errors);
		//sfioValidator.isvalidCIN("companySummonsDto.cin", cin, errors, true);
		sfioValidator.isvalidUserAddress("companySummonsDto.address", address, errors, "errmsg.address", true);
		sfioValidator.isValidEmail("companySummonsDto.email", email, errors);
		
		sfioValidator.isvalidCIN("companySummonsDto.cin", cin, errors, true);
		 
	}

	public void validateIndivialType(SummonDetails summonDetails, Errors errors) {

		String companyName = summonDetails.getCompanySummonsDto().getCompanyName();
		Long type = summonDetails.getSummonTypenewDto().getIndividualId();

		// String type=summonDetails.getSummonTypenewDto().getType();

		// Director fields
		String regNo = summonDetails.getSummonTypenewDto().getDirReg_no();
		String auditorName = summonDetails.getSummonTypenewDto().getDirName();
		String email = summonDetails.getSummonTypenewDto().getDirEmail();
		String phone = summonDetails.getSummonTypenewDto().getDirMobile();
		String address = summonDetails.getSummonTypenewDto().getDirAddr();
		String dirPan = summonDetails.getSummonTypenewDto().getDirpanNumber();
		String dirPassport = summonDetails.getSummonTypenewDto().getDirpassport();
		String diraadhar = summonDetails.getSummonTypenewDto().getDiraadharNumber();

		Date dirofficedate = summonDetails.getSummonTypenewDto().getDiroffJoinDate();

		// formal Director Fields
		String fregNo = summonDetails.getSummonTypenewDto().getFdirReg_no();
		String fauditorName = summonDetails.getSummonTypenewDto().getFdirName();
		String femail = summonDetails.getSummonTypenewDto().getFdirEmail();
		String fphone = summonDetails.getSummonTypenewDto().getFdirMobile();
		String faddress = summonDetails.getSummonTypenewDto().getFdirAddr();

		String fdirPan = summonDetails.getSummonTypenewDto().getFdirpanNumber();
		String fdirPassport = summonDetails.getSummonTypenewDto().getFdirpassport();
		String fdiraadhar = summonDetails.getSummonTypenewDto().getFdiraadharNumber();

		Date fdirofficedate = summonDetails.getSummonTypenewDto().getFdiroffJoinDate();

		// Employeee
		String name = summonDetails.getSummonTypenewDto().getEmpName();
		String designation = summonDetails.getSummonTypenewDto().getEmpdesgi();
		String sex = summonDetails.getSummonTypenewDto().getEmpsex();
		Date dob = summonDetails.getSummonTypenewDto().getEmpDob();
		String nationality = summonDetails.getSummonTypenewDto().getEmpNation();
		String nationalId = summonDetails.getSummonTypenewDto().getEmpnatid();
		String passport = summonDetails.getSummonTypenewDto().getEmpPassport();
		Date issueDate = summonDetails.getSummonTypenewDto().getEmpPassDate();
		String placeofIssues = summonDetails.getSummonTypenewDto().getEmpissuplace();
		String Empmobile = summonDetails.getSummonTypenewDto().getEmpMobile();
		String Empaddress = summonDetails.getSummonTypenewDto().getEmpAddress();
		Date empJoining = summonDetails.getSummonTypenewDto().getEmpoffdate();
		String EmpEmail = summonDetails.getSummonTypenewDto().getEmail();
		String empaddhar = summonDetails.getSummonTypenewDto().getEmpaadharNumber();

		String empPan = summonDetails.getSummonTypenewDto().getEmpPanNumber();

		// femployee

		String fname = summonDetails.getSummonTypenewDto().getFempName();
		String fdesignation = summonDetails.getSummonTypenewDto().getFempdesgi();
		String fsex = summonDetails.getSummonTypenewDto().getFempsex();
		Date fdob = summonDetails.getSummonTypenewDto().getFempDob();
		String fnationality = summonDetails.getSummonTypenewDto().getFempNation();
		String fnationalId = summonDetails.getSummonTypenewDto().getFempnatid();
		String fpassport = summonDetails.getSummonTypenewDto().getFempPassport();
		Date fissueDate = summonDetails.getSummonTypenewDto().getFempPassDate();
		String fplaceofIssues = summonDetails.getSummonTypenewDto().getFempissuplace();
		String fEmpmobile = summonDetails.getSummonTypenewDto().getFempMobile();
		String fEmpaddress = summonDetails.getSummonTypenewDto().getFempAddress();
		Date fempJoining = summonDetails.getSummonTypenewDto().getFempoffdate();
		String fEmpEmail = summonDetails.getSummonTypenewDto().getFempemail();
		String fempaadhar = summonDetails.getSummonTypenewDto().getFempaadharNumber();

		String fempPan = summonDetails.getSummonTypenewDto().getFempPanNumber();

		// Agent

		String memberShipNum = summonDetails.getSummonTypenewDto().getAgentRegno();
		String memberName = summonDetails.getSummonTypenewDto().getAgentName();
		String memberEmail = summonDetails.getSummonTypenewDto().getAgentEmail();
		String memberPhone = summonDetails.getSummonTypenewDto().getAgentMobile();
		String memberAddress = summonDetails.getSummonTypenewDto().getAgentAddress();
		String membercompany = summonDetails.getSummonTypenewDto().getAgentcompany();
		String AgentCin = summonDetails.getSummonTypenewDto().getIsACin().replaceAll(",", "");
		String agentPan = summonDetails.getSummonTypenewDto().getAgentpanNumber();
		String agentPassport = summonDetails.getSummonTypenewDto().getAgentpassport();
		String agentAadhar = summonDetails.getSummonTypenewDto().getAgentaadharNumber();

		// Others

		String isCin = summonDetails.getSummonTypenewDto().getIsOCin().replaceAll(",", "");
		String cinNum = summonDetails.getSummonTypenewDto().getOthRegno();
		String vendorName = summonDetails.getSummonTypenewDto().getOthName();
		String vendorRelation = summonDetails.getSummonTypenewDto().getOthRelation();
		String vendorEmail = summonDetails.getSummonTypenewDto().getOthEmail();
		String vendorPhone = summonDetails.getSummonTypenewDto().getOthMobile();
		String vendorAddress = summonDetails.getSummonTypenewDto().getOthAddress();
		String othPan = summonDetails.getSummonTypenewDto().getOthpanNumber();
		String othPassport = summonDetails.getSummonTypenewDto().getOthpassport();
		String othAadhar = summonDetails.getSummonTypenewDto().getOthaadharNumber();

		String cin = summonDetails.getCompanySummonsDto().getCin();
		String compaddress = summonDetails.getCompanySummonsDto().getAddress();
		String compemail = summonDetails.getCompanySummonsDto().getEmail();

		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

		SfioValidator sfioValidator = new SfioValidator();

		sfioValidator.isValidDropDown("companySummonsDto.companyName", companyName, errors);
		sfioValidator.isValidDropDown1("summonTypenewDto.IndividualId", type, errors);
		// sfioValidator.isvalidRadio("summonTypenewDto.type", type, errors);

		sfioValidator.isvalidCIN("companySummonsDto.cin", cin, errors, true);
		sfioValidator.isvalidUserAddress("companySummonsDto.address", compaddress, errors, "errmsg.address", true);

		if (!compemail.trim().equalsIgnoreCase(""))
			sfioValidator.isValidEmail("companySummonsDto.email", compemail, errors);

		if (type == 1) {
			sfioValidator.isvalidFirmReg("summonTypenewDto.dirReg_no", regNo, errors);
			sfioValidator.isvalidFirmName("summonTypenewDto.dirName", auditorName, errors, "errmsg.firmName", true);
			sfioValidator.isvalidUserAddress("summonTypenewDto.dirAddr", address, errors, "errmsg.address", true);
			if (email != null && !email.equals("")) {
				sfioValidator.isValidEmail("summonTypenewDto.dirEmail", email, errors, false);
			}
			if (phone != null && !phone.equals("")) {
				sfioValidator.isValidFirmPhone("summonTypenewDto.dirMobile", phone, errors);
			}
			if (dirofficedate != null && !dirofficedate.equals("")) {
				boolean isValid = sfioValidator.validateDateFormat(sdf.format(dirofficedate));
				if (!isValid) {
					errors.rejectValue("summonTypenewDto.diroffJoinDate", "errmsg.dob");
				}
			}
			sfioValidator.isValidpanNumber("summonTypenewDto.empPanNumber", dirPan, errors, "errmsg.pan", false);
			
			if (!sfioValidator.isBlank("summonTypenewDto.dirpassport", dirPassport)) {
				sfioValidator.isValidPassport("summonTypenewDto.dirpassport", dirPassport, errors);
			}
		} else if (type == 2) {
			sfioValidator.isvalidFirmReg("summonTypenewDto.fdirReg_no", fregNo, errors);
			sfioValidator.isvalidFirmName("summonTypenewDto.fdirName", fauditorName, errors, "errmsg.firmName", true);
			sfioValidator.isvalidUserAddress("summonTypenewDto.fdirAddr", faddress, errors, "errmsg.address", true);
			if (femail != null && !femail.equals("")) {
				sfioValidator.isValidEmail("summonTypenewDto.fdirEmail", femail, errors, false);
			}
			if (fphone != null && !fphone.equals("")) {
				sfioValidator.isValidFirmPhone("summonTypenewDto.fdirMobile", fphone, errors);
			}
			if (fdirofficedate != null && !fdirofficedate.equals("")) {
				boolean isValid = sfioValidator.validateDateFormat(sdf.format(fdirofficedate));
				if (!isValid) {
					errors.rejectValue("summonTypenewDto.fdiroffJoinDate", "errmsg.dob");
				}
			}
			
			sfioValidator.isValidpanNumber("summonTypenewDto.fdirpanNumber", fdirPan, errors, "errmsg.pan", false);
		
				
			if (!sfioValidator.isBlank("summonTypenewDto.fdirpassport", fdirPassport)) {
				sfioValidator.isValidPassport("summonTypenewDto.fdirpassport", fdirPassport, errors);
			}
		} else if (type == 3) {
			sfioValidator.isvalidName("summonTypenewDto.empName", name, errors, "errmsg.fname", true);
			sfioValidator.isvalidName("summonTypenewDto.empdesgi", designation, errors, "errmsg.fname", true);
			sfioValidator.isvalidRadio("summonTypenewDto.empsex", sex, errors);
			if (dob != null && !dob.equals("")) {
				boolean isValid = sfioValidator.validateDateOfBirthFormat(sdf.format(dob));
				if (!isValid) {
					errors.rejectValue("summonTypenewDto.empDob", "errmsg.dob");
				}

			}
			if (nationality != null && !nationality.equals("")) {
				sfioValidator.isvalidName("summonTypenewDto.empNation", nationality, errors, "errmsg.fname", true);
			}
			if (!sfioValidator.isBlank("summonTypenewDto.empnatid", nationalId))
				sfioValidator.isValidNationalId("summonTypenewDto.empnatid", nationalId, errors);

			if (!sfioValidator.isBlank("summonTypenewDto.empPassport", passport)) {
				sfioValidator.isValidPassport("summonTypenewDto.empPassport", passport, errors);
				if (issueDate != null && !issueDate.equals("")) {
					boolean isValid = sfioValidator.validateDateFormat(sdf.format(issueDate));
					if (!isValid) {
						errors.rejectValue("summonTypenewDto.empPassDate", "errmsg.dob");
					}
				}
				sfioValidator.isvalidPlace("summonTypenewDto.empissuplace", placeofIssues, errors, "errmsg.place",
						true);
			}
			if (Empmobile != null && !Empmobile.equals("")) {
				sfioValidator.isValidMobile("summonTypenewDto.empMobile", Empmobile, errors);
			}
			sfioValidator.isvalidUserAddress("summonTypenewDto.empAddress", Empaddress, errors, "errmsg.address", true);

			if (EmpEmail != null && !EmpEmail.equals("")) {
				sfioValidator.isValidEmail("summonTypenewDto.empemail", EmpEmail, errors);
			}

		}

		else if (type == 4) {
			sfioValidator.isvalidName("summonTypenewDto.fempName", fname, errors, "errmsg.fname", true);
			sfioValidator.isvalidName("summonTypenewDto.fempdesgi", fdesignation, errors, "errmsg.fname", true);
			sfioValidator.isvalidRadio("summonTypenewDto.fempsex", fsex, errors);
			if (fdob != null && !fdob.equals("")) {
				boolean isValid = sfioValidator.validateDateOfBirthFormat(sdf.format(fdob));
				if (!isValid) {
					errors.rejectValue("summonTypenewDto.fempDob", "errmsg.dob");
				}

			}
			if (fnationality != null && !fnationality.equals("")) {
				sfioValidator.isvalidName("summonTypenewDto.fempNation", fnationality, errors, "errmsg.fname", true);
			}
			if (!sfioValidator.isBlank("summonTypenewDto.fempnatid", fnationalId))
				sfioValidator.isValidNationalId("summonTypenewDto.fempnatid", fnationalId, errors);

			if (!sfioValidator.isBlank("summonTypenewDto.empfPassport", fpassport)) {
				sfioValidator.isValidPassport("summonTypenewDto.empfPassport", fpassport, errors);
				if (fissueDate != null && !fissueDate.equals("")) {
					boolean isValid = sfioValidator.validateDateFormat(sdf.format(fissueDate));
					if (!isValid) {
						errors.rejectValue("summonTypenewDto.fempPassDate", "errmsg.dob");
					}
				}
				sfioValidator.isvalidPlace("summonTypenewDto.fempissuplace", fplaceofIssues, errors, "errmsg.place",
						true);
			}
			if (fEmpmobile != null && !fEmpmobile.equals("")) {
				sfioValidator.isValidMobile("summonTypenewDto.fempMobile", fEmpmobile, errors);
			}
			sfioValidator.isvalidUserAddress("summonTypenewDto.fempAddress", fEmpaddress, errors, "errmsg.address",
					true);

			if (fEmpEmail != null && !fEmpEmail.equals("")) {
				sfioValidator.isValidEmail("summonTypenewDto.fempemail", fEmpEmail, errors);
				

				sfioValidator.isValidpanNumber("summonTypenewDto.fempPanNumber", fempPan, errors, "errmsg.pan", false);

			}
		} else if (type == 5 || type == 6) {

			sfioValidator.isvalidRadio("summonTypenewDto.isACin", AgentCin, errors);
			if (isCin.equalsIgnoreCase("Y")) {
				sfioValidator.isvalidCIN("summonTypenewDto.agentRegno", memberShipNum, errors, true);
			}
			sfioValidator.isvalidFirmName("summonTypenewDto.agentcompany", membercompany, errors, "errmsg.firmName",
					true);
			sfioValidator.isvalidUserAddress("summonTypenewDto.agentAddress", memberAddress, errors, "errmsg.address",
					true);
			if (memberEmail != null && !memberEmail.equals("")) {
				sfioValidator.isValidEmail("summonTypenewDto.agentEmail", memberEmail, errors);
			}
			if (memberPhone != null && !memberPhone.equals("")) {
				sfioValidator.isValidFirmPhone("summonTypenewDto.agentMobile", memberPhone, errors);
			}
			sfioValidator.isvalidName("summonTypenewDto.agentName", memberName, errors, "errmsg.fname", true);

			sfioValidator.isValidpanNumber("summonTypenewDto.agentpanNumber", agentPan, errors, "errmsg.pan", false);

			
			if (!sfioValidator.isBlank("summonTypenewDto.agentpassport", agentPassport)) {
				sfioValidator.isValidPassport("summonTypenewDto.agentpassport", agentPassport, errors);
			}
			
		}

		else {

			sfioValidator.isvalidRadio("summonTypenewDto.isOCin", isCin, errors);
			if (isCin.equalsIgnoreCase("Y"))
				sfioValidator.isvalidCIN("summonTypenewDto.othRegno", cinNum, errors, true);
			sfioValidator.isvalidFirmName("summonTypenewDto.othName", vendorName, errors, "errmsg.company", true);
			sfioValidator.isvalidName("summonTypenewDto.othRelation", vendorRelation, errors, "errmsg.fname", true);
			sfioValidator.isvalidUserAddress("summonTypenewDto.othAddress", vendorAddress, errors, "errmsg.address",
					true);
			if (vendorEmail != null && !vendorEmail.equals("")) {
				sfioValidator.isValidEmail("summonTypenewDto.othEmail", vendorEmail, errors);
			}
			if (vendorPhone != null && !vendorPhone.equals("")) {
				sfioValidator.isValidMobile("summonTypenewDto.othMobile", vendorPhone, errors);
			}

			
			sfioValidator.isValidpanNumber("summonTypenewDto.othpanNumber", othPan, errors, "errmsg.pan", false);
		
			if (!sfioValidator.isBlank("summonTypenewDto.othpassport", othPassport)) {
				sfioValidator.isValidPassport("summonTypenewDto.othpassport", othPassport, errors);
			}
		}

	}

	public void validateIndivialTypeOnly(SummonDetails summonDetails, Errors errors) {
		// String companyName=summonDetails.getCompanySummonsDto().getCompanyName();
		Long type = summonDetails.getSummonTypenewDto().getIndividualId();

//	String type=summonDetails.getSummonTypenewDto().getType();

		// Director fields
		String regNo = summonDetails.getSummonTypenewDto().getDirReg_no();
		String auditorName = summonDetails.getSummonTypenewDto().getDirName();
		String email = summonDetails.getSummonTypenewDto().getDirEmail();
		String phone = summonDetails.getSummonTypenewDto().getDirMobile();
		String address = summonDetails.getSummonTypenewDto().getDirAddr();
		Date dirofficedate = summonDetails.getSummonTypenewDto().getDiroffJoinDate();

		String dirPan = summonDetails.getSummonTypenewDto().getDirpanNumber();
		String dirPassport = summonDetails.getSummonTypenewDto().getDirpassport();
		String diraadhar = summonDetails.getSummonTypenewDto().getDiraadharNumber();
		// formal Director Fields
		String fregNo = summonDetails.getSummonTypenewDto().getFdirReg_no();
		String fauditorName = summonDetails.getSummonTypenewDto().getFdirName();
		String femail = summonDetails.getSummonTypenewDto().getFdirEmail();
		String fphone = summonDetails.getSummonTypenewDto().getFdirMobile();
		String faddress = summonDetails.getSummonTypenewDto().getFdirAddr();
		Date fdirofficedate = summonDetails.getSummonTypenewDto().getFdiroffJoinDate();
		String fdirPan = summonDetails.getSummonTypenewDto().getFdirpanNumber();
		String fdirPassport = summonDetails.getSummonTypenewDto().getFdirpassport();
		String fdiraadhar = summonDetails.getSummonTypenewDto().getFdiraadharNumber();
		// Employeee
		String name = summonDetails.getSummonTypenewDto().getEmpName();
		String designation = summonDetails.getSummonTypenewDto().getEmpdesgi();
		String sex = summonDetails.getSummonTypenewDto().getEmpsex();
		Date dob = summonDetails.getSummonTypenewDto().getEmpDob();
		String nationality = summonDetails.getSummonTypenewDto().getEmpNation();
		String nationalId = summonDetails.getSummonTypenewDto().getEmpnatid();
		String passport = summonDetails.getSummonTypenewDto().getEmpPassport();
		Date issueDate = summonDetails.getSummonTypenewDto().getEmpPassDate();
		String placeofIssues = summonDetails.getSummonTypenewDto().getEmpissuplace();
		String Empmobile = summonDetails.getSummonTypenewDto().getEmpMobile();
		String Empaddress = summonDetails.getSummonTypenewDto().getEmpAddress();
		Date empJoining = summonDetails.getSummonTypenewDto().getEmpoffdate();
		String EmpEmail = summonDetails.getSummonTypenewDto().getEmail();
		String empaddhar = summonDetails.getSummonTypenewDto().getEmpaadharNumber();

		String empPan = summonDetails.getSummonTypenewDto().getEmpPanNumber();

		// femployee

		String fname = summonDetails.getSummonTypenewDto().getFempName();
		String fdesignation = summonDetails.getSummonTypenewDto().getFempdesgi();
		String fsex = summonDetails.getSummonTypenewDto().getFempsex();
		Date fdob = summonDetails.getSummonTypenewDto().getFempDob();
		String fnationality = summonDetails.getSummonTypenewDto().getFempNation();
		String fnationalId = summonDetails.getSummonTypenewDto().getFempnatid();
		String fpassport = summonDetails.getSummonTypenewDto().getFempPassport();
		Date fissueDate = summonDetails.getSummonTypenewDto().getFempPassDate();
		String fplaceofIssues = summonDetails.getSummonTypenewDto().getFempissuplace();
		String fEmpmobile = summonDetails.getSummonTypenewDto().getFempMobile();
		String fEmpaddress = summonDetails.getSummonTypenewDto().getFempAddress();
		Date fempJoining = summonDetails.getSummonTypenewDto().getFempoffdate();
		String fEmpEmail = summonDetails.getSummonTypenewDto().getFempemail();
		String fempaadhar = summonDetails.getSummonTypenewDto().getFempaadharNumber();

		String fempPan = summonDetails.getSummonTypenewDto().getFempPanNumber();

		// Agent

		String memberShipNum = summonDetails.getSummonTypenewDto().getAgentRegno();
		String memberName = summonDetails.getSummonTypenewDto().getAgentName();
		String memberEmail = summonDetails.getSummonTypenewDto().getAgentEmail();
		String memberPhone = summonDetails.getSummonTypenewDto().getAgentMobile();
		String memberAddress = summonDetails.getSummonTypenewDto().getAgentAddress();
		String membercompany = summonDetails.getSummonTypenewDto().getAgentcompany();
		String agentPan = summonDetails.getSummonTypenewDto().getAgentpanNumber();
		String agentPassport = summonDetails.getSummonTypenewDto().getAgentpassport();
		String agentAadhar = summonDetails.getSummonTypenewDto().getAgentaadharNumber();

		String AgentCin = "";
		if (summonDetails.getSummonTypenewDto().getIsACin() != null
				&& !summonDetails.getSummonTypenewDto().getIsACin().equals("")) {

			AgentCin = summonDetails.getSummonTypenewDto().getIsACin().replaceAll(",", "");
		}

		// Others

		String isCin = summonDetails.getSummonTypenewDto().getIsOCin();
		String cinNum = summonDetails.getSummonTypenewDto().getOthRegno();
		String vendorName = summonDetails.getSummonTypenewDto().getOthName();
		String vendorRelation = summonDetails.getSummonTypenewDto().getOthRelation();
		String vendorEmail = summonDetails.getSummonTypenewDto().getOthEmail();
		String vendorPhone = summonDetails.getSummonTypenewDto().getOthMobile();
		String vendorAddress = summonDetails.getSummonTypenewDto().getOthAddress();
		String othPan = summonDetails.getSummonTypenewDto().getOthpanNumber();
		String othPassport = summonDetails.getSummonTypenewDto().getOthpassport();
		String othAadhar = summonDetails.getSummonTypenewDto().getOthaadharNumber();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

		SfioValidator sfioValidator = new SfioValidator();

		/*
		 * if(summonDetails.isEdit() == false) {
		 * 
		 * sfioValidator.isValidDropDown("companySummonsDto.companyName", companyName,
		 * errors); } sfioValidator.isValidDropDown1("summonTypenewDto.IndividualId",
		 * type, errors); // sfioValidator.isvalidRadio("summonTypenewDto.type", type,
		 * errors);
		 */
		if (type == 1) {
			sfioValidator.isvalidFirmReg("summonTypenewDto.dirReg_no", regNo, errors);
			sfioValidator.isvalidFirmName("summonTypenewDto.dirName", auditorName, errors, "errmsg.firmName", true);
			sfioValidator.isvalidUserAddress("summonTypenewDto.dirAddr", address, errors, "errmsg.address", true);
			if (email != null && !email.equals("")) {
				sfioValidator.isValidEmail("summonTypenewDto.dirEmail", email, errors, false);
			}
			if (phone != null && !phone.equals("")) {
				sfioValidator.isValidFirmPhone("summonTypenewDto.dirMobile", phone, errors);
			}
			if (dirofficedate != null && !dirofficedate.equals("")) {
				boolean isValid = sfioValidator.validateDateFormat(sdf.format(dirofficedate));
				if (!isValid) {
					errors.rejectValue("summonTypenewDto.diroffJoinDate", "errmsg.dob");
				}
			}

			sfioValidator.isValidpanNumber("summonTypenewDto.empPanNumber", dirPan, errors, "errmsg.pan", false);
		//	sfioValidator.isValidaadhar("summonTypenewDto.diraadharNumber", diraadhar, errors, "errmsg.aadhar", false);
			if (!sfioValidator.isBlank("summonTypenewDto.dirpassport", dirPassport)) {
				sfioValidator.isValidPassport("summonTypenewDto.dirpassport", dirPassport, errors);
			}
		} else if (type == 2) {
			sfioValidator.isvalidFirmReg("summonTypenewDto.fdirReg_no", fregNo, errors);
			sfioValidator.isvalidFirmName("summonTypenewDto.fdirName", fauditorName, errors, "errmsg.firmName", true);
			sfioValidator.isvalidUserAddress("summonTypenewDto.fdirAddr", faddress, errors, "errmsg.address", true);
			if (femail != null && !femail.equals("")) {
				sfioValidator.isValidEmail("summonTypenewDto.fdirEmail", femail, errors, false);
			}
			if (fphone != null && !fphone.equals("")) {
				sfioValidator.isValidFirmPhone("summonTypenewDto.fdirMobile", fphone, errors);
			}
			if (fdirofficedate != null && !fdirofficedate.equals("")) {
				boolean isValid = sfioValidator.validateDateFormat(sdf.format(fdirofficedate));
				if (!isValid) {
					errors.rejectValue("summonTypenewDto.fdiroffJoinDate", "errmsg.dob");
				}
			}

			sfioValidator.isValidpanNumber("summonTypenewDto.fdirpanNumber", fdirPan, errors, "errmsg.pan", false);
		//	sfioValidator.isValidaadhar("summonTypenewDto.fdiraadharNumber", fdiraadhar, errors, "errmsg.aadhar",
//					false);
			if (!sfioValidator.isBlank("summonTypenewDto.fdirpassport", fdirPassport)) {
				sfioValidator.isValidPassport("summonTypenewDto.fdirpassport", fdirPassport, errors);
			}
		} else if (type == 3) {
			sfioValidator.isvalidName("summonTypenewDto.empName", name, errors, "errmsg.fname", true);
			sfioValidator.isvalidName("summonTypenewDto.empdesgi", designation, errors, "errmsg.fname", true);
			sfioValidator.isvalidRadio("summonTypenewDto.empsex", sex, errors);
			if (dob != null && !dob.equals("")) {
				boolean isValid = sfioValidator.validateDateOfBirthFormat(sdf.format(dob));
				if (!isValid) {
					errors.rejectValue("summonTypenewDto.empDob", "errmsg.dob");
				}

			}
			if (nationality != null && !nationality.equals("")) {
				sfioValidator.isvalidName("summonTypenewDto.empNation", nationality, errors, "errmsg.fname", true);
			}
			if (!sfioValidator.isBlank("summonTypenewDto.empnatid", nationalId))
				sfioValidator.isValidNationalId("summonTypenewDto.empnatid", nationalId, errors);

			if (!sfioValidator.isBlank("summonTypenewDto.empPassport", passport)) {
				sfioValidator.isValidPassport("summonTypenewDto.empPassport", passport, errors);
				if (issueDate != null && !issueDate.equals("")) {
					boolean isValid = sfioValidator.validateDateFormat(sdf.format(issueDate));
					if (!isValid) {
						errors.rejectValue("summonTypenewDto.empPassDate", "errmsg.dob");
					}
				}
				sfioValidator.isvalidPlace("summonTypenewDto.empissuplace", placeofIssues, errors, "errmsg.place",
						true);
			}
			if (Empmobile != null && !Empmobile.equals("")) {
				sfioValidator.isValidMobile("summonTypenewDto.empMobile", Empmobile, errors);
			}
			sfioValidator.isvalidUserAddress("summonTypenewDto.empAddress", Empaddress, errors, "errmsg.address", true);

			if (EmpEmail != null && !EmpEmail.equals("")) {
				sfioValidator.isValidEmail("summonTypenewDto.empemail", EmpEmail, errors);
			}
			//sfioValidator.isValidaadhar("summonTypenewDto.empaadharNumber", empaddhar, errors, "errmsg.aadhar", false);
			sfioValidator.isValidpanNumber("summonTypenewDto.empPanNumber", empPan, errors, "errmsg.pan", false);
		}

		else if (type == 4) {
			sfioValidator.isvalidName("summonTypenewDto.fempName", fname, errors, "errmsg.fname", true);
			sfioValidator.isvalidName("summonTypenewDto.fempdesgi", fdesignation, errors, "errmsg.fname", true);
			sfioValidator.isvalidRadio("summonTypenewDto.fempsex", fsex, errors);
			if (fdob != null && !fdob.equals("")) {
				boolean isValid = sfioValidator.validateDateOfBirthFormat(sdf.format(fdob));
				if (!isValid) {
					errors.rejectValue("summonTypenewDto.fempDob", "errmsg.dob");
				}

			}
			if (fnationality != null && !fnationality.equals("")) {
				sfioValidator.isvalidName("summonTypenewDto.fempNation", fnationality, errors, "errmsg.fname", true);
			}
			if (!sfioValidator.isBlank("summonTypenewDto.fempnatid", fnationalId))
				sfioValidator.isValidNationalId("summonTypenewDto.fempnatid", fnationalId, errors);

			if (!sfioValidator.isBlank("summonTypenewDto.empfPassport", fpassport)) {
				sfioValidator.isValidPassport("summonTypenewDto.empfPassport", fpassport, errors);
				if (fissueDate != null && !fissueDate.equals("")) {
					boolean isValid = sfioValidator.validateDateFormat(sdf.format(fissueDate));
					if (!isValid) {
						errors.rejectValue("summonTypenewDto.fempPassDate", "errmsg.dob");
					}
				}
				sfioValidator.isvalidPlace("summonTypenewDto.fempissuplace", fplaceofIssues, errors, "errmsg.place",
						true);
			}
			if (fEmpmobile != null && !fEmpmobile.equals("")) {
				sfioValidator.isValidMobile("summonTypenewDto.fempMobile", fEmpmobile, errors);
			}
			sfioValidator.isvalidUserAddress("summonTypenewDto.fempAddress", fEmpaddress, errors, "errmsg.address",
					true);

			if (fEmpEmail != null && !fEmpEmail.equals("")) {
				sfioValidator.isValidEmail("summonTypenewDto.fempemail", fEmpEmail, errors);
			}
			sfioValidator.isValidpanNumber("summonTypenewDto.fempPanNumber", fempPan, errors, "errmsg.pan", false);

			//sfioValidator.isValidaadhar("summonTypenewDto.fempaadharNumber", fempaadhar, errors, "errmsg.aadhar",
			//		false);

		} else if (type == 5 || type == 6) {

			sfioValidator.isvalidRadio("summonTypenewDto.isACin", AgentCin, errors);
			if (AgentCin.equalsIgnoreCase("Y")) {
				sfioValidator.isvalidCIN("summonTypenewDto.agentRegno", memberShipNum, errors, true);
			}
			sfioValidator.isvalidFirmName("summonTypenewDto.agentcompany", membercompany, errors, "errmsg.firmName",
					true);
			sfioValidator.isvalidUserAddress("summonTypenewDto.agentAddress", memberAddress, errors, "errmsg.address",
					true);
			if (memberEmail != null && !memberEmail.equals("")) {
				sfioValidator.isValidEmail("summonTypenewDto.agentEmail", memberEmail, errors);
			}
			if (memberPhone != null && !memberPhone.equals("")) {
				sfioValidator.isValidFirmPhone("summonTypenewDto.agentMobile", memberPhone, errors);
			}
			sfioValidator.isvalidName("summonTypenewDto.agentName", memberName, errors, "errmsg.fname", true);

			sfioValidator.isValidpanNumber("summonTypenewDto.agentpanNumber", agentPan, errors, "errmsg.pan", false);

			//sfioValidator.isValidaadhar("summonTypenewDto.agentaadharNumber", agentAadhar, errors, "errmsg.aadhar",
					//false);
			if (!sfioValidator.isBlank("summonTypenewDto.agentpassport", agentPassport)) {
				sfioValidator.isValidPassport("summonTypenewDto.agentpassport", agentPassport, errors);
			}
		}

		else {

			sfioValidator.isvalidRadio("summonTypenewDto.isOCin", isCin, errors);
			if (isCin.equalsIgnoreCase("Y"))

				sfioValidator.isvalidCIN("summonTypenewDto.othRegno", cinNum, errors, true);
			sfioValidator.isvalidFirmName("summonTypenewDto.othName", vendorName, errors, "errmsg.company", true);
			sfioValidator.isvalidName("summonTypenewDto.othRelation", vendorRelation, errors, "errmsg.fname", true);
			sfioValidator.isvalidUserAddress("summonTypenewDto.othAddress", vendorAddress, errors, "errmsg.address",
					true);
			if (vendorEmail != null && !vendorEmail.equals("")) {
				sfioValidator.isValidEmail("summonTypenewDto.othEmail", vendorEmail, errors);
			}
			if (vendorPhone != null && !vendorPhone.equals("")) {
				sfioValidator.isValidMobile("summonTypenewDto.othMobile", vendorPhone, errors);
			}
			sfioValidator.isValidpanNumber("summonTypenewDto.othpanNumber", othPan, errors, "errmsg.pan", false);
			//sfioValidator.isValidaadhar("summonTypenewDto.othaadharNumber", othAadhar, errors, "errmsg.aadhar", false);
			if (!sfioValidator.isBlank("summonTypenewDto.othpassport", othPassport)) {
				sfioValidator.isValidPassport("summonTypenewDto.othpassport", othPassport, errors);
			}
		}

	}

	public void validatefile(SummonStatus summonStatus, BindingResult errors) throws IOException {
		MultipartFile summonPhysicallyFile = summonStatus.getSummonPhysicallyFile();
		 SNMSValidator snmsVal = new SNMSValidator();
		 if(summonPhysicallyFile.getSize() > 0 || !summonPhysicallyFile.isEmpty())
		 {
			 snmsVal.isValidFile(summonPhysicallyFile, errors, true,"summonPhysicallyFile");
			 if(!snmsVal.isValidFileName(summonPhysicallyFile.getOriginalFilename()))
					errors.rejectValue("summonPhysicallyFile", "errmsg.filename");
			 if(!snmsVal.isValidFileTikka(summonPhysicallyFile.getOriginalFilename(), summonPhysicallyFile))
					errors.rejectValue("summonPhysicallyFile", "errmsg.maliciousdata");
		 }
	}

	public void validatePhyfile(NoticeStatus noticeStatus, BindingResult errors) throws IOException {
		MultipartFile noticePhysicallyFile = noticeStatus.getNoticePhysicallyFile();
		 SNMSValidator snmsVal = new SNMSValidator();
		 if(noticePhysicallyFile.getSize() > 0 || !noticePhysicallyFile.isEmpty())
		 {
			 snmsVal.isValidFile(noticePhysicallyFile, errors, true,"noticePhysicallyFile");
			 if(!snmsVal.isValidFileName(noticePhysicallyFile.getOriginalFilename()))
					errors.rejectValue("noticePhysicallyFile", "errmsg.filename");
			 if(!snmsVal.isValidFileTikka(noticePhysicallyFile.getOriginalFilename(), noticePhysicallyFile))
					errors.rejectValue("noticePhysicallyFile", "errmsg.maliciousdata");
		 }
	}

	/*
	 * // added BY Gouthami 09/07/2020 public void
	 * validateIndivialTypeNew(SummonDetails summonDetails,Errors errors) {
	 * 
	 * String companyName = summonDetails.getCompanySummonsDto().getCompanyName();
	 * 
	 * Long type = summonDetails.getSummonTypenewDto().getIndividualId();
	 * 
	 * String Name = summonDetails.getSummonTypenewDto().getName().replaceAll(",",
	 * ""); String NameCompany =
	 * summonDetails.getSummonTypenewDto().getNameCompany().replaceAll(",", "");
	 * String registration =
	 * summonDetails.getSummonTypenewDto().getRegistration_no().replaceAll(",", "");
	 * String relation =
	 * summonDetails.getSummonTypenewDto().getRelationwithcompany().replaceAll(",",
	 * ""); String email =
	 * summonDetails.getSummonTypenewDto().getEmail().replaceAll(",", ""); String
	 * phone = summonDetails.getSummonTypenewDto().getMobileNo().replaceAll(",",
	 * ""); String address =
	 * summonDetails.getSummonTypenewDto().getAddress().replaceAll(",", ""); String
	 * nationalId =
	 * summonDetails.getSummonTypenewDto().getNationalId().replaceAll(",", "");
	 * String nationality =
	 * summonDetails.getSummonTypenewDto().getNationality().replaceAll(",", "");
	 * 
	 * Date offJoinDate = summonDetails.getSummonTypenewDto().getOffJoinDate();
	 * String passport =
	 * summonDetails.getSummonTypenewDto().getPassport().replaceAll(",", ""); String
	 * placeof_issued =
	 * summonDetails.getSummonTypenewDto().getPlaceof_issued().replaceAll(",", "");
	 * String sex = summonDetails.getSummonTypenewDto().getSex().replaceAll(",",
	 * "");
	 * 
	 * String designation =
	 * summonDetails.getSummonTypenewDto().getDesignation().replaceAll(",", "");
	 * Date dob = summonDetails.getSummonTypenewDto().getDob();
	 * 
	 * Date issueDate = summonDetails.getSummonTypenewDto().getIssueDate(); String
	 * isCin = summonDetails.getSummonTypenewDto().getIsCin().replaceAll(",", "");
	 * String relationwithcompany =
	 * summonDetails.getSummonTypenewDto().getRelationwithcompany().replaceAll(",",
	 * "");
	 * 
	 * 
	 * SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	 * 
	 * SfioValidator sfioValidator=new SfioValidator();
	 * sfioValidator.isValidDropDown("companySummonsDto.companyName", companyName,
	 * errors);
	 * 
	 * 
	 * if(type ==1 || type ==2) {
	 * sfioValidator.isValidEmail("summonTypenewDto.email", email, errors);
	 * sfioValidator.isvalidFirmReg("{summonTypenewDto.Registration_no",
	 * registration, errors); sfioValidator.isvalidFirmName("summonTypenewDto.Name",
	 * Name, errors, "errmsg.firmName", true);
	 * sfioValidator.isvalidUserAddress("summonTypenewDto.Address", address, errors,
	 * "errmsg.address", true);
	 * 
	 * sfioValidator.isValidFirmPhone("summonTypenewDto.MobileNo", phone, errors);
	 * 
	 * if (offJoinDate != null && !offJoinDate.equals("")) { boolean isValid =
	 * sfioValidator.validateDateFormat(sdf.format(offJoinDate)); if (!isValid) {
	 * errors.rejectValue("summonTypenewDto.offJoinDate", "errmsg.dob"); }}
	 * 
	 * }
	 * 
	 * else if(type == 3 || type == 4) {
	 * sfioValidator.isvalidName("summonTypenewDto.Name", Name, errors,
	 * "errmsg.fname", true);
	 * sfioValidator.isvalidName("summonTypenewDto.designation", designation,
	 * errors, "errmsg.fname", true);
	 * sfioValidator.isvalidRadio("summonTypenewDto.sex", sex, errors); if (dob !=
	 * null && !dob.equals("")) { boolean isValid =
	 * sfioValidator.validateDateOfBirthFormat(sdf.format(dob)); if (!isValid) {
	 * errors.rejectValue("summonTypenewDto.dob", "errmsg.dob"); }
	 * 
	 * }
	 * 
	 * sfioValidator.isvalidName("summonTypenewDto.nationality", nationality,
	 * errors, "errmsg.fname", true);
	 * 
	 * if (!sfioValidator.isBlank("summonTypenewDto.nationalId", nationalId))
	 * sfioValidator.isValidNationalId("summonTypenewDto.nationalId", nationalId,
	 * errors);
	 * 
	 * if (!sfioValidator.isBlank("summonTypenewDto.passport", passport)){
	 * sfioValidator.isValidPassport("summonTypenewDto.passport", passport, errors);
	 * if (issueDate != null && !issueDate.equals("")) { boolean isValid =
	 * sfioValidator.validateDateFormat(sdf.format(dob)); if (!isValid) {
	 * errors.rejectValue("summonTypenewDto.offJoinDate", "errmsg.dob"); } }
	 * sfioValidator.isvalidPlace("summonTypenewDto.placeofIssues", placeof_issued,
	 * errors, "errmsg.place", true); }
	 * sfioValidator.isValidFirmPhone("summonTypenewDto.MobileNo", phone, errors);
	 * 
	 * }
	 * 
	 * else if (type == 5 || type == 6) {
	 * sfioValidator.isvalidRadio("summonTypenewDto.isCin", isCin, errors);
	 * if(isCin.equalsIgnoreCase("Y"))
	 * sfioValidator.isvalidFirmReg("{summonTypenewDto.Registration_no",
	 * registration, errors); sfioValidator.isvalidName("summonTypenewDto.Name",
	 * Name, errors, "errmsg.fname", true);
	 * sfioValidator.isvalidName("summonTypenewDto.NameCompany", NameCompany,
	 * errors, "errmsg.fname", true);
	 * sfioValidator.isvalidUserAddress("summonTypenewDto.Address", address, errors,
	 * "errmsg.address", true); sfioValidator.isValidEmail("summonTypenewDto.email",
	 * email, errors); sfioValidator.isValidFirmPhone("summonTypenewDto.MobileNo",
	 * phone, errors);
	 * 
	 * 
	 * }else { sfioValidator.isvalidRadio("summonTypenewDto.isCin", isCin, errors);
	 * if(isCin.equalsIgnoreCase("Y"))
	 * sfioValidator.isvalidFirmReg("{summonTypenewDto.Registration_no",
	 * registration, errors); sfioValidator.isvalidName("summonTypenewDto.Name",
	 * Name, errors, "errmsg.fname", true);
	 * sfioValidator.isvalidName("summonTypenewDto.relationwithcompany",
	 * relationwithcompany, errors, "errmsg.fname", true);
	 * sfioValidator.isvalidUserAddress("summonTypenewDto.Address", address, errors,
	 * "errmsg.address", true); sfioValidator.isValidEmail("summonTypenewDto.email",
	 * email, errors); sfioValidator.isValidFirmPhone("summonTypenewDto.MobileNo",
	 * phone, errors);
	 * 
	 * }
	 * 
	 * }
	 * 
	 */
}
