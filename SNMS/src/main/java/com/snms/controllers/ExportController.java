package com.snms.controllers;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.bouncycastle.crypto.modes.EAXBlockCipher;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.errors.IntrusionException;
import org.owasp.esapi.errors.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.itextpdf.text.DocumentException;
import com.snms.dao.AppUserDAO;
import com.snms.dao.SummonDao;
import com.snms.dto.SummonReportDTO;
import com.snms.entity.AppUser;
import com.snms.entity.NoticeStatus;
import com.snms.entity.SummonStatus;
import com.snms.entity.UserDetails;
import com.snms.service.SummonRepository;
import com.snms.service.UserDetailsRepository;
import com.snms.service.UserDetailsServiceImpl;
import com.snms.utils.NoticeExcelExporter;
import com.snms.utils.NoticePdfExporter;
import com.snms.utils.PdfExporter;
import com.snms.utils.SummonExcelExporter;
import com.snms.utils.Utils;
import com.snms.validation.SNMSValidator;

@Controller
public class ExportController {
	@Autowired
	private SummonRepository summonRepo;
	@Autowired
	private UserDetailsServiceImpl userDetailsService;
	@Autowired
	private UserDetailsRepository userDetailsRepo;
	@Autowired
	private SummonDao summonDao;
	@Autowired
	private AppUserDAO appUserDAO;
	@Autowired
	private Utils utils;

	@RequestMapping("/SummonListExcel")
	public void exportSummonToExcel(HttpServletResponse response, SummonReportDTO summonStatusmodel) throws Exception {
		response.setContentType("application/octet-stream");
		DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
		String currentDateTime = dateFormatter.format(new Date());

		String headerKey = "Content-Disposition ";
		String headerValue = "inline; filename=SignedSummons" + currentDateTime + ".xlsx";
		response.setHeader(headerKey, headerValue);

		String isphysically_signed = summonStatusmodel.getIsPhysically_signed();

		SummonReportDTO summonStatus = new SummonReportDTO();
		List<SummonStatus> list = null;
		if (isphysically_signed != null)
			summonStatus.setIsPhysically_signed(isphysically_signed);

		list = summonDao.findSummon_signed(getUserDetails());

		List<SummonStatus> signedList = new ArrayList<SummonStatus>();
		int count = 1;
		for (SummonStatus summonlist : list) {
			System.out.println(summonlist.getId());
			UserDetails userdetail = userDetailsRepo.findAllByUserId(summonlist.getAppUser());
			String issuedBY = userDetailsService.getFullName(userdetail);
			if (isphysically_signed == null) {

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
			} else if (isphysically_signed.equals("true")) {
				if (summonlist.getIs_physicallysent() == true) {

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

			}

			else if (isphysically_signed.equals("false")) {
				if (summonlist.getIs_physicallysent() == false) {

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

			}

		}

		SummonExcelExporter excelExporter = new SummonExcelExporter(signedList);

		excelExporter.export(response);
	}

	
	@RequestMapping("/NoticeListExcel")
	public void exportNoticeToExcel(HttpServletResponse response, SummonReportDTO summonStatusmodel) throws Exception
	{
		response.setContentType("application/octet-stream");
		DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
		String currentDateTime = dateFormatter.format(new Date());

		String headerKey = "Content-Disposition ";
		String headerValue = "inline; filename=SignedNotice" + currentDateTime + ".xlsx";
		response.setHeader(headerKey, headerValue);

		String isphysically_signed= summonStatusmodel.getIsPhysically_signed();
		
		 SummonReportDTO noticeStatus= new SummonReportDTO();
		 if (isphysically_signed != null)
		 noticeStatus.setIsPhysically_signed(isphysically_signed);
		
		NoticeStatus ns = new NoticeStatus();
       List<NoticeStatus> list=summonDao.findNotice_signed(getUserDetails());
       List<NoticeStatus> signedList = new ArrayList<NoticeStatus>();
		int count =1;
		for (NoticeStatus noticelist :list)
		{
			
			UserDetails userdetail = userDetailsRepo.findAllByUserId(noticelist.getAppUser());
			String issuedBY =  userDetailsService.getFullName(userdetail);
			if (isphysically_signed == null)
			{
			
			signedList.add(new NoticeStatus(noticelist.getId(),noticelist.getCaseDetails(),noticelist.getSummonType(),noticelist.getAprrovalStage1(),noticelist.getAprrovalStage2(),
					issuedBY,noticelist.getCreatedDate(),noticelist.getOrderSignedDate(),noticelist.getSummonNo(),noticelist.getDateOfAppear(),noticelist.getIsSensitive(),
					noticelist.getUnsignFile(),noticelist.getSignFile(), noticelist.getIsSigned(),noticelist.getNoticeDin(),noticelist.getApprovalDate(),
					noticelist.getIsDSC(),noticelist.getIsRejected(),noticelist.getIs_physicallysent(),
					noticelist.getRemark(),noticelist.getVerify_id(),noticelist.getApproval_Id()));
		count++;
			}
			else if (isphysically_signed.equals("true"))
			{
			if (noticelist.getIs_physicallysent()==true) 
			{
				signedList.add(new NoticeStatus(noticelist.getId(),noticelist.getCaseDetails(),noticelist.getSummonType(),noticelist.getAprrovalStage1(),noticelist.getAprrovalStage2(),
						issuedBY,noticelist.getCreatedDate(),noticelist.getOrderSignedDate(),noticelist.getSummonNo(),noticelist.getDateOfAppear(),noticelist.getIsSensitive(),
						noticelist.getUnsignFile(),noticelist.getSignFile(), noticelist.getIsSigned(),noticelist.getNoticeDin(),noticelist.getApprovalDate(),
						noticelist.getIsDSC(),noticelist.getIsRejected(),noticelist.getIs_physicallysent(),
						noticelist.getRemark(),noticelist.getVerify_id(),noticelist.getApproval_Id()));
			count++;
			}
			}
			else if (isphysically_signed.equals("false"))
			{
			if (noticelist.getIs_physicallysent()==false) 
			{
				signedList.add(new NoticeStatus(noticelist.getId(),noticelist.getCaseDetails(),noticelist.getSummonType(),noticelist.getAprrovalStage1(),noticelist.getAprrovalStage2(),
						issuedBY,noticelist.getCreatedDate(),noticelist.getOrderSignedDate(),noticelist.getSummonNo(),noticelist.getDateOfAppear(),noticelist.getIsSensitive(),
						noticelist.getUnsignFile(),noticelist.getSignFile(), noticelist.getIsSigned(),noticelist.getNoticeDin(),noticelist.getApprovalDate(),
						noticelist.getIsDSC(),noticelist.getIsRejected(),noticelist.getIs_physicallysent(),
						noticelist.getRemark(),noticelist.getVerify_id(),noticelist.getApproval_Id()));
				count++;
			}
			
			}
			
			
		
		}
       
		NoticeExcelExporter excelExporter = new NoticeExcelExporter(signedList);

		excelExporter.export(response);
	}
	
	
	@RequestMapping("/NoticePdfExcel")
	public void exportNoticeToPDF(HttpServletResponse response, SummonReportDTO summonStatusmodel) throws Exception{
		
		
		response.setContentType("application/pdf");
		DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
		String currentDateTime = dateFormatter.format(new Date());

		String headerKey = "Content-Disposition";
		String headerValue = "attachment; filename=SignedSummon_" + currentDateTime + ".pdf";
		response.setHeader(headerKey, headerValue);

		String isphysically_signed= summonStatusmodel.getIsPhysically_signed();
		
		 SummonReportDTO noticeStatus= new SummonReportDTO();
		 if (isphysically_signed != null)
		 noticeStatus.setIsPhysically_signed(isphysically_signed);
		
		NoticeStatus ns = new NoticeStatus();
      List<NoticeStatus> list=summonDao.findNotice_signed(getUserDetails());
      List<NoticeStatus> signedList = new ArrayList<NoticeStatus>();
		int count =1;
		for (NoticeStatus noticelist :list)
		{
			
			UserDetails userdetail = userDetailsRepo.findAllByUserId(noticelist.getAppUser());
			String issuedBY =  userDetailsService.getFullName(userdetail);
			if (isphysically_signed == null)
			{
			
			signedList.add(new NoticeStatus(noticelist.getId(),noticelist.getCaseDetails(),noticelist.getSummonType(),noticelist.getAprrovalStage1(),noticelist.getAprrovalStage2(),
					issuedBY,noticelist.getCreatedDate(),noticelist.getOrderSignedDate(),noticelist.getSummonNo(),noticelist.getDateOfAppear(),noticelist.getIsSensitive(),
					noticelist.getUnsignFile(),noticelist.getSignFile(), noticelist.getIsSigned(),noticelist.getNoticeDin(),noticelist.getApprovalDate(),
					noticelist.getIsDSC(),noticelist.getIsRejected(),noticelist.getIs_physicallysent(),
					noticelist.getRemark(),noticelist.getVerify_id(),noticelist.getApproval_Id()));
		count++;
			}
			else if (isphysically_signed.equals("true"))
			{
			if (noticelist.getIs_physicallysent()==true) 
			{
				signedList.add(new NoticeStatus(noticelist.getId(),noticelist.getCaseDetails(),noticelist.getSummonType(),noticelist.getAprrovalStage1(),noticelist.getAprrovalStage2(),
						issuedBY,noticelist.getCreatedDate(),noticelist.getOrderSignedDate(),noticelist.getSummonNo(),noticelist.getDateOfAppear(),noticelist.getIsSensitive(),
						noticelist.getUnsignFile(),noticelist.getSignFile(), noticelist.getIsSigned(),noticelist.getNoticeDin(),noticelist.getApprovalDate(),
						noticelist.getIsDSC(),noticelist.getIsRejected(),noticelist.getIs_physicallysent(),
						noticelist.getRemark(),noticelist.getVerify_id(),noticelist.getApproval_Id()));
			count++;
			}
			}
			else if (isphysically_signed.equals("false"))
			{
			if (noticelist.getIs_physicallysent()==false) 
			{
				signedList.add(new NoticeStatus(noticelist.getId(),noticelist.getCaseDetails(),noticelist.getSummonType(),noticelist.getAprrovalStage1(),noticelist.getAprrovalStage2(),
						issuedBY,noticelist.getCreatedDate(),noticelist.getOrderSignedDate(),noticelist.getSummonNo(),noticelist.getDateOfAppear(),noticelist.getIsSensitive(),
						noticelist.getUnsignFile(),noticelist.getSignFile(), noticelist.getIsSigned(),noticelist.getNoticeDin(),noticelist.getApprovalDate(),
						noticelist.getIsDSC(),noticelist.getIsRejected(),noticelist.getIs_physicallysent(),
						noticelist.getRemark(),noticelist.getVerify_id(),noticelist.getApproval_Id()));
				count++;
			}
			
			}
			
			
			
			
			
			
		
		}
		  NoticePdfExporter excelExporter = new NoticePdfExporter(signedList);

			excelExporter.export(response);
	}
	
	
	
	@RequestMapping("/SummonPdfExcel")
	public void exportToPDF(HttpServletResponse response, SummonReportDTO summonStatusmodel) throws Exception {
		response.setContentType("application/pdf");
		DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
		String currentDateTime = dateFormatter.format(new Date());

		String headerKey = "Content-Disposition";
		String headerValue = "attachment; filename=SignedSummon_" + currentDateTime + ".pdf";
		response.setHeader(headerKey, headerValue);

		String isphysically_signed = summonStatusmodel.getIsPhysically_signed();

		SummonReportDTO summonStatus = new SummonReportDTO();
		List<SummonStatus> list = null;
		if (isphysically_signed != null)
			summonStatus.setIsPhysically_signed(isphysically_signed);

		list = summonDao.findSummon_signed(getUserDetails());

		List<SummonStatus> signedList = new ArrayList<SummonStatus>();
		int count = 1;
		for (SummonStatus summonlist : list) {
			System.out.println(summonlist.getId());
			UserDetails userdetail = userDetailsRepo.findAllByUserId(summonlist.getAppUser());
			String issuedBY = userDetailsService.getFullName(userdetail);
			if (isphysically_signed == null) {

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
			} else if (isphysically_signed.equals("true")) {
				if (summonlist.getIs_physicallysent() == true) {

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

			}

			else if (isphysically_signed.equals("false")) {
				if (summonlist.getIs_physicallysent() == false) {

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

			}

		}

               PdfExporter excelExporter = new PdfExporter(signedList);

		excelExporter.export(response);

	}

	public String getLoginUserName() {
		return SecurityContextHolder.getContext().getAuthentication().getName();
	}

	private AppUser getUserDetails() throws Exception {
		AppUser appUser = this.appUserDAO.findUserAccount(getLoginUserName());
		return appUser;
	}
}
