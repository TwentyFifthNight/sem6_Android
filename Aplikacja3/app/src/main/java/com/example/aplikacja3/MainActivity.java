package com.example.aplikacja3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;


public class MainActivity extends AppCompatActivity implements AsyncResponse{

    AutoCompleteTextView address;

    private static final int KOD_WRITE_EXTERNAL_STORAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        address = findViewById(R.id.address);

        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line,
                        getResources().getStringArray(R.array.autocompleteAddress));
        address.setAdapter(adapter);

        if(savedInstanceState != null) {
            TextView size = findViewById(R.id.size);
            TextView type = findViewById(R.id.type);
            TextView downloadedSize = findViewById(R.id.downloadedBytes);
            size.setText(savedInstanceState.getString(sizeState));
            type.setText(savedInstanceState.getString(typeState));
            downloadedSize.setText(savedInstanceState.getString(downloadedState));
        }

        Listeners();
    }

    private final String sizeState = "com.example.aplikacja3.Main.fileSize";
    private final String typeState = "com.example.aplikacja3.Main.fileType";
    private final String downloadedState = "com.example.aplikacja3.Main.fileDownloadedBytes";
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        TextView size = findViewById(R.id.size);
        TextView type = findViewById(R.id.type);
        TextView downloadedSize = findViewById(R.id.downloadedBytes);

        outState.putString(sizeState, size.getText().toString());
        outState.putString(typeState,type.getText().toString());
        outState.putString(downloadedState,downloadedSize.getText().toString());
    }

    @Override
    public void onRequestPermissionsResult(int taskCode, @NonNull String[] permissions, @NonNull int[] decisions) {
        super.onRequestPermissionsResult(taskCode, permissions, decisions);
        switch (taskCode) {
            case KOD_WRITE_EXTERNAL_STORAGE:
                if (permissions .length > 0 && permissions[0].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        && decisions[0] == PackageManager.PERMISSION_GRANTED) {
                    DownloadFile.startService(this,address.getText().toString());
                }
                else {
                    Toast.makeText(this, getString(R.string.no_permissions), Toast.LENGTH_LONG).show();
                }
                break;
            default:
                break;
        }
    }


    private void Listeners(){
        Button infoButton = findViewById(R.id.downloadInfoButton);

        infoButton.setOnClickListener(view -> {
            if(address.getText().toString().equals("")){
                Toast.makeText(this, getText(R.string.empty_address),Toast.LENGTH_LONG).show();
            }else{
                DownloadInfo task = new DownloadInfo(this);
                task.execute(address.getText().toString());
            }
        });

        Button downloadButton = findViewById(R.id.downloadFileButton);

        downloadButton.setOnClickListener(view -> {
            if(address.getText().toString().equals("")) {
                Toast.makeText(this, getText(R.string.empty_address), Toast.LENGTH_LONG).show();
            }
            else {
                if(ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                        PackageManager.PERMISSION_GRANTED){
                    Log.d("Main","Perm OK");

                    DownloadFile.startService(this,address.getText().toString());
                }else{
                    Log.d("Main","Perm Not ok");
                    //if(!ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                    Log.d("Main","Show Request");
                    ActivityCompat.requestPermissions(this, new String[] {
                            Manifest.permission.WRITE_EXTERNAL_STORAGE},KOD_WRITE_EXTERNAL_STORAGE);
                    //}
                }
            }
        });
    }

    @Override
    public void setInfoResult(InfoWrapper info){
        TextView size = findViewById(R.id.size);
        TextView type = findViewById(R.id.type);
        if(info.size != -1) {
            size.setText(formatSize(info.size));
            type.setText(info.type);
        }
        else{
            Toast.makeText(MainActivity.this,getText(R.string.invalid_address),Toast.LENGTH_LONG).show();
            address.setError(getText(R.string.invalid_address));
        }
    }


    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            long downloadedBytes = bundle.getLong(DownloadFile.downloadedBytes);
            long totalSize = bundle.getLong(DownloadFile.totalBytes);
            if(totalSize == -1){
                Toast.makeText(MainActivity.this,getText(R.string.invalid_address),Toast.LENGTH_LONG).show();
                address.setError(getText(R.string.invalid_address));
            }

            TextView downloaded = findViewById(R.id.downloadedBytes);
            ProgressBar bar = findViewById(R.id.progressBar);

            downloaded.setText(formatSize(downloadedBytes));
            bar.setProgress((int)(((double)downloadedBytes / totalSize) * 100));
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter(DownloadFile.NOTIFICATION));
    }

    private String formatSize(long size){
        String output;
        if(size > 500000000){
            output = String.format(Locale.ENGLISH, "%,.2f GB", (double) size / 1e9);
        }
        else if(size > 500000) {
            output = String.format(Locale.ENGLISH, "%,.2f MB", (double) size / 1e6);
        }
        else if(size > 500 ){
            output = String.format(Locale.ENGLISH, "%,.2f KB", (double) size / 1e3);
        }
        else{
            output = String.format(Locale.ENGLISH, "%d B", size);
        }
        return output;
    }
}