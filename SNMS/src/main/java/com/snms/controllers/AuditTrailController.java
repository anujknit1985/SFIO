package com.snms.controllers;

import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.snms.dao.AuditBeanDao;
import com.snms.dto.AuditBean;
import com.snms.dto.AuditDto;
import com.snms.entity.AuditTrail;
import com.snms.service.AuditBeanBo;

import com.snms.service.UserDetailsServiceImpl;
import com.snms.utils.SnmsException;
import com.snms.utils.Utils;
import java.util.function.Function;




@Controller
public class AuditTrailController {

	@Autowired
	private UserDetailsServiceImpl userDetailsService;
	
	@Autowired
	private AuditBeanBo auditBeanBo;
	@Autowired
	private Utils utils;
	
	@Autowired
	private AuditBeanDao auditrepo;
	
	@RequestMapping("/showAuditTrail")
	public  String auditTrail(Model modelMap)  throws Exception {
		 modelMap.addAttribute("auditDto", new AuditDto());
	        modelMap.addAttribute("User", userDetailsService.getUserDetails());
	        return "/AuditTrail";
	}
	
	
	

	@RequestMapping(value = "auditTrailReport", method = RequestMethod.POST)
    public String getAuditReport(@ModelAttribute("auditDto") AuditDto auditDto,
            BindingResult result, ModelMap modelMap)
            throws SnmsException, Exception
    {
		String dateStr = auditDto.getDate();
		String toDateStr = auditDto.getToDate();
        String auditFilePath;
        
		/*
		 * UserValidation userValidation = new UserValidation();
		 * userValidation.validateAuditForm(auditDto, result);
		 */
        
		 if (result.hasErrors())
	        {
	            modelMap.addAttribute("auditDto", auditDto);
	            modelMap.addAttribute("auditDate", dateStr);
	            modelMap.addAttribute("User", userDetailsService.getUserDetails());
	            return "/AuditTrail";
	        }
		 
		 
		 
		 /*
		 if (utils.checkCurrentDate(dateStr))
	            auditFilePath = utils.getConfigMessage("log.auditlogFile");
	        else
	            auditFilePath = utils.getConfigMessage("log.auditlogFile") + "."
	                    + dateStr;
	        List<AuditBean> auditList = new ArrayList<AuditBean>();
	        try
	        {
	            BufferedReader br = Files
	                    .newBufferedReader(Paths.get(auditFilePath));
	            
	            auditList = br.lines().filter(line -> !line.isEmpty())
	                    .map(mapToItem).collect(Collectors.toList());
	            br.close();
	        }
	        
	        
	        
	        catch (Exception e)
	        {
	        	
	        	auditDto.setDate("");
	        	return "/AuditTrail";
	        }*/
		 Date date1=new SimpleDateFormat("dd-MM-yyyy" , Locale.ENGLISH).parse(dateStr);
		 Date date2=new SimpleDateFormat("dd-MM-yyyy" , Locale.ENGLISH).parse(toDateStr);
		 dateStr =  new SimpleDateFormat("yyyy-MM-dd").format(date1).toString();
		 toDateStr   = new SimpleDateFormat("yyyy-MM-dd").format(date2).toString();
      // List<AuditTrail> auditList = auditrepo.findAllByActionDate(date1);
		 List<AuditTrail> auditList = auditrepo.findAllByActionDateBetween( dateStr,  toDateStr);
	        modelMap.addAttribute("auditDto", auditDto);
	        modelMap.addAttribute("auditDate", dateStr);
	       modelMap.addAttribute("auditList", auditList);
	        modelMap.addAttribute("User", userDetailsService.getUserDetails());

	        return "/AuditTrail";
	        
	        
	    }
	
	
	 private Function<String, AuditBean> mapToItem = (line) -> {
	        String[] p = line.split("],");
	        AuditBean item = new AuditBean();
	        String act_date, act_id, act_name, act_ip, act_url, act_domain,
	                domain_id, op_type, op_desc, op_time, trgt_user, user_agent,
	                referer;
	        int len = p.length;
	        if (len > 0 && p[1] != null)
	        {
	            act_date = p[0].substring(p[0].indexOf("[DATE:") + 16);
	            item.setDate(act_date);
	        }
	        if (len > 1 && p[1] != null)
	        {
	            act_id = p[1].substring(p[1].indexOf(":") + 1);
	            item.setActorId(Integer.parseInt(act_id));
	        }
	        if (len > 2 && p[2] != null)
	        {
	            act_name = p[2].substring(p[2].indexOf(":") + 1);
	            item.setActorName(act_name);
	        }
	        if (len > 3 && p[3] != null)
	        {
	            act_ip = p[3].substring(p[3].indexOf(":") + 1);
	            item.setActorIp(act_ip);
	        }
	        if (len > 4 && p[4] != null)
	        {
	            act_url = p[4].substring(p[4].indexOf(":") + 1);
	            item.setUrl(act_url);
	        }
	        if (len > 5 && p[5] != null)
	        {
	            act_domain = p[5].substring(p[5].indexOf(":") + 1);
	            item.setDomainName(act_domain);
	        }
	        if (len > 6 && p[6] != null)
	        {
	            domain_id = p[6].substring(p[6].indexOf(":") + 1);
	            item.setDomainId(Integer.parseInt(domain_id));
	        }
	        if (len > 7 && p[7] != null)
	        {
	            op_type = p[7].substring(p[7].indexOf(":") + 1);
	            item.setOperationType(op_type);
	        }
	        if (len > 8 && p[8] != null)
	        {
	            op_desc = p[8].substring(p[8].indexOf(":") + 1);
	            item.setOperationDesc(op_desc);
	        }
	        if (len > 9 && p[9] != null)
	        {
	            op_time = p[9].substring(p[9].indexOf(":") + 1);
	            item.setOpTime(op_time);
	        }
	        if (len > 10 && p[10] != null)
	        {
	            trgt_user = p[10].substring(p[10].indexOf(":") + 1);
	            item.setTargetedUser(trgt_user);
	        }
	        if (len > 11 && p[11] != null)
	        {
	            user_agent = p[11].substring(p[11].indexOf(":") + 1);
	            item.setUserAgent(user_agent);
	        }
	        if (len > 1 && p[len - 1] != null)
	        {
	            referer = p[len - 1].substring(p[len - 1].indexOf(":") + 1,
	                    p[len - 1].length() - 1);
	            item.setReferer(referer);
	        }
	        return item;
	    };
}
