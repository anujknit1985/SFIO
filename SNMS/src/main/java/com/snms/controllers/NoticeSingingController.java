package com.snms.controllers;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.owasp.esapi.ESAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.snms.dao.AppRoleDAO;
import com.snms.dao.AppUserDAO;
import com.snms.dao.DSCDao;
import com.snms.dao.NoticeDao;
import com.snms.dao.SummonDao;
import com.snms.dto.ApproveSummonDto;
import com.snms.dto.InspectorDTO;
import com.snms.dto.InspectorListDTO;
import com.snms.dto.NoticeTempDto;
import com.snms.dto.SignedOfficeOrderDto;
import com.snms.dto.SummonReportDTO;
import com.snms.entity.AppUser;
import com.snms.entity.CaseDetails;
import com.snms.entity.Company;
import com.snms.entity.DSCRegistration;
import com.snms.entity.Inspector;
import com.snms.entity.NoticeDSCSigning;
import com.snms.entity.NoticeStatus;
import com.snms.entity.NoticeTemplate;
import com.snms.entity.SummonStatus;
import com.snms.entity.SummonType;
import com.snms.entity.UserDetails;
import com.snms.service.AddNoticeDscSigningRepository;
import com.snms.service.AppUserRepository;
import com.snms.service.CaseDetailsRepository;
import com.snms.service.InspectorRepository;
import com.snms.service.NoticeRepository;
import com.snms.service.NoticeTemplateRepository;

import com.snms.service.SummonTypeNew;
import com.snms.service.UserDetailsRepository;
import com.snms.service.UserDetailsServiceImpl;
import com.snms.service.UserManagementCustom;
import com.snms.utils.NoticePdf;
import com.snms.utils.SummonPDF;
import com.snms.utils.Utils;
import com.snms.validation.DscValidation;
import com.snms.validation.SNMSValidator;

import app.eoffice.dsc.constants.PDFSignatureConstants;
import app.eoffice.dsc.constants.SigningConstants;
import app.eoffice.dsc.model.CertificateDetails;
import app.eoffice.dsc.model.PDFSignatureProperties;
import app.eoffice.dsc.model.RevocationSupport;
import app.eoffice.dsc.model.SigningSupport;
import app.eoffice.dsc.service.DscService;
import app.eoffice.dsc.util.XmlUtil;
import app.eoffice.dsc.xml.response.DSCResponse;

@Controller
public class NoticeSingingController {
	private static final Logger logger = Logger.getLogger(NoticeSingingController.class);
	
	@Autowired
	private NoticeDao noticeDao;
	
	@Autowired
	private AppUserDAO appUserDAO;
	
	@Autowired
	private CaseDetailsRepository caseDetailsRepository;
	
	@Autowired
	private DSCDao dSCDao;
	
	@Autowired(required = true)
	private DscService dscService;
	
	@Autowired
	private NoticeRepository noticeRepository;
	
	@Autowired
	private NoticeTemplateRepository NoticeTempRepo;
	
	@Autowired
	private UserManagementCustom userMangCustom;
	
	@Autowired
	private InspectorRepository inspRepo;
	@Autowired
	private UserDetailsServiceImpl userDetailsService;
	@Autowired
	private UserDetailsRepository  userDetailsRepo;
	@Autowired
	private SummonDao summonDao;
	
	@Autowired 
	private AppUserRepository appUserRepo;
	
	@Autowired
	private UserDetailsRepository userdetailRepo;
	
	@Autowired
	private SummonTypeNew summonnewTypeDetails;
	
	@Autowired
	private AppRoleDAO appRoleDAO;
	
	@Autowired
	private AddNoticeDscSigningRepository addNoticeDscSigningRepository;
	
	@Value("${file.upload}")
	public String filePath;
	
	@RequestMapping("approvedNotice")
	public String getApproveSummonList(ModelMap modelMap) throws Exception {
		
		//List<Object[]> appObj=noticeDao.findNoticeApproveByUser(getUserDetails());
		List<NoticeStatus> appObj=noticeDao.findNoticeByApprove();
		/*
		 * List<ApproveSummonDto> copyToList = new ArrayList<ApproveSummonDto>(); for
		 * (Object[] dto : appObj) { ApproveSummonDto ins = new
		 * ApproveSummonDto(dto[0].toString(),dto[1].toString(),Long.parseLong(dto[3].
		 * toString()),dto[4].toString(),dto[5]); copyToList.add(ins); }
		 */
		modelMap.addAttribute("approvedNotices", appObj);
		return "notice/AllApprovedNotice";
	}
	
	@RequestMapping("showAllPhyNotice")
	public String showAllPhyNotice(ModelMap modelMap) throws Exception {
		List<NoticeStatus> copyToList = noticeDao.findNoticePhysicallysend();
		
	
		modelMap.addAttribute("approvedNotices",copyToList);
		return "notice/AllPhyNotice";
	}
	
	@RequestMapping("showAllPenPhyNotice")
	public String showAllPenPhyNotice(ModelMap modelMap) throws Exception {
		List<NoticeStatus> copyToList = noticeDao.findNoticePedingPhysicallysend();
		
	
		modelMap.addAttribute("approvedNotices",copyToList);
		return "notice/AllPenPhyNotice";
	}
	@RequestMapping("approvedNoticeUsr")
	public String approvedNoticeUsr(ModelMap modelMap) throws Exception {
		
		//List<Object[]> appObj=noticeDao.findNoticeApproveByUser(getUserDetails());
		//List<Object[]> appObj=noticeDao.findNoticeByUser(getUserDetails());
		/*
		List<Object[]> appObj=noticeDao.findNoticeByUser(getUserDetails());
		List<ApproveSummonDto> copyToList = new ArrayList<ApproveSummonDto>();
		for (Object[] dto : appObj) {
			ApproveSummonDto ins = new ApproveSummonDto(dto[0].toString(),dto[1].toString(),Long.parseLong(dto[3].toString()),dto[4].toString(),dto[5]);
			copyToList.add(ins);
		}*/
		List<NoticeStatus> copyToList = noticeDao.findNoticeByUserId(getUserDetails());
		modelMap.addAttribute("approvedNotices", copyToList);
		return "notice/AllApprovedNotice";
	}
	
	@RequestMapping("getApprovedNotice")
	public String getApprovedNotice(@RequestParam(name = "id", required = true) Long id, ModelMap modelMap)
			throws Exception {
		//gouthami 15/09/2020
				SNMSValidator snmsValid =  new SNMSValidator();
				
				if(!snmsValid.getValidInteger(id)) {
					return "redirect:/approvedNoticeUsr";
					
				}
		NoticeStatus noticeStatus=noticeRepository.findById(id).get();
		Optional<CaseDetails> caseD = caseDetailsRepository.findById(noticeStatus.getCaseDetails().getId());
		CaseDetails caseDetails = caseD.get();
		String OfficeAddress  =  caseDetails.getUnit().getAddress();
		NoticeTemplate noticeTemp=NoticeTempRepo.findByNotice(noticeStatus);
		InspectorListDTO inspList = getInspectorListAndCompany(caseDetails.getId());
		List<String> companyNames = new ArrayList<String>();
		List<Company> comList = inspList.getCompanyList();
		String companyName=summonDao.findCompanyNameById(noticeStatus.getSummonType().getId());
		for (Company com : comList) {
				companyNames.add(com.getName());
		}
		String ioName =inspList.getIoName(); 
		SummonType summonType=summonnewTypeDetails.findById(noticeStatus.getSummonType().getId()).get();
		String name= summonType.getName();
		String designation= "";
		 if(summonType.getDesignation() != null) {
			  designation = summonType.getDesignation();
			  }else {
				  designation = " ";
			  }
		String address= summonType.getAddress();
		String email= summonType.getEmail();
		String noticeCompany = "";
		  if(summonType.getIndividualType().getIndividualId() == 5 || summonType.getIndividualType().getIndividualId() ==6 ) {
			  noticeCompany  = summonType.getNameCompany();
			}
		  else   if(summonType.getIndividualType().getIndividualId() == 7 )
			{
				 noticeCompany="";
			}else {
				noticeCompany = companyName;
			}
		/*
		 * if(summonType.getType().equalsIgnoreCase("official")) {
		 * if(summonType.getIsdirector().equalsIgnoreCase("Y")) {
		 * designation="Director"; name=summonType.getDirName();
		 * address=summonType.getOffAddress(); email=summonType.getOffEmail(); } else{
		 * designation=summonType.getDesignation(); name=summonType.getName();
		 * address=summonType.getOffAddress(); email=summonType.getOffEmail(); } }
		 * 
		 * else if(summonType.getType().equalsIgnoreCase("vendor")) {
		 * designation="Other than CUI"; name=summonType.getVendorName();
		 * address=summonType.getVendorAddress(); email=summonType.getVendorEmail();
		 * }else if(summonType.getType().equalsIgnoreCase("secretary")) {
		 * designation="Company Secretary"; name=summonType.getMemberName();
		 * address=summonType.getMemberAddress(); email=summonType.getMemberEmail(); }
		 * else { designation="Auditor"; name=summonType.getAuditorName();
		 * address=summonType.getAddress(); email=summonType.getEmail(); }
		 */
		//company=summonType.	
		//String userDir = "D:\\eformnfra_app_data\\";
	//	String userDir = filePath;
			File parent = new File(filePath).getParentFile().getCanonicalFile();
			String userDir =  ESAPI.validator().getValidDirectoryPath("DirectoryName",filePath,parent,false);
		String signedFileName = "SNotice_" +id+ ".pdf";
		String unsignedFileName = "UNotice_" +id+".pdf";
		String unsigneddinFileName = "UNoticedin_" +id+".pdf";
		if(signedFileName == null || "".equals(signedFileName.trim()) || unsignedFileName == null || "".equals(unsignedFileName.trim()) || unsigneddinFileName == null || "".equals(unsigneddinFileName.trim())) {
			return "redirect:/approvedNoticeUsr";
		}else {
			
			getCurrentSession().setAttribute("signDoc", signedFileName);
			getCurrentSession().setAttribute("unSignDoc", unsignedFileName);
			getCurrentSession().setAttribute("unSignNoticePre", "viewpdffile/" + unsignedFileName + "#toolbar=0&navpanes=0&scrollbar=0");
			getCurrentSession().setAttribute("unSignNoticedinPre", "viewpdffile/" + unsigneddinFileName);
			
		
		}
	
		getCurrentSession().setAttribute("OfficeAddress  ", getSplitedString(OfficeAddress));
		

	
		String unSignPdfFullPath = "";
		String signPdfFullPath = "";
		String unSignPdfdinFullPath ="";
		File fileDir = new File(userDir);
		if (fileDir.exists()) {
			unSignPdfFullPath = fileDir + File.separator + unsignedFileName;
			unSignPdfdinFullPath = fileDir + File.separator + unsigneddinFileName;
			signPdfFullPath = fileDir + File.separator + signedFileName;
		} else {
			fileDir.mkdir();
			unSignPdfFullPath = fileDir + File.separator + unsignedFileName;
			unSignPdfdinFullPath = fileDir + File.separator + unsigneddinFileName;
			signPdfFullPath = fileDir + File.separator + signedFileName;
		}
		int otherSize=inspList.getCompanyList().size()-1;
		/*
		 * String companydisplay=""; if (inspList.getCompanyList().size()==1)
		 * companydisplay= inspList.getCompanyList().get(0).getName(); else
		 * companydisplay= inspList.getCompanyList().get(0).getName()+" and "
		 * +otherSize+" other companies.";
		 */
		
		String companydisplay="";
		if(otherSize==0)
			companydisplay=companyName;
		else
			companydisplay=companyName+" and "+otherSize+" other company";
		AppUser userDetails = userDetailsService.getUserDetails();
		Inspector inspectrole = inspRepo.findAllByCaseDetailsAndAppUser(caseDetails,userDetails);
		Inspector inspectList = new Inspector();
		if(appRoleDAO.getRoleName(getUserDetails().getUserId()).equals("ROLE_DIRECTOR")) {
			 inspectList = inspRepo.findAllByCaseDetailsAndAppUser(caseDetails,noticeStatus.getAppUser());  
			  UserDetails user = userDetailsRepo.findById(noticeStatus.getAppUser().getUserId()).get();
			 ioName = userDetailsService.getFullName(user);	
		}else {
		
	  if(inspectrole.getIsIO()) {
		  inspectList = inspRepo.findAllByCaseDetailsAndAppUser(caseDetails,userDetails);  
		  UserDetails user = userDetailsRepo.findById(userDetails.getUserId()).get();
		 ioName = userDetailsService.getFullName(user);
		  
	  }else {
     inspectList = inspRepo.findAllByCaseDetailsAndAppUser(caseDetails,noticeStatus.getAppUser());  
		  UserDetails user = userDetailsRepo.findById(noticeStatus.getAppUser().getUserId()).get();
		 ioName = userDetailsService.getFullName(user);
	  }
		}
		  String Designation;
		if(inspectList.getIsAdo() == true) {
		//	Designation = "Addl";
			Designation = "Addl.Director";
			
		}else if(inspectList.getIsIO()== true) {
			Designation = "Investigating Officer";
		}else {
			Designation = "Inspector";
		}	
		getCurrentSession().setAttribute("unSignPdfFile", unSignPdfFullPath);
		getCurrentSession().setAttribute("signPdfFile", signPdfFullPath);
		
		String fnametemp=	appUserDAO.findUserDetails( getUserDetails()).getFirstName()!=null ? appUserDAO.findUserDetails( getUserDetails()).getFirstName():"";
		String mnametemp=	appUserDAO.findUserDetails(getUserDetails()).getMiddleName()!=null? appUserDAO.findUserDetails(getUserDetails()).getMiddleName():"";
	    String lnametemp=	appUserDAO.findUserDetails(getUserDetails()).getLastName()!=null?appUserDAO.findUserDetails(getUserDetails()).getLastName():"";
	   //String username =	getUserDetails().getUserName();
	     String username="";
	 
	 
	 if(appRoleDAO.getRoleName(getUserDetails().getUserId()).equals("ROLE_DIRECTOR"))
		     username = ioName;
		else if(appRoleDAO.getRoleName(getUserDetails().getUserId()).equals("ROLE_USER"))
			 username = fnametemp  + " " +mnametemp  +" "+lnametemp;
		// String username =  appUserDAO.findUserDetails(getUserDetails()).getFirstName()+" "+appUserDAO.findUserDetails(getUserDetails()).getMiddleName()+" "+appUserDAO.findUserDetails(getUserDetails()).getLastName();
	//Gouhtami 02/09/2021 notice without  din	
	 
	 
	 NoticePdf.NoticePreview(id,unSignPdfFullPath,getUserDetails().getUserId());
	//	NoticePdf.noticePreview(new NoticeTempDto(noticeStatus.getNoticeDin(), new Utils().getCurrentDateFormat(caseDetails.getMcaOrderDate()), noticeTemp.getPara1(), noticeTemp.getPara2(), noticeTemp.getPara3(), noticeTemp.getPara4(), noticeTemp.getPara5(), ioName, companyNames, noticeCompany, address,designation,name,email,noticeStatus.getApprovalDate(),Designation), unSignPdfFullPath,username ,OfficeAddress ,companydisplay);
		 //Gouhtami 02/09/2021 notice with din
		//NoticePdf.noticePreviewdin(new NoticeTempDto(noticeStatus.getNoticeDin(), new Utils().getCurrentDateFormat(caseDetails.getMcaOrderDate()), noticeTemp.getPara1(), noticeTemp.getPara2(), noticeTemp.getPara3(), noticeTemp.getPara4(), noticeTemp.getPara5(), ioName, companyNames, noticeCompany, address,designation,name,email,noticeStatus.getApprovalDate(),Designation), unSignPdfdinFullPath,username ,OfficeAddress ,companydisplay);
	 NoticePdf.NoticePreviewdin(id,unSignPdfdinFullPath,getUserDetails().getUserId(),noticeStatus.getNoticeDin());
		modelMap.addAttribute("id", id);
		//Gouthami 22/10/2021 removiging to give privilege to all user of that case 
		//List<Object[]> ioadoList = userMangCustom.getIoAdoByCaseId(caseDetails.getId());
		/*
		 * Long ioId = 0L ; for (Object[] object : inspectorListnew) {
		 * if((Boolean)object[1]) ioId = Long.parseLong(object[0].toString()); }
		 * 
		 * boolean privilege = false; try { if(getUserDetails().getUserId()==ioId)
		 * privilege = true; } catch (Exception e) { logger.info(e.getMessage()); }
		 */
		List<Object[]> inspectorListnew = userMangCustom.getInspectorList(caseDetails.getId());		
		boolean privilege = false;		
		try {
			for (Object[] object : inspectorListnew)				
			{
				
				if(getUserDetails().getUserId() == Integer.parseInt(object[0].toString()))
					privilege = true;
			}
			
		} catch (Exception e) {
			logger.info(e.getMessage());
		}
		modelMap.addAttribute("privilege",privilege);
		if(appRoleDAO.getRoleName(getUserDetails().getUserId()).equals("ROLE_DIRECTOR"))
			modelMap.addAttribute("role","DIRECTOR");
		else if(appRoleDAO.getRoleName(getUserDetails().getUserId()).equals("ROLE_USER"))
			modelMap.addAttribute("role","USER");
		
		return "DSCSigning/ViewNotice";
	}
	
	
	
	@RequestMapping("getNoticeView")
	public String getNoticeView(@RequestParam(name = "id", required = true) Long id, ModelMap modelMap)
			throws Exception {
		
		NoticeStatus noticeStatus=noticeRepository.findById(id).get();
		Optional<CaseDetails> caseD = caseDetailsRepository.findById(noticeStatus.getCaseDetails().getId());
		CaseDetails caseDetails = caseD.get();
		String OfficeAddress  =  caseDetails.getUnit().getAddress();
		NoticeTemplate noticeTemp=NoticeTempRepo.findByNotice(noticeStatus);
		InspectorListDTO inspList = getInspectorListAndCompany(caseDetails.getId());
		
		List<String> companyNames = new ArrayList<String>();
		List<Company> comList = inspList.getCompanyList();
		String companyName=summonDao.findCompanyNameById(noticeStatus.getSummonType().getId());
		for (Company com : comList) {
				companyNames.add(com.getName());
		}
		String ioName =inspList.getIoName(); 
		
		SummonType summonType=summonnewTypeDetails.findById(noticeStatus.getSummonType().getId()).get();
		String name= summonType.getName();
		String designation= "";
		 if(summonType.getDesignation() != null) {
			  designation = summonType.getDesignation();
			  }else {
				  designation = " ";
			  }
		String address= summonType.getAddress();
		String email= summonType.getEmail();
		String noticeCompany = "";
		  if(summonType.getIndividualType().getIndividualId() == 5 || summonType.getIndividualType().getIndividualId() ==6 ) {
			  noticeCompany  = summonType.getNameCompany();
			}else {
				noticeCompany = companyName;
			}
		/*
		 * if(summonType.getType().equalsIgnoreCase("official")) {
		 * if(summonType.getIsdirector().equalsIgnoreCase("Y")) {
		 * designation="Director"; name=summonType.getDirName();
		 * address=summonType.getOffAddress(); email=summonType.getOffEmail(); } else{
		 * designation=summonType.getDesignation(); name=summonType.getName();
		 * address=summonType.getOffAddress(); email=summonType.getOffEmail(); } }
		 * 
		 * else if(summonType.getType().equalsIgnoreCase("vendor")) {
		 * designation="Other than CUI"; name=summonType.getVendorName();
		 * address=summonType.getVendorAddress(); email=summonType.getVendorEmail();
		 * }else if(summonType.getType().equalsIgnoreCase("secretary")) {
		 * designation="Company Secretary"; name=summonType.getMemberName();
		 * address=summonType.getMemberAddress(); email=summonType.getMemberEmail(); }
		 * else { designation="Auditor"; name=summonType.getAuditorName();
		 * address=summonType.getAddress(); email=summonType.getEmail(); }
		 */
		//company=summonType.	
		//String userDir = "D:\\eformnfra_app_data\\";
		//String userDir = filePath;
			File parent = new File(filePath).getParentFile().getCanonicalFile();
			String userDir =  ESAPI.validator().getValidDirectoryPath("DirectoryName",filePath,parent,false);
		String signedFileName = "SNotice_" +id+ ".pdf";
		String unsignedFileName = "UNotice_" +id+".pdf";
		
		getCurrentSession().setAttribute("signDoc", signedFileName);
		getCurrentSession().setAttribute("OfficeAddress  ", getSplitedString(OfficeAddress));
		int otherSize=inspList.getCompanyList().size()-1;
    
		String company = summonDao.findCompanyNameById(summonType.getId());		
		String comapnyPara="";
		if(otherSize==0)
			comapnyPara=company;
		else
			comapnyPara=company+" and "+otherSize+" other companies";
		 String Designation;
		 
		AppUser userDetails = userDetailsService.getUserDetails();
		
		UserDetails userinfo =  userDetailsRepo.findById(userDetails.getUserId()).get();
		
		
		Inspector inspectList = inspRepo.findAllByCaseDetailsAndAppUser(caseDetails,noticeStatus.getAppUser());  
		  UserDetails user = userDetailsRepo.findById(noticeStatus.getAppUser().getUserId()).get();
		 ioName = userDetailsService.getFullName(user);
			
		  
		  
		if(inspectList.getIsAdo() == true) {
			//Designation = "Addl";
			Designation = "Addl.Director";
		}else if(inspectList.getIsIO()== true) {
			Designation = "Investigating Officer";
		}else {
			Designation = "Inspector";
		}
		/*
		String companydisplay="";
		if (inspList.getCompanyList().size()==1)
			companydisplay= inspList.getCompanyList().get(0).getName();
		else 
			companydisplay= inspList.getCompanyList().get(0).getName()+" and "+otherSize+" other companies.";
			*/
		getCurrentSession().setAttribute("unSignDoc", unsignedFileName);
		getCurrentSession().setAttribute("unSignNoticePre", "viewpdffile/" + unsignedFileName);
		String unSignPdfFullPath = "";
		String signPdfFullPath = "";
		File fileDir = new File(userDir);
		if (fileDir.exists()) {
			unSignPdfFullPath = fileDir + File.separator + unsignedFileName;
			signPdfFullPath = fileDir + File.separator + signedFileName;
		} else {
			fileDir.mkdir();
			unSignPdfFullPath = fileDir + File.separator + unsignedFileName;
			signPdfFullPath = fileDir + File.separator + signedFileName;
		}
		getCurrentSession().setAttribute("unSignPdfFile", unSignPdfFullPath);
		getCurrentSession().setAttribute("signPdfFile", signPdfFullPath);
		 String username =  appUserDAO.findUserDetails(getUserDetails()).getFirstName()+" "+appUserDAO.findUserDetails(getUserDetails()).getMiddleName()+" "+appUserDAO.findUserDetails(getUserDetails()).getLastName();
		 NoticePdf.NoticePreview(id,unSignPdfFullPath,getUserDetails().getUserId());

		//NoticePdf.noticePreview(new NoticeTempDto(noticeStatus.getNoticeDin(), new Utils().getCurrentDateFormat(caseDetails.getMcaOrderDate()), noticeTemp.getPara1(), noticeTemp.getPara2(), noticeTemp.getPara3(), noticeTemp.getPara4(), noticeTemp.getPara5(), ioName, companyNames, noticeCompany, address,designation,name,email,noticeStatus.getApprovalDate() ,Designation), unSignPdfFullPath,username ,OfficeAddress,comapnyPara);
		modelMap.addAttribute("id", id);
		
		List<Object[]> ioadoList = userMangCustom.getIoAdoByCaseId(caseDetails.getId());
		Long ioId = 0L ;
		for (Object[] object : ioadoList) {
			if((Boolean)object[1])
				ioId = Long.parseLong(object[0].toString());
		}
		boolean privilege = false;
		try {
			if(getUserDetails().getUserId()==ioId)
				privilege = true;
		} catch (Exception e) {
			logger.info(e.getMessage());
		}
		
		modelMap.addAttribute("privilege",privilege);
		if(appRoleDAO.getRoleName(getUserDetails().getUserId()).equals("ROLE_DIRECTOR"))
			modelMap.addAttribute("role","DIRECTOR");
		else if(appRoleDAO.getRoleName(getUserDetails().getUserId()).equals("ROLE_USER"))
			modelMap.addAttribute("role","USER");
		
		return "notice/ViewNoticeDetails";
	}
	@RequestMapping(value = "/esignNotice", method = { RequestMethod.POST, RequestMethod.GET })
	public String eSigned(@RequestParam(name="id",required=true) Long id,ModelMap modelMap, HttpServletRequest req) throws Exception 
	{

		DscValidation dscVal = new DscValidation();
		AppUser user=getUserDetails();
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		DSCRegistration dscDetails = dSCDao.findUserId(user.getUserId());

		//gouthami 15/09/2020
		SNMSValidator snmsValid =  new SNMSValidator();
		if(!snmsValid.getValidInteger(id)) {
			return "redirect:/getSignedNotices";
		}
		if(dscDetails == null)
		{
			modelMap.addAttribute("id", id);
			modelMap.addAttribute("regDscmsg", "Please Register Digital Signature Certificate");
			//modelMap.addAttribute("User", getUserDetails());
			return "DSCSigning/ViewNotice";
		}
		String serverDate = sdf1.format(new Date());
		if (dscVal.checkDscValiditySigning(dscDetails.getValidity(), serverDate)) {
			modelMap.addAttribute("id", id);
			modelMap.addAttribute("isValidDsc", "This DSC token has been expired.");
			//modelMap.addAttribute("User", getUserDetails());
			return "DSCSigning/ViewNotice";
		}
		String primaryCrlUrl = "http://10.246.57.100/CRLWebService";
		String secondaryCrlUrl = "";

		String revocationRequestXml = dscDetails.getRevocation();
		String serialNumber = dscDetails.getSerialNo();
		String aliasName = dscDetails.getAliasName();

		String aliasNameSpl[] = aliasName.split(" ");
		String dscOwnerName = aliasNameSpl[0];
		for (int i = 1; i < aliasNameSpl.length; i++) {
			dscOwnerName += " \n" + aliasNameSpl[i];
			// dscOwnerName += " " + aliasNameSpl[i] +"\n";
		}

		String unPDFFile = (String) getCurrentSession().getAttribute("unSignPdfFile");
		String SignFile = (String) getCurrentSession().getAttribute("signPdfFile");

		PDFSignatureProperties pdfSignatureProperties = new PDFSignatureProperties(unPDFFile, SignFile,null, 0, dscOwnerName, null, true, PDFSignatureConstants.RIGHT_ALIGN);

		Map<String, PDFSignatureProperties> pdfInputProperties = new HashMap<String, PDFSignatureProperties>();
		pdfInputProperties.put("1", pdfSignatureProperties);

		Map<String, SigningSupport> generatePdfHash = dscService.generatePdfHash(pdfInputProperties);

		getCurrentSession().setAttribute("pdfHash", generatePdfHash);

		RevocationSupport revocationSupport = new RevocationSupport(primaryCrlUrl, secondaryCrlUrl,
				revocationRequestXml);

		String signingRevocationXml = dscService.generateRequestXml(generatePdfHash, serialNumber, serverDate,
				revocationSupport, SigningConstants.PURPOSE_SIGNING);

		modelMap.addAttribute("signingRevocationXml", signingRevocationXml);
		getCurrentSession().setAttribute("nid", id);
		getCurrentSession().removeAttribute("unSignNoticePre");
		return "dsc/PopUpDscPasswordSigned_Notice";
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/savenoticePdf", method = RequestMethod.POST)
	public String saveSignSummon(ModelMap modelMap, @RequestParam String responseXMLSigning) throws Exception 
	{
		Long id=(Long) getCurrentSession().getAttribute("nid");
		NoticeStatus noticeStatus=noticeRepository.findById(id).get();
		String responseXml = responseXMLSigning;
		Map<String, SigningSupport> responseMap = (Map<String, SigningSupport>) getCurrentSession()
				.getAttribute("pdfHash");
		dscService.putSignature(responseXml, responseMap);
		
		AppUser user=getUserDetails();
		String certContent = "";
		try
		{
			DSCResponse dscResponse = (DSCResponse) XmlUtil.unmarshalXmlToObject(DSCResponse.class, responseXml);
			if(dscResponse.getStatus().equalsIgnoreCase("1"))
			{
			dscResponse.getIP();
			dscResponse.getMAC();
			dscResponse.getMsg();
			dscResponse.getStatus();
			for (int i = 0; i < dscResponse.getChainCerts().getChainCertsList().size(); i++) {
					if (dscResponse.getChainCerts().getChainCertsList().get(i).getCertLevel().equals("0")) {
						certContent = dscResponse.getChainCerts().getChainCertsList().get(i).getCertContent();
					}
			}

			// decode CertContent into base64
			Base64.Decoder decoder = Base64.getDecoder();
			byte[] certContentByte = decoder.decode(certContent);

			// getCertificate details
			CertificateDetails certificateDetails = dscService.retriveCertDetails(certContentByte);
		
			// getCurrentSession of signDoc and unsignDoc Path
			String signPdfPath = (String) getCurrentSession().getAttribute("signDoc");
			String unSignPdfPath = (String) getCurrentSession().getAttribute("unSignDoc");
			
			noticeStatus.setUnsignFile(unSignPdfPath);
			noticeStatus.setSignFile(signPdfPath);
			noticeStatus.setOrderSignedDate(new Date());
			noticeStatus.setIsSigned(1);
			
			NoticeDSCSigning dscSigningDetails = new NoticeDSCSigning(certificateDetails.getSerialNumber(),
					certificateDetails.getIssuedTo(), certificateDetails.getIssuedBy(), certificateDetails.getValidUpto(),
					dscResponse.getStatus(), dscResponse.getMAC(), dscResponse.getIP(), certificateDetails.getAliasName(),
					certificateDetails.getCdpPoint(), responseXml, user.getUserId(), noticeStatus.getId());
			
			addNoticeDscSigningRepository.save(dscSigningDetails);
			noticeRepository.save(noticeStatus);
			
			}
			getCurrentSession().removeAttribute("signDoc");
			getCurrentSession().removeAttribute("unSignDoc");
			getCurrentSession().removeAttribute("pdfHash");
			getCurrentSession().removeAttribute("unSignPdfFile");
			getCurrentSession().removeAttribute("signPdfFile");
			getCurrentSession().removeAttribute("nid");
			//System.out.println(responseXml);
			return "redirect:/getSignedNotices";
		}
		catch(Exception e)
		{
			logger.info(e.getMessage());
		}
		return null;
	}
	public HttpSession getCurrentSession() {
		ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
		return attr.getRequest().getSession();
	}

	public String getLoginUserName() {
		return SecurityContextHolder.getContext().getAuthentication().getName();
	}

	private AppUser getUserDetails() throws Exception {
		AppUser appUser = this.appUserDAO.findUserAccount(getLoginUserName());
		return appUser;
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
				ioName = dto[0].toString() + " " + dto[1].toString() + " (" + dto[2].toString() + ")";
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
			listCom.add(c);
			k++;
		}
		return new InspectorListDTO(inspList, copyToList, ioName, listCom);
	}
	
	@RequestMapping("allSignedNotices")
	public String allsignedOrederList(ModelMap modelMap) throws NumberFormatException, ParseException, Exception{
		/*
		 * List<Object[]> signedOreders=noticeDao.findAllSignedNotices();
		 * List<SignedOfficeOrderDto> signedOrderList=new
		 * ArrayList<SignedOfficeOrderDto>(); for(Object[] obj:signedOreders)
		 * signedOrderList.add(new SignedOfficeOrderDto(obj[0].toString(),
		 * obj[1].toString(),
		 * Long.parseLong(obj[2].toString()),obj[3].toString(),obj[4].toString()));
		 * modelMap.addAttribute("signNotices", signedOrderList); return
		 * "DSCSigning/SignedNotice";
		 */
		List<NoticeStatus> list=summonDao.findNotice_signed(getUserDetails());		
		modelMap.addAttribute("signOrders",list);		
		return "DSCSigning/SignedNotice";
	}
	
	@RequestMapping("getSignedNotices")
	public String signedOrederList(ModelMap modelMap, SummonReportDTO summonStatusmodel) throws NumberFormatException, ParseException, Exception{
		/*
		 * List<Object[]> signedOreders=noticeDao.findSignedNotices(getUserDetails());
		 * List<SignedOfficeOrderDto> signedOrderList=new
		 * ArrayList<SignedOfficeOrderDto>(); for(Object[] obj:signedOreders)
		 * signedOrderList.add(new SignedOfficeOrderDto(obj[0].toString(),
		 * obj[1].toString(),
		 * Long.parseLong(obj[2].toString()),obj[3].toString(),obj[4].toString()));
		 * modelMap.addAttribute("signNotices", signedOrderList);
		 * 
		 * 
		 */
		String isphysically_signed= summonStatusmodel.getIsPhysically_signed();
		
		 SummonReportDTO noticeStatus= new SummonReportDTO();
		 if (isphysically_signed != null)
		 noticeStatus.setIsPhysically_signed(isphysically_signed);
		
		NoticeStatus ns = new NoticeStatus();
       List<NoticeStatus> list=summonDao.findNotice_signed(getUserDetails());
       List<NoticeStatus> signedList = new ArrayList<NoticeStatus>();
		int count =1;
		for (NoticeStatus noticelist :list)
		{
			
			UserDetails userdetail = userDetailsRepo.findAllByUserId(noticelist.getAppUser());
			String issuedBY =  userDetailsService.getFullName(userdetail);
			if (isphysically_signed == null)
			{
			
			signedList.add(new NoticeStatus(noticelist.getId(),noticelist.getCaseDetails(),noticelist.getSummonType(),noticelist.getAprrovalStage1(),noticelist.getAprrovalStage2(),
					issuedBY,noticelist.getCreatedDate(),noticelist.getOrderSignedDate(),noticelist.getSummonNo(),noticelist.getDateOfAppear(),noticelist.getIsSensitive(),
					noticelist.getUnsignFile(),noticelist.getSignFile(), noticelist.getIsSigned(),noticelist.getNoticeDin(),noticelist.getApprovalDate(),
					noticelist.getIsDSC(),noticelist.getIsRejected(),noticelist.getIs_physicallysent(),
					noticelist.getRemark(),noticelist.getVerify_id(),noticelist.getApproval_Id()));
		count++;
			}
			else if (isphysically_signed.equals("true"))
			{
			if (noticelist.getIs_physicallysent()==true) 
			{
				signedList.add(new NoticeStatus(noticelist.getId(),noticelist.getCaseDetails(),noticelist.getSummonType(),noticelist.getAprrovalStage1(),noticelist.getAprrovalStage2(),
						issuedBY,noticelist.getCreatedDate(),noticelist.getOrderSignedDate(),noticelist.getSummonNo(),noticelist.getDateOfAppear(),noticelist.getIsSensitive(),
						noticelist.getUnsignFile(),noticelist.getSignFile(), noticelist.getIsSigned(),noticelist.getNoticeDin(),noticelist.getApprovalDate(),
						noticelist.getIsDSC(),noticelist.getIsRejected(),noticelist.getIs_physicallysent(),
						noticelist.getRemark(),noticelist.getVerify_id(),noticelist.getApproval_Id()));
			count++;
			}
			}
			else if (isphysically_signed.equals("false"))
			{
			if (noticelist.getIs_physicallysent()==false) 
			{
				signedList.add(new NoticeStatus(noticelist.getId(),noticelist.getCaseDetails(),noticelist.getSummonType(),noticelist.getAprrovalStage1(),noticelist.getAprrovalStage2(),
						issuedBY,noticelist.getCreatedDate(),noticelist.getOrderSignedDate(),noticelist.getSummonNo(),noticelist.getDateOfAppear(),noticelist.getIsSensitive(),
						noticelist.getUnsignFile(),noticelist.getSignFile(), noticelist.getIsSigned(),noticelist.getNoticeDin(),noticelist.getApprovalDate(),
						noticelist.getIsDSC(),noticelist.getIsRejected(),noticelist.getIs_physicallysent(),
						noticelist.getRemark(),noticelist.getVerify_id(),noticelist.getApproval_Id()));
				count++;
			}
			
			}
			
			
		
		}
       
       modelMap.addAttribute("signOrders",signedList);	
       modelMap.addAttribute("noticeStatus",noticeStatus);
		return "DSCSigning/SignedNotice";
	}
	
	@RequestMapping("/viewSNotice")
	public void viewFile(HttpServletRequest request, HttpServletResponse response,
			@RequestParam("id") Long id) throws Exception {
		NoticeStatus noticeStatus=noticeRepository.findById(id).get();
		String signFile=noticeStatus.getSignFile();
		//String dataDirectory = filePath;
		File parent = new File(filePath).getParentFile().getCanonicalFile();
		String dataDirectory = ESAPI.validator().getValidDirectoryPath("DirectoryName", filePath, parent, false);
	
		Path file = Paths.get(dataDirectory+ File.separator +signFile);
		if (Files.exists(file)) {
			if (signFile.endsWith("pdf")) {
				response.setContentType("application/pdf");
			}
			response.addHeader("Content-Disposition", "inline; filename=" + signFile);
			try {
				Files.copy(file, response.getOutputStream());
				response.getOutputStream().flush();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		
	}
	
	
	private String getSplitedString(String address) {
		String [] splitStr = address.split(",");
		StringBuilder consStr = new StringBuilder();
		for(int i = 0 ; i < splitStr.length ; i++){
			consStr.append(splitStr[i]).append(" ");
			if(i%5==0)
				consStr.append("\r\n");
		}
		return consStr.toString();
	}
}
