package com.ideationdesignservices.txtbook.sms;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import com.ideationdesignservices.txtbook.ConversationActivity;
import com.ideationdesignservices.txtbook.R;
import com.ideationdesignservices.txtbook.util.MMSUtilities;
import com.itextpdf.license.LicenseKey;
import com.itextpdf.text.html.HtmlTags;
import com.itextpdf.text.pdf.BaseField;
import com.itextpdf.text.pdf.parser.Vector;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class MessageListAdapter extends SimpleCursorAdapter {
    private int[] layouts;
    private Context mContext;
    public String myName;
    public String theirName;
    public Boolean usePhotos;
    public Boolean useTimestamps;

    public MessageListAdapter(Context context, int[] layouts, Cursor c, String[] from, int[] to, int flags) {
        super(context, layouts[0], c, from, to, flags);
        this.mContext = context;
        this.layouts = layouts;
    }

    public int getItemViewType(int position) {
        Cursor cursor = (Cursor) getItem(position);
        return MMSUtilities.getMessageSenderType(this.mContext, Long.valueOf(cursor.getLong(cursor.getColumnIndex("_id"))), cursor.getString(cursor.getColumnIndex("ct_t")));
    }

    public int getViewTypeCount() {
        return 2;
    }

    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        int type = getItemViewType(cursor.getPosition());
        return buildView(inflater.inflate(this.layouts[type], parent, false), context, cursor, type);
    }

    public void bindView(View view, Context context, Cursor cursor) {
        buildView(view, context, cursor, -1);
    }

    public View buildView(View view, Context context, Cursor cursor, int type) {
        int position = cursor.getPosition();
        TextView userNameTextView = (TextView) view.findViewById(R.id.userNameTextView);
        LinearLayout userMessageContent = (LinearLayout) view.findViewById(R.id.userMessageContent);
        userMessageContent.removeAllViews();
        if (type == -1) {
            type = getItemViewType(position);
        }
        switch (type) {
            case Vector.I1 /*0*/:
                userNameTextView.setText(this.myName);
                userMessageContent.setGravity(3);
                view.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.conversation_cell_bg_dark));
                break;
            case Vector.I2 /*1*/:
                userNameTextView.setText(this.theirName);
                userMessageContent.setGravity(5);
                view.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.conversation_cell_bg_light));
                break;
        }
        Long date = Long.valueOf(0);
        LayoutParams layoutParams = new LinearLayout.LayoutParams(-2, 150, 0.0f);
        layoutParams.setMargins(0, 0, 0, 10);
        layoutParams = new LinearLayout.LayoutParams(-2, -2, BaseField.BORDER_WIDTH_THIN);
        layoutParams.setMargins(0, 0, 0, 10);
        layoutParams = new LinearLayout.LayoutParams(26, 150, 0.0f);
        layoutParams.setMargins(0, 0, 0, 10);
        String string = cursor.getString(cursor.getColumnIndex("ct_t"));
        Bitmap mmsImage = null;
        Long messageId = Long.valueOf(cursor.getLong(cursor.getColumnIndex("_id")));
        if (this.useTimestamps.booleanValue()) {
            date = Long.valueOf(cursor.getLong(cursor.getColumnIndex("normalized_date")));
        }
        View textView;
        if ("application/vnd.wap.multipart.related".equals(string)) {
            String selectionPart = "mid=" + messageId;
            Cursor mmsCursor = context.getContentResolver().query(Uri.parse("content://mms/part"), null, selectionPart, null, null);
            for (Boolean hasNext = Boolean.valueOf(mmsCursor.moveToFirst()); hasNext.booleanValue(); hasNext = Boolean.valueOf(mmsCursor.moveToNext())) {
                try {
                    String partId = mmsCursor.getString(mmsCursor.getColumnIndex("_id"));
                    String mimetype = mmsCursor.getString(mmsCursor.getColumnIndex("ct"));
                    if (!"application/smil".equals(mimetype)) {
                        if (mimetype.startsWith("image/")) {
                            mmsImage = MMSUtilities.getMmsImage(this.mContext, partId);
                            ImageView imageView = new ImageView(context);
                            imageView.setImageBitmap(mmsImage);
                            imageView.setVisibility(0);
                            imageView.setLayoutParams(layoutParams);
                            imageView.setAdjustViewBounds(true);
                            imageView.setContentDescription(context.getString(R.string.user_message_image));
                            userMessageContent.addView(imageView);
                            if (mmsImage == null || this.usePhotos.booleanValue()) {
                                imageView.setAlpha(BaseField.BORDER_WIDTH_THIN);
                            } else {
                                imageView.setAlpha(0.5f);
                            }
                        } else {
                            if (mimetype.startsWith("text/")) {
                                String body;
                                if (mmsCursor.getString(mmsCursor.getColumnIndex("_data")) != null) {
                                    body = MMSUtilities.getMmsText(context, partId);
                                } else {
                                    body = mmsCursor.getString(mmsCursor.getColumnIndex("text"));
                                }
                                textView = new TextView(context);
                                textView.setLayoutParams(layoutParams);
                                textView.setTextSize(14.0f);
                                textView.setText(body);
                                textView.setGravity(119);
                                userMessageContent.addView(textView);
                            } else {
                                if (mimetype.startsWith("video/")) {
                                    mmsImage = MMSUtilities.getMmsVideoThumbnail(context, partId);
                                    textView = new LinearLayout(context);
                                    textView.setOrientation(0);
                                    textView.setLayoutParams(new RelativeLayout.LayoutParams(-2, -2));
                                    textView = new ImageView(context);
                                    textView.setImageDrawable(context.getResources().getDrawable(R.drawable.film_strip_left));
                                    textView.setLayoutParams(layoutParams);
                                    textView.setContentDescription(context.getString(R.string.user_message_image));
                                    textView.addView(textView);
                                    textView = new ImageView(context);
                                    textView.setImageBitmap(mmsImage);
                                    textView.setVisibility(0);
                                    textView.setLayoutParams(layoutParams);
                                    textView.setAdjustViewBounds(true);
                                    textView.setContentDescription(context.getString(R.string.user_message_image));
                                    textView.addView(textView);
                                    textView = new ImageView(context);
                                    textView.setImageDrawable(context.getResources().getDrawable(R.drawable.film_strip_right));
                                    textView.setLayoutParams(layoutParams);
                                    textView.setContentDescription(context.getString(R.string.user_message_image));
                                    textView.addView(textView);
                                    if (mmsImage == null || this.usePhotos.booleanValue()) {
                                        textView.setAlpha(BaseField.BORDER_WIDTH_THIN);
                                    } else {
                                        textView.setAlpha(0.5f);
                                    }
                                    userMessageContent.addView(textView);
                                } else {
                                    if (mimetype.startsWith("audio/")) {
                                        textView = new TextView(context);
                                        textView.setLayoutParams(layoutParams);
                                        textView.setTextSize(14.0f);
                                        textView.setText("[audio message]");
                                        textView.setGravity(119);
                                        userMessageContent.addView(textView);
                                    } else {
                                        textView = new TextView(context);
                                        textView.setLayoutParams(layoutParams);
                                        textView.setTextSize(14.0f);
                                        textView.setText("[unknown message type]");
                                        textView.setGravity(119);
                                        userMessageContent.addView(textView);
                                    }
                                }
                            }
                        }
                    }
                } catch (OutOfMemoryError e) {
                }
            }
            mmsCursor.close();
        } else {
            Cursor smsCursor = this.mContext.getContentResolver().query(Uri.parse("content://sms"), new String[]{HtmlTags.BODY, LicenseKey.LICENSE_DATE}, "_id = " + messageId, null, null);
            if (smsCursor.moveToFirst()) {
                textView = new TextView(context);
                textView.setLayoutParams(layoutParams);
                textView.setTextSize(14.0f);
                textView.setText(smsCursor.getString(smsCursor.getColumnIndex(HtmlTags.BODY)));
                textView.setGravity(119);
                userMessageContent.addView(textView);
            }
            smsCursor.close();
        }
        TextView userDateTextView = (TextView) view.findViewById(R.id.userDateTextView);
        if (this.useTimestamps.booleanValue()) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM dd, yyyy h:mm a", Locale.US);
            simpleDateFormat.setTimeZone(TimeZone.getDefault());
            userDateTextView.setText(simpleDateFormat.format(new Date(date.longValue())));
        }
        CheckBox checkbox = (CheckBox) view.findViewById(R.id.userCheckBox);
        if (mmsImage == null || this.usePhotos.booleanValue()) {
            checkbox.setChecked(((ConversationActivity) this.mContext).selectedRows.contains(Integer.valueOf(position)));
            checkbox.setEnabled(true);
            userNameTextView.setTextColor(-16777216);
            userDateTextView.setTextColor(Color.argb(100, 119, 119, 119));
        } else {
            checkbox.setChecked(false);
            checkbox.setEnabled(false);
            userNameTextView.setTextColor(Color.argb(50, 0, 0, 0));
            userDateTextView.setTextColor(Color.argb(50, 119, 119, 119));
        }
        checkbox.setTag(Integer.valueOf(position));
        checkbox.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                Integer tagPosition = (Integer) view.getTag();
                if (((CheckBox) view).isChecked()) {
                    ((ConversationActivity) MessageListAdapter.this.mContext).selectedRows.add(tagPosition);
                } else {
                    ((ConversationActivity) MessageListAdapter.this.mContext).selectedRows.remove(tagPosition);
                }
                ((ConversationActivity) MessageListAdapter.this.mContext).updateSelectOrClearAllButton();
            }
        });
        return view;
    }

    public boolean rowHasImage(int position) {
        Cursor cursor = (Cursor) getItem(position);
        Long messageId = Long.valueOf(cursor.getLong(cursor.getColumnIndex("_id")));
        if ("application/vnd.wap.multipart.related".equals(cursor.getString(cursor.getColumnIndex("ct_t")))) {
            String selectionPart = "mid=" + messageId;
            Cursor mmsCursor = this.mContext.getContentResolver().query(Uri.parse("content://mms/part"), null, selectionPart, null, null);
            for (Boolean hasNext = Boolean.valueOf(mmsCursor.moveToFirst()); hasNext.booleanValue(); hasNext = Boolean.valueOf(mmsCursor.moveToNext())) {
                try {
                    String mimetype = mmsCursor.getString(mmsCursor.getColumnIndex("ct"));
                    if ("application/smil".equals(mimetype)) {
                        continue;
                    } else if (mimetype.startsWith("image/")) {
                        return true;
                    } else {
                        if (mimetype.startsWith("video/")) {
                            return true;
                        }
                    }
                } catch (OutOfMemoryError e) {
                }
            }
            mmsCursor.close();
        }
        return false;
    }
}
