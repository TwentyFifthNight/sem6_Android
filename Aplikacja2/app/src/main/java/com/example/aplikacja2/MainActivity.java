package com.example.aplikacja2;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity implements PhoneListAdapter.OnItemClickListener{
    private PhoneViewModel mPhoneViewModel;
    private PhoneListAdapter mAdapter;
    private ActivityResultLauncher<Intent> mActivityResultLauncher;

    public static final String MANUFACTURER_KEY = "manufacturerData";
    public static final String MODEL_KEY = "modelData";
    public static final String VERSION_KEY = "versionData";
    public static final String WEBSITE_KEY = "websiteData";
    public static final String ID_KEY = "idData";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = findViewById(R.id.phone_list);
        mAdapter = new PhoneListAdapter(this);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        mPhoneViewModel = new ViewModelProvider(this)
                .get(PhoneViewModel.class);

        mPhoneViewModel.getAllPhones().observe(this,
                elements -> mAdapter.setElementList(elements));

        Listeners();

        mActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if(result.getResultCode() == RESULT_OK){
                        Toast.makeText(MainActivity.this, getString(R.string.record_added), Toast.LENGTH_LONG).show();
                    }
        });

        swipedItemDelete(recyclerView);
    }

    private void Listeners(){
        FloatingActionButton FAB = findViewById(R.id.floatingActionButton);
        FAB.setOnClickListener(view -> {
            Intent intent = new Intent(this, InsertActivity.class);
            startSecondActivity(intent);
        });
    }

    private void swipedItemDelete(RecyclerView recyclerView){
        ItemTouchHelper.SimpleCallback itemTouchCallback =
                new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int swipedPosition = viewHolder.getAdapterPosition();
                mPhoneViewModel.delete(mAdapter.getPhone(swipedPosition));
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.clear){
            mPhoneViewModel.deleteAll();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void startSecondActivity(Intent intent) {
        mActivityResultLauncher.launch(intent);
    }

    @Override
    public void OnItemClickListener(Phone phone) {
        final Intent intent;
        intent = new Intent(this, UpdateActivity.class);
        intent.putExtra(MANUFACTURER_KEY,phone.getManufacturer());
        intent.putExtra(MODEL_KEY,phone.getModel());
        intent.putExtra(ID_KEY,phone.getId());
        intent.putExtra(WEBSITE_KEY,phone.getWebsite());
        intent.putExtra(VERSION_KEY,phone.getVersion());
        this.startActivity(intent);
    }
}