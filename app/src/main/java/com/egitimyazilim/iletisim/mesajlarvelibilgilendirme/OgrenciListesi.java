package com.egitimyazilim.iletisim.mesajlarvelibilgilendirme;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.provider.Telephony;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SubscriptionManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.egitimyazilim.iletisim.mesajlarvelibilgilendirme.adapters.AdapterForKayıtlıOgr;
import com.egitimyazilim.iletisim.mesajlarvelibilgilendirme.adapters.AdapterForOgrList;
import com.egitimyazilim.iletisim.mesajlarvelibilgilendirme.adapters.AdapterForOgrListOneCheck;
import com.egitimyazilim.iletisim.mesajlarvelibilgilendirme.adapters.AdapterTumOgr;
import com.egitimyazilim.iletisim.mesajlarvelibilgilendirme.contentprovider.MessagesContentProviderHandler;
import com.egitimyazilim.iletisim.mesajlarvelibilgilendirme.fragments.MenuContentFragment;
import com.egitimyazilim.iletisim.mesajlarvelibilgilendirme.fragments.MultiSpecialSms;
import com.egitimyazilim.iletisim.mesajlarvelibilgilendirme.fragments.OgrenciEkleme;
import com.egitimyazilim.iletisim.mesajlarvelibilgilendirme.fragments.OgrenciGuncelle;
import com.egitimyazilim.iletisim.mesajlarvelibilgilendirme.interfaces.CommOgr;
import com.egitimyazilim.iletisim.mesajlarvelibilgilendirme.interfaces.MenuContentComm;
import com.egitimyazilim.iletisim.mesajlarvelibilgilendirme.object_classes.Ogrenci;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class OgrenciListesi extends AppCompatActivity implements CommOgr, MenuContentComm {

    FragmentManager fm;
    Button buttonMenuOpen;
    Button buttonMenuClose;
    MenuContentFragment menuContentFragment;
    TextView textViewTitle;
    String sinifadi="";
    int pageIndex;
    ListView listViewOgrList;
    List<Ogrenci> ogrenciList;
    AdapterForOgrList adapterForOgrList;
    AdapterForOgrListOneCheck adapterForOgrListOneCheck;
    AdapterTumOgr adapterTumOgr;
    AdapterForKayıtlıOgr adapterForKayıtlıOgr;
    Button buttonCall;
    Button buttonSMS;
    Button buttonSave;
    Button buttonAdd;
    Button buttonSpecialSMS;
    Button buttonOtoSMS2;
    Button buttonCancel;
    Button buttonEdit;
    Button buttonDelete;
    CheckBox checkBoxTumSec;
    CheckBox checkBoxSecimiKaldir;
    TextView mevcut;
    Button buttonGonder;
    Button buttonIptal;
    Spinner spinnerDersler;
    TextView textViewMesajGonderilecekler;
    RadioGroup radioGroup;
    RadioButton radioButtonOkula;
    RadioButton radioButtonDerse;

    Button buttonGonder2;
    Button buttonIptal2;
    Spinner spinnerOdevDersler;
    Spinner spinnerHazirMesajlarim;
    TextView textViewMesajGonderilecekler2;
    RadioGroup radioGroup2;
    RadioButton radioButtonHazirMesajlarim;
    RadioButton radioButtonOdev;
    EditText editTextSearch;
    List<Ogrenci> filterList;
    boolean izinVarMi=false;
    private static final int requestCodePermission=111;
    String[] izinler={Manifest.permission.SEND_SMS,Manifest.permission.READ_PHONE_STATE};

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ogrenci_listesi);

        mevcut=(TextView)findViewById(R.id.textViewMevcut);
        checkBoxTumSec=(CheckBox)findViewById(R.id.checkBoxTumSec);
        checkBoxSecimiKaldir=(CheckBox)findViewById(R.id.checkBoxSecimiKaldir);
        textViewTitle=(TextView)findViewById(R.id.textViewTitleOgrLis);
        listViewOgrList=(ListView)findViewById(R.id.listViewOgrList);
        buttonAdd=(Button)findViewById(R.id.buttonOgrAdd);
        buttonSave=(Button)findViewById(R.id.buttonOgrSave);
        buttonEdit=(Button)findViewById(R.id.buttonOgrEdit);
        buttonCancel=(Button)findViewById(R.id.buttonOgrCancel);
        buttonSMS=(Button)findViewById(R.id.buttonOgrSMS);
        buttonSpecialSMS=(Button)findViewById(R.id.buttonOgrSpecialSMS);
        buttonCall=(Button)findViewById(R.id.buttonOgrCall);
        buttonDelete=(Button)findViewById(R.id.buttonOgrDelete);
        buttonOtoSMS2=(Button)findViewById(R.id.buttonOgrOtoSMS2);
        editTextSearch=(EditText)findViewById(R.id.editTextOgrListArama);

        ActionBar bar=getSupportActionBar();
        bar.hide();

        buttonMenuOpen=(Button)findViewById(R.id.buttonMenuOpen);
        buttonMenuClose=(Button)findViewById(R.id.buttonMenuClose);

        fm = getSupportFragmentManager();
        menuContentFragment=(MenuContentFragment)fm.findFragmentById(R.id.fragmentMenu);
        fm.beginTransaction().hide(menuContentFragment).commit();

        buttonMenuOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menuButtonsVisibility2();
            }
        });

        buttonMenuClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menuButtonsVisibility1();
            }
        });

        izinVarMi=checkPermission(getApplicationContext(),izinler);
        if(izinVarMi==false){
            ActivityCompat.requestPermissions(OgrenciListesi.this, izinler, requestCodePermission);
        }

        final String[] aramaIzni=new String[]{Manifest.permission.CALL_PHONE};
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(OgrenciListesi.this, aramaIzni, requestCodePermission);
        }

            final Intent ıntent=getIntent();
        sinifadi=ıntent.getStringExtra("sinifadi");
        pageIndex=ıntent.getIntExtra("pageIndex",pageIndex);

        listele();

        final Dialog dialog1=new Dialog(OgrenciListesi.this);
        dialog1.setContentView(R.layout.auto_sms_gonderme);
        buttonGonder=dialog1.findViewById(R.id.buttonGonderAutoSMS);
        buttonIptal=dialog1.findViewById(R.id.buttonIptalAutoSMS);
        radioGroup=dialog1.findViewById(R.id.radioGroupSMS);
        radioButtonOkula=dialog1.findViewById(R.id.radioButtonOkula);
        radioButtonDerse=dialog1.findViewById(R.id.radioButtonDerse);
        spinnerDersler=dialog1.findViewById(R.id.spinnerDersler);
        textViewMesajGonderilecekler=dialog1.findViewById(R.id.textViewMesajGonderilecekler);

        final Dialog dialog2=new Dialog(OgrenciListesi.this);
        dialog2.setContentView(R.layout.auto_sms_gonderme2);
        buttonGonder2=dialog2.findViewById(R.id.buttonGonderAutoSMS2);
        buttonIptal2=dialog2.findViewById(R.id.buttonIptalAutoSMS2);
        radioGroup2=dialog2.findViewById(R.id.radioGroupSMS2);
        radioButtonOdev=dialog2.findViewById(R.id.radioButtonOdev);
        radioButtonHazirMesajlarim=dialog2.findViewById(R.id.radioButtonHazırMesajlar);
        spinnerHazirMesajlarim=dialog2.findViewById(R.id.spinnerHazırMesajlarım);
        spinnerOdevDersler=dialog2.findViewById(R.id.spinnerOdevDersler);
        textViewMesajGonderilecekler2=dialog2.findViewById(R.id.textViewMesajGonderilecekler2);

        if(pageIndex==1){
            radioButtonOkula.setText("Kursa bugün gün boyu gelmedi/geç geldi.");
            radioButtonDerse.setText("Kurs dersine girmedi/geç geldi.(Ders seç)");
        }

        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterList=new ArrayList<>();

                if(!s.equals("")){
                    for(int i=0;i<ogrenciList.size();i++){
                        if(ogrenciList.get(i).getAdSoyad().toLowerCase().contains(s.toString().toLowerCase())){
                            filterList.add(ogrenciList.get(i));
                        }
                    }
                }

                if(sinifadi.equals("Tüm Öğrenciler")){
                    if(buttonOtoSMS2.getVisibility()==View.INVISIBLE){
                        adapterTumOgr=new AdapterTumOgr(getApplicationContext(),filterList);
                        listViewOgrList.setAdapter(adapterTumOgr);
                    }else{
                        adapterForKayıtlıOgr=new AdapterForKayıtlıOgr(getApplicationContext(),filterList);
                        listViewOgrList.setAdapter(adapterForKayıtlıOgr);
                    }
                }else{
                    if(buttonOtoSMS2.getVisibility()==View.INVISIBLE){
                        adapterForOgrList=new AdapterForOgrList(getApplicationContext(),filterList);
                        listViewOgrList.setAdapter(adapterForOgrList);
                    }else{
                        adapterForOgrListOneCheck=new AdapterForOgrListOneCheck(getApplicationContext(),filterList);
                        listViewOgrList.setAdapter(adapterForOgrListOneCheck);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        radioGroup2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(radioButtonOdev.isChecked()){
                    spinnerOdevDersler.setVisibility(View.VISIBLE);
                    spinnerHazirMesajlarim.setVisibility(View.INVISIBLE);
                }
                if(radioButtonHazirMesajlarim.isChecked()){
                    spinnerOdevDersler.setVisibility(View.INVISIBLE);
                    spinnerHazirMesajlarim.setVisibility(View.VISIBLE);
                }
            }
        });

        buttonOtoSMS2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (izinVarMi == true) {
                    final List<Ogrenci> secilenler = new ArrayList<>();
                    if (sinifadi.equals("Tüm Öğrenciler")) {
                        for (Ogrenci ogrenci : adapterForKayıtlıOgr.ogrenciList) {
                            if (ogrenci.getChecked() == true) {
                                secilenler.add(ogrenci);
                            }
                        }
                    } else {
                        for (Ogrenci ogrenci : adapterForOgrListOneCheck.ogrenciList) {
                            if (ogrenci.getChecked() == true) {
                                secilenler.add(ogrenci);
                            }
                        }
                    }

                    if (secilenler.size() == 0) {
                        Toast.makeText(getApplicationContext(), "Seçilen yok!", Toast.LENGTH_SHORT).show();
                    } else {
                        dialog2.show();
                        SmsManager[] smsManager={SmsManager.getDefault()};
                        if(SMSGonder.isDualSimAvailable(getApplicationContext())){
                            SMSGonder.getDefaultSMSManeger(OgrenciListesi.this,smsManager);
                        }

                        String mesajGonderilecekler = "\n" + "SMS GÖNDERİLECEKLER" + "\n" + "\n";
                        for (Ogrenci ogrenci : secilenler) {
                            mesajGonderilecekler += ogrenci.getAdSoyad() + "\n";
                        }
                        textViewMesajGonderilecekler2.setText(mesajGonderilecekler);

                        Veritabani vt = new Veritabani(getApplicationContext());
                        List<String> dersler = new ArrayList<>();
                        List<String> hazirMesajlarim = new ArrayList<>();
                        dersler = vt.okutulanDersleriGetir();
                        hazirMesajlarim = vt.hazirMesajlarimiGetir();
                        vt.close();
                        if (dersler.size() > 0) {
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), R.layout.custom_spinner_ders, dersler);
                            spinnerOdevDersler.setAdapter(adapter);
                        } else {
                            dersler.add("Kayıtlı ders yok!");
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), R.layout.custom_spinner_ders, dersler);
                            spinnerOdevDersler.setAdapter(adapter);
                        }

                        if (hazirMesajlarim.size() > 0) {
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), R.layout.custom_spinner_ders, hazirMesajlarim);
                            spinnerHazirMesajlarim.setAdapter(adapter);
                        } else {
                            hazirMesajlarim.add("Kayıtlı hazır mesaj yok!");
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), R.layout.custom_spinner_ders, hazirMesajlarim);
                            spinnerHazirMesajlarim.setAdapter(adapter);
                        }

                        buttonGonder2.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                SharedPreferences sharedPref = getSharedPreferences("Kisisel Ayarlar", MODE_PRIVATE);
                                boolean durumIsim = sharedPref.getBoolean("isim", false);
                                boolean durumBrans = sharedPref.getBoolean("brans", false);
                                boolean durumOkuladi = sharedPref.getBoolean("okuladi", false);
                                Veritabani vt = new Veritabani(getApplicationContext());
                                List<String> kisiselBilgiler = new ArrayList<>();
                                kisiselBilgiler = vt.kisiselBilgileriGetir();

                                for (Ogrenci ogrenci : secilenler) {
                                    String mesaj = "";
                                    if (radioButtonOdev.isChecked()) {
                                        if (pageIndex == 0) {
                                            String dersadi = "";
                                            dersadi = spinnerOdevDersler.getSelectedItem().toString();
                                            if (!dersadi.equals("Kayıtlı ders yok!")) {
                                                if (durumIsim == true && durumBrans == true && durumOkuladi == true) {
                                                    mesaj = kisiselBilgiler.get(2) + " öğrencilerinden " + ogrenci.getAdSoyad() + " " + dersadi + " ödevini yapmadı. [" + kisiselBilgiler.get(0) + " (" + kisiselBilgiler.get(1) + " öğretmeni)]";
                                                } else if (durumIsim == true && durumBrans == true && durumOkuladi == false) {
                                                    mesaj = "Okulumuz öğrencilerinden " + ogrenci.getAdSoyad() + " " + dersadi + " ödevini yapmadı. [" + kisiselBilgiler.get(0) + " (" + kisiselBilgiler.get(1) + " öğretmeni)]";
                                                } else if (durumIsim == true && durumBrans == false && durumOkuladi == true) {
                                                    mesaj = kisiselBilgiler.get(2) + " öğrencilerinden " + ogrenci.getAdSoyad() + " " + dersadi + " ödevini yapmadı. [" + kisiselBilgiler.get(0) + "]";
                                                } else if (durumIsim == true && durumBrans == false && durumOkuladi == false) {
                                                    mesaj = "Okulumuz öğrencilerinden " + ogrenci.getAdSoyad() + " " + dersadi + " ödevini yapmadı. [" + kisiselBilgiler.get(0) + "]";
                                                } else if (durumIsim == false && durumBrans == true && durumOkuladi == true) {
                                                    mesaj = kisiselBilgiler.get(2) + " öğrencilerinden " + ogrenci.getAdSoyad() + " " + dersadi + " ödevini yapmadı. (" + kisiselBilgiler.get(1) + " öğretmeni)";
                                                } else if (durumIsim == false && durumBrans == true && durumOkuladi == false) {
                                                    mesaj = "Okulumuz öğrencilerinden " + ogrenci.getAdSoyad() + " " + dersadi + " ödevini yapmadı. (" + kisiselBilgiler.get(1) + " öğretmeni)";
                                                } else if (durumIsim == false && durumBrans == false && durumOkuladi == true) {
                                                    mesaj = kisiselBilgiler.get(2) + " öğrencilerinden " + ogrenci.getAdSoyad() + " " + dersadi + " ödevini yapmadı.";
                                                } else if (durumIsim == false && durumBrans == false && durumOkuladi == false) {
                                                    mesaj = "Okulumuz öğrencilerinden " + ogrenci.getAdSoyad() + " " + dersadi + " ödevini yapmadı.";
                                                }
                                            }
                                        } else if (pageIndex == 1) {
                                            String dersadi = "";
                                            dersadi = spinnerOdevDersler.getSelectedItem().toString();
                                            if (!dersadi.equals("Kayıtlı ders yok!")) {
                                                if (durumIsim == true && durumBrans == true && durumOkuladi == true) {
                                                    mesaj = kisiselBilgiler.get(2) + " öğrencilerinden " + ogrenci.getAdSoyad() + " " + dersadi + " kurs ödevini yapmadı. [" + kisiselBilgiler.get(0) + " (" + kisiselBilgiler.get(1) + " öğretmeni)]";
                                                } else if (durumIsim == true && durumBrans == true && durumOkuladi == false) {
                                                    mesaj = "Okulumuz öğrencilerinden " + ogrenci.getAdSoyad() + " " + dersadi + " kurs ödevini yapmadı. [" + kisiselBilgiler.get(0) + " (" + kisiselBilgiler.get(1) + " öğretmeni)]";
                                                } else if (durumIsim == true && durumBrans == false && durumOkuladi == true) {
                                                    mesaj = kisiselBilgiler.get(2) + " öğrencilerinden " + ogrenci.getAdSoyad() + " " + dersadi + " kurs ödevini yapmadı. [" + kisiselBilgiler.get(0) + "]";
                                                } else if (durumIsim == true && durumBrans == false && durumOkuladi == false) {
                                                    mesaj = "Okulumuz öğrencilerinden " + ogrenci.getAdSoyad() + " " + dersadi + " kurs ödevini yapmadı. [" + kisiselBilgiler.get(0) + "]";
                                                } else if (durumIsim == false && durumBrans == true && durumOkuladi == true) {
                                                    mesaj = kisiselBilgiler.get(2) + " öğrencilerinden " + ogrenci.getAdSoyad() + " " + dersadi + " kurs ödevini yapmadı. (" + kisiselBilgiler.get(1) + " öğretmeni)";
                                                } else if (durumIsim == false && durumBrans == true && durumOkuladi == false) {
                                                    mesaj = "Okulumuz öğrencilerinden " + ogrenci.getAdSoyad() + " " + dersadi + " kurs ödevini yapmadı. (" + kisiselBilgiler.get(1) + " öğretmeni)";
                                                } else if (durumIsim == false && durumBrans == false && durumOkuladi == true) {
                                                    mesaj = kisiselBilgiler.get(2) + " öğrencilerinden " + ogrenci.getAdSoyad() + " " + dersadi + " kurs ödevini yapmadı.";
                                                } else if (durumIsim == false && durumBrans == false && durumOkuladi == false) {
                                                    mesaj = "Okulumuz öğrencilerinden " + ogrenci.getAdSoyad() + " " + dersadi + " kurs ödevini yapmadı.";
                                                }
                                            }
                                        }
                                    } else if (radioButtonHazirMesajlarim.isChecked()) {
                                        String mesajim = "";
                                        mesajim = spinnerHazirMesajlarim.getSelectedItem().toString();
                                        if (!mesajim.equals("Kayıtlı ders yok!")) {
                                            if (durumIsim == true && durumBrans == true && durumOkuladi == true) {
                                                mesaj = kisiselBilgiler.get(2) + " öğrencilerinden " + ogrenci.getAdSoyad() + " " + mesajim + " [" + kisiselBilgiler.get(0) + " (" + kisiselBilgiler.get(1) + " öğretmeni)]";
                                            } else if (durumIsim == true && durumBrans == true && durumOkuladi == false) {
                                                mesaj = "Okulumuz öğrencilerinden " + ogrenci.getAdSoyad() + " " + mesajim + " [" + kisiselBilgiler.get(0) + " (" + kisiselBilgiler.get(1) + " öğretmeni)]";
                                            } else if (durumIsim == true && durumBrans == false && durumOkuladi == true) {
                                                mesaj = kisiselBilgiler.get(2) + " öğrencilerinden " + ogrenci.getAdSoyad() + " " + mesajim + " [" + kisiselBilgiler.get(0) + "]";
                                            } else if (durumIsim == true && durumBrans == false && durumOkuladi == false) {
                                                mesaj = "Okulumuz öğrencilerinden " + ogrenci.getAdSoyad() + " " + mesajim + " [" + kisiselBilgiler.get(0) + "]";
                                            } else if (durumIsim == false && durumBrans == true && durumOkuladi == true) {
                                                mesaj = kisiselBilgiler.get(2) + " öğrencilerinden " + ogrenci.getAdSoyad() + " " + mesajim + " (" + kisiselBilgiler.get(1) + " öğretmeni)";
                                            } else if (durumIsim == false && durumBrans == true && durumOkuladi == false) {
                                                mesaj = "Okulumuz öğrencilerinden " + ogrenci.getAdSoyad() + " " + mesajim + " (" + kisiselBilgiler.get(1) + " öğretmeni)";
                                            } else if (durumIsim == false && durumBrans == false && durumOkuladi == true) {
                                                mesaj = kisiselBilgiler.get(2) + " öğrencilerinden " + ogrenci.getAdSoyad() + " " + mesajim;
                                            } else if (durumIsim == false && durumBrans == false && durumOkuladi == false) {
                                                mesaj = "Okulumuz öğrencilerinden " + ogrenci.getAdSoyad() + " " + mesajim;
                                            }
                                        }
                                    }

                                    String dersadi = "";
                                    if (radioButtonOdev.isChecked()) {
                                        dersadi = spinnerOdevDersler.getSelectedItem().toString();
                                    }
                                    String mesajim = "";
                                    if (radioButtonHazirMesajlarim.isChecked()) {
                                        mesajim = spinnerHazirMesajlarim.getSelectedItem().toString();
                                    }
                                    if (radioButtonOdev.isChecked() && dersadi.equals("Kayıtlı ders yok!")) {
                                        Toast.makeText(getApplicationContext(), "Kayıtlı ders yok!", Toast.LENGTH_SHORT).show();
                                    } else if (radioButtonHazirMesajlarim.isChecked() && mesajim.equals("Kayıtlı hazır mesaj yok!")) {
                                        Toast.makeText(getApplicationContext(), "Kayıtlı hazır mesaj yok!!", Toast.LENGTH_SHORT).show();
                                    } else {
                                       SMSGonder.gonder(getApplicationContext(),smsManager[0]
                                               ,ogrenci.getTelno(),mesaj,ogrenci.getAdSoyad());
                                    }
                                }
                                String dersadi = "";
                                if (radioButtonOdev.isChecked()) {
                                    dersadi = spinnerOdevDersler.getSelectedItem().toString();
                                }
                                String mesajim = "";
                                if (radioButtonHazirMesajlarim.isChecked()) {
                                    mesajim = spinnerHazirMesajlarim.getSelectedItem().toString();
                                }
                                if (radioButtonOdev.isChecked() && dersadi.equals("Kayıtlı ders yok!")) {
                                } else if (radioButtonHazirMesajlarim.isChecked() && mesajim.equals("Kayıtlı hazır mesaj yok!")) {
                                } else {
                                    dialog2.dismiss();
                                }
                            }
                        });

                        buttonIptal2.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog2.dismiss();
                            }
                        });
                    }
                } else {
                    List<Integer> birDahaSormaSayisi = new ArrayList<>();
                    for (String izin : izinler) {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(OgrenciListesi.this, izin)) {
                            birDahaSormaSayisi.add(1);
                            ActivityCompat.requestPermissions(OgrenciListesi.this, new String[]{izin}, requestCodePermission);
                        }
                    }
                    if (birDahaSormaSayisi.size()==0) {
                        AlertDialog.Builder builder=new AlertDialog.Builder(OgrenciListesi.this);
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
            }
        });

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(radioButtonDerse.isChecked()){
                    spinnerDersler.setVisibility(View.VISIBLE);

                    Veritabani vt=new Veritabani(getApplicationContext());
                    List<String> dersler=new ArrayList<>();
                    dersler=vt.okutulanDersleriGetir();
                    if(dersler.size()>0){
                        ArrayAdapter<String> adapter=new ArrayAdapter<>(getApplicationContext(),R.layout.custom_spinner_ders,dersler);
                        spinnerDersler.setAdapter(adapter);
                    }else{
                        dersler.add("Kayıtlı ders yok!");
                        ArrayAdapter<String> adapter=new ArrayAdapter<>(getApplicationContext(),R.layout.custom_spinner_ders,dersler);
                        spinnerDersler.setAdapter(adapter);
                    }
                }
                if(radioButtonOkula.isChecked()){
                    spinnerDersler.setVisibility(View.INVISIBLE);
                }
            }
        });

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ogrenciList.size()==0){
                    Toast.makeText(getApplicationContext(),"Bu sınıfa kayıtlı öğrenci yok!",Toast.LENGTH_SHORT).show();
                }else{
                    Calendar c = Calendar.getInstance();
                    SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd");
                    final String tarih= df2.format(c.getTime());
                    final List<Ogrenci> gelmeyenler=new ArrayList<>();
                    final List<Ogrenci> raporlular=new ArrayList<>();
                    final List<Ogrenci> izinliler=new ArrayList<>();
                    final List<Ogrenci> gecGelenler=new ArrayList<>();
                    String kaydedilecekler="";

                    if(sinifadi.equals("Tüm Öğrenciler")){
                        for(Ogrenci ogrenci:adapterTumOgr.ogrenciList){
                            if(ogrenci.getDurumYok()==true){
                                gelmeyenler.add(ogrenci);
                                kaydedilecekler+=ogrenci.getOkulno()+" "+ ogrenci.getAdSoyad()+" --> gelmedi"+"\n";
                            }
                        }

                        for(Ogrenci ogrenci:adapterTumOgr.ogrenciList){
                            if(ogrenci.getDurumRaporlu()==true){
                                raporlular.add(ogrenci);
                                kaydedilecekler+= ogrenci.getOkulno()+" "+ ogrenci.getAdSoyad()+" --> raporlu"+"\n";
                            }
                        }

                        for(Ogrenci ogrenci:adapterTumOgr.ogrenciList){
                            if(ogrenci.getDurumIzinli()==true){
                                izinliler.add(ogrenci);
                                kaydedilecekler+= ogrenci.getOkulno()+" "+ ogrenci.getAdSoyad()+" --> izinli"+"\n";
                            }
                        }

                        for(Ogrenci ogrenci:adapterTumOgr.ogrenciList){
                            if(ogrenci.getDurumGec()==true){
                                gecGelenler.add(ogrenci);
                                kaydedilecekler+= ogrenci.getOkulno()+" "+ ogrenci.getAdSoyad()+" --> geç geldi"+"\n";
                            }
                        }
                    }else{
                        for(Ogrenci ogrenci:adapterForOgrList.ogrenciList){
                            if(ogrenci.getDurumYok()==true){
                                gelmeyenler.add(ogrenci);
                                kaydedilecekler+=ogrenci.getOkulno()+" "+ ogrenci.getAdSoyad()+" --> gelmedi"+"\n";
                            }
                        }

                        for(Ogrenci ogrenci:adapterForOgrList.ogrenciList){
                            if(ogrenci.getDurumRaporlu()==true){
                                raporlular.add(ogrenci);
                                kaydedilecekler+= ogrenci.getOkulno()+" "+ ogrenci.getAdSoyad()+" --> raporlu"+"\n";
                            }
                        }

                        for(Ogrenci ogrenci:adapterForOgrList.ogrenciList){
                            if(ogrenci.getDurumIzinli()==true){
                                izinliler.add(ogrenci);
                                kaydedilecekler+= ogrenci.getOkulno()+" "+ ogrenci.getAdSoyad()+" --> izinli"+"\n";
                            }
                        }

                        for(Ogrenci ogrenci:adapterForOgrList.ogrenciList){
                            if(ogrenci.getDurumGec()==true){
                                gecGelenler.add(ogrenci);
                                kaydedilecekler+= ogrenci.getOkulno()+" "+ ogrenci.getAdSoyad()+" --> geç geldi"+"\n";
                            }
                        }
                    }

                    int yoklamasonucu=gelmeyenler.size()+gecGelenler.size()+raporlular.size()+izinliler.size();
                    if(yoklamasonucu==0){
                        Toast.makeText(getApplicationContext(),"İşaretlenen yok!",Toast.LENGTH_SHORT).show();
                    }else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(OgrenciListesi.this);
                        builder.setTitle("Yoklama sonucu kaydedilsin mi?");
                        builder.setCancelable(false);
                        builder.setMessage(kaydedilecekler);
                        builder.setPositiveButton("Kaydet", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Veritabani vt = new Veritabani(getApplicationContext());
                                for (Ogrenci ogrenci : gelmeyenler) {
                                    List<Ogrenci> yoklamaGecmisi = new ArrayList<>();
                                    yoklamaGecmisi = vt.ogrenciYoklamaKaydiGetir(sinifadi, ogrenci.getOkulno());
                                    boolean kayitdurumu = false;
                                    for (Ogrenci ogrenci1 : yoklamaGecmisi) {
                                        if (tarih.equals(ogrenci1.getTarih())) {
                                            kayitdurumu = true;
                                        }
                                    }
                                    if (kayitdurumu == false) {
                                        long id = vt.yoklamaKaydet(ogrenci, "yok", tarih);
                                        if (id > 0) {
                                        } else {
                                            Toast.makeText(getApplicationContext(), ogrenci.getAdSoyad() + " kayıt sırasında hata oluştu", Toast.LENGTH_SHORT).show();
                                        }

                                    } else {
                                        Toast.makeText(getApplicationContext(), ogrenci.getAdSoyad() + " bugün zaten kaydedilmiş", Toast.LENGTH_SHORT).show();
                                    }
                                }
                                for (Ogrenci ogrenci : raporlular) {
                                    List<Ogrenci> yoklamaGecmisi = new ArrayList<>();
                                    yoklamaGecmisi = vt.ogrenciYoklamaKaydiGetir(sinifadi, ogrenci.getOkulno());
                                    boolean kayitdurumu = false;
                                    for (Ogrenci ogrenci1 : yoklamaGecmisi) {
                                        if (tarih.equals(ogrenci1.getTarih())) {
                                            kayitdurumu = true;
                                        }
                                    }
                                    if (kayitdurumu == false) {
                                        long id = vt.yoklamaKaydet(ogrenci, "raporlu", tarih);
                                        if (id > 0) {
                                        } else {
                                            Toast.makeText(getApplicationContext(), ogrenci.getAdSoyad() + " kayıt sırasında hata oluştu", Toast.LENGTH_SHORT).show();
                                        }

                                    } else {
                                        Toast.makeText(getApplicationContext(), ogrenci.getAdSoyad() + " bugün zaten kaydedilmiş", Toast.LENGTH_SHORT).show();
                                    }
                                }
                                for (Ogrenci ogrenci : izinliler) {
                                    List<Ogrenci> yoklamaGecmisi = new ArrayList<>();
                                    yoklamaGecmisi = vt.ogrenciYoklamaKaydiGetir(sinifadi, ogrenci.getOkulno());
                                    boolean kayitdurumu = false;
                                    for (Ogrenci ogrenci1 : yoklamaGecmisi) {
                                        if (tarih.equals(ogrenci1.getTarih())) {
                                            kayitdurumu = true;
                                        }
                                    }
                                    if (kayitdurumu == false) {
                                        long id = vt.yoklamaKaydet(ogrenci, "izinli", tarih);
                                        if (id > 0) {
                                        } else {
                                            Toast.makeText(getApplicationContext(), ogrenci.getAdSoyad() + " kayıt sırasında hata oluştu", Toast.LENGTH_SHORT).show();
                                        }

                                    } else {
                                        Toast.makeText(getApplicationContext(), ogrenci.getAdSoyad() + " bugün zaten kaydedilmiş", Toast.LENGTH_SHORT).show();
                                    }
                                }
                                for (Ogrenci ogrenci : gecGelenler) {
                                    List<Ogrenci> yoklamaGecmisi = new ArrayList<>();
                                    yoklamaGecmisi = vt.ogrenciYoklamaKaydiGetir(sinifadi, ogrenci.getOkulno());
                                    boolean kayitdurumu = false;
                                    for (Ogrenci ogrenci1 : yoklamaGecmisi) {
                                        if (tarih.equals(ogrenci1.getTarih())) {
                                            kayitdurumu = true;
                                        }
                                    }
                                    if (kayitdurumu == false) {
                                        long id = vt.yoklamaKaydet(ogrenci, "geç", tarih);
                                        if (id > 0) {
                                        } else {
                                            Toast.makeText(getApplicationContext(), ogrenci.getAdSoyad() + " kayıt sırasında hata oluştu", Toast.LENGTH_SHORT).show();
                                        }

                                    } else {
                                        Toast.makeText(getApplicationContext(), ogrenci.getAdSoyad() + " bugün zaten kaydedilmiş", Toast.LENGTH_SHORT).show();
                                    }
                                }
                                Toast.makeText(getApplicationContext(), "Kaydetme tamamlandı", Toast.LENGTH_SHORT).show();
                                vt.close();
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
            }
        });

        buttonSpecialSMS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Ogrenci> secilenler=new ArrayList<>();
                if(sinifadi.equals("Tüm Öğrenciler")){
                    for(Ogrenci ogrenci:adapterTumOgr.ogrenciList){
                        if(ogrenci.getChecked()==true){
                            secilenler.add(ogrenci);
                        }
                    }
                }else{
                    for(Ogrenci ogrenci:adapterForOgrListOneCheck.ogrenciList){
                        if(ogrenci.getChecked()==true){
                            secilenler.add(ogrenci);
                        }
                    }
                }

                if(secilenler.size()==0){
                    Toast.makeText(getApplicationContext(),"Seçilen yok!",Toast.LENGTH_SHORT).show();
                }else {
                    if (izinVarMi==true) {
                        ozelSMSGonderilecler(secilenler);
                    } else {
                        List<Integer> birDahaSormaSayisi = new ArrayList<>();
                        for (String izin : izinler) {
                            if (ActivityCompat.shouldShowRequestPermissionRationale(OgrenciListesi.this, izin)) {
                                birDahaSormaSayisi.add(1);
                                ActivityCompat.requestPermissions(OgrenciListesi.this, new String[]{izin}, requestCodePermission);
                            }
                        }
                        if (birDahaSormaSayisi.size()==0) {
                            AlertDialog.Builder builder=new AlertDialog.Builder(OgrenciListesi.this);
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
                }
            }
        });

        buttonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Ogrenci> secilenler=new ArrayList<>();
                if(sinifadi.equals("Tüm Öğrenciler")){
                    for(Ogrenci ogrenci:adapterForKayıtlıOgr.ogrenciList){
                        if(ogrenci.getChecked()==true){
                            secilenler.add(ogrenci);
                        }
                    }
                }else{
                    for(Ogrenci ogrenci:adapterForOgrListOneCheck.ogrenciList){
                        if(ogrenci.getChecked()==true){
                            secilenler.add(ogrenci);
                        }
                    }
                }

                if(secilenler.size()==0){
                    Toast.makeText(getApplicationContext(),"Seçilen yok!",Toast.LENGTH_SHORT).show();
                }else if(secilenler.size()==1){
                    guncellenecekOgrenci(secilenler.get(0));
                }else{
                    Toast.makeText(getApplicationContext(),"Birden fazla seçildi!",Toast.LENGTH_SHORT).show();
                }
            }
        });

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder=new AlertDialog.Builder(OgrenciListesi.this);
                builder.setPositiveButton("Elle", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        openOgrenciEkleme(sinifadi);
                    }
                });
                builder.setNegativeButton("Kayıtlı Öğrencilerden", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent ıntent=new Intent(getApplicationContext(),KayitliOgrenciler.class);
                        ıntent.putExtra("sinifadi",sinifadi);
                        startActivityForResult(ıntent,2018);
                    }
                });
                AlertDialog alertDialog=builder.create();
                alertDialog.show();
            }
        });

        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final List<Ogrenci> secilenler=new ArrayList<>();
                if(sinifadi.equals("Tüm Öğrenciler")){
                    for(Ogrenci ogrenci:adapterForKayıtlıOgr.ogrenciList){
                        if(ogrenci.getChecked()==true){
                            secilenler.add(ogrenci);
                        }
                    }
                }else{
                    for(Ogrenci ogrenci:adapterForOgrListOneCheck.ogrenciList){
                        if(ogrenci.getChecked()==true){
                            secilenler.add(ogrenci);
                        }
                    }
                }

                if(secilenler.size()==0){
                    Toast.makeText(getApplicationContext(),"Seçilen yok!",Toast.LENGTH_SHORT).show();
                }else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(OgrenciListesi.this);
                    builder.setMessage("Silmek istediğinizden emin misiniz?");
                    builder.setPositiveButton("Evet", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Veritabani vt = new Veritabani(getApplicationContext());
                            String silinenler = "";
                            for (Ogrenci ogrenci : secilenler) {
                                    vt.siniftanOgrenciSil(ogrenci.getSinif(), ogrenci.getOkulno());
                                    silinenler += ogrenci.getAdSoyad() + "\n";
                            }
                            Toast.makeText(getApplicationContext(), silinenler + " Silindi", Toast.LENGTH_SHORT).show();
                            vt.close();
                            listele();
                            menuButtonsVisibilityFirst();
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

        buttonCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                    List<Ogrenci> secilenler=new ArrayList<>();
                    if(sinifadi.equals("Tüm Öğrenciler")){
                        for(Ogrenci ogrenci:adapterForKayıtlıOgr.ogrenciList){
                            if(ogrenci.getChecked()==true){
                                secilenler.add(ogrenci);
                            }
                        }
                    }else{
                        for(Ogrenci ogrenci:adapterForOgrListOneCheck.ogrenciList){
                            if(ogrenci.getChecked()==true){
                                secilenler.add(ogrenci);
                            }
                        }
                    }

                    if(secilenler.size()==0){
                        Toast.makeText(getApplicationContext(),"Seçilen yok! ",Toast.LENGTH_SHORT).show();
                    }else if(secilenler.size()==1){
                        Intent intent = new Intent(Intent.ACTION_CALL);
                        intent.setData(Uri.parse("tel:"+secilenler.get(0).getTelno()));
                        startActivityForResult(intent,0);
                    }else{
                        Toast.makeText(getApplicationContext(),"Birden fazla öğrenci seçmeyin!",Toast.LENGTH_SHORT).show();
                    }

                } else {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(OgrenciListesi.this,aramaIzni[0])) {
                            ActivityCompat.requestPermissions(OgrenciListesi.this, aramaIzni, requestCodePermission);
                        }else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(OgrenciListesi.this);
                        builder.setTitle("Dikkat!");
                        builder.setMessage("Arama yapabilmek için eksik izin var. Telefon iznini vermeniz gereklidir. İzin vermek için <Ayarlar>'a tıklayınız ve açılan sayfadaki izinler bölümüden izin veriniz.");
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
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                    }
                }
            }
        });



        buttonSMS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (izinVarMi==true) {
                    if(ogrenciList.size()==0){
                        Toast.makeText(getApplicationContext(),"Bu sınıfa kayıtlı öğrenci yok!",Toast.LENGTH_SHORT).show();
                    }else{
                        dialog1.show();
                        SmsManager[] smsManager={SmsManager.getDefault()};
                        if(SMSGonder.isDualSimAvailable(getApplicationContext())){
                            SMSGonder.getDefaultSMSManeger(OgrenciListesi.this,smsManager);
                        }

                        final List<Ogrenci> gelmeyenler=new ArrayList<>();
                        final List<Ogrenci> gecGelenler=new ArrayList<>();
                        String mesajGonderilecekler="";

                        if(sinifadi.equals("Tüm Öğrenciler")){
                            for(Ogrenci ogrenci:adapterTumOgr.ogrenciList){
                                if(ogrenci.getDurumYok()==true){
                                    gelmeyenler.add(ogrenci);
                                    mesajGonderilecekler+= ogrenci.getAdSoyad()+" --> gelmedi"+"\n";
                                }
                            }

                            for(Ogrenci ogrenci:adapterTumOgr.ogrenciList){
                                if(ogrenci.getDurumGec()==true){
                                    gecGelenler.add(ogrenci);
                                    mesajGonderilecekler+= ogrenci.getAdSoyad()+" --> geç geldi"+"\n";
                                }
                            }
                        }else{
                            for(Ogrenci ogrenci:adapterForOgrList.ogrenciList){
                                if(ogrenci.getDurumYok()==true){
                                    gelmeyenler.add(ogrenci);
                                    mesajGonderilecekler+= ogrenci.getAdSoyad()+" --> gelmedi"+"\n";
                                }
                            }

                            for(Ogrenci ogrenci:adapterForOgrList.ogrenciList){
                                if(ogrenci.getDurumGec()==true){
                                    gecGelenler.add(ogrenci);
                                    mesajGonderilecekler+= ogrenci.getAdSoyad()+" --> geç geldi"+"\n";
                                }
                            }
                        }

                        if(gecGelenler.size()+gelmeyenler.size()==0){
                            Toast.makeText(getApplicationContext(),"Mazeretsiz gelmeyen veya geç gelen seçili öğrenci yok!",Toast.LENGTH_SHORT).show();
                        }else{
                            textViewMesajGonderilecekler.setText(mesajGonderilecekler);
                            buttonGonder.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    SharedPreferences sharedPref=getSharedPreferences("Kisisel Ayarlar",MODE_PRIVATE);
                                    boolean durumIsim=sharedPref.getBoolean("isim",false);
                                    boolean durumBrans=sharedPref.getBoolean("brans",false);
                                    boolean durumOkuladi=sharedPref.getBoolean("okuladi",false);
                                    Veritabani vt=new Veritabani(getApplicationContext());
                                    List<String> kisiselBilgiler=new ArrayList<>();
                                    kisiselBilgiler=vt.kisiselBilgileriGetir();

                                    for(Ogrenci ogrenci:gelmeyenler){
                                        String mesaj="";
                                        if(pageIndex==0){
                                            if(radioButtonOkula.isChecked()){
                                                if(durumIsim==true && durumBrans==true && durumOkuladi==true){
                                                    mesaj=kisiselBilgiler.get(2)+" öğrencilerinden "+ogrenci.getAdSoyad()+" bugün kurumumuza gelmedi. ["+kisiselBilgiler.get(0)+" ("+kisiselBilgiler.get(1)+ " öğretmeni)]";
                                                }else if(durumIsim==true && durumBrans==true && durumOkuladi==false){
                                                    mesaj="Okulumuz öğrencilerinden "+ogrenci.getAdSoyad()+" bugün kurumumuza gelmedi. ["+kisiselBilgiler.get(0)+" ("+kisiselBilgiler.get(1)+ " öğretmeni)]";
                                                }else if(durumIsim==true && durumBrans==false && durumOkuladi==true){
                                                    mesaj=kisiselBilgiler.get(2)+" öğrencilerinden "+ogrenci.getAdSoyad()+" bugün kurumumuza gelmedi. ["+kisiselBilgiler.get(0)+"]";
                                                }else if(durumIsim==true && durumBrans==false && durumOkuladi==false){
                                                    mesaj="Okulumuz öğrencilerinden "+ogrenci.getAdSoyad()+" bugün kurumumuza gelmedi. ["+kisiselBilgiler.get(0)+"]";
                                                }else if(durumIsim==false && durumBrans==true && durumOkuladi==true){
                                                    mesaj=kisiselBilgiler.get(2)+" öğrencilerinden "+ogrenci.getAdSoyad()+" bugün kurumumuza gelmedi. ("+kisiselBilgiler.get(1)+ " öğretmeni)";
                                                }else if(durumIsim==false && durumBrans==true && durumOkuladi==false){
                                                    mesaj="Okulumuz öğrencilerinden "+ogrenci.getAdSoyad()+" bugün kurumumuza gelmedi. ("+kisiselBilgiler.get(1)+ " öğretmeni)";
                                                }else if(durumIsim==false && durumBrans==false && durumOkuladi==true){
                                                    mesaj=kisiselBilgiler.get(2)+" öğrencilerinden "+ogrenci.getAdSoyad()+" bugün kurumumuza gelmedi.";
                                                }else if(durumIsim==false && durumBrans==false && durumOkuladi==false){
                                                    mesaj="Okulumuz öğrencilerinden "+ogrenci.getAdSoyad()+" bugün kurumumuza gelmedi.";
                                                }
                                            }else if(radioButtonDerse.isChecked()){
                                                String dersadi="";
                                                dersadi=spinnerDersler.getSelectedItem().toString();
                                                if(!dersadi.equals("Kayıtlı ders yok!")){
                                                    if(durumIsim==true && durumBrans==true && durumOkuladi==true){
                                                        mesaj=kisiselBilgiler.get(2)+" öğrencilerinden "+ogrenci.getAdSoyad()+" bugün "+dersadi+" dersine girmedi. ["+kisiselBilgiler.get(0)+" ("+kisiselBilgiler.get(1)+ " öğretmeni)]";
                                                    }else if(durumIsim==true && durumBrans==true && durumOkuladi==false){
                                                        mesaj="Okulumuz öğrencilerinden "+ogrenci.getAdSoyad()+" bugün "+dersadi+" dersine girmedi. ["+kisiselBilgiler.get(0)+" ("+kisiselBilgiler.get(1)+ " öğretmeni)]";
                                                    }else if(durumIsim==true && durumBrans==false && durumOkuladi==true){
                                                        mesaj=kisiselBilgiler.get(2)+" öğrencilerinden "+ogrenci.getAdSoyad()+" bugün "+dersadi+" dersine girmedi. ["+kisiselBilgiler.get(0)+"]";
                                                    }else if(durumIsim==true && durumBrans==false && durumOkuladi==false){
                                                        mesaj="Okulumuz öğrencilerinden "+ogrenci.getAdSoyad()+" bugün "+dersadi+" dersine girmedi. ["+kisiselBilgiler.get(0)+"]";
                                                    }else if(durumIsim==false && durumBrans==true && durumOkuladi==true){
                                                        mesaj=kisiselBilgiler.get(2)+" öğrencilerinden "+ogrenci.getAdSoyad()+" bugün "+dersadi+" dersine girmedi. ("+kisiselBilgiler.get(1)+ " öğretmeni)";
                                                    }else if(durumIsim==false && durumBrans==true && durumOkuladi==false){
                                                        mesaj="Okulumuz öğrencilerinden "+ogrenci.getAdSoyad()+" bugün "+dersadi+" dersine girmedi. ("+kisiselBilgiler.get(1)+ " öğretmeni)";
                                                    }else if(durumIsim==false && durumBrans==false && durumOkuladi==true){
                                                        mesaj=kisiselBilgiler.get(2)+" öğrencilerinden "+ogrenci.getAdSoyad()+" bugün "+dersadi+" dersine girmedi.";
                                                    }else if(durumIsim==false && durumBrans==false && durumOkuladi==false){
                                                        mesaj="Okulumuz öğrencilerinden "+ogrenci.getAdSoyad()+" bugün "+dersadi+" dersine girmedi.";
                                                    }
                                                }
                                            }
                                        }else if(pageIndex==1){
                                            if(radioButtonOkula.isChecked()){
                                                if(durumIsim==true && durumBrans==true && durumOkuladi==true){
                                                    mesaj=kisiselBilgiler.get(2)+" öğrencilerinden "+ogrenci.getAdSoyad()+" bugün kursa gelmedi. ["+kisiselBilgiler.get(0)+" ("+kisiselBilgiler.get(1)+ " öğretmeni)]";
                                                }else if(durumIsim==true && durumBrans==true && durumOkuladi==false){
                                                    mesaj="Okulumuz öğrencilerinden "+ogrenci.getAdSoyad()+" bugün kursa gelmedi. ["+kisiselBilgiler.get(0)+" ("+kisiselBilgiler.get(1)+ " öğretmeni)]";
                                                }else if(durumIsim==true && durumBrans==false && durumOkuladi==true){
                                                    mesaj=kisiselBilgiler.get(2)+" öğrencilerinden "+ogrenci.getAdSoyad()+" bugün kursa gelmedi. ["+kisiselBilgiler.get(0)+"]";
                                                }else if(durumIsim==true && durumBrans==false && durumOkuladi==false){
                                                    mesaj="Okulumuz öğrencilerinden "+ogrenci.getAdSoyad()+" bugün kursa gelmedi. ["+kisiselBilgiler.get(0)+"]";
                                                }else if(durumIsim==false && durumBrans==true && durumOkuladi==true){
                                                    mesaj=kisiselBilgiler.get(2)+" öğrencilerinden "+ogrenci.getAdSoyad()+" bugün kursa gelmedi. ("+kisiselBilgiler.get(1)+ " öğretmeni)";
                                                }else if(durumIsim==false && durumBrans==true && durumOkuladi==false){
                                                    mesaj="Okulumuz öğrencilerinden "+ogrenci.getAdSoyad()+" bugün kursa gelmedi. ("+kisiselBilgiler.get(1)+ " öğretmeni)";
                                                }else if(durumIsim==false && durumBrans==false && durumOkuladi==true){
                                                    mesaj=kisiselBilgiler.get(2)+" öğrencilerinden "+ogrenci.getAdSoyad()+" bugün kursa gelmedi.";
                                                }else if(durumIsim==false && durumBrans==false && durumOkuladi==false){
                                                    mesaj="Okulumuz öğrencilerinden "+ogrenci.getAdSoyad()+" bugün kursa gelmedi.";
                                                }
                                            }else if(radioButtonDerse.isChecked()){
                                                String dersadi="";
                                                dersadi=spinnerDersler.getSelectedItem().toString();
                                                if(!dersadi.equals("Kayıtlı ders yok!")){
                                                    if(durumIsim==true && durumBrans==true && durumOkuladi==true){
                                                        mesaj=kisiselBilgiler.get(2)+" öğrencilerinden "+ogrenci.getAdSoyad()+" bugün "+dersadi+" kursuna gelmedi. ["+kisiselBilgiler.get(0)+" ("+kisiselBilgiler.get(1)+ " öğretmeni)]";
                                                    }else if(durumIsim==true && durumBrans==true && durumOkuladi==false){
                                                        mesaj="Okulumuz öğrencilerinden "+ogrenci.getAdSoyad()+" bugün "+dersadi+" kursuna gelmedi. ["+kisiselBilgiler.get(0)+" ("+kisiselBilgiler.get(1)+ " öğretmeni)]";
                                                    }else if(durumIsim==true && durumBrans==false && durumOkuladi==true){
                                                        mesaj=kisiselBilgiler.get(2)+" öğrencilerinden "+ogrenci.getAdSoyad()+" bugün "+dersadi+" kursuna gelmedi. ["+kisiselBilgiler.get(0)+"]";
                                                    }else if(durumIsim==true && durumBrans==false && durumOkuladi==false){
                                                        mesaj="Okulumuz öğrencilerinden "+ogrenci.getAdSoyad()+" bugün "+dersadi+" kursuna gelmedi. ["+kisiselBilgiler.get(0)+"]";
                                                    }else if(durumIsim==false && durumBrans==true && durumOkuladi==true){
                                                        mesaj=kisiselBilgiler.get(2)+" öğrencilerinden "+ogrenci.getAdSoyad()+" bugün "+dersadi+" kursuna gelmedi. ("+kisiselBilgiler.get(1)+ " öğretmeni)";
                                                    }else if(durumIsim==false && durumBrans==true && durumOkuladi==false){
                                                        mesaj="Okulumuz öğrencilerinden "+ogrenci.getAdSoyad()+" bugün "+dersadi+" kursuna gelmedi. ("+kisiselBilgiler.get(1)+ " öğretmeni)";
                                                    }else if(durumIsim==false && durumBrans==false && durumOkuladi==true){
                                                        mesaj=kisiselBilgiler.get(2)+" öğrencilerinden "+ogrenci.getAdSoyad()+" bugün "+dersadi+" kursuna gelmedi.";
                                                    }else if(durumIsim==false && durumBrans==false && durumOkuladi==false){
                                                        mesaj="Okulumuz öğrencilerinden "+ogrenci.getAdSoyad()+" bugün "+dersadi+" kursuna gelmedi.";
                                                    }
                                                }
                                            }
                                        }

                                        String dersadi="";
                                        if(radioButtonDerse.isChecked()){
                                            dersadi=spinnerDersler.getSelectedItem().toString();
                                        }
                                        if(radioButtonDerse.isChecked() && dersadi.equals("Kayıtlı ders yok!")){
                                            Toast.makeText(getApplicationContext(),"Kayıtlı ders yok!",Toast.LENGTH_SHORT).show();
                                        }else{
                                            SMSGonder.gonder(OgrenciListesi.this
                                                    , smsManager[0],ogrenci.getTelno(), mesaj,ogrenci.getAdSoyad());
                                        }
                                    }

                                    for(Ogrenci ogrenci:gecGelenler){
                                        String mesaj="";
                                        if(pageIndex==0){
                                            if(radioButtonOkula.isChecked()){
                                                if(durumIsim==true && durumBrans==true && durumOkuladi==true){
                                                    mesaj=kisiselBilgiler.get(2)+" öğrencilerinden "+ogrenci.getAdSoyad()+" bugün kurumumuza geç geldi. ["+kisiselBilgiler.get(0)+" ("+kisiselBilgiler.get(1)+ " öğretmeni)]";
                                                }else if(durumIsim==true && durumBrans==true && durumOkuladi==false){
                                                    mesaj="Okulumuz öğrencilerinden "+ogrenci.getAdSoyad()+" bugün kurumumuza geç geldi. ["+kisiselBilgiler.get(0)+" ("+kisiselBilgiler.get(1)+ " öğretmeni)]";
                                                }else if(durumIsim==true && durumBrans==false && durumOkuladi==true){
                                                    mesaj=kisiselBilgiler.get(2)+" öğrencilerinden "+ogrenci.getAdSoyad()+" bugün kurumumuza geç geldi. ["+kisiselBilgiler.get(0)+"]";
                                                }else if(durumIsim==true && durumBrans==false && durumOkuladi==false){
                                                    mesaj="Okulumuz öğrencilerinden "+ogrenci.getAdSoyad()+" bugün kurumumuza gelmedi. ["+kisiselBilgiler.get(0)+"]";
                                                }else if(durumIsim==false && durumBrans==true && durumOkuladi==true){
                                                    mesaj=kisiselBilgiler.get(2)+" öğrencilerinden "+ogrenci.getAdSoyad()+" bugün kurumumuza geç geldi. ("+kisiselBilgiler.get(1)+ " öğretmeni)";
                                                }else if(durumIsim==false && durumBrans==true && durumOkuladi==false){
                                                    mesaj="Okulumuz öğrencilerinden "+ogrenci.getAdSoyad()+" bugün kurumumuza geç geldi. ("+kisiselBilgiler.get(1)+ " öğretmeni)";
                                                }else if(durumIsim==false && durumBrans==false && durumOkuladi==true){
                                                    mesaj=kisiselBilgiler.get(2)+" öğrencilerinden "+ogrenci.getAdSoyad()+" bugün kurumumuza geç geldi.";
                                                }else if(durumIsim==false && durumBrans==false && durumOkuladi==false){
                                                    mesaj="Okulumuz öğrencilerinden "+ogrenci.getAdSoyad()+" bugün kurumumuza geç geldi.";
                                                }
                                            }else if(radioButtonDerse.isChecked()){
                                                String dersadi="";
                                                dersadi=spinnerDersler.getSelectedItem().toString();
                                                if(!dersadi.equals("Kayıtlı ders yok!")){
                                                    if(durumIsim==true && durumBrans==true && durumOkuladi==true){
                                                        mesaj=kisiselBilgiler.get(2)+" öğrencilerinden "+ogrenci.getAdSoyad()+" bugün "+dersadi+" dersine geç geldi. ["+kisiselBilgiler.get(0)+" ("+kisiselBilgiler.get(1)+ " öğretmeni)]";
                                                    }else if(durumIsim==true && durumBrans==true && durumOkuladi==false){
                                                        mesaj="Okulumuz öğrencilerinden "+ogrenci.getAdSoyad()+" bugün "+dersadi+" dersine geç geldi. ["+kisiselBilgiler.get(0)+" ("+kisiselBilgiler.get(1)+ " öğretmeni)]";
                                                    }else if(durumIsim==true && durumBrans==false && durumOkuladi==true){
                                                        mesaj=kisiselBilgiler.get(2)+" öğrencilerinden "+ogrenci.getAdSoyad()+" bugün "+dersadi+" dersine geç geldi. ["+kisiselBilgiler.get(0)+"]";
                                                    }else if(durumIsim==true && durumBrans==false && durumOkuladi==false){
                                                        mesaj="Okulumuz öğrencilerinden "+ogrenci.getAdSoyad()+" bugün "+dersadi+" dersine geç geldi. ["+kisiselBilgiler.get(0)+"]";
                                                    }else if(durumIsim==false && durumBrans==true && durumOkuladi==true){
                                                        mesaj=kisiselBilgiler.get(2)+" öğrencilerinden "+ogrenci.getAdSoyad()+" bugün "+dersadi+" dersine geç geldi. ("+kisiselBilgiler.get(1)+ " öğretmeni)";
                                                    }else if(durumIsim==false && durumBrans==true && durumOkuladi==false){
                                                        mesaj="Okulumuz öğrencilerinden "+ogrenci.getAdSoyad()+" bugün "+dersadi+" dersine geç geldi. ("+kisiselBilgiler.get(1)+ " öğretmeni)";
                                                    }else if(durumIsim==false && durumBrans==false && durumOkuladi==true){
                                                        mesaj=kisiselBilgiler.get(2)+" öğrencilerinden "+ogrenci.getAdSoyad()+" bugün "+dersadi+" dersine geç geldi.";
                                                    }else if(durumIsim==false && durumBrans==false && durumOkuladi==false){
                                                        mesaj="Okulumuz öğrencilerinden "+ogrenci.getAdSoyad()+" bugün "+dersadi+" dersine geç geldi.";
                                                    }
                                                }
                                            }
                                        }else if(pageIndex==1){
                                            if(radioButtonOkula.isChecked()){
                                                if(durumIsim==true && durumBrans==true && durumOkuladi==true){
                                                    mesaj=kisiselBilgiler.get(2)+" öğrencilerinden "+ogrenci.getAdSoyad()+" bugün kursa geç geldi. ["+kisiselBilgiler.get(0)+" ("+kisiselBilgiler.get(1)+ " öğretmeni)]";
                                                }else if(durumIsim==true && durumBrans==true && durumOkuladi==false){
                                                    mesaj="Okulumuz öğrencilerinden "+ogrenci.getAdSoyad()+" bugün kursa geç geldi. ["+kisiselBilgiler.get(0)+" ("+kisiselBilgiler.get(1)+ " öğretmeni)]";
                                                }else if(durumIsim==true && durumBrans==false && durumOkuladi==true){
                                                    mesaj=kisiselBilgiler.get(2)+" öğrencilerinden "+ogrenci.getAdSoyad()+" bugün kursa geç geldi. ["+kisiselBilgiler.get(0)+"]";
                                                }else if(durumIsim==true && durumBrans==false && durumOkuladi==false){
                                                    mesaj="Okulumuz öğrencilerinden "+ogrenci.getAdSoyad()+" bugün kursa gelmedi. ["+kisiselBilgiler.get(0)+"]";
                                                }else if(durumIsim==false && durumBrans==true && durumOkuladi==true){
                                                    mesaj=kisiselBilgiler.get(2)+" öğrencilerinden "+ogrenci.getAdSoyad()+" bugün kursa geç geldi. ("+kisiselBilgiler.get(1)+ " öğretmeni)";
                                                }else if(durumIsim==false && durumBrans==true && durumOkuladi==false){
                                                    mesaj="Okulumuz öğrencilerinden "+ogrenci.getAdSoyad()+" bugün kursa geç geldi. ("+kisiselBilgiler.get(1)+ " öğretmeni)";
                                                }else if(durumIsim==false && durumBrans==false && durumOkuladi==true){
                                                    mesaj=kisiselBilgiler.get(2)+" öğrencilerinden "+ogrenci.getAdSoyad()+" bugün kursa geç geldi.";
                                                }else if(durumIsim==false && durumBrans==false && durumOkuladi==false){
                                                    mesaj="Okulumuz öğrencilerinden "+ogrenci.getAdSoyad()+" bugün kursa geç geldi.";
                                                }
                                            }else if(radioButtonDerse.isChecked()){
                                                String dersadi="";
                                                dersadi=spinnerDersler.getSelectedItem().toString();
                                                if(!dersadi.equals("Kayıtlı ders yok!")){
                                                    if(durumIsim==true && durumBrans==true && durumOkuladi==true){
                                                        mesaj=kisiselBilgiler.get(2)+" öğrencilerinden "+ogrenci.getAdSoyad()+" bugün "+dersadi+" kursuna geç geldi. ["+kisiselBilgiler.get(0)+" ("+kisiselBilgiler.get(1)+ " öğretmeni)]";
                                                    }else if(durumIsim==true && durumBrans==true && durumOkuladi==false){
                                                        mesaj="Okulumuz öğrencilerinden "+ogrenci.getAdSoyad()+" bugün "+dersadi+" kursuna geç geldi. ["+kisiselBilgiler.get(0)+" ("+kisiselBilgiler.get(1)+ " öğretmeni)]";
                                                    }else if(durumIsim==true && durumBrans==false && durumOkuladi==true){
                                                        mesaj=kisiselBilgiler.get(2)+" öğrencilerinden "+ogrenci.getAdSoyad()+" bugün "+dersadi+" kursuna geç geldi. ["+kisiselBilgiler.get(0)+"]";
                                                    }else if(durumIsim==true && durumBrans==false && durumOkuladi==false){
                                                        mesaj="Okulumuz öğrencilerinden "+ogrenci.getAdSoyad()+" bugün "+dersadi+" kursuna geç geldi. ["+kisiselBilgiler.get(0)+"]";
                                                    }else if(durumIsim==false && durumBrans==true && durumOkuladi==true){
                                                        mesaj=kisiselBilgiler.get(2)+" öğrencilerinden "+ogrenci.getAdSoyad()+" bugün "+dersadi+" kursuna geç geldi. ("+kisiselBilgiler.get(1)+ " öğretmeni)";
                                                    }else if(durumIsim==false && durumBrans==true && durumOkuladi==false){
                                                        mesaj="Okulumuz öğrencilerinden "+ogrenci.getAdSoyad()+" bugün "+dersadi+" kursuna geç geldi. ("+kisiselBilgiler.get(1)+ " öğretmeni)";
                                                    }else if(durumIsim==false && durumBrans==false && durumOkuladi==true){
                                                        mesaj=kisiselBilgiler.get(2)+" öğrencilerinden "+ogrenci.getAdSoyad()+" bugün "+dersadi+" kursuna geç geldi.";
                                                    }else if(durumIsim==false && durumBrans==false && durumOkuladi==false){
                                                        mesaj="Okulumuz öğrencilerinden "+ogrenci.getAdSoyad()+" bugün "+dersadi+" kursuna geç geldi.";
                                                    }
                                                }
                                            }
                                        }

                                        String dersadi="";
                                        if(radioButtonDerse.isChecked()){
                                            dersadi=spinnerDersler.getSelectedItem().toString();
                                        }
                                        if(radioButtonDerse.isChecked() && dersadi.equals("Kayıtlı ders yok!")){
                                            Toast.makeText(getApplicationContext(),"Kayıtlı ders yok!",Toast.LENGTH_SHORT).show();
                                        }else{
                                            SMSGonder.gonder(OgrenciListesi.this
                                                    , smsManager[0],ogrenci.getTelno(), mesaj,ogrenci.getAdSoyad());
                                        }
                                    }
                                    String dersadi="";
                                    if(radioButtonDerse.isChecked()){
                                        dersadi=spinnerDersler.getSelectedItem().toString();
                                    }
                                    if(radioButtonDerse.isChecked() && dersadi.equals("Kayıtlı ders yok!")){ }
                                    else{
                                        dialog1.dismiss();
                                    }
                                    //editTextSearch.setText("");
                                }
                            });

                            buttonIptal.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog1.dismiss();
                                }
                            });
                        }
                    }
                } else {
                    List<Integer> birDahaSormaSayisi = new ArrayList<>();
                    for (String izin : izinler) {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(OgrenciListesi.this, izin)) {
                            birDahaSormaSayisi.add(1);
                            ActivityCompat.requestPermissions(OgrenciListesi.this, new String[]{izin}, requestCodePermission);
                        }
                    }
                    if (birDahaSormaSayisi.size()==0) {
                        AlertDialog.Builder builder=new AlertDialog.Builder(OgrenciListesi.this);
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
            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuButtonsVisibilityFirst();

                if(sinifadi.equals("Tüm Öğrenciler")){
                    for(Ogrenci ogrenci:adapterForKayıtlıOgr.ogrenciList){
                        ogrenci.setChecked(false);
                    }
                    adapterTumOgr =new AdapterTumOgr(getApplicationContext(),ogrenciList);
                    listViewOgrList.setAdapter(adapterTumOgr);
                    editTextSearch.setText("");
                }else{
                    for(Ogrenci ogrenci:adapterForOgrListOneCheck.ogrenciList){
                        ogrenci.setChecked(false);
                    }
                    adapterForOgrList=new AdapterForOgrList(getApplicationContext(),ogrenciList);
                    listViewOgrList.setAdapter(adapterForOgrList);
                    editTextSearch.setText("");
                }

                hideKeyboard(OgrenciListesi.this);
            }
        });

        listViewOgrList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                menuButtonsVisibilitySecond();
                if(editTextSearch.getText().toString().trim().equals("")){
                    if(sinifadi.equals("Tüm Öğrenciler")){
                        adapterForKayıtlıOgr=new AdapterForKayıtlıOgr(getApplicationContext(),ogrenciList);
                        adapterForKayıtlıOgr.ogrenciList.get(position).setChecked(true);
                        listViewOgrList.setAdapter(adapterForKayıtlıOgr);
                        listViewOgrList.setSelection(position);
                    }else{
                        adapterForOgrListOneCheck=new AdapterForOgrListOneCheck(getApplicationContext(),ogrenciList);
                        adapterForOgrListOneCheck.ogrenciList.get(position).setChecked(true);
                        listViewOgrList.setAdapter(adapterForOgrListOneCheck);
                        listViewOgrList.setSelection(position);
                    }

                }else{
                    if(sinifadi.equals("Tüm Öğrenciler")){
                        adapterForKayıtlıOgr=new AdapterForKayıtlıOgr(getApplicationContext(),filterList);
                        adapterForKayıtlıOgr.ogrenciList.get(position).setChecked(true);
                        listViewOgrList.setAdapter(adapterForKayıtlıOgr);
                        listViewOgrList.setSelection(position);
                    }else{
                        adapterForOgrListOneCheck=new AdapterForOgrListOneCheck(getApplicationContext(),filterList);
                        adapterForOgrListOneCheck.ogrenciList.get(position).setChecked(true);
                        listViewOgrList.setAdapter(adapterForOgrListOneCheck);
                        listViewOgrList.setSelection(position);
                    }
                }
                return true;
            }
        });

        TextView textViewTitle=(TextView)findViewById(R.id.textViewTitleOgrLis);
        textViewTitle.setText(sinifadi+" Öğrenci Listesi");

        checkBoxTumSec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sinifadi.equals("Tüm Öğrenciler")){
                    for(Ogrenci ogrenci:adapterForKayıtlıOgr.ogrenciList){
                        ogrenci.setChecked(true);
                    }
                    checkBoxTumSec.setVisibility(View.INVISIBLE);
                    checkBoxSecimiKaldir.setVisibility(View.VISIBLE);
                    checkBoxSecimiKaldir.setChecked(true);
                    listViewOgrList.setAdapter(adapterForKayıtlıOgr);
                }else{
                    for(Ogrenci ogrenci:adapterForOgrListOneCheck.ogrenciList){
                        ogrenci.setChecked(true);
                    }
                    checkBoxTumSec.setVisibility(View.INVISIBLE);
                    checkBoxSecimiKaldir.setVisibility(View.VISIBLE);
                    checkBoxSecimiKaldir.setChecked(true);
                    listViewOgrList.setAdapter(adapterForOgrListOneCheck);
                }
            }
        });

        checkBoxSecimiKaldir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sinifadi.equals("Tüm Öğrenciler")){
                    for(Ogrenci ogrenci:adapterForKayıtlıOgr.ogrenciList){
                        ogrenci.setChecked(false);
                    }
                    checkBoxTumSec.setVisibility(View.VISIBLE);
                    checkBoxSecimiKaldir.setVisibility(View.INVISIBLE);
                    checkBoxTumSec.setChecked(false);
                    listViewOgrList.setAdapter(adapterForKayıtlıOgr);
                }else{
                    for(Ogrenci ogrenci:adapterForOgrListOneCheck.ogrenciList){
                        ogrenci.setChecked(false);
                    }
                    checkBoxTumSec.setVisibility(View.VISIBLE);
                    checkBoxSecimiKaldir.setVisibility(View.INVISIBLE);
                    checkBoxTumSec.setChecked(false);
                    listViewOgrList.setAdapter(adapterForOgrListOneCheck);
                }
            }
        });
    }

    private void listele(){
        Veritabani vt=new Veritabani(getApplicationContext());
        List<Ogrenci> ogrencis=new ArrayList<>();
        ogrencis=vt.getirOgrenci();
        vt.close();

        ogrenciList=new ArrayList<>();
        if(sinifadi.equals("Tüm Öğrenciler")){
            buttonAdd.setVisibility(View.INVISIBLE);
            if(pageIndex==0){
                for(Ogrenci ogrenci:ogrencis) {
                    if(!ogrenci.getSinif().contains("(Kurs)")){
                        ogrenciList.add(ogrenci);
                    }
                }
            }else if(pageIndex==1){
                for(Ogrenci ogrenci:ogrencis) {
                    if(ogrenci.getSinif().contains("(Kurs)")){
                        ogrenciList.add(ogrenci);
                    }
                }
            }

            if(ogrenciList.size()>0) {
                adapterTumOgr = new AdapterTumOgr(getApplicationContext(), ogrenciList);
                listViewOgrList.setAdapter(adapterTumOgr);
            }else{
                listViewOgrList.setAdapter(null);
            }
        }else{
            buttonAdd.setVisibility(View.VISIBLE);
            for(Ogrenci ogrenci:ogrencis){
                if(sinifadi.equals(ogrenci.getSinif())){
                    ogrenciList.add(ogrenci);
                }
            }

            if(ogrenciList.size()>0) {
                adapterForOgrList = new AdapterForOgrList(getApplicationContext(), ogrenciList);
                listViewOgrList.setAdapter(adapterForOgrList);
            }else{
                listViewOgrList.setAdapter(null);
            }
        }

        mevcut.setText("Mevcut: "+String.valueOf(ogrenciList.size()));
       // hideKeyboard(OgrenciListesi.this);
    }

    @Override
    public void openOgrenciEkleme(String sinifadi) {
        FragmentManager manager=getSupportFragmentManager();
        OgrenciEkleme ogrenciEkleme=new OgrenciEkleme();
        //ogrenciEkleme.setCancelable(false);
        ogrenciEkleme.show(manager,"Öğrenci Ekleme");
        ogrenciEkleme.gelenSinif(sinifadi);
    }

    @Override
    public void eklenenOgrenci() {
        listele();
    }

    @Override
    public void guncellenenOgrenci() {
        listele();
        menuButtonsVisibilityFirst();
    }

    @Override
    public void guncellenecekOgrenci(Ogrenci ogrenci) {
        FragmentManager manager=getSupportFragmentManager();
        OgrenciGuncelle ogrenciGuncelle=new OgrenciGuncelle();
        //ogrenciGuncelle.setCancelable(false);
        ogrenciGuncelle.show(manager,"Öğrenci Güncelle");
        ogrenciGuncelle.guncellenecekOgrenci(ogrenci);
    }

    @Override
    public void ozelSMSGonderilecler(List<Ogrenci> ogrenciList) {
        FragmentManager manager=getSupportFragmentManager();
        MultiSpecialSms multiSpecialSms=new MultiSpecialSms();
        multiSpecialSms.setCancelable(false);
        multiSpecialSms.show(manager,"Multi Special SMS");
        multiSpecialSms.gelenList(ogrenciList);
    }

    private void menuButtonsVisibilityFirst(){
        mevcut.setVisibility(View.VISIBLE);
        textViewTitle.setVisibility(View.VISIBLE);
        buttonSMS.setVisibility(View.VISIBLE);
        buttonSave.setVisibility(View.VISIBLE);
        buttonAdd.setVisibility(View.VISIBLE);
        checkBoxSecimiKaldir.setVisibility(View.INVISIBLE);
        checkBoxTumSec.setVisibility(View.INVISIBLE);
        buttonCall.setVisibility(View.INVISIBLE);
        buttonSpecialSMS.setVisibility(View.INVISIBLE);
        buttonCancel.setVisibility(View.INVISIBLE);
        buttonEdit.setVisibility(View.INVISIBLE);
        buttonDelete.setVisibility(View.INVISIBLE);
        buttonOtoSMS2.setVisibility(View.INVISIBLE);
    }

    private void menuButtonsVisibilitySecond(){
        mevcut.setVisibility(View.INVISIBLE);
        textViewTitle.setVisibility(View.INVISIBLE);
        buttonSMS.setVisibility(View.INVISIBLE);
        buttonSave.setVisibility(View.INVISIBLE);
        buttonAdd.setVisibility(View.INVISIBLE);
        checkBoxTumSec.setVisibility(View.VISIBLE);
        buttonCall.setVisibility(View.VISIBLE);
        buttonSpecialSMS.setVisibility(View.VISIBLE);
        buttonCancel.setVisibility(View.VISIBLE);
        buttonEdit.setVisibility(View.VISIBLE);
        buttonDelete.setVisibility(View.VISIBLE);
        buttonOtoSMS2.setVisibility(View.VISIBLE);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent ıntent=new Intent(getApplicationContext(),Siniflar.class);
        setResult(1000,ıntent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==2005){
            sinifadi=data.getStringExtra("kayitliOgrencilerSinif");
            listele();
        }
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        izinVarMi=checkPermission(getApplicationContext(),izinler);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==requestCodePermission){
            izinVarMi=checkPermission(getApplicationContext(),izinler);
        }
    }

    private void menuButtonsVisibility2(){
        fm.beginTransaction().show(menuContentFragment).commit();
        buttonMenuOpen.setVisibility(View.INVISIBLE);
        buttonMenuClose.setVisibility(View.VISIBLE);
    }

    private void menuButtonsVisibility1(){
        fm.beginTransaction().hide(menuContentFragment).commit();
        buttonMenuOpen.setVisibility(View.VISIBLE);
        buttonMenuClose.setVisibility(View.INVISIBLE);
    }

    @Override
    public void menuButtonsVisibility() {
        menuButtonsVisibility1();
    }
}
