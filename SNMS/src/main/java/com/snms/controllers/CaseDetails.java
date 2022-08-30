package com.snms.controllers;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import com.snms.dao.SummonDao;
import com.snms.dao.gamsDao;
import com.snms.dto.StatusDTO;
import com.snms.dto.gamsPersonDto;
import com.snms.entity.CompanySummon;
import com.snms.entity.PersonDATA;
import com.snms.entity.PersonDetails;
import com.snms.entity.RelationpersonCompany;
import com.snms.entity.SummonDetails;
import com.snms.entity.SummonStatus;
import com.snms.entity.SummonType;
import com.snms.entity.UnitDetails;
import com.snms.entity.personcompanyApproval;
import com.snms.service.AddPersonRepository;
import com.snms.service.CaseDetailsRepository;
import com.snms.service.CompanySummonRepository;
import com.snms.service.RelationpersonCompanyRepository;
import com.snms.service.SummonDetailsRepo;
import com.snms.service.SummonTypeNew;
import com.snms.service.UnitDetailsRepository;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@CrossOrigin(origins="*",methods={RequestMethod.GET,RequestMethod.POST},allowedHeaders={"X-Auth-Token", "Content-Type,Access-Control-Allow-Origin"
		,"Access-Control-Allow-Headers","Authorization","x-requested-With"},exposedHeaders={"Access-Control-Allow-Origin"},allowCredentials="false",maxAge=4800)
@RequestMapping(value="caseId")
public class CaseDetails {
	@Autowired 
	private SummonTypeNew  SummonTypeNewDetails;
	@Autowired
	private SummonDao summonDao;
	@Autowired
	
	private CaseDetailsRepository caseRepo; 
	@Autowired
	private SummonDetailsRepo summonDetailsRepo;
	@Autowired
	private CompanySummonRepository companySummonRepo;
	@Autowired
	private gamsDao gamsDao;
	@Autowired
	AddPersonRepository addPersonRepo;
	@Autowired
	RelationpersonCompanyRepository relationpersonCompanyRepo;
	@Autowired
	private UnitDetailsRepository unitDetailsRepo;
//	@PathVariable(value="firstname") String firstname
	
	
		@GetMapping(value="/personName")
		public String getCaseDetails(@RequestParam(value="firstname") String firstName)
		{
			//System.out.println(firstname);
		 int  companySummon= summonDao.findByName(firstName); 
		 if(companySummon!=0) {
			CompanySummon companysummon = companySummonRepo.findById(companySummon).get();
			SummonDetails summon = summonDetailsRepo.findAllByCompanySummon(companysummon);
		
		 com.snms.entity.CaseDetails casedetail = caseRepo.findById(summon.getCaseId()).get();
		 String name;
			if(casedetail!=null)
			name = casedetail.getCaseId();
			else
				name="nodata";
			return name;
		 }
		 return null;
		}

		/*@GetMapping(value="/personDetails")
		public List<PersonDATA> getpersonDetails(@RequestParam(value="panNumber") String panNumber,@RequestParam("passportNumber") String  passportNumber,@RequestParam("aadharNo") String  aadharNo )
		{
			System.out.println(" api details   "+panNumber +passportNumber+aadharNo);
			List<Object[]> personstatus   =  summonDao.findByPanOrPassport(panNumber,passportNumber,aadharNo); 
		 if(!personstatus.isEmpty()) {
			
			 List<PersonDATA> personlist=new ArrayList<PersonDATA>();
				
				if (personstatus.size() > 0)
				{	
				for(Object[] obj:personstatus)
			       		
					
					
					
					personlist.add(new PersonDATA( obj[0].toString(), obj[1].toString(),obj[2].toString(),obj[3].toString() ,Integer.parseInt(obj[4].toString()),obj[5].toString()));
				}
			 
			 return personlist;
		 }
		 return null;
		}*/
		
		
		

		@GetMapping(value="/ApprovedpersonDetails" )
		public List<Object[]> ApprovedpersonDetails()
		{
			
			List<Object[]> personstatus   =  gamsDao.ApprovedpersonDetails(); 
			
		 return personstatus;
		}
		
		@GetMapping(value="/getInvestigationcaseList" )
		public List<Object[]> getInvestigationcaseList()
		{
			
			List<Object[]> caseList = gamsDao.findAllByCaseStage(1 ,Sort.by(Sort.Direction.DESC, "id"));
			
			System.out.println(caseList);
		 return caseList;
		}

		
		@GetMapping(value="/getProsecutioncaseList" )
		public List<Object[]> getProsecutioncaseList()
		{
			
			//List<com.snms.entity.CaseDetails> caseList = caseRepo.findAllByCaseStage(2 ,Sort.by(Sort.Direction.DESC, "id"));
			List<Object[]> caseList = gamsDao.findAllByCaseStage(2 ,Sort.by(Sort.Direction.DESC, "id"));
			
			
		 return caseList;
		}

		
		@GetMapping(value="/getTotalcaseList" )
		public List<com.snms.entity.CaseDetails> getTotalcaseList()
		{
			
			List<com.snms.entity.CaseDetails> caseList = caseRepo.findAll();
			
			
		 return caseList;
		}
		
		
		
		
		@GetMapping(value="/getTotalCUIList" )
		public List<Object[]> getTotalCUIList()
		{
			
			List<Object[]> caseList = gamsDao.findAllByCaseCUI();
			
			
		 return caseList;
		}
		
		
		@GetMapping(value="/updateInGep" )
		public  PersonDetails  updateInGep(@RequestParam(value="personid") int personid)
		{
			
			PersonDetails  personstatus   =  addPersonRepo.findAllByPersonID(personid); 
			personstatus.setIsUpdated(true);
			personstatus.setGepupdatedDate(new Date());
			addPersonRepo.save(personstatus);
		 return personstatus;
		}
		@GetMapping(value="/personDetails" )
		public  PersonDetails  showpersonDetails(@RequestParam(value="personid") int personid)
		{
			
			PersonDetails  personstatus   =  addPersonRepo.findAllByPersonID(personid); 
			
		 return personstatus;
		}
		@GetMapping(value="/personcompanyList" )
		public List<RelationpersonCompany>  personcompanyList(@RequestParam(value="personid") int personid)
		{
			PersonDetails  pd   =  addPersonRepo.findAllByPersonID(personid); 
			List<RelationpersonCompany>  rpc   =  relationpersonCompanyRepo.findAllByPersonDetailsAndIsApprovedAndIsApprovedstage2(pd,true,true,Sort.by(Sort.Direction.DESC, "dateAppointment"));
			
		 return rpc;
		}
		
		@GetMapping(value="/details")
		public String getCaseDetails()
		{
			//System.out.println(firstname);
		 SummonType summondetail = SummonTypeNewDetails.findAll().get(0) ;
			
			return summondetail.getName();
		
		}
		
		
		@GetMapping("formCount")
		public int getCount()
		{
			List<Object[]> personstatus   =  gamsDao.ApprovedpersonDetails(); 
			return  personstatus.size();
		}
		
		
		
		
		@GetMapping(value="/ApprovedpersonDetailsBy")
		public List<Object[]> ApprovedpersonDetailsBy(@RequestParam(value="panNumber") String panNumber,@RequestParam(value="passNum")String passNum)
		{
			
			List<Object[]> personstatus   =  gamsDao.ApprovedpersonDetailsBY(passNum,panNumber); 
			
		 return personstatus;
		}
		
		@GetMapping(value="/showpersonAddedincomp")
		public List<Object[]> showpersonAddedincomp(@RequestParam(value="caseId") long  caseId ,@RequestParam(value="compName") String compName)
		{
			String  compName_decoded=	decode(compName);
			List<Object[]> personstatus   =  gamsDao.personListUnderCompAndCase(caseId,compName_decoded); 
			
		 return personstatus;
		}
		
		@GetMapping(value="/getApprovedPersonCount")
		public List<gamsPersonDto> getApprovedPersonCount()
		{
		//	List<UnitDetails> unitList = unitDetailsRepo.findAll(Sort.by(Sort.Direction.ASC, "unitName"));
			List<UnitDetails> unitList = new ArrayList<UnitDetails>();
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
					}else {
						UnitDetails1.add(new UnitDetails(unitList.get(unitcount).getUnitId(),unitList2.getUnitName(),unitList2.getLocation(),unitList2.getAddress(),unitList2.getTelephoneNo(),unitList2.getFaxNo(),unitList2.getEMail(),unitList2.getCreatedDate()));
					}
					unitcount++;
				}
			List<gamsPersonDto> stdList = new ArrayList<gamsPersonDto>();
			int count =1;
			
			/*
			 * for (UnitDetails unitdtl : unitList) {
			 * 
			 * if(unitdtl.getUnitName().equalsIgnoreCase("Administrator Unit")) {
			 * unitList.remove(unitdtl.getUnitId()); count++; } }
			 */
			
			for (UnitDetails unitdtl : UnitDetails1) {
				List<personcompanyApproval> AllApprovedPerson = gamsDao.personApproveCount(unitdtl.getUnitId());
				List<personcompanyApproval> AllPendingPerson = gamsDao.personPendingCount(unitdtl.getUnitId());
				List<personcompanyApproval> AllReviewPerson = gamsDao.personReviewCount(unitdtl.getUnitId());
				List<personcompanyApproval> AllRejectPerson = gamsDao.personRejectPersonCount(unitdtl.getUnitId());
				
				List<personcompanyApproval> AllPersonCreated = gamsDao.personCreatedCount(unitdtl.getUnitId());
				
				stdList.add(new gamsPersonDto(count, unitdtl.getUnitId(), unitdtl.getUnitName(),AllApprovedPerson.size(),AllPersonCreated.size(),AllPendingPerson.size(),AllReviewPerson.size(),AllRejectPerson.size()));
				count++;
			}
            return stdList;
		}
		
		
		@GetMapping(value="/getApprovedPersonCountByDate")
		public List<gamsPersonDto> getApprovedPersonCountByDate(@RequestParam(value="startDate") String startDate , @RequestParam(value="endDate") String endDate)
		{
			//List<UnitDetails> unitList = unitDetailsRepo.findAll(Sort.by(Sort.Direction.ASC, "unitName"));
			List<UnitDetails> unitList = new ArrayList<UnitDetails>();
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
					}else {
						UnitDetails1.add(new UnitDetails(unitList2.getUnitId(),unitList2.getUnitName(),unitList2.getLocation(),unitList2.getAddress(),unitList2.getTelephoneNo(),unitList2.getFaxNo(),unitList2.getEMail(),unitList2.getCreatedDate()));
					}
					unitcount++;
				}
			
			List<gamsPersonDto> stdList = new ArrayList<gamsPersonDto>();
			int count =1;
			
			/*
			 * for (UnitDetails unitdtl : unitList) {
			 * 
			 * if(unitdtl.getUnitName().equalsIgnoreCase("Administrator Unit")) {
			 * unitList.remove(unitdtl.getUnitId()); count++; } }
			 */
			
			for (UnitDetails unitdtl : UnitDetails1) {
				List<personcompanyApproval> AllApprovedPerson = gamsDao.personApproveCountByDate(unitdtl.getUnitId(),startDate,endDate);
				List<personcompanyApproval> AllPersonCreated = gamsDao.personCreatedCountByDate(unitdtl.getUnitId(),startDate,endDate);
				List<personcompanyApproval> AllPendingPerson = gamsDao.personPendingCountByDate(unitdtl.getUnitId(),startDate,endDate);
				List<personcompanyApproval> AllReviewPerson = gamsDao.personReviewCountByDate(unitdtl.getUnitId(),startDate,endDate);
				List<personcompanyApproval> AllRejectPerson = gamsDao.personRejectPersonCount(unitdtl.getUnitId(),startDate,endDate);			
				stdList.add(new gamsPersonDto(count, unitdtl.getUnitId(), unitdtl.getUnitName(),AllApprovedPerson.size(),AllPersonCreated.size(),AllPendingPerson.size(),AllReviewPerson.size() ,AllRejectPerson.size()  ));
				count++;
			}
            return stdList;
		}
		
		public static String decode(String url)  
	      {  
	                try {  
	                     String prevURL="";  
	                     String decodeURL=url;  
	                     while(!prevURL.equals(decodeURL))  
	                     {  
	                          prevURL=decodeURL;  
	                          decodeURL=URLDecoder.decode( decodeURL, "UTF-8" );  
	                     }  
	                     return decodeURL;  
	                } catch (UnsupportedEncodingException e) {  
	                     return "Issue while decoding" +e.getMessage();  
	                }  
	      }  
		
		
}
