package com.snms.controllers;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.snms.dao.AppRoleDAO;
import com.snms.dao.AppUserDAO;
import com.snms.dao.OfficeOrderDao;
import com.snms.dto.DesignationDaoImpl;
import com.snms.dto.MailInfo;
import com.snms.dto.UserForm;
import com.snms.entity.AddDesignation;
import com.snms.entity.AppRole;
import com.snms.entity.AppUser;
import com.snms.entity.InvestigationStatus;
import com.snms.entity.UnitDetails;
import com.snms.entity.UserDetails;
import com.snms.entity.UserRole;
import com.snms.service.AddDesignationRepository;
import com.snms.service.AddStatusRepository;
import com.snms.service.AppRoleRepository;
import com.snms.service.AppUserRepository;
import com.snms.service.AuditBeanBo;
import com.snms.service.FinancialYearRepository;
import com.snms.service.MailBo;
import com.snms.service.UnitDetailsRepository;
import com.snms.service.UserDetailsRepository;
import com.snms.service.UserDetailsServiceImpl;
import com.snms.service.UserManagementCustom;
import com.snms.service.UserRoleRepository;
import com.snms.utils.Crypt;
import com.snms.utils.SnmsConstant;
import com.snms.utils.SnmsException;
import com.snms.utils.Utils;
import com.snms.validation.ChangePasswordValidator;
import com.snms.validation.SNMSValidator;
import com.snms.validation.UserValidation;

import org.apache.log4j.Logger;

@Controller
public class UserController {
	private static final Logger logger = Logger.getLogger(UserController.class);
    @Autowired
	private AppRoleRepository appRoleRepo;
	@Autowired
	private AddDesignationRepository addDesigRepo;
	@Autowired
	private UserDetailsRepository userDetailsRepo;
	@Autowired
	private AppUserRepository appUserRepo;
	@Autowired
	private AddDesignationRepository designationRepo;
	@Autowired
	private UnitDetailsRepository unitDetailsRepo;
	@Autowired
	private UserRoleRepository userRoleRepo;
	@Autowired
	private AppUserDAO appUserDAO;
	@Autowired
	private MailBo mailBo;
	@Autowired
	private UserDetailsServiceImpl userDetailsService;
	
	@Autowired
	private AuditBeanBo auditBeanBo;
	@Autowired
	private Utils utils;

	@Autowired
	private AppRoleDAO appRoleDao;
	
	@Autowired
	private DesignationDaoImpl designationService;
	
	@Autowired
	public BCryptPasswordEncoder passwordEncoder;
	
	@Autowired
	private UserManagementCustom userMangCustom;
	
	@Autowired
	private FinancialYearRepository financialYearRepo;
	@Autowired
	OfficeOrderDao officeOrderDao;
	@Autowired
	AddStatusRepository addStatusRepo;
	
	@RequestMapping(value ="/addStatus")
	public String addStatus(Model model) {
		model.addAttribute("addstaus", new InvestigationStatus());
		model.addAttribute("listDesignation", addStatusRepo.findAll());
		return "userManagement/addStatus";
	}
	
	@RequestMapping(value = "/addDesignation")
	public String addDesignation(Model model) {
		model.addAttribute("addDesignation", new AddDesignation());
		model.addAttribute("listDesignation", designationRepo.findAll());
		return "userManagement/addDesignation";
	}
	@RequestMapping(value = "/addNewDesiganation")
	public String addNewDesiganation(@ModelAttribute @Valid AddDesignation addDesignation, BindingResult bindResult,
			Model model, RedirectAttributes redirect) throws SnmsException, Exception {
		
		if(addDesignation.getType().equalsIgnoreCase("0")) {
			bindResult.rejectValue("type", "msg.wrongtype");
		}
		if (bindResult.hasErrors()) {
			if(null!=addDesignation)
			model.addAttribute("addDesignation", addDesignation);
			else
				model.addAttribute("addDesignation",new AddDesignation());
			//gouthami 15/09/2020
			List<AddDesignation> desiList =  designationRepo.findAll();
			if(desiList.size()>0) {
			model.addAttribute("listDesignation", desiList);
			}else {
			return "userManagement/addDesignation";
			}
			return "userManagement/addDesignation";
		} else if (addDesignation.getId()!=null){
		addDesignation.setUpdatedDate(new Date());
		addDesignation.setEditdesgi(false);
			
		
			designationService.save(addDesignation);
			redirect.addFlashAttribute("message", " Designation Updated Successfully as " +addDesignation.getDesignation());
			
		}
		else {
			addDesignation.setEditdesgi(false);
		designationService.save(addDesignation);
              System.out.println("error accor");
              redirect.addFlashAttribute("message", " New Designation Added Successfully as " +addDesignation.getDesignation());
      		    
		}
		return "redirect:/addDesignation";
	}
	@RequestMapping(value = "/getDesignations")
	public String getDesignation(Model model) {
		model.addAttribute("listDesignation", designationRepo.findAll());
	//Logger logger = Logger.getLogger(NoticeAndSummonController.class);
		logger.info("getDesignation function call");
		return "userManagement/listDesignation";
	
	}
	
	@RequestMapping(value = "/addRole")
	public String addRole(Model model) {
		model.addAttribute("appRole", new AppRole());
		model.addAttribute("rolelist", appRoleRepo.findAll());
		return "userManagement/createRole";
	}

	@RequestMapping(value = "/createNewRole")
	public String createNewRole(@ModelAttribute @Valid AppRole appRole, BindingResult bindResult, Model model,
			RedirectAttributes redirect) {
		
		if (bindResult.hasErrors()) {
			if(null!=appRole)
			model.addAttribute("appRole", appRole);
			else
				model.addAttribute("appRole",new AppRole());
			
			
			model.addAttribute("rolelist", appRoleRepo.findAll());
			return "userManagement/createRole";
		} else {
			String rolename="ROLE_"+appRole.getRoleName();
			appRole.setRoleName(rolename);
			AppRole app = appRoleRepo.save(appRole);
		
		}
		redirect.addFlashAttribute("message", "New Role Created SuccessFully !!");
		return "redirect:/addRole";
	}

	
	@RequestMapping(value = "/addNewstatus")
	public String createNewRole(@ModelAttribute @Valid InvestigationStatus addstaus, BindingResult bindResult, Model model,
			RedirectAttributes redirect) {
		
		if (bindResult.hasErrors()) {
			if(null!=addstaus)
			model.addAttribute("addstaus", addstaus);
			else
				model.addAttribute("addstaus",new InvestigationStatus());
			model.addAttribute("listDesignation", addStatusRepo.findAll());
			return "userManagement/createRole";
		} else {
			
		addstaus.setStatusName(addstaus.getStatusName());
		addstaus.setType(addstaus.getType());
		addStatusRepo.save(addstaus);
		}
		redirect.addFlashAttribute("message", "New status Created SuccessFully !!");
		return "redirect:/addStatus";
	}
	@RequestMapping(value = "/getRoles")
	public String getRole(Model model) {
		model.addAttribute("rolelist", appRoleRepo.findAll());
		return "userManagement/listRole";
	}
	
	@RequestMapping(value = "/addUser")
	public String addUser(Model model,HttpServletRequest request) {
		model.addAttribute("userDetails", new UserDetails());
		//List<AddDesignation> designationList = designationRepo.findAll();
		List<AddDesignation> designationList = designationService.findDesignationByUsertype(new AddDesignation());
	//	List<UnitDetails> unitList = unitDetailsRepo.findAll(Sort.by(Sort.Direction.ASC, "unitId"));
		List<UnitDetails> unitList = unitDetailsRepo.findAll();
		if(unitList.isEmpty() || unitList.size()<0) {
			unitList = null;
		}
		List<AppRole> roleList=appRoleDao.RoleList();
		model.addAttribute("designationList", designationList);
		model.addAttribute("unitList", unitList);
		model.addAttribute("roleList", roleList);
		model.addAttribute("userList",userDetailsRepo.findAll());
		CsrfToken csrfToken = (CsrfToken) request.getAttribute("_csrf");
		System.out.println("CSRF TOKEN add USER " +csrfToken.getToken());
		return "userManagement/addUser";
	}
	@SuppressWarnings("unused")
	@RequestMapping(value = "/addNewUser")
	public String addNewUser(@ModelAttribute @Valid UserDetails userDetails, BindingResult bindResult,
			Model model, RedirectAttributes redirect,HttpServletRequest request) throws Exception {
		UserValidation validation = new UserValidation();
		validation.validateUserRegComplete(userDetails,bindResult,isUniqueUserName(userDetails.getEmail()),isUniqueMobile(userDetails.getPrimaryMobile()),isUniqueMobile(userDetails.getAlternateNo()));
		CsrfToken csrfToken = (CsrfToken) request.getAttribute("_csrf");
		System.out.println("CSRF TOKEN save USER " +csrfToken.getToken());
		if(userDetails.getDesignationId()==0L)
		{
			bindResult.rejectValue("designation", "msg.wrongId");
		}
		else{
			AddDesignation designationbyId = designationRepo.findById(userDetails.getDesignationId()).get();
			if(designationbyId==null)
				bindResult.rejectValue("designation", "msg.wrongId");
		}
		if(userDetails.getUnitId() != null)
		{
			UnitDetails unitbyId = unitDetailsRepo.findById(userDetails.getUnitId()).get();
			if(unitbyId == null)
				bindResult.rejectValue("unit", "msg.wrongId");
		}
		if(userDetails.getRoleId() != null)
		{
			AppRole roleById=appRoleRepo.findById(userDetails.getRoleId()).get();
			if(roleById==null || userDetails.getRoleId()==1L || userDetails.getRoleId()==4L)
			bindResult.rejectValue("roleId", "msg.wrongId");
		}
		
		AppUser user = new AppUser();
		if (bindResult.hasErrors()) {
			if(null!=userDetails)
             model.addAttribute("userDetails", userDetails);
			else
				model.addAttribute("userDetails", new UserDetails());
			List<AddDesignation> designationList = designationService.findDesignationByUsertype(new AddDesignation());
    		
    		if(designationList.isEmpty()) {
    			designationList = null;
    		}
    		//List<UnitDetails> unitList = unitDetailsRepo.findAll(Sort.by(Sort.Direction.ASC, "unitId"));
    		//gouthami 15/09/2020
    		List<UnitDetails> unitList = unitDetailsRepo.findAll();
			/*
			 * List<UnitDetails> sortedUnitList = null; if(unitList.size()>0) {
			 * sortedUnitList = Collections.sort(unitList); }
			 */
    		List<AppRole> roleList=appRoleDao.RoleList();
    		model.addAttribute("designationList", designationList);
    		if(unitList.size()>0 || unitList!= null ) {
    		model.addAttribute("unitList", unitList);
    		}
    		model.addAttribute("roleList", roleList);
    		
    		//gouthami 15/09/2020

    		List<UserDetails> userdetailList =  userDetailsRepo.findAll();
    	
    		if (userdetailList.isEmpty()|| userdetailList.size()<0) {
    			userdetailList = null;
    		}
    		model.addAttribute("userList",userdetailList);
    		return "userManagement/addUser";
        } else {
        
        	
		user.setUserName(userDetails.getEmail());
		user.setEncrytedPassword("$2a$10$RpjdPpApu2Gk7uUJfj1jpuWpbGxCJ9yKt1ea5dJHZaO0vJBDxenTW");
		user.setEnabled(1);
		user.setOldPassword("$2a$10$RpjdPpApu2Gk7uUJfj1jpuWpbGxCJ9yKt1ea5dJHZaO0vJBDxenTW"+SnmsConstant.OLD_PASS_SEP);
		user.setPassChanged(false);
		Calendar cal = Calendar.getInstance();
		Date today = cal.getTime();
		cal.add(Calendar.YEAR, 2); // to get previous year add -1
		Date nextYear = cal.getTime();
		
		user.setValidFrom(today);
		user.setValidUpto(nextYear);
		user = appUserRepo.save(user);
		
	

		userDetails.setUserId(user);
		userDetails.setDob(userDetails.getUiDob());
		userDetails.setJoiningDate(userDetails.getUiJoiningDate());
		
		userDetails.setCreatedDate(new Date());
		userDetails.setCreatedBy(userDetailsService.getUserDetails());
		
		userDetails.setDesignation(new AddDesignation(userDetails.getDesignationId()));
		userDetails.setUnit(new UnitDetails(userDetails.getUnitId()));
		userDetailsRepo.save(userDetails);
		UserRole userRole = new UserRole(user,new AppRole(userDetails.getRoleId()));
		userRoleRepo.save(userRole);
		
		auditBeanBo.setAuditBean(Integer.parseInt(userDetailsService.getUserDetails().getUserId().toString()),
				userDetails.getFirstName()+" "+userDetails.getMiddleName()+" "+userDetails.getLastName(),
				"User",Integer.parseInt(userDetailsService.getUserDetails().getUserId().toString()) , utils.getMessage("log.user.optype"), utils.getMessage("log.user.optdesc"), userDetailsService.getUserDetails().getUserName(),"true");
		auditBeanBo.save();	
		
		MailInfo info=new MailInfo();
		info.setEmail(userDetails.getEmail());
		mailBo.sendMail(info, SnmsConstant.USER_CREATE, request);
		}
		redirect.addFlashAttribute("message", "New User Added Successfully with Login Id : "+user.getUserName());
		return "redirect:/addUser";
	}
	
	@RequestMapping(value = "/getUsers")
	public String getUsers(Model model) {
		//gouthami 15/09/2020
		List<UserDetails> userList = userDetailsRepo.findAll();
		
	//	userList.clear();
		if(userList.size()>0 || !userList.isEmpty()) {
		model.addAttribute("userList",userList);
		return "userManagement/listUsers";
		}else {
			model.addAttribute("userList",userList);
			return "userManagement/listUsers";
		}
		
		
	}

	@RequestMapping(value = "/getUniqueEmail", method = RequestMethod.POST)
	public boolean isUniqueUserName(@RequestParam("email") String email) throws Exception {
		AppUser user = appUserDAO.findUserAccount(email);
		return (user == null);
	}
	
	@RequestMapping(value = "/getUniqueMobile", method = RequestMethod.POST)
	public boolean isUniqueMobile(@RequestParam("mobile") String mobile) throws Exception {
		UserDetails user = userMangCustom.findUserDetailsMobile(mobile);
		return (user == null);
	}

	@RequestMapping(value = "/addUnit")
	public String addUnit(Model model) {
	      List<UnitDetails> unitList =  unitDetailsRepo.findAll();
	      if(unitList.isEmpty()) {
	    	  unitList = null;
	      }
		model.addAttribute("unitDetails", new UnitDetails());
		model.addAttribute("unitList",unitList);
		return "userManagement/addUnitDetails";

	
	}

	@RequestMapping(value = "/addNewUnit")
	public String addNewUnit(@ModelAttribute @Valid UnitDetails unitDetails, BindingResult bindResult, Model model,
			RedirectAttributes attributes) {
		UserValidation userValidation = new UserValidation();
		userValidation.validateUnitDetails(unitDetails, bindResult);
		
		if (bindResult.hasErrors()) {
			if(null!=unitDetails)
			model.addAttribute("unitDetails", unitDetails);
			else
				model.addAttribute("unitDetails", new UnitDetails());	
			
			
			model.addAttribute("unitList",unitDetailsRepo.findAll());
			return "userManagement/addUnitDetails";
		} 
		
		
		else if(unitDetails.getUnitId()!=null) {
		  	UnitDetails unitDts =  unitDetailsRepo.findById(unitDetails.getUnitId()).get();  
			  unitDetails.setEditUnit(false);
			  unitDts.setUpdatedDate(new Date());
			  unitDts.setAddress(unitDetails.getAddress());
			  unitDts.setAddress_hi(unitDetails.getAddress_hi());
			  unitDts.setEMail(unitDetails.getEMail());
		      unitDts.setUnitName(unitDetails.getUnitName());
		      unitDts.setLocation(unitDetails.getLocation());
		      unitDts.setTelephoneNo(unitDetails.getTelephoneNo());
		      unitDts.setFaxNo(unitDetails.getFaxNo());
				unitDetails = unitDetailsRepo.save(unitDts);
				attributes.addFlashAttribute("message", " New Unit "+ unitDetails.getUnitName()+ " Update  Successfully. ");
			
		}
		else {
			unitDetails.setCreatedDate(new Date());
			//unitDetails.setCreatedBy(new AppUser((long) 1));
			unitDetails = unitDetailsRepo.save(unitDetails);
			attributes.addFlashAttribute("message", " New Unit "+ unitDetails.getUnitName()+ " added Successfully. ");
			
		}
		return "redirect:/addUnit";
	}
	
	@RequestMapping(value = "/getUnits")
	public String getUnits(Model model) {

		//gouthami 15/09/2020
     List<UnitDetails> unitList = unitDetailsRepo.findAll();
	 if (unitList.size()>0 || !unitList.isEmpty()) {
		model.addAttribute("unitList",unitDetailsRepo.findAll());
		return "userManagement/listUnits";
	 }else {
		 model.addAttribute("unitList",unitDetailsRepo.findAll());
			return "userManagement/listUnits";
	 }
	}
	
	@RequestMapping(value = "/editUnit",  params = "editunit")
	public String editUnit(@RequestParam(value = "editunit", required = true) Long id, Model model,HttpServletRequest request) {
		UnitDetails unitDetails = unitDetailsRepo.findById(id)
			      .orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));
	
	
		       unitDetails.setEditUnit(true);
				List<UnitDetails> unitList = unitDetailsRepo.findAll(Sort.by(Sort.Direction.ASC, "unitId"));
				List<AppRole> roleList=appRoleDao.RoleList();
			
				model.addAttribute("unitList", unitList);
				model.addAttribute("roleList", roleList);	    
				model.addAttribute("unitDetails", unitDetails);
				
				return "userManagement/addUnitDetails";
	}
	
	
	//change Password
	@RequestMapping(value = "/changePassword")
	public String changePassword(ModelMap modelMap) throws SnmsException, Exception {
		AppUser user = appUserDAO.findUserAccount(userDetailsService.getLoginUserName());
		UserForm userForm = new UserForm();
		userForm.setId(user.getUserId());
		modelMap.addAttribute("userForm", userForm);
		//modelMap.addAttribute("key", Crypt.encodeKey("mustbe16byteskey"));
		modelMap.addAttribute("key",Crypt.encodeKey());
			
		
		return "changePassword";
	}

	@RequestMapping(value = "/passwordCheck", method = RequestMethod.POST)
	public @ResponseBody String passwordCheck(@RequestParam("current_password") String password,@RequestParam("key") String key) {
		   ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder
	                .currentRequestAttributes();
		String dbpass="";
		//String base64EncodedKey=Crypt.encodeKey("mustbe16byteskey");
		//String base64EncodedKey=Crypt.encodeKey("mustbe16byteskey");
		String base64EncodedKey= key;
	   String decryptPass=Crypt.decrypt(password, base64EncodedKey);
		 
		try {
			AppUser user = appUserDAO.findUserAccount(userDetailsService.getLoginUserName());
			dbpass = user.getEncrytedPassword();
					 
		} catch (Exception er) {
			
		}
		return (passwordEncoder.matches(decryptPass,dbpass)+"");
	}
	@SuppressWarnings("unused")
	@RequestMapping(value = "/saveChangePassword", method = RequestMethod.POST)
	public String resetPassword(@ModelAttribute @Valid UserForm userForm, HttpServletRequest req, HttpServletResponse resp,
			ModelMap modelMap, BindingResult result,RedirectAttributes attributes, @RequestParam(name="key",required=false) String key) throws SnmsException, Exception {
		
		AppUser loginUser = appUserDAO.findUserAccount(userDetailsService.getLoginUserName());
		if(null==userForm || userForm.equals(0)) {
			return "changePassword";
		}
		AppUser passChangeUser=appUserRepo.findById(userForm.getId()).get();

		/*ChangePasswordValidator chgPassword = new ChangePasswordValidator();
		chgPassword.userValidatePassword(userForm, false, 1, result);*/
		if (result.hasErrors()) {
			if(null!=userForm)
			modelMap.addAttribute("userForm", userForm);
			else
				modelMap.addAttribute("userForm", new  UserForm());
				
			//userForm.setId(loginUser.getUserId());
			/*
			 * if(Crypt.encodeKey("mustbe16byteskey")!=null) {
			 * //modelMap.addAttribute("key", Crypt.encodeKey("mustbe16byteskey"));
			 * modelMap.addAttribute("key", Crypt.encodeKey()); }
			 */
			if(Crypt.encodeKey()!=null) {
				//modelMap.addAttribute("key", Crypt.encodeKey("mustbe16byteskey"));
					modelMap.addAttribute("key", Crypt.encodeKey());
				}
			return "changePassword";
		}
		
		//String base64EncodedKey=Crypt.encodeKey("mustbe16byteskey");
		String base64EncodedKey=key;
	    
		// Check if same password is being used
		if (loginUser.getUserId() == passChangeUser.getUserId()) {
			String pass = loginUser.getEncrytedPassword();
			
			String decryptCurrentPass=Crypt.decrypt(userForm.getPassword(), base64EncodedKey);
			// Check if password is authenticated
			if (!passwordEncoder.matches(decryptCurrentPass, pass)) {
				modelMap.addAttribute("unknownUser", utils.getMessage("msg.wrongauthentication"));
				userForm.setId(loginUser.getUserId());
				modelMap.addAttribute("key", base64EncodedKey);
				return "changePassword";
			}
			String decryptNewPass=Crypt.decrypt(userForm.getChangePassword(), base64EncodedKey);
			//Using same old password
			if(null!=decryptNewPass){
			if (passwordEncoder.matches(decryptNewPass,pass)) {
				modelMap.addAttribute("unknownUser", utils.getMessage("msg.samepassdeny"));
				userForm.setId(loginUser.getUserId());
				if(null!=userForm)
					modelMap.addAttribute("userForm", userForm);
					else
						modelMap.addAttribute("userForm", new  UserForm());
				
				modelMap.addAttribute("key", base64EncodedKey);
				return "changePassword";
			}
			}else{
				modelMap.addAttribute("unknownUser", utils.getMessage("msg.wrongauthentication"));
				userForm.setId(loginUser.getUserId());
				modelMap.addAttribute("key", base64EncodedKey);
				return "changePassword";
			}
			
			//check from db store last 3 old password
			String finalOldPass = utils.getUpdatedOldPass(loginUser.getOldPassword(),decryptNewPass,passwordEncoder);
			if(finalOldPass.equals(loginUser.getOldPassword()))
            {
            	modelMap.addAttribute("unknownUser",utils.getMessage("msg.usedoldpass"));
            	userForm.setId(loginUser.getUserId());
            	if(null!=userForm)
        			modelMap.addAttribute("userForm", userForm);
        			else
        				modelMap.addAttribute("userForm", new  UserForm());
        		
				modelMap.addAttribute("key", base64EncodedKey);
				return "changePassword";
            }
			passChangeUser.setOldPassword(finalOldPass);
			passChangeUser.setEncrytedPassword(passwordEncoder.encode(decryptNewPass));
			passChangeUser.setPassChanged(true);
			appUserRepo.save(passChangeUser);
			attributes.addFlashAttribute("message", "Password updated successfully!"+ loginUser.getUserName());
			return "redirect:/changePassword";
		} else {
			modelMap.addAttribute("unknownUser", utils.getMessage("msg.unauthorized"));
			userForm.setId(loginUser.getUserId());
			if(null!=userForm)
				modelMap.addAttribute("userForm", userForm);
				else
					modelMap.addAttribute("userForm", new  UserForm());
			
			modelMap.addAttribute("key", base64EncodedKey);
			return "changePassword";
		}

	}
	@SuppressWarnings("unused")
	@RequestMapping(value = "/saveChangePassword1", method = RequestMethod.POST)
	public String NewresetPassword(@ModelAttribute @Valid UserForm userForm, HttpServletRequest req, HttpServletResponse resp,
			ModelMap modelMap,BindingResult result,@RequestParam(name="key",required=false) String key, RedirectAttributes attributes  ) throws SnmsException, Exception {
		
		AppUser loginUser = appUserDAO.findUserAccount(userDetailsService.getLoginUserName());
		if(null==userForm || userForm.equals(0)) {
			return "changePassword";
		}

		AppUser passChangeUser=appUserRepo.findById(userForm.getId()).get();

		if (result.hasErrors()) {
			if(null!=userForm)
			modelMap.addAttribute("userForm", userForm);
			else
				modelMap.addAttribute("userForm", new  UserForm());
				
			//userForm.setId(loginUser.getUserId());
			/*
			 * if(Crypt.encodeKey("mustbe16byteskey")!=null) { modelMap.addAttribute("key",
			 * Crypt.encodeKey("mustbe16byteskey")); }
			 */
			
			if(Crypt.encodeKey()!=null) {
				modelMap.addAttribute("key", Crypt.encodeKey());
				}
			return "NewUserChangedPass";
		}
		
		//String base64EncodedKey=Crypt.encodeKey("mustbe16byteskey");
		String base64EncodedKey=key;
	    
		// Check if same password is being used
		if (loginUser.getUserId() == passChangeUser.getUserId()) {
			String pass = loginUser.getEncrytedPassword();
			
			String decryptCurrentPass=Crypt.decrypt(userForm.getPassword(), base64EncodedKey);
			// Check if password is authenticated
			if (!passwordEncoder.matches(decryptCurrentPass, pass)) {
				modelMap.addAttribute("unknownUser", utils.getMessage("msg.wrongauthentication"));
				userForm.setId(loginUser.getUserId());
				modelMap.addAttribute("key", base64EncodedKey);
				return "NewUserChangedPass";
			}
			String decryptNewPass=Crypt.decrypt(userForm.getChangePassword(), base64EncodedKey);
			//Using same old password
			if(null!=decryptNewPass){
			if (passwordEncoder.matches(decryptNewPass,pass)) {
				modelMap.addAttribute("unknownUser", utils.getMessage("msg.samepassdeny"));
				userForm.setId(loginUser.getUserId());
				if(null!=userForm)
					modelMap.addAttribute("userForm", userForm);
					else
						modelMap.addAttribute("userForm", new  UserForm());
				
				modelMap.addAttribute("key", base64EncodedKey);
				return "NewUserChangedPass";
			}
			}else{
				modelMap.addAttribute("unknownUser", utils.getMessage("msg.wrongauthentication"));
				userForm.setId(loginUser.getUserId());
				modelMap.addAttribute("key", base64EncodedKey);
				return "NewUserChangedPass";
			}
			
			//check from db store last 3 old password
			String finalOldPass = utils.getUpdatedOldPass(loginUser.getOldPassword(),decryptNewPass,passwordEncoder);
			if(finalOldPass.equals(loginUser.getOldPassword()))
            {
            	modelMap.addAttribute("unknownUser",utils.getMessage("msg.usedoldpass"));
            	userForm.setId(loginUser.getUserId());
            	if(null!=userForm)
        			modelMap.addAttribute("userForm", userForm);
        			else
        				modelMap.addAttribute("userForm", new  UserForm());
        		
				modelMap.addAttribute("key", base64EncodedKey);
				return "NewUserChangedPass";
            }
			passChangeUser.setOldPassword(finalOldPass);
			passChangeUser.setEncrytedPassword(passwordEncoder.encode(decryptNewPass));
			passChangeUser.setPassChanged(true);
			appUserRepo.save(passChangeUser);
		  	req.getSession().setAttribute("menu", passChangeUser.getPassChanged());
			modelMap.addAttribute("NewPassChange","Password updated successfully!  "+ loginUser.getUserName() + "  Click Below to Login again ");
			//attributes.addFlashAttribute("NewPassChange", "Password updated successfully!"+ loginUser.getUserName() + "  Click here to Login again ");
			return "NewPassSuccess";
		} else {
			modelMap.addAttribute("unknownUser", utils.getMessage("msg.unauthorized"));
			userForm.setId(loginUser.getUserId());
			if(null!=userForm)
				modelMap.addAttribute("userForm", userForm);
				else
					modelMap.addAttribute("userForm", new  UserForm());
			
			modelMap.addAttribute("key", base64EncodedKey);
			return "NewUserChangedPass";
		}

	}
	/*@RequestMapping(value = "/financeYear")
	public String financeYear(Model model) {
		model.addAttribute("financeDetails", new FinancialMaster());
		
		model.addAttribute("fincanceList",financialYearRepo.findAll());
		return "userManagement/addFinanceDetails";
	}

	@RequestMapping(value = "/addfinanceYear")
	public String addfinanceYear(@ModelAttribute @Valid FinancialMaster financialDetails, BindingResult bindResult, Model model,
			RedirectAttributes attributes) {
		UserValidation userValidation = new UserValidation();
		userValidation.validateFinanceDetails(financialDetails, bindResult);
		boolean isUniqueFinancialYear = officeOrderDao.isUniqueFinYear(financialDetails.getFromY(),financialDetails.getToY());
		if(!isUniqueFinancialYear)
			bindResult.rejectValue("fromY", "errmsg.finYearExist");
		if (bindResult.hasErrors()) {
			model.addAttribute("financeDetails",financialDetails);
			model.addAttribute("fincanceList",financialYearRepo.findAll());
			return "userManagement/addFinanceDetails";
		}
//		if(financialDetails.isResetDin()){
			financialYearRepo.resetOfficeOrderDinSeq();
			financialYearRepo.resetNoticeDinSeq();
			financialYearRepo.resetSummonDinSeq();
//		}
		financialYearRepo.deactivateFinancialYear();
		financialDetails.setCreatedDate(new Date());
		financialDetails.setCreatedBy(new AppUser(1L));
		financialDetails.setActive(true);
		financialDetails = financialYearRepo.save(financialDetails);
		
		attributes.addFlashAttribute("message", "Financial Year "+ financialDetails.getFromY()+"-"+
				financialDetails.getToY()+" Added successfully");
		return "redirect:/financeYear";
	}*/
	
	@RequestMapping("/forgotPass")
	public String forgotPassword(ModelMap modelMap) throws Exception {
		modelMap.addAttribute("userForm", new UserForm());
		return "ForgotPassword";
	}
  
	@RequestMapping("/NewUserChangedPass")
	public String NewUserChangedPass(ModelMap modelMap) throws Exception {
		AppUser user = appUserDAO.findUserAccount(userDetailsService.getLoginUserName());
		UserForm userForm = new UserForm();
		userForm.setPassword(user.getEncrytedPassword());
		userForm.setId(user.getUserId());
		modelMap.addAttribute("userForm", userForm);
		//modelMap.addAttribute("key", Crypt.encodeKey("mustbe16byteskey"));
		modelMap.addAttribute("key", Crypt.encodeKey());
		
		return "NewUserChangedPass";
	}
	@SuppressWarnings("unused")
	@RequestMapping(value = "/getForgotOtp", method = RequestMethod.POST)
	public String getEmailOtp(@ModelAttribute @Valid UserForm userForm, BindingResult result,ModelMap model
			,HttpServletRequest req, HttpServletResponse resp) throws Exception {
	if(null == userForm || userForm.equals(0)) {
		return "ForgotPassword";
	}
		MailInfo mailInfo = new MailInfo();
		
		SNMSValidator sfioVal = new SNMSValidator();
		/*if(!sfioVal.isValidCaptcha(userForm.getCaptcha(), result))
			model.addAttribute("invalidcaptcha", "Invalid Captcha");
		*/
		if (result.hasErrors()) {
			userForm.setCaptcha("");
			if(null!=userForm)
				model.addAttribute("userForm", userForm);
				else
					model.addAttribute("userForm", new  UserForm());
			
			return "ForgotPassword";
		}

		if (isUniqueUserName(userForm.getUsername())) {
			model.addAttribute("usernameInvalid", "This email address hasn't registered.");
			model.addAttribute("userForm", new UserForm());
			return "ForgotPassword";
		}
		
		if (userForm.getUsername() != null || !"".equals(userForm.getUsername())) {
			String otp = utils.getOTP();
			
			userDetailsService.getCurrentSession().setAttribute("otp", otp);
			String generateDate = utils.getCurrentDate();
			userDetailsService.getCurrentSession().setAttribute("generateostpDate", generateDate);
			mailInfo.setOtp(otp);
			//userForm.setOtpGenerated(true);
			mailInfo.setEmail(userForm.getUsername());
			//mailBo.sendMail(userInfo, NfraConstant.EMAIL_VARIFY_OTP, req);
			model.addAttribute("sendForgotemailmsg", "The OTP has been sent to your registered email.");
		} else {
			model.addAttribute("invalidemail", "Please Enter valid email address");
			model.addAttribute("userForm", new UserForm());
			return "ForgotPassword";
		}
		model.addAttribute("userForm", userForm);
		return "ForgotPassword";
	}
	
	@RequestMapping(value = "/forgotPassword", method = RequestMethod.POST)
	public String forgotPassProcess(@ModelAttribute UserForm userForm, BindingResult result, ModelMap model,
			HttpServletRequest req, HttpServletResponse resp) throws SnmsException, Exception {
		if(null == userForm || userForm.equals(0)) {
			return "ForgotPassword";
		}
		if (result.hasErrors()) {
			if(null!=userForm)
				model.addAttribute("userForm", userForm);
				else
					model.addAttribute("userForm", new  UserForm());
			
			
			return "ForgotPassword";
		}

		SimpleDateFormat format = new SimpleDateFormat(SnmsConstant.DATE_FORMAT);
		Date parseDate = format.parse((String) userDetailsService.getCurrentSession().getAttribute("generateostpDate"));
		Calendar gc = new GregorianCalendar();
		gc.setTime(parseDate);
		gc.add(Calendar.MINUTE, SnmsConstant.OTP_EXPIRE_TIME);
		Date expiryDate = gc.getTime();

		MailInfo mailInfo = new MailInfo();

		if (expiryDate.after(new Date())) {
			if (userForm.getTextOTP().equals(userDetailsService.getCurrentSession().getAttribute("otp"))) {

				mailInfo.setEmail(userForm.getUsername());
				//mailBo.sendMail(userInfo, NfraConstant.SUCCESS_EMAIL_VARIFIED, req);
				userDetailsService.getCurrentSession().removeAttribute("otp");
				model.addAttribute("userForm", userForm);
				/* model.addAttribute("key", Crypt.encodeKey("mustbe16byteskey")); */
				
				model.addAttribute("key", Crypt.encodeKey());
				return "ForgotPasswordProcess";

			} else {
				model.addAttribute("validOtp", "Enter OTP is not valid");
				//userForm.setOtpGenerated(true);
				model.addAttribute("userForm", userForm);
				userForm.setTextOTP("");
				return "ForgotPassword";
			}
		} else {
			model.addAttribute("otpExpired", "Enter OTP has expired");
			model.addAttribute("userForm", new UserForm());
			userDetailsService.getCurrentSession().removeAttribute("otp");
			return "ForgotPassword";
		}

	}
	
	@RequestMapping(value = "/saveForgotPass", method = RequestMethod.POST)
	public String saveForgotPassword(@ModelAttribute @Valid UserForm userForm, BindingResult result, ModelMap model)
			throws Exception {
		ChangePasswordValidator chgPassword = new ChangePasswordValidator();
		chgPassword.userValidatePassword(userForm, false, 2, result);
		if (result.hasErrors()) {
			if(null!=userForm || !userForm.equals(0))
				model.addAttribute("userForm", userForm);
				else
					model.addAttribute("userForm", new  UserForm());
			
			return "ForgotPasswordProcess";
		}

		AppUser objUser = appUserDAO.findUserAccount(userForm.getUsername());
		
		//String base64EncodedKey=Crypt.encodeKey("mustbe16byteskey");
		String base64EncodedKey=Crypt.encodeKey();
	    String decryptPass=Crypt.decrypt(userForm.getChangePassword(), base64EncodedKey);
	    
		//check from db store last 3 old password
		String finalOldPass = utils.getUpdatedOldPass(objUser.getOldPassword(),decryptPass,passwordEncoder);
		if(finalOldPass.equals(objUser.getOldPassword()))
        {
        	model.addAttribute("unknownUser",utils.getMessage("msg.usedoldpass"));
        	model.addAttribute("key", base64EncodedKey);
			model.addAttribute("userForm", userForm);
			return "ForgotPasswordProcess";
        }

		String loginUName =	appUserDAO.findUserDetails(getUserDetails()).getFirstName()!=null ? appUserDAO.findUserDetails(getUserDetails()).getFirstName():""
		+appUserDAO.findUserDetails(getUserDetails()).getMiddleName()!=null? appUserDAO.findUserDetails(getUserDetails()).getMiddleName():""
		+appUserDAO.findUserDetails(getUserDetails()).getLastName()!=null?appUserDAO.findUserDetails(getUserDetails()).getLastName():"";
		objUser.setEncrytedPassword(passwordEncoder.encode(decryptPass));
		objUser.setOldPassword(finalOldPass);
		appUserRepo.save(objUser);
		model.addAttribute("successForgotmsg",
				"Your Password are successfully changed and sent notification on your registered mail.");
		auditBeanBo
				.setAuditBean(objUser.getUserId().intValue(),
						loginUName,
						"User", objUser.getUserId().intValue(), utils.getMessage("log.forgot.pass"),
						utils.getMessage("log.pass.updated"), loginUName,
						"true");
		auditBeanBo.save();
		return "redirect:/login";
	}
	public String getLoginUserName() {
		return SecurityContextHolder.getContext().getAuthentication().getName();
	}
	private AppUser getUserDetails() {
		AppUser appUser = this.appUserDAO.findUserAccount(getLoginUserName());
		return appUser;
	}

	@RequestMapping(value = "/editUser",  params = "edituser")
	public String editUser(@RequestParam(value = "edituser", required = true) Long id, Model model) {
		UserDetails user = userDetailsRepo.findById(id)
			      .orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));
		
		        user.setDesignationId(user.getDesignation().getId());		
		        user.setUnitId(user.getUnit().getUnitId());		
		        user.setUiDob(user.getDob());
		
		        user.setUiJoiningDate(user.getJoiningDate());		
		        UserRole userRole =  appUserDAO.getRoleId(user.getUserId().getUserId());
		 
		        user.setRoleId(userRole.getAppRole().getRoleId());
		 
		        List<AddDesignation> designationList = designationService.findDesignationByUsertype(new AddDesignation());
				List<UnitDetails> unitList = unitDetailsRepo.findAll(Sort.by(Sort.Direction.ASC, "unitId"));
				List<AppRole> roleList=appRoleDao.RoleList();
				model.addAttribute("designationList", designationList);
				model.addAttribute("unitList", unitList);
				model.addAttribute("roleList", roleList);	    
			   
			    model.addAttribute("userDetails", user);
				return "userManagement/updateUser";
	}
	
	
	@RequestMapping(value = "/saveUpdateUser")
	public String updateUser(@ModelAttribute @Valid UserDetails userDetails, BindingResult bindResult,
			Model model, RedirectAttributes redirect,HttpServletRequest request) throws Exception {
		UserValidation validation = new UserValidation();
		
		validation.validateUserRegComplete(userDetails,bindResult,isUniqueUserNameupdate(userDetails.getEmail(),userDetails.getUserId().getUserId()),isUniqueMobile_update(userDetails.getPrimaryMobile(),userDetails.getId()),isUniqueMobile_update(userDetails.getAlternateNo(),userDetails.getId()));
		
		if(userDetails.getDesignationId()==0L)
		{
			bindResult.rejectValue("designation", "msg.wrongId");
		}
		else{
			AddDesignation designationbyId = designationRepo.findById(userDetails.getDesignationId()).get();
			if(designationbyId==null)
				bindResult.rejectValue("designation", "msg.wrongId");
		}
		if(userDetails.getUnitId() != null)
		{
			UnitDetails unitbyId = unitDetailsRepo.findById(userDetails.getUnitId()).get();
			if(unitbyId == null)
				bindResult.rejectValue("unit", "msg.wrongId");
		}
		if(userDetails.getRoleId() != null)
		{
			AppRole roleById=appRoleRepo.findById(userDetails.getRoleId()).get();
			if(roleById==null || userDetails.getRoleId()==1L || userDetails.getRoleId()==4L)
			bindResult.rejectValue("roleId", "msg.wrongId");
		}
		
		//AppUser user = new AppUser();
		if (bindResult.hasErrors()) {
			
			
			if(null!=userDetails)
				model.addAttribute("userDetails", userDetails);
				else
					model.addAttribute("userForm", new  UserDetails());
			
                   
    		List<AddDesignation> designationList = designationRepo.findAll();
    		List<UnitDetails> unitList = unitDetailsRepo.findAll(Sort.by(Sort.Direction.ASC, "unitName"));
    		List<AppRole> roleList=appRoleDao.RoleList();
    		model.addAttribute("designationList", designationList);
    		model.addAttribute("unitList", unitList);
    		model.addAttribute("roleList", roleList);
    		model.addAttribute("userList",userDetailsRepo.findAll());
   
    		return "userManagement/updateUser";
        } else {
        
        	Optional<AppUser> appuserobj = appUserRepo.findByUserId(userDetails.getUserId().getUserId());
        	AppUser user = appuserobj.get();
			/*
			 * user.setUserName(userDetails.getEmail()); 
			 * user.setEncrytedPassword(
			 * "$2a$10$RpjdPpApu2Gk7uUJfj1jpuWpbGxCJ9yKt1ea5dJHZaO0vJBDxenTW");
			 * user.setEnabled(1); user.setOldPassword(
			 * "$2a$10$RpjdPpApu2Gk7uUJfj1jpuWpbGxCJ9yKt1ea5dJHZaO0vJBDxenTW"+SnmsConstant.
			 * OLD_PASS_SEP);
			 */
        	user.setUserName(userDetails.getEmail());
		Calendar cal = Calendar.getInstance();
		Date today = cal.getTime();
		cal.add(Calendar.YEAR, 2); // to get previous year add -1
		Date nextYear = cal.getTime();
		
		//user.setValidFrom(today);
		//user.setValidUpto(nextYear);
		 appUserRepo.save(user);
		
		
	
		userDetails.setUserId(user);
		userDetails.setDob(userDetails.getUiDob());
		userDetails.setJoiningDate(userDetails.getUiJoiningDate());
		
		userDetails.setCreatedDate(new Date());
		userDetails.setCreatedBy(userDetailsService.getUserDetails());
		
		userDetails.setDesignation(new AddDesignation(userDetails.getDesignationId()));
		userDetails.setUnit(new UnitDetails(userDetails.getUnitId()));
		userDetailsRepo.save(userDetails);
		
		 UserRole userRole =  appUserDAO.getRoleId(userDetails.getUserId().getUserId());
		//UserRole userRole = userroleobj.get();
		UserRole userRolenew = new UserRole();
		userRolenew.setId(userRole.getId());
		userRolenew.setAppRole(new AppRole(userDetails.getRoleId()));
		userRolenew.setAppUser(user);
		//UserRole userRole = new UserRole(user,new AppRole(userDetails.getRoleId()));
		userRoleRepo.save(userRolenew);
		
		auditBeanBo.setAuditBean(Integer.parseInt(userDetailsService.getUserDetails().getUserId().toString()),
				userDetails.getFirstName()+" "+userDetails.getMiddleName()+" "+userDetails.getLastName(),
				"User",Integer.parseInt(userDetailsService.getUserDetails().getUserId().toString()) , utils.getMessage("log.user.edit"), utils.getMessage("log.user.edited"), userDetailsService.getUserDetails().getUserName(),"true");
		auditBeanBo.save();	
		
			/*
			 * MailInfo info=new MailInfo(); info.setEmail(userDetails.getEmail());
			 * mailBo.sendMail(info, SnmsConstant.USER_CREATE, request);
			 */
		
		
		}
		redirect.addFlashAttribute("message", " User updated Successfully with Login Id : "+userDetails.getEmail());
		return "redirect:/getUsers";
	}
	
	@RequestMapping(value = "/getUniqueEmailupdate", method = RequestMethod.POST)
	public boolean isUniqueUserNameupdate(@RequestParam("email") String email,Long userid_toupdate) throws Exception {
		AppUser user = appUserDAO.findUserAccount(email);
		if (user == null)
			return true;
		else if (user.getUserId() == userid_toupdate)
		{			
			return true;
		}
		else			
		return false;
	}
	
	@RequestMapping(value = "/getUniqueMobile_update", method = RequestMethod.POST)
	public boolean isUniqueMobile_update(@RequestParam("mobile") String mobile,Long userid_toupdate) throws Exception {
		UserDetails user = userMangCustom.findUserDetailsMobile(mobile);
		if (user == null)
			return true;
		else if (user.getId() == userid_toupdate)
		{			
			return true;
		}
		else			
		return false;
		
	}
	
	@RequestMapping(value = "/editUser",  params = "resetPass")
	public String resetPass(@RequestParam(value = "resetPass", required = true) Long id, Model model) {
		
		SNMSValidator snmsValidator = new SNMSValidator();

		
//		UserDetails user = userDetailsRepo.findById(id)
	//		      .orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));
	//	String finalOldPass = utils.getUpdatedOldPass(loginUser.getOldPassword(),decryptNewPass,passwordEncoder);
		
		if(!snmsValidator.getValidInteger(id)){
			List<UserDetails> userList = userDetailsRepo.findAll();
				model.addAttribute("userList",userList);
				model.addAttribute("message","invalid userId");
				return "userManagement/listUsers";
		}
		
		UserDetails user = userDetailsRepo.findById(id).get();
		
		AppUser appUser =    appUserRepo.findById(id).get();
		
		if(appUser==null) {
		     	List<UserDetails> userList = userDetailsRepo.findAll();
				model.addAttribute("userList",userList);
				model.addAttribute("message","User is not Present");
				return "userManagement/listUsers";
		}
		//String finalOldPass = appUser.getOldPassword();
		
		String CurrentPass = "$2a$10$RpjdPpApu2Gk7uUJfj1jpuWpbGxCJ9yKt1ea5dJHZaO0vJBDxenTW";
		//String base64EncodedKey=Crypt.encodeKey("mustbe16byteskey");
		
		String base64EncodedKey=Crypt.encodeKey("");
		String finalOldPass = utils.getResetOldPass(appUser.getOldPassword(),CurrentPass,passwordEncoder);
		
		appUser.setEncrytedPassword("$2a$10$RpjdPpApu2Gk7uUJfj1jpuWpbGxCJ9yKt1ea5dJHZaO0vJBDxenTW");
		appUser.setOldPassword(finalOldPass);
		appUser.setPassChanged(false);
		appUserRepo.save(appUser);
		//List<UserDetails> userList = userDetailsRepo.findAll();
		 
		model.addAttribute("message", "Password  is Reset successfully of User  " + userDetailsService.getFullName(user));

		return "userManagement/PassResetSucc";
	}
	
		

	
	
	

}
