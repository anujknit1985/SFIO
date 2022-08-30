package com.snms.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

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
import com.itextpdf.text.pdf.PdfAnnotation;
import com.itextpdf.text.pdf.PdfFormField;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.VerticalPositionMark;
import com.snms.dto.NoticeDto;
import com.snms.dto.NoticeTempDto;

public class NoticePdf {
	private static final Logger logger = Logger.getLogger(NoticePdf.class);

	public static ByteArrayInputStream noticeFixed(NoticeDto summon) throws MalformedURLException, IOException {

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
			Font f8 = FontFactory.getFont(FontFactory.TIMES_BOLD, 10, BaseColor.BLACK);
			Font font1 = FontFactory.getFont(FontFactory.TIMES, 11, BaseColor.BLACK);
			Paragraph head = new Paragraph();
			head.setAlignment(Element.ALIGN_CENTER);
			head.add(image);
			document.add(image);
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

			Chunk underline = new Chunk("NOTICE");
			underline.setUnderline(0.1f, -2f); // 0.1 thick, -2 y-location

			Phrase phrase = new Phrase();
			phrase.add(underline);

			Paragraph p = new Paragraph();
			p.add(phrase);
			p.setAlignment(Element.ALIGN_CENTER);

			document.add(p);

			String to_header = "To,\r\nSh." + summon.getCompanyDir();

			if (summon.getDesignation() != null && summon.getDesignation() != "") {
				to_header = to_header + "," + summon.getDesignation();
			}

			if (summon.getCompany() != null && summon.getCompany() != "")
				to_header = to_header + "\r\n" + summon.getCompany();

			to_header = to_header + "\r\nAddress:" + summon.getCompanyAddress();

			// Paragraph copyto = new Paragraph("To,"
			// +summon.getCompanyDir()+"\r\n"+summon.getCompany()+",\r\nAddress
			// "+summon.getCompanyAddress(),font1);
			Paragraph copyto = new Paragraph(to_header, font1);
			copyto.setAlignment(Element.ALIGN_LEFT);

			document.add(copyto);
			document.add(Chunk.NEWLINE);
			Paragraph sub = new Paragraph("Sub: Investigation into the affairs of " + summon.getCompanyPara()
					+ " u/s 212 of the Companies Act, 2013 – calling information u/s 217 (2) - regd.", f8);
			sub.setAlignment(Element.ALIGN_JUSTIFIED);

			document.add(sub);
			document.add(Chunk.NEWLINE);

			Paragraph para1 = new Paragraph(
					"       The Ministry of Corporate Affairs, Government of India, New Delhi, in exercise of the powers conferred under Section 212(1) (c) of the Companies Act, 2013, vide order No."
							+ summon.getOderId() + " dated : " + summon.getOrderDate()
							+ " has ordered the Serious Fraud Investigation Office (SFIO) to investigate into the affairs of "
							+ summon.getCompany() + " and " + summon.getSize()
							+ " other companies i.e. the companies under investigation (CUIs). The undersigned has been appointed as the Investigating Officer by the Competent Authority to carry out the said investigation.",
					font1);
			para1.setAlignment(Element.ALIGN_JUSTIFIED);
			document.add(para1);
			document.add(Chunk.NEWLINE);

			Paragraph para2 = new Paragraph(
					"2       In this regard, it is informed that the analysis of bank account transactions of "
							+ summon.getCompany()
							+ " revealed that your company had entered into the financial transaction with "
							+ summon.getCompany()
							+ " during the FY 2016-17. Therefore, you are directed to provide the details of all the transactions entered by you / your company with "
							+ summon.getCompany() + " and " + summon.getSize()
							+ " other companies under investigation ",
					font1);
			para2.setAlignment(Element.ALIGN_JUSTIFIED);
			document.add(para2);
			document.add(Chunk.NEWLINE);

			// company Disalble By Gouthami 09/10/2020
			/*
			 * List company = new RomanList();
			 * 
			 * company.add(summon.getCompanyPara());
			 * 
			 * for (String str : summon.getCompanylist()) { company.add(str); }
			 * 
			 * document.add(company);
			 * 
			 * document.add(Chunk.NEWLINE);
			 */

			Paragraph para3 = new Paragraph(
					"3       The details of the transactions entered in to between your company and any of the above companies under investigation must be accompanied with the copy of the relevant documents related to the said transactions. It is directed that aforesaid information / documents etc. shall be furnished to this office latest by "
							+ summon.getReportingDate(),
					font1);
			para3.setAlignment(Element.ALIGN_JUSTIFIED);

			document.add(para3);
			document.add(Chunk.NEWLINE);

			Paragraph para4 = new Paragraph(
					"4        This letter may be treated as Notice u/s 217(2) of the Companies Act, 2013 and the non-compliance of the directions shall make you liable to be prosecuted under the provisions of Section 217 sub section (8) of the Companies Act, 2013",
					font1);
			para4.setAlignment(Element.ALIGN_JUSTIFIED);

			document.add(para4);
			document.add(Chunk.NEWLINE);

			Paragraph para6 = new Paragraph(summon.getIo() + "\r\n" + "(" + summon.getIODesignation() + ")\r\n" + "",
					font1);

			para6.setAlignment(Element.ALIGN_RIGHT);

			document.add(para6);

			document.close();
		} catch (DocumentException e) {

		}

		return new ByteArrayInputStream(out.toByteArray());
	}

	public static ByteArrayInputStream noticeSaved(NoticeTempDto summon) throws MalformedURLException, IOException {

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
			Font f8 = FontFactory.getFont(FontFactory.TIMES_BOLD, 10, BaseColor.BLACK);
			Font font1 = FontFactory.getFont(FontFactory.TIMES, 11, BaseColor.BLACK);
			Paragraph head = new Paragraph();
			head.setAlignment(Element.ALIGN_CENTER);
			head.add(image);
			document.add(image);
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
			if (null != summon.getCaseNo())
				gh = new Paragraph("DIN : " + summon.getCaseNo());
			else
				gh = new Paragraph("DIN : ");
			gh.add(new Chunk(glue));
			if (null != summon.getApprovalDate())
				gh.add("Date : " + new SimpleDateFormat("dd-MM-yyyy").format(summon.getApprovalDate()));
			else
				gh.add("Date : ");
			document.add(gh);

			Chunk underline = new Chunk("NOTICE");
			underline.setUnderline(0.1f, -2f); // 0.1 thick, -2 y-location

			Phrase phrase = new Phrase();
			phrase.add(underline);

			Paragraph p = new Paragraph();
			p.add(phrase);
			p.setAlignment(Element.ALIGN_CENTER);

			document.add(p);

			String to_header = "To,\r\nSh." + summon.getName();

			if (summon.getDesignation() != null && summon.getDesignation() != "") {
				to_header = to_header + "," + summon.getDesignation();
			}

			if (summon.getCompany() != null && summon.getCompany() != "")
				to_header = to_header + "\r\n" + summon.getCompany();

			to_header = to_header + "\r\nAddress:" + summon.getCompanyAddress();
			Paragraph copyto = new Paragraph(to_header);
			// Paragraph copyto = new
			// Paragraph("To,\r\n"+summon.getName()+",\r\n"+summon.getCompany()+",\r\nAddress
			// : "+summon.getCompanyAddress()+",\r\nEmail : "+summon.getEmail(),font1);
			copyto.setAlignment(Element.ALIGN_LEFT);

			document.add(copyto);
			document.add(Chunk.NEWLINE);
			Paragraph sub = new Paragraph(summon.getPara1(), f8);
			sub.setAlignment(Element.ALIGN_JUSTIFIED);

			document.add(sub);
			document.add(Chunk.NEWLINE);

			Paragraph para1 = new Paragraph(summon.getPara2(), font1);
			para1.setAlignment(Element.ALIGN_JUSTIFIED);
			document.add(para1);
			document.add(Chunk.NEWLINE);

			Paragraph para2 = new Paragraph(summon.getPara3(), font1);
			para2.setAlignment(Element.ALIGN_JUSTIFIED);
			document.add(para2);
			document.add(Chunk.NEWLINE);

			// company Disalble By Gouthami 09/10/2020
			/*
			 * List company = new RomanList();
			 * 
			 * 
			 * for (String str : summon.getCompanylist()) { company.add(str); }
			 * 
			 * 
			 * company.add(summon.getComapnyPara()); document.add(company);
			 * 
			 * document.add(Chunk.NEWLINE);
			 */

			Paragraph para3 = new Paragraph(summon.getPara4(), font1);
			para3.setAlignment(Element.ALIGN_JUSTIFIED);

			document.add(para3);
			document.add(Chunk.NEWLINE);

			Paragraph para4 = new Paragraph(summon.getPara5(), font1);
			para4.setAlignment(Element.ALIGN_JUSTIFIED);

			document.add(para4);
			document.add(Chunk.NEWLINE);

			Paragraph para6 = new Paragraph(summon.getIo() + "\r\n" + "(" + summon.getIODesignation() + ")\r\n" + "",
					font1);

			para6.setAlignment(Element.ALIGN_RIGHT);

			document.add(para6);
			document.close();
		} catch (DocumentException e) {
			logger.info(e.getMessage());
		}

		return new ByteArrayInputStream(out.toByteArray());
	}

	public static void noticePreview(NoticeTempDto summon, String unSignPdf, String userName, String OfficeAddress,
			String companydisplay) throws MalformedURLException, IOException {

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
			Font f8 = FontFactory.getFont(FontFactory.TIMES_BOLD, 10, BaseColor.BLACK);
			Font font1 = FontFactory.getFont(FontFactory.TIMES, 11, BaseColor.BLACK);
			Paragraph head = new Paragraph();
			head.setAlignment(Element.ALIGN_CENTER);
			head.add(image);
			document.add(image);
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
			if (null != summon.getCaseNo())
				gh = new Paragraph("DIN : ");
			// gh = new Paragraph("DIN : " + summon.getCaseNo());
			else
				gh = new Paragraph("DIN : ");
			gh.add(new Chunk(glue));
			if (null != summon.getApprovalDate())
				gh.add("Date : " + new SimpleDateFormat("dd-MM-yyyy").format(summon.getApprovalDate()));
			else
				gh.add("Date : ");
			document.add(gh);

			Chunk underline = new Chunk("NOTICE");
			underline.setUnderline(0.1f, -2f); // 0.1 thick, -2 y-location

			Phrase phrase = new Phrase();
			phrase.add(underline);

			Paragraph p = new Paragraph();
			p.add(phrase);
			p.setAlignment(Element.ALIGN_CENTER);

			document.add(p);

			String to_header = "To,\r\nSh." + summon.getName();

			if (summon.getDesignation() != null && summon.getDesignation() != "") {
				to_header = to_header + "," + summon.getDesignation();
			}

			if (summon.getCompany() != null && summon.getCompany() != "")
				to_header = to_header + "\r\n" + summon.getCompany();

			to_header = to_header + "\r\nAddress:" + summon.getCompanyAddress();
			Paragraph copyto = new Paragraph(to_header);
			// Paragraph copyto = new
			// Paragraph("To,\r\n"+summon.getName()+",\r\n"+summon.getCompany()+",\r\nAddress
			// : "+summon.getCompanyAddress()+",\r\nEmail : "+summon.getEmail(),font1);
			copyto.setAlignment(Element.ALIGN_LEFT);

			// Paragraph copyto = new Paragraph("To,\r\n"+summon.getName()+",
			// "+",\r\n"+summon.getCompany()+",\r\nAddress :
			// "+summon.getCompanyAddress()+",\r\nEmail : "+summon.getEmail(),font1);
			copyto.setAlignment(Element.ALIGN_LEFT);

			document.add(copyto);
			document.add(Chunk.NEWLINE);
			Paragraph sub = new Paragraph(summon.getPara1(), f8);
			sub.setAlignment(Element.ALIGN_JUSTIFIED);

			document.add(sub);
			document.add(Chunk.NEWLINE);

			Paragraph para1 = new Paragraph(summon.getPara2(), font1);
			para1.setAlignment(Element.ALIGN_JUSTIFIED);
			document.add(para1);
			document.add(Chunk.NEWLINE);

			Paragraph para2 = new Paragraph(summon.getPara3(), font1);
			para2.setAlignment(Element.ALIGN_JUSTIFIED);
			document.add(para2);
			document.add(Chunk.NEWLINE);

			List company = new RomanList();

			for (String str : summon.getCompanylist()) {

			}
			// company Disalble By Gouthami 09/10/2020
			/*
			 * company.add(companydisplay); document.add(company);
			 * 
			 * document.add(Chunk.NEWLINE);
			 */

			Paragraph para3 = new Paragraph(summon.getPara4(), font1);
			para3.setAlignment(Element.ALIGN_JUSTIFIED);

			document.add(para3);
			document.add(Chunk.NEWLINE);

			Paragraph para4 = new Paragraph(summon.getPara5(), font1);
			para4.setAlignment(Element.ALIGN_JUSTIFIED);

			document.add(para4);
			document.add(Chunk.NEWLINE);
			Paragraph para6 = new Paragraph(summon.getIo() + "\r\n (" + summon.getIODesignation() + " )", font1);
			/*
			 * Paragraph para6 =new Paragraph(summon.getIo()+"\r\n" +
			 * "(Investigating Officer)\r\n" + "",font1);
			 */

			para6.setAlignment(Element.ALIGN_RIGHT);

			document.add(para6);
			document.close();
		} catch (DocumentException e) {
			logger.info(e.getMessage());
		}

		finally {
			if (unsignPdf != null) {
				safeClose(unsignPdf);
			}
		}
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

	public static void noticePreviewdin(NoticeTempDto noticeTempDto, String unSignPdfdinFullPath, String username,
			String OfficeAddress, String companydisplay) throws MalformedURLException, IOException {

		Document document = new Document();
		FileOutputStream unsignPdf = new FileOutputStream(unSignPdfdinFullPath);
		try {

			PdfWriter pdfWriter = PdfWriter.getInstance(document, unsignPdf);
			document.open();

			// Add Text to PDF file ->

			Image image = Image.getInstance(new Utils().getConfigMessage("image.emblemPath"));
			image.setAlignment(Element.ALIGN_CENTER);
			image.scaleToFit(100.0f, 50.0f);

			Font font = FontFactory.getFont(FontFactory.TIMES_BOLD, 12, BaseColor.BLACK);
			Font f8 = FontFactory.getFont(FontFactory.TIMES_BOLD, 10, BaseColor.BLACK);
			Font font1 = FontFactory.getFont(FontFactory.TIMES, 11, BaseColor.BLACK);
			Paragraph head = new Paragraph();
			head.setAlignment(Element.ALIGN_CENTER);
			head.add(image);
			document.add(image);
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
			if (null != noticeTempDto.getCaseNo())
				gh = new Paragraph("DIN : " + noticeTempDto.getCaseNo());
			else
				gh = new Paragraph("DIN : ");
			gh.add(new Chunk(glue));
			if (null != noticeTempDto.getApprovalDate())
				gh.add("Date : " + new SimpleDateFormat("dd-MM-yyyy").format(noticeTempDto.getApprovalDate()));
			else
				gh.add("Date : ");
			document.add(gh);

			Chunk underline = new Chunk("NOTICE");
			underline.setUnderline(0.1f, -2f); // 0.1 thick, -2 y-location

			Phrase phrase = new Phrase();
			phrase.add(underline);

			Paragraph p = new Paragraph();
			p.add(phrase);
			p.setAlignment(Element.ALIGN_CENTER);

			document.add(p);

			String to_header = "To,\r\nSh." + noticeTempDto.getName();

			if (noticeTempDto.getDesignation() != null && noticeTempDto.getDesignation() != "") {
				to_header = to_header + "," + noticeTempDto.getDesignation();
			}

			if (noticeTempDto.getCompany() != null && noticeTempDto.getCompany() != "")
				to_header = to_header + "\r\n" + noticeTempDto.getCompany();

			to_header = to_header + "\r\nAddress:" + noticeTempDto.getCompanyAddress();
			Paragraph copyto = new Paragraph(to_header);
			// Paragraph copyto = new
			// Paragraph("To,\r\n"+summon.getName()+",\r\n"+summon.getCompany()+",\r\nAddress
			// : "+summon.getCompanyAddress()+",\r\nEmail : "+summon.getEmail(),font1);
			copyto.setAlignment(Element.ALIGN_LEFT);

			// Paragraph copyto = new Paragraph("To,\r\n"+summon.getName()+",
			// "+",\r\n"+summon.getCompany()+",\r\nAddress :
			// "+summon.getCompanyAddress()+",\r\nEmail : "+summon.getEmail(),font1);
			copyto.setAlignment(Element.ALIGN_LEFT);

			document.add(copyto);
			document.add(Chunk.NEWLINE);
			Paragraph sub = new Paragraph(noticeTempDto.getPara1(), f8);
			sub.setAlignment(Element.ALIGN_JUSTIFIED);

			document.add(sub);
			document.add(Chunk.NEWLINE);

			Paragraph para1 = new Paragraph(noticeTempDto.getPara2(), font1);
			para1.setAlignment(Element.ALIGN_JUSTIFIED);
			document.add(para1);
			document.add(Chunk.NEWLINE);

			Paragraph para2 = new Paragraph(noticeTempDto.getPara3(), font1);
			para2.setAlignment(Element.ALIGN_JUSTIFIED);
			document.add(para2);
			document.add(Chunk.NEWLINE);

			List company = new RomanList();

			for (String str : noticeTempDto.getCompanylist()) {

			}
			// company Disalble By Gouthami 09/10/2020
			/*
			 * company.add(companydisplay); document.add(company);
			 * 
			 * document.add(Chunk.NEWLINE);
			 */

			Paragraph para3 = new Paragraph(noticeTempDto.getPara4(), font1);
			para3.setAlignment(Element.ALIGN_JUSTIFIED);

			document.add(para3);
			document.add(Chunk.NEWLINE);

			Paragraph para4 = new Paragraph(noticeTempDto.getPara5(), font1);
			para4.setAlignment(Element.ALIGN_JUSTIFIED);

			document.add(para4);
			document.add(Chunk.NEWLINE);
			Paragraph para6 = new Paragraph(noticeTempDto.getIo() + "\r\n (" + noticeTempDto.getIODesignation() + " )",
					font1);
			/*
			 * Paragraph para6 =new Paragraph(summon.getIo()+"\r\n" +
			 * "(Investigating Officer)\r\n" + "",font1);
			 */

			para6.setAlignment(Element.ALIGN_RIGHT);

			document.add(para6);

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
		}

		finally {
			if (unsignPdf != null) {
				safeClose(unsignPdf);
			}
		}

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

	public static void NoticePreview(Long officeOrderId, String unSignPdfFullPath, Long userId)
			throws InterruptedException {
		String path = new Utils().getConfigMessage("file.snmsapi");

		String noticeDin = " ";
		// String s = " "+snmsapi ;

		String s = " " + path + "/PdfNoticeAs1?officeOrderId=" + officeOrderId + "&userId=" + userId + "&noticeDin="
				+ noticeDin;
		;

		String Command = s + " ";

		createpdf1(unSignPdfFullPath, Command);

	}

	private static void createpdf1(String unSignPdfFullPath, String s) throws InterruptedException {
		String pdfExe = new Utils().getConfigMessage("pdf.exe");
		String command = pdfExe + " " + " " + s + unSignPdfFullPath;

		try {

			// Running the above command
			Runtime run = Runtime.getRuntime();
			Process proc = run.exec(command);
			proc.waitFor(45, TimeUnit.SECONDS);

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static void NoticePreviewdin(Long officeOrderId, String unSignPdfdinFullPath, Long userId, String noticeDin)
			throws InterruptedException {
		String path = new Utils().getConfigMessage("file.snmsapi");

		// String s = " "+snmsapi ;

		String s = " " + path + "/PdfNoticeAs1?officeOrderId=" + officeOrderId + "&userId=" + userId + "&noticeDin="
				+ noticeDin;

		String Command = s + " ";

		createpdf1(unSignPdfdinFullPath, Command);
	}

}