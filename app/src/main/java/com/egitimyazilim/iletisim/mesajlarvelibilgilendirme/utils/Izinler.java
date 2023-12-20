package com.egitimyazilim.iletisim.mesajlarvelibilgilendirme.utils;

import static androidx.core.app.ActivityCompat.requestPermissions;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
abstract public class Izinler {

    static public boolean checkPermission(Context context,String[] permissions) {
        boolean isPermissionsOk = true;

        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                isPermissionsOk = false;
                break;
            }
        }
        return isPermissionsOk;
    }

    static public void showRequestPermissionDialog(Activity context, String[] permissions, int requestCodePermission){
        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        builder.setTitle("Eksik izinler var!");
        builder.setMessage("İzinleri gözden geçirmek ister misiniz?");
        builder.setPositiveButton("Evet", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent myAppSettings = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + context.getPackageName()));
                myAppSettings.addCategory(Intent.CATEGORY_DEFAULT);
                myAppSettings.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivityForResult(myAppSettings, 35);
            }
        });
        builder.setNegativeButton("Kapat", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog=builder.create();
        alertDialog.show();
    }
}
