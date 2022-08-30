package com.snms.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.impl.xb.xsdschema.Public;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.List;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.RomanList;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfAnnotation;
import com.itextpdf.text.pdf.PdfFormField;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.VerticalPositionMark;

import com.snms.dto.SummonDto;

public class SummonPDF {
	private static final Logger logger = Logger.getLogger(SummonPDF.class);

	/*
	 * public static ByteArrayInputStream summonFixed(SummonDto summon) throws
	 * MalformedURLException, IOException {
	 * 
	 * Document document = new Document(); ByteArrayOutputStream out = new
	 * ByteArrayOutputStream();
	 * 
	 * try {
	 * 
	 * PdfWriter.getInstance(document, out); document.open();
	 * 
	 * // Add Text to PDF file ->
	 * 
	 * Image
	 * image=Image.getInstance("http://localhost:9090/SNMS/images/emblem-dark.png");
	 * image.setAlignment(Element.ALIGN_CENTER); image.scaleToFit(100.0f, 50.0f);
	 * 
	 * Font font = FontFactory.getFont(FontFactory.TIMES_BOLD, 12, BaseColor.BLACK);
	 * //Font f8 = FontFactory.getFont(FontFactory.TIMES_BOLD, 8, BaseColor.BLACK);
	 * 
	 * Font font1 = FontFactory.getFont(FontFactory.TIMES, 11, BaseColor.BLACK);
	 * 
	 * Paragraph head = new Paragraph(); // head.setAlignment(Element.ALIGN_CENTER);
	 * head.add(image); document.add(head);
	 * 
	 * Paragraph para = new Paragraph("Government of India\r\n" +
	 * "Ministry of Corporate Affairs\r\n" +
	 * "Serious Fraud Investigation Office\r\n" + "", font);
	 * para.setAlignment(Element.ALIGN_CENTER); document.add(para);
	 * 
	 * Paragraph add = new Paragraph(
	 * "2nd Floor Pt. Deendayal Antyodaya Bhawan,\r\n" +
	 * "CGO Complex Lodhi Road,New delhi-110003\r\n" + "", font);
	 * add.setAlignment(Element.ALIGN_CENTER);
	 * 
	 * document.add(add); //document.add(Chunk.NEWLINE);
	 * 
	 * Chunk glue = new Chunk(new VerticalPositionMark()); Paragraph gh = new
	 * Paragraph("No : " + summon.getOderId()); gh.add(new Chunk(glue));
	 * gh.add("Date : " + new Utils().currentDate()); document.add(gh);
	 * 
	 * Chunk underline = new Chunk("SUMMONS"); underline.setUnderline(0.1f, -2f);
	 * //0.1 thick, -2 y-location
	 * 
	 * Phrase phrase = new Phrase(); phrase.add(underline);
	 * 
	 * Paragraph p = new Paragraph(); p.add(phrase);
	 * p.setAlignment(Element.ALIGN_CENTER); document.add(p);
	 * 
	 * document.add(Chunk.NEWLINE);
	 * 
	 * if(summon.getSize()==0) { Paragraph para1 = new Paragraph(
	 * "     In exercise of powers conferred under Section 212(1)(c) of the Companies Act, 2013, the Central Government vide order vide order No.: "
	 * + summon.getOderId() + " dated : " +
	 * summon.getOrderDate()+" assigned SFIO to investigate the affairs of "+summon.
	 * getCompanyName(),font1); para1.setAlignment(Element.ALIGN_JUSTIFIED);
	 * 
	 * document.add(para1); } else { Paragraph para1 = new Paragraph(
	 * "    In exercise of powers conferred under Section 212(1)(c) of the Companies Act, 2013, the Central Government vide order vide order No.: "
	 * + summon.getOderId() + " dated : " +
	 * summon.getOrderDate()+" assigned SFIO to investigate the affairs of "+summon.
	 * getCompanyName()+" and "+summon.getSize()+" other companies.",font1);
	 * para1.setAlignment(Element.ALIGN_JUSTIFIED);
	 * 
	 * document.add(para1); } document.add(Chunk.NEWLINE); List company = new
	 * RomanList();
	 * 
	 * for (String str : summon.getCompany()) { company.add(str); }
	 * 
	 * document.add(company);
	 * 
	 * document.add(Chunk.NEWLINE);
	 * 
	 * Paragraph para2 = new Paragraph(
	 * "2       In exercise of powers conferred under section 212(1) of the Companies Act, 2013, Director, SFIO has appointed under signed as Investigating Officer along with "
	 * +summon.getRemainInsSize()+" other inspectors.",font1);
	 * para2.setAlignment(Element.ALIGN_JUSTIFIED);
	 * 
	 * document.add(para2); document.add(Chunk.NEWLINE);
	 * 
	 * Paragraph para3 = new Paragraph(
	 * "3       In exercise of powers vested with the undersigned under Section 217 of the Companies Act, 2013, sh."
	 * +summon.getCompanyDir()+","+summon.getDesignation().toLowerCase()+" of "
	 * +summon.getCompanyName()
	 * +" is hereby summoned to appear before the undersigned personally at Serious Fraud Investigation Office, 2nd Floor, Pt. Deen Dayal Antodaya Bhawan, CGO Complex, Lodhi Road, New Delhi-110003 on "
	 * +summon.getReportingDate()
	 * +".for examination in connection with the investigation into the affairs of above said companies."
	 * ,font1); para3.setAlignment(Element.ALIGN_JUSTIFIED);
	 * 
	 * document.add(para3); document.add(Chunk.NEWLINE);
	 * 
	 * Paragraph para4 = new Paragraph(
	 * "4       You are required to bring with you all documents as listed in ANNEXURE – A (copy enclosed) and all other relevant documents / information which is in your procession. You are also required to bring your Identity Card, Pan Card, two photographs as well as relevant documents such as personal bank account and properties details etc. and not to depart without permission of the undersigned. As the aforesaid investigation is time bound, this may please be noted that any request for grant of time/adjournment may not be permissible."
	 * ,font1); para4.setAlignment(Element.ALIGN_JUSTIFIED);
	 * 
	 * document.add(para4); document.add(Chunk.NEWLINE);
	 * 
	 * Paragraph para5 = new
	 * Paragraph("5       The non-compliance of the directions contained in summons shall make you liable to be prosecuted under the provisions of Section 217 sub section (8) of the Companies Act, 2013"
	 * ,font1); para5.setAlignment(Element.ALIGN_JUSTIFIED);
	 * 
	 * document.add(para5); document.add(Chunk.NEWLINE);
	 * 
	 * Paragraph para6 =new Paragraph("("+summon.getIo()+")\r\n" +
	 * "Investigating Officer\r\n" + "",font1);
	 * 
	 * para6.setAlignment(Element.ALIGN_RIGHT);
	 * 
	 * document.add(para6);
	 * 
	 * Paragraph copyto = new
	 * Paragraph("To,\r\nSh."+summon.getCompanyDir()+","+summon.getDesignation()+
	 * ",\r\n"+summon.getCompanyName()+" ,\r\nAddress "+summon.getCompanyAddress(),
	 * font1); copyto.setAlignment(Element.ALIGN_LEFT);
	 * 
	 * document.add(copyto);
	 * 
	 * 
	 * 
	 * document.close(); } catch (DocumentException e) {
	 * logger.info(e.getMessage()); }
	 * 
	 * return new ByteArrayInputStream(out.toByteArray()); }
	 */

	public static boolean isNullOrEmpty(String str) {
		if (str != null && !str.isEmpty())
			return false;
		return true;
	}

	public static ByteArrayInputStream summonFixed(SummonDto summon) throws MalformedURLException, IOException {

		Document document = new Document();
		ByteArrayOutputStream out = new ByteArrayOutputStream();

		try {

			PdfWriter.getInstance(document, out);
			document.open();

			// Add Text to PDF file ->

			Image image = Image.getInstance(new Utils().getConfigMessage("image.emblemPath"));
			image.setAlignment(Element.ALIGN_CENTER);
			image.scaleToFit(100.0f, 50.0f);

			Font font = FontFactory.getFont(FontFactory.TIMES_BOLD, 12, BaseColor.BLACK);
			// Font f8 = FontFactory.getFont(FontFactory.TIMES_BOLD, 8, BaseColor.BLACK);

			

			Font font1 = FontFactory.getFont(FontFactory.TIMES, 11, BaseColor.BLACK);

			Paragraph head = new Paragraph();
//			head.setAlignment(Element.ALIGN_CENTER);
			head.add(image);
			document.add(head);

			Paragraph para = new Paragraph("Government of India\r\n" + "Ministry of Corporate Affairs\r\n"
					+ "Serious Fraud Investigation Office\r\n" + "", font);
			para.setAlignment(Element.ALIGN_CENTER);
			document.add(para);

			Paragraph add = new Paragraph(summon.getOfficeAddress() + "", font);
			add.setAlignment(Element.ALIGN_CENTER);

			document.add(add);
			// document.add(Chunk.NEWLINE);

			Chunk glue = new Chunk(new VerticalPositionMark());
			Paragraph gh = null;
			if (null != summon.getOderId())
				gh = new Paragraph("DIN : " + summon.getOderId());
			else
				gh = new Paragraph("DIN : ");
			gh.add(new Chunk(glue));
			if (null != summon.getApprovalDate())
				gh.add("Date : " + new SimpleDateFormat("dd-MM-yyyy").format(summon.getApprovalDate()));
			else
				gh.add("Date : ");
			document.add(gh);

			Chunk underline = new Chunk("SUMMONS");
			underline.setUnderline(0.1f, -2f); // 0.1 thick, -2 y-location

			Phrase phrase = new Phrase();
			phrase.add(underline);

			Paragraph p = new Paragraph();
			p.add(phrase);
			p.setAlignment(Element.ALIGN_CENTER);
			document.add(p);

			document.add(Chunk.NEWLINE);

			if (summon.getSize() == 0) {
				Paragraph para1 = new Paragraph(
						"     In exercise of powers conferred under Section 212(1)(c) of the Companies Act, 2013, the Central Government vide order vide order No.: "
								+ summon.getMcaOrderNo() + " dated : " + summon.getOrderDate()
								+ " assigned SFIO to investigate the affairs of " + summon.getCompanyName(),
						font1);
				para1.setAlignment(Element.ALIGN_JUSTIFIED);

				document.add(para1);
			} else {
				Paragraph para1 = new Paragraph(
						"    In exercise of powers conferred under Section 212(1)(c) of the Companies Act, 2013, the Central Government vide order vide order No.: "
								+ summon.getOderId() + " dated : " + summon.getOrderDate()
								+ " assigned SFIO to investigate the affairs of " + summon.getCompanyName() + " and "
								+ summon.getSize() + " other companies.",
						font1);
				para1.setAlignment(Element.ALIGN_JUSTIFIED);

				document.add(para1);
			}
			document.add(Chunk.NEWLINE);

			/*
			 * for (String str : summon.getCompany()) { company.add(str); }
			 * 
			 * document.add(company);
			 */

			// document.add(Chunk.NEWLINE);

			Paragraph para2 = new Paragraph(
					"2       In exercise of powers conferred under section 212(1) of the Companies Act, 2013, Director, SFIO has appointed under signed as Investigating Officer along with "
							+ summon.getRemainInsSize() + " other inspectors.",
					font1);
			para2.setAlignment(Element.ALIGN_JUSTIFIED);

			document.add(para2);
			document.add(Chunk.NEWLINE);

			Paragraph para3 = new Paragraph(
					"3       In exercise of powers vested with the undersigned under Section 217 of the Companies Act, 2013, sh."
							+ summon.getCompanyDir() + "," + summon.getDesignation().toLowerCase() + " of "
							+ summon.getCompanyName()
							+ " is hereby summoned to appear before the undersigned personally at Serious Fraud Investigation Office, 2nd Floor, Pt. Deen Dayal Antodaya Bhawan, CGO Complex, Lodhi Road, New Delhi-110003 on "
							+ summon.getReportingDate()
							+ ".for examination in connection with the investigation into the affairs of above said companies.",
					font1);
			para3.setAlignment(Element.ALIGN_JUSTIFIED);

			document.add(para3);
			document.add(Chunk.NEWLINE);

			// company Disalble By Gouthami 09/10/2020
			/*
			 * List company = new RomanList();
			 * 
			 * company.add(summon.getComapnyPara()); document.add(company);
			 * document.add(Chunk.NEWLINE);
			 */

			Paragraph para4 = new Paragraph(
					"4       You are required to bring with you all documents as listed in ANNEXURE – A (copy enclosed) and all other relevant documents / information which is in your procession. You are also required to bring your Identity Card, Pan Card, two photographs as well as relevant documents such as personal bank account and properties details etc. and not to depart without permission of the undersigned. As the aforesaid investigation is time bound, this may please be noted that any request for grant of time/adjournment may not be permissible.",
					font1);
			para4.setAlignment(Element.ALIGN_JUSTIFIED);

			document.add(para4);
			document.add(Chunk.NEWLINE);

			Paragraph para5 = new Paragraph(
					"5       The non-compliance of the directions contained in summons shall make you liable to be prosecuted under the provisions of Section 217 sub section (8) of the Companies Act, 2013",
					font1);
			para5.setAlignment(Element.ALIGN_JUSTIFIED);

			document.add(para5);
			document.add(Chunk.NEWLINE);

			Paragraph para6 = new Paragraph(summon.getIo() + "\r\n" + "(" + summon.getIODesignation() + ")\r\n" + "",
					font1);

			para6.setAlignment(Element.ALIGN_RIGHT);

			document.add(para6);

			String footer = "To,\r\nSh." + summon.getCompanyDir();

			if (!(isNullOrEmpty(summon.getDesignation()))) {
				footer = footer + "," + summon.getDesignation();
			}

			if (summon.getCompanyName() != null && summon.getCompanyName() != "")
				footer = footer + "," + summon.getCompanyName();

			footer = footer + "\r\nAddress:" + summon.getCompanyAddress();

			Paragraph copyto = new Paragraph(footer, font1);
			copyto.setAlignment(Element.ALIGN_LEFT);

			document.add(copyto);

			// Paragraph copyto = new
			// Paragraph("To,\r\nSh."+summon.getCompanyDir()+","+summon.getCompanyName()+"
			// ,\r\nAddress: "+summon.getCompanyAddress(),font1);
			// copyto.setAlignment(Element.ALIGN_LEFT);

			// document.add(copyto);

			document.close();
		} catch (DocumentException e) {
			logger.info(e.getMessage());
		}

		return new ByteArrayInputStream(out.toByteArray());
	}

	public static ByteArrayInputStream summonSaved(SummonDto summon) throws MalformedURLException, IOException {

		Document document = new Document();
		ByteArrayOutputStream out = new ByteArrayOutputStream();

		try {

			PdfWriter.getInstance(document, out);
			document.open();

			// Add Text to PDF file ->
			
			//http://localhost:9091/SNMS/src/main/resources/static/
	        //final  String hindifont =  "fonts/Mangal Regular.ttf";
		//	String   hindifont = new Utils().getConfigMessage("http://localhost:9091/SNMS/src/main/resources/static/fonts/Mangal Regular.ttf");
		//	String fontname =  getResource(hindifont).toString();
	        //String fontname = SummonPDF.class.getResource(hindifont).toString();
	        //FontFactory.register(fontname);
			
			String   hindifont =new Utils().getConfigMessage("hindi.font");


			  FontFactory.register("F:\\Softwares\\mangal-regular\\Mangal Regular");
		        Font f1 =FontFactory.getFont("Mangal", BaseFont.IDENTITY_H, true);
		           //sample text
			Image image = Image.getInstance(new Utils().getConfigMessage("image.emblemPath"));
			image.setAlignment(Element.ALIGN_CENTER);
			image.scaleToFit(100.0f, 50.0f);

			Font font = FontFactory.getFont(FontFactory.TIMES_BOLD, 12, BaseColor.BLACK);
			// Font f8 = FontFactory.getFont(FontFactory.TIMES_BOLD, 8, BaseColor.BLACK);
     		Font font1 = FontFactory.getFont(FontFactory.TIMES, 11, BaseColor.BLACK);
			/*
			 * BaseFont baseFont3 = BaseFont.createFont(hindifont, BaseFont.IDENTITY_H,
			 * BaseFont.EMBEDDED); Font font2 = new Font(baseFont3, 12);
			 * 
			 * 
			 * Font fonth = FontFactory.getFont(hindifont, BaseFont.IDENTITY_H,
			 * BaseFont.EMBEDDED, 0.8f, Font.NORMAL, BaseColor.BLACK); BaseFont baseFont =
			 * font.getBaseFont();
			 */
     		
     		  String str="रिन्यूअल सूचना";     //sample text

     	        Phrase p1 = new Phrase(str,f1);

     	        document.add(p1);
			Paragraph head = new Paragraph();
//			head.setAlignment(Element.ALIGN_CENTER);
			head.add(image);
			document.add(head);

			Paragraph para = new Paragraph("Government of India\r\n" + "Ministry of Corporate Affairs\r\n"
					+ "Serious Fraud Investigation Office\r\n" + "", font);
			para.setAlignment(Element.ALIGN_CENTER);
			document.add(para);

			Paragraph add = new Paragraph(summon.getOfficeAddress() + "", font);
			add.setAlignment(Element.ALIGN_CENTER);

			document.add(add);
			document.add(Chunk.NEWLINE);

			Chunk glue = new Chunk(new VerticalPositionMark());
			Paragraph gh = null;
			if (null != summon.getOderId())
				gh = new Paragraph("DIN : " + summon.getOderId());
			else
				gh = new Paragraph("DIN : ");
			gh.add(new Chunk(glue));
			if (null != summon.getApprovalDate())
				gh.add("Date : " + new SimpleDateFormat("dd-MM-yyyy").format(summon.getApprovalDate()));
			else
				gh.add("Date : ");
			document.add(gh);

			Chunk underline = new Chunk("SUMMONS");
			underline.setUnderline(0.1f, -2f); // 0.1 thick, -2 y-location

			Phrase phrase = new Phrase();
			phrase.add(underline);

			Paragraph p = new Paragraph();
			p.add(phrase);
			p.setAlignment(Element.ALIGN_CENTER);
			document.add(p);

			document.add(Chunk.NEWLINE);

			Paragraph para1 = new Paragraph(summon.getPara1(), f1);
			para1.setAlignment(Element.ALIGN_JUSTIFIED);

			document.add(para1);
			document.add(Chunk.NEWLINE);

			/*
			 * List company = new RomanList();
			 * 
			 * for (String str : summon.getCompany()) { company.add(str); }
			 * 
			 */

			// document.add(Chunk.NEWLINE);

			Paragraph para2 = new Paragraph(summon.getPara2(), font1);
			para2.setAlignment(Element.ALIGN_JUSTIFIED);

			document.add(para2);
			document.add(Chunk.NEWLINE);

			Paragraph para3 = new Paragraph(summon.getPara3(), font1);
			para3.setAlignment(Element.ALIGN_JUSTIFIED);

			document.add(para3);
			document.add(Chunk.NEWLINE);
			// company Disalble By Gouthami 09/10/2020
			/*
			 * List company = new RomanList(); company.add(summon.getComapnyPara());
			 * document.add(company); document.add(Chunk.NEWLINE);
			 */
			Paragraph para4 = new Paragraph(summon.getPara4(), font1);
			para4.setAlignment(Element.ALIGN_JUSTIFIED);

			document.add(para4);
			document.add(Chunk.NEWLINE);

			Paragraph para5 = new Paragraph(summon.getPara5(), font1);
			para5.setAlignment(Element.ALIGN_JUSTIFIED);
			document.add(para5);
			document.add(Chunk.NEWLINE);

			Paragraph para6 = new Paragraph(summon.getPara6(), font1);
			para5.setAlignment(Element.ALIGN_JUSTIFIED);
			document.add(para6);
			document.add(Chunk.NEWLINE);

			Paragraph para7 = new Paragraph(summon.getIo() + "\r\n" + "(" + summon.getIODesignation() + ")\r\n" + "",
					font1);

			para7.setAlignment(Element.ALIGN_RIGHT);
			document.add(para7);

			String footer = "To,\r\nSh." + summon.getCompanyDir();

			if (!(isNullOrEmpty(summon.getDesignation()))) {
				footer = footer + "," + summon.getDesignation();
			}

			if (summon.getCompanyName() != null && summon.getCompanyName() != "")
				footer = footer + "," + summon.getCompanyName();

			footer = footer + "\r\nAddress:" + summon.getCompanyAddress();

			// Paragraph copyto = new
			// Paragraph("To,\r\nSh."+summon.getCompanyDir()+","+summon.getDesignation()+",\r\n"+summon.getCompanyName()+"
			// ,\r\nAddress "+summon.getCompanyAddress(),font1);
			Paragraph copyto = new Paragraph(footer, font1);
			copyto.setAlignment(Element.ALIGN_LEFT);

			document.add(copyto);

			document.close();
		} catch (DocumentException e) {
			logger.info(e.getMessage());
		}

		return new ByteArrayInputStream(out.toByteArray());
	}

	public static void summonPreview(SummonDto summon, String unSignPdf, String UserName, String OfficeAddress,
			String CompanyDispaly, Boolean offline, Date createdDate) throws MalformedURLException, IOException {

		Document document = new Document();
		FileOutputStream unsignPdf = new FileOutputStream(unSignPdf);

		try {

			PdfWriter.getInstance(document, unsignPdf);
			document.open();

			// Add Text to PDF file ->

			Image image = Image.getInstance(new Utils().getConfigMessage("image.emblemPath"));
			image.setAlignment(Element.ALIGN_CENTER);
			image.scaleToFit(100.0f, 50.0f);

			Font font = FontFactory.getFont(FontFactory.TIMES_BOLD, 12, BaseColor.BLACK);
			// Font f8 = FontFactory.getFont(FontFactory.TIMES_BOLD, 8, BaseColor.BLACK);

			Font font1 = FontFactory.getFont(FontFactory.TIMES, 11, BaseColor.BLACK);

			Paragraph head = new Paragraph();
//			head.setAlignment(Element.ALIGN_CENTER);
			head.add(image);
			document.add(head);

			Paragraph para = new Paragraph("Government of India\r\n" + "Ministry of Corporate Affairs\r\n"
					+ "Serious Fraud Investigation Office\r\n" + "", font);
			para.setAlignment(Element.ALIGN_CENTER);
			document.add(para);

			Paragraph add = new Paragraph(OfficeAddress + "", font);
			add.setAlignment(Element.ALIGN_CENTER);

			document.add(add);
			document.add(Chunk.NEWLINE);

			Chunk glue = new Chunk(new VerticalPositionMark());
			Paragraph gh = null;
			if (null != summon.getOderId())
				// gh = new Paragraph("DIN : " + summon.getOderId());
				gh = new Paragraph("DIN : ");

			else
				gh = new Paragraph("DIN : ");
			gh.add(new Chunk(glue));
			if (null != summon.getApprovalDate()) {
				gh.add("Date : " + new SimpleDateFormat("dd-MM-yyyy").format(summon.getApprovalDate()));
			} else if (offline == true) {
				gh.add("Date : " + new SimpleDateFormat("dd-MM-yyyy").format(createdDate));
			} else {
				gh.add("Date : ");
			}
			document.add(gh);

			Chunk underline = new Chunk("SUMMONS");
			underline.setUnderline(0.1f, -2f); // 0.1 thick, -2 y-location

			Phrase phrase = new Phrase();
			phrase.add(underline);

			Paragraph p = new Paragraph();
			p.add(phrase);
			p.setAlignment(Element.ALIGN_CENTER);
			document.add(p);

			document.add(Chunk.NEWLINE);

			Paragraph para1 = new Paragraph(summon.getPara1(), font1);
			para1.setAlignment(Element.ALIGN_JUSTIFIED);

			document.add(para1);
			document.add(Chunk.NEWLINE);
			// List company = new RomanList();

			// company.add(summon.getComapnyPara());
			/*
			 * document.add(Chunk.NEWLINE); List company = new RomanList();
			 * 
			 * for (String str : summon.getCompany()) { company.add(str); }
			 * 
			 * document.add(company);
			 */

			// document.add(Chunk.NEWLINE);

			Paragraph para2 = new Paragraph(summon.getPara2(), font1);
			para2.setAlignment(Element.ALIGN_JUSTIFIED);

			document.add(para2);
			document.add(Chunk.NEWLINE);

			Paragraph para3 = new Paragraph(summon.getPara3(), font1);
			para3.setAlignment(Element.ALIGN_JUSTIFIED);

			document.add(para3);
			document.add(Chunk.NEWLINE);
			// company Disalble By Gouthami 09/10/2020
			/*
			 * List company = new RomanList(); company.add(CompanyDispaly);
			 * document.add(company); document.add(Chunk.NEWLINE);
			 */
			Paragraph para4 = new Paragraph(summon.getPara4(), font1);
			para4.setAlignment(Element.ALIGN_JUSTIFIED);

			document.add(para4);
			document.add(Chunk.NEWLINE);

			Paragraph para5 = new Paragraph(summon.getPara5(), font1);
			para5.setAlignment(Element.ALIGN_JUSTIFIED);
			document.add(para5);
			document.add(Chunk.NEWLINE);

			Paragraph para6 = new Paragraph(summon.getPara6(), font1);
			para5.setAlignment(Element.ALIGN_JUSTIFIED);
			document.add(para6);
			document.add(Chunk.NEWLINE);

			Paragraph para7 = new Paragraph(summon.getIo() + "\r\n" + "(" + summon.getIODesignation() + ")\r\n" + "",
					font1);
			/*
			 * Paragraph para7 =new Paragraph(summon.getIo()+"\r\n" +
			 * "(Investigating Officer / Inspector )\r\n" + "",font1);
			 */

			para7.setAlignment(Element.ALIGN_RIGHT);
			document.add(para7);

			/*
			 * String address1=""; String address2=""; String address3="";
			 * if(summon.getCompanyAddress().length()>50) address1.
			 */

			String footer = "To,\r\nSh." + summon.getCompanyDir();

			if (!(isNullOrEmpty(summon.getDesignation()))) {
				footer = footer + "," + summon.getDesignation();
			}

			if (summon.getCompanyName() != null && summon.getCompanyName() != "")
				footer = footer + "," + summon.getCompanyName();

			footer = footer + "\r\nAddress:" + summon.getCompanyAddress();

			// Paragraph copyto = new
			// Paragraph("To,\r\nSh."+summon.getCompanyDir()+","+summon.getDesignation()+",\r\n"+summon.getCompanyName()+"
			// ,\r\nAddress "+summon.getCompanyAddress(),font1);
			Paragraph copyto = new Paragraph(footer, font1);
			copyto.setAlignment(Element.ALIGN_LEFT);

			if (offline == false) {
				document.add(copyto);
			}

			document.close();
		} catch (DocumentException e) {
			logger.info(e.getMessage());
		} finally {
			if (unsignPdf != null) {
				safeClose(unsignPdf);
			}
		}
		// return new ByteArrayInputStream(out.toByteArray());
	}

	private static void safeClose(FileOutputStream unsignPdf) {
		if (unsignPdf != null) {
			try {
				unsignPdf.close();
			} catch (IOException e) {
				logger.info(e.getMessage());
			}
		}

	}

	public static ByteArrayInputStream summonOfflineFixed(SummonDto summon) throws MalformedURLException, IOException {
		Document document = new Document();
		ByteArrayOutputStream out = new ByteArrayOutputStream();

		try {

			PdfWriter.getInstance(document, out);
			document.open();

			// Add Text to PDF file ->

			Image image = Image.getInstance(new Utils().getConfigMessage("image.emblemPath"));
			image.setAlignment(Element.ALIGN_CENTER);
			image.scaleToFit(100.0f, 50.0f);

			Font font = FontFactory.getFont(FontFactory.TIMES_BOLD, 12, BaseColor.BLACK);
			// Font f8 = FontFactory.getFont(FontFactory.TIMES_BOLD, 8, BaseColor.BLACK);

			Font font1 = FontFactory.getFont(FontFactory.TIMES, 11, BaseColor.BLACK);

			Paragraph head = new Paragraph();
//			head.setAlignment(Element.ALIGN_CENTER);
			head.add(image);
			document.add(head);

			Paragraph para = new Paragraph("Government of India\r\n" + "Ministry of Corporate Affairs\r\n"
					+ "Serious Fraud Investigation Office\r\n" + "", font);
			para.setAlignment(Element.ALIGN_CENTER);
			document.add(para);

			Paragraph add = new Paragraph(summon.getOfficeAddress() + "", font);
			add.setAlignment(Element.ALIGN_CENTER);

			document.add(add);
			// document.add(Chunk.NEWLINE);

			Chunk glue = new Chunk(new VerticalPositionMark());
			Paragraph gh = null;

			// if(null!=summon.getOderId())
			// gh = new Paragraph("DIN : " + summon.getSummonDin());
			// else
			gh = new Paragraph("DIN : ");
			gh.add(new Chunk(glue));
			if (null != summon.getApprovalDate())
				gh.add("Date : " + new SimpleDateFormat("dd-MM-yyyy").format(summon.getApprovalDate()));
			else
				gh.add("Date : ");
			document.add(gh);

			Chunk underline = new Chunk("SUMMON");
			underline.setUnderline(0.1f, -2f); // 0.1 thick, -2 y-location

			Phrase phrase = new Phrase();
			phrase.add(underline);

			Paragraph p = new Paragraph();
			p.add(phrase);
			p.setAlignment(Element.ALIGN_CENTER);
			document.add(p);

			document.add(Chunk.NEWLINE);

			Paragraph para1 = new Paragraph(
					"Sub: Intimation of Document Identification Number (DIN)  for Physical Summon", font1);
			para1.setAlignment(Element.ALIGN_LEFT);

			document.add(para1);

			document.add(Chunk.NEWLINE);

			/*
			 * for (String str : summon.getCompany()) { company.add(str); }
			 * 
			 * document.add(company);
			 */

			document.add(Chunk.NEWLINE);

			Paragraph para2 = new Paragraph("2    This is with regard to Summon  dated " + summon.getIssueDate()
					+ " issued physically without system generated DIN", font1);
			para2.setAlignment(Element.ALIGN_JUSTIFIED);

			document.add(para2);
			document.add(Chunk.NEWLINE);

			Paragraph para3 = new Paragraph(
					"3    The DIN number for the Summon for referred above is " + summon.getSummonDin(), font1);
			para3.setAlignment(Element.ALIGN_JUSTIFIED);

			document.add(para3);
			document.add(Chunk.NEWLINE);
//List company = new RomanList();

			Paragraph para6 = new Paragraph(summon.getIo() + "\r\n" + "(" + summon.getIODesignation() + ")\r\n" + "",
					font1);

			para6.setAlignment(Element.ALIGN_RIGHT);

			document.add(para6);

			Paragraph copyto = new Paragraph("To,\r\nSh." + summon.getCompanyDir() + "," + summon.getCompanyName()
					+ " ,\r\nAddress " + summon.getCompanyAddress(), font1);
			copyto.setAlignment(Element.ALIGN_LEFT);

			// document.add(copyto);

			document.close();
		} catch (DocumentException e) {
			logger.info(e.getMessage());
		}

		return new ByteArrayInputStream(out.toByteArray());
	}

	public static void summonPreviewdin(SummonDto summon, String unSignPdfFullPathdin, String username,
			String OfficeAddress, String comapnyPara, Boolean offline, Date createdDate)
			throws MalformedURLException, IOException {
		Document document = new Document();
		FileOutputStream unsignPdf = new FileOutputStream(unSignPdfFullPathdin);

		try {

			PdfWriter pdfWriter = PdfWriter.getInstance(document, unsignPdf);
			document.open();

			// Add Text to PDF file ->

			Image image = Image.getInstance(new Utils().getConfigMessage("image.emblemPath"));
			image.setAlignment(Element.ALIGN_CENTER);
			image.scaleToFit(100.0f, 50.0f);

			Font font = FontFactory.getFont(FontFactory.TIMES_BOLD, 12, BaseColor.BLACK);
			// Font f8 = FontFactory.getFont(FontFactory.TIMES_BOLD, 8, BaseColor.BLACK);

			Font font1 = FontFactory.getFont(FontFactory.TIMES, 11, BaseColor.BLACK);

			Paragraph head = new Paragraph();
//			head.setAlignment(Element.ALIGN_CENTER);
			head.add(image);
			document.add(head);

			Paragraph para = new Paragraph("Government of India\r\n" + "Ministry of Corporate Affairs\r\n"
					+ "Serious Fraud Investigation Office\r\n" + "", font);
			para.setAlignment(Element.ALIGN_CENTER);
			document.add(para);

			Paragraph add = new Paragraph(OfficeAddress + "", font);
			add.setAlignment(Element.ALIGN_CENTER);

			document.add(add);
			document.add(Chunk.NEWLINE);

			Chunk glue = new Chunk(new VerticalPositionMark());
			Paragraph gh = null;
			if (null != summon.getOderId())
				gh = new Paragraph("DIN : " + summon.getOderId());
			// gh = new Paragraph("DIN : " );

			else
				gh = new Paragraph("DIN : ");
			gh.add(new Chunk(glue));
			if (null != summon.getApprovalDate()) {
				gh.add("Date : " + new SimpleDateFormat("dd-MM-yyyy").format(summon.getApprovalDate()));
			} else if (offline == true) {
				gh.add("Date : " + new SimpleDateFormat("dd-MM-yyyy").format(createdDate));
			} else {
				gh.add("Date : ");
			}
			document.add(gh);

			Chunk underline = new Chunk("SUMMONS");
			underline.setUnderline(0.1f, -2f); // 0.1 thick, -2 y-location

			Phrase phrase = new Phrase();
			phrase.add(underline);

			Paragraph p = new Paragraph();
			p.add(phrase);
			p.setAlignment(Element.ALIGN_CENTER);
			document.add(p);

			document.add(Chunk.NEWLINE);

			Paragraph para1 = new Paragraph(summon.getPara1(), font1);
			para1.setAlignment(Element.ALIGN_JUSTIFIED);

			document.add(para1);
			document.add(Chunk.NEWLINE);
			// List company = new RomanList();

			// company.add(summon.getComapnyPara());
			/*
			 * document.add(Chunk.NEWLINE); List company = new RomanList();
			 * 
			 * for (String str : summon.getCompany()) { company.add(str); }
			 * 
			 * document.add(company);
			 */

			// document.add(Chunk.NEWLINE);

			Paragraph para2 = new Paragraph(summon.getPara2(), font1);
			para2.setAlignment(Element.ALIGN_JUSTIFIED);

			document.add(para2);
			document.add(Chunk.NEWLINE);

			if (offline == true) {
				String para3_1 = summon.getPara3() + " " + summon.getOderId();
				Paragraph para3 = new Paragraph(para3_1, font1);

				para3.setAlignment(Element.ALIGN_JUSTIFIED);
				document.add(para3);

			} else {
				Paragraph para3 = new Paragraph(summon.getPara3(), font1);

				para3.setAlignment(Element.ALIGN_JUSTIFIED);
				document.add(para3);

			}

			document.add(Chunk.NEWLINE);
			// company Disalble By Gouthami 09/10/2020
			/*
			 * List company = new RomanList(); company.add(CompanyDispaly);
			 * document.add(company); document.add(Chunk.NEWLINE);
			 */
			Paragraph para4 = new Paragraph(summon.getPara4(), font1);
			para4.setAlignment(Element.ALIGN_JUSTIFIED);

			document.add(para4);
			document.add(Chunk.NEWLINE);

			Paragraph para5 = new Paragraph(summon.getPara5(), font1);
			para5.setAlignment(Element.ALIGN_JUSTIFIED);
			document.add(para5);
			document.add(Chunk.NEWLINE);

			Paragraph para6 = new Paragraph(summon.getPara6(), font1);
			para5.setAlignment(Element.ALIGN_JUSTIFIED);
			document.add(para6);
			document.add(Chunk.NEWLINE);

			Paragraph para7 = new Paragraph(summon.getIo() + "\r\n" + "(" + summon.getIODesignation() + ")\r\n" + "",
					font1);
			/*
			 * Paragraph para7 =new Paragraph(summon.getIo()+"\r\n" +
			 * "(Investigating Officer / Inspector )\r\n" + "",font1);
			 */

			para7.setAlignment(Element.ALIGN_RIGHT);
			document.add(para7);

			/*
			 * String address1=""; String address2=""; String address3="";
			 * if(summon.getCompanyAddress().length()>50) address1.
			 */

			String footer = "To,\r\nSh." + summon.getCompanyDir();

			if (!(isNullOrEmpty(summon.getDesignation()))) {
				footer = footer + "," + summon.getDesignation();
			}

			if (summon.getCompanyName() != null && summon.getCompanyName() != "")
				footer = footer + "," + summon.getCompanyName();

			footer = footer + "\r\nAddress:" + summon.getCompanyAddress();

			// Paragraph copyto = new
			// Paragraph("To,\r\nSh."+summon.getCompanyDir()+","+summon.getDesignation()+",\r\n"+summon.getCompanyName()+"
			// ,\r\nAddress "+summon.getCompanyAddress(),font1);
			Paragraph copyto = new Paragraph(footer, font1);
			copyto.setAlignment(Element.ALIGN_LEFT);

			if (offline == false) {
				document.add(copyto);
			}
			PdfPTable table1 = new PdfPTable(1);
			table1.setWidthPercentage(30);
			table1.setHorizontalAlignment(2);
			;
			// table1.addCell("Signer 1: Alice");
			table1.addCell(createSignatureFieldCell(pdfWriter, "sig1"));

			document.add(table1);

			document.close();
		} catch (DocumentException e) {
			logger.info(e.getMessage());
		} finally {
			if (unsignPdf != null) {
				safeClose(unsignPdf);
			}
		}
		// return new ByteArrayInputStream(out.toByteArray());
	}

	protected static PdfPCell createSignatureFieldCell(PdfWriter writer, String name) {
		PdfPCell cell = new PdfPCell();
		cell.setMinimumHeight(50);
		PdfFormField field = PdfFormField.createSignature(writer);
		field.setFieldName(name);
		field.setFlags(PdfAnnotation.FLAGS_PRINT);
		cell.setCellEvent(new MySignatureFieldEvent(field));
		cell.setBorder(Rectangle.NO_BORDER);
		return cell;
	}
	
	
	public static void summonPreview(Long officeOrderId,String unSignPdfFullPath,Long
			  userId) throws Exception {
			  
		String summonDin=" ";
		String   path =   new Utils().getConfigMessage("file.snmsapi");
			  
			 // String s = " "+snmsapi ;
			  
		String   s = " "+path+"/PdfSummonAs1?officeOrderId=" + officeOrderId + "&userId=" + userId+"&summonDin="+summonDin;
			  
			  String Command = s + " "; 
			  
			  createpdf1(unSignPdfFullPath, Command); 
			 
			  
			  
			  
			  
			  }
			  
			  
		private static void createpdf1(String unSignPdfFullPath, String s) throws InterruptedException  {
			
			String   pdfExe =   new Utils().getConfigMessage("pdf.exe");
			String command = pdfExe+" " + " " + s + unSignPdfFullPath;

			try {

				// Running the above command
				Runtime run = Runtime.getRuntime();
				Process proc = run.exec(command);
				proc.waitFor(45, TimeUnit.SECONDS);

			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}

		public static void summonPreviewDin(Long officeOrderId, String unSignPdfFullPath, Long userId, String summonDin,Boolean isOffline) throws InterruptedException {
			String   path =   new Utils().getConfigMessage("file.snmsapi");
			  
			 // String s = " "+snmsapi ;
			  
			String   s = " ";
			if(isOffline==false) {
			s= path+"/PdfSummonAs1?officeOrderId=" + officeOrderId + "&userId=" + userId+"&summonDin="+summonDin;
			}else {
				s= path+"/PdfOfflineSummonAs1?officeOrderId=" + officeOrderId + "&userId=" + userId+"&summonDin="+summonDin;
				  
			}
			
			System.out.println(s);
			  String Command = s + " "; 
			  
			  createpdf1(unSignPdfFullPath, Command); 
			
		}

		public static void summonPreviewOffline(Long officeOrderId, String unSignPdfFullPath, Long userId) throws InterruptedException {
			String summonDin=" ";
			String   path =   new Utils().getConfigMessage("file.snmsapi");
				  
				 // String s = " "+snmsapi ;
				  
			String   s = " "+path+"/PdfOfflineSummonAs1?officeOrderId=" + officeOrderId + "&userId=" + userId+"&summonDin="+summonDin;
				  
				  String Command = s + " "; 
				  
				  createpdf1(unSignPdfFullPath, Command); 
				 

		}

}