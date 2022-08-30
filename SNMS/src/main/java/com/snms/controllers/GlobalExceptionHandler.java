package com.snms.controllers;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.snms.entity.CaseDetails;
import com.snms.validation.CaseJsonResponse;

@ControllerAdvice
public class GlobalExceptionHandler {

    //commons-fileuploa
	
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public @ResponseBody CaseJsonResponse handleError2(MaxUploadSizeExceededException e, RedirectAttributes redirectAttributes) {

    	
    	String sname =  e.getCause().getMessage().toString();
        redirectAttributes.addFlashAttribute("message", e.getCause().getMessage());
        
        CaseJsonResponse caseJsonResponse = new CaseJsonResponse();
    	Map<String, String> errors = new HashMap<String, String>();
    	errors.put("mcaOrderFile", "invalid FIleName");
        CaseDetails caseDetails = new CaseDetails();
        caseDetails.setFileError("ErrorFile");
    	caseDetails.setMcaFile(null);
		caseDetails.setCourtFile(null);
		caseDetails.setOfficeOrderFile(null);
		caseDetails.setDeleteIoOrder(null);
		caseJsonResponse.setErrorsMap(errors);
		caseJsonResponse.setCaseDetails(caseDetails);
		caseJsonResponse.setStatus("ERRORfile");
        return caseJsonResponse;

    }
   /*
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String handleMaxSizeException(
    	      MaxUploadSizeExceededException exc, 
    	      HttpServletRequest request,
    	      HttpServletResponse response,ModelMap modelMAp) {
    	 
    	modelMAp.addAttribute("message", "File too large!");
    	    //    modelAndView.getModel().put("message", "File too large!");
    	        return "caseDetails/ErrorPage";
    	    }
*/
}