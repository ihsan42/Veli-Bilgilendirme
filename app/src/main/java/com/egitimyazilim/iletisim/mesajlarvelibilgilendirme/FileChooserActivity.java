package com.egitimyazilim.iletisim.mesajlarvelibilgilendirme;

import android.content.Intent;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import ir.sohreco.androidfilechooser.ExternalStorageNotAvailableException;
import ir.sohreco.androidfilechooser.FileChooser;

public class FileChooserActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_chooser);

        ActionBar bar=getSupportActionBar();
        bar.setTitle("Dosya Seçiniz");

        Button button=(Button)findViewById(R.id.buttonFileChooserClose);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ıntent=new Intent(getApplicationContext(),Siniflar.class);
                setResult(60,ıntent);
                finish();
            }
        });

        Intent intent=getIntent();
        int excelIstek=intent.getIntExtra("excel",-1);
        FileChooser.Builder builder = new FileChooser.Builder(FileChooser.ChooserType.FILE_CHOOSER, new FileChooser.ChooserListener() {
            @Override
            public void onSelect(String path) {
                Intent ıntent=new Intent(getApplicationContext(),Siniflar.class);
                ıntent.putExtra("path",path);
                setResult(55,ıntent);
                finish();
            }
        })
                .setMultipleFileSelectionEnabled(false)
                .setListItemsTextColor(android.R.color.black)
                .setPreviousDirectoryButtonIcon(R.drawable.ic_arrow_back_black2_24dp)
                .setDirectoryIcon(R.drawable.ic_folder_black2_24dp)
                .setFileIcon(R.drawable.ic_insert_drive_file_black_24dp)
                ;
        if(excelIstek==1){
            builder.setFileFormats(new String[] {".xlsx",".XLSX",".xlsm",".XLSM",".xlsb",".XLSB",".xltx",".XLTX",".xltm",".XLTM",".xls",".XLS",".xlt",".XLT",".xml",".XML",".xlam",".XLAM",".xla",".XLA",".xlw",".XLW",".xlr",".XLR"});
        }else{
            builder.setFileFormats(new String[] {".PDF", ".pdf"});
        }

        try {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fileChooserFragment, builder.build())
                    .commit();
        } catch (ExternalStorageNotAvailableException e) {
            Toast.makeText(this, "Dosya yöneticisi şuan meşgul!",
                    Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

    }
}
