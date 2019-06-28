package com.egitimyazilim.iletisim.mesajlarvelibilgilendirme.utils;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.widget.QuickContactBadge;

import com.egitimyazilim.iletisim.mesajlarvelibilgilendirme.MainActivity;
import com.egitimyazilim.iletisim.mesajlarvelibilgilendirme.MesajKutusu;
import com.egitimyazilim.iletisim.mesajlarvelibilgilendirme.R;


/**
 * Created by wgwj4809 on 02/08/17.
 */

public class NotificationUtil {
    public static void showNotification (Context aoContext, String text,String title){

        Intent aoIntent = new Intent(aoContext, MesajKutusu.class);
        PendingIntent _contentIntent = PendingIntent.getActivity(aoContext, 0, aoIntent, 0);
        NotificationManager _notificationManager = (NotificationManager) aoContext.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder _notifBuilder = new NotificationCompat.Builder(aoContext);
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        long[] vibrate={0,100,200,300};
        _notifBuilder.setContentTitle(title)
                .setContentText(text).setSmallIcon(R.mipmap.ic_launcher_my_appicon)
               // .setLargeIcon(BitmapFactory.decodeResource(Resources.getSystem(), R.mipmap.ic_launcher_foreground_white_app_icon))
                .setSound(alarmSound)
                .setVibrate(vibrate)
                .setLights(155,2000,1000)
//                .setStyle(new NotificationCompat.BigTextStyle().bigText(aoPayload.getMessage()))
                .setAutoCancel(true)
                .setVisibility(Notification.VISIBILITY_PUBLIC);

        _notifBuilder.setContentIntent(_contentIntent);
//        NotificationManager _notificationManager = (NotificationManager) aoContext.getSystemService(Context.NOTIFICATION_SERVICE);
        _notificationManager.notify(0, _notifBuilder.build());
    }
}
