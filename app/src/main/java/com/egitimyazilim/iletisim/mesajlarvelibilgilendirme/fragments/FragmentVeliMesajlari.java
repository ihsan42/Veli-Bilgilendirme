package com.egitimyazilim.iletisim.mesajlarvelibilgilendirme.fragments;

import static android.content.Context.RECEIVER_EXPORTED;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.provider.Telephony;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.egitimyazilim.iletisim.mesajlarvelibilgilendirme.ComposeSmsActivity;
import com.egitimyazilim.iletisim.mesajlarvelibilgilendirme.R;
import com.egitimyazilim.iletisim.mesajlarvelibilgilendirme.Veritabani;
import com.egitimyazilim.iletisim.mesajlarvelibilgilendirme.adapters.AdaptorForMainList;
import com.egitimyazilim.iletisim.mesajlarvelibilgilendirme.object_classes.Chats;
import com.egitimyazilim.iletisim.mesajlarvelibilgilendirme.object_classes.ChatsPerson;
import com.egitimyazilim.iletisim.mesajlarvelibilgilendirme.object_classes.Ogrenci;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.core.content.ContextCompat;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.appcompat.app.AlertDialog;

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
     String[] izinler = {Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS, Manifest.permission.SEND_SMS
             , Manifest.permission.RECEIVE_MMS, Manifest.permission.READ_PHONE_STATE,Manifest.permission.READ_CONTACTS};

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

        mCallbacks=this;
        LoaderManager loaderManager=getLoaderManager();
        loaderManager.initLoader(1,null,mCallbacks);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(getActivity(), ComposeSmsActivity.class);
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            getActivity().registerReceiver(receiver, intentFilter, RECEIVER_EXPORTED);
        }else {
            getActivity().registerReceiver(receiver, intentFilter);
        }
    }

     @Override
     public void onStop() {
         super.onStop();
         getActivity().unregisterReceiver(receiver);
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
                    objChats.setSms_read(c.getString(c.getColumnIndexOrThrow("read")));
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