package com.egitimyazilim.iletisim.mesajlarvelibilgilendirme;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.itextpdf.text.pdf.fonts.otf.FontReadingException;

import java.util.ArrayList;
import java.util.List;

public class KayitliOgrenciler extends AppCompatActivity {

    AdapterForKayıtlıOgr adapterForKayıtlıOgr;
    List<Ogrenci> ogrenciList;
    List<Ogrenci> filterList=new ArrayList<>();
    String sinifadi="";
    ListView listView;
    Button buttonOk;
    Button buttonCancel;
    TextView textViewBilgi;
    Spinner spinner;
    List<String> sinifList;
    List<String> kursSinifList;
    List<String> tumSinifList;
    EditText editTextSearch;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kayitli_ogrenciler);

        spinner=(Spinner)findViewById(R.id.spinnerKayitliOgr);
        listView=(ListView)findViewById(R.id.listViewKayitliOgr);
        buttonOk=(Button)findViewById(R.id.buttonKaytOgrOk);
        buttonCancel=(Button)findViewById(R.id.buttonKaytOgrCancel);
        textViewBilgi=(TextView)findViewById(R.id.textViewKaytOgr);
        editTextSearch=(EditText)findViewById(R.id.editTextKayitliOgrArama);

        ActionBar bar=getSupportActionBar();
        bar.hide();

        Intent ıntent=getIntent();
        sinifadi=ıntent.getStringExtra("sinifadi");

        Veritabani vt=new Veritabani(getApplicationContext());
        sinifList=new ArrayList<>();
        kursSinifList=new ArrayList<>();
        sinifList=vt.getirSinif();
        kursSinifList=vt.getirKursSinif();
        vt.close();

        tumSinifList=new ArrayList<>();
        tumSinifList.add("Tüm Öğrenciler");
        for(String s:sinifList){
            tumSinifList.add(s);
        }
        for(String s:kursSinifList){
            tumSinifList.add(s);
        }

        if(tumSinifList.size()>0){
            ArrayAdapter<String> adapter=new ArrayAdapter<>(getApplicationContext(),R.layout.spinner_custom_item,tumSinifList);
            spinner.setAdapter(adapter);
        }else{
            spinner.setAdapter(null);
        }
        String secilen="";
        secilen=spinner.getSelectedItem().toString();
        ogrenciList=new ArrayList<>();
        ogrenciList=listele(secilen);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String secilen="";
                secilen=spinner.getSelectedItem().toString();
                ogrenciList=new ArrayList<>();
                ogrenciList=listele(secilen);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                textViewBilgi.setText("Kayıtlı sınıf yok!");
            }
        });

        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                filterList = new ArrayList<>();

                if (!s.equals("")) {
                    for (int i = 0; i < ogrenciList.size(); i++) {
                        if (ogrenciList.get(i).getAdSoyad().toLowerCase().contains(s.toString().toLowerCase())) {
                            filterList.add(ogrenciList.get(i));
                        }
                    }
                }

                adapterForKayıtlıOgr = new AdapterForKayıtlıOgr(getApplicationContext(), filterList);
                listView.setAdapter(adapterForKayıtlıOgr);

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(adapterForKayıtlıOgr.ogrenciList.size()==0){
                    Toast.makeText(getApplicationContext(),"Kayıtlı öğrenci yok!",Toast.LENGTH_SHORT).show();
                }else{
                    List<Ogrenci> secilenler=new ArrayList<>();
                    if(adapterForKayıtlıOgr.ogrenciList.size()!=0){
                        for(Ogrenci ogrenci:adapterForKayıtlıOgr.ogrenciList){
                            if(ogrenci.getChecked()==true){
                                secilenler.add(ogrenci);
                            }
                        }
                    }

                    if(secilenler.size()==0){
                        Toast.makeText(getApplicationContext(),"Seçilen Yok",Toast.LENGTH_SHORT).show();

                    }else{
                        Veritabani vt=new Veritabani(getApplicationContext());
                        List<Ogrenci> tumKayitlilar=new ArrayList<>();
                        tumKayitlilar=vt.getirOgrenci();
                        vt.close();

                        List<String> kaydedilenler=new ArrayList<>();
                        List<String> zatenKayitliOlanlar=new ArrayList<>();

                        for(Ogrenci secilenOgrenci:secilenler){
                            secilenOgrenci.setSinif(sinifadi);

                            boolean durum=false;
                            for(Ogrenci kayitliOgrenci:tumKayitlilar){
                                if(secilenOgrenci.getSinif().equals(kayitliOgrenci.getSinif())&&secilenOgrenci.getOkulno().equals(kayitliOgrenci.getOkulno())){
                                    durum=true;
                                }
                            }
                            if(durum==false){
                                 vt=new Veritabani(getApplicationContext());
                                long id=vt.ekleOgrenci(secilenOgrenci);
                                if(id>0){
                                   kaydedilenler.add(secilenOgrenci.getAdSoyad());
                                }else{
                                    Toast.makeText(getApplicationContext(),secilenOgrenci.getAdSoyad()+" eklenirken hata oluştu!",Toast.LENGTH_SHORT).show();
                                }
                            }else{
                                zatenKayitliOlanlar.add(secilenOgrenci.getAdSoyad());
                            }
                        }
                        editTextSearch.setText("");
                        //hideKeyboard(KayitliOgrenciler.this);
                        if(kaydedilenler.size()>0){
                            String mesaj="EKLENENLER\n";
                            for(String s:kaydedilenler){
                                mesaj+=s+"\n";
                            }
                            Toast.makeText(getApplicationContext(),mesaj,Toast.LENGTH_SHORT).show();
                        }
                        if(zatenKayitliOlanlar.size()>0){
                            String mesaj="ÖNCEDEN EKLENMİŞ OLANLAR\n";
                            for(String s:zatenKayitliOlanlar){
                                mesaj+=s+"\n";
                            }
                            Toast.makeText(getApplicationContext(),mesaj,Toast.LENGTH_SHORT).show();
                        }}
                }
            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ıntent1=new Intent(getApplicationContext(),OgrenciListesi.class);
                ıntent1.putExtra("kayitliOgrencilerSinif",sinifadi);
                setResult(2005,ıntent1);
                hideKeyboard(KayitliOgrenciler.this);
                finish();
            }
        });
    }

    private List<Ogrenci> listele(String sinifadi) {
        List<Ogrenci> subeList=new ArrayList<>();
        if (sinifadi.equals("")) {

        } else {
            List<Ogrenci> ogrencis = new ArrayList<>();
            Veritabani vt = new Veritabani(getApplicationContext());
            ogrencis = vt.getirOgrenci();
            vt.close();

            if (spinner.getSelectedItemPosition() == 0) {
                for (Ogrenci ogrenci2 : ogrencis) {
                    subeList.add(ogrenci2);
                }
                adapterForKayıtlıOgr = new AdapterForKayıtlıOgr(KayitliOgrenciler.this, subeList);
                listView.setAdapter(adapterForKayıtlıOgr);
                ogrencis.clear();
            } else if (spinner.getSelectedItemPosition() > 0) {
                for (Ogrenci ogrenci2 : ogrencis) {
                    if (ogrenci2.getSinif().equals(sinifadi)) {

                        Ogrenci ogrenci = new Ogrenci();
                        ogrenci.setSinif(ogrenci2.getSinif());
                        ogrenci.setAdSoyad(ogrenci2.getAdSoyad());
                        ogrenci.setOkulno(ogrenci2.getOkulno());
                        ogrenci.setVeliAdi(ogrenci2.getVeliAdi());
                        ogrenci.setDurumYok(false);
                        ogrenci.setDurumGec(false);
                        ogrenci.setDurumRaporlu(false);
                        ogrenci.setDurumIzinli(false);
                        ogrenci.setTelno(ogrenci2.getTelno());
                        subeList.add(ogrenci);
                    }
                }
                if (subeList.size() > 0) {
                    adapterForKayıtlıOgr = new AdapterForKayıtlıOgr(KayitliOgrenciler.this, subeList);
                    listView.setAdapter(adapterForKayıtlıOgr);
                } else {
                    listView.setAdapter(null);
                }

            } else {
                listView.setAdapter(null);
            }
        }
        return subeList;
    }

    @Override
    public void onBackPressed() {
       // super.onBackPressed();
        Intent ıntent1=new Intent(getApplicationContext(),OgrenciListesi.class);
        ıntent1.putExtra("kayitliOgrencilerSinif",sinifadi);
        setResult(2005,ıntent1);
        finish();
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
}
