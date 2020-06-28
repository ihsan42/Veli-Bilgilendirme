package com.egitimyazilim.iletisim.mesajlarvelibilgilendirme;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SinifGuncelleme extends DialogFragment {

    EditText editText;
    String gelenSinifAdi="";
    int aktifSayfaIndeksi;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.sinif_guncelleme,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        editText=(EditText)view.findViewById(R.id.editTextSinifGuncelleme);
        if(aktifSayfaIndeksi==0){
            editText.setText(gelenSinifAdi);
        }else if(aktifSayfaIndeksi==1){
            String[] s=gelenSinifAdi.split(" ");
            s[s.length-1]="";
            String sinifadi="";
            for(String a:s){
                sinifadi+=a+" ";
            }
            editText.setText(sinifadi.trim());
        }
        Button buttonOk=(Button)view.findViewById(R.id.buttonSinifGuncellemeTamam);
        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sinif="";
                sinif=editText.getText().toString().trim();
                if(sinif.equals("")){
                    Toast.makeText(getActivity(),"Sınıf adı boş bırakılamaz! Lütfen yeni sınıf adını giriniz!",Toast.LENGTH_SHORT).show();
                }else{
                    if(aktifSayfaIndeksi==0){
                        if(sinif.contains("(Kurs)")){
                            Toast.makeText(getActivity(),"Lütfen normal sınıf güncellerken sınıf adına (Kurs) yazmayın!",Toast.LENGTH_SHORT).show();
                        }else{
                            Veritabani vt=new Veritabani(getActivity());
                            long id=vt.guncelleSinif(gelenSinifAdi,sinif);
                            vt.close();

                            if(id>0){
                                Toast.makeText(getActivity(),gelenSinifAdi+" sınıfı "+sinif+" olarak güncellendi!",Toast.LENGTH_SHORT).show();
                                CommSinif commSinif=(CommSinif)getActivity();
                                commSinif.normalSinifGuncellemeOkOnClick();
                                dismiss();
                            }else{
                                Toast.makeText(getActivity(),gelenSinifAdi+" güncellenirken hata oluştu!",Toast.LENGTH_SHORT).show();
                            }
                        }
                    }else if(aktifSayfaIndeksi==1){
                        Veritabani vt=new Veritabani(getActivity());
                        long id=vt.guncelleKursSinif(gelenSinifAdi,sinif+" (Kurs)");
                        vt.close();

                        if(id>0){
                            Toast.makeText(getActivity(),gelenSinifAdi+" sınıfı "+sinif+" olarak güncellendi!",Toast.LENGTH_SHORT).show();
                            CommSinif commSinif=(CommSinif)getActivity();
                            commSinif.kursSinifGuncellemeOkOnClick();
                            dismiss();
                        }else{
                            Toast.makeText(getActivity(),gelenSinifAdi+" güncellenirken hata oluştu!",Toast.LENGTH_SHORT).show();
                        }
                    }

                }
            }
        });

        Button buttonCancel=(Button)view.findViewById(R.id.buttonSinifGuncellemeIptal);
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              dismiss();
            }
        });



    }

    public void gelenSinif(String sinifadi,int activePageIndex) {
        this.gelenSinifAdi=sinifadi;
        this.aktifSayfaIndeksi=activePageIndex;
    }
}
