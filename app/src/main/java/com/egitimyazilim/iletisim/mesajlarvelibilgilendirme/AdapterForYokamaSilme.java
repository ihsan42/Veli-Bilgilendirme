package com.egitimyazilim.iletisim.mesajlarvelibilgilendirme;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;

public class AdapterForYokamaSilme extends BaseAdapter {
    private Context context;
    public List<Ogrenci> ogrenciList;

    public AdapterForYokamaSilme(Context context, List<Ogrenci> ogrenciList) {
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
        final AdapterForYokamaSilme.ViewHolder holder;
        if (convertView == null) {
            holder = new AdapterForYokamaSilme.ViewHolder(); LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.row_yoklama_kayit_silme, null, true);

            holder.checkBoxOne = (CheckBox) convertView.findViewById(R.id.checkBoxYoklamaSil);
            holder.txtTarih = (TextView) convertView.findViewById(R.id.textViewTarih);
            holder.txtDurum = (TextView) convertView.findViewById(R.id.textViewDurum);

            convertView.setTag(holder);
        }else {
            holder = (AdapterForYokamaSilme.ViewHolder)convertView.getTag();
        }

        holder.txtTarih.setText(ogrenciList.get(position).getTarih());
        holder.txtDurum.setText(ogrenciList.get(position).getDurum());

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
        private TextView txtTarih;
        private TextView txtDurum;
    }
}
