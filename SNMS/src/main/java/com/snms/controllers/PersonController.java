package com.snms.controllers;

import java.io.IOException;
import java.time.Instant;
import java.net.MalformedURLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.criteria.Order;
import javax.validation.Valid;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
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
import org.springframework.web.bind.annotation.ResponseBody;

import com.snms.dao.AppRoleDAO;
import com.snms.dao.AppUserDAO;
import com.snms.dao.SummonDao;
import com.snms.dao.gamsDao;
import com.snms.dto.DesignationDaoImpl;
import com.snms.dto.InspectorDTO;
import com.snms.dto.InspectorListDTO;
import com.snms.dto.PendingForApprovalDTO;
import com.snms.dto.pendingPersonDto;
import com.snms.entity.AddDesignation;
import com.snms.entity.AppUser;
import com.snms.entity.CaseDetails;
import com.snms.entity.Company;
import com.snms.entity.CompanySummon;
import com.snms.entity.CompanyType;
import com.snms.entity.Country;
import com.snms.entity.IndividualType;
import com.snms.entity.Inspector;
import com.snms.entity.InvestigationStatus;
import com.snms.entity.NoticeStatus;
import com.snms.entity.OfficeOrder;
import com.snms.entity.PersonDATA;
import com.snms.entity.PersonDetails;
import com.snms.entity.RelationpersonCompany;
import com.snms.entity.SummonDetails;
import com.snms.entity.SummonStatus;
import com.snms.entity.SummonType;
import com.snms.entity.UnitDetails;
import com.snms.entity.UserDetails;
import com.snms.entity.UserRole;
import com.snms.entity.personcompanyApproval;
import com.snms.service.AddDesignationRepository;
import com.snms.service.AddNoticeSummonRepository;
import com.snms.service.AddPersonRepository;
import com.snms.service.AddStatusRepository;
import com.snms.service.AuditBeanBo;
import com.snms.service.CaseDetailsRepository;
import com.snms.service.CompanySummonRepository;
import com.snms.service.CompanyTypeRepositroy;
import com.snms.service.CountryRepository;
import com.snms.service.OfficeOrderRepository;
import com.snms.service.PersoncompanyApprovalRepository;
import com.snms.service.RelationpersonCompanyRepository;
import com.snms.service.SummonTypeNew;
import com.snms.service.UserDetailsRepository;
import com.snms.service.UserDetailsServiceImpl;
import com.snms.service.UserManagementCustom;
import com.snms.utils.SnmsConstant;
import com.snms.utils.SnmsException;
import com.snms.utils.Utils;
import com.snms.validation.SNMSValidator;
import com.snms.validation.SummonNoticeValidation;
import com.snms.validation.personValidation;

@Controller
public class PersonController {

	@Autowired
	private DesignationDaoImpl designationService;
	@Autowired
	private UserDetailsServiceImpl userDetailsService;
	@Autowired
	private UserDetailsRepository  userDetailsRepo;
	@Autowired
	private UserManagementCustom userMangCustom;

	@Autowired
	private OfficeOrderRepository officeOrderRepo;

	@Autowired
	private AddNoticeSummonRepository addNoticeSummonRepository;
	@Autowired
	private AddDesignationRepository designationRepo;
	@Autowired
	private AppUserDAO appUserDAO;
	@Autowired
	private SummonDao summonDao;
	@Autowired
	private AuditBeanBo auditBeanBo;
	@Autowired
	private CompanySummonRepository companySummonRepo;
	@Autowired
	private CaseDetailsRepository caseDetailsRepository;
	@Autowired
	private CompanyTypeRepositroy companyTypeRepo;
	@Autowired
	CaseDetailsRepository CaseDetailsRepo;
	@Autowired
	AddPersonRepository addPersonRepo;
	@Autowired
	AddStatusRepository addStatusRepository;
	@Autowired
	SummonTypeNew summonTypeRepo;
	@Autowired
	PersoncompanyApprovalRepository pcaRepo;
	@Autowired
	RelationpersonCompanyRepository rpcRepo;
	@Autowired
	private Utils utils;
	@Autowired
	private AppRoleDAO  roleDAO;
	@Autowired gamsDao gamsdao;
	
	@Autowired
	private CountryRepository countryRepo;
	
	//When "Add Person GEP Clicked"
	@GetMapping("getAssignCaseList")
	public String getCaseList(ModelMap modelMap) throws Exception {
		
		
		
		List<Object[]> list = userMangCustom.findCaseByUserIdbystage(getUserDetails().getUserId());
		List<PendingForApprovalDTO> caselist = new ArrayList<PendingForApprovalDTO>();
		for (Object[] dto : list) {

			caselist.add(new PendingForApprovalDTO(dto[0].toString(), dto[1].toString(),
					Long.parseLong(dto[2].toString()), dto[3], dto[4], dto[5].toString()));
		}
		String role = roleDAO.getRoleName(getUserDetails().getUserId());
		modelMap.addAttribute("caselist", caselist);
		modelMap.addAttribute("caseDetails", new CaseDetails());
		modelMap.addAttribute("roleId",role );
		
		
		return "person/assignedCase";
	}
	
	@GetMapping("getAssignCaseListAdir")
	public String getAssignCaseListAdir(ModelMap modelMap) throws Exception {
		
		Boolean isADO = true;
		
		List<Object[]> list = userMangCustom.findCaseByUserId(getUserDetails().getUserId(),isADO);
		List<PendingForApprovalDTO> caselist = new ArrayList<PendingForApprovalDTO>();

		for (Object[] dto : list) {

			caselist.add(new PendingForApprovalDTO(dto[0].toString(), dto[1].toString(),
					Long.parseLong(dto[2].toString()), dto[3], dto[4], dto[5].toString()));
		}

		modelMap.addAttribute("caselist", caselist);
		modelMap.addAttribute("caseDetails", new CaseDetails());
		return "person/updateCase";
	}

	
	// Called when click "Add Person"
	@RequestMapping(value = "addPerson", params = "caseDetails")
	public String getNoticeDetails(@RequestParam(value = "caseDetails", required = true) Long caseId, ModelMap modelMap)
			throws Exception, ParseException {
		
		

		SNMSValidator snmsvalid = new SNMSValidator();
		if (snmsvalid.getValidInteger(caseId)) {
			
			commonObjectSummon(modelMap, caseId);
			SummonDetails summonDetails1 = new SummonDetails();
			SummonDetails findByCaseId = summonDao.findByCaseId(caseId);
			
			
			//Date d1 = new Date(year, month, date);
			//Date d2 = new forma
					
			String s1 = "2014-02-14";
			
			
			SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a", Locale.ENGLISH);
			String dateInString1 = "2022-06-12 10:15:55 AM"; 
			Date validdate1 = formatter1.parse(dateInString1);
			
			summonDetails1.setValidDate(validdate1);
			modelMap.addAttribute("validdate", validdate1);
			
			/*
			 * SimpleDateFormat formatter1 = new SimpleDateFormat("dd-MMM-yyyy"); String
			 * dateInString = "7-Jun-2013"; Date date1 = formatter.parse(dateInString);
			 * 
			 * 
			 * String string = "02/01/2014"; //assuming input DateFormat sdf = new
			 * SimpleDateFormat(string); Date dt = sdf.parse(string);
			 * 
			 * 
			 * Date currentDate = new Date(02/10/2021); Date s9 = new
			 * SimpleDateFormat("yyyy-MM-dd").parse(s1);
			 * 
			 * SimpleDateFormat dateFormat= new SimpleDateFormat("dd/MMM/yyyy"); String
			 * dateOnly = dateFormat.format(currentDate);
			 */
			//  Date date = Date.from(Instant.parse("2020-05-05T00:00:00.000Z"));           //working
			  
			/*
			 * LocalDate localdate = LocalDate.of(2022,10,10);
			 * 
			 * 
			 * Date date2 = new GregorianCalendar(2014, Calendar.FEBRUARY, 11).getTime();
			 * 
			 * 
			 * Calendar myCalendar = new GregorianCalendar(2014, 2, 11); Date myDate =
			 * myCalendar.getTime();
			 * 
			 * 
			 * 
			 * System.out.println(summonDetails1.getPersonDetails().getCheckDate());
			 */
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
			
			//List<AddDesignation> designationList = designationRepo.findDesignationBytype("C", "S",Sort.by(Sort.Direction.DESC, "designation"));
			
			List<AddDesignation> designationList = designationService.findDesignationBytype(new AddDesignation());
			
			List<CompanyType> compTypeList = companyTypeRepo.findAll();
			List<InvestigationStatus> StatusList =null;
 			
			String role=roleDAO.getRoleName(userDetailsService.getUserDetails().getUserId());
			if(role.equals("ROLE_USER")) {
			StatusList = addStatusRepository.findAllBytype("I");
			}else {
				StatusList = addStatusRepository.findAllBytype("P");
			}
			summonDetails1.setCaseId(caseId);
		
			
			List<PersonDetails> pd1 = addPersonRepo.findAllByCompanypersonCaseDetails(caseDetailsRepository.findById(caseId),Sort.by(Sort.Direction.DESC, "createdDate"));
				  HashSet<PersonDetails> pd = new HashSet<PersonDetails>(pd1);
		//	List<PersonDetails> pd = addPersonRepo.findAll();
			List<personcompanyApproval> pca = pcaRepo.findAll();
			
		
			
			System.out.println("size of list" + pca.size());
			List<Country> cL = countryRepo.findAll();
			modelMap.addAttribute("Country", cL);
			modelMap.addAttribute("designationList", designationList);
			modelMap.addAttribute("compTypeList", compTypeList);
		    modelMap.addAttribute("statusLst", StatusList);
			modelMap.addAttribute("summonDetails", summonDetails1);
			modelMap.addAttribute("pesoncompList", pd);
			modelMap.addAttribute("pcaList", pca);
			return "person/addPersons";
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

			return "person/assignedCase";
		}

	}
	@RequestMapping(value = "CaseDetailsView", params = "caseDetailsView")
	public String caseDetailsvidew(@RequestParam(value = "caseDetailsView", required = true) Long caseId, ModelMap model)
			throws Exception {
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
		return "person/CaseDetails";
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

	@SuppressWarnings("unused")
	@RequestMapping(value = "addCompany", method = RequestMethod.POST)
	public String addCompany(@ModelAttribute("summonDetails") @Valid SummonDetails summonDetails, BindingResult result,
			ModelMap modelMap) throws Exception {

		if (null == summonDetails || summonDetails.equals(0)) {
			return "person/addPersons";
		}

		commonObjectSummon(modelMap, summonDetails.getCaseId());
		CompanySummon companySummon = new CompanySummon(summonDetails.getCompanySummonsDto().getCin(),
				summonDetails.getCompanySummonsDto().getCompanyName(),
				summonDetails.getCompanySummonsDto().getAddress(), summonDetails.getCompanySummonsDto().getEmail(),
				summonDetails.getCompanySummonsDto().getCompanyType());

		SummonDetails savedSummon = summonDao.findByCaseId(summonDetails.getCaseId());

		SummonNoticeValidation summNoticeval = new SummonNoticeValidation();
		summNoticeval.validateCompleteCompany(summonDetails, result);

		if (result.hasErrors()) {

			summonDetails.setCaseId(summonDetails.getCaseId());

			List<CompanyType> compTypeList = companyTypeRepo.findAll();
			modelMap.addAttribute("compTypeList", compTypeList);
			List<AddDesignation> designationList = designationService.findDesignationBytype(new AddDesignation());
			
			List<InvestigationStatus> StatusList =null;
			String role=roleDAO.getRoleName(userDetailsService.getUserDetails().getUserId());
			if(role.equals("ROLE_USER")) {
			StatusList = addStatusRepository.findAllBytype("I");
			}else {
				StatusList = addStatusRepository.findAllBytype("P");
			}
			//List<PersonDetails> pd1 = addPersonRepo.findAllByCompanypersonCaseDetails(caseDetailsRepository.findById(summonDetails.getCaseId()));
			List<PersonDetails> pd1 = addPersonRepo.findAllByCompanypersonCaseDetails(caseDetailsRepository.findById(summonDetails.getCaseId()),Sort.by(Sort.Direction.DESC, "createdDate"));
			
			HashSet<PersonDetails> pd = new HashSet<PersonDetails>(pd1);
			
			List<personcompanyApproval> pca = pcaRepo.findAll();
			System.out.println("size of list" + pca.size());
			modelMap.addAttribute("designationList", designationList);
			modelMap.addAttribute("compTypeList", compTypeList);
			 modelMap.addAttribute("statusLst", StatusList);
			
			modelMap.addAttribute("pesoncompList", pd);
			modelMap.addAttribute("pcaList", pca); 
			modelMap.addAttribute("summonDetails", summonDetails);
			return "person/addPersons";
		}
		System.out.println("comptype   " + summonDetails.getCompanySummonsDto().getCompanyType());
		if (summonDetails.getId() != 0) {
			boolean isCompExist = false;
			for (int i = 0; i < savedSummon.getCompanySummon().size(); i++) {
				if (savedSummon.getCompanySummon().get(i).getCompanyName()
						.equalsIgnoreCase(summonDetails.getCompanySummonsDto().getCompanyName())) {
					isCompExist = true;
					savedSummon.getCompanySummon().get(i).setCin(summonDetails.getCompanySummonsDto().getCin());
					savedSummon.getCompanySummon().get(i).setAddress(summonDetails.getCompanySummonsDto().getAddress());
					savedSummon.getCompanySummon().get(i).setEmail(summonDetails.getCompanySummonsDto().getEmail());
					savedSummon.getCompanySummon().get(i)
							.setCompanyType(summonDetails.getCompanySummonsDto().getCompanyType());
					savedSummon.getCompanySummonsDto().setCin(companySummon.getCin());
					savedSummon.getCompanySummonsDto().setCompanyType(companySummon.getCompanyType());
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
				savedSummon.getCompanySummonsDto()
						.setCompanyType(summonDetails.getCompanySummonsDto().getCompanyType());
			}
		} else {
			savedSummon = new SummonDetails();
			BeanUtils.copyProperties(summonDetails, savedSummon);
			BeanUtils.copyProperties(summonDetails.getCompanySummonsDto(), companySummon);
			savedSummon.getCompanySummon().add(companySummon);
		}
		
		List<InvestigationStatus> StatusList =null;
		String role=roleDAO.getRoleName(userDetailsService.getUserDetails().getUserId());
		if(role.equals("ROLE_USER")) {
		StatusList = addStatusRepository.findAllBytype("I");
		}else {
			StatusList = addStatusRepository.findAllBytype("P");
		}
//		modelMap.addAttribute("compcin", companySummon1.getCin());
//		modelMap.addAttribute("compAddr", companySummon1.getAddress());
		modelMap.addAttribute("summonbycaseidCompanyList", savedSummon.getCompanySummon());
		List<AddDesignation> designationList = designationService.findDesignationBytype(new AddDesignation());
		 modelMap.addAttribute("statusLst", StatusList);
		modelMap.addAttribute("designationList", designationList);
		addNoticeSummonRepository.save(savedSummon);
		List<CompanyType> compTypeList = companyTypeRepo.findAll();
		modelMap.addAttribute("compTypeList", compTypeList);

		modelMap.addAttribute("summonDetails", savedSummon);
		return "person/addPersons";
	}

	@SuppressWarnings("unused")
	@RequestMapping(value = "savepersondetails")
	public String addperaonCompany(@ModelAttribute("summonDetails") @Valid SummonDetails summonDetails,
			BindingResult result, ModelMap modelMap) throws Exception {
		
		
		SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a", Locale.ENGLISH);
		String dateInString1 = "2022-06-12 10:15:55 AM"; 
		Date validdate1 = formatter1.parse(dateInString1);		
		summonDetails.setValidDate(validdate1);
		modelMap.addAttribute("validdate", validdate1);
		
		
		commonObjectSummon(modelMap, summonDetails.getCaseId());
		if(summonDetails.getPersonDetails().getCompDto().getDesignation()==null)
		{
			result.rejectValue("personDetails.compDto.designation", "msg.wrongId");
		}
		else{
			AddDesignation designationbyId = designationRepo.findById(summonDetails.getPersonDetails().getCompDto().getDesignation().getId()).get();
			if(designationbyId==null)
				result.rejectValue("personDetails.compDto.designation", "msg.wrongId");
		}
		
		
		
		
		
		
		
		// Saurabh Changes
		if(summonDetails.getPersonDetails().getIsDummyValue() == null || (summonDetails.getPersonDetails().getIsDummyValue() == "" ))
		{
			result.rejectValue("personDetails.isDummyValue", "msg.wrongDummy");
		}
		
		if(summonDetails.getPersonDetails().getIsApass().equalsIgnoreCase("Y")) {
		if(summonDetails.getPersonDetails().getCountry()==null) {
			result.rejectValue("personDetails.country", "msg.wrongId");
		}
		}
		if(summonDetails.getPersonDetails().getVoterId()!="") {
			PersonDetails per =addPersonRepo.findAllByVoterId(summonDetails.getPersonDetails().getVoterId());
			
			if(per!=null) {
				if(!per.getPanNumber().equals(summonDetails.getPersonDetails().getPanNumber())) {
					result.rejectValue("personDetails.voterId", "msg.wrongvoterId");
				}
			}
		}
		
		//Gouthami 03/09/2021 duplicate emai Id validation removed
		/*
		 * if(summonDetails.getPersonDetails().getEmail()!="") { PersonDetails per =
		 * addPersonRepo.findAllByEmail(summonDetails.getPersonDetails().getEmail());
		 * if(per!=null) {
		 * if(!per.getPanNumber().equals(summonDetails.getPersonDetails().getPanNumber()
		 * )) { result.rejectValue("personDetails.email", "msg.wrongemailId"); } } }
		 */
		if(summonDetails.getPersonDetails().getPassportNumber()!="") {
			PersonDetails per =addPersonRepo.findAllByPassportNumber(summonDetails.getPersonDetails().getPassportNumber());
			
			if(per!=null) {
				if(!per.getPanNumber().equals(summonDetails.getPersonDetails().getPanNumber())) {
					result.rejectValue("personDetails.passportNumber", "msg.wrongPassportNumber");
				}
			}
		}
		personValidation perVal = new personValidation();
		
		perVal.validatePerson(summonDetails,result);
		// sumVal.validateIndivialType(summonDetails, result);
		if (result.hasErrors()) {
           summonDetails.setEditperson(true);
           List<CompanyType> compTypeList = companyTypeRepo.findAll();
			modelMap.addAttribute("compTypeList", compTypeList);
			List<AddDesignation> designationList = designationService.findDesignationBytype(new AddDesignation());
			
			
			List<InvestigationStatus> StatusList =null;
			String role=roleDAO.getRoleName(userDetailsService.getUserDetails().getUserId());
			if(role.equals("ROLE_USER")) {
			StatusList = addStatusRepository.findAllBytype("I");
			}else {
				StatusList = addStatusRepository.findAllBytype("P");
			}
		
			List<PersonDetails> pd1 = addPersonRepo.findAllByCompanypersonCaseDetails(caseDetailsRepository.findById(summonDetails.getCaseId()),Sort.by(Sort.Direction.DESC, "createdDate"));
			HashSet<PersonDetails> pd = new HashSet<PersonDetails>(pd1);
			List<personcompanyApproval> pca = pcaRepo.findAll();
			System.out.println("size of list" + pca.size());
			List<Country> cL = countryRepo.findAll();
			modelMap.addAttribute("Country", cL);
			modelMap.addAttribute("designationList", designationList);
			modelMap.addAttribute("compTypeList", compTypeList);
		    modelMap.addAttribute("statusLst", StatusList);
			
			modelMap.addAttribute("pesoncompList", pd);
			modelMap.addAttribute("pcaList", pca); 
			modelMap.addAttribute("summonDetails", summonDetails);
			return "person/addPersons";
		}
		SummonDetails savedSummon = summonDao.findByCaseId(summonDetails.getCaseId());

		PersonDetails persondet = new PersonDetails();
		RelationpersonCompany rpc = new RelationpersonCompany(
				summonDetails.getPersonDetails().getCompDto().getCaseDetails(),
				summonDetails.getPersonDetails().getCompDto().getDesignation(),
				summonDetails.getPersonDetails().getCompDto().getDateAppointment(),
				summonDetails.getPersonDetails().getCompDto().getDateCessation(),
				summonDetails.getPersonDetails().getCompDto().getDin(),
				summonDetails.getPersonDetails().getCompDto().getFrn(),
				summonDetails.getPersonDetails().getCompDto().getIec(),
				summonDetails.getPersonDetails().getCompDto().getSummon_type_id(),
				summonDetails.getPersonDetails().getCompDto().getCompanySummon(),
				summonDetails.getPersonDetails().getCompDto().getPersonRemark());

		SummonDetails saveSummon;
		commonObjectSummon(modelMap, summonDetails.getCaseId());
		CompanySummon companySummon = new CompanySummon(summonDetails.getCompanySummonsDto().getCin(),
				summonDetails.getCompanySummonsDto().getCompanyName(),
				summonDetails.getCompanySummonsDto().getAddress(), summonDetails.getCompanySummonsDto().getEmail(),
				summonDetails.getCompanySummonsDto().getCompanyType());

		if (summonDetails.getId() != 0) {

			boolean isCompExist = false;
			for (int i = 0; i < savedSummon.getCompanySummon().size(); i++) {
				if (savedSummon.getCompanySummon().get(i).getCompanyName()
						.equalsIgnoreCase(summonDetails.getCompanySummonsDto().getCompanyName())) {
					isCompExist = true;
					savedSummon.getCompanySummon().get(i).setCin(summonDetails.getCompanySummonsDto().getCin());
					savedSummon.getCompanySummon().get(i).setAddress(summonDetails.getCompanySummonsDto().getAddress());
					savedSummon.getCompanySummon().get(i).setEmail(summonDetails.getCompanySummonsDto().getEmail());
					savedSummon.getCompanySummon().get(i)
							.setCompanyType(summonDetails.getCompanySummonsDto().getCompanyType());
					savedSummon.getCompanySummonsDto().setCin(companySummon.getCin());
					savedSummon.getCompanySummonsDto().setCompanyType(companySummon.getCompanyType());
					savedSummon.getCompanySummonsDto().setAddress(companySummon.getAddress());
					savedSummon.getCompanySummonsDto().setEmail(companySummon.getEmail());
					savedSummon.getCompanySummonsDto().setCompanyName(companySummon.getCompanyName());

					savedSummon.getCompanySummon().get(i).setCin(summonDetails.getCompanySummonsDto().getCin());
					savedSummon.getCompanySummon().get(i).setAddress(summonDetails.getCompanySummonsDto().getAddress());
					summonDetails.setCompanySummonsDto(savedSummon.getCompanySummon().get(i));
					
					break;

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
				savedSummon.getCompanySummonsDto()
						.setCompanyType(summonDetails.getCompanySummonsDto().getCompanyType());
			}
			addNoticeSummonRepository.save(savedSummon);
			for (int i = 0; i < savedSummon.getCompanySummon().size(); i++) {
				if (savedSummon.getCompanySummon().get(i).getCompanyName()
						.equalsIgnoreCase(summonDetails.getCompanySummonsDto().getCompanyName())) {
					
					summonDetails.setCompanySummonsDto(savedSummon.getCompanySummon().get(i));
					
					break;

				}
			}
			//summonDetails.setCompanySummonsDto(savedSummon.getCompanySummonsDto());
		}
			else{
				savedSummon = new SummonDetails();
				savedSummon.setCaseId(summonDetails.getCaseId());
				
				savedSummon.setCreatedDate(new Date());
				savedSummon.setUserid(getUserDetails().getUserId());
				
				savedSummon.getCompanySummon().add(companySummon);
				
				
				
				BeanUtils.copyProperties(summonDetails.getCompanySummonsDto(),companySummon);
				addNoticeSummonRepository.save(savedSummon);
				for (int i = 0; i < savedSummon.getCompanySummon().size(); i++) {
					if (savedSummon.getCompanySummon().get(i).getCompanyName()
							.equalsIgnoreCase(summonDetails.getCompanySummonsDto().getCompanyName())) {
		
						SummonDetails summonDetails1 = addNoticeSummonRepository.findAllByCaseId(summonDetails.getCaseId());
					      summonDetails.setCompanySummonsDto(summonDetails1.getCompanySummon().get(i));
					      break;
					}
				}
				
			}
		
		PersonDetails persondetail = addPersonRepo.findAllByPanNumber(summonDetails.getPersonDetails().getPanNumber());
		UserDetails userDetails = userDetailsService.getUserDetailssss();
		boolean panexist = false;
		if (persondetail != null) {
			panexist = true;
		}
		CaseDetails cd = CaseDetailsRepo.findById(summonDetails.getCaseId()).get();
		CompanySummon compsumid = summonDetails.getCompanySummonsDto();
    	CompanySummon compsumm =	companySummonRepo.findAllById(compsumid.getId());
		RelationpersonCompany personcomp = rpcRepo.findAllByPersonDetailsAndCompanySummonAndCaseDetails(persondetail,compsumm,cd);
		//boolean personcompresent = false;
		if(personcomp!=null) {
			modelMap.addAttribute("personmessage", summonDetails.getPersonDetails().getFullName()+" is Existing in current list corresponding to company "+compsumm.getCompanyName()+" . Hence cannot be added " );
			 //summonDetails.setEditperson(true);
	           List<CompanyType> compTypeList = companyTypeRepo.findAll();
				modelMap.addAttribute("compTypeList", compTypeList);
				List<AddDesignation> designationList = designationService.findDesignationBytype(new AddDesignation());
				
				
				List<InvestigationStatus> StatusList =null;
				String role=roleDAO.getRoleName(userDetailsService.getUserDetails().getUserId());
				if(role.equals("ROLE_USER")) {
				StatusList = addStatusRepository.findAllBytype("I");
				}else {
					StatusList = addStatusRepository.findAllBytype("P");
				}
			
				List<PersonDetails> pd1 = addPersonRepo.findAllByCompanypersonCaseDetails(caseDetailsRepository.findById(summonDetails.getCaseId()),Sort.by(Sort.Direction.DESC, "createdDate"));
				HashSet<PersonDetails> pd = new HashSet<PersonDetails>(pd1);
				List<Country> cL = countryRepo.findAll();
				modelMap.addAttribute("Country", cL);
				List<personcompanyApproval> pca = pcaRepo.findAll();
				System.out.println("size of list" + pca.size());
				modelMap.addAttribute("designationList", designationList);
				modelMap.addAttribute("compTypeList", compTypeList);
			    modelMap.addAttribute("statusLst", StatusList);
				
				modelMap.addAttribute("pesoncompList", pd);
				modelMap.addAttribute("pcaList", pca); 
				modelMap.addAttribute("summonDetails", summonDetails);
				return "person/addPersons";
		}
		if (panexist) {
			// addPersonRepo.save(persondetail);
		   persondet = addPersonRepo.findAllByPanNumber(summonDetails.getPersonDetails().getPanNumber());
			

			//CaseDetails cd = CaseDetailsRepo.findById(summonDetails.getCaseId()).get();
			
			rpc.setId(0);
			rpc.setCaseDetails(cd);
			
			rpc.setCompanySummon(compsumm);
		    persondet.setIsUpdated(false);
			persondet.setPersonID(persondetail.getPersonID());
			persondet.setUpdatedDate(new Date());
			persondet.setUpdatedBy(userDetails);
			if(summonDetails.getPersonDetails().getPassportNumber()!=null) {
			    persondet.setPassportNumber(summonDetails.getPersonDetails().getPassportNumber());
			    persondet.setPassportDate(summonDetails.getPersonDetails().getPassportDate());
				}
				
				
				if(summonDetails.getPersonDetails().getVoterId()!=null) {
					persondet.setVoterId(summonDetails.getPersonDetails().getVoterId());
				}
				
				if(summonDetails.getPersonDetails().getIsDummyValue().equalsIgnoreCase("Y"))
			    {
			    	persondet.setIsDummy(true);
			    }
			  else 
			  {
				persondet.setIsDummy(false);
			 }
				
				
			addPersonRepo.save(persondet);
			persondet.getCompanyperson().add(rpc);
			rpc.setPersonDetails(persondet);
			rpc.setStatus(summonDetails.getPersonDetails().getCompDto().getPca().getStatus());
			rpcRepo.save(rpc);

			//addPersonRepo.save(persondet);

		} else {
			persondet.setAddress(summonDetails.getPersonDetails().getAddress());
			persondet.setFullName(summonDetails.getPersonDetails().getFullName());
			persondet.setGender(summonDetails.getPersonDetails().getGender());
			persondet.setRelation(summonDetails.getPersonDetails().getRelation());
			persondet.setRelationName(summonDetails.getPersonDetails().getRelationName());
			SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");  
			   String strDate = formatter.format(summonDetails.getPersonDetails().getDob());  
			   formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
			   Date date1 = formatter.parse(strDate);
			persondet.setDob(date1);
			persondet.setEmail(summonDetails.getPersonDetails().getEmail());
			persondet.setAlternateNo(summonDetails.getPersonDetails().getAlternateNo());
			persondet.setPrimaryMobile(summonDetails.getPersonDetails().getPrimaryMobile());
			persondet.setPanNumber(summonDetails.getPersonDetails().getPanNumber());
			if(summonDetails.getPersonDetails().getPassportNumber()!=null) {
		    persondet.setPassportNumber(summonDetails.getPersonDetails().getPassportNumber());
		    persondet.setPassportDate(summonDetails.getPersonDetails().getPassportDate());
			}
			
			
			if(summonDetails.getPersonDetails().getVoterId()!=null) {
				persondet.setVoterId(summonDetails.getPersonDetails().getVoterId());
			}
			persondet.setIsApproved(false);
			persondet.setCreatedBy(userDetails);
			persondet.setUpdatedBy(userDetails);
			persondet.setCreatedDate(new Date());
		 //   persondet.setIsDummy(true);
			
		    if(summonDetails.getPersonDetails().getIsDummyValue().equalsIgnoreCase("Y"))
		    {
		    	persondet.setIsDummy(true);
		    }
		  else
		  {
			persondet.setIsDummy(false);
		 }
			
			addPersonRepo.save(persondet);
			
			rpc.setCaseDetails(cd);
			rpc.setStatus(summonDetails.getPersonDetails().getCompDto().getPca().getStatus());
			rpc.setCompanySummon(summonDetails.getCompanySummonsDto());
			persondet.getCompDto().setCaseDetails(rpc.getCaseDetails());
			persondet.getCompanyperson().add(rpc);
			rpc.setPersonDetails(persondet);
			rpcRepo.save(rpc);
			//addPersonRepo.save(persondet);
		}
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
				utils.getMessage("log.person.reg"), summonDetails.getPersonDetails().getFullName() + " " + utils.getMessage("log.person.regdesc"),
				loginUName, "true");
		auditBeanBo.save();
		SummonDetails findByCaseId = summonDao.findByCaseId(summonDetails.getCaseId());
		SummonDetails summonDetails1 = new SummonDetails();
		if (findByCaseId != null) {
			commonObjectSummon(modelMap, summonDetails.getCaseId());
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
		
		List<AddDesignation> designationList = designationService.findDesignationBytype(new AddDesignation());
			
		List<CompanyType> compTypeList = companyTypeRepo.findAll();
		List<InvestigationStatus> StatusList = addStatusRepository.findAll();
		summonDetails1.setCaseId(summonDetails.getCaseId());
		personcompanyApproval pca = new personcompanyApproval();
		List<PersonDetails> pd1 = addPersonRepo.findAllByCompanypersonCaseDetails(caseDetailsRepository.findById(summonDetails.getCaseId()),Sort.by(Sort.Direction.DESC, "createdDate"));
		HashSet<PersonDetails> pd = new HashSet<PersonDetails>(pd1);
	    modelMap.addAttribute("pesoncompList", pd);
		modelMap.addAttribute("pcaList", pca); 
	//   List<personcompanyApproval>	pclist = pcaRepo.findAllByRpcAndApproved_statusAndIsApproved_stage2(rpc,2,true);
		pca.setCreatedBy(userDetails);
		pca.setApproved_status(1);
		pca.setCreatedDate(new Date());
		pca.setIsApproved_stage2(false);
		pca.setStatus(summonDetails.getPersonDetails().getCompDto().getPca().getStatus());
		pca.setRpc(rpc);
		pcaRepo.save(pca);
		//List<AddDesignation> designationList = designationRepo.findDesignationBytype("C", "S");
		modelMap.addAttribute("compTypeList", compTypeList);
		//List<InvestigationStatus> StatusList = addStatusRepository.findAllBytype("I");
		modelMap.addAttribute("message", "Person " +persondet.getFullName() +" added Successfully" );
		modelMap.addAttribute("designationList", designationList);
	    modelMap.addAttribute("statusLst", StatusList);
		return "person/addPersons";
	
	
	}

	
	// When Edit Button clicked, This Method Call
@RequestMapping(value = "personcomEdit")
public String editCompPerson(SummonDetails summonDetails ,ModelMap modelMap ) throws Exception {
	
	SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a", Locale.ENGLISH);
	String dateInString1 = "2022-06-12 10:15:55 AM"; 
	Date validdate1 = formatter1.parse(dateInString1);		
	summonDetails.setValidDate(validdate1);
	modelMap.addAttribute("validdate", validdate1);
	
	System.out.println("prcid "+summonDetails.getPersonDetails().getCompDto().getId());
	commonObjectSummon(modelMap,summonDetails.getCaseId());
	SummonDetails findByCaseId;
	SNMSValidator snmsValid = new SNMSValidator();
	if(summonDetails != null) 
	 findByCaseId=summonDao.findByCaseId(summonDetails.getCaseId());
	else
	findByCaseId=summonDao.findByCaseId(new SummonDetails().getCaseId());
	
   RelationpersonCompany rpc = 	rpcRepo.findAllById(summonDetails.getPersonDetails().getCompDto().getId());
   
   summonDetails.getPersonDetails().setAddress(rpc.getPersonDetails().getAddress());
   summonDetails.getPersonDetails().setAlternateNo(rpc.getPersonDetails().getAlternateNo());
   
   SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");  
   String strDate = formatter.format(rpc.getPersonDetails().getDob());  
   formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
   Date date1 = formatter.parse(strDate);
   
   summonDetails.getPersonDetails().setDob(date1);
   summonDetails.getPersonDetails().setEmail(rpc.getPersonDetails().getEmail());
   summonDetails.getPersonDetails().setFullName(rpc.getPersonDetails().getFullName());
   summonDetails.getPersonDetails().setGender(rpc.getPersonDetails().getGender());
   summonDetails.getPersonDetails().setPassportNumber(rpc.getPersonDetails().getPassportNumber());
   summonDetails.getPersonDetails().setCountry(rpc.getPersonDetails().getCountry());
   summonDetails.getPersonDetails().setPanNumber(rpc.getPersonDetails().getPanNumber());
   summonDetails.getPersonDetails().setPrimaryMobile(rpc.getPersonDetails().getPrimaryMobile());
   summonDetails.getPersonDetails().setPersonID(rpc.getPersonDetails().getPersonID());
   summonDetails.getPersonDetails().setIsApproved(rpc.getPersonDetails().getIsApproved());
   summonDetails.getPersonDetails().setVoterId(rpc.getPersonDetails().getVoterId());
   summonDetails.getPersonDetails().setRelationName(rpc.getPersonDetails().getRelationName());
   summonDetails.getPersonDetails().setPassportDate(rpc.getPersonDetails().getPassportDate());
   CaseDetails cd = CaseDetailsRepo.findById(summonDetails.getCaseId()).get();
   summonDetails.getPersonDetails().getCompDto().setCaseDetails(cd);
   summonDetails.getPersonDetails().getCompDto().setDateAppointment(rpc.getDateAppointment());
   summonDetails.getPersonDetails().getCompDto().setDateCessation(rpc.getDateCessation());
   summonDetails.getPersonDetails().getCompDto().setDesignation(rpc.getDesignation());
   summonDetails.getPersonDetails().getCompDto().setDin(rpc.getDin());
   summonDetails.getPersonDetails().getCompDto().setFrn(rpc.getFrn());
   summonDetails.getPersonDetails().getCompDto().setId(rpc.getId());
   summonDetails.getPersonDetails().getCompDto().setIec(rpc.getIec());
   summonDetails.getPersonDetails().getCompDto().setIsApproved(rpc.getIsApproved());
   summonDetails.getPersonDetails().getCompDto().setStatus(rpc.getStatus());
   summonDetails.getPersonDetails().getCompDto().setPersonRemark(rpc.getPersonRemark());
   summonDetails.getPersonDetails().getCompDto().setSummon_type_id(rpc.getSummon_type_id());
   summonDetails.getPersonDetails().getCompDto().setCompanySummon(rpc.getCompanySummon());
	
   summonDetails.getPersonDetails().getCompDto().setIsApprovedstage2(rpc.getIsApprovedstage2());
      summonDetails.getPersonDetails().getCompDto().setStatus(rpc.getStatus());
      
      
//     if( summonDetails.getPersonDetails().getIsDummyValue().equalsIgnoreCase("Y"))
//     {
      summonDetails.getPersonDetails().setIsDummy(rpc.getPersonDetails().getIsDummy());
//     }
  
      
      List<Country> cL = countryRepo.findAll();
		modelMap.addAttribute("Country", cL);
      List<InvestigationStatus> StatusList =null;
      String role=roleDAO.getRoleName(userDetailsService.getUserDetails().getUserId());
		if(role.equals("ROLE_USER")) {
		StatusList = addStatusRepository.findAllBytype("I");
		}else {
			StatusList = addStatusRepository.findAllBytype("P");
		}
	List<AddDesignation> designationList = designationRepo.findDesignationBytype("C", "S");
	modelMap.addAttribute("designationList", designationList);
    modelMap.addAttribute("statusLst", StatusList);
   modelMap.addAttribute("summonDetails",summonDetails);
	return "person/editpersonCompDetails";
	
}
	
	public void commonObjectSummon(ModelMap modelMap, Long caseId) {

		SNMSValidator snmsvalid = new SNMSValidator();
		if (!snmsvalid.getValidInteger(caseId)) {
			String valid = "invalid case id";

		}

		Optional<CaseDetails> details = caseDetailsRepository.findById(caseId);
		CaseDetails caseDetails = details.get();
		OfficeOrder officeorder = officeOrderRepo.findAllByCaseDetails(caseDetails);
		//if(officeorder!=null) {
		modelMap.addAttribute("officeorder", officeorder);
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

	}

	/*
	 * @RequestMapping(value = "/getpersonListBycompany") public @ResponseBody
	 * List<PersonDATA> getpersonListBycompany(
	 * 
	 * @RequestParam(name = "caseId", required = true) Long id,
	 * 
	 * @RequestParam(name = "company", required = true) String company) throws
	 * Exception { company = company.replaceAll("@and@", "&"); List<Object[]> list =
	 * summonDao.findListByIdCompany(id, company);
	 * 
	 * System.out.println(list.size()); List<PersonDATA> personlist = new
	 * ArrayList<PersonDATA>(); String passport = ""; String pan = ""; if
	 * (list.size() > 0) { for(int i=0;i<list.size();i++) {
	 * System.out.println(list.toString()); } for (Object[] obj : list)
	 * 
	 * personlist.add(new PersonDATA(obj[0].toString(),
	 * String.valueOf(obj[1]),String.valueOf(obj[2]), obj[3].toString(),
	 * obj[4].toString(), Integer.parseInt(obj[5].toString())));
	 * 
	 * }
	 * 
	 * return personlist;
	 * 
	 * }
	 */

	@RequestMapping(value = "/getpersonListBycompany")
	public @ResponseBody List<Object[]> getpersonListBycompany(@RequestParam(name = "caseId", required = true) Long id,
			@RequestParam(name = "company", required = true) String company) throws Exception {
		company = company.replaceAll("@and@", "&");
		List<Object[]> list = summonDao.findListByIdCompany(id, company);

		System.out.println(list.size());
		List<PersonDATA> personlist = new ArrayList<PersonDATA>();
		String passport = "";
		String pan = "";
		if (list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				System.out.println(list.toString());
			}
			/*
			 * for (Object[] obj : list)
			 * 
			 * personlist.add(new PersonDATA(obj[0].toString(),
			 * String.valueOf(obj[1]),String.valueOf(obj[2]), obj[3].toString(),
			 * obj[4].toString(), Integer.parseInt(obj[5].toString())));
			 */
		}

		return list;

	}
	
	
	@RequestMapping(value = "/showPendpersonStage1") // person stage one for ado
	public String showPendingNoticeStage1(Model model) throws Exception {

//	
//		List<Object[]> list = userMangCustom.findOfficeOrderPendingForApproval(1);
	//	List<Object[]> gamspersonlist = userMangCustom.findpersonPendingForApproval(1,userDetailsService.getUserDetails().getUserId());
		//List<RelationpersonCompany> gamspersonlist = null;
		List<personcompanyApproval> gamspersonlist = null;
		String role=roleDAO.getRoleName(userDetailsService.getUserDetails().getUserId());
		if(role.equals("ROLE_USER")) {
		 gamspersonlist  =userMangCustom.findpersonPendingListForApproval(1, userDetailsService.getUserDetails().getUserId(),1);
		}else {
		 gamspersonlist  =userMangCustom.findpersonPendingListForProApproval(1, userDetailsService.getUserDetails().getUserId(),2);
		}
		/*List<pendingPersonDto> pending = new ArrayList<pendingPersonDto>();
		for (Object[] dto : gamspersonlist) {
			pending.add(new pendingPersonDto( dto[0].toString(),
					dto[1].toString(), dto[2].toString(),dto[3].toString(), Integer.valueOf(dto[4].toString()) ));
		}*/

		model.addAttribute("pending", gamspersonlist);
		CaseDetails caseDetails = new CaseDetails();
		model.addAttribute("caseDetails", caseDetails);
		return "ado/personPending";
	}
	
	@RequestMapping(value = "/showApprovedepersonStage1") // person stage one for ado
	public String showApprovedepersonStage1(Model model) throws Exception {
	List<personcompanyApproval> gamspersonlist = null;
		String role=roleDAO.getRoleName(userDetailsService.getUserDetails().getUserId());
		if(role.equals("ROLE_USER")) {
		 gamspersonlist  =userMangCustom.findpersonApprovedListForApproval(1, userDetailsService.getUserDetails().getUserId(),1);
		}else {
		 gamspersonlist  =userMangCustom.findpersonPendingListForProApproval(1, userDetailsService.getUserDetails().getUserId(),2);
		}
		
		model.addAttribute("pending", gamspersonlist);
		CaseDetails caseDetails = new CaseDetails();
		model.addAttribute("caseDetails", caseDetails);
		return "ado/personApproved";
	}
	@RequestMapping(value = "/showPendperson") // person stage one for ado
	public String showPendperson(Model model) throws Exception {

//	
//		List<Object[]> list = userMangCustom.findOfficeOrderPendingForApproval(1);
	//	List<Object[]> gamspersonlist = userMangCustom.findpersonPendingForApproval(1,userDetailsService.getUserDetails().getUserId());
		List<RelationpersonCompany> gamsPendingpersonlist = null;
		String role=roleDAO.getRoleName(userDetailsService.getUserDetails().getUserId());
		if(role.equals("ROLE_USER")) {
			gamsPendingpersonlist  =userMangCustom.findpersonPendingListByUserID(1, userDetailsService.getUserDetails().getUserId(),1);
			
		}else {
			 gamsPendingpersonlist  =userMangCustom.findpersonPendingListByUserID(1, userDetailsService.getUserDetails().getUserId(),2);
			
		}
		/*List<pendingPersonDto> pending = new ArrayList<pendingPersonDto>();
		for (Object[] dto : gamspersonlist) {
			pending.add(new pendingPersonDto( dto[0].toString(),
					dto[1].toString(), dto[2].toString(),dto[3].toString(), Integer.valueOf(dto[4].toString()) ));
		}*/

		
		model.addAttribute("PersonList","Total KMP's, SA's and Director's Pending Status List" );
		model.addAttribute("pending", gamsPendingpersonlist);
		CaseDetails caseDetails = new CaseDetails();
		model.addAttribute("caseDetails", caseDetails);
		return "person/personList";
	}
	@RequestMapping(value = "/showTotalpersonStage1") // person stage one for ado
	public String showTotalpersonStage1(Model model) throws Exception {

		List<RelationpersonCompany> gamspersonlist = null;
		String role=roleDAO.getRoleName(userDetailsService.getUserDetails().getUserId());
		if(role.equals("ROLE_USER")) {
		 gamspersonlist  =userMangCustom.findTotalpersonListForProApproval(1, userDetailsService.getUserDetails().getUserId(),1);
		}else {
		 gamspersonlist  =userMangCustom.findTotalpersonListForProApproval(1, userDetailsService.getUserDetails().getUserId(),2);
		}
		/*List<pendingPersonDto> pending = new ArrayList<pendingPersonDto>();
		for (Object[] dto : gamspersonlist) {
			pending.add(new pendingPersonDto( dto[0].toString(),
					dto[1].toString(), dto[2].toString(),dto[3].toString(), Integer.valueOf(dto[4].toString()) ));
		}*/
        model.addAttribute("PersonList","Total KMP's, SA's and Director's Status List" );
		model.addAttribute("pending", gamspersonlist);
		CaseDetails caseDetails = new CaseDetails();
		model.addAttribute("caseDetails", caseDetails);
		return "person/personList";
	}
	
	
	
	//
	
	@RequestMapping(value = "/showApprovedpersonStage1") // person stage one for ado
	public String showApprovedpersonStage1(Model model) throws Exception {

		List<RelationpersonCompany> gamspersonlist = null;
		String role=roleDAO.getRoleName(userDetailsService.getUserDetails().getUserId());
		if(role.equals("ROLE_USER")) {
		 gamspersonlist  =userMangCustom.findpersonAorrovedListForProApproval(1, userDetailsService.getUserDetails().getUserId(),1);
		}
		else if(role.equals("ROLE_PROSECUTION")) {
			gamspersonlist  =userMangCustom.findApprovePersonListByUserID(1, userDetailsService.getUserDetails().getUserId(),2);
					
		}
		
		else {
		 gamspersonlist  =userMangCustom.findpersonAorrovedListForProApproval(1, userDetailsService.getUserDetails().getUserId(),2);
		}
		/*List<pendingPersonDto> pending = new ArrayList<pendingPersonDto>();
		for (Object[] dto : gamspersonlist) {
			pending.add(new pendingPersonDto( dto[0].toString(),
					dto[1].toString(), dto[2].toString(),dto[3].toString(), Integer.valueOf(dto[4].toString()) ));
		}*/
		model.addAttribute("PersonList","Total KMP's, SA's and Director's Approved Status List" );
		model.addAttribute("pending", gamspersonlist);
		CaseDetails caseDetails = new CaseDetails();
		model.addAttribute("caseDetails", caseDetails);
		return "person/personList";
	}
	
	
	// when click Save on "Update" Button at Update Details Time(Edit DropDown)
	@RequestMapping(value = "updatePersoncompDetails")
	public String updatePersoncomp(@ModelAttribute("summonDetails") @Valid SummonDetails summonDetails,
			BindingResult result, ModelMap modelMap) throws Exception {
		
		SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a", Locale.ENGLISH);
		String dateInString1 = "2022-06-12 10:15:55 AM"; 
		Date validdate1 = formatter1.parse(dateInString1);		
		summonDetails.setValidDate(validdate1);
		modelMap.addAttribute("validdate", validdate1);
	

		personValidation perVal = new personValidation();
		
		SNMSValidator snmsvalid =  new SNMSValidator();
		
           	
	
	   RelationpersonCompany rpcq = rpcRepo.findAllById(summonDetails.getPersonDetails().getCompDto().getId());
	   if(rpcq.getPersonDetails().getIsApproved()==false) {
	   
		if(summonDetails.getPersonDetails().getVoterId()!="") {
			PersonDetails per =addPersonRepo.findAllByVoterId(summonDetails.getPersonDetails().getVoterId());
			
			if(per!=null) {
				if(!per.getPanNumber().equals(rpcq.getPersonDetails().getPanNumber())) {
					result.rejectValue("personDetails.voterId", "msg.wrongvoterId");
				}
			}
		}
		if(summonDetails.getPersonDetails().getPassportNumber()!="") {
			if(summonDetails.getPersonDetails().getCountry()==null) {
				result.rejectValue("personDetails.country", "msg.wrongId");
			}
			PersonDetails per =addPersonRepo.findAllByPassportNumber(summonDetails.getPersonDetails().getPassportNumber());
			
			if(per!=null) {
				if(!per.getPanNumber().equals(rpcq.getPersonDetails().getPanNumber())) {
					result.rejectValue("personDetails.passportNumber", "msg.wrongPassportNumber");
				}
			}
		}
	   }
		/*
		 * if(rpcq.getIsApproved()==false) {
		 * perVal.validateEditcomp1(summonDetails,result); }
		 */
	   
	   
	   if(rpcq.getPersonDetails().getIsApproved()==false) {
		   perVal.validateEditPerson1(summonDetails,result);
	   }
	   else {
		   perVal.validateEditcomp1(summonDetails,result); 
	   }
		// sumVal.validateIndivialType(summonDetails, result);
		if (result.hasErrors()) {
			System.out.println("prcid "+summonDetails.getPersonDetails().getCompDto().getId());
			commonObjectSummon(modelMap,summonDetails.getCaseId());
			SummonDetails findByCaseId;
			SNMSValidator snmsValid = new SNMSValidator();
			if(summonDetails != null) 
			 findByCaseId=summonDao.findByCaseId(summonDetails.getCaseId());
			else
			findByCaseId=summonDao.findByCaseId(new SummonDetails().getCaseId());
			
			
			List<Country> cL = countryRepo.findAll();
			modelMap.addAttribute("Country", cL);
				
			
		   RelationpersonCompany rpc = 	rpcRepo.findAllById(summonDetails.getPersonDetails().getCompDto().getId());
		   
		   summonDetails.getPersonDetails().setAddress(rpc.getPersonDetails().getAddress());
		   summonDetails.getPersonDetails().setAlternateNo(rpc.getPersonDetails().getAlternateNo());
		   SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");  
		   String strDate = formatter.format(rpc.getPersonDetails().getDob());  
		   formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
		   Date date1 = formatter.parse(strDate);
		   
		   summonDetails.getPersonDetails().setDob(date1);
		   summonDetails.getPersonDetails().setEmail(rpc.getPersonDetails().getEmail());
		   summonDetails.getPersonDetails().setFullName(rpc.getPersonDetails().getFullName());
		   summonDetails.getPersonDetails().setGender(rpc.getPersonDetails().getGender());
		   summonDetails.getPersonDetails().setPassportNumber(rpc.getPersonDetails().getPassportNumber());
		   summonDetails.getPersonDetails().setPanNumber(rpc.getPersonDetails().getPanNumber());
		   summonDetails.getPersonDetails().setPrimaryMobile(rpc.getPersonDetails().getPrimaryMobile());
		   summonDetails.getPersonDetails().setPersonID(rpc.getPersonDetails().getPersonID());
		   summonDetails.getPersonDetails().setIsApproved(rpc.getPersonDetails().getIsApproved());
		   summonDetails.getPersonDetails().setVoterId(rpc.getPersonDetails().getVoterId());
		   summonDetails.getPersonDetails().setRelationName(rpc.getPersonDetails().getRelationName());
		   summonDetails.getPersonDetails().setPassportDate(rpc.getPersonDetails().getPassportDate());
		   CaseDetails cd = CaseDetailsRepo.findById(summonDetails.getCaseId()).get();
		   summonDetails.getPersonDetails().getCompDto().setCaseDetails(cd);
		   summonDetails.getPersonDetails().getCompDto().setDateAppointment(rpc.getDateAppointment());
		   summonDetails.getPersonDetails().getCompDto().setDateCessation(rpc.getDateCessation());
		   summonDetails.getPersonDetails().getCompDto().setDesignation(rpc.getDesignation());
		   summonDetails.getPersonDetails().getCompDto().setDin(rpc.getDin());
		   summonDetails.getPersonDetails().getCompDto().setFrn(rpc.getFrn());
		   summonDetails.getPersonDetails().getCompDto().setId(rpc.getId());
		   summonDetails.getPersonDetails().getCompDto().setIec(rpc.getIec());
		   summonDetails.getPersonDetails().getCompDto().setIsApproved(rpc.getIsApproved());
		   summonDetails.getPersonDetails().getCompDto().setIsApproved(rpc.getIsApprovedstage2());
		  
		   summonDetails.getPersonDetails().getCompDto().setStatus(rpc.getStatus());
		   summonDetails.getPersonDetails().getCompDto().setSummon_type_id(rpc.getSummon_type_id());
		   summonDetails.getPersonDetails().getCompDto().setCompanySummon(rpc.getCompanySummon());
			
			List<InvestigationStatus> StatusList = addStatusRepository.findAll();
			List<AddDesignation> designationList = designationService.findDesignationBytype(new AddDesignation());
			
			modelMap.addAttribute("designationList", designationList);
		    modelMap.addAttribute("statusLst", StatusList);
		   modelMap.addAttribute("summonDetails",summonDetails);
			return "person/editpersonCompDetails";
		}
	
		PersonDetails persondet =  addPersonRepo.findAllByPersonID(summonDetails.getPersonDetails().getPersonID());
		if(summonDetails.getPersonDetails().getIsApproved()!=true) {
	//	persondet.setAddress(summonDetails.getPersonDetails().getAddress());
		
		persondet.setPersonID(summonDetails.getPersonDetails().getPersonID());
		persondet.setFullName(summonDetails.getPersonDetails().getFullName());
		persondet.setGender(summonDetails.getPersonDetails().getGender());
		persondet.setRelation(summonDetails.getPersonDetails().getRelation());
		persondet.setRelationName(summonDetails.getPersonDetails().getRelationName());
		persondet.setCountry(summonDetails.getPersonDetails().getCountry());
		persondet.setPanNumber(summonDetails.getPersonDetails().getPanNumber());
		SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");  
		   String strDate = formatter.format(summonDetails.getPersonDetails().getDob());  
		   formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
		   Date date1 = formatter.parse(strDate);
		persondet.setDob(date1);
		persondet.setEmail(summonDetails.getPersonDetails().getEmail());
		persondet.setAlternateNo(summonDetails.getPersonDetails().getAlternateNo());
		if(summonDetails.getPersonDetails().getPrimaryMobile()!="")
		persondet.setPrimaryMobile(summonDetails.getPersonDetails().getPrimaryMobile());
		if(summonDetails.getPersonDetails().getPassportNumber()!=null ||summonDetails.getPersonDetails().getPassportNumber()!="") {
		persondet.setPassportNumber(summonDetails.getPersonDetails().getPassportNumber());
		}
		if(summonDetails.getPersonDetails().getPassportDate()!=null)
			persondet.setPassportDate(summonDetails.getPersonDetails().getPassportDate());
		
		if(summonDetails.getPersonDetails().getVoterId()!=null)
			persondet.setVoterId(summonDetails.getPersonDetails().getVoterId());
		
		
		if(summonDetails.getPersonDetails().getIsDummy() == true)
		{
			persondet.setIsDummy(true);
		}
		else
		{
			persondet.setIsDummy(false);
		}
		 persondet.setIsUpdated(false);
		addPersonRepo.save(persondet);
		}
		
		
		 persondet.setIsUpdated(false);
		 addPersonRepo.save(persondet);
		 RelationpersonCompany rpc = rpcRepo.findAllById(summonDetails.getPersonDetails().getCompDto().getId());
		CaseDetails cd = CaseDetailsRepo.findById(summonDetails.getCaseId()).get();
		rpc.setCaseDetails(cd);
		rpc.setStatus(summonDetails.getPersonDetails().getCompDto().getStatus());
		//rpc.setCompanySummon(summonDetails.getCompanySummonsDto());
		rpc.setDateAppointment(summonDetails.getPersonDetails().getCompDto().getDateAppointment());
		rpc.setDateCessation(summonDetails.getPersonDetails().getCompDto().getDateCessation());
		rpc.setPersonRemark(summonDetails.getPersonDetails().getCompDto().getPersonRemark());
		   if(rpcq.getIsApproved()==false) {
			   rpc.setDesignation(rpcq.getDesignation());
		   }else {
		rpc.setDesignation(summonDetails.getPersonDetails().getCompDto().getDesignation());
		   }
		
		   
		  
		   
		rpc.setIec(summonDetails.getPersonDetails().getCompDto().getIec());
		rpc.setFrn(summonDetails.getPersonDetails().getCompDto().getFrn());
		rpc.setStatus(summonDetails.getPersonDetails().getCompDto().getStatus());
		 summonDetails.getPersonDetails().getCompDto().setIsApprovedstage2(false);
		rpc.setPersonDetails(persondet);
		rpc.setRejected(false);
		rpcRepo.save(rpc);
		
		List<personcompanyApproval> pca = pcaRepo.findAllByRpc(rpc);
		
		pca.get(0).setStatus(summonDetails.getPersonDetails().getCompDto().getStatus());
		pca.get(0).setRejected(false);
		pcaRepo.saveAll(pca);
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
				utils.getMessage("log.person.regupd"), summonDetails.getPersonDetails().getFullName() + " " + utils.getMessage("log.person.regupddesc"),
				loginUName, "true");
		auditBeanBo.save();
		SummonDetails findByCaseId = summonDao.findByCaseId(summonDetails.getCaseId());
		SummonDetails summonDetails1 = new SummonDetails();
		if (findByCaseId != null) {
			commonObjectSummon(modelMap, summonDetails.getCaseId());
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
		
		List<AddDesignation> designationList = designationService.findDesignationBytype(new AddDesignation());
			
		List<CompanyType> compTypeList = companyTypeRepo.findAll();
		List<InvestigationStatus> StatusList = addStatusRepository.findAll();
		summonDetails1.setCaseId(summonDetails.getCaseId());
		List<PersonDetails> pd1 = addPersonRepo.findAllByCompanypersonCaseDetails(caseDetailsRepository.findById(summonDetails.getCaseId()),Sort.by(Sort.Direction.DESC, "createdDate"));
		HashSet<PersonDetails> pd = new HashSet<PersonDetails>(pd1);
		
		pca = pcaRepo.findAll(); 
		System.out.println("size of list" + pca.size());
		modelMap.addAttribute("designationList", designationList);
		modelMap.addAttribute("compTypeList", compTypeList);
	    modelMap.addAttribute("statusLst", StatusList);
		modelMap.addAttribute("summonDetails", summonDetails1);
		modelMap.addAttribute("pesoncompList", pd);
		modelMap.addAttribute("pcaList", pca);
		
		
		
		modelMap.addAttribute("message", "Person "+rpc.getPersonDetails().getFullName()+" status has been updated to  "+rpc.getStatus().getStatusName());
		return "person/caseStatusSuccess";
		//modelMap.addAttribute("message", " Person Updated Successfully as " +persondet.getFullName());
		//return "person/addPersons";
		
	}
	
	
	
	// update status (when click "Save" on edit button show "update Status"(but IsApproved is true))
	@RequestMapping(value = "updatePersoncomp" ,params="upstatus")
	public String updatePersoncompstatus(@ModelAttribute("summonDetails") @Valid SummonDetails summonDetails,
			BindingResult result, ModelMap modelMap) throws Exception {
		
		SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a", Locale.ENGLISH);
		String dateInString1 = "2022-06-12 10:15:55 AM"; 
		Date validdate1 = formatter1.parse(dateInString1);		
		summonDetails.setValidDate(validdate1);
		modelMap.addAttribute("validdate", validdate1);
		
		RelationpersonCompany rpc =   rpcRepo.findAllById(summonDetails.getPersonDetails().getCompDto().getId());
		
		  rpc.setStatus(summonDetails.getPersonDetails().getCompDto().getStatus());
		  rpc.setIsApprovedstage2(false);
		  rpc.setRejected(false);
		  rpcRepo.save(rpc);
		 PersonDetails  prc = addPersonRepo.findAllByPersonID(rpc.getPersonDetails().getPersonID());
		 prc.setIsUpdated(false);
		 prc.setPanNumber(summonDetails.getPersonDetails().getPanNumber());
		 
		 if(summonDetails.getPersonDetails().getIsDummy() == true)
		 {
			 prc.setIsDummy(true);
		 }
		 else
		 {
			 prc.setIsDummy(false);
		 }
		 addPersonRepo.save(prc);
		
		personcompanyApproval pca = new personcompanyApproval();
		List<personcompanyApproval>pca1= pcaRepo.findAllByRpc(rpc);
		
		
		  for (int i=0;i < pca1.size();i++) 
		  {
	   pca1.get(i).setApproved_status(1);
		 
		  pcaRepo.saveAll(pca1);
		  }
		 
		
		UserDetails userDetails = userDetailsService.getUserDetailssss();
		pca.setCreatedBy(userDetails);
		pca.setCreatedDate(new Date());
		pca.setStatus(summonDetails.getPersonDetails().getCompDto().getStatus());
		pca.setApproved_status(1);
		
		
		pca.setIsApproved_stage2(false);
		pca.setRpc(rpc);
		pcaRepo.save(pca);
		
		
		List<Country> cL = countryRepo.findAll();
		modelMap.addAttribute("Country", cL);
		modelMap.addAttribute("message", "Person "+rpc.getPersonDetails().getFullName()+" status has been updated to  "+rpc.getStatus().getStatusName());
		return "person/caseStatusSuccess";
		
	}
	
	@RequestMapping(value = "showpersonDetails", params = "approve")
	public String getPersonDetails(@RequestParam(value = "approve", required = true) int Id, ModelMap modelMap)
			throws Exception {
				
		//RelationpersonCompany rpc = rpcRepo.findAllById(Id);
		
		personcompanyApproval pca = pcaRepo.findAllById(Id);
		
		
		modelMap.addAttribute("pca", pca);
		return "ado/PendingPersonDetail";
		
	}
	
	
	@RequestMapping(value = "companyapproval")
	public String addpersonStatus(personcompanyApproval pca, ModelMap modelMap)
			throws Exception {
				
		
		
		 pca = pcaRepo.findAllById(pca.getId());
		 PersonDetails pd = addPersonRepo.findAllByPersonID(pca.getRpc().getPersonDetails().getPersonID());
			pd.setIsApproved(true);
			addPersonRepo.save(pd);
			
			RelationpersonCompany rpc = rpcRepo.findAllById(pca.getRpc().getId());
			rpc.setIsApprovedstage2(true);		
			rpc.setIsApproved(true);
			rpc.setRejected(false);
			rpcRepo.save(rpc);
		AppUser userDetails = userDetailsService.getUserDetails();
		  UserDetails user = userDetailsRepo.findById(userDetails.getUserId()).get();
		pca.setApproved_status(2);
		pca.setApprovedBY(userDetailsService.getFullName(user));
		pca.setApprovedDate(new Date());
		pca.setIsApproved_stage2(true);
		pcaRepo.save(pca);
		
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
				utils.getMessage("log.status.approve"), pca.getRpc().getPersonDetails().getFullName() + " " + utils.getMessage("log.status.approvedesc"),
				loginUName, "true");
		auditBeanBo.save();
		modelMap.addAttribute("pca", pca);
		modelMap.addAttribute("message","Details of "+pd.getFullName()+" is approved and added into centralised database");
	    return "person/apprPersonSuccess";
		
	}
	
	@RequestMapping(value = "getpanDetails", method = RequestMethod.POST)
	public @ResponseBody PersonDetails getlocaddress(@RequestParam("panNumber") String panNumber) throws Exception {
	PersonDetails pd = new PersonDetails();

		pd = addPersonRepo.findAllByPanNumber(panNumber);
	
		 
		if (pd != null) {
			SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");  
		    String strDate = formatter.format(pd.getDob());  
		    formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
		    Date date1 = formatter.parse(strDate);
		    System.out.println("Date Format with MM/dd/yyyy : "+strDate);  
		    pd.setDob(date1);
	
			System.out.println(pd.getDob());
			return pd;
	 	}
		return pd;
	}
	
	
	@RequestMapping(value = "getpanDetailsAccept", method = RequestMethod.POST)
	public @ResponseBody PersonDetails getpanavilabilty(@RequestParam("panNumber") String panNumber) throws Exception {
	PersonDetails pd = new PersonDetails();

		//pd = addPersonRepo.findAllByPanNumberAndIsApproved(panNumber,false);
	     pd = addPersonRepo.findAllByPanNumber(panNumber);
	
		 
		if (pd != null) {
			if(pd.getIsApproved()==true) {
				
			
			SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");  
		    String strDate = formatter.format(pd.getDob());  
		    formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
		    Date date1 = formatter.parse(strDate);
		    System.out.println("Date Format with MM/dd/yyyy : "+strDate);  
		    pd.setDob(date1);
	
			System.out.println(pd.getDob());
			return pd;
			}else {
				return pd;
			}
	 	}
		
		
		return pd;
	}
	@RequestMapping(value = "getCompanyType", method = RequestMethod.POST)
	public @ResponseBody List<CompanyType>  getCompanyType() throws Exception {
	PersonDetails pd = new PersonDetails();
	List<CompanyType> compTypeList = companyTypeRepo.findAll();
	
		
		return compTypeList;
	}
	
	
	
	@RequestMapping(value = "getpanDetailsPerson", method = RequestMethod.POST)
	public @ResponseBody PersonDetails getpanDetailsPerson(@RequestParam("panNumber") String panNumber,@RequestParam("compName") String compName) throws Exception {
	PersonDetails pd = new PersonDetails();

		pd = addPersonRepo.findAllByPanNumber(panNumber);
		if (pd != null) {
			System.out.println(pd.getDob());
			return pd;
			
			
		}
		return pd;
	}
	
	@RequestMapping(value = "updatecaseStatuspros",method=RequestMethod.POST)
	public String updateCaseStatus(@ModelAttribute("caseDetails") CaseDetails caseDetails,BindingResult bindResult, ModelMap modelmap) throws NumberFormatException, Exception {
		
		
		System.out.println(caseDetails.getCaseStatus());
		if(caseDetails.getCaseStatus().equals("Y")) {
			caseDetails = caseDetailsRepository.findById(caseDetails.getId()).get();
		//caseDetails.setId(caseDetails.getId());	
			caseDetails.setCaseStage(2);
		caseDetailsRepository.save(caseDetails);
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
				utils.getMessage("log.case.transferred"),  utils.getMessage("log.case.transferdesc")+" "+loginUName,
				loginUName, "true");
		auditBeanBo.save();
		modelmap.addAttribute("message", "Case"+caseDetails.getCaseTitle()+" transfered to Prosecution Stage");
		return "person/caseStatusSuccess";
		
		
		}else {
			/*
			 * Optional<CaseDetails> caseDe = caseDetailsRepository.findById(caseId);
			 * CaseDetails caseDetails = caseDe.get();
			 */
			modelmap.addAttribute("caseDetails", caseDetails);
			InspectorListDTO inspList = getInspectorListAndCompany(caseDetails.getId());
			/*
			 * if(inspList.getIoName()!=null) model.addAttribute("ioName",
			 * inspList.getIoName());
			 */
//			inspList.getInspctorList().removeIf( insp -> insp.isAdo()==true );
//			inspList.getCopyToList().removeIf( insp -> insp.isAdo()==true );
			bindResult.rejectValue("caseStatus", "msg.checkbox");
			modelmap.addAttribute("inspList", inspList.getInspctorList());
			modelmap.addAttribute("copyToList", inspList.getCopyToList());
			modelmap.addAttribute("comList", inspList.getCompanyList());
			return "person/CaseDetails";
		}
	}
	
	
	@RequestMapping(value = "/PersonStatus")
	public String PersonStatus (ModelMap modelMap) {
	    List<RelationpersonCompany> companyList = rpcRepo.findAll();
		
	    modelMap.addAttribute("PersonDetails",new PersonDetails());
	    
		modelMap.addAttribute("companyList",companyList);
	    return "person/PersonStatus";
	}
	
	@RequestMapping(value = "/CompanyStatusDetails")
	public String CompanyDetails(ModelMap modelMap , @RequestParam("compId") int compId) {
		RelationpersonCompany comp = rpcRepo.findById(compId).get();
		
		List<personcompanyApproval>  statusList  =  pcaRepo.findAllByRpc(comp ,Sort.by(Sort.Direction.DESC, "createdDate"));
		
		modelMap.addAttribute("companyStatusList", statusList);
		return "person/CompanyStatusDetails";
	}
	
	
	@RequestMapping(value = "/personalDetails")
	public String personalHome(ModelMap modelMap , @RequestParam("personId") int id) {
		PersonDetails PersonDetails = addPersonRepo.findAllByPersonID(id);
			     
		
		modelMap.addAttribute("applicantData", PersonDetails);
		return "person/personalDetails";
	}
	
	//reject	
	
	
	@RequestMapping(value="/PersonRejected")
	private String RejectPermontly(ModelMap modelMap ,@ModelAttribute("pca")  @Valid personcompanyApproval pca ) throws Exception {
		AppUser userDetails = userDetailsService.getUserDetails();
		  UserDetails user = userDetailsRepo.findById(userDetails.getUserId()).get();
		  
		  personcompanyApproval  pca1 = pcaRepo.findAllById(pca.getId());
		  pca1.setIsDeleted(true);
		  pca1.setApprovedDate(new Date());
		  pca1.setApprovedBY(userDetailsService.getFullName(user));
		  RelationpersonCompany rpc = rpcRepo.findAllById(pca.getRpc().getId());
		  rpc.setIsDeleted(true);
		  rpc.setRemark(pca.getRejectRemark());
		  rpc.setCaseDetails(rpc.getCaseDetails());
			rpcRepo.save(rpc);
			pcaRepo.save(pca1);
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
					utils.getMessage("log.status.rejected"), pca1.getRpc().getPersonDetails().getFullName() + " " + utils.getMessage("log.status.rejectedesc"),
					loginUName, "true");
			auditBeanBo.save();
			modelMap.addAttribute("pca", pca1);
			//modelMap.addAttribute("message","Person is Rejected  "+pca1.getRpc().getPersonDetails().getFullName());
			modelMap.addAttribute("message",pca1.getRpc().getPersonDetails().getFullName().toUpperCase()+" is Rejected  ");
			   
			return "person/apprPersonSuccess";
	}	
	
	/*
	@RequestMapping(value="/PersonReject" ,params="reject")
	private String RejectPermontly(ModelMap modelMap ,@ModelAttribute("pca")  @Valid personcompanyApproval pca ) throws Exception {
		AppUser userDetails = userDetailsService.getUserDetails();
		  UserDetails user = userDetailsRepo.findById(userDetails.getUserId()).get();
		  
		  personcompanyApproval  pca1 = pcaRepo.findAllById(pca.getId());
		  pca1.setIsDeleted(true);
		  pca1.setApprovedDate(new Date());
		  pca1.setApprovedBY(userDetailsService.getFullName(user));
		  RelationpersonCompany rpc = rpcRepo.findAllById(pca.getRpc().getId());
		  rpc.setIsDeleted(true);
		  rpc.setRemark(pca.getRejectRemark());
		  rpc.setCaseDetails(rpc.getCaseDetails());
			rpcRepo.save(rpc);
			pcaRepo.save(pca1);
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
					utils.getMessage("log.status.rejected"), pca1.getRpc().getPersonDetails().getFullName() + " " + utils.getMessage("log.status.rejectedesc"),
					loginUName, "true");
			auditBeanBo.save();
			modelMap.addAttribute("pca", pca1);
			//modelMap.addAttribute("message","Person is Rejected  "+pca1.getRpc().getPersonDetails().getFullName());
			modelMap.addAttribute("message",pca1.getRpc().getPersonDetails().getFullName().toUpperCase()+" is Rejected  ");
			   
			return "person/apprPersonSuccess";
	}*/
	
	
	
	@RequestMapping (value="/PersonSendBack")
	private String Reject(ModelMap modelMap ,@ModelAttribute("pca")  @Valid personcompanyApproval pca ) throws Exception {
		// pca = pcaRepo.findAllById(pca.getId());
		//System.out.println(pca.getId());
		
		
		AppUser userDetails = userDetailsService.getUserDetails();
		  UserDetails user = userDetailsRepo.findById(userDetails.getUserId()).get();
		  
		  personcompanyApproval  pca1 = pcaRepo.findAllById(pca.getId());
		pca1.setRejected(true);
		pca1.setApprovedDate(new Date());
		pca1.setApprovedBY(userDetailsService.getFullName(user));
	//	pca1.setRemark(pca.getRpc().getRemark());
		RelationpersonCompany rpc = rpcRepo.findAllById(pca.getRpc().getId());
		//String Remark = pca.getRemark();
		rpc.setRejected(true);
		rpc.setRemark(pca.getRemark());
		rpc.setCaseDetails(rpc.getCaseDetails());
		rpcRepo.save(rpc);
		pcaRepo.save(pca1);
		
		System.out.println(pca.getRpc().getId());
		
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
				utils.getMessage("log.status.rejected"), pca1.getRpc().getPersonDetails().getFullName() + " " + utils.getMessage("log.status.rejectedesc"),
				loginUName, "true");
		auditBeanBo.save();
		modelMap.addAttribute("pca", pca1);
		modelMap.addAttribute("message","Person Status is Send for Review: "+pca1.getRpc().getPersonDetails().getFullName());
	    return "person/apprPersonSuccess";
		
	}
	
	
	/*
	@RequestMapping(value="/PersonReject")
	
	private String Reject(ModelMap modelMap ,@ModelAttribute("pca")  @Valid personcompanyApproval pca ) throws Exception {
		// pca = pcaRepo.findAllById(pca.getId());
		//System.out.println(pca.getId());
		AppUser userDetails = userDetailsService.getUserDetails();
		  UserDetails user = userDetailsRepo.findById(userDetails.getUserId()).get();
		  
		  personcompanyApproval  pca1 = pcaRepo.findAllById(pca.getId());
		pca1.setRejected(true);
		pca1.setApprovedDate(new Date());
		pca1.setApprovedBY(userDetailsService.getFullName(user));
	//	pca1.setRemark(pca.getRpc().getRemark());
		RelationpersonCompany rpc = rpcRepo.findAllById(pca.getRpc().getId());
		//String Remark = pca.getRemark();
		rpc.setRejected(true);
		rpc.setRemark(pca.getRemark());
		rpc.setCaseDetails(rpc.getCaseDetails());
		rpcRepo.save(rpc);
		pcaRepo.save(pca1);
		
		System.out.println(pca.getRpc().getId());
		
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
				utils.getMessage("log.status.rejected"), pca1.getRpc().getPersonDetails().getFullName() + " " + utils.getMessage("log.status.rejectedesc"),
				loginUName, "true");
		auditBeanBo.save();
		modelMap.addAttribute("pca", pca1);
		modelMap.addAttribute("message","Person Status is Send for Review: "+pca1.getRpc().getPersonDetails().getFullName());
	    return "person/apprPersonSuccess";
		
	}
	*/
	@RequestMapping(value = "/CompanyDetails")
	public String ViewCompanyDetails(ModelMap modelMap , @RequestParam("compId") int compId) {
		
		RelationpersonCompany comp = rpcRepo.findById(compId).get();
		
		modelMap.addAttribute("companyData", comp);
		return "person/CompanyDetails";
	}
	public String getLoginUserName() {
		return SecurityContextHolder.getContext().getAuthentication().getName();
	}

	private AppUser getUserDetails() throws Exception {
		AppUser appUser = this.appUserDAO.findUserAccount(getLoginUserName());
		return appUser;
	}

	
	@RequestMapping(value = "updatePersoncomp", params = "caseDetails")
	public String getpercaseDetails(@RequestParam(value = "caseDetails", required = true) Long caseId, ModelMap modelMap)
			throws Exception {

				
		SNMSValidator snmsvalid = new SNMSValidator();
		List<Country> cL = countryRepo.findAll();
		modelMap.addAttribute("Country", cL);
		
		if (snmsvalid.getValidInteger(caseId)) {
			commonObjectSummon(modelMap, caseId);
			SummonDetails summonDetails1 = new SummonDetails();
			SummonDetails findByCaseId = summonDao.findByCaseId(caseId);
						

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
			
			List<AddDesignation> designationList = designationService.findDesignationBytype(new AddDesignation());
			
			List<CompanyType> compTypeList = companyTypeRepo.findAll();
			List<InvestigationStatus> StatusList =null;

			String role=roleDAO.getRoleName(userDetailsService.getUserDetails().getUserId());
			if(role.equals("ROLE_USER")) {
			StatusList = addStatusRepository.findAllBytype("I");
			}else {
				StatusList = addStatusRepository.findAllBytype("P");
			}
			
			
			SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a", Locale.ENGLISH);
			String dateInString1 = "2022-06-12 10:15:55 AM"; 
			Date validdate1 = formatter1.parse(dateInString1);		
			summonDetails1.setValidDate(validdate1);
			modelMap.addAttribute("validdate", validdate1);
			summonDetails1.setCaseId(caseId);
			//List<PersonDetails> pd1 = addPersonRepo.findAllByCompanypersonCaseDetails(caseDetailsRepository.findById(caseId));
			List<PersonDetails> pd1 = addPersonRepo.findAllByCompanypersonCaseDetails(caseDetailsRepository.findById(caseId),Sort.by(Sort.Direction.DESC, "createdDate"));
			
			  HashSet<PersonDetails> pd = new HashSet<PersonDetails>(pd1);
		//	List<PersonDetails> pd = addPersonRepo.findAll();
			List<personcompanyApproval> pca = pcaRepo.findAll();
			System.out.println("size of list" + pca.size());
			modelMap.addAttribute("designationList", designationList);
			modelMap.addAttribute("compTypeList", compTypeList);
		    modelMap.addAttribute("statusLst", StatusList);
			modelMap.addAttribute("summonDetails", summonDetails1);
			modelMap.addAttribute("pesoncompList", pd);
			modelMap.addAttribute("pcaList", pca);
			return "person/addPersons";
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

			return "person/assignedCase";
		}

	}
	
	@RequestMapping(value = "/addOtherDesignation",method = RequestMethod.GET,produces="application/json")
	public @ResponseBody List<AddDesignation> updateGep(
			Model model,@RequestParam("DesignationName") String  DesignationName) throws SnmsException, Exception {
		AddDesignation addDesignation = new AddDesignation();
		addDesignation.setDesignation(DesignationName);
		addDesignation.setType("C");
		designationRepo.save(addDesignation);
        	List<AddDesignation> addDesignationList = designationService.findDesignationBytype(new AddDesignation());
		
		 
		
		return addDesignationList;
	}
	
	
	@RequestMapping(value = "/GEPAsIoAdo")
	public String casesAsIoAdo(ModelMap modelMap) throws Exception {
		List<Object[]> penNotice = userMangCustom.findNoticePendingForApproval(1,
				userDetailsService.getUserDetails().getUserId());
		List<personcompanyApproval> gamspersonlist = userMangCustom.findpersonApprovedListForApproval(1,
				userDetailsService.getUserDetails().getUserId(),1);
		modelMap.addAttribute("totlagamsperson", gamspersonlist.size());
		//modelMap.addAttribute("totlagamsperson", gamspersonlist.size());
		//modelMap.addAttribute("totlaPendSummon", penSummon.size());
		return "ado/adlApprovedDashboard";
	}

	
	@RequestMapping(value = "/showpersonInfo")
	public String showpersonInfo(@RequestParam(name = "rpcId") int rpcId,ModelMap modelMap) throws Exception {
		System.out.println("relation person Id "+rpcId);
		RelationpersonCompany rpc = 	rpcRepo.findAllById(rpcId);
		 modelMap.addAttribute("rpc",rpc);
		return "ado/showpersonInfo";
	}
	@RequestMapping(value = "/getDesignation",method = RequestMethod.GET,produces="application/json")
	public @ResponseBody List<AddDesignation> getDesignation(
			Model model) throws SnmsException, Exception {
		List<AddDesignation> addDesignation = designationRepo.findDesignationBytype("C", "S");
		 
		
		return addDesignation;
	}
	
	
	
	
	
	
	
	
	
	@RequestMapping(value = "/getValidEmail", method = RequestMethod.POST, produces = "application/json")
	public @ResponseBody Boolean validEmagetValidPersonEmailil(@RequestParam("Email") String Email) {

		// String emailReg =
		// "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)$";
		// https://mkyong.com/regular-expressions/how-to-validate-email-address-with-regular-expression/
		String emailReg = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
		Pattern pattern = Pattern.compile(emailReg);
		Matcher matcher = pattern.matcher(Email);

		if (matcher.matches()) {
			return true;
		} else {
			return false;
		}
	}
	
	
	// Pan Number 
	@RequestMapping(value = "/getValidPan", method = RequestMethod.POST, produces = "application/json")
	public @ResponseBody Boolean getValidPan(@RequestParam("pan") String panNumber) {

		String panNumberReg = "^([A-Z]{5}[0-9]{4}[A-Z]{1})";
		Pattern pattern = Pattern.compile(panNumberReg);
		Matcher matcher = pattern.matcher(panNumber);

		if (matcher.matches()) {
			return true;
		} else {
			return false;
		}
		
		
		
	}
	
	
	// Edit Pan Number 
	@RequestMapping(value = "/addPersonNew", params = "editPan")
	public String editPanNumber(@RequestParam(value = "editPan", required = true) int rpcId, SummonDetails summonDetails, ModelMap modelMap) throws Exception {
		
		
		SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a", Locale.ENGLISH);
		String dateInString1 = "2022-06-12 10:15:55 AM"; 
		Date validdate1 = formatter1.parse(dateInString1);		
		summonDetails.setValidDate(validdate1);
		modelMap.addAttribute("validdate", validdate1);
		
	System.out.println("inside edit pan Number method");
	System.out.println("prcid "+summonDetails.getPersonDetails().getCompDto().getId());
	//commonObjectSummon(modelMap,summonDetails.getCaseId());
	SummonDetails findByCaseId;
	SNMSValidator snmsValid = new SNMSValidator();
	if(summonDetails != null) 
	 findByCaseId=summonDao.findByCaseId(summonDetails.getCaseId());
	else
	findByCaseId=summonDao.findByCaseId(new SummonDetails().getCaseId());
	
   RelationpersonCompany rpc = 	rpcRepo.findAllById(rpcId);
   
   summonDetails.getPersonDetails().setAddress(rpc.getPersonDetails().getAddress());
   summonDetails.getPersonDetails().setAlternateNo(rpc.getPersonDetails().getAlternateNo());
   
   SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");  
   String strDate = formatter.format(rpc.getPersonDetails().getDob());  
   formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
   Date date1 = formatter.parse(strDate);
   
   summonDetails.getPersonDetails().setDob(date1);
   summonDetails.getPersonDetails().setEmail(rpc.getPersonDetails().getEmail());
   summonDetails.getPersonDetails().setFullName(rpc.getPersonDetails().getFullName());
   summonDetails.getPersonDetails().setGender(rpc.getPersonDetails().getGender());
   summonDetails.getPersonDetails().setPassportNumber(rpc.getPersonDetails().getPassportNumber());
   summonDetails.getPersonDetails().setCountry(rpc.getPersonDetails().getCountry());
   summonDetails.getPersonDetails().setPanNumber(rpc.getPersonDetails().getPanNumber());
   summonDetails.getPersonDetails().setPrimaryMobile(rpc.getPersonDetails().getPrimaryMobile());
   summonDetails.getPersonDetails().setPersonID(rpc.getPersonDetails().getPersonID());
   summonDetails.getPersonDetails().setIsApproved(rpc.getPersonDetails().getIsApproved());
   summonDetails.getPersonDetails().setVoterId(rpc.getPersonDetails().getVoterId());
   summonDetails.getPersonDetails().setRelationName(rpc.getPersonDetails().getRelationName());
   summonDetails.getPersonDetails().setPassportDate(rpc.getPersonDetails().getPassportDate());
   CaseDetails cd = CaseDetailsRepo.findById(summonDetails.getCaseId()).get();
   summonDetails.getPersonDetails().getCompDto().setCaseDetails(cd);
   summonDetails.getPersonDetails().getCompDto().setDateAppointment(rpc.getDateAppointment());
   summonDetails.getPersonDetails().getCompDto().setDateCessation(rpc.getDateCessation());
   summonDetails.getPersonDetails().getCompDto().setDesignation(rpc.getDesignation());
   summonDetails.getPersonDetails().getCompDto().setDin(rpc.getDin());
   summonDetails.getPersonDetails().getCompDto().setFrn(rpc.getFrn());
   summonDetails.getPersonDetails().getCompDto().setId(rpc.getId());
   summonDetails.getPersonDetails().getCompDto().setIec(rpc.getIec());
   summonDetails.getPersonDetails().getCompDto().setIsApproved(rpc.getIsApproved());
   summonDetails.getPersonDetails().getCompDto().setStatus(rpc.getStatus());
   summonDetails.getPersonDetails().getCompDto().setPersonRemark(rpc.getPersonRemark());
   summonDetails.getPersonDetails().getCompDto().setSummon_type_id(rpc.getSummon_type_id());
   summonDetails.getPersonDetails().getCompDto().setCompanySummon(rpc.getCompanySummon());
	
   summonDetails.getPersonDetails().getCompDto().setIsApprovedstage2(rpc.getIsApprovedstage2());
      summonDetails.getPersonDetails().getCompDto().setStatus(rpc.getStatus());
      
      

      summonDetails.getPersonDetails().setIsDummy(rpc.getPersonDetails().getIsDummy());		

  
      
      List<Country> cL = countryRepo.findAll();
		modelMap.addAttribute("Country", cL);
      List<InvestigationStatus> StatusList =null;
      String role=roleDAO.getRoleName(userDetailsService.getUserDetails().getUserId());
		if(role.equals("ROLE_USER")) {
		StatusList = addStatusRepository.findAllBytype("I");
		}else {
			StatusList = addStatusRepository.findAllBytype("P");
		}
	List<AddDesignation> designationList = designationRepo.findDesignationBytype("C", "S");
        
		
        
     
   modelMap.addAttribute("caseDetails", cd);
   modelMap.addAttribute("summonDetails",summonDetails);

		return "person/editPan";
	}

	// Back Button for editPan Page
	@RequestMapping(value = "updatePersonPan", params = "caseDetails")
	public String getpercaseDetails1(@RequestParam(value = "caseDetails", required = true) Long caseId, ModelMap modelMap)
			throws Exception {

		SNMSValidator snmsvalid = new SNMSValidator();
		List<Country> cL = countryRepo.findAll();
		modelMap.addAttribute("Country", cL);
		
		if (snmsvalid.getValidInteger(caseId)) {
			commonObjectSummon(modelMap, caseId);
			SummonDetails summonDetails1 = new SummonDetails();
			SummonDetails findByCaseId = summonDao.findByCaseId(caseId);

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
			
			List<AddDesignation> designationList = designationService.findDesignationBytype(new AddDesignation());
			
			List<CompanyType> compTypeList = companyTypeRepo.findAll();
			List<InvestigationStatus> StatusList =null;

			String role=roleDAO.getRoleName(userDetailsService.getUserDetails().getUserId());
			if(role.equals("ROLE_USER")) {
			StatusList = addStatusRepository.findAllBytype("I");
			}else {
				StatusList = addStatusRepository.findAllBytype("P");
			}
			
			
			SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a", Locale.ENGLISH);
			String dateInString1 = "2022-06-12 10:15:55 AM"; 
			Date validdate1 = formatter1.parse(dateInString1);		
			summonDetails1.setValidDate(validdate1);
			modelMap.addAttribute("validdate", validdate1);
			summonDetails1.setCaseId(caseId);
			//List<PersonDetails> pd1 = addPersonRepo.findAllByCompanypersonCaseDetails(caseDetailsRepository.findById(caseId));
			List<PersonDetails> pd1 = addPersonRepo.findAllByCompanypersonCaseDetails(caseDetailsRepository.findById(caseId),Sort.by(Sort.Direction.DESC, "createdDate"));
			
			  HashSet<PersonDetails> pd = new HashSet<PersonDetails>(pd1);
		//	List<PersonDetails> pd = addPersonRepo.findAll();
			List<personcompanyApproval> pca = pcaRepo.findAll();
			System.out.println("size of list" + pca.size());
			modelMap.addAttribute("designationList", designationList);
			modelMap.addAttribute("compTypeList", compTypeList);
		    modelMap.addAttribute("statusLst", StatusList);
			modelMap.addAttribute("summonDetails", summonDetails1);
			modelMap.addAttribute("pesoncompList", pd);
			modelMap.addAttribute("pcaList", pca);
			return "person/addPersons";
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

			return "person/assignedCase";
		}

	}
	
	//When click on Save Edit Pen Details(DropDown)
	@RequestMapping(value = "updatePersonPan")
	public String updatePersonPan(@ModelAttribute("summonDetails") @Valid SummonDetails summonDetails,
			BindingResult result, ModelMap modelMap) throws Exception {
	
		SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a", Locale.ENGLISH);
		String dateInString1 = "2022-06-12 10:15:55 AM"; 
		Date validdate1 = formatter1.parse(dateInString1);		
		summonDetails.setValidDate(validdate1);
		modelMap.addAttribute("validdate", validdate1);
	
	   RelationpersonCompany rpcq = rpcRepo.findAllById(summonDetails.getPersonDetails().getCompDto().getId());
	   CaseDetails cd = CaseDetailsRepo.findById(summonDetails.getCaseId()).get();
	
		PersonDetails persondet =  addPersonRepo.findAllByPersonID(summonDetails.getPersonDetails().getPersonID());
		 
	    persondet.setPanNumber(summonDetails.getPersonDetails().getPanNumber());
	    persondet.setIsUpdated(true);
		addPersonRepo.save(persondet);		
		
		
		
		SummonDetails findByCaseId = summonDao.findByCaseId(summonDetails.getCaseId());
		SummonDetails summonDetails1 = new SummonDetails();
		if (findByCaseId != null) {
			commonObjectSummon(modelMap, summonDetails.getCaseId());
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
		
		 List<CompanyType> compTypeList = companyTypeRepo.findAll();
		 List<AddDesignation> designationList = designationService.findDesignationBytype(new AddDesignation());
		List<PersonDetails> pd1 = addPersonRepo.findAllByCompanypersonCaseDetails(caseDetailsRepository.findById(summonDetails.getCaseId()),Sort.by(Sort.Direction.DESC, "createdDate"));
		HashSet<PersonDetails> pd = new HashSet<PersonDetails>(pd1);
		List<personcompanyApproval> pca = pcaRepo.findAll();
		
		
		
		   List<Country> cL = countryRepo.findAll();
			modelMap.addAttribute("Country", cL);
	      List<InvestigationStatus> StatusList =null;
	      String role=roleDAO.getRoleName(userDetailsService.getUserDetails().getUserId());
			if(role.equals("ROLE_USER")) {
			StatusList = addStatusRepository.findAllBytype("I");
			}else {
				StatusList = addStatusRepository.findAllBytype("P");
			}
		//List<AddDesignation> designationList = designationRepo.findDesignationBytype("C", "S");
		
		
		
		modelMap.addAttribute("designationList", designationList);
		modelMap.addAttribute("compTypeList", compTypeList);
	    modelMap.addAttribute("statusLst", StatusList);		
		modelMap.addAttribute("pesoncompList", pd);
		modelMap.addAttribute("pcaList", pca); 
		modelMap.addAttribute("summonDetails", summonDetails);
		modelMap.addAttribute("caseDetails", cd);
	
		modelMap.addAttribute("message", " Person Updated Successfully as " +persondet.getFullName());
		return "person/addPersons";
		
	}
	
	
	
	
	

	@RequestMapping(value = "/getValidPersonEmail", method = RequestMethod.POST, produces = "application/json")
	public @ResponseBody Boolean getValidPersonEmail(@RequestParam("Email") String Email) {

//	    		https://mkyong.com/regular-expressions/how-to-validate-email-address-with-regular-expression/
		String emailReg = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
		Pattern pattern = Pattern.compile(emailReg);
		Matcher matcher = pattern.matcher(Email);

		if (matcher.matches()) {
			return true;
		} else {
			return false;
		}

	}
	
}
