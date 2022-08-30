package com.snms.service;

import java.io.IOException;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.catalina.Session;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Service;

import com.snms.dao.AppUserDAO;
import com.snms.entity.AppUser;
import com.snms.entity.UserDetails;

@Service("customSuccessHandler")
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler
{
	@Autowired
	private AppUserDAO loginDetailBo;
    private static final Logger logger           = Logger
            .getLogger(CustomSuccessHandler.class);


    /**
     * Method for handle the user request
     * 
     * @param request - HttpServletRequest Object of user request
     * @param response - HttpServletResponse Object of user response
     * @param authentication - Authentication object of authenticate user
     */
    /** redirectStrategy object to redirect the user request based on url **/
    private RedirectStrategy    redirectStrategy = new DefaultRedirectStrategy();

    @Override
    protected void handle(HttpServletRequest request,
            HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException
    {
    	System.out.println(" inside  handle   ");
        String targetUrl;
		try {
			targetUrl = determineTargetUrl(authentication, request,response);
			
			System.out.println("   "+targetUrl);
			logger.info("target url is >>>>>>>>> " + targetUrl);
			redirectStrategy.sendRedirect(request, response, targetUrl);
		} catch (Exception e) {
			logger.info(e.getMessage());
		}
    }

    /**
     * Method for find out the url
     * 
     * @param Authentication - object of to find out user authorities
     * @return return authenticate user url
     * @throws Exception 
     */
    protected String determineTargetUrl(Authentication authentication,
            HttpServletRequest req,HttpServletResponse resp) throws Exception
    {
    	
    	System.out.println(" determineTargetUrl method   ");
    	
    	AppUser user = null;
    	
    	Set<String> roles = AuthorityUtils.authorityListToSet(authentication.getAuthorities());
    	String url="";
    	user = loginDetailBo.findUserAccount(authentication.getName());
    	
    	
    	req.getSession().setAttribute("menu", user.getPassChanged());
    	if(user.getPassChanged()== false) {
    		url="/NewUserChangedPass";
    	}
    	else {
        if (roles.contains("ROLE_DIRECTOR")) {
			url="/dirHome";
		} else if (roles.contains("ROLE_ADMIN")) {
			System.out.println(" determineTargetUrl rolevalue   "+roles.toString());
			url="/home";
		} else if (roles.contains("ROLE_USER")) {
			url="/userHome";
		}
		else if (roles.contains("ROLE_ADMIN_SECTION")) {
			url="/adsHome";
		} 
		else if (roles.contains("ROLE_ADMIN_OFFICER")) {
			url="/adoHome";
		}
		else if (roles.contains("ROLE_PROSECUTION")) {
			url="/proHome";
		}
		else if (roles.contains("ROLE_PROSECUTION_ADMIN")) {
			url="/padoHome";
		}
		else
			url="/error403";
    	}
        return url;
    	
    }

    /**
     * Getter Method to get Redirect Strategy
     * 
     * @return Object of RedirectStratedy
     */
    public RedirectStrategy getRedirectStrategy()
    {
        return redirectStrategy;
    }

    /**
     * Setter Method to set Redirect Strategy
     * 
     * @param redirectStrategy - Object of RedirectStratedy
     */
    public void setRedirectStrategy(RedirectStrategy redirectStrategy)
    {
        this.redirectStrategy = redirectStrategy;
    }    
   /* private boolean isSuperAdmin(List<String> roles)
    {
        return roles.contains(NCASConstant.ROLE_SUPERADMIN);
    }*/
   /* private boolean isAdmin(List<String> roles)
    {
        return roles.contains(NfraConstant.ROLE_ADMIN);
    }

    private boolean isUser(List<String> roles)
    {
        return roles.contains(NfraConstant.ROLE_USER);
    }
*/
}
