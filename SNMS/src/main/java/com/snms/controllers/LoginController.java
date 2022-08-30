package com.snms.controllers;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.snms.dao.AppRoleDAO;
import com.snms.dao.AppUserDAO;
import com.snms.dao.NoticeDao;
import com.snms.dao.OfficeOrderDao;
import com.snms.dao.SummonDao;
import com.snms.entity.AppUser;
import com.snms.entity.CaseDetails;
import com.snms.entity.Company;
import com.snms.entity.NoticeStatus;
import com.snms.entity.OfficeOrder;
import com.snms.entity.SnmsErrorReference;
import com.snms.entity.SummonStatus;
import com.snms.service.AddDesignationRepository;
import com.snms.service.AppRoleRepository;
import com.snms.service.AuditBeanBo;
import com.snms.service.CaseDetailsRepository;
import com.snms.service.CompanyRepository;
import com.snms.service.OfficeOrderRepository;
import com.snms.service.SnmsErrorRefRepository;
import com.snms.service.UnitDetailsRepository;
import com.snms.service.UserDetailsRepository;
import com.snms.service.UserDetailsServiceImpl;
import com.snms.service.UserManagementCustom;
import com.snms.utils.Crypt;
import com.snms.utils.SnmsException;
import com.snms.utils.Utils;

@Controller
public class LoginController {

	private static final Logger logger = Logger.getLogger(LoginController.class);
	
	//@Autowired
	//private CaptchaBean captchaBean;
	@Autowired
	private AuditBeanBo auditBeanBo;
	@Autowired
	private UserDetailsServiceImpl userDetailsService;
	
	@Autowired
	private UserManagementCustom userMangCustom;
	
	@Autowired
	private CaseDetailsRepository caseDetailsRepository;
	
	@Autowired
	private SummonDao summonDao;
	
	@Autowired
	private NoticeDao noticeDao;
	
    @Autowired
    private AppUserDAO appUserDAO;
    
    @Autowired
    private AppRoleDAO appRoleDao;
	@Autowired
	private OfficeOrderRepository officeOrderRepo;
    @Autowired
	private Utils utils;
    
    @Autowired
    private SnmsErrorRefRepository snmsErrorRefRepository;
    
    @Autowired
    private UnitDetailsRepository unitDetailsRepository;
    
    @Autowired
    private UserDetailsRepository userDetailsRepository;
    
    @Autowired
    private AppRoleRepository appRoleRepository;
    
    @Autowired
    private AddDesignationRepository addDesignationRepository;
    @Autowired
	private CompanyRepository  companyRepo;
    @Autowired
	private OfficeOrderDao officeOrderDao;
    
    @Value("${server.servlet.context-path}")
    private String servletContextPath;
    
    @RequestMapping(value = {"/","/index"})
	public String index() {
    	logger.info("index page is loaded");
		return "index";
	}

    @RequestMapping(value = "/login")
	public String login(@RequestParam(value = "error", required = false) String error,
			@RequestParam(value = "logout", required = false) String logout, HttpServletRequest req,
			HttpServletResponse resp, ModelMap model) throws NoSuchAlgorithmException {

		//saltGen.generateSalt();
		//String salt=saltGen.genSalt();
		//req.getSession().setAttribute("key", Crypt.encodeKey("mustbe16byteskey"));
    	
    	
    	req.getSession().setAttribute("key", Crypt.encodeKey());
		
    	System.out.println("key value             === "+ req.getSession().getAttribute("Key"));
    	//req.getSession().setAttribute("key", BCrypt.gensalt(4, new SecureRandom(new byte[128])));
		
		if (error != null) {
			model.addAttribute("error", "Invalid username, password or security code");
			return "login";
		}
		if (logout != null) {
			model.addAttribute("logout", "You have been logout sucessfully!");
			return "login";
		}
		return "login";
	}
	
	@RequestMapping(value = "/home")
	public String home(ModelMap modelMap) {
		modelMap.addAttribute("totleUsers", userDetailsRepository.count());
		modelMap.addAttribute("totleUnits", unitDetailsRepository.count());
		modelMap.addAttribute("totleDesignation", addDesignationRepository.count());
		modelMap.addAttribute("totleRole", appRoleRepository.count());
		return "home";
	}
	
	@RequestMapping(value = "/dirHome")
	public String dirHome(ModelMap modelMap) throws Exception {
	//	List<OfficeOrder>caseList =  officeOrderRepo.findAllByAprrovalStage1(true);
		
		List<OfficeOrder>caseList =  userMangCustom.findAllByDirAprrovalStage1(true,1);
		List<Object[]> signedOreders=officeOrderDao.findAllSignedOfficeOrder();
		List<Object[]> signedNotice=noticeDao.findAllSignedNotices();
		List<Object[]> signedSummon=summonDao.findAllSignedSummons();
		
		List<Object[]> appOrder = officeOrderDao.findOfficeOrderByDirApprove();
		List<Object[]> appNotice=noticeDao.findNoticeApproveByUser();
		List<Object[]> PhyNotice =summonDao.findNoticePhysically();
		List<Object[]> appSummon=summonDao.findSummonsByApproveSenstive();
		List<Object[]> Physummon =summonDao.findSummonsPhysically();
		List<SummonStatus> PendingPhysummon =summonDao.findSummonsPedingPhysicallysend();
		List<NoticeStatus> PendingPhyNotice = noticeDao.findNoticePedingPhysicallysend(); 
		modelMap.addAttribute("totalOrder", signedOreders.size());
		modelMap.addAttribute("totalNotice", signedNotice.size());
		modelMap.addAttribute("totalSummon", signedSummon.size());
		modelMap.addAttribute("totalCase", caseList.size());
		
		modelMap.addAttribute("appOrder", appOrder.size());
		modelMap.addAttribute("appNotice", appNotice.size());
		modelMap.addAttribute("appSummon", appSummon.size());
		modelMap.addAttribute("Physummon", Physummon.size());
		modelMap.addAttribute("PhyNotice", PhyNotice.size());
		modelMap.addAttribute("PendingPhysummon", PendingPhysummon.size());
		modelMap.addAttribute("PendingPhyNotice", PendingPhyNotice.size());
		return "director/directorHome";
	}
	
	@RequestMapping(value = "/adoHome")
	public String adoHomadohome(ModelMap modelMap) throws Exception {
		
		//List<OfficeOrder>caseList =  officeOrderRepo.findAllByAprrovalStage1AndAprrovalStage2(false,false);
		//List<OfficeOrder>caseList =  officeOrderRepo.findAll();
		//List<OfficeOrder>caseList =  userMangCustom.findAllByAdoAprrovalStage1(true,1);
		List<OfficeOrder>caseList =  userMangCustom.findAllByAdoAprroval(true);
		List<Object[]> signedOreders=officeOrderDao.findAllSignedOfficeOrder();
		List<Object[]> signedNotice=noticeDao.findAllSignedNotices();
		List<Object[]> signedSummon=summonDao.findAllSignedSummons();
		List<Object[]> appOrder = officeOrderDao.findOfficeLegacyOrderByAdoApprove();
	    List<CaseDetails> pendingCompList = userMangCustom.findCompanyByCasePendingForApprovalByADO( false, 4L);
		//List<Object[]> penOrder = userMangCustom.findOfficeOrderPendingForApproval(1);
		List<Object[]> penOrder = userMangCustom.findOfficeOrderPendingForApproval();
		
		modelMap.addAttribute("totalOrder", signedOreders.size());
		modelMap.addAttribute("totalNotice", signedNotice.size());
		modelMap.addAttribute("totalSummon", signedSummon.size());
		modelMap.addAttribute("appOrder", appOrder.size());
		modelMap.addAttribute("totalCase", caseList.size());
		modelMap.addAttribute("pendingCompList", pendingCompList.size());
		//modelMap.addAttribute("totalCase", caseDetailsRepository.findAll().size());
		modelMap.addAttribute("penOrder", penOrder.size());
		
		return "ado/adoHome";
	}
	
	@RequestMapping(value = "/adsHome")
	public String adsHome(ModelMap modelMap) throws Exception 
	{
		List<Object[]> signedOreders=officeOrderDao.findAllSignedOfficeOrder();
		List<Object[]> signedNotice=noticeDao.findAllSignedNotices();
		List<Object[]> signedSummon=summonDao.findAllSignedSummons();
		List<OfficeOrder> officeOrderPending = officeOrderRepo.findAllByAprrovalStage1(false);
		List<CaseDetails> officeOrderPendingForwarding = userMangCustom.findPeningForfarwarding();
		modelMap.addAttribute("totalOrder", signedOreders.size());
		modelMap.addAttribute("totalNotice", signedNotice.size());
		modelMap.addAttribute("totalSummon", signedSummon.size());
		modelMap.addAttribute("officeOrderPending", officeOrderPending.size());
		modelMap.addAttribute("officeOrderPendingForwarding", officeOrderPendingForwarding.size());
		
		modelMap.addAttribute("totalCase", caseDetailsRepository.findAll().size());
		
		return "adsHome";
	}
	
	@RequestMapping(value = "/userHome")
	public String userHome(ModelMap modelMap) throws Exception {
		List<Object[]> list = userMangCustom.findCaseByUserId(userDetailsService.getUserDetails().getUserId());
		//List<Object[]> list = userMangCustom.findCaseByUserIdbystage(userDetailsService.getUserDetails().getUserId());
		List<Object[]> signedNotice=noticeDao.findSignedNotices(userDetailsService.getUserDetails());
		List<Object[]> signedSummon=summonDao.findSignedSummons(userDetailsService.getUserDetails());
		modelMap.addAttribute("totalAssignCase", list.size());
		modelMap.addAttribute("totalsignedNotice", signedNotice.size());
		modelMap.addAttribute("totalsignedSummon", signedSummon.size());
		return "userHome";
	}

	@RequestMapping(value = "/logout")
	public String logout(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	      if (auth != null) {
	          new SecurityContextLogoutHandler().logout(request, response, auth);
	      }
	      String name = auth.getName();
	      UserDetails userDetails = userDetailsService
	                .loadUserByUsername(name);
	       
	       
	        
	        AppUser user = null;
	      
	            user = appUserDAO.findUserAccount(name);
	        
	        Long loginUId = user.getUserId();
	        int loginUID = loginUId.intValue();  
	        String loginUName =	appUserDAO.findUserDetails(user).getFirstName()!=null ? appUserDAO.findUserDetails(user).getFirstName():""
	    		
		+appUserDAO.findUserDetails(user).getMiddleName()!=null? appUserDAO.findUserDetails(user).getMiddleName():""
		+appUserDAO.findUserDetails(user).getLastName()!=null?appUserDAO.findUserDetails(user).getLastName():"";
    
	      auditBeanBo.setAuditBean(loginUID,   loginUName,
                  utils.getConfigMessage("log.login.app"), loginUID,
                  utils.getConfigMessage("log.login.authuser"),
                  utils.getConfigMessage("log.logout.success"), loginUName,
                  "true");
          		auditBeanBo.save();
		return "redirect:/login";
	}
	
	
	@ExceptionHandler(value = { Exception.class, RuntimeException.class,SnmsException.class })
	public String handleError(Exception ex) throws SnmsException, Exception {
		AppUser loginUser = appUserDAO.findUserAccount(userDetailsService.getLoginUserName());
		ModelMap modelMap=new ModelMap();
		modelMap.addAttribute("ErrorString", getErrorMessage(ex));
		ErrorPageRedirect(loginUser,modelMap);
		return "redirect:/errorPage";
	}
		
	public String getErrorMessage(Exception ex) throws SnmsException, Exception {
		SnmsErrorReference err = new SnmsErrorReference();
		String errorString = "000";
		String errorMsg = "Error";
		if (ex instanceof SnmsException) {
			SnmsException dex = (SnmsException) ex;
			errorString = dex.getERROR_CODE();
			errorMsg = utils.getError(errorString, dex.getParameter());
		} else {
			if (ex instanceof MissingServletRequestParameterException)
				return utils.getMessage("errmsg.missing");
			if (ex instanceof org.springframework.validation.BindException)
				return utils.getMessage("errmsg.mismatch");
			errorMsg = utils.getError(errorString);
		}
		StackTraceElement[] traces = ex.getStackTrace();
		StackTraceElement trace = (traces == null || traces.length == 0) ? null : traces[0];
		String erMsg = (trace == null) ? ""
				: trace.getClassName() + ":" + trace.getMethodName() + ":" + trace.getLineNumber();
		erMsg = (ex.getMessage() == null) ? (erMsg + ":" + ex) : (erMsg + ":" + ex.getMessage());
		err.setErrorMessage(erMsg);
		err.setErrorCode(errorString);
		snmsErrorRefRepository.save(err);
		return (errorMsg + " with reference No:" + err.getId());
	}
	
	@RequestMapping(value = "/{keyword}", method = RequestMethod.GET)
	public String errorRedirect(@PathVariable("keyword") String keyword, ModelMap modelMap)
			throws SnmsException, Exception	{
		modelMap.addAttribute("ErrorString", keyword);
		modelMap.addAttribute("ErrorUrl", "login");
		AppUser loginUser = appUserDAO.findUserAccount(userDetailsService.getLoginUserName());
		ErrorPageRedirect(loginUser,modelMap);
		return "errorPage404";
	}
	
	@RequestMapping(value = "/{keyword1}/{keyword2:.}", method = RequestMethod.GET)
	public String errorRedirect(@PathVariable("keyword1") String keyword1,@PathVariable("keyword2") String keyword2, ModelMap modelMap)
			throws SnmsException, Exception	{
		modelMap.addAttribute("ErrorString", keyword2);
		modelMap.addAttribute("ErrorUrl", "login");
		AppUser loginUser = appUserDAO.findUserAccount(userDetailsService.getLoginUserName());
		ErrorPageRedirect(loginUser,modelMap);
		return "errorPage404";
	}
	
	
	
	@RequestMapping(value = "/error403")
	public String denyAccess(ModelMap modelMap) throws SnmsException, Exception {
		modelMap.addAttribute("ErrorString", "You are not permitted to use this function. Contact Administrator");
		AppUser loginUser = appUserDAO.findUserAccount(userDetailsService.getLoginUserName());
		ErrorPageRedirect(loginUser,modelMap);
		userDetailsService.getCurrentSession().getAttribute("ErrorUrl");
		return "errorPage";
	}
    
	@RequestMapping(value = "/error500")
	public String exsmaptin(ModelMap modelMap) throws SnmsException, Exception {
		modelMap.addAttribute("ErrorString", "You are not permitted to use this function. Contact Administrator");
		AppUser loginUser = appUserDAO.findUserAccount(userDetailsService.getLoginUserName());
		ErrorPageRedirect(loginUser,modelMap);
		userDetailsService.getCurrentSession().getAttribute("ErrorUrl");
		return "errorPage";
	}
	@RequestMapping(value = "/errorPage")
	public String errorPage(ModelMap modelMap) throws SnmsException, Exception {
		modelMap.addAttribute("ErrorString", userDetailsService.getCurrentSession().getAttribute("ErrorString"));
		AppUser loginUser = appUserDAO.findUserAccount(userDetailsService.getLoginUserName());
		ErrorPageRedirect(loginUser,modelMap);
		return "errorPage";
	}

	private String redirectToError(String msg) throws SnmsException, Exception 
	{
		AppUser loginUser = appUserDAO.findUserAccount(userDetailsService.getLoginUserName());
		//userDetailsService.getCurrentSession().setAttribute("ErrorString", utils.getMessage(msg));
		//userDetailsService.getCurrentSession().removeAttribute(NAME);
		ModelMap model=new ModelMap();
		model.addAttribute("ErrorString", utils.getMessage(msg));
		ErrorPageRedirect(loginUser,model);
		return "redirect:/errorPage";
	}
	
	public void ErrorPageRedirect(AppUser loginUser,ModelMap model) 
	{
		String appRoleName=appRoleDao.getRoleName(loginUser.getUserId());
		if (appRoleName.equals("ROLE_DIRECTOR")) 
			model.addAttribute("ErrorUrl", servletContextPath+"/dirHome");
		 else if (appRoleName.equals("ROLE_ADMIN")) 
			 model.addAttribute("ErrorUrl", servletContextPath+"/home");
		 else if (appRoleName.equals("ROLE_USER")) 
			 model.addAttribute("ErrorUrl",servletContextPath+"/userHome");
		else if (appRoleName.equals("ROLE_ADMIN_SECTION")) 
			model.addAttribute("ErrorUrl",servletContextPath+ "/adsHome");
		else if (appRoleName.equals("ROLE_ADMIN_OFFICER")) 
			model.addAttribute("ErrorUrl",servletContextPath+ "/adoHome");
		else
			model.addAttribute("ErrorUrl",servletContextPath+ "/login");
	}
	
	/*
	 * @RequestMapping("/captcha") public void captchaGen1(HttpServletRequest
	 * request, HttpServletResponse response) throws Exception {
	 * captchaBean.build(); CaptchaServletUtil.writeImage(response,
	 * captchaBean.getImage()); request.getSession().setAttribute(NAME,
	 * captchaBean); }
	 */
	
	@RequestMapping(value = "/captcha", method = RequestMethod.GET)
	public void captcha(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("image/jpg");
		int iTotalChars = 6;
		int iHeight = 40;
		int iWidth = 150;
		Font fntStyle1 = new Font("Arial", Font.BOLD, 30);
		Random randChars = new Random();
		String sImageCode = (Long.toString(Math.abs(randChars.nextLong()), 36)).substring(0, iTotalChars);
		BufferedImage biImage = new BufferedImage(iWidth, iHeight, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2dImage = (Graphics2D) biImage.getGraphics();
		int iCircle = 15;
		for (int i = 0; i < iCircle; i++) {
//			g2dImage.setColor(new Color(randChars.nextInt(255), randChars.nextInt(255), randChars.nextInt(255)));
			g2dImage.setColor(new Color(255, 255, 255));
		}
		g2dImage.setFont(fntStyle1);
		for (int i = 0; i < iTotalChars; i++) {
			g2dImage.setColor(new Color(255, 255, 255));
			if (i % 2 == 0) {
				g2dImage.drawString(sImageCode.substring(i, i + 1), 25 * i, 24);
			} else {
				g2dImage.drawString(sImageCode.substring(i, i + 1), 25 * i, 35);
			}
		}
		OutputStream osImage = response.getOutputStream();
		ImageIO.write(biImage, "jpeg", osImage);
		g2dImage.dispose();
		HttpSession session = request.getSession();
		session.setAttribute("captcha_security", sImageCode);
		osImage.close();
	}
	
	//dated on 27-05-2020
	@RequestMapping("/passHashing")
	public String passHash() throws Exception {
		return "passwordHashing";
	}
	
	
	
}
