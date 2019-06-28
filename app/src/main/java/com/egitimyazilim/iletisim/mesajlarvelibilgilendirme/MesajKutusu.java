package com.egitimyazilim.iletisim.mesajlarvelibilgilendirme;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
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