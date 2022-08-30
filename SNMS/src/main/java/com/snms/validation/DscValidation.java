package com.snms.validation;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DscValidation {

	public boolean checkDscValidity(String dscValid, String currentDate) throws ParseException, Exception {
		if (dscValid == null || "".equalsIgnoreCase(dscValid.trim()))
			return false;
		if (currentDate == null || "".equalsIgnoreCase(currentDate.trim()))
			return false;
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
		Date dscDate = sdf.parse(dscValid);
		Date curDate = sdf.parse(currentDate);
		if (dscDate.getTime() < curDate.getTime()) {
			return true;
		}
		return false;
	}

	public boolean checkDscValiditySigning(Date dscValid, String currentDate) throws ParseException, Exception {
		if (dscValid == null)
			return false;
		if (currentDate == null || "".equalsIgnoreCase(currentDate.trim()))
			return false;
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
		Date curDate = sdf.parse(currentDate);
		if (dscValid.getTime() < curDate.getTime()) {
			return true;
		}
		return false;
	}
}
