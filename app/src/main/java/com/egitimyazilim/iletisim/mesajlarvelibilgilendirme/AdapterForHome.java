package com.egitimyazilim.iletisim.mesajlarvelibilgilendirme;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.media.Image;
import android.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class AdapterForHome extends BaseAdapter {

    private Context context;
    public List<HomeButtonListItem> buttons;

    public AdapterForHome(Context context, List<HomeButtonListItem> buttons) {
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
            convertView = inflater.inflate(R.layout.row_home, null, true);

            holder.ımageView = (ImageView) convertView.findViewById(R.id.imageViewRowHome);
            holder.textView = (TextView) convertView.findViewById(R.id.textViewRowHome);

            convertView.setTag(holder);
        }else {
            holder = (ViewHolder)convertView.getTag();
        }

        holder.textView.setText(buttons.get(position).getButtonName());
        holder.ımageView.setImageResource(buttons.get(position).getIcon());

        return convertView;
    }

    private class ViewHolder{
        private TextView textView;
        private ImageView ımageView;
    }
}
