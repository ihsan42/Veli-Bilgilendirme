package com.egitimyazilim.iletisim.mesajlarvelibilgilendirme;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    List<Ogrenci> ogrenciList;
    Button buttonSinif;
    Button buttonEOkul;
    Button buttonYokKayit;
    Button buttonVideo;
    Button buttonAyar;
    Button buttonYazililar;
    Button buttonMesajKutusu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonSinif=(Button)findViewById(R.id.buttonSinif);
        buttonEOkul=(Button)findViewById(R.id.buttonEOkul);
        buttonYokKayit=(Button)findViewById(R.id.buttonYokKayit);
        buttonVideo=(Button)findViewById(R.id.buttonVideo);
        buttonAyar=(Button)findViewById(R.id.buttonAyar);
        buttonYazililar=(Button)findViewById(R.id.buttonYazililar);
        buttonMesajKutusu=(Button)findViewById(R.id.buttonMesajKutusu);

        ActionBar bar=getSupportActionBar();
        bar.hide();

        buttonYazililar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ıntent=new Intent(getApplicationContext(),Yazililar.class);
                startActivity(ıntent);
            }
        });

        buttonSinif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ıntent=new Intent(getApplicationContext(),Siniflar.class);
                startActivity(ıntent);
            }
        });

        buttonEOkul.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://e-okul.meb.gov.tr/logineOkul.aspx"));
                startActivity(browserIntent);
            }
        });

        buttonYokKayit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Intent ıntent=new Intent(getApplicationContext(),YoklamaKayitlari.class);
                    startActivity(ıntent);
            }
        });

        buttonVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager=getSupportFragmentManager();
                Videolar videolar=new Videolar();
                videolar.show(manager,"Videolar");
            }
        });

        buttonAyar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ıntent=new Intent(getApplicationContext(),Ayarlar.class);
                startActivity(ıntent);
            }
        });

        buttonMesajKutusu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ıntent=new Intent(getApplicationContext(),MesajKutusu.class);
                startActivity(ıntent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
        finishAffinity();
    }

}
