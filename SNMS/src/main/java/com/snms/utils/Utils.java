package com.snms.utils;

import java.io.IOException;
import java.io.InputStream;
import java.text.Format;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;
import java.util.Random;
import java.text.ParseException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class Utils {
	 private static final Logger logger = Logger.getLogger(Utils.class);
		
	@Autowired
    private MessageSource   errorSource;
	@Autowired
    private MessageSource   messageSource;
	
	private static final String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNPQRSTUVWXYZ0123456789abcdefghijklmnpqrstuvwxyz";
	
	public MessageSource getMessageSource() {
		return messageSource;
	}
	public String currentDate()
	{
		LocalDate today = LocalDate.now();
		return today.format(DateTimeFormatter.ofPattern("dd-MMM-yyyy"));
	}
	public String getCurrentDateFormat(Date date)
    {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        return sdf.format(date);
    }
	
	public static String getCurrentDateWithMonth(){
		LocalDate today = LocalDate.now();
	 String dated = today.format(DateTimeFormatter.ofPattern("dd-MMM-yyyy"));
	 return dated;
	}
	
	public static String formatMcaOrderDate(Date date)
	{
		Format formatter = new SimpleDateFormat("dd-MMM-yyyy");
		String s = formatter.format(date);
		return s.toUpperCase();
	}
	
	
	/*public static String getRandom3D(){
    	Random r = new Random( System.currentTimeMillis() );
		return String.valueOf(r.nextInt(900) + 100);
    }*/
	
		
		public static String getRandomAlphaNum(int count) {
		StringBuilder builder = new StringBuilder();
		while (count-- != 0) {
		int character = (int)(Math.random()*ALPHA_NUMERIC_STRING.length());
		builder.append(ALPHA_NUMERIC_STRING.charAt(character));
		}
		return builder.toString();
		}
	
	public String getCurrentDate() {
		SimpleDateFormat sdf = new SimpleDateFormat(SnmsConstant.DATE_FORMAT);
		return sdf.format(new Date());
	}
    
    public boolean checkCurrentDate(String date)
    {
    	SimpleDateFormat sdf = new SimpleDateFormat(
                SnmsConstant.DATE_FORMAT_III);
    	Date today = new Date();
    	Date selectedDate,todayDate;
    	try{
    		selectedDate = sdf.parse(date);
    		todayDate = sdf.parse(sdf.format(today));
    	}
    	catch(ParseException p)
    	{
    		return false;
    	}
    	if(todayDate.compareTo(selectedDate) == 0)
    		return true;
    	else
    		return false;
    }

	private final Properties configProp = new Properties();	
	 public Utils ()
	   {
	      //Private constructor to restrict new instances
	      InputStream in = this.getClass().getClassLoader().getResourceAsStream("messages.properties");
	      InputStream conf = this.getClass().getClassLoader().getResourceAsStream("config.properties");
	  
	      try {
	          configProp.load(in);
	          configProp.load(conf);
	      } catch (IOException e) {
	    	  logger.info(e.getMessage());
	      }
	      
	      finally {
				if (in != null) {
				safeClose(in);
				}
				if (conf != null) {
					safeClose(conf);
					}
			}
	   }
		
		
  private void safeClose(InputStream in) {
	  if (in != null) {
			try {
				in.close();
			} catch (IOException e) {
				logger.info(e.getMessage());
			}
			}
		
	}
public String getMessage(String key)
  {
      return configProp.getProperty(key);
  }

	public String getConfigMessage(String key) {
		 return configProp.getProperty(key);
	}

	public String getConfigMessage(String key, String[] param) {
		 if (param == null || param.length == 0)
	            return getConfigMessage(key);
	        return MessageFormat.format(getConfigMessage(key), (Object[]) param);
	}
	 public String getError(String key)
	 {
	        return errorSource.getMessage(key, null, Locale.ENGLISH);
	 }
	 
	 /**
	     * Method for error message to show the user
	     * 
	     * @param key - String variable of key
	     * @param param - String array
	     * @return error message in string variable
	     */
	   public String getError(String key, String[] param)
	   {
	        if (param == null || param.length == 0)
	            return getError(key);
	        if (param.length == 3)
	            return MessageFormat.format(getError(key), param[0], param[1],
	                    param[2]);
	        else if (param.length == 2)
	            return MessageFormat.format(getError(key), param[0], param[1]);
	        else
	            return MessageFormat.format(getError(key), param[0]);
	    }
	   
	   public String getOTP() {
			Random r = new Random(System.currentTimeMillis());
			return String.valueOf(100000 + r.nextInt(20000));
		}

	   public String getUpdatedOldPass(String oldpasses, String currentPass,BCryptPasswordEncoder passwordEncoder)
	    {
	    	if (oldpasses == null || "".equals(oldpasses)) 
	    	{
	    		return passwordEncoder.encode(currentPass)+SnmsConstant.OLD_PASS_SEP;
	    	}
	    	String oldpass[] = oldpasses.split(SnmsConstant.OLD_PASS_SEP);
	    	/*if (oldpass == null || oldpass.length <=0 || oldpass[1] == null || "".equals(oldpass[1]))
	    	{*/
	    	if (oldpass == null || oldpass.length <=0)
	        {
	   
	    		return passwordEncoder.encode(currentPass) + SnmsConstant.OLD_PASS_SEP;
	    	}
	    	String finalpass = "";
	    	boolean isFound = false;
	    	for(int i=0; i<oldpass.length;i++)
	    	{
	    		if (passwordEncoder.matches(currentPass,oldpass[i]))
	    		{
	    			isFound = true;
	    			break;
	    		}
	    	}
	    	if (isFound)
	    		return oldpasses;
	    	else 
	    	{
	    		
	    		if (oldpass.length == 3)
	    		{
	    			
	    			for(int i=1;i<oldpass.length;i++)
	    			{
	    				
	    				finalpass = finalpass + SnmsConstant.OLD_PASS_SEP + oldpass[i];
	    			}
	    			finalpass =  finalpass + SnmsConstant.OLD_PASS_SEP + passwordEncoder.encode(currentPass);
	    			return finalpass;
	    		}
	    		else if (oldpass.length > 3)
	    		{
	    			
	    			for(int i=2;i<=oldpass.length-1;i++)
	    			{
	    				
	    				finalpass = finalpass + SnmsConstant.OLD_PASS_SEP + oldpass[i];
	    			}
	    			finalpass =  finalpass + SnmsConstant.OLD_PASS_SEP + passwordEncoder.encode(currentPass);
	    			
	    			return finalpass;
	    		}
	    		else
	    		{
	    			if (oldpass.length == 1)
	    			   finalpass =  oldpasses + passwordEncoder.encode(currentPass);
	    			else 
	    			   finalpass =  oldpasses + SnmsConstant.OLD_PASS_SEP + passwordEncoder.encode(currentPass);
	    			return finalpass;    			
	    		}
	    	}
	    }


	   public String getResetOldPass(String oldpasses, String currentPass,BCryptPasswordEncoder passwordEncoder)
	    {
	    	if (oldpasses == null || "".equals(oldpasses)) 
	    	{
	    		return passwordEncoder.encode(currentPass)+SnmsConstant.OLD_PASS_SEP;
	    	}
	    	String oldpass[] = oldpasses.split(SnmsConstant.OLD_PASS_SEP);
	    	/*if (oldpass == null || oldpass.length <=0 || oldpass[1] == null || "".equals(oldpass[1]))
	    	{*/
	    	if (oldpass == null || oldpass.length <=0)
	        {
	   
	    		return passwordEncoder.encode(currentPass) + SnmsConstant.OLD_PASS_SEP;
	    	}
	    	String finalpass = "";
	    		System.out.println(oldpass.length);
	    		if (oldpass.length == 3)
	    		{
	    			
	    			for(int i=1;i<oldpass.length;i++)
	    			{
	    				
	    				finalpass = finalpass + SnmsConstant.OLD_PASS_SEP + oldpass[i];
	    			}
	    			finalpass =  finalpass + SnmsConstant.OLD_PASS_SEP + passwordEncoder.encode(currentPass);
	    			return finalpass;
	    		}
	    		else if (oldpass.length > 3)
	    		{
	    			
	    			for(int i=2;i<=oldpass.length-1;i++)
	    			{
	    				
	    				finalpass = finalpass + SnmsConstant.OLD_PASS_SEP + oldpass[i];
	    			}
	    			finalpass =  finalpass + SnmsConstant.OLD_PASS_SEP + passwordEncoder.encode(currentPass);
	    			
	    			return finalpass;
	    		}
	    		else
	    		{
	    			if (oldpass.length == 1)
	    			   finalpass =  oldpasses + passwordEncoder.encode(currentPass);
	    			else 
	    			   finalpass =  oldpasses + SnmsConstant.OLD_PASS_SEP + passwordEncoder.encode(currentPass);
	    			return finalpass;    			
	    		}
	    	}
	    



}
