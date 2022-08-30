package com.snms.controllers;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

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
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.snms.dao.AppRoleDAO;
import com.snms.dao.AppUserDAO;
import com.snms.dao.InspectorDao;
import com.snms.dao.OfficeOrderDao;
import com.snms.dao.SummonDao;
import com.snms.dao.prosecutorDao;
import com.snms.dto.InspectorDTO;
import com.snms.dto.InspectorEditDTO;
import com.snms.dto.InspectorListDTO;
import com.snms.dto.OfficeOrderTempDto;
import com.snms.dto.PendingForApprovalDTO;
import com.snms.entity.AddDesignation;
import com.snms.entity.AppUser;
import com.snms.entity.CaseDetails;
import com.snms.entity.Company;
import com.snms.entity.InitiatorOfficeOrderDraft;
import com.snms.entity.Inspector;
import com.snms.entity.OfficeOrder;
import com.snms.entity.PersonDATA;
import com.snms.entity.PersonDetails;
import com.snms.entity.RelationpersonCompany;
import com.snms.entity.UnitDetails;
import com.snms.entity.UserDetails;
import com.snms.entity.UserRole;
import com.snms.entity.inspectorHistory;
import com.snms.entity.personcompanyApproval;
import com.snms.service.AddCompanyRepository;
import com.snms.service.AddPersonRepository;
import com.snms.service.AuditBeanBo;
import com.snms.service.CaseDetailsRepository;
import com.snms.service.FinancialYearRepository;
import com.snms.service.InitiatorOfficeOrderDraftRepository;
import com.snms.service.InspectorHistoryRepository;
import com.snms.service.InspectorRepository;
import com.snms.service.OfficeOrderRepository;
import com.snms.service.OfficeOrderTemplateRepository;
import com.snms.service.RelationpersonCompanyRepository;
import com.snms.service.UnitDetailsRepository;
import com.snms.service.UserDetailsRepository;
import com.snms.service.UserDetailsServiceImpl;
import com.snms.service.UserManagementCustom;
import com.snms.service.UserRoleRepository;
import com.snms.utils.SnmsConstant;
import com.snms.utils.Utils;
import com.snms.validation.CaseJsonResponse;
import com.snms.validation.SNMSValidator;
import com.snms.validation.UserValidation;

@Controller
public class ProsecuterController {
	private static final Logger logger = Logger.getLogger(ProsecuterController.class);

	@Autowired
	private CaseDetailsRepository caseDetailsRepository;

	@Autowired
	private UnitDetailsRepository unitDetailsRepo;
	@Autowired
	private AppRoleDAO appRoleDAO;
	@Autowired
	private AddCompanyRepository addCompanyRepo;

	@Autowired
	private UserManagementCustom userMangCustom;

	@Autowired
	private AuditBeanBo auditBeanBo;

	@Autowired
	private InspectorHistoryRepository inspectorHistoryRepository;

	@Autowired
	private InspectorRepository inspectorRepo;

	@Autowired
	private OfficeOrderRepository officeOrderRepo;
	
	@Autowired
	RelationpersonCompanyRepository rpcRepo;

	@Autowired
	private OfficeOrderTemplateRepository officeOrderTempRepo;
	
	@Autowired
	private UserRoleRepository userRoleRepo;
	
	@Autowired
	private prosecutorDao proDao;

	@Autowired
	private UserDetailsServiceImpl userDetailsService;

	@Autowired
	private InitiatorOfficeOrderDraftRepository initiatorOffOrdRepo;

	@Autowired
	private OfficeOrderDao officeOrderDao;

	@Autowired
	private UserDetailsRepository userDetailsRepo;

	@Autowired
	private SummonDao summonDao;

	@Autowired
	private FinancialYearRepository financialYearRepo;

	@Autowired
	private AppUserDAO appUserDAO;
	@Autowired
	InspectorDao inspectorDao;
	
	@Autowired
	private AddPersonRepository addPersonRepo;

	@Autowired
	private Utils utils;
	@Value("${file.upload}")
	public String filePath;

	@RequestMapping(value = "/proHome")
	public String proHome(ModelMap model)throws Exception {
		/* Commented by Anuj
		List<CaseDetails> caseList = caseDetailsRepository.findAllByCaseStage(2 ,Sort.by(Sort.Direction.DESC, "id"));
		
		*/
		List<Object[]> caseList = proDao.findAllCases();
		List<RelationpersonCompany> gamsApprovedpersonlist  =userMangCustom.findApprovePersonListByUserID(1, userDetailsService.getUserDetails().getUserId(),2);
		List<RelationpersonCompany> gamsPendingpersonlist  =userMangCustom.findpersonPendingListByUserID(1, userDetailsService.getUserDetails().getUserId(),2);
			
		model.addAttribute("totalCase", caseList.size());
		model.addAttribute("pending", gamsPendingpersonlist.size());
		model.addAttribute("approvedPerson", gamsApprovedpersonlist.size());
		return "prosecutor/proHome";
	}
	
	@RequestMapping(value = "/uploadFileStatus")
	public String uploadFileStatus(ModelMap model)throws Exception {
		
		return "FileErrorPage";
	}
	
	
	@RequestMapping(value = "/padoHome")
	public String padoHome(ModelMap model)throws Exception
	{ 		
		
		/* Commented by Anuj
		List<CaseDetails> caseList = caseDetailsRepository.findAllByCaseStage(2 ,Sort.by(Sort.Direction.DESC, "id"));
		
		*/
		List<Object[]> caseList = proDao.findAllCases();
		List<Object[]> penOrder = userMangCustom.findOfficeOrderPendingForApproval(3);
		
		List<personcompanyApproval> gamsPendingpersonlist  =userMangCustom.findpersonPendingListForProApproval(1, userDetailsService.getUserDetails().getUserId(),2);
		List<RelationpersonCompany> gamsApprovedpersonlist  =userMangCustom.findpersonAorrovedListForProApproval(1, userDetailsService.getUserDetails().getUserId(),2);
		List<RelationpersonCompany> gamsTotalpersonlist  =userMangCustom.findTotalpersonListForProApproval(1, userDetailsService.getUserDetails().getUserId(),2);
		
		
		model.addAttribute("totalCase", caseList.size());
		model.addAttribute("pending", gamsPendingpersonlist.size());
		model.addAttribute("totalApproved", gamsApprovedpersonlist.size());
		model.addAttribute("totalPerson", gamsTotalpersonlist.size());
		model.addAttribute("penOrder", penOrder.size());
		return "prosecutorAdo/padoHome";
	}

	@RequestMapping(value = "/addProcesecutionOrder")
	public String legacyOrder(Model model) {

		CaseDetails caseDetails = new CaseDetails();
		caseDetails.setCaseId("SFIO/INV/2022/");
		caseDetails.setEditLagacy(false);
		model.addAttribute("caseDetails", caseDetails);
		List<Inspector> inspList = new ArrayList<Inspector>();
//		List<UserDetails> unituserList = userMangCustom.findByUnit(new UnitDetails(1L));
		List<UserDetails> unituserList = userMangCustom.findByRole(SnmsConstant.Role_USER, new UnitDetails(1L));
		int count = 1;
		for (UserDetails user : unituserList) {
			inspList.add(new Inspector(count,
					user.getSalutation() + " " + userDetailsService.getFullName(user) + " ("
							+ user.getUnit().getUnitName() + ")",
					user.getDesignation().getDesignation(), 2, user.getUserId().getUserId(), false,
					userMangCustom.getCasesAssigned(user.getUserId().getUserId())));
			count++;
		}
		model.addAttribute("inspList", inspList);
		List<UnitDetails> unitList = unitDetailsRepo.findAll(Sort.by(Sort.Direction.ASC, "unitId"));
		unitList.removeIf(x -> x.getUnitId() == 3);
		model.addAttribute("unitList", unitList);
		return "prosecutor/addLegacyCase";
	}

	@RequestMapping(value = "/addProsecutioncase", method = RequestMethod.POST)
	public @ResponseBody CaseJsonResponse addLegacyDetails(HttpServletRequest request,
			@Valid @ModelAttribute("caseDetails") CaseDetails caseDetails, BindingResult bindResult, Model model)
			throws Exception {
		CaseJsonResponse caseJsonResponse = new CaseJsonResponse();
		UserValidation validation = new UserValidation();
		validation.validateproLegacyDetails(caseDetails, bindResult);

		if (caseDetails.getId() == null) {
			if (officeOrderDao.uniqueCaseCheck(caseDetails.getCaseTitle(), "", "", 1) != null)
				bindResult.rejectValue("caseTitle", "errmsg.uniqueTitle");
			if (!"".equalsIgnoreCase(caseDetails.getMcaOrderNo().trim())) {
				if (officeOrderDao.uniqueCaseCheck("", caseDetails.getMcaOrderNo(), "", 2) != null)
					bindResult.rejectValue("mcaOrderNo", "errmsg.uniqueMcaOrder");
			}

			if (caseDetails.getUnitId() != null) {
				UnitDetails unitbyId = unitDetailsRepo.findById(caseDetails.getUnitId()).get();
				if (unitbyId == null || caseDetails.getUnitId() == 3L)
					bindResult.rejectValue("unitId", "msg.wrongId");
			}
			if (officeOrderDao.uniqueCaseCheck("", "", caseDetails.getCaseId(), 4) != null)
				bindResult.rejectValue("caseId", "errmsg.uniqueCaseId");
		}else {
			CaseDetails caseTitle = officeOrderDao.uniqueCaseCheck(caseDetails.getCaseTitle(), "", "", 1);
			if(caseTitle!=null) {
			if(caseTitle.getId()!= caseDetails.getId()) {
				bindResult.rejectValue("caseTitle", "errmsg.uniqueTitle");
			}
			}
			CaseDetails caseID = officeOrderDao.uniqueCaseCheck("", "", caseDetails.getCaseId(), 4);
			if(caseID!=null) {
			if ( caseID.getId()!= caseDetails.getId()) {
				bindResult.rejectValue("caseId", "errmsg.uniqueCaseId");
			}
		}

		}
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
			caseDetails.setOfficeOrderFile(null);
			caseDetails.setDeleteIoOrder(null);
			caseDetails.setCompanyOrder(null);
			caseJsonResponse.setErrorsMap(errors);
			caseJsonResponse.setCaseDetails(caseDetails);
			caseJsonResponse.setStatus("ERRORui");
			return caseJsonResponse;
		}

		Long id = (caseDetailsRepository.findMaxid() != null) ? (caseDetailsRepository.findMaxid() + 1) : 1;
		if (caseDetails.getId() != null) {

			caseDetails.setId(caseDetails.getId());
			// AppUser appUser = userDetailsService.getUserDetails();
			// Long id = (caseDetailsRepository.findMaxid() != null) ?
			// (caseDetailsRepository.findMaxid() + 1) : 1;

			String unit = "";
			/*
			 * if (caseDetails.getUnitId() == 1) unit = "UNIT-1"; else if
			 * (caseDetails.getUnitId() == 2) unit = "UNIT-2";
			 */

			caseDetails.setCaseId(caseDetails.getCaseId());

			if (null != caseDetails.getMcaDate() && !"".equals(caseDetails.getMcaDate())) {
				caseDetails.setMcaOrderDate(new SimpleDateFormat("dd/MM/yyyy").parse(caseDetails.getMcaDate()));
				if (!caseDetails.getMcaFile().isEmpty()) {
					String fileExt = caseDetails.getMcaFile().getOriginalFilename();
					fileExt = fileExt.substring(fileExt.lastIndexOf("."));
					caseDetails.setMcaOrderFile("MCA_" + id + fileExt);
					caseFileUpload(caseDetails.getMcaFile(), caseDetails.getMcaOrderFile());
				}
			}

			if (!caseDetails.getOfficeOrderFile().isEmpty()) {
				String fileExt = caseDetails.getOfficeOrderFile().getOriginalFilename();
				fileExt = fileExt.substring(fileExt.lastIndexOf("."));
				caseDetails.setLegacyOrderFile("Office_Order_" + id + fileExt);
				caseFileUpload(caseDetails.getOfficeOrderFile(), "Office_Order_" + id + fileExt);

			}
			caseDetails.setUnit(userDetailsService.getUserDetailssss().getUnit());
			// caseDetails.setCaseAddBy(2);
			InspectorListDTO inspcompList = getALLCompany(caseDetails.getId());
			boolean comflag = false;
			long compid = 0;

			List<Company> companyList = new ArrayList<Company>();
			for (int i = 0; i < caseDetails.getCompanyName().length; i++) {

				for (int j = 0; j < inspcompList.getCompanyList().size(); j++) {

					String compname = inspcompList.getCompanyList().get(j).getName().trim();
					if (compname.equals(caseDetails.getCompanyName()[i].trim())) {

						String compidName = inspcompList.getCompanyList().get(j).getName();
						Company comp = inspectorDao.findComapnyNameByCaseIdAndUserId(caseDetails.getId().intValue(),
								compidName);
						compid = comp.getId();
						comflag = true;
						break;
					}

				}

				if (comflag == true) {
					Company company = addCompanyRepo.findById(compid).get();
					companyList.add(company);
					comflag = false;
				} else {
					UserDetails user = userDetailsRepo.findAllByEmail(getLoginUserName());
					Company company = new Company(caseDetails, caseDetails.getCompanyName()[i], true, new Date(),user,1L,2L);
					
					companyList.add(company);
				}
			}

			if (caseDetails.getDeleteCompId() != null) {
				for (int k = 0; k < caseDetails.getDeleteCompId().length; k++) {
					String delteCompName = caseDetails.getDeleteCompId()[k];
					for (int j = 0; j < inspcompList.getCompanyList().size(); j++) {
						if (inspcompList.getCompanyList().get(j).getCompId() == Long.parseLong(delteCompName.trim())) {
							String compidName = inspcompList.getCompanyList().get(j).getName();
							Company comp = inspectorDao.findComapnyNameByCaseIdAndUserId(caseDetails.getId().intValue(),
									compidName);
							compid = comp.getId();
							UserDetails user = userDetailsRepo.findAllByEmail(getLoginUserName());
							Company company = new Company(compid, caseDetails, compidName, false,new Date(),user,1L,2L);
							// company.setIsActive(false);
							companyList.add(company);

						}

					}
				}

			}
			if (!companyList.isEmpty())
				addCompanyRepo.saveAll(companyList);

			boolean flag = false;

			caseDetails.setEditLagacy(true);
			caseDetails.setMcaSubmissionDate(new SimpleDateFormat("dd/MM/yyyy").parse(caseDetails.getMcaSubDate()));
			caseDetails.setUpdateByID(getUserDetails().getUserId());
			caseDetails.setUpdatedDate(new Date());
			caseDetails = caseDetailsRepository.save(caseDetails);
			String loginUName = appUserDAO.findUserDetails(getUserDetails()).getFirstName() != null
					? appUserDAO.findUserDetails(getUserDetails()).getFirstName()
					: "" + appUserDAO.findUserDetails(getUserDetails()).getMiddleName() != null
							? appUserDAO.findUserDetails(getUserDetails()).getMiddleName()
							: "" + appUserDAO.findUserDetails(getUserDetails()).getLastName() != null
									? appUserDAO.findUserDetails(getUserDetails()).getLastName()
									: "";
			auditBeanBo.setAuditBean(Integer.parseInt(userDetailsService.getUserDetails().getUserId().toString()),
					loginUName, "User", Integer.parseInt(userDetailsService.getUserDetails().getUserId().toString()),
					utils.getMessage("log.Legacycase.update"), utils.getMessage("log.Legacycase.updated"), loginUName,
					"true");
			auditBeanBo.save();
		} else {
			/*
			 * String unit = ""; UnitDetails unitDtls =
			 * unitDetailsRepo.findById(caseDetails.getUnitId()).get(); unit =
			 * unitDtls.getUnitName();
			 */

			/*
			 * String caseId = ""; if
			 * (caseDetails.getCaseId().charAt(caseDetails.getCaseId().length() - 1) == '/')
			 * caseId = caseDetails.getCaseId() + unit + "/" + id; else caseId =
			 * caseDetails.getCaseId() + "/" + unit + "/" + id;
			 */
			caseDetails.setCaseId(caseDetails.getCaseId());

			if (null != caseDetails.getMcaDate() && !"".equals(caseDetails.getMcaDate())) {
				caseDetails.setMcaOrderDate(new SimpleDateFormat("dd/MM/yyyy").parse(caseDetails.getMcaDate()));
				String fileExt = caseDetails.getMcaFile().getOriginalFilename();
				fileExt = fileExt.substring(fileExt.lastIndexOf("."));
				caseDetails.setMcaOrderFile("MCA_" + id + fileExt);
				caseFileUpload(caseDetails.getMcaFile(), caseDetails.getMcaOrderFile());
			}

			String fileExt = caseDetails.getOfficeOrderFile().getOriginalFilename();
			fileExt = fileExt.substring(fileExt.lastIndexOf("."));
			caseDetails.setLegacyOrderFile("Office_Order_" + id + fileExt);
			caseFileUpload(caseDetails.getOfficeOrderFile(), "Office_Order_" + id + fileExt);
			caseDetails.setUnit(userDetailsService.getUserDetailssss().getUnit());
			caseDetails.setCaseStage(2);
			caseDetails.setEditLagacy(false);
			if (null != caseDetails.getMcaSubmissionDate() && !"".equals(caseDetails.getMcaSubmissionDate())) {
			caseDetails.setMcaSubmissionDate(new SimpleDateFormat("dd/MM/yyyy").parse(caseDetails.getMcaSubDate()));
			}
			caseDetails = caseDetailsRepository.save(caseDetails);
			List<Company> companyList = new ArrayList<Company>();

			for (int i = 0; i < caseDetails.getCompanyName().length; i++) {
				UserDetails user = userDetailsRepo.findAllByEmail(getLoginUserName());
				Company company = new Company(caseDetails, caseDetails.getCompanyName()[i], true, new Date(),user,1L,2L);
				companyList.add(company);
			}

			if (!companyList.isEmpty())
				addCompanyRepo.saveAll(companyList);
			List<Inspector> inspectorList = new ArrayList<Inspector>();

			String loginUName = appUserDAO.findUserDetails(getUserDetails()).getFirstName() != null
					? appUserDAO.findUserDetails(getUserDetails()).getFirstName()
					: "" + appUserDAO.findUserDetails(getUserDetails()).getMiddleName() != null
							? appUserDAO.findUserDetails(getUserDetails()).getMiddleName()
							: "" + appUserDAO.findUserDetails(getUserDetails()).getLastName() != null
									? appUserDAO.findUserDetails(getUserDetails()).getLastName()
									: "";
			auditBeanBo.setAuditBean(Integer.parseInt(userDetailsService.getUserDetails().getUserId().toString()),
					loginUName, "User", Integer.parseInt(userDetailsService.getUserDetails().getUserId().toString()),
					utils.getMessage("log.Legacycase.create"), utils.getMessage("log.Legacycase.created"), loginUName,
					"true");
			auditBeanBo.save();

		}
		CaseDetails result = new CaseDetails();
		result.setCaseTitle(caseDetails.getCaseTitle());
		result.setCaseId(caseDetails.getCaseId());
		result.setId(caseDetails.getId());

//			return ResponseEntity.ok(result);
		caseDetails.setMcaFile(null);
		caseDetails.setCourtFile(null);
		caseDetails.setOfficeOrderFile(null);
		caseDetails.setCompanyOrder(null);
		caseJsonResponse.setCaseDetails(caseDetails);
		caseJsonResponse.setStatus("SUCCESS");
		return caseJsonResponse;

	}

	@RequestMapping(value = "/legacyCasedSuccessd")
	public String legacyCaseSuccess(@ModelAttribute("caseId") String caseId, @ModelAttribute("id") Long id,
			@ModelAttribute("mcaOrderNo") String mcaOrderNo, @ModelAttribute("mcaOrderDate") String orderDate,
			Model model) {
		model.addAttribute("message", "Prosecution Case Details Added/Updated Successfully for case id : " + caseId);
		CaseDetails caseDetails = new CaseDetails();
		caseDetails.setId(id);
		caseDetails.setCaseId(caseId);
		caseDetails.setMcaOrderNo(mcaOrderNo);
		caseDetails.setMcaDate(orderDate);
		model.addAttribute("caseDetails", caseDetails);
		return "caseDetails/legacyCaseSuccess";
	}

	@RequestMapping(value = "/caseFileUpload1", method = RequestMethod.POST)
	public void caseFileUpload(@RequestParam("file") MultipartFile file, String name) {
		BufferedOutputStream stream = null;

		try {

			// String directory = filePath;

			File parent = new File(filePath).getParentFile().getCanonicalFile();
			String directory = ESAPI.validator().getValidDirectoryPath("DirectoryName", filePath, parent, false);

			Boolean validFileName = ESAPI.validator().isValidFileName("FileName", name.trim(), false);
			String filepath = null;
			if (validFileName == true) {
				filepath = Paths.get(directory + File.separator + name.trim()).toString();
			}

			// Save the file locally
			stream = new BufferedOutputStream(new FileOutputStream(new File(filepath)));
			stream.write(file.getBytes());
			stream.close();
		}

		catch (Exception e) {
			logger.info(e.getMessage());
		}

		finally {
			if (stream != null) {
				safeClose(stream);
			}
		}
//		  return new ResponseEntity<>(HttpStatus.OK);
	} // method upload

	private void safeClose(BufferedOutputStream stream) {
		if (stream != null) {
			try {
				stream.close();
			} catch (IOException e) {
				logger.info(e.getMessage());
			}
		}
	}

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
	
	
	
	@RequestMapping(value = "/caseAndByProsecutorOrdDetail", params = "editOrders")
	public String caseAndOffOrdDetailedit(@RequestParam(value = "editOrders", required = true) Long caseId,
			Model model) {
		SNMSValidator snmsValid = new SNMSValidator();

		if (!snmsValid.getValidInteger(caseId)) {
			model.addAttribute("message", "Invalid caseId ");
			return "caseDetails/ErrorPage";
		}
		Optional<CaseDetails> caseDe = caseDetailsRepository.findById(caseId);
		CaseDetails caseDetails = caseDe.get();

		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		if (caseDetails.getMcaOrderDate() != null) {
			String ss = dateFormat.format(caseDetails.getMcaOrderDate());
			caseDetails.setMcaDate(dateFormat.format(caseDetails.getMcaOrderDate()));
		}

		if (caseDetails.getCourtOrderDate() != null) {
			caseDetails.setCourtDate(dateFormat.format(caseDetails.getCourtOrderDate()));
		}

		caseDetails.setMcaOrderFile(caseDetails.getMcaOrderFile());
		caseDetails.setCourtOrderFile(caseDetails.getCourtOrderFile());
		caseDetails.setLegacyOrderFile(caseDetails.getLegacyOrderFile());
		// model.addAttribute("caseDetails", caseDetails);
		// model.addAttribute("file", caseDetails);

		List<Inspector> inspList = new ArrayList<Inspector>();
//		List<UserDetails> unituserList = userMangCustom.findByUnit(new UnitDetails(1L));
		List<UserDetails> unituserList = userMangCustom.findByRole(SnmsConstant.Role_USER, new UnitDetails(1L));
		int count = 1;
		for (UserDetails user : unituserList) {
			inspList.add(new Inspector(count,
					user.getSalutation() + " " + userDetailsService.getFullName(user) + " ("
							+ user.getUnit().getUnitName() + ")",
					user.getDesignation().getDesignation(), 2, user.getUserId().getUserId(), false,
					userMangCustom.getCasesAssigned(user.getUserId().getUserId())));
			count++;
		}
		// model.addAttribute("inspList", inspList);

		InspectorListDTO inspcompList = getInspectorListAndCompany(caseDetails.getId());

		model.addAttribute("fyList", financialYearRepo.findActiveFY());

		List<UnitDetails> unitList = unitDetailsRepo.findAll(Sort.by(Sort.Direction.ASC, "unitId"));
		unitList.removeIf(x -> x.getUnitId() == 3);
		model.addAttribute("unitList", unitList);

		// model.addAttribute("copyToList", inspList1.getCopyToList());
		model.addAttribute("comList", inspcompList.getCompanyList());

		if (caseDetails.getRadioValue().equalsIgnoreCase("Prosecution")) {
			caseDetails.setEditLagacy(true);
		}
		model.addAttribute("caseDetails", caseDetails);

		List<OfficeOrder> order = officeOrderDao.findOrderByAdoApprove(caseDetails.getId());
		
			return "prosecutor/editCaseDetails";
		
		
		// return "caseDetails/editCaseDetails";

	}
	
	@RequestMapping(value = "/caseAndByProsecutorOrdDetail", params = "caseDetails")
	public String caseAndOffOrdDetail(@RequestParam(value = "caseDetails", required = true) long caseId, Model model) {
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
		if(officeorder!=null) {
			model.addAttribute("officeorder", officeorder);
		}
		/*
		 * if(inspList.getIoName()!=null) model.addAttribute("ioName",
		 * inspList.getIoName());
		 */
//		inspList.getInspctorList().removeIf( insp -> insp.isAdo()==true );
//		inspList.getCopyToList().removeIf( insp -> insp.isAdo()==true );
		model.addAttribute("inspList", inspList.getInspctorList());
		model.addAttribute("copyToList", inspList.getCopyToList());
		model.addAttribute("comList", inspList.getCompanyList());
		return "prosecutor/caseDetailView";
	}

	@RequestMapping(value = "/caseAndByProsecutorOrdDetail", params = "officeOrders")
	public String caseAndOfficeOrdDetail(@RequestParam(value = "officeOrders", required = true) Long caseId,
			Model model) {

		if (caseId == null || caseId == 0) {
			model.addAttribute("message", "Invalid Input");
			return "caseDetails/ErrorPage";
		}
		List<OfficeOrder> officeList = (officeOrderRepo.findByCaseDetails(new CaseDetails(caseId),
				Sort.by(Sort.Direction.DESC, "id"))) != null
						? officeOrderRepo.findByCaseDetails(new CaseDetails(caseId), Sort.by(Sort.Direction.DESC, "id"))
						: new ArrayList<OfficeOrder>();

		Optional<CaseDetails> caseDtls = caseDetailsRepository.findById(caseId);

		if (caseDtls.get().getRadioValue().equals("Prosecution")) {
			System.out.println("downloadFiles?fileName=" + caseDtls.get().getLegacyOrderFile());
//			model.addAttribute("message", "Legacy office order Draft found for this case ");
			model.addAttribute("ooFile", "downloadFiles?fileName=" + caseDtls.get().getLegacyOrderFile());
			model.addAttribute("caseDetails", new CaseDetails(caseId));
			if (officeList.size() > 0) {
				model.addAttribute("forwarded", true);
				if (officeList.get(0).getAprrovalStage1() == true && officeList.get(0).getAprrovalStage2() == true)
					model.addAttribute("approved", true);
				else
					model.addAttribute("approved", false);
			} else if (officeList.size() == 0) {
				 // model.addAttribute("approved", false);
				  model.addAttribute("forwarded", false);
			}
			return "caseDetails/legacyOfficeOrderFound";
			
		}

		if (caseDtls.get().getRadioValue().equals("Legacy")) {
			System.out.println("downloadFiles?fileName=" + caseDtls.get().getLegacyOrderFile());
//			model.addAttribute("message", "Legacy office order Draft found for this case ");
			model.addAttribute("ooFile", "downloadFiles?fileName=" + caseDtls.get().getLegacyOrderFile());
			model.addAttribute("caseDetails", new CaseDetails(caseId));
			if (officeList.size() > 0) {
				model.addAttribute("forwarded", true);
				if (officeList.get(0).getAprrovalStage1() == true && officeList.get(0).getAprrovalStage2() == true)
					model.addAttribute("approved", true);
				else
					model.addAttribute("approved", false);
			} else if (officeList.size() == 0)
				model.addAttribute("forwarded", false);
				
			return "caseDetails/legacyOfficeOrderFound";
		}
		if (officeList.size() == 0) {
			InitiatorOfficeOrderDraft office = (officeOrderDao.findDraftByCaseDetails(new CaseDetails(caseId)) == null)
					? new InitiatorOfficeOrderDraft()
					: officeOrderDao.findDraftByCaseDetails(new CaseDetails(caseId));

			if (office.getId() != null) {
				model.addAttribute("message", "An office order Draft found for this case ");
				model.addAttribute("caseDetails", new CaseDetails(caseId));
				return "caseDetails/officeOrderDraftFound";
			} else {
				model.addAttribute("message", "No office order exists for this case ");
				model.addAttribute("caseDetails", new CaseDetails(caseId));
				return "caseDetails/officeOrderNotFound";
			}
		}
		model.addAttribute("officeList", officeList);
		return "caseDetails/viewOfficeOrders";
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
			c.setCompId(com.getId());
			listCom.add(c);
			k++;
		}

		return new InspectorListDTO(inspList, copyToList, ioName, listCom);
	}

	@RequestMapping(value = "listAddCase")
	public String listAddCase(ModelMap model) {

		List<CaseDetails> caseList = caseDetailsRepository.findAllByCaseStage(2 ,Sort.by(Sort.Direction.DESC, "id"));
		model.addAttribute("caseList", caseList);
		return "prosecutor/listAddCase";
	}
	
	
	
	@RequestMapping(value = "/pendingForApprBypro")
	public String pendingForApprBypro(ModelMap modelMap) throws Exception {
		List<personcompanyApproval> gamspersonlist  =userMangCustom.findpersonPendingListForProApproval(1, userDetailsService.getUserDetails().getUserId(),2);
		List<Object[]> penOrder = userMangCustom.findOfficeOrderPendingForApproval(3);
		modelMap.addAttribute("totlaPendOrder", gamspersonlist.size());
		modelMap.addAttribute("totlaPenOrder", penOrder.size());
		
		return "prosecutorAdo/pendingForApproval";
	}
	
	
	
	
	@RequestMapping(value = "/showPendProOOStage1") // pending office orders stage one for proado
	public String showPendingOfficeOrderStage1(Model model) {

		List<Object[]> list = userMangCustom.findOfficeOrderPendingForApproval(3);
		List<PendingForApprovalDTO> pending = new ArrayList<PendingForApprovalDTO>();
		for (Object[] dto : list) {
			pending.add(new PendingForApprovalDTO(Long.parseLong(dto[0].toString()), dto[1].toString(),
					dto[2].toString(), Long.parseLong(dto[3].toString()),dto[4].toString()));
		}
		
		model.addAttribute("pending", pending);
		CaseDetails caseDetails = new CaseDetails();
		model.addAttribute("caseDetails", caseDetails);
		return "prosecutorAdo/officeOrderPending";
	}
	
	@RequestMapping(value = "/showCaseDetailsByProsecutor", params = "approve")
	public String showCaseDetailsApprove(@RequestParam(value = "approve", required = true) Long officeOrderId,
			Model model) {

		SNMSValidator snmsValid = new SNMSValidator();
		if (!snmsValid.getValidInteger(officeOrderId)) {
			model.addAttribute("message", "Invalid Id ");
			return "caseDetails/ErrorPage";
		}

		Optional<OfficeOrder> officeOrder = officeOrderRepo.findById(officeOrderId);
		OfficeOrder office = officeOrder.get();
		CaseDetails caseDetails = office.getCaseDetails();
		
			model.addAttribute("ooFile", "downloadFiles?fileName=" + caseDetails.getLegacyOrderFile());
			model.addAttribute("caseDetails", new CaseDetails(caseDetails.getId()));
			model.addAttribute("forwarded", true);
			if (office.getAprrovalStage1() == true && office.getAprrovalStage2() == true)
				model.addAttribute("approved", true);
			else
				model.addAttribute("approved", false);

			return "caseDetails/legacyOfficeOrderFound";
		

	}

	@RequestMapping(value = "/generateLegacyOfficeOrder", params = "proapproved")
	public String approveLegacyOfficeOrder(@ModelAttribute CaseDetails caseDetailsDto, Model model) throws Exception {

		Optional<CaseDetails> caseDtls = caseDetailsRepository.findById(caseDetailsDto.getId());

		String ooDIN = "";
		ooDIN = caseDtls.get().getFinanceYear().substring(caseDtls.get().getFinanceYear().lastIndexOf('-') + 1)
				+ caseDtls.get().getFinanceYear().substring(0, caseDtls.get().getFinanceYear().lastIndexOf('-'))
						.concat(SnmsConstant.OO_DIN_ID);
		String sequence = officeOrderRepo.getOfficeOrderDinSequence().toString();
		ooDIN = ooDIN.concat("00000".substring(sequence.length()).concat(sequence))
				.concat(utils.getRandomAlphaNum(SnmsConstant.DIN_RANDOM_DIGIT));

		officeOrderRepo.approveLegacyOrderById(new CaseDetails(caseDetailsDto.getId()), ooDIN);
		OfficeOrder order = new OfficeOrder(caseDetailsDto.getId());
		model.addAttribute("message", "Legacy Office Order approved successfully for caseId : SFIO/INV/2020/"
				+ order.getId() + " and DIN " + ooDIN);
		return "caseDetails/officeOrderSuccess";
	}
	
	@RequestMapping(value = "/showCaseDetailsByProsecutor", params = "caseDetails")
	public String showCaseDetails(@RequestParam(value = "caseDetails", required = true) Long caseId, Model model) {

		SNMSValidator snmsValid = new SNMSValidator();

		if (!snmsValid.getValidInteger(caseId)) {
			model.addAttribute("message", "Invalid Id ");
			return "caseDetails/ErrorPage";
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
//		inspList.removeIf( insp -> insp.isAdo()==true ); // Check not to add Additional director
		model.addAttribute("inspList", inspList);
		List<Company> comList = userMangCustom.findByCaseId(new CaseDetails(caseDetails.getId()));
		model.addAttribute("comList", comList);

		return "prosecutor/caseDetailView";
	}
	@RequestMapping(value = "/companyListAddedSuccess")
	public String inspListSuccess(@ModelAttribute("caseId") String caseId, @ModelAttribute("id") Long id,
			@ModelAttribute("mcaOrderNo") String mcaOrderNo, @ModelAttribute("mcaOrderDate") String orderDate,
			Model model) throws Exception {
		
	     AppUser user  =   getUserDetails();
	     CaseDetails caseDetails1 = caseDetailsRepository.findById(id).get();
	    // UserRole  role = userRoleRepo.findAllByAppUser(user);
	    
			 if(appRoleDAO.getRoleName(getUserDetails().getUserId()).equals("ROLE_USER")) {
				 Inspector inspector = inspectorRepo.findByCaseDetailsAndIsActiveAndIsAdo(caseDetails1,true,true);
			     String fullName =  appUserDAO.findUserDetails(inspector.getAppUser()).getFirstName() != null
							? appUserDAO.findUserDetails(getUserDetails()).getFirstName()
							: "" + appUserDAO.findUserDetails(getUserDetails()).getMiddleName() != null
									? appUserDAO.findUserDetails(getUserDetails()).getMiddleName()
									: "" + appUserDAO.findUserDetails(getUserDetails()).getLastName() != null
											? appUserDAO.findUserDetails(getUserDetails()).getLastName()
											: "";
	    	model.addAttribute("message", "Company has been added and  forwarded to  "+fullName+" for Approval");
	    }else {
		model.addAttribute("message", "Company List is Updated  Successfully ");
	    }
		CaseDetails caseDetails = new CaseDetails();
		caseDetails.setId(id);
		caseDetails.setCaseId(caseId);
		caseDetails.setMcaOrderNo(mcaOrderNo);
		caseDetails.setMcaDate(orderDate);
		model.addAttribute("caseDetails", caseDetails);
		return "caseDetails/InspLstUpd";
	}
	@RequestMapping(value = "/UpdateCaseDetailsbyPro", method = RequestMethod.POST)
	public @ResponseBody CaseJsonResponse UpdateCaseDetails(HttpServletRequest request,
			@Valid @ModelAttribute("caseDetails") CaseDetails caseDetails, BindingResult bindResult, Model model)
			throws Exception {
		boolean flag = false;
		// Long id = (caseDetailsRepository.findMaxid() != null) ?
		// (caseDetailsRepository.findMaxid() + 1) : 1;

		long inspectorid = 0;
		CaseJsonResponse caseJsonResponse = new CaseJsonResponse();
		UserValidation validation = new UserValidation();
		//validation.validateIODetails(caseDetails, bindResult);
		// String deleteFile1 = caseDetails.getDeleteIoOrder().getOriginalFilename();
		if (caseDetails.getId() == null) {
			if (officeOrderDao.uniqueCaseCheck(caseDetails.getCaseTitle(), "", "", 1) != null)
				bindResult.rejectValue("caseTitle", "errmsg.uniqueTitle");
			if (!"".equalsIgnoreCase(caseDetails.getMcaOrderNo().trim())) {
				if (officeOrderDao.uniqueCaseCheck("", caseDetails.getMcaOrderNo(), "", 2) != null)
					bindResult.rejectValue("mcaOrderNo", "errmsg.uniqueMcaOrder");
			}
			if (!"".equalsIgnoreCase(caseDetails.getCourtOrderNo().trim())) {
				if (officeOrderDao.uniqueCaseCheck("", "", caseDetails.getCourtOrderNo(), 3) != null)
					bindResult.rejectValue("courtOrderNo", "errmsg.uniqueCourtOrder");
			}

			if (caseDetails.getUnitId() != null) {
				UnitDetails unitbyId = unitDetailsRepo.findById(caseDetails.getUnitId()).get();
				if (unitbyId == null || caseDetails.getUnitId() == 3L)
					bindResult.rejectValue("unitId", "msg.wrongId");
			}
		}

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

		List<Company> companyList = new ArrayList<Company>();
		for (int i = 0; i < caseDetails.getCompanyName().length; i++) {

			for (int j = 0; j < inspcompList.getCompanyList().size(); j++) {

				String compname = inspcompList.getCompanyList().get(j).getName().trim();
				if (compname.equals(caseDetails.getCompanyName()[i].trim())) {

					String compidName = inspcompList.getCompanyList().get(j).getName();
					Company comp = inspectorDao.findComapnyNameByCaseIdAndUserId(caseDetails.getId().intValue(),
							compidName);
					compid = comp.getId();
					comflag = true;
					break;
				}

			}

			if (comflag == true) {
				//Company company = new Company(compid, caseDetails, caseDetails.getCompanyName()[i], true);
				// company.setIsActive(true);
				Company company = addCompanyRepo.findById(compid).get();
				companyList.add(company);
				comflag = false;
			} else {
				UserDetails user = userDetailsRepo.findAllByEmail(getLoginUserName());
				Company company = new Company(caseDetails, caseDetails.getCompanyName()[i], true, new Date(),user,1L,2L);
				// company.setIsActive(true);
				companyList.add(company);
			}
		}
		// caseDetails = caseDetailsRepository.save(caseDetails);
		if (!companyList.isEmpty())
			addCompanyRepo.saveAll(companyList);


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


	@RequestMapping(value = "/showAllCaseAddByProsecutor")
	public String showAllCase(Model model) 
	{	
		
		List<Object[]> list = proDao.findAllCases();
		List<PendingForApprovalDTO> caselist = new ArrayList<PendingForApprovalDTO>();

		for (Object[] dto : list) {

			caselist.add(new PendingForApprovalDTO(dto[0].toString(), dto[1].toString(),
					Long.parseLong(dto[2].toString()), dto[3], dto[4], dto[5].toString()));
		}

		model.addAttribute("caselist", caselist);
		/* Commented by Anuj on 26 Nov2021
		List<CaseDetails> caseList = caseDetailsRepository.findAllByCaseStage(2 ,Sort.by(Sort.Direction.DESC, "id"));
		modelMap.addAttribute("caseDetails", new CaseDetails());
		
		model.addAttribute("caseList", caseList);
		*/
		return "prosecutor/viewAllCase";
	}
	
	@RequestMapping(value = "/kmpsPersonList")
	public String kmpsPersonList(Model model) {
		List<PersonDetails> personList = addPersonRepo.findAll();
		model.addAttribute("personList", personList);
		return "prosecutor/PersonList";
	}
	
	// view cases transfer from investigation team 
	@RequestMapping(value = "/personStatusUpdate",params = "updateStatus")
	public String personStatusUpdate(Model model,@RequestParam("personId") int personId) {
		PersonDetails personList = addPersonRepo.findById(personId).get();
		List<RelationpersonCompany> personRelationList = rpcRepo.findAllByPersonDetails(personList);
		model.addAttribute("personRelationList", personRelationList);
		return "prosecutor/PersonStatusupdate";
	}
	
	
	@GetMapping("getpersonStatus")
	public String getCaseList(ModelMap modelMap) throws Exception {
		
		
		List<Object[]> list = proDao.findAllCases();
		List<PendingForApprovalDTO> caselist = new ArrayList<PendingForApprovalDTO>();

		for (Object[] dto : list) {

			caselist.add(new PendingForApprovalDTO(dto[0].toString(), dto[1].toString(),
					Long.parseLong(dto[2].toString()), dto[3], dto[4], dto[5].toString()));
		}

		modelMap.addAttribute("caselist", caselist);
		modelMap.addAttribute("caseDetails", new CaseDetails());
		return "prosecutor/assignedCase";
	}
	@RequestMapping(value = "/Personsearch", params = "searchperson")
public String Applicantsearch(@ModelAttribute @Valid PersonDetails applicantData, BindingResult bindResult,
		Model modelMap, RedirectAttributes redirect,HttpServletRequest request) {
	String personName = null;
	

  
    List<RelationpersonCompany> companyList = proDao.findByPersonDetails(applicantData.getPanNumber(), applicantData.getPassportNumber());
   // System.out.println("data "+companyList.get(0).getIsApprovedStage2());

    modelMap.addAttribute("PersonDetails",new PersonDetails());
    
	modelMap.addAttribute("companyList",companyList);
    return "person/PersonStatus";
}
	
	public String getLoginUserName() {
		return SecurityContextHolder.getContext().getAuthentication().getName();
	}

	public AppUser getUserDetails() throws Exception {
		AppUser appUser = this.appUserDAO.findUserAccount(getLoginUserName());
		return appUser;
	}

	public com.snms.entity.UserDetails getUserDetailssss() throws Exception {
		com.snms.entity.UserDetails usrDetails = this.appUserDAO.findUserDetails(getUserDetails());
		return usrDetails;
	}
	
	public String getDirectorName() {
		UserDetails director = userDetailsRepo.findByDesignation(new AddDesignation(1L));
		return userDetailsService.getFullName(director);
	}
}
