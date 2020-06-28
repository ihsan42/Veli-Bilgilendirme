package com.egitimyazilim.iletisim.mesajlarvelibilgilendirme;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class AdaptorForMainList extends BaseAdapter {
    private Context context;
    public List<Chats> people;

    public AdaptorForMainList(Context context, List<Chats> people) {
        this.context = context;
        this.people = people;
    }

    @Override
    public int getCount() {
        return people.size();
    }

    @Override
    public Object getItem(int position) {
        return people.get(position);
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
            convertView = inflater.inflate(R.layout.row_main_list, null, true);

            // holder.ımageView = (ImageView) convertView.findViewById(R.id.imageViewRowHome);
            holder.textViewKisi = (TextView) convertView.findViewById(R.id.textViewPersonName);
            holder.textViewMesaj = (TextView) convertView.findViewById(R.id.textViewPersonMessage);
            holder.textViewTarh = (TextView) convertView.findViewById(R.id.textViewPersonDate);

            convertView.setTag(holder);
        }else {
            holder = (ViewHolder)convertView.getTag();
        }


        long longtime =people.get(position).getTime()/1000;//getTimes().get(people.get(position).getTimes().size()-1)/1000;
        Date date= new Date(TimeUnit.SECONDS.toMillis(longtime));
        SimpleDateFormat df2 = new SimpleDateFormat("kk:mm:ss  dd.MM.yyyy");
        String tarih=df2.format(date).toString();
        if(people.get(position).getSender_name()==null){
            holder.textViewKisi.setText(people.get(position).getSender());
        }else{
            holder.textViewKisi.setText(people.get(position).getSender_name());
        }

        holder.textViewTarh.setText(tarih);
        holder.textViewMesaj.setText(people.get(position).getMessage());//getMessages().get(people.get(position).getMessages().size()-1));
        //holder.ımageView.setImageResource(mesajlar.get(position).getIcon());

        return convertView;
    }

    private class ViewHolder{
        private TextView textViewKisi;
        private TextView textViewMesaj;
        private TextView textViewTarh;
        //private ImageView ımageView;
    }
}
