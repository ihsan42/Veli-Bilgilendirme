package com.egitimyazilim.iletisim.mesajlarvelibilgilendirme;

import java.util.Date;

import jxl.write.DateTime;

public class Ogrenci {
    String sinif;
    String okulno;
    String adSoyad;
    String veliAdi;
    String telno;
    String durum;
    String tarih;
    boolean durumYok;
    boolean durumGec;
    boolean durumRaporlu;
    boolean durumIzinli;
    boolean isChecked;


    public Ogrenci(String sinif, String okulno, String ad, String soyad, String telno) {
        this.sinif = sinif;
        this.okulno = okulno;
        this.adSoyad = ad;
        this.veliAdi = soyad;
        this.telno = telno;
    }

    public Ogrenci() {
    }

    public boolean getDurumRaporlu() {
        return durumRaporlu;
    }

    public void setDurumRaporlu(boolean durumRaporlu) {
        this.durumRaporlu = durumRaporlu;
    }

    public boolean getDurumIzinli() {
        return durumIzinli;
    }

    public void setDurumIzinli(boolean durumIzinli) {
        this.durumIzinli = durumIzinli;
    }

    public String getTarih() {
        return tarih;
    }

    public void setTarih(String tarih) {
        this.tarih = tarih;
    }

    public String getDurum() {
        return durum;
    }

    public void setDurum(String durum) {
        this.durum = durum;
    }

    public boolean getChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public boolean getDurumYok() {
        return durumYok;
    }

    public void setDurumYok(boolean durumYok) {
        this.durumYok = durumYok;
    }

    public boolean getDurumGec() {
        return durumGec;
    }

    public void setDurumGec(boolean durumGec) {
        this.durumGec = durumGec;
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

    public String getVeliAdi() {
        return veliAdi;
    }

    public void setVeliAdi(String veliAdi) {
        this.veliAdi = veliAdi;
    }

    public String getTelno() {
        return telno;
    }

    public void setTelno(String telno) {
        this.telno = telno;
    }
}
