package com.egitimyazilim.iletisim.mesajlarvelibilgilendirme;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import androidx.annotation.Nullable;

import com.egitimyazilim.iletisim.mesajlarvelibilgilendirme.adapters.TabPageAdapter;
import com.egitimyazilim.iletisim.mesajlarvelibilgilendirme.fragments.MenuContentFragment;
import com.egitimyazilim.iletisim.mesajlarvelibilgilendirme.fragments.SinifEkleme;
import com.egitimyazilim.iletisim.mesajlarvelibilgilendirme.fragments.SinifGuncelleme;
import com.egitimyazilim.iletisim.mesajlarvelibilgilendirme.interfaces.CommSinif;
import com.egitimyazilim.iletisim.mesajlarvelibilgilendirme.interfaces.MenuContentComm;
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
import android.widget.TextView;

public class Siniflar extends AppCompatActivity implements CommSinif, MenuContentComm {

    ViewPager viewPager;
    TabPageAdapter tabPageAdapter;
    FragmentManager fm;
    Button buttonMenuOpen;
    Button buttonMenuClose;
    MenuContentFragment menuContentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_siniflar);

        viewPager = (ViewPager) findViewById(R.id.pager);
        tabPageAdapter = new TabPageAdapter(getSupportFragmentManager());
        viewPager.setAdapter(tabPageAdapter);

        ActionBar bar = getSupportActionBar();
        bar.hide();

        buttonMenuOpen = (Button) findViewById(R.id.buttonMenuOpen);
        buttonMenuClose = (Button) findViewById(R.id.buttonMenuClose);
        TextView textView = (TextView) findViewById(R.id.textViewTitleToolbar);
        textView.setText("Sınıflar");

        fm = getSupportFragmentManager();
        menuContentFragment = (MenuContentFragment) fm.findFragmentById(R.id.fragmentMenu);
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

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);

        // final String[] depolamaIzni = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_PHONE_STATE,Manifest.permission.READ_CONTACTS,Manifest.permission.WRITE_CONTACTS,Manifest.permission.CALL_PHONE,Manifest.permission.READ_SMS,Manifest.permission.SEND_SMS,};

        // ActivityCompat.requestPermissions(Siniflar.this, depolamaIzni, 102);


        Button buttonAdd = (Button) findViewById(R.id.buttonSiniflarAdd);
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // final String[] depolamaIzni = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE};
                //if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                //  ActivityCompat.requestPermissions(Siniflar.this, depolamaIzni, 102);
                // }
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
                        Intent ıntent = new Intent(getApplicationContext(), ExceldenListeAl.class);
                        startActivity(ıntent);
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
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

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder=new AlertDialog.Builder(Siniflar.this);
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
