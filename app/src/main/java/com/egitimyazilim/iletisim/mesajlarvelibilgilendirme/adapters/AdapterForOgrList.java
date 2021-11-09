package com.egitimyazilim.iletisim.mesajlarvelibilgilendirme.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.egitimyazilim.iletisim.mesajlarvelibilgilendirme.object_classes.Ogrenci;
import com.egitimyazilim.iletisim.mesajlarvelibilgilendirme.R;

import java.util.List;

public class AdapterForOgrList extends BaseAdapter {

    private Context context;
    public List<Ogrenci> ogrenciList;

    public AdapterForOgrList(Context context, List<Ogrenci> ogrenciList) {
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
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder(); LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.row_ogr_list, null, true);

            holder.checkBoxIzinli=(CheckBox) convertView.findViewById(R.id.checkBoxButtonIzinli);
            holder.checkBoxRaporlu=(CheckBox) convertView.findViewById(R.id.checkBoxButtonRaporlu);
            holder.checkBoxYok = (CheckBox) convertView.findViewById(R.id.checkBoxYok);
            holder.checkBoxGec = (CheckBox) convertView.findViewById(R.id.checkBoxGec);
            holder.txtAdSoyad = (TextView) convertView.findViewById(R.id.txtAdSoyad);
            holder.txtTelNo = (TextView) convertView.findViewById(R.id.txtTelNo);
            holder.txtOkulNo = (TextView) convertView.findViewById(R.id.txtOkulNo);
            holder.txtVeliAdi=(TextView)convertView.findViewById(R.id.txtVeliAdi) ;

            convertView.setTag(holder);
        }else {
            holder = (ViewHolder)convertView.getTag();
        }

        holder.txtAdSoyad.setText(ogrenciList.get(position).getAdSoyad());
        holder.txtOkulNo.setText(ogrenciList.get(position).getOkulno());
        holder.txtVeliAdi.setText("Veli: "+ogrenciList.get(position).getVeliAdi());
        holder.txtTelNo.setText("Tel: "+ogrenciList.get(position).getTelno());

        holder.checkBoxYok.setChecked(ogrenciList.get(position).getDurumYok());
        holder.checkBoxYok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ogrenciList.get(position).getDurumYok()==true){
                    ogrenciList.get(position).setDurumYok(false);
                    holder.checkBoxRaporlu.setVisibility(View.INVISIBLE);
                    holder.checkBoxIzinli.setVisibility(View.INVISIBLE);
                    holder.checkBoxRaporlu.setChecked(false);
                    holder.checkBoxIzinli.setChecked(false);
                    ogrenciList.get(position).setDurumRaporlu(false);
                    ogrenciList.get(position).setDurumIzinli(false);
                }else{
                    ogrenciList.get(position).setDurumYok(true);
                    holder.checkBoxRaporlu.setVisibility(View.VISIBLE);
                    holder.checkBoxIzinli.setVisibility(View.VISIBLE);
                    holder.checkBoxRaporlu.setChecked(false);
                    holder.checkBoxIzinli.setChecked(false);
                    ogrenciList.get(position).setDurumRaporlu(false);
                    ogrenciList.get(position).setDurumIzinli(false);
                }

                if(ogrenciList.get(position).getDurumGec()==true){
                    ogrenciList.get(position).setDurumGec(false);
                    holder.checkBoxGec.setChecked(false);
                }
            }
        });

        holder.checkBoxGec.setChecked(ogrenciList.get(position).getDurumGec());
        holder.checkBoxGec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ogrenciList.get(position).getDurumGec()==true){
                    ogrenciList.get(position).setDurumGec(false);
                }else{
                    ogrenciList.get(position).setDurumGec(true);
                }
                if(ogrenciList.get(position).getDurumYok()==true || ogrenciList.get(position).getDurumIzinli()==true || ogrenciList.get(position).getDurumRaporlu()==true){
                    ogrenciList.get(position).setDurumYok(false);
                    holder.checkBoxYok.setChecked(false);
                    holder.checkBoxRaporlu.setVisibility(View.INVISIBLE);
                    holder.checkBoxIzinli.setVisibility(View.INVISIBLE);
                    holder.checkBoxRaporlu.setChecked(false);
                    holder.checkBoxIzinli.setChecked(false);
                    ogrenciList.get(position).setDurumRaporlu(false);
                    ogrenciList.get(position).setDurumIzinli(false);
                }
            }
        });

        holder.checkBoxRaporlu.setChecked(ogrenciList.get(position).getDurumRaporlu());
        holder.checkBoxRaporlu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ogrenciList.get(position).getDurumRaporlu()==true){
                    ogrenciList.get(position).setDurumRaporlu(false);
                    ogrenciList.get(position).setDurumYok(true);
                    holder.checkBoxYok.setChecked(true);
                }else{
                    ogrenciList.get(position).setDurumRaporlu(true);
                    ogrenciList.get(position).setDurumYok(false);
                    holder.checkBoxYok.setChecked(false);
                }
                if(ogrenciList.get(position).getDurumIzinli()==true){
                    ogrenciList.get(position).setDurumIzinli(false);
                    holder.checkBoxIzinli.setChecked(false);
                }

            }
        });

        holder.checkBoxIzinli.setChecked(ogrenciList.get(position).getDurumIzinli());
        holder.checkBoxIzinli.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ogrenciList.get(position).getDurumIzinli()==true){
                    ogrenciList.get(position).setDurumIzinli(false);
                    ogrenciList.get(position).setDurumYok(true);
                    holder.checkBoxYok.setChecked(true);
                }else{
                    ogrenciList.get(position).setDurumIzinli(true);
                    ogrenciList.get(position).setDurumYok(false);
                    holder.checkBoxYok.setChecked(false);
                }
                if(ogrenciList.get(position).getDurumRaporlu()==true){
                    ogrenciList.get(position).setDurumRaporlu(false);
                    holder.checkBoxRaporlu.setChecked(false);
                }
            }
        });

        return convertView;
    }

    private class ViewHolder {
        private CheckBox checkBoxIzinli;
        private CheckBox checkBoxRaporlu;
        protected CheckBox checkBoxYok;
        protected CheckBox checkBoxGec;
        private TextView txtAdSoyad;
        private TextView txtVeliAdi;
        private TextView txtTelNo;
        private TextView txtOkulNo;
    }
}
