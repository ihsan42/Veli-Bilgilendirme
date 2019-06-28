package com.egitimyazilim.iletisim.mesajlarvelibilgilendirme;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class KursSiniflarFragment extends Fragment {

    private static final int MY_PERMISSIONS_READ_AND_WRITE_CONTACTS=1790;
    List<HomeButtonListItem> sinifList;
    GridView gridView;
    GridView gridViewKurslar;
    String sinif="";
    String kaydedilecekSinif="";
    ProgressDialog progressDialog;
    String kayitliNolar="";
    int uyariKodu;

    boolean izinVarMi=false;
    private static final int requestCodePermission=111;
    String[] izinler={Manifest.permission.READ_CONTACTS,Manifest.permission.WRITE_CONTACTS};

    public void onResume() {
        super.onResume();
        izinVarMi=checkPermission(getActivity(),izinler);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==requestCodePermission){
            izinVarMi=checkPermission(getActivity(),izinler);
        }
    }

    public boolean checkPermission(Context context, String[] permissions) {
        boolean isPermissionsOk = true;

        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                isPermissionsOk = false;
            }
        }
        return isPermissionsOk;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.kurs_siniflar_fragment,null,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        gridView=(GridView)view.findViewById(R.id.gridViewKurs);

        listele();

        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                if (izinVarMi==false) {
                    ActivityCompat.requestPermissions(getActivity(), izinler, requestCodePermission);
                }
                sinif = sinifList.get(position).getButtonName();
                kayitliNolar="";
                final AlertDialog.Builder[] builder = {new AlertDialog.Builder(getContext())};

                builder[0].setNeutralButton("Numaraları Telefon Rehberine Kaydet", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (izinVarMi==true) {
                            new RehbereKaydet().execute();
                        } else {
                            List<Integer> birDahaSormaSayisi = new ArrayList<>();
                            for (String izin : izinler) {
                                if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), izin)) {
                                    birDahaSormaSayisi.add(1);
                                    ActivityCompat.requestPermissions(getActivity(), new String[]{izin}, requestCodePermission);
                                }
                            }
                            if (birDahaSormaSayisi.size()==0) {
                                AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
                                builder.setTitle("Dikkat!");
                                builder.setMessage("Rehbere kayıt edebilmek için eksik izinler var. Kişiler iznini vermeniz gereklidir. İzin vermek için <Ayarlar>'a tıklayınız ve açılan sayfadaki izinler bölümüden izin veriniz.");
                                builder.setPositiveButton("Ayarlar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent myAppSettings = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + getActivity().getPackageName()));
                                        myAppSettings.addCategory(Intent.CATEGORY_DEFAULT);
                                        myAppSettings.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivityForResult(myAppSettings, 35);
                                    }
                                });
                                builder.setNegativeButton("İptal", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                                AlertDialog alertDialog=builder.create();
                                alertDialog.show();
                            }
                        }
                    }
                });

                if(!sinif.equals("Tüm Öğrenciler")) {
                    builder[0].setPositiveButton("Sil", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogSil, int which) {
                            builder[0] = new AlertDialog.Builder(getContext());
                            builder[0].setMessage("Bu sınıfı silerseniz bu sınıfa kayıtlı tüm öğrencileri, bu sınıfa ait tüm yazılı kayıtlarını ve bu sınıfa ait tüm yoklama kayıtlarını da silmiş olursunuz.");
                            builder[0].setTitle("DİKKAT!");
                            builder[0].setPositiveButton("Sil", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Veritabani vt = new Veritabani(getActivity());
                                    vt.kursSinifSil(sinif);
                                    vt.siniftakiTumOgrSil(sinif);
                                    vt.sınıfYoklamaKayitSil(sinif);
                                    vt.sinifinTumYaziliKayitlariniSil(sinif);
                                    vt.close();
                                    Toast.makeText(getActivity(), sinif + " silindi!", Toast.LENGTH_SHORT).show();
                                    CommSinif commSinif = (CommSinif) getActivity();
                                    commSinif.kursSinifEklemeOkOnClick();
                                }
                            });
                            builder[0].setNegativeButton("İptal", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                            AlertDialog alertDialog1 = builder[0].create();
                            alertDialog1.show();

                        }
                    });
                }

                if(!sinif.equals("Tüm Öğrenciler")) {
                    builder[0].setNegativeButton("Güncelle", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogGuncelle, int which) {
                            CommSinif commSinif = (CommSinif) getActivity();
                            commSinif.guncellenecekSinif(sinif);
                            dialogGuncelle.dismiss();
                        }
                    });
                }
                AlertDialog alertDialog = builder[0].create();
                alertDialog.show();
                return true;
            }
        });

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String sinifadi=sinifList.get(position).getButtonName();
                CommSinif commSinif=(CommSinif)getActivity();
                commSinif.ogrenciListesiniAc(sinifadi);
            }
        });
    }

    private void listele(){
        Veritabani vt=new Veritabani(getActivity());
        List<String> stringList=new ArrayList<>();
        stringList=vt.getirKursSinif();
        stringList.add("Tüm Öğrenciler");

        sinifList=new ArrayList<>();
        if(stringList.size()>1){
            for(String sinif:stringList){
                HomeButtonListItem item=new HomeButtonListItem(R.drawable.ic_stars_black_24dp,sinif);
                sinifList.add(item);
            }

            if(sinifList.size()>1){
                AdapterForHome adapter=new AdapterForHome(getActivity(),sinifList);
                gridView.setAdapter(adapter);
            }else{
                gridView.setAdapter(null);
            }
        }
    }

    private List<String> getRawContactsPhoneNumberList() {
        List<String> ret = new ArrayList<>();

        Uri readContactsUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        Cursor cursor = getContext().getContentResolver().query(readContactsUri, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                int idColumnIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                    String rawContactsId = cursor.getString(idColumnIndex);
                    ret.add(new String(rawContactsId));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return ret;
    }

    private void insertContactDisplayName(Uri addContactsUri, long rawContactId, String displayName)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId);
        contentValues.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE);
        contentValues.put(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, displayName);
        getContext().getContentResolver().insert(addContactsUri, contentValues);
    }

    private void insertContactPhoneNumber(Uri addContactsUri, long rawContactId, String phoneNumber)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId);
        contentValues.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
        contentValues.put(ContactsContract.CommonDataKinds.Phone.NUMBER, phoneNumber);
        contentValues.put(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE);
        getContext().getContentResolver().insert(addContactsUri, contentValues);
    }

    private class RehbereKaydet extends AsyncTask<Void,Void,Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog=new ProgressDialog(getActivity());
            progressDialog.setTitle("Rehbere Kaydediliyor...");
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressDialog.dismiss();
            AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
            if(uyariKodu==1){
                if(!kayitliNolar.equals("")){
                    builder.setMessage(kayitliNolar+"\n"+" ZATEN KAYITLI!");
                }
                builder.setTitle("Kayıt Tamamlandı");
                builder.setPositiveButton("Tamam", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
            }else if(uyariKodu==2){
                builder.setTitle("Bu sınıfa kayıtlı öğrenci yok!");
                builder.setPositiveButton("Tamam", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
            }
            AlertDialog alertDialog=builder.create();
            alertDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            List<String> rawContactsPhoneNumberList = getRawContactsPhoneNumberList();

            Veritabani vt=new Veritabani(getActivity());
            List<Ogrenci> ogrenciList=new ArrayList<>();
            ogrenciList=vt.getirOgrenci();

            List<Ogrenci> sinifOgrList=new ArrayList<>();
            if(sinif.equals("Tüm Öğrenciler")){
                for(Ogrenci ogrenci:ogrenciList){
                    if(ogrenci.getSinif().contains("(Kurs)")){
                        sinifOgrList.add(ogrenci);
                    }
                }
            }else{
                for(Ogrenci ogrenci:ogrenciList){
                    if(ogrenci.getSinif().equals(sinif)){
                        sinifOgrList.add(ogrenci);
                    }
                }
            }

            if(sinifOgrList.size()>0){
                for(Ogrenci ogrenci:sinifOgrList) {
                    boolean durum = false;
                    for (String s : rawContactsPhoneNumberList) {
                        String[] parcalar = s.split(" ");
                        String telno = "";
                        for (String parca : parcalar) {
                            telno += parca;
                        }
                        if (telno.contains(ogrenci.getTelno())) {
                            durum = true;
                        }
                    }
                    if (durum == false) {
                        Uri addContactsUri = ContactsContract.Data.CONTENT_URI;
                        ContentValues contentValues = new ContentValues();
                        Uri rawContactUri = getContext().getContentResolver().insert(ContactsContract.RawContacts.CONTENT_URI, contentValues);
                        long ret = ContentUris.parseId(rawContactUri);

                        insertContactDisplayName(addContactsUri, ret, ogrenci.getSinif() + " " + ogrenci.getAdSoyad() + " Velisi " + ogrenci.getVeliAdi());
                        insertContactPhoneNumber(addContactsUri, ret, ogrenci.getTelno());
                    } else {
                        if(!ogrenci.getTelno().equals("0")){
                            kayitliNolar +=ogrenci.getAdSoyad()+" "+ogrenci.getTelno() + "\n";
                        }
                    }
                }
                uyariKodu=1;
            }
            else{
                uyariKodu=2;
            }
            return null;
        }
    }
}
