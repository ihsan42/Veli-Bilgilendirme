package com.egitimyazilim.iletisim.mesajlarvelibilgilendirme.fragments;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.provider.Telephony;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.egitimyazilim.iletisim.mesajlarvelibilgilendirme.R;
import com.egitimyazilim.iletisim.mesajlarvelibilgilendirme.SMSGonder;
import com.egitimyazilim.iletisim.mesajlarvelibilgilendirme.Veritabani;
import com.egitimyazilim.iletisim.mesajlarvelibilgilendirme.adapters.AdapterForYokamaSilme;
import com.egitimyazilim.iletisim.mesajlarvelibilgilendirme.contentprovider.MessagesContentProviderHandler;
import com.egitimyazilim.iletisim.mesajlarvelibilgilendirme.interfaces.CommYoklama;
import com.egitimyazilim.iletisim.mesajlarvelibilgilendirme.object_classes.Ogrenci;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class YoklamaGecmisi extends DialogFragment {

    public static final String SMS_SENT_ACTION = "com.andriodgifts.gift.SMS_SENT_ACTION";
    public static final String SMS_DELIVERED_ACTION = "com.andriodgifts.gift.SMS_DELIVERED_ACTION";
    String tarihler="";
    List<Ogrenci> yoklamaList;
    List<Ogrenci> secilenler;
    TextView textViewBilgi;
    ListView listView;
    Button buttonSil;
    CheckBox checkBoxSelectAll;
    CheckBox checkBoxSelectAny;
    AdapterForYokamaSilme adapterForYokamaSilme;
    String gelenSinifAdi;
    String gelenOkulNo;
    Button buttonCancel;
    Button buttonVeliBilgilendir;
    String gelenAdAsoyad;
    String gelenTelNo;
    boolean izinVarMi=false;
    private static final int requestCodePermission=111;
    String[] izinler={Manifest.permission.SEND_SMS,Manifest.permission.READ_PHONE_STATE};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.yoklama_gecmisi,null,false);
    }

    @Override
    public void onStart() {
        super.onStart();

        listele(gelenSinifAdi,gelenOkulNo);
        if(yoklamaList.size()==0){
            textViewBilgi.setText("Kayıt Yok!");
        }
    }

    public boolean checkPermission(Context context, String[] permissions) {
        boolean isPermissionsOk = true;

        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                isPermissionsOk = false;
            }
        }
        return isPermissionsOk;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        listView=(ListView)view.findViewById(R.id.listviewYokKaySil);
        buttonSil=(Button)view.findViewById(R.id.buttonYokKaySil);
        buttonCancel=(Button)view.findViewById(R.id.buttonYokKayCancel);
        buttonVeliBilgilendir=(Button)view.findViewById(R.id.buttonYokKayVeliBilg);
        checkBoxSelectAll=(CheckBox)view.findViewById(R.id.checkSelectAll);
        checkBoxSelectAny=(CheckBox)view.findViewById(R.id.checkSelectAny);
        textViewBilgi=(TextView)view.findViewById(R.id.textViewBilgi);

        izinVarMi=checkPermission(getActivity(),izinler);
        if(izinVarMi==false){
            ActivityCompat.requestPermissions(getActivity(), izinler, requestCodePermission);
        }

        buttonVeliBilgilendir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(yoklamaList.size()==0){
                    Toast.makeText(getActivity(),"Kayıt Yok!",Toast.LENGTH_SHORT).show();
                }else{
                    secilenler=new ArrayList<>();
                    for(Ogrenci ogrenci:adapterForYokamaSilme.ogrenciList){
                        if(ogrenci.getChecked()==true){
                            secilenler.add(ogrenci);
                        }
                    }

                    if(secilenler.size()==0){
                        Toast.makeText(getActivity(),"Seçilen Yok!",Toast.LENGTH_SHORT).show();

                    }else if(izinVarMi==false){
                        List<Integer> birDahaSormaSayisi = new ArrayList<>();
                        for (String izin : izinler) {
                            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), izin)) {
                                birDahaSormaSayisi.add(1);
                                ActivityCompat.requestPermissions(getActivity(), new String[]{izin}, requestCodePermission);
                            }
                        }
                        if (birDahaSormaSayisi.size()==0) {
                            AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
                            builder.setTitle("Dikkat!");
                            builder.setMessage("Sms gönderebilmek için eksik izinler var. SMS ve Telefon izinlerinin ikisini de vermeniz gereklidir. İzinleri tamamlamak için <Ayarlar>'a tıklayınız ve açılan sayfadaki izinler bölümüden bu izinlerden eksik olanına izin veriniz.");
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
                    }else{
                        tarihler="";
                        String y="yok";
                        String g="geç";
                        String r="raporlu";
                        String i="izinli";
                        for(Ogrenci ogrenci1:secilenler){
                            if(ogrenci1.getDurum().equals(y)){
                                if(ogrenci1.getSinif().contains("(Kurs)")){
                                    tarihler+=ogrenci1.getTarih()+" tarihinde kursa gelmedi."+"\n";
                                }else{
                                    tarihler+=ogrenci1.getTarih()+" tarihinde okula gelmedi."+"\n";
                                }
                            }
                            if(ogrenci1.getDurum().equals(g)){
                                if(ogrenci1.getSinif().contains("(Kurs)")){
                                    tarihler+=ogrenci1.getTarih()+" tarihinde kursa geç geldi."+"\n";
                                }else{
                                    tarihler+=ogrenci1.getTarih()+" tarihinde okula geç geldi."+"\n";
                                }
                            }
                            if(ogrenci1.getDurum().equals(r)){
                                tarihler+=ogrenci1.getTarih()+" tarihinde raporludur."+"\n";
                            }
                            if(ogrenci1.getDurum().equals(i)){
                                if(ogrenci1.getSinif().contains("(Kurs)")){
                                    tarihler+=ogrenci1.getTarih()+" tarihinde kurs için izinlidir."+"\n";
                                }else{
                                    tarihler+=ogrenci1.getTarih()+" tarihinde izinlidir."+"\n";
                                }
                            }
                        }
                        AlertDialog.Builder builder1=new AlertDialog.Builder(getActivity());
                        builder1.setTitle("SMS gönderilsin mi?");
                        builder1.setMessage("Okulumuz öğrencilerinden "+gelenAdAsoyad+"\n"+tarihler);
                        builder1.setPositiveButton("Gönder", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ArrayList<PendingIntent> sentIntents=new ArrayList<>();
                                PendingIntent sentIntent=PendingIntent.getBroadcast(getActivity(), 0, new Intent(SMS_SENT_ACTION), PendingIntent.FLAG_MUTABLE);

                                final String mesaj="Okulumuz öğrencilerinden "+gelenAdAsoyad+"\n"+tarihler;
                                SMSGonder.gonder(getActivity(),gelenTelNo,mesaj,gelenAdAsoyad);
                                dialog.dismiss();
                            }
                        });
                        builder1.setNegativeButton("İptal", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        AlertDialog alertDialog=builder1.create();
                        alertDialog.show();
                    }
                }
            }
        });

        checkBoxSelectAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(yoklamaList.size()==0){
                    Toast.makeText(getActivity(),"Kayıt yok!",Toast.LENGTH_SHORT).show();
                }else{
                    for(Ogrenci ogrenci:adapterForYokamaSilme.ogrenciList){
                        ogrenci.setChecked(true);
                    }
                    checkBoxSelectAny.setVisibility(View.VISIBLE);
                    checkBoxSelectAny.setChecked(true);
                    checkBoxSelectAll.setVisibility(View.INVISIBLE);
                    listView.setAdapter(adapterForYokamaSilme);
                }
            }
        });

        checkBoxSelectAny.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(yoklamaList.size()==0){
                    Toast.makeText(getActivity(),"Kayıt yok!",Toast.LENGTH_SHORT).show();
                }else{
                    for(Ogrenci ogrenci:adapterForYokamaSilme.ogrenciList){
                        ogrenci.setChecked(false);
                    }
                    checkBoxSelectAny.setVisibility(View.INVISIBLE);
                    checkBoxSelectAll.setVisibility(View.VISIBLE);
                    checkBoxSelectAll.setChecked(false);
                    listView.setAdapter(adapterForYokamaSilme);
                }
            }
        });

        buttonSil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(yoklamaList.size()==0){
                    Toast.makeText(getActivity(),"Kayıt Yok!",Toast.LENGTH_SHORT).show();
                }else{
                    secilenler=new ArrayList<>();
                    for(Ogrenci ogrenci:adapterForYokamaSilme.ogrenciList){
                        if(ogrenci.getChecked()==true){
                            secilenler.add(ogrenci);
                        }
                    }

                    if(secilenler.size()==0){
                        Toast.makeText(getActivity(),"Seçilen Yok!",Toast.LENGTH_SHORT).show();

                    }else{
                        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
                        builder.setTitle("DİKKAT!");
                        builder.setMessage("Seçilen kayıtları silmek istiyor musunuz?");
                        builder.setPositiveButton("Sil", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                for(Ogrenci ogrenci:secilenler){
                                    if(ogrenci.getChecked()==true){
                                        Veritabani vt=new Veritabani(getActivity());
                                        vt.ogrenciYoklamaSil(gelenSinifAdi,gelenOkulNo,ogrenci.getTarih());
                                    }
                                }
                                Toast.makeText(getActivity(),"Silindi",Toast.LENGTH_SHORT).show();
                                CommYoklama commYoklama=(CommYoklama)getActivity();
                                commYoklama.yoklamaKayitSilmeOk();
                                dismiss();
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
            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

    }

    private void listele(String sinifadi, String okulno){
        yoklamaList=new ArrayList<>();
        Veritabani vt=new Veritabani(getActivity());
        yoklamaList=vt.ogrenciYoklamaKaydiGetir(sinifadi,okulno);

        if(yoklamaList.size()>0){
            adapterForYokamaSilme=new AdapterForYokamaSilme(getActivity(),yoklamaList);
            listView.setAdapter(adapterForYokamaSilme);
        }else{
            listView.setAdapter(null);
        }
    }

    public void gelenOgrenci(String sinifadi, String okulno,String adsoyad, String telno){
        this.gelenSinifAdi=sinifadi;
        this.gelenOkulNo=okulno;
        this.gelenAdAsoyad=adsoyad;
        this.gelenTelNo=telno;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==requestCodePermission){
            izinVarMi=checkPermission(getActivity(),izinler);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        izinVarMi=checkPermission(getActivity(),izinler);
    }
}
