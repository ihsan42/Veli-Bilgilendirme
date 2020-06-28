package com.egitimyazilim.iletisim.mesajlarvelibilgilendirme;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class KisiselBilgiler extends AppCompatActivity {

    ListView listView;
    List<String> dersler;
    EditText editTextDersler;
    EditText editTextAdSoyad;
    EditText editTextBrans;
    EditText editTextOkulAdi;
    ArrayAdapter<String> adapter;
    String adSoyadYeni="";
    String bransYeni="";
    String okulAdiYeni="";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kisisel_bilgiler);

        ActionBar bar=getSupportActionBar();
        bar.hide();

        listView=(ListView)findViewById(R.id.listViewDersler);
        editTextDersler=(EditText)findViewById(R.id.editTextDersler);
        editTextAdSoyad=(EditText)findViewById(R.id.editTextAdSoyad);
        editTextBrans=(EditText)findViewById(R.id.editTextBrans);
        editTextOkulAdi=(EditText)findViewById(R.id.editTextOkulAdi);

        List<String> bilgiler=new ArrayList();
        dersler=new ArrayList();
        Veritabani vt=new Veritabani(getApplicationContext());
        bilgiler=vt.kisiselBilgileriGetir();
        dersler=vt.okutulanDersleriGetir();
        vt.close();
        if(bilgiler.size()>0){
            editTextAdSoyad.setText(bilgiler.get(0));
            editTextBrans.setText(bilgiler.get(1));
            editTextOkulAdi.setText(bilgiler.get(2));
        }
        if(dersler.size()>0){
            adapter=new ArrayAdapter<>(getApplication(),android.R.layout.simple_list_item_1,dersler);
            listView.setAdapter(adapter);
        }else{
            listView.setAdapter(null);
        }

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder builder=new AlertDialog.Builder(KisiselBilgiler.this);
                builder.setTitle("Dikkat!");
                builder.setCancelable(false);
                builder.setMessage("Ders silinsin mi?");
                builder.setPositiveButton("Sil", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Veritabani vt=new Veritabani(getApplicationContext());
                        vt.okutulanDersiSil(dersler.get(position));
                        vt.close();
                        dersler.remove(dersler.get(position));
                        if(dersler.size()>0){
                            adapter=new ArrayAdapter<>(getApplication(),android.R.layout.simple_list_item_1,dersler);
                            listView.setAdapter(adapter);
                        }else{
                            listView.setAdapter(null);
                        }
                    }
                });
                builder.setNegativeButton("İptal", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alertDialog=builder.create();
                alertDialog.show();
                return false;
            }
        });

        Button buttonKaydet=(Button)findViewById(R.id.buttonBilgilerKaydet);
        buttonKaydet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adSoyadYeni=editTextAdSoyad.getText().toString();
                bransYeni=editTextBrans.getText().toString();
                okulAdiYeni=editTextOkulAdi.getText().toString();

                if(adSoyadYeni.equals("") || bransYeni.equals("") || okulAdiYeni.equals("")){
                    Toast.makeText(getApplicationContext(),"Lütfen doldurulması gereken tüm bölümleri doldurunuz!",Toast.LENGTH_SHORT).show();
                }else{
                    Veritabani vt=new Veritabani(getApplicationContext());
                    vt.tumKisiselBilgileriSil();
                    long id=vt.kisiselBilgileriKaydet(adSoyadYeni,bransYeni,okulAdiYeni);
                    if(id>0){
                        hideKeyboard(KisiselBilgiler.this);
                        Toast.makeText(getApplicationContext(),"Bilgiler kaydedildi",Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(getApplicationContext(),"Hata!",Toast.LENGTH_SHORT).show();
                    }
                    vt.close();
                }
            }
        });

        Button buttonEkle=(Button)findViewById(R.id.buttonBilgilerEkle);
        buttonEkle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ders="";
                ders=editTextDersler.getText().toString().trim();

                if(!ders.equals("")){
                    if(!dersler.contains(ders)){
                        Veritabani vt=new Veritabani(getApplicationContext());
                        long id=vt.okutulanDersiKaydet(ders);
                        if(id>0){
                            dersler.add(ders);
                            editTextDersler.setText("");
                            hideKeyboard(KisiselBilgiler.this);
                            if(dersler.size()>0){
                                adapter=new ArrayAdapter<>(getApplication(),android.R.layout.simple_list_item_1,dersler);
                                listView.setAdapter(adapter);
                            }else{
                                listView.setAdapter(null);
                            }
                            Toast.makeText(getApplicationContext(),ders+" eklendi",Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(getApplicationContext(),"Hata! "+ders+" eklenemedi!",Toast.LENGTH_SHORT).show();
                        }
                    }
                }else{
                    Toast.makeText(getApplicationContext(),"Lütfen ders adı giriniz!",Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    public static void hideKeyboard(Activity activity) {
        View v = activity.getWindow().getCurrentFocus();
        if (v != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }
}
