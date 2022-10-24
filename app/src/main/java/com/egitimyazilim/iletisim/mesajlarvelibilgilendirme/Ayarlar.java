package com.egitimyazilim.iletisim.mesajlarvelibilgilendirme;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.Settings;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.egitimyazilim.iletisim.mesajlarvelibilgilendirme.fragments.MenuContentFragment;
import com.egitimyazilim.iletisim.mesajlarvelibilgilendirme.interfaces.MenuContentComm;
import com.egitimyazilim.iletisim.mesajlarvelibilgilendirme.object_classes.Ogrenci;
import com.egitimyazilim.iletisim.mesajlarvelibilgilendirme.object_classes.OgrenciForYazili;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Ayarlar extends AppCompatActivity implements MenuContentComm {

    private static final int requestCodePermissionRead=1990;
    private static final int requestCodePermissionWrite=1991;
    private static final int FILE_SELECT_CODE=55;


    FragmentManager fm;
    Button buttonMenuOpen;
    Button buttonMenuClose;
    MenuContentFragment menuContentFragment;
    Uri fileUri;
    List<Ogrenci> ogrenciList;
    List<String> sinifList;
    List<String> dersList;
    List<String> kursSinifList;
    List<Ogrenci> yedekList;
    List<OgrenciForYazili> yaziliList;
    ActionBar bar;
    Switch switchIsim;
    Switch switchBrans;
    Switch switchOkulAdi;
    boolean yaziliKaydiGetirme=false;
    int hatakodu=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ayarlar);

        bar = getSupportActionBar();
        bar.hide();

        buttonMenuOpen=(Button)findViewById(R.id.buttonMenuOpen);
        buttonMenuClose=(Button)findViewById(R.id.buttonMenuClose);

        fm = getSupportFragmentManager();
        menuContentFragment=(MenuContentFragment)fm.findFragmentById(R.id.fragmentMenu);
        fm.beginTransaction().hide(menuContentFragment).commit();

        buttonMenuOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menuButtonsVisibilitySecond();
            }
        });

        buttonMenuClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menuButtonsVisibilityFirst();
            }
        });

        switchIsim = (Switch) findViewById(R.id.switchIsim);
        switchBrans = (Switch) findViewById(R.id.switchBrans);
        switchOkulAdi = (Switch) findViewById(R.id.switchOkulAdi);

        SharedPreferences sharedPref = getSharedPreferences("Kisisel Ayarlar", MODE_PRIVATE);
        boolean durumIsim = sharedPref.getBoolean("isim", false);
        boolean durumBrans = sharedPref.getBoolean("brans", false);
        boolean durumOkuladi = sharedPref.getBoolean("okuladi", false);
        switchIsim.setChecked(durumIsim);
        switchBrans.setChecked(durumBrans);
        switchOkulAdi.setChecked(durumOkuladi);

        final String[] depolamaIzni = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE};
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(Ayarlar.this, depolamaIzni, requestCodePermissionRead);
        }

        Button buttonExcelTaslak = (Button) findViewById(R.id.buttonAyarExcelTaslak);
        buttonExcelTaslak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(Ayarlar.this, depolamaIzni[0])) {
                        ActivityCompat.requestPermissions(Ayarlar.this, depolamaIzni, requestCodePermissionWrite);
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(Ayarlar.this);
                        builder.setTitle("Dikkat!");
                        builder.setMessage("Dosyayı indirebilmek için eksik izin var. Depolama iznini vermeniz gereklidir. İzin vermek için <Ayarlar>'a tıklayınız ve açılan sayfadaki izinler bölümüden izin veriniz.");
                        builder.setPositiveButton("Ayarlar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent myAppSettings = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + getApplicationContext().getPackageName()));
                                myAppSettings.addCategory(Intent.CATEGORY_DEFAULT);
                                myAppSettings.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivityForResult(myAppSettings, 35);
                            }
                        });
                        builder.setNegativeButton("İptal", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                    }
                }else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(Ayarlar.this);
                    builder.setTitle("Dosya İndirilsin mi?");
                    builder.setNegativeButton("İptal", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.setPositiveButton("İndir", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                Workbook ogrencilerExelDosyasi = new SXSSFWorkbook();
                                Sheet ogrencilerSayfasi = ogrencilerExelDosyasi.createSheet("Sınıf_Listesi");
                                Row row=ogrencilerSayfasi.createRow(0);

                                Cell ders=row.createCell(1);
                                ders.setCellValue("ÖĞRENCİ NO");

                                Cell ad=row.createCell(4);
                                ad.setCellValue("AD");

                                Cell soyad=row.createCell(9);
                                soyad.setCellValue("SOYAD");

                                Cell telNo=row.createCell(16);
                                telNo.setCellValue("TELEFON NO");

                                Cell veliAdi=row.createCell(18);
                                veliAdi.setCellValue("VELİ ADI");

                                String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/Veli_Bilgilendirme_Sınıf_Ekleme_Taslağı.xls";
                                File file = new File(getExternalFilesDir(null),path);

                                FileOutputStream fileOutputStream = new FileOutputStream(file);
                                ogrencilerExelDosyasi.write(fileOutputStream);

                                Toast.makeText(getApplicationContext(), "Excel dosyası <Download> klasörüne <Veli_Bilgilendirme_Sınıf_Ekleme_Taslağı.xls> adıyla kaydedildi ", Toast.LENGTH_LONG).show();

                            } catch (Exception e) {
                                Log.e("hata",e.getLocalizedMessage());
                            }
                        }
                    });

                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
            }
        });

        switchIsim.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences sharedPref = getSharedPreferences("Kisisel Ayarlar", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                Veritabani vt = new Veritabani(getApplicationContext());
                List<String> kisiselBilgiler = new ArrayList<>();
                kisiselBilgiler = vt.kisiselBilgileriGetir();

                if (switchIsim.isChecked()) {
                    if (kisiselBilgiler.size() == 0 || kisiselBilgiler.get(0).equals("")) {
                        Toast.makeText(getApplicationContext(), "Ad-Soyad kaydınız mevcut değil!", Toast.LENGTH_SHORT).show();
                        switchIsim.setChecked(false);
                    } else {
                        editor.putBoolean("isim", true);
                        editor.commit();
                    }
                } else {
                    editor.putBoolean("isim", false);
                    editor.commit();
                }
            }
        });

        switchBrans.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences sharedPref = getSharedPreferences("Kisisel Ayarlar", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                Veritabani vt = new Veritabani(getApplicationContext());
                List<String> kisiselBilgiler = new ArrayList<>();
                kisiselBilgiler = vt.kisiselBilgileriGetir();

                if (switchBrans.isChecked()) {
                    if (kisiselBilgiler.size() == 0 || kisiselBilgiler.get(1).equals("")) {
                        Toast.makeText(getApplicationContext(), "Branş kaydınız mevcut değil!", Toast.LENGTH_SHORT).show();
                        switchBrans.setChecked(false);
                    } else {
                        editor.putBoolean("brans", true);
                        editor.commit();
                    }
                } else {
                    editor.putBoolean("brans", false);
                    editor.commit();
                }
            }
        });

        switchOkulAdi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences sharedPref = getSharedPreferences("Kisisel Ayarlar", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                Veritabani vt = new Veritabani(getApplicationContext());
                List<String> kisiselBilgiler = new ArrayList<>();
                kisiselBilgiler = vt.kisiselBilgileriGetir();

                if (switchOkulAdi.isChecked()) {
                    if (kisiselBilgiler.size() == 0 || kisiselBilgiler.get(2).equals("")) {
                        Toast.makeText(getApplicationContext(), "Okul adı kaydınız mevcut değil!", Toast.LENGTH_SHORT).show();
                        switchOkulAdi.setChecked(false);
                    } else {
                        editor.putBoolean("okuladi", true);
                        editor.commit();
                    }
                } else {
                    editor.putBoolean("okuladi", false);
                    editor.commit();
                }
            }
        });

        Button buttonYedekle = (Button) findViewById(R.id.buttonAyarYedekle);
        buttonYedekle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] depolamaIzni = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(Ayarlar.this, depolamaIzni, requestCodePermissionWrite);
                }
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Ayarlar.this);
                    builder.setMessage("Tüm kayıtlı veriler yedeklensin mi?");
                    builder.setPositiveButton("Evet", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            new Ayarlar.Yedekle().execute();
                        }
                    });
                    builder.setNegativeButton("İptal", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                } else {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(Ayarlar.this, depolamaIzni[0])) {
                        ActivityCompat.requestPermissions(Ayarlar.this, depolamaIzni, requestCodePermissionWrite);
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(Ayarlar.this);
                        builder.setTitle("Dikkat!");
                        builder.setMessage("Yedekleme için eksik izin var. Depolama iznini vermeniz gereklidir. İzin vermek için <Ayarlar>'a tıklayınız ve açılan sayfadaki izinler bölümüden izin veriniz.");
                        builder.setPositiveButton("Ayarlar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent myAppSettings = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + getApplicationContext().getPackageName()));
                                myAppSettings.addCategory(Intent.CATEGORY_DEFAULT);
                                myAppSettings.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivityForResult(myAppSettings, 35);
                            }
                        });
                        builder.setNegativeButton("İptal", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                    }
                }
            }
        });

        Button buttonYedektenAl = (Button) findViewById(R.id.buttonAyarYedektenAl);
        buttonYedektenAl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    showFileChooser();
                } else {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(Ayarlar.this, depolamaIzni[0])) {
                        ActivityCompat.requestPermissions(Ayarlar.this, depolamaIzni, requestCodePermissionRead);
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(Ayarlar.this);
                        builder.setTitle("Dikkat!");
                        builder.setMessage("Yedekten almak için eksik izin var. Depolama iznini vermeniz gereklidir. İzin vermek için <Ayarlar>'a tıklayınız ve açılan sayfadaki izinler bölümüden izin veriniz.");
                        builder.setPositiveButton("Ayarlar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent myAppSettings = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + getApplicationContext().getPackageName()));
                                myAppSettings.addCategory(Intent.CATEGORY_DEFAULT);
                                myAppSettings.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivityForResult(myAppSettings, 35);
                            }
                        });
                        builder.setNegativeButton("İptal", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                    }
                }
            }
        });
    }

    private class Yedekle extends AsyncTask<Void,Void,Void> {
        ProgressDialog progressDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog=new ProgressDialog(Ayarlar.this);
            progressDialog.setCancelable(false);
            progressDialog.setTitle("Yedekleniyor...");
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressDialog.dismiss();
            Toast.makeText(getApplicationContext(),"Yedek dosyası Downloads klasörüne Veli_Bilgilendirme_yedek.xls adıyla kaydedildi",Toast.LENGTH_LONG).show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Veritabani vt=new Veritabani(getApplicationContext());
            List<Ogrenci> ogrenciList=new ArrayList<>();
            List<String> sinifList=new ArrayList<>();
            List<String> kursSinifList=new ArrayList<>();
            List<Ogrenci> yoklamaList=new ArrayList<>();
            List<OgrenciForYazili> yaziliList=new ArrayList<>();
            List<String> dersList=new ArrayList<>();

            ogrenciList=vt.getirOgrenci();
            sinifList=vt.getirSinif();
            kursSinifList=vt.getirKursSinif();
            yoklamaList=vt.tumYoklamaKaydiGetir();
            yaziliList=vt.tumYaziliKayitlariniGetir();
            dersList=vt.okutulanDersleriGetir();
            vt.close();

            Calendar c = Calendar.getInstance();
            SimpleDateFormat df2 = new SimpleDateFormat("dd-MM-yyyy__kk:mm:ss");
            final String tarihBugun= df2.format(c.getTime());

            String path= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+"/Veli_Bilgilendirme_yedek_"+tarihBugun+".xls";
            File file=new File(path);
          /*  try {
                WritableWorkbook ogrencilerExelDosyasi= Workbook.createWorkbook(file);
                WritableSheet ogrencilerSayfasi=ogrencilerExelDosyasi.createSheet("Ogrenciler",0);
                WritableSheet siniflarSayfasi=ogrencilerExelDosyasi.createSheet("Siniflar",1);
                WritableSheet yoklamaKayitlariSayfasi=ogrencilerExelDosyasi.createSheet("Yoklama Kayitlari",2);
                WritableSheet yazililarSayfasi=ogrencilerExelDosyasi.createSheet("Yazili Kayitlari",3);
                WritableSheet derslerSayfasi=ogrencilerExelDosyasi.createSheet("Okutulan Dersler",4);

                Label ders=new Label(0,0,"DERS ADI");
                derslerSayfasi.addCell(ders);
                for(int i=0;i<dersList.size();i++){
                    Label ders4=new Label(0,i+1,dersList.get(i));
                    derslerSayfasi.addCell(ders4);
                }

                Label sinif3 =new Label(0,0,"SINIF");
                Label okulno3=new Label(1,0,"OKUL NO");
                Label adsoyad3=new Label(2,0,"AD-SOYAD");
                Label telno3=new Label(3,0,"TEL NO");
                Label yazili1=new Label(4,0,"1. YAZILI");
                Label yazili2=new Label(5,0,"2. YAZILI");
                Label ders3=new Label(6,0,"DERS");
                yazililarSayfasi.addCell(sinif3);
                yazililarSayfasi.addCell(okulno3);
                yazililarSayfasi.addCell(adsoyad3);
                yazililarSayfasi.addCell(telno3);
                yazililarSayfasi.addCell(yazili1);
                yazililarSayfasi.addCell(yazili2);
                yazililarSayfasi.addCell(ders3);
                for(int i=0;i<yaziliList.size();i++){
                    Label sinif4 =new Label(0,i+1,yaziliList.get(i).getSinif());
                    Label okulno4=new Label(1,i+1,yaziliList.get(i).getOkulno());
                    Label adsoyad4=new Label(2,i+1,yaziliList.get(i).getAdSoyad());
                    Label telno4=new Label(3,i+1,yaziliList.get(i).getTelno());
                    Label yazili11=new Label(4,i+1,yaziliList.get(i).getYazili1());
                    Label yazili22=new Label(5,i+1,yaziliList.get(i).getYazili2());
                    Label ders4=new Label(6,i+1,yaziliList.get(i).getDers());
                    yazililarSayfasi.addCell(sinif4);
                    yazililarSayfasi.addCell(okulno4);
                    yazililarSayfasi.addCell(adsoyad4);
                    yazililarSayfasi.addCell(telno4);
                    yazililarSayfasi.addCell(yazili11);
                    yazililarSayfasi.addCell(yazili22);
                    yazililarSayfasi.addCell(ders4);
                }


                Label normal=new Label(0,0,"NORMAL");
                Label kurs=new Label(1,0,"KURS");
                siniflarSayfasi.addCell(normal);
                siniflarSayfasi.addCell(kurs);

                for(int i=0;i<sinifList.size();i++){
                    Label sinif=new Label(0,i+1,sinifList.get(i));
                    siniflarSayfasi.addCell(sinif);
                }

                for(int i=0;i<kursSinifList.size();i++){
                    Label sinif=new Label(1,i+1,kursSinifList.get(i));
                    siniflarSayfasi.addCell(sinif);
                }

                Label sinif=new Label(0,0,"Sınıf");
                Label okulno=new Label(1,0,"No");
                Label adsoyad=new Label(2,0,"Ad-Soyad");
                Label veliadi=new Label(3,0,"Veli Adı");
                Label telno=new Label(4,0,"Tel No");
                ogrencilerSayfasi.addCell(sinif);
                ogrencilerSayfasi.addCell(okulno);
                ogrencilerSayfasi.addCell(adsoyad);
                ogrencilerSayfasi.addCell(veliadi);
                ogrencilerSayfasi.addCell(telno);

                for(int i=0;i<ogrenciList.size();i++){
                    Label sinif1=new Label(0,i+1,ogrenciList.get(i).getSinif());
                    Label okulno1=new Label(1,i+1,ogrenciList.get(i).getOkulno());
                    Label adsoyad1=new Label(2,i+1,ogrenciList.get(i).getAdSoyad());
                    Label veliadi1=new Label(3,i+1,ogrenciList.get(i).getVeliAdi());
                    Label telno1=new Label(4,i+1,ogrenciList.get(i).getTelno());
                    ogrencilerSayfasi.addCell(sinif1);
                    ogrencilerSayfasi.addCell(okulno1);
                    ogrencilerSayfasi.addCell(adsoyad1);
                    ogrencilerSayfasi.addCell(veliadi1);
                    ogrencilerSayfasi.addCell(telno1);
                }

                Label sinif2=new Label(0,0,"Sınıf");
                Label okulno2=new Label(1,0,"No");
                Label adsoyad2=new Label(2,0,"Ad-Soyad");
                Label durum=new Label(3,0,"Durum");
                Label tarih=new Label(4,0,"Tarih");
                yoklamaKayitlariSayfasi.addCell(sinif2);
                yoklamaKayitlariSayfasi.addCell(okulno2);
                yoklamaKayitlariSayfasi.addCell(adsoyad2);
                yoklamaKayitlariSayfasi.addCell(durum);
                yoklamaKayitlariSayfasi.addCell(tarih);

                for(int i=0;i<yoklamaList.size();i++){
                    Label sinif1=new Label(0,i+1,yoklamaList.get(i).getSinif());
                    Label okulno1=new Label(1,i+1,yoklamaList.get(i).getOkulno());
                    Label adsoyad1=new Label(2,i+1,yoklamaList.get(i).getAdSoyad());
                    Label durum1=new Label(3,i+1,yoklamaList.get(i).getDurum());
                    Label tarih1=new Label(4,i+1,String.valueOf(yoklamaList.get(i).getTarih()));
                    yoklamaKayitlariSayfasi.addCell(sinif1);
                    yoklamaKayitlariSayfasi.addCell(okulno1);
                    yoklamaKayitlariSayfasi.addCell(adsoyad1);
                    yoklamaKayitlariSayfasi.addCell(durum1);
                    yoklamaKayitlariSayfasi.addCell(tarih1);
                }

                ogrencilerExelDosyasi.write();
                ogrencilerExelDosyasi.close();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (RowsExceededException e) {
                e.printStackTrace();
            } catch (WriteException e) {
                e.printStackTrace();
            }
        */    return null;
        }
    }

    private class YedektenAl extends AsyncTask<Void,Void,Void> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(Ayarlar.this);
            progressDialog.setCancelable(false);
            progressDialog.setTitle("Yedek Getiriliyor...");
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressDialog.dismiss();
            if (hatakodu == 21) {
                Toast.makeText(getApplicationContext(), "Yedek dosyasında değişiklikler yaptıysanız Excel 97-2003 Çalışma Kitabı formatında .xls uzantılı olacak şekilde kaydettiğinizden emin olun!", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(), "Yedekteki veriler alındı ve kaydedildi", Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected Void doInBackground(Void... voids) {
            hatakodu = 0;
           /* try {
                ogrenciList = new ArrayList<>();
                sinifList = new ArrayList<>();
                kursSinifList = new ArrayList<>();
                yedekList = new ArrayList<>();
                yaziliList = new ArrayList<>();
                dersList = new ArrayList<>();

                File file = new File(filePath);
                try {
                    WorkbookSettings vs = new WorkbookSettings();
                    vs.setEncoding("CP1254");
                    Workbook CalismaKitabi = Workbook.getWorkbook(file, vs);
                    Sheet[] sayfalar = CalismaKitabi.getSheets();

                    Sheet ExcelSayfasi = CalismaKitabi.getSheet(0);
                    Sheet sinifSayfasi = CalismaKitabi.getSheet(1);
                    Sheet yoklamaSayfasi = CalismaKitabi.getSheet(2);

                    for (int i = 1; i < sinifSayfasi.getColumn(0).length; i++) {
                        Cell sinif = sinifSayfasi.getCell(0, i);
                        sinifList.add(sinif.getContents());
                    }

                    for (int i = 1; i < sinifSayfasi.getColumn(1).length; i++) {
                        Cell sinif = sinifSayfasi.getCell(1, i);
                        kursSinifList.add(sinif.getContents());
                    }

                    for (int j = 1; j < ExcelSayfasi.getRows(); j++) {
                        Ogrenci ogrenci = new Ogrenci();

                        Cell sinif = ExcelSayfasi.getCell(0, j);
                        ogrenci.setSinif(sinif.getContents());

                        Cell okulno = ExcelSayfasi.getCell(1, j);
                        ogrenci.setOkulno(okulno.getContents());

                        Cell adsoyad = ExcelSayfasi.getCell(2, j);
                        ogrenci.setAdSoyad(adsoyad.getContents());

                        Cell veliadi = ExcelSayfasi.getCell(3, j);
                        ogrenci.setVeliAdi(veliadi.getContents());

                        Cell telno = ExcelSayfasi.getCell(4, j);
                        ogrenci.setTelno(telno.getContents());
                        ogrenciList.add(ogrenci);
                    }

                    for (int j = 1; j < yoklamaSayfasi.getRows(); j++) {
                        Ogrenci ogrenci = new Ogrenci();

                        Cell sinif = yoklamaSayfasi.getCell(0, j);
                        ogrenci.setSinif(sinif.getContents());

                        Cell okulno = yoklamaSayfasi.getCell(1, j);
                        ogrenci.setOkulno(okulno.getContents());

                        Cell adsoyad = yoklamaSayfasi.getCell(2, j);
                        ogrenci.setAdSoyad(adsoyad.getContents());

                        Cell durum = yoklamaSayfasi.getCell(3, j);
                        ogrenci.setDurum(durum.getContents());

                        Cell tarih = yoklamaSayfasi.getCell(4, j);
                        ogrenci.setTarih(tarih.getContents());
                        yedekList.add(ogrenci);
                    }

                    Veritabani vt = new Veritabani(getApplicationContext());
                    List<String> kayitliSiniflar = new ArrayList<>();
                    List<String> kayitliKursSiniflar = new ArrayList<>();
                    List<Ogrenci> kayitliOgrenciler = new ArrayList<>();
                    List<Ogrenci> kayitliYoklamalar = new ArrayList<>();
                    List<OgrenciForYazili> kayitliYazililar = new ArrayList<>();
                    List<String> kayitliDersler = new ArrayList<>();
                    kayitliOgrenciler = vt.getirOgrenci();
                    kayitliSiniflar = vt.getirSinif();
                    kayitliKursSiniflar = vt.getirKursSinif();
                    kayitliYoklamalar = vt.tumYoklamaKaydiGetir();

                    if (sayfalar.length > 4) {
                        Sheet yaziliSayfasi = CalismaKitabi.getSheet(3);
                        Sheet dersSayfasi = CalismaKitabi.getSheet(4);

                        for (int i = 1; i < dersSayfasi.getColumn(0).length; i++) {
                            Cell ders = dersSayfasi.getCell(0, i);
                            dersList.add(ders.getContents());
                        }

                        for (int i = 1; i < yaziliSayfasi.getColumn(0).length; i++) {
                            OgrenciForYazili ogrenci = new OgrenciForYazili();
                            Cell sinif = yaziliSayfasi.getCell(0, i);
                            ogrenci.setSinif(sinif.getContents());
                            Cell okulno = yaziliSayfasi.getCell(1, i);
                            ogrenci.setOkulno(okulno.getContents());
                            Cell adsoyad = yaziliSayfasi.getCell(2, i);
                            ogrenci.setAdSoyad(adsoyad.getContents());
                            Cell telno = yaziliSayfasi.getCell(3, i);
                            ogrenci.setTelno(telno.getContents());
                            Cell yazili1 = yaziliSayfasi.getCell(4, i);
                            ogrenci.setYazili1(yazili1.getContents());
                            Cell yazili2 = yaziliSayfasi.getCell(5, i);
                            ogrenci.setYazili2(yazili2.getContents());
                            Cell ders = yaziliSayfasi.getCell(6, i);
                            ogrenci.setDers(ders.getContents());
                            yaziliList.add(ogrenci);
                        }

                        kayitliYazililar = vt.tumYaziliKayitlariniGetir();
                        kayitliDersler = vt.okutulanDersleriGetir();

                        for (String yedektenGelen : dersList) {
                            boolean durum = false;
                            for (String kayitliOlan : kayitliDersler) {
                                if (yedektenGelen.equals(kayitliOlan)) {
                                    durum = true;
                                }
                            }

                            if (durum == false) {
                                long id = vt.okutulanDersiKaydet(yedektenGelen);
                                if (id > 0) {
                                } else {
                                    Log.e("Ders Kayıt", yedektenGelen + " kaydedilemedi");
                                }
                            }
                        }

                        if (yaziliKaydiGetirme == true) {
                            boolean durumYedekYazili1 = false;
                            boolean durumYedekYazili2 = false;

                            for (OgrenciForYazili ogrenci : yaziliList) {
                                if (!TextUtils.isEmpty(ogrenci.getYazili1())) {
                                    durumYedekYazili1 = true;
                                }
                                if (!TextUtils.isEmpty(ogrenci.getYazili2())) {
                                    durumYedekYazili2 = true;
                                }
                            }

                            for (OgrenciForYazili yedektenGelen : yaziliList) {
                                boolean durumSinifYaziliKaydi = false;
                                boolean durumYazili1 = false;
                                boolean durumYazili2 = false;
                                for (OgrenciForYazili kayitliOlan : kayitliYazililar) {
                                    if (yedektenGelen.getSinif().equals(kayitliOlan.getSinif()) && yedektenGelen.getDers().equals(kayitliOlan.getDers())) {
                                        durumSinifYaziliKaydi = true;
                                        if (!TextUtils.isEmpty(kayitliOlan.getYazili1())) {
                                            durumYazili1 = true;
                                        }
                                        if (!TextUtils.isEmpty(kayitliOlan.getYazili2())) {
                                            durumYazili2 = true;
                                        }
                                    }
                                }

                                if (durumSinifYaziliKaydi == false) {
                                    long id = vt.yaziliKaydet(yedektenGelen);
                                    if (id > 0) {
                                    } else {
                                        Log.e("Yazılı Kayıt", yedektenGelen.getAdSoyad() + " " + yedektenGelen.getDers() + " yazılılar kaydedilemedi");
                                    }
                                } else if (durumYedekYazili1 == true && durumYazili1 == false) {
                                    long id = vt.yazili1iGuncelle(yedektenGelen);
                                    if (id > 0) {
                                    } else {
                                        Log.e("Yazılı Kayıt", yedektenGelen.getAdSoyad() + " " + yedektenGelen.getDers() + " 1.yazılı kaydedilemedi");
                                    }
                                } else if (durumYedekYazili2 == true && durumYazili2 == false) {
                                    long id = vt.yazili2yiGuncelle(yedektenGelen);
                                    if (id > 0) {
                                    } else {
                                        Log.e("Yazılı Kayıt", yedektenGelen.getAdSoyad() + " " + yedektenGelen.getDers() + " 2.yazılı kaydedilemedi");
                                    }
                                }
                            }
                        }

                    }

                    for (String yedektenGelen : sinifList) {
                        boolean durumSinif = false;
                        for (String kayitli : kayitliSiniflar) {
                            if (yedektenGelen.equals(kayitli)) {
                                durumSinif = true;
                            }
                        }
                        if (durumSinif == false) {
                            long id = vt.ekleSinif(yedektenGelen);
                            if (id > 0) {
                            } else {
                                Log.e("Sınıf Kayıt", yedektenGelen + " kaydedilemedi");
                            }
                        }
                    }

                    for (String yedektenGelen : kursSinifList) {
                        boolean durumSinif = false;
                        for (String kayitli : kayitliKursSiniflar) {
                            if (yedektenGelen.equals(kayitli)) {
                                durumSinif = true;
                            }
                        }
                        if (durumSinif == false) {
                            long id = vt.ekleKursSinif(yedektenGelen);
                            if (id > 0) {
                            } else {
                                Log.e("Sınıf Kayıt", yedektenGelen + " kaydedilemedi");
                            }
                        }
                    }

                    for (Ogrenci yedektenGelen : ogrenciList) {
                        boolean durumOgenci = false;
                        for (Ogrenci kayitli : kayitliOgrenciler) {
                            if (yedektenGelen.getOkulno().equals(kayitli.getOkulno()) && yedektenGelen.getSinif().equals(kayitli.getSinif())) {
                                durumOgenci = true;
                            }
                        }
                        if (durumOgenci == false) {
                            long id = vt.ekleOgrenci(yedektenGelen);
                            if (id > 0) {
                            } else {
                                Log.e("Öğrenci Kayıt", yedektenGelen.getSinif() + " " + yedektenGelen.getAdSoyad() + " kaydedilemedi");
                            }
                        } else {
                            long id = vt.ogrenciGuncelle(yedektenGelen);
                            if (id > 0) {
                            } else {
                                Log.e("Öğrenci Kayıt", yedektenGelen.getSinif() + " " + yedektenGelen.getAdSoyad() + " güncellenemedi");
                            }
                        }
                    }

                    for (Ogrenci yedektenGelen : yedekList) {
                        boolean durumOgenci = false;
                        for (Ogrenci kayitli : kayitliYoklamalar) {
                            if (yedektenGelen.getOkulno().equals(kayitli.getOkulno()) && yedektenGelen.getSinif().equals(kayitli.getSinif()) && yedektenGelen.getTarih().equals(kayitli.getTarih())) {
                                durumOgenci = true;
                            }
                        }
                        if (durumOgenci == false) {
                            long id = vt.yoklamaKaydet(yedektenGelen, yedektenGelen.getDurum(), yedektenGelen.getTarih());
                            if (id > 0) {
                            } else {
                                Log.e("Öğrenci Kayıt", yedektenGelen.getSinif() + " " + yedektenGelen.getAdSoyad() + " kaydedilemedi");
                            }
                        }
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (BiffException e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                hatakodu = 21;
            }
           */ return null;
        }
    }

    public void openDirectory() {
        String[] mimetypes =
                { "application/vnd.ms-excel", // .xls
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"}; // .xlsx

        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
        startActivityForResult(intent, FILE_SELECT_CODE);
    }

    private void showFileChooser() {
        AlertDialog.Builder builder=new AlertDialog.Builder(Ayarlar.this);
        builder.setTitle("DİKKAT!");
        builder.setMessage("Yedekteki yazılı notları eski dönemlere ait olabilir.");
        builder.setNegativeButton("Yazılılar gelmesin", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                yaziliKaydiGetirme=false;
                openDirectory();
            }
        });

        builder.setPositiveButton("Yazılılar gelsin", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                yaziliKaydiGetirme=true;
                openDirectory();
            }
        });
        AlertDialog alertDialog=builder.create();
        alertDialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData) {
        super.onActivityResult(requestCode, resultCode, resultData);
        if (requestCode == FILE_SELECT_CODE
                && resultCode == Activity.RESULT_OK) {
            // The result data contains a URI for the document or directory that
            // the user selected.
            Uri uri = null;
            if (resultData != null) {
                fileUri = resultData.getData();
                Log.e("PATH",fileUri.getPath());
                new YedektenAl().execute();
            }
        }
    }

    private void menuButtonsVisibilitySecond(){
        fm.beginTransaction().show(menuContentFragment).commit();
        buttonMenuOpen.setVisibility(View.INVISIBLE);
        buttonMenuClose.setVisibility(View.VISIBLE);
    }

    private void menuButtonsVisibilityFirst(){
        fm.beginTransaction().hide(menuContentFragment).commit();
        buttonMenuOpen.setVisibility(View.VISIBLE);
        buttonMenuClose.setVisibility(View.INVISIBLE);
    }

    @Override
    public void menuButtonsVisibility() {
        menuButtonsVisibilityFirst();
    }

    @Override
    public void onBackPressed() {
        androidx.appcompat.app.AlertDialog.Builder builder=new androidx.appcompat.app.AlertDialog.Builder(Ayarlar.this);
        builder.setTitle("Uygulamadan çıkılsın mı?");
        builder.setPositiveButton("Çıkış", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent a = new Intent(Intent.ACTION_MAIN);
                a.addCategory(Intent.CATEGORY_HOME);
                a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(a);
                finishAffinity();
            }
        });
        builder.setNegativeButton("İptal", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        androidx.appcompat.app.AlertDialog alertDialog=builder.create();
        alertDialog.show();
    }
}
