package com.egitimyazilim.iletisim.mesajlarvelibilgilendirme;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;

public class AdapterForKayıtlıOgr extends BaseAdapter{

    private Context context;
    public List<Ogrenci> ogrenciList;

    public AdapterForKayıtlıOgr(Context context, List<Ogrenci> ogrenciList) {
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
        if (getCount() > 0) {
            return getCount();
        } else {
            return super.getViewTypeCount();
        }
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final AdapterForKayıtlıOgr.ViewHolder holder;
        if (convertView == null) {
            holder = new AdapterForKayıtlıOgr.ViewHolder(); LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.row_kayitli_ogr, null, true);

            holder.checkBoxKayitOgr = (CheckBox) convertView.findViewById(R.id.checkBoxKayitOgr);
            holder.txtAdSoyad3 = (TextView) convertView.findViewById(R.id.txtAdSoyad3);
            holder.txtTelNo3 = (TextView) convertView.findViewById(R.id.txtTelNo3);
            holder.txtOkulNo3 = (TextView) convertView.findViewById(R.id.txtOkulNo3);
            holder.txtVeliAdi3=(TextView)convertView.findViewById(R.id.txtVeliAdi3) ;
            holder.txtSinif3=(TextView)convertView.findViewById(R.id.txtSinif3) ;

            convertView.setTag(holder);
        }else {
            holder = (AdapterForKayıtlıOgr.ViewHolder)convertView.getTag();
        }

        holder.txtAdSoyad3.setText(ogrenciList.get(position).getAdSoyad());
        holder.txtOkulNo3.setText(ogrenciList.get(position).getOkulno());
        holder.txtVeliAdi3.setText("Veli: "+ogrenciList.get(position).getVeliAdi());
        holder.txtTelNo3.setText("Tel: "+ogrenciList.get(position).getTelno());
        holder.txtSinif3.setText(ogrenciList.get(position).getSinif());

        holder.checkBoxKayitOgr.setChecked(ogrenciList.get(position).getChecked());
        holder.checkBoxKayitOgr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ogrenciList.get(position).getChecked()==true){
                    ogrenciList.get(position).setChecked(false);
                }else{
                    ogrenciList.get(position).setChecked(true);
                }
            }
        });
        return convertView;
    }

    private class ViewHolder {
        protected CheckBox checkBoxKayitOgr;
        private TextView txtAdSoyad3;
        private TextView txtVeliAdi3;
        private TextView txtTelNo3;
        private TextView txtOkulNo3;
        private TextView txtSinif3;
    }
}
