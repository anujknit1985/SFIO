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
import org.owasp.esapi.errors.IntrusionException;
import org.owasp.esapi.errors.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.snms.dao.AppUserDAO;
import com.snms.dao.DSCDao;
import com.snms.dao.OfficeOrderDao;
import com.snms.dto.ApprovedOrder;
import com.snms.dto.OfficeOrderDto;
import com.snms.dto.SignedOfficeOrderDto;
import com.snms.entity.AddDesignation;
import com.snms.entity.AppUser;
import com.snms.entity.CaseDetails;
import com.snms.entity.Company;
import com.snms.entity.DSCRegistration;
import com.snms.entity.DSCSigning;
import com.snms.entity.OfficeOrder;
import com.snms.entity.OfficeOrderTemplate;
import com.snms.entity.UserDetails;
import com.snms.service.AddDSCSigningRepository;
import com.snms.service.CaseDetailsRepository;
import com.snms.service.OfficeOrderRepository;
import com.snms.service.OfficeOrderTemplateRepository;
import com.snms.service.UserDetailsRepository;
import com.snms.service.UserDetailsServiceImpl;
import com.snms.service.UserManagementCustom;
import com.snms.utils.NoticePdf;
import com.snms.utils.OfficeOrerPDF;
import com.snms.utils.Utils;
import com.snms.validation.DscValidation;
import com.snms.validation.SNMSValidator;

import app.eoffice.dsc.constants.SigningConstants;
import app.eoffice.dsc.model.CertificateDetails;
import app.eoffice.dsc.model.PDFSignatureProperties;
import app.eoffice.dsc.model.RevocationSupport;
import app.eoffice.dsc.model.SigningSupport;
import app.eoffice.dsc.service.DscService;
import app.eoffice.dsc.util.XmlUtil;
import app.eoffice.dsc.xml.response.DSCResponse;

@Controller
public class OfficeOrderSigningController {
	private static final Logger logger = Logger.getLogger(OfficeOrderSigningController.class);
	
	@Autowired
	private OfficeOrderDao officeOrderDao;

	@Autowired
	private CaseDetailsRepository caseDetailsRepository;

	@Autowired
	private OfficeOrderRepository officeOrderRepository;

	@Autowired
	private OfficeOrderTemplateRepository officeOrderTempRepo;

	@Autowired
	private UserManagementCustom userMangCustom;

	@Autowired
	private DSCDao dSCDao;
	
	@Autowired
	private AppUserDAO appUserDAO;
	
	@Autowired
	private UserDetailsServiceImpl userDetailsService;

	@Autowired(required = true)
	private DscService dscService;
	
	@Autowired
	private AddDSCSigningRepository addDSCSigningRepository;
	
	@Autowired
	private UserDetailsRepository userDetailsRepo;
	
	@Value("${file.upload}")
	public String filePath;
	
	@RequestMapping("approvedOrders")
	public String getApproveList(ModelMap modelMap) {
		List<Object[]> order = officeOrderDao.findOfficeOrderByDirApprove();
		List<ApprovedOrder> approvedOrders = new ArrayList<ApprovedOrder>();

		for (Object obj[] : order)
			approvedOrders
					.add(new ApprovedOrder(obj[0].toString(), obj[1].toString(), Long.parseLong(obj[2].toString()),obj[3],obj[4].toString()));

		modelMap.addAttribute("approvedOrders", approvedOrders);
		return "director/AllApprovedOrder";
	}

	@RequestMapping("getApprovedOrder")
	public String getApproveList(@RequestParam(name = "id", required = true) Long id, ModelMap modelMap)
			throws MalformedURLException, IOException, IntrusionException, ValidationException {
		
		//gouthami 15/09/2020
				SNMSValidator snmsValid =  new SNMSValidator();
				
				if(!snmsValid.getValidInteger(id)) {
					return "redirect:/approvedOrders";
					
				};
		
		OfficeOrder officeorder = officeOrderRepository.findById(id).get();
		Optional<CaseDetails> caseD = caseDetailsRepository.findById(officeorder.getCaseDetails().getId());
		CaseDetails caseDetails = caseD.get();
		if("Legacy".equals(caseDetails.getRadioValue())||"Prosecution".equals(caseDetails.getRadioValue()))
		{
			getCurrentSession().setAttribute("unSignDocPre", "viewpdffile/" + caseDetails.getLegacyOrderFile());
			modelMap.addAttribute("id", caseDetails.getId());
			modelMap.addAttribute("isLegacy",true);
			return "DSCSigning/ViewOrder";
		}
		OfficeOrderTemplate officeOrderTemplate = officeOrderTempRepo.findByOfficeOrder(officeorder);
		String ioName = "";
		List<Object[]> inspectorList = userMangCustom.findByCase(officeorder.getCaseDetails().getId());

		List<String> inspector = new ArrayList<String>();

		for (Object[] dto : inspectorList) {
//			if(!(Boolean) dto[4]) // Check not to add Additional director
			inspector.add(dto[0].toString() + " " + dto[1].toString() + " (" + dto[2].toString() + ")");
			if ((Boolean) dto[3])
				ioName = dto[0].toString() + " " + dto[1].toString() + " (" + dto[2].toString() + ")";
		}

		List<String> company = new ArrayList<String>();

		List<Company> comList = userMangCustom.findByCaseId(new CaseDetails(officeorder.getCaseDetails().getId()));

		for (Company com : comList) {
			company.add(com.getName());
		}
		//String director = "Mr Amardeep Singh Bhatia";
		File parent = new File(filePath).getParentFile().getCanonicalFile();
		String userDir =  ESAPI.validator().getValidDirectoryPath("DirectoryName",filePath,parent,false);
     
		
		//String signedFileName = "SOfficeOrder" +caseDetails.getCaseId()+ ".pdf";
		//String unsignedFileName = "UOfficeOrder" +caseDetails.getCaseId()+".pdf";
		
		String signedFileName = "SOfficeOrder_" +id+ ".pdf";
		String unsignedFileName = "UOfficeOrder_" +id+".pdf";
		String unsigneddinFileName = "UOfficeOrderdin_" +id+".pdf";
		//gouthami 15/09/2020
		if(signedFileName!= null || ESAPI.validator().isValidFileName("FileName", signedFileName, false)) {
		getCurrentSession().setAttribute("signDoc", signedFileName);
		}
		if(unsignedFileName!= null || ESAPI.validator().isValidFileName("FileName", unsignedFileName, false)) {
		getCurrentSession().setAttribute("unSignDoc", unsignedFileName);
		getCurrentSession().setAttribute("unSignDocPre", "viewpdffile/" + unsignedFileName + "#toolbar=0&navpanes=0&scrollbar=0");
		
		getCurrentSession().setAttribute("unSignDocdinPre", "viewpdffile/" + unsigneddinFileName);
		}
		
		String unSignPdfFullPath = "";
		String signPdfFullPath = "";
		String unSigndinPdfFullPath="";
		File fileDir = new File(userDir);
		if (fileDir.exists()) {
			unSignPdfFullPath = fileDir + File.separator + unsignedFileName;
			unSigndinPdfFullPath = fileDir + File.separator + unsigneddinFileName;
			signPdfFullPath = fileDir + File.separator + signedFileName;
		} else {
			fileDir.mkdir();
			unSignPdfFullPath = fileDir + File.separator + unsignedFileName;
			signPdfFullPath = fileDir + File.separator + signedFileName;
			unSigndinPdfFullPath = fileDir + File.separator + unsigneddinFileName;
			
		}
		
		getCurrentSession().setAttribute("unSignPdfFile", unSignPdfFullPath);
		getCurrentSession().setAttribute("signPdfFile", signPdfFullPath);
		OfficeOrerPDF.officeOrderFinal(
				new OfficeOrderDto(Utils.getCurrentDateWithMonth(),officeorder.getOoDin() , inspector, ioName, company,
						getDirectorName(), officeOrderTemplate.getPara1(), officeOrderTemplate.getPara2(),
						officeOrderTemplate.getPara3(), officeOrderTemplate.getPara4(), officeOrderTemplate.getPara5(),officeorder.getApprovalDate()),
				unSigndinPdfFullPath);
		OfficeOrerPDF.officeOrderWitoutDinFinal(
				new OfficeOrderDto(Utils.getCurrentDateWithMonth(),officeorder.getOoDin() , inspector, ioName, company,
						getDirectorName(), officeOrderTemplate.getPara1(), officeOrderTemplate.getPara2(),
						officeOrderTemplate.getPara3(), officeOrderTemplate.getPara4(), officeOrderTemplate.getPara5(),officeorder.getApprovalDate()),
				unSignPdfFullPath);
		modelMap.addAttribute("isLegacy",false);
		modelMap.addAttribute("id", id);
		return "DSCSigning/ViewOrder";
	}

	
	
	
	@RequestMapping(value = "/esignOrder", method = { RequestMethod.POST, RequestMethod.GET })
	public String eSigned(@RequestParam(name="id",required=true) Long id,ModelMap modelMap, HttpServletRequest req) throws Exception {
		//gouthami 15/09/2020
				SNMSValidator snmsValid =  new SNMSValidator();
				if(!snmsValid.getValidInteger(id)) {
					
					modelMap.addAttribute("id", !snmsValid.getValidInteger(id));
					return "DSCSigning/ViewOrder";
				};
		
		DscValidation dscVal = new DscValidation();
		AppUser user=getUserDetails();
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		DSCRegistration dscDetails = dSCDao.findUserId(user.getUserId());
		String serverDate = sdf1.format(new Date());
		if(dscDetails == null)
		{
			modelMap.addAttribute("id", !snmsValid.getValidInteger(id));
			modelMap.addAttribute("regDscmsg", "Please Register Digital Signature Certificate");
			return "DSCSigning/ViewOrder";
		}
		if (dscVal.checkDscValiditySigning(dscDetails.getValidity(), serverDate)) {
			modelMap.addAttribute("id", id);
			modelMap.addAttribute("isValidDsc", "This DSC token has been expired.");
			return "DSCSigning/ViewOrder";
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

		PDFSignatureProperties pdfSignatureProperties = new PDFSignatureProperties(unPDFFile, SignFile,
				"400,700,160,100", 0, dscOwnerName, null, true, null);

		Map<String, PDFSignatureProperties> pdfInputProperties = new HashMap<String, PDFSignatureProperties>();
		pdfInputProperties.put("1", pdfSignatureProperties);

		Map<String, SigningSupport> generatePdfHash = dscService.generatePdfHash(pdfInputProperties);

		getCurrentSession().setAttribute("pdfHash", generatePdfHash);

		RevocationSupport revocationSupport = new RevocationSupport(primaryCrlUrl, secondaryCrlUrl,
				revocationRequestXml);

		String signingRevocationXml = dscService.generateRequestXml(generatePdfHash, serialNumber, serverDate,
				revocationSupport, SigningConstants.PURPOSE_SIGNING);

		//System.out.println("signingRevocationXml\n" + signingRevocationXml);
		modelMap.addAttribute("signingRevocationXml", signingRevocationXml);
		modelMap.addAttribute("id", id);
		getCurrentSession().setAttribute("id", id);
		getCurrentSession().removeAttribute("unSignDocPre");
		return "dsc/PopUpDscPasswordSigned";
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/savepdf", method = RequestMethod.POST)
	public String savepdf(ModelMap modelMap, @RequestParam String responseXMLSigning) throws Exception 
	{
		Long id=(Long) getCurrentSession().getAttribute("id");
		OfficeOrder officeOrder=officeOrderRepository.findById(id).get();
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
			
			officeOrder.setUnsignFile(unSignPdfPath);
			officeOrder.setSignFile(signPdfPath);
			officeOrder.setOrderSignedDate(new Date());
			officeOrder.setIsSigned(1);
			officeOrder.setIsDSC(1);
			
			DSCSigning dscSigningDetails = new DSCSigning(certificateDetails.getSerialNumber(),
					certificateDetails.getIssuedTo(), certificateDetails.getIssuedBy(), certificateDetails.getValidUpto(),
					dscResponse.getStatus(), dscResponse.getMAC(), dscResponse.getIP(), certificateDetails.getAliasName(),
					certificateDetails.getCdpPoint(), responseXml, user.getUserId(), officeOrder.getId());
			
			addDSCSigningRepository.save(dscSigningDetails);
			officeOrderRepository.save(officeOrder);
			
			}
			getCurrentSession().removeAttribute("signDoc");
			getCurrentSession().removeAttribute("unSignDoc");
			getCurrentSession().removeAttribute("pdfHash");
			getCurrentSession().removeAttribute("unSignPdfFile");
			getCurrentSession().removeAttribute("signPdfFile");
			getCurrentSession().removeAttribute("id");
		
			return "redirect:/allSignedOrders";
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

	@RequestMapping("/viewpdffile/{fileName:.+}")
	public void viewFile(HttpServletRequest request, HttpServletResponse response,
			@PathVariable("fileName") String fileName) throws Exception {
		
		SNMSValidator snmsvalid = new SNMSValidator();
	if(	snmsvalid.isValidFileName(fileName)) {
		File parent = new File(filePath).getParentFile().getCanonicalFile();
		String dataDirectory =  ESAPI.validator().getValidDirectoryPath("DirectoryName",filePath,parent,false);
		Path file = Paths.get(dataDirectory+ File.separator + fileName);
		if (Files.exists(file)) {
			if (fileName.endsWith("pdf")) {
				response.setContentType("application/pdf");
			}
			response.addHeader("Content-Disposition", "inline; filename=" + fileName);
			try {
				Files.copy(file, response.getOutputStream());
				response.getOutputStream().flush();
			} catch (IOException ex) {
				ex.printStackTrace();
			}  
		}
	}
		
	}
	
	@RequestMapping("getSignedOrders")
	public String signedOrederList(ModelMap modelMap) throws NumberFormatException, ParseException, Exception{
		List<Object[]> signedOreders=officeOrderDao.findSignedOfficeOrder(getUserDetails());
		List<SignedOfficeOrderDto> signedOrderList=new ArrayList<SignedOfficeOrderDto>();
		if (signedOreders.size() > 0)
		{	
		for(Object[] obj:signedOreders)
			signedOrderList.add(new SignedOfficeOrderDto(obj[0].toString(), obj[1].toString(), Long.parseLong(obj[2].toString()),obj[3].toString(),obj[4].toString()));
		}
	
		modelMap.addAttribute("signOrders", signedOrderList);
		return "DSCSigning/SignedOfficeOrders";
	}
	@RequestMapping("allSignedOrders")
	public String allSignedOrederList(ModelMap modelMap) throws NumberFormatException, ParseException, Exception{
		List<Object[]> signedOreders=officeOrderDao.findAllSignedOfficeOrder();
		List<SignedOfficeOrderDto> signedOrderList=new ArrayList<SignedOfficeOrderDto>();
		if (signedOreders.size() > 0) {
		for(Object[] obj:signedOreders)
		{
			String signed_date = obj[3]==null ? "Legacy Order" : obj[3].toString();
			String signed_file = obj[4]==null ? obj[5].toString() : obj[4].toString();
			String id =  obj[6].toString();
			signedOrderList.add(new SignedOfficeOrderDto( obj[0].toString(), obj[1].toString(), Long.parseLong(obj[2].toString()),signed_date,signed_file, Long.parseLong(id), obj[7].toString(),obj[8].toString()));
			//signedOrderList.add(new SignedOfficeOrderDto(obj[0].toString(), obj[1].toString(), Long.parseLong(obj[2].toString()),obj[3].toString(),obj[4].toString()));
		}
		}
		modelMap.addAttribute("signOrders", signedOrderList);
		return "DSCSigning/SignedOfficeOrders";
	}
	
	@RequestMapping("/viewSorders")
	public void viewFile(HttpServletRequest request, HttpServletResponse response,
			@RequestParam("id") Long id,@RequestParam("caseid") Long caseid) throws Exception {
	  
		
		
        Optional<CaseDetails> caseDtls = caseDetailsRepository.findById(caseid);	
            String signFile;
		if(caseDtls.get().getRadioValue().equals("Legacy") || caseDtls.get().getRadioValue().equals("Prosecution"))
		{
			signFile=caseDtls.get().getLegacyOrderFile();
		}
		
		else
		{
	      
		OfficeOrder officeOrder=officeOrderRepository.findById(id).get();
		 signFile=officeOrder.getSignFile();
		}
		
		 File parent = new File(filePath).getParentFile().getCanonicalFile();
		 String dataDirectory = ESAPI.validator().getValidDirectoryPath("DirectoryName",filePath,parent,false);
		Path file = Paths.get(dataDirectory+ File.separator + signFile);
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
	
	@RequestMapping("approvedLegacyOrders")
	public String approvedLegacyOrders(ModelMap modelMap) {
		List<Object[]> order = officeOrderDao.findLegacyOrderByAdoApprove();
		List<ApprovedOrder> approvedOrders = new ArrayList<ApprovedOrder>();

		for (Object obj[] : order)
			approvedOrders
					.add(new ApprovedOrder(obj[0].toString(), obj[1].toString(), Long.parseLong(obj[2].toString()),obj[3],obj[4].toString()));

		modelMap.addAttribute("approvedOrders", approvedOrders);
		return "director/AllApprovedOrder";
	}
	
	public String getDirectorName() {
		UserDetails director = userDetailsRepo.findByDesignation(new AddDesignation(1L));
		return director.getSalutation() + " " + userDetailsService.getFullName(director);
	}
}
