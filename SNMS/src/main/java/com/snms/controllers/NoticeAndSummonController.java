package com.snms.controllers;

import java.io.ByteArrayInputStream;

import java.io.IOException;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.snms.dao.AjaxDao;
import com.snms.dao.AppRoleDAO;
import com.snms.dao.AppUserDAO;
import com.snms.dao.InspectorDao;
import com.snms.dao.NoticeDao;
import com.snms.dao.SummonDao;
import com.snms.dto.InspectorDTO;
import com.snms.dto.InspectorEditDTO;
import com.snms.dto.InspectorListDTO;
import com.snms.dto.NoticeDto;
import com.snms.dto.NoticeTempDto;
import com.snms.dto.PdfPreviewDto;
import com.snms.dto.PendingForApprovalDTO;
import com.snms.dto.SummonDto;
import com.snms.dto.SummonTempDto;
import com.snms.entity.AppRole;
import com.snms.entity.AppUser;
import com.snms.entity.CaseDetails;
import com.snms.entity.Company;
import com.snms.entity.CompanySummon;
import com.snms.entity.CompanyType;
import com.snms.entity.ICAI;
import com.snms.entity.IndividualType;
import com.snms.entity.InitiateNoticeDraft;
import com.snms.entity.InitiateSummonDraft;
import com.snms.entity.Inspector;
import com.snms.entity.NoticeStatus;
import com.snms.entity.OfficeOrder;
import com.snms.entity.SummonDetails;
import com.snms.entity.SummonStatus;
import com.snms.entity.SummonTemplate;
import com.snms.entity.SummonType;
import com.snms.entity.UnitDetails;
import com.snms.entity.UserDetails;
import com.snms.entity.UserRole;
import com.snms.entity.inspectorHistory;
import com.snms.entity.personcompanyApproval;
import com.snms.service.AddCompanyRepository;
import com.snms.service.AddNoticeSummonRepository;
import com.snms.service.AuditBeanBo;
import com.snms.service.CaseDetailsRepository;
import com.snms.service.CompanySummonRepository;
import com.snms.service.IdividualTypeRepository;
import com.snms.service.InitiateNoticeDraftRepository;
import com.snms.service.InitiateSummonDraftRepository;
import com.snms.service.InspectorRepository;
import com.snms.service.NoticeRepository;
import com.snms.service.OfficeOrderRepository;
import com.snms.service.SummonDetailsRepo;
import com.snms.service.SummonRepository;

import com.snms.service.SummonTypeNew;
import com.snms.service.UserDetailsRepository;
import com.snms.service.UserDetailsServiceImpl;
import com.snms.service.UserManagementCustom;

import com.snms.utils.NoticeConstant;
import com.snms.utils.NoticePdf;
import com.snms.utils.SnmsConstant;
import com.snms.utils.SnmsException;
import com.snms.utils.SummonConstant;
import com.snms.utils.SummonPDF;
import com.snms.utils.Utils;
import com.snms.validation.CaseJsonResponse;
import com.snms.validation.SNMSValidator;

import com.snms.validation.SummonNoticeValidation;
import com.snms.validation.UserValidation;

@Controller
public class NoticeAndSummonController {
	private static final Logger logger = Logger.getLogger(NoticeAndSummonController.class);

	@Autowired
	private AjaxDao ajaxDao;
	  @Autowired
	    private AppRoleDAO appRoleDao;
	@Autowired
	private AppUserDAO appUserDAO;
	@Autowired
	InspectorDao inspectorDao;
	@Autowired
	private OfficeOrderRepository officeOrderRepo;

	// gouthami 15/09/2020
	/*
	 * @Autowired private AppRoleDAO appRoleDAO;
	 */

	/*
	 * @Autowired private UserRoleRepository userRoleRepo;
	 */

	@Autowired
	private CaseDetailsRepository caseDetailsRepository;

	@Autowired
	private UserManagementCustom userMangCustom;

	@Autowired
	private AddNoticeSummonRepository addNoticeSummonRepository;

	@Autowired
	private SummonDao summonDao;

	@Autowired
	private UserDetailsRepository userDetailsRepo;

	@Autowired
	private NoticeDao noticeDao;

	@Autowired
	SummonTypeNew SummonTypeNewDetails;

	@Autowired
	private InitiateSummonDraftRepository initiatesummonrepo;

	@Autowired
	private InitiateNoticeDraftRepository initiateNoticeDarftRepo;

	@Autowired
	private NoticeRepository noticeRepo;

	@Autowired
	private SummonRepository summonRepo;

	@Autowired
	private AuditBeanBo auditBeanBo;

	@Autowired
	private UserDetailsServiceImpl userDetailsService;

	@Autowired
	private Utils utils;

	@Autowired
	private CompanySummonRepository companySummonRepo;

	@Autowired
	private AddCompanyRepository addCompanyRepo;
	@Autowired
	private IdividualTypeRepository idividualRepo;

	@Autowired
	private SummonDetailsRepo summonDtlRepo;

	@Autowired
	private InspectorRepository inspRepo;

	@GetMapping("getCaseList")
	public String getCaseList(ModelMap modelMap) throws Exception {
		List<Object[]> list = userMangCustom.findCaseByUserId(getUserDetails().getUserId());
		// List<Object[]> list =
		// userMangCustom.findCaseByUserIdbystage(getUserDetails().getUserId());
		List<PendingForApprovalDTO> caselist = new ArrayList<PendingForApprovalDTO>();

		for (Object[] dto : list) {

			caselist.add(new PendingForApprovalDTO(dto[0].toString(), dto[1].toString(),
					Long.parseLong(dto[2].toString()), dto[3], dto[4], dto[5].toString()));
		}

		modelMap.addAttribute("caselist", caselist);
		modelMap.addAttribute("caseDetails", new CaseDetails());
		return "case/assignedCase";
	}

	@RequestMapping(value = "/createNotice", params = "caseDetails")
	public String CaseDetails(@RequestParam(value = "caseDetails", required = true) Long caseId, Model model) {
		SNMSValidator snmsValid = new SNMSValidator();

		if (!snmsValid.getValidInteger(caseId)) {
			model.addAttribute("message", "Invalid caseId ");
			return "caseDetails/ErrorPage";
		}
		Optional<CaseDetails> caseDe = caseDetailsRepository.findById(caseId);
		CaseDetails caseDetails = caseDe.get();
		model.addAttribute("caseDetails", caseDetails);
		InspectorListDTO inspList = getInspectorListAndCompany(caseDetails.getId());
		OfficeOrder officeorder = officeOrderRepo.findAllByCaseDetails(caseDetails);
		if (officeorder != null) {
			model.addAttribute("officeorder", officeorder);
		}
		model.addAttribute("inspList", inspList.getInspctorList());
		model.addAttribute("copyToList", inspList.getCopyToList());
		model.addAttribute("comList", inspList.getCompanyList());
		model.addAttribute("backtype", 1);
		return "notice/caseDetailView";
	}

	@RequestMapping(value = "/createNotice", params = "AddCompany")
	public String AddCompCase(@RequestParam(value = "AddCompany", required = true) Long caseId, Model model) {
		SNMSValidator snmsValid = new SNMSValidator();

		if (!snmsValid.getValidInteger(caseId)) {
			model.addAttribute("message", "Invalid caseId ");
			return "caseDetails/ErrorPage";
		}
		Optional<CaseDetails> caseDe = caseDetailsRepository.findById(caseId);
		CaseDetails caseDetails = caseDe.get();
		model.addAttribute("caseDetails", caseDetails);
		InspectorListDTO inspList = getInspectorListAndAllCompany(caseDetails.getId());

		model.addAttribute("inspList", inspList.getInspctorList());
		model.addAttribute("copyToList", inspList.getCopyToList());
		model.addAttribute("comList", inspList.getCompanyList());
		return "notice/AddCompanyCase";
	}

	private InspectorListDTO getInspectorListAndAllCompany(Long caseId) {
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

		List<Company> comList = userMangCustom.findByAllCompByCaseId(new CaseDetails(caseId));
		List<Company> listCom = new ArrayList<Company>();
		Long k = 1L;
		for (Company com : comList) {
			Company c = new Company();
			c.setId(k);
			c.setName(com.getName());
			c.setApproved_status(com.getApproved_status());
			listCom.add(c);
			k++;
		}

		return new InspectorListDTO(inspList, copyToList, ioName, listCom);

	}

	@RequestMapping(value = "/showPendcompStage1") // Notice orders stage one for ado
	public String showPendingNoticeStage1(Model model) throws Exception {
		List<CaseDetails> penCompanyByCase = null;
		String appRoleName=appRoleDao.getRoleName(	userDetailsService.getUserDetails().getUserId());
		 if (appRoleName.equals("ROLE_ADMIN_OFFICER")) {
			 penCompanyByCase =	 userMangCustom.findCompanyByCasePendingForApprovalByADO( false, 4L);
		 }else {
		    penCompanyByCase = userMangCustom.findCompanyByCasePendingForApproval(1,
				userDetailsService.getUserDetails().getUserId(), false, 1L);
		 }
		CaseDetails caseDetails = new CaseDetails();
		model.addAttribute("caseDetails", caseDetails);
		model.addAttribute("totlapenCompany", penCompanyByCase);
		return "notice/compPending";
	}

	@RequestMapping(value = "updateCompanyDetails", params = "CompApprove")
	public String getcompapproved(@RequestParam(value = "CompApprove", required = true) Long compId, ModelMap model)
			throws Exception {

		Company compDetails = addCompanyRepo.findById(compId).get();
		compDetails.setApproval_date(new Date());
		compDetails.setApprovedBY(getUserDetails().getUserId());
		compDetails.setIsActive(true);
		compDetails.setApproved_status(2L);
		addCompanyRepo.save(compDetails);
		Optional<CaseDetails> caseDe = caseDetailsRepository.findById(compDetails.getCaseDetails().getId());
		CaseDetails caseDetails = caseDe.get();
		model.addAttribute("caseDetails", caseDetails);
		InspectorListDTO inspList = getInspectorListAndPendingCompany(caseDetails.getId());
		if (inspList.getCompanyList().isEmpty()) {
			model.addAttribute("message", "Company " + compDetails.getName() + "  has been approved ");
			return "notice/CompanySucesss";
		} else {
			model.addAttribute("inspList", inspList.getInspctorList());
			model.addAttribute("copyToList", inspList.getCopyToList());
			model.addAttribute("comList", inspList.getCompanyList());
			model.addAttribute("message", "Company " + compDetails.getName() + " has been approved ");
			return "notice/compapprove";
		}

	}

	@RequestMapping(value = "updateCompanyDetails", params = "CompReject")
	public String getCompReject(@RequestParam(value = "CompId", required = true) Long compId,
			@Valid CaseDetails casedetails, ModelMap model) throws Exception {

		Company compDetails = addCompanyRepo.findById(compId).get();

		compDetails.setApprovedBY(getUserDetails().getUserId());
		compDetails.setIsActive(false);
		compDetails.setApproved_status(3L);
		compDetails.setRemark(casedetails.getCompRemark());
		addCompanyRepo.save(compDetails);
		Optional<CaseDetails> caseDe = caseDetailsRepository.findById(compDetails.getCaseDetails().getId());
		CaseDetails caseDetails = caseDe.get();
		model.addAttribute("caseDetails", caseDetails);
		InspectorListDTO inspList = getInspectorListAndPendingCompany(caseDetails.getId());
		if (inspList.getCompanyList().isEmpty()) {
			model.addAttribute("message", "Company " + compDetails.getName() + "  has been Rejected ");
			return "notice/CompanySucesss";
		} else {
			model.addAttribute("inspList", inspList.getInspctorList());
			model.addAttribute("copyToList", inspList.getCopyToList());
			model.addAttribute("comList", inspList.getCompanyList());
			model.addAttribute("message", "Company " + compDetails.getName() + " has been Rejected ");
			return "notice/compapprove";
		}

	}

	// @RequestMapping(value="/updateCompanyDetails")
	@RequestMapping(value = "/companyRejected")
	public String unpublishDocs(HttpServletRequest request, HttpServletResponse response,
			@RequestParam("compId") Long compId, @RequestParam("remark") String remark, ModelMap model,
			RedirectAttributes redirectAttr) throws SnmsException, Exception {
		Company compDetails = addCompanyRepo.findById(compId).get();

		compDetails.setApprovedBY(getUserDetails().getUserId());
		compDetails.setIsActive(false);
		compDetails.setApproved_status(3L);
		compDetails.setRemark(remark);
		addCompanyRepo.save(compDetails);
		Optional<CaseDetails> caseDe = caseDetailsRepository.findById(compDetails.getCaseDetails().getId());
		CaseDetails caseDetails = caseDe.get();
		model.addAttribute("caseDetails", caseDetails);
		InspectorListDTO inspList = getInspectorListAndPendingCompany(caseDetails.getId());
		if (inspList.getCompanyList().isEmpty()) {
			model.addAttribute("message", "Company " + compDetails.getName() + "  has been Rejected ");
			return "notice/CompanySucesss";
		} else {
			model.addAttribute("inspList", inspList.getInspctorList());
			model.addAttribute("copyToList", inspList.getCopyToList());
			model.addAttribute("comList", inspList.getCompanyList());
			model.addAttribute("message", "Company " + compDetails.getName() + " has been Rejected ");
			return "notice/compapprove";
		}
	}

	@RequestMapping(value = "showcompanyApproval", params = "compapprove")
	public String getcompapprove(@RequestParam(value = "compapprove", required = true) Long caseId, ModelMap model) throws Exception {
		SNMSValidator snmsValid = new SNMSValidator();

		if (!snmsValid.getValidInteger(caseId)) {
			model.addAttribute("message", "Invalid caseId ");
			return "caseDetails/ErrorPage";
		}
		Optional<CaseDetails> caseDe = caseDetailsRepository.findById(caseId);
		CaseDetails caseDetails = caseDe.get();
		model.addAttribute("caseDetails", caseDetails);
		InspectorListDTO inspList = getInspectorListAndPendingCompany(caseDetails.getId());

		model.addAttribute("inspList", inspList.getInspctorList());
		model.addAttribute("copyToList", inspList.getCopyToList());
		model.addAttribute("comList", inspList.getCompanyList());

		return "notice/compapprove";
	}

	private InspectorListDTO getInspectorListAndPendingCompany(Long caseId) throws Exception {
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

		String appRoleName=appRoleDao.getRoleName(	userDetailsService.getUserDetails().getUserId());		
		
		List<Company> comList = userMangCustom.findByPendingCompByCaseId(new CaseDetails(caseId) ,appRoleName);

		
		List<Company> listCom = new ArrayList<Company>();
		Long k = 1L;
		for (Company com : comList) {
			Company c = new Company();
			c.setId(com.getId());
			c.setName(com.getName());
			c.setApproved_status(com.getApproved_status());
			listCom.add(c);
			k++;
		}

		return new InspectorListDTO(inspList, copyToList, ioName, listCom);

	}

	@RequestMapping(value = "createNotice", params = "addIndividual")
	public String getNoticeDetails(@RequestParam(value = "addIndividual", required = true) Long caseId,
			ModelMap modelMap) throws Exception {

		SNMSValidator snmsvalid = new SNMSValidator();
		if (snmsvalid.getValidInteger(caseId)) {
			commonObjectSummon(modelMap, caseId);
			SummonDetails summonDetails1 = new SummonDetails();
			SummonDetails findByCaseId = summonDao.findByCaseId(caseId);
			List<IndividualType> individualType = idividualRepo.findAll();

			if (findByCaseId != null) {
				summonDetails1.setId(findByCaseId.getId());
				modelMap.addAttribute("summonbycaseid", findByCaseId);
				modelMap.addAttribute("summonbycaseidCompanyList", findByCaseId.getCompanySummon());
				for (int i = 0; i < findByCaseId.getCompanySummon().size(); i++) {
					summonDetails1.getCompanySummonsDto().setId(findByCaseId.getCompanySummon().get(i).getId());
					for (int j = 0; j < findByCaseId.getCompanySummon().get(i).getSummonType().size(); j++) {
						summonDetails1.getSummonTypenewDto()
								.setId(findByCaseId.getCompanySummon().get(i).getSummonType().get(j).getId());
					}
				}
			}
			/*
			 * OfficeOrder officeorder =
			 * officeOrderRepo.findAllByCaseDetails(summonDetails1.);
			 */
			/*
			 * if(officeorder!=null) { modelMap.addAttribute("officeorder", officeorder); }
			 */
			summonDetails1.setCaseId(caseId);
			modelMap.addAttribute("summonDetails", summonDetails1);
			modelMap.addAttribute("individualTlist", individualType);

			return "notice/CreateNotice";
		} else {
			modelMap.addAttribute("errorCaseID", "invalidCaseId");
			List<Object[]> list = userMangCustom.findCaseByUserId(getUserDetails().getUserId());
			List<PendingForApprovalDTO> caselist = new ArrayList<PendingForApprovalDTO>();

			for (Object[] dto : list) {

				caselist.add(new PendingForApprovalDTO(dto[0].toString(), dto[1].toString(),
						Long.parseLong(dto[2].toString()), dto[3], dto[4], dto[5].toString()));
			}

			modelMap.addAttribute("caselist", caselist);
			modelMap.addAttribute("caseDetails", new CaseDetails());

			return "case/assignedCase";
		}

	}

	@SuppressWarnings("unused")
	@RequestMapping(value = "addnewCompany", method = RequestMethod.POST)
	public String addNewCompany(@ModelAttribute("summonDetails") @Valid SummonDetails summonDetails,
			BindingResult result, ModelMap modelMap) throws Exception {

		if (null == summonDetails || summonDetails.equals(0)) {
			return "notice/CreateNotice";
		}
		List<IndividualType> individualType = idividualRepo.findAll();
		commonObjectSummon(modelMap, summonDetails.getCaseId());
		CompanySummon companySummon = new CompanySummon(summonDetails.getCompanySummonsDto().getCin(),
				summonDetails.getCompanySummonsDto().getCompanyName(),
				summonDetails.getCompanySummonsDto().getAddress(), summonDetails.getCompanySummonsDto().getEmail());

		SummonDetails savedSummon = summonDao.findByCaseId(summonDetails.getCaseId());
		if (companySummon.getCompanyType() == null) {
			CompanyType comp = new CompanyType();
			comp.setId(1);
			companySummon.setCompanyType(comp);
		}

		SummonNoticeValidation summNoticeval = new SummonNoticeValidation();
		summNoticeval.validateCompleteCompanysummon(summonDetails, result);

		if (result.hasErrors()) {

			// summonDetails.setCaseId(summonDetails.getCaseId());

			modelMap.addAttribute("individualTlist", individualType);
			modelMap.addAttribute("summonDetails", summonDetails);
			return "notice/CreateNotice";
		}

		if (summonDetails.getId() != 0) {
			boolean isCompExist = false;
			for (int i = 0; i < savedSummon.getCompanySummon().size(); i++) {
				if (savedSummon.getCompanySummon().get(i).getCompanyName()
						.equalsIgnoreCase(summonDetails.getCompanySummonsDto().getCompanyName())) {
					isCompExist = true;
					savedSummon.getCompanySummon().get(i).setCin(summonDetails.getCompanySummonsDto().getCin());
					savedSummon.getCompanySummon().get(i).setAddress(summonDetails.getCompanySummonsDto().getAddress());
					savedSummon.getCompanySummon().get(i).setEmail(summonDetails.getCompanySummonsDto().getEmail());
					savedSummon.getCompanySummonsDto().setCin(companySummon.getCin());
					savedSummon.getCompanySummonsDto().setAddress(companySummon.getAddress());
					savedSummon.getCompanySummonsDto().setEmail(companySummon.getEmail());
					savedSummon.getCompanySummonsDto().setCompanyName(companySummon.getCompanyName());
					/*
					 * savedSummon.getCompanySummon().get(i).setCin(summonDetails.
					 * getCompanySummonsDto().getCin());
					 * savedSummon.getCompanySummon().get(i).setAddress(summonDetails.
					 * getCompanySummonsDto().getAddress()); break;
					 */
				}
			}
			if (!isCompExist) {
				companySummon.setId(0);
				savedSummon.getCompanySummon().add(companySummon);
				savedSummon.getCompanySummonsDto().setCin(companySummon.getCin());
				savedSummon.getCompanySummonsDto().setAddress(companySummon.getAddress());
				savedSummon.getCompanySummonsDto().setEmail(companySummon.getEmail());
				savedSummon.getCompanySummonsDto().setCompanyName(companySummon.getCompanyName());
				savedSummon.getCompanySummonsDto().setCompanyType(companySummon.getCompanyType());
			}
		} else {

			savedSummon = new SummonDetails();
			BeanUtils.copyProperties(summonDetails, savedSummon);
			BeanUtils.copyProperties(summonDetails.getCompanySummonsDto(), companySummon);
			if (companySummon.getCompanyType() == null) {
				CompanyType comp = new CompanyType();
				comp.setId(1);
				companySummon.setCompanyType(comp);
			}
			savedSummon.getCompanySummon().add(companySummon);
		}
//		modelMap.addAttribute("compcin", companySummon1.getCin());
//		modelMap.addAttribute("compAddr", companySummon1.getAddress());
		modelMap.addAttribute("summonbycaseidCompanyList", savedSummon.getCompanySummon());
		addNoticeSummonRepository.save(savedSummon);

		String loginUName = appUserDAO.findUserDetails(getUserDetails()).getFirstName() != null
				? appUserDAO.findUserDetails(getUserDetails()).getFirstName()
				: ""

						+ appUserDAO.findUserDetails(getUserDetails()).getMiddleName() != null
								? appUserDAO.findUserDetails(getUserDetails()).getMiddleName()
								: "" + appUserDAO.findUserDetails(getUserDetails()).getLastName() != null
										? appUserDAO.findUserDetails(getUserDetails()).getLastName()
										: "";

		auditBeanBo.setAuditBean(Integer.parseInt(userDetailsService.getUserDetails().getUserId().toString()),
				loginUName, "User", Integer.parseInt(userDetailsService.getUserDetails().getUserId().toString()),
				utils.getMessage("log.Company.save"), utils.getMessage("log.Company.saved"), loginUName, "true");
		auditBeanBo.save();

		modelMap.addAttribute("individualTlist", individualType);
		modelMap.addAttribute("summonDetails", savedSummon);
		return "notice/CreateNotice";
	}

	@RequestMapping(value = "/AddCompnybyIO", method = RequestMethod.POST)
	public @ResponseBody CaseJsonResponse UpdateCaseDetails(HttpServletRequest request,
			@Valid @ModelAttribute("caseDetails") CaseDetails caseDetails, BindingResult bindResult, Model model)
			throws Exception {
		boolean flag = false;
		long inspectorid = 0;
		CaseJsonResponse caseJsonResponse = new CaseJsonResponse();
		UserValidation validation = new UserValidation();
		validation.validateCompanyDetails(caseDetails, bindResult);

		if (bindResult.hasErrors()) {
			Map<String, String> errors = new HashMap<String, String>();
			List<FieldError> fieldErrors = bindResult.getFieldErrors();
			for (FieldError fieldError : fieldErrors) {
				String[] resolveMessageCodes = bindResult.resolveMessageCodes(fieldError.getCode());
				String string = resolveMessageCodes[0];
				errors.put(fieldError.getField(), "" + utils.getMessage(string.substring(0, string.lastIndexOf("."))));
			}

			caseDetails.setMcaFile(null);
			caseDetails.setCourtFile(null);
			caseDetails.setAddIoOrder(null);
			caseDetails.setDeleteIoOrder(null);
			caseJsonResponse.setErrorsMap(errors);
			caseJsonResponse.setCaseDetails(caseDetails);
			caseJsonResponse.setStatus("ERROR");
			return caseJsonResponse;
		}
		InspectorListDTO inspcompList = getALLCompany(caseDetails.getId());
		boolean comflag = false;
		long compid = 0;
		UserDetails user = new UserDetails();
		long section = 0;
		long aprrovalStage;
		List<Company> companyList = new ArrayList<Company>();
		for (int i = 0; i < caseDetails.getCompanyName().length; i++) {

			for (int j = 0; j < inspcompList.getCompanyList().size(); j++) {

				String compname = inspcompList.getCompanyList().get(j).getName().trim();
				if (compname.equals(caseDetails.getCompanyName()[i].trim())) {

					String compidName = inspcompList.getCompanyList().get(j).getName();
					Company comp = inspectorDao.findComapnyNameByCaseIdAndUserId(caseDetails.getId().intValue(),
							compidName);
					compid = comp.getId();
					user = comp.getCreatedBy();

					comflag = true;
					break;
				}

			}

			if (comflag == true) {
				Company company = addCompanyRepo.findById(compid).get();
				companyList.add(company);
				comflag = false;
			} else {
				user = userDetailsRepo.findAllByEmail(getLoginUserName());
				Company company = new Company(caseDetails, caseDetails.getCompanyName()[i], false, new Date(), user, 2L,
						1L);
				// company.setIsActive(true);
				companyList.add(company);
			}
		}
		// caseDetails = caseDetailsRepository.save(caseDetails);
		if (!companyList.isEmpty())
			addCompanyRepo.saveAll(companyList);
		inspectorHistory inh = new inspectorHistory();

		String loginUName = appUserDAO.findUserDetails(getUserDetails()).getFirstName() != null
				? appUserDAO.findUserDetails(getUserDetails()).getFirstName()
				: "" + appUserDAO.findUserDetails(getUserDetails()).getMiddleName() != null
						? appUserDAO.findUserDetails(getUserDetails()).getMiddleName()
						: "" + appUserDAO.findUserDetails(getUserDetails()).getLastName() != null
								? appUserDAO.findUserDetails(getUserDetails()).getLastName()
								: "";
		auditBeanBo.setAuditBean(Integer.parseInt(userDetailsService.getUserDetails().getUserId().toString()),
				loginUName, "User", Integer.parseInt(userDetailsService.getUserDetails().getUserId().toString()),
				utils.getMessage("log.case.update"), utils.getMessage("log.case.updated"), loginUName, "true");
		auditBeanBo.save();

		caseJsonResponse.setCaseDetails(caseDetails);
		caseDetails.setAddIoOrder(null);
		caseDetails.setDeleteIoOrder(null);
		caseJsonResponse.setStatus("SUCCESS");
		return caseJsonResponse;
		// return caseJsonResponse;
	}

	/*
	 * @RequestMapping(value="addNewSummon",method=RequestMethod.POST) public String
	 * saveSummonDetails(@Valid @ModelAttribute("summonDetails") SummonDetails
	 * summonDetails,BindingResult result, ModelMap modelMap) throws Exception {
	 * commonObjectSummon(modelMap,summonDetails.getCaseId()); SummonDetails
	 * saveSummon; CompanySummon companySummon=new CompanySummon(); //SummonType
	 * summonType=new SummonType();
	 * 
	 * SummonNoticeValidation sumVal=new SummonNoticeValidation();
	 * sumVal.validateIndivialType(summonDetails, result); if(result.hasErrors()) {
	 * 
	 * //summonDetails.getSummonTypeDto().setType(summonDetails.getSummonTypeDto().
	 * getType()); modelMap.addAttribute("summonDetails", summonDetails); return
	 * "notice/CreateNotice"; } //for summonType object set and save SummonType
	 * summonType1; CompanySummon companySummon1 = new CompanySummon();
	 * if(summonDetails.getId() != 0){
	 * saveSummon=summonDao.findByCaseId(summonDetails.getCaseId());
	 * saveSummon.setId(summonDetails.getId());
	 * if(summonDetails.getSummonTypeDto().getType().equals("auditor".trim())) {
	 * summonType1=new SummonType(summonDetails.getSummonTypeDto().getType(),
	 * summonDetails.getSummonTypeDto().getRegNo(),
	 * summonDetails.getSummonTypeDto().getAuditorName(),
	 * summonDetails.getSummonTypeDto().getEmail(),
	 * summonDetails.getSummonTypeDto().getPhone(),
	 * summonDetails.getSummonTypeDto().getAddress()); } else
	 * if(summonDetails.getSummonTypeDto().getType().equals("vendor".trim())){
	 * summonType1=new SummonType();
	 * summonType1.setType(summonDetails.getSummonTypeDto().getType());
	 * summonType1.setIsCin(summonDetails.getSummonTypeDto().getIsCin());
	 * summonType1.setCinNum(summonDetails.getSummonTypeDto().getCinNum());
	 * summonType1.setVendorName(summonDetails.getSummonTypeDto().getVendorName());
	 * summonType1.setVendorRelation(summonDetails.getSummonTypeDto().
	 * getVendorRelation());
	 * summonType1.setVendorEmail(summonDetails.getSummonTypeDto().getVendorEmail())
	 * ;
	 * summonType1.setVendorPhone(summonDetails.getSummonTypeDto().getVendorPhone())
	 * ; summonType1.setVendorAddress(summonDetails.getSummonTypeDto().
	 * getVendorAddress());
	 * 
	 * }else
	 * if(summonDetails.getSummonTypeDto().getType().equals("secretary".trim())){
	 * summonType1=new SummonType();
	 * summonType1.setType(summonDetails.getSummonTypeDto().getType());
	 * summonType1.setMemberShipNum(summonDetails.getSummonTypeDto().
	 * getMemberShipNum());
	 * summonType1.setMemberName(summonDetails.getSummonTypeDto().getMemberName());
	 * summonType1.setMemberEmail(summonDetails.getSummonTypeDto().getMemberEmail())
	 * ;
	 * summonType1.setMemberPhone(summonDetails.getSummonTypeDto().getMemberPhone())
	 * ; summonType1.setMemberAddress(summonDetails.getSummonTypeDto().
	 * getMemberAddress()); } else { summonType1=new SummonType();
	 * summonType1.setType(summonDetails.getSummonTypeDto().getType());
	 * if(summonDetails.getSummonTypeDto().getIsdirector().equalsIgnoreCase("Y".trim
	 * ())) {
	 * summonType1.setIsdirector(summonDetails.getSummonTypeDto().getIsdirector());
	 * summonType1.setIsExOfficial(summonDetails.getSummonTypeDto().getIsExOfficial(
	 * )); summonType1.setDin(summonDetails.getSummonTypeDto().getDin());
	 * summonType1.setDirName(summonDetails.getSummonTypeDto().getDirName());
	 * summonType1.setOffJoinDate(summonDetails.getSummonTypeDto().getOffJoinDate())
	 * ; summonType1.setOffEmail(summonDetails.getSummonTypeDto().getOffEmail());
	 * summonType1.setOffPhone(summonDetails.getSummonTypeDto().getOffPhone());
	 * summonType1.setOffAddress(summonDetails.getSummonTypeDto().getOffAddress());
	 * } else {
	 * summonType1.setIsdirector(summonDetails.getSummonTypeDto().getIsdirector());
	 * summonType1.setName(summonDetails.getSummonTypeDto().getName());
	 * summonType1.setDesignation(summonDetails.getSummonTypeDto().getDesignation())
	 * ; summonType1.setSex(summonDetails.getSummonTypeDto().getSex());
	 * summonType1.setDob(summonDetails.getSummonTypeDto().getDob());
	 * summonType1.setNationality(summonDetails.getSummonTypeDto().getNationality())
	 * ;
	 * summonType1.setNationalId(summonDetails.getSummonTypeDto().getNationalId());
	 * summonType1.setPassport(summonDetails.getSummonTypeDto().getPassport());
	 * summonType1.setIssueDate(summonDetails.getSummonTypeDto().getIssueDate());
	 * summonType1.setPlaceofIssues(summonDetails.getSummonTypeDto().
	 * getPlaceofIssues());
	 * 
	 * summonType1.setOffJoinDate(summonDetails.getSummonTypeDto().getOffJoinDate())
	 * ; summonType1.setOffEmail(summonDetails.getSummonTypeDto().getOffEmail());
	 * summonType1.setOffPhone(summonDetails.getSummonTypeDto().getOffPhone());
	 * summonType1.setOffAddress(summonDetails.getSummonTypeDto().getOffAddress());
	 * } } companySummon1=new
	 * CompanySummon(summonDetails.getCompanySummonsDto().getCin(),
	 * summonDetails.getCompanySummonsDto().getCompanyName(),
	 * summonDetails.getCompanySummonsDto().getAddress(),summonDetails.
	 * getCompanySummonsDto().getEmail());
	 * saveSummon.setCaseId(summonDetails.getCaseId());
	 * saveSummon.setUpdatedDate(new Date());
	 * saveSummon.setUserid(getUserDetails().getUserId());
	 * 
	 * boolean isCompExist =false; for (int i = 0; i
	 * <saveSummon.getCompanySummon().size(); i++) {
	 * if(saveSummon.getCompanySummon().get(i).getCompanyName().equalsIgnoreCase(
	 * summonDetails.getCompanySummonsDto().getCompanyName())) { isCompExist =true;
	 * saveSummon.getCompanySummon().get(i).setCin(summonDetails.
	 * getCompanySummonsDto().getCin());
	 * saveSummon.getCompanySummon().get(i).setAddress(summonDetails.
	 * getCompanySummonsDto().getAddress());
	 * saveSummon.getCompanySummon().get(i).setEmail(summonDetails.
	 * getCompanySummonsDto().getEmail());
	 * if(null!=summonDetails.getSummonTypeDto().getType() &&
	 * !"".equals(summonDetails.getSummonTypeDto().getType()) ){
	 * summonType1.setId(0);
	 * saveSummon.getCompanySummon().get(i).getSummonType().add(summonType1); }
	 * break; } } if(!isCompExist){ companySummon1.setId(0);
	 * if(null!=summonDetails.getSummonTypeDto().getType() &&
	 * !"".equals(summonDetails.getSummonTypeDto().getType()) ){
	 * companySummon1.getSummonType().add(summonType1); }
	 * saveSummon.getCompanySummon().add(companySummon1); } //
	 * modelMap.addAttribute("compcin", companySummon1.getCin()); //
	 * modelMap.addAttribute("compAddr", companySummon1.getAddress());
	 * addNoticeSummonRepository.save(saveSummon); } //firstTime occur only else{
	 * saveSummon=new SummonDetails();
	 * saveSummon.setCaseId(summonDetails.getCaseId());
	 * saveSummon.setCreatedDate(new Date());
	 * saveSummon.setUserid(getUserDetails().getUserId());
	 * 
	 * BeanUtils.copyProperties(summonDetails.getCompanySummonsDto(),companySummon);
	 * /*BeanUtils.copyProperties(summonDetails.getSummonTypeDto(),summonType);
	 * if(summonDetails.getSummonTypeDto().getType().equals("auditor".trim())) {
	 * summonType1=new SummonType(summonDetails.getSummonTypeDto().getType(),
	 * summonDetails.getSummonTypeDto().getRegNo(),
	 * summonDetails.getSummonTypeDto().getAuditorName(),
	 * summonDetails.getSummonTypeDto().getEmail(),
	 * summonDetails.getSummonTypeDto().getPhone(),
	 * summonDetails.getSummonTypeDto().getAddress()); } else
	 * if(summonDetails.getSummonTypeDto().getType().equals("vendor".trim())){
	 * summonType1=new SummonType();
	 * summonType1.setType(summonDetails.getSummonTypeDto().getType());
	 * summonType1.setVendorName(summonDetails.getSummonTypeDto().getVendorName());
	 * summonType1.setVendorRelation(summonDetails.getSummonTypeDto().
	 * getVendorRelation());
	 * summonType1.setVendorEmail(summonDetails.getSummonTypeDto().getVendorEmail())
	 * ;
	 * summonType1.setVendorPhone(summonDetails.getSummonTypeDto().getVendorPhone())
	 * ; summonType1.setVendorAddress(summonDetails.getSummonTypeDto().
	 * getVendorAddress());
	 * 
	 * } else { summonType1=new SummonType();
	 * summonType1.setType(summonDetails.getSummonTypeDto().getType());
	 * if(summonDetails.getSummonTypeDto().getIsdirector().equalsIgnoreCase("Y".trim
	 * ())) {
	 * summonType1.setIsdirector(summonDetails.getSummonTypeDto().getIsdirector());
	 * summonType1.setDin(summonDetails.getSummonTypeDto().getDin());
	 * summonType1.setDirName(summonDetails.getSummonTypeDto().getDirName());
	 * summonType1.setOffJoinDate(summonDetails.getSummonTypeDto().getOffJoinDate())
	 * ; summonType1.setOffEmail(summonDetails.getSummonTypeDto().getOffEmail());
	 * summonType1.setOffPhone(summonDetails.getSummonTypeDto().getOffPhone());
	 * summonType1.setOffAddress(summonDetails.getSummonTypeDto().getOffAddress());
	 * } else {
	 * summonType1.setIsdirector(summonDetails.getSummonTypeDto().getIsdirector());
	 * summonType1.setName(summonDetails.getSummonTypeDto().getName());
	 * summonType1.setDesignation(summonDetails.getSummonTypeDto().getDesignation())
	 * ; summonType1.setSex(summonDetails.getSummonTypeDto().getSex());
	 * summonType1.setDob(summonDetails.getSummonTypeDto().getDob());
	 * summonType1.setNationality(summonDetails.getSummonTypeDto().getNationality())
	 * ;
	 * summonType1.setNationalId(summonDetails.getSummonTypeDto().getNationalId());
	 * summonType1.setPassport(summonDetails.getSummonTypeDto().getPassport());
	 * summonType1.setIssueDate(summonDetails.getSummonTypeDto().getIssueDate());
	 * summonType1.setPlaceofIssues(summonDetails.getSummonTypeDto().
	 * getPlaceofIssues());
	 * 
	 * summonType1.setOffJoinDate(summonDetails.getSummonTypeDto().getOffJoinDate())
	 * ; summonType1.setOffEmail(summonDetails.getSummonTypeDto().getOffEmail());
	 * summonType1.setOffPhone(summonDetails.getSummonTypeDto().getOffPhone());
	 * summonType1.setOffAddress(summonDetails.getSummonTypeDto().getOffAddress());
	 * } } companySummon.getSummonType().add(summonType1);
	 * saveSummon.getCompanySummon().add(companySummon);
	 * addNoticeSummonRepository.save(saveSummon); }
	 * 
	 * SummonDetails summonDetails1=new SummonDetails(); SummonDetails
	 * findByCaseId=summonDao.findByCaseId(saveSummon.getCaseId());
	 * if(findByCaseId!= null){ summonDetails1.setId(findByCaseId.getId());
	 * modelMap.addAttribute("summonbycaseid", findByCaseId);
	 * modelMap.addAttribute("summonbycaseidCompanyList",
	 * findByCaseId.getCompanySummon()); }
	 * summonDetails1.setCaseId(saveSummon.getCaseId());
	 * summonDetails1.getCompanySummonsDto().setCin(companySummon1.getCin());
	 * summonDetails1.getCompanySummonsDto().setAddress(companySummon1.getAddress())
	 * ; summonDetails1.getCompanySummonsDto().setCompanyName(companySummon1.
	 * getCompanyName());
	 * summonDetails1.getCompanySummonsDto().setEmail(companySummon1.getEmail());
	 * modelMap.addAttribute("summonDetails",summonDetails1);
	 * 
	 * return "notice/CreateNotice"; }
	 * 
	 * 
	 * 
	 */

	public InspectorListDTO getALLCompany(Long caseId) {

		List<Company> comList = userMangCustom.findByAllCompByCaseId(new CaseDetails(caseId));
		List<Company> listCom = new ArrayList<Company>();
		Long k = 1L;
		for (Company com : comList) {
			Company c = new Company();
			c.setId(k);
			c.setName(com.getName());
			c.setCompId(com.getId());
			listCom.add(c);
			k++;
		}

		return new InspectorListDTO(listCom);
	}

	@RequestMapping(value = "editCompany", method = RequestMethod.POST)
	public String editCompany(@ModelAttribute("summonDetails") SummonDetails summonDetails, BindingResult result,
			ModelMap modelMap, @RequestParam("editDeleteId") int editDeleteId) throws Exception {
		commonObjectSummon(modelMap, summonDetails.getCaseId());

		SummonDetails findByCaseId = summonDao.findByCaseId(summonDetails.getCaseId());
		CompanySummon compSummon = new CompanySummon();
		for (int i = 0; i < findByCaseId.getCompanySummon().size(); i++) {
			if (findByCaseId.getCompanySummon().get(i).getId() == editDeleteId) {
				compSummon = findByCaseId.getCompanySummon().get(i);
				findByCaseId.getCompanySummon().remove(i);
				break;
			}
		}
		if (findByCaseId != null) {
			findByCaseId.setCaseId(summonDetails.getCaseId());
			findByCaseId.getCompanySummonsDto().setCin(compSummon.getCin());
			findByCaseId.getCompanySummonsDto().setAddress(compSummon.getAddress());
			findByCaseId.getCompanySummonsDto().setCompanyName(compSummon.getCompanyName());
			findByCaseId.getCompanySummonsDto().setEmail(compSummon.getEmail());
			summonDetails.setId(findByCaseId.getId());
			modelMap.addAttribute("summonbycaseid", findByCaseId);
			modelMap.addAttribute("summonbycaseidCompanyList", findByCaseId.getCompanySummon());
		}
		List<IndividualType> individualType = idividualRepo.findAll();

		modelMap.addAttribute("individualTlist", individualType);
		modelMap.addAttribute("summonDetails", findByCaseId);
		return "notice/CreateNotice";
	}

	@RequestMapping(value = "deleteCompany", method = RequestMethod.POST)
	public String deleteCompany(@ModelAttribute("summonDetails") SummonDetails summonDetails, BindingResult result,
			ModelMap modelMap, @RequestParam("editDeleteId") int editDeleteId) throws Exception {

		if (summonDetails.equals(0)) {
			return "notice/CreateNotice";
		}
		commonObjectSummon(modelMap, summonDetails.getCaseId());
		SummonDetails findByCaseId = summonDao.findByCaseId(summonDetails.getCaseId());
		CompanySummon compSummon = new CompanySummon();
		for (int i = 0; i < findByCaseId.getCompanySummon().size(); i++) {
			if (findByCaseId.getCompanySummon().get(i).getId() == editDeleteId) {
				compSummon = findByCaseId.getCompanySummon().get(i);
				findByCaseId.getCompanySummon().remove(i);
				break;
			}
		}
		addNoticeSummonRepository.save(findByCaseId);
		List<IndividualType> individualType = idividualRepo.findAll();

		findByCaseId.setCaseId(summonDetails.getCaseId());
		findByCaseId.getCompanySummonsDto().setCin(compSummon.getCin());
		findByCaseId.getCompanySummonsDto().setAddress(compSummon.getAddress());
		findByCaseId.getCompanySummonsDto().setCompanyName(compSummon.getCompanyName());
		summonDetails.setId(findByCaseId.getId());
		modelMap.addAttribute("summonbycaseid", findByCaseId);
		modelMap.addAttribute("summonbycaseidCompanyList", findByCaseId.getCompanySummon());
		modelMap.addAttribute("summonDetails", summonDetails);
		modelMap.addAttribute("individualTlist", individualType);
		return "notice/CreateNotice";
	}

	@RequestMapping(value = "deleteType", method = RequestMethod.POST)
	public @ResponseBody String deleteType(@RequestParam("editDeleteId") int editDeleteId) throws Exception {
		if (editDeleteId != 0)
			summonDao.deleteSummonType(editDeleteId);
		return "";
	}

	@RequestMapping(value = "getCompanyDetail", method = RequestMethod.POST)
	public @ResponseBody Object[] getCompanyDetail(@RequestParam("companyName") String companyName) throws Exception {
		companyName = companyName.replaceAll("@and@", "&");
		Object[] companyList = {};
		if (null != companyName && !"".equals(companyName))
			companyList = summonDao.getCompanyByName(companyName);

		return companyList;
	}

	public void commonObjectSummon(ModelMap modelMap, Long caseId) {

		SNMSValidator snmsvalid = new SNMSValidator();
		if (!snmsvalid.getValidInteger(caseId)) {
			String valid = "invalid case id";

		}

		Optional<CaseDetails> details = caseDetailsRepository.findById(caseId);
		CaseDetails caseDetails = details.get();
		OfficeOrder officeorder = officeOrderRepo.findAllByCaseDetails(caseDetails);
		if (officeorder != null) {
			modelMap.addAttribute("officeorder", officeorder);
		}

		modelMap.addAttribute("caseDetails", caseDetails);

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
//		inspList.removeIf( insp -> insp.isAdo()==true ); // Check not to add Additional director
		modelMap.addAttribute("inspList", inspList);
		List<Company> comList = userMangCustom.findByCaseId(new CaseDetails(caseDetails.getId()));
		modelMap.addAttribute("comList", comList);
		// Added By gouthami
		List<IndividualType> individualType = idividualRepo.findAll();
		modelMap.addAttribute("indType", individualType);

	}

	/*
	 * public void commonObjectSummon(ModelMap modelMap,Long caseId){
	 * Optional<CaseDetails> details = caseDetailsRepository.findById(caseId);
	 * CaseDetails caseDetails = details.get(); modelMap.addAttribute("caseDetails",
	 * caseDetails);
	 * 
	 * List<Object[]> inspectorList =
	 * userMangCustom.findByCase(caseDetails.getId()); int i = 1; List<InspectorDTO>
	 * inspList = new ArrayList<InspectorDTO>(); for (Object[] dto : inspectorList)
	 * { InspectorDTO ins = new InspectorDTO(i, dto[0].toString() + " " +
	 * dto[1].toString() + " (" + dto[2].toString() + ")", (Boolean) dto[3]);
	 * inspList.add(ins); i++; } modelMap.addAttribute("inspList", inspList);
	 * List<Company> comList = userMangCustom.findByCaseId(new
	 * CaseDetails(caseDetails.getId())); modelMap.addAttribute("comList", comList);
	 * }
	 */
	@RequestMapping(value = "/getIcaiByFirmNo", method = RequestMethod.GET)
	public @ResponseBody ICAI getIcaiByFirmReg(@RequestParam("firmNo") String firmNo) throws Exception {

		ICAI icaiData = ajaxDao.findByRegNo(firmNo.toUpperCase());
		return icaiData;
	}

	@GetMapping("getCompleteCase")
	public String completeData(ModelMap modelMap) throws Exception {
		List<Object[]> findListByUserId = summonDao.findListByUserId(getUserDetails().getUserId());
		List<PendingForApprovalDTO> caselist = new ArrayList<PendingForApprovalDTO>();
		for (Object[] dto : findListByUserId) {
			PendingForApprovalDTO pes = new PendingForApprovalDTO();
			pes.setCaseId(Long.parseLong(dto[0].toString()));
			pes.setCaseStrId(dto[1].toString());
			pes.setCaseTitle(dto[2].toString());
			caselist.add(pes);
		}
		modelMap.addAttribute("caseList", caselist);
		return "notice/showSummonDetails";
	}

	@RequestMapping(value = "getCompleteCaseById", method = RequestMethod.POST)
	public String getCompleteCase(@RequestParam("id") Long id, ModelMap modelMap) throws Exception {

		// gouthami 15/09/2020
		SNMSValidator snmsValid = new SNMSValidator();

		if (!snmsValid.getValidInteger(id)) {
			return "redirect:/getCompleteCase";

		}
		commonObjectSummon(modelMap, id);

		boolean offline = false;
		modelMap.addAttribute("id", id);
		modelMap.addAttribute("offline", offline);
		return "notice/completeCaseById";
	}

	@RequestMapping(value = "/getCompByType", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody List<Object[]> getCompByType(@RequestParam(name = "id", required = true) Long id,
			@RequestParam(name = "comp", required = true) String company,
			@RequestParam(name = "type", required = true) int type) throws Exception {
		company = company.replaceAll("@and@", "&");
		List<Object[]> list = summonDao.findListByIdCompanyTypeNew(id, company, type);

		return list;
	}

	@RequestMapping(value = "/genNoticeAs")
	public String getNotice(@RequestParam(name = "caseId", required = false) Long id,
			@RequestParam(name = "chooseComapny", required = false) String company,
			@RequestParam(name = "type", required = false) String type,
			@RequestParam(name = "individualType", required = false) String individualtype,
			@RequestParam(name = "summontypeid", required = false, defaultValue = "0") int summontypeid,
			@RequestParam(name = "dateOfAppear", required = false) String dateOfAppear,
			@RequestParam(name = "summonNo", required = false) String summonNo, ModelMap model) throws Exception {
		SNMSValidator snmsValid = new SNMSValidator();

		if (id == null) {
			return "redirect:/getCompleteCase";
		}
		if (!snmsValid.getValidZeroInteger(summontypeid)) {
			return "redirect:/getCompleteCase";
		}
		if (!(summonNo.equals("SFIO/INV/2022/") || summonNo.equals("SFIO/INV/2021/"))) {
			return "redirect:/getCompleteCase";
		}
		if (snmsValid.getValidInteger(id) || snmsValid.getValidZeroInteger(summontypeid)
				|| snmsValid.validateDateFormat(dateOfAppear) || snmsValid.getvalidCompany(company, false)) {
			if (validateGenerateNoticeSummon(id, company, type, summontypeid, dateOfAppear, summonNo, individualtype,
					model)) {

				commonObjectSummon(model, id);
				boolean offline = false;

				model.addAttribute("id", id);
				model.addAttribute("offline", offline);
				return "notice/completeCaseById";
			}
		} else {
			return "redirect:/getCompleteCase";
		}

		Optional<CaseDetails> caseD = caseDetailsRepository.findById(id);
		CaseDetails caseDetails = caseD.get();

		SummonType summonType = SummonTypeNewDetails.findById(summontypeid).get();

		InitiateNoticeDraft initiateNoticeDraft = (summonDao.findNoticeDraftByCaseDetailsNew(caseDetails,
				summonType) == null) ? new InitiateNoticeDraft()
						: summonDao.findNoticeDraftByCaseDetailsNew(caseDetails, summonType);

		String OfficeAddress = caseDetails.getUnit().getAddress();
		String OfficeAddress_hi = caseDetails.getUnit().getAddress_hi();
//		Long snid = (noticeRepo.findMaxId() != null) ? (noticeRepo.findMaxId()+1):1;

		Long snid = noticeRepo.findMaxId();

		String caseNo = "No : " + caseDetails.getCaseId() + "/" + snid.toString();

		if (summonNo.charAt(summonNo.length() - 1) == '/')
			summonNo = summonNo + snid;
		else
			summonNo = summonNo + "/" + snid;

		String date = " Date :" + new Utils().currentDate();
		InspectorListDTO inspList = getInspectorListAndCompany(caseDetails.getId());
		// String io=inspList.getIoName();

		// Gouthami 07/01/2021
		AppUser userDetails = userDetailsService.getUserDetails();
		Inspector inspectList = inspRepo.findAllByCaseDetailsAndAppUser(caseDetails, userDetails);
		String Designation;
		String DesignationHi = null;
		if (inspectList.getIsAdo() == true) {
			// Designation = "Addl";
			Designation = "Addl.Director";
			DesignationHi="अपर निदेशक";
		} else if (inspectList.getIsIO() == true) {
			Designation = "Investigating Officer";
			DesignationHi="जांच अधिकारी";
		} else {
			DesignationHi="निरीक्षक";
			Designation = "Inspector";
		}
		UserDetails user = userDetailsRepo.findById(userDetails.getUserId()).get();
		String io = userDetailsService.getFullName(user);
		int otherSize = inspList.getCompanyList().size() - 1;
		model.addAttribute("listCom", inspList.getCompanyList());

		/*
		 * String companydisplay=""; if (inspList.getCompanyList().size()==1)
		 * companydisplay= inspList.getCompanyList().get(0).getName(); else
		 * companydisplay= inspList.getCompanyList().get(0).getName()+ " and Others.";
		 */

		// String companyAdd="";
		// gouthami 15/09/2020
		// SummonDetails summonDetails=summonDao.findByCaseId(id);

		String name = "";
		String designation = "";
		String address = "";
		String email = "";
		String noticeCompany = "";

		name = summonType.getName();

		if (summonType.getDesignation() != null) {
			designation = summonType.getDesignation();
		} else {
			designation = " ";
		}
		long Type = summonType.getIndividualType().getIndividualId();
		address = summonType.getAddress();
		email = summonType.getEmail();
		if (summonType.getIndividualType().getIndividualId() == 5
				|| summonType.getIndividualType().getIndividualId() == 6) {
			noticeCompany = summonType.getNameCompany();
		} else if (summonType.getIndividualType().getIndividualId() == 7) {
			noticeCompany = "";
		}

		else {
			noticeCompany = company;
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
		 * designation="Vendor/Other"; name=summonType.getVendorName();
		 * address=summonType.getVendorAddress(); email=summonType.getVendorEmail();
		 * }else if(summonType.getType().equalsIgnoreCase("secretary")) {
		 * designation="Company Secretary"; name=summonType.getMemberName();
		 * address=summonType.getMemberAddress(); email=summonType.getMemberEmail(); }
		 * else { designation="Auditor"; name=summonType.getAuditorName();
		 * address=summonType.getAddress(); email=summonType.getEmail(); }
		 */
		String comapnyPara = "";
		String comapnyPara_hi = "";
		if (otherSize == 0) {
			comapnyPara = company;
		    comapnyPara_hi   = company   ;
		}
		else {
			comapnyPara = company + " and " + otherSize + " other company";
		comapnyPara_hi  = company + " तथा " + otherSize + "अन्य कंपनी";
		}

		// gouthami 15/09/2020
		/*
		 * for(int i=0;i<summonDetails.getCompanySummon().size();i++) {
		 * if(summonDetails.getCompanySummon().get(i).getCompanyName().equals(company))
		 * companyAdd=summonDetails.getCompanySummon().get(i).getAddress();
		 * 
		 * }
		 */
		if (summonDao.findNoticeDraftByCaseDetailsNew(caseDetails, summonType) != null) {
			NoticeTempDto caseDto = new NoticeTempDto(caseNo, date, initiateNoticeDraft.getPara1(),
					initiateNoticeDraft.getPara2(), initiateNoticeDraft.getPara3(), initiateNoticeDraft.getPara4(),
					initiateNoticeDraft.getPara5(), 
					initiateNoticeDraft.getPara1h(),initiateNoticeDraft.getPara2h(), initiateNoticeDraft.getPara3h(), initiateNoticeDraft.getPara4h(),
					initiateNoticeDraft.getPara5h(),
					1L, caseDetails.getCaseId(), caseDetails.getId(), io, summonNo,
					dateOfAppear, summontypeid, "", Designation,DesignationHi);
//			caseDto.setNoticeDin(noticeDIN);
			model.addAttribute("caseDto", caseDto);
		} else {

			initiateNoticeDraft.setPara1(NoticeConstant.PARA_1 + comapnyPara + NoticeConstant.PARA_1_1);
            initiateNoticeDraft.setPara1h(NoticeConstant.PARA_1_h + comapnyPara_hi + NoticeConstant.PARA_1_1_h);     
			initiateNoticeDraft.setPara2(NoticeConstant.PARA_2 + caseDetails.getMcaOrderNo() + " dated : "
					+ Utils.formatMcaOrderDate(caseDetails.getMcaOrderDate()) + NoticeConstant.PARA_2_1 + comapnyPara
					+ NoticeConstant.PARA_2_2);

			initiateNoticeDraft.setPara2h(NoticeConstant.PARA_2_h + caseDetails.getMcaOrderNo() + " dated : "
					+ Utils.formatMcaOrderDate(caseDetails.getMcaOrderDate()) + NoticeConstant.PARA_2_1_h + comapnyPara_hi
					+ NoticeConstant.PARA_2_2_h);
			
			initiateNoticeDraft.setPara3(NoticeConstant.PARA_3 + company + NoticeConstant.PARA_3_1 + company
					+ NoticeConstant.PARA_3_2 + comapnyPara + NoticeConstant.PARA_3_3);
			
			
			initiateNoticeDraft.setPara3h(NoticeConstant.PARA_3_h + " " + NoticeConstant.PARA_3_1_h + " "
					+ NoticeConstant.PARA_3_2_h + comapnyPara_hi + NoticeConstant.PARA_3_3_h);
			
			initiateNoticeDraft.setPara4(NoticeConstant.PARA_4 + dateOfAppear);
			initiateNoticeDraft.setPara4h(NoticeConstant.PARA_4_h + dateOfAppear+NoticeConstant.PARA_4_1_h);
			initiateNoticeDraft.setPara5(NoticeConstant.PARA_5);
			initiateNoticeDraft.setPara5h(NoticeConstant.PARA_5_h);
			NoticeTempDto caseDto = new NoticeTempDto(caseNo, date, initiateNoticeDraft.getPara1(),
					initiateNoticeDraft.getPara2(), initiateNoticeDraft.getPara3(), initiateNoticeDraft.getPara4(),
					initiateNoticeDraft.getPara5(), 
					initiateNoticeDraft.getPara1h(),initiateNoticeDraft.getPara2h(), initiateNoticeDraft.getPara3h(), initiateNoticeDraft.getPara4h(),
					initiateNoticeDraft.getPara5h(),
					1L, caseDetails.getCaseId(), caseDetails.getId(), io, summonNo,
					dateOfAppear, summontypeid, "", Designation,DesignationHi);
//			caseDto.setNoticeDin(noticeDIN);
			model.addAttribute("caseDto", caseDto);
		}

		List<Object[]> inspectorList = userMangCustom.getInspectorList(caseDetails.getId());
		boolean privilege = false;
		try {
			for (Object[] object : inspectorList) {

				if (getUserDetails().getUserId() == Integer.parseInt(object[0].toString()))
					privilege = true;
			}

		} catch (Exception e) {
			logger.info(e.getMessage());
		}

		PdfPreviewDto pdfPreviewDto = new PdfPreviewDto(id, company, type, summontypeid, dateOfAppear, "");
		model.addAttribute("officeadrress", getSplitedString(OfficeAddress));
		model.addAttribute("OfficeAddress_hi", getSplitedString(OfficeAddress_hi));
		
		model.addAttribute("privilege", privilege);
		if (pdfPreviewDto != null) {
			model.addAttribute("pdfPreview", pdfPreviewDto);
		}
		model.addAttribute("companyName", noticeCompany);
		model.addAttribute("name", name);
		model.addAttribute("designation", designation);
		model.addAttribute("address", getSplitedString(address));
		model.addAttribute("email", email);
		model.addAttribute("Type", Type);
		model.addAttribute("companydisplay", comapnyPara);
		return "notice/getNoticeAs";

	}

	public boolean validateGenerateNoticeSummon(Long id, String company, String type, int sumtypeId,
			String dateOfAppear, String summonNo, String individualtype, ModelMap model) {
		if (dateOfAppear == null || "".equals(dateOfAppear.trim())) {
			commonObjectSummon(model, id);
			model.addAttribute("id", id);
			model.addAttribute("dateofAppearErr", "Required Field");
			return true;
		} else if (company == null || "".equals(company.trim())) {
			commonObjectSummon(model, id);
			model.addAttribute("id", id);
			model.addAttribute("chooseComapnyErr", "Please choose company");
			return true;
		} else if (type == null || "".equals(type.trim())) {
			commonObjectSummon(model, id);
			model.addAttribute("id", id);
			model.addAttribute("typeErr", "Required Field");
			return true;
		} else if (individualtype == null || "".equals(individualtype.trim())
				|| "0".equalsIgnoreCase(individualtype.trim())) {
			commonObjectSummon(model, id);
			model.addAttribute("id", id);
			model.addAttribute("individualTypeErr", "Required Field");
			return true;
		}
		/*
		 * else if(!individualtype.equals("official".trim()) ||
		 * !individualtype.equals("vendor".trim()) ||
		 * !individualtype.equals("auditor".trim()) ||
		 * !individualtype.equals("secretary".trim())) { commonObjectSummon(model,id);
		 * model.addAttribute("id", id); model.addAttribute("individualTypeErr",
		 * "Invalid Individual Type"); return true; }
		 */
		else if (sumtypeId <= 0) {
			commonObjectSummon(model, id);
			model.addAttribute("id", id);
			model.addAttribute("summonTypeErr", "Required Field");
			return true;
		} else if (summonNo == null || "".equals(summonNo.trim())) {
			commonObjectSummon(model, id);
			model.addAttribute("id", id);
			model.addAttribute("summonNoErr", "Required Field");
			return true;
		}
		return false;
	}

	@RequestMapping(value = "/genSummonAs")
	public String genSummon(@RequestParam(name = "caseId", required = false) Long id,
			@RequestParam(name = "chooseComapny", required = false) String company,
			@RequestParam(name = "type", required = false) String type,
			@RequestParam(name = "summontypeid", required = false, defaultValue = "0") int sumtypeId,
			@RequestParam(name = "individualType", required = false) String individualtype,
			@RequestParam(name = "dateOfAppear", required = false) String dateOfAppear,
			@RequestParam(name = "summonNo", required = false) String summonNo, ModelMap model) throws Exception {

		SNMSValidator snmsValid = new SNMSValidator();
		if (id == null || id == 0) {
			// gouthami 15/09/2020

			if (!snmsValid.getValidInteger(id)) {
				return "redirect:/getCompleteCase";

			}
			;

		}
		if (!(summonNo.equals("SFIO/INV/2022/") || summonNo.equals("SFIO/INV/2021/"))) {
			return "redirect:/getCompleteCase";
		}

		/*
		 * if(!summonNo.contains("SFIO/INV/")) { return "redirect:/getCompleteCase"; }
		 */

		if (snmsValid.getValidInteger(id) || snmsValid.isvalidCompanyName(company)
				|| snmsValid.getValidZeroInteger(sumtypeId)) {
			if (validateGenerateNoticeSummon(id, company, type, sumtypeId, dateOfAppear, summonNo, individualtype,
					model)) {
				commonObjectSummon(model, id);
				boolean offline = false;
				if (id != null) {
					model.addAttribute("id", id);
				}
				model.addAttribute("offline", offline);
				return "notice/completeCaseById";
			}
		} else {
			return "redirect:/getCompleteCase";
		}
		Optional<CaseDetails> caseD = caseDetailsRepository.findById(id);
		CaseDetails caseDetails = caseD.get();
//	String OfficeAddress  =  caseDetails.getUnit().getAddress();

		UserDetails userDet = userDetailsRepo.findAllByUserId(getUserDetails());
		
		String OfficeAddress = userDet.getUnit().getAddress();

//		Long snid = (summonRepo.findMaxId() != null) ? (summonRepo.findMaxId()+1):1;
		Long snid = summonRepo.findMaxId();
		if (summonNo.charAt(summonNo.length() - 1) == '/')
			summonNo = summonNo + snid;
		else
			summonNo = summonNo + "/" + snid;

//		String caseNo = "No : " + caseDetails.getCaseId();
		if (type.equals("summon".trim())) {
			// SummonType summonType=summonTypeDetails.findById(sumtypeId).get();
			// by gouthami
			SummonType summonType = SummonTypeNewDetails.findById(sumtypeId).get();
			InitiateSummonDraft office = (summonDao.findSummonDraftByCaseDetails(caseDetails, summonType) == null)
					? new InitiateSummonDraft()
					: summonDao.findSummonDraftByCaseDetails(caseDetails, summonType);
			String caseNo = "No : " + caseDetails.getCaseId();
			String date = " Date :" + new Utils().currentDate();
//			summonNo = "SFIO/INV/2020/"+summonNo;
			InspectorListDTO inspList = getInspectorListAndCompany(caseDetails.getId());
			int otherSize = inspList.getCompanyList().size() - 1;
			String para1 = "";
			String para1_hi="";
			if (otherSize == 0) {
				para1 = company;
			para1_hi = company;
			}
			else
				para1 = company + " and " + otherSize + " other company";

			para1_hi = company + " तथा  " + otherSize + " अन्य कंपनी ";

			long Type = summonType.getIndividualType().getIndividualId();

			String name = summonType.getName();
			String designation = "";
			if (summonType.getDesignation() != null) {
				designation = summonType.getDesignation();
			} else {
				designation = " ";
			}

			String address = summonType.getAddress();
			String OfficeAddress_hi = userDet.getUnit().getAddress_hi();
			String sumCompany = "";
			if (summonType.getIndividualType().getIndividualId() == 5
					|| summonType.getIndividualType().getIndividualId() == 6) {
				sumCompany = summonType.getNameCompany();
			} else {
				sumCompany = company;
			}
			//

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
			// String ioName=inspList.getIoName();
			// Gouthami 07/01/2021
			AppUser userDetails = userDetailsService.getUserDetails();
			Inspector inspectorList = inspRepo.findAllByCaseDetailsAndAppUser(caseDetails, userDetails);
			String Designation;
			String DesignationHi;
			if (inspectorList.getIsAdo() == true) {
				// Designation = "Addl";
				Designation = "Addl.Director";
				
				DesignationHi="अपर निदेशक";
			} else if (inspectorList.getIsIO() == true) {
				Designation = "Investigating Officer";
				DesignationHi="जांच अधिकारी";
			} else {
				Designation = "Inspector";
				DesignationHi="निदेशक";
			}

			UserDetails user = userDetailsRepo.findById(userDetails.getUserId()).get();
			String ioName = userDetailsService.getFullName(user);
			if (summonDao.findSummonDraftByCaseDetails(caseDetails, summonType) != null) {
				SummonTempDto caseDto = new SummonTempDto(caseNo, date, office.getPara1(), office.getPara2(),
						office.getPara3(), office.getPara4(), office.getPara5(), office.getPara6(), office.getPara1_h(),
						office.getPara2_h(), office.getPara3_h(), office.getPara4_h(), office.getPara5_h(),
						office.getPara6_h(), 1L, caseDetails.getCaseId(), caseDetails.getId(), ioName, summonNo,
						dateOfAppear, "", sumtypeId, "", Designation,DesignationHi);
				model.addAttribute("caseDto", caseDto);
			} else {

				office.setPara1(SummonConstant.PARA_1 + caseDetails.getMcaOrderNo() + " dated "
						+ Utils.formatMcaOrderDate(caseDetails.getMcaOrderDate()) + SummonConstant.PARA_1_1 + para1);

				office.setPara2(SummonConstant.PARA_2 + String.valueOf(inspList.getInspctorList().size() - 1)
						+ " other inspectors");

				office.setPara3(SummonConstant.PARA_3 + "Sh." + name + ", " + designation + " of " + company
						+ SummonConstant.PARA_3_1 + OfficeAddress + SummonConstant.PARA_3_1_1 + dateOfAppear + " hrs."
						+ SummonConstant.PARA_3_2);

				office.setPara4(SummonConstant.PARA_4);
				office.setPara5(SummonConstant.PARA_5);

				office.setPara6(SummonConstant.PARA_6);

				office.setPara1_h(SummonConstant.PARA_1_h + caseDetails.getMcaOrderNo() + " dated "
						+ Utils.formatMcaOrderDate(caseDetails.getMcaOrderDate()) + SummonConstant.PARA_1_1_h + para1_hi
						+ SummonConstant.PARA_1_1_1_h);

				office.setPara2_h(SummonConstant.PARA_2_h + String.valueOf(inspList.getInspctorList().size() - 1) + " "
						+ SummonConstant.PARA_2_2_h);

				office.setPara3_h(SummonConstant.PARA_3_h + company + " " + SummonConstant.PARA_3_1_h + name + ", "
						+ designation + SummonConstant.PARA_3_2_h + OfficeAddress_hi + " " + dateOfAppear + " hrs."
						+ SummonConstant.PARA_3_3_h);

				office.setPara4_h(SummonConstant.PARA_4_h);
				office.setPara5_h(SummonConstant.PARA_5_h);

				office.setPara6_h(SummonConstant.PARA_6_h);
				SummonTempDto caseDto = new SummonTempDto(caseNo, date, office.getPara1(), office.getPara2(),
						office.getPara3(), office.getPara4(), office.getPara5(), office.getPara6(), office.getPara1_h(),
						office.getPara2_h(), office.getPara3_h(), office.getPara4_h(), office.getPara5_h(),
						office.getPara6_h(), 1L, caseDetails.getCaseId(), caseDetails.getId(), ioName, summonNo,
						dateOfAppear, "", sumtypeId, "", Designation,DesignationHi);
				model.addAttribute("caseDto", caseDto);
			}

			model.addAttribute("companyName", sumCompany);
			model.addAttribute("name", name);
			model.addAttribute("Type", Type);

			model.addAttribute("designation", designation);
			
			model.addAttribute("address", getSplitedString(address));
			model.addAttribute("OfficeAddress", getSplitedString(OfficeAddress));
			model.addAttribute("OfficeAddress_hi", getSplitedString(OfficeAddress_hi));
			model.addAttribute("companydisplay", para1);		
		}

		List<Object[]> inspectorList = userMangCustom.getInspectorList(caseDetails.getId());
		boolean privilege = false;
		try {
			for (Object[] object : inspectorList) {

				if (getUserDetails().getUserId() == Integer.parseInt(object[0].toString()))
					privilege = true;
			}

		} catch (Exception e) {
			logger.info(e.getMessage());
		}

		PdfPreviewDto pdfPreviewDto = new PdfPreviewDto(id, company, type, sumtypeId, dateOfAppear, "");
		model.addAttribute("privilege", privilege);
		model.addAttribute("pdfPreview", pdfPreviewDto);
		return "notice/genSummonAs";

	}

	@RequestMapping(value = "/saveSummonDraft", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<SummonTempDto> saveSummonDraft(HttpServletRequest request,
			@RequestBody SummonTempDto caseDetailsDto) throws Exception {
		initiatesummonrepo.save(new InitiateSummonDraft(new CaseDetails(caseDetailsDto.getCaseId()),
				caseDetailsDto.getPara1(), caseDetailsDto.getPara2(), caseDetailsDto.getPara3(),
				caseDetailsDto.getPara4(), caseDetailsDto.getPara5(), caseDetailsDto.getPara6(),
				caseDetailsDto.getPara1h(), caseDetailsDto.getPara2h(), caseDetailsDto.getPara3h(),
				caseDetailsDto.getPara4h(), caseDetailsDto.getPara5h(), caseDetailsDto.getPara6h(),
				false,
				getUserDetails(), new Date(), new SummonType(caseDetailsDto.getSummontypeid()),
				caseDetailsDto.getDateOfAppear(), caseDetailsDto.getSummonNo()));

		String loginUName = appUserDAO.findUserDetails(getUserDetails()).getFirstName() != null
				? appUserDAO.findUserDetails(getUserDetails()).getFirstName()
				: "" + appUserDAO.findUserDetails(getUserDetails()).getMiddleName() != null
						? appUserDAO.findUserDetails(getUserDetails()).getMiddleName()
						: "" + appUserDAO.findUserDetails(getUserDetails()).getLastName() != null
								? appUserDAO.findUserDetails(getUserDetails()).getLastName()
								: "";

		auditBeanBo.setAuditBean(Integer.parseInt(userDetailsService.getUserDetails().getUserId().toString()),
				loginUName, "User", Integer.parseInt(userDetailsService.getUserDetails().getUserId().toString()),
				utils.getMessage("log.SummonDraft.save"), utils.getMessage("log.SummonDraft.saved"), loginUName,
				"true");
		auditBeanBo.save();

		return ResponseEntity.ok(caseDetailsDto);

	}

	@RequestMapping(value = "/saveNoticeDraft", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<NoticeTempDto> saveNoticeDraft(HttpServletRequest request,
			@RequestBody NoticeTempDto caseDetailsDto) throws Exception {

		initiateNoticeDarftRepo.save(new InitiateNoticeDraft(new CaseDetails(caseDetailsDto.getCaseId()),
				caseDetailsDto.getPara1(), caseDetailsDto.getPara2(), caseDetailsDto.getPara3(),
				caseDetailsDto.getPara4(), caseDetailsDto.getPara5(), 
				caseDetailsDto.getPara1h(), caseDetailsDto.getPara2h(), caseDetailsDto.getPara3h(),
				caseDetailsDto.getPara4h(), caseDetailsDto.getPara5h(),
				false, getUserDetails(), new Date(),
				new SummonType(caseDetailsDto.getSummontypeid()), caseDetailsDto.getDateOfAppear(),
				caseDetailsDto.getSummonNo()));
		String loginUName = appUserDAO.findUserDetails(getUserDetails()).getFirstName() != null
				? appUserDAO.findUserDetails(getUserDetails()).getFirstName()
				: "" + appUserDAO.findUserDetails(getUserDetails()).getMiddleName() != null
						? appUserDAO.findUserDetails(getUserDetails()).getMiddleName()
						: "" + appUserDAO.findUserDetails(getUserDetails()).getLastName() != null
								? appUserDAO.findUserDetails(getUserDetails()).getLastName()
								: "";

		auditBeanBo.setAuditBean(Integer.parseInt(userDetailsService.getUserDetails().getUserId().toString()),
				loginUName, "User", Integer.parseInt(userDetailsService.getUserDetails().getUserId().toString()),
				utils.getMessage("log.NoticeDraft.save"), utils.getMessage("log.NoticeDraft.saved"), loginUName,
				"true");
		auditBeanBo.save();
		return ResponseEntity.ok(caseDetailsDto);
	}

	@RequestMapping(value = "/genSummonPdf", produces = MediaType.APPLICATION_PDF_VALUE)
	public ResponseEntity<InputStreamResource> generatesummonpdf(
			@RequestParam(name = "caseId", required = true) Long id,
			@RequestParam(name = "chooseComapny", required = true) String company,
			@RequestParam(name = "summontypeid") int sumtypeId,
			@RequestParam(name = "dateOfAppear") String dateOfAppear, @RequestParam(name = "summonNo") String summonNo,
			Model model) throws Exception {

		SNMSValidator snmsValid = new SNMSValidator();

		if (!snmsValid.getValidInteger(sumtypeId)) {
			model.addAttribute("message", "Invalid Input");
			return null;
		}
		if (!snmsValid.getValidInteger(id)) {
			model.addAttribute("message", "Invalid Id ");
			return null;
		}
		if (company != "") {
			if (!snmsValid.getvalidCompany(company, true)) {
				model.addAttribute("message", "Invalid Id ");
				return null;
			}
		}
		/*
		 * if(!snmsValid.validateDateFormat(dateOfAppear)) {
		 * model.addAttribute("message", "Invalid Date"); return null; }
		 */

		Optional<CaseDetails> caseD = caseDetailsRepository.findById(id);
		CaseDetails caseDetails = caseD.get();

		String OfficeAddress = caseDetails.getUnit().getAddress();

		// SummonType summonType=summonTypeDetails.findById(sumtypeId).get();

		SummonType summonType = SummonTypeNewDetails.findById(sumtypeId).get();
		String name = summonType.getName();
		String designation = "";
		String address = summonType.getAddress();

		if (summonType.getDesignation() != null) {
			designation = summonType.getDesignation();
		} else {
			designation = "";
		}

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
		 * 
		 */
		InspectorListDTO inspList = getInspectorListAndCompany(caseDetails.getId());

		// SummonStatus summon = summonRepo.findAllBySummonNo(summonNo);

		// String ioName=inspList.getIoName();

		// Gouthami 07/02/2021
		Inspector inspectorList = null;
		String ioName;
		if (summonNo != "") {
			SummonStatus summon = summonRepo.findAllBySummonNo(summonNo);
			inspectorList = inspRepo.findAllByCaseDetailsAndAppUser(caseDetails, summon.getAppUser());

			UserDetails user = userDetailsRepo.findById(summon.getAppUser().getUserId()).get();
			ioName = userDetailsService.getFullName(user);

		} else {
			inspectorList = inspRepo.findAllByCaseDetailsAndAppUser(caseDetails, userDetailsService.getUserDetails());

			UserDetails user = userDetailsRepo.findById(userDetailsService.getUserDetails().getUserId()).get();
			ioName = userDetailsService.getFullName(user);

		}
		String Designation;
		if (inspectorList.getIsAdo() == true) {
			// Designation = "Addl";
			Designation = "Addl.Director";

		} else if (inspectorList.getIsIO() == true) {
			Designation = "Investigating Officer";
		} else {
			Designation = "Inspector";
		}

		InitiateSummonDraft summonDraft = summonDao.findSummonDraftByCaseDetails(caseDetails, summonType);
		List<String> companyNames = new ArrayList<String>();

		List<Company> comList = inspList.getCompanyList();
		int otherSize = inspList.getCompanyList().size() - 1;
		for (Company com : comList) {
			companyNames.add(com.getName());
		}

		String comapnyPara = "";
		if (otherSize == 0)
			comapnyPara = company;
		else
			comapnyPara = company + " and " + otherSize + " other company";

		String noticeCompany = "";
		if (summonType.getIndividualType().getIndividualId() == 5
				|| summonType.getIndividualType().getIndividualId() == 6) {
			noticeCompany = summonType.getNameCompany();
		} else if (summonType.getIndividualType().getIndividualId() == 7) {
			noticeCompany = "";
		}

		else {
			noticeCompany = company;
		}
		int remainIoSize = inspList.getInspctorList().size() - 1;
		if (summonDraft == null) {
			ByteArrayInputStream bis = SummonPDF.summonFixed(new SummonDto(
					Utils.formatMcaOrderDate(caseDetails.getMcaOrderDate()), summonNo, ioName, noticeCompany,
					getSplitedString(address), designation, name, dateOfAppear, otherSize, remainIoSize,
					caseDetails.getMcaOrderNo(), getSplitedString(OfficeAddress), comapnyPara, Designation));
			HttpHeaders headers = new HttpHeaders();
			headers.add("Content-Disposition", "inline; filename=notices.pdf");

			return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF)
					.body(new InputStreamResource(bis));
		} else {
			ByteArrayInputStream bis = SummonPDF.summonSaved(new SummonDto(
					new Utils().getCurrentDateFormat(caseDetails.getMcaOrderDate()), summonNo, ioName, noticeCompany,
					getSplitedString(address), designation, name, summonDraft.getPara1(), summonDraft.getPara2(),
					summonDraft.getPara3(), summonDraft.getPara4(), summonDraft.getPara5(), summonDraft.getPara6(),
					getSplitedString(OfficeAddress), comapnyPara, Designation));
			HttpHeaders headers = new HttpHeaders();
			headers.add("Content-Disposition", "inline; filename=summons.pdf");

			return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF)
					.body(new InputStreamResource(bis));
		}
	}

	@SuppressWarnings("unused")
	@RequestMapping(value = "/genNoticePdf", produces = MediaType.APPLICATION_PDF_VALUE)
	public ResponseEntity<InputStreamResource> genNotice(@RequestParam(name = "caseId", required = true) Long id,
			@RequestParam(name = "chooseComapny", required = true) String company,
			@RequestParam(name = "type", required = true) String type,
			@RequestParam(name = "summontypeid") int sumtypeId,
			@RequestParam(name = "dateOfAppear") String dateOfAppear, @RequestParam(name = "summonNo") String summonNo,
			Model model) throws Exception {

		SNMSValidator snmsValid = new SNMSValidator();
		if (!snmsValid.getValidInteger(id)) {
			model.addAttribute("message", "Invalid Id ");
			return null;
		}
		if (!snmsValid.getValidInteger(sumtypeId)) {
			model.addAttribute("message", "Invalid Id ");
			return null;
		}

		/*
		 * if(!snmsValid.validateDateFormat(dateOfAppear)) { return null; }
		 */
		Optional<CaseDetails> caseD = caseDetailsRepository.findById(id);
		CaseDetails caseDetails = caseD.get();
		// gouthami 15/09/2020
		// String caseNo = "No : " + caseDetails.getCaseId();

		String OfficeAddress = caseDetails.getUnit().getAddress();
		/*
		 * String companyAdd=""; SummonDetails summonDetails=summonDao.findByCaseId(id);
		 * for(int i=0;i<summonDetails.getCompanySummon().size();i++) {
		 * if(summonDetails.getCompanySummon().get(i).getCompanyName().equals(company))
		 * companyAdd=summonDetails.getCompanySummon().get(i).getAddress();
		 * 
		 * }
		 */
		// SummonType summonType=summonTypeDetails.findById(sumtypeId).get();

		// added BY gouthami
		SummonType summonType = SummonTypeNewDetails.findById(sumtypeId).get();
		String name = summonType.getName();
		String designation = summonType.getDesignation();
		String address = summonType.getAddress();
		String email = summonType.getEmail();

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
		InspectorListDTO inspList = getInspectorListAndCompany(caseDetails.getId());
		// String ioName =inspList.getIoName();
		int otherSize = inspList.getCompanyList().size() - 1;
		List<String> companyNames = new ArrayList<String>();
		List<Company> comList = inspList.getCompanyList();
		for (Company com : comList) {
			companyNames.add(com.getName());
		}
		String comapnyPara = "";
		if (otherSize == 0)
			comapnyPara = company;
		else
			comapnyPara = company + " and " + otherSize + " other company";
		String noticeCompany = "";
		if (summonType.getIndividualType().getIndividualId() == 5
				|| summonType.getIndividualType().getIndividualId() == 6) {
			noticeCompany = summonType.getNameCompany();
		} else if (summonType.getIndividualType().getIndividualId() == 7) {
			noticeCompany = "";
		} else {
			noticeCompany = company;
		}

		// gouthami 07/01/2021

		Inspector inspectorList = null;
		String ioName;
		if (summonNo != "") {
			NoticeStatus notice = noticeRepo.findAllBySummonNo(summonNo);
			inspectorList = inspRepo.findAllByCaseDetailsAndAppUser(caseDetails, notice.getAppUser());
			UserDetails user = userDetailsRepo.findById(notice.getAppUser().getUserId()).get();
			ioName = userDetailsService.getFullName(user);

		} else {
			inspectorList = inspRepo.findAllByCaseDetailsAndAppUser(caseDetails, userDetailsService.getUserDetails());

			UserDetails user = userDetailsRepo.findById(userDetailsService.getUserDetails().getUserId()).get();
			ioName = userDetailsService.getFullName(user);

		}
		String Designation;
		if (inspectorList.getIsAdo() == true) {
			// Designation = "Addl";
			Designation = "Addl.Director";

		} else if (inspectorList.getIsIO() == true) {
			Designation = "Investigating Officer";
		} else {
			Designation = "Inspector";
		}

		InitiateNoticeDraft initiateNoticeDraft = summonDao.findNoticeDraftByCaseDetails(caseDetails, summonType);
		NoticeTempDto caseDto = new NoticeTempDto();
		ByteArrayInputStream bis = null;
		if (initiateNoticeDraft != null) {
			caseDto = new NoticeTempDto(summonNo, new Utils().getCurrentDateFormat(caseDetails.getMcaOrderDate()),
					initiateNoticeDraft.getPara1(), initiateNoticeDraft.getPara2(), initiateNoticeDraft.getPara3(),
					initiateNoticeDraft.getPara4(), initiateNoticeDraft.getPara5(), ioName, comapnyPara, noticeCompany,
					address, designation, name, email, OfficeAddress, Designation);
			bis = NoticePdf.noticeSaved(caseDto);
		} else {
			bis = NoticePdf.noticeFixed(new NoticeDto(new Utils().getCurrentDateFormat(caseDetails.getMcaOrderDate()),
					summonNo, ioName, companyNames, noticeCompany, address, dateOfAppear, otherSize, name, designation,
					comapnyPara, OfficeAddress, Designation));
			/*
			 * HttpHeaders headers = new HttpHeaders(); headers.add("Content-Disposition",
			 * "inline; filename=notices.pdf");
			 * 
			 * return
			 * ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF)
			 * .body(new InputStreamResource(bis));
			 */
		}

		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Disposition", "inline; filename=notices.pdf");

		return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF)
				.body(new InputStreamResource(bis));

	}

	private String getSplitedString(String address) {
		
		if(address!=null) {
		String[] splitStr = address.split(" ");
		StringBuilder consStr = new StringBuilder();
		for (int i = 0; i < splitStr.length; i++) {
			consStr.append(splitStr[i]).append(" ");
			if (i % 5 == 0 && i != 0)
				consStr.append("\r\n");
		}
		return consStr.toString();
		}else {
			return address;
		}
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
			c.setSection(com.getSection());
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
	@RequestMapping(value = "getCompanyByCin", method = RequestMethod.POST)
	public @ResponseBody Object[] getCompanyByCin(@RequestParam("cinNum") String cinNum) throws Exception {
		Object[] companyList = {};
		if (null != cinNum && !"".equals(cinNum))
			companyList = summonDao.getCompanyByCin(cinNum);
		return companyList;
	}

	@RequestMapping(value = "getIcaiDataByMemberNo", method = RequestMethod.POST)
	public @ResponseBody Object[] getIcaiDataByMemberNo(@RequestParam("memberNo") String memberNo) throws Exception {
		Object[] companyList = {};
		if (null != memberNo && !"".equals(memberNo))
			companyList = summonDao.getIcaiDataByMemberNo(memberNo);
		return companyList;
	}
	/*
	 * @RequestMapping(value = "/noticeStatus") public String noticeStatus(Model
	 * model) throws Exception {
	 * 
	 * List<NoticeStatus> notice = summonDao.findNoticeByUserId(getUserDetails());
	 * 
	 * // notice.get(0).getSummonType().getIndividualType().getIndividualId();
	 * model.addAttribute("notice",notice); List<SummonStatus> summon = new
	 * ArrayList<SummonStatus>(); model.addAttribute("summon",summon);
	 * List<OfficeOrder> officeOrder = new ArrayList<OfficeOrder>();
	 * model.addAttribute("officeOrder",officeOrder); List<personcompanyApproval>
	 * personStatus = new ArrayList<personcompanyApproval>();
	 * model.addAttribute("personStatus",personStatus);
	 * 
	 * return "caseDetails/status"; }
	 */
	/*
	 * @RequestMapping(value = "/summonStatus") public String summonStatus(Model
	 * model) throws Exception { List<OfficeOrder> officeOrder = new
	 * ArrayList<OfficeOrder>(); model.addAttribute("officeOrder",officeOrder);
	 * List<NoticeStatus> notice = new ArrayList<NoticeStatus>();
	 * model.addAttribute("notice",notice); getLoginUserName(); List<SummonStatus>
	 * summon = summonDao.findSummonByUserId(getUserDetails());
	 * List<personcompanyApproval> personStatus = new
	 * ArrayList<personcompanyApproval>();
	 * model.addAttribute("personStatus",personStatus);
	 * model.addAttribute("summon",summon); return "caseDetails/status"; } /*public
	 * String getDirectorName() { UserDetails director =
	 * userDetailsRepo.findByDesignation(new AddDesignation(1L)); return
	 * director.getSalutation() + " " + director.getFullName(); }
	 */

	@RequestMapping(value = "/filterSummonPdf", produces = MediaType.APPLICATION_PDF_VALUE, method = RequestMethod.GET)
	public ResponseEntity<InputStreamResource> filterSummonPdf(@RequestParam(name = "summontypeid") Long sumtypeId,
			Model model) throws Exception {
		Optional<SummonStatus> summonStatus = summonRepo.findById(sumtypeId);
		SummonStatus summon = summonStatus.get();
		CaseDetails caseDetails = summon.getCaseDetails();

		String OfficeAddress = caseDetails.getUnit().getAddress();

//		SummonTemplate summonDraft = summonTempRepo.findBySummon(summon);
		InitiateSummonDraft summonDraft = summonDao.findSummonDraftByCaseDetails(caseDetails, summon.getSummonType());

		InspectorListDTO inspList = getInspectorListAndCompany(caseDetails.getId());

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

		SummonTempDto caseDto = new SummonTempDto(caseDetails.getCaseId(), Utils.getCurrentDateWithMonth(),
				summonDraft.getPara1(), summonDraft.getPara2(), summonDraft.getPara3(), summonDraft.getPara4(),
				summonDraft.getPara5(), summonDraft.getPara6(), summon.getId(), caseDetails.getCaseId(),
				caseDetails.getId(), inspList.getIoName(), summon.getSummonNo(), "", summon.getIsSensitive(),
				summon.getSummonType().getId(), "", Designation);

		model.addAttribute("caseDto", caseDto);

		model.addAttribute("inspList", inspList.getInspctorList());
		model.addAttribute("copyToList", inspList.getCopyToList());
		model.addAttribute("listCom", inspList.getCompanyList());

		SummonType summonType = SummonTypeNewDetails.findById(summon.getSummonType().getId()).get();
		String name = summonType.getName();
		String designation = " ";
		if (summonType.getDesignation() != null) {
			designation = summonType.getDesignation();
		} else {
			designation = " ";
		}

		String address = summonType.getAddress();

		List<String> companyNames = new ArrayList<String>();

		List<Company> comList = inspList.getCompanyList();
		int otherSize = inspList.getCompanyList().size() - 1;
		for (Company com : comList) {
			companyNames.add(com.getName());
		}

		String companydisplay = "";
		if (inspList.getCompanyList().size() == 1)
			companydisplay = inspList.getCompanyList().get(0).getName();
		else
			companydisplay = inspList.getCompanyList().get(0).getName() + " and " + otherSize + " other companies.";
		// gouthami 15/09/2020
		// int remainIoSize=inspList.getInspctorList().size()-1;
		String company = summonDao.findCompanyNameById(summonType.getId());

		String noticeCompany = "";
		if (summonType.getIndividualType().getIndividualId() == 5
				|| summonType.getIndividualType().getIndividualId() == 6) {
			noticeCompany = summonType.getNameCompany();
		} else {
			noticeCompany = company;
		}
		ByteArrayInputStream bis = SummonPDF
				.summonSaved(new SummonDto(new Utils().getCurrentDateFormat(caseDetails.getMcaOrderDate()),
						summon.getSummonDin(), inspList.getIoName(), noticeCompany, getSplitedString(address),
						designation, name, summonDraft.getPara1(), summonDraft.getPara2(), summonDraft.getPara3(),
						summonDraft.getPara4(), summonDraft.getPara5(), summonDraft.getPara6(),
						getSplitedString(OfficeAddress), companydisplay, Designation));
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Disposition", "inline; filename=summons.pdf");

		return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF)
				.body(new InputStreamResource(bis));

	}

	@RequestMapping(value = "/filterNoticePdf", produces = MediaType.APPLICATION_PDF_VALUE, method = RequestMethod.GET)
	public ResponseEntity<InputStreamResource> filterNoticePdf(@RequestParam(name = "summontypeid") Long sumtypeId,
			Model model) throws Exception {
		Optional<NoticeStatus> noticeStatus = noticeRepo.findById(sumtypeId);
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

//		NoticeTemplate noticeDraft = noticeTempRepo.findByNotice(notice);
		InitiateNoticeDraft noticeDraft = summonDao.findNoticeDraftByCaseDetails(caseDetails, notice.getSummonType());
		InspectorListDTO inspList = getInspectorListAndCompany(caseDetails.getId());
		NoticeTempDto caseDto = new NoticeTempDto(caseDetails.getCaseId(), Utils.getCurrentDateWithMonth(),
				noticeDraft.getPara1(), noticeDraft.getPara2(), noticeDraft.getPara3(), noticeDraft.getPara4(),
				noticeDraft.getPara5(), notice.getId(), caseDetails.getCaseId(), caseDetails.getId(),
				inspList.getIoName(), notice.getSummonNo(), "", notice.getSummonType().getId(), notice.getIsSensitive(),
				Designation);

		model.addAttribute("caseDto", caseDto);

		model.addAttribute("inspList", inspList.getInspctorList());
		model.addAttribute("copyToList", inspList.getCopyToList());
		model.addAttribute("listCom", inspList.getCompanyList());

		// SummonType
		// summonType=summonTypeDetails.findById(notice.getSummonType().getId()).get();

		// added by Gouthami

		SummonType summonType = SummonTypeNewDetails.findById(notice.getSummonType().getId()).get();
		String name = summonType.getName();
		String designation = "";
		String address = summonType.getAddress();
		String email = summonType.getEmail();
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

//		inspList.getInspctorList());
		// String ioName =inspList.getIoName();
		// gouthami 15/09/2020
		// int otherSize=inspList.getCompanyList().size()-1;
		List<String> companyNames = new ArrayList<String>();
		List<Company> comList = inspList.getCompanyList();
		for (Company com : comList) {
			companyNames.add(com.getName());
		}

		String companyName = summonDao.findCompanyNameById(summonType.getId());
		/*
		 * String comapnyPara=""; if(otherSize==0) comapnyPara=company; else
		 * comapnyPara=company+" and "+otherSize+" other companies";
		 */
		String noticeCompany = "";
		if (summonType.getIndividualType().getIndividualId() == 5
				|| summonType.getIndividualType().getIndividualId() == 6) {
			noticeCompany = summonType.getNameCompany();
		} else {
			noticeCompany = companyName;
		}

		InitiateNoticeDraft initiateNoticeDraft = summonDao.findNoticeDraftByCaseDetails(caseDetails, summonType);
		ByteArrayInputStream bis = null;
		if (initiateNoticeDraft != null) {
			caseDto = new NoticeTempDto(notice.getNoticeDin(),
					new Utils().getCurrentDateFormat(caseDetails.getMcaOrderDate()), initiateNoticeDraft.getPara1(),
					initiateNoticeDraft.getPara2(), initiateNoticeDraft.getPara3(), initiateNoticeDraft.getPara4(),
					initiateNoticeDraft.getPara5(), ioName, companyNames, noticeCompany, address, designation, name,
					email);
			bis = NoticePdf.noticeSaved(caseDto);
		}
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Disposition", "inline; filename=summons.pdf");

		return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF)
				.body(new InputStreamResource(bis));

	}

	@RequestMapping(value = "/filterCase")
	public String filterCase(Model model) throws Exception {
		// gouthami 15/09/2020
		// List<CaseDetails> lstCase = caseDetailsRepository.findAll();

		List<Object[]> findListByUserId = summonDao.findListByUserId(getUserDetails().getUserId());
		List<PendingForApprovalDTO> caselist = new ArrayList<PendingForApprovalDTO>();
		for (Object[] dto : findListByUserId) {
			PendingForApprovalDTO pes = new PendingForApprovalDTO();
			pes.setCaseId(Long.parseLong(dto[0].toString()));
			pes.setCaseStrId(dto[1].toString());
			pes.setCaseTitle(dto[2].toString());
			caselist.add(pes);
		}

		model.addAttribute("lstCase", caselist);
		return "caseDetails/filterCase";
	}

	@GetMapping("getDraftedSummonNotices")
	public String getDraftedSummonNotices(ModelMap modelMap) throws Exception {
//		List<Object[]> findListByUserId=summonDao.findListByUserId(getUserDetails().getUserId());
		List<Object[]> draftSummon = summonDao.getDraftedSummon(getUserDetails());
		List<Object[]> draftNoitice = summonDao.getDraftedNotice(getUserDetails());

		List<PendingForApprovalDTO> caselist = new ArrayList<PendingForApprovalDTO>();
//		String[][] twoD_arr = new String[draftSummon.size()+draftNoitice.size()][2];

//		int i = 0;

		for (Object[] dto : draftSummon) {
			PendingForApprovalDTO pes = new PendingForApprovalDTO();

			pes.setCaseId(Long.parseLong(dto[0].toString()));
			pes.setCaseStrId(dto[1].toString());
			pes.setCaseTitle(dto[2].toString());
			pes.setMcaOrderFile("Summon");
			pes.setSummonTypeId(Long.parseLong(dto[3].toString()));
			if (!caselist.contains(pes))
				caselist.add(pes);
//				twoD_arr[i][0]="Summon";
//				twoD_arr[i][1]=dto[3].toString();
//				i++;
		}
		for (Object[] dto : draftNoitice) {
			PendingForApprovalDTO pes = new PendingForApprovalDTO();
			pes.setCaseId(Long.parseLong(dto[0].toString()));
			pes.setCaseStrId(dto[1].toString());
			pes.setCaseTitle(dto[2].toString());
			pes.setMcaOrderFile("Notice");
			pes.setSummonTypeId(Long.parseLong(dto[3].toString()));
			if (!caselist.contains(pes))
				caselist.add(pes);
		}
		modelMap.addAttribute("caselist", caselist);
		modelMap.addAttribute("caseDetails", new CaseDetails());
		return "case/draftedCase";
	}

	@RequestMapping(value = "/openDraftSummon")
	public String openDraftSummon(@RequestParam(name = "caseId", required = false) Long id,
			@RequestParam(name = "type", required = false) String type,
			@RequestParam(name = "summontypeid", required = false, defaultValue = "0") int sumtypeId, ModelMap model)
			throws Exception {

		SNMSValidator snmsvalid = new SNMSValidator();

		if (id == null) {
			return "redirect:/getCompleteCase";
		}

		if (!snmsvalid.getValidInteger(id)) {

			return "redirect:/getDraftedSummonNotices";

		}
		if (!snmsvalid.getValidInteger(sumtypeId)) {

			return "redirect:/getDraftedSummonNotices";

		}
		if (!snmsvalid.isValidName(type)) {

			return "redirect:/getDraftedSummonNotices";

		}
		if (!snmsvalid.getValidInteger(sumtypeId)) {
			return "redirect:/getDraftedSummonNotices";
		}
		String company = "";
		String dateOfAppear = "";
		Optional<CaseDetails> caseD = caseDetailsRepository.findById(id);
		CaseDetails caseDetails = caseD.get();

//		Long snid = (summonRepo.findMaxId() != null) ? (summonRepo.findMaxId()+1):1;
//		********************
		/*
		 * if(summonNo.charAt(summonNo.length()-1)== '/') summonNo=summonNo+snid; else
		 * summonNo=summonNo+"/"+snid;
		 */
//		*******************
		AppUser userDetails = userDetailsService.getUserDetails();
		Inspector inspector = inspRepo.findAllByCaseDetailsAndAppUser(caseDetails, userDetails);
		String Designation;
		String DesignationHi = null;
		if (inspector.getIsAdo() == true) {
			// Designation = "Addl";
			Designation = "Addl.Director";
			
			DesignationHi="निरीक्षक";
		} else if (inspector.getIsIO() == true) {
			Designation = "Investigating Officer";
			DesignationHi="जांच अधिकारी";
		} else {
			Designation = "Inspector";
			DesignationHi="जांच अधिकारी";
		}

		UserDetails user = userDetailsRepo.findById(userDetails.getUserId()).get();
		String ioName = userDetailsService.getFullName(user);
		// UserDetails user =
		// userDetailsRepo.findById(notice.getAppUser().getUserId()).get();
		if (type.equalsIgnoreCase("summon".trim())) {
			// SummonType summonType=summonTypeDetails.findById(sumtypeId).get();
			// BY Gouthami
			SummonType summonType = SummonTypeNewDetails.findById(sumtypeId).get();

			InitiateSummonDraft office = (summonDao.findSummonDraftByCaseDetails(caseDetails, summonType) == null)
					? new InitiateSummonDraft()
					: summonDao.findSummonDraftByCaseDetails(caseDetails, summonType);
			String caseNo = "No : " + caseDetails.getCaseId();
			String date = " Date :" + new Utils().currentDate();
			dateOfAppear = office.getDateOfAppear();
//			summonNo = "SFIO/INV/2020/"+summonNo;
			InspectorListDTO inspList = getInspectorListAndCompany(caseDetails.getId());
			int otherSize = inspList.getCompanyList().size() - 1;
			String para1 = "";

			company = summonDao.findCompanyNameBySummonTypeId(sumtypeId);

			if (otherSize == 0)
				para1 = company;
			else
				para1 = company + " and " + otherSize + " other company";

			String name = summonType.getName();
			String designation = "";
			String address = summonType.getAddress();

			if (summonType.getDesignation() != null) {
				designation = summonType.getDesignation();
			} else {
				designation = " ";
			}
			String noticeCompany = "";
			if (summonType.getIndividualType().getIndividualId() == 5
					|| summonType.getIndividualType().getIndividualId() == 6) {
				noticeCompany = summonType.getNameCompany();
			} else {
				noticeCompany = company;
			}

			String OfficeAddress = caseDetails.getUnit().getAddress();

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
			// String ioName=inspList.getIoName();
			if (summonDao.findSummonDraftByCaseDetails(caseDetails, summonType) != null) {
				SummonTempDto caseDto = new SummonTempDto(caseNo, date, office.getPara1(), office.getPara2(),
						office.getPara3(), office.getPara4(), office.getPara5(), office.getPara6(),
						 office.getPara1_h(), office.getPara2_h(),
							office.getPara3_h(), office.getPara4_h(), office.getPara5_h(), office.getPara6_h(),
						1L,
						caseDetails.getCaseId(), caseDetails.getId(), ioName, office.getSummonNo(),
						office.getDateOfAppear(), "", sumtypeId, "", Designation,DesignationHi);
				model.addAttribute("caseDto", caseDto);
			}

			model.addAttribute("companyName", noticeCompany);
			model.addAttribute("name", name);

			model.addAttribute("designation", designation);
			model.addAttribute("address", getSplitedString(address));
			model.addAttribute("OfficeAddress", getSplitedString(OfficeAddress));
			model.addAttribute("companydisplay", para1);
		}

		/*
		 * List<Object[]> ioadoList =
		 * userMangCustom.getIoAdoByCaseId(caseDetails.getId()); Long ioId = 0L ; for
		 * (Object[] object : ioadoList) { if((Boolean)object[1]) ioId =
		 * Long.parseLong(object[0].toString()); } boolean privilege = false; try {
		 * if(getUserDetails().getUserId()==ioId) privilege = true; } catch (Exception
		 * e) { logger.info(e.getMessage()); }
		 */

		List<Object[]> inspectorList = userMangCustom.getInspectorList(caseDetails.getId());
		boolean privilege = false;
		try {
			for (Object[] object : inspectorList) {

				if (getUserDetails().getUserId() == Integer.parseInt(object[0].toString()))
					privilege = true;
			}

		} catch (Exception e) {
			logger.info(e.getMessage());
		}

		PdfPreviewDto pdfPreviewDto = new PdfPreviewDto(id, company, type, sumtypeId, dateOfAppear, "");
		model.addAttribute("privilege", privilege);
		model.addAttribute("pdfPreview", pdfPreviewDto);
		return "notice/genSummonAs";

	}

	@RequestMapping(value = "/openDraftNotice")
	public String openDraftNotice(@RequestParam(name = "caseId", required = false) Long id,
			@RequestParam(name = "type", required = false) String type,
			@RequestParam(name = "summontypeid", required = false, defaultValue = "0") int sumtypeId, ModelMap model)
			throws Exception {

		String company = "";
		String dateOfAppear = "";

		Optional<CaseDetails> caseD = caseDetailsRepository.findById(id);
		CaseDetails caseDetails = caseD.get();

		String OfficeAddress = caseDetails.getUnit().getAddress();
		// SummonType summonType=summonTypeDetails.findById(sumtypeId).get();

		// Gouthami 07/12/2021
		AppUser userDetails = userDetailsService.getUserDetails();
		Inspector inspector = inspRepo.findAllByCaseDetailsAndAppUser(caseDetails, userDetails);
		String Designation;
		String DesignationHi = null;
		if (inspector.getIsAdo() == true) {
			// Designation = "Addl";
			Designation = "Addl.Director";
			
			DesignationHi="निरीक्षक";
		} else if (inspector.getIsIO() == true) {
			Designation = "Investigating Officer";
			DesignationHi="जांच अधिकारी";
		} else {
			Designation = "Inspector";
			DesignationHi="जांच अधिकारी";
		}

		UserDetails user = userDetailsRepo.findById(userDetails.getUserId()).get();
		String ioName = userDetailsService.getFullName(user);

		// change BY gouthami
		SummonType summonType = SummonTypeNewDetails.findById(sumtypeId).get();
		InitiateNoticeDraft initiateNoticeDraft = (summonDao.findNoticeDraftByCaseDetails(caseDetails,
				summonType) == null) ? new InitiateNoticeDraft()
						: summonDao.findNoticeDraftByCaseDetails(caseDetails, summonType);

		dateOfAppear = initiateNoticeDraft.getDateOfAppear();
//		Long snid = (noticeRepo.findMaxId() != null) ? (noticeRepo.findMaxId()+1):1;

//		String caseNo = "No : " + caseDetails.getCaseId()+"/"+snid.toString();

		String date = " Date :" + new Utils().currentDate();
		InspectorListDTO inspList = getInspectorListAndCompany(caseDetails.getId());
		// String io=inspList.getIoName();
		int otherSize = inspList.getCompanyList().size() - 1;
		model.addAttribute("listCom", inspList.getCompanyList());
		// gouthami 15/09/2020
		// String companyAdd="";

		// SummonDetails summonDetails=summonDao.findByCaseId(id);

		String name = summonType.getName();
		String designation = " ";
		String address = summonType.getAddress();
		String email = summonType.getEmail();
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
		 * designation="Vendor/Other"; name=summonType.getVendorName();
		 * address=summonType.getVendorAddress(); email=summonType.getVendorEmail();
		 * }else if(summonType.getType().equalsIgnoreCase("secretary")) {
		 * designation="Company Secretary"; name=summonType.getMemberName();
		 * address=summonType.getMemberAddress(); email=summonType.getMemberEmail(); }
		 * else { designation="Auditor"; name=summonType.getAuditorName();
		 * address=summonType.getAddress(); email=summonType.getEmail(); }
		 */
		company = summonDao.findCompanyNameBySummonTypeId(sumtypeId);
		/*
		 * if(company == null) { return "getDraftedSummonNotices"; }
		 */
		String noticeCompany = "";
		if (summonType.getIndividualType().getIndividualId() == 5
				|| summonType.getIndividualType().getIndividualId() == 6) {
			noticeCompany = summonType.getNameCompany();
		} else if (summonType.getIndividualType().getIndividualId() == 7) {
			noticeCompany = "";
		} else {
			noticeCompany = company;
		}
		String comapnyPara = "";
		if (otherSize == 0)
			comapnyPara = company;
		else
			comapnyPara = company + " and " + otherSize + " other companies";
		// gouthami 15/09/2020
		/*
		 * for(int i=0;i<summonDetails.getCompanySummon().size();i++) {
		 * if(summonDetails.getCompanySummon().get(i).getCompanyName().equals(company))
		 * companyAdd=summonDetails.getCompanySummon().get(i).getAddress();
		 * 
		 * }
		 */
		if (summonDao.findNoticeDraftByCaseDetails(caseDetails, summonType) != null) {
			NoticeTempDto caseDto = new NoticeTempDto("caseNo", date, initiateNoticeDraft.getPara1(),
					initiateNoticeDraft.getPara2(), initiateNoticeDraft.getPara3(), initiateNoticeDraft.getPara4(),
					initiateNoticeDraft.getPara5(),
					initiateNoticeDraft.getPara1h(),initiateNoticeDraft.getPara2h(), initiateNoticeDraft.getPara3h(), initiateNoticeDraft.getPara4h(),
					initiateNoticeDraft.getPara5h(),
					1L, caseDetails.getCaseId(), caseDetails.getId(), ioName,
					initiateNoticeDraft.getSummonNo(), dateOfAppear, sumtypeId, "", Designation,DesignationHi);
//			caseDto.setNoticeDin(noticeDIN);

			if (null != caseDto)
				model.addAttribute("caseDto", caseDto);

		}
		/*
		 * List<Object[]> ioadoList =
		 * userMangCustom.getIoAdoByCaseId(caseDetails.getId()); Long ioId = 0L ; for
		 * (Object[] object : ioadoList) { if((Boolean)object[1]) ioId =
		 * Long.parseLong(object[0].toString()); } boolean privilege = false; try {
		 * if(getUserDetails().getUserId()==ioId) privilege = true; } catch (Exception
		 * e) { logger.info(e.getMessage()); }
		 */

		List<Object[]> inspectorList = userMangCustom.getInspectorList(caseDetails.getId());
		boolean privilege = false;
		try {
			for (Object[] object : inspectorList) {

				if (getUserDetails().getUserId() == Integer.parseInt(object[0].toString()))
					privilege = true;
			}

		} catch (Exception e) {
			logger.info(e.getMessage());
		}

		PdfPreviewDto pdfPreviewDto = new PdfPreviewDto(id, noticeCompany, type, sumtypeId, dateOfAppear, "");
		model.addAttribute("privilege", privilege);
		model.addAttribute("pdfPreview", pdfPreviewDto);
		model.addAttribute("companyName", noticeCompany);
		model.addAttribute("name", name);
		model.addAttribute("designation", designation);
		model.addAttribute("Designation", Designation);
		model.addAttribute("address", getSplitedString(address));
		model.addAttribute("email", email);
		model.addAttribute("officeadrress", OfficeAddress);
		model.addAttribute("companydisplay", comapnyPara);
		return "notice/getNoticeAs";
	}

	@RequestMapping(value = "/checkDraftForNoticeSummon", method = RequestMethod.POST)
	public @ResponseBody int getCompanyListByCaseId(@RequestParam("type") String type,
			@RequestParam("caseId") Long caseId, @RequestParam("summontypeid") int summontypeid, Model model) {
		int status = 0;
		if (type.equalsIgnoreCase("summon")) {
			InitiateSummonDraft summonDraft = summonDao.findSummonDraftByCaseDetails(new CaseDetails(caseId),
					new SummonType(summontypeid));
			if (summonDraft != null) {
				status = SnmsConstant.NOTICE_SUMMON_DRAFT;
			}
//			Optional<SummonStatus> summon = summonRepo.findById(Long.parseLong(String.valueOf(summontypeid)));
			Optional<SummonStatus> summon;
			if (summonDraft != null)
				summon = summonRepo.findBySummonTypeAndSummonNo(new SummonType(summontypeid),
						summonDraft.getSummonNo());
			else
				summon = summonRepo.findBySummonTypeAndSummonNo(new SummonType(summontypeid), "");

			if (summon.isPresent() && summon.get().getIsSigned() == 0
					&& summon.get().getSummonNo().equals(summonDraft.getSummonNo())
					&& summon.get().getIsRejected().equals(false)) {
				status = SnmsConstant.NOTICE_SUMMON_PROGRESS;
			} else if (summon.isPresent() && summon.get().getIsSigned() == 1
					&& summon.get().getSummonNo().equals(summonDraft.getSummonNo())) {
				status = SnmsConstant.NOTICE_SUMMON_SIGNED;
			} else if (summon.isPresent() && summon.get().getIsSigned() == 0
					&& summon.get().getSummonNo().equals(summonDraft.getSummonNo())
					&& summon.get().getIsRejected().equals(true)) {
				status = SnmsConstant.NOTICE_SUMMON_REJECTED;
			}
			return status;
		} else if (type.equalsIgnoreCase("notice")) {

			// InitiateNoticeDraft noticeDraft = summonDao.findNoticeDraftByCaseDetails(new
			// CaseDetails(caseId), new SummonType(summontypeid));

			// change BY Gouthami
			InitiateNoticeDraft noticeDraft = summonDao.findNoticeDraftByCaseDetails(new CaseDetails(caseId),
					new SummonType(summontypeid));

			if (noticeDraft != null) {
				status = SnmsConstant.NOTICE_SUMMON_DRAFT;
			}
			Optional<NoticeStatus> notice;
			if (noticeDraft != null)
				notice = noticeRepo.findBySummonTypeAndSummonNo(new SummonType(summontypeid),
						noticeDraft.getSummonNo());
			else
				notice = noticeRepo.findBySummonTypeAndSummonNo(new SummonType(summontypeid), "");
			if (notice.isPresent() && notice.get().getIsSigned() == 0
					&& notice.get().getSummonNo().equals(noticeDraft.getSummonNo())
					&& notice.get().getIsRejected().equals(false)) {
				status = SnmsConstant.NOTICE_SUMMON_PROGRESS;
			} else if (notice.isPresent() && notice.get().getIsSigned() == 1
					&& notice.get().getSummonNo().equals(noticeDraft.getSummonNo())) {
				status = SnmsConstant.NOTICE_SUMMON_SIGNED;
			} else if (notice.isPresent() && notice.get().getIsSigned() == 1
					&& notice.get().getSummonNo().equals(noticeDraft.getSummonNo())
					&& notice.get().getIsRejected().equals(true)) {
				status = SnmsConstant.NOTICE_SUMMON_REJECTED;
			}
			return status;
		}
		return SnmsConstant.NOTICE_SUMMON_SIGNED;

	}

	// Made By Gouthami 08072020

	@RequestMapping(value = "addNewSummonNew", method = RequestMethod.POST)
	public String saveSummonDetailsNew(@Valid @ModelAttribute("summonDetails") SummonDetails summonDetails,
			BindingResult result, ModelMap modelMap) throws Exception {
		commonObjectSummon(modelMap, summonDetails.getCaseId());
		SummonDetails saveSummon;
		CompanySummon companySummon = new CompanySummon();
		// SummonType2 summonType=new SummonType();

		SummonNoticeValidation sumVal = new SummonNoticeValidation();
		sumVal.validateIndivialType(summonDetails, result);
		if (result.hasErrors()) {
			// summonDetails.getSummonTypeDto().setType(summonDetails.getSummonTypeDto().getType());
			List<IndividualType> individualType = idividualRepo.findAll();
			modelMap.addAttribute("individualTlist", individualType);
			modelMap.addAttribute("summonDetails", summonDetails);

			return "notice/CreateNotice";
		}
		// for summonType object set and save
		SummonType summonType1 = null;
		CompanySummon companySummon1 = new CompanySummon();
		if (summonDetails.getId() != 0) {
			saveSummon = summonDao.findByCaseId(summonDetails.getCaseId());
			saveSummon.setId(summonDetails.getId());
			summonDetails.getSummonTypenewDto()
					.setIndividualType(new IndividualType(summonDetails.getSummonTypenewDto().getIndividualId()));
			/// summon

			// String Type =
			// String.valueOf(summonDetails.getSummonTypenewDto().getIndividualType().getIndividualId())
			// ;
			if (summonDetails.getSummonTypenewDto().getIndividualId() == 1) {

				summonType1 = new SummonType();

				summonType1.setRegistration_no(summonDetails.getSummonTypenewDto().getDirReg_no());
				summonType1.setName(summonDetails.getSummonTypenewDto().getDirName());
				summonType1.setEmail(summonDetails.getSummonTypenewDto().getDirEmail());
				summonType1.setOffJoinDate(summonDetails.getSummonTypenewDto().getDiroffJoinDate());
				summonType1.setAddress(summonDetails.getSummonTypenewDto().getDirAddr());
				summonType1.setMobileNo(summonDetails.getSummonTypenewDto().getDirMobile());
				summonType1.setPassport(summonDetails.getSummonTypenewDto().getDirpassport());
				summonType1.setPanNumber(summonDetails.getSummonTypenewDto().getDirpanNumber());
				summonType1
						.setIndividualType(new IndividualType(summonDetails.getSummonTypenewDto().getIndividualId()));
			} else if (summonDetails.getSummonTypenewDto().getIndividualId() == 2) {

				summonType1 = new SummonType();

				summonType1.setRegistration_no(summonDetails.getSummonTypenewDto().getFdirReg_no());
				summonType1.setName(summonDetails.getSummonTypenewDto().getFdirName());
				summonType1.setEmail(summonDetails.getSummonTypenewDto().getFdirEmail());
				summonType1.setOffJoinDate(summonDetails.getSummonTypenewDto().getFdiroffJoinDate());
				summonType1.setAddress(summonDetails.getSummonTypenewDto().getFdirAddr());
				summonType1.setMobileNo(summonDetails.getSummonTypenewDto().getFdirMobile());
				summonType1.setPassport(summonDetails.getSummonTypenewDto().getFdirpassport());
				summonType1.setPanNumber(summonDetails.getSummonTypenewDto().getFdirpanNumber());

				summonType1
						.setIndividualType(new IndividualType(summonDetails.getSummonTypenewDto().getIndividualId()));
			} else if (summonDetails.getSummonTypenewDto().getIndividualId() == 3) {

				summonType1 = new SummonType();

				summonType1.setName(summonDetails.getSummonTypenewDto().getEmpName());

				summonType1.setDesignation(summonDetails.getSummonTypenewDto().getEmpdesgi());

				summonType1.setSex(summonDetails.getSummonTypenewDto().getEmpsex());

				summonType1.setDob(summonDetails.getSummonTypenewDto().getEmpDob());

				summonType1.setNationalId(summonDetails.getSummonTypenewDto().getEmpnatid());

				summonType1.setNationality(summonDetails.getSummonTypenewDto().getEmpNation());
				summonType1.setPanNumber(summonDetails.getSummonTypenewDto().getEmpPanNumber());

				summonType1.setPassport(summonDetails.getSummonTypenewDto().getEmpPassport());
				summonType1.setIssueDate(summonDetails.getSummonTypenewDto().getEmpPassDate());
				summonType1.setPlaceof_issued(summonDetails.getSummonTypenewDto().getEmpissuplace());
				summonType1.setOffJoinDate(summonDetails.getSummonTypenewDto().getEmpoffdate());
				summonType1.setAddress(summonDetails.getSummonTypenewDto().getEmpAddress());
				summonType1.setMobileNo(summonDetails.getSummonTypenewDto().getEmpMobile());
				summonType1.setEmail(summonDetails.getSummonTypenewDto().getEmpemail());
				summonType1
						.setIndividualType(new IndividualType(summonDetails.getSummonTypenewDto().getIndividualId()));

			}

			else if (summonDetails.getSummonTypenewDto().getIndividualId() == 4) {
				summonType1 = new SummonType();

				summonType1.setName(summonDetails.getSummonTypenewDto().getFempName());

				summonType1.setDesignation(summonDetails.getSummonTypenewDto().getFempdesgi());

				summonType1.setSex(summonDetails.getSummonTypenewDto().getFempsex());

				summonType1.setDob(summonDetails.getSummonTypenewDto().getFempDob());

				summonType1.setNationalId(summonDetails.getSummonTypenewDto().getFempnatid());

				summonType1.setNationality(summonDetails.getSummonTypenewDto().getFempNation());
				summonType1.setPassport(summonDetails.getSummonTypenewDto().getFempPassport());
				summonType1.setIssueDate(summonDetails.getSummonTypenewDto().getFempPassDate());
				summonType1.setPlaceof_issued(summonDetails.getSummonTypenewDto().getFempissuplace());
				summonType1.setOffJoinDate(summonDetails.getSummonTypenewDto().getFempoffdate());
				summonType1.setAddress(summonDetails.getSummonTypenewDto().getFempAddress());
				summonType1.setMobileNo(summonDetails.getSummonTypenewDto().getFempMobile());
				summonType1.setEmail(summonDetails.getSummonTypenewDto().getFempemail());
				summonType1.setPanNumber(summonDetails.getSummonTypenewDto().getFempPanNumber());

				summonType1
						.setIndividualType(new IndividualType(summonDetails.getSummonTypenewDto().getIndividualId()));
			}

			else if (summonDetails.getSummonTypenewDto().getIndividualId() == 5
					|| summonDetails.getSummonTypenewDto().getIndividualId() == 6) {
				summonType1 = new SummonType();

				summonType1.setIsCin(summonDetails.getSummonTypenewDto().getIsACin());
				summonType1.setName(summonDetails.getSummonTypenewDto().getAgentName());
				summonType1.setNameCompany(summonDetails.getSummonTypenewDto().getAgentcompany());
				summonType1.setAddress(summonDetails.getSummonTypenewDto().getAgentAddress());
				summonType1.setMobileNo(summonDetails.getSummonTypenewDto().getAgentMobile());
				summonType1.setEmail(summonDetails.getSummonTypenewDto().getAgentEmail());
				summonType1.setRegistration_no(summonDetails.getSummonTypenewDto().getAgentRegno());
				summonType1
						.setIndividualType(new IndividualType(summonDetails.getSummonTypenewDto().getIndividualId()));
				summonType1.setPassport(summonDetails.getSummonTypenewDto().getAgentpanNumber());
				summonType1.setPanNumber(summonDetails.getSummonTypenewDto().getAgentpanNumber());

			} else {
				summonType1 = new SummonType();
				summonType1.setIsCin(summonDetails.getSummonTypenewDto().getIsOCin().replaceAll(",", ""));
				summonType1.setName(summonDetails.getSummonTypenewDto().getOthName());
				summonType1.setRelationwithcompany(summonDetails.getSummonTypenewDto().getOthRelation());
				summonType1.setAddress(summonDetails.getSummonTypenewDto().getOthAddress());
				summonType1.setMobileNo(summonDetails.getSummonTypenewDto().getOthMobile());
				summonType1.setEmail(summonDetails.getSummonTypenewDto().getOthEmail());
				summonType1.setRegistration_no(summonDetails.getSummonTypenewDto().getOthRegno());
				summonType1
						.setIndividualType(new IndividualType(summonDetails.getSummonTypenewDto().getIndividualId()));
				summonType1.setPassport(summonDetails.getSummonTypenewDto().getOthpanNumber());
				summonType1.setPanNumber(summonDetails.getSummonTypenewDto().getOthpassport());

			}
			CompanyType comp = new CompanyType();
			if (summonDetails.getCompanySummonsDto().getCompanyType() == null) {
				// CompanyType comp = new CompanyType();
				comp.setId(1);
				summonDetails.getCompanySummonsDto().setCompanyType(comp);

			}
			companySummon1 = new CompanySummon(summonDetails.getCompanySummonsDto().getCin(),
					summonDetails.getCompanySummonsDto().getCompanyName(),
					summonDetails.getCompanySummonsDto().getAddress(), summonDetails.getCompanySummonsDto().getEmail(),
					summonDetails.getCompanySummonsDto().getCompanyType());
			saveSummon.setCaseId(summonDetails.getCaseId());
			saveSummon.setUpdatedDate(new Date());
			saveSummon.setUserid(getUserDetails().getUserId());

			boolean isCompExist = false;
			for (int i = 0; i < saveSummon.getCompanySummon().size(); i++) {
				if (saveSummon.getCompanySummon().get(i).getCompanyName()
						.equalsIgnoreCase(summonDetails.getCompanySummonsDto().getCompanyName())) {
					isCompExist = true;
					saveSummon.getCompanySummon().get(i).setCin(summonDetails.getCompanySummonsDto().getCin());
					saveSummon.getCompanySummon().get(i).setAddress(summonDetails.getCompanySummonsDto().getAddress());
					saveSummon.getCompanySummon().get(i).setEmail(summonDetails.getCompanySummonsDto().getEmail());
					if (0 != summonDetails.getSummonTypenewDto().getIndividualId()
							&& !"".equals(summonDetails.getSummonTypenewDto().getIndividualId())) {
						summonType1.setId(0);
						saveSummon.getCompanySummon().get(i).getSummonType().add(summonType1);
					}
					break;
				}
			}
			if (!isCompExist) {
				companySummon1.setId(0);
				if (0 != summonDetails.getSummonTypenewDto().getIndividualId()) {
					companySummon1.getSummonType().add(summonType1);
				}
				saveSummon.getCompanySummon().add(companySummon1);
			}

			addNoticeSummonRepository.save(saveSummon);
			String loginUName = appUserDAO.findUserDetails(getUserDetails()).getFirstName() != null
					? appUserDAO.findUserDetails(getUserDetails()).getFirstName()
					: ""

							+ appUserDAO.findUserDetails(getUserDetails()).getMiddleName() != null
									? appUserDAO.findUserDetails(getUserDetails()).getMiddleName()
									: "" + appUserDAO.findUserDetails(getUserDetails()).getLastName() != null
											? appUserDAO.findUserDetails(getUserDetails()).getLastName()
											: "";

			auditBeanBo.setAuditBean(Integer.parseInt(userDetailsService.getUserDetails().getUserId().toString()),
					loginUName, "User", Integer.parseInt(userDetailsService.getUserDetails().getUserId().toString()),
					utils.getMessage("log.NoticeSummon.save"), utils.getMessage("log.NoticeSummon.saved"), loginUName,
					"true");
			auditBeanBo.save();

		}
		// firstTime occur only
		else {
			saveSummon = new SummonDetails();
			saveSummon.setCaseId(summonDetails.getCaseId());

			saveSummon.setCreatedDate(new Date());
			saveSummon.setUserid(getUserDetails().getUserId());

			BeanUtils.copyProperties(summonDetails.getCompanySummonsDto(), companySummon);
			// summonDetails.getSummonTypenewDto().setIndividualType(new
			// IndividualType(summonType1.getIndividualId()));

			if (summonDetails.getSummonTypenewDto().getIndividualId() == 1) {

				summonType1 = new SummonType();

				summonType1.setRegistration_no(summonDetails.getSummonTypenewDto().getDirReg_no());
				summonType1.setName(summonDetails.getSummonTypenewDto().getDirName());
				summonType1.setEmail(summonDetails.getSummonTypenewDto().getDirEmail());
				summonType1.setOffJoinDate(summonDetails.getSummonTypenewDto().getDiroffJoinDate());
				summonType1.setAddress(summonDetails.getSummonTypenewDto().getDirAddr());
				summonType1.setMobileNo(summonDetails.getSummonTypenewDto().getDirMobile());
				summonType1
						.setIndividualType(new IndividualType(summonDetails.getSummonTypenewDto().getIndividualId()));
				summonType1.setPassport(summonDetails.getSummonTypenewDto().getDirpassport());
				summonType1.setPanNumber(summonDetails.getSummonTypenewDto().getDirpanNumber());

			} else if (summonDetails.getSummonTypenewDto().getIndividualId() == 2) {

				summonType1 = new SummonType();

				summonType1.setRegistration_no(summonDetails.getSummonTypenewDto().getFdirReg_no());
				summonType1.setName(summonDetails.getSummonTypenewDto().getFdirName());
				summonType1.setEmail(summonDetails.getSummonTypenewDto().getFdirEmail());
				summonType1.setOffJoinDate(summonDetails.getSummonTypenewDto().getFdiroffJoinDate());
				summonType1.setAddress(summonDetails.getSummonTypenewDto().getFdirAddr());
				summonType1.setMobileNo(summonDetails.getSummonTypenewDto().getFdirMobile());
				summonType1.setPassport(summonDetails.getSummonTypenewDto().getFdirpassport());
				summonType1.setPanNumber(summonDetails.getSummonTypenewDto().getFdirpanNumber());

				summonType1
						.setIndividualType(new IndividualType(summonDetails.getSummonTypenewDto().getIndividualId()));
			} else if (summonDetails.getSummonTypenewDto().getIndividualId() == 3) {

				summonType1 = new SummonType();

				summonType1.setName(summonDetails.getSummonTypenewDto().getEmpName());

				summonType1.setDesignation(summonDetails.getSummonTypenewDto().getEmpdesgi());

				summonType1.setSex(summonDetails.getSummonTypenewDto().getEmpsex());

				summonType1.setDob(summonDetails.getSummonTypenewDto().getEmpDob());

				summonType1.setNationalId(summonDetails.getSummonTypenewDto().getEmpnatid());

				summonType1.setNationality(summonDetails.getSummonTypenewDto().getEmpNation());
				summonType1.setPassport(summonDetails.getSummonTypenewDto().getEmpPassport());
				summonType1.setIssueDate(summonDetails.getSummonTypenewDto().getEmpPassDate());
				summonType1.setPlaceof_issued(summonDetails.getSummonTypenewDto().getEmpissuplace());
				summonType1.setOffJoinDate(summonDetails.getSummonTypenewDto().getEmpoffdate());
				summonType1.setAddress(summonDetails.getSummonTypenewDto().getEmpAddress());
				summonType1.setMobileNo(summonDetails.getSummonTypenewDto().getEmpMobile());
				summonType1.setEmail(summonDetails.getSummonTypenewDto().getEmpemail());
				summonType1
						.setIndividualType(new IndividualType(summonDetails.getSummonTypenewDto().getIndividualId()));

			}

			else if (summonDetails.getSummonTypenewDto().getIndividualId() == 4) {
				summonType1 = new SummonType();
				summonType1.setName(summonDetails.getSummonTypenewDto().getFempName());
				summonType1.setDesignation(summonDetails.getSummonTypenewDto().getFempdesgi());
				summonType1.setSex(summonDetails.getSummonTypenewDto().getFempsex());
				summonType1.setDob(summonDetails.getSummonTypenewDto().getFempDob());
				summonType1.setNationalId(summonDetails.getSummonTypenewDto().getFempnatid());
				summonType1.setNationality(summonDetails.getSummonTypenewDto().getFempNation());
				summonType1.setPassport(summonDetails.getSummonTypenewDto().getFempPassport());
				summonType1.setIssueDate(summonDetails.getSummonTypenewDto().getFempPassDate());
				summonType1.setPlaceof_issued(summonDetails.getSummonTypenewDto().getFempissuplace());
				summonType1.setOffJoinDate(summonDetails.getSummonTypenewDto().getFempoffdate());
				summonType1.setAddress(summonDetails.getSummonTypenewDto().getFempAddress());
				summonType1.setMobileNo(summonDetails.getSummonTypenewDto().getFempMobile());
				summonType1.setEmail(summonDetails.getSummonTypenewDto().getFempemail());
				summonType1
						.setIndividualType(new IndividualType(summonDetails.getSummonTypenewDto().getIndividualId()));
			}

			else if (summonDetails.getSummonTypenewDto().getIndividualId() == 5
					|| summonDetails.getSummonTypenewDto().getIndividualId() == 6) {
				summonType1 = new SummonType();

				summonType1.setIsCin(summonDetails.getSummonTypenewDto().getIsACin());
				summonType1.setName(summonDetails.getSummonTypenewDto().getAgentName());
				summonType1.setNameCompany(summonDetails.getSummonTypenewDto().getAgentcompany());
				summonType1.setAddress(summonDetails.getSummonTypenewDto().getAgentAddress());
				summonType1.setMobileNo(summonDetails.getSummonTypenewDto().getAgentMobile());
				summonType1.setEmail(summonDetails.getSummonTypenewDto().getAgentEmail());
				summonType1.setRegistration_no(summonDetails.getSummonTypenewDto().getAgentRegno());
				summonType1
						.setIndividualType(new IndividualType(summonDetails.getSummonTypenewDto().getIndividualId()));
				summonType1.setPassport(summonDetails.getSummonTypenewDto().getAgentpanNumber());
				summonType1.setPanNumber(summonDetails.getSummonTypenewDto().getAgentpanNumber());

			} else {
				summonType1 = new SummonType();
				summonType1.setIsCin(summonDetails.getSummonTypenewDto().getIsOCin().replaceAll(",", ""));
				summonType1.setName(summonDetails.getSummonTypenewDto().getOthName());
				summonType1.setRelationwithcompany(summonDetails.getSummonTypenewDto().getOthRelation());
				summonType1.setAddress(summonDetails.getSummonTypenewDto().getOthAddress());
				summonType1.setMobileNo(summonDetails.getSummonTypenewDto().getOthMobile());
				summonType1.setEmail(summonDetails.getSummonTypenewDto().getOthEmail());
				summonType1.setRegistration_no(summonDetails.getSummonTypenewDto().getOthRegno());
				summonType1
						.setIndividualType(new IndividualType(summonDetails.getSummonTypenewDto().getIndividualId()));
				summonType1.setPassport(summonDetails.getSummonTypenewDto().getOthpanNumber());
				summonType1.setPanNumber(summonDetails.getSummonTypenewDto().getOthpassport());
			}

			companySummon.getSummonType().add(summonType1);
			if (companySummon.getCompanyType() == null) {
				CompanyType comp = new CompanyType();
				comp.setId(1);
				companySummon.setCompanyType(comp);
			}
			saveSummon.getCompanySummon().add(companySummon);
			addNoticeSummonRepository.save(saveSummon);
			String loginUName = appUserDAO.findUserDetails(getUserDetails()).getFirstName() != null
					? appUserDAO.findUserDetails(getUserDetails()).getFirstName()
					: ""

							+ appUserDAO.findUserDetails(getUserDetails()).getMiddleName() != null
									? appUserDAO.findUserDetails(getUserDetails()).getMiddleName()
									: "" + appUserDAO.findUserDetails(getUserDetails()).getLastName() != null
											? appUserDAO.findUserDetails(getUserDetails()).getLastName()
											: "";

			auditBeanBo.setAuditBean(Integer.parseInt(userDetailsService.getUserDetails().getUserId().toString()),
					loginUName, "User", Integer.parseInt(userDetailsService.getUserDetails().getUserId().toString()),
					utils.getMessage("log.NoticeSummon.save"), utils.getMessage("log.NoticeSummon.saved"), loginUName,
					"true");
			auditBeanBo.save();
		}

		SummonDetails summonDetails1 = new SummonDetails();
		SummonDetails findByCaseId = summonDao.findByCaseId(saveSummon.getCaseId());
		if (findByCaseId != null) {
			summonDetails1.setId(findByCaseId.getId());
			modelMap.addAttribute("summonbycaseid", findByCaseId);
			modelMap.addAttribute("summonbycaseidCompanyList", findByCaseId.getCompanySummon());
		}
		summonDetails1.setCaseId(saveSummon.getCaseId());
		summonDetails1.getCompanySummonsDto().setCin(companySummon1.getCin());
		summonDetails1.getCompanySummonsDto().setAddress(companySummon1.getAddress());
		summonDetails1.getCompanySummonsDto().setCompanyName(companySummon1.getCompanyName());
		summonDetails1.getCompanySummonsDto().setEmail(companySummon1.getEmail());

		List<IndividualType> individualType = idividualRepo.findAll();
		modelMap.addAttribute("individualTlist", individualType);
		modelMap.addAttribute("summonDetails", summonDetails1);

		return "notice/CreateNotice";
	}

	@RequestMapping(value = "/checkDraftForEdit", method = RequestMethod.POST)
	public @ResponseBody int getSummonNoticeGenertion(@RequestParam("type") String type,
			@RequestParam("caseId") Long caseId, @RequestParam("summontypeid") int summontypeid, Model model) {
		int status = 0;

		SummonStatus summon = summonDao.getSummonStatusBysummonId(new SummonType(summontypeid));
		InitiateSummonDraft summonDraft = summonDao.findSummonDraftByCaseDetails(new CaseDetails(caseId),
				new SummonType(summontypeid));
     //   SummonTemplate  st = summonDao.getsum
		/*if (summon != null && summon.getAprrovalStage1() == true) {
			status = SnmsConstant.NOTICE_SUMMON_PROGRESS;
			return status;

		}
*/
		
		if (summon != null ) {
			status = SnmsConstant.NOTICE_SUMMON_PROGRESS;
			return status;

		}
         else if (summonDraft!=null) {
 			status = SnmsConstant.NOTICE_SUMMON_DRAFT;
 			return status;
		}
		
		else {

			NoticeStatus notice = noticeDao.getNoticeStatusBysummonId(new SummonType(summontypeid));
			InitiateNoticeDraft noticeDraft = summonDao.findNoticeDraftByCaseDetails(new CaseDetails(caseId),
					new SummonType(summontypeid));
			if (notice != null && notice.getAprrovalStage1() == true) {
				status = SnmsConstant.NOTICE_SUMMON_PROGRESS;
				return status;
			}
			else if (noticeDraft!=null) {
				status = SnmsConstant.NOTICE_SUMMON_DRAFT;
			}

		}
		return status = SnmsConstant.NOTICE_SUMMON_NOT_GENRATED;
	}

	@RequestMapping(value = "addEditSummonNew", method = RequestMethod.POST)
	public String saveSummonEditDetails(@Valid @ModelAttribute("summonDetails") SummonDetails summonDetails,
			BindingResult result, ModelMap modelMap) throws Exception {
		if (null == summonDetails || summonDetails.equals(0)) {
			return "notice/CreateNotice";
		}
		SummonDetails saveSummon;
		// gouthami 15/09/2020
		SummonNoticeValidation sumVal = new SummonNoticeValidation();
		sumVal.validateIndivialTypeOnly(summonDetails, result);
		if (result.hasErrors()) {
			// summonDetails.getSummonTypeDto().setType(summonDetails.getSummonTypeDto().getType());
			List<IndividualType> individualType = idividualRepo.findAll();
			modelMap.addAttribute("individualTlist", individualType);
			if (null != summonDetails)
				modelMap.addAttribute("summonDetails", summonDetails);
			else
				modelMap.addAttribute("summonDetails", new SummonDetails());
			return "notice/CreateNotice";
		}
		SummonType summonType1 = null;
		// gouthami 15/09/2020
		// CompanySummon companySummon1 = new CompanySummon();
		if (summonDetails.getId() != 0) {
			saveSummon = summonDao.findByCaseId(summonDetails.getCaseId());
			saveSummon.setId(summonDetails.getId());
			int companyId = summonDao.findBySummonTypeId(summonDetails.getSummonTypenewDto().getId());

			CompanySummon companySummon = companySummonRepo.findAllById(companyId);
			if (companyId != summonDetails.getCompanySummonsDto().getId()) {
				String CompName = companySummonRepo.findAllById(summonDetails.getCompanySummonsDto().getId())
						.getCompanyName();
				summonDao.updateCompany(summonDetails.getCompanySummonsDto().getId(),
						summonDetails.getSummonTypenewDto().getId());

				String loginUName = appUserDAO.findUserDetails(getUserDetails()).getFirstName() != null
						? appUserDAO.findUserDetails(getUserDetails()).getFirstName()
						: ""

								+ appUserDAO.findUserDetails(getUserDetails()).getMiddleName() != null
										? appUserDAO.findUserDetails(getUserDetails()).getMiddleName()
										: "" + appUserDAO.findUserDetails(getUserDetails()).getLastName() != null
												? appUserDAO.findUserDetails(getUserDetails()).getLastName()
												: "";
				String individulName = null;
				if (summonDetails.getSummonTypenewDto().getIndividualId() == 1) {
					individulName = summonDetails.getSummonTypenewDto().getDirName();
				}
				else if (summonDetails.getSummonTypenewDto().getIndividualId() == 2) {
					individulName = summonDetails.getSummonTypenewDto().getFdirName();
				}
				else if (summonDetails.getSummonTypenewDto().getIndividualId() == 3) {
					individulName = summonDetails.getSummonTypenewDto().getEmpName();
				}
				else if (summonDetails.getSummonTypenewDto().getIndividualId() == 4) {
					individulName = summonDetails.getSummonTypenewDto().getFempName();
				}
				else if (summonDetails.getSummonTypenewDto().getIndividualId() == 5
						|| summonDetails.getSummonTypenewDto().getIndividualId() == 6) {
					individulName = summonDetails.getSummonTypenewDto().getAgentName();
				}
				auditBeanBo.setAuditBean(Integer.parseInt(userDetailsService.getUserDetails().getUserId().toString()),
						loginUName, "User",
						Integer.parseInt(userDetailsService.getUserDetails().getUserId().toString()),
						utils.getMessage("log.individual.compUp"),
						individulName+ " "
								+ utils.getMessage("log.individual.compUpdated") + " " + companySummon.getCompanyName()
								+ " " + utils.getMessage("log.individual.compUpdated1") + " " + CompName + " "
								+ utils.getMessage("log.individual.compUpdatedBy") + " "
								+ userDetailsService.getFullName(userDetailsService.getUserDetailssss()),
						loginUName, "true");
				auditBeanBo.save();
			}

			summonDetails.getSummonTypenewDto()
					.setIndividualType(new IndividualType(summonDetails.getSummonTypenewDto().getIndividualId()));
			/// summon

			// String Type =
			// String.valueOf(summonDetails.getSummonTypenewDto().getIndividualType().getIndividualId())
			// ;

			summonType1 = new SummonType();
			if (summonDetails.getSummonTypenewDto().getId() != 0) {

				if (summonDetails.getSummonTypenewDto().getIndividualId() == 1) {

					// summonType1=new SummonType();
					summonType1.setId(summonDetails.getSummonTypenewDto().getId());
					summonType1.setRegistration_no(summonDetails.getSummonTypenewDto().getDirReg_no());
					summonType1.setName(summonDetails.getSummonTypenewDto().getDirName());
					summonType1.setEmail(summonDetails.getSummonTypenewDto().getDirEmail());
					summonType1.setOffJoinDate(summonDetails.getSummonTypenewDto().getDiroffJoinDate());
					summonType1.setAddress(summonDetails.getSummonTypenewDto().getDirAddr());
					summonType1.setMobileNo(summonDetails.getSummonTypenewDto().getDirMobile());
					summonType1.setIndividualType(
							new IndividualType(summonDetails.getSummonTypenewDto().getIndividualId()));
					summonType1.setPanNumber(summonDetails.getSummonTypenewDto().getDirpanNumber());
					summonType1.setPassport(summonDetails.getSummonTypenewDto().getDirpassport());
				} else if (summonDetails.getSummonTypenewDto().getIndividualId() == 2) {

					// summonType1=new SummonType();
					summonType1.setId(summonDetails.getSummonTypenewDto().getId());
					summonType1.setRegistration_no(summonDetails.getSummonTypenewDto().getFdirReg_no());
					summonType1.setName(summonDetails.getSummonTypenewDto().getFdirName());
					summonType1.setEmail(summonDetails.getSummonTypenewDto().getFdirEmail());
					summonType1.setOffJoinDate(summonDetails.getSummonTypenewDto().getFdiroffJoinDate());
					summonType1.setAddress(summonDetails.getSummonTypenewDto().getFdirAddr());
					summonType1.setMobileNo(summonDetails.getSummonTypenewDto().getFdirMobile());
					summonType1.setPanNumber(summonDetails.getSummonTypenewDto().getFdirpanNumber());
					summonType1.setPassport(summonDetails.getSummonTypenewDto().getFdirpassport());
					summonType1.setIndividualType(
							new IndividualType(summonDetails.getSummonTypenewDto().getIndividualId()));
				} else if (summonDetails.getSummonTypenewDto().getIndividualId() == 3) {

					// summonType1 = new SummonType();

					summonType1.setId(summonDetails.getSummonTypenewDto().getId());

					summonType1.setName(summonDetails.getSummonTypenewDto().getEmpName());

					summonType1.setDesignation(summonDetails.getSummonTypenewDto().getEmpdesgi());

					summonType1.setSex(summonDetails.getSummonTypenewDto().getEmpsex());

					summonType1.setDob(summonDetails.getSummonTypenewDto().getEmpDob());

					summonType1.setNationalId(summonDetails.getSummonTypenewDto().getEmpnatid());

					summonType1.setNationality(summonDetails.getSummonTypenewDto().getEmpNation());
					summonType1.setPassport(summonDetails.getSummonTypenewDto().getEmpPassport());
					summonType1.setIssueDate(summonDetails.getSummonTypenewDto().getEmpPassDate());
					summonType1.setPlaceof_issued(summonDetails.getSummonTypenewDto().getEmpissuplace());
					summonType1.setOffJoinDate(summonDetails.getSummonTypenewDto().getEmpoffdate());
					summonType1.setAddress(summonDetails.getSummonTypenewDto().getEmpAddress());
					summonType1.setMobileNo(summonDetails.getSummonTypenewDto().getEmpMobile());
					summonType1.setEmail(summonDetails.getSummonTypenewDto().getEmpemail());
					summonType1.setIndividualType(
							new IndividualType(summonDetails.getSummonTypenewDto().getIndividualId()));
					summonType1.setPanNumber(summonDetails.getSummonTypenewDto().getEmpPanNumber());

				}

				else if (summonDetails.getSummonTypenewDto().getIndividualId() == 4) {
					// summonType1 = new SummonType();

					summonType1.setId(summonDetails.getSummonTypenewDto().getId());
					summonType1.setName(summonDetails.getSummonTypenewDto().getFempName());

					summonType1.setDesignation(summonDetails.getSummonTypenewDto().getFempdesgi());

					summonType1.setSex(summonDetails.getSummonTypenewDto().getFempsex());

					summonType1.setDob(summonDetails.getSummonTypenewDto().getFempDob());

					summonType1.setNationalId(summonDetails.getSummonTypenewDto().getFempnatid());

					summonType1.setNationality(summonDetails.getSummonTypenewDto().getFempNation());
					summonType1.setPassport(summonDetails.getSummonTypenewDto().getFempPassport());
					summonType1.setIssueDate(summonDetails.getSummonTypenewDto().getFempPassDate());
					summonType1.setPlaceof_issued(summonDetails.getSummonTypenewDto().getFempissuplace());
					summonType1.setOffJoinDate(summonDetails.getSummonTypenewDto().getFempoffdate());
					summonType1.setAddress(summonDetails.getSummonTypenewDto().getFempAddress());
					summonType1.setMobileNo(summonDetails.getSummonTypenewDto().getFempMobile());
					summonType1.setEmail(summonDetails.getSummonTypenewDto().getFempemail());
					summonType1.setIndividualType(
							new IndividualType(summonDetails.getSummonTypenewDto().getIndividualId()));
					summonType1.setPanNumber(summonDetails.getSummonTypenewDto().getFempPanNumber());

				}

				else if (summonDetails.getSummonTypenewDto().getIndividualId() == 5
						|| summonDetails.getSummonTypenewDto().getIndividualId() == 6) {
					// summonType1 = new SummonType();
					summonType1.setId(summonDetails.getSummonTypenewDto().getId());
					summonType1.setIsCin(summonDetails.getSummonTypenewDto().getIsACin());
					summonType1.setName(summonDetails.getSummonTypenewDto().getAgentName());
					summonType1.setNameCompany(summonDetails.getSummonTypenewDto().getAgentcompany());
					summonType1.setAddress(summonDetails.getSummonTypenewDto().getAgentAddress());
					summonType1.setMobileNo(summonDetails.getSummonTypenewDto().getAgentMobile());
					summonType1.setEmail(summonDetails.getSummonTypenewDto().getAgentEmail());
					summonType1.setRegistration_no(summonDetails.getSummonTypenewDto().getAgentRegno());
					summonType1.setIndividualType(
							new IndividualType(summonDetails.getSummonTypenewDto().getIndividualId()));
					summonType1.setPanNumber(summonDetails.getSummonTypenewDto().getAgentpanNumber());
					summonType1.setPassport(summonDetails.getSummonTypenewDto().getAgentpassport());

				} else {
					summonType1.setId(summonDetails.getSummonTypenewDto().getId());
					summonType1.setIsCin(summonDetails.getSummonTypenewDto().getIsOCin().replaceAll(",", ""));
					summonType1.setName(summonDetails.getSummonTypenewDto().getOthName());
					summonType1.setRelationwithcompany(summonDetails.getSummonTypenewDto().getOthRelation());
					summonType1.setAddress(summonDetails.getSummonTypenewDto().getOthAddress());
					summonType1.setMobileNo(summonDetails.getSummonTypenewDto().getOthMobile());
					summonType1.setEmail(summonDetails.getSummonTypenewDto().getOthEmail());
					summonType1.setRegistration_no(summonDetails.getSummonTypenewDto().getOthRegno());
					summonType1.setPanNumber(summonDetails.getSummonTypenewDto().getOthpanNumber());
					summonType1.setPassport(summonDetails.getSummonTypenewDto().getOthpassport());
					summonType1.setIndividualType(
							new IndividualType(summonDetails.getSummonTypenewDto().getIndividualId()));

				}

				/*
				 * companySummon.getSummonType().add(summonType1);
				 * saveSummon.getCompanySummon().add(companySummon);
				 */

				SummonTypeNewDetails.save(summonType1);
			}
			// firstTime occur only
		}

		SummonDetails summonDetails1 = new SummonDetails();
		commonObjectSummon(modelMap, summonDetails.getCaseId());

		// SummonDetails findByCaseId=summonDao.findByCaseId(summonDetails.getCaseId());

		SummonDetails findByCaseId = summonDtlRepo.findAllByCaseId(summonDetails.getCaseId());
		List<IndividualType> individualType = idividualRepo.findAll();

		if (findByCaseId != null) {
			summonDetails1.setId(findByCaseId.getId());
			summonDetails1.setCaseId(summonDetails.getCaseId());
			modelMap.addAttribute("summonbycaseid", findByCaseId);
			modelMap.addAttribute("summonbycaseidCompanyList", findByCaseId.getCompanySummon());
			for (int i = 0; i < findByCaseId.getCompanySummon().size(); i++) {
				summonDetails1.getCompanySummonsDto().setId(findByCaseId.getCompanySummon().get(i).getId());
				for (int j = 0; j < findByCaseId.getCompanySummon().get(i).getSummonType().size(); j++) {
					summonDetails1.getSummonTypenewDto()
							.setId(findByCaseId.getCompanySummon().get(i).getSummonType().get(j).getId());
				}
			}
		}

		// summonDetails1.setCaseId(caseId);
		modelMap.addAttribute("summonDetails", summonDetails1);
		modelMap.addAttribute("individualTlist", individualType);
		modelMap.addAttribute("message", "Individual details has been updated");
        modelMap.addAttribute("caseId", summonDetails1.getCaseId());
		return "notice/CreateNoticeSucess";
	}

	@SuppressWarnings("unused")
	@RequestMapping(value = "editIndividual", method = RequestMethod.POST)
	public String editIndividual(@ModelAttribute("summonDetails") SummonDetails summonDetails, BindingResult result,
			ModelMap modelMap, @RequestParam("editDeleteId") int editId, @RequestParam("compId") int compid,
			@RequestParam("dataId") int dataId) throws Exception {
		if (null == summonDetails || summonDetails.equals(0)) {
			return "notice/CreateNotice";
		}
		commonObjectSummon(modelMap, summonDetails.getCaseId());
		SummonDetails findByCaseId;
		SNMSValidator snmsValid = new SNMSValidator();
		if (summonDetails != null)
			findByCaseId = summonDao.findByCaseId(summonDetails.getCaseId());
		else
			findByCaseId = summonDao.findByCaseId(new SummonDetails().getCaseId());

		if (!snmsValid.getValidInteger(editId) || !snmsValid.getValidInteger(compid)
				|| !snmsValid.getValidInteger(dataId)) {
			List<IndividualType> individualType = idividualRepo.findAll();
			modelMap.addAttribute("individualTlist", individualType);
			modelMap.addAttribute("summonDetails", summonDetails);
			modelMap.addAttribute("errorCaseID", "Invalid CaseId");
			return "notice/CreateNotice";
		}
		SummonType findByTypeDetails = SummonTypeNewDetails.findById(editId).get();
		// int compId
		CompanySummon compSummon = new CompanySummon();
		for (int i = 0; i < findByCaseId.getCompanySummon().size(); i++) {
			if (findByCaseId.getCompanySummon().get(i).getId() == compid) {
				compSummon = findByCaseId.getCompanySummon().get(i);
				// findByCaseId.getCompanySummon().remove(i);
				break;
			}
		}
		if (findByCaseId != null) {
			findByCaseId.setCaseId(summonDetails.getCaseId());
			findByCaseId.getCompanySummonsDto().setCin(compSummon.getCin());
			findByCaseId.getCompanySummonsDto().setAddress(compSummon.getAddress());
			findByCaseId.getCompanySummonsDto().setCompanyName(compSummon.getCompanyName());
			findByCaseId.getCompanySummonsDto().setEmail(compSummon.getEmail());
			summonDetails.setId(findByCaseId.getId());
			modelMap.addAttribute("summonbycaseid", findByCaseId);
			modelMap.addAttribute("summonbycaseidCompanyList", findByCaseId.getCompanySummon());
		}

		// summonDetails.setCaseId(summonDetails.getCaseId());
		// modelMap.addAttribute("summonDetails",summonDetails);
		summonDetails.setEdit(true);
		if (dataId == 101) {
			summonDetails.setEditDisable(true);
		} else if (dataId == 104) {
			summonDetails.setEditDisable(false);
		}
		long Typeid = findByTypeDetails.getIndividualType().getIndividualId();
		String individualName = findByTypeDetails.getIndividualType().getIndividualName();
		SummonType summonType = new SummonType();
		summonType.setCompId(findByTypeDetails.getCompId());
		int companyId = summonDao.findBySummonTypeId(findByTypeDetails.getId());
		summonDetails.getCompanySummonsDto().setId(companyId);
		if (Typeid == 2) {
			summonType.setId(editId);
			summonType.setFdirName(findByTypeDetails.getName());
			summonType.setFdirReg_no(findByTypeDetails.getRegistration_no());
			summonType.setFdirAddr(findByTypeDetails.getAddress());
			summonType.setFdirEmail(findByTypeDetails.getEmail());
			summonType.setFdirMobile(findByTypeDetails.getMobileNo());
			summonType.setFdiroffJoinDate(findByTypeDetails.getOffJoinDate());
			summonType.setIndividualId(findByTypeDetails.getIndividualType().getIndividualId());

			summonDetails.setSummonTypenewDto(summonType);
		}
		if (Typeid == 1) {
			summonType.setId(editId);
			summonType.setDirName(findByTypeDetails.getName());
			summonType.setDirReg_no(findByTypeDetails.getRegistration_no());
			summonType.setDirAddr(findByTypeDetails.getAddress());
			summonType.setDirEmail(findByTypeDetails.getEmail());
			summonType.setDirMobile(findByTypeDetails.getMobileNo());
			summonType.setDiroffJoinDate(findByTypeDetails.getOffJoinDate());
			summonType.setIndividualId(findByTypeDetails.getIndividualType().getIndividualId());

			summonDetails.setSummonTypenewDto(summonType);
		}
		if (Typeid == 3) {
			summonType.setId(editId);
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

			summonDetails.setSummonTypenewDto(summonType);
		}

		if (Typeid == 4) {
			summonType.setId(editId);
			summonType.setFempName(findByTypeDetails.getName());
			summonType.setFempAddress(findByTypeDetails.getAddress());
			summonType.setFempdesgi(findByTypeDetails.getDesignation());
			summonType.setFempDob(findByTypeDetails.getDob());
			summonType.setFempemail(findByTypeDetails.getEmail());
			summonType.setFempissuplace(findByTypeDetails.getPlaceof_issued());
			summonType.setFempMobile(findByTypeDetails.getMobileNo());
			summonType.setFempnatid(findByTypeDetails.getNationalId());
			summonType.setFempNation(findByTypeDetails.getNationality());
			summonType.setFempoffdate(findByTypeDetails.getOffJoinDate());
			summonType.setFempPassDate(findByTypeDetails.getIssueDate());
			summonType.setFempPassport(findByTypeDetails.getPassport());
			summonType.setFempsex(findByTypeDetails.getSex());
			summonType.setIndividualId(findByTypeDetails.getIndividualType().getIndividualId());

			summonDetails.setSummonTypenewDto(summonType);
		}

		if (Typeid == 5 || Typeid == 6) {
			summonType.setId(editId);
			summonType.setAgentAddress(findByTypeDetails.getAddress());
			summonType.setAgentcompany(findByTypeDetails.getNameCompany());

			summonType.setAgentEmail(findByTypeDetails.getEmail());
			summonType.setAgentMobile(findByTypeDetails.getMobileNo());
			summonType.setAgentName(findByTypeDetails.getName());
			summonType.setAgentRegno(findByTypeDetails.getRegistration_no());
			summonType.setIsACin(findByTypeDetails.getIsCin());
			summonType.setIndividualId(findByTypeDetails.getIndividualType().getIndividualId());
			summonDetails.setSummonTypenewDto(summonType);
		}

		if (Typeid == 7) {
			summonType.setId(editId);
			summonType.setOthAddress(findByTypeDetails.getAddress());
			summonType.setOthRelation(findByTypeDetails.getRelationwithcompany());

			summonType.setOthEmail(findByTypeDetails.getEmail());
			summonType.setOthMobile(findByTypeDetails.getMobileNo());
			summonType.setOthName(findByTypeDetails.getName());
			summonType.setIsOCin(findByTypeDetails.getIsCin());
			summonType.setOthRegno(findByTypeDetails.getRegistration_no());
			summonType.setIndividualId(findByTypeDetails.getIndividualType().getIndividualId());
			summonDetails.setSummonTypenewDto(summonType);
		}
		// compSummon.getSummonType().add(summonType);
		// saveSummon.getCompanySummon().add(companySummon);
		List<IndividualType> individualType = idividualRepo.findAll();
		modelMap.addAttribute("summonDetails", summonDetails);
		modelMap.addAttribute("individualName", individualName);
		modelMap.addAttribute("TypeId", Typeid);
		modelMap.addAttribute("individualTlist", individualType);

		return "notice/CreateEditNotice";
	}

	@RequestMapping(value = "/getSummonList", method = RequestMethod.GET)
	public @ResponseBody List<SummonStatus> getSummonList(Model model) throws Exception {
		List<SummonStatus> summon = summonDao.findSummonByUserId(getUserDetails());
		return summon;
	}

	@RequestMapping(value = "/getNoticeList", method = RequestMethod.GET)
	public @ResponseBody List<NoticeStatus> getNoticeList(Model model) throws Exception {
		List<NoticeStatus> notice = summonDao.findNoticeByUserId(getUserDetails());
		return notice;
	}

	@RequestMapping(value = "/getApprovedNoticeList", method = RequestMethod.GET)
	public @ResponseBody List<NoticeStatus> getApprovedNoticeList(Model model) throws Exception {
		List<NoticeStatus> approvNotice = noticeDao.findNoticeByApprove();
		return approvNotice;
	}

	@RequestMapping(value = "/getEsignedNoticeList", method = RequestMethod.GET)
	public @ResponseBody List<NoticeStatus> getEsignedNoticeList(Model model) throws Exception {
		List<NoticeStatus> approvNotice = noticeDao.findNoticeByApprove();
		return approvNotice;
	}
	
	
	@RequestMapping(value = "/checkaddress", method = RequestMethod.GET)
	public @ResponseBody Boolean checkaddresst(Model model) throws Exception {
		UserDetails userDet = userDetailsRepo.findAllByUserId(getUserDetails());
		if(userDet.getUnit().getAddress_hi()!=null) {
			return true;
		}else {
			return false;
		}
		
	}
	@RequestMapping(value = "/getReportList", method = RequestMethod.GET)
	public @ResponseBody List<SummonStatus> getReportList(@RequestParam("status") int status,
			@RequestParam("radioValue") String type, @RequestParam("unitId") Long unitId,
			@RequestParam("inspList") int inspList, @RequestParam("startDate") String startDate,
			@RequestParam("endDate") String endDate, Model model) throws Exception {

		SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);

		// String dateInString = "7-Jun-2013";
		if (startDate != "" && endDate != "") {
			Date date = formatter.parse(startDate);
			Date date1 = formatter.parse(endDate);

			startDate = new SimpleDateFormat("yyyy-MM-dd").format(date).toString();
			endDate = new SimpleDateFormat("yyyy-MM-dd").format(date1).toString();
		}
		if (status == 1 && type.equalsIgnoreCase("S") && unitId == 0 && inspList == 0) {
			List<SummonStatus> approvedSummon = summonDao.findSummonsByAll(startDate, endDate);
			return approvedSummon;
		}
		if (status == 2 && type.equalsIgnoreCase("S") && unitId == 0 && inspList == 0) {
			List<SummonStatus> approvedSummon = summonDao.findSummonsByApprove(startDate, endDate);
			return approvedSummon;
		}

		if (status == 3 && type.equalsIgnoreCase("S") && unitId == 0 && inspList == 0) {
			List<SummonStatus> EsignedSummon = summonDao.findSummon_signed(getUserDetails(), startDate, endDate);
			return EsignedSummon;
		}

		if (status == 4 && type.equalsIgnoreCase("S") && unitId == 0 && inspList == 0) {
			List<SummonStatus> PendingSummon = summonDao.findSummon_Peding(getUserDetails(), startDate, endDate);
			return PendingSummon;
		}

		else if (type.equalsIgnoreCase("S") && unitId != 0L) {
			List<SummonStatus> approvedunitSummon = summonDao.findSummonsByUnit(unitId, status, inspList, startDate,
					endDate);
			return approvedunitSummon;
		}

		else if (type.equalsIgnoreCase("S") && unitId == 0L && inspList != 0) {
			List<SummonStatus> approvedunitSummon = summonDao.findSummonsByUnit(unitId, status, inspList, startDate,
					endDate);
			return approvedunitSummon;
		}

		return null;

	}

	@RequestMapping(value = "/getReportNoticeList", method = RequestMethod.GET)
	public @ResponseBody List<NoticeStatus> getReportNoticeList(@RequestParam("status") int status,
			@RequestParam("radioValue") String type, @RequestParam("unitId") Long unitId,
			@RequestParam("inspList") int inspList, @RequestParam("startDate") String startDate,
			@RequestParam("endDate") String endDate, Model model) throws Exception {

		if (status == 1 && type.equalsIgnoreCase("S") && unitId == 0 && inspList == 0) {
			List<NoticeStatus> AllNOtices = noticeDao.findNoticeByAll(startDate, endDate);
			return AllNOtices;
		}

		if (status == 2 && type.equalsIgnoreCase("N") && unitId == 0 && inspList == 0) {
			List<NoticeStatus> approvedSummon = noticeDao.findNoticeByApprove(startDate, endDate);
			return approvedSummon;
		}

		if (status == 3 && type.equalsIgnoreCase("N") && unitId == 0 && inspList == 0) {
			List<NoticeStatus> EsignedSummon = summonDao.findNotice_signed(getUserDetails(), startDate, endDate);
			return EsignedSummon;
		}

		if (status == 4 && type.equalsIgnoreCase("N") && unitId == 0 && inspList == 0) {
			List<NoticeStatus> PendingSummon = summonDao.findNotice_Peding(getUserDetails(), startDate, endDate);
			return PendingSummon;
		}

		else if (type.equalsIgnoreCase("N") && unitId != 0L) {
			List<NoticeStatus> approvedunitSummon = summonDao.findNoticeByUnit(unitId, status, inspList, startDate,
					endDate);
			return approvedunitSummon;
		} else if (type.equalsIgnoreCase("S") && unitId == 0L && inspList != 0) {
			List<NoticeStatus> approvedunitSummon = summonDao.findNoticeByUnit(unitId, status, inspList, startDate,
					endDate);
			return approvedunitSummon;
		}

		return null;

	}
}
