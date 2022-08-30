package com.snms.service;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import com.snms.dto.MailInfo;
import com.snms.utils.SnmsConstant;
import com.snms.utils.Utils;

@Component("mailBo")
public class MailBoImpl implements MailBo
{
	private final int MAX_DATA = 4000;
	private final int EXPECTED_BUFFER_DATA = 10000;
    /** JavaMailSender object to send mail **/
    @Autowired
    private JavaMailSender mailSender;

    /** SimpleMailMessage object to configure the mail message properties **/
    @Autowired
    SimpleMailMessage      emailConfig;

    /** Utils object */
    @Autowired
    private Utils          utils;
    

    public SimpleMailMessage getEmailConfig() 
    {
        return emailConfig;
    }

    public void setEmailConfig(SimpleMailMessage emailConfig)
            throws  Exception
    {
        this.emailConfig = emailConfig;
    }

    /**
     * Method for sending mail
     * 
     * @param info - object of MailInfo
     * @param messageType - int variable for messageType
     * @param req - object of HttpServletRequest
     * @throws SnmsException
     */
    @Override
    public void sendMail(MailInfo info, int messageType, HttpServletRequest req) throws  Exception
           
    {
        if (!"true".equalsIgnoreCase(utils.getConfigMessage("mail.enabled"))
                || "".equals(utils.getConfigMessage("mail.enabled")))
            return;
//        String imageUrl = utils.getMailImageUrl(req);
        try
        {
            MimeMessage message = null;
            message = getEmailTemplate(info, messageType, "", req);
            mailSender.send(message);
        }
       /* catch (AddressException ae)
        {
            dex = new NfraException(ae.getMessage(), ae.getCause());
            dex.setERROR_CODE("002");
            dex.setParameter(new String[]
            {info.getEmail()});
            throw dex;
        }
        catch (MessagingException me)
        {
            dex = new NfraException(me.getMessage(), me.getCause());
            dex.setERROR_CODE("003");
            throw dex;
        }*/
        catch (Exception er)
        {
           /* dex = new NfraException(er.getMessage(), er.getCause());
            dex.setERROR_CODE("004");
            dex.setParameter(new String[]
            {info.getEmail()});
            throw dex;*/
        	er.printStackTrace();
        }
    }

    /**
     * Method for sending mail
     * 
     * @param info - object of MailInfo
     * @param messageType - int variable for messageType
     * @param imageUrl - String variable for mail image path
     * @throws SnmsException
     * @throws MessagingException
     * @throws AddressException
     */
    private MimeMessage getEmailTemplate(MailInfo info, int messageType,
            String imageUrl, HttpServletRequest req)
            throws AddressException, MessagingException
    {

    	MimeMessage message = mailSender.createMimeMessage();
        message.addRecipients(Message.RecipientType.TO, new Address[]
        {new InternetAddress(info.getEmail())});
        message.setFrom(new InternetAddress(emailConfig.getFrom()));
        StringBuffer content = new StringBuffer(EXPECTED_BUFFER_DATA);
    
        String data = utils.getConfigMessage("mail.header.css");
        String data2 = utils.getConfigMessage("mail.body.header", new String[]{imageUrl});
        if (data.length() < MAX_DATA) content.append(data);

        if (data2.length() < MAX_DATA) content.append(data2);
        String msg = "";
        switch (messageType)
        {
        	case SnmsConstant.USER_CREATE :
            message.setSubject("SNMS : User Created SuccessFully");
            msg = utils.getConfigMessage("mail.body.usercreate",new String[]{info.getEmail()});
            break;
            
        	case SnmsConstant.Password_Reset :
                message.setSubject("SNMS : Password Change Request");
                msg = utils.getConfigMessage("mail.body.forgotpasswordotp",new String[]{info.getName(), info.getOtp()});
                break;
        }
    
        if (msg.length() < MAX_DATA) content.append(msg);
         String mailfoot =utils.getConfigMessage("mail.footer.css");     
         if (mailfoot.length() < MAX_DATA) content.append(mailfoot);
        message.setText(content.toString(), "utf-8", "html");
        return message;
    }

	/*@Override
	public void sendMessage(String obj, int smsType,String mobileno) throws  Exception
	{
		if (!"true".equalsIgnoreCase(utils.getConfigMessage("sms.enabled"))
                || "".equals(utils.getConfigMessage("sms.enabled")))
            return;
		String api="";
		TrustManager[] trustAllCerts = new TrustManager[]{
	    	    new X509TrustManager() {
	    	        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
	    	            return null;
	    	        }
	    	        public void checkClientTrusted(
	    	            java.security.cert.X509Certificate[] certs, String authType) {
	    	        }
	    	        public void checkServerTrusted(
	    	            java.security.cert.X509Certificate[] certs, String authType) {
	    	        }
	    	    }
	    	};
		try{
			SSLContext sc = SSLContext.getInstance("SSL");
	    	 sc.init(null, trustAllCerts,new java.security.SecureRandom());
			switch (smsType)
	        {
	        case NfraConstant.SMS_VARIFY_OTP :
	            api="https://smsgw.sms.gov.in/failsafe/HttpLink?username=nfra.sms&pin=3f51rfok&message="+URLEncoder.encode(utils.getConfigMessage("sms.body.otp", new String[]{obj}), "UTF-8")+"&mnumber=" +mobileno+ "&signature=NFINRA";
	            break; 
	            case NfraConstant.SMS_SUCCESS_OTP :
		          api="https://smsgw.sms.gov.in/failsafe/HttpLink?username=nfra.sms&pin=3f51rfok&message="
		          +URLEncoder.encode(utils.getConfigMessage("sms.body.otpvarified",new String[]{obj}),"UTF-8")+ "&mnumber=" +mobileno+ "&signature=NFINRA";
		          break; 
	        }
		URL url = new URL(api);
		HttpsURLConnection uc = (HttpsURLConnection)url.openConnection();
	    uc.setSSLSocketFactory(sc.getSocketFactory());
	    uc.connect();
	    
	    uc.disconnect();
	    }
		catch(Exception ex){
	   
	    }
		
	}*/
	
//	Get Auto reminder mail template
	
   
}
