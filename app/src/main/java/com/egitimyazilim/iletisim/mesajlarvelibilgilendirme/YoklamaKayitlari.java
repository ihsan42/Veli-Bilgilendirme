package com.egitimyazilim.iletisim.mesajlarvelibilgilendirme;

import android.content.DialogInterface;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.egitimyazilim.iletisim.mesajlarvelibilgilendirme.adapters.AdapterForYoklama;
import com.egitimyazilim.iletisim.mesajlarvelibilgilendirme.fragments.GecmisYoklamaGirme;
import com.egitimyazilim.iletisim.mesajlarvelibilgilendirme.fragments.MenuContentFragment;
import com.egitimyazilim.iletisim.mesajlarvelibilgilendirme.fragments.YoklamaGecmisi;
import com.egitimyazilim.iletisim.mesajlarvelibilgilendirme.interfaces.CommYoklama;
import com.egitimyazilim.iletisim.mesajlarvelibilgilendirme.interfaces.MenuContentComm;
import com.egitimyazilim.iletisim.mesajlarvelibilgilendirme.object_classes.Ogrenci;
import com.egitimyazilim.iletisim.mesajlarvelibilgilendirme.object_classes.OgrenciForYoklama;

import java.util.ArrayList;
import java.util.List;

public class YoklamaKayitlari extends AppCompatActivity implements CommYoklama, MenuContentComm {

    FragmentManager fm;
    Button buttonMenuOpen;
    Button buttonMenuClose;
    MenuContentFragment menuContentFragment;
    Spinner spinner;
    ListView listView;
    List<String> sinifList;
    List<String> kursSinifList;
    List<String> tumSinifList;
    List<OgrenciForYoklama> subeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yoklama_kayitlari);

        spinner=(Spinner)findViewById(R.id.spinnerYokKayit);
        listView=(ListView)findViewById(R.id.listViewYokKayit);
        Button buttonTumSil=(Button)findViewById(R.id.buttonSilTum);

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
                menuButtonsVisibilitySecond();
            }
        });

        buttonMenuClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menuButtonsVisibilityFirst();
            }
        });

        Veritabani vt=new Veritabani(getApplicationContext());
        sinifList=new ArrayList<>();
        kursSinifList=new ArrayList<>();
        sinifList=vt.getirSinif();
        kursSinifList=vt.getirKursSinif();
        vt.close();

        tumSinifList=new ArrayList<>();
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

        buttonTumSil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tumSinifList.size()==0){
                    Toast.makeText(getApplicationContext(),"Kayıtlı sınıf yok!",Toast.LENGTH_SHORT).show();
                }else{
                    boolean kayitdurumu = false;
                    for (OgrenciForYoklama ogrenci : subeList) {
                        if (!ogrenci.getDurumYok().equals("0 gün Yok (0 gün Raporlu) (0 gün İzinli)")||!ogrenci.getDurumGec().equals("0 gün Geç")) {
                            kayitdurumu = true;
                        }
                    }

                    if (kayitdurumu == true) {
                        AlertDialog.Builder builder=new AlertDialog.Builder(YoklamaKayitlari.this);
                        builder.setTitle("DİKKAT!");
                        builder.setMessage("Bu sınıfa ait tüm kayıtlar silinecek. Yine de silmek istiyor musunuz?");
                        builder.setPositiveButton("Evet", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Veritabani vt=new Veritabani(getApplicationContext());
                                vt.sınıfYoklamaKayitSil(spinner.getSelectedItem().toString());
                                listele(spinner.getSelectedItem().toString());
                                Toast.makeText(getApplicationContext(),spinner.getSelectedItem().toString()+ " yoklama kayıtları silindi", Toast.LENGTH_SHORT).show();
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
                    else{
                        Toast.makeText(getApplicationContext(),"Silinecek kayıt yok!",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String secilen="";
                secilen=spinner.getSelectedItem().toString();
                listele(secilen);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,final int position, long id) {
                AlertDialog.Builder builder=new AlertDialog.Builder(YoklamaKayitlari.this);
                builder.setPositiveButton("Yoklama Gir", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String sinif=subeList.get(position).getSinif();
                        String okulno=subeList.get(position).getOkulno();
                        String adsoyad=subeList.get(position).getAdSoyad();
                        gecmisYoklamaGirmeyeGonder(sinif,okulno,adsoyad);
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
        });
    }

    private void listele(String sinifadi) {
        if(sinifadi.equals("")){

        }else{
            List<Ogrenci> ogrencis=new ArrayList<>();
            Veritabani vt=new Veritabani(getApplicationContext());
            ogrencis=vt.getirOgrenci();
            vt.close();

            subeList=new ArrayList<>();
            for(Ogrenci ogrenci:ogrencis){
                if(ogrenci.getSinif().equals(sinifadi)){
                    List<Ogrenci> ogrenciList=new ArrayList<>();
                    ogrenciList=vt.ogrenciYoklamaKaydiGetir(ogrenci.getSinif(),ogrenci.getOkulno());
                    int yok=0;
                    int gec=0;
                    int rapor=0;
                    int izin=0;
                    String y="yok";
                    String g="geç";
                    String r="raporlu";
                    String i="izinli";
                    for(Ogrenci ogrenci1:ogrenciList){
                        if(ogrenci1.getDurum().equals(y)){
                                yok++;
                        }
                        if(ogrenci1.getDurum().equals(g)){
                                gec++;
                        }
                        if(ogrenci1.getDurum().equals(r)){
                                rapor++;
                        }
                        if(ogrenci1.getDurum().equals(i)){
                                izin++;
                        }
                    }
                    OgrenciForYoklama ogrenciForYoklama=new OgrenciForYoklama();
                    ogrenciForYoklama.setSinif(ogrenci.getSinif());
                    ogrenciForYoklama.setAdSoyad(ogrenci.getAdSoyad());
                    ogrenciForYoklama.setOkulno(ogrenci.getOkulno());
                    ogrenciForYoklama.setDurumYok(String.valueOf(yok+rapor+izin)+" gün Yok ("+String.valueOf(rapor)+" gün Raporlu) ("+String.valueOf(izin)+" gün İzinli)");
                    ogrenciForYoklama.setDurumGec(String.valueOf(gec)+" gün Geç");
                    ogrenciForYoklama.setDurumRaporlu(String.valueOf(rapor)+" gün Raporlu");
                    ogrenciForYoklama.setDurumIzinli(String.valueOf(izin)+" gün İzinli");
                    ogrenciForYoklama.setTelno(ogrenci.getTelno());
                    subeList.add(ogrenciForYoklama);
                }
            }
            ogrencis.clear();

            if(subeList.size()>0){
                AdapterForYoklama adapterForYoklama=new AdapterForYoklama(YoklamaKayitlari.this,subeList);
                listView.setAdapter(adapterForYoklama);
            }else{
                listView.setAdapter(null);
            }
        }
    }

    @Override
    public void gecmisYoklamaGirmeyeGonder(String sinif,String okulno,String adsoyad) {
        FragmentManager manager=getSupportFragmentManager();
        GecmisYoklamaGirme gecmisYoklamaGirme=new GecmisYoklamaGirme();
        gecmisYoklamaGirme.show(manager,"Geçmiş Yoklama Girme");
        gecmisYoklamaGirme.gelenOgrenci(sinif,okulno,adsoyad);
    }

    @Override
    public void gecmisYoklamaOkOnClick(String sinif) {
        listele(sinif);
        spinner.setSelection(tumSinifList.indexOf(sinif));
    }

    @Override
    public void yoklamaKayitSilmeOk() {
        listele(spinner.getSelectedItem().toString());
    }

    @Override
    public void openYoklamaKayitSilme(String sinifadi, String okulno, String adsoyad, String telno) {
        FragmentManager manager=getSupportFragmentManager();
        YoklamaGecmisi yoklamaGecmisi =new YoklamaGecmisi();
        yoklamaGecmisi.show(manager,"Yoklama Kayıt Silme");
        yoklamaGecmisi.gelenOgrenci(sinifadi,okulno,adsoyad,telno);
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
    public void onBackPressed() {
        AlertDialog.Builder builder=new AlertDialog.Builder(YoklamaKayitlari.this);
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
