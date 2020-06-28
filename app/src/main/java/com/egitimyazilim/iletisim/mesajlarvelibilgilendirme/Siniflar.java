package com.egitimyazilim.iletisim.mesajlarvelibilgilendirme;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import androidx.annotation.Nullable;
import com.google.android.material.tabs.TabLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Siniflar extends AppCompatActivity implements CommSinif {

    ViewPager viewPager;
    TabPageAdapter tabPageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_siniflar);

        viewPager = (ViewPager) findViewById(R.id.pager);
        tabPageAdapter = new TabPageAdapter(getSupportFragmentManager());
        viewPager.setAdapter(tabPageAdapter);

        ActionBar bar = getSupportActionBar();
        bar.hide();

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);

        Button buttonAdd = (Button) findViewById(R.id.buttonSiniflarAdd);
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String[] depolamaIzni = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE};
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(Siniflar.this, depolamaIzni, 102);
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(Siniflar.this);
                builder.setPositiveButton("Elle", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sinifEklemeyiAc(viewPager.getCurrentItem());
                    }
                });

                builder.setNegativeButton("Excel'den", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                            Intent ıntent = new Intent(getApplicationContext(), ExceldenListeAl.class);
                            startActivity(ıntent);
                        } else {
                            if (ActivityCompat.shouldShowRequestPermissionRationale(Siniflar.this, depolamaIzni[0])) {
                                ActivityCompat.requestPermissions(Siniflar.this, depolamaIzni, 102);
                            } else {
                                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(Siniflar.this);
                                builder.setTitle("Dikkat!");
                                builder.setMessage("Cihazınızdaki Excel dosyasını okuyabilmek için eksik izin var. Depolama iznini vermeniz gereklidir. İzin vermek için <Ayarlar>'a tıklayınız ve açılan sayfadaki izinler bölümüden izin veriniz.");
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
                                android.app.AlertDialog alertDialog = builder.create();
                                alertDialog.show();
                            }
                        }
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
    }

    @Override
    public void guncellenecekSinif(String sinifadi) {
        FragmentManager manager=getSupportFragmentManager();
        SinifGuncelleme sinifGuncelleme=new SinifGuncelleme();
        sinifGuncelleme.show(manager,"Sinif Güncelleme");
        int index=viewPager.getCurrentItem();
        sinifGuncelleme.gelenSinif(sinifadi,index);
    }

    @Override
    public void sinifEklemeyiAc(int activePageIndex) {
        FragmentManager manager=getSupportFragmentManager();
        SinifEkleme sinifEkleme=new SinifEkleme();
        sinifEkleme.show(manager,"Sınıf Ekleme");
        sinifEkleme.gelenPageIndex(activePageIndex);

    }

    @Override
    public void kursSinifEklemeOkOnClick() {
        viewPager.setAdapter(tabPageAdapter);
        viewPager.setCurrentItem(1);
    }

    @Override
    public void normalSinifEklemeOkOnClick() {
        viewPager.setAdapter(tabPageAdapter);
        viewPager.setCurrentItem(0);
    }

    @Override
    public void kursSinifGuncellemeOkOnClick() {
        viewPager.setAdapter(tabPageAdapter);
        viewPager.setCurrentItem(1);
    }

    @Override
    public void normalSinifGuncellemeOkOnClick() {
        viewPager.setAdapter(tabPageAdapter);
        viewPager.setCurrentItem(0);
    }

    @Override
    public void ogrenciListesiniAc(String sinifadi) {
        int index=viewPager.getCurrentItem();
        Intent ıntent=new Intent(getApplicationContext(),OgrenciListesi.class);
        ıntent.putExtra("sinifadi",sinifadi);
        ıntent.putExtra("pageIndex",index);
        startActivityForResult(ıntent,1970);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1000){
            viewPager.setAdapter(tabPageAdapter);
            viewPager.setCurrentItem(1);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent ıntent=new Intent(getApplicationContext(),MainActivity.class);
        startActivity(ıntent);
        finish();
    }
}
