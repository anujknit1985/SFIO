package com.snms.controllers;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.apache.log4j.Logger;
import org.owasp.esapi.ESAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.snms.dao.AppRoleDAO;
import com.snms.dao.AppUserDAO;
import com.snms.dao.NoticeDao;
import com.snms.dao.SummonDao;
import com.snms.dto.InspectorDTO;
import com.snms.dto.InspectorListDTO;
import com.snms.dto.NoticeTempDto;
import com.snms.dto.OfficeOrderTempDto;
import com.snms.dto.PdfPreviewDto;
import com.snms.dto.PendingForApprovalDTO;
import com.snms.dto.StatusDTO;
import com.snms.dto.SummonTempDto;
import com.snms.entity.AddDesignation;
import com.snms.entity.AppUser;
import com.snms.entity.CaseDetails;
import com.snms.entity.Company;
import com.snms.entity.CompanySummon;
import com.snms.entity.IndividualType;
import com.snms.entity.InitiateNoticeDraft;
import com.snms.entity.InitiateSummonDraft;
import com.snms.entity.Inspector;
import com.snms.entity.Linkofficer;
import com.snms.entity.NoticeStatus;
import com.snms.entity.NoticeTemplate;
import com.snms.entity.OfficeOrder;
import com.snms.entity.OfficeOrderTemplate;
import com.snms.entity.SummonDetails;
import com.snms.entity.SummonStatus;
import com.snms.entity.SummonTemplate;

import com.snms.entity.SummonType;
import com.snms.entity.UnitDetails;
import com.snms.entity.UserDetails;
import com.snms.entity.UserRole;
import com.snms.entity.personcompanyApproval;
import com.snms.service.AuditBeanBo;
import com.snms.service.CaseDetailsRepository;
import com.snms.service.InitiateNoticeDraftRepository;
import com.snms.service.InitiateSummonDraftRepository;
import com.snms.service.InspectorRepository;
import com.snms.service.LinkOfficerRepository;
import com.snms.service.NoticeRepository;
import com.snms.service.NoticeTemplateRepository;
import com.snms.service.SummonRepository;
import com.snms.service.SummonTemplateRepository;

import com.snms.service.SummonTypeNew;
import com.snms.service.UnitDetailsRepository;
import com.snms.service.UserDetailsRepository;
import com.snms.service.UserDetailsServiceImpl;
import com.snms.service.UserManagementCustom;
import com.snms.service.UserRoleRepository;
import com.snms.utils.NoticePdf;
import com.snms.utils.SnmsConstant;
import com.snms.utils.SnmsException;
import com.snms.utils.Utils;
import com.snms.validation.SNMSValidator;
import com.snms.validation.SfioValidator;
import com.snms.validation.SummonNoticeValidation;

import groovy.lang.Binding;

@Controller
public class SummonNoticeFlowController {
	private static final Logger logger = Logger.getLogger(SummonNoticeFlowController.class);

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
	private LinkOfficerRepository linkoffRepo;
	// Notice Flow Code Started
	@Value("${file.upload}")
	public String filePath;

	public boolean validateForwardNotice(NoticeTempDto caseDetailsDto, BindingResult result, RedirectAttributes model) {
		if (caseDetailsDto.getCaseId() == null) {
			model.addAttribute("invalidId", "Required Field");
			return true;
		} else if (caseDetailsDto.getCaseStrId() == null || "".equals(caseDetailsDto.getCaseStrId().trim())) {
			model.addFlashAttribute("caseStrId", "Case id is required");
			return true;
		} else if (caseDetailsDto.getDateOfAppear() == null || "".equals(caseDetailsDto.getDateOfAppear().trim())) {
			model.addFlashAttribute("dateOfapper", "Date of Appearance is required");
			return true;
		} else if (caseDetailsDto.getIsSensitive() == null || "".equals(caseDetailsDto.getIsSensitive().trim())) {
			model.addFlashAttribute("isSensitive", "Please choose forward type");
			return true;
		} else if (caseDetailsDto.getSummonNo() == null || "".equals(caseDetailsDto.getSummonNo().trim())) {
			model.addFlashAttribute("summonNo", "Summon No is required");
			return true;
		}
		return false;
	}

	@RequestMapping(value = "/generateNotice", params = "save")
	public String generateNotice(@ModelAttribute NoticeTempDto caseDetailsDto, BindingResult result, Model model,
			RedirectAttributes redirect) throws Exception {
		caseDetailsDto.setIsSensitive("N");
		List<Object[]> inspectorList = userMangCustom.getInspectorList(caseDetailsDto.getCaseId());
		boolean privilege = false;
		try {
			for (Object[] object : inspectorList) {

				if (getUserDetails().getUserId() == Integer.parseInt(object[0].toString()))
					privilege = true;
			}

		} catch (Exception e) {
			logger.info(e.getMessage());
		}
		if (validateForwardNotice(caseDetailsDto, result, redirect))
			return "redirect:/getCompleteCase";
		if (!privilege)
			return "notice/getNoticeAs";

		// change BY gouthami
		NoticeStatus noticeStatus = new NoticeStatus(new CaseDetails(caseDetailsDto.getCaseId()), false, false,
				userDetailsService.getUserDetails(), new Date(), caseDetailsDto.getSummonNo(),
				caseDetailsDto.getDateOfAppear(), caseDetailsDto.getIsSensitive(),
				new SummonType(caseDetailsDto.getSummontypeid()));
//		noticeStatus.setNoticeDin(caseDetailsDto.getNoticeDin());
		noticeStatus = noticeRepo.save(noticeStatus);

		Long srNo = (noticeTempRepo.findMaxSrNo(noticeStatus) == null) ? 1
				: noticeTempRepo.findMaxSrNo(noticeStatus) + 1;
		NoticeStatus noticeStatus1 = noticeRepo.findById(noticeStatus.getId()).get();

		Optional<CaseDetails> caseD = caseDetailsRepository.findById(caseDetailsDto.getCaseId());
		CaseDetails caseDetails = caseD.get();
		// change BY gouthami
		// SummonType2
		// summonType=SummonTypeNewDetails.findById(caseDetailsDto.getSummontypeid()).get();

		SummonType summonType = SummonTypeNewDetails.findById(caseDetailsDto.getSummontypeid()).get();
		// Gouthami
		InitiateNoticeDraft initiateNoticeDraft = (summonDao.findNoticeDraftByCaseDetails(caseDetails,
				summonType) == null)
						? initiateNoticeDarftRepo
								.save(new InitiateNoticeDraft(new CaseDetails(caseDetailsDto.getCaseId()),
										caseDetailsDto.getPara1(), caseDetailsDto.getPara2(), caseDetailsDto.getPara3(),
										caseDetailsDto.getPara4(), caseDetailsDto.getPara5(),caseDetailsDto.getPara1h(),caseDetailsDto.getPara2h(),caseDetailsDto.getPara3h(),
										caseDetailsDto.getPara4h(),caseDetailsDto.getPara5h(),false, getUserDetails(),
										new Date(), new SummonType(caseDetailsDto.getSummontypeid()),
										caseDetailsDto.getDateOfAppear(), caseDetailsDto.getSummonNo()))
						: null;

		NoticeTemplate noticeTemplate = new NoticeTemplate(noticeStatus, srNo, caseDetailsDto.getPara1(),
				caseDetailsDto.getPara2(), caseDetailsDto.getPara3(),caseDetailsDto.getPara4(), caseDetailsDto.getPara5(), caseDetailsDto.getPara1h(),caseDetailsDto.getPara2h(),caseDetailsDto.getPara3h(),
				caseDetailsDto.getPara4h(),caseDetailsDto.getPara5h(),true, userDetailsService.getUserDetails(), new Date());

		noticeTempRepo.save(noticeTemplate);

		String loginUName = appUserDAO.findUserDetails(getUserDetails()).getFirstName() != null
				? appUserDAO.findUserDetails(getUserDetails()).getFirstName()
				: "" + appUserDAO.findUserDetails(getUserDetails()).getMiddleName() != null
						? appUserDAO.findUserDetails(getUserDetails()).getMiddleName()
						: "" + appUserDAO.findUserDetails(getUserDetails()).getLastName() != null
								? appUserDAO.findUserDetails(getUserDetails()).getLastName()
								: "";

		auditBeanBo.setAuditBean(Integer.parseInt(userDetailsService.getUserDetails().getUserId().toString()),
				loginUName, "User", Integer.parseInt(userDetailsService.getUserDetails().getUserId().toString()),
				utils.getMessage("log.noticeTemplate.save"), utils.getMessage("log.noticeTemplate.saved"), loginUName,
				"true");
		auditBeanBo.save();
		model.addAttribute("message",
				"Notice Created and sent for approval successfully for Notice No.: " + noticeStatus1.getSummonNo());
		return "caseDetails/officeOrderSuccess";
	}

	@RequestMapping(value = "/showPendNoticeStage1") // Notice orders stage one for ado
	public String showPendingNoticeStage1(Model model) throws Exception {

//		List<Object[]> list = userMangCustom.findOfficeOrderPendingForApproval(1);
		List<Object[]> list = userMangCustom.findNoticePendingForApproval(1, getUserDetails().getUserId());

		List<PendingForApprovalDTO> pending = new ArrayList<PendingForApprovalDTO>();
		for (Object[] dto : list) {
			pending.add(new PendingForApprovalDTO(Long.parseLong(dto[0].toString()), dto[1].toString(),
					dto[2].toString(), Long.parseLong(dto[3].toString())));
		}

		model.addAttribute("pending", pending);
		CaseDetails caseDetails = new CaseDetails();
		model.addAttribute("caseDetails", caseDetails);
		return "ado/NoticePending";
	}

	@RequestMapping(value = "/showNoticeDetails", params = "caseDetails")
	public String showNoticeDetails(@RequestParam(value = "caseDetails", required = true) Long caseId, Model model)
			throws SnmsException, Exception {
		/*
		 * if(!userMangCustom.isAdo(getUserDetails().getUserId())) return
		 * redirectToError("errmsg.unknownaddl");
		 */
		// gouthami 15/09/2020
		SNMSValidator snmsValid = new SNMSValidator();

		if (!snmsValid.getValidInteger(caseId)) {
			return "redirect:/showPendNoticeStage1";

		}
		;

		Optional<CaseDetails> details = caseDetailsRepository.findById(caseId);

		CaseDetails caseDetails = details.get();
		// gouthami 15/09/2020
		if (caseDetails != null) {
			model.addAttribute("caseDetails", caseDetails);
		}
		List<Object[]> inspectorList = userMangCustom.findByCase(caseDetails.getId());
		int i = 1;
		List<InspectorDTO> inspList = new ArrayList<InspectorDTO>();
		for (Object[] dto : inspectorList) {
			InspectorDTO ins = new InspectorDTO(i,
					dto[0].toString() + " " + dto[1].toString() + " (" + dto[2].toString() + ")", (Boolean) dto[3],
					(Boolean) dto[4]);
			inspList.add(ins);
			i++;
		}
		model.addAttribute("inspList", inspList);
		List<Company> comList = userMangCustom.findByCaseId(new CaseDetails(caseDetails.getId()));
		model.addAttribute("comList", comList);
		return "case/previewNoticeDetails";
	}

	/*
	 * @RequestMapping(value = "/showNoticeDetails", params = "caseDetails") public
	 * String showNoticeDetails(@RequestParam(value = "caseDetails", required =
	 * true) Long caseId, Model model) {
	 * 
	 * Optional<CaseDetails> details = caseDetailsRepository.findById(caseId);
	 * 
	 * CaseDetails caseDetails = details.get();
	 * 
	 * model.addAttribute("caseDetails", caseDetails);
	 * 
	 * List<Object[]> inspectorList =
	 * userMangCustom.findByCase(caseDetails.getId()); int i = 1; List<InspectorDTO>
	 * inspList = new ArrayList<InspectorDTO>(); for (Object[] dto : inspectorList)
	 * { InspectorDTO ins = new InspectorDTO(i, dto[0].toString() + " " +
	 * dto[1].toString() + " (" + dto[2].toString() + ")", (Boolean) dto[3]);
	 * inspList.add(ins); i++; } model.addAttribute("inspList", inspList);
	 * List<Company> comList = userMangCustom.findByCaseId(new
	 * CaseDetails(caseDetails.getId())); model.addAttribute("comList", comList);
	 * return "case/previewNoticeDetails"; }
	 */
	@RequestMapping(value = "/showNoticeDetails", params = "approve")
	public String showNoticeDetailsApprove(@RequestParam(value = "approve", required = true) Long officeOrderId,
			Model model) throws Exception {

		Optional<NoticeStatus> noticeStatus = noticeRepo.findById(officeOrderId);
		NoticeStatus notice = noticeStatus.get();
		CaseDetails caseDetails = notice.getCaseDetails();

		String OfficeAddress = caseDetails.getUnit().getAddress();
		String OfficeAddress_hi = caseDetails.getUnit().getAddress_hi();


//		NoticeTemplate noticeDraft = noticeTempRepo.findByNotice(notice);
		InitiateNoticeDraft noticeDraft = summonDao.findNoticeDraftByCaseDetails(caseDetails, notice.getSummonType());

		// Gouthami 07/01/2021
		AppUser userDetails = userDetailsService.getUserDetails();
		Inspector inspectorList = inspRepo.findAllByCaseDetailsAndAppUser(caseDetails, noticeDraft.getAppUser());
		String Designation;
		String DesignationHi = null;
		if (inspectorList.getIsAdo() == true) {
			// Designation = "Addl";
			Designation = "Addl.Director";
			
			DesignationHi="निरीक्षक";
		} else if (inspectorList.getIsIO() == true) {
			Designation = "Investigating Officer";
			DesignationHi="जांच अधिकारी";
		} else {
			Designation = "Inspector";
			DesignationHi="जांच अधिकारी";
		}

		UserDetails user = userDetailsRepo.findById(noticeDraft.getAppUser().getUserId()).get();
		String io = userDetailsService.getFullName(user);

		InspectorListDTO inspList = getInspectorListAndCompany(caseDetails.getId());
		NoticeTempDto caseDto = new NoticeTempDto(caseDetails.getCaseId(), Utils.getCurrentDateWithMonth(),
				noticeDraft.getPara1(), noticeDraft.getPara2(), noticeDraft.getPara3(), noticeDraft.getPara4(),
				noticeDraft.getPara5(),
				noticeDraft.getPara1h(), noticeDraft.getPara2h(), noticeDraft.getPara3h(), noticeDraft.getPara4h(),
				noticeDraft.getPara5h(),
				notice.getId(), caseDetails.getCaseId(), caseDetails.getId(), io,
				notice.getSummonNo(), "", notice.getSummonType().getId(), notice.getIsSensitive(), Designation,DesignationHi);

		model.addAttribute("caseDto", caseDto);

		model.addAttribute("inspList", inspList.getInspctorList());
		model.addAttribute("copyToList", inspList.getCopyToList());
		model.addAttribute("listCom", inspList.getCompanyList());

		// SummonType
		// summonType=summonTypeDetails.findById(notice.getSummonType().getId()).get();

		// Gouthami
		SummonType summonType = SummonTypeNewDetails.findById(notice.getSummonType().getId()).get();
		String name = summonType.getName();
		String designation = "";
		String address = summonType.getAddress();
		// String email=summonType.getEmail();
		if (summonType.getDesignation() != null) {
			designation = summonType.getDesignation();
		} else {
			designation = " ";
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
		 * address=summonType.getMemberAddress(); } else { designation="Auditor";
		 * name=summonType.getAuditorName(); address=summonType.getAddress();
		 * email=summonType.getEmail(); }
		 */
		String company = summonDao.findCompanyNameById(summonType.getId());
		String noticeCompany = "";
		if (summonType.getIndividualType().getIndividualId() == 5
				|| summonType.getIndividualType().getIndividualId() == 6) {
			noticeCompany = summonType.getNameCompany();
		} else {
			noticeCompany = company;
		}
		int otherSize = inspList.getCompanyList().size() - 1;
		String comapnyPara = "";
		if (otherSize == 0)
			comapnyPara = company;
		else
			comapnyPara = company + " and " + otherSize + " other company";

		List<Object[]> ioadoList = userMangCustom.getIoAdoByCaseId(caseDetails.getId());
		Long ioId = 0L;
		for (Object[] object : ioadoList) {
			if ((Boolean) object[2])
				ioId = Long.parseLong(object[0].toString());
		}
		boolean privilege = false;
		try {
			if (getUserDetails().getUserId() == ioId)
				privilege = true;
		} catch (Exception e) {
			logger.info(e.getMessage());
		}
		// int otherSize=inspList.getCompanyList().size()-1;
		String companydisplay = "";
		if (inspList.getCompanyList().size() == 1)
			companydisplay = inspList.getCompanyList().get(0).getName();
		else
			companydisplay = inspList.getCompanyList().get(0).getName() + " and " + otherSize + " other companies.";

		model.addAttribute("companydisplay", comapnyPara);
		model.addAttribute("privilege", privilege);
		model.addAttribute("companyName", noticeCompany);
		model.addAttribute("name", name);
		model.addAttribute("designation", designation);
		model.addAttribute("address", getSplitedString(address));

		long Type = summonType.getIndividualType().getIndividualId();
		model.addAttribute("Type", Type);

		/*
		 * SummonDetails savedSummon=summonDao.findByCaseId(summonDetails.getCaseId());
		 * 
		 * PdfPreviewDto pdfPreviewDto=new PdfPreviewDto(caseDetails.getId(),
		 * caseDetails.get , type,sumtypeId); model.addAttribute("pdfPreview",
		 * pdfPreviewDto);
		 */
		// PdfPreviewDto pdfPreviewDto=new PdfPreviewDto(caseDetails.getId(), company ,
		// summonType.getType(),summonType.getId(),notice.getDateOfAppear(),notice.getSummonNo());

		PdfPreviewDto pdfPreviewDto = new PdfPreviewDto(caseDetails.getId(), company,
				summonType.getIndividualType().getIndividualName(), summonType.getId(), notice.getDateOfAppear(),
				notice.getSummonNo());

		model.addAttribute("OfficeAddress", getSplitedString(OfficeAddress));
		model.addAttribute("pdfPreview", pdfPreviewDto);
		model.addAttribute("officeOrderId", officeOrderId);
		return "ado/approveNotice";
	}

	@RequestMapping(value = "/approveNoticeStage1")
	public String approveNoticeStage1(@ModelAttribute NoticeTempDto caseDetailsDto, Model model) throws Exception {

		List<Object[]> ioadoList = userMangCustom.getIoAdoByCaseId(caseDetailsDto.getCaseId());
		Long ioId = 0L;
		for (Object[] object : ioadoList) {
			if ((Boolean) object[2])
				ioId = Long.parseLong(object[0].toString());
		}

		// Gouthami 01/10/2021 by
		boolean privilege = false;
		try {
			if (getUserDetails().getUserId() == ioId)
				privilege = true;
		} catch (Exception e) {
			logger.info(e.getMessage());
		}
		// Gouthami 01/10/2021 link officer authentication
		if (!privilege) {
			UserDetails userdet = userDetailsRepo.findById(ioId).get();
			Linkofficer linkofficer = linkoffRepo.findAllByRegularIdAndIsActive(userdet, true);
			try {
				if (linkofficer.getUserDetails().getId() == ioId)
					privilege = true;
			} catch (Exception e) {
				logger.info(e.getMessage());
			}
		}

		Optional<CaseDetails> caseDtls = null;
		if (caseDetailsDto.getIsSensitive().equals("Y")) {
			noticeRepo.approveInfoByIdStage1(new CaseDetails(caseDetailsDto.getCaseId()), caseDetailsDto.getNoticeId());
		} else if (caseDetailsDto.getIsSensitive().equals("N")) {

			caseDtls = caseDetailsRepository.findById(caseDetailsDto.getCaseId());
			String noticeDIN = "";

			System.out.println(caseDtls.get().getFinanceYear());
			noticeDIN = caseDtls.get().getFinanceYear().substring(caseDtls.get().getFinanceYear().lastIndexOf('-') + 1)
					+ caseDtls.get().getFinanceYear().substring(0, caseDtls.get().getFinanceYear().lastIndexOf('-'))
							.concat(SnmsConstant.NOTICE_DIN_ID);
			String sequence = noticeRepo.getNoticeDinSequence().toString();
			noticeDIN = noticeDIN.concat("00000".substring(sequence.length()).concat(sequence))
					.concat(utils.getRandomAlphaNum(SnmsConstant.DIN_RANDOM_DIGIT));
			noticeRepo.approveInfoByIdStage1Final(new CaseDetails(caseDetailsDto.getCaseId()),
					caseDetailsDto.getNoticeId(), noticeDIN, getUserDetails().getUserId().intValue());

		}
		noticeRepo.flush();
		NoticeStatus noticeStatus = new NoticeStatus(caseDetailsDto.getNoticeId());
		Long srNo = (noticeTempRepo.findMaxSrNo(noticeStatus) == null) ? 1
				: noticeTempRepo.findMaxSrNo(noticeStatus) + 1;

		NoticeTemplate noticeTemplate = new NoticeTemplate(noticeStatus, srNo, caseDetailsDto.getPara1(),
				caseDetailsDto.getPara2(), caseDetailsDto.getPara3(), caseDetailsDto.getPara4(),
				caseDetailsDto.getPara5(), true, userDetailsService.getUserDetails(), new Date());

		noticeTempRepo.save(noticeTemplate);

		String loginUName = appUserDAO.findUserDetails(getUserDetails()).getFirstName() != null
				? appUserDAO.findUserDetails(getUserDetails()).getFirstName()
				: "" + appUserDAO.findUserDetails(getUserDetails()).getMiddleName() != null
						? appUserDAO.findUserDetails(getUserDetails()).getMiddleName()
						: "" + appUserDAO.findUserDetails(getUserDetails()).getLastName() != null
								? appUserDAO.findUserDetails(getUserDetails()).getLastName()
								: "";

		auditBeanBo.setAuditBean(Integer.parseInt(userDetailsService.getUserDetails().getUserId().toString()),
				loginUName, "User", Integer.parseInt(userDetailsService.getUserDetails().getUserId().toString()),
				utils.getMessage("log.noticeTemplate.approve"), utils.getMessage("log.noticeTemplate.approved"),
				loginUName, "true");
		auditBeanBo.save();
		NoticeStatus noticeStatus1 = noticeRepo.findById(noticeStatus.getId()).get();
		if (caseDetailsDto.getIsSensitive().equals("Y")) {
			model.addAttribute("message",
					"Notice approved and sent for approval successfully to Director SFIO for Notice No : "
							+ noticeStatus1.getSummonNo());
		} else {
			/*
			 * model.addAttribute("message", "Notice approved successfully for Notice No : "
			 * + noticeStatus1.getSummonNo() + " and DIN " + noticeStatus1.getNoticeDin());
			 */
			model.addAttribute("message",
					"Notice approved successfully for Notice No : " + noticeStatus1.getSummonNo());
			initiateNoticeDarftRepo.updateNoticeDraftFinal(caseDtls.get(), noticeStatus1.getSummonType());
			auditBeanBo.setAuditBean(Integer.parseInt(userDetailsService.getUserDetails().getUserId().toString()),
					loginUName, "User", Integer.parseInt(userDetailsService.getUserDetails().getUserId().toString()),
					utils.getMessage("log.notice.approve"), utils.getMessage("log.notice.approved"), loginUName,
					"true");
			auditBeanBo.save();
		}
		return "ado/apprNoticeSuccess";
	}

	@RequestMapping(value = "/showPendingNoticeDir")
	public String showPendingNoticeDir(Model model) throws Exception {

		List<Object[]> list = userMangCustom.findNoticePendingForApproval(2, getUserDetails().getUserId());

		List<PendingForApprovalDTO> pending = new ArrayList<PendingForApprovalDTO>();

		for (Object[] dto : list) {

			pending.add(new PendingForApprovalDTO(Long.parseLong(dto[0].toString()), dto[1].toString(),
					dto[2].toString(), Long.parseLong(dto[3].toString())));
		}
		model.addAttribute("pending", pending);
		CaseDetails caseDetails = new CaseDetails();
		model.addAttribute("caseDetails", caseDetails);
		return "director/NoticePending";
	}

	@RequestMapping(value = "/showNoticeDetailsDir", params = "caseDetails")
	public String showNoticeDetailsDir(@RequestParam(value = "caseDetails", required = true) Long caseId, Model model) {

		Optional<CaseDetails> details = caseDetailsRepository.findById(caseId);
		CaseDetails caseDetails = details.get();
		model.addAttribute("caseDetails", caseDetails);

		List<Object[]> inspectorList = userMangCustom.findByCase(caseDetails.getId());
		int i = 1;
		List<InspectorDTO> inspList = new ArrayList<InspectorDTO>();
		for (Object[] dto : inspectorList) {
			InspectorDTO ins = new InspectorDTO(i,
					dto[0].toString() + " " + dto[1].toString() + " (" + dto[2].toString() + ")", (Boolean) dto[3],
					(Boolean) dto[4]);
			inspList.add(ins);
			i++;
		}

		model.addAttribute("inspList", inspList);
		List<Company> comList = userMangCustom.findByCaseId(new CaseDetails(caseDetails.getId()));
		model.addAttribute("comList", comList);
		return "director/previewNoticeDetails";
	}
	/*
	 * @RequestMapping(value = "/showNoticeDetailsDir", params = "caseDetails")
	 * public String showNoticeDetailsDir(@RequestParam(value = "caseDetails",
	 * required = true) Long caseId, Model model) {
	 * 
	 * Optional<CaseDetails> details = caseDetailsRepository.findById(caseId);
	 * CaseDetails caseDetails = details.get(); model.addAttribute("caseDetails",
	 * caseDetails);
	 * 
	 * List<Object[]> inspectorList =
	 * userMangCustom.findByCase(caseDetails.getId()); int i = 1; List<InspectorDTO>
	 * inspList = new ArrayList<InspectorDTO>(); for (Object[] dto : inspectorList)
	 * { InspectorDTO ins = new InspectorDTO(i, dto[0].toString() + " " +
	 * dto[1].toString() + " (" + dto[2].toString() + ")", (Boolean) dto[3]);
	 * inspList.add(ins); i++; }
	 * 
	 * model.addAttribute("inspList", inspList); List<Company> comList =
	 * userMangCustom.findByCaseId(new CaseDetails(caseDetails.getId()));
	 * model.addAttribute("comList", comList); return
	 * "director/previewNoticeDetails"; }
	 */

	@RequestMapping(value = "/showNoticeDetailsDir", params = "approve")
	public String showNoticeDetailsDirApprove(@RequestParam(value = "approve", required = true) Long officeOrderId,
			Model model) throws Exception {

		Optional<NoticeStatus> noticeStatus = noticeRepo.findById(officeOrderId);
		NoticeStatus notice = noticeStatus.get();
		CaseDetails caseDetails = notice.getCaseDetails();

		// Gouthami 07/01/2021
		AppUser userDetails = userDetailsService.getUserDetails();
		Inspector inspectorList = inspRepo.findAllByCaseDetailsAndAppUser(caseDetails, userDetails);
		String Designation;
		if (inspectorList.getIsAdo() == true) {
			// Designation = "Addl";

			Designation = "Addl.Director";

		} else if (inspectorList.getIsIO() == true) {
			Designation = "Investigating Officer";
		} else {
			Designation = "Inspector";
		}

		UserDetails user = userDetailsRepo.findById(userDetails.getUserId()).get();
		String ioName = userDetailsService.getFullName(user);
//		NoticeTemplate draft = noticeTempRepo.findByNotice(notice);
		InitiateNoticeDraft draft = summonDao.findNoticeDraftByCaseDetails(caseDetails, notice.getSummonType());
		NoticeTempDto caseDto = new NoticeTempDto(caseDetails.getCaseId(), Utils.getCurrentDateWithMonth(),
				draft.getPara1(), draft.getPara2(), draft.getPara3(), draft.getPara4(), draft.getPara5(),
				notice.getId(), caseDetails.getCaseId(), caseDetails.getId(), getDirectorName(), notice.getSummonNo(),
				"", notice.getSummonType().getId(), notice.getIsSensitive(), Designation);

		model.addAttribute("caseDto", caseDto);
		InspectorListDTO inspList = getInspectorListAndCompany(caseDetails.getId());
		model.addAttribute("inspList", inspList.getInspctorList());
		model.addAttribute("copyToList", inspList.getCopyToList());
		model.addAttribute("listCom", inspList.getCompanyList());

		SummonType summonType = SummonTypeNewDetails.findById(notice.getSummonType().getId()).get();
		String name = summonType.getName();
		String designation = "";
		if (summonType.getDesignation() != null) {
			designation = summonType.getDesignation();
		} else {
			designation = " ";
		}
		String address = summonType.getAddress();
		// String email= summonType.getEmail();
		/*
		 * if(summonType.getType().equalsIgnoreCase("official")) {
		 * if(summonType.getIsdirector().equalsIgnoreCase("Y")) {
		 * designation="Director"; name=summonType.getDirName();
		 * address=summonType.getOffAddress(); email=summonType.getOffEmail(); } else{
		 * designation=summonType.getDesignation(); name=summonType.getName();
		 * address=summonType.getOffAddress(); email=summonType.getOffEmail(); } } else
		 * if(summonType.getType().equalsIgnoreCase("vendor")) {
		 * designation="Other than CUI"; name=summonType.getVendorName();
		 * address=summonType.getVendorAddress(); email=summonType.getVendorEmail();
		 * }else if(summonType.getType().equalsIgnoreCase("secretary")) {
		 * designation="Company Secretary"; name=summonType.getMemberName();
		 * address=summonType.getMemberAddress(); } else { designation="Auditor";
		 * name=summonType.getAuditorName(); address=summonType.getAddress();
		 * email=summonType.getEmail(); }
		 */

		String company = summonDao.findCompanyNameById(summonType.getId());
		String noticeCompany = "";
		if (summonType.getIndividualType().getIndividualId() == 5
				|| summonType.getIndividualType().getIndividualId() == 6) {
			noticeCompany = summonType.getNameCompany();
		} else {
			noticeCompany = company;
		}
		model.addAttribute("companyName", noticeCompany);
		model.addAttribute("name", name);
		model.addAttribute("designation", designation);
		model.addAttribute("address", getSplitedString(address));
		model.addAttribute("io", inspList.getIoName());
		PdfPreviewDto pdfPreviewDto = new PdfPreviewDto(caseDetails.getId(), company,
				summonType.getIndividualType().getIndividualName(), summonType.getId(), notice.getDateOfAppear(),
				notice.getSummonNo());
		model.addAttribute("pdfPreview", pdfPreviewDto);
		return "director/approveNotice";
	}

	@RequestMapping(value = "/approveNoticeDir")
	public String approveNoticeDirector(@ModelAttribute NoticeTempDto caseDetailsDto, Model model) throws Exception {
		Optional<CaseDetails> caseDtls = caseDetailsRepository.findById(caseDetailsDto.getCaseId());
		String noticeDIN = "";
		noticeDIN = caseDtls.get().getFinanceYear().substring(caseDtls.get().getFinanceYear().lastIndexOf('-') + 1)
				+ caseDtls.get().getFinanceYear().substring(0, caseDtls.get().getFinanceYear().lastIndexOf('-'))
						.concat(SnmsConstant.NOTICE_DIN_ID);
		String sequence = noticeRepo.getNoticeDinSequence().toString();
		noticeDIN = noticeDIN.concat("00000".substring(sequence.length()).concat(sequence))
				.concat(utils.getRandomAlphaNum(SnmsConstant.DIN_RANDOM_DIGIT));
		noticeRepo.approveInfoByIdStage2(new CaseDetails(caseDetailsDto.getCaseId()), caseDetailsDto.getNoticeId(),
				noticeDIN);
		NoticeStatus notice = new NoticeStatus(caseDetailsDto.getNoticeId());

		Long srNo = (noticeTempRepo.findMaxSrNo(notice) == null) ? 1 : noticeTempRepo.findMaxSrNo(notice) + 1;
		NoticeTemplate noticeTemplate = new NoticeTemplate(notice, srNo, caseDetailsDto.getPara1(),
				caseDetailsDto.getPara2(), caseDetailsDto.getPara3(), caseDetailsDto.getPara4(),
				caseDetailsDto.getPara5(), true, userDetailsService.getUserDetails(), new Date());
		noticeTempRepo.save(noticeTemplate);

		NoticeStatus notice1 = noticeRepo.findById(notice.getId()).get();
		initiateNoticeDarftRepo.updateNoticeDraftFinal(caseDtls.get(), notice1.getSummonType());
		model.addAttribute("message", "Notice approved successfully for Notice No. : " + notice1.getSummonNo()
				+ " and DIN " + notice1.getNoticeDin());
		return "case/apprOOSuccess1";
	}

//	Notice Flow Code Ended

//	Summon Flow Code Started

	public boolean validateForwardSummon(SummonTempDto caseDetailsDto, BindingResult result, RedirectAttributes model) {
		if (caseDetailsDto.getCaseId() == null) {
			model.addAttribute("invalidId", "Required Field");
			return true;
		} else if (caseDetailsDto.getCaseStrId() == null || "".equals(caseDetailsDto.getCaseStrId().trim())) {
			model.addFlashAttribute("caseStrId", "Case id is required");
			return true;
		} else if (caseDetailsDto.getDateOfAppear() == null || "".equals(caseDetailsDto.getDateOfAppear().trim())) {
			model.addFlashAttribute("dateOfapper", "Date of Appearance is required");
			return true;
		}
		/*
		 * else if(caseDetailsDto.getIsSensitive() == null ||
		 * "".equals(caseDetailsDto.getIsSensitive().trim())) {
		 * model.addFlashAttribute("isSensitive", "Please choose forward type"); return
		 * true; }
		 */
		else if (caseDetailsDto.getSummonNo() == null || "".equals(caseDetailsDto.getSummonNo().trim())) {
			model.addFlashAttribute("summonNo", "Summon No is required");
			return true;
		}
		return false;
	}

	@RequestMapping(value = "/generateSummon", params = "forward")
	public String generateSummon(@ModelAttribute SummonTempDto caseDetailsDto, BindingResult result, Model model,
			RedirectAttributes redirect) throws Exception {

		List<Object[]> inspectorList = userMangCustom.getInspectorList(caseDetailsDto.getCaseId());
		boolean privilege = false;
		try {
			for (Object[] object : inspectorList) {

				if (getUserDetails().getUserId() == Integer.parseInt(object[0].toString()))
					privilege = true;
			}

		} catch (Exception e) {
			logger.info(e.getMessage());
		}
		if (validateForwardSummon(caseDetailsDto, result, redirect))
			return "redirect:/getCompleteCase";
		if (!privilege)
			return "notice/genSummonAs";
		// BY Gouthami
		SummonStatus summonStatus = new SummonStatus(new CaseDetails(caseDetailsDto.getCaseId()), false, false,
				userDetailsService.getUserDetails(), new Date(), caseDetailsDto.getSummonNo(),
				caseDetailsDto.getDateOfAppear(), new SummonType(caseDetailsDto.getSummontypeid()));
//		summonStatus.setSummonDin(caseDetailsDto.getSummonDin());
		// summonStatus.setAppUserApprovedId(null);
		summonStatus = summonRepo.save(summonStatus);

		Long srNo = (summonTempRepo.findMaxSrNo(summonStatus) == null) ? 1
				: summonTempRepo.findMaxSrNo(summonStatus) + 1;

		Optional<CaseDetails> caseD = caseDetailsRepository.findById(caseDetailsDto.getCaseId());
		CaseDetails caseDetails = caseD.get();
		// SummonType
		// summonType=summonTypeDetails.findById(caseDetailsDto.getSummontypeid()).get();
		// BY Gouthami

		SummonType summonType = SummonTypeNewDetails.findById(caseDetailsDto.getSummontypeid()).get();

		InitiateSummonDraft office = (summonDao.findSummonDraftByCaseDetails(caseDetails, summonType) == null)
				? initiatesummonrepo.save(new InitiateSummonDraft(new CaseDetails(caseDetailsDto.getCaseId()),
						caseDetailsDto.getPara1(), caseDetailsDto.getPara2(), caseDetailsDto.getPara3(),
						caseDetailsDto.getPara4(), caseDetailsDto.getPara5(), caseDetailsDto.getPara6(),
						caseDetailsDto.getPara1h(), caseDetailsDto.getPara2h(), caseDetailsDto.getPara3h(),
						caseDetailsDto.getPara4h(), caseDetailsDto.getPara5h(), caseDetailsDto.getPara6h(), false,
						getUserDetails(), new Date(), new SummonType(caseDetailsDto.getSummontypeid()),
						caseDetailsDto.getDateOfAppear(), caseDetailsDto.getSummonNo()))
				: null;

		SummonTemplate summonTemplate = new SummonTemplate(summonStatus, srNo, caseDetailsDto.getPara1(),
				caseDetailsDto.getPara2(), caseDetailsDto.getPara3(), caseDetailsDto.getPara4(),
				caseDetailsDto.getPara5(), caseDetailsDto.getPara6(), caseDetailsDto.getPara1h(),
				caseDetailsDto.getPara2h(), caseDetailsDto.getPara3h(), caseDetailsDto.getPara4h(),
				caseDetailsDto.getPara5h(), caseDetailsDto.getPara6h(), true, userDetailsService.getUserDetails(),
				new Date());

		summonTempRepo.save(summonTemplate);

		String loginUName = appUserDAO.findUserDetails(getUserDetails()).getFirstName() != null
				? appUserDAO.findUserDetails(getUserDetails()).getFirstName()
				: "" + appUserDAO.findUserDetails(getUserDetails()).getMiddleName() != null
						? appUserDAO.findUserDetails(getUserDetails()).getMiddleName()
						: "" + appUserDAO.findUserDetails(getUserDetails()).getLastName() != null
								? appUserDAO.findUserDetails(getUserDetails()).getLastName()
								: "";

		auditBeanBo.setAuditBean(Integer.parseInt(userDetailsService.getUserDetails().getUserId().toString()),
				loginUName, "User", Integer.parseInt(userDetailsService.getUserDetails().getUserId().toString()),
				utils.getMessage("log.SummonTemplate.save"), utils.getMessage("log.SummonTemplate.saved"), loginUName,
				"true");
		auditBeanBo.save();
		model.addAttribute("message",
				"Summon Created and sent for approval successfully for caseId : SFIO/INV/2020/" + summonStatus.getId());
		return "caseDetails/officeOrderSuccess";
	}

	@RequestMapping(value = "/showPendSummonStage1") // Notice orders stage one for ado
	public String showPendSummonStage1(Model model) throws Exception {

		List<Object[]> list = userMangCustom.findSummonPendingForApproval(1, getUserDetails().getUserId());

		List<PendingForApprovalDTO> pending = new ArrayList<PendingForApprovalDTO>();
		for (Object[] dto : list) {
			pending.add(new PendingForApprovalDTO(Long.parseLong(dto[0].toString()), dto[1].toString(),
					dto[2].toString(), Long.parseLong(dto[3].toString())));
		}

		model.addAttribute("pending", pending);
		CaseDetails caseDetails = new CaseDetails();
		model.addAttribute("caseDetails", caseDetails);
		return "ado/SummonPending";
	}

	@RequestMapping(value = "/showSummonDetails", params = "caseDetails")
	public String showSummonDetails(@RequestParam(value = "caseDetails", required = true) Long caseId, Model model)
			throws SnmsException, Exception {
		/*
		 * if(!userMangCustom.isAdo(getUserDetails().getUserId())) return
		 * redirectToError("errmsg.unknownaddl");
		 */

		// gouthami 15/09/2020

		SNMSValidator snmsValid = new SNMSValidator();

		if (!snmsValid.getValidInteger(caseId)) {
			return "redirect:/showPendSummonStage1";
		}
		Optional<CaseDetails> details = caseDetailsRepository.findById(caseId);
		CaseDetails caseDetails = details.get();
		model.addAttribute("caseDetails", caseDetails);

		List<Object[]> inspectorList = userMangCustom.findByCase(caseDetails.getId());
		int i = 1;
		List<InspectorDTO> inspList = new ArrayList<InspectorDTO>();
		for (Object[] dto : inspectorList) {
			InspectorDTO ins = new InspectorDTO(i,
					dto[0].toString() + " " + dto[1].toString() + " (" + dto[2].toString() + ")", (Boolean) dto[3],
					(Boolean) dto[4]);
			inspList.add(ins);
			i++;
		}

		model.addAttribute("inspList", inspList);
		List<Company> comList = userMangCustom.findByCaseId(new CaseDetails(caseDetails.getId()));
		model.addAttribute("comList", comList);
		return "case/previewSummonDetails";
	}

	/*
	 * @RequestMapping(value = "/showSummonDetails", params = "caseDetails") public
	 * String showSummonDetails(@RequestParam(value = "caseDetails", required =
	 * true) Long caseId, Model model) {
	 * 
	 * Optional<CaseDetails> details = caseDetailsRepository.findById(caseId);
	 * CaseDetails caseDetails = details.get(); model.addAttribute("caseDetails",
	 * caseDetails);
	 * 
	 * List<Object[]> inspectorList =
	 * userMangCustom.findByCase(caseDetails.getId()); int i = 1; List<InspectorDTO>
	 * inspList = new ArrayList<InspectorDTO>(); for (Object[] dto : inspectorList)
	 * { InspectorDTO ins = new InspectorDTO(i, dto[0].toString() + " " +
	 * dto[1].toString() + " (" + dto[2].toString() + ")", (Boolean) dto[3]);
	 * inspList.add(ins); i++; }
	 * 
	 * model.addAttribute("inspList", inspList); List<Company> comList =
	 * userMangCustom.findByCaseId(new CaseDetails(caseDetails.getId()));
	 * model.addAttribute("comList", comList); return "case/previewSummonDetails"; }
	 */

	@RequestMapping(value = "/showSummonDetails", params = "approve")
	public String showSummonDetailsApprove(@RequestParam(value = "approve", required = true) Long officeOrderId,
			Model model) throws Exception {
		SNMSValidator snmsValid = new SNMSValidator();
		if (!snmsValid.getValidInteger(officeOrderId)) {
			return "redirect:/showPendSummonStage1";

		}
		Optional<SummonStatus> summonStatus = summonRepo.findById(officeOrderId);
		SummonStatus summon = summonStatus.get();
		CaseDetails caseDetails = summon.getCaseDetails();
		String OfficeAddress = caseDetails.getUnit().getAddress();

		/*
		 * // Gouthami UserDetails userDet =
		 * userDetailsRepo.findAllByUserId(getUserDetails()); String OfficeAddress =
		 * userDet.getUnit().getAddress();
		 */
//		SummonTemplate summonDraft = summonTempRepo.findBySummon(summon);
		InitiateSummonDraft summonDraft = summonDao.findSummonDraftByCaseDetails(caseDetails, summon.getSummonType());
		InspectorListDTO inspList = getInspectorListAndCompany(caseDetails.getId());
		// Gouthami 07/01/2021
		Inspector inspectorList = inspRepo.findAllByCaseDetailsAndAppUser(caseDetails, summonDraft.getAppUser());
		String Designation;
		String DesignationHi = null;
		if (inspectorList.getIsAdo() == true) {
			// Designation = "Addl";
			Designation = "Addl.Director";
			DesignationHi="अपर निदेशक";
			
		} else if (inspectorList.getIsIO() == true) {
			Designation = "Investigating Officer";
			DesignationHi="जांच अधिकारी";
		} else {
			Designation = "Inspector";
			DesignationHi="निरीक्षक";
		}
		UserDetails user = userDetailsRepo.findById(summonDraft.getAppUser().getUserId()).get();
		String ioName = userDetailsService.getFullName(user);
		SummonTempDto caseDto = new SummonTempDto(caseDetails.getCaseId(), Utils.getCurrentDateWithMonth(),
				summonDraft.getPara1(), summonDraft.getPara2(), summonDraft.getPara3(), summonDraft.getPara4(),
				summonDraft.getPara5(), summonDraft.getPara6(),summonDraft.getPara1_h(),summonDraft.getPara2_h(),summonDraft.getPara3_h(),summonDraft.getPara4_h(),summonDraft.getPara5_h(),summonDraft.getPara6_h(), summon.getId(), caseDetails.getCaseId(),
				caseDetails.getId(), ioName, summon.getSummonNo(), "", summon.getIsSensitive(),
				summon.getSummonType().getId(), "", Designation,DesignationHi);
		model.addAttribute("caseDto", caseDto);
		model.addAttribute("inspList", inspList.getInspctorList());
		model.addAttribute("copyToList", inspList.getCopyToList());
		model.addAttribute("listCom", inspList.getCompanyList());
		SummonType summonType = SummonTypeNewDetails.findById(summon.getSummonType().getId()).get();
		String name = summonType.getName();
		String designation = "";

		if (summonType.getDesignation() != null) {
			designation = summonType.getDesignation();
		} else {
			designation = " ";
		}
		String address = summonType.getAddress();
		/*
		 * if(summonType.getType().equalsIgnoreCase("official")) {
		 * if(summonType.getIsdirector().equalsIgnoreCase("Y")) {
		 * designation="Director"; name=summonType.getDirName();
		 * address=summonType.getOffAddress(); } else{
		 * designation=summonType.getDesignation(); name=summonType.getName();
		 * address=summonType.getOffAddress(); } }
		 * 
		 * else if(summonType.getType().equalsIgnoreCase("vendor")) {
		 * designation="Other than CUI"; name=summonType.getVendorName();
		 * address=summonType.getVendorAddress(); }else
		 * if(summonType.getType().equalsIgnoreCase("secretary")) {
		 * designation="Company Secretary"; name=summonType.getMemberName();
		 * address=summonType.getMemberAddress(); } else { designation="Auditor";
		 * name=summonType.getAuditorName(); address=summonType.getAddress(); }
		 */
		// String company = summonDao.findCompanyNameById(summonType.getId());

		List<Object[]> ioadoList = userMangCustom.getIoAdoByCaseId(caseDetails.getId());
		Long ioId = 0L;
		for (Object[] object : ioadoList) {
			if ((Boolean) object[2])
				ioId = Long.parseLong(object[0].toString());
		}
		boolean privilege = false;
		try {
			if (getUserDetails().getUserId() == ioId)
				privilege = true;
		} catch (Exception e) {
			logger.info(e.getMessage());
		}

		model.addAttribute("privilege", privilege);

		String company = summonDao.findCompanyNameById(summonType.getId());
		String noticeCompany = "";
		if (summonType.getIndividualType().getIndividualId() == 5
				|| summonType.getIndividualType().getIndividualId() == 6) {
			noticeCompany = summonType.getNameCompany();
		} else {
			noticeCompany = company;
		}

		int otherSize = inspList.getCompanyList().size() - 1;
		String comapnyPara = "";
		String comapnyPara_hi = "";
		if (otherSize == 0) {
			comapnyPara = company;
		comapnyPara_hi = company;
		}
		else {
			comapnyPara = company + " and " + otherSize + " other companies";
		comapnyPara_hi = company + " तथा " + otherSize + "अन्य कंपनी";
		}
		model.addAttribute("companydisplay", comapnyPara);
		model.addAttribute("companydisplay_hi", comapnyPara_hi);
		model.addAttribute("companyName", noticeCompany);
		model.addAttribute("name", name);
		model.addAttribute("designation", designation);
		model.addAttribute("Designation", Designation);
		model.addAttribute("Designation_Hi", DesignationHi);
		model.addAttribute("address", getSplitedString(address));
		model.addAttribute("io", ioName);
		model.addAttribute("OfficeAddress", OfficeAddress);
		model.addAttribute("officeOrderId", officeOrderId);
		// model.addAttribute("summonId", officeOrderId);

		PdfPreviewDto pdfPreviewDto = new PdfPreviewDto(caseDetails.getId(), company,
				summonType.getIndividualType().getIndividualName(), summonType.getId(), summon.getDateOfAppear(),
				summon.getSummonNo());
		model.addAttribute("pdfPreview", pdfPreviewDto);
		return "ado/approveSummon";
	}

	@RequestMapping(value = "/approveSummonStage1")
	public String approveSummonStage1(@ModelAttribute SummonTempDto caseDetailsDto, Model model) throws Exception {

		List<Object[]> ioadoList = userMangCustom.getIoAdoByCaseId(caseDetailsDto.getCaseId());
		Long ioId = 0L;
		for (Object[] object : ioadoList) {
			if ((Boolean) object[2])
				ioId = Long.parseLong(object[0].toString());
		}
		boolean privilege = false;
		try {
			if (getUserDetails().getUserId() == ioId)
				privilege = true;
		} catch (Exception e) {
			logger.info(e.getMessage());
		}
		if (!privilege) {
			UserDetails userdet = userDetailsRepo.findById(ioId).get();
			Linkofficer linkofficer = linkoffRepo.findAllByRegularIdAndIsActive(userdet, true);
			try {
				if (linkofficer.getUserDetails().getId() == ioId)
					privilege = true;
			} catch (Exception e) {
				logger.info(e.getMessage());
			}
		}

		Optional<CaseDetails> caseDtls = null;
		if (caseDetailsDto.getIsSensitive().equals("Y")) {
			summonRepo.approveInfoByIdStage1(new CaseDetails(caseDetailsDto.getCaseId()), caseDetailsDto.getSummonId(),
					caseDetailsDto.getIsSensitive());
		}

		else if (caseDetailsDto.getIsSensitive().equals("N")) {
			caseDtls = caseDetailsRepository.findById(caseDetailsDto.getCaseId());
			String summonDIN = "";
			summonDIN = caseDtls.get().getFinanceYear().substring(caseDtls.get().getFinanceYear().lastIndexOf('-') + 1)
					+ caseDtls.get().getFinanceYear().substring(0, caseDtls.get().getFinanceYear().lastIndexOf('-'))
							.concat(SnmsConstant.SUMMON_DIN_ID);
			String sequence = summonRepo.getSummonDinSequence().toString();
			summonDIN = summonDIN.concat("00000".substring(sequence.length()).concat(sequence))
					.concat(utils.getRandomAlphaNum(SnmsConstant.DIN_RANDOM_DIGIT));
			summonRepo.approveInfoByIdStage1Final(new CaseDetails(caseDetailsDto.getCaseId()),
					caseDetailsDto.getSummonId(), summonDIN, caseDetailsDto.getIsSensitive(),
					getUserDetails().getUserId().intValue());

		}

		SummonStatus summonStatus = new SummonStatus(caseDetailsDto.getSummonId());

		Long srNo = (summonTempRepo.findMaxSrNo(summonStatus) == null) ? 1
				: summonTempRepo.findMaxSrNo(summonStatus) + 1;
		SummonTemplate summonTemplate = new SummonTemplate(summonStatus, srNo, caseDetailsDto.getPara1(),
				caseDetailsDto.getPara2(), caseDetailsDto.getPara3(), caseDetailsDto.getPara4(),
				caseDetailsDto.getPara5(), caseDetailsDto.getPara6(), true, userDetailsService.getUserDetails(),
				new Date());

		summonTempRepo.save(summonTemplate);
		String loginUName = appUserDAO.findUserDetails(getUserDetails()).getFirstName() != null
				? appUserDAO.findUserDetails(getUserDetails()).getFirstName()
				: "" + appUserDAO.findUserDetails(getUserDetails()).getMiddleName() != null
						? appUserDAO.findUserDetails(getUserDetails()).getMiddleName()
						: "" + appUserDAO.findUserDetails(getUserDetails()).getLastName() != null
								? appUserDAO.findUserDetails(getUserDetails()).getLastName()
								: "";

		auditBeanBo.setAuditBean(Integer.parseInt(userDetailsService.getUserDetails().getUserId().toString()),
				loginUName, "User", Integer.parseInt(userDetailsService.getUserDetails().getUserId().toString()),
				utils.getMessage("log.summonTemplate.approve"), utils.getMessage("log.summonTemplate.approved"),
				loginUName, "true");
		auditBeanBo.save();
		SummonStatus summonStatus1 = summonRepo.findById(summonStatus.getId()).get();
		if (caseDetailsDto.getIsSensitive().equals("Y")) {

			model.addAttribute("message", "Summon approved successfully for Summon No : " + summonStatus1.getSummonNo()
					+ " and DIN " + summonStatus1.getSummonDin());
			auditBeanBo.setAuditBean(Integer.parseInt(userDetailsService.getUserDetails().getUserId().toString()),
					loginUName, "User", Integer.parseInt(userDetailsService.getUserDetails().getUserId().toString()),
					utils.getMessage("log.Summon.forward"), utils.getMessage("log.Summon.forwarded"), loginUName,
					"true");
			auditBeanBo.save();
			model.addAttribute("message",
					"Summon approved and sent for approval successfully to Director SFIO for Summon No : "
							+ summonStatus1.getSummonNo());
		} else {
			// BYgouthami
			initiatesummonrepo.updateSummonDraftFinal(caseDtls.get(), summonStatus1.getSummonType());
			/*
			 * model.addAttribute("message", "Summon approved successfully for Summon No : "
			 * + summonStatus1.getSummonNo() + " and DIN " + summonStatus1.getSummonDin());
			 */

			model.addAttribute("message",
					"Summon approved successfully for Summon No : " + summonStatus1.getSummonNo());
			auditBeanBo.setAuditBean(Integer.parseInt(userDetailsService.getUserDetails().getUserId().toString()),
					loginUName, "User", Integer.parseInt(userDetailsService.getUserDetails().getUserId().toString()),
					utils.getMessage("log.Summon.approve"), utils.getMessage("log.Summon.approved"), loginUName,
					"true");
			auditBeanBo.save();

		}
		return "ado/apprNoticeSuccess";
	}

	@RequestMapping(value = "/showPendingSummonDir")
	public String showPendingSummonDir(Model model) throws Exception {

		List<Object[]> list = userMangCustom.findSummonPendingForApproval(2, getUserDetails().getUserId());

		List<PendingForApprovalDTO> pending = new ArrayList<PendingForApprovalDTO>();

		for (Object[] dto : list) {

			pending.add(new PendingForApprovalDTO(Long.parseLong(dto[0].toString()), dto[1].toString(),
					dto[2].toString(), Long.parseLong(dto[3].toString())));
		}
		model.addAttribute("pending", pending);
		CaseDetails caseDetails = new CaseDetails();
		model.addAttribute("caseDetails", caseDetails);
		return "director/SummonPending";
	}

	@RequestMapping(value = "/showSummonDetailsDir", params = "caseDetails")
	public String showSummonDetailsDir(@RequestParam(value = "caseDetails", required = true) Long caseId, Model model) {

		// gouthami 15/09/2020
		SNMSValidator snmsValid = new SNMSValidator();
		if (!snmsValid.getValidInteger(caseId)) {

			model.addAttribute("id", !snmsValid.getValidInteger(caseId));

			return "redirect:/showPendingSummonDir";
		}
		;
		Optional<CaseDetails> details = caseDetailsRepository.findById(caseId);
		CaseDetails caseDetails = details.get();
		model.addAttribute("caseDetails", caseDetails);
		List<Object[]> inspectorList = userMangCustom.findByCase(caseDetails.getId());
		int i = 1;
		List<InspectorDTO> inspList = new ArrayList<InspectorDTO>();
		for (Object[] dto : inspectorList) {
			InspectorDTO ins = new InspectorDTO(i,
					dto[0].toString() + " " + dto[1].toString() + " (" + dto[2].toString() + ")", (Boolean) dto[3],
					(Boolean) dto[4]);
			inspList.add(ins);
			i++;
		}
		model.addAttribute("inspList", inspList);
		List<Company> comList = userMangCustom.findByCaseId(new CaseDetails(caseDetails.getId()));
		model.addAttribute("comList", comList);
		return "director/previewSummonDetails";
	}
	/*
	 * @RequestMapping(value = "/showSummonDetailsDir", params = "caseDetails")
	 * public String showSummonDetailsDir(@RequestParam(value = "caseDetails",
	 * required = true) Long caseId, Model model) {
	 * 
	 * Optional<CaseDetails> details = caseDetailsRepository.findById(caseId);
	 * CaseDetails caseDetails = details.get(); model.addAttribute("caseDetails",
	 * caseDetails);
	 * 
	 * List<Object[]> inspectorList =
	 * userMangCustom.findByCase(caseDetails.getId()); int i = 1; List<InspectorDTO>
	 * inspList = new ArrayList<InspectorDTO>(); for (Object[] dto : inspectorList)
	 * { InspectorDTO ins = new InspectorDTO(i, dto[0].toString() + " " +
	 * dto[1].toString() + " (" + dto[2].toString() + ")", (Boolean) dto[3]);
	 * inspList.add(ins); i++; } model.addAttribute("inspList", inspList);
	 * List<Company> comList = userMangCustom.findByCaseId(new
	 * CaseDetails(caseDetails.getId())); model.addAttribute("comList", comList);
	 * return "director/previewSummonDetails"; }
	 */

	@RequestMapping(value = "/showSummonDetailsDir", params = "approve")
	public String showSummonDetailsDirApprove(@RequestParam(value = "approve", required = true) Long officeOrderId,
			Model model) throws Exception {
		// gouthami 15/09/2020
		SNMSValidator snmsValid = new SNMSValidator();
		if (!snmsValid.getValidInteger(officeOrderId)) {
			return "redirect:/showPendingSummonDir";
		}
		;
		Optional<SummonStatus> summonStatus = summonRepo.findById(officeOrderId);
		SummonStatus summon = summonStatus.get();
		CaseDetails caseDetails = summon.getCaseDetails();
//		SummonTemplate draft = summonTempRepo.findBySummon(summon);
		InitiateSummonDraft draft = summonDao.findSummonDraftByCaseDetails(caseDetails, summon.getSummonType());

		AppUser userDetails = userDetailsService.getUserDetails();
		Inspector inspectorList = inspRepo.findAllByCaseDetailsAndAppUser(caseDetails, draft.getAppUser());
		String Designation;
		String DesignationHi = null;
		if (inspectorList.getIsAdo() == true) {
			// Designation = "Addl";
			Designation = "Addl.Director";
			DesignationHi="अपर निदेशक";
		} else if (inspectorList.getIsIO() == true) {
			Designation = "Investigating Officer";
			DesignationHi="जांच अधिकारी";
		} else {
			Designation = "Inspector";
			DesignationHi="निरीक्षक";
		}
		UserDetails user = userDetailsRepo.findById(draft.getAppUser().getUserId()).get();
		String ioName = userDetailsService.getFullName(user);

		SummonTempDto caseDto = new SummonTempDto(caseDetails.getCaseId(), Utils.getCurrentDateWithMonth(),
				draft.getPara1(), draft.getPara2(), draft.getPara3(), draft.getPara4(), draft.getPara5(),
				draft.getPara6(),
				draft.getPara1_h(), draft.getPara2_h(), draft.getPara3_h(), draft.getPara4_h(), draft.getPara5_h(),
				draft.getPara6_h(),
				summon.getId(), caseDetails.getCaseId(), caseDetails.getId(), getDirectorName(),
				summon.getSummonNo(), summon.getDateOfAppear(), summon.getIsSensitive(), summon.getSummonType().getId(),
				"", Designation,DesignationHi);

		model.addAttribute("caseDto", caseDto);
		InspectorListDTO inspList = getInspectorListAndCompany(caseDetails.getId());
		model.addAttribute("inspList", inspList.getInspctorList());
		model.addAttribute("copyToList", inspList.getCopyToList());
		model.addAttribute("listCom", inspList.getCompanyList());

		String OfficeAddress = caseDetails.getUnit().getAddress();
		SummonType summonType = SummonTypeNewDetails.findById(summon.getSummonType().getId()).get();
		String name = summonType.getName();
		String designation = "";
		if (summonType.getDesignation() != null) {
			designation = summonType.getDesignation();
		} else {
			designation = " ";
		}
		String address = summonType.getAddress();
		// String email=summonType.getEmail();
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
		 * address=summonType.getMemberAddress(); } else { designation="Auditor";
		 * name=summonType.getAuditorName(); address=summonType.getAddress();
		 * email=summonType.getEmail(); }
		 */
		int otherSize = inspList.getCompanyList().size() - 1;
		String company = summonDao.findCompanyNameById(summonType.getId());
		String comapnyPara = "";
		if (otherSize == 0)
			comapnyPara = company;
		else
			comapnyPara = company + " and " + otherSize + " other companies";
		model.addAttribute("companyName", company);
		model.addAttribute("name", name);
		model.addAttribute("designation", designation);
		model.addAttribute("address", getSplitedString(address));
		model.addAttribute("io", ioName);
		model.addAttribute("companydisplay", comapnyPara);
		model.addAttribute("OfficeAddress  ", OfficeAddress);
		model.addAttribute("officeOrderId", officeOrderId);
		/*
		 * SummonDetails savedSummon=summonDao.findByCaseId(summonDetails.getCaseId());
		 */

		PdfPreviewDto pdfPreviewDto = new PdfPreviewDto(caseDetails.getId(), company,
				summonType.getIndividualType().getIndividualName(), summonType.getId(), summon.getDateOfAppear(),
				summon.getSummonNo());
		model.addAttribute("pdfPreview", pdfPreviewDto);

		return "director/approveSummon";
	}

	@RequestMapping(value = "/approveSummonDir")
	public String approveSummonDirector(@ModelAttribute SummonTempDto caseDetailsDto, Model model) throws Exception {

		Optional<CaseDetails> caseDtls = caseDetailsRepository.findById(caseDetailsDto.getCaseId());
		String summonDIN = "";
		summonDIN = caseDtls.get().getFinanceYear().substring(caseDtls.get().getFinanceYear().lastIndexOf('-') + 1)
				+ caseDtls.get().getFinanceYear().substring(0, caseDtls.get().getFinanceYear().lastIndexOf('-'))
						.concat(SnmsConstant.SUMMON_DIN_ID);
		String sequence = summonRepo.getSummonDinSequence().toString();
		summonDIN = summonDIN.concat("00000".substring(sequence.length()).concat(sequence))
				.concat(utils.getRandomAlphaNum(SnmsConstant.DIN_RANDOM_DIGIT));

		summonRepo.approveInfoByIdStage2(new CaseDetails(caseDetailsDto.getCaseId()), caseDetailsDto.getSummonId(),
				summonDIN);
		SummonStatus order = new SummonStatus(caseDetailsDto.getSummonId());
		Long srNo = (summonTempRepo.findMaxSrNo(order) == null) ? 1 : summonTempRepo.findMaxSrNo(order) + 1;
		SummonTemplate summonTemplate = new SummonTemplate(order, srNo, caseDetailsDto.getPara1(),
				caseDetailsDto.getPara2(), caseDetailsDto.getPara3(), caseDetailsDto.getPara4(),
				caseDetailsDto.getPara5(), caseDetailsDto.getPara6(), true, userDetailsService.getUserDetails(),
				new Date());
		summonTempRepo.save(summonTemplate);
		SummonStatus summonStatus1 = summonRepo.findById(order.getId()).get();

		initiatesummonrepo.updateSummonDraftFinal(caseDtls.get(), summonStatus1.getSummonType());
		String loginUName = appUserDAO.findUserDetails(getUserDetails()).getFirstName() != null
				? appUserDAO.findUserDetails(getUserDetails()).getFirstName()
				: "" + appUserDAO.findUserDetails(getUserDetails()).getMiddleName() != null
						? appUserDAO.findUserDetails(getUserDetails()).getMiddleName()
						: "" + appUserDAO.findUserDetails(getUserDetails()).getLastName() != null
								? appUserDAO.findUserDetails(getUserDetails()).getLastName()
								: "";

		auditBeanBo.setAuditBean(Integer.parseInt(userDetailsService.getUserDetails().getUserId().toString()),
				loginUName, "User", Integer.parseInt(userDetailsService.getUserDetails().getUserId().toString()),
				utils.getMessage("log.Summon.dirapprove"), utils.getMessage("log.Summon.dirapproved"), loginUName,
				"true");
		auditBeanBo.save();
		model.addAttribute("message", "Summon approved successfully for summon no.: " + summonStatus1.getSummonNo()
				+ " and DIN " + summonStatus1.getSummonDin());
		return "case/apprOOSuccess1";
	}
	// Summon Flow Code Ended

	private String getSplitedString(String address) {
		String[] splitStr = address.split(" ");
		StringBuilder consStr = new StringBuilder();
		for (int i = 0; i < splitStr.length; i++) {
			consStr.append(splitStr[i]).append(" ");
			if (i % 2 == 0)
				consStr.append("\n");
		}
		return consStr.toString();
	}

	// fetching all the list of companies and inspectors from the blew method
	public InspectorListDTO getInspectorListAndCompany(Long caseId) {

		List<Object[]> inspectorList = userMangCustom.findByCase(caseId);
		String ioName = "";
		int i = 1;
		List<InspectorDTO> inspList = new ArrayList<InspectorDTO>();
		List<InspectorDTO> copyToList = new ArrayList<InspectorDTO>();
		for (Object[] dto : inspectorList) {
			InspectorDTO ins = new InspectorDTO(i,
					dto[0].toString() + " " + dto[1].toString() + " (" + dto[2].toString() + ")", (Boolean) dto[3],
					(Boolean) dto[4]);
			inspList.add(ins);
			copyToList.add(ins);
			if ((Boolean) dto[3])
				ioName = dto[1].toString() + " (" + dto[2].toString() + ")";
			i++;
		}
		int length = copyToList.size();
		copyToList.add(new InspectorDTO(length + 1, "PPS to Director, SFIO", false, false));
		copyToList.add(new InspectorDTO(length + 2, "Guard File", false, false));

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
	/*
	 * public InspectorListDTO getInspectorListAndCompany(Long caseId) {
	 * 
	 * List<Object[]> inspectorList = userMangCustom.findByCase(caseId); String
	 * ioName = ""; int i = 1; List<InspectorDTO> inspList = new
	 * ArrayList<InspectorDTO>(); List<InspectorDTO> copyToList = new
	 * ArrayList<InspectorDTO>(); for (Object[] dto : inspectorList) { InspectorDTO
	 * ins = new InspectorDTO(i, dto[0].toString() + " " + dto[1].toString() + " ("
	 * + dto[2].toString() + ")", (Boolean) dto[3]); inspList.add(ins);
	 * copyToList.add(ins); if ((Boolean) dto[3]) ioName = dto[0].toString() + " " +
	 * dto[1].toString() + " (" + dto[2].toString() + ")"; i++; } int length =
	 * copyToList.size(); copyToList.add(new InspectorDTO(length + 1,
	 * "PPS to Director, SFIO", false)); copyToList.add(new InspectorDTO(length + 2,
	 * "Guard File", false));
	 * 
	 * List<Company> comList = userMangCustom.findByCaseId(new CaseDetails(caseId));
	 * List<Company> listCom = new ArrayList<Company>(); Long k = 1L; for (Company
	 * com : comList) { Company c = new Company(); c.setId(k);
	 * c.setName(com.getName()); listCom.add(c); k++; }
	 * 
	 * return new InspectorListDTO(inspList, copyToList, ioName, listCom); }
	 */

	public String getDirectorName() {
		UserDetails director = userDetailsRepo.findByDesignation(new AddDesignation(1L));
		return director.getSalutation() + " " + userDetailsService.getFullName(director);
	}

	public String getLoginUserName() {
		return SecurityContextHolder.getContext().getAuthentication().getName();
	}

	private AppUser getUserDetails() throws Exception {
		AppUser appUser = this.appUserDAO.findUserAccount(getLoginUserName());
		return appUser;
	}

	private String redirectToError(String msg) throws SnmsException, Exception {
		AppUser loginUser = appUserDAO.findUserAccount(userDetailsService.getLoginUserName());
		// userDetailsService.getCurrentSession().setAttribute("ErrorString",
		// utils.getMessage(msg));
		// userDetailsService.getCurrentSession().removeAttribute(NAME);
		ModelMap model = new ModelMap();
		model.addAttribute("ErrorString", utils.getMessage(msg));
		ErrorPageRedirect(loginUser, model);
		return "redirect:/errorPage";
	}

	public void ErrorPageRedirect(AppUser loginUser, ModelMap model) {
		String appRoleName = appRoleDao.getRoleName(loginUser.getUserId());
		if (appRoleName.contains("ROLE_DIRECTOR"))
			model.addAttribute("ErrorUrl", "dirHome");
		else if (appRoleName.contains("ROLE_ADMIN"))
			model.addAttribute("ErrorUrl", "home");
		else if (appRoleName.contains("ROLE_USER"))
			model.addAttribute("ErrorUrl", "userHome");
		else if (appRoleName.contains("ROLE_ADMIN_SECTION"))
			model.addAttribute("ErrorUrl", "adsHome");
		else if (appRoleName.contains("ROLE_ADMIN_OFFICER"))
			model.addAttribute("ErrorUrl", "adoHome");
		else
			model.addAttribute("ErrorUrl", "login");
	}

	@RequestMapping(value = "/approveSummonStage1", params = "Reject")
	public String RejectSummonStage1(@ModelAttribute SummonTempDto caseDetailsDto, Model model) throws Exception {

		List<Object[]> ioadoList = userMangCustom.getIoAdoByCaseId(caseDetailsDto.getCaseId());
		Long ioId = 0L;
		for (Object[] object : ioadoList) {
			if ((Boolean) object[2])
				ioId = Long.parseLong(object[0].toString());
		}
		boolean privilege = false;
		try {
			if (getUserDetails().getUserId() == ioId)
				privilege = true;
		} catch (Exception e) {
			logger.info(e.getMessage());
		}

		if (!privilege) {
			UserDetails userdet = userDetailsRepo.findById(ioId).get();
			Linkofficer linkofficer = linkoffRepo.findAllByRegularIdAndIsActive(userdet, true);
			try {
				if (linkofficer.getUserDetails().getId() == ioId)
					privilege = true;
			} catch (Exception e) {
				logger.info(e.getMessage());
			}
		}

		Optional<CaseDetails> caseDtls = null;

		SummonStatus summonStatus = new SummonStatus(caseDetailsDto.getSummonId());
		SummonStatus summonStatus1 = summonRepo.findById(summonStatus.getId()).get();

		// summonStatus.setIsRejected(true);
		summonRepo.approveInfoByIdReject(new CaseDetails(caseDetailsDto.getCaseId()), caseDetailsDto.getSummonId());
		// BYgouthami
		// initiatesummonrepo.updateSummonDraftFinal(caseDtls.get(),summonStatus1.getSummonType());
		model.addAttribute("message", "Summon " + summonStatus1.getSummonNo() + " has been sent back to Review.");

		return "ado/apprNoticeSuccess";
	}

	@RequestMapping(value = "/approveNoticeStage1", params = "Reject")
	public String RejectNoticeStage1(@ModelAttribute NoticeTempDto caseDetailsDto, Model model) throws Exception {

		List<Object[]> ioadoList = userMangCustom.getIoAdoByCaseId(caseDetailsDto.getCaseId());
		Long ioId = 0L;
		for (Object[] object : ioadoList) {
			if ((Boolean) object[2])
				ioId = Long.parseLong(object[0].toString());
		}
		boolean privilege = false;
		try {
			if (getUserDetails().getUserId() == ioId)
				privilege = true;
		} catch (Exception e) {
			logger.info(e.getMessage());
		}
		if (!privilege) {
			UserDetails userdet = userDetailsRepo.findById(ioId).get();
			Linkofficer linkofficer = linkoffRepo.findAllByRegularIdAndIsActive(userdet, true);
			try {
				if (linkofficer.getUserDetails().getId() == ioId)
					privilege = true;
			} catch (Exception e) {
				logger.info(e.getMessage());
			}
		}

		// Optional<CaseDetails> caseDtls = null;

		NoticeStatus noticeStatus = new NoticeStatus(caseDetailsDto.getNoticeId());
		NoticeStatus noticeStatus1 = noticeRepo.findById(noticeStatus.getId()).get();

		// summonStatus.setIsRejected(true);
		noticeRepo.approveInfoByIdReject(new CaseDetails(caseDetailsDto.getCaseId()), caseDetailsDto.getNoticeId());
		// BYgouthami
		// initiatesummonrepo.updateSummonDraftFinal(caseDtls.get(),summonStatus1.getSummonType());
		model.addAttribute("message", "Notice " + noticeStatus1.getSummonNo() + " has been sent back to Review.");
		return "ado/apprNoticeSuccess";
	}

	@RequestMapping(value = "/approveSummonDir", params = "Reject")
	public String RejectSummonDir(@ModelAttribute SummonTempDto caseDetailsDto, Model model) throws Exception {

		List<Object[]> ioadoList = userMangCustom.getIoAdoByCaseId(caseDetailsDto.getCaseId());

		Optional<CaseDetails> caseDtls = null;

		SummonStatus summonStatus = new SummonStatus(caseDetailsDto.getSummonId());
		SummonStatus summonStatus1 = summonRepo.findById(summonStatus.getId()).get();

		// summonStatus.setIsRejected(true);
		summonStatus1.setAprrovalStage1(false);
		summonStatus1.setIsSensitive("");
		summonRepo.save(summonStatus1);
		// summonRepo.sendBackToReview(new
		// CaseDetails(caseDetailsDto.getCaseId()),caseDetailsDto.getSummonId(),"Y");
		// BYgouthami
		// initiatesummonrepo.updateSummonDraftFinal(caseDtls.get(),summonStatus1.getSummonType());
		model.addAttribute("message", "Summon " + summonStatus1.getSummonNo() + " has been sent back to Review.");

		return "ado/apprNoticeSuccess";
	}

}
