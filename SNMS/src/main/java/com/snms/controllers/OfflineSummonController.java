package com.snms.controllers;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.apache.tika.Tika;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.Office;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.RecursiveParserWrapper;
import org.apache.tika.sax.BasicContentHandlerFactory;
import org.apache.tika.sax.ContentHandlerFactory;
import org.owasp.esapi.ESAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.xml.sax.helpers.DefaultHandler;

import com.snms.dao.AppRoleDAO;
import com.snms.dao.AppUserDAO;
import com.snms.dao.SummonDao;
import com.snms.dto.CaseDetailsDto;
import com.snms.dto.InspectorDTO;
import com.snms.dto.InspectorListDTO;
import com.snms.dto.PdfPreviewDto;
import com.snms.dto.PendingForApprovalDTO;
import com.snms.dto.SummonDto;
import com.snms.dto.SummonTempDto;
import com.snms.entity.AppUser;
import com.snms.entity.CaseDetails;
import com.snms.entity.Company;
import com.snms.entity.IndividualType;
import com.snms.entity.InitiateSummonDraft;
import com.snms.entity.Inspector;
import com.snms.entity.SummonStatus;
import com.snms.entity.SummonTemplate;
import com.snms.entity.SummonType;
import com.snms.entity.UserDetails;
import com.snms.service.AuditBeanBo;
import com.snms.service.CaseDetailsRepository;
import com.snms.service.IdividualTypeRepository;
import com.snms.service.InitiateNoticeDraftRepository;
import com.snms.service.InitiateSummonDraftRepository;
import com.snms.service.InspectorRepository;
import com.snms.service.NoticeRepository;
import com.snms.service.NoticeTemplateRepository;
import com.snms.service.SummonRepository;
import com.snms.service.SummonTemplateRepository;
import com.snms.service.SummonTypeNew;
import com.snms.service.UserDetailsRepository;
import com.snms.service.UserDetailsServiceImpl;
import com.snms.service.UserManagementCustom;
import com.snms.utils.NoticePdf;
import com.snms.utils.SnmsConstant;
import com.snms.utils.SummonConstant;
import com.snms.utils.SummonPDF;
import com.snms.utils.Utils;
import com.snms.validation.SNMSValidator;

@Controller
public class OfflineSummonController {
	private static final Logger logger = Logger.getLogger(OfflineSummonController.class);

	@Autowired
	private UserDetailsServiceImpl userDetailsService;

	@Autowired
	private AppRoleDAO appRoleDao;
	@Autowired
	private Utils utils;
	@Autowired
	private NoticeRepository noticeRepo;
	@Autowired
	private UserManagementCustom userMangCustom;
	@Autowired
	private CaseDetailsRepository caseDetailsRepository;
	@Autowired
	private SummonDao summonDao;
	@Autowired
	private SummonRepository summonRepo;
	@Autowired
	private SummonTemplateRepository summonTempRepo;
	@Autowired
	private UserDetailsRepository userDetailsRepo;
	@Autowired
	private AuditBeanBo auditBeanBo;

	@Autowired
	private InspectorRepository inspRepo;
	/*
	 * @Autowired private SummonTypeDetails summonTypeDetails;
	 */

	@Autowired
	private SummonTypeNew SummonTypeNewDetails;
	@Autowired
	private AppUserDAO appUserDAO;
	@Autowired
	private InitiateSummonDraftRepository initiatesummonrepo;

	@Value("${file.upload}")
	public String filePath;

	@Autowired
	private IdividualTypeRepository idividualRepo;

	public static final long MB_IN_BYTES = 2097152; // 1 MB file size
	public static final String PDF_MIME_TYPE = "application/pdf";

	@RequestMapping(value = "getCompleteCaseByIdForOffline", method = RequestMethod.POST)
	public String getCompleteCaseByIdForOffline(@RequestParam("id") Long id, ModelMap modelMap) throws Exception {

		SNMSValidator snmsValid = new SNMSValidator();

		if (!snmsValid.getValidInteger(id)) {
			modelMap.addAttribute("message", "Invalid input ");
			return "caseDetails/ErrorPage";
		}
		commonObjectSummon(modelMap, id);

		boolean offline = true;
		modelMap.addAttribute("id", id);
		modelMap.addAttribute("offline", offline);

		return "notice/completeCaseById";
	}

	@GetMapping("getCompleteCaseoffline")
	public String getCompleteCaseoffline(ModelMap modelMap) throws Exception {
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
		return "notice/OfflineSummonDetails";
	}

	@RequestMapping(value = "/genSummonOflineAs")
	public String genSummonOflineAs(@RequestParam(name = "caseId", required = false) Long id,
			@RequestParam(name = "chooseComapny", required = false) String company,
			@RequestParam(name = "type", required = false) String type,
			@RequestParam(name = "summontypeid", required = false, defaultValue = "0") int sumtypeId,
			@RequestParam(name = "individualType", required = false) String individualtype,
			@RequestParam(name = "dateOfAppear", required = false) String dateOfAppear,
			@RequestParam(name = "dateofIssue", required = false) String dateofIssue,
			@RequestParam(name = "summonNo", required = false) String summonNo,
			@RequestParam(name = "summonType", required = false) String SummonType,
			@RequestParam(name = "summonFileName", required = false) MultipartFile file, ModelMap model)
			throws Exception {
		SNMSValidator snmsValid = new SNMSValidator();
		if (SummonType.equals("true")) {
			type = "summon";
		}

		if (id == null) {
			return "redirect:/getCompleteCase";
		}
		if (snmsValid.getValidInteger(id) || snmsValid.isvalidCompanyName(company)
				|| snmsValid.getValidZeroInteger(sumtypeId)) {

			if (validateGenerateNoticeSummon(id, company, type, sumtypeId, dateOfAppear, summonNo, individualtype,
					dateofIssue, model)) {
				boolean offline = true;

				model.addAttribute("offline", offline);
				return "notice/completeCaseById";
			}
		} else {
			return "redirect:/getCompleteCase";
		}
		Optional<CaseDetails> caseD = caseDetailsRepository.findById(id);
		CaseDetails caseDetails = caseD.get();
		String OfficeAddress = caseDetails.getUnit().getAddress();
		String OfficeAddress_hi = caseDetails.getUnit().getAddress_hi();
		String summonDIN = "";
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
			if (otherSize == 0)
				para1 = company;
			else
				para1 = company + " and " + otherSize + " other company";

			long Type = summonType.getIndividualType().getIndividualId();

			String name = summonType.getName();
			String designation = "";
			if (summonType.getDesignation() != null) {
				designation = summonType.getDesignation();
			} else {
				designation = "";
			}

			String address = summonType.getAddress();
			String sumCompany = "";
			if (summonType.getIndividualType().getIndividualId() == 5
					|| summonType.getIndividualType().getIndividualId() == 6) {
				sumCompany = summonType.getNameCompany();
			}
			if (summonType.getIndividualType().getIndividualId() == 7) {
				sumCompany = "";
			}

			else {
				sumCompany = company;
			}
			// Gouthami 07/01/2021
			AppUser userDetails = userDetailsService.getUserDetails();
			Inspector inspectorList = inspRepo.findAllByCaseDetailsAndAppUser(caseDetails, userDetails);
			String Designation;
			String DesignationHi = null;
			if (inspectorList.getIsAdo() == true) {
				// Designation = "Addl";
				Designation = "Addl.Director";

				DesignationHi = "अपर निदेशक";
			} else if (inspectorList.getIsIO() == true) {
				Designation = "Investigating Officer";
				DesignationHi = "जांच अधिकारी";
			} else {
				Designation = "Inspector";
				DesignationHi = "निरीक्षक";
			}

			UserDetails user = userDetailsRepo.findById(userDetails.getUserId()).get();
			String ioName = userDetailsService.getFullName(user);

			Optional<CaseDetails> caseDtls = caseDetailsRepository.findById(id);

			summonDIN = caseDtls.get().getFinanceYear().substring(caseDtls.get().getFinanceYear().lastIndexOf('-') + 1)
					+ caseDtls.get().getFinanceYear().substring(0, caseDtls.get().getFinanceYear().lastIndexOf('-'))
							.concat(SnmsConstant.SUMMON_DIN_ID);
			String sequence = summonRepo.getSummonDinSequence().toString();
			summonDIN = summonDIN.concat("00000".substring(sequence.length()).concat(sequence))
					.concat(Utils.getRandomAlphaNum(SnmsConstant.DIN_RANDOM_DIGIT));

			office.setPara1(SummonConstant.PARA_Of_1);

			office.setPara1_h(SummonConstant.PARA_Of_1h);
			String para2 = SummonConstant.PARA_Of_2 + dateofIssue + " " + SummonConstant.PARA_Of_2_1 + name;

			String para2h = SummonConstant.PARA_Of_2h + name;

			if (designation != null && designation != "")
				para2 = para2 + "," + designation;
			para2h = para2h + "  " + designation;
			if (sumCompany != null && sumCompany != "")
				para2 = para2 + "," + sumCompany;
			para2h = para2h + "," + sumCompany;

			para2 = para2 + "," + address + SummonConstant.PARA_Of_2_2;
			para2h = para2h + " ," + address + SummonConstant.PARA_Of_1_2h + dateofIssue + SummonConstant.PARA_Of_2_2h;
			// office.setPara2(SummonConstant.PARA_Of_2 + dateofIssue + " " +
			// SummonConstant.PARA_Of_2_1 + name +" , "+designation +" , "+sumCompany + " ,
			// "+address+" , " +SummonConstant.PARA_Of_2_2);
			office.setPara2(para2);
			office.setPara2_h(para2h);

			office.setPara3(SummonConstant.PARA_Of_3 + " " + summonDIN);
			office.setPara3(SummonConstant.PARA_Of_3h + " " + summonDIN + SummonConstant.PARA_Of_3_1h);
			office.setPara3_1(SummonConstant.PARA_Of_3);
			office.setPara3_1h(SummonConstant.PARA_Of_3h);

			SummonTempDto caseDto = new SummonTempDto(caseNo, date, office.getPara1(), office.getPara2(),
					office.getPara3(), office.getPara3_1(), office.getPara1_h(), office.getPara2_h(),
					office.getPara3_h(), office.getPara3_1h(), 1L, caseDetails.getCaseId(), caseDetails.getId(), ioName,
					summonNo, dateOfAppear, sumtypeId, dateofIssue, summonDIN, "Offline", Designation);

			model.addAttribute("caseDto", caseDto);

			model.addAttribute("companyName", sumCompany);
			model.addAttribute("name", name);
			model.addAttribute("Type", Type);

			model.addAttribute("designation", designation);
			model.addAttribute("address", getSplitedString(address));
			model.addAttribute("OfficeAddress", getSplitedString(OfficeAddress));
			model.addAttribute("OfficeAddress_hi", getSplitedString(OfficeAddress_hi));
			model.addAttribute("Iodesignation", Designation);
			model.addAttribute("Iodesignation_hi", DesignationHi);
			model.addAttribute("individualtype", individualtype);
			// model.addAttribute("companydisplay",para1);
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

		PdfPreviewDto pdfPreviewDto = new PdfPreviewDto(id, company, type, sumtypeId, dateOfAppear, "", dateofIssue,
				summonDIN);
		model.addAttribute("privilege", privilege);
		model.addAttribute("pdfPreview", pdfPreviewDto);
		return "notice/genOfflineSummonAs";

	}

	private String getSplitedString(String address) {
		String[] splitStr = address.split(" ");
		StringBuilder consStr = new StringBuilder();
		for (int i = 0; i < splitStr.length; i++) {
			consStr.append(splitStr[i]).append(" ");
			if (i % 5 == 0 && i != 0)
				consStr.append("\r\n");
		}
		return consStr.toString();
	}

	private AppUser getUserDetails() throws Exception {
		AppUser appUser = this.appUserDAO.findUserAccount(getLoginUserName());
		return appUser;
	}

	public String getLoginUserName() {
		return SecurityContextHolder.getContext().getAuthentication().getName();
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
			listCom.add(c);
			k++;
		}

		return new InspectorListDTO(inspList, copyToList, ioName, listCom);
	}

	public boolean validateGenerateNoticeSummon(Long id, String company, String type, int sumtypeId,
			String dateOfAppear, String summonNo, String individualtype, String dateofIssue, ModelMap model) {
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
		if (dateofIssue == null || "".equals(dateofIssue.trim())) {
			commonObjectSummon(model, id);
			model.addAttribute("id", id);
			model.addAttribute("dateofIssueErr", "Required Field");
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

	public void commonObjectSummon(ModelMap modelMap, Long caseId) {
		Optional<CaseDetails> details = caseDetailsRepository.findById(caseId);
		CaseDetails caseDetails = details.get();
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

	@RequestMapping(value = "/generateOfflineSummon", params = "forward")
	public String generateOfflineSummon(@ModelAttribute SummonTempDto caseDetailsDto, BindingResult result, Model model,
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
		if (validateForwardSummon(caseDetailsDto, result, redirect)) {
			model.addAttribute("caseDetail", caseDetailsDto);
			return "redirect:/getCompleteCaseoffline";
		}
		if (!privilege)
			return "notice/genSummonOflineAs";
		// BY Gouthami
		Long id = (summonRepo.findMaxid() != null) ? (summonRepo.findMaxid() + 1) : 1;
		String fileName = null;
		if (!caseDetailsDto.getSummonOfflineFile().isEmpty()) {
			String fileExt = caseDetailsDto.getSummonOfflineFile().getOriginalFilename();
			fileExt = fileExt.substring(fileExt.lastIndexOf("."));
			fileName = "OfflineSummon_" + id + fileExt;
			// caseDetails.setMcaOrderFile("OfflineSummon_"+ fileExt);
			caseFileUpload(caseDetailsDto.getSummonOfflineFile(), fileName);
		}
		SummonStatus summonStatus = new SummonStatus(new CaseDetails(caseDetailsDto.getCaseId()), true, true,
				userDetailsService.getUserDetails(), new Date(), caseDetailsDto.getSummonNo(),
				caseDetailsDto.getDateOfAppear(), new SummonType(caseDetailsDto.getSummontypeid()),
				caseDetailsDto.getDateOfIssue(), caseDetailsDto.getSummonDin(), true, fileName);
//		summonStatus.setSummonDin(caseDetailsDto.getSummonDin());
		summonStatus = summonRepo.save(summonStatus);

		Long srNo = (summonTempRepo.findMaxSrNo(summonStatus) == null) ? 1
				: summonTempRepo.findMaxSrNo(summonStatus) + 1;

		Optional<CaseDetails> caseD = caseDetailsRepository.findById(caseDetailsDto.getCaseId());
		// CaseDetails caseDetails = caseD.get();
		// SummonType
		// summonType=summonTypeDetails.findById(caseDetailsDto.getSummontypeid()).get();
		// BY Gouthami

		// SummonType summonType =
		// SummonTypeNewDetails.findById(caseDetailsDto.getSummontypeid()).get();

		SummonTemplate summonTemplate = new SummonTemplate(summonStatus, srNo, caseDetailsDto.getPara1(),
				caseDetailsDto.getPara2(), caseDetailsDto.getPara3_1(), caseDetailsDto.getPara1h(),
				caseDetailsDto.getPara2h(), caseDetailsDto.getPara3_1h(), userDetailsService.getUserDetails(),
				new Date());
		summonTemplate.setIsFinal(true);
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
				utils.getMessage("log.SummonOffTemplate.save"), utils.getMessage("log.SummonOffTemplate.saved"),
				loginUName, "true");
		auditBeanBo.save();

		Optional<CaseDetails> caseDtls = caseDetailsRepository.findById(caseDetailsDto.getCaseId());

		summonRepo.approveInfoByIdStage2(new CaseDetails(caseDetailsDto.getCaseId()), caseDetailsDto.getSummonId(),
				caseDetailsDto.getSummonDin());
		SummonStatus order = new SummonStatus(summonStatus.getId());

		SummonStatus summonStatus1 = summonRepo.findById(order.getId()).get();

		initiatesummonrepo.updateSummonDraftFinal(caseDtls.get(), summonStatus1.getSummonType());

		initiatesummonrepo.updateSummonDraftFinal(caseDtls.get(), summonStatus1.getSummonType());

		model.addAttribute("message", "Summon approved successfully for Summon No : " + summonStatus1.getSummonNo()
				+ " and DIN " + summonStatus1.getSummonDin());
		auditBeanBo.setAuditBean(Integer.parseInt(userDetailsService.getUserDetails().getUserId().toString()),
				loginUName, "User", Integer.parseInt(userDetailsService.getUserDetails().getUserId().toString()),
				utils.getMessage("log.OfflinSummon.approve"), utils.getMessage("log.OfflinSummon.approved"), loginUName,
				"true");
		auditBeanBo.save();

		/*
		 * model.addAttribute("message", "DIN generated successfully for summon no.: " +
		 * summonStatus1.getSummonNo() + " and DIN " + summonStatus1.getSummonDin());
		 */

		// Gouthami06092021
		model.addAttribute("message", "Summon Uploaded successfully for summon no.: " + summonStatus1.getSummonNo());
		model.addAttribute("summonId", summonStatus.getId());
		return "caseDetails/SummonOflineAproval";
	}

	public boolean validateForwardSummon(SummonTempDto caseDetailsDto, BindingResult result, RedirectAttributes model)
			throws IOException {
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

		else if (caseDetailsDto.getSummonNo() == null || "".equals(caseDetailsDto.getSummonNo().trim())) {
			model.addFlashAttribute("summonNo", "Summon No is required");
			return true;
		}

		else if (caseDetailsDto.getDateOfIssue() == null || "".equals(caseDetailsDto.getDateOfIssue().trim())) {
			model.addFlashAttribute("dateOfIssue", "Date of issue is required");
			return true;
		}

		else if (caseDetailsDto.getSummonDin() == null || "".equals(caseDetailsDto.getSummonDin().trim())) {
			model.addFlashAttribute("summonDin", "summonDin is required");
			return true;
		} else if (caseDetailsDto.getSummonOfflineFile().isEmpty()) {
			model.addFlashAttribute("summonofflineFile", "File is required");

			return true;
		} else if (caseDetailsDto.getSummonOfflineFile().getSize() > 0
				|| !caseDetailsDto.getSummonOfflineFile().isEmpty()) {

			MultipartFile summonOrderFile = caseDetailsDto.getSummonOfflineFile();

			if ((summonOrderFile.isEmpty() || summonOrderFile == null)) {
				model.addFlashAttribute("summonofflineFile", "File is required");
				return true;
			} else if (!isValidFile(summonOrderFile)) {
				model.addFlashAttribute("summonofflineFile", "Invalid File");
				return true;

			}

			else if (!isValidFileName(summonOrderFile.getOriginalFilename())) {
				model.addFlashAttribute("summonofflineFile", "Invalid File Name");

				return true;
			} else if (!isValidFileTikka(summonOrderFile.getOriginalFilename(), summonOrderFile)) {
				model.addFlashAttribute("summonofflineFile", "Malicious data,unable to upload file only pdf allow");
				return true;
			} else {
				model.addFlashAttribute("summonDin", "summonDin is required");
			}
		}
		return false;
	}

	private boolean isValidFile(MultipartFile file) {
		if (PDF_MIME_TYPE.equalsIgnoreCase(file.getContentType())) {
			if (file.getSize() > MB_IN_BYTES) {
				return false;
			}
		} else {
			return false;
		}
		return true;
	}

	public boolean isValidFileTikka(String filename, MultipartFile part) throws IOException {
		String allowedMime = "application/pdf,application/PDF";
		String mimetype[] = allowedMime.split(",");
		List<String> mimelist = Arrays.asList(mimetype);
		Tika tika = new Tika();
		InputStream ins = null;

		boolean correct = false;
		try {

			ins = part.getInputStream();
			String mediaType = tika.detect(ins);
			// logger.info(filename + " " + mediaType);
			if (mimelist.contains(mediaType))
				correct = true;

			if (!correct)
				return false;

			if ("application/pdf".equalsIgnoreCase(mediaType)) {
				Parser p = new AutoDetectParser();
				ContentHandlerFactory factory = new BasicContentHandlerFactory(
						BasicContentHandlerFactory.HANDLER_TYPE.HTML, -1);

				RecursiveParserWrapper wrapper = new RecursiveParserWrapper(p, factory);
				Metadata metadata = new Metadata();
				ParseContext context = new ParseContext();

				try (InputStream stream = (part.getInputStream())) {
					wrapper.parse(stream, new DefaultHandler(), metadata, context);
				}
				List<Metadata> l = wrapper.getMetadata();
				for (int k = 0; k < l.size(); k++) {
					// logger.info(l.get(k).get(Metadata.CONTENT_TYPE));
					if (!mimelist.contains(l.get(k).get(Metadata.CONTENT_TYPE)))
						return false;
				}
			}
			return true;
		} catch (Exception e1) {
			// logger.error(e1.getMessage(), e1);
			return false;
		} finally {

			if (ins != null) {
				safeClose(ins);
			}
		}

	}

	private void safeClose(InputStream filepart) {
		if (filepart != null) {
			try {
				filepart.close();
			} catch (IOException e) {
				logger.info(e.getMessage());
			}
		}

	}

	public boolean isValidFileName(String fileName) {
		if (fileName == null)
			return false;
		if (fileName.lastIndexOf(".") <= 0)
			return true;
		String allowExt = "pdf";
		String fileReg = "([^*$#@!%&]+(\\.(?i)(" + allowExt + "))$)";
		Pattern pattern = Pattern.compile(fileReg);
		Matcher matcher = pattern.matcher(fileName.trim().toLowerCase());
		if (!matcher.matches()) {
			return false;
		}
		// To check Filename contains more than one (.)dot");
		if (fileName.split("\\.").length > 2) {
			return false;
		}
		return true;
	}

	@RequestMapping(value = "/genOfflineSummonPdf", produces = MediaType.APPLICATION_PDF_VALUE)
	public ResponseEntity<InputStreamResource> generatesummonpdf(
			@RequestParam(name = "caseId", required = true) Long id,
			@RequestParam(name = "chooseComapny", required = true) String company,
			@RequestParam(name = "summontypeid") int sumtypeId,
			@RequestParam(name = "dateOfAppear") String dateOfAppear, @RequestParam(name = "summonNo") String summonNo,
			@RequestParam(name = "summonDin") String summonDin, @RequestParam(name = "IssueDate") String IssueDate,

			Model model) throws Exception {
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
			designation = " ";
		}

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

		InspectorListDTO inspList = getInspectorListAndCompany(caseDetails.getId());
		// String ioName = inspList.getIoName();

		// InitiateSummonDraft summonDraft =
		// summonDao.findSummonDraftByCaseDetails(caseDetails, summonType);
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
		} else {
			noticeCompany = company;
		}
		int remainIoSize = inspList.getInspctorList().size() - 1;

		ByteArrayInputStream bis = SummonPDF.summonOfflineFixed(new SummonDto(
				Utils.formatMcaOrderDate(caseDetails.getMcaOrderDate()), summonNo, ioName, noticeCompany, summonDin,
				IssueDate, getSplitedString(address), designation, name, dateOfAppear, otherSize, remainIoSize,
				caseDetails.getMcaOrderNo(), getSplitedString(OfficeAddress), comapnyPara, Designation));

		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Disposition", "inline; filename=summons.pdf");

		return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF)
				.body(new InputStreamResource(bis));

	}

	@RequestMapping(value = "/caseoffFileUpload", method = RequestMethod.POST)
	public void caseFileUpload(@RequestParam("file") MultipartFile file, String name) {
		BufferedOutputStream stream = null;
		try {

			// name = "abc";
			File parent = new File(filePath).getParentFile().getCanonicalFile();
			String directory = ESAPI.validator().getValidDirectoryPath("DirectoryName", filePath, parent, false);

			Boolean validFileName = ESAPI.validator().isValidFileName("FileName", name, false);
			String filepath = null;
			if (validFileName == true) {
				filepath = Paths.get(directory + File.separator + name).toString();
			}
			// Save the file locally
			stream = new BufferedOutputStream(new FileOutputStream(new File(filepath)));
			stream.write(file.getBytes());
			stream.close();
		} catch (Exception e) {
			logger.info(e.getMessage());
		} finally {
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
}
