package com.egitimyazilim.iletisim.mesajlarvelibilgilendirme;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.egitimyazilim.iletisim.mesajlarvelibilgilendirme.adapters.AdapterForOkutulanDersler;
import com.egitimyazilim.iletisim.mesajlarvelibilgilendirme.fragments.MenuContentFragment;
import com.egitimyazilim.iletisim.mesajlarvelibilgilendirme.interfaces.MenuContentComm;

import java.util.ArrayList;
import java.util.List;

public class HazirMesajlarim extends AppCompatActivity implements MenuContentComm {

    FragmentManager fm;
    Button buttonMenuOpen;
    Button buttonMenuClose;
    MenuContentFragment menuContentFragment;
    ListView listViewKayitliMesajlar;
    List<String> kayitliMesajlarim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hazir_mesajlarim);

        ActionBar bar=getSupportActionBar();
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

        final EditText editTextHazirMesajim1=(EditText)findViewById(R.id.editTextHazirMesajim1);
        final EditText editTextHazirMesajim2=(EditText)findViewById(R.id.editTextHazirMesajim2);
        listViewKayitliMesajlar=(ListView)findViewById(R.id.listViewKayitliMesajlarim);
        Button buttonKaydet=(Button)findViewById(R.id.buttonMesajiKaydet);

        listele();

        listViewKayitliMesajlar.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,final int position, long id) {
                AlertDialog.Builder builder=new AlertDialog.Builder(HazirMesajlarim.this);
                builder.setTitle("Dikkat!");
                builder.setMessage("Bu mesaj silinsin mi?");
                builder.setPositiveButton("Sil", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(kayitliMesajlarim.get(position).contains(" <ad-soyad> ")){
                            String hazirmesajim1=kayitliMesajlarim.get(position).split(" <ad-soyad> ")[0];
                            String hazirmesajim2=kayitliMesajlarim.get(position).split(" <ad-soyad> ")[1];

                            if(editTextHazirMesajim1.getText().toString().equals(hazirmesajim1) && editTextHazirMesajim2.getText().toString().equals(hazirmesajim2)){
                                editTextHazirMesajim1.setText("");
                                editTextHazirMesajim2.setText("");
                            }
                        }


                        Veritabani vt= new Veritabani(getApplicationContext());
                        String mesaj=kayitliMesajlarim.get(position);
                        if(mesaj.contains("'")){
                            mesaj=mesaj.replaceAll("'","/");
                        }

                        if(mesaj.contains("\"")){
                            mesaj=mesaj.replaceAll("\"","//");
                        }
                        long id=vt.hazirMesajSil(mesaj);
                        if(id<0){
                            Toast.makeText(getApplicationContext(),"Hata! Silinemedi!",Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(getApplicationContext(),"Silindi!",Toast.LENGTH_SHORT).show();
                            listele();
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

        listViewKayitliMesajlar.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(kayitliMesajlarim.get(position).contains(" <ad-soyad> ")){
                    String hazirmesajim1=kayitliMesajlarim.get(position).split(" <ad-soyad> ")[0];
                    String hazirmesajim2=kayitliMesajlarim.get(position).split(" <ad-soyad> ")[1];
                    editTextHazirMesajim1.setText(hazirmesajim1);
                    editTextHazirMesajim2.setText(hazirmesajim2);
                }else{
                    editTextHazirMesajim2.setText(kayitliMesajlarim.get(position));
                }
            }
        });

        buttonKaydet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String hazirmesajim1="";
                String hazirmesajim2="";
                hazirmesajim1=editTextHazirMesajim1.getText().toString().trim();
                hazirmesajim2=editTextHazirMesajim2.getText().toString().trim();

                String tamMesajim=hazirmesajim1+" <ad-soyad> "+hazirmesajim2;

                if(tamMesajim.length()<=12){
                    Toast.makeText(getApplicationContext(),"Lütfen mesaj yazınız!",Toast.LENGTH_SHORT).show();
                }else{
                    Veritabani vt=new Veritabani(getApplicationContext());
                    if(kayitliMesajlarim.contains(tamMesajim)){
                        Toast.makeText(getApplicationContext(),"Bu mesaj zaten kayıtlı",Toast.LENGTH_SHORT).show();
                    }else{
                        if(tamMesajim.contains("'")){
                            tamMesajim=tamMesajim.replaceAll("'","/");
                        }
                        if(tamMesajim.contains("\"")){
                            tamMesajim=tamMesajim.replaceAll("\"","//");
                        }

                        long id=vt.hazirMesajKaydet(tamMesajim);
                        if(id>0){
                            listele();
                            hideKeyboard(HazirMesajlarim.this);
                            editTextHazirMesajim1.setText("");
                            editTextHazirMesajim2.setText("");
                            Toast.makeText(getApplicationContext(),"Hazır mesaj kaydedildi.",Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(getApplicationContext(),"Hazır mesaj kaydedilirken hata oluştu!",Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });
    }

    private void listele(){
        Veritabani vt=new Veritabani(getApplicationContext());
        kayitliMesajlarim=new ArrayList<>();
        kayitliMesajlarim=vt.hazirMesajlarimiGetir();
        vt.close();
        if(kayitliMesajlarim.size()>0){
            AdapterForOkutulanDersler adapter = new AdapterForOkutulanDersler(getApplicationContext(), kayitliMesajlarim);
            listViewKayitliMesajlar.setAdapter(adapter);
        }else{
            listViewKayitliMesajlar.setAdapter(null);
        }
    }

    public static void hideKeyboard(Activity activity) {
        View v = activity.getWindow().getCurrentFocus();
        if (v != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
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

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        androidx.appcompat.app.AlertDialog.Builder builder=new androidx.appcompat.app.AlertDialog.Builder(HazirMesajlarim.this);
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
