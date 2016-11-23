package com.ideationdesignservices.txtbook.util;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract.PhoneLookup;
import android.provider.ContactsContract.Profile;

public class ContactUtilities {
    public static String findNameByAddress(Context context, String phoneNumber) {
        Cursor cursor = context.getContentResolver().query(Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber)), new String[]{"display_name"}, null, null, null);
        if (!cursor.moveToFirst()) {
            return phoneNumber;
        }
        String name = cursor.getString(cursor.getColumnIndex("display_name"));
        cursor.close();
        return name;
    }

    public static String getPhoneOwnerName(Context context) {
        String myName = "I";
        Cursor c = context.getContentResolver().query(Profile.CONTENT_URI, null, null, null, null);
        int count = c.getCount();
        if (c.moveToFirst()) {
            int position = c.getPosition();
            if (count == 1 && position == 0) {
                myName = c.getString(c.getColumnIndex("display_name"));
            }
        }
        c.close();
        return myName;
    }
}
