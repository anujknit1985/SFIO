package com.snms.validation;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.apache.tika.Tika;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.RecursiveParserWrapper;
import org.apache.tika.sax.BasicContentHandlerFactory;
import org.apache.tika.sax.ContentHandlerFactory;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.errors.IntrusionException;
import org.owasp.esapi.errors.ValidationException;
import org.springframework.validation.BindingResult;
/*import org.apache.log4j.Logger;
import org.apache.tika.Tika;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.RecursiveParserWrapper;
import org.apache.tika.sax.BasicContentHandlerFactory;
import org.apache.tika.sax.ContentHandlerFactory;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.errors.IntrusionException;
import org.owasp.esapi.errors.ValidationException;*/
import org.springframework.validation.Errors;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.helpers.DefaultHandler;

import com.snms.entity.CaseDetails;

//import nl.captcha.Captcha;
//import nl.captcha.CaptchaBean;

public class SNMSValidator
{
 
  private static final Logger      logger = Logger
      .getLogger(SNMSValidator.class);
  public static final String PDF_MIME_TYPE="application/pdf";
 // public static final long MB_IN_BYTES = 1048576; // 1 MB file size
  public static final long KB_IN_BYTES = 256000; // 250 kb file size
  public static final long MB_IN_BYTES = 2097152; // 2 MB file size
    public void isValidDropDown(String fieldName, int fieldValue, Errors errors)
    {
        if (fieldValue == 0)
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
    public void isValidEmail(String fieldName, String fieldValue, Errors errors,boolean required)
    {
    	
        if (required && (fieldValue == null || "".equals(fieldValue.trim()))
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
        String numberReg;
        
        if(fieldValue.contains("-")) {
        	   numberReg = "^[0-9]\\d{2,4}-\\d{6,15}$";
        }else {
        	numberReg = "^[0-9]{10,10}$";
        }
        Pattern pattern = Pattern.compile(numberReg);
        Matcher matcher = pattern.matcher(fieldValue);
        if(fieldValue.contains("-")) {
        	if (!isBlank(fieldName, fieldValue) && !matcher.matches())
                errors.rejectValue(fieldName, "errmsg.landline");
     }else {
        if (!isBlank(fieldName, fieldValue) && !matcher.matches())
            errors.rejectValue(fieldName, "errmsg.mobile");
     }
    }
    public void isValidMobilenonmadatory(String fieldName, String fieldValue,
            Errors errors)
    {
    	
     
        String numberReg;
        
        if(fieldValue.contains("-")) {
        	   numberReg = "^[0-9]\\d{2,4}-\\d{6,15}$";
        }else {
        	numberReg = "^[0-9]{10,10}$";
        }
        Pattern pattern = Pattern.compile(numberReg);
        Matcher matcher = pattern.matcher(fieldValue);
        if(fieldValue.contains("-")) {
        	if (!isBlank(fieldName, fieldValue) && !matcher.matches())
                errors.rejectValue(fieldName, "errmsg.landline");
     }else {
        if (!isBlank(fieldName, fieldValue) && !matcher.matches())
            errors.rejectValue(fieldName, "errmsg.mobile");
     }
    }
    public void isValidlandMobile(String fieldName, String fieldValue,
            Errors errors)
    {
    	
        if (fieldValue == null || "".equals(fieldValue.trim())
                || "null".equalsIgnoreCase(fieldValue.trim()))
        {
            errors.rejectValue(fieldName, "errmsg.required");
            return;
        }
        String numberReg;
        
       
        	   numberReg = "^[0-9]\\d{2,4}-\\d{6,15}$";
       
        Pattern pattern = Pattern.compile(numberReg);
        Matcher matcher = pattern.matcher(fieldValue);
       
        	if (!isBlank(fieldName, fieldValue) && !matcher.matches())
                errors.rejectValue(fieldName, "errmsg.landline");
    
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
    
    
  //gouthami 15/09/2020
    public boolean getValidInteger( long id) {
    	  boolean flag = true;
    	if(id==0) {
    	
    		   flag = false;
    	}
    	
    	if(String.valueOf(id)==null) {
    		 flag = false;
    	}
    	else {
    		
    		 String numberReg = "[+]?[0-9][0-9]*";
//           String numberReg = "^([0-9/]){3,}$";
           Pattern pattern = Pattern.compile(numberReg);
           Matcher matcher = pattern.matcher(String.valueOf(id));
           if ( !matcher.matches())
              flag = false;
    	}
		return flag;
    }
  
    
    public boolean getValidClaus( StringBuilder Clause) {
  	  boolean flag = true;
  	if(Clause==null) {
  	
  		   flag = false;
  	}else {
  		String numberReg = "\\(([a-z)]+)\\)";
//      String numberReg = "^([0-9/]){3,}$";
      Pattern pattern = Pattern.compile(numberReg);
      Matcher matcher = pattern.matcher(String.valueOf(Clause));
      if ( !matcher.matches())
         flag = false;
  	}
		return flag;
  }
    
    public boolean isvalidCompanyName( String fieldValue) {
    	boolean flag = true;
	      if (fieldValue == null || "".equals(fieldValue.trim()) || "null".equalsIgnoreCase(fieldValue.trim())) {
	      
	    	  flag = false;
	      }
	      String firmCity="^[a-zA-Z0-9\\s-&()._]{1,100}$";
	      Pattern pattern = Pattern.compile(firmCity);
	      Matcher matcher = pattern.matcher(fieldValue);
	      if ( !matcher.matches()) {
	    	  flag = false;
	      }
	      return flag;
	    }

    //gouthami 15/09/2020
    public boolean getValidZeroInteger( long id) {
    	  boolean flag = true;
    
    		
    		 String numberReg = "[+]?[0-9][0-9]*";
//           String numberReg = "^([0-9/]){3,}$";
           Pattern pattern = Pattern.compile(numberReg);
           Matcher matcher = pattern.matcher(String.valueOf(id));
           if ( !matcher.matches())
              flag = false;
    	
		return flag;
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
    public boolean isgetBlank(String fieldValue)
    {
        boolean flag = false;
        if (fieldValue == null || "".equals(fieldValue.trim()))
        {
            flag = true;
        }
        return flag;
    }
    
   /* public int getValidInteger(String dirname) {
      int status = -1;
      try {
        status = ESAPI.validator().getValidInteger("testInt", dirname, 0, Integer.MAX_VALUE, false);
      } catch (IntrusionException | ValidationException e) {
       logger.info(e.getMessage());
      }
      return status;
    }
    
    public String getSafeString(String value) {
      String status = "";
      try {
        status = ESAPI.validator().getValidInput("StringTest", value, "SafeString", Integer.MAX_VALUE, false);
      } catch (IntrusionException | ValidationException e) {
        logger.error(e.getMessage(), e);
      }
      return status;
    }*/

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
        String fnameReg = "^([0-9a-zA-Z\\/]){1,20}$";
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
      String address="^[\\w\\d\\s,._:()/-]{3,200}$";
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
      String fnameReg = "^[a-zA-Z\\s.-_&()]{5,100}$";
      Pattern pattern = Pattern.compile(fnameReg);
      Matcher matcher = pattern.matcher(fieldValue);
      if (!isBlank(fieldName, fieldValue) && !matcher.matches())
        errors.rejectValue(fieldName, errMsg);
    }
    public void isvalidorder(String fieldName, String fieldValue, Errors errors,String errMsg, boolean required) {
    
      if (required && (fieldValue == null || "".equals(fieldValue.trim()) || "null".equalsIgnoreCase(fieldValue.trim()))) {
        errors.rejectValue(fieldName, "errmsg.required");
        return;
      }
      String fnameReg = "^[\\w\\d\\s._()/-]{5,50}$";
      Pattern pattern = Pattern.compile(fnameReg);
      Matcher matcher = pattern.matcher(fieldValue);
      if (!isBlank(fieldName, fieldValue) && !matcher.matches())
        errors.rejectValue(fieldName, errMsg);
    }
    public void isvalidPersonName(String fieldName, String fieldValue, Errors errors,String errMsg, boolean required) {
    
      if (required && (fieldValue == null || "".equals(fieldValue.trim()) || "null".equalsIgnoreCase(fieldValue.trim()))) {
        errors.rejectValue(fieldName, "errmsg.required");
        return;
      }
      String fnameReg = "^[a-zA-Z\\s.-_&()]{1,70}$";
      Pattern pattern = Pattern.compile(fnameReg);
      Matcher matcher = pattern.matcher(fieldValue);
      if (!isBlank(fieldName, fieldValue) && !matcher.matches())
        errors.rejectValue(fieldName, errMsg);
    }
    public Boolean getvalidorder( String fieldValue) {
        
        if ((fieldValue == null || "".equals(fieldValue.trim()) || "null".equalsIgnoreCase(fieldValue.trim()))) {
          
          return false;
        }
        String fnameReg = "^[\\w\\d\\s._()/-]{5,50}$";
        Pattern pattern = Pattern.compile(fnameReg);
        Matcher matcher = pattern.matcher(fieldValue);
        if (!isgetBlank( fieldValue) && !matcher.matches()) {
         
		return false;
        }
		else
			return true;
      }
    
    public void isvalidSalutation(String fieldName, String fieldValue, Errors errors,String errMsg, boolean required) {
    
        if (required && (fieldValue == null || "".equals(fieldValue.trim()) || "null".equalsIgnoreCase(fieldValue.trim()))) {
          errors.rejectValue(fieldName, "errmsg.required");
          return;
        }
        String fnameReg = "^[a-zA-Z]{2,3}$";
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
        String fnameReg = "^[a-zA-Z\\s]{2,40}$";
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

    /*public boolean isValidCaptcha(String captcha, Errors errors) {
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        //CaptchaBean cptBean = (CaptchaBean) (attr.getRequest().getSession().getAttribute("captcha_security"));
        
        String captch  = (String) attr.getRequest().getSession().getAttribute("captcha_security");
          attr.getRequest().getSession().removeAttribute(NAME);
        return (captcha != null && attr.getRequest().getSession().getAttribute("captcha_security") != null && attr.getRequest().getSession().getAttribute("captcha_security").equals(captcha));
      }*/
    
    public void isValidData(String fieldname, String fieldValue,
        Errors errors) {
    if (fieldValue == null)
        
    {
        errors.rejectValue(fieldname, "errmsg.invalid");
        return;
    }
    String dataReg = "^[\\w\\d\\s._!@+#$:()/=-]+$";
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
        String address="^[\\w\\d\\s.,_()/-]{2,70}$";
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
	
/*	public void isvalidFCRN(String fieldName, String fieldValue, Errors errors, String errMsg, boolean required) {
		if (required && (fieldValue == null || "".equals(fieldValue.trim())
                || "null".equalsIgnoreCase(fieldValue.trim())))
        {
            errors.rejectValue(fieldName, "errmsg.required");
            return;
        }
//        String patternFCRN = "^([0-9a-zA-Z]){6}$";
        String patternFCRN = "^[A-Z]{1}[0-9]{5}$";
        Pattern pattern = Pattern.compile(patternFCRN);
        Matcher matcher = pattern.matcher(fieldValue);
        if (!isBlank(fieldName, fieldValue) && !matcher.matches())
            errors.rejectValue(fieldName, errMsg);
	}*/

	public void isvalidFCRN(String fieldName, String fieldValue, Errors errors, String errMsg, boolean required) {
		if (required && (fieldValue == null || "".equals(fieldValue.trim())
                || "null".equalsIgnoreCase(fieldValue.trim())))
        {
            errors.rejectValue(fieldName, "errmsg.required");
            return;
        }
		String patternFCRN="";
		if(fieldValue.indexOf(0)=='F')
			patternFCRN = "^[F]{1}[0-9]{5}$";
		else
			patternFCRN = "[A-Z0-9]{6,13}$";
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
	
	public void isvalidUnit(String fieldName, String fieldValue, Errors errors, String errMsg, boolean required) {
		 if (required && (fieldValue == null || "".equals(fieldValue.trim()) || "null".equalsIgnoreCase(fieldValue.trim()))) {
		        errors.rejectValue(fieldName, "errmsg.required");
		        return;
		      }
		      String fnameReg = "^[a-zA-Z0-9\\s.-_ /()-]{2,20}$";
		      Pattern pattern = Pattern.compile(fnameReg);
		      Matcher matcher = pattern.matcher(fieldValue);
		      if (!isBlank(fieldName, fieldValue) && !matcher.matches())
		        errors.rejectValue(fieldName, errMsg);
	}
	public void isvalidLocation(String fieldName, String fieldValue, Errors errors, String errMsg, boolean required) {
		 if (required && (fieldValue == null || "".equals(fieldValue.trim()) || "null".equalsIgnoreCase(fieldValue.trim()))) {
		        errors.rejectValue(fieldName, "errmsg.required");
		        return;
		      }
		      String fnameReg = "^[a-zA-Z\\s.-_/()]{2,40}$";
		      Pattern pattern = Pattern.compile(fnameReg);
		      Matcher matcher = pattern.matcher(fieldValue);
		      if (!isBlank(fieldName, fieldValue) && !matcher.matches())
		        errors.rejectValue(fieldName, errMsg);
		      
	}
	
	public void isvalidFinanceYear(String fieldName, String fieldValue, Errors errors, String errMsg, boolean required) {
		 if (required && (fieldValue == null || "".equals(fieldValue.trim()) || "null".equalsIgnoreCase(fieldValue.trim()))) {
		        errors.rejectValue(fieldName, "errmsg.required");
		        return;
		      }
		      String fnameReg = "(20)\\d{2}-(20)\\d{2}";
		      Pattern pattern = Pattern.compile(fnameReg);
		      Matcher matcher = pattern.matcher(fieldValue);
		      if (!isBlank(fieldName, fieldValue) && !matcher.matches())
		        errors.rejectValue(fieldName, errMsg);
		      else{
		    	  if(Integer.parseInt(fieldValue.substring(fieldValue.lastIndexOf('-')+1))-
		    			  Integer.parseInt(fieldValue.substring(0,fieldValue.lastIndexOf('-')))!=1)
		    		  errors.rejectValue(fieldName, "errmsg.finYearSeq");
		      }
	}


	  public void isValidFax(String fieldName, String fieldValue, Errors errors)
	  {
	        if (fieldValue == null || "".equals(fieldValue.trim())
	                || "null".equalsIgnoreCase(fieldValue.trim()))
	        {
	            errors.rejectValue(fieldName, "errmsg.required");
	            return;
	        }
//	        String numberReg = "/^\\+?[0-9]{6,10}$/";
	        String numberReg = "^[0-9-]{6,12}$";
	        
//	        String numberReg = "^([0-9/]){3,}$";
	        Pattern pattern = Pattern.compile(numberReg);
	        Matcher matcher = pattern.matcher(fieldValue);
	        if (!isBlank(fieldName, fieldValue) && !matcher.matches())
	            errors.rejectValue(fieldName, "errmsg.fax");
	   }
   
	public void isvalidCaseId(String fieldName, String fieldValue, BindingResult errors, boolean required) {
		if (required && (fieldValue == null || "".equals(fieldValue.trim())
                || "null".equalsIgnoreCase(fieldValue.trim())))
        {
            errors.rejectValue(fieldName, "errmsg.required");
            return;
        }
        String patternCIN = "^[a-zA-Z0-9\\s/-]{5,100}$";
        Pattern pattern = Pattern.compile(patternCIN);
        Matcher matcher = pattern.matcher(fieldValue);
        if (!isBlank(fieldName, fieldValue) && !matcher.matches())
            errors.rejectValue(fieldName, "errmsg.caseid");
	}
	
	 public void isvalidCompany(String fieldName, String fieldValue, Errors errors,String errMsg, boolean required) {
	      if (required && (fieldValue == null || "".equals(fieldValue.trim()) || "null".equalsIgnoreCase(fieldValue.trim()))) {
	        errors.rejectValue(fieldName, "errmsg.required");
	        return;
	      }
	      String firmCity="^[a-zA-Z0-9\\s-&()._]{1,100}$";
	      Pattern pattern = Pattern.compile(firmCity);
	      Matcher matcher = pattern.matcher(fieldValue);
	      if (!isBlank(fieldName, fieldValue) && !matcher.matches())
	        errors.rejectValue(fieldName, errMsg);
	    }

	 
	 //file uploads validation
	 public void isValidFile(MultipartFile file, Errors errors, boolean isRequired,String errFieldName) {
		if (isRequired && (file.isEmpty() || file == null)) {
				errors.rejectValue(errFieldName, "errmsg.required");
			}
		else if(PDF_MIME_TYPE.equalsIgnoreCase(file.getContentType())){
				if(file.getSize() > MB_IN_BYTES){
				    errors.rejectValue(errFieldName, "errmsg.exceeded.size");
			}
			}else{
				errors.rejectValue(errFieldName, "errmsg.invalid.file");
			}
		}
		
	 
	 public Boolean isValidFileCheck(MultipartFile file, boolean isRequired) {
		if (isRequired && (file.isEmpty() || file == null)) {
			return false;
			}
		else if(PDF_MIME_TYPE.equalsIgnoreCase(file.getContentType())){
				if(file.getSize() > MB_IN_BYTES){
				   return false;
			}
			}else{
				return false;
			}
		return true;
		}
	 
	    public boolean isValidFileName(String fileName)
	    {
	        if (fileName == null)
	            return false;
	        if (fileName.lastIndexOf(".") <= 0)
	            return true;
	        String allowExt = "pdf";
	        String fileReg = "([^*$#@!%&]+(\\.(?i)(" + allowExt + "))$)";
	        Pattern pattern = Pattern.compile(fileReg);
	        Matcher matcher = pattern.matcher(fileName.trim().toLowerCase());
	        if (!matcher.matches())
	        {
	             return false;
	        }
	        // To check Filename contains more than one (.)dot");
	        if (fileName.split("\\.").length > 2)
	        {
	            return false;
	        }
	        return true;
	    }

	    public boolean isValidMime(String requestMime)
	    {
	        String allowedMime="application/pdf";
	        if (requestMime != null && !"".equals(requestMime) && requestMime.equalsIgnoreCase("pdf"))
	                return true;
	        String mimetype[] = allowedMime.split(",");
	        for (int i = 0; i < mimetype.length; i++)
	        {
	         if (requestMime != null && !"".equals(requestMime)
	                    && requestMime.equalsIgnoreCase(mimetype[i]))
	                return true;
	        }
	        return false;
	    }

	    public boolean isValidFileTikka(String filename, MultipartFile part) throws IOException
	    {
	    	String allowedMime="application/pdf,application/PDF";
	        String mimetype[] = allowedMime.split(",");
	        List<String> mimelist = Arrays.asList(mimetype);
	        Tika tika = new Tika();
	        boolean correct = false;
	        InputStream  filepart = part.getInputStream();
	        try
	        {
	            String mediaType = tika.detect(filepart);
	            //logger.info(filename + "  " + mediaType);
	            if (mimelist.contains(mediaType))
	                correct = true;
	          
	            if (!correct)
	                return false;

	            if ("application/pdf".equalsIgnoreCase(mediaType))
	            {
	                Parser p = new AutoDetectParser();
	                ContentHandlerFactory factory = new BasicContentHandlerFactory(
	                        BasicContentHandlerFactory.HANDLER_TYPE.HTML, -1);

	                RecursiveParserWrapper wrapper = new RecursiveParserWrapper(p,
	                        factory);
	                Metadata metadata = new Metadata();
	                ParseContext context = new ParseContext();

	                try (InputStream stream = (part.getInputStream()))
	                {
	                    wrapper.parse(stream, new DefaultHandler(), metadata,
	                            context);
	                }
	                List<Metadata> l = wrapper.getMetadata();
	                for (int k = 0; k < l.size(); k++)
	                {
	                    //logger.info(l.get(k).get(Metadata.CONTENT_TYPE));
	                    if (!mimelist.contains(l.get(k).get(Metadata.CONTENT_TYPE)))
	                        return false;
	                }
	            }
	            return true;
	        }
	        catch (Exception e1)
	        {
	           // logger.error(e1.getMessage(), e1);
	            return false;
	        }
	        
	        finally {
				if (filepart != null) {
				safeClose(filepart);
				}
			}
	    }
	    
	    private void safeClose(InputStream filepart) {
	    	if (filepart != null) {
				try {
					filepart.close();
				} catch (IOException e) {
					logger.info(e.getMessage());
				}
				}
			
		}

		public String getSafeString(String value) {
	        String status = "";
	        try {
	          status = ESAPI.validator().getValidInput("StringTest", value, "SafeString", Integer.MAX_VALUE, false);
	        } catch (IntrusionException | ValidationException e) {
	          logger.error(e.getMessage(), e);
	        }
	        return status;
	    }

	    public boolean  getvalidCompany( String fieldValue , boolean required) {
		      if (required && (fieldValue == null || "".equals(fieldValue.trim()) || "null".equalsIgnoreCase(fieldValue.trim()))) {
		      
		        return  false ;
		      }
		      String firmCity="^[a-zA-Z0-9\\s-&()._]{1,100}$";
		      Pattern pattern = Pattern.compile(firmCity);
		      Matcher matcher = pattern.matcher(fieldValue);
		      if (!isgetBlank(fieldValue) && !matcher.matches())
		     return false;
		      else 
		      return true;
		    }
	   
 public Boolean isValidName(String fieldValue) {
	        
	        if ((fieldValue == null || "".equals(fieldValue.trim()) || "null".equalsIgnoreCase(fieldValue.trim()))) {
	        
	          return false;
	        }
	        String fnameReg = "^[a-zA-Z\\s.-_&()]{1,35}$";
	        Pattern pattern = Pattern.compile(fnameReg);
	        Matcher matcher = pattern.matcher(fieldValue);
	        if (!isgetBlank(fieldValue) && !matcher.matches())
	        	 return false;
	        else
	    return true;
}

 public boolean validateDOB(String format) throws ParseException {
		// TODO Auto-generated method stub
	     Date  CurrentDate = new Date();
	     Date date1=new SimpleDateFormat("dd/MM/yyyy").parse(format);

	     if(date1.after(CurrentDate)){
	    	 return false;
	    	}
		return true;
	}
 public boolean validateDOB1(String format) throws ParseException {
		// TODO Auto-generated method stub
	     Date  CurrentDate = new Date();
	     Date date1=new SimpleDateFormat("dd-MM-yyyy").parse(format);

	     if(date1.after(CurrentDate)){
	    	 return false;
	    	}
		return true;
	}
	public boolean validatedateces(String dateAppointment, String dateCessation) throws ParseException {
		// TODO Auto-generated method stub
		
		Date date1=new SimpleDateFormat("dd/MM/yyyy").parse(dateAppointment);
		Date date2=new SimpleDateFormat("dd/MM/yyyy").parse(dateCessation);
	if(date2.before(date1)) {
		return false;
	}
	return true;
	}
	public boolean validatedatappoitAnddob(String dateAppointment, String dob) throws ParseException {
		// TODO Auto-generated method stub
		
		Date date1=new SimpleDateFormat("dd/MM/yyyy").parse(dateAppointment);
		Date date2=new SimpleDateFormat("dd/MM/yyyy").parse(dob);
	if(date1.before(date2)) {
		return false;
	}
	if(date1.equals(date2)) {
		return false;
	}
	return true;
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
	    
	 public void isValidVoterNumber(String fieldName, String fieldValue, Errors errors) {
	        
		 if (fieldValue == null || "".equals(fieldValue.trim())
	                || "null".equalsIgnoreCase(fieldValue.trim()))
	        {
	            errors.rejectValue(fieldName, "errmsg.required");
	            return;
	        }
	        //String numberReg = "^[0-9]{10,10}$";
	        String numberReg = "^([a-zA-Z]){3}([0-9]){7}$";        

	        Pattern pattern = Pattern.compile(numberReg);
	        Matcher matcher = pattern.matcher(fieldValue);
	        if (!isBlank(fieldName, fieldValue) && !matcher.matches())
	            errors.rejectValue(fieldName, "errmsg.voterId");
	      }
	    
	 
	
	   public void isValidpasportNumber(String fieldName, String fieldValue, Errors errors)
	    {
	        if (fieldValue == null || "".equals(fieldValue.trim())
	                || "null".equalsIgnoreCase(fieldValue.trim()))
	        {
	            errors.rejectValue(fieldName, "errmsg.required");
	            return;
	        }
	        //String numberReg = "^[0-9]{10,10}$";
	       // String numberReg = "^[A-Za-z][0-9]\\d\\s?\\d{4}[0-9]$"; 
	        String numberReg = "^[A-Za-z0-9]{0,9}$";

	        Pattern pattern = Pattern.compile(numberReg);
	        Matcher matcher = pattern.matcher(fieldValue);
	        if (!isBlank(fieldName, fieldValue) && !matcher.matches())
	            errors.rejectValue(fieldName, "errmsg.passport");
	    }
	   
	   public void isValidpasportNumber(String fieldName, String fieldValue, long countryCode , Errors errors)
	    {
	        if (fieldValue == null || "".equals(fieldValue.trim())
	                || "null".equalsIgnoreCase(fieldValue.trim()))
	        {
	            errors.rejectValue(fieldName, "errmsg.required");
	            return;
	        }
	        //String numberReg = "^[0-9]{10,10}$";
	        String numberReg ;
	       
        if(countryCode == 74) {	        
	        numberReg = "^[A-Za-z0-9]{0,9}$";
        }else {
	        numberReg = "^[A-Za-z][0-9]\\d\\s?\\d{4}[0-9]$"; 
        }
	        Pattern pattern = Pattern.compile(numberReg);
	        Matcher matcher = pattern.matcher(fieldValue);
	        if (!isBlank(fieldName, fieldValue) && !matcher.matches())
	            errors.rejectValue(fieldName, "errmsg.passport");
	    }

	public void validateCompNameOrder(@Valid CaseDetails caseDetails1, BindingResult errors) throws IOException {
   MultipartFile companyOrder = caseDetails1.getCompanyOrder();
   String compName = caseDetails1.getUpCompName();
   SNMSValidator snmsVal = new SNMSValidator();
	  snmsVal.isvalidCompany("upCompName",compName, errors, "errmsg.company", true); ;
		 if(companyOrder.getSize() > 0 || !companyOrder.isEmpty())
		 {
			 snmsVal.isValidFile(companyOrder, errors, true,"companyOrder");
			 if(!snmsVal.isValidFileName(companyOrder.getOriginalFilename()))
					errors.rejectValue("companyOrder", "errmsg.filename");
			 if(!snmsVal.isValidFileTikka(companyOrder.getOriginalFilename(), companyOrder))
					errors.rejectValue("companyOrder", "errmsg.maliciousdata");
		 }else {
			 errors.rejectValue("companyOrder", "errmsg.required");
		 }
		
		
	}
}