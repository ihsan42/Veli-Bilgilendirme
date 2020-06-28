package com.egitimyazilim.iletisim.mesajlarvelibilgilendirme;

import android.content.Context;

import androidx.appcompat.app.AlertDialog;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

public class AdapterForYoklama extends BaseAdapter {

    private Context context;
    public List<OgrenciForYoklama> ogrenciList;

    public AdapterForYoklama(Context context, List<OgrenciForYoklama> ogrenciList) {
        this.context = context;
        this.ogrenciList = ogrenciList;
    }

    @Override
    public int getCount() {
        return ogrenciList.size();
    }

    @Override
    public Object getItem(int position) {
        return ogrenciList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return getCount();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final AdapterForYoklama.ViewHolder holder;
        if (convertView == null) {
            holder = new AdapterForYoklama.ViewHolder(); LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.row_yoklama_kayit, null, true);

            holder.buttonInfo=(Button)convertView.findViewById(R.id.buttonInfo);
            holder.txtAdSoyad2 = (TextView) convertView.findViewById(R.id.txtAdSoyad4);
            holder.txtTelNo2 = (TextView) convertView.findViewById(R.id.txtTelNo4);
            holder.txtOkulNo2 = (TextView) convertView.findViewById(R.id.txtOkulNo4);
            holder.txtVeliAdi2=(TextView)convertView.findViewById(R.id.txtVeliAdi4) ;

            convertView.setTag(holder);
        }else {
            holder = (AdapterForYoklama.ViewHolder)convertView.getTag();
        }

        holder.txtAdSoyad2.setText(ogrenciList.get(position).getAdSoyad());
        holder.txtOkulNo2.setText(ogrenciList.get(position).getOkulno());
        holder.txtVeliAdi2.setText(ogrenciList.get(position).getDurumYok());
        holder.txtTelNo2.setText(ogrenciList.get(position).getDurumGec());

        holder.buttonInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CommYoklama commYoklama=(CommYoklama)context;
                commYoklama.openYoklamaKayitSilme(ogrenciList.get(position).getSinif(),ogrenciList.get(position).getOkulno(),ogrenciList.get(position).getAdSoyad(),ogrenciList.get(position).getTelno());
               /* final AlertDialog.Builder builder=new android.support.v7.app.AlertDialog.Builder(context);
                builder.setTitle("Yoklama Geçmişi");
                if(ogrenciList.get(position).getTarih().equals("")){
                    builder.setMessage("Kayıt yok!");
                }else{
                    builder.setMessage(ogrenciList.get(position).getTarih());
                }
                builder.setPositiveButton("Veli Bilgilendir", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(ogrenciList.get(position).getDurumYok().equals("0 gün Yok (0 gün Raporlu) (0 gün İzinli)")&&ogrenciList.get(position).getDurumGec().equals("0 gün Geç")){
                            Toast.makeText(context,"Devamsızlık ve geç gelme yok",Toast.LENGTH_SHORT).show();
                        }else{
                            AlertDialog.Builder builder1=new AlertDialog.Builder(context);
                            builder1.setTitle("SMS gönderilsin mi?");
                            builder1.setMessage("Okulumuz öğrencilerinden "+ogrenciList.get(position).getAdSoyad()+"\n"+ogrenciList.get(position).getTarih());
                            builder1.setPositiveButton("Gönder", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ArrayList<PendingIntent> sentIntents=new ArrayList<>();
                                    PendingIntent sentIntent=PendingIntent.getBroadcast(context, 0, new Intent(SMS_SENT_ACTION), 0);
                                    sentIntents.add(sentIntent);

                                    ArrayList<PendingIntent> deliveryIntents=new ArrayList<>();
                                    PendingIntent deliveryIntent=PendingIntent.getBroadcast(context, 0, new Intent(SMS_DELIVERED_ACTION), 0);
                                    deliveryIntents.add(deliveryIntent);

                                    final String mesaj="Okulumuz öğrencilerinden "+ogrenciList.get(position).getAdSoyad()+"\n"+ogrenciList.get(position).getTarih();
                                    SmsManager sms = SmsManager.getDefault();
                                    ArrayList<String> parts = sms.divideMessage(mesaj);
                                    sms.sendMultipartTextMessage(ogrenciList.get(position).getTelno(),null, parts, sentIntents, deliveryIntents);
                                    Toast.makeText(context,"Gönderildi",Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                }
                            });
                            builder1.setNegativeButton("İptal", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            AlertDialog alertDialog=builder1.create();
                            alertDialog.show();
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
                alertDialog.show();*/
            }
        });

        return convertView;
    }

    private class ViewHolder {
        protected Button buttonInfo;
        private TextView txtAdSoyad2;
        private TextView txtVeliAdi2;
        private TextView txtTelNo2;
        private TextView txtOkulNo2;
    }
}
