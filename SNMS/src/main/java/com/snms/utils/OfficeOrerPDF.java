package com.snms.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

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
import com.itextpdf.text.RomanList;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.VerticalPositionMark;
import com.snms.controllers.OfflineSummonController;
import com.snms.dto.OfficeOrderDto;

public class OfficeOrerPDF {
	private static final Logger logger = Logger.getLogger(OfficeOrerPDF.class);
	public static ByteArrayInputStream officeOrderFixed(OfficeOrderDto office) throws MalformedURLException, IOException {
		
		Document document = new Document();
		ByteArrayOutputStream out = new ByteArrayOutputStream();

		try {

			PdfWriter.getInstance(document, out);
			document.open();
			document.setMargins(100, 100, 100, 100);
			
			Image image=Image.getInstance(new Utils().getConfigMessage("image.emblemPath"));
			image.setAlignment(Element.ALIGN_CENTER);
			image.scaleToFit(100.0f, 50.0f);
			Paragraph head = new Paragraph();
//			head.setAlignment(Element.ALIGN_CENTER);
			head.add(image);
			document.add(head);
			// Add Text to PDF file ->
			
			Font font = FontFactory.getFont(FontFactory.TIMES_BOLD, 12, BaseColor.BLACK);
			
			Font font1 = FontFactory.getFont(FontFactory.TIMES, 11, BaseColor.BLACK);

			Paragraph para = new Paragraph("Government of India\r\n" + "Ministry of Corporate Affairs\r\n"
					+ "Serious Fraud Investigation Office\r\n" + "", font);
			para.setAlignment(Element.ALIGN_CENTER);
			document.add(para);

			Paragraph add = new Paragraph(
					"2nd Floor Pt. Deendayal Antyodaya Bhawan,\r\n" + "CGO Complex Lodhi Road,New delhi-110003\r\n" + "",
					font);
			add.setAlignment(Element.ALIGN_CENTER);

			document.add(add);
			//document.add(Chunk.NEWLINE);		
			
			Chunk glue = new Chunk(new VerticalPositionMark());
			Paragraph gh=null;
			if(null!=office.getOderId())
			 gh = new Paragraph("DIN : " + office.getOderId());
			else
				gh = new Paragraph("DIN : " );
			gh.add(new Chunk(glue));
			if(null!=office.getApprovalDate())
				gh.add("Date : "+new SimpleDateFormat("dd-MM-yyyy").format(office.getApprovalDate()) );
			else
				gh.add("Date : ");
			document.add(gh);
			
			Chunk underline = new Chunk("ORDER");
			underline.setUnderline(0.1f, -2f); //0.1 thick, -2 y-location
			
			Phrase phrase = new Phrase();
			phrase.add(underline);

			Paragraph p = new Paragraph();
			p.add(phrase);
			p.setAlignment(Element.ALIGN_CENTER);
			document.add(p);

			Paragraph para1 = new Paragraph(
					"Whereas in exercise of powers conferred under section 212 of the Companies Act, 2013 the Central Government has ordered investigation under section 212(1) "+office.getClause()+" of the Companies Act, 2013 into the affairs of following companies and has assigned the same to Serious Fraud Investigation Office (SFIO) vide order no.: "
							+ office.getMcaOrderNo() + " dated : " + office.getOrderDate(),font1);
			para1.setAlignment(Element.ALIGN_JUSTIFIED);

			document.add(para1);
			document.add(Chunk.NEWLINE);
			List company = new RomanList();
			
			for (String str : office.getCompany()) {
				company.add(str);
			}
			document.add(company);
			document.add(Chunk.NEWLINE);

			Paragraph para2 = new Paragraph(
					"2       And whereas the officers of SFIO are to be designated as Inspectors to carry out the investigation under Section 212(1) and Investigating Officer under Section 212(4) of the Companies Act, 2013.",font1);
			para2.setAlignment(Element.ALIGN_JUSTIFIED);
			document.add(para2);
			document.add(Chunk.NEWLINE);

			Paragraph para3 = new Paragraph(
					"3       Now, therefore, in exercise of powers conferred under Section 212(1) of the Companies Act 2013, the following officers are designated as Inspectors to carry out the investigation into the affairs of the above-mentioned companies and shall exercise all the powers available to them under the Companies Act, 2013",font1);
			para3.setAlignment(Element.ALIGN_JUSTIFIED);
			document.add(para3);
			document.add(Chunk.NEWLINE);

			List inspectors = new RomanList();
			for (String str : office.getInspectors()) {
				inspectors.add(str);
			}

			document.add(inspectors);
			document.add(Chunk.NEWLINE);

			Paragraph para4 = new Paragraph(
					"4          And further, in exercise of powers conferred under Section 212(4) of the Companies Act, 2013,"+office.getIo()+" is appointed as Investigating Officer to carry out the above noted investigation. The inspectors shall exercise all the powers available to them under section 217 of  the Companies Acts, 2013",font1);
			para4.setAlignment(Element.ALIGN_JUSTIFIED);
			document.add(para4);
			document.add(Chunk.NEWLINE);
			
			Paragraph para5 = new Paragraph("5       The Inspectors and the Investigating Officer shall complete the investigation and submit their report.",font1);
			para5.setAlignment(Element.ALIGN_JUSTIFIED);
			document.add(para5);
			document.add(Chunk.NEWLINE);
			
			Paragraph para6 =new Paragraph(office.getDirector()+"\r\n" + 
					"(DIRECTOR)\r\n" + 
					"",font1);
			
			para6.setAlignment(Element.ALIGN_RIGHT);
			
			document.add(para6);
			
			Paragraph copyto = new Paragraph("Copy To :",font1);
			copyto.setAlignment(Element.ALIGN_LEFT);
			
			document.add(copyto);
			
			List copy = new List(true);
			
			for (String str : office.getInspectors()) {
				copy.add(str);
			}
			copy.add("PPS to Director, SFIO");
			copy.add("Guard File");
			
			document.add(copy);
			
			
			document.close();
		} catch (DocumentException e) {
		}

		return new ByteArrayInputStream(out.toByteArray());
	}
	
	public static ByteArrayInputStream officeOrderSaved(OfficeOrderDto office) throws MalformedURLException, IOException {
		
		Document document = new Document();
		ByteArrayOutputStream out = new ByteArrayOutputStream();

		try {

			PdfWriter.getInstance(document, out);
			document.open();

			String emblemPath = new Utils().getConfigMessage("image.emblemPath");
			Image image=Image.getInstance(emblemPath);
			image.setAlignment(Element.ALIGN_CENTER);
			image.scaleToFit(100.0f, 50.0f);
			Paragraph head = new Paragraph();
//			head.setAlignment(Element.ALIGN_CENTER);
			head.add(image);
			document.add(head);
			
			// Add Text to PDF file ->
			
			Font font = FontFactory.getFont(FontFactory.TIMES_BOLD, 12, BaseColor.BLACK);
			
			Font font1 = FontFactory.getFont(FontFactory.TIMES, 11, BaseColor.BLACK);

			Paragraph para = new Paragraph("Government of India\r\n" + "Ministry of Corporate Affairs\r\n"
					+ "Serious Fraud Investigation Office\r\n" + "", font);
			para.setAlignment(Element.ALIGN_CENTER);
			document.add(para);

			Paragraph add = new Paragraph(
					"2nd Floor Pt. Deendayal Antyodaya Bhawan,\r\n" + "CGO Complex Lodhi Road,New delhi-110003\r\n" + "",
					font);
			add.setAlignment(Element.ALIGN_CENTER);
			document.add(add);
			document.add(Chunk.NEWLINE);
			/*Paragraph filenum = new Paragraph("File No. : " + office.getOderId());
			document.add(filenum);*/
			Chunk glue = new Chunk(new VerticalPositionMark());
			Paragraph gh=null;
			if(null!=office.getOderId())
			 gh = new Paragraph("DIN : " + office.getOderId());
			else
				gh = new Paragraph("DIN : " );
			gh.add(new Chunk(glue));
			
			if(null!=office.getApprovalDate())
				gh.add("Date : "+new SimpleDateFormat("dd-MM-yyyy").format(office.getApprovalDate()) );
				else
					gh.add("Date : ");
			document.add(gh);
			
			Chunk underline = new Chunk("ORDER");
			underline.setUnderline(0.1f, -2f); //0.1 thick, -2 y-location
			
			Phrase phrase = new Phrase();
			phrase.add(underline);

			Paragraph p = new Paragraph();
			p.add(phrase);
			p.setAlignment(Element.ALIGN_CENTER);
			
			
			document.add(p);

			Paragraph para1 = new Paragraph(office.getPara1(),font1);
			para1.setAlignment(Element.ALIGN_JUSTIFIED);

			document.add(para1);
			document.add(Chunk.NEWLINE);
			List company = new RomanList();
			
			for (String str : office.getCompany()) {
				company.add(str);
			}
			
			

			document.add(company);

			document.add(Chunk.NEWLINE);

			Paragraph para2 = new Paragraph(office.getPara2(),font1);
			para2.setAlignment(Element.ALIGN_JUSTIFIED);
			document.add(para2);
			document.add(Chunk.NEWLINE);

			Paragraph para3 = new Paragraph(office.getPara3(),font1);
			para3.setAlignment(Element.ALIGN_JUSTIFIED);

			document.add(para3);
			document.add(Chunk.NEWLINE);

			List inspectors = new RomanList();
			
			for (String str : office.getInspectors()) {
				inspectors.add(str);
			}

			document.add(inspectors);

			document.add(Chunk.NEWLINE);

			Paragraph para4 = new Paragraph(office.getPara4(),font1);
			para4.setAlignment(Element.ALIGN_JUSTIFIED);

			document.add(para4);
			document.add(Chunk.NEWLINE);
			
			Paragraph para5 = new Paragraph(office.getPara5(),font1);
			para5.setAlignment(Element.ALIGN_JUSTIFIED);

			document.add(para5);
			document.add(Chunk.NEWLINE);
			
			Paragraph para6 =new Paragraph(office.getDirector()+"\r\n" + 
					"(DIRECTOR)\r\n" + 
					"",font1);
			
			para6.setAlignment(Element.ALIGN_RIGHT);
			document.add(para6);
			
			Paragraph copyto = new Paragraph("Copy To :",font1);
			copyto.setAlignment(Element.ALIGN_LEFT);
			document.add(copyto);
			
			List copy = new List(true);
			for (String str : office.getInspectors()) {
				copy.add(str);
			}
			copy.add("PPS to Director, SFIO");
			copy.add("Guard File");
			document.add(copy);
			
			document.close();
		} catch (DocumentException e) {
		}

		return new ByteArrayInputStream(out.toByteArray());
	}
	
	public static void officeOrderFinal(OfficeOrderDto office,String unSignPdf) throws MalformedURLException, IOException {
		
		Document document = new Document();
		FileOutputStream out = new FileOutputStream(unSignPdf);

		try {

			PdfWriter.getInstance(document, out);
			document.open();

			
			Image image=Image.getInstance(new Utils().getConfigMessage("image.emblemPath"));
			image.setAlignment(Element.ALIGN_CENTER);
			image.scaleToFit(100.0f, 50.0f);
			Paragraph head = new Paragraph();
//			head.setAlignment(Element.ALIGN_CENTER);
			head.add(image);
			document.add(head);
			
			// Add Text to PDF file ->
			
			Font font = FontFactory.getFont(FontFactory.TIMES_BOLD, 12, BaseColor.BLACK);
			
			Font font1 = FontFactory.getFont(FontFactory.TIMES, 11, BaseColor.BLACK);

			Paragraph para = new Paragraph("Government of India\r\n" + "Ministry of Corporate Affairs\r\n"
					+ "Serious Fraud Investigation Office\r\n" + "", font);
			para.setAlignment(Element.ALIGN_CENTER);
			document.add(para);

			Paragraph add = new Paragraph(
					"2nd Floor Pt. Deendayal Antyodaya Bhawan,\r\n" + "CGO Complex Lodhi Road,New delhi-110003\r\n" + "",
					font);
			add.setAlignment(Element.ALIGN_CENTER);
			document.add(add);
			document.add(Chunk.NEWLINE);
			
			Chunk glue = new Chunk(new VerticalPositionMark());
			Paragraph gh=null;
			if(null!=office.getOderId())
			 gh = new Paragraph("DIN : " + office.getOderId());
			else
				gh = new Paragraph("DIN : " );
			gh.add(new Chunk(glue));
			if(null!=office.getApprovalDate())
				gh.add("Date : "+new SimpleDateFormat("dd-MM-yyyy").format(office.getApprovalDate()) );
				else
					gh.add("Date : ");
			document.add(gh);
			
			Chunk underline = new Chunk("ORDER");
			underline.setUnderline(0.1f, -2f); //0.1 thick, -2 y-location
			
			Phrase phrase = new Phrase();
			phrase.add(underline);

			Paragraph p = new Paragraph();
			p.add(phrase);
			p.setAlignment(Element.ALIGN_CENTER);
			
			
			document.add(p);

			Paragraph para1 = new Paragraph(office.getPara1(),font1);
			para1.setAlignment(Element.ALIGN_JUSTIFIED);

			document.add(para1);
			document.add(Chunk.NEWLINE);
			List company = new RomanList();
			
			for (String str : office.getCompany()) {
				company.add(str);
			}
			
			

			document.add(company);

			document.add(Chunk.NEWLINE);

			Paragraph para2 = new Paragraph(office.getPara2(),font1);
			para2.setAlignment(Element.ALIGN_JUSTIFIED);
			document.add(para2);
			document.add(Chunk.NEWLINE);

			Paragraph para3 = new Paragraph(office.getPara3(),font1);
			para3.setAlignment(Element.ALIGN_JUSTIFIED);

			document.add(para3);
			document.add(Chunk.NEWLINE);

			List inspectors = new RomanList();
			
			for (String str : office.getInspectors()) {
				inspectors.add(str);
			}

			document.add(inspectors);

			document.add(Chunk.NEWLINE);

			Paragraph para4 = new Paragraph(office.getPara4(),font1);
			para4.setAlignment(Element.ALIGN_JUSTIFIED);

			document.add(para4);
			document.add(Chunk.NEWLINE);
			
			Paragraph para5 = new Paragraph(office.getPara5(),font1);
			para5.setAlignment(Element.ALIGN_JUSTIFIED);

			document.add(para5);
			document.add(Chunk.NEWLINE);
			
			Paragraph para6 =new Paragraph(office.getDirector()+"\r\n" + 
					"(DIRECTOR)\r\n" + 
					"",font1);
			
			para6.setAlignment(Element.ALIGN_RIGHT);
			document.add(para6);
			
			Paragraph copyto = new Paragraph("Copy To :",font1);
			copyto.setAlignment(Element.ALIGN_LEFT);
			document.add(copyto);
			
			List copy = new List(true);
			for (String str : office.getInspectors()) {
				copy.add(str);
			}
			copy.add("PPS to Director, SFIO");
			copy.add("Guard File");
			document.add(copy);
			
			document.close();
		} catch (DocumentException e) {
			logger.info(e.getMessage());
		}

		finally {
			if (out != null) {
			safeClose(out);
			}
		}
	}

	private static void safeClose(FileOutputStream out) {
		if (out != null) {
			try {
				out.close();
			} catch (IOException e) {
				logger.info(e.getMessage());
			}
			}
		
	}

	public static void officeOrderWitoutDinFinal(OfficeOrderDto office, String unSignPdfFullPath) throws MalformedURLException, IOException {
		Document document = new Document();
		FileOutputStream out = new FileOutputStream(unSignPdfFullPath);

		try {

			PdfWriter.getInstance(document, out);
			document.open();

			
			Image image=Image.getInstance(new Utils().getConfigMessage("image.emblemPath"));
			image.setAlignment(Element.ALIGN_CENTER);
			image.scaleToFit(100.0f, 50.0f);
			Paragraph head = new Paragraph();
//			head.setAlignment(Element.ALIGN_CENTER);
			head.add(image);
			document.add(head);
			
			// Add Text to PDF file ->
			
			Font font = FontFactory.getFont(FontFactory.TIMES_BOLD, 12, BaseColor.BLACK);
			
			Font font1 = FontFactory.getFont(FontFactory.TIMES, 11, BaseColor.BLACK);

			Paragraph para = new Paragraph("Government of India\r\n" + "Ministry of Corporate Affairs\r\n"
					+ "Serious Fraud Investigation Office\r\n" + "", font);
			para.setAlignment(Element.ALIGN_CENTER);
			document.add(para);

			Paragraph add = new Paragraph(
					"2nd Floor Pt. Deendayal Antyodaya Bhawan,\r\n" + "CGO Complex Lodhi Road,New delhi-110003\r\n" + "",
					font);
			add.setAlignment(Element.ALIGN_CENTER);
			document.add(add);
			document.add(Chunk.NEWLINE);
			
			Chunk glue = new Chunk(new VerticalPositionMark());
			Paragraph gh=null;
			if(null!=office.getOderId())
			 gh = new Paragraph("DIN : " );
			else
				gh = new Paragraph("DIN : " );
			gh.add(new Chunk(glue));
			if(null!=office.getApprovalDate())
				gh.add("Date : "+new SimpleDateFormat("dd-MM-yyyy").format(office.getApprovalDate()) );
				else
					gh.add("Date : ");
			document.add(gh);
			
			Chunk underline = new Chunk("ORDER");
			underline.setUnderline(0.1f, -2f); //0.1 thick, -2 y-location
			
			Phrase phrase = new Phrase();
			phrase.add(underline);

			Paragraph p = new Paragraph();
			p.add(phrase);
			p.setAlignment(Element.ALIGN_CENTER);
			
			
			document.add(p);

			Paragraph para1 = new Paragraph(office.getPara1(),font1);
			para1.setAlignment(Element.ALIGN_JUSTIFIED);

			document.add(para1);
			document.add(Chunk.NEWLINE);
			List company = new RomanList();
			
			for (String str : office.getCompany()) {
				company.add(str);
			}
			
			

			document.add(company);

			document.add(Chunk.NEWLINE);

			Paragraph para2 = new Paragraph(office.getPara2(),font1);
			para2.setAlignment(Element.ALIGN_JUSTIFIED);
			document.add(para2);
			document.add(Chunk.NEWLINE);

			Paragraph para3 = new Paragraph(office.getPara3(),font1);
			para3.setAlignment(Element.ALIGN_JUSTIFIED);

			document.add(para3);
			document.add(Chunk.NEWLINE);

			List inspectors = new RomanList();
			
			for (String str : office.getInspectors()) {
				inspectors.add(str);
			}

			document.add(inspectors);

			document.add(Chunk.NEWLINE);

			Paragraph para4 = new Paragraph(office.getPara4(),font1);
			para4.setAlignment(Element.ALIGN_JUSTIFIED);

			document.add(para4);
			document.add(Chunk.NEWLINE);
			
			Paragraph para5 = new Paragraph(office.getPara5(),font1);
			para5.setAlignment(Element.ALIGN_JUSTIFIED);

			document.add(para5);
			document.add(Chunk.NEWLINE);
			
			Paragraph para6 =new Paragraph(office.getDirector()+"\r\n" + 
					"(DIRECTOR)\r\n" + 
					"",font1);
			
			para6.setAlignment(Element.ALIGN_RIGHT);
			document.add(para6);
			
			Paragraph copyto = new Paragraph("Copy To :",font1);
			copyto.setAlignment(Element.ALIGN_LEFT);
			document.add(copyto);
			
			List copy = new List(true);
			for (String str : office.getInspectors()) {
				copy.add(str);
			}
			copy.add("PPS to Director, SFIO");
			copy.add("Guard File");
			document.add(copy);
			
			document.close();
		} catch (DocumentException e) {
			logger.info(e.getMessage());
		}

		finally {
			if (out != null) {
			safeClose(out);
			}
		}

		
	}
}