package com.snms.controllers;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.owasp.esapi.ESAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.itextpdf.text.Image;
import com.snms.dao.SummonDao;
import com.snms.dto.InspectorDTO;
import com.snms.dto.InspectorListDTO;
import com.snms.dto.NoticeDto;
import com.snms.dto.NoticeTempDto;
import com.snms.dto.PdfPreviewDto;
import com.snms.dto.SummonDto;
import com.snms.dto.SummonTempDto;
import com.snms.entity.AppUser;
import com.snms.entity.CaseDetails;
import com.snms.entity.Company;
import com.snms.entity.IndividualType;
import com.snms.entity.InitiateNoticeDraft;
import com.snms.entity.InitiateSummonDraft;
import com.snms.entity.Inspector;
import com.snms.entity.NoticeStatus;
import com.snms.entity.OfficeOrder;
import com.snms.entity.SummonStatus;
import com.snms.entity.SummonTemplate;
import com.snms.entity.SummonType;
import com.snms.entity.UserDetails;
import com.snms.service.AppUserRepository;
import com.snms.service.CaseDetailsRepository;
import com.snms.service.IdividualTypeRepository;
import com.snms.service.InspectorRepository;
import com.snms.service.NoticeRepository;
import com.snms.service.OfficeOrderRepository;
import com.snms.service.SummonRepository;
import com.snms.service.SummonTemplateRepository;
import com.snms.service.SummonTypeNew;
import com.snms.service.UserDetailsRepository;
import com.snms.service.UserDetailsServiceImpl;
import com.snms.service.UserManagementCustom;
import com.snms.utils.NoticeConstant;
import com.snms.utils.NoticePdf;
import com.snms.utils.SnmsConstant;
import com.snms.utils.SummonConstant;
import com.snms.utils.Utils;
import com.snms.validation.SNMSValidator;

@Controller
public class NoticeSummopdfViewController {

	private static final Logger logger = Logger.getLogger(NoticeSummopdfViewController.class);

	@Value("${file.upload}")
	public String filePath1;

	@Value("${file.snmsapi}")
	public String snmsapi;

	@Value("${pdf.exe}")
	public String pdfExe;

	@Value("${logo.path}")
	public String image;

	@Autowired
	private SummonRepository summonRepository;

	@Autowired
	private SummonTemplateRepository SummonTempRepo;
	@Autowired
	private SummonRepository summonRepo;
	@Autowired
	private UserManagementCustom userMangCustom;
	@Autowired
	private UserDetailsServiceImpl userDetailsService;
	@Autowired
	private NoticeRepository noticeRepo;

	@Autowired
	private CaseDetailsRepository caseDetailsRepository;

	@Autowired
	private SummonDao summonDao;
	@Autowired
	private AppUserRepository appUserRepo;
	@Autowired
	private UserDetailsRepository userDetailsRepo;
	@Autowired
	private IdividualTypeRepository idividualRepo;

	@Autowired
	private InspectorRepository inspRepo;

	@Autowired
	SummonTypeNew SummonTypeNewDetails;

	@Autowired
	private OfficeOrderRepository officeOrderRepo;

	@RequestMapping(value = "/genPdfPreview")
	public ResponseEntity<Resource> genFullSummonPdf(
			@RequestParam(name = "officeOrderId", required = false) Long officeOrderId,

			@RequestParam(name = "type", required = false) String Type) throws Exception {

		String din = "";
		try {

			Long userId = userDetailsService.getUserDetails().getUserId();

			File file = File.createTempFile("Summon", ".pdf");

			String s = "";
			if (Type.equalsIgnoreCase("summon"))

				s = " " + snmsapi.trim() + "/PdfSummonAs1?officeOrderId=" + officeOrderId + "&userId=" + userId
						+ "&summonDin" + din;
			else
				s = " " + snmsapi.trim() + "/PdfNoticeAs1?officeOrderId=" + officeOrderId + "&userId=" + userId
						+ "&noticeDin=" + din;
			String Command = s.trim() + " ";
			createpdf(file, Command);
			// String filePath = "E:\\SNMS\\file_upload\\Preview";

			String filePath = filePath1 + File.separator + "Preview";

			File parent = new File(filePath).getParentFile().getCanonicalFile();
			ESAPI.validator().getValidDirectoryPath("DirectoryName", filePath, parent, false);
			Path path = Paths.get(filePath + File.separator + file.getName());

			Resource resource = null;
			try {
				resource = new UrlResource(path.toUri());

			} catch (MalformedURLException e) {
				logger.info(e.getMessage());
			}

			HttpHeaders headers = new HttpHeaders();
			headers.add("Content-Disposition", "inline; filename=" + file.getName());

			return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(resource);

		}

		catch (Exception e) {
			System.out.println(e.getMessage());
			throw new RuntimeException(e);
		}
	}

	@RequestMapping(value = "/genOfflineSummonPdf1")
	public ResponseEntity<Resource> genSummonOflineAs(@RequestParam(name = "caseId", required = false) Long id,
			@RequestParam(name = "chooseComapny", required = false) String company,
			@RequestParam(name = "type", required = false) String type,
			@RequestParam(name = "summontypeid", required = false, defaultValue = "0") int sumtypeId,
			@RequestParam(name = "individualtype1", required = false) String individualtype,
			@RequestParam(name = "dateOfAppear", required = false) String dateOfAppear,
			@RequestParam(name = "IssueDate", required = false) String dateofIssue,
			@RequestParam(name = "summonNo1", required = false) String summonNo,
			@RequestParam(name = "summonType", required = false) String SummonType, ModelMap model) throws Exception {

		SummonType = "true";

		type = "summon";
		// String individualtype = "1";

		String dateOfAppear1 = dateOfAppear.replace(" ", "ab");
		company = company.replace(" ", "%");

		Optional<CaseDetails> caseD = caseDetailsRepository.findById(id);
		CaseDetails caseDetails = caseD.get();
		SummonType summonType = SummonTypeNewDetails.findById(sumtypeId).get();

		InitiateSummonDraft summonDraft = summonDao.findSummonDraftByCaseDetails(caseDetails, summonType);

		try {

			Long userId = userDetailsService.getUserDetails().getUserId();

			File file = File.createTempFile("OfflineSummon", ".pdf");

			String s = "";

			s = " " + snmsapi.trim() + "/genOfflineSummonAs1?id=" + id + "&company=" + company + "&type=" + type
					+ "&sumtypeId=" + sumtypeId + "&individualtype=" + individualtype + "&dateOfAppear=" + dateOfAppear1
					+ "&dateofIssue=" + dateofIssue + "&summonNo=" + summonNo + "&userId=" + userId;

			String Command = s.trim() + " ";
			createpdf(file, Command);
			// String filePath = "E:\\SNMS\\file_upload\\Preview";

			String filePath = filePath1 + File.separator + "Preview";

			File parent = new File(filePath).getParentFile().getCanonicalFile();
			ESAPI.validator().getValidDirectoryPath("DirectoryName", filePath, parent, false);
			Path path = Paths.get(filePath + File.separator + file.getName());

			Resource resource = null;
			try {
				resource = new UrlResource(path.toUri());

			} catch (MalformedURLException e) {
				logger.info(e.getMessage());
			}

			HttpHeaders headers = new HttpHeaders();
			headers.add("Content-Disposition", "inline; filename=" + file.getName());

			return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(resource);

		}

		catch (Exception e) {
			System.out.println(e.getMessage());
			throw new RuntimeException(e);
		}

	}

	@RequestMapping(value = "/genFullSummonPdf")
	public ResponseEntity<Resource> genFullSummonPdf(@RequestParam(name = "caseId", required = false) Long id,
			@RequestParam(name = "chooseComapny", required = false) String company,
			@RequestParam(name = "type", required = false) String type,
			@RequestParam(name = "summontypeid", required = false) int sumtypeId,
			@RequestParam(name = "individualtype1") String individualtype,
			@RequestParam(name = "dateOfAppear", required = false) String dateOfAppear,
			@RequestParam(name = "summonNo1", required = false) String summonNo,
			@RequestParam(name = "officeOrderId", required = false) Long officeOrderId, ModelMap model)
			throws Exception {

		type = "summon";
		// String individualtype = "1";

		String dateOfAppear1 = dateOfAppear.replace(" ", "ab");
		company = company.replace(" ", "%");

		Optional<CaseDetails> caseD = caseDetailsRepository.findById(id);
		CaseDetails caseDetails = caseD.get();
		SummonType summonType = SummonTypeNewDetails.findById(sumtypeId).get();

		InitiateSummonDraft summonDraft = summonDao.findSummonDraftByCaseDetails(caseDetails, summonType);

		try {

			Long userId = userDetailsService.getUserDetails().getUserId();

			File file = File.createTempFile("Summon", ".pdf");

			String s = "";

			if (officeOrderId != null) {
				s = " " + snmsapi.trim() + "/PdfSummonAs1?officeOrderId=" + officeOrderId + "&userId=" + userId;
			} else {

				s = " " + snmsapi.trim() + "/genSummonAs1?id=" + id + "&company=" + company + "&type=" + type
						+ "&sumtypeId=" + sumtypeId + "&individualtype=" + individualtype + "&dateOfAppear="
						+ dateOfAppear1 + "&summonNo=" + summonNo + "&userId=" + userId;
			}

			System.out.println("FilePath=====================" + s);
			String Command = s.trim() + " ";
			createpdf(file, Command);
			// String filePath = "E:\\SNMS\\file_upload\\Preview";

			String filePath = filePath1 + File.separator + "Preview";

			File parent = new File(filePath).getParentFile().getCanonicalFile();
			ESAPI.validator().getValidDirectoryPath("DirectoryName", filePath, parent, false);
			Path path = Paths.get(filePath + File.separator + file.getName());

			Resource resource = null;
			try {
				resource = new UrlResource(path.toUri());

			} catch (MalformedURLException e) {
				logger.info(e.getMessage());
			}

			HttpHeaders headers = new HttpHeaders();
			headers.add("Content-Disposition", "inline; filename=" + file.getName());

			return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(resource);

		}

		catch (Exception e) {
			System.out.println(e.getMessage());
			throw new RuntimeException(e);
		}
	}

	@RequestMapping(value = "/PdfOfflineSummonAs1")
	public String PdfOfflineSummonAs1(Long officeOrderId, Long userId, String summonDin, Model model) throws Exception {

		Optional<SummonStatus> summonStatus = summonRepo.findById(officeOrderId);
		SummonStatus summon = summonStatus.get();
		CaseDetails caseDetails = summon.getCaseDetails();
		String OfficeAddress = caseDetails.getUnit().getAddress();
		String OfficeAddress_hi = caseDetails.getUnit().getAddress_hi();

		/*
		 * // Gouthami UserDetails userDet =
		 * userDetailsRepo.findAllByUserId(getUserDetails()); String OfficeAddress =
		 * userDet.getUnit().getAddress();
		 */
//		SummonTemplate summonDraft = summonTempRepo.findBySummon(summon);
		InitiateSummonDraft summonDraft;

		summonDraft = summonDao.findSummonDraftByCaseDetails(caseDetails, summon.getSummonType());

		if (summonDraft == null) {
			summonDraft = summonDao.findSummonDraftByCaseDetails1(caseDetails, summon.getSummonType());

		}
		InspectorListDTO inspList = getInspectorListAndCompany(caseDetails.getId());
		// Gouthami 07/01/2021
		Inspector inspectorList = inspRepo.findAllByCaseDetailsAndAppUser(caseDetails, summonDraft.getAppUser());
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
		SummonTemplate summonTemp = SummonTempRepo.findBySummon(summonStatus.get());
		UserDetails user = userDetailsRepo.findById(summonDraft.getAppUser().getUserId()).get();
		String ioName = userDetailsService.getFullName(user);
		SummonTempDto caseDto = new SummonTempDto(caseDetails.getCaseId(), Utils.getCurrentDateWithMonth(),
				summonTemp.getPara1(), summonTemp.getPara2(), summonTemp.getPara3(), summonTemp.getPara4(),
				summonTemp.getPara5(), summonTemp.getPara6(), summonTemp.getPara1H(), summonTemp.getPara2H(),
				summonTemp.getPara3H(), summonTemp.getPara4H(), summonTemp.getPara5H(), summonTemp.getPara6H(),
				summon.getId(), caseDetails.getCaseId(), caseDetails.getId(), ioName, summon.getSummonNo(), "",
				summon.getIsSensitive(), summon.getSummonType().getId(), "", Designation, DesignationHi);
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

		List<Object[]> ioadoList = userMangCustom.getIoAdoByCaseId(caseDetails.getId());
		Long ioId = 0L;
		for (Object[] object : ioadoList) {
			if ((Boolean) object[2])
				ioId = Long.parseLong(object[0].toString());
		}
		boolean privilege = false;
		try {
			if (userId == ioId)
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
		if (otherSize == 0)
			comapnyPara = company;
		else
			comapnyPara = company + " and " + otherSize + " other companies";

		model.addAttribute("companydisplay", comapnyPara);
		model.addAttribute("companyName", noticeCompany);
		model.addAttribute("name", name);
		model.addAttribute("designation", designation);
		model.addAttribute("Designation", Designation);
		model.addAttribute("DesignationHi", DesignationHi);
		model.addAttribute("address", getSplitedString(address));
		model.addAttribute("io", ioName);
		model.addAttribute("OfficeAddress", OfficeAddress);
		model.addAttribute("officeOrderId", officeOrderId);

		model.addAttribute("OfficeAddress_hi", OfficeAddress_hi);

		model.addAttribute("summonDin", summonDin);
		model.addAttribute("date", new Date());

		// model.addAttribute("summonId", officeOrderId);

		PdfPreviewDto pdfPreviewDto = new PdfPreviewDto(caseDetails.getId(), company,
				summonType.getIndividualType().getIndividualName(), summonType.getId(), summon.getDateOfAppear(),
				summon.getSummonNo());
		model.addAttribute("pdfPreview", pdfPreviewDto);

		return "notice/genOfflineSummonAs1";

	}

	@RequestMapping(value = "/PdfSummonAs1")
	public String showSummonDetailsApprove(Long officeOrderId, Long userId, String summonDin, Model model)
			throws Exception {
		SNMSValidator snmsValid = new SNMSValidator();
		if (!snmsValid.getValidInteger(officeOrderId)) {
			return "redirect:/showPendSummonStage1";

		}
		Optional<SummonStatus> summonStatus = summonRepo.findById(officeOrderId);
		SummonStatus summon = summonStatus.get();
		CaseDetails caseDetails = summon.getCaseDetails();
		String OfficeAddress = caseDetails.getUnit().getAddress();
		String OfficeAddress_hi = caseDetails.getUnit().getAddress_hi();

		/*
		 * // Gouthami UserDetails userDet =
		 * userDetailsRepo.findAllByUserId(getUserDetails()); String OfficeAddress =
		 * userDet.getUnit().getAddress();
		 */
//		SummonTemplate summonDraft = summonTempRepo.findBySummon(summon);
		InitiateSummonDraft summonDraft;

		summonDraft = summonDao.findSummonDraftByCaseDetails(caseDetails, summon.getSummonType());

		if (summonDraft == null) {
			summonDraft = summonDao.findSummonDraftByCaseDetails1(caseDetails, summon.getSummonType());

		}
		InspectorListDTO inspList = getInspectorListAndCompany(caseDetails.getId());
		// Gouthami 07/01/2021
		Inspector inspectorList = inspRepo.findAllByCaseDetailsAndAppUser(caseDetails, summonDraft.getAppUser());
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

		UserDetails user = userDetailsRepo.findById(summonDraft.getAppUser().getUserId()).get();
		String ioName = userDetailsService.getFullName(user);
		SummonTempDto caseDto = new SummonTempDto(caseDetails.getCaseId(), Utils.getCurrentDateWithMonth(),
				summonDraft.getPara1(), summonDraft.getPara2(), summonDraft.getPara3(), summonDraft.getPara4(),
				summonDraft.getPara5(), summonDraft.getPara6(), summonDraft.getPara1_h(), summonDraft.getPara2_h(),
				summonDraft.getPara3_h(), summonDraft.getPara4_h(), summonDraft.getPara5_h(), summonDraft.getPara6_h(),
				summon.getId(), caseDetails.getCaseId(), caseDetails.getId(), ioName, summon.getSummonNo(), "",
				summon.getIsSensitive(), summon.getSummonType().getId(), "", Designation, DesignationHi);
		
		
		
		
		 String para1_1h = summonDraft.getPara1_h();

			// split string by no space
			String[] strSplit = para1_1h.split("\n");

			// Now convert string into ArrayList
			ArrayList<String> strList = new ArrayList<String>(Arrays.asList(strSplit));
			ArrayList<String> para1h = new ArrayList<String>();

			// Now print the ArrayList
			for (String s : strList) {
				
				para1h.add(s);

			}
			model.addAttribute("para1h", para1h);
		 
			String para2_1h = summonDraft.getPara2_h();

			// split string by no space
		    strSplit = para2_1h.split("\n");

			// Now convert string into ArrayList
			 strList = new ArrayList<String>(Arrays.asList(strSplit));
			ArrayList<String> para2h = new ArrayList<String>();

			// Now print the ArrayList
			for (String s : strList) {
				
				para2h.add(s);

			}
			model.addAttribute("para2h", para2h);

			String para3_1h = summonDraft.getPara3_h();

			// split string by no space
			strSplit = para3_1h.split("\n");

			// Now convert string into ArrayList
			strList = new ArrayList<String>(Arrays.asList(strSplit));
			ArrayList<String> para3h = new ArrayList<String>();

			// Now print the ArrayList
			for (String s : strList) {
				
				para3h.add(s);

			}

			model.addAttribute("para3h", para3h);

			String para4_1h = summonDraft.getPara4_h();

			// split string by no space
			strSplit = para4_1h.split("\n");

			// Now convert string into ArrayList
			strList = new ArrayList<String>(Arrays.asList(strSplit));
			ArrayList<String> para4h = new ArrayList<String>();

			// Now print the ArrayList
			for (String s : strList) {
				
				para4h.add(s);

			}

			model.addAttribute("para4h", para4h);

			String para5_1h = summonDraft.getPara5_h();

			// split string by no space
			strSplit = para5_1h.split("\n");

			// Now convert string into ArrayList
			strList = new ArrayList<String>(Arrays.asList(strSplit));
			ArrayList<String> para5h = new ArrayList<String>();

			// Now print the ArrayList
			for (String s : strList) {
				
				para5h.add(s);

			}

			model.addAttribute("para5h", para5h);
			
			
			String para6_1h = summonDraft.getPara6_h();

			// split string by no space
			strSplit = para6_1h.split("\n");

			// Now convert string into ArrayList
			strList = new ArrayList<String>(Arrays.asList(strSplit));
			ArrayList<String> para6h = new ArrayList<String>();

			// Now print the ArrayList
			for (String s : strList) {
				
				para6h.add(s);

			}

			model.addAttribute("para6h", para6h);

			
			
			String para1_1 = summonDraft.getPara1();

			// split string by no space
			strSplit = para1_1.split("\n");

			// Now convert string into ArrayList
			strList = new ArrayList<String>(Arrays.asList(strSplit));
			ArrayList<String> para1_a = new ArrayList<String>();

			// Now print the ArrayList
			for (String s : strList) {
				
				para1_a.add(s);

			}
			model.addAttribute("para1", para1_a);
			
			String para2_1 = summonDraft.getPara2();

			// split string by no space
			strSplit = para2_1.split("\n");

			// Now convert string into ArrayList
			strList = new ArrayList<String>(Arrays.asList(strSplit));
			ArrayList<String> para2 = new ArrayList<String>();

			// Now print the ArrayList
			for (String s : strList) {
				
				para2.add(s);

			}
			model.addAttribute("para2", para2);

			String para3_1 = summonDraft.getPara3();

			// split string by no space
			strSplit = para3_1.split("\n");

			// Now convert string into ArrayList
			strList = new ArrayList<String>(Arrays.asList(strSplit));
			ArrayList<String> para3 = new ArrayList<String>();

			// Now print the ArrayList
			for (String s : strList) {
				
				para3.add(s);

			}

			model.addAttribute("para3", para3);

			String para4_1 = summonDraft.getPara4();

			// split string by no space
			strSplit = para4_1.split("\n");

			// Now convert string into ArrayList
			strList = new ArrayList<String>(Arrays.asList(strSplit));
			ArrayList<String> para4 = new ArrayList<String>();

			// Now print the ArrayList
			for (String s : strList) {
				
				para4.add(s);

			}

			model.addAttribute("para4", para4);

			String para5_1 = summonDraft.getPara5();

			// split string by no space
			strSplit = para5_1.split("\n");

			// Now convert string into ArrayList
			strList = new ArrayList<String>(Arrays.asList(strSplit));
			ArrayList<String> para5 = new ArrayList<String>();

			// Now print the ArrayList
			for (String s : strList) {
				
				para5.add(s);

			}

			model.addAttribute("para5", para5);
		 

			
			String para6_1 = summonDraft.getPara6();

			// split string by no space
			strSplit = para6_1.split("\n");

			// Now convert string into ArrayList
			strList = new ArrayList<String>(Arrays.asList(strSplit));
			ArrayList<String> para6 = new ArrayList<String>();

			// Now print the ArrayList
			for (String s : strList) {
				
				para6.add(s);

			}

			model.addAttribute("para6", para6);
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

		List<Object[]> ioadoList = userMangCustom.getIoAdoByCaseId(caseDetails.getId());
		Long ioId = 0L;
		for (Object[] object : ioadoList) {
			if ((Boolean) object[2])
				ioId = Long.parseLong(object[0].toString());
		}
		boolean privilege = false;
		try {
			if (userId == ioId)
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
		if (otherSize == 0)
			comapnyPara = company;
		else
			comapnyPara = company + " and " + otherSize + " other companies";

		model.addAttribute("companydisplay", comapnyPara);
		model.addAttribute("companyName", noticeCompany);
		model.addAttribute("name", name);
		model.addAttribute("designation", designation);
		model.addAttribute("Designation", Designation);
		model.addAttribute("DesignationHi", DesignationHi);
		model.addAttribute("address", getSplitedString(address));
		model.addAttribute("io", ioName);
		model.addAttribute("OfficeAddress", OfficeAddress);
		model.addAttribute("officeOrderId", officeOrderId);

		model.addAttribute("OfficeAddress_hi", OfficeAddress_hi);

		model.addAttribute("summonDin", summonDin);
		model.addAttribute("date", new Date());

		// model.addAttribute("summonId", officeOrderId);

		PdfPreviewDto pdfPreviewDto = new PdfPreviewDto(caseDetails.getId(), company,
				summonType.getIndividualType().getIndividualName(), summonType.getId(), summon.getDateOfAppear(),
				summon.getSummonNo());
		model.addAttribute("pdfPreview", pdfPreviewDto);

		return "notice/genSummonAs1";
		// return "director/approveSummon";
	}

	@RequestMapping(value = "/PdfNoticeAs1")
	public String showNoticeDetailsApprove(Long officeOrderId, Long userId, String noticeDin, Model model)
			throws Exception {
		SNMSValidator snmsValid = new SNMSValidator();
		if (!snmsValid.getValidInteger(officeOrderId)) {
			return "redirect:/showPendSummonStage1";

		}
		Optional<NoticeStatus> noticeStatus = noticeRepo.findById(officeOrderId);
		NoticeStatus notice = noticeStatus.get();
		CaseDetails caseDetails = notice.getCaseDetails();

		String OfficeAddress = caseDetails.getUnit().getAddress();
		String OfficeAddress_hi = caseDetails.getUnit().getAddress_hi();

//		NoticeTemplate noticeDraft = noticeTempRepo.findByNotice(notice);
		InitiateNoticeDraft noticeDraft;
		noticeDraft = summonDao.findNoticeDraftByCaseDetails(caseDetails, notice.getSummonType());
		if (noticeDraft == null) {
			noticeDraft = summonDao.findNoticeDraftByCaseDetails1(caseDetails, notice.getSummonType());
		}
		// Gouthami 07/01/2021
		AppUser userDetails = userDetailsService.getUserDetails();
		Inspector inspectorList = inspRepo.findAllByCaseDetailsAndAppUser(caseDetails, noticeDraft.getAppUser());
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
			DesignationHi = "निरीक्षक";
			Designation = "Inspector";
		}

		UserDetails user = userDetailsRepo.findById(noticeDraft.getAppUser().getUserId()).get();
		String io = userDetailsService.getFullName(user);

		InspectorListDTO inspList = getInspectorListAndCompany(caseDetails.getId());
		NoticeTempDto caseDto = new NoticeTempDto(caseDetails.getCaseId(), Utils.getCurrentDateWithMonth(),
				noticeDraft.getPara1(), noticeDraft.getPara2(), noticeDraft.getPara3(), noticeDraft.getPara4(),
				noticeDraft.getPara5(), noticeDraft.getPara1h(), noticeDraft.getPara2h(), noticeDraft.getPara3h(),
				noticeDraft.getPara4h(), noticeDraft.getPara5h(), notice.getId(), caseDetails.getCaseId(),
				caseDetails.getId(), io, notice.getSummonNo(), "", notice.getSummonType().getId(),
				notice.getIsSensitive(), Designation, DesignationHi);

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
			if (userId == ioId)
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

		String para2_1h = caseDto.getPara2h();

		// split string by no space
		String[] strSplit = para2_1h.split("\n");

		// Now convert string into ArrayList
		ArrayList<String> strList = new ArrayList<String>(Arrays.asList(strSplit));
		ArrayList<String> para2h = new ArrayList<String>();

		// Now print the ArrayList
		for (String s : strList) {
			
			para2h.add(s);

		}
		model.addAttribute("para2h", para2h);

		String para3_1h = caseDto.getPara3h();

		// split string by no space
		strSplit = para3_1h.split("\n");

		// Now convert string into ArrayList
		strList = new ArrayList<String>(Arrays.asList(strSplit));
		ArrayList<String> para3h = new ArrayList<String>();

		// Now print the ArrayList
		for (String s : strList) {
			
			para3h.add(s);

		}

		model.addAttribute("para3h", para3h);

		String para4_1h = caseDto.getPara4h();

		// split string by no space
		strSplit = para4_1h.split("\n");

		// Now convert string into ArrayList
		strList = new ArrayList<String>(Arrays.asList(strSplit));
		ArrayList<String> para4h = new ArrayList<String>();

		// Now print the ArrayList
		for (String s : strList) {
			
			para4h.add(s);

		}

		model.addAttribute("para4h", para4h);

		String para5_1h = caseDto.getPara5h();

		// split string by no space
		strSplit = para5_1h.split("\n");

		// Now convert string into ArrayList
		strList = new ArrayList<String>(Arrays.asList(strSplit));
		ArrayList<String> para5h = new ArrayList<String>();

		// Now print the ArrayList
		for (String s : strList) {
			
			para5h.add(s);

		}

		model.addAttribute("para5h", para5h);

		String para2_1 = caseDto.getPara2();

		// split string by no space
		strSplit = para2_1.split("\n");

		// Now convert string into ArrayList
		strList = new ArrayList<String>(Arrays.asList(strSplit));
		ArrayList<String> para2 = new ArrayList<String>();

		// Now print the ArrayList
		for (String s : strList) {
			
			para2.add(s);

		}
		model.addAttribute("para2", para2);

		String para3_1 = caseDto.getPara3();

		// split string by no space
		strSplit = para3_1.split("\n");

		// Now convert string into ArrayList
		strList = new ArrayList<String>(Arrays.asList(strSplit));
		ArrayList<String> para3 = new ArrayList<String>();

		// Now print the ArrayList
		for (String s : strList) {
			
			para3.add(s);

		}

		model.addAttribute("para3", para3);

		String para4_1 = caseDto.getPara4();

		// split string by no space
		strSplit = para4_1.split("\n");

		// Now convert string into ArrayList
		strList = new ArrayList<String>(Arrays.asList(strSplit));
		ArrayList<String> para4 = new ArrayList<String>();

		// Now print the ArrayList
		for (String s : strList) {
			
			para4.add(s);

		}

		model.addAttribute("para4", para4);

		String para5_1 = caseDto.getPara5();

		// split string by no space
		strSplit = para5_1.split("\n");

		// Now convert string into ArrayList
		strList = new ArrayList<String>(Arrays.asList(strSplit));
		ArrayList<String> para5 = new ArrayList<String>();

		// Now print the ArrayList
		for (String s : strList) {
			
			para5.add(s);

		}

		model.addAttribute("para5", para5);

		PdfPreviewDto pdfPreviewDto = new PdfPreviewDto(caseDetails.getId(), company,
				summonType.getIndividualType().getIndividualName(), summonType.getId(), notice.getDateOfAppear(),
				notice.getSummonNo());

		model.addAttribute("OfficeAddress", getSplitedString(OfficeAddress));
		model.addAttribute("pdfPreview", pdfPreviewDto);
		model.addAttribute("noticeDin", noticeDin);
		model.addAttribute("date", new Date());
		return "notice/getNoticeAs1";
		// return "director/approveSummon";
	}

	@RequestMapping(value = "/genSummonAs1")
	public String genSummon1(Long id, String company, String type, int sumtypeId, String individualtype,
			String dateOfAppear, String summonNo, Long userId, ModelMap model) throws Exception {

		// userId = 4L;
		// individualtype = "1";
		SNMSValidator snmsValid = new SNMSValidator();
		if (id == null || id == 0) {
			// gouthami 15/09/2020

			if (!snmsValid.getValidInteger(id)) {
				return "redirect:/getCompleteCase";

			}
			;

		}
		dateOfAppear = dateOfAppear.replace("ab", " ");
		company = company.replace("%", " ");
		/*
		 * if(!summonNo.contains("SFIO/INV/")) { return "redirect:/getCompleteCase"; }
		 */

		Optional<CaseDetails> caseD = caseDetailsRepository.findById(id);
		CaseDetails caseDetails = caseD.get();
//		String OfficeAddress  =  caseDetails.getUnit().getAddress();

		AppUser appUser = appUserRepo.findById(userId).get();

		UserDetails userDet = userDetailsRepo.findAllByUserId(appUser);

		String OfficeAddress = userDet.getUnit().getAddress();

		String OfficeAddress_hi = userDet.getUnit().getAddress_hi();

//			String caseNo = "No : " + caseDetails.getCaseId();
		if (type.equals("summon".trim())) {
			// SummonType summonType=summonTypeDetails.findById(sumtypeId).get();
			// by gouthami
			SummonType summonType = SummonTypeNewDetails.findById(sumtypeId).get();
			InitiateSummonDraft office = (summonDao.findSummonDraftByCaseDetails(caseDetails, summonType) == null)
					? new InitiateSummonDraft()
					: summonDao.findSummonDraftByCaseDetails(caseDetails, summonType);
			String caseNo = "No : " + caseDetails.getCaseId();
			String date = " Date :" + new Utils().currentDate();
//				summonNo = "SFIO/INV/2020/"+summonNo;
			InspectorListDTO inspList = getInspectorListAndCompany(caseDetails.getId());
			int otherSize = inspList.getCompanyList().size() - 1;
			String para1 = "";
			String para1_hi = "";
			if (otherSize == 0)
				para1 = company;
			else

				para1_hi = company + " तथा   " + otherSize + " अन्य कंपनी";
			para1 = company + " and " + otherSize + " other company";

			long Type = summonType.getIndividualType().getIndividualId();

			String name = summonType.getName();
			String designation = "";
			if (summonType.getDesignation() != null) {
				designation = summonType.getDesignation();
			} else {
				designation = " ";
			}

			String address = summonType.getAddress();
			String sumCompany = "";
			if (summonType.getIndividualType().getIndividualId() == 5
					|| summonType.getIndividualType().getIndividualId() == 6) {
				sumCompany = summonType.getNameCompany();
			} else {
				sumCompany = company;
			}
			//

			// AppUser userDetails = userDetailsService.getUserDetails();
			Inspector inspectorList = inspRepo.findAllByCaseDetailsAndAppUser(caseDetails, appUser);
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
			UserDetails user = userDetailsRepo.findById(appUser.getUserId()).get();
			String ioName = userDetailsService.getFullName(user);

			InitiateSummonDraft summonDraft = summonDao.findSummonDraftByCaseDetails(caseDetails, summonType);
			/*
			 * if(summonDraft)
			 */ if (summonDao.findSummonDraftByCaseDetails(caseDetails, summonType) != null) {
				SummonTempDto caseDto = new SummonTempDto(caseNo, date, office.getPara1(), office.getPara2(),
						office.getPara3(), office.getPara4(), office.getPara5(), office.getPara6(), office.getPara1_h(),
						office.getPara2_h(), office.getPara3_h(), office.getPara4_h(), office.getPara5_h(),
						office.getPara6_h(), 1L, caseDetails.getCaseId(), caseDetails.getId(), ioName, summonNo,
						dateOfAppear, "", sumtypeId, "", Designation, DesignationHi);
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
						dateOfAppear, "", sumtypeId, "", Designation, DesignationHi);
				model.addAttribute("caseDto", caseDto);
			}

			 
			 
			 String para1_1h = office.getPara1_h();

				// split string by no space
				String[] strSplit = para1_1h.split("\n");

				// Now convert string into ArrayList
				ArrayList<String> strList = new ArrayList<String>(Arrays.asList(strSplit));
				ArrayList<String> para1h = new ArrayList<String>();

				// Now print the ArrayList
				for (String s : strList) {
					
					para1h.add(s);

				}
				model.addAttribute("para1h", para1h);
			 
				String para2_1h = office.getPara2_h();

				// split string by no space
			    strSplit = para2_1h.split("\n");

				// Now convert string into ArrayList
				 strList = new ArrayList<String>(Arrays.asList(strSplit));
				ArrayList<String> para2h = new ArrayList<String>();

				// Now print the ArrayList
				for (String s : strList) {
					
					para2h.add(s);

				}
				model.addAttribute("para2h", para2h);

				String para3_1h = office.getPara3_h();

				// split string by no space
				strSplit = para3_1h.split("\n");

				// Now convert string into ArrayList
				strList = new ArrayList<String>(Arrays.asList(strSplit));
				ArrayList<String> para3h = new ArrayList<String>();

				// Now print the ArrayList
				for (String s : strList) {
					
					para3h.add(s);

				}

				model.addAttribute("para3h", para3h);

				String para4_1h = office.getPara4_h();

				// split string by no space
				strSplit = para4_1h.split("\n");

				// Now convert string into ArrayList
				strList = new ArrayList<String>(Arrays.asList(strSplit));
				ArrayList<String> para4h = new ArrayList<String>();

				// Now print the ArrayList
				for (String s : strList) {
					
					para4h.add(s);

				}

				model.addAttribute("para4h", para4h);

				String para5_1h = office.getPara5_h();

				// split string by no space
				strSplit = para5_1h.split("\n");

				// Now convert string into ArrayList
				strList = new ArrayList<String>(Arrays.asList(strSplit));
				ArrayList<String> para5h = new ArrayList<String>();

				// Now print the ArrayList
				for (String s : strList) {
					
					para5h.add(s);

				}

				model.addAttribute("para5h", para5h);
				
				
				String para6_1h = office.getPara6_h();

				// split string by no space
				strSplit = para6_1h.split("\n");

				// Now convert string into ArrayList
				strList = new ArrayList<String>(Arrays.asList(strSplit));
				ArrayList<String> para6h = new ArrayList<String>();

				// Now print the ArrayList
				for (String s : strList) {
					
					para6h.add(s);

				}

				model.addAttribute("para6h", para6h);

				
				
				String para1_1 = office.getPara1();

				// split string by no space
				strSplit = para1_1.split("\n");

				// Now convert string into ArrayList
				strList = new ArrayList<String>(Arrays.asList(strSplit));
				ArrayList<String> para1_a = new ArrayList<String>();

				// Now print the ArrayList
				for (String s : strList) {
					
					para1_a.add(s);

				}
				model.addAttribute("para1", para1_a);
				
				String para2_1 = office.getPara2();

				// split string by no space
				strSplit = para2_1.split("\n");

				// Now convert string into ArrayList
				strList = new ArrayList<String>(Arrays.asList(strSplit));
				ArrayList<String> para2 = new ArrayList<String>();

				// Now print the ArrayList
				for (String s : strList) {
					
					para2.add(s);

				}
				model.addAttribute("para2", para2);

				String para3_1 = office.getPara3();

				// split string by no space
				strSplit = para3_1.split("\n");

				// Now convert string into ArrayList
				strList = new ArrayList<String>(Arrays.asList(strSplit));
				ArrayList<String> para3 = new ArrayList<String>();

				// Now print the ArrayList
				for (String s : strList) {
					
					para3.add(s);

				}

				model.addAttribute("para3", para3);

				String para4_1 = office.getPara4();

				// split string by no space
				strSplit = para4_1.split("\n");

				// Now convert string into ArrayList
				strList = new ArrayList<String>(Arrays.asList(strSplit));
				ArrayList<String> para4 = new ArrayList<String>();

				// Now print the ArrayList
				for (String s : strList) {
					
					para4.add(s);

				}

				model.addAttribute("para4", para4);

				String para5_1 = office.getPara5();

				// split string by no space
				strSplit = para5_1.split("\n");

				// Now convert string into ArrayList
				strList = new ArrayList<String>(Arrays.asList(strSplit));
				ArrayList<String> para5 = new ArrayList<String>();

				// Now print the ArrayList
				for (String s : strList) {
					
					para5.add(s);

				}

				model.addAttribute("para5", para5);
			 

				
				String para6_1 = office.getPara6();

				// split string by no space
				strSplit = para6_1.split("\n");

				// Now convert string into ArrayList
				strList = new ArrayList<String>(Arrays.asList(strSplit));
				ArrayList<String> para6 = new ArrayList<String>();

				// Now print the ArrayList
				for (String s : strList) {
					
					para6.add(s);

				}

				model.addAttribute("para6", para6);

			 
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

				if (appUser.getUserId() == Integer.parseInt(object[0].toString()))
					privilege = true;
			}

		} catch (Exception e) {
			logger.info(e.getMessage());
		}

		PdfPreviewDto pdfPreviewDto = new PdfPreviewDto(id, company, type, sumtypeId, dateOfAppear, "");
		model.addAttribute("privilege", privilege);
		model.addAttribute("pdfPreview", pdfPreviewDto);
		model.addAttribute("individualtype", individualtype);

		return "notice/genSummonAs1";

	}

	private String getSplitedString(String address) {

		if (address != null) {
			String[] splitStr = address.split(" ");
			StringBuilder consStr = new StringBuilder();
			for (int i = 0; i < splitStr.length; i++) {
				consStr.append(splitStr[i]).append(" ");
				if (i % 5 == 0 && i != 0)
					consStr.append("\r\n");
			}
			return consStr.toString();
		} else {
			return address;
		}
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

	public void summonPreview(Long officeOrderId, String unSignPdfFullPath, Long userId) throws Exception {

		String s = "";

		s = " " + snmsapi.trim() + "/PdfSummonAs1?officeOrderId=" + officeOrderId + "&userId=" + userId;

		String Command = s.trim() + " ";

		createpdf1(unSignPdfFullPath, Command);

	}

	private void createpdf(File file, String s) throws InterruptedException {
		// String output= file.getPath();

		String output = filePath1 + File.separator + "Preview" + File.separator + file.getName();

		System.out.println("output===========================" + output);
		String command = pdfExe.trim() + " " + " " + s + output.trim();
		System.out.println("command==========================" + command);
		try {

			// Running the above command
			Runtime run = Runtime.getRuntime();
			Process proc = run.exec(command);
			proc.waitFor(45, TimeUnit.SECONDS);

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void createpdf1(String unSignPdfFullPath, String s) throws InterruptedException {
		String command = pdfExe.trim() + " " + " " + s + unSignPdfFullPath;

		try {

			// Running the above command
			Runtime run = Runtime.getRuntime();
			Process proc = run.exec(command.trim());
			proc.waitFor(45, TimeUnit.SECONDS);

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@RequestMapping(value = "/genFullNoticePdf", produces = MediaType.APPLICATION_PDF_VALUE)
	public ResponseEntity<Resource> genFullNoticePdf(@RequestParam(name = "caseId", required = false) Long id,
			@RequestParam(name = "chooseComapny", required = false) String company,
			@RequestParam(name = "type", required = false) String type,
			@RequestParam(name = "individualtype1", required = false) String individualtype,
			@RequestParam(name = "summontypeid", required = false, defaultValue = "0") int summontypeid,
			@RequestParam(name = "dateOfAppear", required = false) String dateOfAppear,
			@RequestParam(name = "summonNo1", required = false) String summonNo, ModelMap model) throws Exception {

		individualtype = "1";

		String dateOfAppear1 = dateOfAppear.replace(" ", "ab");
		company = company.replace(" ", "%");

		Optional<CaseDetails> caseD = caseDetailsRepository.findById(id);
		CaseDetails caseDetails = caseD.get();
		// SummonType summonType = SummonTypeNewDetails.findById(sumtypeId).get();

		// InitiateSummonDraft summonDraft =
		// summonDao.findSummonDraftByCaseDetails(caseDetails, summonType);

		try {

			Long userId = userDetailsService.getUserDetails().getUserId();

			File file = File.createTempFile("Notice", ".pdf");

			String s = "";

			s = " " + snmsapi.trim() + "/genNoticeAs1?id=" + id + "&company=" + company.trim() + "&type=" + type.trim()
					+ "&summontypeid=" + summontypeid + "&individualtype=" + individualtype + "&dateOfAppear="
					+ dateOfAppear1 + "&summonNo=" + summonNo + "&userId=" + userId;

			String Command = s.trim() + " ";
			createpdf(file, Command);
			// String filePath = "E:\\SNMS\\file_upload\\Preview";

			String filePath = filePath1 + File.separator + "Preview";

			File parent = new File(filePath).getParentFile().getCanonicalFile();
			ESAPI.validator().getValidDirectoryPath("DirectoryName", filePath, parent, false);
			Path path = Paths.get(filePath + File.separator + file.getName());

			Resource resource = null;
			try {
				resource = new UrlResource(path.toUri());

			} catch (MalformedURLException e) {
				logger.info(e.getMessage());
			}

			HttpHeaders headers = new HttpHeaders();
			headers.add("Content-Disposition", "inline; filename=" + file.getName());

			return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(resource);

		}

		catch (Exception e) {
			System.out.println(e.getMessage());
			throw new RuntimeException(e);
		}

	}

	/*
	 * @RequestMapping(value = "/genNoticeAs1") public String getNotice(Long id,
	 * String company, String type, int sumtypeId ) throws Exception {
	 * System.out.println(company); System.out.println(type);
	 * System.out.println(id);
	 * 
	 * return company; }
	 */
	@RequestMapping(value = "/genNoticeAs1")
	public String getNotice(Long id, String company, String type, int summontypeid, String individualtype,
			String dateOfAppear, String summonNo, Long userId, ModelMap model) throws Exception {
		SNMSValidator snmsValid = new SNMSValidator();

		if (id == null) {
			return "redirect:/getCompleteCase";
		}

		dateOfAppear = dateOfAppear.replace("ab", " ");
		company = company.replace("%", " ");
		AppUser appUser = appUserRepo.findById(userId).get();

		UserDetails userDet = userDetailsRepo.findAllByUserId(appUser);

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

		String date = " Date :" + new Utils().currentDate();
		InspectorListDTO inspList = getInspectorListAndCompany(caseDetails.getId());
		// String io=inspList.getIoName();

		// Gouthami 07/01/2021
		// AppUser userDetails = userDetailsService.getUserDetails();
		Inspector inspectList = inspRepo.findAllByCaseDetailsAndAppUser(caseDetails, appUser);
		String Designation;
		String DesignationHi = null;
		if (inspectList.getIsAdo() == true) {
			// Designation = "Addl";
			Designation = "Addl.Director";
			DesignationHi = "अपर निदेशक";
		} else if (inspectList.getIsIO() == true) {
			Designation = "Investigating Officer";
			DesignationHi = "जांच अधिकारी";
		} else {
			DesignationHi = "निरीक्षक";
			Designation = "Inspector";
		}
		UserDetails user = userDetailsRepo.findById(appUser.getUserId()).get();
		String io = userDetailsService.getFullName(user);
		int otherSize = inspList.getCompanyList().size() - 1;
		model.addAttribute("listCom", inspList.getCompanyList());

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

		String comapnyPara = "";
		String comapnyPara_hi = "";
		if (otherSize == 0) {
			comapnyPara = company;
			comapnyPara_hi = company;
		} else {
			comapnyPara = company + " and " + otherSize + " other company";
			comapnyPara_hi = company + " तथा " + otherSize + "अन्य कंपनी";
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
					initiateNoticeDraft.getPara5(), initiateNoticeDraft.getPara1h(), initiateNoticeDraft.getPara2h(),
					initiateNoticeDraft.getPara3h(), initiateNoticeDraft.getPara4h(), initiateNoticeDraft.getPara5h(),
					1L, caseDetails.getCaseId(), caseDetails.getId(), io, summonNo, dateOfAppear, summontypeid, "",
					Designation, DesignationHi);
//			caseDto.setNoticeDin(noticeDIN);
			model.addAttribute("caseDto", caseDto);
		} else {

			initiateNoticeDraft.setPara1(NoticeConstant.PARA_1 + comapnyPara + NoticeConstant.PARA_1_1);
			initiateNoticeDraft.setPara1h(NoticeConstant.PARA_1_h + comapnyPara_hi + NoticeConstant.PARA_1_1_h);
			initiateNoticeDraft.setPara2(NoticeConstant.PARA_2 + caseDetails.getMcaOrderNo() + " dated : "
					+ Utils.formatMcaOrderDate(caseDetails.getMcaOrderDate()) + NoticeConstant.PARA_2_1 + comapnyPara
					+ NoticeConstant.PARA_2_2);

			initiateNoticeDraft.setPara2h(NoticeConstant.PARA_2_h + caseDetails.getMcaOrderNo() + " दिनांक : "
					+ Utils.formatMcaOrderDate(caseDetails.getMcaOrderDate()) + NoticeConstant.PARA_2_1_h
					+ comapnyPara_hi + NoticeConstant.PARA_2_2_h);

			initiateNoticeDraft.setPara3(NoticeConstant.PARA_3 + company + NoticeConstant.PARA_3_1 + company
					+ NoticeConstant.PARA_3_2 + comapnyPara + NoticeConstant.PARA_3_3);

			initiateNoticeDraft.setPara3h(NoticeConstant.PARA_3_h + " " + NoticeConstant.PARA_3_1_h + company
					+ NoticeConstant.PARA_3_2_h + comapnyPara_hi + NoticeConstant.PARA_3_3_h);

			initiateNoticeDraft.setPara4(NoticeConstant.PARA_4 + dateOfAppear);
			initiateNoticeDraft.setPara4h(NoticeConstant.PARA_4_h + dateOfAppear + NoticeConstant.PARA_4_1_h);
			initiateNoticeDraft.setPara5(NoticeConstant.PARA_5);
			initiateNoticeDraft.setPara5h(NoticeConstant.PARA_5_h);
			NoticeTempDto caseDto = new NoticeTempDto(caseNo, date, initiateNoticeDraft.getPara1(),
					initiateNoticeDraft.getPara2(), initiateNoticeDraft.getPara3(), initiateNoticeDraft.getPara4(),
					initiateNoticeDraft.getPara5(), initiateNoticeDraft.getPara1h(), initiateNoticeDraft.getPara2h(),
					initiateNoticeDraft.getPara3h(), initiateNoticeDraft.getPara4h(), initiateNoticeDraft.getPara5h(),
					1L, caseDetails.getCaseId(), caseDetails.getId(), io, summonNo, dateOfAppear, summontypeid, "",
					Designation, DesignationHi);
//			caseDto.setNoticeDin(noticeDIN);
			model.addAttribute("caseDto", caseDto);
		}

		String para2_1h = initiateNoticeDraft.getPara2h();

		// split string by no space
		String[] strSplit = para2_1h.split("\n");

		// Now convert string into ArrayList
		ArrayList<String> strList = new ArrayList<String>(Arrays.asList(strSplit));
		ArrayList<String> para2h = new ArrayList<String>();

		// Now print the ArrayList
		for (String s : strList) {
			
			para2h.add(s);

		}
		model.addAttribute("para2h", para2h);

		String para3_1h = initiateNoticeDraft.getPara3h();

		// split string by no space
		strSplit = para3_1h.split("\n");

		// Now convert string into ArrayList
		strList = new ArrayList<String>(Arrays.asList(strSplit));
		ArrayList<String> para3h = new ArrayList<String>();

		// Now print the ArrayList
		for (String s : strList) {
			
			para3h.add(s);

		}

		model.addAttribute("para3h", para3h);

		String para4_1h = initiateNoticeDraft.getPara4h();

		// split string by no space
		strSplit = para4_1h.split("\n");

		// Now convert string into ArrayList
		strList = new ArrayList<String>(Arrays.asList(strSplit));
		ArrayList<String> para4h = new ArrayList<String>();

		// Now print the ArrayList
		for (String s : strList) {
			
			para4h.add(s);

		}

		model.addAttribute("para4h", para4h);

		String para5_1h = initiateNoticeDraft.getPara5h();

		// split string by no space
		strSplit = para5_1h.split("\n");

		// Now convert string into ArrayList
		strList = new ArrayList<String>(Arrays.asList(strSplit));
		ArrayList<String> para5h = new ArrayList<String>();

		// Now print the ArrayList
		for (String s : strList) {
			
			para5h.add(s);

		}

		model.addAttribute("para5h", para5h);

		String para2_1 = initiateNoticeDraft.getPara2();

		// split string by no space
		strSplit = para2_1.split("\n");

		// Now convert string into ArrayList
		strList = new ArrayList<String>(Arrays.asList(strSplit));
		ArrayList<String> para2 = new ArrayList<String>();

		// Now print the ArrayList
		for (String s : strList) {
			
			para2.add(s);

		}
		model.addAttribute("para2", para2);

		String para3_1 = initiateNoticeDraft.getPara3();

		// split string by no space
		strSplit = para3_1.split("\n");

		// Now convert string into ArrayList
		strList = new ArrayList<String>(Arrays.asList(strSplit));
		ArrayList<String> para3 = new ArrayList<String>();

		// Now print the ArrayList
		for (String s : strList) {
			
			para3.add(s);

		}

		model.addAttribute("para3", para3);

		String para4_1 = initiateNoticeDraft.getPara4();

		// split string by no space
		strSplit = para4_1.split("\n");

		// Now convert string into ArrayList
		strList = new ArrayList<String>(Arrays.asList(strSplit));
		ArrayList<String> para4 = new ArrayList<String>();

		// Now print the ArrayList
		for (String s : strList) {
			
			para4.add(s);

		}

		model.addAttribute("para4", para4);

		String para5_1 = initiateNoticeDraft.getPara5();

		// split string by no space
		strSplit = para5_1.split("\n");

		// Now convert string into ArrayList
		strList = new ArrayList<String>(Arrays.asList(strSplit));
		ArrayList<String> para5 = new ArrayList<String>();

		// Now print the ArrayList
		for (String s : strList) {
			
			para5.add(s);

		}

		model.addAttribute("para5", para5);

		List<Object[]> inspectorList = userMangCustom.getInspectorList(caseDetails.getId());
		boolean privilege = false;
		try {
			for (Object[] object : inspectorList) {

				if (userId == Integer.parseInt(object[0].toString()))
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
		return "notice/getNoticeAs1";

	}

	@RequestMapping(value = "/genOfflineSummonAs1")
	public String genOfflineSummonAs1(Long id, String company, String type, int sumtypeId, String individualtype,
			String dateOfAppear, String dateofIssue, String summonNo, String SummonType, Long userId, ModelMap model)
			throws Exception {
		SNMSValidator snmsValid = new SNMSValidator();

		if (id == null) {
			return "redirect:/getCompleteCase";
		}
		dateOfAppear = dateOfAppear.replace("ab", " ");
		company = company.replace("%", " ");

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

			String address = summonType.getAddress() + " ";
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
			AppUser appUser = appUserRepo.findById(userId).get();

			UserDetails userDet = userDetailsRepo.findAllByUserId(appUser);
			Inspector inspectorList = inspRepo.findAllByCaseDetailsAndAppUser(caseDetails, appUser);
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
				DesignationHi = "निरीक्षक";
				Designation = "Inspector";
			}
			UserDetails user = userDetailsRepo.findById(userDet.getId()).get();
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

			if (designation != null && designation != "") {
				para2 = para2 + " , " + designation;
				para2h = para2h + " , " + designation;
			}
			if (sumCompany != null && sumCompany != "") {
				para2 = para2 + " , " + sumCompany;
				para2h = para2h + " , " + sumCompany;
			}
			para2 = para2 + " , " + address + SummonConstant.PARA_Of_2_2;
			para2h = para2h + " , " + address + SummonConstant.PARA_Of_1_2h + dateofIssue + SummonConstant.PARA_Of_2_2h;
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
			Image image1 = Image.getInstance(new Utils().getConfigMessage("image.emblemPath"));

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
			model.addAttribute("image", image);
			// model.addAttribute("companydisplay",para1);
		}

		List<Object[]> inspectorList = userMangCustom.getInspectorList(caseDetails.getId());
		boolean privilege = false;
		try {
			for (Object[] object : inspectorList) {

				if (userId == Integer.parseInt(object[0].toString()))
					privilege = true;
			}

		} catch (Exception e) {
			logger.info(e.getMessage());
		}

		PdfPreviewDto pdfPreviewDto = new PdfPreviewDto(id, company, type, sumtypeId, dateOfAppear, "", dateofIssue,
				summonDIN);
		model.addAttribute("privilege", privilege);
		model.addAttribute("pdfPreview", pdfPreviewDto);
		return "notice/genOfflineSummonAs1";

	}

}
