package com.egitimyazilim.iletisim.mesajlarvelibilgilendirme;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class OgrenciEkleme extends DialogFragment {

    String sinifadi="";
    EditText editTextAdSoyad;
    EditText editTextVeliAdi;
    EditText editTextOgrNo;
    EditText editTextTelNo;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.ogrenci_ekleme,null,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        editTextAdSoyad=(EditText)view.findViewById(R.id.editTextAdSoyad);
        editTextVeliAdi=(EditText)view.findViewById(R.id.editTextVeliAd);
        editTextOgrNo=(EditText)view.findViewById(R.id.editTextOgrNo);
        editTextTelNo=(EditText)view.findViewById(R.id.editTextTelNo);
        int maxLength = 11;
        InputFilter[] FilterArray = new InputFilter[1];
        FilterArray[0] = new InputFilter.LengthFilter(maxLength);
        editTextTelNo.setFilters(FilterArray);

        Button buttonOk=(Button)view.findViewById(R.id.buttonOk);
        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String adsoyad=editTextAdSoyad.getText().toString();
                String veliadi=editTextVeliAdi.getText().toString();
                String okulno=editTextOgrNo.getText().toString();
                String telno=editTextTelNo.getText().toString();
                if(adsoyad.equals("")){
                    Toast.makeText(getActivity(),"Ad-Soyad bölümü boş bırakılamaz!",Toast.LENGTH_SHORT).show();
                }else if(okulno.equals("")){
                    Toast.makeText(getActivity(),"Öğrenci No bölümü boş bırakılamaz!",Toast.LENGTH_SHORT).show();
                }else{
                    if(telno.equals("")){
                        telno="0";
                    }
                    if(veliadi.equals("")){
                        veliadi="";
                    }

                    Veritabani vt=new Veritabani(getActivity());
                    List<Ogrenci> ogrencis=new ArrayList<>();
                    ogrencis=vt.getirOgrenci();
                    boolean durum=false;
                    for(Ogrenci ogrenci:ogrencis){
                        if(sinifadi.equals(ogrenci.getSinif())&& okulno.equals(ogrenci.getOkulno())){
                            durum=true;
                        }
                    }

                    Ogrenci ogrenci=new Ogrenci();
                    ogrenci.setSinif(sinifadi);
                    ogrenci.setOkulno(okulno);
                    ogrenci.setAdSoyad(adsoyad);
                    ogrenci.setVeliAdi(veliadi);
                    ogrenci.setTelno(telno);
                    if(durum==false){
                        long id=vt.ekleOgrenci(ogrenci);
                        if(id>0){
                            Toast.makeText(getActivity(),ogrenci.getAdSoyad()+" kaydedildi",Toast.LENGTH_SHORT).show();
                            vt.close();
                            CommOgr commOgr=(CommOgr)getActivity();
                            commOgr.eklenenOgrenci();
                            dismiss();
                        }else{
                            Toast.makeText(getActivity(),"Kayıt sırasında hata oluştu!",Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(getActivity(),ogrenci.getOkulno()+" numaralı öğrenci zaten kayıtlı!",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        Button buttonCancel=(Button)view.findViewById(R.id.buttonCancel);
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    public void gelenSinif(String gelenSinifadi) {
        this.sinifadi=gelenSinifadi;
    }
}
