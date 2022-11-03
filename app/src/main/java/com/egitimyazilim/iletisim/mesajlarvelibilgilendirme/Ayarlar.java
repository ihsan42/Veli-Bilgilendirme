package com.egitimyazilim.iletisim.mesajlarvelibilgilendirme;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.Settings;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;
import com.egitimyazilim.iletisim.mesajlarvelibilgilendirme.fragments.MenuContentFragment;
import com.egitimyazilim.iletisim.mesajlarvelibilgilendirme.interfaces.MenuContentComm;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
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
    ActionBar bar;
    Switch switchIsim;
    Switch switchBrans;
    Switch switchOkulAdi;

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
                                SXSSFWorkbook ogrencilerExelDosyasi = new SXSSFWorkbook();
                                SXSSFSheet ogrencilerSayfasi = (SXSSFSheet) ogrencilerExelDosyasi.createSheet("Sinif_Listesi");
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

                                File download=Environment.getExternalStoragePublicDirectory(DOWNLOAD_SERVICE);
                                File file = new File(download,"Veli_Bilg_Sinif_Ekleme_Taslagi.xlsx");

                                FileOutputStream fileOutputStream = new FileOutputStream(file);
                                ogrencilerExelDosyasi.write(fileOutputStream);

                                Toast.makeText(getApplicationContext(), "Excel dosyası 'Download' klasörüne 'Veli_Bilgilendirme_Sinif_Ekleme_Taslagi' adıyla kaydedildi ", Toast.LENGTH_LONG).show();

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
                            Calendar c = Calendar.getInstance();
                            SimpleDateFormat df2 = new SimpleDateFormat("dd.MM.yyyy kk.mm.ss");
                            final String tarihBugun= df2.format(c.getTime());

                            yedekle(Ayarlar.this,"Yedek Veli Bilgilendirme  "+tarihBugun);
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
                   dosyaYoneticisiniAc();
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

    String currentDBPath = "//data/com.egitimyazilim.iletisim.mesajlarvelibilgilendirme" +
            "/databases/sinif_ogrenci_kayitlari";
    public  Boolean yedekle(Context context, String yedekismi){
        try {

            File download=Environment.getExternalStoragePublicDirectory(DOWNLOAD_SERVICE);
            File data = Environment.getDataDirectory();

            if (download.canWrite()) {
                File currentDB = new File(data, currentDBPath);
                File backupDB = new File(download, yedekismi);

                if (currentDB.exists()) {
                    FileChannel src = new FileInputStream(currentDB).getChannel();
                    FileChannel dst = new FileOutputStream(backupDB).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();

                   AlertDialog.Builder builder=new AlertDialog.Builder(context);
                   builder.setTitle("YEDEKLEME BAŞARILI!");
                   builder.setMessage("Dosya yöneticisindeki Download bölümüne '"+yedekismi+"' ismiyle kaydedildi.");
                   builder.setPositiveButton("Kapat", new DialogInterface.OnClickListener() {
                       @Override
                       public void onClick(DialogInterface dialogInterface, int i) {
                           dialogInterface.dismiss();
                       }
                   });
                   builder.setNegativeButton("Download'ı Aç", new DialogInterface.OnClickListener() {
                       @Override
                       public void onClick(DialogInterface dialogInterface, int i) {
                           Intent intent=new Intent(DownloadManager.ACTION_VIEW_DOWNLOADS);
                           startActivity(intent);
                       }
                   });
                   AlertDialog dialog=builder.create();
                   dialog.show();

                    return true;
                }
                else{
                    Toast.makeText(context, "Veritabanı Bulunamadı", Toast.LENGTH_LONG).show();
                    return false;
                }
            }
            else{
                AlertDialog.Builder builder=new AlertDialog.Builder(context);
                builder.setTitle("HATA!");
                builder.setMessage("Dosya yöneticisindeki Download bölümüne kayıt yapılamadı.");
                builder.setPositiveButton("Kapat", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                AlertDialog dialog=builder.create();
                dialog.show();

                return false;
            }
        }
        catch (Exception e) {
            AlertDialog.Builder builder=new AlertDialog.Builder(context);
            builder.setTitle("HATA!");
            builder.setMessage("Hata Oluştu: " + e.getMessage());
            builder.setPositiveButton("Kapat", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            AlertDialog dialog=builder.create();
            dialog.show();

            return false;
        }
    }

    public Boolean yedektenGeriYukle(Context context,File backupDB) {
        try {
            File data = Environment.getDataDirectory();
            File currentDB = new File(data, currentDBPath);

            if (currentDB.exists()) {
                FileChannel src = new FileInputStream(backupDB).getChannel();
                FileChannel dst = new FileOutputStream(currentDB).getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();

                AlertDialog.Builder builder=new AlertDialog.Builder(context);
                builder.setTitle("BAŞARILI!");
                builder.setMessage("Yedekteki veriler başarılı şekilde uygulamaya aktarıldı.");
                builder.setPositiveButton("Kapat", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                AlertDialog dialog=builder.create();
                dialog.show();

                return true;
            } else {
                Toast.makeText(context, "Veritabanı Bulunamadı", Toast.LENGTH_LONG).show();
                return false;
            }
        } catch (Exception e) {
            AlertDialog.Builder builder=new AlertDialog.Builder(context);
            builder.setTitle("HATA!");
            builder.setMessage("Hata Oluştu: " + e.getMessage());
            builder.setPositiveButton("Kapat", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            AlertDialog dialog=builder.create();
            dialog.show();
            return false;
        }
    }

    public void dosyaYoneticisiniAc() {
        String[] mimetypes = {"application/x-sqlite3","application/vnd.sqlite3"
                , "application/octet-stream", "application/x-trash"};

        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
        startActivityForResult(intent, FILE_SELECT_CODE);
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

                File backupDB= null;
                try {
                    backupDB = GetFileFromUri.getFile(getApplicationContext(),fileUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                yedektenGeriYukle(Ayarlar.this,backupDB);
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
