package com.snms.validation;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.Errors;
import org.springframework.web.multipart.MultipartFile;



public class SfioValidator
{
	private static final Logger logger = Logger.getLogger(SfioValidator.class);
	
	@Value("errmsg.required")
	public String required;

  public static final String PDF_MIME_TYPE="application/pdf";
  public static final long MB_IN_BYTES = 2097152; // 1 MB file size
    public void isValidDropDown(String fieldName, String fieldValue, Errors errors)
    {
        if (fieldValue == null || "".equalsIgnoreCase(fieldValue.trim()))
        {
            errors.rejectValue(fieldName, "errmsg.required");
            return;
        }
    }

    /**
     * Check valid email
     * 
     * @param fieldName - String variable for email
     * @param fieldValue -String variable for email value
     * @param errors - Errors variable for containing field error
     */
    public void isValidEmail(String fieldName, String fieldValue, Errors errors)
    {
        if (fieldValue == null || "".equals(fieldValue.trim())
                || "null".equalsIgnoreCase(fieldValue.trim()))
        {
            errors.rejectValue(fieldName, "errmsg.required");
            return;
        }
        String emailReg = "^([\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4})?$";
        Pattern pattern = Pattern.compile(emailReg);
        Matcher matcher = pattern.matcher(fieldValue);
        if (!isBlank(fieldName, fieldValue) && !matcher.matches())
            errors.rejectValue(fieldName, "errmsg.email");
    }

    /**
     * Check valid phone no
     * 
     * @param fieldName - String variable for phone
     * @param fieldValue -String variable for phone value
     * @param errors - Errors variable for containing field error
     */
    public void isValidFirmPhone(String fieldName, String fieldValue, Errors errors)
    {
        if (fieldValue == null || "".equals(fieldValue.trim())
                || "null".equalsIgnoreCase(fieldValue.trim()))
        {
            errors.rejectValue(fieldName, "errmsg.required");
            return;
        }
        String numberReg = "^\\(?([0-9]{2,3})\\)?[-]?([0-9]{2,4})[-]?([0-9]{4,8})$";
//        String numberReg = "^([0-9/]){3,}$";
        Pattern pattern = Pattern.compile(numberReg);
        Matcher matcher = pattern.matcher(fieldValue);
        if (!isBlank(fieldName, fieldValue) && !matcher.matches())
            errors.rejectValue(fieldName, "errmsg.phone");
    }
    
    public void isValidPincode(String fieldName, String fieldValue, Errors errors)
    {
        if (fieldValue == null || "".equals(fieldValue.trim())
                || "null".equalsIgnoreCase(fieldValue.trim()))
        {
            errors.rejectValue(fieldName, "errmsg.required");
            return;
        }
        //String numberReg = "^[0-9]{10,10}$";
        String numberReg = "^[1-9]{1}[0-9]{5}$";
        Pattern pattern = Pattern.compile(numberReg);
        Matcher matcher = pattern.matcher(fieldValue);
        if (!isBlank(fieldName, fieldValue) && !matcher.matches())
            errors.rejectValue(fieldName, "errmsg.pincode");
    }
    
    public void isValidZIPcode(String fieldName, String fieldValue, Errors errors)
    {
        if (fieldValue == null || "".equals(fieldValue.trim())
                || "null".equalsIgnoreCase(fieldValue.trim()))
        {
            errors.rejectValue(fieldName, "errmsg.required");
            return;
        }
        //String numberReg = "^[0-9]{10,10}$";
        String numberReg = "^([A-Za-z0-9\\s,-]){3,8}$";
        Pattern pattern = Pattern.compile(numberReg);
        Matcher matcher = pattern.matcher(fieldValue);
        if (!isBlank(fieldName, fieldValue) && !matcher.matches())
            errors.rejectValue(fieldName, "errmsg.zipcode");
    }

    /**
     * Check valid mobile
     * 
     * @param fieldName - String variable for mobile
     * @param fieldValue -String variable for mobile value
     * @param errors - Errors variable for containing field error
     */
    public void isValidMobile(String fieldName, String fieldValue,
            Errors errors)
    {
    
        if (fieldValue == null || "".equals(fieldValue.trim())
                || "null".equalsIgnoreCase(fieldValue.trim()))
        {
            errors.rejectValue(fieldName, "errmsg.required");
            return;
        }
        String numberReg = "^[0-9]{10,10}$";
        Pattern pattern = Pattern.compile(numberReg);
        Matcher matcher = pattern.matcher(fieldValue);
        if (!isBlank(fieldName, fieldValue) && !matcher.matches())
            errors.rejectValue(fieldName, "errmsg.mobile");
    }



    /**
     * Check the input data is blank or not.
     * 
     * @param fieldname - String variable contains input field name
     * @param fieldValue -String variable contains input field value
     * @param errors - Errors variable for containing field error
     */
    public void isBlank(String fieldname, String fieldValue, Errors errors)
    {
        if (fieldValue == null)
        {
            errors.rejectValue(fieldname, "errmsg.invalid");
            return;
        }
        if ("".equals(fieldValue) || fieldValue.isEmpty())
        {
            errors.rejectValue(fieldname, "errmsg.required");
        }
    }

    /**
     * Check the input data is blank or not.
     * 
     * @param fieldName - String variable contains input field name
     * @param fieldValue -String variable contains input field value
     * @return true if input is blank or false
     */
    public boolean isBlank(String fieldname, String fieldValue)
    {
        boolean flag = false;
        if (fieldValue == null || "".equals(fieldValue.trim()))
        {
            flag = true;
        }
        return flag;
    }
    

    
  
    /**
     * Check valid name
     * 
     * @param fieldName - String variable for field name
     * @param fieldValue -String variable for field value
     * @param errors - Errors variable for containing field error
     */
    public void isvalidDesignation(String fieldName, String fieldValue, Errors errors)
    {
        if (fieldValue == null || "".equals(fieldValue.trim())
                || "null".equalsIgnoreCase(fieldValue.trim()))
        {
            errors.rejectValue(fieldName, "errmsg.required");
            return;
        }
        String fnameReg = "^([A-Za-z\\s.]){2,50}$";
        Pattern pattern = Pattern.compile(fnameReg);
        Matcher matcher = pattern.matcher(fieldValue);
        if (!isBlank(fieldName, fieldValue) && !matcher.matches())
            errors.rejectValue(fieldName, "errmsg.designation");
    }

    public void isValidText200(String fieldName, String fieldValue, Errors errors, String errMsg,boolean required) {
		if (required && (fieldValue == null || "".equals(fieldValue.trim())
                || "null".equalsIgnoreCase(fieldValue.trim())))
        {
            errors.rejectValue(fieldName, "errmsg.required");
            return;
        }
        String patternRemark = "^([0-9a-zA-Z\\s\\/,_-]){3,200}$";
        Pattern pattern = Pattern.compile(patternRemark);
        Matcher matcher = pattern.matcher(fieldValue);
        if (!isBlank(fieldName, fieldValue) && !matcher.matches())
            errors.rejectValue(fieldName, errMsg);
	}
    
    public void isvalidFirmReg(String fieldName, String fieldValue, Errors errors)
    {
        if (fieldValue == null || "".equals(fieldValue.trim())
                || "null".equalsIgnoreCase(fieldValue.trim()))
        {
            errors.rejectValue(fieldName, "errmsg.required");
            return;
        }
        String fnameReg = "^([0-9a-zA-Z\\/]){1,25}$";
        Pattern pattern = Pattern.compile(fnameReg);
        Matcher matcher = pattern.matcher(fieldValue);
        if (!isBlank(fieldName, fieldValue) && !matcher.matches())
            errors.rejectValue(fieldName, "errmsg.firmReg");
    }
    
    public void isvalidFirmCity(String fieldName, String fieldValue, Errors errors,String errMsg, boolean required) {
      if (required && (fieldValue == null || "".equals(fieldValue.trim()) || "null".equalsIgnoreCase(fieldValue.trim()))) {
        errors.rejectValue(fieldName, "errmsg.required");
        return;
      }
     // String fnameReg = "^(a-zA-Z\\()\\s]){3,70}$";
      String firmCity="^([a-zA-Z\\(\\)\\s]){1,50}$";
      Pattern pattern = Pattern.compile(firmCity);
      Matcher matcher = pattern.matcher(fieldValue);
      if (!isBlank(fieldName, fieldValue) && !matcher.matches())
        errors.rejectValue(fieldName, errMsg);
    }
    
    public void isvalidFirmName(String fieldName, String fieldValue, Errors errors,String errMsg, boolean required) {
      if (required && (fieldValue == null || "".equals(fieldValue.trim()) || "null".equalsIgnoreCase(fieldValue.trim()))) {
        errors.rejectValue(fieldName, "errmsg.required");
        return;
      }
      String firmCity="^[a-zA-Z0-9\\s-&()._]{1,70}$";
      Pattern pattern = Pattern.compile(firmCity);
      Matcher matcher = pattern.matcher(fieldValue);
      if (!isBlank(fieldName, fieldValue) && !matcher.matches())
        errors.rejectValue(fieldName, errMsg);
    }
    public void isvalidUserAddress(String fieldName, String fieldValue, Errors errors,String errMsg, boolean required) {
      if (required && (fieldValue == null || "".equals(fieldValue.trim()) || "null".equalsIgnoreCase(fieldValue.trim()))) {
        errors.rejectValue(fieldName, "errmsg.required");
        return;
      }
      //String address="^(\\w*\\s*[-,.\\(\\)\\&]{3,200}$";
      String address="^[\\w\\d\\s,._:()/-]{3,255}$";
      Pattern pattern = Pattern.compile(address);
      Matcher matcher = pattern.matcher(fieldValue);
      if (!isBlank(fieldName, fieldValue) && !matcher.matches())
        errors.rejectValue(fieldName, errMsg);
    }
    public void isvalidRadio(String fieldName, String fieldValue, Errors errors)
    {
        if (fieldValue == null || "".equals(fieldValue.trim()))
        {
            errors.rejectValue(fieldName, "errmsg.required");
            return;
        }
    }

    public void isRadioChecked(String fieldName, boolean fieldValue, Errors errors)
    {
        if (fieldValue==false)
        {
            errors.rejectValue(fieldName, "errmsg.required");
            return;
        }
    }
    
    public void isvalidName(String fieldName, String fieldValue, Errors errors,String errMsg, boolean required) {
      if (required && (fieldValue == null || "".equals(fieldValue.trim()) || "null".equalsIgnoreCase(fieldValue.trim()))) {
        errors.rejectValue(fieldName, "errmsg.required");
        return;
      }
      String fnameReg = "^[a-zA-Z\\s.-_&()]{3,35}$";
      Pattern pattern = Pattern.compile(fnameReg);
      Matcher matcher = pattern.matcher(fieldValue);
      if (!isBlank(fieldName, fieldValue) && !matcher.matches())
        errors.rejectValue(fieldName, errMsg);
    }
    
    public void isvalidPlace(String fieldName, String fieldValue, Errors errors,String errMsg, boolean required) {
        if (required && (fieldValue == null || "".equals(fieldValue.trim()) || "null".equalsIgnoreCase(fieldValue.trim()))) {
          errors.rejectValue(fieldName, "errmsg.required");
          return;
        }
        String fnameReg = "^[a-zA-Z\\s.-_&()]{3,70}$";
        Pattern pattern = Pattern.compile(fnameReg);
        Matcher matcher = pattern.matcher(fieldValue);
        if (!isBlank(fieldName, fieldValue) && !matcher.matches())
          errors.rejectValue(fieldName, errMsg);
      }
    
    public void isvalidUserName(String fieldName, String fieldValue, Errors errors,String errMsg, boolean required) {
        if (required && (fieldValue == null || "".equals(fieldValue.trim()) || "null".equalsIgnoreCase(fieldValue.trim()))) {
          errors.rejectValue(fieldName, "errmsg.required");
          return;
        }
        String fnameReg = "^[a-zA-Z\\s]{3,35}$";
        Pattern pattern = Pattern.compile(fnameReg);
        Matcher matcher = pattern.matcher(fieldValue);
        if (!isBlank(fieldName, fieldValue) && !matcher.matches())
          errors.rejectValue(fieldName, errMsg);
      }
    
    public String getValidBoolean(String value)
    {
        if (("false").equalsIgnoreCase(value)
                || ("true").equalsIgnoreCase(value))
            return value;
        return "";
    }

    public void isValidData(String fieldname, String fieldValue,
        Errors errors) {
    if (fieldValue == null)
        
    {
        errors.rejectValue(fieldname, "errmsg.invalid");
        return;
    }
    String dataReg = "^[\\w\\d\\s._!@#$:()/=-]+$";
    Pattern pattern = Pattern.compile(dataReg);
    Matcher matcher = pattern.matcher(fieldValue.trim());
    if (!matcher.matches()){
        errors.rejectValue(fieldname, "errmsg.malicious");
    }
}
    
    public void isValidPassword(String fieldname, String fieldValue,
            Errors errors) {
        if (fieldValue == null)
            
        {
            errors.rejectValue(fieldname, "errmsg.invalid");
            return;
        }
        String passwordPattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#$&_.])[a-zA-Z0-9!@#$_.]{8,20}$";
        Pattern pattern = Pattern.compile(passwordPattern);
        Matcher matcher = pattern.matcher(fieldValue.trim());
        if (!matcher.matches()){
            errors.rejectValue(fieldname, "errmsg.malicious");
        }
    }
    
    public boolean validateYear(String date)
    {
        Pattern pattern = Pattern.compile("((20)\\d\\d)");
        Matcher matcher = pattern.matcher(date);
        if (matcher.matches())
            return true;
        else
        	return false;
    }
    /**
     * Method for validating date format considering leap year
     * 
     * @param date - input date in string
     * @return true or false
     */
    public boolean validateDateFormat(final String date)
    {
    	//String regex = "^(1[0-2]|0[1-9])/(3[01]|[12][0-9]|0[1-9])/[0-9]{4}$";
        //Pattern pattern = Pattern.compile(regex);
    	Pattern pattern = Pattern.compile("(0?[1-9]|[12][0-9]|3[01])/(0?[1-9]|1[012])/((19|20)\\d\\d)");
        Matcher matcher = pattern.matcher(date);
        if (matcher.matches())
        {
            matcher.reset();
            if (matcher.find())
            {
                String day = matcher.group(1);
                String month = matcher.group(2);
                int year = Integer.parseInt(matcher.group(3));

                if (day.equals("31") && (month.equals("4") || month.equals("6")
                        || month.equals("9") || month.equals("11")
                        || month.equals("04") || month.equals("06")
                        || month.equals("09")))
                {
                    return false; // only 1,3,5,7,8,10,12 has 31 days
                }
                else if (month.equals("2") || month.equals("02"))
                {
                    // leap year
                    if (year % 4 == 0)
                    {
                        if (day.equals("30") || day.equals("31"))
                        {
                            return false;
                        }
                        else
                        {
                            return true;
                        }
                    }
                    else
                    {
                        if (day.equals("29") || day.equals("30")
                                || day.equals("31"))
                        {
                            return false;
                        }
                        else
                        {
                            return true;
                        }
                    }
                }
                else
                {
                    return true;
                }
            }
            else
            {
                return false;
            }
        }
        else
        {
            return false;
        }
    }
    
//    eFormNFRA Validation
    
    public void isvalidPan(String fieldName, String fieldValue, Errors errors)
    {
        if (fieldValue == null || "".equals(fieldValue.trim())
                || "null".equalsIgnoreCase(fieldValue.trim()))
        {
            errors.rejectValue(fieldName, "errmsg.required");
            return;
        }
        //String numberReg = "^[0-9]{10,10}$";
        String patternPan = "[A-Z]{5}[0-9]{4}[A-Z]{1}";
        Pattern pattern = Pattern.compile(patternPan);
        Matcher matcher = pattern.matcher(fieldValue);
        if (!isBlank(fieldName, fieldValue) && !matcher.matches())
            errors.rejectValue(fieldName, "errmsg.pan");
    }
    
    public void isValidAssignedBy(String fieldName, String fieldValue, Errors errors)
    {
        if (fieldValue == null || "".equals(fieldValue.trim())
                || "null".equalsIgnoreCase(fieldValue.trim()))
        {
            errors.rejectValue(fieldName, "errmsg.required");
            return;
        }
        //String numberReg = "^[0-9]{10,10}$";
        String patternAssignedBy = "^[a-zA-Z0-9\\s-&,_.]{3,70}$";
        Pattern pattern = Pattern.compile(patternAssignedBy);
        Matcher matcher = pattern.matcher(fieldValue);
        if (!isBlank(fieldName, fieldValue) && !matcher.matches())
            errors.rejectValue(fieldName, "errmsg.assignedby");
    }
    
    public void isvalidRegnWithAgency(String fieldName, String fieldValue, Errors errors)
    {
        if (fieldValue == null || "".equals(fieldValue.trim())
                || "null".equalsIgnoreCase(fieldValue.trim()))
        {
            errors.rejectValue(fieldName, "errmsg.required");
            return;
        }
        String fnameReg = "^([0-9a-zA-Z\\/]){1,20}$";
        Pattern pattern = Pattern.compile(fnameReg);
        Matcher matcher = pattern.matcher(fieldValue);
        if (!isBlank(fieldName, fieldValue) && !matcher.matches())
            errors.rejectValue(fieldName, "errmsg.regnwithagency");
    }
    
    public void isvalidAddrLine1(String fieldName, String fieldValue, Errors errors,String errMsg, boolean required) {
        if (required && (fieldValue == null || "".equals(fieldValue.trim()) || "null".equalsIgnoreCase(fieldValue.trim()))) {
          errors.rejectValue(fieldName, "errmsg.required");
          return;
        }
        //String address="^(\\w*\\s*[-,.\\(\\)\\&]{3,200}$";
        String address="^[\\w\\d\\s,_()/-]{3,50}$";
        Pattern pattern = Pattern.compile(address);
        Matcher matcher = pattern.matcher(fieldValue);
        if (!isBlank(fieldName, fieldValue) && !matcher.matches())
          errors.rejectValue(fieldName, errMsg);
      }
    
    public void isvalidAddrLine2(String fieldName, String fieldValue, Errors errors,String errMsg, boolean required) {
        if (required && (fieldValue == null || "".equals(fieldValue.trim()) || "null".equalsIgnoreCase(fieldValue.trim()))) {
          errors.rejectValue(fieldName, "errmsg.required");
          return;
        }
        //String address="^(\\w*\\s*[-,.\\(\\)\\&]{3,200}$";
        String address="^[\\w\\d\\s,_()/-]{3,50}$";
        Pattern pattern = Pattern.compile(address);
        Matcher matcher = pattern.matcher(fieldValue);
        if (!isBlank(fieldName, fieldValue) && !matcher.matches())
          errors.rejectValue(fieldName, errMsg);
      }
    
    public void isvalidCity(String fieldName, String fieldValue, Errors errors,String errMsg, boolean required) {
        if (required && (fieldValue == null || "".equals(fieldValue.trim()) || "null".equalsIgnoreCase(fieldValue.trim()))) {
          errors.rejectValue(fieldName, "errmsg.required");
          return;
        }
       // String fnameReg = "^(a-zA-Z\\()\\s]){3,70}$";
        String firmCity="^([a-zA-Z\\(\\)\\s]){1,50}$";
        Pattern pattern = Pattern.compile(firmCity);
        Matcher matcher = pattern.matcher(fieldValue);
        if (!isBlank(fieldName, fieldValue) && !matcher.matches())
          errors.rejectValue(fieldName, errMsg);
      }
    
    public void isValidWebsite(String fieldName, String fieldValue, Errors errors, boolean required)
    {
        if (required && (fieldValue == null || "".equals(fieldValue.trim())
                || "null".equalsIgnoreCase(fieldValue.trim())))
        {
            errors.rejectValue(fieldName, "errmsg.required");
            return;
        }
        //String numberReg = "^[0-9]{10,10}$";
        String numberReg = "^(http:\\/\\/www\\.|https:\\/\\/www\\.|http:\\/\\/|https:\\/\\/)?[a-z0-9]+([\\-\\.]{1}[a-z0-9]+)*\\.[a-z]{2,5}(:[0-9]{1,5})?(\\/.*)?$";
        Pattern pattern = Pattern.compile(numberReg);
        Matcher matcher = pattern.matcher(fieldValue);
        if (!isBlank(fieldName, fieldValue) && !matcher.matches())
            errors.rejectValue(fieldName, "errmsg.website");
    }

	public void isValidRatingScale(String fieldName, String fieldValue, Errors errors, boolean required) {
		if (required && (fieldValue == null || "".equals(fieldValue.trim())
                || "null".equalsIgnoreCase(fieldValue.trim())))
        {
            errors.rejectValue(fieldName, "errmsg.required");
            return;
        }
//        String patternRating = "\\d+(\\.\\d{1,1})?";
        String patternRating = "^([A-Za-z\\s.-_]){1,25}$";
        Pattern pattern = Pattern.compile(patternRating);
        Matcher matcher = pattern.matcher(fieldValue);
        if (!isBlank(fieldName, fieldValue) && !matcher.matches())
            errors.rejectValue(fieldName, "errmsg.rating");
	}

	public void isValidRemark(String fieldName, String fieldValue, Errors errors, String errMsg,boolean required) {
		if (required && (fieldValue == null || "".equals(fieldValue.trim())
                || "null".equalsIgnoreCase(fieldValue.trim())))
        {
            errors.rejectValue(fieldName, "errmsg.required");
            return;
        }
        String patternRemark = "^\\w.+(?:\\s+\\w[0-9a-zA-Z\\s\\/,._-]+){0,99}$";
        Pattern pattern = Pattern.compile(patternRemark);
        Matcher matcher = pattern.matcher(fieldValue);
        if (!isBlank(fieldName, fieldValue) && !matcher.matches())
            errors.rejectValue(fieldName, errMsg);
	}
	
	public void isValidDescription(String fieldName, String fieldValue, Errors errors, String errMsg,boolean required) {
		if (required && (fieldValue == null || "".equals(fieldValue.trim())
                || "null".equalsIgnoreCase(fieldValue.trim())))
        {
            errors.rejectValue(fieldName, "errmsg.required");
            return;
        }
        String patternRemark = "^\\w.+(?:\\s+\\w[0-9a-zA-Z'()\\s\\/,\\._-]+){0,499}$";
        Pattern pattern = Pattern.compile(patternRemark);
        Matcher matcher = pattern.matcher(fieldValue);
        if (!isBlank(fieldName, fieldValue) && !matcher.matches())
            errors.rejectValue(fieldName, errMsg);
	}

	public void isvalidCIN(String fieldName, String fieldValue, Errors errors, boolean required) {
		if (required && (fieldValue == null || "".equals(fieldValue.trim())
                || "null".equalsIgnoreCase(fieldValue.trim())))
        {
            errors.rejectValue(fieldName, "errmsg.required");
            return;
        }
        String patternCIN = "^([L|U]{1})([0-9]{5})([A-Za-z]{2})([0-9]{4})([A-Za-z]{3})([0-9]{6})$";
        Pattern pattern = Pattern.compile(patternCIN);
        Matcher matcher = pattern.matcher(fieldValue);
        if (!isBlank(fieldName, fieldValue) && !matcher.matches())
            errors.rejectValue(fieldName, "errmsg.cin");
	}

	public void isvalidGLN(String fieldName, String fieldValue, Errors errors, boolean required) {
		if (required && (fieldValue == null || "".equals(fieldValue.trim())
                || "null".equalsIgnoreCase(fieldValue.trim())))
        {
            errors.rejectValue(fieldName, "errmsg.required");
            return;
        }
        String patternGLN = "^416(\\d{13})$";
        Pattern pattern = Pattern.compile(patternGLN);
        Matcher matcher = pattern.matcher(fieldValue);
        if (!isBlank(fieldName, fieldValue) && !matcher.matches())
            errors.rejectValue(fieldName, "errmsg.gln");
	}
	
	public void isvalidRegnum(String fieldName, String fieldValue, Errors errors, String errMsg, boolean required) {
		if (required && (fieldValue == null || "".equals(fieldValue.trim())
                || "null".equalsIgnoreCase(fieldValue.trim())))
        {
            errors.rejectValue(fieldName, "errmsg.required");
            return;
        }
        String patternRegnum = "^([0-9a-zA-Z\\/]){1,20}$";
        Pattern pattern = Pattern.compile(patternRegnum);
        Matcher matcher = pattern.matcher(fieldValue);
        if (!isBlank(fieldName, fieldValue) && !matcher.matches())
            errors.rejectValue(fieldName, errMsg);
	}

	public void isValidNumeric(String fieldName, String fieldValue, Errors errors, String errMsg, boolean required) {
		if (required && (fieldValue == null || fieldValue.equals("0.0") || fieldValue.equals("0") || "".equals(fieldValue.trim())
                || "null".equalsIgnoreCase(fieldValue.trim())))
        {
            errors.rejectValue(fieldName, "errmsg.required");
            return;
        }
        String patternNumeric = "\\d+(\\.\\d{1,2})?";
        Pattern pattern = Pattern.compile(patternNumeric);
        Matcher matcher = pattern.matcher(fieldValue);
        if (!isBlank(fieldName, fieldValue) && !matcher.matches())
            errors.rejectValue(fieldName, errMsg);
	}
	
	public void isValidNumericWithZero(String fieldName, String fieldValue, Errors errors, String errMsg, boolean required) {
		if (required && (fieldValue == null || "".equals(fieldValue.trim())
                || "null".equalsIgnoreCase(fieldValue.trim())))
        {
            errors.rejectValue(fieldName, "errmsg.required");
            return;
        }
        String patternNumeric = "\\d+(\\.\\d{1,2})?";
        Pattern pattern = Pattern.compile(patternNumeric);
        Matcher matcher = pattern.matcher(fieldValue);
        if (!isBlank(fieldName, fieldValue) && !matcher.matches())
            errors.rejectValue(fieldName, errMsg);
	}
	
	public void isvalidFCRN(String fieldName, String fieldValue, Errors errors, String errMsg, boolean required) {
		if (required && (fieldValue == null || "".equals(fieldValue.trim())
                || "null".equalsIgnoreCase(fieldValue.trim())))
        {
            errors.rejectValue(fieldName, "errmsg.required");
            return;
        }
//        String patternFCRN = "^([0-9a-zA-Z]){6}$";
		String patternFCRN="";
		if(fieldValue.indexOf(0)=='F')
			patternFCRN = "^[F]{1}[0-9]{5}$";
		else
			patternFCRN = "[0-9]{8}$";
        Pattern pattern = Pattern.compile(patternFCRN);
        Matcher matcher = pattern.matcher(fieldValue);
        if (!isBlank(fieldName, fieldValue) && !matcher.matches())
            errors.rejectValue(fieldName, errMsg);
	}
	public void isvalidFirmLLPIN(String fieldName, String fieldValue, Errors errors)
    {
        if (fieldValue == null || "".equals(fieldValue.trim())
                || "null".equalsIgnoreCase(fieldValue.trim()))
        {
            errors.rejectValue(fieldName, "errmsg.required");
            return;
        }
        String patternllpin = "^([0-9a-zA-Z\\/]){3,20}$";
        Pattern pattern = Pattern.compile(patternllpin);
        Matcher matcher = pattern.matcher(fieldValue);
        if (!isBlank(fieldName, fieldValue) && !matcher.matches())
            errors.rejectValue(fieldName, "errmsg.firmllpin");
    }
	
	
	public void isValidFile(MultipartFile file, Errors errors, boolean isRequired) {
		if (isRequired && (file.isEmpty() || file == null)) {
			errors.rejectValue("file", "errmsg.required");
		}
		else if(PDF_MIME_TYPE.equalsIgnoreCase(file.getContentType())){
			if(file.getSize() > MB_IN_BYTES){
			    errors.rejectValue("file", "errmsg.exceeded.size");
			}

			}else{
			errors.rejectValue("file", "errmsg.invalid.file");
			}

	}
	
	public void isValidMemberNo(String fieldName, String fieldValue, Errors errors)
    {
        if (fieldValue == null || "".equals(fieldValue.trim())
                || "null".equalsIgnoreCase(fieldValue.trim()))
        {
            errors.rejectValue(fieldName, "errmsg.required");
            return;
        }
        //String numberReg = "^[0-9]{10,10}$";
        String numberReg = "^([A-Fa-f0-9]){2,8}$";
        Pattern pattern = Pattern.compile(numberReg);
        Matcher matcher = pattern.matcher(fieldValue);
        if (!isBlank(fieldName, fieldValue) && !matcher.matches())
            errors.rejectValue(fieldName, "errmsg.memNo");
    }
	
	public void isValidDIN(String fieldName, String fieldValue, Errors errors)
    {
        if (fieldValue == null || "".equals(fieldValue.trim())
                || "null".equalsIgnoreCase(fieldValue.trim()))
        {
            errors.rejectValue(fieldName, "errmsg.required");
            return;
        }
        //String numberReg = "^[0-9]{10,10}$";
        String numberReg = "^[\\w\\d/-]{8,8}$";
        Pattern pattern = Pattern.compile(numberReg);
        Matcher matcher = pattern.matcher(fieldValue);
        if (!isBlank(fieldName, fieldValue) && !matcher.matches())
            errors.rejectValue(fieldName, "errmsg.din");
    }
   
	public void isValidPassport(String fieldName, String fieldValue, Errors errors)
    {
		 if (fieldValue == null || "".equals(fieldValue.trim())
	                || "null".equalsIgnoreCase(fieldValue.trim()))
	        {
	            errors.rejectValue(fieldName, "errmsg.required");
	            return;
	        }
	        //String numberReg = "^[0-9]{10,10}$";
	        String numberReg = "^[A-PR-WYa-pr-wy][1-9]\\d\\s?\\d{4}[1-9]$";        

        Pattern pattern = Pattern.compile(numberReg);
        Matcher matcher = pattern.matcher(fieldValue);
        if (!isBlank(fieldName, fieldValue) && !matcher.matches())
            errors.rejectValue(fieldName, "errmsg.passport");
    }
	
	public void isValidNationalId(String fieldName, String fieldValue, Errors errors)
    {
        if (fieldValue == null || "".equals(fieldValue.trim())
                || "null".equalsIgnoreCase(fieldValue.trim()))
        {
            errors.rejectValue(fieldName, "errmsg.required");
            return;
        }
        //String numberReg = "^[0-9]{10,10}$";
        String numberReg = "^([A-za-z0-9]){6,15}$";
        Pattern pattern = Pattern.compile(numberReg);
        Matcher matcher = pattern.matcher(fieldValue);
        if (!isBlank(fieldName, fieldValue) && !matcher.matches())
            errors.rejectValue(fieldName, "errmsg.nationalId");
    }

	 public boolean validateDateOfBirthFormat(final String date)
	    {
	    	
	    	 //String formateDate  =  date.replaceAll("/","-");
	    	
	    	
	    	Date birthdate1 = null;
			try {
				birthdate1 = new SimpleDateFormat("dd/MM/yyyy").parse(date);
			} catch (java.text.ParseException e) {
				// TODO Auto-generated catch block
				logger.info(e.getMessage());
			}
	    	
	    	LocalDate date1 = birthdate1.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

	    	


	    	LocalDate pdate =  date1;
	    	
	    	 LocalDate now = LocalDate.now();
	    	 
	    	 Period diff = Period.between(pdate, now);
	    	 if (pdate.isAfter(now)) {
	    	     
	    	    }
	    	 if (diff.getYears()<18) {
			return false;
	    	 }else {
	    		 return true ;
	    	 }
	    }

	public void isValidDropDown1(String fieldName, Long fieldValue, Errors errors) {
		 if (fieldValue == 0)
	        {
	            errors.rejectValue(fieldName, "errmsg.required");
	            return;
	        }
	}

	public void isValidEmail(String fieldName, String fieldValue, Errors errors, boolean b) {
		 if (fieldValue == null || "".equals(fieldValue.trim())
	                || "null".equalsIgnoreCase(fieldValue.trim()))
	        {
	            errors.rejectValue(fieldName, "errmsg.required");
	            return;
	        }
	        String emailReg = "^([\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4})?$";
	        Pattern pattern = Pattern.compile(emailReg);
	        Matcher matcher = pattern.matcher(fieldValue);
	        if (!isBlank(fieldName, fieldValue) && !matcher.matches())
	            errors.rejectValue(fieldName, "errmsg.email");
	}

	 public void isValidpanNumber(String fieldName, String fieldValue, Errors errors,String errMsg, boolean required) {
        
        if (required && (fieldValue == null || "".equals(fieldValue.trim()) || "null".equalsIgnoreCase(fieldValue.trim()))) {
          errors.rejectValue(fieldName, "errmsg.required");
          return;
        }
         String numberReg ="[A-Z]{5}[0-9]{4}[A-Z]{1}";
        
        Pattern pattern = Pattern.compile(numberReg);
        Matcher matcher = pattern.matcher(fieldValue);
        if (!isBlank(fieldName, fieldValue) && !matcher.matches())
          errors.rejectValue(fieldName, errMsg);
      }
              
	 public void isValidaadhar(String fieldName, String fieldValue, Errors errors,String errMsg, boolean required) {
	        
	        if (required && (fieldValue == null || "".equals(fieldValue.trim()) || "null".equalsIgnoreCase(fieldValue.trim()))) {
	          errors.rejectValue(fieldName, "errmsg.required");
	          return;
	        }
	         String numberReg = "^[2-9]{1}[0-9]{3}[0-9]{4}[0-9]{4}$";
	        
	        Pattern pattern = Pattern.compile(numberReg);
	        Matcher matcher = pattern.matcher(fieldValue);
	        if (!isBlank(fieldName, fieldValue) && !matcher.matches())
	          errors.rejectValue(fieldName, errMsg);
	      } 
	 public void isvalidLLPIN(String fieldName, String fieldValue, Errors errors, boolean required) {
			if (required && (fieldValue == null || "".equals(fieldValue.trim())
	                || "null".equalsIgnoreCase(fieldValue.trim())))
	        {
	            errors.rejectValue(fieldName, "errmsg.required");
	            return;
	        }
	        String patternCIN = "^([A-Za-z]{3}([-]{1})([0-9]{4}))$";
	        Pattern pattern = Pattern.compile(patternCIN);
	        Matcher matcher = pattern.matcher(fieldValue);
	        if (!isBlank(fieldName, fieldValue) && !matcher.matches())
	            errors.rejectValue(fieldName, "errmsg.llp ");
		}
	  

}