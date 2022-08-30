package com.snms.controllers;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.snms.dao.AppRoleDAO;
import com.snms.dao.AppUserDAO;
import com.snms.dao.NoticeDao;
import com.snms.dao.SummonDao;
import com.snms.dto.StatusDTO;
import com.snms.entity.AppUser;
import com.snms.entity.Company;
import com.snms.entity.Inspector;
import com.snms.entity.NoticeStatus;
import com.snms.entity.SummonStatus;
import com.snms.entity.UnitDetails;
import com.snms.entity.UserDetails;
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
import com.snms.utils.Utils;

@Controller
public class SummonNoticeReportController {
	private static final Logger logger = Logger.getLogger(SummonNoticeReportController.class);
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
	private  LinkOfficerRepository linkoffRepo;
	// Notice Flow Code Started
	@Value("${file.upload}")
	public String filePath;
	
	@RequestMapping(value = "/ReportStatus")
	public String summonStatus(Model model) throws Exception {

		getLoginUserName();
		List<SummonStatus> summon = summonDao.findSummonByUserId(getUserDetails());

		// List<UserRole> userLIst = userRoleRepo.findByAppRole(2);
		List<UserDetails> inspectorList = summonDao.findInspectorLIst();
		int count = 1;
		List<Inspector> inspList = new ArrayList<Inspector>();

		for (UserDetails user : inspectorList) {
			inspList.add(new Inspector(count,
					user.getSalutation() + " " + userDetailsService.getFullName(user) + " ("
							+ user.getUnit().getUnitName() + ")",
					user.getDesignation().getDesignation(), 2, user.getUserId().getUserId(), false,
					userMangCustom.getCasesAssigned(user.getUserId().getUserId())));
			count++;
		}
		model.addAttribute("summon", summon);
		model.addAttribute("inspList", inspList);
		List<UnitDetails> unitList = unitDetailsRepo.findAll(Sort.by(Sort.Direction.ASC, "unitId"));
		unitList.remove(2);
		model.addAttribute("unitList", unitList);
		return "caseDetails/ReportStatus";
	}

	@RequestMapping(value = "/SumomReportStatus")
	public String SumomReportStatus(Model model) throws Exception {
			//List<UnitDetails> unitList = unitDetailsRepo.findAll(Sort.by(Sort.Direction.ASC, "unitName"));
		     //List<UnitDetails> unitList = summonDao.findAllUnit();
		//List unitList = summonDao.findAlllUnit();
		List<UnitDetails> unitList = new ArrayList<UnitDetails>();;
           List <Object[]> unitList1 = summonDao.findAllBYUnit();
		
		for (Object[] object : unitList1) {
			UnitDetails  unitDetails = new UnitDetails(Long.parseLong(object[0].toString()),object[1].toString());
			
			unitList.add(unitDetails);
		}
		List <UnitDetails> UnitDetails1 =  new ArrayList<UnitDetails>();
		int unitcount=0;
		for(UnitDetails unitList2 : unitList) {
			if(unitList2.getUnitName().equalsIgnoreCase("Administrator Unit")) {
				
				unitList.remove(unitList2.getUnitId()-1);
			}
			else if(unitList2.getUnitName().equalsIgnoreCase("Prosecution")) {
				
				unitList.remove(unitList2.getUnitId()-1);
			}
			
			else {
				UnitDetails1.add(new UnitDetails(unitList2.getUnitId(),unitList2.getUnitName(),unitList2.getLocation(),unitList2.getAddress(),unitList2.getTelephoneNo(),unitList2.getFaxNo(),unitList2.getEMail(),unitList2.getCreatedDate()));
			}
			unitcount++;
		}
		StatusDTO std = new StatusDTO();
		UnitDetails unit = new UnitDetails();
		String date = new Utils().currentDate();
		List<StatusDTO> stdList = new ArrayList<StatusDTO>();
		int count = 1;
		for (UnitDetails unitdtl : UnitDetails1) {
			List<SummonStatus> TotalSummonUnit = summonDao.findSummonByUnits(unitdtl.getUnitId(), 1);
			List<SummonStatus> approvedSummonUnit = summonDao.findSummonByUnits(unitdtl.getUnitId(), 2);
			List<SummonStatus> EsignedSummonUnit = summonDao.findSummonByUnits(unitdtl.getUnitId(), 3);
			List<SummonStatus> PendingSummonUnit = summonDao.findSummonByUnits(unitdtl.getUnitId(), 4);
			List<SummonStatus> offlineSummon = summonDao.findOfflineSummonByUnits(unitdtl.getUnitId());
			List<SummonStatus> physummon    = summonDao.findPhyReviewSummonByUnits(unitdtl.getUnitId());
			List<SummonStatus> physummonYetToUpload = summonDao.physummonYetToUploadByUnits(unitdtl.getUnitId());
			List<SummonStatus> physummonYetToApproved = summonDao.findSummonByUnits(unitdtl.getUnitId(), 6);
			List<SummonStatus> summonPendingDir = summonDao.findSummonByUnits(unitdtl.getUnitId(), 7);
			List<SummonStatus>  TotalSigned          = summonDao.findSummonByUnits(unitdtl.getUnitId(), 8);

			stdList.add(new StatusDTO(count, unitdtl.getUnitId(), unitdtl.getUnitName(),TotalSummonUnit.size(), approvedSummonUnit.size(),
					EsignedSummonUnit.size(),PendingSummonUnit.size(), offlineSummon.size(),physummonYetToUpload.size(),physummon.size(),physummonYetToApproved.size(),summonPendingDir.size(),TotalSigned.size()));
			count++;
			/*
			 * if(count == 3) break;
			 */
		}

		model.addAttribute("stdList", stdList);
		model.addAttribute("date", date);
		return "director/summonStatusReport";
	}

	
	
	
	@RequestMapping(value = "/AddlStatusReportByDate")
	public String AddlStatusReportByDate(Model model) throws Exception {
		List<UnitDetails> unitList = unitDetailsRepo.findAll(Sort.by(Sort.Direction.ASC, "unitName"));
		StatusDTO std = new StatusDTO();
		UnitDetails unit = new UnitDetails();
		String date = new Utils().currentDate();
		List<StatusDTO> stdList = new ArrayList<StatusDTO>();
		int count = 1;
		List<Inspector>caselist = inspRepo.findAllByAppUserAndIsAdoAndIsActive(getUserDetails(),true,true);
		for(Inspector caselst : caselist){
			List<NoticeStatus> allNotice = summonDao.findNoticeAddl(getUserDetails(), 1,caselst.getCaseDetails());
			List<NoticeStatus> approvedNotice = summonDao.findNoticeAddl(getUserDetails(), 2,caselst.getCaseDetails());
			List<NoticeStatus> EsignedNotice = summonDao.findNoticeAddl(getUserDetails(), 3,caselst.getCaseDetails());
			List<NoticeStatus> PendingNotice = summonDao.findNoticeAddl(getUserDetails(), 4,caselst.getCaseDetails());

			List<SummonStatus> allSummonUnit = summonDao.findSummonByStatusAddl(getUserDetails(), 1,caselst.getCaseDetails());
			List<SummonStatus> approvedSummon = summonDao.findSummonByStatusAddl(getUserDetails(), 2,caselst.getCaseDetails());
			List<SummonStatus> EsignedSummon = summonDao.findSummonByStatusAddl(getUserDetails(), 3,caselst.getCaseDetails());
			List<SummonStatus> PendingSummon = summonDao.findSummonByStatusAddl(getUserDetails(), 4,caselst.getCaseDetails());
			List<SummonStatus> offlineSummon = summonDao.findOfflineSummonByAddl(getUserDetails(),caselst.getCaseDetails());  
			  stdList.add(new StatusDTO(count,
					  caselst.getCaseDetails().getCaseId(),
					  caselst.getCaseDetails().getCaseTitle(),
					  allNotice.size(), 
					  approvedNotice.size(), 
					  EsignedNotice.size(),
					  PendingNotice.size(), 
					  allSummonUnit.size(),
					  approvedSummon.size(),
					  EsignedSummon.size(),
					  PendingSummon.size(),
					  offlineSummon.size()));
					  
					 
			  count++;
			  }
		
		
			
			count++;
			model.addAttribute("stdList", stdList);
			model.addAttribute("StdNotice", unit);

			model.addAttribute("UnitList", unitList);

			model.addAttribute("date", date);
		
			return "caseDetails/StatusByIO";
	}
	
	@RequestMapping(value = "/StatusReportByDate")
	public String StatusReportByDate(Model model) throws Exception {
		String date = new Utils().currentDate();

		String endDate = "";
		String startDate = "";
		int inspList = 0;
		List<UnitDetails> unitList = unitDetailsRepo.findAll(Sort.by(Sort.Direction.ASC, "unitName"));
		StatusDTO std = new StatusDTO();
		UnitDetails unit = new UnitDetails();

		List<StatusDTO> stdList = new ArrayList<StatusDTO>();
		int count = 1;
		
	
		List<Inspector>caselist = inspRepo.findAllByAppUser(getUserDetails());
		for(Inspector caselst : caselist){
		List<NoticeStatus> allNotice = summonDao.findNoticeByStatus(getUserDetails(), 1,caselst.getCaseDetails());
		List<NoticeStatus> approvedNotice = summonDao.findNoticeByStatus(getUserDetails(), 2,caselst.getCaseDetails());
		List<NoticeStatus> EsignedNotice = summonDao.findNoticeByStatus(getUserDetails(), 3,caselst.getCaseDetails());
		List<NoticeStatus> PendingNotice = summonDao.findNoticeByStatus(getUserDetails(), 4,caselst.getCaseDetails());
		List<NoticeStatus> PhyPendingNotice = summonDao.findNoticeByStatus(getUserDetails(), 5,caselst.getCaseDetails());
		List<NoticeStatus> PhyNotice = summonDao.findNoticeByStatus(getUserDetails(), 6,caselst.getCaseDetails());

		
		
		
		List<SummonStatus> allSummonUnit = summonDao.findSummonByStatus(getUserDetails(), 1,caselst.getCaseDetails());
		List<SummonStatus> approvedSummon = summonDao.findSummonByStatus(getUserDetails(), 2,caselst.getCaseDetails());
		List<SummonStatus> EsignedSummon = summonDao.findSummonByStatus(getUserDetails(), 3,caselst.getCaseDetails());
		List<SummonStatus> PendingSummon = summonDao.findSummonByStatus(getUserDetails(), 4,caselst.getCaseDetails());
		List<SummonStatus> offlineSummon = summonDao.findOfflineSummonByUsers(getUserDetails(),caselst.getCaseDetails());  
		  stdList.add(new StatusDTO(count,
				  caselst.getCaseDetails().getCaseId(),
				  caselst.getCaseDetails().getCaseTitle(),
				  allNotice.size(), 
				  approvedNotice.size(), 
				  EsignedNotice.size(),
				  PendingNotice.size(), 
				  allSummonUnit.size(),
				  approvedSummon.size(),
				  EsignedSummon.size(),
				  PendingSummon.size(),
				  offlineSummon.size()));
				  
				 
		  count++;
		  }
		
		model.addAttribute("stdList", stdList);
		model.addAttribute("StdNotice", unit);

		model.addAttribute("UnitList", unitList);

		model.addAttribute("date", date);

		return "caseDetails/StatusByIO";
	}

	@RequestMapping(value = "/StatusByDate")
	public String StatusByDate(Model model) throws Exception {

		String date = new Utils().currentDate();
		String endDate = "";
		String startDate = "";
		int inspList = 0;
		List<UnitDetails> unitList = new ArrayList<UnitDetails>();;
        List <Object[]> unitList1 = summonDao.findAllBYUnit();
		
		for (Object[] object : unitList1) {
			UnitDetails  unitDetails = new UnitDetails(Long.parseLong(object[0].toString()),object[1].toString());
			
			unitList.add(unitDetails);
		}
		StatusDTO std = new StatusDTO();
		UnitDetails unit = new UnitDetails();
		
		 List <UnitDetails> UnitDetails1 =  new ArrayList<UnitDetails>();
			int unitcount=0;
			
			for(UnitDetails unitList2 : unitList) {
				if(unitList2.getUnitName().equalsIgnoreCase("Administrator Unit")) {
					
					unitList.remove(unitList2.getUnitId()-1);
				}
				
				else if(unitList2.getUnitName().equalsIgnoreCase("Prosecution")) {
					
					unitList.remove(unitList2.getUnitId()-1);
				}
				
				else {
					UnitDetails1.add(new UnitDetails(unitList.get(unitcount).getUnitId(),unitList2.getUnitName(),unitList2.getLocation(),unitList2.getAddress(),unitList2.getTelephoneNo(),unitList2.getFaxNo(),unitList2.getEMail(),unitList2.getCreatedDate()));
				}
				unitcount++;
			}
		List<StatusDTO> stdList = new ArrayList<StatusDTO>();
		int count = 1;

		for (UnitDetails unitdtl : UnitDetails1) {
			List<NoticeStatus> allNoticeUnit = summonDao.findNoticeByUnits(unitdtl.getUnitId(), 1);
			List<NoticeStatus> approvedNoticeUnit = summonDao.findNoticeByUnits(unitdtl.getUnitId(), 2);
			List<NoticeStatus> EsignedNoticeUnit  =  summonDao.findNoticeByUnits(unitdtl.getUnitId(), 3);
			List<NoticeStatus> PendingNoticeUnit  =  summonDao.findNoticeByUnits(unitdtl.getUnitId(), 4);
            List<NoticeStatus> PendingPhyNotice   =  summonDao.findNoticeByUnits(unitdtl.getUnitId(), 5);
			List<NoticeStatus> PhyNotice          =  summonDao.findNoticeByUnits(unitdtl.getUnitId(), 6); 
			List<NoticeStatus> PhyNoticeyetApproved   =  summonDao.findNoticeByUnits(unitdtl.getUnitId(), 7);
			List<NoticeStatus> PhyNoticeSendBack   =  summonDao.findNoticeByUnits(unitdtl.getUnitId(), 8);
        	 stdList.add(new StatusDTO(count, unitdtl.getUnitId(), unitdtl.getUnitName(), allNoticeUnit.size(),
						approvedNoticeUnit.size(), EsignedNoticeUnit.size(), PendingNoticeUnit.size(), PendingPhyNotice.size(),PhyNotice.size(),PhyNoticeyetApproved.size()));
			 count++;
			/*
			 * if(count == 3) break;
			 */
		}

		model.addAttribute("stdList", stdList);
		model.addAttribute("StdNotice", unit);

		model.addAttribute("UnitList", unitList);

		model.addAttribute("date", date);

		return "caseDetails/StatusByDate";
		//return "Report/NoticeReportDir";
	}
	
	
	@RequestMapping(value = "/summonReportIO")
	public String summonReportIO(Model model) throws Exception {
		String date = new Utils().currentDate();

		String endDate = "";
		String startDate = "";
		int inspList = 0;
		
		StatusDTO std = new StatusDTO();
		UnitDetails unit = new UnitDetails();

		List<StatusDTO> stdList = new ArrayList<StatusDTO>();
		int count = 1;
		
	
		List<Inspector>caselist = inspRepo.findAllByAppUserAndIsActiveAndIsIO(getUserDetails(),true,true);
		for(Inspector caselst : caselist){
        List<SummonStatus> allSummonUnit = summonDao.findSummonByStatus(getUserDetails(), 1,caselst.getCaseDetails());
		List<SummonStatus> approvedSummon = summonDao.findSummonByStatus(getUserDetails(), 2,caselst.getCaseDetails());
		List<SummonStatus> EsignedSummon = summonDao.findSummonByStatus(getUserDetails(), 3,caselst.getCaseDetails());
		List<SummonStatus> PendingSummon = summonDao.findSummonByStatus(getUserDetails(), 4,caselst.getCaseDetails());
		List<SummonStatus> offlineSummon = summonDao.findOfflineSummonByUsers(getUserDetails(),caselst.getCaseDetails());  
		List<SummonStatus> phyApprovesummon = summonDao.findPhyReviewSummonByUsers(getUserDetails(),caselst.getCaseDetails());  
		List<SummonStatus> physummonYetToUpload = summonDao.physummonYetToUploadByUser(getUserDetails(),caselst.getCaseDetails()); 
		stdList.add(new StatusDTO(count,
				  caselst.getCaseDetails().getCaseId(),
				  caselst.getCaseDetails().getCaseTitle(),
				  allSummonUnit.size(),
				  approvedSummon.size(),
				  EsignedSummon.size(),
				  PendingSummon.size(),
				  offlineSummon.size(),
				  phyApprovesummon.size(),
				  physummonYetToUpload.size()));
				  
				 
		  count++;
		  }
		
		model.addAttribute("stdList", stdList);
		model.addAttribute("StdNotice", unit);

		//model.addAttribute("UnitList", unitList);

		model.addAttribute("date", date);

		return "Report/summonReportIO";
	}
	
	
	@RequestMapping(value = "/noticeReportIO")
	public String NoticeReportIO(Model model) throws Exception {
		String date = new Utils().currentDate();

		String endDate = "";
		String startDate = "";
		int inspList = 0;
		
		StatusDTO std = new StatusDTO();
		UnitDetails unit = new UnitDetails();

		List<StatusDTO> stdList = new ArrayList<StatusDTO>();
		int count = 1;
		
	
		List<Inspector>caselist = inspRepo.findAllByAppUserAndIsActiveAndIsAdo(getUserDetails(),true,false);
		for(Inspector caselst : caselist){
			List<NoticeStatus> allNotice = summonDao.findNoticeByStatus(getUserDetails(), 1,caselst.getCaseDetails());
			List<NoticeStatus> approvedNotice = summonDao.findNoticeByStatus(getUserDetails(), 2,caselst.getCaseDetails());
			List<NoticeStatus> EsignedNotice = summonDao.findNoticeByStatus(getUserDetails(), 3,caselst.getCaseDetails());
			List<NoticeStatus> PendingNotice = summonDao.findNoticeByStatus(getUserDetails(), 4,caselst.getCaseDetails());  
		    List<NoticeStatus> phyPendingNotice = summonDao.findNoticeByStatus(getUserDetails(), 5,caselst.getCaseDetails());  
		    List<NoticeStatus> phyApproveNotice = summonDao.findNoticeByStatus(getUserDetails(), 6,caselst.getCaseDetails()); 
		stdList.add(new StatusDTO(count,
				  caselst.getCaseDetails().getCaseId(),
				  caselst.getCaseDetails().getCaseTitle(),
				  allNotice.size(),
				  approvedNotice.size(),
				  EsignedNotice.size(),
				  PendingNotice.size(),
				  phyPendingNotice.size(),
				  phyApproveNotice.size()));
				  
				 
		  count++;
		  }
		
		model.addAttribute("stdList", stdList);
		model.addAttribute("StdNotice", unit);

		//model.addAttribute("UnitList", unitList);

		model.addAttribute("date", date);

		return "Report/NoticeReportIO";
	}
	
	
	
	@RequestMapping(value = "/summonReportAddl")
	public String summonReportAddl(Model model) throws Exception {
		String date = new Utils().currentDate();

		String endDate = "";
		String startDate = "";
		int inspList = 0;
		
		StatusDTO std = new StatusDTO();
		UnitDetails unit = new UnitDetails();

		List<StatusDTO> stdList = new ArrayList<StatusDTO>();
		int count = 1;
		
	
		List<Inspector>caselist = inspRepo.findAllByAppUserAndIsActiveAndIsAdo(getUserDetails(),true,true);
		for(Inspector caselst : caselist){
			List<SummonStatus> allSummonUnit = summonDao.findSummonByStatusAddl(getUserDetails(), 1,caselst.getCaseDetails());
			List<SummonStatus> approvedSummon = summonDao.findSummonByStatusAddl(getUserDetails(), 2,caselst.getCaseDetails());
			List<SummonStatus> EsignedSummon = summonDao.findSummonByStatusAddl(getUserDetails(), 3,caselst.getCaseDetails());
			List<SummonStatus> PendingSummon = summonDao.findSummonByStatusAddl(getUserDetails(), 4,caselst.getCaseDetails());
			List<SummonStatus> offlineSummon = summonDao.findOfflineSummonByAddl(getUserDetails(),caselst.getCaseDetails());  
		List<SummonStatus> phyApprovesummon = summonDao.findSummonByStatusAddl(getUserDetails(),6,caselst.getCaseDetails());  
		List<SummonStatus> physummonYetToUpload = summonDao.findSummonByStatusAddl(getUserDetails(),5,caselst.getCaseDetails()); 
		stdList.add(new StatusDTO(count,
				  caselst.getCaseDetails().getCaseId(),
				  caselst.getCaseDetails().getCaseTitle(),
				  allSummonUnit.size(),
				  approvedSummon.size(),
				  EsignedSummon.size(),
				  PendingSummon.size(),
				  offlineSummon.size(),
				  phyApprovesummon.size(),
				  physummonYetToUpload.size()));
				  
				 
		  count++;
		  }
		
		model.addAttribute("stdList", stdList);
		model.addAttribute("StdNotice", unit);

		//model.addAttribute("UnitList", unitList);

		model.addAttribute("date", date);

		return "Report/summonReportAddl";
	}
	@RequestMapping(value = "/NoticeReportAddl")
	public String NoticeReportAddl(Model model) throws Exception {
		String date = new Utils().currentDate();

		String endDate = "";
		String startDate = "";
		int inspList = 0;
		
		StatusDTO std = new StatusDTO();
		UnitDetails unit = new UnitDetails();

		List<StatusDTO> stdList = new ArrayList<StatusDTO>();
		int count = 1;
		
	
		List<Inspector>caselist = inspRepo.findAllByAppUserAndIsActiveAndIsAdo(getUserDetails(),true,true);
		for(Inspector caselst : caselist){
			List<NoticeStatus> allNotice = summonDao.findNoticeAddl(getUserDetails(), 1,caselst.getCaseDetails());
			List<NoticeStatus> approvedNotice = summonDao.findNoticeAddl(getUserDetails(), 2,caselst.getCaseDetails());
			List<NoticeStatus> EsignedNotice = summonDao.findNoticeAddl(getUserDetails(), 3,caselst.getCaseDetails());
			List<NoticeStatus> PendingNotice = summonDao.findNoticeAddl(getUserDetails(), 4,caselst.getCaseDetails());
			List<NoticeStatus> phyPendingNotice = summonDao.findNoticeAddl(getUserDetails(), 5,caselst.getCaseDetails());  
		    List<NoticeStatus> phyApproveNotice = summonDao.findNoticeAddl(getUserDetails(), 6,caselst.getCaseDetails()); 
		stdList.add(new StatusDTO(count,
				  caselst.getCaseDetails().getCaseId(),
				  caselst.getCaseDetails().getCaseTitle(),
				  allNotice.size(),
				  approvedNotice.size(),
				  EsignedNotice.size(),
				  PendingNotice.size(),
				  phyPendingNotice.size(),
				  phyApproveNotice.size()));
				  
				 
		  count++;
		  }
		
		model.addAttribute("stdList", stdList);
		model.addAttribute("StdNotice", unit);

		//model.addAttribute("UnitList", unitList);

		model.addAttribute("date", date);

		return "Report/NoticeReportAddl";
	}
	
	
	public String getLoginUserName() {
		return SecurityContextHolder.getContext().getAuthentication().getName();
	}

	private AppUser getUserDetails() throws Exception {
		AppUser appUser = this.appUserDAO.findUserAccount(getLoginUserName());
		return appUser;
	}
}
