package com.snms.service;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.snms.dao.AppUserDAO;
import com.snms.entity.AppUser;
import com.snms.utils.Crypt;
import com.snms.utils.NoticePdf;
import com.snms.utils.SaltGenerator;
import com.snms.utils.Utils;
import com.snms.validation.SNMSValidator;
@Component
public class CustomAuthenticationProvider implements AuthenticationProvider
{
	 private static final Logger logger = Logger.getLogger(CustomAuthenticationProvider.class);
		
	@Autowired
	private UserDetailsServiceImpl userDetailsService;

	@Autowired
	private AppUserDAO appUserDAO;
	
	@Autowired
	public BCryptPasswordEncoder passwordEncoder;
	
	@Autowired
	private SaltGenerator saltGen;
	
    @Autowired
    private AuditBeanBo              auditBeanBo;
	@Autowired
	private Utils utils;
	/*
	 * @Override public void setUserDetailsService(UserDetailsService
	 * userDetailsService) { super.setUserDetailsService(userDetailsService);
	 * //super.setPasswordEncoder(passwordEncoder()); }
	 * 
	 * protected void additionalAuthenticationChecks(UserDetails userDetails,
	 * UsernamePasswordAuthenticationToken authentication) throws
	 * AuthenticationException { super.additionalAuthenticationChecks(userDetails,
	 * authentication);
	 * 
	 * Object object = authentication.getDetails(); if(!(object instanceof
	 * CaptchaDetails)) { throw new
	 * InsufficientAuthenticationException("Captcha Details Not Found."); }
	 * 
	 * CaptchaDetails captchaDetails = (CaptchaDetails) object; String captcha =
	 * captchaDetails.getCaptcha(); if(captcha != null) {
	 * if(!captcha.equals(captchaDetails.getAnswer())) {
	 * System.err.println("captcha not match"); throw new
	 * InsufficientAuthenticationException("Captcha does not match."); } } }
	 */
	
    @Override
    public Authentication authenticate(Authentication authentication)
            throws AuthenticationException
    {
       // logger.info("Authentication Provider");
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder
                .currentRequestAttributes();
        
        String captcha = attr.getRequest().getParameter("captcha");
        System.out.println("Captcha is "+captcha);
        SNMSValidator snmsVal = new SNMSValidator();
        captcha = snmsVal.getSafeString(captcha);
        	if ("".equals(captcha)) throw new InternalAuthenticationServiceException("Empty captcha"); 
        	if(!captcha.equals(attr.getRequest().getSession().getAttribute("captcha_security")))
        		throw new InternalAuthenticationServiceException("Invalid Captcha");
        	attr.getRequest().getSession().setAttribute("captcha_security","");
        	
      //  String captcha = attr.getRequest().getParameter("captcha");
		/*
		 * SNMSValidator snmsVal = new SNMSValidator(); captcha =
		 * snmsVal.getSafeString(captcha); if ("".equals(captcha)) throw new
		 * InternalAuthenticationServiceException("Empty captcha"); CaptchaBean cptBean
		 * = (CaptchaBean) (attr.getRequest().getSession() .getAttribute(NAME)); if
		 * (cptBean == null || !cptBean.isCorrect(captcha)) throw new
		 * InternalAuthenticationServiceException("Invalid Captcha");
		 */
        
		/*
		 * Object object = authentication.getDetails(); if(!(object instanceof
		 * CaptchaDetails)) { throw new
		 * InsufficientAuthenticationException("Captcha Details Not Found."); }
		 * 
		 * CaptchaDetails captchaDetails = (CaptchaDetails) object; String captcha =
		 * captchaDetails.getCaptcha(); if(captcha != null) {
		 * if(!captcha.equals(captchaDetails.getAnswer())) {
		 * System.err.println("captcha not match"); throw new
		 * InsufficientAuthenticationException("Captcha does not match."); } }
		 */
        String name = authentication.getName();
        String password = authentication.getCredentials().toString();
        String base64EncodedKey = attr.getRequest().getParameter("key");
      
       
       //String base64EncodedKey=Crypt.encodeKey("mustbe16byteskey");
       // String base64EncodedKey=(String) attr.getRequest().getSession().getAttribute("key");
       String decrypt=Crypt.decrypt(password, base64EncodedKey);
    
       UserDetails userDetails = userDetailsService
                .loadUserByUsername(name);
       
        if (!userDetails.isEnabled())
            return null;
        
        AppUser user = null;
        try
        {
            user = appUserDAO.findUserAccount(name);
        }
       /* catch (SnmsException e1)
        {
            //logger.error(e1.getMessage(),e1);
        	e1.printStackTrace();
        }*/
        catch (Exception e)
        {
           // logger.error(e.getMessage(),e);
        	logger.info(e.getMessage());
        }
       Long loginUId = user.getUserId();
       int loginUID = loginUId.intValue();  
     
//        UserDetails appuserDetails=new UserDetails();
       
      
	
	String loginUName =	appUserDAO.findUserDetails(user).getFirstName()!=null ? appUserDAO.findUserDetails(user).getFirstName():""
		
		+appUserDAO.findUserDetails(user).getMiddleName()!=null? appUserDAO.findUserDetails(user).getMiddleName():""
		+appUserDAO.findUserDetails(user).getLastName()!=null?appUserDAO.findUserDetails(user).getLastName():"";
        //String loginUName = user.getUserInfo().getFirstName()+" "+user.getUserInfo().getMiddleName()+" "+user.getUserInfo().getLastName();
        if (passwordEncoder.matches(decrypt, userDetails.getPassword()))
        //if (BCrypt.checkpw(password, saltedPass))
        {           
            try
            {
            	
                auditBeanBo.setAuditBean(loginUID, loginUName,
                        utils.getConfigMessage("log.login.app"), loginUID,
                        utils.getConfigMessage("log.login.authuser"),
                        utils.getConfigMessage("log.login.success"), loginUName,
                        "true");
                		auditBeanBo.save();
                HttpSession hsession = attr.getRequest().getSession(false);
                SecurityContextHolder.clearContext();
                if (hsession != null)
                    hsession.invalidate();
            }
           /* catch (SnmsException e1)
            {
                //logger.error(e1.getMessage(),e1);
            	e1.printStackTrace();
            }*/
            catch (Exception e)
            {
            	logger.info(e.getMessage());
                //logger.error(e.getMessage(),e);
            }
            return new UsernamePasswordAuthenticationToken(name,
                    userDetails.getPassword(), userDetails.getAuthorities());
        }
        else
        {
            try
            {           	
                auditBeanBo.setAuditBean(loginUID, loginUName,
                        utils.getMessage("log.login.app"), loginUID,
                        utils.getMessage("log.login.unauthuser"),
                        utils.getMessage("log.login.fail"), loginUName, "true");
                auditBeanBo.save();
            }
           /* catch (SnmsException e)
            {
                //logger.error(e.getMessage(),e);
            }*/
            catch (Exception e)
            {
                logger.error(e.getMessage(),e);
            }
        }
        return null;
    }

    @Override
    public boolean supports(Class<?> authentication)
    {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
    
	
}
