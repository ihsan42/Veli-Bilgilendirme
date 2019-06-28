package com.egitimyazilim.iletisim.mesajlarvelibilgilendirme;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;

public class AdapterForOgrListOneCheck extends BaseAdapter {

    private Context context;
    public List<Ogrenci> ogrenciList;

    public AdapterForOgrListOneCheck(Context context, List<Ogrenci> ogrenciList) {
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
        final AdapterForOgrListOneCheck.ViewHolder holder;
        if (convertView == null) {
            holder = new AdapterForOgrListOneCheck.ViewHolder(); LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.row_with_one_checkbox, null, true);

            holder.checkBoxOne = (CheckBox) convertView.findViewById(R.id.checkBoxOne);
            holder.txtAdSoyad2 = (TextView) convertView.findViewById(R.id.txtAdSoyad2);
            holder.txtTelNo2 = (TextView) convertView.findViewById(R.id.txtTelNo2);
            holder.txtOkulNo2 = (TextView) convertView.findViewById(R.id.txtOkulNo2);
            holder.txtVeliAdi2=(TextView)convertView.findViewById(R.id.txtVeliAdi2) ;

            convertView.setTag(holder);
        }else {
            holder = (AdapterForOgrListOneCheck.ViewHolder)convertView.getTag();
        }

        holder.txtAdSoyad2.setText(ogrenciList.get(position).getAdSoyad());
        holder.txtOkulNo2.setText(ogrenciList.get(position).getOkulno());
        holder.txtVeliAdi2.setText("Veli: "+ogrenciList.get(position).getVeliAdi());
        holder.txtTelNo2.setText("Tel: "+ogrenciList.get(position).getTelno());

        holder.checkBoxOne.setChecked(ogrenciList.get(position).getChecked());
        holder.checkBoxOne.setOnClickListener(new View.OnClickListener() {
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
        protected CheckBox checkBoxOne;
        private TextView txtAdSoyad2;
        private TextView txtVeliAdi2;
        private TextView txtTelNo2;
        private TextView txtOkulNo2;

    }
}
