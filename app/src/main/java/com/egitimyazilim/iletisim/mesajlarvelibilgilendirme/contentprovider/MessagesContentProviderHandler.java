package com.egitimyazilim.iletisim.mesajlarvelibilgilendirme.contentprovider;

import android.content.ContentValues;
import android.content.Context;
import android.provider.Telephony;

public class MessagesContentProviderHandler {

    public static void addReceivedMessageToContentProvider(Context context, String textMsg , String phone,long time) {

        ContentValues values = new ContentValues();
        values.put(Telephony.Sms.ADDRESS, phone );
        values.put(Telephony.Sms.BODY, textMsg);
        values.put(Telephony.Sms.DATE, time);
        context.getApplicationContext().getContentResolver().insert(Telephony.Sms.Inbox.CONTENT_URI, values);
    }

    public static void addSentMessageToContentProvider(Context context, String textMsg , String phone,Long date) {
        ContentValues values = new ContentValues();
        values.put(Telephony.Sms.ADDRESS, phone);
        values.put(Telephony.Sms.BODY, textMsg);
        values.put(Telephony.Sms.DATE, date);
        context.getApplicationContext().getContentResolver().insert(Telephony.Sms.Sent.CONTENT_URI, values);
    }
}
