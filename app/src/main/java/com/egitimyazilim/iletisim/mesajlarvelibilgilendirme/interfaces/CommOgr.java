package com.egitimyazilim.iletisim.mesajlarvelibilgilendirme.interfaces;

import com.egitimyazilim.iletisim.mesajlarvelibilgilendirme.object_classes.Ogrenci;

import java.util.List;

public interface CommOgr {
    public void openOgrenciEkleme(String sinifadi);
    public void eklenenOgrenci();
    public void guncellenenOgrenci();
    public void guncellenecekOgrenci(Ogrenci ogrenci);
    public void ozelSMSGonderilecler(List<Ogrenci> ogrenciList);
}
