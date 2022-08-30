package com.snms.controllers;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.apache.log4j.Logger;
import org.owasp.esapi.ESAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.snms.dao.AppRoleDAO;
import com.snms.dao.AppUserDAO;
import com.snms.dao.NoticeDao;
import com.snms.dao.SummonDao;
import com.snms.dto.InspectorDTO;
import com.snms.dto.InspectorListDTO;
import com.snms.dto.StatusDTO;
import com.snms.entity.AppUser;
import com.snms.entity.CaseDetails;
import com.snms.entity.Company;
import com.snms.entity.Inspector;
import com.snms.entity.NoticeStatus;
import com.snms.entity.SummonStatus;
import com.snms.entity.SummonType;
import com.snms.entity.UserDetails;
import com.snms.entity.inspectorHistory;
import com.snms.service.AppUserRepository;
import com.snms.service.AuditBeanBo;
import com.snms.service.CaseDetailsRepository;
import com.snms.service.InitiateNoticeDraftRepository;
import com.snms.service.InitiateSummonDraftRepository;
import com.snms.service.InspectorRepository;
import com.snms.service.NoticeRepository;
import com.snms.service.NoticeTemplateRepository;
import com.snms.service.SummonRepository;
import com.snms.service.SummonTemplateRepository;
import com.snms.service.SummonTypeNew;
import com.snms.service.UnitDetailsRepository;
import com.snms.service.UserDeatilsRepository;
import com.snms.service.UserDetailsRepository;
import com.snms.service.UserDetailsServiceImpl;
import com.snms.service.UserManagementCustom;
import com.snms.service.UserRoleRepository;
import com.snms.utils.Utils;
import com.snms.validation.SNMSValidator;
import com.snms.validation.SfioValidator;
import com.snms.validation.SummonNoticeValidation;
@Controller
public class PhySummonNoticeController {
	private static final Logger logger = Logger.getLogger(PhySummonNoticeController.class);
	@Autowired
	private UserDetailsServiceImpl userDetailsService;
	@Autowired
	private AuditBeanBo auditBeanBo;
	@Autowired
	private AppRoleDAO appRoleDao;
	@Autowired
	private Utils utils;
	@Autowired
	private NoticeRepository noticeRepo;
	@Autowired
	private NoticeTemplateRepository noticeTempRepo;
	@Autowired
	private UserManagementCustom userMangCustom;
	@Autowired
	private CaseDetailsRepository caseDetailsRepository;
	@Autowired
	private SummonDao summonDao;
	@Autowired
	private UserDeatilsRepository userdetailRepo;
	@Autowired
	private NoticeDao noticeDao;
	@Autowired
	private SummonRepository summonRepo;
	@Autowired
	private SummonTemplateRepository summonTempRepo;
	@Autowired
	private UserDetailsRepository userDetailsRepo;
	@Autowired
	private InspectorRepository inspRepo;
	@Autowired
	private UnitDetailsRepository unitDetailsRepo;
	@Autowired
	private UserRoleRepository userRoleRepo;
	
	
	@Autowired 
	private AppUserRepository appUserRepo;
	/*
	 * @Autowired private SummonTypeDetails summonTypeDetails;
	 */

	@Autowired
	private SummonTypeNew SummonTypeNewDetails;
	@Autowired
	private AppUserDAO appUserDAO;
	@Autowired
	private InitiateNoticeDraftRepository initiateNoticeDarftRepo;
	@Autowired
	private InitiateSummonDraftRepository initiatesummonrepo;

	@Autowired 
	private InspectorRepository inspectRepo;
	// Notice Flow Code Started
	@Value("${file.upload}")
	public String filePath;


	@RequestMapping(value = "getPhysicalsent")
	public String physicalSendSummonNotice(Model model) throws Exception {
	model.addAttribute("summonStatus", new SummonStatus());
	return "notice/physicalSendSummonNotice";
	}
	
	
	@RequestMapping(value = "PhysicallysentList")
	public String PhysicallysentList(ModelMap modelMap) throws Exception {
		List<SummonStatus> sumonlist=summonDao.findPhySummonList(getUserDetails(),false,true);		
		modelMap.addAttribute("PhySummon",sumonlist);	
		 List<NoticeStatus> noticelist = noticeDao.findPhyNoticeList(getUserDetails(),false,true);
		  modelMap.addAttribute("PhyNotice",noticelist);	
		  modelMap.addAttribute("summonStatus", new SummonStatus());
	return "notice/physicalSendSummonNoticeList";
	}
	
	
	@RequestMapping(value ="getPeningPhysicalsentNotice")
	public String getPeningPhysicalsent(ModelMap modelMap) throws Exception {
		UserDetails user = userdetailRepo.findById(getUserDetails().getUserId()).get();
		 List<NoticeStatus> PendingPhyNotice   =  summonDao.findNoticeByUnits(user.getUnit().getUnitId(), 5);
		 modelMap.addAttribute("PendingPhyNotice", PendingPhyNotice);
		  modelMap.addAttribute("noticeStatus", new NoticeStatus());
		 modelMap.addAttribute("UnitName", user.getUnit().getUnitName());
		return "notice/pendingPhysicalSentNotice";
	}
	
	
	@RequestMapping(value ="getPeningPhysicalsentNoticeAddl")
	public String getPeningPhysicalsentNoticeAddl(ModelMap modelMap) throws Exception {
		UserDetails user = userdetailRepo.findById(getUserDetails().getUserId()).get();
	//	List<Inspector>caselist = inspRepo.findAllByAppUserAndIsActiveAndIsAdo(getUserDetails(),true,true);
		List<NoticeStatus> phyPendingNotice = new ArrayList<NoticeStatus>();;
		int count = 1;
		List<Inspector>caselist = inspRepo.findAllByAppUserAndIsActiveAndIsAdo(getUserDetails(),true,true);
		
		List<NoticeStatus> 	 phyPendingNotice1 = summonDao.findPendingNoticeAddl(getUserDetails());  
	    for(NoticeStatus ns : phyPendingNotice1) {
	    	UserDetails userDetals = userdetailRepo.findById(ns.getAppUser().getUserId()).get();

			String UName = userDetailsService.getFullName(userDetals);
	    	phyPendingNotice.add(new NoticeStatus(ns.getId(),ns.getCaseDetails(),ns.getSummonType(),ns.getAprrovalStage1(),ns.getAprrovalStage2(),ns.getAppUser(),ns.getApprovalDate(),ns.getDateOfAppear(),ns.getIsSensitive(),ns.getNoticeDin(),ns.getRemark(),ns.getSummonNo(),UName,ns.getCreatedDate()));
	    	count++;
	    }
		 modelMap.addAttribute("PendingPhyNotice", phyPendingNotice);
		  modelMap.addAttribute("noticeStatus", new NoticeStatus());
		 modelMap.addAttribute("UnitName", user.getUnit().getUnitName());
		return "notice/pendingPhyNoticeAddl";
	}
	@RequestMapping(value ="getPeningPhysicalsentSummonAddl")
	public String getPeningPhysicalsentSummonAddl(ModelMap modelMap) throws Exception {
		UserDetails user = userdetailRepo.findById(getUserDetails().getUserId()).get();
	//	List<Inspector>caselist = inspRepo.findAllByAppUserAndIsActiveAndIsAdo(getUserDetails(),true,true);
		List<SummonStatus> physummonYetToUpload = summonDao.findPendingSummonAddl(getUserDetails());  
		List<SummonStatus> phyPendingSummon = new ArrayList<SummonStatus>();
		int count =1;
		for(SummonStatus ns : physummonYetToUpload) {
	    	UserDetails userDetals = userdetailRepo.findById(ns.getAppUser().getUserId()).get();

			String UName = userDetailsService.getFullName(userDetals);
			phyPendingSummon.add(new SummonStatus(ns.getId(),ns.getCaseDetails(),ns.getSummonType(),ns.getAprrovalStage1(),ns.getAprrovalStage2(),ns.getAppUser(),ns.getApprovalDate(),ns.getDateOfAppear(),ns.getIsSensitive(),ns.getSummonDin(),ns.getRemark(),ns.getSummonNo(),UName,ns.getCreatedDate()));
	    	count++;
	    }
		 modelMap.addAttribute("physummonYetToUpload", phyPendingSummon);
		 modelMap.addAttribute("summonStatus", new SummonStatus());
		 modelMap.addAttribute("UnitName", user.getUnit().getUnitName());
		return "notice/pendingPhySummonAddl";
	}
	@RequestMapping(value="getPeningPhysicalsentSummon")
	public String getPeningPhysicalsentSummon(ModelMap modelMap) throws Exception{
		UserDetails user = userdetailRepo.findById(getUserDetails().getUserId()).get();
		List<SummonStatus> physummonYetToUpload = summonDao.physummonYetToUploadByUnits(user.getUnit().getUnitId());
		modelMap.addAttribute("physummonYetToUpload", physummonYetToUpload);
		 modelMap.addAttribute("UnitName", user.getUnit().getUnitName());
		 modelMap.addAttribute("summonStatus", new SummonStatus());
		return "notice/physummonYetToUpload";
	}
	
	
	@RequestMapping(value="pendingphysummonDetails", params = "caseDetails")
	public String getpendingSummon(@RequestParam(value = "caseDetails", required = true) Long caseId, Model model) {
		SNMSValidator snmsValid = new SNMSValidator();

		if (!snmsValid.getValidInteger(caseId)) {
			model.addAttribute("message", "Invalid caseId ");
			return "caseDetails/ErrorPage";
		}
		Optional<CaseDetails> caseDe = caseDetailsRepository.findById(caseId);
		CaseDetails caseDetails = caseDe.get();
		model.addAttribute("caseDetails", caseDetails);
		InspectorListDTO inspList = getInspectorListAndCompany(caseDetails.getId());
		/*
		 * if(inspList.getIoName()!=null) model.addAttribute("ioName",
		 * inspList.getIoName());
		 */
//		inspList.getInspctorList().removeIf( insp -> insp.isAdo()==true );
//		inspList.getCopyToList().removeIf( insp -> insp.isAdo()==true );
		model.addAttribute("inspList", inspList.getInspctorList());
		model.addAttribute("copyToList", inspList.getCopyToList());
		model.addAttribute("comList", inspList.getCompanyList());
		model.addAttribute("backtype", 2);
		return "notice/caseDetailView";
	}
	
	
	@RequestMapping(value="pendingphynoticeDetails", params = "caseDetails")
	public String pendingphynoticeDetails(@RequestParam(value = "caseDetails", required = true) Long caseId, Model model) {
		SNMSValidator snmsValid = new SNMSValidator();

		if (!snmsValid.getValidInteger(caseId)) {
			model.addAttribute("message", "Invalid caseId ");
			return "caseDetails/ErrorPage";
		}
		Optional<CaseDetails> caseDe = caseDetailsRepository.findById(caseId);
		CaseDetails caseDetails = caseDe.get();
		model.addAttribute("caseDetails", caseDetails);
		InspectorListDTO inspList = getInspectorListAndCompany(caseDetails.getId());
		/*
		 * if(inspList.getIoName()!=null) model.addAttribute("ioName",
		 * inspList.getIoName());
		 */
//		inspList.getInspctorList().removeIf( insp -> insp.isAdo()==true );
//		inspList.getCopyToList().removeIf( insp -> insp.isAdo()==true );
		model.addAttribute("inspList", inspList.getInspctorList());
		model.addAttribute("copyToList", inspList.getCopyToList());
		model.addAttribute("comList", inspList.getCompanyList());
		model.addAttribute("backtype", 2);
		return "notice/caseDetailView";
	}
	
	@RequestMapping(value = "PysicatDetails")
	public String PysicatDetails(@ModelAttribute SummonStatus summonStatus,ModelMap modelMap,BindingResult result) throws Exception {
		SfioValidator sfv = new SfioValidator();
		if(summonStatus.getType()==null) {
			result.rejectValue("type", "msg.wrongId");
		}
		if(summonStatus.getType()==" ") {
			result.rejectValue("type", "msg.wrongId");
		}
		if(summonStatus.getDinNumber()==null) {
			result.rejectValue("DinNumber", "msg.wrongId");
		}
		if(summonStatus.getDinNumber()!=null) {
		  if(summonStatus.getDinNumber().length()<18) {
			  result.rejectValue("DinNumber", "msg.wrongId");
		  }
		 // sfv.isValidDIN("DinNumber", summonStatus.getDinNumber(), result);
		}
		if (result.hasErrors()) {
			modelMap.addAttribute("summonStatus", summonStatus);
	    	// modelMap.addAttribute("message", "This summon is already E-signed");
	    	 return "notice/physicalSendSummonNotice";
		}
		System.out.println(summonStatus.getType());
		 SimpleDateFormat sdformat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
	    String  type =  summonStatus.getType();
	    if(type.equalsIgnoreCase("S")) {
	    SummonStatus summonSatus1 = summonRepo.findAllBySummonDin(summonStatus.getDinNumber().trim());
	    if(summonSatus1==null) {
	    	
	    	logger.info(summonStatus.getDinNumber());
       	 modelMap.addAttribute("summonStatus", summonStatus);
	    	 modelMap.addAttribute("message", "This summon is not available ");
	    	 return "notice/physicalSendSummonNotice";
	     }
       
	             Boolean userdetail = summonDao.finduserPresentisPresent(summonSatus1.getCaseDetails(),getUserDetails());
	 //   SummonStatus summonSatus18 = summonDao.findAllBySummonDin(summonStatus.getDinNumber(),getUserDetails())
	             
	             if(userdetail==false) {
	            	 modelMap.addAttribute("summonStatus", summonStatus);
	    	    	 modelMap.addAttribute("message", "This case is not assigned to  this IO ");
	    	    	 return "notice/physicalSendSummonNotice"; 
	             }
	       Date d2 = sdformat.parse("2021-09-01");
	     
	    //  System.out.println("summon date"+summonSatus1.getCreatedDate());
			/*
			 * if(summonSatus1.getCreatedDate().after(d2)) {
			 * System.out.println("print date "); }
			 */
	      
        
        if(summonSatus1.getCreatedDate().after(d2)) {
        	 modelMap.addAttribute("summonStatus", summonStatus);
	    	 modelMap.addAttribute("message", "Din generated before 01-09-2021 can upload only  ");
	    	 return "notice/physicalSendSummonNotice";
	      }
	     if(summonSatus1.getIsSigned()==1) {
	    	 modelMap.addAttribute("summonStatus", summonStatus);
	    	 modelMap.addAttribute("message", "This summon is already E-signed");
	    	 return "notice/physicalSendSummonNotice";
	     }
	     
			/*
			 * if(summonSatus1.getIsOffline()==true) { modelMap.addAttribute("summonStatus",
			 * summonStatus); modelMap.addAttribute("message",
			 * "This summon is offline Generated"); return
			 * "notice/physicalSendSummonNotice"; }
			 */
	   
	     
			/*
			 * if(summonSatus1.getCreatedDate().after(d2)) {
			 * modelMap.addAttribute("SummonStatus", summonStatus);
			 * modelMap.addAttribute("message", "Physically"); return
			 * "notice/physicalSendSummonNotice"; }
			 */
	     long individualType = summonSatus1.getSummonType().getIndividualType().getIndividualId();
	 	commonObjectSummon(modelMap,summonSatus1.getCaseDetails().getId());
	 	
		 SummonType   findByTypeDetails = SummonTypeNewDetails.findById(summonSatus1.getSummonType().getId()).get();
		 
	 	 SummonType  summonType  = new  SummonType();
			if(individualType==1 ||individualType==2) {
			    
				summonType.setFdirName(findByTypeDetails.getName());
				summonType.setFdirReg_no(findByTypeDetails.getRegistration_no());
				summonType.setFdirAddr(findByTypeDetails.getAddress());
				summonType.setFdirEmail(findByTypeDetails.getEmail());
				summonType.setFdirMobile(findByTypeDetails.getMobileNo());
				summonType.setFdiroffJoinDate(findByTypeDetails.getOffJoinDate());
			    summonType.setIndividualId(findByTypeDetails.getIndividualType().getIndividualId());
			    summonType.setIndividualType(findByTypeDetails.getIndividualType());
			    summonSatus1.setSummonType(summonType);
			}
			
			if(individualType==3 || individualType==4) {
				
				summonType.setEmpName(findByTypeDetails.getName());
				summonType.setEmpAddress(findByTypeDetails.getAddress());
				summonType.setEmpdesgi(findByTypeDetails.getDesignation());
				summonType.setEmpDob(findByTypeDetails.getDob());
				summonType.setEmpemail(findByTypeDetails.getEmail());
				summonType.setEmpissuplace(findByTypeDetails.getPlaceof_issued());
				summonType.setEmpMobile(findByTypeDetails.getMobileNo());
				summonType.setEmpnatid(findByTypeDetails.getNationalId());
				summonType.setEmpNation(findByTypeDetails.getNationality());
				summonType.setEmpoffdate(findByTypeDetails.getOffJoinDate());
				summonType.setEmpPassDate(findByTypeDetails.getIssueDate());
				summonType.setEmpPassport(findByTypeDetails.getPassport());
				summonType.setEmpsex(findByTypeDetails.getSex());
			    summonType.setIndividualId(findByTypeDetails.getIndividualType().getIndividualId());
			    summonType.setIndividualType(findByTypeDetails.getIndividualType());
			    summonSatus1.setSummonType(summonType);
			}
			
			
			if(individualType == 5 || individualType == 6) {
				summonType.setAgentAddress(findByTypeDetails.getAddress());
				summonType.setAgentcompany(findByTypeDetails.getNameCompany());
				summonType.setAgentEmail(findByTypeDetails.getEmail());
				summonType.setAgentMobile(findByTypeDetails.getMobileNo());
				summonType.setAgentName(findByTypeDetails.getName());
				summonType.setAgentRegno(findByTypeDetails.getRegistration_no());
			    summonType.setIsACin(findByTypeDetails.getIsCin());
				summonType.setIndividualId(findByTypeDetails.getIndividualType().getIndividualId());
				 summonType.setIndividualType(findByTypeDetails.getIndividualType());
				 summonSatus1.setSummonType(summonType);
			}
			
			if(individualType== 7) {
				summonType.setOthAddress(findByTypeDetails.getAddress());
				summonType.setOthRelation(findByTypeDetails.getRelationwithcompany());
				summonType.setOthEmail(findByTypeDetails.getEmail());
				summonType.setOthMobile(findByTypeDetails.getMobileNo());
				summonType.setOthName(findByTypeDetails.getName());
				summonType.setIsOCin(findByTypeDetails.getIsCin());
				summonType.setOthRegno(findByTypeDetails.getRegistration_no());
				summonType.setIndividualType(findByTypeDetails.getIndividualType());
				summonType.setIndividualId(findByTypeDetails.getIndividualType().getIndividualId());
				summonSatus1.setSummonType(summonType);
			}
	 	modelMap.addAttribute("summonStatus", summonSatus1);
	 	return "notice/physicalSummonNoticeDetails";
	    }
	    else{
	    NoticeStatus ns =  noticeRepo.findAllByNoticeDin(summonStatus.getDinNumber());	
	    if(ns ==null) {
       	 modelMap.addAttribute("summonStatus", summonStatus);
       	 result.rejectValue("DinNumber", "msg.wrongDin");
       		if (result.hasErrors()) {
       			modelMap.addAttribute("summonStatus", summonStatus);
       	    	// modelMap.addAttribute("message", "This summon is already E-signed");
       			 modelMap.addAttribute("summonStatus", summonStatus);
       	    	 return "notice/physicalSendSummonNotice";
       		}
	    	 
	     }
	    Boolean userdetail = summonDao.finduserPresentisPresent(ns.getCaseDetails(),getUserDetails());
	    if(userdetail==false) {
	    	 modelMap.addAttribute("summonStatus", summonStatus);
	    	 modelMap.addAttribute("message", "This case is not assigned to  this IO");
	    	 return "notice/physicalSendSummonNotice";
	    }
	    long individualType = ns.getSummonType().getIndividualType().getIndividualId();
	 	 	commonObjectSummon(modelMap,ns.getCaseDetails().getId());
	 	 	
	 		 SummonType   findByTypeDetails = SummonTypeNewDetails.findById(ns.getSummonType().getId()).get();
	 		
	 		 
		     Date d2 = sdformat.parse("2021-09-01");
		        if(ns.getIsSigned()==1) {
			    	 modelMap.addAttribute("summonStatus", summonStatus);
			    	 modelMap.addAttribute("message", "This Notice is already E-signed");
			    	 return "notice/physicalSendSummonNotice";
			     }
		        if(ns.getCreatedDate().after(d2)) {
		        	 modelMap.addAttribute("summonStatus", summonStatus);
			    	 modelMap.addAttribute("message", "Din generated before 01-09-2021 can be uploaded  ");
			    	 return "notice/physicalSendSummonNotice";
			      }
	    SummonType  summonType  = new  SummonType();
		if(individualType==1 ||individualType==2) {
		    
			summonType.setFdirName(findByTypeDetails.getName());
			summonType.setFdirReg_no(findByTypeDetails.getRegistration_no());
			summonType.setFdirAddr(findByTypeDetails.getAddress());
			summonType.setFdirEmail(findByTypeDetails.getEmail());
			summonType.setFdirMobile(findByTypeDetails.getMobileNo());
			summonType.setFdiroffJoinDate(findByTypeDetails.getOffJoinDate());
		    summonType.setIndividualId(findByTypeDetails.getIndividualType().getIndividualId());
		    summonType.setIndividualType(findByTypeDetails.getIndividualType());
		    ns.setSummonType(summonType);
		}
		
		if(individualType==3 || individualType==4) {
			
			summonType.setEmpName(findByTypeDetails.getName());
			summonType.setEmpAddress(findByTypeDetails.getAddress());
			summonType.setEmpdesgi(findByTypeDetails.getDesignation());
			summonType.setEmpDob(findByTypeDetails.getDob());
			summonType.setEmpemail(findByTypeDetails.getEmail());
			summonType.setEmpissuplace(findByTypeDetails.getPlaceof_issued());
			summonType.setEmpMobile(findByTypeDetails.getMobileNo());
			summonType.setEmpnatid(findByTypeDetails.getNationalId());
			summonType.setEmpNation(findByTypeDetails.getNationality());
			summonType.setEmpoffdate(findByTypeDetails.getOffJoinDate());
			summonType.setEmpPassDate(findByTypeDetails.getIssueDate());
			summonType.setEmpPassport(findByTypeDetails.getPassport());
			summonType.setEmpsex(findByTypeDetails.getSex());
		    summonType.setIndividualId(findByTypeDetails.getIndividualType().getIndividualId());
		    summonType.setIndividualType(findByTypeDetails.getIndividualType());
		    ns.setSummonType(summonType);
		}
		
		
		if(individualType == 5 || individualType == 6) {
			summonType.setAgentAddress(findByTypeDetails.getAddress());
			summonType.setAgentcompany(findByTypeDetails.getNameCompany());
			summonType.setAgentEmail(findByTypeDetails.getEmail());
			summonType.setAgentMobile(findByTypeDetails.getMobileNo());
			summonType.setAgentName(findByTypeDetails.getName());
			summonType.setAgentRegno(findByTypeDetails.getRegistration_no());
		    summonType.setIsACin(findByTypeDetails.getIsCin());
			summonType.setIndividualId(findByTypeDetails.getIndividualType().getIndividualId());
			 summonType.setIndividualType(findByTypeDetails.getIndividualType());
			 ns.setSummonType(summonType);
		}
		
		if(individualType== 7) {
			summonType.setOthAddress(findByTypeDetails.getAddress());
			summonType.setOthRelation(findByTypeDetails.getRelationwithcompany());
			summonType.setOthEmail(findByTypeDetails.getEmail());
			summonType.setOthMobile(findByTypeDetails.getMobileNo());
			summonType.setOthName(findByTypeDetails.getName());
			summonType.setIsOCin(findByTypeDetails.getIsCin());
			summonType.setOthRegno(findByTypeDetails.getRegistration_no());
			summonType.setIndividualType(findByTypeDetails.getIndividualType());
			summonType.setIndividualId(findByTypeDetails.getIndividualType().getIndividualId());
			ns.setSummonType(summonType);
		}
	    modelMap.addAttribute("NoticeStatus", ns);
	    return "notice/physicalNoticeDetails";
	    }
		
	
	}

	
	@RequestMapping(value = "genPhysicalsendAs")
	public String genPhysicalsendAs(@ModelAttribute SummonStatus summonStatus,ModelMap modelMap,BindingResult result) throws Exception {
	//model.addAttribute("SummonStatus", new SummonStatus());
		SummonStatus summonStatusfile= summonRepo.findById(summonStatus.getId()).get();
		SummonNoticeValidation snv = new SummonNoticeValidation();
		snv.validatefile(summonStatus, result);
		if (result.hasErrors()) {
			//  modelMap.addAttribute("summonStatus", summonStatus);
			 SummonStatus summonSatus1 = summonRepo.findById(summonStatus.getId()).get();
			 SummonType   findByTypeDetails = SummonTypeNewDetails.findById(summonSatus1.getSummonType().getId()).get();
			 summonStatus.setCaseDetails(summonSatus1.getCaseDetails());
			 commonObjectSummon(modelMap,summonSatus1.getCaseDetails().getId());
			    Long individualType = summonSatus1.getSummonType().getIndividualType().getIndividualId();
		 	 SummonType  summonType  = new  SummonType();
				if(individualType==1 ||individualType==2) {
				    
					summonType.setFdirName(findByTypeDetails.getName());
					summonType.setFdirReg_no(findByTypeDetails.getRegistration_no());
					summonType.setFdirAddr(findByTypeDetails.getAddress());
					summonType.setFdirEmail(findByTypeDetails.getEmail());
					summonType.setFdirMobile(findByTypeDetails.getMobileNo());
					summonType.setFdiroffJoinDate(findByTypeDetails.getOffJoinDate());
				    summonType.setIndividualId(findByTypeDetails.getIndividualType().getIndividualId());
				    summonType.setIndividualType(findByTypeDetails.getIndividualType());
				    summonSatus1.setSummonType(summonType);
				}
				
				if(individualType==3 || individualType==4) {
					
					summonType.setEmpName(findByTypeDetails.getName());
					summonType.setEmpAddress(findByTypeDetails.getAddress());
					summonType.setEmpdesgi(findByTypeDetails.getDesignation());
					summonType.setEmpDob(findByTypeDetails.getDob());
					summonType.setEmpemail(findByTypeDetails.getEmail());
					summonType.setEmpissuplace(findByTypeDetails.getPlaceof_issued());
					summonType.setEmpMobile(findByTypeDetails.getMobileNo());
					summonType.setEmpnatid(findByTypeDetails.getNationalId());
					summonType.setEmpNation(findByTypeDetails.getNationality());
					summonType.setEmpoffdate(findByTypeDetails.getOffJoinDate());
					summonType.setEmpPassDate(findByTypeDetails.getIssueDate());
					summonType.setEmpPassport(findByTypeDetails.getPassport());
					summonType.setEmpsex(findByTypeDetails.getSex());
				    summonType.setIndividualId(findByTypeDetails.getIndividualType().getIndividualId());
				    summonType.setIndividualType(findByTypeDetails.getIndividualType());
				    summonSatus1.setSummonType(summonType);
				}
				
				
				if(individualType == 5 || individualType == 6) {
					summonType.setAgentAddress(findByTypeDetails.getAddress());
					summonType.setAgentcompany(findByTypeDetails.getNameCompany());
					summonType.setAgentEmail(findByTypeDetails.getEmail());
					summonType.setAgentMobile(findByTypeDetails.getMobileNo());
					summonType.setAgentName(findByTypeDetails.getName());
					summonType.setAgentRegno(findByTypeDetails.getRegistration_no());
				    summonType.setIsACin(findByTypeDetails.getIsCin());
					summonType.setIndividualId(findByTypeDetails.getIndividualType().getIndividualId());
					 summonType.setIndividualType(findByTypeDetails.getIndividualType());
					 summonSatus1.setSummonType(summonType);
				}
				
				if(individualType== 7) {
					summonType.setOthAddress(findByTypeDetails.getAddress());
					summonType.setOthRelation(findByTypeDetails.getRelationwithcompany());
					summonType.setOthEmail(findByTypeDetails.getEmail());
					summonType.setOthMobile(findByTypeDetails.getMobileNo());
					summonType.setOthName(findByTypeDetails.getName());
					summonType.setIsOCin(findByTypeDetails.getIsCin());
					summonType.setOthRegno(findByTypeDetails.getRegistration_no());
					summonType.setIndividualType(findByTypeDetails.getIndividualType());
					summonType.setIndividualId(findByTypeDetails.getIndividualType().getIndividualId());
					summonSatus1.setSummonType(summonType);
				}
				summonStatus.setSummonType(summonType);
		 	modelMap.addAttribute("summonStatus", summonStatus);
		 	return "notice/physicalSummonNoticeDetails";
	    	 
		}
		String fileName = null;
			String fileExt = summonStatus.getSummonPhysicallyFile().getOriginalFilename();
			fileExt = fileExt.substring(fileExt.lastIndexOf("."));
			fileName = "summon_PS_" + summonStatus.getId() + fileExt;
			// caseDetails.setMcaOrderFile("OfflineSummon_"+ fileExt);
			caseFileUpload(summonStatus.getSummonPhysicallyFile(), fileName);
		
			summonStatusfile.setSignFile(fileName);
			summonStatusfile.setIs_physicallysent(true);
			summonStatusfile.setAprrovalStage1(false);
			summonStatusfile.setIsRejected(false);
		summonStatus = summonRepo.save(summonStatusfile);
		String loginUName = appUserDAO.findUserDetails(getUserDetails()).getFirstName() != null
				? appUserDAO.findUserDetails(getUserDetails()).getFirstName()
				: "" + appUserDAO.findUserDetails(getUserDetails()).getMiddleName() != null
						? appUserDAO.findUserDetails(getUserDetails()).getMiddleName()
						: "" + appUserDAO.findUserDetails(getUserDetails()).getLastName() != null
								? appUserDAO.findUserDetails(getUserDetails()).getLastName()
								: "";
	
		auditBeanBo.setAuditBean(Integer.parseInt(userDetailsService.getUserDetails().getUserId().toString()),
				loginUName, "User", Integer.parseInt(userDetailsService.getUserDetails().getUserId().toString()),
				utils.getMessage("log.PhySummonTemplate.save"), utils.getMessage("log.PhySummonTemplate.saved"), loginUName,
				"true");
		auditBeanBo.save();
		modelMap.addAttribute("message", "Physically Signed Summon  Uploaded successfully for summon no.: " + summonStatus.getSummonNo()+" and  has been farwarded to Addl.Director for verification");
		modelMap.addAttribute("summonId", summonStatus.getId());
		 return "notice/SucessPhySummonNotice";
	}
	
	
	@RequestMapping(value = "genPhysicalNoticesendAs")
	public String genPhysicalNoticesendAs(@ModelAttribute NoticeStatus NoticeStatus,ModelMap modelMap,BindingResult result) throws Exception {
	//model.addAttribute("SummonStatus", new SummonStatus());
		
		SummonNoticeValidation snv = new SummonNoticeValidation();
		snv.validatePhyfile(NoticeStatus, result);
		
		NoticeStatus noticeStatusfile= noticeRepo.findById(NoticeStatus.getId()).get();
		if (result.hasErrors()) {
			//  modelMap.addAttribute("summonStatus", summonStatus);
			NoticeStatus noticeStatus1 = noticeRepo.findById(NoticeStatus.getId()).get();
			 SummonType   findByTypeDetails = SummonTypeNewDetails.findById(noticeStatus1.getSummonType().getId()).get();
			 NoticeStatus.setCaseDetails(noticeStatus1.getCaseDetails());
			 commonObjectSummon(modelMap,noticeStatus1.getCaseDetails().getId());
			    Long individualType = noticeStatus1.getSummonType().getIndividualType().getIndividualId();
		 	 SummonType  summonType  = new  SummonType();
				if(individualType==1 ||individualType==2) {
				    
					summonType.setFdirName(findByTypeDetails.getName());
					summonType.setFdirReg_no(findByTypeDetails.getRegistration_no());
					summonType.setFdirAddr(findByTypeDetails.getAddress());
					summonType.setFdirEmail(findByTypeDetails.getEmail());
					summonType.setFdirMobile(findByTypeDetails.getMobileNo());
					summonType.setFdiroffJoinDate(findByTypeDetails.getOffJoinDate());
				    summonType.setIndividualId(findByTypeDetails.getIndividualType().getIndividualId());
				    summonType.setIndividualType(findByTypeDetails.getIndividualType());
				    noticeStatus1.setSummonType(summonType);
				}
				
				if(individualType==3 || individualType==4) {
					
					summonType.setEmpName(findByTypeDetails.getName());
					summonType.setEmpAddress(findByTypeDetails.getAddress());
					summonType.setEmpdesgi(findByTypeDetails.getDesignation());
					summonType.setEmpDob(findByTypeDetails.getDob());
					summonType.setEmpemail(findByTypeDetails.getEmail());
					summonType.setEmpissuplace(findByTypeDetails.getPlaceof_issued());
					summonType.setEmpMobile(findByTypeDetails.getMobileNo());
					summonType.setEmpnatid(findByTypeDetails.getNationalId());
					summonType.setEmpNation(findByTypeDetails.getNationality());
					summonType.setEmpoffdate(findByTypeDetails.getOffJoinDate());
					summonType.setEmpPassDate(findByTypeDetails.getIssueDate());
					summonType.setEmpPassport(findByTypeDetails.getPassport());
					summonType.setEmpsex(findByTypeDetails.getSex());
				    summonType.setIndividualId(findByTypeDetails.getIndividualType().getIndividualId());
				    summonType.setIndividualType(findByTypeDetails.getIndividualType());
				    noticeStatus1.setSummonType(summonType);
				}
				
				
				if(individualType == 5 || individualType == 6) {
					summonType.setAgentAddress(findByTypeDetails.getAddress());
					summonType.setAgentcompany(findByTypeDetails.getNameCompany());
					summonType.setAgentEmail(findByTypeDetails.getEmail());
					summonType.setAgentMobile(findByTypeDetails.getMobileNo());
					summonType.setAgentName(findByTypeDetails.getName());
					summonType.setAgentRegno(findByTypeDetails.getRegistration_no());
				    summonType.setIsACin(findByTypeDetails.getIsCin());
					summonType.setIndividualId(findByTypeDetails.getIndividualType().getIndividualId());
					 summonType.setIndividualType(findByTypeDetails.getIndividualType());
					 noticeStatus1.setSummonType(summonType);
				}
				
				if(individualType== 7) {
					summonType.setOthAddress(findByTypeDetails.getAddress());
					summonType.setOthRelation(findByTypeDetails.getRelationwithcompany());
					summonType.setOthEmail(findByTypeDetails.getEmail());
					summonType.setOthMobile(findByTypeDetails.getMobileNo());
					summonType.setOthName(findByTypeDetails.getName());
					summonType.setIsOCin(findByTypeDetails.getIsCin());
					summonType.setOthRegno(findByTypeDetails.getRegistration_no());
					summonType.setIndividualType(findByTypeDetails.getIndividualType());
					summonType.setIndividualId(findByTypeDetails.getIndividualType().getIndividualId());
					noticeStatus1.setSummonType(summonType);
				}
				NoticeStatus.setSummonType(summonType);
		 	modelMap.addAttribute("NoticeStatus", NoticeStatus);
		 	modelMap.addAttribute("message", "Invalid File Name");
			
		 	return "notice/physicalNoticeDetails";
	    	 
		}
		String fileName = null;
			String fileExt = NoticeStatus.getNoticePhysicallyFile().getOriginalFilename();
			fileExt = fileExt.substring(fileExt.lastIndexOf("."));
			fileName = "notice_PS_" + NoticeStatus.getId() + fileExt;
			// caseDetails.setMcaOrderFile("OfflineSummon_"+ fileExt);
			caseFileUpload(NoticeStatus.getNoticePhysicallyFile(), fileName);
		
			noticeStatusfile.setSignFile(fileName);
			noticeStatusfile.setIs_physicallysent(true);
			noticeStatusfile.setAprrovalStage1(false);
			noticeStatusfile.setIsRejected(false);
			NoticeStatus = noticeRepo.save(noticeStatusfile);
			String loginUName = appUserDAO.findUserDetails(getUserDetails()).getFirstName() != null
			? appUserDAO.findUserDetails(getUserDetails()).getFirstName()
			: "" + appUserDAO.findUserDetails(getUserDetails()).getMiddleName() != null
					? appUserDAO.findUserDetails(getUserDetails()).getMiddleName()
					: "" + appUserDAO.findUserDetails(getUserDetails()).getLastName() != null
							? appUserDAO.findUserDetails(getUserDetails()).getLastName()
							: "";

	auditBeanBo.setAuditBean(Integer.parseInt(userDetailsService.getUserDetails().getUserId().toString()),
			loginUName, "User", Integer.parseInt(userDetailsService.getUserDetails().getUserId().toString()),
			utils.getMessage("log.PhyNoticeTemplate.save"), utils.getMessage("log.PhyNoticeTemplate.saved"), loginUName,
			"true");
	auditBeanBo.save();
		modelMap.addAttribute("message", "Physically Signed Notice  Uploaded successfully for Notice no.: " + NoticeStatus.getSummonNo()+" and has been farwarded to Addl Director for verification");
		modelMap.addAttribute("summonId", NoticeStatus.getId());
		 return "notice/SucessPhySummonNotice";
	}
	
	@RequestMapping(value = "ApprocvedPhySummon", params="approvedSummon")
	public String ApprocvedPhySummon(@ModelAttribute SummonStatus summonStatus,ModelMap modelMap) throws Exception {
	//model.addAttribute("SummonStatus", new SummonStatus());
		SummonStatus summonStatusfile= summonRepo.findById(summonStatus.getId()).get();
		  UserDetails userdetail = userdetailRepo.findById(getUserDetails().getUserId()).get();
			summonStatusfile.setIs_physicallysent(true);
			summonStatusfile.setAprrovalStage1(true);
			summonStatusfile.setIsSigned(1);
		    summonStatusfile.setOrderSignedDate(new Date());	
			summonStatusfile.setVerify_id(userdetail.getId());
			summonStatusfile.setIsverified(true);
			summonStatusfile.setIsRejected(false);
		summonStatus = summonRepo.save(summonStatusfile);
		String loginUName = appUserDAO.findUserDetails(getUserDetails()).getFirstName() != null
				? appUserDAO.findUserDetails(getUserDetails()).getFirstName()
				: "" + appUserDAO.findUserDetails(getUserDetails()).getMiddleName() != null
						? appUserDAO.findUserDetails(getUserDetails()).getMiddleName()
						: "" + appUserDAO.findUserDetails(getUserDetails()).getLastName() != null
								? appUserDAO.findUserDetails(getUserDetails()).getLastName()
								: "";

		auditBeanBo.setAuditBean(Integer.parseInt(userDetailsService.getUserDetails().getUserId().toString()),
				loginUName, "User", Integer.parseInt(userDetailsService.getUserDetails().getUserId().toString()),
				utils.getMessage("log.PhyNoticeTemplate.save"), utils.getMessage("log.PhyNoticeTemplate.saved"), loginUName,
				"true");
		auditBeanBo.save();
		modelMap.addAttribute("message", "Physically Sent Summon Approved  successfully for summon no.: " + summonStatus.getSummonNo());
		modelMap.addAttribute("summonId", summonStatus.getId());
		 return "notice/SucessPhySummonNotice";
	}
	
	
	@RequestMapping(value = "ApprocvedPhyNotice",params="approvedNotice")
	public String ApprocvedPhyNotice(@ModelAttribute NoticeStatus noticeStatus,ModelMap modelMap) throws Exception {
	//model.addAttribute("SummonStatus", new SummonStatus());
		NoticeStatus noticeStatusfile= noticeRepo.findById(noticeStatus.getId()).get();
		  UserDetails userdetail = userdetailRepo.findById(getUserDetails().getUserId()).get();
		noticeStatusfile.setIs_physicallysent(true);
		noticeStatusfile.setAprrovalStage1(true);
		noticeStatusfile.setIsSigned(1);
		noticeStatusfile.setIsRejected(false);
		noticeStatusfile.setIsverified(true);
		noticeStatusfile.setOrderSignedDate(new Date());
		noticeStatusfile.setVerify_id(userdetail.getId());
		noticeStatus = noticeRepo.save(noticeStatusfile);
		modelMap.addAttribute("message", "Physically Sent  Notice Approved  successfully for Notice no.: " + noticeStatus.getSummonNo());
		modelMap.addAttribute("summonId", noticeStatus.getId());
		 return "notice/SucessPhySummonNotice";
	}
	
	@RequestMapping(value = "ApprocvedPhyNotice")
	public String RejectPhyNotice(@ModelAttribute NoticeStatus noticeStatus,ModelMap modelMap) throws Exception {
	//model.addAttribute("SummonStatus", new SummonStatus());
		NoticeStatus noticeStatusfile= noticeRepo.findById(noticeStatus.getId()).get();
		
		noticeStatusfile.setIs_physicallysent(true);
		noticeStatusfile.setIsRejected(true);
		noticeStatusfile.setRemark(noticeStatus.getRemark());
		noticeStatusfile.setIsSigned(0);
		noticeStatusfile.setVerify_id(getUserDetails().getUserId());
		noticeStatus = noticeRepo.save(noticeStatusfile);
		modelMap.addAttribute("message", "Physically send Notice is Send back to review  for Notice no.: " + noticeStatus.getSummonNo());
		modelMap.addAttribute("summonId", noticeStatus.getId());
		 return "notice/SucessPhySummonNotice";
	}
	
	@RequestMapping(value = "ApprocvedPhySummon")
	public String RejectPhySummon(@ModelAttribute SummonStatus summonStatus,ModelMap modelMap) throws Exception {
	//model.addAttribute("SummonStatus", new SummonStatus());
		SummonStatus summonStatusfile= summonRepo.findById(summonStatus.getId()).get();
		
			summonStatusfile.setIs_physicallysent(true);
			summonStatusfile.setIsRejected(true);
			summonStatusfile.setRemark(summonStatus.getRemark());
			summonStatusfile.setIsSigned(0);
			summonStatusfile.setVerify_id(getUserDetails().getUserId());
		summonStatus = summonRepo.save(summonStatusfile);
		modelMap.addAttribute("message", "Physically send Summon is Send back to review for summon no.: " + summonStatus.getSummonNo());
		modelMap.addAttribute("summonId", summonStatus.getId());
		 return "notice/SucessPhySummonNotice";
	}
	
	@RequestMapping(value = "showPendPhypersonStage1")
	public String showPendPhypersonStage1(@ModelAttribute SummonStatus summonStatus,ModelMap modelMap) throws Exception {
		
		List<SummonStatus> list=summonDao.findPhySummon(getUserDetails(),false,true);		
		modelMap.addAttribute("signOrders",list);		
		CaseDetails caseDetails = new CaseDetails();
		modelMap.addAttribute("caseDetails", caseDetails);
		return "ado/PhySummonPending";
	}
	
	
	

	public String getLoginUserName() {
		return SecurityContextHolder.getContext().getAuthentication().getName();
	}

	private AppUser getUserDetails() throws Exception {
		AppUser appUser = this.appUserDAO.findUserAccount(getLoginUserName());
		return appUser;
	}


	@RequestMapping(value = "showPendPhyNoticeStage1")
	public String showPendPhyNoticeStage1(@ModelAttribute NoticeStatus noticeStatus,ModelMap modelMap) throws Exception {
		
		//List<SummonStatus> list=summonDao.findPhySummon(getUserDetails(),false,true);
		  List<NoticeStatus> list = noticeDao.findPhyNotice(getUserDetails(),false,true);
		  modelMap.addAttribute("signOrders",list);		
		  CaseDetails caseDetails = new CaseDetails();
		  modelMap.addAttribute("caseDetails", caseDetails);
		return "ado/PhyNoticePending";
	}
	
	
	
	
	@RequestMapping(value = "showReviewPendPhyNoticeStage1")
	public String showReviewPendPhyNoticeStage1(@ModelAttribute NoticeStatus noticeStatus,ModelMap modelMap) throws Exception {
		
		//List<SummonStatus> list=summonDao.findPhySummon(getUserDetails(),false,true);
		  List<NoticeStatus> list = noticeDao.findPhyReviewNotice(getUserDetails(),false,true);
		  modelMap.addAttribute("signOrders",list);		
		  CaseDetails caseDetails = new CaseDetails();
		  modelMap.addAttribute("caseDetails", caseDetails);
		return "ado/PhyReviewNotice";
	}
	
	 
	@RequestMapping(value = "showReviewPhySummonStage1")
	public String showReviewPendPhypersonStage1(@ModelAttribute SummonStatus summonStatus,ModelMap modelMap) throws Exception {
		
		List<SummonStatus> list=summonDao.findPhyReviewSummon(getUserDetails(),false,true);		
		modelMap.addAttribute("signOrders",list);		
		CaseDetails caseDetails = new CaseDetails();
		modelMap.addAttribute("caseDetails", caseDetails);
		return "ado/PhyReviewSummon";
	}
	
	
	@RequestMapping(value="casesAsIoAddl")
	public String showListofReview(ModelMap modelMap) throws Exception {
		List<Object[]> penPhyNotice = userMangCustom.findPhyNoticeSendBackforApproval(1,userDetailsService.getUserDetails().getUserId(),true);
		List<Object[]> penPhySummon = userMangCustom.findPhySummonSendBackforApproval(1,userDetailsService.getUserDetails().getUserId(),true);
		modelMap.addAttribute("totlaPenPhySummon", penPhySummon.size());
		modelMap.addAttribute("totlaPenPhyNotice", penPhyNotice.size());
		return "addl/adlDashboard";
	}
	
	
	@RequestMapping(value = "/PhysicallySummonview", params = "SummonOrders")
	public String caseAndOffOrdDetail(@RequestParam(value = "SummonOrders", required = true) Long Id, Model model) {
		SNMSValidator snmsValid = new SNMSValidator();

		if (!snmsValid.getValidInteger(Id)) {
			model.addAttribute("message", "Invalid caseId ");
			return "caseDetails/ErrorPage";
		}else {
			SummonStatus st = summonRepo.findById(Id).get();
			model.addAttribute("summonStatus", st);
			model.addAttribute("ooFile", "downloadFiles?fileName=" + st.getSignFile()+"#toolbar=0&navpanes=0&scrollbar=0");
			return "notice/viewPhySummons";
		}
		
	}

	@RequestMapping(value = "/PhysicallyNoticeview", params = "NoticeOrders")
	public String PhysicallyNoticeview(@RequestParam(value = "NoticeOrders", required = true) Long Id, Model model) {
		SNMSValidator snmsValid = new SNMSValidator();

		if (!snmsValid.getValidInteger(Id)) {
			model.addAttribute("message", "Invalid caseId ");
			return "caseDetails/ErrorPage";
		}else {
			NoticeStatus Ns = noticeRepo.findById(Id).get();
			model.addAttribute("noticeStatus", Ns);
			model.addAttribute("ooFile", "downloadFiles?fileName=" + Ns.getSignFile()+"#toolbar=0&navpanes=0&scrollbar=0");
			return "notice/viewPhyNotice";
		}
		
	}
	
	@RequestMapping(value = "/caseoPhysicalFileUpload", method = RequestMethod.POST)
	public void caseFileUpload(@RequestParam("file") MultipartFile file, String name) {
		BufferedOutputStream stream = null ;
		try {

		//	name =  "abc";
			 File parent = new File(filePath).getParentFile().getCanonicalFile();
			 String directory = ESAPI.validator().getValidDirectoryPath("DirectoryName",filePath,parent,false);
			 
			 
			 Boolean validFileName = ESAPI.validator().isValidFileName("FileName", name, false);
			 String filepath = null;
		      if(validFileName == true) {
			 filepath = Paths.get(directory+ File.separator +name).toString();
		     }
			// Save the file locally
			 stream = new BufferedOutputStream(new FileOutputStream(new File(filepath)));
			stream.write(file.getBytes());
			stream.close();
		} catch (Exception e) {
			logger.info(e.getMessage());
		}
		finally {
			if (stream != null) {
			safeClose(stream);
			}
		}
//		  return new ResponseEntity<>(HttpStatus.OK);
	} //
	private void safeClose(BufferedOutputStream stream) {
		if (stream != null) {
			try {
				stream.close();
			} catch (IOException e) {
				logger.info(e.getMessage());
			}
		}
		
	}
	public void commonObjectSummon(ModelMap modelMap,Long caseId){
		
		SNMSValidator snmsvalid = new SNMSValidator();
		if(!snmsvalid.getValidInteger(caseId)) {
			String valid = "invalid case id";
		
		}
		
		
		Optional<CaseDetails> details = caseDetailsRepository.findById(caseId);
		CaseDetails caseDetails = details.get();
		
		
		
		modelMap.addAttribute("caseDetails", caseDetails);

		List<Object[]> inspectorList = userMangCustom.findByCase(caseDetails.getId());
		int i = 1;
		List<InspectorDTO> inspList = new ArrayList<InspectorDTO>();
		for (Object[] dto : inspectorList) {
			InspectorDTO ins = new InspectorDTO(i,
					dto[0].toString() + " " + dto[1].toString() + " (" + dto[2].toString() + ")", (Boolean) dto[3],(Boolean) dto[4]);
			inspList.add(ins);
			i++;
		}
//		inspList.removeIf( insp -> insp.isAdo()==true ); // Check not to add Additional director
		modelMap.addAttribute("inspList", inspList);
		List<Company> comList = userMangCustom.findByCaseId(new CaseDetails(caseDetails.getId()));
		modelMap.addAttribute("comList", comList);
		
		
		
	}
	public InspectorListDTO getInspectorListAndCompany(Long caseId) {

		List<Object[]> inspectorList = userMangCustom.findByCase(caseId);
		String ioName = "";
		int i = 1;
		List<InspectorDTO> inspList = new ArrayList<InspectorDTO>();
		List<InspectorDTO> copyToList = new ArrayList<InspectorDTO>();
		for (Object[] dto : inspectorList) {
			InspectorDTO ins = new InspectorDTO(i,
					dto[0].toString() + " " + dto[1].toString() + " (" + dto[2].toString() + ")", (Boolean) dto[3],(Boolean) dto[4]);
			inspList.add(ins);
			copyToList.add(ins);
			if ((Boolean) dto[3])
				ioName = dto[1].toString() + " (" + dto[2].toString() + ")";
			i++;
		}
		int length = copyToList.size();
		copyToList.add(new InspectorDTO(length + 1, "PPS to Director, SFIO", false,false));
		copyToList.add(new InspectorDTO(length + 2, "Guard File", false,false));

		List<Company> comList = userMangCustom.findByCaseId(new CaseDetails(caseId));
		List<Company> listCom = new ArrayList<Company>();
		Long k = 1L;
		for (Company com : comList) {
			Company c = new Company();
			c.setId(k);
			c.setName(com.getName());
			c.setSection(com.getSection());
			listCom.add(c);
			k++;
		}

		return new InspectorListDTO(inspList, copyToList, ioName, listCom);
	}
	
	@RequestMapping(value = "/getPhySummonList",method = RequestMethod.GET)
	public @ResponseBody List<SummonStatus> getPhySummonList(
			Model model) throws Exception {
		List<SummonStatus> summon = summonDao.findSummonByUserId(getUserDetails());
		return summon;
	}
	
	@RequestMapping(value="getUserDetailsById", method = RequestMethod.GET)
	public @ResponseBody String getUserDetailsById(@RequestParam("UserId") String UserId) {
		long Id=Long.parseLong(UserId);
		AppUser user = appUserRepo.findById(Id).get();
		String FullName =	appUserDAO.findUserDetails(user).getFirstName()!=null ? appUserDAO.findUserDetails(user).getFirstName():""
			
		+appUserDAO.findUserDetails(user).getMiddleName()!=null? appUserDAO.findUserDetails(user).getMiddleName():""
		+appUserDAO.findUserDetails(user).getLastName()!=null?appUserDAO.findUserDetails(user).getLastName():"";
		
		return FullName;
	}
	
}
