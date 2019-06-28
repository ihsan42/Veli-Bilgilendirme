package com.egitimyazilim.iletisim.mesajlarvelibilgilendirme.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;


public class Izinler {

    public boolean checkPermission(Context context,String[] permissions) {
        boolean isPermissionsOk = true;

        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                isPermissionsOk = false;
            }
        }
        return isPermissionsOk;
    }
}
