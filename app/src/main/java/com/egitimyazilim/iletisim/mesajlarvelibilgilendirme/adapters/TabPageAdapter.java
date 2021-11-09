package com.egitimyazilim.iletisim.mesajlarvelibilgilendirme.adapters;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.egitimyazilim.iletisim.mesajlarvelibilgilendirme.fragments.KursSiniflarFragment;
import com.egitimyazilim.iletisim.mesajlarvelibilgilendirme.fragments.NormalSiniflarFragment;

public class TabPageAdapter extends FragmentPagerAdapter {
    public TabPageAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        switch(i){
            case 0:
                return new NormalSiniflarFragment();
            case 1:
                return new KursSiniflarFragment();
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
                return "Normal";
            case 1:
                return "Kurs";
        }
        return null;
    }
}
