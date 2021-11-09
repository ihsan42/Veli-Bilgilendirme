package com.egitimyazilim.iletisim.mesajlarvelibilgilendirme;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.egitimyazilim.iletisim.mesajlarvelibilgilendirme.R;
import com.egitimyazilim.iletisim.mesajlarvelibilgilendirme.adapters.AdapterForHome;
import com.egitimyazilim.iletisim.mesajlarvelibilgilendirme.object_classes.HomeButtonListItem;

import java.util.ArrayList;
import java.util.List;


public class YardimciVideolar extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.yardimci_videolar);

        ActionBar bar=getSupportActionBar();
        bar.hide();

        ListView listView=(ListView)findViewById(R.id.listViewVideolar);

        List<HomeButtonListItem> listItems=new ArrayList<>();

        HomeButtonListItem item1=new HomeButtonListItem(R.drawable.ic_ondemand_video_black_24dp,"E-Okuldan Excel sınıf listesini nasıl indiririm?");
       // HomeButtonListItem item2=new HomeButtonListItem(R.drawable.ic_ondemand_video_black_24dp,"Uygulamayı nasıl kullanırım?");
       // HomeButtonListItem item3=new HomeButtonListItem(R.drawable.ic_ondemand_video_black_24dp,"Telefon Numaralı sınıf listesini nasıl oluştururum? Kurs sınıflarını Excelden nasıl alırım?");

        listItems.add(item1);
       // listItems.add(item2);
       // listItems.add(item3);

        AdapterForHome adapterForHome=new AdapterForHome(getApplicationContext(),listItems);
        listView.setAdapter(adapterForHome);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position==0){
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://youtu.be/D8Ao18qsnJ0"));
                    startActivity(browserIntent);
                }
              /*  if(position==1){
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://youtube.com/playlist?list=PLH0V8EY87HM12s5pv4aaFWigZaqLZb_fl"));
                    startActivity(browserIntent);
                }
                if(position==2){
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://youtu.be/DlMcHVGrUXw"));
                    startActivity(browserIntent);
                }*/
            }
        });
    }
}
