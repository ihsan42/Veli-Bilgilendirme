package com.egitimyazilim.iletisim.mesajlarvelibilgilendirme;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class GecmisYoklamaGirme extends DialogFragment{

    String gelensinif;
    String gelenokulno;
    String gelenadsoyad;
    RadioButton radioButtonYok;
    RadioButton radioButtonGec;
    RadioButton radioButtonRaporlu;
    RadioButton radioButtonIzinli;
    TextView textView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.gecmis_yoklama_girmee,null,false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        radioButtonGec=(RadioButton)view.findViewById(R.id.radioButtonGec);
        radioButtonYok=(RadioButton)view.findViewById(R.id.radioButtonYok);
        radioButtonRaporlu=(RadioButton)view.findViewById(R.id.radioButtonRaporlu);
        radioButtonIzinli=(RadioButton)view.findViewById(R.id.radioButtonIzinli);
        textView=(TextView)view.findViewById(R.id.textViewGecmisYokTitle);

        final List<String> tarihler=new ArrayList<>();
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd");
        final String tarih= df2.format(c.getTime());
        tarihler.add(tarih);

        CalendarView calendarView=(CalendarView)view.findViewById(R.id.calendarView);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                String gun=String.valueOf(dayOfMonth);
                if(gun.length()==1){
                    gun=0+gun;
                }
                String ay=String.valueOf(month+1);
                if(ay.length()==1){
                    ay=0+ay;
                }
                String yil=String.valueOf(year);
                tarihler.add(yil+"-"+ay+"-"+gun);
            }
        });

        Button buttonOk=(Button)view.findViewById(R.id.buttonOk);
        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tarihler.get(tarihler.size()-1).compareTo(tarihler.get(0))>0){
                    Toast.makeText(getActivity(),"Lütfen ileri tarih seçmeyin!",Toast.LENGTH_SHORT).show();
                }else{
                    Veritabani vt=new Veritabani(getActivity());
                    List<Ogrenci> kayitlar=new ArrayList<>();
                    kayitlar=vt.tumYoklamaKaydiGetir();
                    boolean stokdurumu=false;
                    for(Ogrenci kayit:kayitlar){
                        if(gelensinif.equals(kayit.getSinif())&&gelenokulno.equals(kayit.getOkulno())&&tarihler.get(tarihler.size()-1).equals(kayit.getTarih())){
                            stokdurumu=true;
                        }
                    }

                    if(stokdurumu==false){
                        Ogrenci ogrenci=new Ogrenci();
                        ogrenci.setSinif(gelensinif);
                        ogrenci.setOkulno(gelenokulno);
                        ogrenci.setAdSoyad(gelenadsoyad);
                        String durum="";
                        if(radioButtonYok.isChecked()){
                            durum="yok";
                        }else if(radioButtonGec.isChecked()){
                            durum="geç";
                        }else if(radioButtonRaporlu.isChecked()){
                            durum="raporlu";
                        }else if(radioButtonIzinli.isChecked()){
                            durum="izinli";
                        }

                        long id=vt.yoklamaKaydet(ogrenci,durum,tarihler.get(tarihler.size()-1));
                        if(id>0){
                            Toast.makeText(getActivity(),ogrenci.getAdSoyad()+" "+tarihler.get(tarihler.size()-1)+" "+durum+" olarak kaydedi",Toast.LENGTH_SHORT).show();
                            CommYoklama commYoklama=(CommYoklama)getActivity();
                            commYoklama.gecmisYoklamaOkOnClick(ogrenci.getSinif());
                            dismiss();
                        }else{
                            Toast.makeText(getActivity(),ogrenci.getAdSoyad()+" yoklama durumu kaydedilirken hata oluştu",Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(getActivity(),gelenadsoyad+" "+tarihler.get(tarihler.size()-1)+" tarihli kayıt mevcut",Toast.LENGTH_SHORT).show();
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

    public void gelenOgrenci(String sinif,String okulno,String adsoyad) {
        this.gelensinif=sinif;
        this.gelenokulno=okulno;
        this.gelenadsoyad=adsoyad;
    }
}
