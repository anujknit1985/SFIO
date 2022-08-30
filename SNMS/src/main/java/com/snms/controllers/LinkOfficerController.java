package com.snms.controllers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.snms.dao.AppUserDAO;
import com.snms.entity.AddDesignation;
import com.snms.entity.AppRole;
import com.snms.entity.AppUser;
import com.snms.entity.Inspector;
import com.snms.entity.InvestigationStatus;
import com.snms.entity.Linkofficer;
import com.snms.entity.PersonDATA;
import com.snms.entity.UnitDetails;
import com.snms.entity.UserDetails;
import com.snms.entity.personcompanyApproval;
import com.snms.service.AuditBeanBo;
import com.snms.service.LinkOfficerRepository;
import com.snms.service.UnitDetailsRepository;
import com.snms.service.UserDetailsRepository;
import com.snms.service.UserDetailsServiceImpl;
import com.snms.service.UserManagementCustom;
import com.snms.utils.SnmsConstant;
import com.snms.utils.Utils;
import com.snms.validation.UserValidation;

@Controller
public class LinkOfficerController {
	private static final Logger logger = Logger.getLogger(LinkOfficerController.class);
	@Autowired
	private UserManagementCustom userMangCustom;
	@Autowired
	private UserDetailsServiceImpl userDetailsService;
	@Autowired
	private UserDetailsRepository userDetailsRepo;
	@Autowired
	private UnitDetailsRepository unitDetailsRepo;
	@Autowired
	private AuditBeanBo auditBeanBo;
	@Autowired
	private AppUserDAO appUserDAO;
	@Autowired
	private Utils utils;
	@Autowired
	private LinkOfficerRepository linkoffRepo;

	@RequestMapping(value = "/LinkOfficer")
	public String addStatus(ModelMap model) {
		List<UnitDetails> unitList = unitDetailsRepo.findAll(Sort.by(Sort.Direction.ASC, "unitName"));
	
         List <UnitDetails> UnitDetails1 =  new ArrayList<UnitDetails>();
		int unitcount=0;
		for(UnitDetails unitList2 : unitList) {
			if(unitList2.getUnitName().equalsIgnoreCase("Administrator Unit")) {
				
				unitList.remove(unitList2.getUnitId()-1);
			}else {
				UnitDetails1.add(new UnitDetails(unitList2.getUnitId(),unitList2.getUnitName(),unitList2.getLocation(),unitList2.getAddress(),unitList2.getTelephoneNo(),unitList2.getFaxNo(),unitList2.getEMail(),unitList2.getCreatedDate()));
			}
			unitcount++;
		}
		List<UserDetails> AllUserList = userMangCustom.findByRole(SnmsConstant.Role_USER);
		

		Long count = 0L;
		List<UserDetails> UserList = new ArrayList<UserDetails>();

		for (UserDetails user : AllUserList) {
			UserList.add(new UserDetails(user.getId(), user.getSalutation() + " " + userDetailsService.getFullName(user)
					+ " (" + user.getUnit().getUnitName() + ") and" + user.getDesignation().getDesignation() + ""));
			count++;
		}

		model.addAttribute("unitList",UnitDetails1);
		model.addAttribute("UserList", UserList);
		model.addAttribute("linkofficer", new Linkofficer());
		return "userManagement/LinkOfficer";
	}

	
	@RequestMapping(value = "/LinkOfficerList")
	public String LinkofficeLinst (ModelMap model) {
		List<Linkofficer> linkofficerList = linkoffRepo.findAll();
		
		model.addAttribute("linkofficerList", linkofficerList);
		
		return "userManagement/LinkOfficerList";
	}
	
	@RequestMapping(value = "/DeactivateLink",  params = "Deactvuser")
	public String deactivateUser(@RequestParam(value = "Deactvuser", required = true) Long id, Model model) {
		Linkofficer link = linkoffRepo.findById(id).get();
		link.setIsActive(false);
		linkoffRepo.save(link);
		List<Linkofficer> linkofficerList = linkoffRepo.findAll();
		
		model.addAttribute("linkofficerList", linkofficerList);
		model.addAttribute("message", "Link Officer is Deactivate");
		return "userManagement/LinkOfficerList";
	}
	
	@RequestMapping(value = "/addLinkOfficer")
	public String createNewRole(@ModelAttribute @Valid Linkofficer linkofficer, BindingResult bindResult, Model model,
			RedirectAttributes redirect) throws Exception {

		UserValidation userval = new UserValidation();
		SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
		if (linkofficer.getUnitId() == null) {
			bindResult.rejectValue("unitId", "errmsg.required");
		}
		if (linkofficer.getLinkunitId() == null) {
			bindResult.rejectValue("LinkunitId", "errmsg.required");
		}

		if (linkofficer.getRegularId() == null) {
			bindResult.rejectValue("regularId", "errmsg.required");
		}
		if (linkofficer.getUserDetails() == null) {
			bindResult.rejectValue("userDetails", "errmsg.required");
		}
		
	Boolean linkid =  getlinkuser(linkofficer);
	if(linkid == true) {
	       redirect.addFlashAttribute("message1", "Link Officer is On Leave  " +linkofficer.getUserDetails().getFirstName());
	       return "redirect:/LinkOfficer";
	}
		/*
		 * if(linkofficer.getFromDate()!=null) { if(linkofficer.getFromDate().before(new
		 * Date())){ bindResult.rejectValue("fromDate", "errmsg.fromdatelink"); } }
		 */
		String loginUName = appUserDAO.findUserDetails(getUserDetails()).getFirstName() != null
				? appUserDAO.findUserDetails(getUserDetails()).getFirstName()
				: "" + appUserDAO.findUserDetails(getUserDetails()).getMiddleName() != null
						? appUserDAO.findUserDetails(getUserDetails()).getMiddleName()
						: "" + appUserDAO.findUserDetails(getUserDetails()).getLastName() != null
								? appUserDAO.findUserDetails(getUserDetails()).getLastName()
								: "";
		userval.validLinkofficer(linkofficer, bindResult);
		if (bindResult.hasErrors()) {
			List<UnitDetails> unitList = unitDetailsRepo.findAll(Sort.by(Sort.Direction.ASC, "unitName"));
			unitList.remove(2);

			List<UserDetails> AllUserList = userMangCustom.findByRole(SnmsConstant.Role_USER);
			;

			Long count = 0L;
			List<UserDetails> UserList = new ArrayList<UserDetails>();
			 List <UnitDetails> UnitDetails1 =  new ArrayList<UnitDetails>();
				int unitcount=0;
				for(UnitDetails unitList2 : unitList) {
					if(unitList2.getUnitName().equalsIgnoreCase("Administrator Unit")) {
						
						unitList.remove(unitList2.getUnitId()-1);
					}else {
						UnitDetails1.add(new UnitDetails(unitList2.getUnitId(),unitList2.getUnitName(),unitList2.getLocation(),unitList2.getAddress(),unitList2.getTelephoneNo(),unitList2.getFaxNo(),unitList2.getEMail(),unitList2.getCreatedDate()));
					}
					unitcount++;
				}
			for (UserDetails user : AllUserList) {
				UserList.add(new UserDetails(user.getId(),
						user.getSalutation() + " " + userDetailsService.getFullName(user) + " ("
								+ user.getUnit().getUnitName() + ") and" + user.getDesignation().getDesignation()
								+ ""));
				count++;
			}

			model.addAttribute("unitList", UnitDetails1);
			model.addAttribute("UserList", UserList);
			model.addAttribute("linkofficer", linkofficer);

			auditBeanBo.setAuditBean(Integer.parseInt(userDetailsService.getUserDetails().getUserId().toString()),
					loginUName, "User", Integer.parseInt(userDetailsService.getUserDetails().getUserId().toString()),
					utils.getMessage("log.linkOfficer.activatefail"),
					utils.getMessage("log.linkOfficer.activatefailDesc"), loginUName, "true");
			auditBeanBo.save();
			return "userManagement/LinkOfficer";
		} else {
			List<Linkofficer> lo = linkoffRepo.findAllByRegularId(linkofficer.getRegularId());

			for (int i = 0; i < lo.size(); i++) {
				lo.get(i).setIsActive(false);

				linkoffRepo.saveAll(lo);
			}

			// Date fromdate = formatter.parse(linkofficer.getFromDate1());
			// Date todate = formatter.parse(linkofficer.getToDate1());
			/*
			 * linkofficer.setFromDate(fromdate); linkofficer.setToDate(todate);
			 */
			linkofficer.setIsActive(true);
			linkoffRepo.save(linkofficer);
			auditBeanBo.setAuditBean(Integer.parseInt(userDetailsService.getUserDetails().getUserId().toString()),
					loginUName, "User", Integer.parseInt(userDetailsService.getUserDetails().getUserId().toString()),
					utils.getMessage("log.linkOfficer.activate"), utils.getMessage("log.linkOfficer.activateDesc"),
					loginUName, "true");
			auditBeanBo.save();

		}
		List<UnitDetails> unitList = unitDetailsRepo.findAll(Sort.by(Sort.Direction.ASC, "unitName"));
		
		 List <UnitDetails> UnitDetails1 =  new ArrayList<UnitDetails>();
			int unitcount=0;
			for(UnitDetails unitList2 : unitList) {
				if(unitList2.getUnitName().equalsIgnoreCase("Administrator Unit")) {
					
					unitList.remove(unitList2.getUnitId()-1);
				}else {
					UnitDetails1.add(new UnitDetails(unitList2.getUnitId(),unitList2.getUnitName(),unitList2.getLocation(),unitList2.getAddress(),unitList2.getTelephoneNo(),unitList2.getFaxNo(),unitList2.getEMail(),unitList2.getCreatedDate()));
				}
				unitcount++;
			}

		List<UserDetails> AllUserList = userMangCustom.findByRole(SnmsConstant.Role_USER);
		;

		Long count = 0L;
		List<UserDetails> UserList = new ArrayList<UserDetails>();

		for (UserDetails user : AllUserList) {
			UserList.add(new UserDetails(user.getId(), user.getSalutation() + " " + userDetailsService.getFullName(user)
					+ " (" + user.getUnit().getUnitName() + ") and" + user.getDesignation().getDesignation() + ""));
			count++;
		}

		model.addAttribute("unitList", UnitDetails1);
		model.addAttribute("UserList", UserList);
		model.addAttribute("linkofficer", new Linkofficer());
		model.addAttribute("message", "Link Officer is Activated ");
		return "userManagement/LinkOfficer";
	}

	
	
	public String getLoginUserName() {
		return SecurityContextHolder.getContext().getAuthentication().getName();
	}

	public AppUser getUserDetails() throws Exception {
		AppUser appUser = this.appUserDAO.findUserAccount(getLoginUserName());
		return appUser;
	}

	public Boolean getlinkuser( Linkofficer linkid) throws Exception {
		
		//UserDetails userdetail = userDetailsRepo.findById(linkid).get();
   List<Linkofficer> linkOff = 	linkoffRepo.findAllByRegularId(linkid.getRegularId());
   
   for (Linkofficer linkuser : linkOff) {
   if(linkuser.getIsActive() == true ) {
	   if(linkid.getFromDate().before(linkuser.getToDate())) {
		   return true;
	   }
   }
   }
   return false;
	}
}
