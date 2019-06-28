package com.egitimyazilim.iletisim.mesajlarvelibilgilendirme;

public class OgrenciForYoklama {
    String sinif;
    String okulno;
    String adSoyad;
    String telno;
    String durum;
    String tarih;
    String durumYok;
    String durumGec;
    String durumRaporlu;
    String durumIzinli;
    boolean isChecked;

    public OgrenciForYoklama() {
    }

    public String getDurumRaporlu() {
        return durumRaporlu;
    }

    public void setDurumRaporlu(String durumRaporlu) {
        this.durumRaporlu = durumRaporlu;
    }

    public String getDurumIzinli() {
        return durumIzinli;
    }

    public void setDurumIzinli(String durumIzinli) {
        this.durumIzinli = durumIzinli;
    }

    public boolean getChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public String getSinif() {
        return sinif;
    }

    public void setSinif(String sinif) {
        this.sinif = sinif;
    }

    public String getOkulno() {
        return okulno;
    }

    public void setOkulno(String okulno) {
        this.okulno = okulno;
    }

    public String getAdSoyad() {
        return adSoyad;
    }

    public void setAdSoyad(String adSoyad) {
        this.adSoyad = adSoyad;
    }

    public String getTelno() {
        return telno;
    }

    public void setTelno(String telno) {
        this.telno = telno;
    }

    public String getDurum() {
        return durum;
    }

    public void setDurum(String durum) {
        this.durum = durum;
    }

    public String getTarih() {
        return tarih;
    }

    public void setTarih(String tarih) {
        this.tarih = tarih;
    }

    public String getDurumYok() {
        return durumYok;
    }

    public void setDurumYok(String durumYok) {
        this.durumYok = durumYok;
    }

    public String getDurumGec() {
        return durumGec;
    }

    public void setDurumGec(String durumGec) {
        this.durumGec = durumGec;
    }
}
