package com.example.aplikacja3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity implements AsyncResponse{

    private static final int KOD_WRITE_EXTERNAL_STORAGE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button infoButton = findViewById(R.id.downloadInfoButton);

        infoButton.setOnClickListener(view -> {
            EditText address = findViewById(R.id.address);
            if(address.getText().toString().equals("")){
                Toast.makeText(this, getText(R.string.empty_address),Toast.LENGTH_LONG).show();
            }else{
                AsyncDownloadInfo task = new AsyncDownloadInfo(this);
                task.execute(address.getText().toString());
            }
        });

        Button downloadButton = findViewById(R.id.downloadFileButton);

        downloadButton.setOnClickListener(view -> {
            EditText address = findViewById(R.id.address);
            if(address.getText().toString().equals("")) {
                Toast.makeText(this, getText(R.string.empty_address), Toast.LENGTH_LONG).show();
            }else {
                if(ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                        PackageManager.PERMISSION_GRANTED){
                    Log.d("Main","Perm OK");
                    DownloadFile.startService(this,address.getText().toString());
                }else{
                    Log.d("Main","Perm No ok");
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
    public void setResult(InfoWrapper info){
        TextView size = findViewById(R.id.size);
        TextView type = findViewById(R.id.type);
        if(info.size != -1) {
            String finalSize = info.size + " B";
            size.setText(finalSize);
            type.setText(info.type);
        }
        else{
            size.setText("0");
            type.setText(R.string.invalid_address);
        }
    }

    @Override
    public void onRequestPermissionsResult(int taskCode, @NonNull String[] permissions, @NonNull int[] decisions) {
        super.onRequestPermissionsResult(taskCode, permissions, decisions);
        switch (taskCode) {
            case KOD_WRITE_EXTERNAL_STORAGE:
                if (permissions .length > 0 && permissions[0].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        && decisions[0] == PackageManager.PERMISSION_GRANTED) {
                    EditText address = findViewById(R.id.address);
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
}