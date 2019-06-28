package com.egitimyazilim.iletisim.mesajlarvelibilgilendirme;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
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

public class FragmentDiğerMesajlar extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    List<Chats> phone_chat;
    List<ChatsPerson> chatsPeople;
    ListView listView;
    AdaptorForMainList adaptorForMainList;
    FloatingActionButton fabSendSMS;
    String cursor_filter;
    TextView textViewSMSyok;
    private LoaderManager.LoaderCallbacks<Cursor> mCallbacks;
    BroadcastReceiver receiver;
    boolean varsayılanMi = false;
    boolean izinlerTamamMi = false;
    private static final int requestDefaultSmsAppCode = 120;
    private static final int requestPermissionsCode = 1990;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_diger_sms,null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listView = (ListView) view.findViewById(R.id.listView);
        textViewSMSyok=(TextView)view.findViewById(R.id.textViewVeliSMSYok2);

        izinlerTamamMi = checkPermission();
        if (izinlerTamamMi == false) {
            requestPermissions();
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
                // intent.putExtra( "listedengelen","listedengelen");
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

        String readSMS = Manifest.permission.READ_SMS;
        String recieveSMS = Manifest.permission.RECEIVE_SMS;
        String sendSMS = Manifest.permission.SEND_SMS;
        String recieveMMS = Manifest.permission.RECEIVE_MMS;
        String readPhoneState = Manifest.permission.READ_PHONE_STATE;
        String readContacts=Manifest.permission.READ_CONTACTS;

        String[] permissions = {readSMS, recieveSMS, sendSMS, recieveMMS, readPhoneState,readContacts};

        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(getActivity(), permission) != PackageManager.PERMISSION_GRANTED) {
                isPermissionsOk = false;
            }
        }
        return isPermissionsOk;
    }

    public void requestPermissions() {
        String readSMS = Manifest.permission.READ_SMS;
        String recieveSMS = Manifest.permission.RECEIVE_SMS;
        String sendSMS = Manifest.permission.SEND_SMS;
        String recieveMMS = Manifest.permission.RECEIVE_MMS;
        String readPhoneState = Manifest.permission.READ_PHONE_STATE;
        String readContacts=Manifest.permission.READ_CONTACTS;

        String[] permissions = {readSMS, recieveSMS, sendSMS, recieveMMS, readPhoneState,readContacts};
        ActivityCompat.requestPermissions(getActivity(), permissions, requestPermissionsCode);
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
                requestPermissions();
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

    private List<Chats> getAllSms(Cursor c) {

        List<Chats> chatsList1 = new ArrayList<Chats>();
        List<Chats> digerChatsList = new ArrayList<Chats>();
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

                    chatsList1.add(objChats);
                    c.moveToNext();
                }
            }
        }

        Veritabani vt=new Veritabani(getActivity());
        List<Ogrenci> ogrenciList=new ArrayList<>();
        ogrenciList=vt.getirOgrenci();
        vt.close();
        for(Chats chats1:chatsList1){
            boolean durum=false;
            String chatsTelNo=chats1.getSender();
            if(chatsTelNo.length()>10){
                chatsTelNo=chatsTelNo.substring(chatsTelNo.length()-10,chatsTelNo.length());
            }
            for(Ogrenci ogrenci:ogrenciList){
                String ogrTelNo=ogrenci.getTelno();
                if(ogrenci.getTelno().length()>10){
                    ogrTelNo=ogrenci.getTelno().substring(ogrenci.getTelno().length()-10,ogrenci.getTelno().length());
                }
                if (ogrTelNo.equals(chatsTelNo)){
                    durum=true;
                }
            }
            if(durum==false){
                digerChatsList.add(chats1);
            }
        }
        chatsList1.clear();
        ogrenciList.clear();
        return digerChatsList;
        //Log.d(TAG,"Size before "+data.size());
        //SetToRecycler(lstSms);
    }

    private void listele(List<Chats> chatsList,List<ChatsPerson> chatspeople) {
        Set<Chats> s = new LinkedHashSet<>(chatsList);
        chatsList = new ArrayList<>(s);

        List<Chats> chats=new ArrayList<>();
        List<String> telnolar=new ArrayList<>();
        for(int i=0;i<chatsList.size();i++){
            String chatsTelNo=chatsList.get(i).getSender();
            if(chatsTelNo.length()>10){
                chatsTelNo=chatsTelNo.substring(chatsTelNo.length()-10,chatsTelNo.length());
            }
            telnolar.add(chatsTelNo);
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
                String chatsTelNo=chatsperson.getSender();
                if(chatsTelNo.length()>10){
                    chatsTelNo=chatsTelNo.substring(chatsTelNo.length()-10,chatsTelNo.length());
                }
                if (telno.equals(chatsTelNo)) {
                    telno=chatsperson.getSender();
                    messages.add(chatsperson.getMessage());
                    times.add(chatsperson.getTime());
                    sms_read.add(chatsperson.getSms_read());
                    folderNames.add(chatsperson.getFolder_name());
                }
            }

            ChatsPerson chatsPerson=new ChatsPerson(telno,messages,sms_read,times,folderNames);
            chatspeople.add(chatsPerson);

            int sonIndex=0;//chatsPerson.getMessages().size()-1;
            String sender=chatsPerson.getSender();
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
        chatsOrder.clear();
        List<String> rehberdekiTelNolar=new ArrayList<>();
        rehberdekiTelNolar=getRawContactsPhoneNumberList();
        List<String> rehberdekiKisiIsimleri=new ArrayList<>();
        rehberdekiKisiIsimleri=getRawContactsDisplayNameList();
        for(int i=0;i<rehberdekiTelNolar.size();i++){
            String rehberdekiTelNo=rehberdekiTelNolar.get(i);
            if(rehberdekiTelNolar.get(i).length()>10){
                rehberdekiTelNo=rehberdekiTelNolar.get(i).substring(rehberdekiTelNolar.get(i).length()-10,rehberdekiTelNolar.get(i).length());
            }
            for(Chats chats1:chatsList){
                String chatsTelNo=chats1.getSender();
                if(chatsTelNo.length()>10){
                    chatsTelNo=chatsTelNo.substring(chatsTelNo.length()-10,chatsTelNo.length());
                }
                if (rehberdekiTelNo.equals(chatsTelNo)){
                    chats1.setSender_name(rehberdekiKisiIsimleri.get(i));
                }
            }
        }

        rehberdekiKisiIsimleri.clear();
        rehberdekiTelNolar.clear();

        adaptorForMainList = new AdaptorForMainList(getActivity(), chatsList);
        if (chatsList.size() > 0) {
            listView.setAdapter(adaptorForMainList);
            textViewSMSyok.setText("");
        } else {
            listView.setAdapter(null);
            textViewSMSyok.setText("Görüntülenecek mesaj yok!");
        }
    }

    private List<String> getRawContactsPhoneNumberList() {
        List<String> ret = new ArrayList<>();
        Uri readContactsUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        Cursor cursor = getActivity().getContentResolver().query(readContactsUri, null, null, null, null);
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

    private List<String> getRawContactsDisplayNameList() {
        List<String> ret = new ArrayList<>();
        Uri readContactsUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        Cursor cursor = getActivity().getContentResolver().query(readContactsUri, null, null, null, null);
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