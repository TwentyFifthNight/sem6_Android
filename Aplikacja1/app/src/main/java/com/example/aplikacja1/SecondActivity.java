package com.example.aplikacja1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;

import java.util.ArrayList;

public class SecondActivity extends AppCompatActivity {

    private static ArrayList<GradeModel> grades;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        Bundle bundle = getIntent().getExtras();
        int count = Integer.parseInt(bundle.getString(MainActivity.GRADES_KEY));

        String[] subjects = getResources().getStringArray(R.array.subjects);

        if(grades == null || grades.size() != count) {
            grades = new ArrayList<>();
            for (int index = 0; index < count; index++) {
                grades.add(new GradeModel(subjects[index], 2));
            }
        }


        RecyclerView recyclerView = findViewById(R.id.lista_ocen);
        InteractiveArrayAdapter adapter = new InteractiveArrayAdapter(this, grades);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Button returnButton = findViewById(R.id.liczSrednia);
        returnButton.setOnClickListener(v -> returnToMain());

    }

    public final static String RESULT_KEY =
            "com.example.laboratorium1_second_activity.result";

    private void returnToMain() {
        int suma = 0;
        for (GradeModel grade: grades) {
            suma += grade.getGrade();
        }
        String result = Double.toString(suma * 1.0 / grades.size());
        Intent intent = new Intent();
        intent.putExtra(RESULT_KEY, result);
        setResult(RESULT_OK, intent);
        finish();
    }

    public boolean onOptionsItemSelected(MenuItem item){
        if (item.getItemId() == android.R.id.home){
            setResult(RESULT_CANCELED);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
