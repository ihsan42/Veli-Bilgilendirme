package com.egitimyazilim.iletisim.mesajlarvelibilgilendirme.fragments;

import static android.content.Context.MODE_PRIVATE;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;

import android.Manifest;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;

import com.egitimyazilim.iletisim.mesajlarvelibilgilendirme.R;
import com.egitimyazilim.iletisim.mesajlarvelibilgilendirme.SMSGonder;
import com.egitimyazilim.iletisim.mesajlarvelibilgilendirme.Veritabani;
import com.egitimyazilim.iletisim.mesajlarvelibilgilendirme.adapters.AdapterForYokamaSilme;
import com.egitimyazilim.iletisim.mesajlarvelibilgilendirme.interfaces.CommYoklama;
import com.egitimyazilim.iletisim.mesajlarvelibilgilendirme.object_classes.Ogrenci;
import com.egitimyazilim.iletisim.mesajlarvelibilgilendirme.utils.Izinler;

import java.util.ArrayList;
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

        ActivityCompat.requestPermissions(getActivity(), izinler, requestCodePermission);

        buttonVeliBilgilendir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Izinler.checkPermission(getActivity(), izinler)) {
                    Izinler.showRequestPermissionDialog(getActivity(),izinler,requestCodePermission);
                } else if(yoklamaList.size()==0){
                    Toast.makeText(getActivity(),"Kayıt Yok!",Toast.LENGTH_SHORT).show();
                }else {
                    secilenler = new ArrayList<>();
                    for (Ogrenci ogrenci : adapterForYokamaSilme.ogrenciList) {
                        if (ogrenci.getChecked() == true) {
                            secilenler.add(ogrenci);
                        }
                    }

                    if (secilenler.size() == 0) {
                        Toast.makeText(getActivity(), "Seçilen Yok!", Toast.LENGTH_SHORT).show();

                    } else {
                        tarihler = "";
                        String y = "yok";
                        String g = "geç";
                        String r = "raporlu";
                        String i = "izinli";
                        for (Ogrenci ogrenci1 : secilenler) {
                            if (ogrenci1.getDurum().equals(y)) {
                                if (ogrenci1.getSinif().contains("(Kurs)")) {
                                    tarihler += ogrenci1.getTarih() + " tarihinde kursa gelmedi." + "\n";
                                } else {
                                    tarihler += ogrenci1.getTarih() + " tarihinde okula gelmedi." + "\n";
                                }
                            }
                            if (ogrenci1.getDurum().equals(g)) {
                                if (ogrenci1.getSinif().contains("(Kurs)")) {
                                    tarihler += ogrenci1.getTarih() + " tarihinde kursa geç geldi." + "\n";
                                } else {
                                    tarihler += ogrenci1.getTarih() + " tarihinde okula geç geldi." + "\n";
                                }
                            }
                            if (ogrenci1.getDurum().equals(r)) {
                                tarihler += ogrenci1.getTarih() + " tarihinde raporludur." + "\n";
                            }
                            if (ogrenci1.getDurum().equals(i)) {
                                if (ogrenci1.getSinif().contains("(Kurs)")) {
                                    tarihler += ogrenci1.getTarih() + " tarihinde kurs için izinlidir." + "\n";
                                } else {
                                    tarihler += ogrenci1.getTarih() + " tarihinde izinlidir." + "\n";
                                }
                            }
                        }
                        SmsManager[] smsManager = {SmsManager.getDefault()};

                        SharedPreferences sharedPref = getActivity().getSharedPreferences("Kisisel Ayarlar", MODE_PRIVATE);
                        boolean durumIsim = sharedPref.getBoolean("isim", false);
                        boolean durumBrans = sharedPref.getBoolean("brans", false);
                        boolean durumOkuladi = sharedPref.getBoolean("okuladi", false);
                        Veritabani vt = new Veritabani(getActivity());
                        List<String> kisiselBilgiler = new ArrayList<>();
                        kisiselBilgiler = vt.kisiselBilgileriGetir();

                        String mesaj="";
                        if (durumIsim == true && durumBrans == true && durumOkuladi == true) {
                            mesaj = kisiselBilgiler.get(2) + " öğrencilerinden " + gelenAdAsoyad +" "+tarihler+" [" + kisiselBilgiler.get(0) + " (" + kisiselBilgiler.get(1) + " öğretmeni)]";
                        } else if (durumIsim == true && durumBrans == true && durumOkuladi == false) {
                            mesaj = "Okulumuz öğrencilerinden " + gelenAdAsoyad + " "+tarihler +" [" + kisiselBilgiler.get(0) + " (" + kisiselBilgiler.get(1) + " öğretmeni)]";
                        } else if (durumIsim == true && durumBrans == false && durumOkuladi == true) {
                            mesaj = kisiselBilgiler.get(2) + " öğrencilerinden " + gelenAdAsoyad +" "+tarihler+ " [" + kisiselBilgiler.get(0) + "]";
                        } else if (durumIsim == true && durumBrans == false && durumOkuladi == false) {
                            mesaj = "Okulumuz öğrencilerinden " + gelenAdAsoyad +" "+tarihler+ " [" + kisiselBilgiler.get(0) + "]";
                        } else if (durumIsim == false && durumBrans == true && durumOkuladi == true) {
                            mesaj = kisiselBilgiler.get(2) + " öğrencilerinden " + gelenAdAsoyad +" "+tarihler+ " (" + kisiselBilgiler.get(1) + " öğretmeni)";
                        } else if (durumIsim == false && durumBrans == true && durumOkuladi == false) {
                            mesaj = "Okulumuz öğrencilerinden " + gelenAdAsoyad +" "+tarihler+ " (" + kisiselBilgiler.get(1) + " öğretmeni)";
                        } else if (durumIsim == false && durumBrans == false && durumOkuladi == true) {
                            mesaj = kisiselBilgiler.get(2) + " öğrencilerinden " + gelenAdAsoyad +" "+ tarihler;
                        } else if (durumIsim == false && durumBrans == false && durumOkuladi == false) {
                            mesaj = "Okulumuz öğrencilerinden " + gelenAdAsoyad +" "+tarihler;
                        }

                        AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
                        builder1.setTitle("SMS gönderilsin mi?");
                        builder1.setMessage(mesaj);
                        String finalMesaj = mesaj;
                        builder1.setPositiveButton("Gönder", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                SMSGonder.gonder(getActivity(), smsManager[0], gelenTelNo, finalMesaj, gelenAdAsoyad);
                                dialog.dismiss();
                            }
                        });
                        builder1.setNegativeButton("İptal", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        AlertDialog alertDialog = builder1.create();
                        alertDialog.show();

                        if (SMSGonder.isDualSimAvailable(getActivity())) {
                            SMSGonder.getDefaultSMSManeger(getActivity(), smsManager);
                        }
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
                                        long id=vt.ogrenciYoklamaSil(gelenSinifAdi,gelenOkulNo,ogrenci.getTarih());
                                        if (id>-1){
                                            Toast.makeText(getActivity(),ogrenci.getTarih()+" silindi",Toast.LENGTH_SHORT).show();
                                        }else{
                                            Toast.makeText(getActivity(),"Hata! "+ogrenci.getTarih()+" silinemedi",Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }

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
}
