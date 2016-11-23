package com.ideationdesignservices.txtbook.util;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.net.Uri;
import com.itextpdf.text.pdf.BaseField;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ImageUtilities {
    public static Bitmap getBitmapForFile(Context context, String filename) {
        try {
            return BitmapFactory.decodeStream(context.getAssets().open(filename));
        } catch (IOException e) {
            return null;
        }
    }

    public static byte[] getImageDataForFile(Context context, String filename) {
        Bitmap bitmap = getBitmapForFile(context, filename);
        if (bitmap == null) {
            return null;
        }
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(CompressFormat.PNG, 50, stream);
        return stream.toByteArray();
    }

    public static Bitmap scaleCenterCrop(Bitmap source, int newWidth, int newHeight) {
        int sourceWidth = source.getWidth();
        int sourceHeight = source.getHeight();
        float scale = Math.max(((float) newWidth) / ((float) sourceWidth), ((float) newHeight) / ((float) sourceHeight));
        float scaledWidth = scale * ((float) sourceWidth);
        float scaledHeight = scale * ((float) sourceHeight);
        float left = (((float) newWidth) - scaledWidth) / BaseField.BORDER_WIDTH_MEDIUM;
        float top = (((float) newHeight) - scaledHeight) / BaseField.BORDER_WIDTH_MEDIUM;
        RectF targetRect = new RectF(left, top, left + scaledWidth, top + scaledHeight);
        Bitmap dest = Bitmap.createBitmap(newWidth, newHeight, source.getConfig());
        new Canvas(dest).drawBitmap(source, null, targetRect, null);
        return dest;
    }

    public static Bitmap getCorrectlyOrientedImage(Context context, Uri photoUri) throws IOException {
        InputStream is = context.getContentResolver().openInputStream(photoUri);
        Options dbo = new Options();
        dbo.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(is, null, dbo);
        is.close();
        int orientation = getOrientation(context, photoUri);
        is = context.getContentResolver().openInputStream(photoUri);
        Bitmap srcBitmap = BitmapFactory.decodeStream(is);
        is.close();
        if (orientation <= 0) {
            return srcBitmap;
        }
        Matrix matrix = new Matrix();
        matrix.postRotate((float) orientation);
        return Bitmap.createBitmap(srcBitmap, 0, 0, srcBitmap.getWidth(), srcBitmap.getHeight(), matrix, true);
    }

    public static int getOrientation(Context context, Uri photoUri) {
        Cursor cursor = context.getContentResolver().query(photoUri, new String[]{"orientation"}, null, null, null);
        if (cursor.getCount() != 1) {
            return -1;
        }
        cursor.moveToFirst();
        return cursor.getInt(0);
    }
}
