package com.egitimyazilim.iletisim.mesajlarvelibilgilendirme;

import java.util.List;

public interface CommOgr {
    public void openOgrenciEkleme(String sinifadi);
    public void eklenenOgrenci();
    public void guncellenenOgrenci();
    public void guncellenecekOgrenci(Ogrenci ogrenci);
    public void ozelSMSGonderilecler(List<Ogrenci> ogrenciList);
}
