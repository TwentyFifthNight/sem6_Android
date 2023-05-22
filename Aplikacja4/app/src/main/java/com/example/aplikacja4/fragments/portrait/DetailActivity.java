package com.example.aplikacja4.fragments.portrait;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Configuration;
import android.os.Bundle;

import com.example.aplikacja4.Image;
import com.example.aplikacja4.fragments.FragmentDetails;
import com.example.aplikacja4.R;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getResources().getConfiguration().orientation ==
                Configuration.ORIENTATION_LANDSCAPE) {
            finish();
        }
        setContentView(R.layout.activity_detail);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            Image image = extras.getParcelable("img");
            FragmentDetails detailFragment =
                    (FragmentDetails) getSupportFragmentManager().findFragmentById(R.id.detailFragment);
            // ustawiamy tekst fragmentu w tej aktywności
            if(detailFragment != null)
                detailFragment.setImage(image);
        }
    }
}