package com.snms.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.snms.dao.promisDAO;
import com.snms.dto.StatusDTO;
import com.snms.entity.CaseDetails;
import com.snms.entity.Company;
import com.snms.entity.RelationpersonCompany;
import com.snms.service.AddCompanyRepository;
import com.snms.service.CaseDetailsRepository;
import com.snms.service.CompanySummonRepository;
import com.snms.service.RelationpersonCompanyRepository;

@RestController
@CrossOrigin(origins="*",methods={RequestMethod.GET,RequestMethod.POST},allowedHeaders={"X-Auth-Token", "Content-Type,Access-Control-Allow-Origin"
		,"Access-Control-Allow-Headers","Authorization","x-requested-With"},exposedHeaders={"Access-Control-Allow-Origin"},allowCredentials="false",maxAge=4800)
@RequestMapping(value="mcaOrderDtl")
public class promisCasedtlAPI {
	@Autowired
	private CaseDetailsRepository caseDetailsRepository;
	
	@Autowired
	private CompanySummonRepository  compSummonRepo;
	
	@Autowired
	private promisDAO    promisdao;
	
	@Autowired
	private  RelationpersonCompanyRepository  relationCompanyRepo;
	@Autowired
	private AddCompanyRepository  compRepo;

	@GetMapping(value="/showmcaOrderDetails" )
	public com.snms.entity.CaseDetails ApprovedpersonDetails(@RequestParam(value="mcaNo") String mcaNo, @RequestParam(value="compName") String compName)
	{
		CaseDetails caseDtl=null;
		mcaNo = mcaNo.replaceAll("\\?", " ");
		compName = compName.replaceAll("\\?", " ");
		//CaseDetails caseDtl   =  caseDetailsRepository.findAllByMcaOrderNo(mcaNo); 
		if(!mcaNo.equalsIgnoreCase("")) {
		 caseDtl   =  promisdao.findAllByMcaOrderNo(mcaNo); 
		}
		if(!compName.equalsIgnoreCase("")) {
			caseDtl   =  promisdao.findAllByCaseTitle(compName); 
		}
	 return caseDtl;
	}
	
	
	
	@GetMapping(value="/showInvcomplist" )
	public List<Object[]> showInvcomplist(@RequestParam(value="invCaseId") Long invCaseId)
	{
		
		//CaseDetails  caseDtl = caseDetailsRepository.findAllById(invCaseId);
		List<Object[]> compLst = promisdao.findCompListByCaseID(invCaseId);
	       System.out.println(compLst);
		//CaseDetails caseDtl   =  caseDetailsRepository.findAllByMcaOrderNo(mcaNo); 
	 
	 return compLst;
	}
	
	
	@GetMapping(value="/showInvKmplist" )
	public List<Object[]> showInvKmplist(@RequestParam(value="invCaseId") Long invCaseId)
	{
		
		CaseDetails  caseDtl = caseDetailsRepository.findAllById(invCaseId);
		List<Object[]> personLst = promisdao.findAllByPersonCaseDetails1(caseDtl);
		 int count = 1;
		 
			
		//CaseDetails caseDtl   =  caseDetailsRepository.findAllByMcaOrderNo(mcaNo); 
	 
	 return personLst;
	}
	
	@GetMapping(value="/searchmcaOrder" )
	public  List<Object[]> searchmcaOrder(@RequestParam(value="mcaNo") String mcaNo)
	{
		
		List<Object[]> caseDtl   =  caseDetailsRepository.findAllByMcaOrderNoStartsWith(mcaNo); 
		
	 return caseDtl;
	}
	
	
}
