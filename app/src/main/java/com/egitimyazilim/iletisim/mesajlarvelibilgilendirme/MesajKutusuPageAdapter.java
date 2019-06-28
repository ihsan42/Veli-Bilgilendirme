package com.egitimyazilim.iletisim.mesajlarvelibilgilendirme;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class MesajKutusuPageAdapter extends FragmentPagerAdapter {
    public MesajKutusuPageAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        switch(i){
            case 0:
                return new FragmentVeliMesajlari();
            case 1:
                return new FragmentDiğerMesajlar();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0:
                return "Veli Mesajları";
            case 1:
                return "Diğer Mesajlar";
        }
        return null;
    }
}
