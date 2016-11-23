package com.ideationdesignservices.txtbook.pdf;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Environment;
import com.flurry.android.FlurryAgent;
import com.flurry.org.codehaus.jackson.util.MinimalPrettyPrinter;
import com.ideationdesignservices.txtbook.Txtbook;
import com.ideationdesignservices.txtbook.util.ImageUtilities;
import com.ideationdesignservices.txtbook.util.MMSUtilities;
import com.itextpdf.license.LicenseKey;
import com.itextpdf.text.Anchor;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.html.HtmlTags;
import com.itextpdf.text.pdf.BaseField;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfAction;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfObject;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.TimeZone;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class TxtBookPdf {
    private static final float BUBBLE_B_HEIGHT = 9.6f;
    private static final float BUBBLE_L_WIDTH = 9.6f;
    private static final float BUBBLE_R_WIDTH = 3.1f;
    private static final float BUBBLE_TEXT_INDENT_ALTERNATE = 5.0f;
    private static final float BUBBLE_TEXT_INDENT_MAIN = -1.0f;
    private static final float BUBBLE_T_HEIGHT = 1.44f;
    private static final float[][] COLUMNS_ALT = new float[][]{new float[]{Txtbook.RIGHT_MARGIN_ODD, Txtbook.TOP_MARGIN, 284.0f, 729.0f}, new float[]{344.0f, Txtbook.TOP_MARGIN, 562.0f, 729.0f}};
    private static final float[][] COLUMNS_REG = new float[][]{new float[]{Txtbook.RIGHT_MARGIN_EVEN, Txtbook.TOP_MARGIN, 268.0f, 729.0f}, new float[]{328.0f, Txtbook.TOP_MARGIN, 546.0f, 729.0f}};
    private static final float COLUMN_WIDTH = 218.0f;
    private static final float GAP_WIDTH = 60.0f;
    private static final float MAX_COLUMN_CONTENT_WIDTH = 206.0f;
    private final String[] closings = new String[]{" said [speaker].", " replied [speaker].", " answered [speaker].", " responded [speaker].", " texted [speaker]."};
    private int currentColumn;
    private float currentY;
    public String filename;
    private Random generator = new Random();
    private int lastPhraseIdx = -1;
    private Context mContext;
    private final String[] openings = new String[]{"[speaker] continued, ", "[speaker] said, ", "Then [speaker] replied, ", "[speaker] went on, ", "[speaker] texted, ", "Then [speaker] said, ", "[speaker] then replied with, ", "To which [speaker] responded, "};
    private Font sansFont11Gray;
    private Font sansFont6Gray;
    private Font sansFont9;
    private Font sansFont9Gray;
    private Font serifFont11;
    private Font serifFont14;
    private Font serifFont24;
    private Font serifFont8Gray;
    public TxtBookPdfSettings settings;
    private Boolean wasLastPhraseOpening = Boolean.valueOf(false);
    private PdfWriter writer;

    public void createPDF(Context context) throws DocumentException, IOException {
        LicenseKey.loadLicenseFile(context.getAssets().open("itextkey.xml"));
        this.mContext = context;
        Map<String, String> pdfParams = new HashMap();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_Hmmss", Locale.US);
        dateFormat.setTimeZone(TimeZone.getDefault());
        this.filename = "txtbook_" + dateFormat.format(new Date()) + ".pdf";
        float leftMargin = Txtbook.RIGHT_MARGIN_EVEN;
        float rightMargin = Txtbook.RIGHT_MARGIN_ODD;
        if (this.settings.addFrontCover.booleanValue()) {
            leftMargin = Txtbook.RIGHT_MARGIN_ODD;
            rightMargin = Txtbook.RIGHT_MARGIN_EVEN;
        }
        Document document = new Document(PageSize.LETTER, leftMargin, rightMargin, Txtbook.TOP_MARGIN, Txtbook.TOP_MARGIN);
        File file = new File(Environment.getExternalStorageDirectory(), this.filename);
        if (!(file.exists() && file.canRead())) {
            file.createNewFile();
        }
        this.writer = PdfWriter.getInstance(document, new FileOutputStream(file));
        this.writer.setCompressionLevel(this.settings.compressionLevel);
        this.writer.setStrictImageSequence(true);
        FontFactory.register("assets/fonts/DroidSans.ttf");
        this.sansFont6Gray = FontFactory.getFont("DroidSans", BaseFont.WINANSI, true, 6.0f, 0, new BaseColor(152, 152, 152));
        this.sansFont9 = FontFactory.getFont("DroidSans", BaseFont.WINANSI, true, 9.0f);
        this.sansFont9Gray = FontFactory.getFont("DroidSans", BaseFont.WINANSI, true, 9.0f, 0, new BaseColor(103, 103, 103));
        this.sansFont11Gray = FontFactory.getFont("DroidSans", BaseFont.WINANSI, true, 11.0f, 0, new BaseColor(152, 152, 152));
        FontFactory.register("assets/fonts/CourierNew.ttf");
        this.serifFont8Gray = FontFactory.getFont("Courier New", BaseFont.WINANSI, true, 8.0f, 0, new BaseColor(103, 103, 103));
        this.serifFont11 = FontFactory.getFont("Courier New", BaseFont.WINANSI, true, 11.0f);
        this.serifFont14 = FontFactory.getFont("Courier New", BaseFont.WINANSI, true, 14.0f);
        this.serifFont24 = FontFactory.getFont("Courier New", BaseFont.WINANSI, true, 24.0f);
        document.open();
        document.setMarginMirroring(true);
        document.addCreationDate();
        document.addCreator("Created with txt-book for Android www.txt-book.com");
        document.addTitle("txt-book for Android");
        if (this.settings.bookStyle == 1) {
            ConversationHeaderFooter hf = new ConversationHeaderFooter();
            hf.footerFont = this.sansFont9;
            hf.hasFrontCover = this.settings.addFrontCover;
            this.writer.setPageEvent(hf);
        } else {
            StorybookHeaderFooter hf2 = new StorybookHeaderFooter();
            hf2.title = this.settings.bookCoverTitle;
            hf2.headerFont = this.serifFont8Gray;
            hf2.hasFrontCover = this.settings.addFrontCover;
            this.writer.setPageEvent(hf2);
        }
        if (this.settings.addFrontCover.booleanValue()) {
            createFrontCoverPage(document, this.settings.bookCoverTitle, this.settings.addFrontCoverImage.booleanValue() ? this.settings.coverPhoto : null);
            pdfParams.put("PDF Front Cover", "YES");
        } else {
            pdfParams.put("PDF Front Cover", "NO");
        }
        if (this.settings.addFrontCoverImage.booleanValue()) {
            pdfParams.put("PDF Front Cover Image", "YES");
        } else {
            pdfParams.put("PDF Front Cover Image", "NO");
        }
        if (this.settings.bookStyle == 1) {
            createContentPagesConversation(document);
        } else {
            createContentPagesStorybook(document, this.settings.bookCoverTitle);
        }
        int pages = this.writer.getPageNumber();
        if (this.settings.bookStyle == 2) {
            ((StorybookHeaderFooter) this.writer.getPageEvent()).reachedEndOfContent = Boolean.valueOf(true);
        } else {
            ((ConversationHeaderFooter) this.writer.getPageEvent()).reachedEndOfContent = Boolean.valueOf(true);
        }
        if (this.settings.addBackCover.booleanValue()) {
            pages++;
            pdfParams.put("PDF Back Cover", "YES");
            if (pages % 2 == 1) {
                createBlankSpacerPage(document);
            }
            createBackCoverPage(document, this.settings.backCoverNote);
        } else {
            pdfParams.put("PDF Back Cover", "NO");
            if (pages % 2 == 1) {
                createBlankSpacerPage(document);
            }
        }
        if (this.settings.bookStyle == 2) {
            pdfParams.put("PDF Style", "Storybook");
        } else {
            pdfParams.put("PDF Style", "Conversation");
        }
        pdfParams.put("PDF Num Pages", Integer.valueOf(this.writer.getPageNumber()).toString());
        document.close();
        FlurryAgent.logEvent("PDF_CREATED", (Map) pdfParams);
    }

    public Boolean createFrontCoverPage(Document document, String coverTitle, Bitmap photo) throws DocumentException, MalformedURLException, IOException {
        if (photo != null) {
            int imageMaxWidth;
            int imageMaxHeight;
            int imagePosX;
            int imagePosY;
            if (photo.getWidth() < photo.getHeight()) {
                imageMaxWidth = 900;
                imageMaxHeight = 1200;
                imagePosX = 198;
                imagePosY = 379;
            } else {
                imageMaxWidth = 1200;
                imageMaxHeight = 900;
                imagePosX = 162;
                imagePosY = 379;
            }
            OutputStream stream = new ByteArrayOutputStream();
            ImageUtilities.scaleCenterCrop(photo, imageMaxWidth, imageMaxHeight).compress(CompressFormat.JPEG, 50, stream);
            Image coverImage = Image.getInstance(stream.toByteArray());
            coverImage.setAbsolutePosition(0.0f, 0.0f);
            PdfTemplate t = this.writer.getDirectContent().createTemplate((float) imageMaxWidth, (float) imageMaxHeight);
            t.newPath();
            t.moveTo(0.0f, (float) imageMaxHeight);
            t.lineTo(0.0f, 71.0f);
            t.lineTo(17.0f, 71.0f);
            t.lineTo(17.0f, 0.0f);
            t.lineTo(72.0f, 71.0f);
            t.lineTo((float) imageMaxWidth, 71.0f);
            t.lineTo((float) imageMaxWidth, (float) imageMaxHeight);
            t.lineTo(0.0f, (float) imageMaxHeight);
            t.closePath();
            t.clip();
            t.newPath();
            t.addImage(coverImage);
            t.setColorStroke(new BaseColor(0, 0, 0));
            t.setLineWidth(BUBBLE_TEXT_INDENT_ALTERNATE);
            t.newPath();
            t.moveTo(0.0f, (float) imageMaxHeight);
            t.lineTo(0.0f, 71.0f);
            t.lineTo(17.0f, 71.0f);
            t.lineTo(17.0f, 0.0f);
            t.lineTo(72.0f, 71.0f);
            t.lineTo((float) imageMaxWidth, 71.0f);
            t.lineTo((float) imageMaxWidth, (float) imageMaxHeight);
            t.lineTo(0.0f, (float) imageMaxHeight);
            t.closePathStroke();
            Image clipped = Image.getInstance(t);
            clipped.scalePercent(24.0f);
            clipped.setAbsolutePosition((float) imagePosX, (float) imagePosY);
            clipped.setCompressionLevel(this.settings.compressionLevel);
            clipped.setAlignment(5);
            document.add(clipped);
        }
        if (coverTitle != null && coverTitle.length() > 0) {
            PdfContentByte canvas = this.writer.getDirectContent();
            Paragraph coverTitleEl = new Paragraph(coverTitle, this.serifFont24);
            coverTitleEl.setAlignment(1);
            PdfPTable table = new PdfPTable(1);
            table.setTotalWidth(311.0f);
            PdfPCell cell = new PdfPCell();
            cell.setBorder(0);
            cell.addElement(coverTitleEl);
            cell.setPadding(0.0f);
            cell.setIndent(0.0f);
            table.addCell(cell);
            table.completeRow();
            table.writeSelectedRows(0, -1, 147.0f, 390.0f, canvas);
        }
        return Boolean.valueOf(true);
    }

    public Boolean createBackCoverPage(Document document, String backCoverNote) throws DocumentException, MalformedURLException, IOException {
        document.newPage();
        Image backCoverImageFrame = Image.getInstance(ImageUtilities.getImageDataForFile(this.mContext, "pdf/txtbook_backpage_300.png"));
        backCoverImageFrame.scalePercent(24.0f);
        backCoverImageFrame.setAbsolutePosition(87.0f, 78.0f);
        backCoverImageFrame.setCompressionLevel(this.settings.compressionLevel);
        document.add(backCoverImageFrame);
        PdfContentByte canvas = this.writer.getDirectContent();
        PdfPTable table = new PdfPTable(1);
        table.setTotalWidth(215.0f);
        if (backCoverNote != null && backCoverNote.length() > 0) {
            Paragraph backCoverEl = new Paragraph(backCoverNote, this.serifFont14);
            PdfPCell cell = new PdfPCell();
            cell.setBorder(0);
            cell.addElement(backCoverEl);
            cell.setPadding(13.0f);
            cell.setIndent(0.0f);
            cell.setFixedHeight(310.0f);
            table.addCell(cell);
            table.completeRow();
        }
        Element backUrl = new Anchor("txt-book.com", this.sansFont11Gray);
        backUrl.setName("txt-book.com");
        backUrl.setReference("http://www.txt-book.com");
        Paragraph paragraph = new Paragraph();
        paragraph.setAlignment(2);
        paragraph.add(backUrl);
        PdfPCell cell2 = new PdfPCell();
        cell2.setBorder(0);
        cell2.setHorizontalAlignment(2);
        cell2.addElement(paragraph);
        cell2.setPadding(0.0f);
        cell2.setPaddingTop(0.0f);
        cell2.setIndent(0.0f);
        table.addCell(cell2);
        table.completeRow();
        table.writeSelectedRows(0, -1, 306.0f, 400.0f, canvas);
        return Boolean.valueOf(true);
    }

    public Boolean createBlankSpacerPage(Document document) {
        document.newPage();
        this.writer.setPageEmpty(false);
        return Boolean.valueOf(true);
    }

    public int createContentPagesConversation(Document document) throws DocumentException {
        float[][] COLUMNS;
        document.newPage();
        ColumnText ct = new ColumnText(this.writer.getDirectContent());
        this.currentColumn = 0;
        if (COLUMNS_REG[0][0] == Txtbook.leftMargin(this.writer, document, this.settings.addFrontCover)) {
            COLUMNS = COLUMNS_REG;
        } else {
            COLUMNS = COLUMNS_ALT;
        }
        ct.setSimpleColumn(COLUMNS[this.currentColumn][0], COLUMNS[this.currentColumn][1], COLUMNS[this.currentColumn][2], COLUMNS[this.currentColumn][3]);
        Cursor cursor = this.mContext.getContentResolver().query(Uri.parse("content://mms-sms/conversations/" + this.settings.threadId), new String[]{"_id", "ct_t", "normalized_date"}, "normalized_date >= " + this.settings.startDateMillis + " AND normalized_date <= " + this.settings.endDateMillis, null, "normalized_date");
        Iterator<Integer> itr = this.settings.getSelectedRows().iterator();
        while (itr.hasNext()) {
            if (cursor.moveToPosition(((Integer) itr.next()).intValue())) {
                String senderString;
                Boolean isMe;
                Long messageId = Long.valueOf(cursor.getLong(cursor.getColumnIndex("_id")));
                String mimetype = cursor.getString(cursor.getColumnIndex("ct_t"));
                if (MMSUtilities.getMessageSenderType(this.mContext, messageId, mimetype) == 1) {
                    senderString = this.settings.theirName;
                    isMe = Boolean.valueOf(false);
                } else {
                    senderString = this.settings.myName;
                    isMe = Boolean.valueOf(true);
                }
                String contentString = PdfObject.NOTHING;
                Boolean isVideo = Boolean.valueOf(false);
                if ("application/vnd.wap.multipart.related".equals(mimetype)) {
                    Cursor mmsCursor = this.mContext.getContentResolver().query(Uri.parse("content://mms/part"), null, "mid=" + messageId, null, null);
                    Boolean hasNext = Boolean.valueOf(mmsCursor.moveToFirst());
                    while (hasNext.booleanValue()) {
                        contentString = PdfObject.NOTHING;
                        Bitmap contentImage = null;
                        isVideo = Boolean.valueOf(false);
                        String partId = mmsCursor.getString(mmsCursor.getColumnIndex("_id"));
                        String mimetype2 = mmsCursor.getString(mmsCursor.getColumnIndex("ct"));
                        if ("application/smil".equals(mimetype2)) {
                            hasNext = Boolean.valueOf(mmsCursor.moveToNext());
                        } else {
                            if (mimetype2.startsWith("image/")) {
                                contentImage = MMSUtilities.getMmsImage(this.mContext, partId);
                            } else {
                                if (mimetype2.startsWith("text/")) {
                                    contentString = mmsCursor.getString(mmsCursor.getColumnIndex("_data")) != null ? MMSUtilities.getMmsText(this.mContext, partId) : mmsCursor.getString(mmsCursor.getColumnIndex("text"));
                                } else {
                                    if (mimetype2.startsWith("video/")) {
                                        isVideo = Boolean.valueOf(true);
                                        contentImage = wrapVideoThumbnailWithFilm(this.mContext, MMSUtilities.getMmsVideoThumbnail(this.mContext, partId));
                                    } else {
                                        contentString = mimetype2.startsWith("audio/") ? "[audio message]" : "[unknown message type]";
                                    }
                                }
                            }
                            doAddNextConversation(document, cursor, ct, senderString, contentString, contentImage, isVideo, isMe);
                            hasNext = Boolean.valueOf(mmsCursor.moveToNext());
                        }
                    }
                    mmsCursor.close();
                } else {
                    Cursor smsCursor = this.mContext.getContentResolver().query(Uri.parse("content://sms"), new String[]{HtmlTags.BODY, LicenseKey.LICENSE_DATE}, "_id = " + messageId, null, null);
                    if (smsCursor.moveToFirst()) {
                        contentString = smsCursor.getString(smsCursor.getColumnIndex(HtmlTags.BODY));
                    }
                    smsCursor.close();
                    doAddNextConversation(document, cursor, ct, senderString, contentString, null, Boolean.valueOf(false), isMe);
                }
            }
        }
        return 0;
    }

    public void doAddNextConversation(Document document, Cursor cursor, ColumnText ct, String senderString, String contentString, Bitmap contentImage, Boolean isVideo, Boolean isMe) {
        float[][] COLUMNS;
        if (COLUMNS_REG[0][0] == Txtbook.leftMargin(this.writer, document, this.settings.addFrontCover)) {
            COLUMNS = COLUMNS_REG;
        } else {
            COLUMNS = COLUMNS_ALT;
        }
        String dateString = PdfObject.NOTHING;
        if (this.settings.useTimestamps.booleanValue()) {
            Long date = Long.valueOf(cursor.getLong(cursor.getColumnIndex("normalized_date")));
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM dd, yyyy h:mm a", Locale.US);
            simpleDateFormat.setTimeZone(TimeZone.getDefault());
            dateString = simpleDateFormat.format(new Date(date.longValue()));
        }
        try {
            this.currentY = ct.getYLine();
            addConversationPart(ct, this.currentColumn, dateString, senderString, contentString, contentImage, isVideo, isMe);
            if (ColumnText.hasMoreText(ct.go(true))) {
                this.currentColumn = (this.currentColumn + 1) % 2;
                if (this.currentColumn == 0) {
                    document.newPage();
                    if (COLUMNS_REG[0][0] == Txtbook.leftMargin(this.writer, document, this.settings.addFrontCover)) {
                        COLUMNS = COLUMNS_REG;
                    } else {
                        COLUMNS = COLUMNS_ALT;
                    }
                }
                ct.setSimpleColumn(COLUMNS[this.currentColumn][0], COLUMNS[this.currentColumn][1], COLUMNS[this.currentColumn][2], COLUMNS[this.currentColumn][3]);
                this.currentY = COLUMNS[this.currentColumn][3];
            }
            ct.setYLine(this.currentY);
            ct.setText(null);
            float width = addConversationPart(ct, this.currentColumn, dateString, senderString, contentString, contentImage, isVideo, isMe);
            int status = ct.go(false);
            Document document2 = document;
            ColumnText columnText = ct;
            addConversationBackground(document2, columnText, this.currentColumn, this.currentY, width, this.currentY - ct.getYLine(), isMe);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public float addConversationPart(ColumnText ct, int column, String dateString, String senderString, String contentString, Bitmap contentBitmap, Boolean isVideo, Boolean isMe) throws DocumentException, MalformedURLException, IOException {
        float messageWidth = 196.0f;
        Chunk dateChunk = new Chunk(new StringBuilder(String.valueOf(dateString)).append(MinimalPrettyPrinter.DEFAULT_ROOT_VALUE_SEPARATOR).toString(), this.sansFont6Gray);
        float dateWidth = dateChunk.getWidthPoint();
        Paragraph contentParagraph = new Paragraph();
        if (contentString.length() > 0) {
            Element contentChunk = new Chunk(contentString, this.sansFont9);
            messageWidth = contentChunk.getWidthPoint();
            contentParagraph.add(contentChunk);
        }
        if (messageWidth < dateWidth) {
            messageWidth = dateWidth;
        }
        if (messageWidth > MAX_COLUMN_CONTENT_WIDTH) {
            messageWidth = MAX_COLUMN_CONTENT_WIDTH;
            dateWidth += 7.0f;
        }
        Paragraph dateParagraph = new Paragraph(dateChunk);
        if (isMe.booleanValue()) {
            dateParagraph.setAlignment(0);
            dateParagraph.setIndentationLeft((((BUBBLE_L_WIDTH + messageWidth) + BUBBLE_R_WIDTH) + 7.0f) - dateWidth);
        } else {
            dateParagraph.setAlignment(2);
            dateParagraph.setIndentationRight((((BUBBLE_L_WIDTH + messageWidth) + BUBBLE_R_WIDTH) + 7.0f) - dateWidth);
        }
        ct.addElement(dateParagraph);
        contentParagraph.setExtraParagraphSpace(10.0f);
        if (contentString.length() > 0) {
            contentParagraph.setAlignment(0);
            if (isMe.booleanValue()) {
                contentParagraph.setIndentationLeft(8.6f);
                contentParagraph.setIndentationRight(BUBBLE_TEXT_INDENT_ALTERNATE);
            } else {
                contentParagraph.setIndentationRight(8.6f);
                float indentLeft = COLUMN_WIDTH - (BUBBLE_L_WIDTH + messageWidth);
                if (messageWidth == MAX_COLUMN_CONTENT_WIDTH) {
                    indentLeft += BUBBLE_TEXT_INDENT_ALTERNATE;
                }
                contentParagraph.setIndentationLeft(indentLeft);
            }
            ct.addElement(contentParagraph);
        } else if (contentBitmap != null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            if (isVideo.booleanValue()) {
                contentBitmap.compress(CompressFormat.PNG, 50, stream);
            } else {
                contentBitmap.compress(CompressFormat.JPEG, 50, stream);
            }
            Image contentImage = Image.getInstance(stream.toByteArray());
            contentImage.scaleToFit(198.0f, 198.0f);
            if (isVideo.booleanValue()) {
                contentImage.setCompressionLevel(this.settings.compressionLevel);
            }
            contentImage.setSpacingBefore(10.0f);
            contentImage.setSpacingAfter(10.0f);
            if (isMe.booleanValue()) {
                contentImage.setAlignment(1);
            } else {
                contentImage.setAlignment(1);
            }
            ct.addElement(contentImage);
        }
        Paragraph senderParagraph = new Paragraph(new Chunk(senderString, this.sansFont9Gray));
        if (!isMe.booleanValue()) {
            senderParagraph.setAlignment(2);
        }
        senderParagraph.setSpacingAfter(BUBBLE_TEXT_INDENT_ALTERNATE);
        ct.addElement(senderParagraph);
        return messageWidth;
    }

    public void addConversationBackground(Document document, ColumnText ct, int column, float top, float messageWidth, float messageHeight, Boolean isMe) throws DocumentException, MalformedURLException, IOException {
        float[][] COLUMNS;
        if (messageWidth < MAX_COLUMN_CONTENT_WIDTH) {
            messageWidth += 8.0f;
        }
        messageHeight -= 35.0f;
        top -= BaseField.BORDER_WIDTH_THICK;
        if (COLUMNS_REG[0][0] == Txtbook.leftMargin(this.writer, document, this.settings.addFrontCover)) {
            COLUMNS = COLUMNS_REG;
        } else {
            COLUMNS = COLUMNS_ALT;
        }
        float left = COLUMNS[column][0];
        PdfContentByte under = this.writer.getDirectContentUnder();
        PdfTemplate bg = this.writer.getDirectContentUnder().createTemplate((BUBBLE_L_WIDTH + messageWidth) + BUBBLE_R_WIDTH, (BUBBLE_T_HEIGHT + messageHeight) + BUBBLE_L_WIDTH);
        String num = isMe.booleanValue() ? "1" : "2";
        Image bgTL = Image.getInstance(ImageUtilities.getImageDataForFile(this.mContext, "pdf/bubble_" + num + "_tl.png"));
        bgTL.scaleAbsoluteWidth(BUBBLE_L_WIDTH);
        bgTL.scaleAbsoluteHeight(BUBBLE_T_HEIGHT);
        bgTL.setAbsolutePosition(0.0f, BUBBLE_L_WIDTH + messageHeight);
        bg.addImage(bgTL);
        Image bgT = Image.getInstance(ImageUtilities.getImageDataForFile(this.mContext, "pdf/bubble_" + num + "_t.png"));
        bgT.scaleAbsoluteWidth(messageWidth);
        bgT.scaleAbsoluteHeight(BUBBLE_T_HEIGHT);
        bgT.setAbsolutePosition(BUBBLE_L_WIDTH, BUBBLE_L_WIDTH + messageHeight);
        bg.addImage(bgT);
        Image bgTR = Image.getInstance(ImageUtilities.getImageDataForFile(this.mContext, "pdf/bubble_" + num + "_tr.png"));
        bgTR.scaleAbsoluteWidth(BUBBLE_R_WIDTH);
        bgTR.scaleAbsoluteHeight(BUBBLE_T_HEIGHT);
        bgTR.setAbsolutePosition(BUBBLE_L_WIDTH + messageWidth, BUBBLE_L_WIDTH + messageHeight);
        bg.addImage(bgTR);
        Image bgL = Image.getInstance(ImageUtilities.getImageDataForFile(this.mContext, "pdf/bubble_" + num + "_l.png"));
        bgL.scaleAbsoluteWidth(BUBBLE_L_WIDTH);
        bgL.scaleAbsoluteHeight(messageHeight);
        bgL.setAbsolutePosition(0.0f, BUBBLE_L_WIDTH);
        bg.addImage(bgL);
        bg.saveState();
        if (isMe.booleanValue()) {
            bg.setRGBColorFill(241, 241, 241);
        } else {
            bg.setRGBColorFill(208, 231, 196);
        }
        bg.rectangle(BUBBLE_L_WIDTH, BUBBLE_L_WIDTH, messageWidth, messageHeight);
        bg.fill();
        bg.restoreState();
        Image bgR = Image.getInstance(ImageUtilities.getImageDataForFile(this.mContext, "pdf/bubble_" + num + "_r.png"));
        bgR.scaleAbsoluteWidth(BUBBLE_R_WIDTH);
        bgR.scaleAbsoluteHeight(messageHeight);
        bgR.setAbsolutePosition(BUBBLE_L_WIDTH + messageWidth, BUBBLE_L_WIDTH);
        bg.addImage(bgR);
        Image bgBL = Image.getInstance(ImageUtilities.getImageDataForFile(this.mContext, "pdf/bubble_" + num + "_bl.png"));
        bgBL.scaleAbsoluteWidth(BUBBLE_L_WIDTH);
        bgBL.scaleAbsoluteHeight(BUBBLE_L_WIDTH);
        bgBL.setAbsolutePosition(0.0f, 0.0f);
        bg.addImage(bgBL);
        Image bgB = Image.getInstance(ImageUtilities.getImageDataForFile(this.mContext, "pdf/bubble_" + num + "_b.png"));
        bgB.scaleAbsoluteWidth(messageWidth);
        bgB.scaleAbsoluteHeight(BUBBLE_L_WIDTH);
        bgB.setAbsolutePosition(BUBBLE_L_WIDTH, 0.0f);
        bg.addImage(bgB);
        Image bgBR = Image.getInstance(ImageUtilities.getImageDataForFile(this.mContext, "pdf/bubble_" + num + "_br.png"));
        bgBR.scaleAbsoluteWidth(BUBBLE_R_WIDTH);
        bgBR.scaleAbsoluteHeight(BUBBLE_L_WIDTH);
        bgBR.setAbsolutePosition(BUBBLE_L_WIDTH + messageWidth, 0.0f);
        bg.addImage(bgBR);
        if (!isMe.booleanValue()) {
            bg.setMatrix(BUBBLE_TEXT_INDENT_MAIN, 0.0f, 0.0f, BaseField.BORDER_WIDTH_THIN, 0.0f, 0.0f);
            left += COLUMN_WIDTH;
        }
        under.addTemplate(bg, left, (top - messageHeight) - 20.0f);
    }

    public int createContentPagesStorybook(Document document, String title) throws DocumentException {
        document.newPage();
        ((StorybookHeaderFooter) this.writer.getPageEvent()).addHeader(this.writer, document);
        Cursor cursor = this.mContext.getContentResolver().query(Uri.parse("content://mms-sms/conversations/" + this.settings.threadId), new String[]{"_id", "ct_t", "normalized_date"}, "normalized_date >= " + this.settings.startDateMillis + " AND normalized_date <= " + this.settings.endDateMillis, null, "normalized_date");
        int i = 0;
        Long previousDate = null;
        String previousSenderString = PdfObject.NOTHING;
        Iterator<Integer> itr = this.settings.getSelectedRows().iterator();
        while (itr.hasNext()) {
            if (cursor.moveToPosition(((Integer) itr.next()).intValue())) {
                Long messageId = Long.valueOf(cursor.getLong(cursor.getColumnIndex("_id")));
                String mimetype = cursor.getString(cursor.getColumnIndex("ct_t"));
                int type = MMSUtilities.getMessageSenderType(this.mContext, messageId, mimetype);
                String senderString = this.settings.myName;
                String receiverString = this.settings.theirName;
                if (type == 1) {
                    senderString = this.settings.theirName;
                    receiverString = this.settings.myName;
                }
                String contentString = PdfObject.NOTHING;
                Boolean isVideo = Boolean.valueOf(false);
                Long date;
                if ("application/vnd.wap.multipart.related".equals(mimetype)) {
                    Cursor mmsCursor = this.mContext.getContentResolver().query(Uri.parse("content://mms/part"), null, "mid=" + messageId, null, null);
                    Boolean hasNext = Boolean.valueOf(mmsCursor.moveToFirst());
                    while (hasNext.booleanValue()) {
                        contentString = PdfObject.NOTHING;
                        Bitmap contentImage = null;
                        isVideo = Boolean.valueOf(false);
                        String partId = mmsCursor.getString(mmsCursor.getColumnIndex("_id"));
                        String mimetype2 = mmsCursor.getString(mmsCursor.getColumnIndex("ct"));
                        if ("application/smil".equals(mimetype2)) {
                            hasNext = Boolean.valueOf(mmsCursor.moveToNext());
                        } else {
                            if (mimetype2.startsWith("image/")) {
                                contentImage = MMSUtilities.getMmsImage(this.mContext, partId);
                            } else {
                                if (mimetype2.startsWith("text/")) {
                                    contentString = mmsCursor.getString(mmsCursor.getColumnIndex("_data")) != null ? MMSUtilities.getMmsText(this.mContext, partId) : mmsCursor.getString(mmsCursor.getColumnIndex("text"));
                                } else {
                                    if (mimetype2.startsWith("video/")) {
                                        isVideo = Boolean.valueOf(true);
                                        contentImage = wrapVideoThumbnailWithFilm(this.mContext, MMSUtilities.getMmsVideoThumbnail(this.mContext, partId));
                                    } else {
                                        contentString = mimetype2.startsWith("audio/") ? "[audio message]" : "[unknown message type]";
                                    }
                                }
                            }
                            date = null;
                            if (this.settings.useTimestamps.booleanValue()) {
                                date = Long.valueOf(cursor.getLong(cursor.getColumnIndex("normalized_date")));
                            }
                            try {
                                addStorybookPart(document, i, date, previousDate, senderString, previousSenderString, receiverString, contentString, contentImage, isVideo);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            previousDate = date;
                            previousSenderString = senderString;
                            hasNext = Boolean.valueOf(mmsCursor.moveToNext());
                        }
                    }
                    mmsCursor.close();
                } else {
                    Cursor smsCursor = this.mContext.getContentResolver().query(Uri.parse("content://sms"), new String[]{HtmlTags.BODY, LicenseKey.LICENSE_DATE}, "_id = " + messageId, null, null);
                    if (smsCursor.moveToFirst()) {
                        contentString = smsCursor.getString(smsCursor.getColumnIndex(HtmlTags.BODY));
                    }
                    smsCursor.close();
                    date = null;
                    if (this.settings.useTimestamps.booleanValue()) {
                        date = Long.valueOf(cursor.getLong(cursor.getColumnIndex("normalized_date")));
                    }
                    try {
                        addStorybookPart(document, i, date, previousDate, senderString, previousSenderString, receiverString, contentString, null, Boolean.valueOf(false));
                    } catch (Exception e2) {
                        e2.printStackTrace();
                    }
                    previousDate = date;
                    previousSenderString = senderString;
                }
            }
            i++;
        }
        return 0;
    }

    public void addStorybookPart(Document document, int i, Long date, Long previousDate, String senderString, String previousSenderString, String receiverString, String contentString, Bitmap contentBitmap, Boolean isVideo) throws DocumentException, MalformedURLException, IOException {
        Paragraph paragraph = new Paragraph();
        if (i == 0) {
            paragraph.add(new Chunk("T", this.serifFont24));
            paragraph.add(new Chunk("his story begins", this.serifFont11));
            if (!this.settings.useTimestamps.booleanValue()) {
                paragraph.add(new Chunk(MinimalPrettyPrinter.DEFAULT_ROOT_VALUE_SEPARATOR, this.serifFont11));
            }
        } else if (!senderString.equals(previousSenderString)) {
            document.add(Chunk.NEWLINE);
        }
        Boolean dateChanged = Boolean.valueOf(false);
        if (this.settings.useTimestamps.booleanValue()) {
            SimpleDateFormat dateFormat;
            if (i == 0) {
                dateFormat = new SimpleDateFormat("MMMM dd, yyyy", Locale.US);
                dateFormat.setTimeZone(TimeZone.getDefault());
                paragraph.add(new Chunk(" on " + dateFormat.format(new Date(date.longValue())) + MinimalPrettyPrinter.DEFAULT_ROOT_VALUE_SEPARATOR, this.serifFont11));
            } else {
                dateFormat = new SimpleDateFormat("MMMM dd, yyyy", Locale.US);
                dateFormat.setTimeZone(TimeZone.getDefault());
                String dateString = dateFormat.format(new Date(date.longValue()));
                String previousDateString = PdfObject.NOTHING;
                if (previousDate != null) {
                    previousDateString = dateFormat.format(new Date(previousDate.longValue()));
                }
                if (!previousDateString.equals(dateString)) {
                    dateChanged = Boolean.valueOf(true);
                    document.add(Chunk.NEWLINE);
                    paragraph.add(new Chunk("O", this.serifFont24));
                    paragraph.add(new Chunk("n " + dateString + MinimalPrettyPrinter.DEFAULT_ROOT_VALUE_SEPARATOR, this.serifFont11));
                }
            }
        }
        if (i == 0) {
            paragraph.add(new Chunk("when " + senderString + " texted " + receiverString + ", ", this.serifFont11));
        }
        if (contentString.length() > 0) {
            Boolean addPhrase = Boolean.valueOf(false);
            String phrase = null;
            if (dateChanged.booleanValue()) {
                paragraph.add(new Chunk(new StringBuilder(String.valueOf(senderString)).append(" texted ").append(receiverString).append(", ").toString(), this.serifFont11));
            } else if (!(i == 0 || dateChanged.booleanValue() || senderString.equals(previousSenderString))) {
                Boolean isOpening = Boolean.valueOf(this.wasLastPhraseOpening.booleanValue() ? this.generator.nextBoolean() : true);
                int phraseIdx = this.lastPhraseIdx;
                if (isOpening.booleanValue() && !this.wasLastPhraseOpening.booleanValue()) {
                    this.lastPhraseIdx = -1;
                }
                if (isOpening.booleanValue()) {
                    do {
                        phraseIdx = this.generator.nextInt(this.openings.length);
                    } while (phraseIdx == this.lastPhraseIdx);
                    phrase = this.openings[phraseIdx];
                } else {
                    do {
                        phraseIdx = this.generator.nextInt(this.closings.length);
                    } while (phraseIdx == this.lastPhraseIdx);
                    phrase = this.closings[phraseIdx];
                }
                this.lastPhraseIdx = phraseIdx;
                this.wasLastPhraseOpening = isOpening;
                phrase = phrase.replace("[speaker]", senderString);
                addPhrase = Boolean.valueOf(true);
            }
            if (addPhrase.booleanValue() && this.wasLastPhraseOpening.booleanValue()) {
                paragraph.add(new Chunk(phrase, this.serifFont11));
            }
            paragraph.add(new Chunk("\"" + contentString.trim() + "\"", this.serifFont11));
            if (addPhrase.booleanValue() && !this.wasLastPhraseOpening.booleanValue()) {
                paragraph.add(new Chunk(phrase, this.serifFont11));
            }
            document.add(paragraph);
        } else if (contentBitmap != null) {
            if (i != 0) {
                paragraph.add(new Chunk(new StringBuilder(String.valueOf(senderString)).append(" sent ").append(receiverString).append(":").toString(), this.serifFont11));
                document.add(paragraph);
                document.add(Chunk.NEWLINE);
            }
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            if (isVideo.booleanValue()) {
                contentBitmap.compress(CompressFormat.PNG, 50, stream);
            } else {
                contentBitmap.compress(CompressFormat.JPEG, 50, stream);
            }
            Image contentImage = Image.getInstance(stream.toByteArray());
            contentImage.scaleToFit(445.0f, 195.0f);
            contentImage.setAlignment(1);
            if (isVideo.booleanValue()) {
                contentImage.setCompressionLevel(this.settings.compressionLevel);
            }
            document.add(contentImage);
            document.add(Chunk.NEWLINE);
        }
    }

    public void zipPdf() {
        try {
            File srcFile = new File(Environment.getExternalStorageDirectory(), this.filename);
            if (srcFile.exists() && srcFile.canRead()) {
                String zipFilename = this.filename.replace(".pdf", ".zip");
                byte[] buffer = new byte[PdfAction.SUBMIT_EXCL_NON_USER_ANNOTS];
                ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(new File(Environment.getExternalStorageDirectory(), zipFilename)));
                FileInputStream fis = new FileInputStream(srcFile);
                zos.putNextEntry(new ZipEntry(srcFile.getName()));
                while (true) {
                    int length = fis.read(buffer);
                    if (length <= 0) {
                        zos.closeEntry();
                        fis.close();
                        zos.close();
                        this.filename = zipFilename;
                        return;
                    }
                    zos.write(buffer, 0, length);
                }
            }
        } catch (IOException ioe) {
            System.out.println("Error creating zip file" + ioe);
        }
    }

    public Bitmap wrapVideoThumbnailWithFilm(Context context, Bitmap bitmap) {
        Bitmap filmLeft = ImageUtilities.getBitmapForFile(context, "pdf/film_strip_left_300.png");
        Bitmap filmRight = ImageUtilities.getBitmapForFile(context, "pdf/film_strip_right_300.png");
        int filmLeftWidth = filmLeft.getWidth();
        int filmRightWidth = filmRight.getWidth();
        int height = filmLeft.getHeight();
        int mainImageAdjWidth = (int) ((((float) bitmap.getWidth()) * ((float) height)) / ((float) bitmap.getHeight()));
        Bitmap combined = Bitmap.createBitmap((filmLeftWidth + mainImageAdjWidth) + filmRightWidth, height, Config.ARGB_8888);
        Canvas comboImage = new Canvas(combined);
        comboImage.drawBitmap(filmLeft, 0.0f, 0.0f, null);
        comboImage.drawBitmap(bitmap, null, new Rect(filmLeftWidth, 0, filmLeftWidth + mainImageAdjWidth, height), null);
        comboImage.drawBitmap(filmRight, (float) (filmLeftWidth + mainImageAdjWidth), 0.0f, null);
        return combined;
    }
}
