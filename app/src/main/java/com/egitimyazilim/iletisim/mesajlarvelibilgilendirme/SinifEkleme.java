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

import java.util.ArrayList;
import java.util.List;

public class
SinifEkleme extends DialogFragment {

    int activePageIndex;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.sinif_ekleme,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final EditText editText=(EditText)view.findViewById(R.id.editTextSinifEkleme);

        Button buttonOk=(Button)view.findViewById(R.id.buttonSinifEklemeTamam);
        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(activePageIndex==0){
                    String sinifadi=editText.getText().toString().trim();
                    if(sinifadi.equals("")){
                        Toast.makeText(getActivity(),"Lütfen sınıf ismi giriniz!",Toast.LENGTH_SHORT).show();
                    }else{
                        if(sinifadi.contains("(Kurs)")){
                            Toast.makeText(getActivity(),"Lütfen normal sınıf eklerken sınıf adına (Kurs) yazmayın!",Toast.LENGTH_SHORT).show();
                        }else if(sinifadi.contains("'") || sinifadi.contains("=")|| sinifadi.contains("?")|| sinifadi.contains("+")|| sinifadi.contains(",")|| sinifadi.contains("!")){
                            Toast.makeText(getActivity(),"Lütfen sınıf adında özel karakterler(?,!'+!) kullanmayınız!",Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Veritabani vt=new Veritabani(getActivity());
                            List<String> stringList=new ArrayList<>();
                            stringList=vt.getirSinif();
                            vt.close();

                            boolean durum=false;
                            for(String sinif:stringList){
                                if(sinif.equals(sinifadi)){
                                    durum=true;
                                }
                            }

                            if(durum==false){
                                long id=vt.ekleSinif(sinifadi);
                                vt.close();
                                if(id>0){
                                    Toast.makeText(getActivity(),sinifadi+" sınıfı eklendi",Toast.LENGTH_SHORT).show();
                                    CommSinif commSinif=(CommSinif)getActivity();
                                    commSinif.normalSinifEklemeOkOnClick();
                                    dismiss();
                                }else{
                                    Toast.makeText(getActivity(),"Ekleme başarısız!",Toast.LENGTH_SHORT).show();
                                }
                            }else{
                                Toast.makeText(getActivity(),sinifadi+" sınıfı zaten kayıtlı!",Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                }else if (activePageIndex==1){
                    String sinifadi=editText.getText().toString().trim();
                    if(sinifadi.equals("")){
                        Toast.makeText(getActivity(),"Lütfen sınıf ismi giriniz!",Toast.LENGTH_SHORT).show();
                    }else if(sinifadi.contains("'") || sinifadi.contains("=")|| sinifadi.contains("?")|| sinifadi.contains("+")|| sinifadi.contains(",")|| sinifadi.contains("!")){
                        Toast.makeText(getActivity(),"Lütfen sınıf adında özel karakterler(?,!'+!) kullanmayınız!",Toast.LENGTH_SHORT).show();
                    }else{
                        Veritabani vt=new Veritabani(getActivity());
                        List<String> stringList=new ArrayList<>();
                        stringList=vt.getirKursSinif();
                        vt.close();

                        boolean durum=false;
                        for(String sinif:stringList){
                            if(sinif.equals(sinifadi)){
                                durum=true;
                            }
                        }

                        if(durum==false){
                            long id=vt.ekleKursSinif(sinifadi+" (Kurs)");
                            vt.close();
                            if(id>0){
                                Toast.makeText(getActivity(),sinifadi+" sınıfı eklendi",Toast.LENGTH_SHORT).show();
                                CommSinif commSinif=(CommSinif)getActivity();
                                commSinif.kursSinifEklemeOkOnClick();
                                dismiss();
                            }else{
                                Toast.makeText(getActivity(),"Ekleme başarısız!",Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            Toast.makeText(getActivity(),sinifadi+" sınıfı zaten kayıtlı!",Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });

        Button buttonCancel=(Button)view.findViewById(R.id.buttonSinifEklemeIptal);
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    public void gelenPageIndex(int currentItem) {
        activePageIndex=currentItem;
    }
}
