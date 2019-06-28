package com.egitimyazilim.iletisim.mesajlarvelibilgilendirme.messagerecieve;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.telephony.SmsMessage;
import android.widget.Toast;

import com.egitimyazilim.iletisim.mesajlarvelibilgilendirme.Chats;
import com.egitimyazilim.iletisim.mesajlarvelibilgilendirme.Ogrenci;
import com.egitimyazilim.iletisim.mesajlarvelibilgilendirme.Veritabani;
import com.egitimyazilim.iletisim.mesajlarvelibilgilendirme.contentprovider.MessagesContentProviderHandler;
import com.egitimyazilim.iletisim.mesajlarvelibilgilendirme.utils.NotificationUtil;

import java.security.Permission;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wgwj4809 on 01/08/17.
 */

public class SmsBroadcastReceiver extends BroadcastReceiver {
    public static final String SMS_BUNDLE = "pdus";

    public void onReceive(Context context, Intent intent) {

        Bundle intentExtras = intent.getExtras();



        if (intentExtras != null) {

            Object[] sms = (Object[]) intentExtras.get(SMS_BUNDLE);
            String smsMessageStr = "";
            String phone_no = "";
            long timeMillis=0;
            for (int i = 0; i < sms.length; i++) {

                SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) sms[i]);

                String smsBody = smsMessage.getMessageBody();
                String address = smsMessage.getOriginatingAddress();
                timeMillis = smsMessage.getTimestampMillis();

                smsMessageStr += smsBody + "\n";
                phone_no += address;
            }

            Intent broadcastIntent = new Intent();
            broadcastIntent.setAction("SMS_RECEIVED_ACTION");
            broadcastIntent.putExtra("new_sms", smsMessageStr);
            broadcastIntent.putExtra("mob_no", phone_no);
            broadcastIntent.putExtra("date" ,timeMillis);

            MessagesContentProviderHandler.addReceivedMessageToContentProvider(context, smsMessageStr, phone_no, timeMillis);

            if(ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS)== PackageManager.PERMISSION_GRANTED){
                List<String> rehberdekiTelNolar=new ArrayList<>();
                rehberdekiTelNolar=getRawContactsPhoneNumberList(context);
                List<String> rehberdekiKisiIsimleri=new ArrayList<>();
                rehberdekiKisiIsimleri=getRawContactsDisplayNameList(context);
                for(int i=0;i<rehberdekiTelNolar.size();i++){
                    String rehberdekiTelNo=rehberdekiTelNolar.get(i);
                    if(rehberdekiTelNolar.get(i).length()>10){
                        rehberdekiTelNo=rehberdekiTelNolar.get(i).substring(rehberdekiTelNolar.get(i).length()-10,rehberdekiTelNolar.get(i).length());
                    }
                    String chatsTelNo=phone_no;
                    if(chatsTelNo.length()>10){
                        chatsTelNo=chatsTelNo.substring(chatsTelNo.length()-10,chatsTelNo.length());
                    }
                    if (rehberdekiTelNo.equals(chatsTelNo)){
                        phone_no=rehberdekiKisiIsimleri.get(i);
                    }
                }

                rehberdekiKisiIsimleri.clear();
                rehberdekiTelNolar.clear();
            }

            Veritabani vt=new Veritabani(context);
            List<Ogrenci> ogrenciList=new ArrayList<>();
            ogrenciList=vt.getirOgrenci();
            vt.close();
            for(Ogrenci ogrenci:ogrenciList){
                String ogrTelNo=ogrenci.getTelno();
                if(ogrenci.getTelno().length()>10){
                    ogrTelNo=ogrenci.getTelno().substring(ogrenci.getTelno().length()-10,ogrenci.getTelno().length());
                }

                    String chatsTelNo=phone_no;
                    if(chatsTelNo.length()>10){
                        chatsTelNo=chatsTelNo.substring(chatsTelNo.length()-10,chatsTelNo.length());
                    }
                    if (ogrTelNo.equals(chatsTelNo) &&!ogrenci.getTelno().equals("0")){
                        phone_no=ogrenci.getSinif()+" "+ogrenci.getAdSoyad()+" "+"Velisi:"+ogrenci.getVeliAdi();
                    }
            }
            ogrenciList.clear();

            NotificationUtil.showNotification(context, smsMessageStr,phone_no);
        }
    }

    private List<String> getRawContactsPhoneNumberList(Context context) {
        List<String> ret = new ArrayList<>();
        Uri readContactsUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        Cursor cursor = context.getContentResolver().query(readContactsUri, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                int idColumnIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                String rawContactsId = cursor.getString(idColumnIndex);
                ret.add(new String(rawContactsId));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return ret;
    }

    private List<String> getRawContactsDisplayNameList(Context context) {
        List<String> ret = new ArrayList<>();
        Uri readContactsUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        Cursor cursor = context.getContentResolver().query(readContactsUri, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                int idColumnIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                String rawContactsId = cursor.getString(idColumnIndex);
                ret.add(new String(rawContactsId));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return ret;
    }
}
