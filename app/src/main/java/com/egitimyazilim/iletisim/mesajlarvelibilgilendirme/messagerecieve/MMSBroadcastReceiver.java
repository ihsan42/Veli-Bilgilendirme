package com.egitimyazilim.iletisim.mesajlarvelibilgilendirme.messagerecieve;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by wgwj4809 on 01/08/17.
 */

public class MMSBroadcastReceiver extends BroadcastReceiver {
    public static final String SMS_BUNDLE = "pdus";

    public void onReceive(Context context, Intent intent) {
        Bundle intentExtras = intent.getExtras();

    }
}
