package com.snms.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.xml.crypto.dsig.CanonicalizationMethod;
import javax.xml.crypto.dsig.DigestMethod;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.SignatureMethod;
import javax.xml.crypto.dsig.SignedInfo;
import javax.xml.crypto.dsig.Transform;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.X509Data;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;
import org.bouncycastle.util.encoders.Base64;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfDictionary;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfSignatureAppearance;
import com.itextpdf.text.pdf.PdfString;

 public class ESign1 
  {
	 
	 private static final Logger logger = Logger.getLogger(ESign1.class);
		
	    private static final XPath xPath = XPathFactory.newInstance().newXPath();
	    private final String aspID;
	    private final String pfxFilePath;
	    private final String pfxPassword;
	    private final String pfxAlias;
	    private final String responseURL;
     
	    private  static PdfSignatureAppearance appearance;
	 
      public ESign1(String aspID, String pfxFilePath, String pfxPassword, String pfxAlias, String responseURL)
        {
    	  
         this.aspID = aspID;
         this.pfxFilePath = pfxFilePath;
         this.pfxPassword = pfxPassword;
         this.pfxAlias = pfxAlias;        
         this.responseURL = responseURL;
        }
	
      
/*  public Response requestXml(String AadharNumber, String UniqueTransactionId, String Inputfilepath, String Outputfilepath, SignatureAppearance userAppearance, AuthMode authMode,String documentInfo ) 
   {
         
    	   Response res = new Response();
           int Aadhaarlength = 0;
       
         try {
        	 
             if (AadharNumber == null || AadharNumber.trim().equals(""))
              {
                 res.setStatus(false);
                 res.setErrorMessage("Aadhaar number is not passed.");
                 return res;
              }            
             if (UniqueTransactionId == null || UniqueTransactionId.trim().equals(""))
              {
                 res.setStatus(false);
                 res.setErrorMessage("Transaction id is not passed.");
                 return res;
              }
             if (Inputfilepath == null || Inputfilepath.trim().equals("")) 
              {
                 res.setStatus(false);
                 res.setErrorMessage("Input file path is not passed.");
                 return res;
              }
             if (Outputfilepath == null || Outputfilepath.trim().equals(""))
              {
                 res.setStatus(false);
                 res.setErrorMessage("Output file path is not passed.");
                 return res;
              }
             Aadhaarlength = (int) Math.log10(Long.parseLong(AadharNumber)) + 1;
             if (Aadhaarlength < 12 || Aadhaarlength > 12) 
              {
                 res.setStatus(false);
                 res.setErrorMessage("Length of aadhaar number is not valid.");
                 return res;
              }
             if (authMode == null) 
              {
                 res.setStatus(false);
                 res.setErrorMessage("Authmode type is empty");
                 return res;
              }
             
        PdfReader readerpdf = new PdfReader(Inputfilepath);
        OutputStream fout = new FileOutputStream(Outputfilepath);

        PdfStamper stamperpdf = PdfStamper.createSignature(readerpdf, fout, '\0');
        PdfSignatureAppearance appearance = stamperpdf.getSignatureAppearance();
        appearance.setReason(userAppearance.getReason());
        appearance.setLocation(userAppearance.getLocation());
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, 5);
        appearance.setSignDate(cal);

        appearance.setAcro6Layers(false);
        appearance.setCertificationLevel(PdfSignatureAppearance.NOT_CERTIFIED);
        appearance.setImage(null);

        appearance.setVisibleSignature(userAppearance.getCoordinates(), userAppearance.getPageNumber(), null);
        int contentEstimated = 8192;
        HashMap<PdfName, Integer> exc = new HashMap<>();
        exc.put(PdfName.CONTENTS, contentEstimated * 2 + 2);
        PdfSignature dic = new PdfSignature(PdfName.ADOBE_PPKLITE, PdfName.ADBE_PKCS7_DETACHED);
        dic.setReason(appearance.getReason());
        dic.setLocation(appearance.getLocation());
        dic.setContact(appearance.getContact());
        dic.setDate(new PdfDate(appearance.getSignDate()));
        appearance.setCryptoDictionary(dic);
        appearance.preClose(exc);
        InputStream data = appearance.getRangeStream();
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        byte buf[] = new byte[contentEstimated];
        int n = 0;
        while ((n = data.read(buf, 0, contentEstimated)) > 0) 
         {
            messageDigest.update(buf, 0, n);
         }
        byte hash[] = messageDigest.digest();
        byte[] reqBytesdata = Hex.encode(hash);
        String docHash = new String(reqBytesdata, "UTF8");

        KeyStore.PrivateKeyEntry keyEntry = Utilities.getKeyFromKeyStore(pfxFilePath, pfxPassword.toCharArray(),pfxAlias);
      


        String timestamp = Utilities.getCurrentDateTimeISOFormat();

        int AuthMode = 0;
        switch (authMode) 
          {
            case OTP:
                AuthMode = 1;
                break;
            case FP:
                AuthMode = 2;
                break;
            case IRIS:
                AuthMode = 3;
                break;
          }
        String esignxml = "<Esign ver=\"2.0\" sc=\"Y\" ekycMode=\"U\" ekycIdType=\"A\" ekycId=\"" + AadharNumber + "\" aspId=\"" + aspID + "\" AuthMode=\"" + AuthMode + "\"  responseSigType=\"pkcs7\" preVerified=\"" + "n" + "\"  ts=\"" + timestamp + "\" txn=\"" + UniqueTransactionId +"\" responseUrl=\"" + responseURL +"\">"
                + "<Docs>\n"
                + "<InputHash id=\"1\" hashAlgorithm=\"SHA256\" docInfo=\"" + documentInfo + "\">" + docHash + "</InputHash>\n"
                + "</Docs>"
                + "</Esign>";

        Document XmlDoc = Utilities.convertStringToDocument(esignxml);
        String esignxmlSigned = Utilities.signXML(Utilities.convertDocumentToString(XmlDoc), true, keyEntry);
        byte[]   bytesEncoded = Base64.encode(esignxmlSigned.getBytes());
        
        res.setAppearance(appearance);
        
        res.setRequestXml(new String(bytesEncoded ));
        
        return res;
     }
        
         catch (RuntimeException ex) 
          {
             throw ex;
          } 
         catch (Exception ex)
          {
             res.setStatus(false);
             res.setErrorMessage(ex.toString());
             return res;
          }
     

  }
*/
      
      public static void signDocument(byte[] pkcs7Response,PdfSignatureAppearance appearance1 ) throws IOException, DocumentException, Exception {
  		int contentEstimated = 8192;
  		appearance=appearance1;
  		
  		byte[] paddedSig = new byte[contentEstimated];
  		System.arraycopy(pkcs7Response, 0, paddedSig, 0, pkcs7Response.length);
  		PdfDictionary dic2 = new PdfDictionary();
  		dic2.put(PdfName.CONTENTS, (new PdfString(paddedSig)).setHexWriting(true));
  	
  		appearance.close(dic2);
  	}
      
      
  private static String getEmployeeNameById(Document doc, XPath xpath, int id,PdfSignatureAppearance appearance  ) throws UnsupportedEncodingException, IOException, DocumentException, Exception {
      String name = null;
      try {
          XPathExpression expr =
              xpath.compile("/EsignResp/Signatures/DocSignature[@id='1']/text()");
          name = (String) expr.evaluate(doc, XPathConstants.STRING);
 
     
     
     signDocument( Base64.decode(name.getBytes("UTF8")),appearance);
   /*  expr =
             xpath.compile("/EsignResp/Signatures/DocSignature[@id='1']/text()");
         name = (String) expr.evaluate(doc, XPathConstants.STRING);
         
        PdfHash.getDocumentHash("d:\\dummy.pdf", "d:\\dummy1.pdf",null , null, "sanjiv");
     
         PdfHash.signDocument( Base64.decode(name.getBytes("UTF8")));
   */
      } catch (XPathExpressionException e) {
    		logger.info(e.getMessage());
        
      }

      return name;
  }

  public String getUserCertificate(String input) {
	    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	    DocumentBuilder builder = null;
	    Document eSignResponseDoc = null;
	    XPath xpath = null;
	    StringBuilder userCertificate = new StringBuilder();
	    try {
			/*
			 * factory.setFeature("http://javax.xml.XMLConstants/feature/secure-processing",
			 * true);
			 * factory.setFeature("http://xml.org/sax/features/external-general-entities",
			 * false);
			 * factory.setFeature("http://xml.org/sax/features/external-parameter-entities",
			 * false); factory.setValidating(true);
			 * factory.setFeature("http://xml.org/sax/features/validation", true);
			 */
	      builder = factory.newDocumentBuilder();
	      InputSource is = new InputSource();
	      is.setCharacterStream(new StringReader(input));
	      eSignResponseDoc = builder.parse(is);
	      xpath = XPathFactory.newInstance().newXPath() ;
	      String expression = "/EsignResp/UserX509Certificate";
	      userCertificate.append(xpath.compile(expression).evaluate(eSignResponseDoc));
	    } catch (ParserConfigurationException e) {
	   
	    	logger.info(e.getMessage());
	    } catch (SAXException e) {
	      logger.info(e.getMessage());
	    } catch (IOException e) {
	      logger.info(e.getMessage());
	    } catch (XPathExpressionException e) {
	      logger.info(e.getMessage());
	    } 
	    return userCertificate.toString();
	  }

  public Response getSignedPdf(String eSignResponseXml,PdfSignatureAppearance appearance ) 
   {
	            
		        int contentEstimated = 8192;
		        Response res = new Response();
		        
		     try {
		        	 Document doc = convertStringToDocument(eSignResponseXml);
		             //Read the Public Key
		             String pkcs7response;
		             String WsErrMsg = "";
		             String RespStatus = getXpathValue(xPath, "/EsignResp/@status", doc);
		            
		             getEmployeeNameById( doc,xPath, 0,appearance ) ;
		             if (RespStatus.equals("1"))
		              {
		                 pkcs7response = "1-" + xPath.compile("/EsignResp/Signatures/DocSignature").evaluate(doc);
		              }
		             else
		              {
		                 String errcode = getXpathValue(xPath, "/EsignResp/@errCode", doc);
		                 res.setErrorCode(errcode);
		                 WsErrMsg = xPath.compile("/EsignResp/@errMsg").evaluate(doc);
		                 pkcs7response = "0-" + WsErrMsg;
		              }

		             String pkcsres = pkcs7response;
		             String[] result = pkcsres.split("-");
		             String pkcsressuccessfailure = result[0];
		             String returnedstring = result[1];
		             byte[] PKCS7Response = Base64.decode(returnedstring.getBytes("UTF8"));
		                
		           // PdfHash.signDocument(PKCS7Response);
		 			
		 			
		             if (!pkcsressuccessfailure.equals("0"))
		               {
		                 PKCS7Response = Base64.decode(returnedstring.getBytes("UTF8"));
		                 byte[] paddedSig = new byte[contentEstimated];
		                 System.arraycopy(PKCS7Response, 0, paddedSig, 0, PKCS7Response.length);
		                 PdfDictionary dic2 = new PdfDictionary();
		                 dic2.put(PdfName.CONTENTS, new PdfString(paddedSig).setHexWriting(true));
		                 //appearance.close(dic2);
		                 res.setStatus(true);
		                 res.setErrorMessage("Pdf is signed successfully");
		                 return res;
		               } 
		              else
		               {
		                 res.setStatus(false);
		                 res.setErrorMessage(WsErrMsg);
		                 return res;
		               }
		       }
		         catch (RuntimeException ex)
		           {
		             throw ex;
		           } 
		         catch (Exception ex)
		           {
		             res.setStatus(false);
		             res.setErrorMessage(ex.toString());
		             return res;
		    		}
 
    }
	 
  
	
    public static class Response 
      {

    	private String requestXml;
    	private PdfSignatureAppearance appearance;                 
        private String ErrorMessage;
        private Boolean Status;       
        private String ErrorCode; 
       
        
       
       
        public void setRequestXml(String requestXml)
         {
        	this.requestXml=requestXml;
         }
               
        public String getRequestXml()
         {
        	return requestXml;
         }
 
        
        public void setAppearance(PdfSignatureAppearance appearance)
         {
        	this.appearance=appearance;
         }
        
        public PdfSignatureAppearance getAppearence()
         {
        	
        	return appearance;
         }
        
        
        
        public String getErrorMessage() 
         {
            return ErrorMessage;
         }
        
        protected void setErrorMessage(String ErrorMessage)
         {
            this.ErrorMessage = ErrorMessage;
         }

  
        
        public Boolean getStatus() 
         {
            return Status;
         }
  
        protected void setStatus(Boolean Status) 
         {
            this.Status = Status;
         }

      
        public String getErrorCode()
         {
            return ErrorCode;
         }

        public void setErrorCode(String ErrorCode)
         {
            this.ErrorCode = ErrorCode;
         }
   }

    
    
 /* public static class SignatureAppearance
     {
        private final String reason;
        private final String location;
        private final Rectangle coordinates;
        private final int pageNumber;

        public SignatureAppearance(String reason,String location,Rectangle coordinates,int pageNumber)
         {
            this.reason = reason;
            this.location = location;
            this.coordinates = coordinates;
            this.pageNumber = pageNumber;
          }

        
        public String getReason() 
         {
            return reason;
         }

       
        public String getLocation()
         {
            return location;
         }

        
        public Rectangle getCoordinates()
         {
            return coordinates;
         }

       
        public int getPageNumber() 
         {
            return pageNumber;
         }
        
  }*/

 
   public enum AuthMode
    {
        OTP,        
        FP,        
        IRIS;
    }

 //public static class Utilities {

      private static final String prov = System.getProperty("jsr105Provider", "org.jcp.xml.dsig.internal.dom.XMLDSigRI");

      public static KeyStore.PrivateKeyEntry getKeyFromKeyStore(String keyStoreFile, char[] keyStorePassword, String alias) throws KeyStoreException, FileNotFoundException, IOException, NoSuchAlgorithmException, UnrecoverableEntryException, CertificateException 
       {
            FileInputStream keyFileStream = null;
            try
             {
                KeyStore ks = KeyStore.getInstance("PKCS12");
                keyFileStream = new FileInputStream(keyStoreFile);
                ks.load(keyFileStream, keyStorePassword);
                KeyStore.PrivateKeyEntry entry = (KeyStore.PrivateKeyEntry) ks.getEntry(alias, new KeyStore.PasswordProtection(keyStorePassword));
                keyFileStream.close();
                return entry;
             } 
            catch (RuntimeException | KeyStoreException | IOException | NoSuchAlgorithmException | CertificateException | UnrecoverableEntryException ex) 
             {
                if (keyFileStream != null)
                  {
                    keyFileStream.close();
                  }
                throw ex;
             }
        }

      
      /*public static String signXML(String xmlDocument, boolean includeKeyInfo, KeyStore.PrivateKeyEntry keyEntry) throws TransformerException, ParserConfigurationException, Exception 
         {
            // Parse the input XML
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            Document inputDocument = dbf.newDocumentBuilder().parse(new InputSource(new StringReader(xmlDocument)));
            // Sign the input XML's DOM document
            Document signedDocument = sign(inputDocument, includeKeyInfo, keyEntry);
            // Convert the signedDocument to XML String
            StringWriter stringWriter = new StringWriter();
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer trans = tf.newTransformer();
            trans.transform(new DOMSource(signedDocument), new StreamResult(stringWriter));
            return stringWriter.getBuffer().toString();
         }      
      */
      

    public  String getCurrentDateTimeISOFormat()
         {
        	SimpleDateFormat tsFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            Date now = new Date(System.currentTimeMillis() + 3 * 60 * 1000);
            String ts = tsFormat.format(now);
            
            return ts;
         }
    
       
     public  String getXpathValue(XPath xPath, String RequestPath, Document doc) throws XPathExpressionException 
         {
            String XpathValue = xPath.compile(RequestPath).evaluate(doc);
            xPath.reset();
            return XpathValue;
         }
     

      public Document convertStringToDocument(String xmlStr) throws SAXException, ParserConfigurationException, IOException
         {
    	  DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
          DocumentBuilder builder;
          factory.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", true);
          factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
			factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
			factory.setValidating(true);
  	    factory.setFeature("http://xml.org/sax/features/validation", true);
          builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new InputSource(new StringReader(xmlStr)));
            return doc;
         }

        public  String convertDocumentToString(Document doc) throws TransformerException 
         {
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer;
           
            tf.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", true);
            tf.setFeature("http://xml.org/sax/features/external-general-entities", false);
			tf.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            transformer = tf.newTransformer();
            // below code to remove XML declaration
            // transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(doc), new StreamResult(writer));
            String output = writer.getBuffer().toString();
            return output;

         }


     public Document sign(Document xmlDoc, boolean includeKeyInfo, KeyStore.PrivateKeyEntry keyEntry)throws Exception 
        {
            if (System.getenv("SKIP_DIGITAL_SIGNATURE") != null) 
             {
                return xmlDoc;
             }
            // Creating the XMLSignature factory.
            XMLSignatureFactory factory = XMLSignatureFactory.getInstance("DOM", (Provider) Class.forName(prov).newInstance());
            DigestMethod digestMethod = factory.newDigestMethod(DigestMethod.SHA1, null);
            Transform transform = factory.newTransform(Transform.ENVELOPED, (TransformParameterSpec) null);
            Reference reference = factory.newReference("", digestMethod, Collections.singletonList(transform), null, null);
            CanonicalizationMethod canonicalizationMethod = factory.newCanonicalizationMethod(CanonicalizationMethod.INCLUSIVE_WITH_COMMENTS, (C14NMethodParameterSpec) null);
            SignatureMethod signatureMethod = factory.newSignatureMethod(SignatureMethod.RSA_SHA1, null);
            SignedInfo sInfo = factory.newSignedInfo(canonicalizationMethod, signatureMethod, Collections.singletonList(reference));
            //keyEntry = getKeyFromKeyStore(AspSigngingPfxFilePath, AspSigngingPfxPassword.toCharArray(), AspSigngingPfxAlias);
            if (keyEntry == null) 
             {
                throw new RuntimeException("Key could not be read for digital signature. Please check value of signature alias and signature password, and restart the Auth Client");
             }
            X509Certificate x509Cert = (X509Certificate) keyEntry.getCertificate();
            KeyInfo kInfo = getKeyInfo(x509Cert, factory);
            DOMSignContext dsc = new DOMSignContext(keyEntry.getPrivateKey(), xmlDoc.getDocumentElement());
            XMLSignature signature = factory.newXMLSignature(sInfo, includeKeyInfo ? kInfo : null);
            signature.sign(dsc);
            Node node = dsc.getParent();
            return node.getOwnerDocument();
            
        }
        
     
       public  KeyInfo getKeyInfo(X509Certificate cert, XMLSignatureFactory fac)
         {
            // Create the KeyInfo containing the X509Data.
    	   // Create the KeyInfo containing the X509Data.
           KeyInfoFactory kif = fac.getKeyInfoFactory();
           List x509Content = new ArrayList();
           x509Content.add(cert.getSubjectX500Principal().getName());
           x509Content.add(cert);
           X509Data xd = kif.newX509Data(x509Content);
           return kif.newKeyInfo(Collections.singletonList(xd));
          }
       
  //}

 
    
}
 
