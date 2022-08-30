package com.snms.controllers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
//import java.util.Base64;
import org.bouncycastle.util.encoders.Base64;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.XMLStructure;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.SignedInfo;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureException;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.X509Data;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.nic.eoffice.esign.client.hash.TextVerification;
import org.nic.eoffice.esign.client.parse.ParseXML;
import org.nic.eoffice.esign.client.util.ClientUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfDate;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfSignature;
import com.itextpdf.text.pdf.PdfSignatureAppearance;
import com.itextpdf.text.pdf.PdfSignatureAppearance.RenderingMode;
import com.itextpdf.text.pdf.PdfStamper;
import com.snms.common.ESign1;
import com.snms.dto.EsignDTO;
import com.snms.entity.AppUser;
import com.snms.entity.NoticeStatus;
import com.snms.entity.OfficeOrder;
import com.snms.entity.SummonStatus;
import com.snms.entity.UserDetails;
import com.snms.service.AuditBeanBo;
import com.snms.service.NoticeRepository;
import com.snms.service.OfficeOrderRepository;
import com.snms.service.SummonRepository;
import com.snms.service.UserDetailsServiceImpl;
import com.snms.utils.Utils;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.jsoup.Jsoup;
import org.jsoup.parser.Parser;

@Controller
public class EsignService {

	@Autowired
	Utils utils;
	@Autowired
	private UserDetailsServiceImpl userDetailsService;
	@Autowired
	private OfficeOrderRepository officeOrderRepo;
	@Autowired
	private OfficeOrderRepository officeOrderRepository;
	@Autowired
	private NoticeRepository noticeRepo;
	@Autowired
	private SummonRepository summonRepo;
	private PdfSignatureAppearance appearance;
	private XMLSignatureFactory fac = null;
	@Value("${file.upload}")
	public String filePath;
	@Autowired
	private AuditBeanBo auditBeanBo;
	
	// private String respon;

	/*
	 * public String getRespon() { return respon; }
	 * 
	 * public void setRespon(String respon) { this.respon = respon; }
	 */

	/*@RequestMapping(value = "/unsignPdf")
	public String unsignPdf(ModelMap modelMap) {
		modelMap.addAttribute("esignDTO", new EsignDTO());
		return "esign/unsignPdf";
	}*/
	
	@RequestMapping(value="esignPdf",method=RequestMethod.POST)
	public @ResponseBody String esignPdf(@RequestParam("name") String name,@RequestParam("fileName")String fileName,
			@RequestParam("ooid")Long ooid,@RequestParam("docType")String docType, ModelMap modelMap,HttpServletRequest request) throws IOException,Exception
	{
		
		/*if(ooid == null)
		{
			modelMap.addAttribute("","");
		}*/
		// String fullURL = request.getRequestURL().toString();
		 
		 String requestUrl=((HttpServletRequest) request).getHeader("Referer");
		 System.out.println("requestUrl======="+requestUrl);
		 String appBaseUrl=requestUrl!=null?requestUrl.substring(0, requestUrl.lastIndexOf("/")):"";
		 System.out.println("appUrl======="+appBaseUrl);
		 
		// String fullURL="https://uat-saccess.nic.in";
		sessionMap.put("ooid",ooid);
		
				
		EsignDTO esignDTO = new EsignDTO();
		File file = new File(filePath+File.separator+fileName);
		FileInputStream input = new FileInputStream(file);
		MultipartFile multipartFile = new MockMultipartFile("file",file.getName(), "application/pdf", IOUtils.toByteArray(input));
		esignDTO.setName(name);
		esignDTO.setUserImage(multipartFile);
		esignDTO.setUsername(userDetailsService.getFullName(userDetailsService.getUserDetailssss() ));
		System.out.println("full name - %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%"+esignDTO.getUsername());
		/*if(result.hasErrors()){
			System.out.println(result.getErrorCount());
			modelMap.addAttribute("esignDTO", esignDTO);
			return "esign/unsignPdf";
		}
		String gateway_type;
		if (fullURL.contains("saccess.nic.in"))
				gateway_type="vpn"	;	
		else
			gateway_type="nicnet"	;
			*/
			
		String xml = createHash(esignDTO,docType,appBaseUrl);
		if(xml==null || "".equals(xml.trim()))
			return null;
		else
		{
			esignDTO.setXml(xml);
		modelMap.addAttribute("xml", xml);
		return xml;
		}
	}

	/*@RequestMapping(value = "generateHash", method = RequestMethod.POST)
	public String generateHash(@ModelAttribute("esignDTO") EsignDTO esignDTO, BindingResult result, ModelMap modelMap)
			throws IOException, Exception {
		if (result.hasErrors()) {
			System.out.println(result.getErrorCount());
			modelMap.addAttribute("esignDTO", esignDTO);
			return "esign/unsignPdf";
		}

		String xml = createHash(esignDTO);
		if (xml == null || "".equals(xml.trim()))
			return null;
		else {
			esignDTO.setXml(xml);
			modelMap.addAttribute("esignDTO", esignDTO);
			return "esign/unsignPdf";
		}
	}*/

	public String createHash(EsignDTO esignDTO,String docType,String appBaseUrl) throws IOException, Exception {

		String timestamp = getCurrentDateTimeISOFormat();
		System.out.println("filepatha is " + filePath);
		System.out.println("Image Location:" + filePath);

		
		String eFilename="";
		String eSignGateway="";
		if(docType.equals("order")){
			//esignDTO.setClientrequestURL(utils.getConfigMessage("esign.clientrequestURL"));
			esignDTO.setClientrequestURL(appBaseUrl+"/hello1");
			eFilename = "eOfficeOrder_"+sessionMap.get("ooid")+ ".pdf";
			// eSignGateway = utils.getConfigMessage("esign.gateway");
			eSignGateway = "https://nic-esign2gateway.nic.in/esign/response?rs="+appBaseUrl+"/hello1";
			
			sessionMap.put("ClientrequestURL", appBaseUrl+"/hello1");
		}
		else if(docType.equals("notice")){
			//esignDTO.setClientrequestURL(utils.getConfigMessage("esign.clientrequestURLNotice"));
			esignDTO.setClientrequestURL(appBaseUrl+"/respNotice");
			eFilename = "eNotice_"+sessionMap.get("ooid")+ ".pdf";
			// eSignGateway = utils.getConfigMessage("esign.gatewayNotice");
			eSignGateway= "https://nic-esign2gateway.nic.in/esign/response?rs="+appBaseUrl+"/respNotice";
			sessionMap.put("ClientrequestURL", appBaseUrl+"/respNotice");
		}
		else if(docType.equals("summon"))
		{
			//esignDTO.setClientrequestURL(utils.getConfigMessage("esign.clientrequestURLSummon"));
			esignDTO.setClientrequestURL(appBaseUrl+"/respSummon");
			eFilename = "eSummons_"+sessionMap.get("ooid")+ ".pdf";
	/*		
			 eSignGateway = utils.getConfigMessage("esign.gatewaySummon");*/
			
			eSignGateway= "https://nic-esign2gateway.nic.in/esign/response?rs="+appBaseUrl+"/respSummon";
			sessionMap.put("ClientrequestURL", appBaseUrl+"/respSummon");
		}
//		String eFilename="eOfficeOrder_" +sessionMap.get("ooid")+ ".pdf";
		sessionMap.put("eFilename", eFilename);
		Files.copy(esignDTO.getUserImage().getInputStream(),
				Paths.get(filePath+File.separator + eFilename),
				StandardCopyOption.REPLACE_EXISTING);

		String hash = getDocumentHash(filePath +File.separator+  eFilename,
				filePath +File.separator+eFilename, null, null, esignDTO.getUsername());
		System.out.println("hash value is " + hash);
		sessionMap.put("hash", hash);
		sessionMap.put("name", esignDTO.getName());
		sessionMap.put("fileNametoDisplay", filePath + eFilename);
		sessionMap.put("appearancepdf", appearance);
		sessionMap.put("signingUser", esignDTO.getUsername());
        
		//System.out.println("appearance ......................." + appearance);

		//System.out.println("aadhar number........." + esignDTO.getName());

		UUID uniqueKey = UUID.randomUUID();
		System.out.println(uniqueKey + timestamp);

		String pattern = "yyyy-MM-dd'T'HH:mm:ss";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		timestamp = simpleDateFormat.format(new Date());
		String txntimestamp = new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date());
		//System.out.println(timestamp);
		
		String sequence = officeOrderRepo.getEsignTxnSequence().toString();
		if(sequence.length()<7)
			sequence = "000000".substring(sequence.length()).concat(sequence);
		
			
		String st = "";

		st = "<Esign AuthMode=\"1\" ver=\"2.1\" sc=\"Y\" ts=\"" + timestamp + "\" txn=\""+utils.getConfigMessage("esign.txn") + txntimestamp+"-"+sequence
				+ "\" ekycId=\"\" aspId=\""+utils.getConfigMessage("esign.aspId")+"\" ekycIdType=\"A\" responseSigType=\"pkcs7\" responseUrl=\""+eSignGateway+"\" ><Docs>"
				+ "<InputHash id=\"1\" hashAlgorithm=\"SHA256\" docInfo=\"hello\">" + hash + "</InputHash>\n"
				+ "</Docs></Esign>";

		// System.out.println("xml is "+ st);

		try {
			st = sign(st);
			System.out.println("singing xml is " + st);

		} catch (NoSuchProviderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnrecoverableEntryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (KeyStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		hash = st;
		String xml = hash;
		return xml;

	}

	public String getDocumentHash(String inputFilePath, String outputFilePath, String coOrdinates, String pageNo,
			String userName) {
		String hashdocument = null;
		String[] pdfCordinates = null;

		try {
			Calendar cal = Calendar.getInstance();
			ClientUtil ex = new ClientUtil();
			System.out.println("");
			FileInputStream fis = new FileInputStream(inputFilePath);
			PdfReader reader = new PdfReader(fis);
			fis.close();
			FileOutputStream fout = new FileOutputStream(outputFilePath);
			PdfStamper stamper = PdfStamper.createSignature(reader, fout, ' ', (File) null, true);
			appearance = stamper.getSignatureAppearance();
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss z");
			appearance.setLayer2Text("Digitally signed by " + userName + "\nDate: " + sdf.format(cal.getTime()));
			appearance.setRenderingMode(RenderingMode.DESCRIPTION);
			appearance.setAcro6Layers(false);
			appearance.setCertificationLevel(0);
			appearance.setImage((Image) null);
			if (coOrdinates != null && pageNo != null) {
				pdfCordinates = coOrdinates.split(",");
				appearance.setVisibleSignature(
						new Rectangle(Float.parseFloat(pdfCordinates[0]), Float.parseFloat(pdfCordinates[1]),
								Float.parseFloat(pdfCordinates[2]), Float.parseFloat(pdfCordinates[3])),
						Integer.parseInt(pageNo), (String) null);
			} else {
				int exc = reader.getNumberOfPages();
				appearance.setVisibleSignature(new Rectangle(600.0F, 135.0F, 400.0F, 100.0F), exc, (String) null);
				//appearance.setVisibleSignature("sig1");
			}

			int contentEstimated = 8192;
			HashMap exc1 = new HashMap();
			exc1.put(PdfName.CONTENTS, Integer.valueOf(contentEstimated * 2 + 2));
			PdfSignature dic = new PdfSignature(PdfName.ADOBE_PPKLITE, PdfName.ADBE_PKCS7_DETACHED);
			dic.setDate(new PdfDate(appearance.getSignDate()));
			appearance.setCryptoDictionary(dic);
			appearance.preClose(exc1);

			// sessionMap.put("appearancepdf", appearance);
			System.out.println("appppppppp........" + appearance);
			InputStream is = appearance.getRangeStream();
			byte[] bytes = ClientUtil.toByteArray(is);
			// sessionMap.put("appearancepdf", appearance);
			String generatedHash = ex.generateHash(bytes);
			hashdocument = generatedHash;
		} catch (Exception arg18) {
			arg18.printStackTrace();
		}

		return hashdocument;
	}

	public String sign(String xml) throws NoSuchProviderException, UnrecoverableEntryException, KeyStoreException {
		Document doc = null;
		try {
			fac = XMLSignatureFactory.getInstance("DOM");
			Reference ref = fac
					.newReference("", fac.newDigestMethod("http://www.w3.org/2000/09/xmldsig#sha1", null),
							Collections.singletonList(fac.newTransform(
									"http://www.w3.org/2000/09/xmldsig#enveloped-signature", (XMLStructure) null)),
							null, null);
			SignedInfo si = fac.newSignedInfo(
					fac.newCanonicalizationMethod("http://www.w3.org/TR/2001/REC-xml-c14n-20010315",
							(XMLStructure) null),
					fac.newSignatureMethod("http://www.w3.org/2000/09/xmldsig#rsa-sha1", null),
					Collections.singletonList(ref));
			KeyStore ks = KeyStore.getInstance("JKS");

			// InputStream is = new FileInputStream("D://nicesign.jks");
//			InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("nicesign.jks");  //Staging
			
			InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("047-SFIO-SUMMONS.jks");  //production
//			ks.load(is, "nic-esign0108".toCharArray());
			ks.load(is, "xvKL@27#rWb$6K".toCharArray());

			Enumeration<String> enumAlias = ks.aliases();
			String aliase = null;
			while (enumAlias.hasMoreElements()) {
				aliase = (String) enumAlias.nextElement();
			}

			System.out.println(aliase);

			KeyStore.PrivateKeyEntry keyEntry = (KeyStore.PrivateKeyEntry) ks.getEntry(aliase,
					new KeyStore.PasswordProtection("xvKL@27#rWb$6K".toCharArray()));

			X509Certificate cert = (X509Certificate) keyEntry.getCertificate();

			KeyInfoFactory kif = fac.getKeyInfoFactory();
			ArrayList x509Content = new ArrayList();
			x509Content.add(cert.getSubjectX500Principal().getName());
			x509Content.add(cert);
			X509Data xd = kif.newX509Data(x509Content);
			KeyInfo ki = kif.newKeyInfo(Collections.singletonList(xd));

			DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			InputSource ins = new InputSource();
			ins.setCharacterStream(new StringReader(xml));
			doc = db.parse(ins);

			DOMSignContext dsc = new DOMSignContext(keyEntry.getPrivateKey(), doc.getDocumentElement());
			XMLSignature signature = fac.newXMLSignature(si, ki);
			signature.sign(dsc);

			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer trans = tf.newTransformer();

			OutputStream os = new ByteArrayOutputStream();
			trans.transform(new DOMSource(doc), new StreamResult(os));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (InvalidAlgorithmParameterException e) {
			e.printStackTrace();
		} catch (CertificateException e) {
			e.printStackTrace();
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (MarshalException e) {
			e.printStackTrace();
		} catch (XMLSignatureException e) {
			e.printStackTrace();
		} catch (TransformerFactoryConfigurationError e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}
		String signedXML = convertDocumentToString(doc);

		return signedXML;
	}

	private String convertDocumentToString(Document doc) {
		TransformerFactory tf = TransformerFactory.newInstance();

		String output = null;
		try {
			Transformer transformer = tf.newTransformer();
			StringWriter writer = new StringWriter();
			transformer.transform(new DOMSource(doc), new StreamResult(writer));
			return writer.getBuffer().toString();
		} catch (TransformerException e) {
			e.printStackTrace();
		}
		return null;
	}

	private Map<String, Object> sessionMap = new HashMap<String, Object>();

	public void setSession(Map<String, Object> sessionMap) {
		this.sessionMap = sessionMap;
	}

	public Map<String, Object> getSessionMap() {
		return sessionMap;
	}

	public void setSessionMap(Map<String, Object> sessionMap) {
		this.sessionMap = sessionMap;
	}

	public String getCurrentDateTimeISOFormat() {
		SimpleDateFormat tsFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		Date now = new Date(System.currentTimeMillis() + 3 * 60 * 1000);
		String ts = tsFormat.format(now);
		return ts;
	}

	@RequestMapping(value = "/hello1", method = RequestMethod.POST)
	public String hello(ModelMap modelMap, HttpServletResponse response, HttpServletRequest request,RedirectAttributes redirectAttr)
			throws IOException, Exception {
		String respon = request.getParameter("respon");
System.out.println(respon);
		// 1.check and validate xml.if valid then write pdf into file directory
		// 2.if its not valid then then return error page with message

		org.jsoup.nodes.Document doc = Jsoup.parse(respon, "", Parser.xmlParser());
		doc.normalise();
		System.out.println("response status==" + doc.normalise().select("EsignResp").attr("status"));

		try {
			if (doc.normalise().select("EsignResp").attr("status").equalsIgnoreCase("1".trim())) {
				System.out.println("response is" + respon);
			//	ESign1 ex = new ESign1("ASPNIC", "pfxFilePath", "pfxPassowrd", "pfx alias", utils.getConfigMessage("esign.clientrequestURL"));
				ESign1 ex = new ESign1("ASPNIC", "pfxFilePath", "pfxPassowrd", "pfx alias", sessionMap.get("ClientrequestURL").toString());
				// ESign1 ex=new
				// ESign1("ASPNIC","pfxFilePath","pfxPassowrd","pfx
				// alias","https://nic-esigngateway.nic.in/eSign21/acceptClient");
				
				String userCert=ex.getUserCertificate(respon);
				HashMap<String, String> certUtils=retriveCertDetails(userCert);
				String adhaarName=certUtils.get("commonName").trim();
				String signingUser=sessionMap.get("signingUser").toString();
				if(adhaarName.equalsIgnoreCase(signingUser.trim()))
				{
				appearance = (PdfSignatureAppearance) sessionMap.get("appearancepdf");
				System.out.println("apppppppppppppppppp...." + appearance);
				ex.getSignedPdf(respon, appearance);

				Long id=(Long) sessionMap.get("ooid");
				OfficeOrder officeOrder=officeOrderRepository.findById(id).get();
				
				officeOrder.setIsDSC(0);
				officeOrder.setSignFile(sessionMap.get("eFilename").toString());
				officeOrder.setOrderSignedDate(new Date());
				officeOrder.setIsSigned(1);
				officeOrder.setTxnId(doc.normalise().select("EsignResp").attr("txn"));
				officeOrderRepository.save(officeOrder);
				
				auditBeanBo.setAuditBean(Integer.parseInt(userDetailsService.getUserDetails().getUserId().toString()),
						signingUser, "User", Integer.parseInt(userDetailsService.getUserDetails().getUserId().toString()),
						utils.getMessage("log.OfficeOrder.Esign"), utils.getMessage("log.OfficeOrder.Esigned"), signingUser,
						"true");
				auditBeanBo.save();
				return "redirect:/allSignedOrders"; // redirect to success page
				}
				else {
					auditBeanBo.setAuditBean(Integer.parseInt(userDetailsService.getUserDetails().getUserId().toString()),
							signingUser, "User", Integer.parseInt(userDetailsService.getUserDetails().getUserId().toString()),
							utils.getMessage("log.OfficeOrder.Esign1"), utils.getMessage("log.OfficeOrder.Esigned1"), signingUser,
							"true");
					auditBeanBo.save();
					redirectAttr.addFlashAttribute("errorEsign", "Aadhar name mismatched. Name as per the C-DAC response is  "+adhaarName.trim());
					
					return "redirect:/approvedOrders"; // redirect to error page
					
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		redirectAttr.addFlashAttribute("errorEsign", "Invalid esign");
		return "redirect:/approvedOrders"; // redirect to error page

	}

	/*
	 * private static Document convertStringToDocument(String xmlStr) {
	 * DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	 * DocumentBuilder builder; try { builder = factory.newDocumentBuilder();
	 * Document doc = builder.parse( new InputSource( new StringReader( xmlStr )
	 * ) ); return doc; } catch (Exception e) { e.printStackTrace(); } return
	 * null; }
	 */
	
	@RequestMapping(value = "/respNotice", method = RequestMethod.POST)
	public String respNotice(ModelMap modelMap, HttpServletResponse response, HttpServletRequest request,RedirectAttributes redirectAttr)
			throws IOException, Exception {
		String respon = request.getParameter("respon");
System.out.println(respon);
		// 1.check and validate xml.if valid then write pdf into file directory
		// 2.if its not valid then then return error page with message

		org.jsoup.nodes.Document doc = Jsoup.parse(respon, "", Parser.xmlParser());
		doc.normalise();
		System.out.println("response status==" + doc.normalise().select("EsignResp").attr("status"));

		try {
			if (doc.normalise().select("EsignResp").attr("status").equalsIgnoreCase("1".trim())) {
				System.out.println("response is" + respon);
				//ESign1 ex = new ESign1("ASPNIC", "pfxFilePath", "pfxPassowrd", "pfx alias",	utils.getConfigMessage("esign.clientrequestURLNotice"));
				
				ESign1 ex = new ESign1("ASPNIC", "pfxFilePath", "pfxPassowrd", "pfx alias", sessionMap.get("ClientrequestURL").toString());
				
				// ESign1 ex=new
				// ESign1("ASPNIC","pfxFilePath","pfxPassowrd","pfx
				// alias","https://nic-esigngateway.nic.in/eSign21/acceptClient");
				
				/*Calendar cal = Calendar.getInstance();
				String userCert=ex.getUserCertificate(respon);
				HashMap<String, String> certDetails =retriveCertDetails(userCert);
				System.out.println(certDetails.get("commonName"));
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss z");
				appearance.setLayer2Text("Digitally signed by " + certDetails.get("commonName") + "\nDate: " + sdf.format(cal.getTime()));*/
				
				String userCert=ex.getUserCertificate(respon);
				HashMap<String, String> certUtils=retriveCertDetails(userCert);
				String adhaarName=certUtils.get("commonName").trim();
				
				String signingUser=sessionMap.get("signingUser").toString();
				if(adhaarName.equalsIgnoreCase(signingUser.trim()))
				{
				appearance = (PdfSignatureAppearance) sessionMap.get("appearancepdf");
				System.out.println("apppppppppppppppppp...." + appearance);
				ex.getSignedPdf(respon, appearance);

				Long id=(Long) sessionMap.get("ooid");
				NoticeStatus notice=noticeRepo.findById(id).get();
				
				notice.setIsDSC(0);
				notice.setSignFile(sessionMap.get("eFilename").toString());
				notice.setOrderSignedDate(new Date());
				notice.setIsSigned(1);
				notice.setTxnId(doc.normalise().select("EsignResp").attr("txn"));
				noticeRepo.save(notice);
				auditBeanBo.setAuditBean(Integer.parseInt(userDetailsService.getUserDetails().getUserId().toString()),
						signingUser, "User", Integer.parseInt(userDetailsService.getUserDetails().getUserId().toString()),
						utils.getMessage(
					   "log.Notice.Esign"), utils.getMessage("log.Notice.Esigned"), signingUser,
						"true");
				auditBeanBo.save();
				return "redirect:/getSignedNotices"; // redirect to success page
				}else {
					redirectAttr.addFlashAttribute("errorEsign", "Aadhar name mismatched. Name as per the C-DAC response is  "+adhaarName.trim());
					auditBeanBo.setAuditBean(Integer.parseInt(userDetailsService.getUserDetails().getUserId().toString()),
							signingUser, "User", Integer.parseInt(userDetailsService.getUserDetails().getUserId().toString()),
							utils.getMessage("log.Notice.Esign1"), utils.getMessage("log.Notice.Esigned1"), signingUser,
							"true");
					auditBeanBo.save();
					return "redirect:/approvedNotice"; // redirect to error page
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		redirectAttr.addFlashAttribute("errorEsign", "Invalid esign");
		return "redirect:/approvedNotice"; // redirect to error page

	}

	@RequestMapping(value = "/respSummon", method = RequestMethod.POST)
	public String respSummon(ModelMap modelMap, HttpServletResponse response, HttpServletRequest request,RedirectAttributes redirectAttr)
			throws IOException, Exception {
		String respon = request.getParameter("respon");
System.out.println(respon);
		// 1.check and validate xml.if valid then write pdf into file directory
		// 2.if its not valid then then return error page with message

		org.jsoup.nodes.Document doc = Jsoup.parse(respon, "", Parser.xmlParser());
		doc.normalise();
		System.out.println("response status==" + doc.normalise().select("EsignResp").attr("status"));

		try {
			if (doc.normalise().select("EsignResp").attr("status").equalsIgnoreCase("1".trim())) {
				System.out.println("response is" + respon);
				//ESign1 ex = new ESign1("ASPNIC", "pfxFilePath", "pfxPassowrd", "pfx alias", utils.getConfigMessage("esign.clientrequestURLSummon"));
				
				ESign1 ex = new ESign1("ASPNIC", "pfxFilePath", "pfxPassowrd", "pfx alias", sessionMap.get("ClientrequestURL").toString());
				// ESign1 ex=new
				// ESign1("ASPNIC","pfxFilePath","pfxPassowrd","pfx
				// alias","https://nic-esigngateway.nic.in/eSign21/acceptClient");
				String userCert=ex.getUserCertificate(respon);
				HashMap<String, String> certUtils=retriveCertDetails(userCert);
				String adhaarName=certUtils.get("commonName").trim();
				
				String signingUser=sessionMap.get("signingUser").toString();
				if(adhaarName.equalsIgnoreCase(signingUser.trim() ))
				{
				
				
				appearance = (PdfSignatureAppearance) sessionMap.get("appearancepdf");
				System.out.println("apppppppppppppppppp...." + appearance);
				ex.getSignedPdf(respon, appearance);

				Long id=(Long) sessionMap.get("ooid");
				SummonStatus summon=summonRepo.findById(id).get();
				
				summon.setIsDSC(0);
				summon.setSignFile(sessionMap.get("eFilename").toString());
				summon.setOrderSignedDate(new Date());
				summon.setIsSigned(1);
				summon.setTxnId(doc.normalise().select("EsignResp").attr("txn"));
				summonRepo.save(summon);
				
				auditBeanBo.setAuditBean(Integer.parseInt(userDetailsService.getUserDetails().getUserId().toString()),
						signingUser, "User", Integer.parseInt(userDetailsService.getUserDetails().getUserId().toString()),
						utils.getMessage(
					   "log.Summon.Esign"), utils.getMessage("log.Summon.Esigned"), signingUser,
						"true");
				auditBeanBo.save();
				return "redirect:/getSignedSummons"; // redirect to success page
				}else {
					redirectAttr.addFlashAttribute("errorEsign", "Aadhar name mismatched. Name as per the C-DAC response is  "+adhaarName.trim());
					auditBeanBo.setAuditBean(Integer.parseInt(userDetailsService.getUserDetails().getUserId().toString()),
							signingUser, "User", Integer.parseInt(userDetailsService.getUserDetails().getUserId().toString()),
							utils.getMessage(
						   "log.Summon.Esign1"), utils.getMessage("log.Summon.Esigned1"), signingUser,
							"true");
					auditBeanBo.save();
					return "redirect:/approvedSummon"; // redirect to error page
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		redirectAttr.addFlashAttribute("errorEsign", "Invalid esign");
		return "redirect:/approvedSummon"; // redirect to error page

	}
	
	public java.util.HashMap<String, String> retriveCertDetails(String userCert) throws UnsupportedEncodingException {
		    HashMap<String, String> certDetails = null;
		    if (userCert == null) {
		      System.out.println("User Certificate filed is null");
		      return null;
		    } 
		    try {
		      CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
		      //byte[] certBytes = Base64.getDecoder().decode(userCert.getBytes());
		      
		      //Base64.Decoder decoder = Base64.getDecoder();
//				byte[] certBytes = decoder.decode(userCert);
				byte[] certBytes = Base64.decode(userCert.getBytes("UTF8"));
//				byte[] certBytes = decoder.decode(userCert);
		      
		      InputStream in = new ByteArrayInputStream(certBytes);
		      X509Certificate cert = (X509Certificate)certFactory.generateCertificate(in);
		      System.out.println(cert);
		      certDetails = new HashMap<>();
		      String issuedTo = cert.getSubjectDN().toString();
		      String[] issuedToInfo = issuedTo.split(",");
		      Map<String, String> information = new HashMap<>();
		      byte b;
		      int i;
		      String[] arrayOfString1;
		      for (i = (arrayOfString1 = issuedToInfo).length, b = 0; b < i; ) {
		        String info = arrayOfString1[b];
		        String[] tempInformation = info.split("=");
		        if (tempInformation.length == 2)
		          information.put(tempInformation[0].trim(), tempInformation[1]); 
		        b++;
		      } 
		      certDetails.put("commonName", information.get("CN"));
		      certDetails.put("Email", information.get("EMAILADDRESS"));
		      certDetails.put("Locality", information.get("L"));
		      certDetails.put("state", information.get("ST"));
		      certDetails.put("OU", information.get("OU"));
		      certDetails.put("Org", information.get("O"));
		      certDetails.put("country", information.get("C"));
		      certDetails.put("subject", information.get("EMAILADDRESS"));
		      System.out.println(certDetails.get("commonName"));
		    } catch (CertificateException e) {
		      e.printStackTrace();
		    } 
		    return certDetails;
		  }

	
	
}
