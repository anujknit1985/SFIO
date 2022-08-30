package com.snms.controllers;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.snms.dao.AppUserDAO;
import com.snms.dao.DSCDao;
import com.snms.dto.DscRegForm;
import com.snms.entity.AppUser;
import com.snms.entity.DSCRegistration;
import com.snms.service.DSCRegDetailsRepository;
import com.snms.utils.NoticePdf;
import com.snms.validation.DscValidation;

import app.eoffice.dsc.constants.SigningConstants;
import app.eoffice.dsc.model.CertificateDetails;
import app.eoffice.dsc.model.RevocationSupport;
import app.eoffice.dsc.model.SigningSupport;
import app.eoffice.dsc.service.DscService;
import app.eoffice.dsc.util.XmlUtil;
import app.eoffice.dsc.xml.request.RevocationRequest;
import app.eoffice.dsc.xml.response.CertListResponse;
import app.eoffice.dsc.xml.response.DSCResponse;

@Controller
public class DSCController {
	 private static final Logger logger = Logger.getLogger(DSCController.class);
		
	@Autowired(required=true)
	private DscService dscService;
	
	@Autowired
	private DSCRegDetailsRepository dscRegRepo;
	
	 @Autowired
	 private AppUserDAO appUserDAO;
	 
	 @Autowired
	 private DSCDao dSCDao;
	
	@RequestMapping(value = "/showCert")
	public String showCert(ModelMap modelMap) throws Exception {
		DSCRegistration dscDetails = dSCDao.findUserId(getUserDetails().getUserId());
		if (dscDetails != null) {
			modelMap.addAttribute("dsc", dscDetails);
			return "dsc/SuccessDSCDetails";
		} else {
			return "dsc/getCertificate";
		}

	}
	
	@RequestMapping(value = "/getCertList", method = RequestMethod.POST)
	public String showCertList(ModelMap modelMap, @RequestParam String certValue, HttpServletRequest request)
			throws Exception {
		String certificateValue = certValue;
			CertListResponse certListResponse = dscService.parseDscList(certificateValue);

		DateFormat formatter = new SimpleDateFormat("E MMM dd HH:mm:ss Z yyyy");
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");

		for (int i = 0; i < certListResponse.getCertificates().size(); i++) {
			String validity = certListResponse.getCertificates().get(i).getNotAfter();
			java.util.Date valdate = formatter.parse(validity);

			String validityDate = sdf.format(valdate);
			certListResponse.getCertificates().get(i).setNotAfter(validityDate);
		}
		modelMap.addAttribute("certList", certListResponse.getCertificates());
		modelMap.addAttribute("dscRegForm", new DscRegForm());
		getCurrentSession().setAttribute("certlist", certListResponse);
		return "dsc/showCertList";
	}
	
	@RequestMapping(value = "/saveCert", method = RequestMethod.POST)
	public String saveDemoDSC(@ModelAttribute DscRegForm dscRegForm, ModelMap modelMap, BindingResult result)
			throws Exception {
		DscValidation dscval = new DscValidation();
		if (result.hasErrors()) {
			return "dsc/getCertificate";
		}
		String revocationRequextXml = "";
		String serverDate = getCurrentDate();
	
		String serialNo = dscRegForm.getSerialNo();
		CertListResponse certresp= (CertListResponse) getCurrentSession().getAttribute("certlist");
		
		
		String valid = dscRegForm.getValidity();
		
		if (dscval.checkDscValidity(valid, serverDate)) {
			modelMap.addAttribute("dscRegForm", dscRegForm);
			modelMap.addAttribute("isValid", "This DSC token has been expired.");
			return "dsc/showCertList";
		}
		revocationRequextXml += "<Revocation>\n";
		boolean flag=false;
		for(int i=0;i<certresp.getCertificates().size();i++)
		{
			for(int j=0;j<certresp.getCertificates().get(i).getRevocation().getRevReq().size();j++)
			{
				if(certresp.getCertificates().get(i).getRevocation().getRevReq().get(j).getSerialNumber().equals(serialNo))
				{
					List<RevocationRequest> revreq=certresp.getCertificates().get(i).getRevocation().getRevReq();
					for(int k=0;k<revreq.size();k++)
					{
					revocationRequextXml += "<RevocationRequest>\n" + "<SerialNumber>" + revreq.get(k).getSerialNumber()
							+ "</SerialNumber>\n" + "<CdpPoint>" + revreq.get(k).getCdpPoint() + "</CdpPoint>\n" + "<CertLevel>"
							+ revreq.get(k).getCertLevel() + "</CertLevel>\n" + "</RevocationRequest>\n";
					}
					flag=true;
					break;
				}
				
			}
		}
		revocationRequextXml += "</Revocation>\n";

	
		if(flag==false){
			return  "dsc/showCertList";}
		else{
		String primaryCrlUrl = "http://10.246.57.100/CRLWebService";
		String secondaryCrlUrl = "";

		// Create RevocationSupport Constructor RevocationSupport
		RevocationSupport revocationSupport = new RevocationSupport(primaryCrlUrl, secondaryCrlUrl,
				revocationRequextXml);

		String randomSeed = dscService.generateRandomSeed();
		getCurrentSession().setAttribute("randomSeedValue", randomSeed);

		// get revocation response xml
		Map<String, String> input = new HashMap<>();
		input.put("1", randomSeed);
		Map<String, SigningSupport> genartedrandomStringMap = dscService.generateTextHash(input);
		String registrationRequestXml = dscService.generateRequestXml(genartedrandomStringMap, serialNo, serverDate,
				revocationSupport, SigningConstants.PURPOSE_REGISTRATION);
	
		modelMap.addAttribute("responseXml", registrationRequestXml);
		getCurrentSession().removeAttribute("certlist");
		return "dsc/PopUpDscPassword_Register";
		}

	}

	@RequestMapping(value = "/successReg", method = RequestMethod.POST)
	public String showRegisterXml(ModelMap modelMap, @RequestParam("successXml") String successXml) throws Exception {
		String signXml = successXml;
		String revocationRequextXml = "";
		//logger.info("Regxml = " + signXml);
		String certContent = "";
		try {
			//logger.info("try block showRegisterXml method");
			if (dscService.isValidResponse(signXml, (String) getCurrentSession().getAttribute("randomSeedValue"))) {
				//logger.info("inside isvalidresponse method");
				DSCResponse dscResponse = (DSCResponse) XmlUtil.unmarshalXmlToObject(DSCResponse.class, signXml);
				for (int i = 0; i < dscResponse.getChainCerts().getChainCertsList().size(); i++) {
					//logger.info("******dscResponse.getChainCerts().getChainCertsList() for loop****");
					if (dscResponse.getChainCerts().getChainCertsList().get(i).getCertLevel().equals("0")) {
						certContent = dscResponse.getChainCerts().getChainCertsList().get(i).getCertContent();
						//logger.info("dscResponse.getChainCerts().getChainCertsList().get(i).getCertLevel().equals zero++++");

					}
				}

				// decode CertContent into base64
				Base64.Decoder decoder = Base64.getDecoder();
				byte[] certContentByte = decoder.decode(certContent);

				// getCertificate details
				CertificateDetails certificateDetails = dscService.retriveCertDetails(certContentByte);

				revocationRequextXml += "<Revocation>\n";
				for (int i = 0; i < dscResponse.getChainCerts().getChainCertsList().size(); i++) {
					revocationRequextXml += "<RevocationRequest>\n" + "<SerialNumber>"
							+ dscResponse.getChainCerts().getChainCertsList().get(i).getSerialNumber()
							+ "</SerialNumber>\n" + "<CdpPoint>"
							+ dscResponse.getChainCerts().getChainCertsList().get(i).getCdpPoint() + "</CdpPoint>\n"
							+ "<CertLevel>" + dscResponse.getChainCerts().getChainCertsList().get(i).getCertLevel()
							+ "</CertLevel>\n" + "</RevocationRequest>\n";
				}
				revocationRequextXml += "</Revocation>";
				
				//Save Dsc Details
				DSCRegistration dscdetails=new DSCRegistration(certificateDetails.getSerialNumber(),certificateDetails.getAliasName(), 
								certificateDetails.getIssuedTo(),certificateDetails.getIssuedBy() 
								,certificateDetails.getValidUpto(),dscResponse.getStatus(),getUserDetails().getUserId(),
								dscResponse.getMAC(), dscResponse.getIP(), certificateDetails.getCdpPoint(), signXml, revocationRequextXml,1);
						
				dscRegRepo.save(dscdetails);		
				modelMap.addAttribute("successDSCMsg", "The Digital Signature Certificate has been registered.");
			} // end of if(isValidResponse-method).

			else {
				//logger.info("else part of isvalidResponse method");
				return null;
			}
		} catch (Exception e) {
			logger.info(e.getMessage());
			modelMap.addAttribute("errorDSCReg", "The Digital Signature Certificate has not registered yet.Please try again.");
		}
		return "redirect:/showCert";

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
	
	public String getCurrentDate()
    {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        return sdf.format(new Date());
    }
	
	@RequestMapping(value="/unregDSC",method = RequestMethod.POST)
	public String getunregisterDSC(@RequestParam("id") Long id,ModelMap modelMap)throws Exception{
	
		DSCRegistration dscById =dscRegRepo.findById(id).get();
		if(id==null || dscById.getRegStatus()==0)
		{
			modelMap.addAttribute("invalid", "Invalid id");
			return "redirect:/showCert";
		}
		if(dscById.getUserid().equals(getUserDetails().getUserId()))
		{
			
			dscById.setId(id);
			dscById.setRegStatus(0);
			dscRegRepo.save(dscById);
			return "redirect:/showCert";
		}
		else
		{
			modelMap.addAttribute("unauthorized", "A Current logged in user is unauthorized.");
			return "redirect:/showCert";
		}
	}

}
