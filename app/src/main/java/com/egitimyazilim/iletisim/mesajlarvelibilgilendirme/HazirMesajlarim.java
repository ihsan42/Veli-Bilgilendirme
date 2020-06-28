package com.egitimyazilim.iletisim.mesajlarvelibilgilendirme;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
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

public class HazirMesajlarim extends AppCompatActivity {

    ListView listViewKayitliMesajlar;
    List<String> kayitliMesajlarim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hazir_mesajlarim);

        ActionBar bar=getSupportActionBar();
        bar.setTitle("Hazır Mesajlarım");

        final EditText editTextHazirMesajim=(EditText)findViewById(R.id.editTextHazirMesajim);
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
                        if(editTextHazirMesajim.getText().toString().equals(kayitliMesajlarim.get(position))){
                            editTextHazirMesajim.setText("");
                        }
                        Veritabani vt= new Veritabani(getApplicationContext());
                        vt.hazirMesajSil(kayitliMesajlarim.get(position));
                        Toast.makeText(getApplicationContext(),"Silindi!",Toast.LENGTH_SHORT).show();
                        listele();
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
                /*AlertDialog.Builder builder2=new AlertDialog.Builder(HazirMesajlarim.this);
                builder2.setMessage(kayitliMesajlarim.get(position));
                AlertDialog alertDialog2=builder2.create();
                alertDialog2.show();*/
                editTextHazirMesajim.setText(kayitliMesajlarim.get(position));
            }
        });

        buttonKaydet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String hazirmesajim="";
                hazirmesajim=editTextHazirMesajim.getText().toString().trim();

                if(hazirmesajim.equals("")){
                    Toast.makeText(getApplicationContext(),"Lütfen mesaj yazınız!",Toast.LENGTH_SHORT).show();
                }else{
                    Veritabani vt=new Veritabani(getApplicationContext());
                    if(kayitliMesajlarim.contains(hazirmesajim)){
                        Toast.makeText(getApplicationContext(),"Bu mesaj zaten kayıtlı",Toast.LENGTH_SHORT).show();
                    }else{
                        long id=vt.hazirMesajKaydet(hazirmesajim);
                        if(id>0){
                            listele();
                            hideKeyboard(HazirMesajlarim.this);
                            editTextHazirMesajim.setText("");
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
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), R.layout.custom_spinner_ders, kayitliMesajlarim);
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
}
