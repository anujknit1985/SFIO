package com.snms.controllers;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;

import com.snms.dao.AppRoleDAO;
import com.snms.dao.AppUserDAO;
import com.snms.entity.AppUser;
import com.snms.service.UserDetailsServiceImpl;
 
@Controller
public class CustomErrorController  implements ErrorController {
	  @Autowired
	    private AppUserDAO appUserDAO;
	  @Autowired
		private UserDetailsServiceImpl userDetailsService;

	  @Autowired
	    private AppRoleDAO appRoleDao;
	  @Value("${server.servlet.context-path}")
	    private String servletContextPath;
		
    @GetMapping("/error")
    public String handleError(HttpServletRequest request,ModelMap modelMap) {
        String errorPage = "error"; // default
         
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
         
        if (status != null) {
            Integer statusCode = Integer.valueOf(status.toString());
             
            if (statusCode == HttpStatus.NOT_FOUND.value()) {
                // handle HTTP 404 Not Found error
                errorPage = "errorPage404";
                 
            } else if (statusCode == HttpStatus.FORBIDDEN.value()) {
                // handle HTTP 403 Forbidden error
                errorPage = "errorPage404";
                
            } else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                // handle HTTP 500 Internal Server error
                errorPage = "errorPage404";
                 
            }
        }
        
        modelMap.addAttribute("ErrorString","");
		modelMap.addAttribute("ErrorUrl", "login");
		AppUser loginUser = appUserDAO.findUserAccount(userDetailsService.getLoginUserName());
		ErrorPageRedirect(loginUser,modelMap);
              return errorPage;
    }
    public void ErrorPageRedirect(AppUser loginUser,ModelMap model) 
	{
		String appRoleName=appRoleDao.getRoleName(loginUser.getUserId());
		if (appRoleName.contains("ROLE_DIRECTOR")) 
			model.addAttribute("ErrorUrl", servletContextPath+"dirHome");
		 else if (appRoleName.contains("ROLE_ADMIN")) 
			 model.addAttribute("ErrorUrl", servletContextPath+"home");
		 else if (appRoleName.contains("ROLE_USER")) 
			 model.addAttribute("ErrorUrl", servletContextPath+"/userHome");
		else if (appRoleName.contains("ROLE_ADMIN_SECTION")) 
			model.addAttribute("ErrorUrl", servletContextPath+"adsHome");
		else if (appRoleName.contains("ROLE_ADMIN_OFFICER")) 
			model.addAttribute("ErrorUrl", servletContextPath+"adoHome");
		else
			model.addAttribute("ErrorUrl",servletContextPath+ "login");
	}
    @Override
    public String getErrorPath() {
        return "/error";
    }
}