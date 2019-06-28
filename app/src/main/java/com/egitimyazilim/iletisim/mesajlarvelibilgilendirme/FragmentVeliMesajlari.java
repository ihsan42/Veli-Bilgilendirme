package com.egitimyazilim.iletisim.mesajlarvelibilgilendirme;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.Settings;
import android.provider.Telephony;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.egitimyazilim.iletisim.mesajlarvelibilgilendirme.utils.ContentContract;
import com.egitimyazilim.iletisim.mesajlarvelibilgilendirme.utils.ValueConstants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static android.support.test.InstrumentationRegistry.getContext;

 public class FragmentVeliMesajlari extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    List<Chats> phone_chat;
    List<ChatsPerson> chatsPeople;
    ListView listView;
    AdaptorForMainList adaptorForMainList;
    FloatingActionButton fabSendSMS;
    String cursor_filter;
    private LoaderManager.LoaderCallbacks<Cursor> mCallbacks;
    boolean varsayılanMi = false;
     BroadcastReceiver receiver;
     boolean izinlerTamamMi = false;
     TextView textViewSMSyok;
    private static final int requestDefaultSmsAppCode = 120;
    private static final int requestPermissionsCode = 1990;

     String readSMS = Manifest.permission.READ_SMS;
     String recieveSMS = Manifest.permission.RECEIVE_SMS;
     String sendSMS = Manifest.permission.SEND_SMS;
     String recieveMMS = Manifest.permission.RECEIVE_MMS;
     String readPhoneState = Manifest.permission.READ_PHONE_STATE;
     String readContacts=Manifest.permission.READ_CONTACTS;

     String[] izinler = {readSMS, recieveSMS, sendSMS, recieveMMS, readPhoneState,readContacts};

     private void izinIste(){
         ActivityCompat.requestPermissions(getActivity(), izinler, requestPermissionsCode);

         List<Integer> birDahaSormaSayisi = new ArrayList<>();
         for (String izin : izinler) {
             if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), izin)) {
                 birDahaSormaSayisi.add(1);
             }
         }
         if (birDahaSormaSayisi.size()==0) {
             AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
             builder.setTitle("Dikkat!");
             builder.setMessage("Sms gönderebilmek ve Sms'lerinizi görüntüleyebilmek için eksik izinler var. SMS, Telefon ve Kişiler izinlerinin üçünü de vermeniz gereklidir. İzinleri tamamlamak için <Ayarlar>'a tıklayınız ve açılan sayfadaki izinler bölümüden bu izinlerden eksik olanına izin veriniz.");
             builder.setPositiveButton("Ayarlar", new DialogInterface.OnClickListener() {
                 @Override
                 public void onClick(DialogInterface dialog, int which) {
                     Intent myAppSettings = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + getActivity().getPackageName()));
                     myAppSettings.addCategory(Intent.CATEGORY_DEFAULT);
                     myAppSettings.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                     startActivityForResult(myAppSettings, 35);
                 }
             });
             builder.setNegativeButton("İptal", new DialogInterface.OnClickListener() {
                 @Override
                 public void onClick(DialogInterface dialog, int which) {
                     dialog.dismiss();
                 }
             });
             AlertDialog alertDialog=builder.create();
             alertDialog.show();
         }
     }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_veli_sms,null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listView = (ListView) view.findViewById(R.id.listView);
        textViewSMSyok=(TextView)view.findViewById(R.id.textViewVeliSMSYok);

        varsayılanMi = checkDefaultSMSapp();
        if (varsayılanMi == true) {
        } else {
            requestDefaultSMSapp();
        }

        izinlerTamamMi = checkPermission();
        if (izinlerTamamMi == false) {
            izinIste();
        } else {
            mCallbacks=this;

            LoaderManager loaderManager=getLoaderManager();
            loaderManager.initLoader(1,null,mCallbacks);
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(getActivity(),ComposeSmsActivity.class);
                intent.putExtra("telno",adaptorForMainList.people.get(position).getSender());
                intent.putExtra("name",adaptorForMainList.people.get(position).getSender_name());
                startActivity(intent);
            }
        });

        fabSendSMS = (FloatingActionButton) view.findViewById(R.id.send_sms);
        fabSendSMS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (varsayılanMi == true && izinlerTamamMi == true) {
                    startActivity(new Intent(getActivity(), ComposeSmsActivity.class));
                }
            }
        });
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int i, @Nullable Bundle bundle) {
        String selection = null;
        String[] selectionArgs = null;

        if (cursor_filter != null) {
            selection = ContentContract.SMS_SELECTION_SEARCH;
            selectionArgs = new String[]{"%" + cursor_filter + "%", "%" + cursor_filter + "%"};
        }

        return new CursorLoader(getActivity(),
                ContentContract.ALL_SMS_URI,
                null,
                selection,
                selectionArgs, ContentContract.SORT_DESC);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if(cursor!=null&& cursor.getCount() > 0){

            chatsPeople=new ArrayList<>();
            phone_chat=getAllSms(cursor);
            listele(phone_chat,chatsPeople);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        phone_chat.clear();
        listView.setAdapter(null);
    }

    @Override
    public void onResume() {
        super.onResume();

        IntentFilter intentFilter = new IntentFilter("android.intent.action.MAIN");

        receiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {

                boolean new_sms = intent.getBooleanExtra("new_sms", false);

                if (new_sms)
                    getLoaderManager().restartLoader(ValueConstants.ALL_SMS_LOADER, null, (LoaderManager.LoaderCallbacks<Object>) context);

            }
        };

        getActivity().registerReceiver(receiver, intentFilter);
    }

     @Override
     public void onStop() {
         super.onStop();
         getActivity().unregisterReceiver(receiver);
     }

    private boolean checkDefaultSMSapp() {
        boolean isDefault = false;
        final String myPackageName = getActivity().getPackageName();
        if (Telephony.Sms.getDefaultSmsPackage(getActivity()).equals(myPackageName)) {
            isDefault = true;
        }
        return isDefault;
    }

    private void requestDefaultSMSapp() {
        final String myPackageName = getActivity().getPackageName();
        Intent intent =
                new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
        intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME,
                myPackageName);
        startActivityForResult(intent, requestDefaultSmsAppCode);
    }

    private boolean checkPermission() {
        boolean isPermissionsOk = true;

        for (String permission : izinler) {
            if (ContextCompat.checkSelfPermission(getActivity(), permission) != PackageManager.PERMISSION_GRANTED) {
                isPermissionsOk = false;
            }
        }
        return isPermissionsOk;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == requestDefaultSmsAppCode) {
            varsayılanMi = checkDefaultSMSapp();
            if (varsayılanMi == false) {
                Toast.makeText(getActivity(),"Bu bölümden mesaj gönderebilmek bu uygulamanın varsayılan sms uygulaması olması gerekir!",Toast.LENGTH_LONG).show();
            }

            izinlerTamamMi = checkPermission();
            if (izinlerTamamMi == false) {
                izinIste();
            }else{
                mCallbacks=this;

                LoaderManager loaderManager=getLoaderManager();
                loaderManager.initLoader(1,null,mCallbacks);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == requestPermissionsCode) {
            izinlerTamamMi = checkPermission();
            if(izinlerTamamMi==true){
                mCallbacks=this;

                LoaderManager loaderManager=getLoaderManager();
                loaderManager.initLoader(1,null,mCallbacks);
            }else{
                Toast.makeText(getActivity(),"Mesajları görüntüleyebilmek için istenilen izinleri vermeniz gerekir!",Toast.LENGTH_SHORT).show();

            }
        }
    }

    public List<Chats> getAllSms(Cursor c) {

        List<Chats> chatsList = new ArrayList<Chats>();
        List<Chats> veliChatsList = new ArrayList<Chats>();
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
                    objChats.setSms_read(c.getString(c.getColumnIndex("read")));
                    objChats.setTime(c.getLong(c.getColumnIndexOrThrow("date")));
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

        Veritabani vt=new Veritabani(getActivity());
        List<Ogrenci> ogrenciList=new ArrayList<>();
        ogrenciList=vt.getirOgrenci();
        vt.close();
        for(Ogrenci ogrenci:ogrenciList){
            String ogrTelNo=ogrenci.getTelno();
            if(ogrenci.getTelno().length()>10){
                ogrTelNo=ogrenci.getTelno().substring(ogrenci.getTelno().length()-10,ogrenci.getTelno().length());
            }
            for(Chats chats1:chatsList){
                String chatsTelNo=chats1.getSender();
                if(chatsTelNo.length()>10){
                    chatsTelNo=chatsTelNo.substring(chatsTelNo.length()-10,chatsTelNo.length());
                }
                if (ogrTelNo.equals(chatsTelNo)){
                veliChatsList.add(chats1);
                }
            }
        }
        chatsList.clear();
        ogrenciList.clear();
        return veliChatsList;
        //Log.d(TAG,"Size before "+data.size());
        //SetToRecycler(lstSms);
    }

    private void listele(List<Chats> chatsList,List<ChatsPerson> chatspeople) {
        Set<Chats> s = new LinkedHashSet<>(chatsList);
        chatsList = new ArrayList<>(s);

        List<Chats> chats=new ArrayList<>();
        List<String> telnolar=new ArrayList<>();
        for(int i=0;i<chatsList.size();i++){
            telnolar.add(chatsList.get(i).getSender());
        }

        HashSet<String> telNolarHash=new HashSet<>();
        telNolarHash.addAll(telnolar);
        telnolar.clear();
        telnolar.addAll(telNolarHash);

        for(String telno:telnolar) {
            List<String> messages = new ArrayList<>();
            List<String> sms_read = new ArrayList<>();
            List<Long> times = new ArrayList<>();
            List<String> folderNames = new ArrayList<>();
            for (Chats chatsperson:chatsList) {
                if (telno.equals(chatsperson.getSender())) {
                    messages.add(chatsperson.getMessage());
                    times.add(chatsperson.getTime());
                    sms_read.add(chatsperson.getSms_read());
                    folderNames.add(chatsperson.getFolder_name());
                }
            }

            ChatsPerson chatsPerson=new ChatsPerson(telno,messages,sms_read,times,folderNames);
            chatspeople.add(chatsPerson);

            int sonIndex=0;//chatsPerson.getMessages().size()-1;
            String sender=telno;
            String message=chatsPerson.getMessages().get(sonIndex);
            String foldername=chatsPerson.getFolder_names().get(sonIndex);
            Long time=chatsPerson.getTimes().get(sonIndex);
            String smsread=chatsPerson.getSms_read().get(sonIndex);


            Chats chat=new Chats(sender,message,smsread,time,foldername);
            chats.add(chat);
        }

        Collections.sort(chats);
        List<Chats> chatsOrder=new ArrayList<>();
        for(int i=0;i<chats.size();i++){
            chatsOrder.add(chats.get(chats.size()-1-i));
        }
        chatsList.clear();
        chatsList.addAll(chatsOrder);

        Veritabani vt=new Veritabani(getActivity());
        List<Ogrenci> ogrenciList=new ArrayList<>();
        ogrenciList=vt.getirOgrenci();
        vt.close();
        for(Ogrenci ogrenci:ogrenciList){
            String ogrTelNo=ogrenci.getTelno();
            if(ogrenci.getTelno().length()>10){
                ogrTelNo=ogrenci.getTelno().substring(ogrenci.getTelno().length()-10,ogrenci.getTelno().length());
            }
            for(Chats chats1:chatsList){
                String chatsTelNo=chats1.getSender();
                if(chatsTelNo.length()>10){
                    chatsTelNo=chatsTelNo.substring(chatsTelNo.length()-10,chatsTelNo.length());
                }
                if (ogrTelNo.equals(chatsTelNo) &&!ogrenci.getTelno().equals("0")){
                    chats1.setSender_name(ogrenci.getSinif()+" "+ogrenci.getAdSoyad()+" "+"Velisi:"+ogrenci.getVeliAdi());
                }
            }
        }

        adaptorForMainList = new AdaptorForMainList(getActivity(), chatsList);
        if (chatsList.size() > 0) {
            listView.setAdapter(adaptorForMainList);
            textViewSMSyok.setText("");
        } else {
            listView.setAdapter(null);
            textViewSMSyok.setText("Görüntülenecek mesaj yok!");
        }
    }
}