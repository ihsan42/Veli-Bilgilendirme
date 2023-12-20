package com.egitimyazilim.iletisim.mesajlarvelibilgilendirme;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.loader.app.LoaderManager;
import androidx.core.content.ContextCompat;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.egitimyazilim.iletisim.mesajlarvelibilgilendirme.adapters.AdaptorForChatPersonMessages;
import com.egitimyazilim.iletisim.mesajlarvelibilgilendirme.contentprovider.MessagesContentProviderHandler;
import com.egitimyazilim.iletisim.mesajlarvelibilgilendirme.object_classes.Chats;
import com.egitimyazilim.iletisim.mesajlarvelibilgilendirme.utils.ContentContract;
import com.egitimyazilim.iletisim.mesajlarvelibilgilendirme.utils.Izinler;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by xvbp3947 on 01/08/17.
 */
public class ComposeSmsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String SMS_SENT_ACTION = "com.andriodgifts.gift.SMS_SENT_ACTION";
    public static final String SMS_DELIVERED_ACTION = "com.andriodgifts.gift.SMS_DELIVERED_ACTION";

    AdaptorForChatPersonMessages adaptorForChatPersonMessages;
    List<Long> dates=new ArrayList<>();
    boolean varsayilanMi=false;
    RecyclerView recyclerView;
    EditText editTextMesaj;
    // EditText editTextAlici;
    Button buttonGonder;
    TextView sendStatusTextView;
    List<Chats> mesajlar=new ArrayList<>();
    String kisiTel="";
    private LoaderManager.LoaderCallbacks<Cursor> mCallbacks;
    private static final int requestPermissionsCode = 1990;

    private BroadcastReceiver sentStatusReceiver, deliveredStatusReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        //listViewChat=(ListView) findViewById(R.id.listViewChat);
        recyclerView = (RecyclerView) findViewById(R.id.recylerViewChat);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        editTextMesaj = (EditText) findViewById(R.id.message_edit_text);
        //editTextAlici=(EditText)findViewById(R.id.phone_number_edit_text);
        buttonGonder = (Button) findViewById(R.id.send_button);
        sendStatusTextView = (TextView) findViewById(R.id.message_status_text_view2);
        TextView textViewTitle = (TextView) findViewById(R.id.textViewChat);

        Intent ıntent = getIntent();
        kisiTel = ıntent.getStringExtra("telno");
        String name = ıntent.getStringExtra("name");
        ActionBar bar = getSupportActionBar();
        bar.hide();

        String title = "";
        if (name == null) {
            title = kisiTel;
        } else {
            title = name + "\n" + kisiTel;
        }
        textViewTitle.setText(title);

        /*String nerdengeldi=ıntent.getStringExtra("listedengelen");
        if(nerdengeldi!=null&&nerdengeldi.equals("listedengelen")){
            editTextAlici.setEnabled(false);
        }*/
       // varsayilanMi = checkDefaultSMSapp();

        char[] chars = kisiTel.toCharArray();

        boolean durum = false;
        for (int i = 0; i < chars.length; i++) {
            String s = String.valueOf(chars[i]);
            if (s.matches("^[a-zA-Z]")) {
                durum = true;
            }
        }
        if (durum == true) {
            buttonGonder.setVisibility(View.GONE);
            editTextMesaj.setVisibility(View.GONE);
        }

       /* if(varsayilanMi==false){
            buttonGonder.setVisibility(View.INVISIBLE);
            editTextMesaj.setVisibility(View.INVISIBLE);
        }else {
            buttonGonder.setVisibility(View.VISIBLE);
            editTextMesaj.setVisibility(View.VISIBLE);
        }*/

        // editTextAlici.setText(kisiTel);
       // if (izinlerTamamMi == true) {
            mCallbacks = this;
            LoaderManager loaderManager = getSupportLoaderManager();
            loaderManager.initLoader(1, null, mCallbacks);
       // }


        buttonGonder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] izinler={Manifest.permission.SEND_SMS,Manifest.permission.READ_PHONE_STATE};

                if (!Izinler.checkPermission(getApplicationContext(), izinler)) {
                    Izinler.showRequestPermissionDialog(ComposeSmsActivity.this,izinler,requestPermissionsCode);
                } else {
                    sendMySMS();
                    sendStatusTextView.setText("");
                }
            }
        });
    }

   /* @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        varsayilanMi=checkDefaultSMSapp();
        if (varsayilanMi == false) {
            Toast.makeText(getApplicationContext(),"Mesaj gönderebilmek için bu uygulamanın varsayılan sms uygulaması olması gerekir!",Toast.LENGTH_LONG).show();
        }else{
            izinlerTamamMi = checkPermission();
            if(izinlerTamamMi==true){
                mCallbacks=this;

                LoaderManager loaderManager=getSupportLoaderManager();
                loaderManager.initLoader(1,null,mCallbacks);
            }else{
                Toast.makeText(getApplicationContext(),"Mesajları görüntüleyebilmek için istenilen izinleri vermeniz gerekir!",Toast.LENGTH_LONG).show();
            }
        }*/

    private List<Chats> getAllSms(Cursor c) {
        List<Chats> chatsList = new ArrayList<Chats>();
        Chats objChats = null;
        int totalSMS = c.getCount();

        if (c.moveToFirst()) {
            for (int i = 0; i < totalSMS; i++) {

                try {
                    objChats = new Chats();
                    objChats.set_id(c.getLong(c.getColumnIndexOrThrow("_id")));
                    String num = c.getString(c.getColumnIndexOrThrow("address"));
                    objChats.setSender(num);
                    objChats.setMessage(c.getString(c.getColumnIndexOrThrow("body")));
                    objChats.setSms_read(c.getString(c.getColumnIndexOrThrow("read")));
                    objChats.setTime(c.getLong(c.getColumnIndexOrThrow("date")));
                    // objChats.setSent_time(c.getLong(c.getColumnIndexOrThrow("date_sent")));
                    if (c.getString(c.getColumnIndexOrThrow("type")).contains("1")) {
                        objChats.setFolder_name("inbox");
                    } else {
                        objChats.setFolder_name("sent");
                    }

                } catch (Exception e) {

                } finally {

                    chatsList.add(objChats);
                    c.moveToNext();
                }
            }
        }
        return chatsList;
    }

    public void onResume() {
        super.onResume();
        sentStatusReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                String s = "Gönderilemedi:Bilinmeyen Hata!";
                switch (getResultCode()) {
                    case RESULT_OK:
                        s = "Gönderildi";
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        s = "Generic Failure Error";
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        s = "Error :Kullanılabilir mesaj servisi yok!";
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        s = "Error : Null PDU";
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        s = "Error : Radio is off";
                        break;
                    default:
                        break;
                }
                sendStatusTextView.setText(s);
            }
        };
        deliveredStatusReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context arg0, Intent arg1) {
                String s = "Mesaj iletilemedi!";
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        s = "İletildi";
                        break;
                    case Activity.RESULT_CANCELED:
                        break;
                }
                sendStatusTextView.setText(s);
            }
        };

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(sentStatusReceiver,  new IntentFilter("SMS_SENT"), RECEIVER_EXPORTED);
        }else {
            registerReceiver(sentStatusReceiver,  new IntentFilter("SMS_SENT"));
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(deliveredStatusReceiver, new IntentFilter("SMS_DELIVERED"), RECEIVER_EXPORTED);
        }else {
            registerReceiver(deliveredStatusReceiver, new IntentFilter("SMS_DELIVERED"));
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(sentStatusReceiver);
        unregisterReceiver(deliveredStatusReceiver);
    }

    public void sendMySMS() {
        String phone =kisiTel;//editTextAlici.getText().toString();
        String message = String.valueOf(editTextMesaj.getText()).trim();
        //Check if the phoneNumber is empty
        if (phone.isEmpty()) {
            Toast.makeText(getApplicationContext(), "geçerli bir telefon numarası giriniz.", Toast.LENGTH_SHORT).show();
        } else {
            if(TextUtils.isEmpty(message)){
                Toast.makeText(getApplicationContext(), "mesaj yazılmamış!", Toast.LENGTH_SHORT).show();
            }else{
                if(SMSGonder.isDualSimAvailable(getApplicationContext())){
                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(ComposeSmsActivity.this);
                    builder.setCancelable(false);
                    builder.setTitle("Hat seçiniz!");
                    builder.setPositiveButton("2.Hat", new DialogInterface.OnClickListener() {
                        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
                        @Override
                        public void onClick(DialogInterface dialogInterface, int ii) {
                            SmsManager smsManager = SmsManager.getSmsManagerForSubscriptionId(1);
                            SMSGonder.gonder(ComposeSmsActivity.this,smsManager
                                    ,phone,message,null);
                            dialogInterface.dismiss();
                        }
                    });
                    builder.setNeutralButton("1.hat", new DialogInterface.OnClickListener() {
                        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
                        @Override
                        public void onClick(DialogInterface dialogInterface, int ii) {
                            SmsManager smsManager = SmsManager.getSmsManagerForSubscriptionId(0);
                            SMSGonder.gonder(ComposeSmsActivity.this
                                    ,smsManager,phone,message,null);
                            dialogInterface.dismiss();
                        }
                    });
                    android.app.AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }else{
                    SmsManager smsManager = SmsManager.getDefault();
                    SMSGonder.gonder(ComposeSmsActivity.this
                            ,smsManager,phone,message,null);
                }

            }

        }
            editTextMesaj.setText("");
    }

    private void listele(){
        adaptorForChatPersonMessages=new AdaptorForChatPersonMessages(getApplicationContext(),mesajlar);
        if(mesajlar.size()>0){
            recyclerView.setAdapter(adaptorForChatPersonMessages);
            recyclerView.findViewHolderForAdapterPosition(mesajlar.size()-1);
        }else{
            recyclerView.setAdapter(null);
        }

    }
    public boolean checkDefaultSMSapp(){
        boolean isDefault = false;
        final String myPackageName = String.valueOf(getPackageName());
        if (String.valueOf(Telephony.Sms.getDefaultSmsPackage(this)).equals(myPackageName)) {
            isDefault = true;
        }
        return isDefault;
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int i, @Nullable Bundle bundle) {
        String selection = null;
        String[] selectionArgs = null;
        String cursor_filter=kisiTel;

        if (cursor_filter != null) {
            selection = ContentContract.SMS_SELECTION_SEARCH;
            selectionArgs = new String[]{"%" + cursor_filter + "%", "%" + cursor_filter + "%"};
        }

        return new CursorLoader(getApplicationContext(),
                ContentContract.ALL_SMS_URI,
                null,
                selection,
                selectionArgs, ContentContract.SORT_ASC);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        if(cursor!=null&& cursor.getCount() > 0){
            mesajlar=new ArrayList<>();
            mesajlar=getAllSms(cursor);
            listele();
            recyclerView.getLayoutManager().scrollToPosition(mesajlar.size()-1);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        mesajlar.clear();
        recyclerView.setAdapter(null);
    }
}
