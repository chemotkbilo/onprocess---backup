package com.ideationdesignservices.txtbook.pdf;

import android.graphics.Bitmap;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;

public class TxtBookPdfSettings {
    public static final String BOOK_STYLE = "BOOK_STYLE";
    public static final String CHOSEN_THREAD_ID = "CHOSEN_THREAD_ID";
    public static final String END_DATE_LIMIT_MILLIS = "END_DATE_LIMIT_MILLIS";
    public static final String MY_NAME = "MY_NAME";
    public static final String SELECTED_ROWS_LIST = "SELECTED_ROWS";
    public static final String START_DATE_LIMIT_MILLIS = "START_DATE_LIMIT_MILLIS";
    public static final String THEIR_NAME = "THEIR_NAME";
    public static final String USE_PHOTOS = "USE_PHOTOS";
    public static final String USE_TIMESTAMPS = "USE_TIMESTAMPS";
    public Boolean addBackCover;
    public Boolean addFrontCover;
    public Boolean addFrontCoverImage;
    public String backCoverNote;
    public String bookCoverTitle;
    public int bookStyle;
    public int compressionLevel;
    public Bitmap coverPhoto;
    public Long endDateMillis;
    public String myName;
    public String selectedRowsList;
    public Long startDateMillis;
    public String theirName;
    public Long threadId;
    public Boolean useTimestamps;

    public ArrayList<Integer> getSelectedRows() {
        ArrayList<Integer> selectedRows = new ArrayList();
        try {
            JSONArray jsonArray = new JSONArray(this.selectedRowsList);
            if (jsonArray != null) {
                int len = jsonArray.length();
                for (int i = 0; i < len; i++) {
                    selectedRows.add(Integer.valueOf(Integer.parseInt(jsonArray.get(i).toString())));
                }
            }
        } catch (JSONException e) {
        }
        return selectedRows;
    }
}
