package com.snms.controllers;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.errors.IntrusionException;
import org.owasp.esapi.errors.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.snms.dao.AppUserDAO;
import com.snms.dao.InspectorDao;
import com.snms.dao.OfficeOrderDao;
import com.snms.dao.SummonDao;
import com.snms.dto.InspectorDTO;
import com.snms.dto.InspectorDetailDTO;
import com.snms.dto.InspectorEditDTO;
import com.snms.dto.InspectorHisEditDTO;
import com.snms.dto.InspectorListDTO;
import com.snms.dto.OfficeOrderDto;
import com.snms.dto.OfficeOrderTempDto;
import com.snms.dto.PendingForApprovalDTO;
import com.snms.entity.AddDesignation;
import com.snms.entity.AppRole;
import com.snms.entity.AppUser;
import com.snms.entity.CaseDetails;
import com.snms.entity.Company;
import com.snms.entity.CompanySummon;
import com.snms.entity.InitiateSummonDraft;
import com.snms.entity.InitiatorOfficeOrderDraft;
import com.snms.entity.Inspector;
import com.snms.entity.NoticeStatus;
import com.snms.entity.OfficeOrder;
import com.snms.entity.OfficeOrderTemplate;
import com.snms.entity.RelationpersonCompany;
import com.snms.entity.SummonDetails;
import com.snms.entity.SummonStatus;
import com.snms.entity.SummonType;

import com.snms.entity.UnitDetails;
import com.snms.entity.UserDetails;
import com.snms.entity.UserRole;
import com.snms.entity.inspectorHistory;
import com.snms.entity.personcompanyApproval;
import com.snms.service.AddCompanyRepository;
import com.snms.service.AuditBeanBo;
import com.snms.service.CaseDetailsRepository;
import com.snms.service.CompanyRepository;
import com.snms.service.FinancialYearRepository;
import com.snms.service.InitiatorOfficeOrderDraftRepository;
import com.snms.service.InspectorHistoryRepository;
import com.snms.service.InspectorRepository;
import com.snms.service.OfficeOrderRepository;
import com.snms.service.OfficeOrderTemplateRepository;
import com.snms.service.UnitDetailsRepository;
import com.snms.service.UserDetailsRepository;
import com.snms.service.UserDetailsServiceImpl;
import com.snms.service.UserManagementCustom;
import com.snms.utils.OfficeOrderConstant;
import com.snms.utils.OfficeOrerPDF;
import com.snms.utils.SnmsConstant;
import com.snms.utils.Utils;
import com.snms.validation.CaseJsonResponse;
import com.snms.validation.SNMSValidator;
import com.snms.validation.UserValidation;

import net.bytebuddy.asm.Advice.Exit;

@Controller
public class CaseController {
	private static final Logger logger = Logger.getLogger(CaseController.class);

	@Autowired
	private CaseDetailsRepository caseDetailsRepository;

	@Autowired
	private UnitDetailsRepository unitDetailsRepo;

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
	private OfficeOrderTemplateRepository officeOrderTempRepo;

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
	private CompanyRepository  companyRepo;
	
	@Autowired
	private Utils utils;
	@Value("${file.upload}")
	public String filePath;

	@RequestMapping(value = "/caseDetails")
	public String caseDetails(Model model) {

		CaseDetails caseDetails = new CaseDetails();
		caseDetails.setCaseId("SFIO/INV/2022/");
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

		model.addAttribute("fyList", financialYearRepo.findActiveFY());

		List<UnitDetails> unitList = unitDetailsRepo.findAll(Sort.by(Sort.Direction.ASC, "unitId"));
		unitList.removeIf(x -> x.getUnitId() == 3);
		model.addAttribute("unitList", unitList);
		return "caseDetails/createCase";
	}
	/*
	 * @RequestMapping(value = "/caseDetails") public String caseDetails(Model
	 * model) {
	 * 
	 * CaseDetails caseDetails = new CaseDetails();
	 * caseDetails.setCaseId("SFIO/INV/2020/"); model.addAttribute("caseDetails",
	 * caseDetails);
	 * 
	 * List<Inspector> inspList = new ArrayList<Inspector>(); // List<UserDetails>
	 * unituserList = userMangCustom.findByUnit(new UnitDetails(1L));
	 * List<UserDetails> unituserList =
	 * userMangCustom.findByRole(SnmsConstant.Role_USER ); int count = 1;
	 * 
	 * for (UserDetails user : unituserList) { inspList.add(new Inspector(count,
	 * user.getSalutation() + " " +
	 * user.getFullName()+" ("+user.getUnit().getUnitName()+")",
	 * user.getDesignation().getDesignation(), 2, user.getUserId().getUserId(),
	 * false,userMangCustom.getCasesAssigned(user.getUserId().getUserId())));
	 * count++; } model.addAttribute("inspList", inspList); List<UnitDetails>
	 * unitList = unitDetailsRepo.findAll(Sort.by(Sort.Direction.ASC, "unitId"));
	 * unitList.removeIf (x -> x.getUnitId() == 3); model.addAttribute("unitList",
	 * unitList); return "caseDetails/createCase"; }
	 */

	/*
	 * private final Properties configProp = new Properties();
	 * 
	 * private CaseController () { //Private constructor to restrict new instances
	 * InputStream in =
	 * this.getClass().getClassLoader().getResourceAsStream("messages.properties");
	 * configProp.load(in); } catch (IOException e) { logger.info(e.getMessage()); }
	 * }
	 */

	/*
	 * @RequestMapping(value = "/addCaseDetails", method = RequestMethod.POST,
	 * produces = "application/json") public @ResponseBody CaseJsonResponse
	 * addCaseDetails(HttpServletRequest request, @RequestBody @Valid CaseDetails
	 * caseDetails, BindingResult bindResult,Model model) throws Exception {
	 * CaseJsonResponse caseJsonResponse = new CaseJsonResponse(); UserValidation
	 * validation = new UserValidation();
	 * validation.validateCaseDetails(caseDetails,bindResult);
	 * 
	 * if (bindResult.hasErrors()) { Map<String ,String> errors=new HashMap<String,
	 * String>(); List<FieldError> fieldErrors = bindResult.getFieldErrors(); for
	 * (FieldError fieldError : fieldErrors) { String[] resolveMessageCodes =
	 * bindResult.resolveMessageCodes(fieldError.getCode()); String string =
	 * resolveMessageCodes[0];
	 * 
	 * errors.put(fieldError.getField(),
	 * ""+configProp.getProperty(string.substring(0,string.lastIndexOf(".")) )) ;
	 * caseJsonResponse.setErrorsMap(errors);
	 * caseJsonResponse.setCaseDetails(caseDetails);
	 * caseJsonResponse.setStatus("ERROR"); } return caseJsonResponse; }
	 * 
	 * // AppUser appUser = userDetailsService.getUserDetails(); Long id =
	 * (caseDetailsRepository.findMaxid() != null) ?
	 * (caseDetailsRepository.findMaxid()+1):1;
	 * 
	 * String unit = ""; if (caseDetails.getUnitId() == 1) unit = "UNIT-1"; else if
	 * (caseDetails.getUnitId() == 2) unit = "UNIT-2";
	 * 
	 * caseDetails.setCaseId(caseDetails.getCaseId() + unit + "/" + id);
	 * 
	 * try { if (caseDetails.getRadioValue().equals("Both")) {
	 * caseDetails.setMcaOrderDate(new
	 * SimpleDateFormat("dd/MM/yyyy").parse(caseDetails.getMcaDate()));
	 * caseDetails.setCourtOrderDate(new
	 * SimpleDateFormat("dd/MM/yyyy").parse(caseDetails.getCourtDate())); } else if
	 * (caseDetails.getRadioValue().equals("MCA")) { caseDetails.setMcaOrderDate(new
	 * SimpleDateFormat("dd/MM/yyyy").parse(caseDetails.getMcaDate())); } else if
	 * (caseDetails.getRadioValue().equals("Court")) {
	 * caseDetails.setCourtOrderDate(new
	 * SimpleDateFormat("dd/MM/yyyy").parse(caseDetails.getCourtDate())); } } catch
	 * (ParseException e) { logger.info(e.getMessage()); }
	 * 
	 * // caseDetails.setCourtOrderFile("lic.pdf");
	 * caseDetails.setMcaOrderFile("lic.pdf");
	 * 
	 * caseDetails.setUnit(new UnitDetails(caseDetails.getUnitId()));
	 * 
	 * caseDetails = caseDetailsRepository.save(caseDetails);
	 * 
	 * JSONArray companyArray = new JSONArray(caseDetails.getCompanies());
	 * 
	 * List<Company> companyList = new ArrayList<Company>();
	 * 
	 * for (int i = 0; i < companyArray.length(); i++) { JSONObject object =
	 * companyArray.getJSONObject(i); Company company = new Company(caseDetails,
	 * object.getString("Company Name")); companyList.add(company); }
	 * 
	 * if (!companyList.isEmpty()) addCompanyRepo.saveAll(companyList);
	 * 
	 * JSONArray inspectorArray = new JSONArray(caseDetails.getInspectorList());
	 * 
	 * List<Inspector> inspectorList = new ArrayList<Inspector>();
	 * 
	 * for (int i = 0; i < inspectorArray.length(); i++) { JSONObject object =
	 * inspectorArray.getJSONObject(i); if (i == 0) inspectorList .add(new
	 * Inspector(caseDetails, new
	 * AppUser(Long.parseLong(object.getString("userId"))), true)); else
	 * inspectorList.add( new Inspector(caseDetails, new
	 * AppUser(Long.parseLong(object.getString("userId"))), false)); }
	 * 
	 * inspectorRepo.saveAll(inspectorList);
	 * 
	 * CaseDetails result = new CaseDetails();
	 * result.setCaseTitle(caseDetails.getCaseTitle());
	 * result.setCaseId(caseDetails.getCaseId()); result.setId(caseDetails.getId());
	 * 
	 * // return ResponseEntity.ok(result);
	 * caseJsonResponse.setCaseDetails(caseDetails);
	 * caseJsonResponse.setStatus("SUCCESS"); return caseJsonResponse;
	 * 
	 * }
	 */
	@RequestMapping(value = "/addCaseDetails", method = RequestMethod.POST)
	public @ResponseBody CaseJsonResponse addCaseDetails(HttpServletRequest request,
			@Valid @ModelAttribute("caseDetails") CaseDetails caseDetails, BindingResult bindResult, Model model)
			throws Exception {
		CaseJsonResponse caseJsonResponse = new CaseJsonResponse();
		UserValidation validation = new UserValidation();
		validation.validateCaseDetails(caseDetails, bindResult);
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
			caseJsonResponse.setErrorsMap(errors);
			caseJsonResponse.setCaseDetails(caseDetails);
			caseJsonResponse.setStatus("ERROR");
			return caseJsonResponse;
		}

		if (caseDetails.getId() != null) {

			caseDetails.setId(caseDetails.getId());
			// AppUser appUser = userDetailsService.getUserDetails();
			Long id = (caseDetailsRepository.findMaxid() != null) ? (caseDetailsRepository.findMaxid() + 1) : 1;

			String unit = "";
			/*
			 * if (caseDetails.getUnitId() == 1) unit = "UNIT-1"; else if
			 * (caseDetails.getUnitId() == 2) unit = "UNIT-2";
			 */

			UnitDetails unitDtls = unitDetailsRepo.findById(caseDetails.getUnitId()).get();
			unit = unitDtls.getUnitName();

			String caseId = "";
			if (caseDetails.getCaseId().charAt(caseDetails.getCaseId().length() - 1) == '/')
				caseId = caseDetails.getCaseId() + unit + "/" + id;
			else
				caseId = caseDetails.getCaseId() + "/" + unit + "/" + id;
			caseDetails.setCaseId(caseDetails.getCaseId());

			String radioVal = caseDetails.getRadioValue().replace(",", "");
			try {
				if (radioVal.equals("MCA") || radioVal.equals("Both")) {
					caseDetails.setMcaOrderDate(new SimpleDateFormat("dd/MM/yyyy").parse(caseDetails.getMcaDate()));
					if (!caseDetails.getMcaFile().isEmpty()) {
						String fileExt = caseDetails.getMcaFile().getOriginalFilename();
						fileExt = fileExt.substring(fileExt.lastIndexOf("."));
						caseDetails.setMcaOrderFile("MCA_" + id + fileExt);
						caseFileUpload(caseDetails.getMcaFile(), caseDetails.getMcaOrderFile());
					}
				}
				if (radioVal.equals("Court") || radioVal.equals("Both")) {
					caseDetails.setCourtOrderDate(new SimpleDateFormat("dd/MM/yyyy").parse(caseDetails.getCourtDate()));

					if (!caseDetails.getCourtFile().isEmpty()) {
						String fileExt = caseDetails.getCourtFile().getOriginalFilename();
						fileExt = fileExt.substring(fileExt.lastIndexOf("."));
						caseDetails.setCourtOrderFile("COURT_" + id + fileExt);
						caseFileUpload(caseDetails.getCourtFile(), caseDetails.getCourtOrderFile());
					}
				}
			} catch (ParseException e) {
				logger.info(e.getMessage());
			}
			caseDetails.setRadioValue(radioVal);

			// caseDetails.setCourtOrderFile("lic.pdf");

			//Gouthami 27092021 
			//   setUnitId corresponding to Adouser
			long userAdoId=Long.parseLong(caseDetails.getChooseAdo());  
			 UserDetails ud = userDetailsRepo.findById(userAdoId).get();
			caseDetails.setUnit(ud.getUnit());

			// caseDetails = caseDetailsRepository.save(caseDetails);

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
			if (caseDetails.getDeleteCompId() != null) {
				for (int k = 0; k < caseDetails.getDeleteCompId().length; k++) {
					String delteCompName = caseDetails.getDeleteCompId()[k];
					for (int j = 0; j < inspcompList.getCompanyList().size(); j++) {
						if (inspcompList.getCompanyList().get(j).getCompId() == Long.parseLong(delteCompName.trim())) {
							UserDetails user = userDetailsRepo.findAllByEmail(getLoginUserName());
							String compidName = inspcompList.getCompanyList().get(j).getName();
							Company comp = inspectorDao.findComapnyNameByCaseIdAndUserId(caseDetails.getId().intValue(),
									compidName);
							compid = comp.getId();
							Company company = new Company(compid, caseDetails, compidName, false, new Date(),user,1L,2L);
							// company.setIsActive(false);
							companyList.add(company);
						}

					}
				}
			}
			// caseDetails = caseDetailsRepository.save(caseDetails);
			if (!companyList.isEmpty())
				addCompanyRepo.saveAll(companyList);
			InspectorEditDTO inspList1 = getAllInspectorList(caseDetails.getId());
			List<Inspector> inspectorList = new ArrayList<Inspector>();
			boolean flag = false;
			long inspectorid = 0;
			for (int i = 0; i < caseDetails.getInspectors().length; i++) {
//				JSONObject object = inspectorArray.getJSONObject(i);

				int userid = Integer.parseInt(caseDetails.getInspectors()[i]);
				for (int j = 0; j < inspList1.getInspctorList().size(); j++) {
					int inspectoeId = inspList1.getInspctorList().get(j).getUserID();

					if (inspList1.getInspctorList().get(j).getUserID() == userid) {
						flag = true;

						Inspector ispect = inspectorDao.findAllByCaseIdAndUserId(caseDetails.getId().intValue(),
								userid);
						inspectorid = ispect.getId();

						break;

					}

				}
				if (caseDetails.getInspectors()[i].equals(caseDetails.getChooseIo().replace(",", ""))) {
					if (flag == true) {
						inspectorList.add(new Inspector(inspectorid, caseDetails,
								new AppUser(Long.parseLong(caseDetails.getInspectors()[i])), true));

						flag = false;
					} else {
						inspectorList.add(new Inspector(caseDetails,
								new AppUser(Long.parseLong(caseDetails.getInspectors()[i])), true));
					}
				} else

				if (flag == true) {
					inspectorList.add(new Inspector(inspectorid, caseDetails,
							new AppUser(Long.parseLong(caseDetails.getInspectors()[i])), false));
					flag = false;
				} else {
					inspectorList.add(

							new Inspector(caseDetails, new AppUser(Long.parseLong(caseDetails.getInspectors()[i])),
									false));
				}
			}
			if (caseDetails.getDeleteIO() != null) {
				for (int k = 0; k < caseDetails.getDeleteIO().length; k++) {
					String delteuserid = caseDetails.getDeleteIO()[k];
					for (int j = 0; j < inspList1.getInspctorList().size(); j++) {
						if (inspList1.getInspctorList().get(j).getUserID() == Integer.valueOf(delteuserid)) {
							Inspector ispect = inspectorDao.findAllByCaseIdAndUserId(caseDetails.getId().intValue(),
									Integer.parseInt(delteuserid));
							long delid = ispect.getId();
							if (delteuserid.equals(caseDetails.getChooseIo().replace(",", ""))) {

								inspectorList.add(new Inspector(delid, caseDetails,
										new AppUser(Long.parseLong(delteuserid)), true));
							} else {
								inspectorList.add(new Inspector(delid, caseDetails,
										new AppUser(Long.parseLong(delteuserid)), false));

							}
						}

					}
				}

			}

			String deleteIO[] = caseDetails.getDeleteIO();
			String choosenAdo = caseDetails.getChooseAdo();
			inspectorList.forEach(inspector -> {
				if (inspector.getAppUser().getUserId() == Long.parseLong(choosenAdo)) {
					inspector.setIsAdo(true);
					inspector.setIsActive(true);

				} else {
					inspector.setIsAdo(false);
					inspector.setIsActive(true);

					if (deleteIO != null) {

						for (int k = 0; k < deleteIO.length; k++) {
							String delteuserid = deleteIO[k];
							if (inspector.getAppUser().getUserId() == Long.parseLong(delteuserid)) {

								inspector.setIsActive(false);

								inspector.setIsAdo(false);
								break;
							}

						}
					}
				}
			});
			inspectorRepo.saveAll(inspectorList);
			caseDetails = caseDetailsRepository.save(caseDetails);

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
					utils.getMessage("log.case.update"), utils.getMessage("log.case.updated"), loginUName, "true");
			auditBeanBo.save();

		}

		else {
			// AppUser appUser = userDetailsService.getUserDetails();
			Long id = (caseDetailsRepository.findMaxid() != null) ? (caseDetailsRepository.findMaxid() + 1) : 1;
			String unit = "";
			/*
			 * if (caseDetails.getUnitId() == 1) unit = "UNIT-1"; else if
			 * (caseDetails.getUnitId() == 2) unit = "UNIT-2";
			 */
			UnitDetails unitDtls = unitDetailsRepo.findById(caseDetails.getUnitId()).get();
			unit = unitDtls.getUnitName();

			String caseId = "";
			if (caseDetails.getCaseId().charAt(caseDetails.getCaseId().length() - 1) == '/')
				caseId = caseDetails.getCaseId() + unit + "/" + id;
			else
				caseId = caseDetails.getCaseId() + "/" + unit + "/" + id;
			caseDetails.setCaseId(caseId);

			String radioVal = caseDetails.getRadioValue().replace(",", "");
			try {
				if (radioVal.equals("MCA") || radioVal.equals("Both")) {
					caseDetails.setMcaOrderDate(new SimpleDateFormat("dd/MM/yyyy").parse(caseDetails.getMcaDate()));
					String fileExt = caseDetails.getMcaFile().getOriginalFilename();
					fileExt = fileExt.substring(fileExt.lastIndexOf("."));
					caseDetails.setMcaOrderFile("MCA_" + id + fileExt);
					caseFileUpload(caseDetails.getMcaFile(), caseDetails.getMcaOrderFile());
				}
				if (radioVal.equals("Court") || radioVal.equals("Both")) {
					caseDetails.setCourtOrderDate(new SimpleDateFormat("dd/MM/yyyy").parse(caseDetails.getCourtDate()));
					String fileExt = caseDetails.getCourtFile().getOriginalFilename();
					fileExt = fileExt.substring(fileExt.lastIndexOf("."));
					caseDetails.setCourtOrderFile("COURT_" + id + fileExt);
					caseFileUpload(caseDetails.getCourtFile(), caseDetails.getCourtOrderFile());
				}
			} catch (ParseException e) {
				logger.info(e.getMessage());
			}
			caseDetails.setRadioValue(radioVal);
			;
			// caseDetails.setCourtOrderFile("lic.pdf");

			
			//Gouthami 27092021 
			//   setUnitId corresponding to Adouser
			long userAdoId=Long.parseLong(caseDetails.getChooseAdo());  
			 UserDetails ud = userDetailsRepo.findById(userAdoId).get();
			 UnitDetails unitdtl = unitDetailsRepo.findById(ud.getUnit().getUnitId()).get();
			caseDetails.setUnit(unitdtl);
			
			//caseDetails.setUnit(new UnitDetails(caseDetails.getUnitId()));
			//caseDetails.setUnit();  
			caseDetails = caseDetailsRepository.save(caseDetails);

//			JSONArray companyArray = new JSONArray(caseDetails.getCompanies());

			List<Company> companyList = new ArrayList<Company>();
			/*
			 * for (int i = 0; i < companyArray.length(); i++) { JSONObject object =
			 * companyArray.getJSONObject(i); Company company = new Company(caseDetails,
			 * object.getString("Company Name")); companyList.add(company); }
			 */ for (int i = 0; i < caseDetails.getCompanyName().length; i++) {
				UserDetails user = userDetailsRepo.findAllByEmail(getLoginUserName());
				Company company = new Company(caseDetails, caseDetails.getCompanyName()[i], true, new Date(),user,1L,2L);
				companyList.add(company);
			}

			if (!companyList.isEmpty())
				addCompanyRepo.saveAll(companyList);

			// JSONArray inspectorArray = new JSONArray(caseDetails.getInspectorList());

			List<Inspector> inspectorList = new ArrayList<Inspector>();

			for (int i = 0; i < caseDetails.getInspectors().length; i++) {
				// JSONObject object = inspectorArray.getJSONObject(i);
				if (caseDetails.getInspectors()[i].equals(caseDetails.getChooseIo().replace(",", "")))
					inspectorList.add(new Inspector(caseDetails,
							new AppUser(Long.parseLong(caseDetails.getInspectors()[i])), true));
				else
					inspectorList.add(new Inspector(caseDetails,
							new AppUser(Long.parseLong(caseDetails.getInspectors()[i])), false));
			}
			String choosenAdo = caseDetails.getChooseAdo();
			inspectorList.forEach(inspector -> {
				if (inspector.getAppUser().getUserId() == Long.parseLong(choosenAdo)) {
					inspector.setIsAdo(true);
					inspector.setIsActive(true);

				} else {
					inspector.setIsAdo(false);
					inspector.setIsActive(true);

				}
			});
			inspectorRepo.saveAll(inspectorList);
			auditBeanBo.save();

			String loginUName = appUserDAO.findUserDetails(getUserDetails()).getFirstName() != null
					? appUserDAO.findUserDetails(getUserDetails()).getFirstName()
					: "" + appUserDAO.findUserDetails(getUserDetails()).getMiddleName() != null
							? appUserDAO.findUserDetails(getUserDetails()).getMiddleName()
							: "" + appUserDAO.findUserDetails(getUserDetails()).getLastName() != null
									? appUserDAO.findUserDetails(getUserDetails()).getLastName()
									: "";
			auditBeanBo.setAuditBean(Integer.parseInt(userDetailsService.getUserDetails().getUserId().toString()),
					loginUName, "User", Integer.parseInt(userDetailsService.getUserDetails().getUserId().toString()),
					utils.getMessage("log.case.create"), utils.getMessage("log.case.created"), loginUName, "true");

		}
		CaseDetails result = new CaseDetails();
		result.setCaseTitle(caseDetails.getCaseTitle());
		result.setCaseId(caseDetails.getCaseId());
		result.setId(caseDetails.getId());

//			return ResponseEntity.ok(result);
		caseDetails.setMcaFile(null);
		caseDetails.setCourtFile(null);
		caseDetails.setCompanyOrder(null);
		caseJsonResponse.setCaseDetails(caseDetails);
		caseJsonResponse.setStatus("SUCCESS");
		return caseJsonResponse;

	}

	public String getLoginUserName() {
		return SecurityContextHolder.getContext().getAuthentication().getName();
	}

	public AppUser getUserDetails() throws Exception {
		AppUser appUser = this.appUserDAO.findUserAccount(getLoginUserName());
		return appUser;
	}

	
	@RequestMapping(value="updateCaseId")
	public String updateCaseId(@Valid @ModelAttribute("caseDetails") CaseDetails caseDetails, BindingResult bindResult, Model model ) throws Exception {
		
		
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

		CaseDetails casedata = caseDetailsRepository.findById(caseDetails.getId()).get();
		UserValidation validation = new UserValidation();
		validation.validatecaseDetails(caseDetails, bindResult);
		if (bindResult.hasErrors()) {
			DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
			if (casedata.getMcaOrderDate() != null) {
				String ss = dateFormat.format(casedata.getMcaOrderDate());
				caseDetails.setMcaDate(dateFormat.format(casedata.getMcaOrderDate()));
				caseDetails.setMcaOrderDate(casedata.getMcaOrderDate());
				caseDetails.setMcaOrderNo(casedata.getMcaOrderNo());
			}

			if (casedata.getCourtOrderDate() != null) {
				caseDetails.setCourtDate(dateFormat.format(casedata.getCourtOrderDate()));
			}
			caseDetails.setRadioValue(casedata.getRadioValue());
			caseDetails.setMcaOrderFile(casedata.getMcaOrderFile());
			caseDetails.setCourtOrderFile(casedata.getCourtOrderFile());
			caseDetails.setLegacyOrderFile(casedata.getLegacyOrderFile());
			// model.addAttribute("caseDetails", caseDetails);
			// model.addAttribute("file", caseDetails);

			List<Inspector> inspList = new ArrayList<Inspector>();
//			List<UserDetails> unituserList = userMangCustom.findByUnit(new UnitDetails(1L));
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
			InspectorEditDTO inspList1 = getInspectorList(caseDetails.getId());

			model.addAttribute("inspList", inspList1.getInspctorList());

			// model.addAttribute("copyToList", inspList1.getCopyToList());
			model.addAttribute("comList", inspcompList.getCompanyList());

			model.addAttribute("caseDetails", caseDetails);
			return "caseDetails/editCaseID";
		}
		
		
		casedata.setCaseId(caseDetails.getCaseId());
		casedata.setCaseTitle(caseDetails.getCaseTitle());
		casedata.setRemark(caseDetails.getCaseRemark());
		casedata.setUpdateByID(getUserDetails().getUserId());
		casedata.setUpdatedDate(new Date());
		caseDetailsRepository.save(casedata);
		model.addAttribute("message", " Case details has been  updated  successfully");
		
		model.addAttribute("caseDetails", casedata);
		return "caseDetails/InspLstUpd";
	}
 	
	
	@RequestMapping(value = "/validateDeleteIo", method = RequestMethod.POST)
	public @ResponseBody CaseJsonResponse validateDeleteIo(HttpServletRequest request,
			@Valid @ModelAttribute("caseDetails") CaseDetails caseDetails, BindingResult bindResult, Model model)
			throws Exception {
		boolean flag = false;
		// Long id = (caseDetailsRepository.findMaxid() != null) ?
		// (caseDetailsRepository.findMaxid() + 1) : 1;

		long inspectorid = 0;
		CaseJsonResponse caseJsonResponse = new CaseJsonResponse();
		UserValidation validation = new UserValidation();
		validation.validateIOfile(caseDetails, bindResult);
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
		
		caseJsonResponse.setCaseDetails(caseDetails);
		caseDetails.setAddIoOrder(null);
		caseDetails.setDeleteIoOrder(null);
		caseDetails.setCompanyOrder(null);
		caseJsonResponse.setStatus("SUCCESS");
		return caseJsonResponse;
	}
	
	@RequestMapping(value="Upcompamy")
   public String Upcompamy(@Valid @ModelAttribute("caseDetails") CaseDetails caseDetails1, BindingResult bindResult, Model model) throws IOException	{
		
		SNMSValidator snmsValid = new SNMSValidator();
		CaseDetails   caseDetails = caseDetailsRepository.findById(caseDetails1.getId()).get();
	    
		if (!snmsValid.getValidInteger(caseDetails1.getId())) {
			model.addAttribute("message", "Invalid caseId ");
			return "caseDetails/ErrorPage";
		}
		
	snmsValid.validateCompNameOrder(caseDetails1,bindResult);
	 if(bindResult.hasErrors()) {
		 DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
			if (caseDetails.getMcaOrderDate() != null) {
				String ss = dateFormat.format(caseDetails.getMcaOrderDate());
				caseDetails.setMcaDate(dateFormat.format(caseDetails.getMcaOrderDate()));
			}
	if(caseDetails.getMcaSubmissionDate()!=null) {
		String ss = dateFormat.format(caseDetails.getMcaSubmissionDate());
		caseDetails.setMcaSubDate(dateFormat.format(caseDetails.getMcaSubmissionDate()));
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
//			List<UserDetails> unituserList = userMangCustom.findByUnit(new UnitDetails(1L));
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
			InspectorEditDTO inspList1 = getInspectorList(caseDetails.getId());

			model.addAttribute("inspList", inspList1.getInspctorList());

			// model.addAttribute("copyToList", inspList1.getCopyToList());
			model.addAttribute("comList", inspcompList.getCompanyList());
	       
			if (caseDetails.getRadioValue().equalsIgnoreCase("Legacy")) {
				caseDetails.setEditLagacy(true);
			}
			model.addAttribute("caseDetails", caseDetails);

			List<OfficeOrder> order = officeOrderDao.findOrderByAdoApprove(caseDetails.getId());
  			if (order == null) {

				if (caseDetails.getRadioValue().equalsIgnoreCase("Legacy")) {
					return "caseDetails/legacyOrder";
				} 
				else if(caseDetails.getRadioValue().equalsIgnoreCase("Prosecution")) {
					
						caseDetails.setEditLagacy(true);
					
					return "prosecutor/addLegacyCase";
				}
				
				else {
					return "caseDetails/UpdateCaseDetails";
				}
			}
			 if(caseDetails.getRadioValue().equalsIgnoreCase("Prosecution") &&  order!=null) {
					return "prosecutor/editCaseDetails";
				}

			 
			else {

	       return "caseDetails/editCaseDetails";
			}
		 
	 }
		
		
		
	                 
                       Company  comp = new Company();
                       
                       comp  = companyRepo.findById(caseDetails1.getUpCompId()).get();
                       comp.setName(caseDetails1.getUpCompName());
                       if (!caseDetails1.getCompanyOrder().isEmpty()) {
   						String fileExt = caseDetails1.getCompanyOrder().getOriginalFilename();
   						fileExt = fileExt.substring(fileExt.lastIndexOf("."));
   						comp.setCompanyOrderFile("COMPANY_" + comp.getId() + fileExt);
   						caseFileUpload(caseDetails1.getCompanyOrder(), comp.getCompanyOrderFile());
   						comp.setApproved_status(4L);
   						comp.setIsActive(false);
   					}
                    		   companyRepo.save(comp);
                       
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		if (caseDetails.getMcaOrderDate() != null) {
			String ss = dateFormat.format(caseDetails.getMcaOrderDate());
			caseDetails.setMcaDate(dateFormat.format(caseDetails.getMcaOrderDate()));
		}
if(caseDetails.getMcaSubmissionDate()!=null) {
	String ss = dateFormat.format(caseDetails.getMcaSubmissionDate());
	caseDetails.setMcaSubDate(dateFormat.format(caseDetails.getMcaSubmissionDate()));
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
		InspectorEditDTO inspList1 = getInspectorList(caseDetails.getId());

		model.addAttribute("inspList", inspList1.getInspctorList());

		// model.addAttribute("copyToList", inspList1.getCopyToList());
		model.addAttribute("comList", inspcompList.getCompanyList());

		if (caseDetails.getRadioValue().equalsIgnoreCase("Legacy")) {
			caseDetails.setEditLagacy(true);
		}
		model.addAttribute("caseDetails", caseDetails);
	    model.addAttribute("message", "Company has been updated  and farward to ADO for approval ");

		List<OfficeOrder> order = officeOrderDao.findOrderByAdoApprove(caseDetails.getId());

		caseDetails.setId(caseDetails.getId());
		caseDetails.setCaseId(caseDetails.getCaseId());
		caseDetails.setMcaOrderNo(caseDetails.getMcaOrderNo());
		caseDetails.setMcaDate(caseDetails.getMcaDate());
		model.addAttribute("caseDetails", caseDetails);
		return "caseDetails/CaseUpdateSuccess";
	}
	
	@RequestMapping(value = "/UpdateCaseDetails", method = RequestMethod.POST)
	public @ResponseBody CaseJsonResponse UpdateCaseDetails(HttpServletRequest request,
			@Valid @ModelAttribute("caseDetails") CaseDetails caseDetails, BindingResult bindResult, Model model)
			throws Exception {
		boolean flag = false;
		// Long id = (caseDetailsRepository.findMaxid() != null) ?
		// (caseDetailsRepository.findMaxid() + 1) : 1;

		
		//Gouthami 27092021 
		//   setUnitId corresponding to Adouser
		long userAdoId=Long.parseLong(caseDetails.getChooseAdo());  
		 UserDetails ud = userDetailsRepo.findById(userAdoId).get();
		 CaseDetails cd1 = caseDetailsRepository.findById(caseDetails.getId()).get();
		 cd1.setUnit(ud.getUnit());
		caseDetailsRepository.save(cd1);
		
		
		long inspectorid = 0;
		CaseJsonResponse caseJsonResponse = new CaseJsonResponse();
		UserValidation validation = new UserValidation();
		validation.validateIODetails(caseDetails, bindResult);
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
			caseDetails.setCourtOrderFile(null);
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
		inspectorHistory inh = new inspectorHistory();
		// UserValidation validation = new UserValidation();
		// validation.validateCaseDetails(caseDetails,bindResult);
		InspectorEditDTO inspList1 = getAllInspectorList(caseDetails.getId());
		List<Inspector> inspectorList = new ArrayList<Inspector>();
		List<Inspector> DeleteIo = new ArrayList<Inspector>();
		List<inspectorHistory> ispHist = new ArrayList<inspectorHistory>();
		for (int i = 0; i < caseDetails.getInspectors().length; i++) {
//				JSONObject object = inspectorArray.getJSONObject(i);

			int userid = Integer.parseInt(caseDetails.getInspectors()[i]);
			for (int j = 0; j < inspList1.getInspctorList().size(); j++) {
				int inspectoeId = inspList1.getInspctorList().get(j).getUserID();

				if (inspList1.getInspctorList().get(j).getUserID() == userid) {
					flag = true;

					Inspector ispect = inspectorDao.findAllByCaseIdAndUserId(caseDetails.getId().intValue(), userid);
					inspectorid = ispect.getId();

					break;

				} // else {
					// flag = false;
					// ispHist.add(new inspectorHistory(caseDetails , new
					// AppUser(Long.parseLong(caseDetails.getInspectors()[i]))));

				// }

			}
			// inspList1.getInspctorList(). forEach( inspector -> {
			// if(inspector.getUserID()== userid) flag = true;});
			if (caseDetails.getInspectors()[i].equals(caseDetails.getChooseIo().replace(",", ""))) {

				if (flag == true) {
					inspectorList.add(new Inspector(inspectorid, caseDetails,
							new AppUser(Long.parseLong(caseDetails.getInspectors()[i])), true));

					flag = false;

				} else {
					inspectorList.add(new Inspector(caseDetails,
							new AppUser(Long.parseLong(caseDetails.getInspectors()[i])), true));

					for (int b = 0; b < caseDetails.getNewIOorder().length; b++) {

						String NewIOdetail = caseDetails.getNewIOorder()[b];

						int index = NewIOdetail.indexOf(" ");
						int ln = NewIOdetail.length();
						String Newuserid = NewIOdetail.substring(0, index);

						String fileName = NewIOdetail.substring(index, ln);

						if (Long.parseLong(Newuserid) == Long.parseLong(caseDetails.getInspectors()[i])) {
							String fileExt = fileName;
							fileExt = "MCAIO_" + fileExt;
							ispHist.add(new inspectorHistory(caseDetails,
									new AppUser(Long.parseLong(caseDetails.getInspectors()[i])), null, fileExt,
									new Date(), null));

							for (int l = 0; l < caseDetails.getAddIoOrder().length; l++) {

								String fileExt1 = caseDetails.getAddIoOrder()[l].getOriginalFilename();
								fileExt1 = fileExt1.substring(fileExt1.lastIndexOf("."));

								caseFileUpload(caseDetails.getAddIoOrder()[l], inh.getDirectorOrderFrom());
							}

						}
					}
				}
			} else

			if (flag == true) {
				inspectorList.add(new Inspector(inspectorid, caseDetails,
						new AppUser(Long.parseLong(caseDetails.getInspectors()[i])), false));
				flag = false;
			} else {
				inspectorList.add(

						new Inspector(caseDetails, new AppUser(Long.parseLong(caseDetails.getInspectors()[i])), false));

				for (int k = 0; k < caseDetails.getNewIOorder().length; k++) {

					String NewIOdetail = caseDetails.getNewIOorder()[k];
					int index = NewIOdetail.indexOf(" ");
					int ln = NewIOdetail.length();
					String Newuserid = NewIOdetail.substring(0, index);

					String fileName = NewIOdetail.substring(index, ln);
					if (Long.parseLong(Newuserid) == userid) {
						String fileExt = fileName.trim();
						// fileExt = fileExt;
						ispHist.add(new inspectorHistory(caseDetails,
								new AppUser(Long.parseLong(caseDetails.getInspectors()[i])), null, fileExt, new Date(),
								null));
						for (int l = 0; l < caseDetails.getAddIoOrder().length; l++) {
							String fileExt1 = caseDetails.getAddIoOrder()[l].getOriginalFilename();
							// fileExt1 ="+fileExt1;
							if (fileExt.equalsIgnoreCase(fileExt1)) {
								caseFileUpload(caseDetails.getAddIoOrder()[l], fileExt);
								break;
							}
						}
					}
				}
			}
		}

		String choosenAdo = caseDetails.getChooseAdo();
		String deleteid = caseDetails.getDeletid();
		inspectorHistory insh = new inspectorHistory();

		/*
		 * insh.setIoActiveDateFrom(new Date()); insh.setCaseDetails(caseDetails);
		 * insh.setAppUser(inspector.getAppUser());
		 * inspectorHistoryRepository.save(insh);
		 */
		if (caseDetails.getDeleteIO() != null) {
			for (int k = 0; k < caseDetails.getDeleteIO().length; k++) {
				String IOdetail = caseDetails.getDeleteIO()[k];
	
				int index = IOdetail.indexOf(" ");
				int ln = IOdetail.length();
				String delteuserid = IOdetail.substring(0, index);
				String fileName = IOdetail.substring(index, ln);
				
				// MultipartFile DeletIofile	= (MultipartFile) fileName.getByte;
			
				
				OfficeOrder officeorder = officeOrderRepo.findAllByCaseDetails(caseDetails);
				for (int j = 0; j < inspList1.getInspctorList().size(); j++) {
					if (inspList1.getInspctorList().get(j).getUserID() == Integer.valueOf(delteuserid)) {

						Inspector ispect = inspectorDao.findAllByCaseIdAndUserId(caseDetails.getId().intValue(),
								Integer.parseInt(delteuserid));
						long delid = ispect.getId();
						if (delteuserid.equals(caseDetails.getChooseIo().replace(",", ""))) {

							inspectorList.add(
									new Inspector(delid, caseDetails, new AppUser(Long.parseLong(delteuserid)), true));
						} else {
							inspectorList.add(
									new Inspector(delid, caseDetails, new AppUser(Long.parseLong(delteuserid)), false));

						}
						Long id = (inspectorHistoryRepository.findMaxid() != null) ? (inspectorHistoryRepository.findMaxid() + 1) : 1;
						String fileExt = fileName.trim();
						String datafileExt = "MCADeleteIO_"+id +"_"+fileExt;

						// ispHist.add(new inspectorHistory(caseDetails, new
						// AppUser(Long.parseLong(delteuserid)),
						// fileExt,null,officeorder.getCreatedDate(),new Date()));
						inspectorHistory inspHisList1 = inspectorDao.findAllByInspHisCaseIdAndUserId(
								caseDetails.getId().intValue(), Integer.parseInt(delteuserid));

						if (inspHisList1 == null || inspHisList1.getId() == 0) {
							ispHist.add(new inspectorHistory(caseDetails, new AppUser(Long.parseLong(delteuserid)),
									datafileExt, null, officeorder.getCreatedDate(), new Date()));

						} else {
							ispHist.add(new inspectorHistory(inspHisList1.getId(), caseDetails,
									new AppUser(Long.parseLong(delteuserid)), datafileExt,
									inspHisList1.getDirectorOrderFrom(), inspHisList1.getIoActiveDateFrom(),
									new Date()));

						}
						if(caseDetails.getDeleteIoOrder()!=null) {
							for (int l = 0; l < caseDetails.getDeleteIoOrder().length; l++) {
							String fileExt1 =  caseDetails.getDeleteIoOrder()[l].getOriginalFilename();
								// fileExt1 ="+fileExt1;
								if (fileExt.equalsIgnoreCase(fileExt1)) {
									caseFileUpload(caseDetails.getDeleteIoOrder()[l], datafileExt);
									break;
								}
								
							}
						}
					//	casedFileUpload(fileName);
						// case
					}

				}
			}

		}

		inspectorList.forEach(inspector -> {

			if (inspector.getAppUser().getUserId() == Long.parseLong(choosenAdo)) {
				inspector.setIsAdo(true);
				inspector.setIsActive(true);

			}

			else {
				inspector.setIsAdo(false);
				inspector.setIsActive(true);

				if (caseDetails.getDeleteIO() != null) {
					for (int k = 0; k < caseDetails.getDeleteIO().length; k++) {

						String IOdetail = caseDetails.getDeleteIO()[k];

						int index = IOdetail.indexOf(" ");
						int ln = IOdetail.length();
						String userid = IOdetail.substring(0, index);

						if (inspector.getAppUser().getUserId() == Long.parseLong(userid)) {

							inspector.setIsActive(false);

							inspector.setIsAdo(false);
							break;
						}

					}
				}

				// inspector.setIsActive(true);

			}

		});
		
		//caseDetailsRepository.save(caseDetails);
		
		// inspectorRepo.findAllByCaseIdAndUserId(caseDetails.getId(),);
		inspectorRepo.saveAll(inspectorList);

		inspectorHistoryRepository.saveAll(ispHist);

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
		caseDetails.setCourtOrderFile(null);
		caseDetails.setDeleteIoOrder(null);
		caseDetails.setCompanyOrder(null);
		caseJsonResponse.setStatus("SUCCESS");
		return caseJsonResponse;
		// return caseJsonResponse;
	}

	@RequestMapping(value = "/caseFileUpload", method = RequestMethod.POST)
	public void caseFileUpload(@RequestParam("file") MultipartFile file, String name) {
		BufferedOutputStream stream = null;
             
		try {

			// String directory = filePath;

			File parent = new File(filePath).getParentFile().getCanonicalFile();
			String directory = ESAPI.validator().getValidDirectoryPath("DirectoryName", filePath, parent, false);
		
			
			 Boolean validFileName = ESAPI.validator().isValidFileName("FileName", name.trim(), false);
			 String filepath = null;
		      if(validFileName == true) {
			 filepath = Paths.get(directory+ File.separator +name.trim()).toString();
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

	
	
	@RequestMapping(value = "/casedFileUpload", method = RequestMethod.POST)
	public void casedFileUpload(String name) {
		BufferedOutputStream stream = null;
             
		try {

			// String directory = filePath;

			File parent = new File(filePath).getParentFile().getCanonicalFile();
			String directory = ESAPI.validator().getValidDirectoryPath("DirectoryName", filePath, parent, false);
		
			
			 //Boolean validFileName = ESAPI.validator().isValidFileName("FileName", name, false);
			 String filepath = null;
		     // if(validFileName == true) {
			 filepath = Paths.get(directory+ File.separator +name.trim()).toString().trim();
		      //}
			

			 MultipartFile file=(MultipartFile) new File(name);
			// Save the file locally
			stream = new BufferedOutputStream(new FileOutputStream(new File(filepath)));
			//stream.write(name.getBytes());
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

	@RequestMapping(value = "/newCaseSuccess")
	public String newCaseSuccess(@ModelAttribute("caseId") String caseId, @ModelAttribute("id") Long id,
			@ModelAttribute("mcaOrderNo") String mcaOrderNo, @ModelAttribute("mcaOrderDate") String orderDate,
			Model model, BindingResult bindResult) {

		SNMSValidator snmsValid = new SNMSValidator();

		if (!snmsValid.getValidInteger(id)) {
			model.addAttribute("message", "Invalid Id ");
			return "caseDetails/ErrorPage";
		}
		if (!snmsValid.validateDateFormat(orderDate)) {
			model.addAttribute("message", "Invalid Date");
			return "caseDetails/ErrorPage";
		}
		if (!snmsValid.getvalidorder(mcaOrderNo)) {
			model.addAttribute("message", "Invalid mca order no ");
			return "caseDetails/ErrorPage";
		}

		if (caseId == null || "".equals(caseId.trim())) {
			model.addAttribute("message", "Invalid CaseID");
			return "caseDetails/ErrorPage";
		}

		if (bindResult.hasErrors()) {
			return "caseDetails/ErrorPage";
		}
		model.addAttribute("message", "Case Details Added Successfully for Order No. : " + mcaOrderNo);
		CaseDetails caseDetails = new CaseDetails();
		caseDetails.setId(id);
		caseDetails.setCaseId(caseId);
		caseDetails.setMcaOrderNo(mcaOrderNo);
		caseDetails.setMcaDate(orderDate);
		model.addAttribute("caseDetails", caseDetails);
		return "caseDetails/caseSuccess";
	}

	@RequestMapping(value = "/inspListSuccess")
	public String inspListSuccess(@ModelAttribute("caseId") String caseId, @ModelAttribute("id") Long id,
			@ModelAttribute("mcaOrderNo") String mcaOrderNo, @ModelAttribute("mcaOrderDate") String orderDate,
			Model model) {
		model.addAttribute("message", "Inspector/Company List is Updated  Successfully ");
		CaseDetails caseDetails = new CaseDetails();
		caseDetails.setId(id);
		caseDetails.setCaseId(caseId);
		caseDetails.setMcaOrderNo(mcaOrderNo);
		caseDetails.setMcaDate(orderDate);
		model.addAttribute("caseDetails", caseDetails);
		return "caseDetails/InspLstUpd";
	}

	@RequestMapping(value = "/CaseUpdateSuccess")
	public String CaseUpdateSuccess(@ModelAttribute("caseId") String caseId, @ModelAttribute("id") Long id,
			@ModelAttribute("mcaOrderNo") String mcaOrderNo, @ModelAttribute("mcaOrderDate") String orderDate,
			Model model) {
		model.addAttribute("message", " Case is Updated  Successfully ");
		CaseDetails caseDetails = new CaseDetails();
		caseDetails.setId(id);
		caseDetails.setCaseId(caseId);
		caseDetails.setMcaOrderNo(mcaOrderNo);
		caseDetails.setMcaDate(orderDate);
		model.addAttribute("caseDetails", caseDetails);
		return "caseDetails/CaseUpdateSuccess";
	}

	@RequestMapping(value = "/showEditAllCase")
	public String showEditAllCase(Model model) {
		//List<CaseDetails> caseList = caseDetailsRepository.findAllByCaseStage(1 ,Sort.by(Sort.Direction.DESC, "id"));
		List<CaseDetails> caseList = caseDetailsRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
		
			model.addAttribute("caseList", caseList);
		return "caseDetails/viewAllCaseNew";
	}

	@RequestMapping(value = "/showAllCase")
	public String showAllCase(Model model) {
	List<CaseDetails> caseList = caseDetailsRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
		
		model.addAttribute("caseList", caseList);
		return "caseDetails/viewAllCase";
	}

	@RequestMapping(value = "/showAllCaseDir")
	public String showAllCaseDir(Model model) {
		int CaseStage = 1;
	//	List<CaseDetails> caseList = caseDetailsRepository.findAllByCaseStage(1 ,Sort.by(Sort.Direction.DESC, "id"));
	//	List<OfficeOrder>caseList =  officeOrderRepo.findAllByAprrovalStage1(true);
		//List<OfficeOrder>caseList =  userMangCustom.findAllByDirAprrovalStage1(true,1);
		List<OfficeOrder>caseList =  userMangCustom.findAllByDirAprrovalStage1(true);
		model.addAttribute("caseList", caseList);
		return "caseDetails/viewAllDirCase";
	}
	@RequestMapping(value = "/showAllADOCase")
	public String showAllADOCase(Model model) {
	//	List<CaseDetails> caseList = caseDetailsRepository.findAllByCaseStage(1 ,Sort.by(Sort.Direction.DESC, "id"));
		//List<OfficeOrder>caseList =  officeOrderRepo.findAllByAprrovalStage1(true);
	//	List<OfficeOrder>caseList =  officeOrderRepo.findAllByAprrovalStage1(false);
		List<OfficeOrder>caseList =  officeOrderRepo.findAll(Sort.by(Sort.Direction.DESC, "id"));
		model.addAttribute("caseList", caseList);
		return "caseDetails/viewAllDirCase";
	}

	
	public String getDirectorName() {
		UserDetails director = userDetailsRepo.findByDesignation(new AddDesignation(1L));
		return userDetailsService.getFullName(director);
	}

	// generate office order Admin section at very first stage
	@RequestMapping(value = "/genOfficeOrdAS")
	public String officeOrder(@ModelAttribute CaseDetails caseDet,
			@RequestParam(name = "sectionClause", required = false, defaultValue = "0") StringBuilder sectionClause,
			Model model, RedirectAttributes redirect) {

		Optional<CaseDetails> caseD = caseDetailsRepository.findById(caseDet.getId());
		CaseDetails caseDetails = caseD.get();
		InitiatorOfficeOrderDraft office = (officeOrderDao.findDraftByCaseDetails(caseDetails) == null)
				? new InitiatorOfficeOrderDraft()
				: officeOrderDao.findDraftByCaseDetails(caseDetails);

		if (office.getId() == null) {
			if (sectionClause.toString() == null || "".equals(sectionClause.toString())
					|| sectionClause.toString().equalsIgnoreCase("0".trim())
							&& !caseDet.getSection().equals("216(1)")) {
				redirect.addFlashAttribute("choosesectionClause", "Please choose any section clause");
				return "redirect:/showAllCase";
			}
		}

		/*
		 * String ooDIN=""; ooDIN =
		 * caseDetails.getFinanceYear().substring(caseDetails.getFinanceYear().
		 * lastIndexOf('-')+1)
		 * +caseDetails.getFinanceYear().substring(0,caseDetails.getFinanceYear().
		 * lastIndexOf('-')).concat("01"); String sequence =
		 * officeOrderRepo.findMaxid().toString(); if(sequence.length()<6){ ooDIN =
		 * ooDIN.concat("00000".substring(sequence.length()).concat(sequence)).concat(
		 * utils.getRandom3D()); }else{ ooDIN =
		 * ooDIN.concat(sequence).concat(utils.getRandom3D()); } String caseNo =ooDIN;
		 */
//		String caseNo = "No : " + caseDetails.getCaseId();
		String caseNo = "";
		String date = " Date :" + Utils.getCurrentDateWithMonth();

		InspectorListDTO inspList = getInspectorListAndCompany(caseDetails.getId());
//		inspList.getInspctorList().removeIf( insp -> insp.isAdo()==true );
		model.addAttribute("inspList", inspList.getInspctorList());
		model.addAttribute("listCom", inspList.getCompanyList());
//		inspList.getCopyToList().removeIf( insp -> insp.isAdo()==true );
		model.addAttribute("copyToList", inspList.getCopyToList());
		String IoName = inspList.getIoName();

		String para1 = "";
		if (null != caseDetails.getMcaOrderDate() && !"".equals(caseDetails.getMcaOrderDate()))
			para1 = caseDetails.getMcaOrderNo() + " dated : " + Utils.formatMcaOrderDate(caseDetails.getMcaOrderDate());

		if (officeOrderDao.findDraftByCaseDetails(caseDetails) != null) {
			OfficeOrderTempDto caseDto = new OfficeOrderTempDto(caseNo, date, office.getPara1(), office.getPara2(),
					office.getPara3(), office.getPara4(), office.getPara5(), 1L, caseDetails.getCaseId(),
					caseDetails.getId(), getDirectorName(), "");
			model.addAttribute("caseDto", caseDto);

		} else {

			if (sectionClause.toString().contains(","))
				sectionClause.setCharAt(sectionClause.lastIndexOf(","), '&');
			office.setPara1(OfficeOrderConstant.PARA_1 + caseDet.getSection() + " " + sectionClause
					+ OfficeOrderConstant.PARA_1_1 + para1);
			office.setPara2(OfficeOrderConstant.PARA_2);
			office.setPara3(OfficeOrderConstant.PARA_3);
			office.setPara4(OfficeOrderConstant.PARA_4 + IoName + OfficeOrderConstant.PARA_4_1);
			office.setPara5(OfficeOrderConstant.PARA_5);
			OfficeOrderTempDto caseDto = new OfficeOrderTempDto(caseNo, date, office.getPara1(), office.getPara2(),
					office.getPara3(), office.getPara4(), office.getPara5(), 1L, caseDetails.getCaseId(),
					caseDetails.getId(), getDirectorName(), sectionClause.toString());
			model.addAttribute("caseDto", caseDto);
		}

		model.addAttribute("sectionClause", sectionClause);

		return "caseDetails/genOfficeOrdAS";
	}

	@RequestMapping(value = "/saveOrderDraft", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<OfficeOrderTempDto> saveOrderDraft(HttpServletRequest request,
			@RequestBody OfficeOrderTempDto caseDetailsDto) throws Exception {
		initiatorOffOrdRepo.save(
				new InitiatorOfficeOrderDraft(new CaseDetails(caseDetailsDto.getCaseId()), caseDetailsDto.getPara1(),
						caseDetailsDto.getPara2(), caseDetailsDto.getPara3(), caseDetailsDto.getPara4(),
						caseDetailsDto.getPara5(), false, userDetailsService.getUserDetails(), new Date()));
		
		
		 UserRole userRole =  appUserDAO.getRoleId(getUserDetails().getUserId());
		String loginUName = appUserDAO.findUserDetails(getUserDetails()).getFirstName() != null
				? appUserDAO.findUserDetails(getUserDetails()).getFirstName()
				: "" + appUserDAO.findUserDetails(getUserDetails()).getMiddleName() != null
						? appUserDAO.findUserDetails(getUserDetails()).getMiddleName()
						: "" + appUserDAO.findUserDetails(getUserDetails()).getLastName() != null
								? appUserDAO.findUserDetails(getUserDetails()).getLastName()
								: "";
								
		if (userRole.getId() == 4) {						
		auditBeanBo.setAuditBean(Integer.parseInt(userDetailsService.getUserDetails().getUserId().toString()),
				loginUName, "User", Integer.parseInt(userDetailsService.getUserDetails().getUserId().toString()),
				utils.getMessage("log.OrderDraft.stage1"), utils.getMessage("log.OrderDrafted.stage1"), loginUName,
				"true");
		auditBeanBo.save();
		}else if(userRole.getId() == 5) {
			auditBeanBo.setAuditBean(Integer.parseInt(userDetailsService.getUserDetails().getUserId().toString()),
					loginUName, "User", Integer.parseInt(userDetailsService.getUserDetails().getUserId().toString()),
					utils.getMessage("log.OrderDraft.stage2"), utils.getMessage("log.OrderDrafted.stage2"), loginUName,
					"true");
			auditBeanBo.save();
		}
		return ResponseEntity.ok(caseDetailsDto);

	}

	@RequestMapping(value = "/caseAndOffOrdDetail", params = "caseDetails")
	public String caseAndOffOrdDetail(@RequestParam(value = "caseDetails", required = true) Long caseId, Model model) {
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
		//if(officeorder!=null) {
			model.addAttribute("officeorder", officeorder);
	//	}
		/*
		 * if(inspList.getIoName()!=null) model.addAttribute("ioName",
		 * inspList.getIoName());
		 */
//		inspList.getInspctorList().removeIf( insp -> insp.isAdo()==true );
//		inspList.getCopyToList().removeIf( insp -> insp.isAdo()==true );
		model.addAttribute("inspList", inspList.getInspctorList());
		model.addAttribute("copyToList", inspList.getCopyToList());
		model.addAttribute("comList", inspList.getCompanyList());
		
		return "caseDetails/caseDetailView";
	}

	@RequestMapping(value = "/caseAndOffOrdDetail", params = "editOrders")
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
if(caseDetails.getMcaSubmissionDate()!=null) {
	String ss = dateFormat.format(caseDetails.getMcaSubmissionDate());
	caseDetails.setMcaSubDate(dateFormat.format(caseDetails.getMcaSubmissionDate()));
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
		InspectorEditDTO inspList1 = getInspectorList(caseDetails.getId());

		model.addAttribute("inspList", inspList1.getInspctorList());

		// model.addAttribute("copyToList", inspList1.getCopyToList());
		model.addAttribute("comList", inspcompList.getCompanyList());

		if (caseDetails.getRadioValue().equalsIgnoreCase("Legacy")) {
			caseDetails.setEditLagacy(true);
		}
		model.addAttribute("caseDetails", caseDetails);

		List<OfficeOrder> order = officeOrderDao.findOrderByAdoApprove(caseDetails.getId());

		if (order == null) {

			if (caseDetails.getRadioValue().equalsIgnoreCase("Legacy")) {
				return "caseDetails/legacyOrder";
			} 
			else if(caseDetails.getRadioValue().equalsIgnoreCase("Prosecution")) {
				
					caseDetails.setEditLagacy(true);
				
				return "prosecutor/addLegacyCase";
			}
			
			else {
				return "caseDetails/UpdateCaseDetails";
			}

		}
		 if(caseDetails.getRadioValue().equalsIgnoreCase("Prosecution") &&  order!=null) {
				return "prosecutor/editCaseDetails";
			}

		 
		else {

          return "caseDetails/editCaseDetails";
		}
	}

	
	
	
	
	@RequestMapping(value = "/caseAndOffOrdDetail", params = "editCaseID")
	public String caseDetailedit(@RequestParam(value = "editCaseID", required = true) Long caseId,
			Model model) {
		SNMSValidator snmsValid = new SNMSValidator();

		if (!snmsValid.getValidInteger(caseId)) {
			model.addAttribute("message", "Invalid caseId ");
			return "caseDetails/ErrorPage";
		}
		
		
		List<OfficeOrder> officeList = (officeOrderRepo.findByCaseDetails(new CaseDetails(caseId),
				Sort.by(Sort.Direction.DESC, "id"))) != null
						? officeOrderRepo.findByCaseDetails(new CaseDetails(caseId), Sort.by(Sort.Direction.DESC, "id"))
						: new ArrayList<OfficeOrder>();

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
		InspectorEditDTO inspList1 = getInspectorList(caseDetails.getId());

		model.addAttribute("inspList", inspList1.getInspctorList());

		// model.addAttribute("copyToList", inspList1.getCopyToList());
		model.addAttribute("comList", inspcompList.getCompanyList());

		if (caseDetails.getRadioValue().equalsIgnoreCase("Legacy")) {
			caseDetails.setEditLagacy(true);
		}
		model.addAttribute("caseDetails", caseDetails);
        model.addAttribute("officeList", officeList.get(0).getIsSigned())  ;
		List<OfficeOrder> order = officeOrderDao.findOrderByAdoApprove(caseDetails.getId());

	

			return "caseDetails/editCaseID";
		
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	private InspectorEditDTO getInspectorList(Long caseId) {
		List<Object[]> inspectorList = userMangCustom.findByCaseId(caseId);
		String ioName = "";
		int i = 1;
		List<InspectorDetailDTO> inspList = new ArrayList<InspectorDetailDTO>();
		List<InspectorDetailDTO> copyToList = new ArrayList<InspectorDetailDTO>();
		for (Object[] dto : inspectorList) {
			int iduser = (int) dto[5];

			// Integer grandChildCount = ((BigInteger) dto[6]).intValue();
			InspectorDetailDTO ins = new InspectorDetailDTO(i, dto[0].toString() + " " + dto[1].toString(),
					(Boolean) dto[3], (Boolean) dto[4], dto[2].toString(),
					userMangCustom.getCasesAssigned(Long.valueOf(iduser)), iduser);
			inspList.add(ins);
			copyToList.add(ins);
			if ((Boolean) dto[3])
				ioName = dto[1].toString() + " (" + dto[2].toString() + ")";
			i++;
		}
		int length = copyToList.size();
		// copyToList.add(new InspectorDTO(length + 1, "PPS to Director, SFIO",
		// false,false));
		// copyToList.add(new InspectorDTO(length + 2, "Guard File", false,false));
		return new InspectorEditDTO(inspList);
	}

	@RequestMapping(value = "/caseAndOffOrdDetail", params = "officeOrders")
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

		if (caseDtls.get().getRadioValue().equals("Legacy")) {
			System.out.println("downloadFiles?fileName=" + caseDtls.get().getLegacyOrderFile());
//			model.addAttribute("message", "Legacy office order Draft found for this case ");
			model.addAttribute("ooFile", "downloadFiles?fileName=" + caseDtls.get().getLegacyOrderFile()+"#toolbar=0&navpanes=0&scrollbar=0");
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

	// generates and forward the office order first time

	@RequestMapping(value = "/generateOfficeOrder", params = "save")
	public String generateOfficeOrder(@ModelAttribute OfficeOrderTempDto caseDetailsDto, Model model) throws Exception {
		OfficeOrder order = new OfficeOrder(new CaseDetails(caseDetailsDto.getCaseId()), false, false,
				userDetailsService.getUserDetails(), new Date());
//		order.setOoDin(caseDetailsDto.getCaseNo());
		order = officeOrderRepo.save(order);
		Long srNo = (officeOrderTempRepo.findMaxSrNo(order) == null) ? 1 : officeOrderTempRepo.findMaxSrNo(order) + 1;

		InitiatorOfficeOrderDraft office = (officeOrderDao
				.findDraftByCaseDetails(new CaseDetails(caseDetailsDto.getCaseId())) == null)
						? initiatorOffOrdRepo.save(new InitiatorOfficeOrderDraft(
								new CaseDetails(caseDetailsDto.getCaseId()), caseDetailsDto.getPara1(),
								caseDetailsDto.getPara2(), caseDetailsDto.getPara3(), caseDetailsDto.getPara4(),
								caseDetailsDto.getPara5(), false, userDetailsService.getUserDetails(), new Date()))
						: null;

		OfficeOrderTemplate officeOrderTemplate = new OfficeOrderTemplate(order, srNo, caseDetailsDto.getPara1(),
				caseDetailsDto.getPara2(), caseDetailsDto.getPara3(), caseDetailsDto.getPara4(),
				caseDetailsDto.getPara5(), true, userDetailsService.getUserDetails(), new Date());
		officeOrderTempRepo.save(officeOrderTemplate);
		model.addAttribute("message",
				"Office Order Created and sent for approval successfully for caseId : SFIO/INV/2020/" + order.getId());
		return "caseDetails/officeOrderSuccess";
	}

	// office order generated and forwarded to additional director now additonal
	// director part

	@RequestMapping(value = "/showPendOOStage1") // pending office orders stage one for ado
	public String showPendingOfficeOrderStage1(Model model) {

		List<Object[]> list = userMangCustom.findOfficeOrderPendingForApproval();
		List<PendingForApprovalDTO> pending = new ArrayList<PendingForApprovalDTO>();
		for (Object[] dto : list) {
			pending.add(new PendingForApprovalDTO(Long.parseLong(dto[0].toString()), dto[1].toString(),
					dto[2].toString(), Long.parseLong(dto[3].toString()),dto[4].toString()));
		}

		model.addAttribute("pending", pending);
		CaseDetails caseDetails = new CaseDetails();
		model.addAttribute("caseDetails", caseDetails);
		return "ado/officeOrderPending";
	}

	@RequestMapping(value = "/showCaseDetails", params = "approve")
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
		if ("Legacy".equals(caseDetails.getRadioValue())) {
			model.addAttribute("ooFile", "downloadFiles?fileName=" + caseDetails.getLegacyOrderFile());
			model.addAttribute("caseDetails", new CaseDetails(caseDetails.getId()));
			model.addAttribute("forwarded", true);
			if (office.getAprrovalStage1() == true && office.getAprrovalStage2() == true)
				model.addAttribute("approved", true);
			else
				model.addAttribute("approved", false);

			return "caseDetails/legacyOfficeOrderFound";
		}
		
		if ("Prosecution".equals(caseDetails.getRadioValue())) {
			model.addAttribute("ooFile", "downloadFiles?fileName=" + caseDetails.getLegacyOrderFile());
			model.addAttribute("caseDetails", new CaseDetails(caseDetails.getId()));
			model.addAttribute("forwarded", true);
			model.addAttribute("stage", caseDetails.getCaseStage());
			if (office.getAprrovalStage1() == true && office.getAprrovalStage2() == true)
				model.addAttribute("approved", true);
			else
				model.addAttribute("approved", false);

			return "caseDetails/legacyOfficeOrderFound";
		}
//		OfficeOrderTemplate draft = officeOrderTempRepo.findByOfficeOrder(office);
		InitiatorOfficeOrderDraft draft = officeOrderDao.findDraftByCaseDetails(caseDetails);
		OfficeOrderTempDto caseDto = new OfficeOrderTempDto(office.getOoDin(), Utils.getCurrentDateWithMonth(),
				draft.getPara1(), draft.getPara2(), draft.getPara3(), draft.getPara4(), draft.getPara5(),
				office.getId(), caseDetails.getCaseId(), caseDetails.getId(), getDirectorName(), "");

		model.addAttribute("caseDto", caseDto);

		InspectorListDTO inspList = getInspectorListAndCompany(caseDetails.getId());
		/*
		 * inspList.getInspctorList().removeIf( insp -> insp.isAdo()==true );
		 * inspList.getCopyToList().removeIf( insp -> insp.isAdo()==true );
		 */
		model.addAttribute("inspList", inspList.getInspctorList());
		model.addAttribute("copyToList", inspList.getCopyToList());
		model.addAttribute("listCom", inspList.getCompanyList());
		model.addAttribute("stage", caseDetails.getCaseStage());

		return "ado/approveOfficeOrder";
	}

	@RequestMapping(value = "/saveOfficeOrderDraft", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<OfficeOrderTempDto> saveOfficeOrderDraft(HttpServletRequest request,
			@RequestBody OfficeOrderTempDto caseDetailsDto) throws Exception {

		OfficeOrder order = new OfficeOrder(caseDetailsDto.getOfficeOrderId());

		Long srNo = (officeOrderTempRepo.findMaxSrNo(order) == null) ? 1 : officeOrderTempRepo.findMaxSrNo(order) + 1;
		OfficeOrderTemplate officeOrderTemplate = new OfficeOrderTemplate(order, srNo, caseDetailsDto.getPara1(),
				caseDetailsDto.getPara2(), caseDetailsDto.getPara3(), caseDetailsDto.getPara4(),
				caseDetailsDto.getPara5(), false, userDetailsService.getUserDetails(), new Date());
		officeOrderTempRepo.save(officeOrderTemplate);

		return ResponseEntity.ok(caseDetailsDto);

	}

	@RequestMapping(value = "/approveOffOrdStage1")
	public String approveOfficeOrderStage1(@ModelAttribute OfficeOrderTempDto caseDetailsDto, Model model)
			throws Exception {

		officeOrderRepo.approveInfoByIdStage1(new CaseDetails(caseDetailsDto.getCaseId()));
		OfficeOrder order = new OfficeOrder(caseDetailsDto.getOfficeOrderId());
		Long srNo = (officeOrderTempRepo.findMaxSrNo(order) == null) ? 1 : officeOrderTempRepo.findMaxSrNo(order) + 1;
		OfficeOrderTemplate officeOrderTemplate = new OfficeOrderTemplate(order, srNo, caseDetailsDto.getPara1(),
				caseDetailsDto.getPara2(), caseDetailsDto.getPara3(), caseDetailsDto.getPara4(),
				caseDetailsDto.getPara5(), true, userDetailsService.getUserDetails(), new Date());
		officeOrderTempRepo.save(officeOrderTemplate);
		String loginUName = appUserDAO.findUserDetails(getUserDetails()).getFirstName() != null
				? appUserDAO.findUserDetails(getUserDetails()).getFirstName()
				: "" + appUserDAO.findUserDetails(getUserDetails()).getMiddleName() != null
						? appUserDAO.findUserDetails(getUserDetails()).getMiddleName()
						: "" + appUserDAO.findUserDetails(getUserDetails()).getLastName() != null
								? appUserDAO.findUserDetails(getUserDetails()).getLastName()
								: "";
		auditBeanBo.setAuditBean(Integer.parseInt(userDetailsService.getUserDetails().getUserId().toString()),
				loginUName, "User", Integer.parseInt(userDetailsService.getUserDetails().getUserId().toString()),
				utils.getMessage("log.OrderApprove.stage1"), utils.getMessage("log.OrderApproved.stage1"), loginUName,
				"true");
		auditBeanBo.save();
		model.addAttribute("message",
				"Office Order approved and sent for approval successfully to Director SFIO for case No : "
						+ caseDetailsDto.getCaseStrId());
		return "case/apprOOSuccess1";
	}

	/// director part..........

	@RequestMapping(value = "/showPendingOfficeOrderDir")
	public String showPendingOfficeOrderDir(Model model) {

		List<Object[]> list = userMangCustom.findOfficeOrderPendingForApproval(2);

		List<PendingForApprovalDTO> pending = new ArrayList<PendingForApprovalDTO>();

		for (Object[] dto : list) {
			pending.add(new PendingForApprovalDTO(Long.parseLong(dto[0].toString()), dto[1].toString(),
					dto[2].toString(), Long.parseLong(dto[3].toString()) ,dto[4].toString()));
		}
		model.addAttribute("pending", pending);
		CaseDetails caseDetails = new CaseDetails();
		model.addAttribute("caseDetails", caseDetails);
		return "director/officeOrderPending";
	}

	/*
	 * @RequestMapping(value = "/showCaseDetailsDir", params = "caseDetails") public
	 * String showCaseDetailsDir(@RequestParam(value = "caseDetails", required =
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
	 * model.addAttribute("comList", comList); return "director/previewCaseDetails";
	 * }
	 */
	@RequestMapping(value = "/showCaseDetailsDir", params = "caseDetails")
	public String showCaseDetailsDir(@RequestParam(value = "caseDetails", required = true) Long caseId, Model model) {

		SNMSValidator snmsValid = new SNMSValidator();

		if (!snmsValid.getValidInteger(caseId)) {
			model.addAttribute("message", "Invalid caseId ");
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
		return "director/previewCaseDetails";
	}

	@RequestMapping(value = "/showCaseDetailsDir", params = "approve")
	public String showCaseDetailsDirApprove(@RequestParam(value = "approve", required = true) Long officeOrderId,
			Model model) {
		SNMSValidator snmsValid = new SNMSValidator();

		if (!snmsValid.getValidInteger(officeOrderId)) {
			model.addAttribute("message", "Invalid Id ");
			return "caseDetails/ErrorPage";
		}
		Optional<OfficeOrder> officeOrder = officeOrderRepo.findById(officeOrderId);
		OfficeOrder office = officeOrder.get();
		CaseDetails caseDetails = office.getCaseDetails();
//		OfficeOrderTemplate draft = officeOrderTempRepo.findByOfficeOrder(office);
		InitiatorOfficeOrderDraft draft = officeOrderDao.findDraftByCaseDetails(caseDetails);
		OfficeOrderTempDto caseDto = new OfficeOrderTempDto(office.getOoDin(), Utils.getCurrentDateWithMonth(),
				draft.getPara1(), draft.getPara2(), draft.getPara3(), draft.getPara4(), draft.getPara5(),
				office.getId(), caseDetails.getCaseId(), caseDetails.getId(), getDirectorName(), "");

		model.addAttribute("caseDto", caseDto);
		InspectorListDTO inspList = getInspectorListAndCompany(caseDetails.getId());
		/*
		 * inspList.getInspctorList().removeIf( insp -> insp.isAdo()==true );
		 * inspList.getCopyToList().removeIf( insp -> insp.isAdo()==true );
		 */
		model.addAttribute("inspList", inspList.getInspctorList());
		model.addAttribute("copyToList", inspList.getCopyToList());
		model.addAttribute("listCom", inspList.getCompanyList());
		/*
		 * List<Object[]> list = userMangCustom.findOfficeOrderPendingForApproval(2);
		 * List<PendingForApprovalDTO> pending = new ArrayList<PendingForApprovalDTO>();
		 * for (Object[] dto : list) { pending.add(new
		 * PendingForApprovalDTO(Long.parseLong(dto[0].toString()), dto[1].toString(),
		 * dto[2].toString(), Long.parseLong(dto[3].toString()))); }
		 * model.addAttribute("pending", pending);
		 */

		return "director/approveOfficeOrder";
	}

	@RequestMapping(value = "/approveOffOrdDir")
	public String approveOfficeOrderDirector(@ModelAttribute OfficeOrderTempDto caseDetailsDto, Model model)
			throws Exception {

		Optional<CaseDetails> caseDtls = caseDetailsRepository.findById(caseDetailsDto.getCaseId());

		String ooDIN = "";
		ooDIN = caseDtls.get().getFinanceYear().substring(caseDtls.get().getFinanceYear().lastIndexOf('-') + 1)
				+ caseDtls.get().getFinanceYear().substring(0, caseDtls.get().getFinanceYear().lastIndexOf('-'))
						.concat(SnmsConstant.OO_DIN_ID);
		String sequence = officeOrderRepo.getOfficeOrderDinSequence().toString();
		ooDIN = ooDIN.concat("00000".substring(sequence.length()).concat(sequence))
				.concat(utils.getRandomAlphaNum(SnmsConstant.DIN_RANDOM_DIGIT));

		officeOrderRepo.approveInfoByIdStage2(new CaseDetails(caseDetailsDto.getCaseId()), ooDIN);
		OfficeOrder order = new OfficeOrder(caseDetailsDto.getOfficeOrderId());
		Long srNo = (officeOrderTempRepo.findMaxSrNo(order) == null) ? 1 : officeOrderTempRepo.findMaxSrNo(order) + 1;
		OfficeOrderTemplate officeOrderTemplate = new OfficeOrderTemplate(order, srNo, caseDetailsDto.getPara1(),
				caseDetailsDto.getPara2(), caseDetailsDto.getPara3(), caseDetailsDto.getPara4(),
				caseDetailsDto.getPara5(), true, userDetailsService.getUserDetails(), new Date());
		officeOrderTempRepo.save(officeOrderTemplate);
		String loginUName = appUserDAO.findUserDetails(getUserDetails()).getFirstName() != null
				? appUserDAO.findUserDetails(getUserDetails()).getFirstName()
				: "" + appUserDAO.findUserDetails(getUserDetails()).getMiddleName() != null
						? appUserDAO.findUserDetails(getUserDetails()).getMiddleName()
						: "" + appUserDAO.findUserDetails(getUserDetails()).getLastName() != null
								? appUserDAO.findUserDetails(getUserDetails()).getLastName()
								: "";
		auditBeanBo.setAuditBean(Integer.parseInt(userDetailsService.getUserDetails().getUserId().toString()),
				loginUName, "User", Integer.parseInt(userDetailsService.getUserDetails().getUserId().toString()),
				utils.getMessage("log.OrderApprove.stage2"), utils.getMessage("log.OrderApproved.stage2"), loginUName,
				"true");
		auditBeanBo.save();
		/*
		 * model.addAttribute("message",
		 * "Office Order approved successfully for case id : " +
		 * caseDetailsDto.getCaseStrId() + " and din " + ooDIN);
		 */
		model.addAttribute("message", "Office Order approved successfully for case id : "
				+ caseDetailsDto.getCaseStrId() + ". Please esign it. " );
		return "case/apprOOSuccess1";
	}

	// fetching all the list of companies and inspectors from the blew method

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

	@RequestMapping(value = "/OfficeOrderPDF", params = "preview", produces = MediaType.APPLICATION_PDF_VALUE)
	public ResponseEntity<InputStreamResource> OfficeOrderPDF(
			@RequestParam(value = "preview", required = true) Long officeOrderId, Model model) throws IOException {

		SNMSValidator snmsValid = new SNMSValidator();

		if (!snmsValid.getValidInteger(officeOrderId)) {
			model.addAttribute("message", "Invalid Id ");
			return null;
		}
		Optional<OfficeOrder> order = officeOrderRepo.findById(officeOrderId);

		OfficeOrder officeOrder = order.get();

		CaseDetails caseDetails = officeOrder.getCaseDetails();

		String ioName = "";
		List<Object[]> inspectorList = userMangCustom.findByCase(caseDetails.getId());

		List<String> inspector = new ArrayList<String>();

		for (Object[] dto : inspectorList) {
//			if (!(Boolean) dto[4])  // Check not to add Additional director
			inspector.add(dto[0].toString() + " " + dto[1].toString() + " (" + dto[2].toString() + ")");
			if ((Boolean) dto[3])
				ioName = dto[1].toString() + " (" + dto[2].toString() + ")";
		}

		List<String> company = new ArrayList<String>();

		List<Company> comList = userMangCustom.findByCaseId(new CaseDetails(caseDetails.getId()));

		for (Company com : comList) {
			company.add(com.getName());
		}

		String director = getDirectorName();
		Optional<CaseDetails> caseD = caseDetailsRepository.findById(caseDetails.getId());
		CaseDetails caseDetail = caseD.get();
		/*
		 * ByteArrayInputStream bis = OfficeOrerPDF .officeOrderFixed(new
		 * OfficeOrderDto(dated, caseDetails.getCaseId(), inspector, ioName, company,
		 * director));
		 * 
		 * HttpHeaders headers = new HttpHeaders(); headers.add("Content-Disposition",
		 * "attachment; filename=" + caseDetails.getCaseId() + ".pdf");
		 * 
		 * return
		 * ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF)
		 * .body(new InputStreamResource(bis));
		 */
		InitiatorOfficeOrderDraft officeDraft = officeOrderDao.findDraftByCaseDetails(caseDetail);
		if (officeDraft == null) {
			ByteArrayInputStream bis = OfficeOrerPDF.officeOrderFixed(new OfficeOrderDto(caseDetail.getMcaDate(),
					caseDetails.getCaseId(), inspector, ioName, company, "", "", caseDetail.getMcaOrderNo()));
			HttpHeaders headers = new HttpHeaders();
			headers.add("Content-Disposition", "inline; filename=customers.pdf");

			return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF)
					.body(new InputStreamResource(bis));
		} else {
			ByteArrayInputStream bis = OfficeOrerPDF.officeOrderSaved(
					new OfficeOrderDto(Utils.getCurrentDateWithMonth(), officeOrder.getOoDin(), inspector, ioName,
							company, director, officeDraft.getPara1(), officeDraft.getPara2(), officeDraft.getPara3(),
							officeDraft.getPara4(), officeDraft.getPara5(), officeOrder.getApprovalDate()));
			HttpHeaders headers = new HttpHeaders();
			headers.add("Content-Disposition", "inline; filename=");
			return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF)
					.body(new InputStreamResource(bis));
		}
	}

	@RequestMapping(value = "/generatePDF1", produces = MediaType.APPLICATION_PDF_VALUE)
	public ResponseEntity<InputStreamResource> generatePDF1(@ModelAttribute OfficeOrderTempDto caseDetailsDto)
			throws IOException {

		Optional<CaseDetails> caseD = caseDetailsRepository.findById(caseDetailsDto.getCaseId());
		CaseDetails caseDetails = caseD.get();
		String ioName = "";
		List<Object[]> inspectorList = userMangCustom.findByCase(caseDetailsDto.getCaseId());

		List<String> inspector = new ArrayList<String>();

		for (Object[] dto : inspectorList) {
//			if(!(Boolean) dto[4]) // Check not to add Additional director
			inspector.add(dto[0].toString() + " " + dto[1].toString() + " (" + dto[2].toString() + ")");
			if ((Boolean) dto[3])
				ioName = dto[1].toString() + " (" + dto[2].toString() + ")";
		}

		List<String> company = new ArrayList<String>();

		List<Company> comList = userMangCustom.findByCaseId(new CaseDetails(caseDetailsDto.getCaseId()));

		for (Company com : comList) {
			company.add(com.getName());
		}
		String director = "Amardeep Singh Bhatia";

		InitiatorOfficeOrderDraft officeDraft = officeOrderDao.findDraftByCaseDetails(caseDetails);
		if (officeDraft == null) {
			ByteArrayInputStream bis = OfficeOrerPDF.officeOrderFixed(new OfficeOrderDto(
					new Utils().getCurrentDateFormat(caseDetails.getMcaOrderDate()), caseDetailsDto.getCaseNo(),
					inspector, ioName, company, director, caseDetailsDto.getClause(), caseDetails.getMcaOrderNo()));
			HttpHeaders headers = new HttpHeaders();
			headers.add("Content-Disposition", "inline; filename=customers.pdf");
			return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF)
					.body(new InputStreamResource(bis));
		} else {
			ByteArrayInputStream bis = OfficeOrerPDF
					.officeOrderSaved(new OfficeOrderDto(Utils.getCurrentDateWithMonth(), caseDetailsDto.getCaseStrId(),
							inspector, ioName, company, director, officeDraft.getPara1(), officeDraft.getPara2(),
							officeDraft.getPara3(), officeDraft.getPara4(), officeDraft.getPara5()));
			HttpHeaders headers = new HttpHeaders();
			headers.add("Content-Disposition", "inline; filename=customers.pdf");
			return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF)
					.body(new InputStreamResource(bis));
		}
	}

	@RequestMapping(value = "/showCaseDetails", params = "caseDetails")
	public String showCaseDetails(@RequestParam(value = "caseDetails", required = true) Long caseId, Model model) {

		SNMSValidator snmsValid = new SNMSValidator();

		if (!snmsValid.getValidInteger(caseId)) {
			model.addAttribute("message", "Invalid Id ");
			return "caseDetails/ErrorPage";
		}
		Optional<CaseDetails> details = caseDetailsRepository.findById(caseId);
		CaseDetails caseDetails = details.get();
		model.addAttribute("caseDetails", caseDetails);
		OfficeOrder officeorder = officeOrderRepo.findAllByCaseDetails(caseDetails);
		
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

		return "case/previewCaseDetails";
	}
	/*
	 * @RequestMapping(value = "/showCaseDetails", params = "caseDetails") public
	 * String showCaseDetails(@RequestParam(value = "caseDetails", required = true)
	 * Long caseId, Model model) {
	 * 
	 * Optional<CaseDetails> details = caseDetailsRepository.findById(caseId);
	 * CaseDetails caseDetails = details.get(); model.addAttribute("caseDetails",
	 * caseDetails); List<Object[]> inspectorList =
	 * userMangCustom.findByCase(caseDetails.getId()); int i = 1; List<InspectorDTO>
	 * inspList = new ArrayList<InspectorDTO>(); for (Object[] dto : inspectorList)
	 * { InspectorDTO ins = new InspectorDTO(i, dto[0].toString() + " " +
	 * dto[1].toString() + " (" + dto[2].toString() + ")", (Boolean) dto[3]);
	 * inspList.add(ins); i++; }
	 * 
	 * model.addAttribute("inspList", inspList); List<Company> comList =
	 * userMangCustom.findByCaseId(new CaseDetails(caseDetails.getId()));
	 * model.addAttribute("comList", comList); return "case/previewCaseDetails"; }
	 */

	@RequestMapping(value = "/downloadFiles", method = RequestMethod.GET)
	public ResponseEntity<Resource> downloadFileFromLocal(@Param(value = "fileName") String fileName)
			throws IOException, IntrusionException, ValidationException {

		SNMSValidator snmsVal = new SNMSValidator();
		if (!snmsVal.isValidFileName(fileName)) {
			return null;
		} else {
			File parent = new File(filePath).getParentFile().getCanonicalFile();
			ESAPI.validator().getValidDirectoryPath("DirectoryName", filePath, parent, false);
			Path path = Paths.get(filePath + File.separator + fileName);
			Resource resource = null;
			try {
				resource = new UrlResource(path.toUri());
			} catch (MalformedURLException e) {
				logger.info(e.getMessage());
			}

			HttpHeaders headers = new HttpHeaders();
			headers.add("Content-Disposition", "inline; filename=" + fileName);

			return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(resource);

		}

	}

	SimpleDateFormat formatter = new SimpleDateFormat("dd-yyyy");

	@RequestMapping(value = "/uploadFile", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<?> uploadFile(@RequestParam("uploadfile") MultipartFile uploadfile) {
		BufferedOutputStream stream = null;
		
		try {
			// Get the filename and build the local file path (be sure that the
			// application have write permissions on such directory)
			String filename = uploadfile.getOriginalFilename();
			String directory = "E:/eformnfra_app_data/case";

			File parent = new File(directory).getParentFile().getCanonicalFile();
			directory = ESAPI.validator().getValidDirectoryPath("DirectoryName", directory, parent, false);
                
			String filepath = null;
			 Boolean validFileName = ESAPI.validator().isValidFileName("FileName", filename, false);
			  if(validFileName == true) {
			filepath = Paths.get(directory + File.separator + filename).toString();

			  }
			// Save the file locally
			 stream = new BufferedOutputStream(new FileOutputStream(new File(filepath)));
			stream.write(uploadfile.getBytes());
			stream.close();
		} catch (Exception e) {

			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		  finally {
				
				if(stream!=null) {
					safeClose(stream);
				}
			}
		return new ResponseEntity<>(HttpStatus.OK);
	} // method upload

	@RequestMapping(value = "/getCompanyListByCaseId", method = RequestMethod.POST)
	public @ResponseBody List<CompanySummon> getCompanyListByCaseId(@RequestParam("caseId") Long caseId, Model model) {
		SNMSValidator snmsvalid = new SNMSValidator();
		if (snmsvalid.getValidInteger(caseId)) {
			SummonDetails findByCaseId = summonDao.findByCaseId(caseId);
			if (findByCaseId != null)
				return findByCaseId.getCompanySummon();

			else
				return null;
		} else {
			return null;
		}
	}

	@RequestMapping(value = "/getIndividualType", method = RequestMethod.POST)
	public @ResponseBody List<Object[]> getIndividualType(@RequestParam("compId") int compId, Model model) {

		SNMSValidator snmsvalid = new SNMSValidator();
		if (!snmsvalid.getValidInteger(compId)) {
			return null;
		}
		List<Object[]> summonType = summonDao.getIndividualType(compId);
		return summonType;
	}

	@RequestMapping(value = "/getNoticeBySmnTypId", method = RequestMethod.POST)
	public @ResponseBody List<NoticeStatus> getNoticeBySmnTypId(@RequestParam("stypeId") int stypeId, Model model) {

		SNMSValidator snmsvalid = new SNMSValidator();
		if (!snmsvalid.getValidInteger(stypeId)) {
			return null;
		}
		List<NoticeStatus> noticeStatus = summonDao.getNoticeBySummonType(new SummonType(stypeId));
		return noticeStatus;
	}

	@RequestMapping(value = "/getSummonBySmnTypId", method = RequestMethod.POST)
	public @ResponseBody List<InitiateSummonDraft> getSummonBySmnTypId(@RequestParam("stypeId") int stypeId,
			Model model) {
		SNMSValidator snmsvalid = new SNMSValidator();
		if (!snmsvalid.getValidInteger(stypeId)) {
			return null;
		}
		List<InitiateSummonDraft> summonStatus = summonDao.getSummonBySmnTypId(new SummonType(stypeId));
		return summonStatus;
	}

	@RequestMapping(value = "/getInspectorListByUnitId", method = RequestMethod.POST)
	public @ResponseBody List<Inspector> getInspectorListByUnitId(@RequestParam("unitId") Long unitId, Model model) {
		
		SNMSValidator snmsvalid = new SNMSValidator();
		if (!snmsvalid.getValidInteger(unitId)) {
			return null;
		}
		List<UserDetails> unituserList = userMangCustom.findByRole(SnmsConstant.Role_USER, new UnitDetails(unitId));
		int count = 1;
		List<Inspector> inspList = new ArrayList<Inspector>();
		for (UserDetails user : unituserList) {
			inspList.add(new Inspector(count,
					user.getSalutation() + " " + userDetailsService.getFullName(user) + " ("
							+ user.getUnit().getUnitName() + ")",
					user.getDesignation().getDesignation(), 2, user.getUserId().getUserId(), false,
					userMangCustom.getCasesAssigned(user.getUserId().getUserId())));
			count++;
		}
		return inspList;
	}
	@RequestMapping(value = "/getInspectorListReportByUnitId", method = RequestMethod.GET)
	public @ResponseBody List<Inspector> getInspectorListReportByUnitId(@RequestParam("unitId") Long unitId, Model model) {
		
		SNMSValidator snmsvalid = new SNMSValidator();
		if (!snmsvalid.getValidInteger(unitId)) {
			return null;
		}
		List<UserDetails> unituserList = userMangCustom.findByRole(SnmsConstant.Role_USER, new UnitDetails(unitId));
		int count = 1;
		List<Inspector> inspList = new ArrayList<Inspector>();
		for (UserDetails user : unituserList) {
			inspList.add(new Inspector(count,
					user.getSalutation() + " " + userDetailsService.getFullName(user) + " ("
							+ user.getUnit().getUnitName() + ")",
					user.getDesignation().getDesignation(), 2, user.getUserId().getUserId(), false,
					userMangCustom.getCasesAssigned(user.getUserId().getUserId())));
			count++;
		}
		return inspList;
	}
	@RequestMapping(value = "/getDeletedInspectorList", method = RequestMethod.POST)
	public @ResponseBody List<InspectorDetailDTO> getDeletedInspectorList(@RequestParam("caseID") Long caseID,
			Model model) {

		SNMSValidator snmsvalid = new SNMSValidator();
		if (!snmsvalid.getValidInteger(caseID)) {
			return null;
		}
		InspectorEditDTO inspList1 = getdelInspectorList(caseID);
		return inspList1.getInspctorList();

		// return inspList;
	}

	private InspectorEditDTO getdelInspectorList(Long caseId) {
		List<Object[]> inspectorList = userMangCustom.findDelInpBYCaseId(caseId);
		String ioName = "";
		int i = 1;
		List<InspectorDetailDTO> inspList = new ArrayList<InspectorDetailDTO>();
		List<InspectorDetailDTO> copyToList = new ArrayList<InspectorDetailDTO>();
		for (Object[] dto : inspectorList) {
			int iduser = (int) dto[5];
			String ToDate = null;
			String FromDate = null;

			DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
			if ((Date) dto[8] != null) {
				ToDate = dateFormat.format((Date) dto[8]);
			} else {
				ToDate = "";
			}

			if ((Date) dto[7] != null) {
				FromDate = dateFormat.format((Date) dto[7]);
			} else {
				FromDate = "";
			}

			// Integer grandChildCount = ((BigInteger) dto[6]).intValue();
			InspectorDetailDTO ins = new InspectorDetailDTO(i, dto[0].toString() + " " + dto[1].toString(),
					(Boolean) dto[3], (Boolean) dto[4], dto[2].toString(),
					userMangCustom.getCasesAssigned(Long.valueOf(iduser)), iduser, FromDate, ToDate);
			inspList.add(ins);
			copyToList.add(ins);
			if ((Boolean) dto[3])
				ioName = dto[1].toString() + " (" + dto[2].toString() + ")";
			i++;
		}
		int length = copyToList.size();
		// copyToList.add(new InspectorDTO(length + 1, "PPS to Director, SFIO",
		// false,false));
		// copyToList.add(new InspectorDTO(length + 2, "Guard File", false,false));
		return new InspectorEditDTO(inspList);
	}

	@RequestMapping(value = "/orderStatus")
	public String orderStatus(Model model) {

		List<NoticeStatus> notice = new ArrayList<NoticeStatus>();
		model.addAttribute("notice", notice);
		List<SummonStatus> summon = new ArrayList<SummonStatus>();
		model.addAttribute("summon", summon);
		List<personcompanyApproval> personStatus = new ArrayList<personcompanyApproval>();
		model.addAttribute("personStatus",personStatus);
		
		List<OfficeOrder> officeOrder = officeOrderRepo.findAll();
		model.addAttribute("officeOrder", officeOrder);
		return "caseDetails/status";
	}

	

	@RequestMapping(value = "/PendingOfficestatus")
	public String OfficeorderStatus(Model model) {

		List<OfficeOrder> officeOrder = officeOrderRepo.findAllByAprrovalStage1(false);
		model.addAttribute("officeOrder", officeOrder);
		return "caseDetails/PendingOfficestatus";
	}

	@RequestMapping(value = "/PendingForForwarding")
	public String PendingForForwarding(Model model) {

		List<CaseDetails> caseDetails = userMangCustom.findPeningForfarwarding();
		model.addAttribute("caseList", caseDetails);
		return "caseDetails/PendingForForwarding";
	}
	
	@RequestMapping(value = "/casesAsIoAdo")
	public String casesAsIoAdo(ModelMap modelMap) throws Exception {
		List<Object[]> penNotice = userMangCustom.findNoticePendingForApproval(1,
				userDetailsService.getUserDetails().getUserId());
		List<Object[]> penPhyNotice = userMangCustom.findPhyNoticePendingForApproval(1,
				userDetailsService.getUserDetails().getUserId(),true);
		List<Object[]> penSummon = userMangCustom.findSummonPendingForApproval(1,
				userDetailsService.getUserDetails().getUserId());
		List<Object[]> penPhySummon = userMangCustom.findPhySummonPendingForApproval(1,
				userDetailsService.getUserDetails().getUserId(),true);
		List<Company> penCompany = userMangCustom.findCompanyPendingForApproval(1,
				userDetailsService.getUserDetails().getUserId(),false,1L);
		List<CaseDetails> penCompanyByCase = userMangCustom.findCompanyByCasePendingForApproval(1,
				userDetailsService.getUserDetails().getUserId(),false,1L);
		
		List<personcompanyApproval> gamspersonlist = userMangCustom.findpersonPendingListForApproval(1,
				userDetailsService.getUserDetails().getUserId(),1);
		modelMap.addAttribute("totlaPendNotice", penNotice.size());
		modelMap.addAttribute("totlagamsperson", gamspersonlist.size());
		modelMap.addAttribute("totlaPendSummon", penSummon.size());
		modelMap.addAttribute("totlaPenPhySummon", penPhySummon.size());
		modelMap.addAttribute("totlaPenPhyNotice", penPhyNotice.size());
		modelMap.addAttribute("totlapenCompany", penCompany.size());
		modelMap.addAttribute("totalpenCompanyByCase", penCompanyByCase.size());
		return "ado/adlDashboard";
	}

	@RequestMapping(value = "/pendingForAppr")
	public String adoHome(ModelMap modelMap) throws Exception {
		List<Object[]> penOrder = userMangCustom.findOfficeOrderPendingForApproval();
		List<Object[]> penNotice = userMangCustom.findNoticePendingForApproval(1,
				userDetailsService.getUserDetails().getUserId());
		List<Object[]> penSummon = userMangCustom.findSummonPendingForApproval(1,
				userDetailsService.getUserDetails().getUserId());
		modelMap.addAttribute("totlaPendOrder", penOrder.size());
		modelMap.addAttribute("totlaPendNotice", penNotice.size());
		modelMap.addAttribute("totlaPendSummon", penSummon.size());
		return "ado/pendingForApproval";
	}

	@RequestMapping(value = "/dirPendingApp")
	public String dirPendingApp(ModelMap modelMap) throws Exception {
		List<Object[]> penOrder = userMangCustom.findOfficeOrderPendingForApproval(2);
		List<Object[]> penNotice = userMangCustom.findNoticePendingForApproval(1,
				userDetailsService.getUserDetails().getUserId());
		List<Object[]> penSummon = userMangCustom.findSummonPendingForApproval(2,getUserDetails().getUserId());

		modelMap.addAttribute("totlaPendOrder", penOrder.size());
		modelMap.addAttribute("totlaPendNotice", penNotice.size());
		modelMap.addAttribute("totlaPendSummon", penSummon.size());
		return "director/dirPendingAppoval";
	}

	@RequestMapping(value = "/legacyOrder")
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
		return "caseDetails/legacyOrder";
	}

	@RequestMapping(value = "/addLegacyDetails", method = RequestMethod.POST)
	public @ResponseBody CaseJsonResponse addLegacyDetails(HttpServletRequest request,
			@Valid @ModelAttribute("caseDetails") CaseDetails caseDetails, BindingResult bindResult, Model model)
			throws Exception {
		CaseJsonResponse caseJsonResponse = new CaseJsonResponse();
		UserValidation validation = new UserValidation();
		validation.validateLegacyDetails(caseDetails, bindResult);

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
			caseJsonResponse.setErrorsMap(errors);
			caseJsonResponse.setCaseDetails(caseDetails);
			caseJsonResponse.setStatus("ERROR");
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

			UnitDetails unitDtls = unitDetailsRepo.findById(caseDetails.getUnitId()).get();
			unit = unitDtls.getUnitName();

			
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
			
			
			//Gouthami 27092021 
			//   setUnitId corresponding to Adouser
			long userAdoId=Long.parseLong(caseDetails.getChooseAdo());  
			 UserDetails ud = userDetailsRepo.findById(userAdoId).get();
			caseDetails.setUnit(ud.getUnit());
			
			
			
			
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
			InspectorEditDTO inspList1 = getAllInspectorList(caseDetails.getId());
			List<Inspector> inspectorList = new ArrayList<Inspector>();
			boolean flag = false;
			long inspectorid = 0;
			for (int i = 0; i < caseDetails.getInspectors().length; i++) {
//				JSONObject object = inspectorArray.getJSONObject(i);

				int userid = Integer.parseInt(caseDetails.getInspectors()[i]);
				for (int j = 0; j < inspList1.getInspctorList().size(); j++) {
					int inspectoeId = inspList1.getInspctorList().get(j).getUserID();

					if (inspList1.getInspctorList().get(j).getUserID() == userid) {
						flag = true;

						Inspector ispect = inspectorDao.findAllByCaseIdAndUserId(caseDetails.getId().intValue(),
								userid);
						inspectorid = ispect.getId();

						break;

					}

				}
				if (caseDetails.getInspectors()[i].equals(caseDetails.getChooseIo().replace(",", ""))) {

					if (flag == true) {
						inspectorList.add(new Inspector(inspectorid, caseDetails,
								new AppUser(Long.parseLong(caseDetails.getInspectors()[i])), true));

						flag = false;

					} else {
						inspectorList.add(new Inspector(caseDetails,
								new AppUser(Long.parseLong(caseDetails.getInspectors()[i])), true));
					}
				} else

				if (flag == true) {
					inspectorList.add(new Inspector(inspectorid, caseDetails,
							new AppUser(Long.parseLong(caseDetails.getInspectors()[i])), false));
					flag = false;
				} else {
					inspectorList.add(

							new Inspector(caseDetails, new AppUser(Long.parseLong(caseDetails.getInspectors()[i])),
									false));

				}
			}
			if (caseDetails.getDeleteIO() != null) {
				for (int k = 0; k < caseDetails.getDeleteIO().length; k++) {
					String delteuserid = caseDetails.getDeleteIO()[k];
					for (int j = 0; j < inspList1.getInspctorList().size(); j++) {
						if (inspList1.getInspctorList().get(j).getUserID() == Integer.valueOf(delteuserid)) {
							Inspector ispect = inspectorDao.findAllByCaseIdAndUserId(caseDetails.getId().intValue(),
									Integer.parseInt(delteuserid));
							long delid = ispect.getId();
							if (delteuserid.equals(caseDetails.getChooseIo().replace(",", ""))) {

								inspectorList.add(new Inspector(delid, caseDetails,
										new AppUser(Long.parseLong(delteuserid)), true));
							} else {
								inspectorList.add(new Inspector(delid, caseDetails,
										new AppUser(Long.parseLong(delteuserid)), false));

							}
						}

					}
				}

			}

			String deleteIO[] = caseDetails.getDeleteIO();
			String choosenAdo = caseDetails.getChooseAdo();
			inspectorList.forEach(inspector -> {
				if (inspector.getAppUser().getUserId() == Long.parseLong(choosenAdo)) {
					inspector.setIsAdo(true);
					inspector.setIsActive(true);

				} else {
					inspector.setIsAdo(false);
					inspector.setIsActive(true);

					if (deleteIO != null) {

						for (int k = 0; k < deleteIO.length; k++) {
							String delteuserid = deleteIO[k];
							if (inspector.getAppUser().getUserId() == Long.parseLong(delteuserid)) {

								inspector.setIsActive(false);

								inspector.setIsAdo(false);
								break;
							}

						}
					}
				}
			});
			inspectorRepo.saveAll(inspectorList);
			caseDetails.setEditLagacy(true);
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
			String unit = "";
			UnitDetails unitDtls = unitDetailsRepo.findById(caseDetails.getUnitId()).get();
			unit = unitDtls.getUnitName();

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
			//Gouthami 27092021 
			//   setUnitId corresponding to Adouser
			long userAdoId=Long.parseLong(caseDetails.getChooseAdo());  
			 UserDetails ud = userDetailsRepo.findById(userAdoId).get();
             caseDetails.setUnit(ud.getUnit());
			
			caseDetails.setEditLagacy(false);
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

			for (int i = 0; i < caseDetails.getInspectors().length; i++) {
				if (caseDetails.getInspectors()[i].equals(caseDetails.getChooseIo().replace(",", "")))
					inspectorList.add(new Inspector(caseDetails,
							new AppUser(Long.parseLong(caseDetails.getInspectors()[i])), true));
				else
					inspectorList.add(new Inspector(caseDetails,
							new AppUser(Long.parseLong(caseDetails.getInspectors()[i])), false));
			}

			String choosenAdo = caseDetails.getChooseAdo();
			inspectorList.forEach(inspector -> {
				if (inspector.getAppUser().getUserId() == Long.parseLong(choosenAdo)) {
					inspector.setIsAdo(true);
					inspector.setIsActive(true);
				} else {
					inspector.setIsAdo(false);
					inspector.setIsActive(true);
				}
			});

			inspectorRepo.saveAll(inspectorList);

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

	@RequestMapping(value = "/legacyCaseSuccess")
	public String legacyCaseSuccess(@ModelAttribute("caseId") String caseId, @ModelAttribute("id") Long id,
			@ModelAttribute("mcaOrderNo") String mcaOrderNo, @ModelAttribute("mcaOrderDate") String orderDate,
			Model model) {
		model.addAttribute("message", "Legacy Case Details Added/Updated Successfully for case id : " + caseId);
		CaseDetails caseDetails = new CaseDetails();
		caseDetails.setId(id);
		caseDetails.setCaseId(caseId);
		caseDetails.setMcaOrderNo(mcaOrderNo);
		caseDetails.setMcaDate(orderDate);
		model.addAttribute("caseDetails", caseDetails);
		return "caseDetails/legacyCaseSuccess";
	}

	@RequestMapping(value = "/generateLegacyOfficeOrder", params = "forward")
	public String generateLegacyOfficeOrder(@ModelAttribute CaseDetails caseDetailsDto, Model model) throws Exception {

		Optional<CaseDetails> caseDtls = caseDetailsRepository.findById(caseDetailsDto.getId());

		/*
		 * String ooDIN=""; ooDIN =
		 * caseDtls.get().getFinanceYear().substring(caseDtls.get().getFinanceYear().
		 * lastIndexOf('-')+1)
		 * +caseDtls.get().getFinanceYear().substring(0,caseDtls.get().getFinanceYear().
		 * lastIndexOf('-')).concat(SnmsConstant.OO_DIN_ID); String sequence =
		 * officeOrderRepo.getOfficeOrderDinSequence().toString(); ooDIN =
		 * ooDIN.concat("00000".substring(sequence.length()).concat(sequence)).concat(
		 * utils.getRandomAlphaNum(SnmsConstant.DIN_RANDOM_DIGIT));
		 */

		OfficeOrder order = new OfficeOrder(new CaseDetails(caseDetailsDto.getId()), false, false,
				userDetailsService.getUserDetails(), new Date());
		order.setUnsignFile(caseDtls.get().getLegacyOrderFile());
//		 order.setOoDin(ooDIN);
		order = officeOrderRepo.save(order);
		/*
		 * Long srNo = (officeOrderTempRepo.findMaxSrNo(order) == null) ? 1 :
		 * officeOrderTempRepo.findMaxSrNo(order) + 1;
		 * 
		 * 
		 * InitiatorOfficeOrderDraft office = (officeOrderDao.findDraftByCaseDetails(new
		 * CaseDetails(caseDetailsDto.getCaseId())) == null) ? initiatorOffOrdRepo.save(
		 * new InitiatorOfficeOrderDraft(new CaseDetails(caseDetailsDto.getCaseId()),
		 * caseDetailsDto.getPara1(), caseDetailsDto.getPara2(),
		 * caseDetailsDto.getPara3(), caseDetailsDto.getPara4(),
		 * caseDetailsDto.getPara5(), false, userDetailsService.getUserDetails(), new
		 * Date())) : null;
		 * 
		 * OfficeOrderTemplate officeOrderTemplate = new OfficeOrderTemplate(order,
		 * srNo, caseDetailsDto.getPara1(), caseDetailsDto.getPara2(),
		 * caseDetailsDto.getPara3(), caseDetailsDto.getPara4(),
		 * caseDetailsDto.getPara5(), true, userDetailsService.getUserDetails(), new
		 * Date()); officeOrderTempRepo.save(officeOrderTemplate);
		 */
		model.addAttribute("message",
				"Office Order Created and sent for approval successfully for case '" +caseDtls.get().getCaseTitle()+"'");
		return "caseDetails/officeOrderSuccess";
	}

	@RequestMapping(value = "/generateLegacyOfficeOrder", params = "approve")
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
		if(caseDtls.get().getCaseStage()==1) {
		model.addAttribute("message", "Legacy Office Order approved successfully for caseId : SFIO/INV/2020/"
				+ order.getId() + " and DIN " + ooDIN);
		}else if(caseDtls.get().getCaseStage()==2) {
			model.addAttribute("message", "Procesution  Office Order approved successfully for caseId " +caseDetailsDto.getCaseId()+" "
					+ order.getId() + " and DIN " + ooDIN);
		}
		return "caseDetails/officeOrderSuccess";
	}

	@GetMapping(path = "/hello", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> sayHello() {
		ArrayList<AppRole> cars = new ArrayList<AppRole>();
		AppRole empOne = new AppRole(1L);

		cars.add(empOne);

		List<JSONObject> entities = new ArrayList<JSONObject>();
		for (AppRole n : cars) {
			JSONObject entity = new JSONObject();
			entity.put("aa", "bb");
			entities.add(entity);
		}
		return new ResponseEntity<Object>(entities, HttpStatus.OK);
	}

	private InspectorEditDTO getAllInspectorList(Long caseId) {
		List<Object[]> inspectorList = userMangCustom.findAllInspByCaseId(caseId);
		String ioName = "";
		int i = 1;
		List<InspectorDetailDTO> inspList = new ArrayList<InspectorDetailDTO>();
		List<InspectorDetailDTO> copyToList = new ArrayList<InspectorDetailDTO>();
		for (Object[] dto : inspectorList) {
			int iduser = (int) dto[5];

			// Integer grandChildCount = ((BigInteger) dto[6]).intValue();
			InspectorDetailDTO ins = new InspectorDetailDTO(i, dto[0].toString() + " " + dto[1].toString(),
					(Boolean) dto[3], (Boolean) dto[4], dto[2].toString(),
					userMangCustom.getCasesAssigned(Long.valueOf(iduser)), iduser);
			inspList.add(ins);
			copyToList.add(ins);
			if ((Boolean) dto[3])
				ioName = dto[1].toString() + " (" + dto[2].toString() + ")";
			i++;
		}
		int length = copyToList.size();
		// copyToList.add(new InspectorDTO(length + 1, "PPS to Director, SFIO",
		// false,false));
		// copyToList.add(new InspectorDTO(length + 2, "Guard File", false,false));
		return new InspectorEditDTO(inspList);
	}
	@GetMapping(value = "/welcome")
    @ResponseBody
    public String welcomeAsHTML() {
        return "<p>helloo</p>";
    }
	
	@RequestMapping(value = "/getValidCompOrder", method = RequestMethod.POST, produces = "application/json")
	public @ResponseBody Boolean getValidCompOrder(@RequestParam("compName") String compName, @RequestParam("compOrder") MultipartFile orderFile) throws IOException {
		Boolean valid = true;
		 SNMSValidator snmsVal = new SNMSValidator();
		 valid = snmsVal.getvalidCompany(orderFile.getOriginalFilename(), true);
		 valid   = snmsVal.isValidFileCheck(orderFile,true);
		 valid = snmsVal.isValidFileTikka(orderFile.getOriginalFilename(), orderFile);
		 return valid ;
	}
	
}

