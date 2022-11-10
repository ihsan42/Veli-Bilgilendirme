package com.egitimyazilim.iletisim.mesajlarvelibilgilendirme;

import android.Manifest;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.provider.Telephony;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.egitimyazilim.iletisim.mesajlarvelibilgilendirme.adapters.AdapterForYazili;
import com.egitimyazilim.iletisim.mesajlarvelibilgilendirme.contentprovider.MessagesContentProviderHandler;
import com.egitimyazilim.iletisim.mesajlarvelibilgilendirme.fragments.MenuContentFragment;
import com.egitimyazilim.iletisim.mesajlarvelibilgilendirme.interfaces.MenuContentComm;
import com.egitimyazilim.iletisim.mesajlarvelibilgilendirme.object_classes.Ogrenci;
import com.egitimyazilim.iletisim.mesajlarvelibilgilendirme.object_classes.OgrenciForYazili;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class Yazililar extends AppCompatActivity implements MenuContentComm {

    public static final String SMS_SENT_ACTION = "com.andriodgifts.gift.SMS_SENT_ACTION";
    public static final String SMS_DELIVERED_ACTION = "com.andriodgifts.gift.SMS_DELIVERED_ACTION";

    FragmentManager fm;
    Button buttonMenuOpen;
    Button buttonMenuClose;
    MenuContentFragment menuContentFragment;
    boolean durum1;
    boolean durum2;
    Button buttonYaziliSil;
    Button buttonGonder;
    Button buttonIptal;
    RadioButton radioButtonYazili1;
    RadioButton radioButtonYazili2;
    Button buttonSMS;
    Button buttonKaydet;
    Spinner spinnerSinif;
    Spinner spinnerDers;
    ListView listView;
    List<String> sinifList;
    List<String> dersList;
    List<String> kursSinifList;
    List<String> tumSinifList;
    List<OgrenciForYazili> yaziliList;
    AdapterForYazili adapterForYazili;
    private static final int requestCodePermission=111;
    String[] izinler={Manifest.permission.SEND_SMS,Manifest.permission.READ_PHONE_STATE};
    boolean izinVarMi=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yazililar);

        ActionBar bar = getSupportActionBar();
        bar.hide();

        buttonMenuOpen=(Button)findViewById(R.id.buttonMenuOpen);
        buttonMenuClose=(Button)findViewById(R.id.buttonMenuClose);

        fm = getSupportFragmentManager();
        menuContentFragment=(MenuContentFragment)fm.findFragmentById(R.id.fragmentMenu);
        fm.beginTransaction().hide(menuContentFragment).commit();

        buttonMenuOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menuButtonsVisibilitySecond();
            }
        });

        buttonMenuClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menuButtonsVisibilityFirst();
            }
        });

        izinVarMi=checkPermission(getApplicationContext(),izinler);
        if(izinVarMi==false){
            ActivityCompat.requestPermissions(Yazililar.this, izinler, requestCodePermission);
        }

        buttonSMS=(Button)findViewById(R.id.buttonYaziliSms);
        buttonKaydet = (Button) findViewById(R.id.buttonYaziliKaydet);
        buttonYaziliSil=(Button)findViewById(R.id.buttonYaziliSil);
        spinnerSinif = (Spinner) findViewById(R.id.spinnerSiniflar);
        spinnerDers = (Spinner) findViewById(R.id.spinnerDersler);
        listView = (ListView) findViewById(R.id.listViewYazililar);

        final Dialog dialogOzel=new Dialog(Yazililar.this);
        dialogOzel.setContentView(R.layout.yazili_sonucu_gonderme);
        buttonGonder=dialogOzel.findViewById(R.id.buttonGonderYaziliSms);
        buttonIptal=dialogOzel.findViewById(R.id.buttonIptalYaziliSms);
        radioButtonYazili1=dialogOzel.findViewById(R.id.radioButtonYazili1);
        radioButtonYazili2=dialogOzel.findViewById(R.id.radioButtonYazili2);

        Veritabani vt = new Veritabani(getApplicationContext());
        dersList = new ArrayList<>();
        sinifList = new ArrayList<>();
        kursSinifList = new ArrayList<>();
        sinifList = vt.getirSinif();
        kursSinifList = vt.getirKursSinif();
        dersList = vt.okutulanDersleriGetir();
        vt.close();

        tumSinifList = new ArrayList<>();
        for (String s : sinifList) {
            tumSinifList.add(s);
        }
        for (String s : kursSinifList) {
            tumSinifList.add(s);
        }

        if (tumSinifList.size() > 0) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_custom_item_for_yazililar, tumSinifList);
            spinnerSinif.setAdapter(adapter);
        } else {
            tumSinifList.add("Kayıtlı sınıf yok!");
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_custom_item_for_yazililar, tumSinifList);
            spinnerSinif.setAdapter(adapter);
        }
        if (dersList.size() > 0) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_custom_item_for_yazililar, dersList);
            spinnerDers.setAdapter(adapter);
        } else {
            dersList.add("Kayıtlı ders yok!");
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_custom_item_for_yazililar, dersList);
            spinnerDers.setAdapter(adapter);
        }

        spinnerSinif.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String secilenSinif = "";
                String secilenDers = "";
                secilenSinif = spinnerSinif.getSelectedItem().toString();
                secilenDers = spinnerDers.getSelectedItem().toString();

                if (tumSinifList.size() > 0 && dersList.size() > 0) {
                    listele(secilenSinif, secilenDers);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerDers.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String secilenSinif = "";
                String secilenDers = "";
                secilenSinif = spinnerSinif.getSelectedItem().toString();
                secilenDers = spinnerDers.getSelectedItem().toString();

                if (tumSinifList.size() > 0 && dersList.size() > 0) {
                    listele(secilenSinif, secilenDers);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        buttonYaziliSil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!spinnerSinif.getSelectedItem().toString().equals("Kayıtlı sınıf yok!")){
                    if(!spinnerDers.getSelectedItem().toString().equals("Kayıtlı ders yok!")){
                        AlertDialog.Builder builder=new AlertDialog.Builder(Yazililar.this);
                        builder.setTitle("Dikkat!");
                        builder.setMessage(spinnerSinif.getSelectedItem().toString()+" sınıfı "+spinnerDers.getSelectedItem().toString()+" dersine ait tüm kayıtlar silinecektir"+"\n\nSilmek istediğinizden emin misiniz?");
                        builder.setNegativeButton("İptal", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        builder.setPositiveButton("Sil", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Veritabani vt=new Veritabani(getApplicationContext());
                                vt.yaziliKayitlariSil(spinnerSinif.getSelectedItem().toString(),spinnerDers.getSelectedItem().toString());
                                vt.close();
                                Toast.makeText(getApplicationContext(),"Silindi",Toast.LENGTH_SHORT).show();
                                listele(spinnerSinif.getSelectedItem().toString(),spinnerDers.getSelectedItem().toString());
                            }
                        });
                        AlertDialog alertDialog=builder.create();
                        alertDialog.show();
                    }else{
                        Toast.makeText(getApplicationContext(), "Kayıtlı ders yok!", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(getApplicationContext(), "Kayıtlı sınıf yok!", Toast.LENGTH_SHORT).show();
                }
            }
        });



        buttonSMS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!spinnerSinif.getSelectedItem().toString().equals("Kayıtlı sınıf yok!")){
                    if(!spinnerDers.getSelectedItem().toString().equals("Kayıtlı ders yok!")){
                        durum1=false;
                        for(OgrenciForYazili ogrenci:adapterForYazili.ogrenciList){
                            ogrenci.setDers(spinnerDers.getSelectedItem().toString());
                            if(!TextUtils.isEmpty(ogrenci.getYazili1())){
                                durum1=true;
                            }
                        }
                        durum2=false;
                        for(OgrenciForYazili ogrenci:adapterForYazili.ogrenciList){
                            if(!TextUtils.isEmpty(ogrenci.getYazili2())){
                                durum2=true;
                            }
                        }
                        if (izinVarMi==true) {
                            dialogOzel.show();

                            SmsManager[] smsManager={SmsManager.getDefault()};
                            if(SMSGonder.isDualSimAvailable(Yazililar.this)){
                                SMSGonder.getDefaultSMSManeger(Yazililar.this,smsManager);
                            }

                            buttonGonder.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (!radioButtonYazili1.isChecked() && !radioButtonYazili2.isChecked()) {
                                        Toast.makeText(getApplicationContext(), "Lütfen hangi yazılı sonucunun gönderileceğini işaretleyiniz", Toast.LENGTH_SHORT).show();
                                    }else if (radioButtonYazili1.isChecked() && durum1 == false) {
                                        Toast.makeText(getApplicationContext(), "Girilmiş 1. yazılı notu yok!", Toast.LENGTH_SHORT).show();
                                    } else if (radioButtonYazili2.isChecked() && durum2 == false) {
                                        Toast.makeText(getApplicationContext(), "Girilmiş 2. yazılı notu yok!", Toast.LENGTH_SHORT).show();
                                    }else {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(Yazililar.this);
                                        String yaziliIndex="";
                                        if(radioButtonYazili1.isChecked()){
                                            yaziliIndex="1.";
                                        }
                                        if(radioButtonYazili2.isChecked()){
                                            yaziliIndex="2.";
                                        }
                                        builder.setTitle(spinnerSinif.getSelectedItem().toString() + " " + spinnerDers.getSelectedItem().toString() +" "+yaziliIndex+" yazılı notları gönderilsin mi?");
                                        builder.setPositiveButton("Gönder", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                SharedPreferences sharedPref = getSharedPreferences("Kisisel Ayarlar", MODE_PRIVATE);
                                                boolean durumIsim = sharedPref.getBoolean("isim", false);
                                                boolean durumBrans = sharedPref.getBoolean("brans", false);
                                                boolean durumOkuladi = sharedPref.getBoolean("okuladi", false);
                                                Veritabani vt = new Veritabani(getApplicationContext());
                                                List<String> kisiselBilgiler = new ArrayList<>();
                                                kisiselBilgiler = vt.kisiselBilgileriGetir();
                                                vt.close();

                                                String mesaj = "";
                                                String dersadi = "";
                                                dersadi = spinnerDers.getSelectedItem().toString();
                                                if (radioButtonYazili1.isChecked()) {
                                                    for (OgrenciForYazili ogrenci : adapterForYazili.ogrenciList) {
                                                        if (TextUtils.isEmpty(ogrenci.getYazili1())) {
                                                            if (durumIsim == true && durumBrans == true && durumOkuladi == true) {
                                                                mesaj = kisiselBilgiler.get(2) + " öğrencilerinden " + ogrenci.getAdSoyad() + " " + dersadi + " 1. yazılısına girmedi. [" + kisiselBilgiler.get(0) + " (" + kisiselBilgiler.get(1) + " öğretmeni)]";
                                                            } else if (durumIsim == true && durumBrans == true && durumOkuladi == false) {
                                                                mesaj = "Okulumuz öğrencilerinden " + ogrenci.getAdSoyad() + " " + dersadi + " 1. yazılısına girmedi. [" + kisiselBilgiler.get(0) + " (" + kisiselBilgiler.get(1) + " öğretmeni)]";
                                                            } else if (durumIsim == true && durumBrans == false && durumOkuladi == true) {
                                                                mesaj = kisiselBilgiler.get(2) + " öğrencilerinden " + ogrenci.getAdSoyad() + " " + dersadi + " 1. yazılısına girmedi. [" + kisiselBilgiler.get(0) + "]";
                                                            } else if (durumIsim == true && durumBrans == false && durumOkuladi == false) {
                                                                mesaj = "Okulumuz öğrencilerinden " + ogrenci.getAdSoyad() + " " + dersadi + " 1. yazılısına girmedi. [" + kisiselBilgiler.get(0) + "]";
                                                            } else if (durumIsim == false && durumBrans == true && durumOkuladi == true) {
                                                                mesaj = kisiselBilgiler.get(2) + " öğrencilerinden " + ogrenci.getAdSoyad() + " " + dersadi + " 1. yazılısına girmedi. (" + kisiselBilgiler.get(1) + " öğretmeni)";
                                                            } else if (durumIsim == false && durumBrans == true && durumOkuladi == false) {
                                                                mesaj = "Okulumuz öğrencilerinden " + ogrenci.getAdSoyad() + " " + dersadi + " 1. yazılısına girmedi. (" + kisiselBilgiler.get(1) + " öğretmeni)";
                                                            } else if (durumIsim == false && durumBrans == false && durumOkuladi == true) {
                                                                mesaj = kisiselBilgiler.get(2) + " öğrencilerinden " + ogrenci.getAdSoyad() + " " + dersadi + " 1. yazılısına girmedi.";
                                                            } else if (durumIsim == false && durumBrans == false && durumOkuladi == false) {
                                                                mesaj = "Okulumuz öğrencilerinden " + ogrenci.getAdSoyad() + " " + dersadi + " 1. yazılısına girmedi.";
                                                            }
                                                        } else {
                                                            if (durumIsim == true && durumBrans == true && durumOkuladi == true) {
                                                                mesaj = kisiselBilgiler.get(2) + " öğrencilerinden " + ogrenci.getAdSoyad() + " " + dersadi + " 1. yazılısından " + ogrenci.getYazili1() + " aldı. [" + kisiselBilgiler.get(0) + " (" + kisiselBilgiler.get(1) + " öğretmeni)]";
                                                            } else if (durumIsim == true && durumBrans == true && durumOkuladi == false) {
                                                                mesaj = "Okulumuz öğrencilerinden " + ogrenci.getAdSoyad() + " " + dersadi + " 1. yazılısından " + ogrenci.getYazili1() + " aldı. [" + kisiselBilgiler.get(0) + " (" + kisiselBilgiler.get(1) + " öğretmeni)]";
                                                            } else if (durumIsim == true && durumBrans == false && durumOkuladi == true) {
                                                                mesaj = kisiselBilgiler.get(2) + " öğrencilerinden " + ogrenci.getAdSoyad() + " " + dersadi + " 1. yazılısından " + ogrenci.getYazili1() + " aldı. [" + kisiselBilgiler.get(0) + "]";
                                                            } else if (durumIsim == true && durumBrans == false && durumOkuladi == false) {
                                                                mesaj = "Okulumuz öğrencilerinden " + ogrenci.getAdSoyad() + " " + dersadi + " 1. yazılısından " + ogrenci.getYazili1() + " aldı. [" + kisiselBilgiler.get(0) + "]";
                                                            } else if (durumIsim == false && durumBrans == true && durumOkuladi == true) {
                                                                mesaj = kisiselBilgiler.get(2) + " öğrencilerinden " + ogrenci.getAdSoyad() + " " + dersadi + " 1. yazılısından " + ogrenci.getYazili1() + " aldı. (" + kisiselBilgiler.get(1) + " öğretmeni)";
                                                            } else if (durumIsim == false && durumBrans == true && durumOkuladi == false) {
                                                                mesaj = "Okulumuz öğrencilerinden " + ogrenci.getAdSoyad() + " " + dersadi + " 1. yazılısından " + ogrenci.getYazili1() + " aldı. (" + kisiselBilgiler.get(1) + " öğretmeni)";
                                                            } else if (durumIsim == false && durumBrans == false && durumOkuladi == true) {
                                                                mesaj = kisiselBilgiler.get(2) + " öğrencilerinden " + ogrenci.getAdSoyad() + " " + dersadi + " 1. yazılısından " + ogrenci.getYazili1() + " aldı.";
                                                            } else if (durumIsim == false && durumBrans == false && durumOkuladi == false) {
                                                                mesaj = "Okulumuz öğrencilerinden " + ogrenci.getAdSoyad() + " " + dersadi + " 1. yazılısından " + ogrenci.getYazili1() + " aldı.";
                                                            }
                                                        }
                                                        SMSGonder.gonder(getApplicationContext(),smsManager[0],ogrenci.getTelno(),mesaj,ogrenci.getAdSoyad());
                                                    }
                                                    dialog.dismiss();
                                                    dialogOzel.dismiss();
                                                } else if (radioButtonYazili2.isChecked()) {
                                                    for (OgrenciForYazili ogrenci : adapterForYazili.ogrenciList) {
                                                        if (TextUtils.isEmpty(ogrenci.getYazili2())) {
                                                            if (durumIsim == true && durumBrans == true && durumOkuladi == true) {
                                                                mesaj = kisiselBilgiler.get(2) + " öğrencilerinden " + ogrenci.getAdSoyad() + " " + dersadi + " 2. yazılısına girmedi. [" + kisiselBilgiler.get(0) + " (" + kisiselBilgiler.get(1) + " öğretmeni)]";
                                                            } else if (durumIsim == true && durumBrans == true && durumOkuladi == false) {
                                                                mesaj = "Okulumuz öğrencilerinden " + ogrenci.getAdSoyad() + " " + dersadi + " 2. yazılısına girmedi. [" + kisiselBilgiler.get(0) + " (" + kisiselBilgiler.get(1) + " öğretmeni)]";
                                                            } else if (durumIsim == true && durumBrans == false && durumOkuladi == true) {
                                                                mesaj = kisiselBilgiler.get(2) + " öğrencilerinden " + ogrenci.getAdSoyad() + " " + dersadi + " 2. yazılısına girmedi. [" + kisiselBilgiler.get(0) + "]";
                                                            } else if (durumIsim == true && durumBrans == false && durumOkuladi == false) {
                                                                mesaj = "Okulumuz öğrencilerinden " + ogrenci.getAdSoyad() + " " + dersadi + " 2. yazılısına girmedi. [" + kisiselBilgiler.get(0) + "]";
                                                            } else if (durumIsim == false && durumBrans == true && durumOkuladi == true) {
                                                                mesaj = kisiselBilgiler.get(2) + " öğrencilerinden " + ogrenci.getAdSoyad() + " " + dersadi + " 2. yazılısına girmedi. (" + kisiselBilgiler.get(1) + " öğretmeni)";
                                                            } else if (durumIsim == false && durumBrans == true && durumOkuladi == false) {
                                                                mesaj = "Okulumuz öğrencilerinden " + ogrenci.getAdSoyad() + " " + dersadi + " 2. yazılısına girmedi. (" + kisiselBilgiler.get(1) + " öğretmeni)";
                                                            } else if (durumIsim == false && durumBrans == false && durumOkuladi == true) {
                                                                mesaj = kisiselBilgiler.get(2) + " öğrencilerinden " + ogrenci.getAdSoyad() + " " + dersadi + " 2. yazılısına girmedi.";
                                                            } else if (durumIsim == false && durumBrans == false && durumOkuladi == false) {
                                                                mesaj = "Okulumuz öğrencilerinden " + ogrenci.getAdSoyad() + " " + dersadi + " 2. yazılısına girmedi.";
                                                            }
                                                        } else {
                                                            if (durumIsim == true && durumBrans == true && durumOkuladi == true) {
                                                                mesaj = kisiselBilgiler.get(2) + " öğrencilerinden " + ogrenci.getAdSoyad() + " " + dersadi + " 2. yazılısından " + ogrenci.getYazili2() + " aldı. [" + kisiselBilgiler.get(0) + " (" + kisiselBilgiler.get(1) + " öğretmeni)]";
                                                            } else if (durumIsim == true && durumBrans == true && durumOkuladi == false) {
                                                                mesaj = "Okulumuz öğrencilerinden " + ogrenci.getAdSoyad() + " " + dersadi + " 2. yazılısından " + ogrenci.getYazili2() + " aldı. [" + kisiselBilgiler.get(0) + " (" + kisiselBilgiler.get(1) + " öğretmeni)]";
                                                            } else if (durumIsim == true && durumBrans == false && durumOkuladi == true) {
                                                                mesaj = kisiselBilgiler.get(2) + " öğrencilerinden " + ogrenci.getAdSoyad() + " " + dersadi + " 2. yazılısından " + ogrenci.getYazili2() + " aldı. [" + kisiselBilgiler.get(0) + "]";
                                                            } else if (durumIsim == true && durumBrans == false && durumOkuladi == false) {
                                                                mesaj = "Okulumuz öğrencilerinden " + ogrenci.getAdSoyad() + " " + dersadi + " 2. yazılısından " + ogrenci.getYazili2() + " aldı. [" + kisiselBilgiler.get(0) + "]";
                                                            } else if (durumIsim == false && durumBrans == true && durumOkuladi == true) {
                                                                mesaj = kisiselBilgiler.get(2) + " öğrencilerinden " + ogrenci.getAdSoyad() + " " + dersadi + " 2. yazılısından " + ogrenci.getYazili2() + " aldı. (" + kisiselBilgiler.get(1) + " öğretmeni)";
                                                            } else if (durumIsim == false && durumBrans == true && durumOkuladi == false) {
                                                                mesaj = "Okulumuz öğrencilerinden " + ogrenci.getAdSoyad() + " " + dersadi + " 2. yazılısından " + ogrenci.getYazili2() + " aldı. (" + kisiselBilgiler.get(1) + " öğretmeni)";
                                                            } else if (durumIsim == false && durumBrans == false && durumOkuladi == true) {
                                                                mesaj = kisiselBilgiler.get(2) + " öğrencilerinden " + ogrenci.getAdSoyad() + " " + dersadi + " 2. yazılısından " + ogrenci.getYazili2() + " aldı.";
                                                            } else if (durumIsim == false && durumBrans == false && durumOkuladi == false) {
                                                                mesaj = "Okulumuz öğrencilerinden " + ogrenci.getAdSoyad() + " " + dersadi + " 2. yazılısından " + ogrenci.getYazili2() + " aldı.";
                                                            }
                                                        }
                                                       SMSGonder.gonder(getApplicationContext(),smsManager[0]
                                                               ,ogrenci.getTelno(),mesaj,ogrenci.getAdSoyad());
                                                    }
                                                    dialog.dismiss();
                                                    dialogOzel.dismiss();
                                                }
                                            }
                                        });
                                        builder.setNegativeButton("İptal", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        });
                                        AlertDialog alertDialog = builder.create();
                                        alertDialog.show();
                                    }
                                }
                            });

                            buttonIptal.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialogOzel.dismiss();
                                }
                            });
                        } else {
                            List<Integer> birDahaSormaSayisi = new ArrayList<>();
                            for (String izin : izinler) {
                                if (ActivityCompat.shouldShowRequestPermissionRationale(Yazililar.this, izin)) {
                                    birDahaSormaSayisi.add(1);
                                    ActivityCompat.requestPermissions(Yazililar.this, izinler, requestCodePermission);
                                }
                            }
                            if (birDahaSormaSayisi.size()==0) {
                                AlertDialog.Builder builder=new AlertDialog.Builder(Yazililar.this);
                                builder.setTitle("Dikkat!");
                                builder.setMessage("Sms gönderebilmek için eksik izinler var. SMS ve Telefon izinlerinin ikisini de vermeniz gereklidir. İzinleri tamamlamak için <Ayarlar>'a tıklayınız ve açılan sayfadaki izinler bölümüden bu izinlerden eksik olanına izin veriniz.");
                                builder.setPositiveButton("Ayarlar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent myAppSettings = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + getApplicationContext().getPackageName()));
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
                    }else{
                        Toast.makeText(getApplicationContext(), "Kayıtlı ders yok!", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(getApplicationContext(), "Kayıtlı sınıf yok!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        buttonKaydet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Veritabani vt = new Veritabani(getApplicationContext());
                if(yaziliList.size()==0){
                    if(!spinnerSinif.getSelectedItem().toString().equals("Kayıtlı sınıf yok!")){
                        if(!spinnerDers.getSelectedItem().toString().equals("Kayıtlı ders yok!")){
                            AlertDialog.Builder builder=new AlertDialog.Builder(Yazililar.this);
                            builder.setTitle("Notlar kaydedilsin mi?");
                            builder.setPositiveButton("Kaydet", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    for (OgrenciForYazili ogrenci :adapterForYazili.ogrenciList) {
                                        long id = vt.yaziliKaydet(ogrenci);
                                        if (id > 0) {
                                        } else {
                                            Toast.makeText(getApplicationContext(), ogrenci.getAdSoyad() + " yazılı sonucu kaydedilemedi!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                    Toast.makeText(getApplicationContext(), "Kaydetme işlemi tamamlandı", Toast.LENGTH_SHORT).show();
                                    listele(spinnerSinif.getSelectedItem().toString(),spinnerDers.getSelectedItem().toString());
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
                        }else{
                            Toast.makeText(getApplicationContext(), "Kayıtlı ders yok!", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(getApplicationContext(), "Kayıtlı sınıf yok!", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    if(!spinnerSinif.getSelectedItem().toString().equals("Kayıtlı sınıf yok!")){
                        if(!spinnerDers.getSelectedItem().toString().equals("Kayıtlı ders yok!")){
                            AlertDialog.Builder builder=new AlertDialog.Builder(Yazililar.this);
                            builder.setTitle("Notlar kaydedilsin mi?");
                            builder.setPositiveButton("Kaydet", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    for (OgrenciForYazili ogrenci : adapterForYazili.ogrenciList) {
                                        ogrenci.setDers(spinnerDers.getSelectedItem().toString());
                                        long id=vt.yaziliGuncelle(ogrenci);
                                        if (id>0) {
                                        } else {
                                            Toast.makeText(getApplicationContext(), ogrenci.getAdSoyad() + " yazılı sonucu kaydedilemedi!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                    Toast.makeText(getApplicationContext(), "Kaydetme işlemi tamamlandı", Toast.LENGTH_SHORT).show();
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
                        }else{
                            Toast.makeText(getApplicationContext(), "Kayıtlı ders yok!", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(getApplicationContext(), "Kayıtlı sınıf yok!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void menuButtonsVisibilitySecond(){
        fm.beginTransaction().show(menuContentFragment).commit();
        buttonMenuOpen.setVisibility(View.INVISIBLE);
        buttonMenuClose.setVisibility(View.VISIBLE);
    }

    private void menuButtonsVisibilityFirst(){
        fm.beginTransaction().hide(menuContentFragment).commit();
        buttonMenuOpen.setVisibility(View.VISIBLE);
        buttonMenuClose.setVisibility(View.INVISIBLE);
    }

    @Override
    public void menuButtonsVisibility() {
        menuButtonsVisibilityFirst();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==requestCodePermission){
            izinVarMi=checkPermission(getApplicationContext(),izinler);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        izinVarMi=checkPermission(getApplicationContext(),izinler);
    }

    public boolean checkPermission(Context context, String[] permissions) {
        boolean isPermissionsOk = true;

        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_DENIED) {
                isPermissionsOk = false;
            }
        }
        return isPermissionsOk;
    }

    private void listele(String secilenSinif, String secilenDers) {
        yaziliList = new ArrayList<>();
        List<Ogrenci> ogrenciList = new ArrayList<>();
        Veritabani vt = new Veritabani(getApplicationContext());
        if(!secilenSinif.equals("Kayıtlı sınıf yok!") && !secilenDers.equals("Kayıtlı ders yok!")){
            ogrenciList = vt.getirSinifOgrenciList(secilenSinif);
            yaziliList = vt.yaziliKayitlariGetir(secilenSinif,secilenDers);

            if (yaziliList.size() > 0) {
                adapterForYazili = new AdapterForYazili(getApplicationContext(), yaziliList);
                listView.setAdapter(adapterForYazili);
            } else {
                List<OgrenciForYazili> forYazilis=new ArrayList<>();
                adapterForYazili = new AdapterForYazili(getApplicationContext(), forYazilis);
                if (ogrenciList.size() > 0) {
                    for (Ogrenci ogrenci : ogrenciList) {
                        OgrenciForYazili ogrenciForYazili = new OgrenciForYazili();
                        ogrenciForYazili.setSinif(ogrenci.getSinif());
                        ogrenciForYazili.setAdSoyad(ogrenci.getAdSoyad());
                        ogrenciForYazili.setOkulno(ogrenci.getOkulno());
                        ogrenciForYazili.setTelno(ogrenci.getTelno());
                        ogrenciForYazili.setDers(secilenDers);
                        forYazilis.add(ogrenciForYazili);
                    }
                    listView.setAdapter(adapterForYazili);
                } else {
                    if(adapterForYazili.ogrenciList.size()!=0){
                        adapterForYazili.ogrenciList.clear();
                    }
                    listView.setAdapter(null);
                }
            }
        }
        vt.close();
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder=new AlertDialog.Builder(Yazililar.this);
        builder.setTitle("Uygulamadan çıkılsın mı?");
        builder.setPositiveButton("Çıkış", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent a = new Intent(Intent.ACTION_MAIN);
                a.addCategory(Intent.CATEGORY_HOME);
                a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(a);
                finishAffinity();
            }
        });
        builder.setNegativeButton("İptal", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        AlertDialog alertDialog=builder.create();
        alertDialog.show();
    }
}
