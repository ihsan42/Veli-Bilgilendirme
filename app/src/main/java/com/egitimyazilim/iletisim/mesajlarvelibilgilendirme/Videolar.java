package com.egitimyazilim.iletisim.mesajlarvelibilgilendirme;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;


public class Videolar extends DialogFragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.videolar,null,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button buttonCancel=(Button)view.findViewById(R.id.buttonVideoCancel);
        ListView listView=(ListView)view.findViewById(R.id.listViewVideolar);

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        List<HomeButtonListItem> listItems=new ArrayList<>();

        HomeButtonListItem item1=new HomeButtonListItem(R.drawable.ic_ondemand_video_black_24dp,"Uygulamayı nasıl kullanırım?");
        HomeButtonListItem item2=new HomeButtonListItem(R.drawable.ic_ondemand_video_black_24dp,"E-Okuldan Excel sınıf listesini nasıl indiririm?");
        HomeButtonListItem item3=new HomeButtonListItem(R.drawable.ic_ondemand_video_black_24dp,"Telefon Numaralı sınıf listesini nasıl oluştururum? Kurs sınıflarını Excelden nasıl alırım?");

        listItems.add(item1);
        listItems.add(item2);
        listItems.add(item3);

        AdapterForHome adapterForHome=new AdapterForHome(getActivity(),listItems);
        listView.setAdapter(adapterForHome);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position==0){
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://youtu.be/FRNZKfQw_hI"));
                    startActivity(browserIntent);
                }
                if(position==1){
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://youtu.be/fpUs8mBmPj8"));
                    startActivity(browserIntent);
                }
                if(position==2){
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://youtu.be/DlMcHVGrUXw"));
                    startActivity(browserIntent);
                }
            }
        });

    }
}
