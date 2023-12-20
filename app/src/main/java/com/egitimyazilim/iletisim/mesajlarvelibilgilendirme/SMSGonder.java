package com.egitimyazilim.iletisim.mesajlarvelibilgilendirme;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Telephony;
import android.telephony.SmsManager;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

import com.egitimyazilim.iletisim.mesajlarvelibilgilendirme.contentprovider.MessagesContentProviderHandler;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class SMSGonder {

    public static final String SMS_SENT_ACTION = "com.andriodgifts.gift.SMS_SENT_ACTION";
    public static final String SMS_DELIVERED_ACTION = "com.andriodgifts.gift.SMS_DELIVERED_ACTION";

    public static void gonder(Context context, SmsManager smsManager, String telno, String mesaj, String ogrenciAdSoyad) {
        int flag = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            flag = PendingIntent.FLAG_IMMUTABLE;
        }

        ArrayList<PendingIntent> sentIntents = new ArrayList<>();
        PendingIntent sentIntent = PendingIntent.getBroadcast(context
                , 0, new Intent(SMS_SENT_ACTION), flag);

        ArrayList<PendingIntent> deliveryIntents = new ArrayList<>();
        PendingIntent deliveryIntent = PendingIntent.getBroadcast(context
                , 0, new Intent(SMS_DELIVERED_ACTION), flag);

        ArrayList<String> parts = smsManager.divideMessage(mesaj);
        for (int i = 0; i < parts.size(); i++) {
            sentIntents.add(sentIntent);
            deliveryIntents.add(deliveryIntent);
        }
        smsManager.sendMultipartTextMessage(telno, null, parts, sentIntents, deliveryIntents);
        if (ogrenciAdSoyad != null) {
            Toast.makeText(context, ogrenciAdSoyad + " gönderiliyor...", Toast.LENGTH_SHORT).show();
        }

        if (String.valueOf(Telephony.Sms.getDefaultSmsPackage(context)).equals(context.getPackageName())) {
            Calendar c = Calendar.getInstance();
            long time = c.getTimeInMillis();
            MessagesContentProviderHandler.addSentMessageToContentProvider(getApplicationContext()
                    , mesaj, telno, time);
            if (ogrenciAdSoyad != null) {
                Toast.makeText(context, ogrenciAdSoyad + " gönderiliyor...", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static boolean isDualSimAvailable(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            SubscriptionManager sManager = (SubscriptionManager) context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);
            @SuppressLint("MissingPermission") SubscriptionInfo infoSim1 = sManager.getActiveSubscriptionInfoForSimSlotIndex(0);
            @SuppressLint("MissingPermission") SubscriptionInfo infoSim2 = sManager.getActiveSubscriptionInfoForSimSlotIndex(1);
            if(infoSim1 != null && infoSim2 != null) {
                return true;
            }
        }
        return false;
    }

    public static void getDefaultSMSManeger(Context context, SmsManager[] smsManager) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
        builder.setCancelable(false);
        builder.setTitle("Hat seçiniz!");
        builder.setPositiveButton("2.Hat", new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
            @Override
            public void onClick(DialogInterface dialogInterface, int ii) {
                smsManager[0] = SmsManager.getSmsManagerForSubscriptionId(1);
                dialogInterface.dismiss();
            }
        });
        builder.setNeutralButton("1.hat", new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
            @Override
            public void onClick(DialogInterface dialogInterface, int ii) {
                smsManager[0] = SmsManager.getSmsManagerForSubscriptionId(0);
                dialogInterface.dismiss();
            }
        });
        android.app.AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
