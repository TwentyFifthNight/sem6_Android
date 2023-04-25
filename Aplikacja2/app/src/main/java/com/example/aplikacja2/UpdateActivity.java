package com.example.aplikacja2;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.regex.Pattern;

public class UpdateActivity extends AppCompatActivity {

    private PhoneViewModel mPhoneViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        Bundle bundle = getIntent().getExtras();
        EditText manufact = findViewById(R.id.manufacturer);
        EditText model = findViewById(R.id.model);
        EditText version = findViewById(R.id.version);
        EditText website = findViewById(R.id.website);

        manufact.setText(bundle.getString(MainActivity.MANUFACTURER_KEY));
        model.setText(bundle.getString(MainActivity.MODEL_KEY));
        version.setText(bundle.getString(MainActivity.VERSION_KEY));
        website.setText(bundle.getString(MainActivity.WEBSITE_KEY));

        mPhoneViewModel = new ViewModelProvider(this)
                .get(PhoneViewModel.class);

        Listeners(bundle.getLong(MainActivity.ID_KEY));
    }

    private void Listeners(Long id){
        Button cancel = findViewById(R.id.cancelButton);
        Button update = findViewById(R.id.updateButton);
        Button websiteBtn = findViewById(R.id.websiteButton);
        EditText manufacturer = findViewById(R.id.manufacturer);
        EditText model = findViewById(R.id.model);
        EditText version = findViewById(R.id.version);
        EditText website = findViewById(R.id.website);

        ArrayList<EditText> editTextList = new ArrayList<>();
        editTextList.add(manufacturer);
        editTextList.add(model);
        editTextList.add(version);
        editTextList.add(website);


        websiteBtn.setOnClickListener(view -> startWWW());

        cancel.setOnClickListener(view -> {
            setResult(RESULT_CANCELED);
            finish();
        });

        update.setOnClickListener(view -> {
            if(manufacturer.getText().toString().equals("") || model.getText().toString().equals("") ||
                    version.getText().toString().equals("") || website.getText().toString().equals("")){
                Toast.makeText(UpdateActivity.this, getString(R.string.invalid_data), Toast.LENGTH_LONG).show();
            }
            else if(isValidVersion(version.getText().toString())){
                //dodatkowo zdobyć id
                mPhoneViewModel.update(new Phone(id ,model.getText().toString(), manufacturer.getText().toString(),
                        version.getText().toString(), website.getText().toString()));
                setResult(RESULT_OK);
                finish();
            }
            else{
                Toast.makeText(UpdateActivity.this, getString(R.string.invalid_version), Toast.LENGTH_LONG).show();
            }
        });

        editTextList.forEach(editText -> editText.setOnFocusChangeListener((view, hasFocus) -> {
            if(!hasFocus){
                if(editText.getText().toString().equals("")){
                    editText.setError(getString(R.string.invalid_data));
                }
            }
        }));
    }

    private boolean isValidVersion(String version){
        return Pattern.compile("^\\d+[A-z]?(.\\d[A-Z]?){0,2}$").matcher(version).matches();
    }

    private void startWWW(){
        EditText website = findViewById(R.id.website);
        String adres = website.getText().toString();

        if(adres.equals("")){
            Toast.makeText(UpdateActivity.this, getString(R.string.empty_website), Toast.LENGTH_LONG).show();
            return;
        }
        else if (!(adres.startsWith("http://") || adres.startsWith("https://"))) {
            adres = "http://" + adres;
        }

        try {
            Intent intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse(adres));
            startActivity(intent);
        }catch (Exception ignored){
            Toast.makeText(UpdateActivity.this, getString(R.string.invalid_website), Toast.LENGTH_LONG).show();
        }
    }

}