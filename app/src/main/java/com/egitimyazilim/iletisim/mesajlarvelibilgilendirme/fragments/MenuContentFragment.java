package com.egitimyazilim.iletisim.mesajlarvelibilgilendirme.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.egitimyazilim.iletisim.mesajlarvelibilgilendirme.Ayarlar;
import com.egitimyazilim.iletisim.mesajlarvelibilgilendirme.HazirMesajlarim;
import com.egitimyazilim.iletisim.mesajlarvelibilgilendirme.KisiselBilgiler;
import com.egitimyazilim.iletisim.mesajlarvelibilgilendirme.MesajKutusu;
import com.egitimyazilim.iletisim.mesajlarvelibilgilendirme.R;
import com.egitimyazilim.iletisim.mesajlarvelibilgilendirme.Siniflar;
import com.egitimyazilim.iletisim.mesajlarvelibilgilendirme.YardimciVideolar;
import com.egitimyazilim.iletisim.mesajlarvelibilgilendirme.Yazililar;
import com.egitimyazilim.iletisim.mesajlarvelibilgilendirme.YoklamaKayitlari;
import com.egitimyazilim.iletisim.mesajlarvelibilgilendirme.adapters.AdapterForMenuContent;
import com.egitimyazilim.iletisim.mesajlarvelibilgilendirme.interfaces.MenuContentComm;

import java.util.ArrayList;
import java.util.List;

public class MenuContentFragment extends Fragment {

    Intent intent;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_menu_content,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final Button buttonMenuOpen=(Button)view.findViewById(R.id.buttonMenuOpen);
        final Button buttonMenuClose=(Button)view.findViewById(R.id.buttonMenuClose);
        ListView listView=view.findViewById(R.id.listViewMenuContent);
        List<String> menuButtonList=new ArrayList<>();
        menuButtonList.add("Sınıflar");
        menuButtonList.add("Yazılılar");
        menuButtonList.add("Yoklama Kayıtları");
        menuButtonList.add("Hazır(Taslak) Mesajlarım");
        menuButtonList.add("Kişisel Bilgilerim");
        menuButtonList.add("Mesajlar");
        menuButtonList.add("E-Okul");
        menuButtonList.add("Ayarlar");
        menuButtonList.add("Yardımcı Videolar");
        menuButtonList.add("Gizlilik Politikamız");

        AdapterForMenuContent adapter=new AdapterForMenuContent(getActivity(),menuButtonList);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i){
                    case 0:
                        intent=new Intent(getActivity(), Siniflar.class);
                        if(!(getActivity().getClass().getSimpleName()).equals("Siniflar")){
                            startActivityForResult(intent,1001);
                            getActivity().finish();
                        }else{
                            MenuContentComm comm=(MenuContentComm)getActivity();
                            comm.menuButtonsVisibility();
                        }
                        break;

                    case 1:
                        intent=new Intent(getActivity(), Yazililar.class);
                        if(!(getActivity().getClass().getSimpleName()).equals("Yazililar")){
                            startActivityForResult(intent,1002);
                            getActivity().finish();
                        }else{
                            MenuContentComm comm=(MenuContentComm)getActivity();
                            comm.menuButtonsVisibility();
                        }
                        break;

                    case 2:
                        intent=new Intent(getActivity(), YoklamaKayitlari.class);
                        if(!(getActivity().getClass().getSimpleName()).equals("YoklamaKayitlari")){
                            startActivityForResult(intent,1003);
                            getActivity().finish();
                        }else{
                            MenuContentComm comm=(MenuContentComm)getActivity();
                            comm.menuButtonsVisibility();
                        }
                        break;

                    case 3:
                        intent=new Intent(getActivity(), HazirMesajlarim.class);
                        if(!(getActivity().getClass().getSimpleName()).equals("HazirMesajlarim")){
                            startActivityForResult(intent,1003);
                            getActivity().finish();
                        }else{
                            MenuContentComm comm=(MenuContentComm)getActivity();
                            comm.menuButtonsVisibility();
                        }
                        break;

                    case 4:
                        intent=new Intent(getActivity(), KisiselBilgiler.class);
                        if(!(getActivity().getClass().getSimpleName()).equals("KisiselBilgiler")){
                            startActivityForResult(intent,1005);
                            getActivity().finish();
                        }else{
                            MenuContentComm comm=(MenuContentComm)getActivity();
                            comm.menuButtonsVisibility();
                        }
                        break;

                    case 5:
                        intent=new Intent(getActivity(), MesajKutusu.class);
                        if(!(getActivity().getClass().getSimpleName()).equals("MesajKutusu")){
                            startActivityForResult(intent,1006);
                            getActivity().finish();
                        }else{
                            MenuContentComm comm=(MenuContentComm)getActivity();
                            comm.menuButtonsVisibility();
                        }
                        break;

                    case 6:
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://e-okul.meb.gov.tr/logineOkul.aspx"));
                        startActivity(browserIntent);
                        break;

                    case 7:
                        intent=new Intent(getActivity(), Ayarlar.class);
                        if(!(getActivity().getClass().getSimpleName()).equals("Ayarlar")){
                            startActivityForResult(intent,1006);
                            getActivity().finish();
                        }else{
                            MenuContentComm comm=(MenuContentComm)getActivity();
                            comm.menuButtonsVisibility();
                        }
                        break;

                    case 8:
                        Intent browserIntent2 = new Intent(Intent.ACTION_VIEW, Uri.parse("https://youtube.com/playlist?list=PLH0V8EY87HM1fp0Qm6nOEYOkR1mW87ViQ"));
                        startActivity(browserIntent2);
                        /*intent=new Intent(getActivity(), YardimciVideolar.class);
                        if(!(getActivity().getClass().getSimpleName()).equals("YardimciVideolar")){
                            startActivityForResult(intent,1006);
                            getActivity().finish();
                        }else{
                            MenuContentComm comm=(MenuContentComm)getActivity();
                            comm.menuButtonsVisibility();
                        }
                        break;*/
                    case 9:
                        Intent browserIntent3 = new Intent(Intent.ACTION_VIEW, Uri.parse("https://ihsan4246.wixsite.com/okul-veli-sms"));
                        startActivity(browserIntent3);
                }
            }
        });
    }
}
