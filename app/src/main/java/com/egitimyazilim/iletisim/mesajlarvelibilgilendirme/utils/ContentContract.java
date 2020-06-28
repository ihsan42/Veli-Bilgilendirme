package com.egitimyazilim.iletisim.mesajlarvelibilgilendirme.utils;

import android.net.Uri;

public class ContentContract {

    public static final Uri ALL_SMS_URI = Uri.parse("content://sms");
    public static final Uri SENT_SMS = Uri.parse("content://sms/sent");
    public static final String SMS_SELECTION = "address = ? ";
    public static final String SMS_SELECTION_ID = "_id = ? ";
    public static final String COLUMN_ID = "_id";
    public static final String SMS_SELECTION_SEARCH = "address LIKE ? OR body LIKE ?";
    public static final String SORT_DESC = "date DESC";
    public static final String SORT_ASC = "date ASC";
}