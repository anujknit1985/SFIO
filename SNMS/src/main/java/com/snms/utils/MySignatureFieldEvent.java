package com.snms.utils;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfAnnotation;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfFormField;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPCellEvent;
import com.itextpdf.text.pdf.PdfWriter;

public class MySignatureFieldEvent implements PdfPCellEvent {
	
	public PdfFormField field;
	 public MySignatureFieldEvent(PdfFormField field) {
	 this.field = field;
	 }
	 
	 public void cellLayout(PdfPCell cell, Rectangle position,
			 PdfContentByte[] canvases) {
			 PdfWriter writer = canvases[0].getPdfWriter();
			 field.setPage();
			 field.setWidget(position, PdfAnnotation.HIGHLIGHT_INVERT);
			 writer.addAnnotation(field);
			 }


}
