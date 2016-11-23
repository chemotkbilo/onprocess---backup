package com.ideationdesignservices.txtbook.pdf;

import com.ideationdesignservices.txtbook.Txtbook;
import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfObject;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;

public class StorybookHeaderFooter extends PdfPageEventHelper {
    public Boolean hasFrontCover;
    public Font headerFont;
    public Boolean reachedEndOfContent = Boolean.valueOf(false);
    public String title;

    public void onStartPage(PdfWriter writer, Document document) {
        addHeader(writer, document);
    }

    public void addHeader(PdfWriter writer, Document document) {
        int pageNumber = writer.getPageNumber();
        if ((!this.hasFrontCover.booleanValue() || pageNumber != 1) && !this.reachedEndOfContent.booleanValue()) {
            if (this.hasFrontCover.booleanValue()) {
                pageNumber--;
            }
            String headerString = PdfObject.NOTHING;
            Boolean isLeft = Boolean.valueOf(pageNumber % 2 == 0);
            if (isLeft.booleanValue()) {
                headerString = new StringBuilder(String.valueOf(headerString)).append("   ").toString();
            } else if (this.title != null && this.title.length() > 0) {
                headerString = new StringBuilder(String.valueOf(headerString)).append(this.title).append("  ").toString();
            }
            headerString = new StringBuilder(String.valueOf(headerString)).append("|").append(pageNumber).append("|").toString();
            if (!isLeft.booleanValue()) {
                headerString = new StringBuilder(String.valueOf(headerString)).append("   ").toString();
            } else if (this.title != null && this.title.length() > 0) {
                headerString = new StringBuilder(String.valueOf(headerString)).append("  ").append(this.title).toString();
            }
            Phrase header = new Phrase(headerString, this.headerFont);
            if (isLeft.booleanValue()) {
                ColumnText.showTextAligned(writer.getDirectContent(), 0, header, Txtbook.leftMargin(writer, document, this.hasFrontCover), document.top() + 7.0f, 0.0f);
                return;
            }
            ColumnText.showTextAligned(writer.getDirectContent(), 2, header, document.getPageSize().getWidth() - Txtbook.rightMargin(writer, document, this.hasFrontCover), document.top() + 7.0f, 0.0f);
        }
    }
}
