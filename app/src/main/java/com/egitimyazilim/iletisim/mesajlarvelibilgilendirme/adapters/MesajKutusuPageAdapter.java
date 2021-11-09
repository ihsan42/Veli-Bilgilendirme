package com.egitimyazilim.iletisim.mesajlarvelibilgilendirme.adapters;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.egitimyazilim.iletisim.mesajlarvelibilgilendirme.fragments.FragmentDiğerMesajlar;
import com.egitimyazilim.iletisim.mesajlarvelibilgilendirme.fragments.FragmentVeliMesajlari;

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
