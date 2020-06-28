package com.egitimyazilim.iletisim.mesajlarvelibilgilendirme;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.List;

public class AdapterForYazili extends BaseAdapter {

    private Context context;
    public List<OgrenciForYazili> ogrenciList;

    public AdapterForYazili(Context context, List<OgrenciForYazili> ogrenciList) {
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
        final AdapterForYazili.ViewHolder holder;
        if (convertView == null) {
            holder = new AdapterForYazili.ViewHolder(); LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.row_yazililar, null, true);

            holder.txtAdSoyad = (TextView) convertView.findViewById(R.id.txtAdSoyad);
            holder.txtTelNo = (TextView) convertView.findViewById(R.id.txtTelNo);
            holder.txtOkulNo = (TextView) convertView.findViewById(R.id.txtOkulNo);
            holder.editTextYazili1=(EditText)convertView.findViewById(R.id.editTextYazili1);
            holder.editTextYazili2=(EditText)convertView.findViewById(R.id.editTextYazili2);

            convertView.setTag(holder);
        }else {
            holder = (AdapterForYazili.ViewHolder)convertView.getTag();
        }

        holder.txtAdSoyad.setText(ogrenciList.get(position).getAdSoyad());
        holder.txtOkulNo.setText(ogrenciList.get(position).getOkulno());
        holder.txtTelNo.setText("Tel: "+ogrenciList.get(position).getTelno());
        holder.editTextYazili1.setText(ogrenciList.get(position).getYazili1());
        holder.editTextYazili2.setText(ogrenciList.get(position).getYazili2());

        holder.editTextYazili1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                ogrenciList.get(position).setYazili1(holder.editTextYazili1.getText().toString());
            }
        });

        holder.editTextYazili2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                ogrenciList.get(position).setYazili2(holder.editTextYazili2.getText().toString());
            }
        });

        return convertView;
    }

    private class ViewHolder {
        protected EditText editTextYazili1;
        protected EditText editTextYazili2;
        private TextView txtAdSoyad;
        private TextView txtTelNo;
        private TextView txtOkulNo;
    }
}
