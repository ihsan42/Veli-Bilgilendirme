package com.egitimyazilim.iletisim.mesajlarvelibilgilendirme;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;

public class ExceldenListeAl extends AppCompatActivity implements MenuContentComm {

    private static final int requestCodePermissionRead = 1990;
    private static final int FILE_SELECT_CODE = 55;
    FragmentManager fm;
    Button buttonMenuOpen;
    Button buttonMenuClose;
    MenuContentFragment menuContentFragment;
    String filePath = "";
    String sinifadi = "";
    List<String> stringList;
    List<Ogrenci> ogrenciList = new ArrayList<>();
    ListView listView;
    int hatakodu1 = 0;
    EditText editTextSinifAdi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_excelden_liste_al);

        ActionBar bar = getSupportActionBar();
        bar.hide();

        buttonMenuOpen = (Button) findViewById(R.id.buttonMenuOpen);
        buttonMenuClose = (Button) findViewById(R.id.buttonMenuClose);

        fm = getSupportFragmentManager();
        menuContentFragment = (MenuContentFragment) fm.findFragmentById(R.id.fragmentMenu);
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

        listView = (ListView) findViewById(R.id.listViewExceldenAl);
        editTextSinifAdi = (EditText) findViewById(R.id.editTextExceldenSinifAdi);

        final String[] depolamaIzni = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE};
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ExceldenListeAl.this, depolamaIzni, requestCodePermissionRead);
        }
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            showFileChooser();
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(ExceldenListeAl.this, depolamaIzni[0])) {
                ActivityCompat.requestPermissions(ExceldenListeAl.this, depolamaIzni, requestCodePermissionRead);
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(ExceldenListeAl.this);
                builder.setTitle("Dikkat!");
                builder.setMessage("Cihazınızdaki Excel dosyasını okuyabilmek için eksik izin var. Depolama iznini vermeniz gereklidir. İzin vermek için <Ayarlar>'a tıklayınız ve açılan sayfadaki izinler bölümüden izin veriniz.");
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

        Button buttonSec = (Button) findViewById(R.id.buttonExceldenSec);
        buttonSec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(ExceldenListeAl.this, depolamaIzni, requestCodePermissionRead);
                }
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    showFileChooser();
                } else {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(ExceldenListeAl.this, depolamaIzni[0])) {
                        ActivityCompat.requestPermissions(ExceldenListeAl.this, depolamaIzni, requestCodePermissionRead);
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(ExceldenListeAl.this);
                        builder.setTitle("Dikkat!");
                        builder.setMessage("Cihazınızdaki Excel dosyasını okuyabilmek için eksik izin var. Depolama iznini vermeniz gereklidir. İzin vermek için <Ayarlar>'a tıklayınız ve açılan sayfadaki izinler bölümüden izin veriniz.");
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

        Button buttonKaydet = (Button) findViewById(R.id.buttonExceldenKaydet);
        buttonKaydet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sinifadi = editTextSinifAdi.getText().toString().trim();
                if (ogrenciList.size() > 0) {
                    if (editTextSinifAdi.getText().toString().trim().equals("")) {
                        Toast.makeText(getApplicationContext(), "Lütfen sınıf adını giriniz!", Toast.LENGTH_SHORT).show();
                    } else if (sinifadi.contains("'") || sinifadi.contains("=") || sinifadi.contains("?") || sinifadi.contains("+") || sinifadi.contains(",") || sinifadi.contains("!")) {
                        Toast.makeText(getApplicationContext(), "Lütfen sınıf adında özel karakterler(?,!'+!) kullanmayınız!", Toast.LENGTH_SHORT).show();
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(ExceldenListeAl.this);
                        builder.setMessage("Lüften sınıf türünü seçiniz");
                        builder.setCancelable(false);
                        builder.setNeutralButton("İptal", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        builder.setPositiveButton("Normal", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                sinifadi = editTextSinifAdi.getText().toString().trim();
                                for (Ogrenci ogrenci : ogrenciList) {
                                    ogrenci.setSinif(sinifadi);
                                }
                                Veritabani vt = new Veritabani(getApplicationContext());
                                List<String> vtList = vt.getirSinif();

                                boolean durum = false;
                                for (String s : vtList) {
                                    if (s.equals(sinifadi.trim())) {
                                        durum = true;
                                    }
                                }

                                if (durum == false) {
                                    long id = vt.ekleSinif(sinifadi);
                                    if (id > 0) {
                                        Toast.makeText(getApplicationContext(), sinifadi + " kaydedildi", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(getApplicationContext(), sinifadi + " kaydedilirken hata oluştu", Toast.LENGTH_SHORT).show();
                                    }
                                    for (Ogrenci ogrenci : ogrenciList) {
                                        long id2 = vt.ekleOgrenci(ogrenci);
                                        if (id2 > 0) {
                                        } else {
                                            Toast.makeText(getApplicationContext(), ogrenci.getAdSoyad() + " kaydedilirken hata oluştu", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                } else {
                                    Toast.makeText(getApplicationContext(), sinifadi + " zaten kayıtlı", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        builder.setNegativeButton("Kurs", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                sinifadi = editTextSinifAdi.getText().toString();
                                sinifadi = sinifadi + ("(Kurs)");
                                for (Ogrenci ogrenci : ogrenciList) {
                                    ogrenci.setSinif(sinifadi);
                                }
                                Veritabani vt = new Veritabani(getApplicationContext());
                                List<String> vtList = vt.getirKursSinif();

                                boolean durum = false;
                                for (String s : vtList) {
                                    if (s.equals(sinifadi.trim())) {
                                        durum = true;
                                    }
                                }

                                if (durum == false) {
                                    long id = vt.ekleKursSinif(sinifadi);
                                    if (id > 0) {
                                        Toast.makeText(getApplicationContext(), sinifadi + " kaydedildi", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(getApplicationContext(), sinifadi + " kaydedilirken hata oluştu", Toast.LENGTH_SHORT).show();
                                    }
                                    for (Ogrenci ogrenci : ogrenciList) {
                                        long id2 = vt.ekleOgrenci(ogrenci);
                                        if (id2 > 0) {
                                        } else {
                                            Toast.makeText(getApplicationContext(), ogrenci.getAdSoyad() + " kaydedilirken hata oluştu", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                } else {
                                    Toast.makeText(getApplicationContext(), sinifadi + " zaten kayıtlı", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), " Kaydedilecek liste yok!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void showFileChooser() {
        final Dialog dialog = new Dialog(ExceldenListeAl.this);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.uyari);
        Button buttonKapat = (Button) dialog.findViewById(R.id.buttonKapat);
        final CheckBox checkBoxBirDahaGosterme = (CheckBox) dialog.findViewById(R.id.checkBoxBirDahaGösterme);

        buttonKapat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), FileChooserActivity.class);
                intent.putExtra("excel", 1);
                startActivityForResult(intent, FILE_SELECT_CODE);
                dialog.dismiss();
            }
        });

        SharedPreferences sharedPref = getSharedPreferences("Bir daha gösteme", MODE_PRIVATE);
        boolean durumBirdaha = sharedPref.getBoolean("durum", false);
        if (durumBirdaha == false) {
            dialog.show();
        } else {
            Intent intent = new Intent(getApplicationContext(), FileChooserActivity.class);
            intent.putExtra("excel", 1);
            startActivityForResult(intent, FILE_SELECT_CODE);
        }

        checkBoxBirDahaGosterme.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences sharedPref = getSharedPreferences("Bir daha gösteme", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                if (isChecked) {
                    editor.putBoolean("durum", true);
                    editor.commit();
                } else {
                    editor.putBoolean("durum", false);
                    editor.commit();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case FILE_SELECT_CODE:
                if (resultCode == FILE_SELECT_CODE) {
                    try {
                        filePath = data.getStringExtra("path");
                        new ExceldenListeAl.Listele().execute();
                    } catch (Error e) {
                        Toast.makeText(getApplicationContext(), "Hata!Lütfen tekrar deneyiniz", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent ıntent = new Intent(getApplicationContext(), Siniflar.class);
        startActivity(ıntent);
    }

    private class Listele extends AsyncTask<Void, Void, Void> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(ExceldenListeAl.this);
            progressDialog.setCancelable(false);
            progressDialog.setTitle("Liste Getiriliyor...");
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressDialog.dismiss();
            if (hatakodu1 == 10) {
                Toast.makeText(getApplicationContext(), "Hata! Excel dosyasının .XLS formatında kaydedildiğinden emin olun.Dosyada değişiklik yaptıysanız <Excel 97-2003 Çalışma Kitabı> formatında kaydettiğinizden emin olun!", Toast.LENGTH_LONG).show();
            } else {
                AdapterForPdfdenAl adapter = new AdapterForPdfdenAl(getApplicationContext(), stringList);
                if (stringList.size() > 0) {
                    listView.setAdapter(adapter);
                    Toast.makeText(getApplicationContext(), "Listelendi", Toast.LENGTH_SHORT).show();
                } else {
                    listView.setAdapter(null);
                    Toast.makeText(getApplicationContext(), "Listelenecek öğrenci yok!", Toast.LENGTH_SHORT).show();
                }

            }
        }

        @Override
        protected Void doInBackground(Void... voids) {
            hatakodu1 = 0;
            ogrenciList = new ArrayList<>();
            stringList = new ArrayList<>();
            File file = new File(filePath);
            try {
                WorkbookSettings vs = new WorkbookSettings();
                vs.setEncoding("CP1254");
                Workbook CalismaKitabi = Workbook.getWorkbook(file, vs);
                Sheet[] sayfalar = CalismaKitabi.getSheets();

                Sheet excelSayfasi = CalismaKitabi.getSheet(0);

                for (int i = 1; i < excelSayfasi.getColumn(1).length; i++) {
                    if (!excelSayfasi.getCell(0, i).getContents().toString().equals("Kız Öğrenci Sayısı        :")) {
                        Ogrenci ogrenci = new Ogrenci();

                        Cell ogrNo = excelSayfasi.getCell(1, i);
                        ogrenci.setOkulno(ogrNo.getContents().trim());

                        Cell ad = excelSayfasi.getCell(3, i);
                        Cell soyad = excelSayfasi.getCell(8, i);
                        String isim = ad.getContents().trim();
                        String soyIsim = soyad.getContents().trim();
                        if (isim.equals("") && soyIsim.equals("")) {
                            Cell ad2 = excelSayfasi.getCell(4, i);
                            Cell soyad2 = excelSayfasi.getCell(9, i);
                            isim = ad2.getContents().trim();
                            soyIsim = soyad2.getContents().trim();
                        }
                        ogrenci.setAdSoyad(isim + " " + soyIsim);

                        if (excelSayfasi.getColumns() > 15) {
                            Cell telNo = excelSayfasi.getCell(15, i);
                            ogrenci.setTelno(telNo.getContents().trim());
                            if (telNo.getContents().trim().equals("")) {
                                ogrenci.setTelno("0");
                            }
                        } else {
                            ogrenci.setTelno("0");
                        }

                        if (excelSayfasi.getColumns() > 17) {
                            Cell veliAdi = excelSayfasi.getCell(17, i);
                            ogrenci.setVeliAdi(veliAdi.getContents().trim());
                        } else {
                            ogrenci.setVeliAdi("");
                        }


                        ogrenci.setDurumYok(false);
                        ogrenci.setDurumGec(false);
                        ogrenci.setSinif("");

                        ogrenciList.add(ogrenci);
                        stringList.add(ogrenci.getOkulno() + " " + isim + " " + soyIsim + "\n        Veli:" + ogrenci.getVeliAdi() + "   Tel:" + ogrenci.getTelno());
                    }
                }
                for (int i = 1; i < stringList.size() + 1; i++) {
                    stringList.set(i - 1, i + ")  " + stringList.get(i - 1));
                }
            } catch (Exception e) {
                hatakodu1 = 10;
                Log.e("hata", e.toString());
            }
            return null;
        }
    }

    private void menuButtonsVisibilitySecond() {
        fm.beginTransaction().show(menuContentFragment).commit();
        buttonMenuOpen.setVisibility(View.INVISIBLE);
        buttonMenuClose.setVisibility(View.VISIBLE);
    }

    private void menuButtonsVisibilityFirst() {
        fm.beginTransaction().hide(menuContentFragment).commit();
        buttonMenuOpen.setVisibility(View.VISIBLE);
        buttonMenuClose.setVisibility(View.INVISIBLE);
    }

    @Override
    public void menuButtonsVisibility() {
        menuButtonsVisibilityFirst();
    }
}
