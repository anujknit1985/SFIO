package com.snms.controllers;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.itextpdf.text.pdf.codec.Base64.InputStream;
import com.snms.dao.SummonDao;
import com.snms.dto.APIDTO;
import com.snms.dto.SummonTempDto;
import com.snms.entity.AppRole;
import com.snms.entity.CaseDetails;
import com.snms.entity.NoticeStatus;
import com.snms.entity.SummonStatus;
import com.snms.service.AppRoleRepository;
import com.snms.service.CaseDetailsRepository;
import com.snms.utils.NoticePdf;

@RestController
@RequestMapping("/students")
public class Restcontroller {
	private static final Logger logger = Logger.getLogger(Restcontroller.class);
	
	@Autowired
	private AppRoleRepository appRoleRepo;
	@Autowired
	private CaseDetailsRepository caseDetailsRepository;
	
	@Autowired
	private SummonDao summonDao;
	 
	   // @GetMapping("/{id}/{id1}")  
	@Value("${file.upload}")
	public String filePath;

	@GetMapping("/getData") 
	//http://localhost:9090/SNMS/students/getData?id=3&id1=7
	
	//http://localhost:9090/SNMS/students/getData?summon_type=1&DIN=201820170300014vYp
	    public APIDTO read(@RequestParam Long summon_type,@RequestParam String DIN) throws IOException 
	    {
		    String signFilename; 		
		    APIDTO obj =new APIDTO();
		    DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		    String fnameReg = "^[a-zA-Z0-9\\s]{18,18}$";
	        Pattern pattern = Pattern.compile(fnameReg);
	        Matcher matcher = pattern.matcher(DIN);
	        if (!(DIN.length() ==18) || !matcher.matches() ||!(summon_type==1L||summon_type==2L))
	        	return obj;
	        
		  
		if (summon_type == 1L)
		{
		
		NoticeStatus objnotice =summonDao.findNotice_byDIN(DIN);
		signFilename=objnotice.getSignFile();
		 obj.setCaseNo(objnotice.getCaseDetails().getCaseTitle());
		 			
		 obj.setDateOfAppear( dateFormat.format(objnotice.getCreatedDate()));
		 obj.setName(objnotice.getSummonType().getName());
		}
		else
		{
			SummonStatus objsummon =summonDao.findSummon_byDIN(DIN);
			signFilename=objsummon.getSignFile();
			 obj.setCaseNo(objsummon.getCaseDetails().getCaseTitle());
			// obj.setDateOfAppear(objsummon.getDateOfAppear());
			 obj.setDateOfAppear(dateFormat.format(objsummon.getCreatedDate()));
			 obj.setName(objsummon.getSummonType().getName());
		}
			

		 
	        //Optional<CaseDetails> caseDtls = caseDetailsRepository.findById(46L);			
			//signFilename=caseDtls.get().getLegacyOrderFile(); 
			
		     String	signFile = filePath + File.separator + signFilename;			
		      byte data[] = convertPDFToByteArray(signFile);		     
		      obj.setData(data);
		    
		      return obj;

	   // return appRoleRepo.findById(id);
	    }
	
	private static byte[] convertPDFToByteArray(String filepath) {

        FileInputStream inputStream = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {

            inputStream = new FileInputStream(filepath);

            byte[] buffer = new byte[1024];
            baos = new ByteArrayOutputStream();

            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                baos.write(buffer, 0, bytesRead);
            }

        } catch (FileNotFoundException e) {
           logger.info(e.getMessage());
        } catch (IOException e) {
           logger.info(e.getMessage());
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                   logger.info(e.getMessage());
                }
            }
        }
        return baos.toByteArray();
        }
}
