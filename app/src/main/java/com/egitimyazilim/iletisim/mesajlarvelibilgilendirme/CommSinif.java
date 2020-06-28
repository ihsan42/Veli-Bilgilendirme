package com.egitimyazilim.iletisim.mesajlarvelibilgilendirme;

public interface CommSinif {
    public void guncellenecekSinif(String sinifadi);
    public void sinifEklemeyiAc(int activePageIndex);
    public void kursSinifEklemeOkOnClick();
    public void normalSinifEklemeOkOnClick();
    public void kursSinifGuncellemeOkOnClick();
    public void normalSinifGuncellemeOkOnClick();
    public void ogrenciListesiniAc(String sinifadi);
}
