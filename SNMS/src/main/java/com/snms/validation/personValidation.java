package com.snms.validation;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;

import javax.validation.Valid;

import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;

import com.snms.entity.SummonDetails;

import bsh.ParseException;

public class personValidation {

	public void validatePerson(@Valid SummonDetails summonDetails, Errors errors) throws java.text.ParseException {

		String address = summonDetails.getPersonDetails().getAddress();
		String primaryMobile = summonDetails.getPersonDetails().getPrimaryMobile();
		String alternateNo = summonDetails.getPersonDetails().getAlternateNo();
		String perremark = summonDetails.getPersonDetails().getCompDto().getPersonRemark();
		long   countryCode = 0L;
		if(summonDetails.getPersonDetails().getCountry()!=null)
         countryCode = summonDetails.getPersonDetails().getCountry().getId();
		Date dob = summonDetails.getPersonDetails().getDob();
		String email = summonDetails.getPersonDetails().getEmail();
		String fullName = summonDetails.getPersonDetails().getFullName();
		String relation = summonDetails.getPersonDetails().getRelationName();
		String gender = summonDetails.getPersonDetails().getGender();
		String isApass = summonDetails.getPersonDetails().getIsApass();
		String isAvoter = summonDetails.getPersonDetails().getIsAvoter();
		String panNumber = summonDetails.getPersonDetails().getPanNumber();
		String passportNumber = summonDetails.getPersonDetails().getPassportNumber();
		String voterNUmber = summonDetails.getPersonDetails().getVoterId();
		Date passportDate = summonDetails.getPersonDetails().getPassportDate();
	//	String companyName = summonDetails.getPersonDetails().getCompDto().getCompanySummon().getCompanyName();
		//String companyType = summonDetails.getPersonDetails().getCompDto().getCompanySummon().getCompanyType()
			//	.getName();
	//	String cin = summonDetails.getPersonDetails().getCompDto().getCompanySummon().getCin();
		//String cemail = summonDetails.getPersonDetails().getCompDto().getCompanySummon().getEmail();
		Date dateCessation = summonDetails.getPersonDetails().getCompDto().getDateCessation();
		Date dateAppointment = summonDetails.getPersonDetails().getCompDto().getDateAppointment();
		//String designation = summonDetails.getPersonDetails().getCompDto().getDesignation().getDesignation();
		String din = summonDetails.getPersonDetails().getCompDto().getDin();
		String frn = summonDetails.getPersonDetails().getCompDto().getFrn();
		String iec = summonDetails.getPersonDetails().getCompDto().getIec();
		int compType = summonDetails.getCompanySummonsDto().getCompanyType().getId();
		String cin = summonDetails.getCompanySummonsDto().getCin();
		String compaddress = summonDetails.getCompanySummonsDto().getAddress();
		String compemail = summonDetails.getCompanySummonsDto().getEmail();
		String companyName = summonDetails.getCompanySummonsDto().getCompanyName();
		SNMSValidator snmsValid = new SNMSValidator();
		//snmsValid.isValidDropDown("companySummonsDto.companyName", companyName, errors);
		
		// sfioValidator.isvalidRadio("summonTypenewDto.type", type, errors);

		if(compType == 3) {
			snmsValid.isvalidLLPIN("companydetDto.cin", cin, errors, true);
		 }else {
			 snmsValid.isvalidCIN("companydetDto.cin", cin, errors, true);
		 }
		snmsValid.isvalidUserAddress("companySummonsDto.address", compaddress, errors, "errmsg.address", true);
		snmsValid.isvalidUserAddress("personDetails.address", address, errors, "errmsg.address", true);
		snmsValid.isvalidPersonName("personDetails.fullName", fullName, errors, "errmsg.fnames", true);
		snmsValid.isvalidPersonName("personDetails.RelationName", relation, errors, "errmsg.fnames", true);
		snmsValid.isvalidFirmName("personDetails.compDto.personRemark", perremark, errors, "errmsg.Personremark", false);
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	
	//    System.out.println(sdf.format(dob));
		
		
		  //String strDate = formatter.format(dob.to);  
		/*
		 * if (dob != null && !dob.equals("")) { boolean isValid =
		 * snmsValid.validateDateFormat(sdf.format(dob)); boolean isValiddate = true;
		 * isValiddate = snmsValid.validateDOB(sdf.format(dob)); if (!isValiddate) {
		 * errors.rejectValue("personDetails.dob", "errmsg.DOB"); } if (!isValid) {
		 * errors.rejectValue("personDetails.dob", "errmsg.toDate"); } } else if (dob ==
		 * null || dob.equals("")) { errors.rejectValue("personDetails.dob",
		 * "errmsg.required"); }
		 */

		if (dateAppointment != null && !dateAppointment.equals("")) {
			boolean isValid = snmsValid.validateDateFormat(sdf.format(dateAppointment));
			boolean isValiddate = true;
			boolean  isvalidateAndDob = snmsValid.validatedatappoitAnddob(sdf.format(dateAppointment),sdf.format(dob));
			isValiddate = snmsValid.validateDOB(sdf.format(dateAppointment));
			if (!isValiddate) {
				errors.rejectValue("dob", "errmsg.DOB");
			}
			if (!isValid) {
				errors.rejectValue("personDetails.compDto.dateAppointment", "errmsg.toDate");
			}
			if (!isvalidateAndDob) {
				errors.rejectValue("personDetails.compDto.dateAppointment", "errmsg.DateOfapoitment");
			}
		} else if (dateAppointment == null || dateAppointment.equals("")) {
			errors.rejectValue("personDetails.compDto.dateAppointment", "errmsg.required");
		}

		if (dateCessation != null && !dateCessation.equals("")) {
			boolean isValid = snmsValid.validateDateFormat(sdf.format(dateCessation));
			if (!isValid) {
				errors.rejectValue("personDetails.compDto.dateCessation", "errmsg.toDate");
			}

			boolean isvalidcesration = snmsValid.validatedateces(sdf.format(dateAppointment),
					sdf.format(dateCessation));

			if (!isvalidcesration) {
				errors.rejectValue("personDetails.compDto.dateCessation", "errmsg.Date");
			}
		}

		snmsValid.isValidMobilenonmadatory("personDetails.primaryMobile", primaryMobile, errors);

		if (!snmsValid.isBlank("personDetails.alternateNo", alternateNo)) {
			if(alternateNo.contains("-")) {
				snmsValid.isValidlandMobile("personDetails.alternateNo", alternateNo, errors);
			}else {
			snmsValid.isValidMobile("personDetails.alternateNo", alternateNo, errors);
			}
		}
		snmsValid.isValidEmail("personDetails.email", email, errors, true);

		snmsValid.isValidpanNumber("personDetails.panNumber", panNumber, errors, "errmsg.pan", true);

		
		if(summonDetails.getPersonDetails().getIsApass().equalsIgnoreCase("Y")) {
		
			snmsValid.isValidpasportNumber("personDetails.passportNumber", passportNumber, countryCode, errors);
			if (passportDate != null && !passportDate.equals("")) {
				boolean isValid = snmsValid.validateDateFormat(sdf.format(passportDate));
				if (!isValid) {
					errors.rejectValue("personDetails.passportDate", "errmsg.dob");
				}
			}

		
		}
		if(isAvoter.equalsIgnoreCase("Y")) {
		
			snmsValid.isValidVoterNumber("personDetails.voterId", voterNUmber, errors);
		
		}
		// TODO Auto-generated method stub

	}

	public void validateEditPerson1(@Valid SummonDetails summonDetails, Errors errors) throws java.text.ParseException {
	//	String address = summonDetails.getPersonDetails().getAddress();
		long   countryCode = 0L;
		if(summonDetails.getPersonDetails().getCountry()!=null)
         countryCode = summonDetails.getPersonDetails().getCountry().getId();
		String primaryMobile = summonDetails.getPersonDetails().getPrimaryMobile();
		String alternateNo = summonDetails.getPersonDetails().getAlternateNo();
		Date dob = summonDetails.getPersonDetails().getDob();
		String email = summonDetails.getPersonDetails().getEmail();
		String fullName = summonDetails.getPersonDetails().getFullName();
		String relation = summonDetails.getPersonDetails().getRelationName();
		String gender = summonDetails.getPersonDetails().getGender();
		String isApass = summonDetails.getPersonDetails().getIsApass();
		String isAvoter = summonDetails.getPersonDetails().getIsAvoter();
		//String panNumber = summonDetails.getPersonDetails().getPanNumber();
		String passportNumber = summonDetails.getPersonDetails().getPassportNumber();
		Date passportDate = summonDetails.getPersonDetails().getPassportDate();
		String voterNUmber = summonDetails.getPersonDetails().getVoterId();
        
	//	String companyName = summonDetails.getPersonDetails().getCompDto().getCompanySummon().getCompanyName();
		//String companyType = summonDetails.getPersonDetails().getCompDto().getCompanySummon().getCompanyType()
			//	.getName();
	//	String cin = summonDetails.getPersonDetails().getCompDto().getCompanySummon().getCin();
		//String cemail = summonDetails.getPersonDetails().getCompDto().getCompanySummon().getEmail();
		Date dateCessation = summonDetails.getPersonDetails().getCompDto().getDateCessation();
		Date dateAppointment = summonDetails.getPersonDetails().getCompDto().getDateAppointment();
		//String designation = summonDetails.getPersonDetails().getCompDto().getDesignation().getDesignation();
		String din = summonDetails.getPersonDetails().getCompDto().getDin();
		String frn = summonDetails.getPersonDetails().getCompDto().getFrn();
		String iec = summonDetails.getPersonDetails().getCompDto().getIec();

		SNMSValidator snmsValid = new SNMSValidator();

		//snmsValid.isvalidUserAddress("personDetails.address", address, errors, "errmsg.address", true);
		snmsValid.isvalidPersonName("personDetails.fullName", fullName, errors, "errmsg.fnames", true);
		snmsValid.isvalidPersonName("personDetails.RelationName", relation, errors, "errmsg.fnames", true);
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		if (dob != null && !dob.equals("")) {
			//boolean isValid = snmsValid.validateDateFormat(dob.toString());
			boolean isValiddate = true;
			isValiddate = snmsValid.validateDOB(sdf.format(dob));
			if (!isValiddate) {
				errors.rejectValue("personDetails.dob", "errmsg.DOB");
			}
			/*
			 * if (!isValid) { errors.rejectValue("personDetails.dob", "errmsg.toDate"); }
			 */
		} else if (dob == null || dob.equals("")) {
			errors.rejectValue("personDetails.dob", "errmsg.required");
		}

		if (dateAppointment != null && !dateAppointment.equals("")) {
			boolean isValid = snmsValid.validateDateFormat(sdf.format(dateAppointment));
			boolean isValiddate = true;
			isValiddate = snmsValid.validateDOB(sdf.format(dateAppointment));
			if (!isValiddate) {
				errors.rejectValue("dob", "errmsg.DOB");
			}
			if (!isValid) {
				errors.rejectValue("personDetails.compDto.dateAppointment", "errmsg.toDate");
			}
		} else if (dateAppointment == null || dateAppointment.equals("")) {
			errors.rejectValue("personDetails.compDto.dateAppointment", "errmsg.required");
		}

		if (dateCessation != null && !dateCessation.equals("")) {
			boolean isValid = snmsValid.validateDateFormat(sdf.format(dateCessation));
			if (!isValid) {
				errors.rejectValue("personDetails.compDto.dateCessation", "errmsg.toDate");
			}

			boolean isvalidcesration = snmsValid.validatedateces(sdf.format(dateAppointment),
					sdf.format(dateCessation));

			if (!isvalidcesration) {
				errors.rejectValue("personDetails.compDto.dateCessation", "errmsg.Date");
			}
		}
 if(primaryMobile!="") {
		snmsValid.isValidMobile("personDetails.primaryMobile", primaryMobile, errors);
 }
		if (!snmsValid.isBlank("personDetails.alternateNo", alternateNo))
			snmsValid.isValidMobile("personDetails.alternateNo", alternateNo, errors);

		snmsValid.isValidEmail("personDetails.email", email, errors, true);

//		snmsValid.isValidpanNumber("personDetails.panNumber", panNumber, errors, "errmsg.pan", true);
		if(summonDetails.getPersonDetails().getVoterId()!="") {
			snmsValid.isValidVoterNumber("personDetails.voterId", voterNUmber, errors);
		}
		if (!snmsValid.isBlank("personDetails.passportNumber", passportNumber)) {
			snmsValid.isValidpasportNumber("personDetails.passportNumber", passportNumber, countryCode, errors);
			if (passportDate != null && !passportDate.equals("")) {
				boolean isValid = snmsValid.validateDateFormat(sdf.format(passportDate));
				if (!isValid) {
					errors.rejectValue("personDetails.passportDate", "errmsg.dob");
				}
			}

		}

		}
	//}

	public void validateEditcomp1(@Valid SummonDetails summonDetails, Errors errors) throws java.text.ParseException {
		String din = summonDetails.getPersonDetails().getCompDto().getDin();
		String frn = summonDetails.getPersonDetails().getCompDto().getFrn();
		String iec = summonDetails.getPersonDetails().getCompDto().getIec();
		Date dateCessation = summonDetails.getPersonDetails().getCompDto().getDateCessation();
		Date dateAppointment = summonDetails.getPersonDetails().getCompDto().getDateAppointment();    
		SNMSValidator snmsValid = new SNMSValidator();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		if (dateAppointment != null && !dateAppointment.equals("")) {
			boolean isValid = snmsValid.validateDateFormat(sdf.format(dateAppointment));
			boolean isValiddate = true;
			isValiddate = snmsValid.validateDOB(sdf.format(dateAppointment));
			if (!isValiddate) {
				errors.rejectValue("dob", "errmsg.DOB");
			}
			if (!isValid) {
				errors.rejectValue("personDetails.compDto.dateAppointment", "errmsg.toDate");
			}
		} else if (dateAppointment == null || dateAppointment.equals("")) {
			errors.rejectValue("personDetails.compDto.dateAppointment", "errmsg.required");
		}

		if (dateCessation != null && !dateCessation.equals("")) {
			boolean isValid = snmsValid.validateDateFormat(sdf.format(dateCessation));
			if (!isValid) {
				errors.rejectValue("personDetails.compDto.dateCessation", "errmsg.toDate");
			}

			boolean isvalidcesration = snmsValid.validatedateces(sdf.format(dateAppointment),
					sdf.format(dateCessation));

			if (!isvalidcesration) {
				errors.rejectValue("personDetails.compDto.dateCessation", "errmsg.Date");
			}
		}
		
	}

}
