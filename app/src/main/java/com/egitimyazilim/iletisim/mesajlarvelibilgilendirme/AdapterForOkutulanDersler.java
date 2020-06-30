package com.egitimyazilim.iletisim.mesajlarvelibilgilendirme;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class AdapterForOkutulanDersler extends BaseAdapter {

    private Context context;
    public List<String> buttons;

    public AdapterForOkutulanDersler(Context context, List<String> buttons) {
        this.context = context;
        this.buttons = buttons;
    }

    @Override
    public int getCount() {
        return buttons.size();
    }

    @Override
    public Object getItem(int position) {
        return buttons.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder(); LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.row_okutulan_dersler, null, true);

            holder.textView = (TextView) convertView.findViewById(R.id.textViewRowMenuContent);

            convertView.setTag(holder);
        }else {
            holder = (ViewHolder)convertView.getTag();
        }

        holder.textView.setText(buttons.get(position));
        return convertView;
    }

    private class ViewHolder{
        private TextView textView;
    }
}
