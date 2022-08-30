package com.snms.controllers;

import org.springframework.data.util.Streamable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.Param;

import com.snms.dao.AppUserDAO;
import com.snms.dao.NoticeDao;
import com.snms.dao.SummonDao;
import com.snms.dto.SummonReportDTO;
import com.snms.entity.AppUser;
import com.snms.entity.NoticeStatus;
import com.snms.entity.OfficeOrder;
import com.snms.entity.SummonStatus;
import com.snms.entity.UserDetails;
import com.snms.entity.personcompanyApproval;
import com.snms.service.PersonStatusListPagingRepo;
import com.snms.service.SummonRepository;
import com.snms.service.SummonStatusListPagingRepo;
import com.snms.service.UserDetailsRepository;
import com.snms.service.UserDetailsServiceImpl;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

@Controller
public class PaginationController {

	private static final Logger logger = Logger.getLogger(PaginationController.class);
	@Autowired
	private SummonDao summonDao;
	@Autowired
	private NoticeDao noticeDao;
	@Autowired
	private SummonStatusListPagingRepo SummonPagingRepo;
	@Autowired
	private AppUserDAO appUserDAO;
	@Autowired
	private SummonRepository summonRepo;
	@Autowired
	private UserDetailsServiceImpl userDetailsService;
	@Autowired
	private UserDetailsRepository  userDetailsRepo;
	@Autowired
	private PersonStatusListPagingRepo personRepo;

	@RequestMapping(value = "/personStatus")
	public String personStatus(Model model) throws Exception {

		List<personcompanyApproval> personStatus = summonDao.findPersonStatusByUserId(getUserDetails());

		model.addAttribute("personStatus", personStatus);
		List<OfficeOrder> officeOrder = new ArrayList<OfficeOrder>();
		model.addAttribute("officeOrder", officeOrder);
		List<NoticeStatus> notice = new ArrayList<NoticeStatus>();
		model.addAttribute("notice", notice);
		List<SummonStatus> summon = new ArrayList<SummonStatus>();
		model.addAttribute("summon", summon);

		return "caseDetails/status";
		// return "caseDetails/status1";
	}

	@RequestMapping(value = "/summonStatus")
	public String summonStatus(Model model) throws Exception {
		List<OfficeOrder> officeOrder = new ArrayList<OfficeOrder>();
		model.addAttribute("officeOrder", officeOrder);
		List<NoticeStatus> notice = new ArrayList<NoticeStatus>();
		model.addAttribute("notice", notice);
		getLoginUserName();
		model.addAttribute("totalItems", SummonPagingRepo.count());
		List<SummonStatus> summon = summonRepo.findAll();
		// List<SummonStatus> summon = summonDao.findSummonByUserId(getUserDetails());
		List<personcompanyApproval> personStatus = new ArrayList<personcompanyApproval>();
		model.addAttribute("personStatus", personStatus);
		model.addAttribute("summon", summon); // model.addAttribute("totalItems", summ)
		return "caseDetails/status1";
	}

	@RequestMapping(value = "/noticeStatus")
	public String noticeStatus(Model model) throws Exception {

		List<NoticeStatus> notice = summonDao.findNoticeByUserId(getUserDetails());

		// notice.get(0).getSummonType().getIndividualType().getIndividualId();
		model.addAttribute("notice", notice);
		List<SummonStatus> summon = new ArrayList<SummonStatus>();
		model.addAttribute("summon", summon);
		List<OfficeOrder> officeOrder = new ArrayList<OfficeOrder>();
		model.addAttribute("officeOrder", officeOrder);
		List<personcompanyApproval> personStatus = new ArrayList<personcompanyApproval>();
		model.addAttribute("personStatus", personStatus);

		return "caseDetails/status1";
	}

	@RequestMapping(value = "/SummonStatusAsAddl")
	public String SummonStatusAsIO(ModelMap model) throws Exception {

		return "Report/StatusIO";
	}

	@RequestMapping("getSignedSummons1")
	public String signedOrederList(ModelMap modelMap, SummonReportDTO summonStatusmodel)
			throws NumberFormatException, ParseException, Exception {
		return viewSummonPage(modelMap, 1, "id", "asc", summonStatusmodel);
	}

	/*
	 * @RequestMapping("getSignedNotices1") public String signednoticeList(ModelMap
	 * modelMap, SummonReportDTO noticeStatusmodel) throws NumberFormatException,
	 * ParseException, Exception { String isphysically_signed=
	 * noticeStatusmodel.getIsPhysically_signed(); SummonReportDTO noticeStatus= new
	 * SummonReportDTO(); if (isphysically_signed != null)
	 * noticeStatus.setIsPhysically_signed(isphysically_signed);
	 * modelMap.addAttribute("noticeStatus",noticeStatus); return
	 * "DSCSigning/SignedNotice1"; }
	 */
	
	
	@RequestMapping("getSignedNotices1")
	public String signednoticeList(ModelMap modelMap, SummonReportDTO noticeStatusmodel)
			throws NumberFormatException, ParseException, Exception {
		return viewNoticePage(modelMap, 1, "id", "desc", noticeStatusmodel);
	}
	private String viewNoticePage(ModelMap modelmap, @PathVariable(name = "pageNum") int pageNum,
			@Param("sortField") String sortField, @Param("sortDir") String sortDir, SummonReportDTO noticeStatusmodel) throws Exception {
		String isphysically_signed= noticeStatusmodel.getIsPhysically_signed();
		int total = 50;
		 SummonReportDTO noticeStatus= new SummonReportDTO();
		 if (isphysically_signed != null)
		 noticeStatus.setIsPhysically_signed(isphysically_signed);
		
		NoticeStatus ns = new NoticeStatus();
      List<NoticeStatus> list=summonDao.findNotice_signeds(getUserDetails(),noticeStatusmodel);
  	List<NoticeStatus> pageNoticeList = summonDao.findNotice_signed1(pageNum, total, "id", "desc",
			getUserDetails(),noticeStatusmodel);
      
      List<NoticeStatus> signedList = new ArrayList<NoticeStatus>();
		int count =1;
		for (NoticeStatus noticelist :pageNoticeList)
		{
			
			UserDetails userdetail = userDetailsRepo.findAllByUserId(noticelist.getAppUser());
			String issuedBY =  userDetailsService.getFullName(userdetail);
			
			
			signedList.add(new NoticeStatus(noticelist.getId(),noticelist.getCaseDetails(),noticelist.getSummonType(),noticelist.getAprrovalStage1(),noticelist.getAprrovalStage2(),
					issuedBY,noticelist.getCreatedDate(),noticelist.getOrderSignedDate(),noticelist.getSummonNo(),noticelist.getDateOfAppear(),noticelist.getIsSensitive(),
					noticelist.getUnsignFile(),noticelist.getSignFile(), noticelist.getIsSigned(),noticelist.getNoticeDin(),noticelist.getApprovalDate(),
					noticelist.getIsDSC(),noticelist.getIsRejected(),noticelist.getIs_physicallysent(),
					noticelist.getRemark(),noticelist.getVerify_id(),noticelist.getApproval_Id()));
		count++;
			
			
		
		}
		double totalPages = (double) list.size()/total;
		int  totalsize = (int) Math.ceil(totalPages);
		modelmap.addAttribute("currentPage", pageNum);
		modelmap.addAttribute("totalPages",totalsize);
		modelmap.addAttribute("totalItems", list.size());
      modelmap.addAttribute("signOrders",signedList);	
      modelmap.addAttribute("noticeStatus",noticeStatus);
		return "DSCSigning/SignedNotice1";
	} 


	private String viewSummonPage(ModelMap modelmap, @PathVariable(name = "pageNum") int pageNum,
			@Param("sortField") String sortField, @Param("sortDir") String sortDir, SummonReportDTO summonStatusmodel) throws Exception {
		String isphysically_signed = summonStatusmodel.getIsPhysically_signed();
		int total = 50;
		SummonReportDTO summonStatus = new SummonReportDTO();

		if (isphysically_signed != null)
			summonStatus.setIsPhysically_signed(isphysically_signed);

		List<SummonStatus> list = summonDao.findSummon_signeds(getUserDetails() ,summonStatusmodel);
		
		
		List<SummonStatus> pageSummonList = summonDao.findSummon_signed1(pageNum, total, "id", "desc",
				getUserDetails(),summonStatusmodel);
		
		//List<SummonStatus> signedSummon = pageSummonList.getContent();
		// List<SummonStatus> signedList = null;
		List<SummonStatus> signedList = new ArrayList<SummonStatus>();
		int count = 1;
		for (SummonStatus summonlist : pageSummonList) {
			System.out.println(summonlist.getId());
			UserDetails userdetail = userDetailsRepo.findAllByUserId(summonlist.getAppUser());
			String issuedBY = userDetailsService.getFullName(userdetail);
		

				signedList.add(new SummonStatus(summonlist.getId(), summonlist.getCaseDetails(),
						summonlist.getSummonType(), summonlist.getAprrovalStage1(), summonlist.getAprrovalStage2(),
						issuedBY, summonlist.getCreatedDate(), summonlist.getOrderSignedDate(),
						summonlist.getSummonNo(), summonlist.getDateOfAppear(), summonlist.getIsSensitive(),
						summonlist.getUnsignFile(), summonlist.getSignFile(), summonlist.getIsSigned(),
						summonlist.getSummonDin(), summonlist.getApprovalDate(), summonlist.getIsDSC(),
						summonlist.getIsOffline(), summonlist.getIssueDate(), summonlist.getIsRejected(),
						summonlist.getIs_physicallysent(), summonlist.getRemark(), summonlist.getVerify_id(),
						summonlist.getApproval_Id()));
				count++;
		
		}
		double totalPages = (double) list.size()/total;
		int  totalsize = (int) Math.ceil(totalPages);
		System.out.println(list.size());
		System.out.println( Math.ceil(totalPages));
		modelmap.addAttribute("currentPage", pageNum);
		modelmap.addAttribute("totalPages", totalsize);
		modelmap.addAttribute("totalItems", list.size());
		modelmap.addAttribute("signOrders", signedList);
		modelmap.addAttribute("summonStatus", summonStatus);
		return "DSCSigning/SignedSummons1";
	}

	@RequestMapping("/AllPersonList")
	public String viewHomePage(Model model) throws Exception {
		return viewPage(model, 1, "id", "desc");

	}

	@RequestMapping("/page/{pageNum}")
	public String viewPage(Model model, @PathVariable(name = "pageNum") int pageNum,
			@Param("sortField") String sortField, @Param("sortDir") String sortDir) throws Exception {
		int total = 50;

		Pageable currentPaging = PageRequest.of(pageNum, total);

		// Page<personcompanyApproval> pageSummonList =
		// personRepo.findAll(currentPaging);
		Page<personcompanyApproval> pageSummonList = summonDao.PersonlistAll(pageNum, total, "id", "desc",
				getUserDetails());
		List<personcompanyApproval> personStatus = pageSummonList.getContent();

		model.addAttribute("currentPage", pageNum);
		model.addAttribute("totalPages", pageSummonList.getTotalPages());
		model.addAttribute("totalItems", pageSummonList.getTotalElements());

		model.addAttribute("sortDir", sortDir);
		model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");

		model.addAttribute("personStatus", personStatus);

		// return "Report/Summonstatus";
		return "director/PersonList";
	}

	@RequestMapping(value = "PersonLisDetails", method = RequestMethod.POST)
	public String PersonLisDetails(Model model, @RequestParam("pageId") int pageNum) throws Exception {
		int total = 50;

		String sortDir = "desc";

		Pageable currentPaging = PageRequest.of(pageNum, total);
		// Page<personcompanyApproval> pageSummonList =
		// personRepo.findAll(currentPaging);
		Page<personcompanyApproval> pageStatusList = summonDao.PersonlistAll(pageNum, total, "id", "desc",
				getUserDetails());

		List<personcompanyApproval> personStatus = pageStatusList.getContent();

		model.addAttribute("currentPage", pageNum);
		model.addAttribute("totalPages", pageStatusList.getTotalPages());
		model.addAttribute("totalItems", pageStatusList.getTotalElements());

		model.addAttribute("sortDir", sortDir);
		model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");

		model.addAttribute("personStatus", personStatus);

		// return "Report/Summonstatus";
		return "director/PersonList";
	}


	
	@RequestMapping(value="NoticeListDetails" , method = RequestMethod.POST)
	public String NoticeListDetails(ModelMap modelmap, @RequestParam("pageId") int pageNum ,SummonReportDTO noticeStatusmodel )throws Exception {
		String isphysically_signed= noticeStatusmodel.getIsPhysically_signed();
		int total = 50;
		 SummonReportDTO noticeStatus= new SummonReportDTO();
		 if (isphysically_signed != null)
		 noticeStatus.setIsPhysically_signed(isphysically_signed);
		
		NoticeStatus ns = new NoticeStatus();
		 List<NoticeStatus> list=summonDao.findNotice_signeds(getUserDetails(),noticeStatusmodel);
  	List<NoticeStatus> pageNoticeList = summonDao.findNotice_signed1(pageNum, total, "id", "desc",
			getUserDetails(),noticeStatusmodel);
      
      List<NoticeStatus> signedList = new ArrayList<NoticeStatus>();
		int count =1;
		for (NoticeStatus noticelist :pageNoticeList)
		{
			
			UserDetails userdetail = userDetailsRepo.findAllByUserId(noticelist.getAppUser());
			String issuedBY =  userDetailsService.getFullName(userdetail);
			
			
			signedList.add(new NoticeStatus(noticelist.getId(),noticelist.getCaseDetails(),noticelist.getSummonType(),noticelist.getAprrovalStage1(),noticelist.getAprrovalStage2(),
					issuedBY,noticelist.getCreatedDate(),noticelist.getOrderSignedDate(),noticelist.getSummonNo(),noticelist.getDateOfAppear(),noticelist.getIsSensitive(),
					noticelist.getUnsignFile(),noticelist.getSignFile(), noticelist.getIsSigned(),noticelist.getNoticeDin(),noticelist.getApprovalDate(),
					noticelist.getIsDSC(),noticelist.getIsRejected(),noticelist.getIs_physicallysent(),
					noticelist.getRemark(),noticelist.getVerify_id(),noticelist.getApproval_Id()));
		count++;
			
			
		
		}
		double totalPages = (double) list.size()/total;
		int  totalsize = (int) Math.ceil(totalPages);
		modelmap.addAttribute("currentPage", pageNum);
		modelmap.addAttribute("totalPages",totalsize);
		modelmap.addAttribute("totalItems", list.size());
      modelmap.addAttribute("signOrders",signedList);	
      modelmap.addAttribute("noticeStatus",noticeStatus);
		return "DSCSigning/SignedNotice1";
	}
	
	
	
	@RequestMapping(value="NoticeList")
	public String NoticeList(ModelMap modelmap,SummonReportDTO noticeStatusmodel )throws Exception {
		String isphysically_signed= noticeStatusmodel.getIsPhysically_signed();
		int total = 50;
		int pageNum = 1;
		 SummonReportDTO noticeStatus= new SummonReportDTO();
		 if (isphysically_signed != null)
		 noticeStatus.setIsPhysically_signed(isphysically_signed);
		
		NoticeStatus ns = new NoticeStatus();
		 List<NoticeStatus> list=summonDao.findNotice_signeds(getUserDetails(),noticeStatusmodel);
  	List<NoticeStatus> pageNoticeList = summonDao.findNotice_signed1(pageNum, total, "id", "desc",
			getUserDetails(),noticeStatusmodel);
      
      List<NoticeStatus> signedList = new ArrayList<NoticeStatus>();
		int count =1;
		for (NoticeStatus noticelist :pageNoticeList)
		{
			
			UserDetails userdetail = userDetailsRepo.findAllByUserId(noticelist.getAppUser());
			String issuedBY =  userDetailsService.getFullName(userdetail);
			
			
			signedList.add(new NoticeStatus(noticelist.getId(),noticelist.getCaseDetails(),noticelist.getSummonType(),noticelist.getAprrovalStage1(),noticelist.getAprrovalStage2(),
					issuedBY,noticelist.getCreatedDate(),noticelist.getOrderSignedDate(),noticelist.getSummonNo(),noticelist.getDateOfAppear(),noticelist.getIsSensitive(),
					noticelist.getUnsignFile(),noticelist.getSignFile(), noticelist.getIsSigned(),noticelist.getNoticeDin(),noticelist.getApprovalDate(),
					noticelist.getIsDSC(),noticelist.getIsRejected(),noticelist.getIs_physicallysent(),
					noticelist.getRemark(),noticelist.getVerify_id(),noticelist.getApproval_Id()));
		count++;
			
			
		
		}
		double totalPages = (double) list.size()/total;
		int  totalsize = (int) Math.ceil(totalPages);
		modelmap.addAttribute("currentPage", pageNum);
		modelmap.addAttribute("totalPages",totalsize);
		modelmap.addAttribute("totalItems", list.size());
      modelmap.addAttribute("signOrders",signedList);	
      modelmap.addAttribute("noticeStatus",noticeStatus);
		return "DSCSigning/SignedNotice1";
	}
	
	@RequestMapping(value = "SummonLisDetails", method = RequestMethod.POST)
	public String SummonLisDetails(ModelMap modelmap, @RequestParam("pageId") int pageNum , SummonReportDTO summonStatusmodel) throws Exception {
		int total = 50;

		String sortDir = "desc";
		String isphysically_signed = summonStatusmodel.getIsPhysically_signed();
		
		SummonReportDTO summonStatus = new SummonReportDTO();

		if (isphysically_signed != null)
			summonStatus.setIsPhysically_signed(isphysically_signed);

		Pageable currentPaging = PageRequest.of(pageNum, total);
		// Page<personcompanyApproval> pageSummonList =
		// personRepo.findAll(currentPaging);
		List<SummonStatus> pageSummonList = summonDao.findSummon_signed1(pageNum, total, "id", "desc",
				getUserDetails(),summonStatusmodel);
		
	//	List<SummonStatus> signedSummon = pageSummonList.getContent();
		

		

		modelmap.addAttribute("sortDir", sortDir);
		modelmap.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");

		List<SummonStatus> signedList = new ArrayList<SummonStatus>();
		int count = 1;
		for (SummonStatus summonlist : pageSummonList) {
			System.out.println(summonlist.getId());
			UserDetails userdetail = userDetailsRepo.findAllByUserId(summonlist.getAppUser());
			String issuedBY = userDetailsService.getFullName(userdetail);
			

				signedList.add(new SummonStatus(summonlist.getId(), summonlist.getCaseDetails(),
						summonlist.getSummonType(), summonlist.getAprrovalStage1(), summonlist.getAprrovalStage2(),
						issuedBY, summonlist.getCreatedDate(), summonlist.getOrderSignedDate(),
						summonlist.getSummonNo(), summonlist.getDateOfAppear(), summonlist.getIsSensitive(),
						summonlist.getUnsignFile(), summonlist.getSignFile(), summonlist.getIsSigned(),
						summonlist.getSummonDin(), summonlist.getApprovalDate(), summonlist.getIsDSC(),
						summonlist.getIsOffline(), summonlist.getIssueDate(), summonlist.getIsRejected(),
						summonlist.getIs_physicallysent(), summonlist.getRemark(), summonlist.getVerify_id(),
						summonlist.getApproval_Id()));
				count++;
			
			}


		
		List<SummonStatus> list = summonDao.findSummon_signeds(getUserDetails(),summonStatusmodel);
		double totalPages = (double) list.size()/total;
		int  totalsize = (int) Math.ceil(totalPages);
		
		modelmap.addAttribute("currentPage", pageNum);
		modelmap.addAttribute("totalPages", totalsize);
		modelmap.addAttribute("totalItems", list.size());
		modelmap.addAttribute("signOrders", signedList);
		modelmap.addAttribute("summonStatus", summonStatus);
		return "DSCSigning/SignedSummons1";
		
	}
	@RequestMapping(value = "/PesonData", produces = "application/json")
	public @ResponseBody Page<personcompanyApproval> PesonData(
			@RequestParam(value = "page_id", required = true) int page_id, ModelMap model) throws Exception {

		int total = 50; // per page record fetch from db
		// List<personcompanyApproval> personStatus =
		// summonDao.findPersonStatusByUserId(page_id, total, "id",
		// "Asc",getUserDetails());
		Page<personcompanyApproval> personStatus = summonDao.PersonlistAll(page_id, total, "id", "Asc",
				getUserDetails());
		// Page<personcompanyApproval> noticeList = (Page<personcompanyApproval>)
		// personStatus;

		Pageable currentPaging = PageRequest.of(page_id, total, Sort.by("id"));
		return personStatus;
	}

	@RequestMapping(value = "/summonData", produces = "application/json")
	public @ResponseBody Page<SummonStatus> pagination1(@RequestParam(value = "page_id", required = true) int page_id,
			ModelMap model) throws Exception {
		int total = 50; // per page record fetch from db
		Page<SummonStatus> SummonList = summonDao.summonlistAll(page_id, total, "id", "desc", getUserDetails());
		Pageable currentPaging = PageRequest.of(page_id, total, Sort.by("id"));
		return SummonList;
	}

	@RequestMapping(value = "/summonAddlData", produces = "application/json")
	public @ResponseBody Page<SummonStatus> summonAddlData(
			@RequestParam(value = "page_id", required = true) int page_id, ModelMap model) throws Exception {
		int total = 50; // per page record fetch from db
		Page<SummonStatus> SummonList = summonDao.summonlistAsIO(page_id, total, "id", "desc", getUserDetails());
		Pageable currentPaging = PageRequest.of(page_id, total, Sort.by("id"));
		return SummonList;
	}

	@RequestMapping(value = "/noticeData", produces = "application/json")
	public @ResponseBody Page<NoticeStatus> noticeData(@RequestParam(value = "page_id", required = true) int page_id,
			ModelMap model) throws Exception {
		int total = 50;

		Page<NoticeStatus> noticeList = noticeDao.noticelistAll(page_id, total, "id", "desc", getUserDetails());
		Pageable currentPaging = PageRequest.of(page_id, total, Sort.by("id"));
		/// Streamable<SummonStatus> SummonList =
		/// SummonPagingRepo.findAll(currentPaging);

		return noticeList;
	}

	
	/*
	 * @RequestMapping(value = "/SignednoticeData", produces = "application/json")
	 * public @ResponseBody List<NoticeStatus> SignednoticeData(@RequestParam(value
	 * = "page_id", required = true) int page_id,@RequestParam(value = "radioValue",
	 * required = true) String radioValue, ModelMap model) throws Exception { int
	 * total = 10;
	 * 
	 * List<NoticeStatus> pageNoticeList = summonDao.findNotice_signed1(page_id,
	 * total, "id", "Asc", getUserDetails(),radioValue); int count =1;
	 * List<NoticeStatus> signedList = new ArrayList<NoticeStatus>(); for
	 * (NoticeStatus noticelist :pageNoticeList) {
	 * 
	 * UserDetails userdetail =
	 * userDetailsRepo.findAllByUserId(noticelist.getAppUser()); String issuedBY =
	 * userDetailsService.getFullName(userdetail);
	 * 
	 * if (radioValue == null) {
	 * 
	 * signedList.add(new
	 * NoticeStatus(noticelist.getId(),noticelist.getCaseDetails(),noticelist.
	 * getSummonType(),noticelist.getAprrovalStage1(),noticelist.getAprrovalStage2()
	 * , issuedBY,noticelist.getCreatedDate(),noticelist.getOrderSignedDate(),
	 * noticelist.getSummonNo(),noticelist.getDateOfAppear(),noticelist.
	 * getIsSensitive(), noticelist.getUnsignFile(),noticelist.getSignFile(),
	 * noticelist.getIsSigned(),noticelist.getNoticeDin(),noticelist.getApprovalDate
	 * (), noticelist.getIsDSC(),noticelist.getIsRejected(),noticelist.
	 * getIs_physicallysent(),
	 * noticelist.getRemark(),noticelist.getVerify_id(),noticelist.getApproval_Id())
	 * ); count++; } } return signedList; }
	 */
	
	public String getLoginUserName() {
		return SecurityContextHolder.getContext().getAuthentication().getName();
	}

	private AppUser getUserDetails() throws Exception {
		AppUser appUser = this.appUserDAO.findUserAccount(getLoginUserName());
		return appUser;
	}

}
