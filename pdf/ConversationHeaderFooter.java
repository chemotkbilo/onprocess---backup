package com.ideationdesignservices.txtbook.pdf;

import com.ideationdesignservices.txtbook.Txtbook;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseField;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;

public class ConversationHeaderFooter extends PdfPageEventHelper {
    public Font footerFont;
    public Boolean hasFrontCover;
    public Boolean reachedEndOfContent = Boolean.valueOf(false);

    public void onStartPage(PdfWriter writer, Document document) {
        if (!this.reachedEndOfContent.booleanValue()) {
            int pageNumber = writer.getPageNumber();
            if (this.hasFrontCover.booleanValue() || pageNumber > 1) {
                addPageNumberAndDivider(writer, document, pageNumber);
            }
        }
    }

    public void onEndPage(PdfWriter writer, Document document) {
        if (!this.hasFrontCover.booleanValue()) {
            int pageNumber = writer.getPageNumber();
            if (pageNumber <= 1) {
                addPageNumberAndDivider(writer, document, pageNumber);
            }
        }
    }

    public void addPageNumberAndDivider(PdfWriter writer, Document document, int pageNumber) {
        if (this.hasFrontCover.booleanValue()) {
            pageNumber--;
        }
        float leftMargin = Txtbook.leftMargin(writer, document, this.hasFrontCover);
        float rightMargin = Txtbook.rightMargin(writer, document, this.hasFrontCover);
        ColumnText.showTextAligned(writer.getDirectContent(), 2, new Phrase(pageNumber, this.footerFont), (((document.getPageSize().getWidth() - rightMargin) - leftMargin) / BaseField.BORDER_WIDTH_MEDIUM) + leftMargin, document.bottom(), 0.0f);
        PdfContentByte under = writer.getDirectContentUnder();
        under.setColorStroke(new BaseColor(205, 204, 204));
        under.setLineWidth(BaseField.BORDER_WIDTH_THIN);
        under.newPath();
        float x = ((((document.getPageSize().getWidth() - rightMargin) - leftMargin) / BaseField.BORDER_WIDTH_MEDIUM) + leftMargin) - BaseField.BORDER_WIDTH_THICK;
        under.moveTo(x, document.getPageSize().getHeight() - document.topMargin());
        under.lineTo(x, document.bottomMargin() + 20.0f);
        under.closePathStroke();
    }
}
