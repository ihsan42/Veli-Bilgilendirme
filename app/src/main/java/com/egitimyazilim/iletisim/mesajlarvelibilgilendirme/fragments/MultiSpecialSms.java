package com.egitimyazilim.iletisim.mesajlarvelibilgilendirme.fragments;

import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Telephony;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AlertDialog;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.egitimyazilim.iletisim.mesajlarvelibilgilendirme.OgrenciListesi;
import com.egitimyazilim.iletisim.mesajlarvelibilgilendirme.R;
import com.egitimyazilim.iletisim.mesajlarvelibilgilendirme.SMSGonder;
import com.egitimyazilim.iletisim.mesajlarvelibilgilendirme.Veritabani;
import com.egitimyazilim.iletisim.mesajlarvelibilgilendirme.contentprovider.MessagesContentProviderHandler;
import com.egitimyazilim.iletisim.mesajlarvelibilgilendirme.object_classes.Ogrenci;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class MultiSpecialSms extends DialogFragment {

    public static final String SMS_SENT_ACTION = "com.andriodgifts.gift.SMS_SENT_ACTION";
    public static final String SMS_DELIVERED_ACTION = "com.andriodgifts.gift.SMS_DELIVERED_ACTION";
    private static int MY_PERMISSIONS_SEND_SMS=1991;
    List<Ogrenci> smsGondList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.ozel_sms_gonderme,null,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final EditText editText=(EditText)view.findViewById(R.id.editTextSinifEkleme);
        TextView textViewTitle=(TextView)view.findViewById(R.id.textViewSinifEklemeTitle);
        textViewTitle.setText("Mesaj Yaz");
        editText.setHint("Mesajınızı buraya yazınız...");

        SharedPreferences sharedPref=getActivity().getSharedPreferences("Kisisel Ayarlar",MODE_PRIVATE);
        boolean durumIsim=sharedPref.getBoolean("isim",false);
        boolean durumBrans=sharedPref.getBoolean("brans",false);
        boolean durumOkuladi=sharedPref.getBoolean("okuladi",false);

        Veritabani vt=new Veritabani(getActivity());
        List<String> kisiselBilgiler=new ArrayList<>();
        kisiselBilgiler=vt.kisiselBilgileriGetir();

        String mesaj="";
        if(durumIsim==true && durumBrans==true && durumOkuladi==true){
            mesaj=kisiselBilgiler.get(2)+"\n\n["+kisiselBilgiler.get(0)+" ("+kisiselBilgiler.get(1)+ " öğretmeni)]";
        }else if(durumIsim==true && durumBrans==true && durumOkuladi==false){
            mesaj="\n\n["+kisiselBilgiler.get(0)+" ("+kisiselBilgiler.get(1)+ " öğretmeni)]";
        }else if(durumIsim==true && durumBrans==false && durumOkuladi==true){
            mesaj=kisiselBilgiler.get(2)+"\n\n["+kisiselBilgiler.get(0)+"]";
        }else if(durumIsim==true && durumBrans==false && durumOkuladi==false){
            mesaj="\n\n["+kisiselBilgiler.get(0)+"]";
        }else if(durumIsim==false && durumBrans==true && durumOkuladi==true){
            mesaj=kisiselBilgiler.get(2)+"\n\n("+kisiselBilgiler.get(1)+ " öğretmeni)";
        }else if(durumIsim==false && durumBrans==true && durumOkuladi==false){
            mesaj="\n\n("+kisiselBilgiler.get(1)+ " öğretmeni)";
        }else if(durumIsim==false && durumBrans==false && durumOkuladi==true){
            mesaj=kisiselBilgiler.get(2);
        }else if(durumIsim==false && durumBrans==false && durumOkuladi==false){
        }

        editText.setText(mesaj);

        Button buttonOk=(Button)view.findViewById(R.id.buttonSinifEklemeTamam);
        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String mesaj=editText.getText().toString().trim();
                if(mesaj.equals("")){
                    Toast.makeText(getActivity(),"Lüften mesajınızı yazınız!",Toast.LENGTH_SHORT).show();
                }else{
                    AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
                    builder.setCancelable(false);
                    builder.setTitle("Sms gönderilsin mi?");
                    builder.setPositiveButton("Gönder", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            for(Ogrenci ogrenci:smsGondList){
                                SMSGonder.gonder(getActivity(),ogrenci.getTelno(),mesaj,ogrenci.getAdSoyad());
                            }
                            dismiss();
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
                }
            }
        });

        Button buttonCancel=(Button)view.findViewById(R.id.buttonSinifEklemeIptal);
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(editText.getText())){
                    dismiss();
                }else{
                    AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
                    builder.setTitle("Mesaj göndermeden çıkılsın mı?");
                    builder.setPositiveButton("Çıkış", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dismiss();
                        }
                    });
                    builder.setNegativeButton("İptal", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    AlertDialog alertDialog=builder.create();
                    alertDialog.show();
                }
            }
        });
    }

    public void gelenList(List<Ogrenci> gelenList){
        this.smsGondList=new ArrayList<>();
        this.smsGondList=gelenList;
    }
}
