package com.egitimyazilim.iletisim.mesajlarvelibilgilendirme;

import android.os.Bundle;
import com.google.android.material.tabs.TabLayout;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
public class MesajKutusu extends AppCompatActivity{

    ViewPager viewPager;
    MesajKutusuPageAdapter tabPageAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mesaj_kutusu);

        ActionBar bar = getSupportActionBar();
        bar.hide();

        viewPager = (ViewPager) findViewById(R.id.pagerMesajlar);
        tabPageAdapter = new MesajKutusuPageAdapter(getSupportFragmentManager());
        viewPager.setAdapter(tabPageAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);
    }
}