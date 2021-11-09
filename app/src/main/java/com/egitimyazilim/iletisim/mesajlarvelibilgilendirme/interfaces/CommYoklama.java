package com.egitimyazilim.iletisim.mesajlarvelibilgilendirme.interfaces;

public interface CommYoklama {
    public void gecmisYoklamaGirmeyeGonder(String sinif,String okulno,String adsoyad);

    public void gecmisYoklamaOkOnClick(String sinif);

    public void yoklamaKayitSilmeOk();
    public void openYoklamaKayitSilme(String sinifadi, String okulno, String adsoyad, String telno);
}
