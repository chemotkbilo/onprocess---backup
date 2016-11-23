package com.ideationdesignservices.txtbook.util;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import com.itextpdf.license.LicenseKey;
import com.itextpdf.text.xml.xmp.XmpWriter;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MMSUtilities {
    public static final int MESSAGE_BOX_DRAFTS = 3;
    public static final int MESSAGE_BOX_INBOX = 1;
    public static final int MESSAGE_BOX_OUTBOX = 4;
    public static final int MESSAGE_BOX_SENT = 2;
    public static final int TYPE_MAX_COUNT = 2;
    public static final int TYPE_MINE = 0;
    public static final int TYPE_THEIRS = 1;
    public static final int TYPE_UNKNOWN = -1;

    public static String getMmsText(Context context, String id) {
        Uri partURI = Uri.parse("content://mms/part/" + id);
        InputStream is = null;
        StringBuilder sb = new StringBuilder();
        try {
            is = context.getContentResolver().openInputStream(partURI);
            if (is != null) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, XmpWriter.UTF8));
                for (String temp = reader.readLine(); temp != null; temp = reader.readLine()) {
                    sb.append(temp);
                }
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                }
            }
        } catch (IOException e2) {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e3) {
                }
            }
        } catch (Throwable th) {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e4) {
                }
            }
        }
        return sb.toString();
    }

    public static Bitmap getMmsImage(Context context, String _id) {
        InputStream is = null;
        Bitmap bitmap = null;
        try {
            is = context.getContentResolver().openInputStream(Uri.parse("content://mms/part/" + _id));
            bitmap = BitmapFactory.decodeStream(is);
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                }
            }
        } catch (IOException e2) {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e3) {
                }
            }
        } catch (Throwable th) {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e4) {
                }
            }
        }
        return bitmap;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static android.graphics.Bitmap getMmsVideoThumbnail(android.content.Context r6, java.lang.String r7) {
        /*
        r3 = new java.lang.StringBuilder;
        r4 = "content://mms/part/";
        r3.<init>(r4);
        r3 = r3.append(r7);
        r3 = r3.toString();
        r2 = android.net.Uri.parse(r3);
        r0 = 0;
        r1 = new android.media.MediaMetadataRetriever;
        r1.<init>();
        r1.setDataSource(r6, r2);	 Catch:{ RuntimeException -> 0x0026, all -> 0x002d }
        r4 = -1;
        r0 = r1.getFrameAtTime(r4);	 Catch:{ RuntimeException -> 0x0026, all -> 0x002d }
        r1.release();	 Catch:{ RuntimeException -> 0x0034 }
    L_0x0025:
        return r0;
    L_0x0026:
        r3 = move-exception;
        r1.release();	 Catch:{ RuntimeException -> 0x002b }
        goto L_0x0025;
    L_0x002b:
        r3 = move-exception;
        goto L_0x0025;
    L_0x002d:
        r3 = move-exception;
        r1.release();	 Catch:{ RuntimeException -> 0x0032 }
    L_0x0031:
        throw r3;
    L_0x0032:
        r4 = move-exception;
        goto L_0x0031;
    L_0x0034:
        r3 = move-exception;
        goto L_0x0025;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.ideationdesignservices.txtbook.util.MMSUtilities.getMmsVideoThumbnail(android.content.Context, java.lang.String):android.graphics.Bitmap");
    }

    public static int getMessageSenderType(Context context, Long messageId, String mimetype) {
        Boolean isSent = Boolean.valueOf(false);
        String[] projection;
        if ("application/vnd.wap.multipart.related".equals(mimetype)) {
            Uri mmsUri = Uri.parse("content://mms");
            projection = new String[TYPE_THEIRS];
            projection[TYPE_MINE] = "msg_box";
            Cursor mmsCursor = context.getContentResolver().query(mmsUri, projection, "_id = " + messageId, null, null);
            if (mmsCursor.moveToFirst()) {
                isSent = Boolean.valueOf(TYPE_THEIRS != mmsCursor.getInt(mmsCursor.getColumnIndex("msg_box")));
            }
            mmsCursor.close();
        } else {
            Uri smsUri = Uri.parse("content://sms");
            projection = new String[TYPE_THEIRS];
            projection[TYPE_MINE] = LicenseKey.PRODUCT_TYPE;
            Cursor smsCursor = context.getContentResolver().query(smsUri, projection, "_id = " + messageId, null, null);
            if (smsCursor.moveToFirst()) {
                isSent = Boolean.valueOf(TYPE_MAX_COUNT == smsCursor.getInt(smsCursor.getColumnIndex(LicenseKey.PRODUCT_TYPE)));
            }
            smsCursor.close();
        }
        if (isSent.booleanValue()) {
            return TYPE_MINE;
        }
        return TYPE_THEIRS;
    }
}
