package com.egitimyazilim.iletisim.mesajlarvelibilgilendirme;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class OgrenciGuncelle extends DialogFragment {

    Ogrenci gelenOgrenci;
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

        editTextAdSoyad.setText(gelenOgrenci.getAdSoyad());
        editTextVeliAdi.setText(gelenOgrenci.getVeliAdi());
        editTextOgrNo.setText(gelenOgrenci.getOkulno());
        editTextTelNo.setText(gelenOgrenci.getTelno());

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
                    Ogrenci ogrenci=new Ogrenci();
                    ogrenci.setOkulno(okulno);
                    ogrenci.setTelno(telno);
                    ogrenci.setVeliAdi(veliadi);
                    ogrenci.setAdSoyad(adsoyad);
                    ogrenci.setSinif(gelenOgrenci.getSinif());

                    Veritabani vt=new Veritabani(getActivity());
                    long id=vt.ogrenciGuncelle(ogrenci);
                    if(id>0){
                        Toast.makeText(getActivity(),gelenOgrenci.getAdSoyad()+" güncellendi",Toast.LENGTH_SHORT).show();
                        vt.close();
                        CommOgr commOgr=(CommOgr)getActivity();
                        commOgr.guncellenenOgrenci();
                        dismiss();
                    }else{
                        Toast.makeText(getActivity(),"Güncelleme sırasında hata oluştu!",Toast.LENGTH_SHORT).show();
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

    public void guncellenecekOgrenci(Ogrenci ogrenci) {
        this.gelenOgrenci=new Ogrenci();
        this.gelenOgrenci=ogrenci;
    }
}
