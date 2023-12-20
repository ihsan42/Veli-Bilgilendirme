package com.egitimyazilim.iletisim.mesajlarvelibilgilendirme;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.egitimyazilim.iletisim.mesajlarvelibilgilendirme.adapters.AdapterForPdfdenAl;
import com.egitimyazilim.iletisim.mesajlarvelibilgilendirme.fragments.MenuContentFragment;
import com.egitimyazilim.iletisim.mesajlarvelibilgilendirme.interfaces.MenuContentComm;
import com.egitimyazilim.iletisim.mesajlarvelibilgilendirme.object_classes.Ogrenci;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ExceldenListeAl extends AppCompatActivity implements MenuContentComm {
    private static final int FILE_SELECT_CODE = 55;
    FragmentManager fm;
    Button buttonMenuOpen;
    Button buttonMenuClose;
    MenuContentFragment menuContentFragment;
    Uri fileUri;
    String sinifadi = "";
    List<String> stringList;
    List<Ogrenci> ogrenciList = new ArrayList<>();
    ListView listView;
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

         showFileChooser();

        Button buttonSec = (Button) findViewById(R.id.buttonExceldenSec);
        buttonSec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             showFileChooser();
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

    public void openDirectory() {
        String[] mimetypes =
                { "application/vnd.ms-excel", // .xls
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"}; // .xlsx

        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
      //  intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
        startActivityForResult(intent, FILE_SELECT_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData) {
        super.onActivityResult(requestCode, resultCode, resultData);
        if (requestCode == FILE_SELECT_CODE
                && resultCode == Activity.RESULT_OK) {

            if (resultData != null) {
                fileUri = resultData.getData();

                ContentResolver cR = getApplicationContext().getContentResolver();
                MimeTypeMap mime = MimeTypeMap.getSingleton();
                String type = mime.getExtensionFromMimeType(cR.getType(fileUri));

               if(TextUtils.isEmpty(type)){
                   AlertDialog.Builder builder = new AlertDialog.Builder(ExceldenListeAl.this);
                   builder.setTitle("HATA!");
                   builder.setMessage("Seçtiğiniz dosya bu klasördeyken cihazınız tarafından tanınmıyor. " +
                           "Genelde bu hata 'İndirilenler'den dosya seçilirken meydana gelir." +
                           " Lütfen bu dosyayı 'İndirilenler'den değil de 'Dahili Depolama/ Download'" +
                           " klasöründen seçiniz.");
                   builder.setNeutralButton("Kapat", new DialogInterface.OnClickListener() {
                       @Override
                       public void onClick(DialogInterface dialog, int which) {
                           dialog.dismiss();
                       }
                   });
                   AlertDialog dialog=builder.create();
                   dialog.show();
               }else{
                   Log.e("PATH",type);
                   if(type.equals("XLS") || type.equals("xls")
                           || type.equals("XLSX") || type.equals("xlsx")){
                       new Listele().execute();
                   }else{
                       AlertDialog.Builder builder = new AlertDialog.Builder(ExceldenListeAl.this);
                       builder.setTitle("HATA!");
                       builder.setMessage("Excel dosyası seçmediniz! Lüften  uzantısı '.XLS' , '.xls' , '.XLSX'" +
                               " veya '.xlsx' olan Excel dosyası seçiniz.");
                       builder.setNeutralButton("Kapat", new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface dialog, int which) {
                               dialog.dismiss();
                           }
                       });
                       AlertDialog dialog=builder.create();
                       dialog.show();
                   }
               }
            }
        }
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
               /* Intent intent = new Intent(getApplicationContext(), FileChooserActivity.class);
                intent.putExtra("excel", 1);
                startActivityForResult(intent, FILE_SELECT_CODE);*/


                openDirectory();

                dialog.dismiss();
            }
        });

        SharedPreferences sharedPref = getSharedPreferences("Bir daha gösteme", MODE_PRIVATE);
        boolean durumBirdaha = sharedPref.getBoolean("durum", false);
        if (durumBirdaha == false) {
            dialog.show();
        } else {
            /*Intent intent = new Intent(getApplicationContext(), FileChooserActivity.class);
            intent.putExtra("excel", 1);
            startActivityForResult(intent, FILE_SELECT_CODE);*/
            openDirectory();
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
    public void onBackPressed() {
        super.onBackPressed();
        Intent ıntent = new Intent(getApplicationContext(), Siniflar.class);
        startActivity(ıntent);
    }

    private class Listele extends AsyncTask<Void, Void, Void> {
        ProgressDialog progressDialog;
        String hatakodu = "";

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
            if (hatakodu.equals("")) {
                AdapterForPdfdenAl adapter = new AdapterForPdfdenAl(getApplicationContext(), stringList);
                if (stringList.size() > 0) {
                    listView.setAdapter(adapter);
                    Toast.makeText(getApplicationContext(), "Listelendi", Toast.LENGTH_SHORT).show();
                } else {
                    listView.setAdapter(null);
                    Toast.makeText(getApplicationContext(), "Listelenecek öğrenci yok!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), hatakodu, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected Void doInBackground(Void... voids) {
            ogrenciList = new ArrayList<>();
            stringList = new ArrayList<>();

            InputStream inputStream = null;
            Workbook workbook = null;
            try {
                inputStream = getContentResolver().openInputStream(fileUri);
                workbook = WorkbookFactory.create(inputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Sheet sheet = workbook.getSheetAt(0);
            if (sheet.getLastRowNum() > 0) {
                String siraNo = "S.No";
                Boolean eOkulOrtaOkuldanMi = false;
                Cell cell = sheet.getRow(0).getCell(0);
                if (cell != null) {
                    switch (cell.getCellType()) {
                        case NUMERIC:
                            if (String.valueOf(cell.getNumericCellValue()).equals(siraNo)) {
                                eOkulOrtaOkuldanMi = true;
                            }
                            break;
                        case STRING:
                            if (cell.getStringCellValue().equals(siraNo)) {
                                eOkulOrtaOkuldanMi = true;
                            }
                            break;
                    }
                }

                Boolean eOkulLisedenMi = false;
                if(sheet.getLastRowNum()>2){
                    cell = sheet.getRow(3).getCell(0);
                    if (cell != null) {
                        switch (cell.getCellType()) {
                            case NUMERIC:
                                if (String.valueOf(cell.getNumericCellValue()).equals(siraNo)) {
                                    eOkulLisedenMi = true;
                                }
                                break;
                            case STRING:
                                if (cell.getStringCellValue().equals(siraNo)) {
                                    eOkulLisedenMi = true;
                                }
                                break;
                        }
                    }
                }

                String ogrenciNo = "ÖĞRENCİ NO";
                Boolean taslaktanMi = false;
                cell = sheet.getRow(0).getCell(1);
                if (cell != null) {
                    switch (cell.getCellType()) {
                        case NUMERIC:
                            if (String.valueOf(cell.getNumericCellValue()).equals(ogrenciNo)) {
                                taslaktanMi = true;
                            }
                            break;
                        case STRING:
                            if (cell.getStringCellValue().equals(ogrenciNo)) {
                                taslaktanMi = true;
                            }
                            break;
                    }
                }

                if (eOkulOrtaOkuldanMi) {
                    Row row = sheet.getRow(sheet.getLastRowNum());
                    sheet.removeRow(row);
                    row = sheet.getRow(sheet.getLastRowNum());
                    sheet.removeRow(row);
                }

                if (eOkulLisedenMi) {
                    Row row = sheet.getRow(sheet.getLastRowNum());
                    sheet.removeRow(row);
                    row = sheet.getRow(sheet.getLastRowNum());
                    sheet.removeRow(row);
                    row = sheet.getRow(sheet.getLastRowNum());
                    sheet.removeRow(row);
                }

               // Log.e("bool", eOkulOrtaOkuldanMi.toString() + "," + eOkulLisedenMi.toString() + "," + taslaktanMi.toString());

                if (eOkulOrtaOkuldanMi || eOkulLisedenMi || taslaktanMi) {
                    if (sheet.getLastRowNum() > 0) {
                        int k=1;
                        if(eOkulLisedenMi){
                            k=4;
                        }
                        for (int r = k; r < sheet.getLastRowNum()+1; r++) {
                            Ogrenci ogrenci = new Ogrenci();
                            Row row = sheet.getRow(r);

                            Cell ogrNoCell = row.getCell(1);
                            if (ogrNoCell != null) {
                                switch (ogrNoCell.getCellType()) {
                                    case NUMERIC:
                                        ogrenci.setOkulno(String.valueOf(Math.round(ogrNoCell.getNumericCellValue())));
                                        break;
                                    case STRING:
                                        ogrenci.setOkulno(ogrNoCell.getStringCellValue().trim());
                                        break;
                                }
                            }

                            Cell ad = null;
                            if (eOkulLisedenMi) {
                                ad = row.getCell(3);
                            } else {
                                ad = row.getCell(4);
                            }

                            Cell soyad = null;
                            if (eOkulLisedenMi) {
                                soyad = row.getCell(7);
                            } else {
                                soyad = row.getCell(9);
                            }

                            String isim="";
                            if(ad!=null){
                                 isim = ad.getStringCellValue().trim();
                            }
                            String soyIsim="";
                            if(soyad!=null){
                                 soyIsim = soyad.getStringCellValue().trim();
                            }
                            ogrenci.setAdSoyad(isim + " " + soyIsim);

                            if (row.getLastCellNum() > 14) {
                                Cell telNo = row.getCell(16);
                                String tel = "";

                                if(telNo!=null){
                                    switch (telNo.getCellType()) {
                                        case NUMERIC:
                                            tel = String.valueOf(Math.round(telNo.getNumericCellValue()));
                                            break;
                                        case STRING:
                                            tel = telNo.getStringCellValue().trim();
                                            break;
                                    }
                                    ogrenci.setTelno(tel);
                                }

                                if (tel.equals("")) {
                                    ogrenci.setTelno("0");
                                }
                                Cell veliAdi = row.getCell(18);
                                if(veliAdi!=null){
                                    ogrenci.setVeliAdi(veliAdi.getStringCellValue().trim());
                                }
                            } else {
                                ogrenci.setTelno("0");
                                ogrenci.setVeliAdi("");
                            }

                            ogrenci.setDurumYok(false);
                            ogrenci.setDurumGec(false);
                            ogrenci.setSinif("");

                            ogrenciList.add(ogrenci);
                            stringList.add(ogrenci.getOkulno() + " " + ogrenci.getAdSoyad() + "\n       " +
                                    " Veli:" + ogrenci.getVeliAdi() + "   Tel:" + ogrenci.getTelno());
                        }
                        for (int i = 1; i < stringList.size() + 1; i++) {
                            stringList.set(i - 1, i + ")  " + stringList.get(i - 1));
                        }
                    } else {
                        hatakodu = "Listede öğrenci yok";
                    }
                } else {
                    hatakodu = "geçersiz excel dosyası";
                }
            } else {
                hatakodu = "excel dosyası boş";
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
