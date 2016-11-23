package com.ideationdesignservices.txtbook;

import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfWriter;

public class Txtbook {
    public static final float BOTTOM_MARGIN = 63.0f;
    public static final String FLURRY_KEY = "887JBVRT358GYTMMN2TW";
    public static final int IMAGE_COMPRESSION_PERCENT = 50;
    public static final float LEFT_MARGIN_EVEN = 66.0f;
    public static final float LEFT_MARGIN_ODD = 50.0f;
    public static final int PDF_COMPRESSION_LEVEL = 9;
    public static final float RIGHT_MARGIN_EVEN = 50.0f;
    public static final float RIGHT_MARGIN_ODD = 66.0f;
    public static final float TOP_MARGIN = 63.0f;
    public static final int WARNING_NUM_IMAGES = 50;
    public static final int WARNING_NUM_TXTS = 5000;

    public static float leftMargin(PdfWriter writer, Document document, Boolean hasCover) {
        int pageNumber = writer.getPageNumber();
        if (hasCover.booleanValue()) {
            pageNumber--;
        }
        return pageNumber % 2 == 1 ? RIGHT_MARGIN_EVEN : RIGHT_MARGIN_ODD;
    }

    public static float rightMargin(PdfWriter writer, Document document, Boolean hasCover) {
        int pageNumber = writer.getPageNumber();
        if (hasCover.booleanValue()) {
            pageNumber--;
        }
        return pageNumber % 2 == 1 ? RIGHT_MARGIN_ODD : RIGHT_MARGIN_EVEN;
    }
}
