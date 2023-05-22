package com.example.aplikacja4.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.aplikacja4.Image;
import com.example.aplikacja4.R;
import com.example.aplikacja4.fragments.portrait.DetailActivity;

public class FragmentActivity extends AppCompatActivity implements
        ImageListAdapter.OnItemClickListener
{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_layout);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void OnItemClickListener(Image image) {
        FragmentDetails fragment = (FragmentDetails) getSupportFragmentManager().findFragmentById(R.id.detailFragment);

        if (fragment != null && fragment.isInLayout()) {
            // ustawiamy teskt we fragmencie
            fragment.setImage(image);
        }
        else {
            // w trybie portrait wywołujemy drugą aktywność
            Intent intent = new Intent(getApplicationContext(), DetailActivity.class);
            intent.putExtra("img",image);
            startActivity(intent);
        }
    }
}