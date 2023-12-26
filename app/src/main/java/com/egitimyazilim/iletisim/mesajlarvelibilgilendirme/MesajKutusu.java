package com.egitimyazilim.iletisim.mesajlarvelibilgilendirme;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.role.RoleManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.egitimyazilim.iletisim.mesajlarvelibilgilendirme.adapters.MesajKutusuPageAdapter;
import com.egitimyazilim.iletisim.mesajlarvelibilgilendirme.fragments.MenuContentFragment;
import com.egitimyazilim.iletisim.mesajlarvelibilgilendirme.interfaces.MenuContentComm;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.loader.app.LoaderManager;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
public class MesajKutusu extends AppCompatActivity implements MenuContentComm {

    FragmentManager fm;
    Button buttonMenuOpen;
    Button buttonMenuClose;
    MenuContentFragment menuContentFragment;
    ViewPager viewPager;
    MesajKutusuPageAdapter tabPageAdapter;
    private static final int requestPermissionsCode = 1990;
    private static final int requestDefaultSmsAppCode=1991;
    private static final int readPhoneStatePermCode=1992;
    boolean varsayılanMi=false;
    String[] izinler = {Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS, Manifest.permission.SEND_SMS
            , Manifest.permission.RECEIVE_MMS,Manifest.permission.READ_CONTACTS};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mesaj_kutusu);

        ActionBar bar = getSupportActionBar();
        bar.hide();

        SharedPreferences sharedPref = getSharedPreferences(getPackageName(), MODE_PRIVATE);
        boolean gorulduMu=sharedPref.getBoolean("görüldü mü",false);
        if(gorulduMu){
            varsayılanMi=checkDefaultSMSapp();
            if(!varsayılanMi){
                requestDefaultSMSapp();
            }
        }else{
            AlertDialog.Builder builder=new AlertDialog.Builder(MesajKutusu.this);
            builder.setCancelable(false);
            builder.setTitle("Uyarı");
            builder.setMessage("Google Play Store'daki güvenlik politikalarından dolayı SMS gönderen uygulamalara çeşitli kısıtlamalar getirilmiştir. Varsayılan SMS uygulaması olmayan uygulamalar mağazaya kabul edilmemektedir. \n   Bu uygulamayı sizlerin hizmetine sunmak ve geliştirebilmek için \"Varsayılan SMS Uygulaması\" formatında yazıyoruz. Bu yüzden bu uygulama \"Varsayılan SMS Uygulaması\" olsun mu diye size sormak zorundayız. Ama siz bu uygulamayı \"Varsayılan SMS Uygulaması\" olarak seçmeyiniz; varsayılan yaparsanız çeşitli hatalarla karşılaşabilirsiniz. \n  Açılacak pencereden ilk açılışta \"İptal\"e, ikinci açılışta da \"Bir daha sorma\"yı işaretleyip her zamanki kullandığınız sms uygulamanız seçiliyken \"Varsayılan olarak ayarla\"ya tıklarsanız bir daha uyarıyla karşılaşmazsınız.");
            builder.setPositiveButton("Devam", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putBoolean("görüldü mü",true);
                    editor.commit();

                    varsayılanMi=checkDefaultSMSapp();
                    if(!varsayılanMi){
                        requestDefaultSMSapp();
                    }
                }
            });
            AlertDialog alertDialog=builder.create();
            alertDialog.show();
        }

        buttonMenuOpen=(Button)findViewById(R.id.buttonMenuOpen);
        buttonMenuClose=(Button)findViewById(R.id.buttonMenuClose);

        fm = getSupportFragmentManager();
        menuContentFragment=(MenuContentFragment)fm.findFragmentById(R.id.fragmentMenu);
        fm.beginTransaction().hide(menuContentFragment).commit();

        buttonMenuOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menuButtonsVisibilitySecond();
            }
        });

        buttonMenuClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menuButtonsVisibilityFirst();
            }
        });

    }
    private boolean checkDefaultSMSapp() {
        boolean isDefault = false;
        final String myPackageName = String.valueOf(getApplicationContext().getPackageName());
        if (String.valueOf(Telephony.Sms.getDefaultSmsPackage(getApplicationContext())).equals(myPackageName)) {
            isDefault = true;
        }
        return isDefault;
    }

    private void requestDefaultSMSapp() {
        final String packageName = getApplicationContext().getPackageName();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            RoleManager roleManager =getSystemService(RoleManager.class);
            if (roleManager.isRoleAvailable(RoleManager.ROLE_SMS)) {
                if (roleManager.isRoleHeld(RoleManager.ROLE_SMS)) {
                    ActivityCompat.requestPermissions(this, izinler, requestPermissionsCode);
                } else {
                    Intent intent = roleManager.createRequestRoleIntent(RoleManager.ROLE_SMS);
                    startActivityForResult(intent, requestDefaultSmsAppCode);
                }
            } else {
                Toast.makeText(getApplicationContext(),"Sisteminiz SMS'leri göstermek için müsait değil!",Toast.LENGTH_LONG).show();
            }
        } else {
            if (Telephony.Sms.getDefaultSmsPackage(this).equals(packageName)) {
                ActivityCompat.requestPermissions(this, izinler, requestPermissionsCode);
            } else {
                Intent intent =new  Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
                intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, packageName);
                startActivityForResult(intent, requestDefaultSmsAppCode);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==requestDefaultSmsAppCode){
            //varsayılanMi=checkDefaultSMSapp();
            //if(varsayılanMi){
            ActivityCompat.requestPermissions(this, izinler, requestPermissionsCode);
           /* }else{
               AlertDialog.Builder builder=new AlertDialog.Builder(MesajKutusu.this);
               builder.setMessage("Bu bölümü kullanabilmek için uygulamanın varsayılan sms uygulaması olarak secilmesi gerekir.");
               builder.setNegativeButton("Kapat", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {
                       dialog.dismiss();
                   }
               });
               builder.setPositiveButton("Varsayılan Yap", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {
                       requestDefaultSMSapp();
                   }
               });
               AlertDialog alertDialog=builder.create();
               alertDialog.show();
            }*/
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){
            case requestPermissionsCode:
                if(grantResults.length==izinler.length){
                    int toplam=0;
                    for(int result:grantResults){
                        toplam+=result;
                    }
                    if(toplam==0){
                        listele();
                    }else{
                        Toast.makeText(getApplicationContext(),"Eksik izinler var! " +
                                "Mesajlar gösterilemiyor.",Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }
    private void menuButtonsVisibilitySecond(){
        fm.beginTransaction().show(menuContentFragment).commit();
        buttonMenuOpen.setVisibility(View.INVISIBLE);
        buttonMenuClose.setVisibility(View.VISIBLE);
    }

    private void menuButtonsVisibilityFirst(){
        fm.beginTransaction().hide(menuContentFragment).commit();
        buttonMenuOpen.setVisibility(View.VISIBLE);
        buttonMenuClose.setVisibility(View.INVISIBLE);
    }

    @Override
    public void menuButtonsVisibility() {
        menuButtonsVisibilityFirst();
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder=new AlertDialog.Builder(MesajKutusu.this);
        builder.setTitle("Uygulamadan çıkılsın mı?");
        builder.setPositiveButton("Çıkış", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent a = new Intent(Intent.ACTION_MAIN);
                a.addCategory(Intent.CATEGORY_HOME);
                a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(a);
                finishAffinity();
            }
        });
        builder.setNegativeButton("İptal", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        AlertDialog alertDialog=builder.create();
        alertDialog.show();
    }

    private void listele() {
        viewPager = (ViewPager) findViewById(R.id.pagerMesajlar);
        tabPageAdapter = new MesajKutusuPageAdapter(getSupportFragmentManager());
        viewPager.setAdapter(tabPageAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);
    }
}