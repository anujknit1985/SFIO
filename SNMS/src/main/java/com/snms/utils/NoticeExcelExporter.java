package com.snms.utils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.snms.entity.NoticeStatus;
import com.snms.entity.SummonStatus;

public class NoticeExcelExporter {
	 private XSSFWorkbook workbook;
	    private XSSFSheet sheet;
	    private List<NoticeStatus> Noticelist;
	public NoticeExcelExporter(List<NoticeStatus> Noticelist) {
		this.Noticelist = Noticelist;
        workbook = new XSSFWorkbook();
	}
	private void writeHeaderLine() {
	        sheet = workbook.createSheet("Signed Notice ");
	         
	        Row row = sheet.createRow(0);
	         
	        CellStyle style = workbook.createCellStyle();
	        XSSFFont font = workbook.createFont();
	        font.setBold(true);
	        font.setFontHeight(16);
	        style.setFont(font);
	         
	        createCell(row, 0, "S.No", style);      
	        createCell(row, 1, "Din", style);      
	        createCell(row, 2, "Case Title  Summon Id", style);       
	        createCell(row, 3, "Individual  Type", style);    
	        createCell(row, 4, "CIN available (Y/N)", style);
	        createCell(row, 5, "CIN No", style);
	        createCell(row, 6, "Name", style);
	        createCell(row, 7, "Address", style);
	        createCell(row, 8, "Mobile  No", style);
	        createCell(row, 9, "Email Id", style);
	        createCell(row, 10, "Relation with  Company", style);
	        createCell(row, 11, "Date of   issue ", style);
	        createCell(row, 12, "Date of Appearance", style);
	        createCell(row, 13, "Issued  By", style);
	        
	         
	    }
	     
	    private void createCell(Row row, int columnCount, Object value, CellStyle style) {
	        sheet.autoSizeColumn(columnCount);
	        Cell cell = row.createCell(columnCount);
	        if (value instanceof Integer) {
	            cell.setCellValue((Integer) value);
	        } else if (value instanceof Boolean) {
	            cell.setCellValue((Boolean) value);
	        }else {
	            cell.setCellValue((String) value);
	        }
	        cell.setCellStyle(style);
	    }
	     
	    private void writeDataLines() {
	        int rowCount = 1;
	        int count = 1;       
	        CellStyle style = workbook.createCellStyle();
	        XSSFFont font = workbook.createFont();
	        font.setFontHeight(14);
	        style.setFont(font);
	                 
	        for (NoticeStatus ss : Noticelist) {
	            Row row = sheet.createRow(rowCount++);
	            int columnCount = 0;
	             
	            createCell(row, columnCount++, count, style);
	          
	            createCell(row, columnCount++, ss.getNoticeDin()
	            		, style);
	            createCell(row, columnCount++, ss.getCaseDetails().getCaseTitle()+" "+ss.getSummonNo() , style);
	            createCell(row, columnCount++, ss.getSummonType().getIndividualType().getIndividualName(), style);
	            createCell(row, columnCount++, ss.getSummonType().getIsCin(), style);
	            if(ss.getSummonType().getIsCin()!=null) {
	            if(ss.getSummonType().getIsCin().equalsIgnoreCase("Y")) {
	            createCell(row, columnCount++, ss.getSummonType().getRegistration_no(), style);
	            }
	            else {
	            	 createCell(row, columnCount++, " ", style);
	            }
	            }
	            else {
	            	 createCell(row, columnCount++, " ", style);
	            }
	            createCell(row, columnCount++, ss.getSummonType().getName(), style);
	            createCell(row, columnCount++, ss.getSummonType().getAddress(), style);
	            createCell(row, columnCount++, ss.getSummonType().getMobileNo(), style);
	            
	            createCell(row, columnCount++, ss.getSummonType().getEmail(), style);
	            createCell(row, columnCount++, ss.getSummonType().getRelationwithcompany(), style);
	            createCell(row, columnCount++,  new SimpleDateFormat("dd-MM-yyyy").format(ss.getCreatedDate()), style);
	            createCell(row, columnCount++, ss.getDateOfAppear(), style);
	            createCell(row, columnCount++, ss.getUName(), style);
	             count++;
	        }
	    }
	     
	    public void export(HttpServletResponse response) throws IOException {
	        writeHeaderLine();
	        writeDataLines();
	         
	        ServletOutputStream outputStream = response.getOutputStream();
	        workbook.write(outputStream);
	        workbook.close();
	         
	        outputStream.close();
	         
	    }

}
