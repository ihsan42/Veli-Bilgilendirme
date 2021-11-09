package com.egitimyazilim.iletisim.mesajlarvelibilgilendirme.adapters;

        import android.content.Context;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.BaseAdapter;
        import android.widget.TextView;

        import com.egitimyazilim.iletisim.mesajlarvelibilgilendirme.R;

        import java.util.List;

public class AdapterForPdfdenAl extends BaseAdapter {

    private Context context;
    public List<String> ogrenciList;

    public AdapterForPdfdenAl(Context context, List<String> ogrenciList) {
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
        final AdapterForPdfdenAl.ViewHolder holder;
        if (convertView == null) {
            holder = new AdapterForPdfdenAl.ViewHolder(); LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.row_pdfden_al, null, true);

            holder.txtAdSoyad2 = (TextView) convertView.findViewById(R.id.textView4Pdf);

            convertView.setTag(holder);
        }else {
            holder = (AdapterForPdfdenAl.ViewHolder)convertView.getTag();
        }

        holder.txtAdSoyad2.setText(ogrenciList.get(position));

        return convertView;
    }

    private class ViewHolder {
        private TextView txtAdSoyad2;
    }
}
