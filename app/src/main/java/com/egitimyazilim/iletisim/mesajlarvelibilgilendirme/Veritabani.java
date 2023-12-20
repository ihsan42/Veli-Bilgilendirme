package com.egitimyazilim.iletisim.mesajlarvelibilgilendirme;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.egitimyazilim.iletisim.mesajlarvelibilgilendirme.object_classes.Ogrenci;
import com.egitimyazilim.iletisim.mesajlarvelibilgilendirme.object_classes.OgrenciForYazili;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Veritabani extends SQLiteOpenHelper {

    private static final String veritabani_adi="sinif_ogrenci_kayitlari";
    private static final String tablo_adi_siniflar= "sinif_tablosu";
    private static final String tablo_adi_kurs_siniflar= "kurs_sinif_tablosu";
    private static final String tablo_adi_ogrenciler="ogrenci_tablosu";
    private static final String tablo_adi_yoklama_kayitlari="yoklama_kayitlari";
    private static final String tablo_adi_kisisel_bilgiler="kisisel_bilgiler";
    private static final String tablo_adi_okutulan_dersler="okutulan_dersler";
    private static final String tablo_adi_hazir_mesajlarim="hazir_mesajlarim";
    private static final String tablo_adi_yazili_kayitlari="yazili_kayitlari";

    private static  final int veritabani_versiyonu=2;
    public boolean durum;

    private static final String CREATE_TABLE_YAZILI_KAYITLARI="CREATE TABLE IF NOT EXISTS "+tablo_adi_yazili_kayitlari+"(ID INTEGER PRIMARY KEY AUTOINCREMENT, sinif TEXT, okulno TEXT, adsoyad TEXT, telno TEXT, yazili1 TEXT, yazili2 TEXT, ders TEXT);";
    private static final String CREATE_TABLE_HAZIR_MESAJLARIM="CREATE TABLE IF NOT EXISTS "+tablo_adi_hazir_mesajlarim+"(ID INTEGER PRIMARY KEY AUTOINCREMENT, mesaj TEXT);";
    private static final String CREATE_TABLE_OKUTULAN_DERSLER="CREATE TABLE IF NOT EXISTS "+tablo_adi_okutulan_dersler+"(ID INTEGER PRIMARY KEY AUTOINCREMENT, dersadi TEXT);";
    private static final String CREATE_TABLE_KISISEL_BILGILER="CREATE TABLE IF NOT EXISTS " +tablo_adi_kisisel_bilgiler+"(ID INTEGER PRIMARY KEY AUTOINCREMENT, adsoyad TEXT, brans TEXT, okuladi TEXT);";
    private static final String CREATE_TABLO_SINIFLAR="CREATE TABLE IF NOT EXISTS "+tablo_adi_siniflar+"(ID INTEGER PRIMARY KEY AUTOINCREMENT, sinifadi TEXT);";
    private static final String CREATE_TABLO_KURS_SINIFLAR="CREATE TABLE IF NOT EXISTS "+tablo_adi_kurs_siniflar+"(ID INTEGER PRIMARY KEY AUTOINCREMENT, sinifadi TEXT);";
    private static final String CREATE_TABLO_OGRENCILER="CREATE TABLE IF NOT EXISTS "+tablo_adi_ogrenciler+"(ID INTEGER, sinifadi TEXT, okulno INTEGER, adsoyad TEXT, veliadi TEXT, telno TEXT);";
    private static final String CREAT_TABLO_YOKLAMA_KAYITLARI="CREATE TABLE IF NOT EXISTS "+tablo_adi_yoklama_kayitlari+"(ID INTEGER PRIMARY KEY AUTOINCREMENT, sinifadi TEXT, okulno TEXT, adsoyad TEXT, durum TEXT, tarih DATE);";

    public Veritabani(Context context) {
        super(context, veritabani_adi, null, veritabani_versiyonu);
    }

    public List<OgrenciForYazili> tumYaziliKayitlariniGetir(){
        List<OgrenciForYazili> ogrenciYaziliKayitlariList=new ArrayList<>();
        SQLiteDatabase db=this.getReadableDatabase();
        String sqlSorgusu="SELECT * FROM "+tablo_adi_yazili_kayitlari;
        Log.e(veritabani_adi,sqlSorgusu);
        Cursor c=db.rawQuery(sqlSorgusu,null);

        int siraNoSinif=c.getColumnIndex("sinif");
        int siraNoOkulNo=c.getColumnIndex("okulno");
        int siraNoAdSoyad=c.getColumnIndex("adsoyad");
        int siraNoTelno=c.getColumnIndex("telno");
        int siraNoYazili1=c.getColumnIndex("yazili1");
        int siraNoYazili2=c.getColumnIndex("yazili2");
        int siraNoDers=c.getColumnIndex("ders");

        try {
            while (c.moveToNext()){
                OgrenciForYazili ogrenci= new OgrenciForYazili();
                ogrenci.setSinif(c.getString(siraNoSinif));
                ogrenci.setOkulno(c.getString(siraNoOkulNo));
                ogrenci.setAdSoyad(c.getString(siraNoAdSoyad));
                ogrenci.setTelno(c.getString(siraNoTelno));
                ogrenci.setYazili1(c.getString(siraNoYazili1));
                ogrenci.setYazili2(c.getString(siraNoYazili2));
                ogrenci.setDers(c.getString(siraNoDers));
                ogrenciYaziliKayitlariList.add(ogrenci);
            }
        }
        finally {
            c.close();
            db.close();
        }
        return ogrenciYaziliKayitlariList;
    }

    public long yazili1iGuncelle(OgrenciForYazili ogrenci){
        SQLiteDatabase db=getWritableDatabase();

        String yazili1=ogrenci.getYazili1();
        String yazili2=ogrenci.getYazili2();
        String okulno=ogrenci.getOkulno();
        String sinif=ogrenci.getSinif();
        String ders=ogrenci.getDers();

        ContentValues cv= new ContentValues();
        cv.put("yazili1",yazili1);

        long id=db.update(tablo_adi_yazili_kayitlari,cv,"sinif='"+sinif+"' AND okulno='"+okulno+"' AND ders='"+ders+"'",null);
        db.close();
        return id;
    }

    public long yazili2yiGuncelle(OgrenciForYazili ogrenci){
        SQLiteDatabase db=getWritableDatabase();

        String yazili1=ogrenci.getYazili1();
        String yazili2=ogrenci.getYazili2();
        String okulno=ogrenci.getOkulno();
        String sinif=ogrenci.getSinif();
        String ders=ogrenci.getDers();

        ContentValues cv= new ContentValues();
        cv.put("yazili2",yazili2);

        long id=db.update(tablo_adi_yazili_kayitlari,cv,"sinif='"+sinif+"' AND okulno='"+okulno+"' AND ders='"+ders+"'",null);
        db.close();
        return id;
    }

    public long yaziliGuncelle(OgrenciForYazili ogrenci){
        SQLiteDatabase db=getWritableDatabase();

        String yazili1=ogrenci.getYazili1();
        String yazili2=ogrenci.getYazili2();
        String okulno=ogrenci.getOkulno();
        String sinif=ogrenci.getSinif();
        String ders=ogrenci.getDers();

        ContentValues cv= new ContentValues();
        cv.put("yazili1",yazili1);
        cv.put("yazili2",yazili2);

        long id=db.update(tablo_adi_yazili_kayitlari,cv,"sinif='"+sinif+"' AND okulno='"+okulno+"' AND ders='"+ders+"'",null);
        db.close();
        return id;
    }

    public long yaziliKaydet(OgrenciForYazili ogrenci){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("sinif", ogrenci.getSinif());
        cv.put("okulno", ogrenci.getOkulno());
        cv.put("adsoyad", ogrenci.getAdSoyad());
        cv.put("telno", ogrenci.getTelno());
        cv.put("yazili1", ogrenci.getYazili1());
        cv.put("yazili2", ogrenci.getYazili2());
        cv.put("ders", ogrenci.getDers());
        long id = db.insert(tablo_adi_yazili_kayitlari, null, cv);
        db.close();
        return id;
    }

    public List<OgrenciForYazili> yaziliKayitlariGetir(String sinifadi, String dersadi){
        List<OgrenciForYazili> ogrenciYaziliKayitlariList=new ArrayList<>();
        SQLiteDatabase db=this.getReadableDatabase();
        String sqlSorgusu="SELECT * FROM "+tablo_adi_yazili_kayitlari+" WHERE sinif='"+sinifadi+"' and ders='"+dersadi+"'";
        Log.e(veritabani_adi,sqlSorgusu);
        Cursor c=db.rawQuery(sqlSorgusu,null);

        int siraNoSinif=c.getColumnIndex("sinif");
        int siraNoOkulNo=c.getColumnIndex("okulno");
        int siraNoAdSoyad=c.getColumnIndex("adsoyad");
        int siraNoTelno=c.getColumnIndex("telno");
        int siraNoYazili1=c.getColumnIndex("yazili1");
        int siraNoYazili2=c.getColumnIndex("yazili2");

        try {
            while (c.moveToNext()){
                OgrenciForYazili ogrenci= new OgrenciForYazili();
                ogrenci.setSinif(c.getString(siraNoSinif));
                ogrenci.setOkulno(c.getString(siraNoOkulNo));
                ogrenci.setAdSoyad(c.getString(siraNoAdSoyad));
                ogrenci.setTelno(c.getString(siraNoTelno));
                ogrenci.setYazili1(c.getString(siraNoYazili1));
                ogrenci.setYazili2(c.getString(siraNoYazili2));
                ogrenciYaziliKayitlariList.add(ogrenci);
            }
        }
        finally {
            c.close();
            db.close();
        }
        return ogrenciYaziliKayitlariList;
    }

    public void sinifinTumYaziliKayitlariniSil(String sinifadi){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM "+tablo_adi_yazili_kayitlari+" WHERE sinif='"+sinifadi+"'");
        db.close();
    }

    public void yaziliKayitlariSil(String sinifadi, String dersadi){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM "+tablo_adi_yazili_kayitlari+" WHERE sinif='"+sinifadi+"' and ders='"+dersadi+"'");
        db.close();
    }

    public long hazirMesajKaydet(String mesaj){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("mesaj", mesaj);
        long id = db.insert(tablo_adi_hazir_mesajlarim, null, cv);
        db.close();
        return id;
    }

    public long hazirMesajSil(String mesajim){
        try{
            SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL("DELETE FROM "+tablo_adi_hazir_mesajlarim+" WHERE mesaj='"+mesajim+"'");
            db.close();
        }catch (SQLException e){
            Log.e("HazirMesajSil",e.getMessage());
            return -1;
        }
       return 1;
    }

    public List<String> hazirMesajlarimiGetir(){
        List<String> hazirMesajlarim=new ArrayList<>();
        String hazirmesajim="";
        SQLiteDatabase db=this.getReadableDatabase();
        String sqlSorgusu= "SELECT * FROM "+tablo_adi_hazir_mesajlarim;
        Log.e(veritabani_adi,sqlSorgusu);
        Cursor c=db.rawQuery(sqlSorgusu,null);

        int siraNoMesaj=c.getColumnIndex("mesaj");

        try {
            while (c.moveToNext()){
                hazirmesajim=c.getString(siraNoMesaj);

                if(hazirmesajim.contains("/")){
                    hazirmesajim=hazirmesajim.replaceAll("/","'");
                }

                if(hazirmesajim.contains("//")){
                    hazirmesajim=hazirmesajim.replaceAll("//","\"");
                }
                hazirMesajlarim.add(hazirmesajim);
            }
        }
        finally {
            c.close();
            db.close();
        }

        return hazirMesajlarim;
    }

    public List<String> kisiselBilgileriGetir(){
        List<String> bilgiler=new ArrayList<>();
        String adsoyad=""; String brans=""; String okuladi="";
        SQLiteDatabase db=this.getReadableDatabase();
        String sqlSorgusu= "SELECT * FROM "+tablo_adi_kisisel_bilgiler;
        Log.e(veritabani_adi,sqlSorgusu);
        Cursor c=db.rawQuery(sqlSorgusu,null);

        int siraNoAdSoyad=c.getColumnIndex("adsoyad");
        int siraNoBrans=c.getColumnIndex("brans");
        int siraNoOkulAdi=c.getColumnIndex("okuladi");

        try {
            while (c.moveToNext()){
                adsoyad=c.getString(siraNoAdSoyad);
                brans=c.getString(siraNoBrans);
                okuladi=c.getString(siraNoOkulAdi);
                bilgiler.add(adsoyad);
                bilgiler.add(brans);
                bilgiler.add(okuladi);
            }
        }
        finally {
            c.close();
            db.close();
        }

        return bilgiler;
    }

    public long kisiselBilgileriKaydet(String adsoyad, String brans, String okuladi){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("adsoyad", adsoyad);
        cv.put("brans", brans);
        cv.put("okuladi", okuladi);
        long id = db.insert(tablo_adi_kisisel_bilgiler, null, cv);
        db.close();
        return id;
    }

    public void tumKisiselBilgileriSil(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM "+tablo_adi_kisisel_bilgiler);
        db.close();
    }

    public List<String> okutulanDersleriGetir(){
        List<String> dersler=new ArrayList<>();
        SQLiteDatabase db=this.getReadableDatabase();
        String sqlSorgusu= "SELECT * FROM "+tablo_adi_okutulan_dersler;
        Log.e(veritabani_adi,sqlSorgusu);
        Cursor c=db.rawQuery(sqlSorgusu,null);

        int siraNoDersadi=c.getColumnIndex("dersadi");

        try {
            while (c.moveToNext()){
                String dersadi="";
                dersadi=c.getString(siraNoDersadi);
                dersler.add(dersadi);
            }
        }
        finally {
            c.close();
            db.close();
        }
        return dersler;
    }

    public void okutulanDersiSil(String dersadi){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM "+tablo_adi_okutulan_dersler+" WHERE dersadi='"+dersadi+"'");
        db.close();
    }

    public long okutulanDersiKaydet(String dersadi){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("dersadi", dersadi);
        long id = db.insert(tablo_adi_okutulan_dersler, null, cv);
        db.close();
        return id;
    }

    public long ogrenciYoklamaSil(String sinif, String ogrNo, String kayittarihi) {
        try {
            Date date=new SimpleDateFormat("dd.MM.yyyy").parse(kayittarihi);

            SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL("DELETE FROM "+tablo_adi_yoklama_kayitlari+" " +
                    "WHERE sinifadi='"+sinif+"' " +
                    "and okulno='"+ogrNo+"' " +
                    "and tarih='"+ new SimpleDateFormat("yyyy-MM-dd").format(date)+"'");
            db.close();

            return 0;
        } catch (ParseException e) {
            Log.e("YoklamaKayitSil",e.getLocalizedMessage());
            return -1;
        }


    }

    public long yoklamaKaydet(Ogrenci ogrenci, String durum, String tarih){
        SQLiteDatabase db= this.getWritableDatabase();
        ContentValues cv= new ContentValues();
        cv.put("sinifadi",ogrenci.getSinif());
        cv.put("okulno",ogrenci.getOkulno());
        cv.put("adsoyad",ogrenci.getAdSoyad());
        cv.put("durum",durum);
        cv.put("tarih",tarih);
        long id=db.insert(tablo_adi_yoklama_kayitlari,null,cv);
        db.close();
        return id;
    }

    public void sınıfYoklamaKayitSil(String sinif) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM "+tablo_adi_yoklama_kayitlari+" WHERE sinifadi='"+sinif+"'");
        db.close();
    }

    public List<Ogrenci> tumYoklamaKaydiGetir(){
        List<Ogrenci> ogrenciList=new ArrayList<Ogrenci>();
        SQLiteDatabase db=this.getReadableDatabase();
        String sqlSorgusu= "SELECT * FROM "+tablo_adi_yoklama_kayitlari+" ORDER BY sinifadi, okulno";
        Log.e(veritabani_adi,sqlSorgusu);
        Cursor c=db.rawQuery(sqlSorgusu,null);

        int siraNoSinif=c.getColumnIndex("sinifadi");
        int siraNoOkulNo=c.getColumnIndex("okulno");
        int siraNoAdSoyad=c.getColumnIndex("adsoyad");
        int siraNoDurum=c.getColumnIndex("durum");
        int siraNoTarih=c.getColumnIndex("tarih");

        try {
            while (c.moveToNext()){
                Ogrenci ogrenci= new Ogrenci();
                ogrenci.setSinif(c.getString(siraNoSinif));
                ogrenci.setOkulno(c.getString(siraNoOkulNo));
                ogrenci.setAdSoyad(c.getString(siraNoAdSoyad));
                ogrenci.setDurum(c.getString(siraNoDurum));
                ogrenci.setTarih(c.getString(siraNoTarih));
                ogrenciList.add(ogrenci);
            }
        }
        finally {
            c.close();
            db.close();
        }
        return ogrenciList;
    }

    public List<Ogrenci> ogrenciYoklamaKaydiGetir(String sinifadi, String okulno){
        List<Ogrenci> ogrenciYoklamaKayitlariList=new ArrayList<Ogrenci>();
        SQLiteDatabase db=this.getReadableDatabase();
        String sqlSorgusu= "SELECT * FROM "+tablo_adi_yoklama_kayitlari+" WHERE sinifadi=? and okulno=? ORDER BY tarih ASC";
        Log.e(veritabani_adi,sqlSorgusu);
        Cursor c=db.rawQuery(sqlSorgusu,new String[]{sinifadi,okulno});

        int siraNoSinif=c.getColumnIndex("sinifadi");
        int siraNoOkulNo=c.getColumnIndex("okulno");
        int siraNoAdSoyad=c.getColumnIndex("adsoyad");
        int siraNoDurum=c.getColumnIndex("durum");
        int siraNoTarih=c.getColumnIndex("tarih");

        try {
            while (c.moveToNext()){
                Ogrenci ogrenci= new Ogrenci();
                ogrenci.setSinif(c.getString(siraNoSinif));
                ogrenci.setOkulno(c.getString(siraNoOkulNo));
                ogrenci.setAdSoyad(c.getString(siraNoAdSoyad));
                ogrenci.setDurum(c.getString(siraNoDurum));

                String tarih=c.getString(siraNoTarih);

                Date date=new SimpleDateFormat("yyyy-MM-dd").parse(tarih);
                ogrenci.setTarih(new SimpleDateFormat("dd.MM.yyyy").format(date));

                ogrenciYoklamaKayitlariList.add(ogrenci);
            }
        } catch (ParseException e) {
            throw new RuntimeException(e);
        } finally {
            c.close();
            db.close();
        }
        return ogrenciYoklamaKayitlariList;
    }

    public long ogrenciGuncelle(Ogrenci ogrenci){
        SQLiteDatabase db=getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put("okulno",ogrenci.getOkulno());
        values.put("adsoyad",ogrenci.getAdSoyad());
        values.put("veliadi",ogrenci.getVeliAdi());
        values.put("telno",ogrenci.getTelno());

        ContentValues values2=new ContentValues();
        values2.put("okulno",ogrenci.getOkulno());
        values2.put("adsoyad",ogrenci.getAdSoyad());
        values2.put("telno",ogrenci.getTelno());

        long id=db.update(tablo_adi_ogrenciler,values, "okulno=?",new String[]{ogrenci.getOkulno()});
        db.update(tablo_adi_yazili_kayitlari,values2, "okulno=?",new String[]{ogrenci.getOkulno()});
        db.close();
        return id;
    }

    public void sinifSil(String sinif) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + tablo_adi_siniflar+ " WHERE sinifadi='"+sinif+"'");
        db.close();
    }

    public void kursSinifSil(String sinif) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + tablo_adi_kurs_siniflar+ " WHERE sinifadi='"+sinif+"'");
        db.close();
    }

    public void siniftakiTumOgrSil(String sinif) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + tablo_adi_ogrenciler+ " WHERE sinifadi='"+sinif+"'");
        db.close();
    }

    public  void siniftanOgrenciSil(String sinif, String ogrNo){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + tablo_adi_ogrenciler+ " WHERE sinifadi='"+sinif+"' and okulno='"+ogrNo+"'");
        db.close();
    }

    public long ekleSinif(String sinif) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("sinifadi", sinif);
        long id = db.insert(tablo_adi_siniflar, null, cv);
        db.close();
        return id;
    }

    public long ekleKursSinif(String sinif) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("sinifadi", sinif);
        long id = db.insert(tablo_adi_kurs_siniflar, null, cv);
        db.close();
        return id;
    }

    public long guncelleSinif(String guncelleneceksinif, String yeniSinif) {
        SQLiteDatabase db=getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put("sinifadi",yeniSinif);

        long id=db.update(tablo_adi_siniflar,values, "sinifadi=?",new String[]{guncelleneceksinif});
        db.update(tablo_adi_ogrenciler,values, "sinifadi=?",new String[]{guncelleneceksinif});
        db.close();
        return id;
    }

    public long guncelleKursSinif(String guncelleneceksinif, String yeniSinif) {
        SQLiteDatabase db=getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put("sinifadi",yeniSinif);

        long id=db.update(tablo_adi_kurs_siniflar,values, "sinifadi=?",new String[]{guncelleneceksinif});
        db.update(tablo_adi_ogrenciler,values, "sinifadi=?",new String[]{guncelleneceksinif});
        db.close();
        return id;
    }

    public long ekleOgrenci(Ogrenci ogrenci){
        SQLiteDatabase db= this.getWritableDatabase();
        ContentValues cv= new ContentValues();
        cv.put("sinifadi",ogrenci.getSinif());
        cv.put("okulno",ogrenci.getOkulno());
        cv.put("adsoyad",ogrenci.getAdSoyad());
        cv.put("veliadi",ogrenci.getVeliAdi());
        cv.put("telno",ogrenci.getTelno());
        long id=db.insert(tablo_adi_ogrenciler,null,cv);
        db.close();
        return id;
    }

    public List<String> getirSinif() {
        List<String> sinifList=new ArrayList<>();
        SQLiteDatabase db=this.getReadableDatabase();
        String sqlSorgusu= "SELECT ID, sinifadi FROM "+tablo_adi_siniflar+" ORDER BY sinifadi";
        Log.e(veritabani_adi,sqlSorgusu);
        Cursor c=db.rawQuery(sqlSorgusu ,null);

        int siraNoID=c.getColumnIndex("ID");
        int siraNoSinifadi=c.getColumnIndex("sinifadi");

        try {
            while (c.moveToNext()){
                 String sinif=c.getString(siraNoSinifadi);
                sinifList.add(sinif);
            }
        }
        finally {
            c.close();
            db.close();
        }
        return sinifList;
    }

    public List<String> getirKursSinif() {
        List<String> sinifList=new ArrayList<>();
        SQLiteDatabase db=this.getReadableDatabase();
        String sqlSorgusu= "SELECT ID, sinifadi FROM "+tablo_adi_kurs_siniflar+" ORDER BY sinifadi";
        Log.e(veritabani_adi,sqlSorgusu);
        Cursor c=db.rawQuery(sqlSorgusu ,null);

        int siraNoID=c.getColumnIndex("ID");
        int siraNoSinifadi=c.getColumnIndex("sinifadi");

        try {
            while (c.moveToNext()){
                String sinif=c.getString(siraNoSinifadi);
                sinifList.add(sinif);
            }
        }
        finally {
            c.close();
            db.close();
        }
        return sinifList;
    }

    public List<Ogrenci> getirSinifOgrenciList(String sinif){
        List<Ogrenci> ogrenciList=new ArrayList<Ogrenci>();
        SQLiteDatabase db=this.getReadableDatabase();
        String sqlSorgusu= "SELECT * FROM "+tablo_adi_ogrenciler+" WHERE sinifadi=? ORDER BY okulno";
        Log.e(veritabani_adi,sqlSorgusu);
        Cursor c=db.rawQuery(sqlSorgusu,new String[]{sinif});

        int siraNoSinif=c.getColumnIndex("sinifadi");
        int siraNoOkulNo=c.getColumnIndex("okulno");
        int siraNoAdSoyad=c.getColumnIndex("adsoyad");
        int siraNoVeliAdi=c.getColumnIndex("veliadi");
        int siraNoTelNo=c.getColumnIndex("telno");

        try {
            while (c.moveToNext()){
                Ogrenci ogrenci= new Ogrenci();
                ogrenci.setSinif(c.getString(siraNoSinif));
                ogrenci.setOkulno(c.getString(siraNoOkulNo));
                ogrenci.setAdSoyad(c.getString(siraNoAdSoyad));
                ogrenci.setVeliAdi(c.getString(siraNoVeliAdi));
                ogrenci.setTelno(c.getString(siraNoTelNo));
                ogrenci.setDurumGec(false);
                ogrenci.setDurumYok(false);
                ogrenciList.add(ogrenci);
            }
        }
        finally {
            c.close();
            db.close();
        }
        return ogrenciList;
    }

    public List<Ogrenci> getirOgrenci(){
        List<Ogrenci> ogrenciList=new ArrayList<Ogrenci>();
        SQLiteDatabase db=this.getReadableDatabase();
        String sqlSorgusu= "SELECT ID, sinifadi, okulno, adsoyad, veliadi, telno FROM "+tablo_adi_ogrenciler+" ORDER BY sinifadi, okulno";
        Log.e(veritabani_adi,sqlSorgusu);
        Cursor c=db.rawQuery(sqlSorgusu,null);

        int siraNoSinif=c.getColumnIndex("sinifadi");
        int siraNoOkulNo=c.getColumnIndex("okulno");
        int siraNoAdSoyad=c.getColumnIndex("adsoyad");
        int siraNoVeliAdi=c.getColumnIndex("veliadi");
        int siraNoTelNo=c.getColumnIndex("telno");

        try {
            while (c.moveToNext()){
                Ogrenci ogrenci= new Ogrenci();
                ogrenci.setSinif(c.getString(siraNoSinif));
                ogrenci.setOkulno(c.getString(siraNoOkulNo));
                ogrenci.setAdSoyad(c.getString(siraNoAdSoyad));
                ogrenci.setVeliAdi(c.getString(siraNoVeliAdi));
                ogrenci.setTelno(c.getString(siraNoTelNo));
                ogrenci.setDurumGec(false);
                ogrenci.setDurumYok(false);
                ogrenciList.add(ogrenci);
            }
        }
        finally {
            c.close();
            db.close();
        }
        return ogrenciList;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_HAZIR_MESAJLARIM);
        db.execSQL(CREATE_TABLO_SINIFLAR);
        db.execSQL(CREATE_TABLO_KURS_SINIFLAR);
        db.execSQL(CREATE_TABLO_OGRENCILER);
        db.execSQL(CREAT_TABLO_YOKLAMA_KAYITLARI);
        db.execSQL(CREATE_TABLE_KISISEL_BILGILER);
        db.execSQL(CREATE_TABLE_OKUTULAN_DERSLER);
        db.execSQL(CREATE_TABLE_YAZILI_KAYITLARI);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + tablo_adi_hazir_mesajlarim);
        db.execSQL("DROP TABLE IF EXISTS " + tablo_adi_siniflar);
        db.execSQL("DROP TABLE IF EXISTS " + tablo_adi_kurs_siniflar);
        db.execSQL("DROP TABLE IF EXISTS " + tablo_adi_ogrenciler);
        db.execSQL("DROP TABLE IF EXISTS " + tablo_adi_yoklama_kayitlari);
        db.execSQL("DROP TABLE IF EXISTS " + tablo_adi_kisisel_bilgiler);
        db.execSQL("DROP TABLE IF EXISTS " + tablo_adi_okutulan_dersler);
        db.execSQL("DROP TABLE IF EXISTS " + tablo_adi_yazili_kayitlari);
        onCreate(db);
    }
}
