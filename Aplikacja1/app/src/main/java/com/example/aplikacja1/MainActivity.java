package com.example.aplikacja1;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public static final String GRADES_KEY = "gradesCount";
    private double mean;

    private ActivityResultLauncher<Intent> mActivityResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Listeners();

        mActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    TextView resultTextView = findViewById(R.id.result);
                    Button button2 = findViewById(R.id.button2);

                    if (result.getResultCode() == RESULT_OK) {
                        Intent resultIntent = result.getData();

                        assert resultIntent != null;
                            String output = getString(R.string.mean) + String.format("%,.2f",Double.parseDouble(resultIntent.getStringExtra(SecondActivity.RESULT_KEY)));
                            resultTextView.setText(output);
                            mean = Double.parseDouble(resultIntent.getStringExtra(SecondActivity.RESULT_KEY));
                        if(Double.parseDouble(resultIntent.getStringExtra(SecondActivity.RESULT_KEY)) >= 3.0)
                            button2.setText(R.string.goodGrade);
                        else
                            button2.setText(R.string.badGrade);

                        ArrayList<TextView> list = new ArrayList<>();
                        list.add(findViewById(R.id.imie));
                        list.add(findViewById(R.id.nazwisko));
                        list.add(findViewById(R.id.liczbaOcen));
                        list.add(findViewById(R.id.button));
                        list.add(findViewById(R.id.textView));
                        list.add(findViewById(R.id.textView1));
                        list.add(findViewById(R.id.textView2));

                        button2.setVisibility(View.VISIBLE);
                        resultTextView.setVisibility(View.VISIBLE);

                        list.forEach(textView -> textView.setVisibility(View.GONE));
                    }
                    else if (result.getResultCode() == RESULT_CANCELED) {
                        button2.setVisibility(View.INVISIBLE);
                        resultTextView.setVisibility(View.INVISIBLE);
                    }
                });

    }

    private void startSecondActivity(Intent intent) {
        mActivityResultLauncher.launch(intent);
    }

    private void Listeners(){
        ArrayList<EditText> editTextList = new ArrayList<>();

        EditText name = findViewById(R.id.imie);
        EditText surname = findViewById(R.id.nazwisko);
        EditText gradesCount = findViewById(R.id.liczbaOcen);
        Button button = findViewById(R.id.button);
        Button button2 = findViewById(R.id.button2);

        editTextList.add(name);
        editTextList.add(surname);
        editTextList.add(gradesCount);


        button.setOnClickListener(view -> {
            Intent intent = new Intent(this, SecondActivity.class);
            intent.putExtra(GRADES_KEY,gradesCount.getText().toString());
            startSecondActivity(intent);
        });

        button2.setOnClickListener(view -> {
            if (mean >= 3.0)
                Toast.makeText(MainActivity.this, getString(R.string.succesAnnouncement), Toast.LENGTH_LONG).show();
            else
                Toast.makeText(MainActivity.this, getString(R.string.failAnnouncement), Toast.LENGTH_LONG).show();
            MainActivity.this.finish();
            System.exit(0);
        });

        editTextList.forEach((textField) -> textField.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                int count = !isNumeric(gradesCount.getText().toString()) ? 0 : Integer.parseInt(gradesCount.getText().toString());

                if(name.getText().toString().equals("") || surname.getText().toString().equals("") ||
                        count > 15 || count < 5){
                    button.setVisibility(View.INVISIBLE);
                }else{
                    button.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void afterTextChanged(Editable editable) {}
        }));

        name.setOnFocusChangeListener((v, hasFocus) -> {
            if(!hasFocus){
                if(name.getText().toString().equals("")){
                    name.setError(getString(R.string.nameError));
                    Toast.makeText(MainActivity.this, getString(R.string.nameError), Toast.LENGTH_LONG).show();
                }
            }
        });

        surname.setOnFocusChangeListener((v, hasFocus) -> {
            if(!hasFocus){
                if(surname.getText().toString().equals("")){
                    surname.setError(getString(R.string.surnameError));
                    Toast.makeText(MainActivity.this, getString(R.string.surnameError), Toast.LENGTH_LONG).show();
                }
            }
        });

        gradesCount.setOnFocusChangeListener((v, hasFocus) -> {
            if(!hasFocus){
                int count = !isNumeric(gradesCount.getText().toString()) ? 0 : Integer.parseInt(gradesCount.getText().toString());
                if(count > 15 || count < 5){
                    gradesCount.setError(getString(R.string.gradeCountError));
                    Toast.makeText(MainActivity.this, getString(R.string.gradeCountError), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public static boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch(NumberFormatException e){
            return false;
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        int name = findViewById(R.id.imie).getVisibility();
        TextView resultView = findViewById(R.id.result);
        Button button = findViewById(R.id.button2);

        outState.putInt("Name",name);
        outState.putString("resultText",resultView.getText().toString());
        outState.putString("buttonText",button.getText().toString());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if(savedInstanceState.getInt("Name") != View.VISIBLE){
            TextView resultTextView = findViewById(R.id.result);
            Button button2 = findViewById(R.id.button2);
            ArrayList<TextView> list = new ArrayList<>();
            list.add(findViewById(R.id.imie));
            list.add(findViewById(R.id.nazwisko));
            list.add(findViewById(R.id.liczbaOcen));
            list.add(findViewById(R.id.button));
            list.add(findViewById(R.id.textView));
            list.add(findViewById(R.id.textView1));
            list.add(findViewById(R.id.textView2));

            button2.setText(savedInstanceState.getString("buttonText"));
            resultTextView.setText(savedInstanceState.getString("resultText"));
            button2.setVisibility(View.VISIBLE);
            resultTextView.setVisibility(View.VISIBLE);

            list.forEach(textView -> textView.setVisibility(View.GONE));
        }
    }
}