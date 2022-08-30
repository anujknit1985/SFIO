package com.snms.utils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.snms.entity.NoticeStatus;
import com.snms.entity.SummonStatus;

public class NoticePdfExporter {
	
    private List<NoticeStatus> Noticelist;
	public NoticePdfExporter(List<NoticeStatus> Noticelist) {
			this.Noticelist = Noticelist;

	}
	 private void writeTableHeader(PdfPTable table) {
	        PdfPCell cell = new PdfPCell();
	        cell.setBackgroundColor(BaseColor.BLUE );
	        cell.setPadding(14);
	         
	        Font font = FontFactory.getFont(FontFactory.HELVETICA);
	        font.setColor(BaseColor.WHITE);
	         
	        cell.setPhrase(new Phrase("S.No", font));
	        table.addCell(cell);
	         
	        cell.setPhrase(new Phrase("Din", font));
	        table.addCell(cell);
	         
	        cell.setPhrase(new Phrase("Case Title  Summon Id", font));
	        table.addCell(cell);
	         
	        cell.setPhrase(new Phrase("Individual  Type", font));
	        table.addCell(cell);
	         
	        cell.setPhrase(new Phrase("CIN available (Y/N)", font));
	        table.addCell(cell);  
	        
	        cell.setPhrase(new Phrase("CIN No", font));
	        table.addCell(cell);
	         
	        cell.setPhrase(new Phrase("Name", font));
	        table.addCell(cell); 
	        
	        cell.setPhrase(new Phrase("Address", font));
	        table.addCell(cell);
	         
	        cell.setPhrase(new Phrase("Mobile  No", font));
	        table.addCell(cell); 
	        
	        cell.setPhrase(new Phrase("Email Id", font));
	        table.addCell(cell);
	         
	        cell.setPhrase(new Phrase("Relation with  Company", font));
	        table.addCell(cell); 
	        
	        cell.setPhrase(new Phrase("Date of   issue Summon", font));
	        table.addCell(cell);
	         
	        cell.setPhrase(new Phrase("Date of Appearance", font));
	        table.addCell(cell); 
	        
	        cell.setPhrase(new Phrase("Issued  By", font));
	        table.addCell(cell);
	         
	        
	        
	    }
	     
	    private void writeTableData(PdfPTable table) {
	    	Long rowCount = 1L;
	        for (NoticeStatus ss : Noticelist) {
	        	
	        	
	        	ss.setId(rowCount);
	            table.addCell(ss.getId().toString());
	            table.addCell(ss.getNoticeDin());
	            table.addCell(ss.getCaseDetails().getCaseTitle()+" "+ss.getSummonNo() );
	            table.addCell(ss.getSummonType().getIndividualType().getIndividualName());
	            table.addCell( ss.getSummonType().getIsCin());
	            if(ss.getSummonType().getIsCin()!=null) {
		            if(ss.getSummonType().getIsCin().equalsIgnoreCase("Y")) {
		            	  table.addCell( ss.getSummonType().getRegistration_no());
		            }
		            else {
		            	ss.setUName("");
		            	 table.addCell(ss.getUName());
		            }
		            }
		            else {
		            	ss.setUName("");
		            	 table.addCell(ss.getUName());
		            }
	            
	            table.addCell( ss.getSummonType().getName());
	            
	            table.addCell( ss.getSummonType().getAddress());
	            table.addCell( ss.getSummonType().getMobileNo());
	            table.addCell( ss.getSummonType().getEmail());
	            if(ss.getSummonType().getRelationwithcompany()!=null) {
	            table.addCell( ss.getSummonType().getRelationwithcompany());
	            }else {
	            	ss.setUName("");
	            	 table.addCell(ss.getUName());
	            }
	            table.addCell( ss.getDateOfAppear());
	            table.addCell( new SimpleDateFormat("dd-MM-yyyy").format(ss.getCreatedDate()));
	            table.addCell( ss.getUName());
	            rowCount++;
	        }
	    }
	     
	    public void export(HttpServletResponse response) throws DocumentException, IOException {
	        Document document = new Document(PageSize.A0);
	        PdfWriter.getInstance(document, response.getOutputStream());
	         
	        document.open();
	        Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
	        font.setSize(40);
	        font.setColor(BaseColor.BLUE);
	         
	        Paragraph p = new Paragraph("List of Signed Notice", font);
	        p.setAlignment(Paragraph.ALIGN_CENTER);
	         
	        document.add(p);
	         
	        PdfPTable table = new PdfPTable(14);
	        table.setWidthPercentage(100f);
	        table.setWidths(new float[] {3.5f, 3.5f, 3.0f, 3.0f, 1.5f, 3.0f, 3.0f, 3.0f, 3.0f, 3.0f, 3.0f, 3.0f, 3.0f, 3.0f});
	        table.setSpacingBefore(10);
	         
	        writeTableHeader(table);
	        writeTableData(table);
	         
	        document.add(table);
	         
	        document.close();
	         
	    }
}
