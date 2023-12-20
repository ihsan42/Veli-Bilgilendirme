package com.egitimyazilim.iletisim.mesajlarvelibilgilendirme;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.egitimyazilim.iletisim.mesajlarvelibilgilendirme.adapters.MesajKutusuPageAdapter;
import com.egitimyazilim.iletisim.mesajlarvelibilgilendirme.fragments.MenuContentFragment;
import com.egitimyazilim.iletisim.mesajlarvelibilgilendirme.interfaces.MenuContentComm;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.loader.app.LoaderManager;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
public class MesajKutusu extends AppCompatActivity implements MenuContentComm {

    FragmentManager fm;
    Button buttonMenuOpen;
    Button buttonMenuClose;
    MenuContentFragment menuContentFragment;
    ViewPager viewPager;
    MesajKutusuPageAdapter tabPageAdapter;
    private static final int requestPermissionsCode = 1990;
    String[] izinler = {Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS, Manifest.permission.SEND_SMS
            , Manifest.permission.RECEIVE_MMS, Manifest.permission.READ_PHONE_STATE,Manifest.permission.READ_CONTACTS};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mesaj_kutusu);

        ActionBar bar = getSupportActionBar();
        bar.hide();

        ActivityCompat.requestPermissions(this, izinler, requestPermissionsCode);

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

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder=new AlertDialog.Builder(MesajKutusu.this);
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode==requestPermissionsCode){
            if(grantResults.length==izinler.length){
                int toplam=0;
                for(int result:grantResults){
                    toplam+=result;
                }
                if(toplam==0){
                    viewPager = (ViewPager) findViewById(R.id.pagerMesajlar);
                    tabPageAdapter = new MesajKutusuPageAdapter(getSupportFragmentManager());
                    viewPager.setAdapter(tabPageAdapter);

                    TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
                    tabLayout.setupWithViewPager(viewPager);
                }else{
                    Toast.makeText(getApplicationContext(),"Eksik izinler var! Mesajlar gösterilemiyor.",Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}