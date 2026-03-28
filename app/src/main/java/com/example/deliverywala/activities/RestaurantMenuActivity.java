package com.example.deliverywala.activities;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.example.deliverywala.R;
import com.example.deliverywala.adapters.RestaurantMenuRecyclerAdapter;
import com.example.deliverywala.database.FoodDatabase;
import com.example.deliverywala.model.FoodDetails;
import com.example.deliverywala.util.ConnectionManager;
import com.example.deliverywala.util.Constants;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class RestaurantMenuActivity extends AppCompatActivity {

    private RecyclerView recyclerRestaurantmenu;
    private RecyclerView.LayoutManager layoutManager;
    private RestaurantMenuRecyclerAdapter recyclerAdapter;
    private ProgressBar progressBar;
    private RelativeLayout progressLayout;
    private Button btnProceed;
    private RelativeLayout rlProceed;
    private Toolbar toolbar;
    private String restaurant_id;
    private List<FoodDetails> restaurantMenu = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_menu);

        initializeViews();
        setupToolbar();
        checkConnectivity();
    }

    private void initializeViews() {
        toolbar = findViewById(R.id.toolbar);
        recyclerRestaurantmenu = findViewById(R.id.recyclerRestrauntMenu);
        progressLayout = findViewById(R.id.progressLayout);
        progressBar = findViewById(R.id.progressBar);
        btnProceed = findViewById(R.id.btnProceed);
        rlProceed = findViewById(R.id.rlProceed);
        layoutManager = new LinearLayoutManager(this);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getIntent().getStringExtra("restaurant_name"));
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void checkConnectivity() {
        if (new ConnectionManager().checkConnectivity(this)) {
            progressLayout.setVisibility(View.VISIBLE);
            btnProceed.setVisibility(View.GONE);
            restaurant_id = getIntent().getStringExtra("restaurant_id");
            if (restaurant_id == null) {
                Toast.makeText(this, "Some Unexpected error occurred", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
            fetchMenuFromFirebase();
        } else {
            showNoInternetDialog();
        }
    }

    private void fetchMenuFromFirebase() {
        FirebaseDatabase.getInstance().getReference(Constants.DBItemName)
            .addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot postsnapshot : snapshot.getChildren()) {
                        try {
                            FoodDetails user = postsnapshot.getValue(FoodDetails.class);
                            if (user != null) {
                                Log.e("name", user.getFoodName());
                                restaurantMenu.add(user);
                            }
                        } catch (Exception e) {
                            Log.e("name", e.toString());
                        }
                    }
                    setupRecyclerView();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("FirebaseError", error.getMessage());
                }
            });
    }

    private void setupRecyclerView() {
        recyclerAdapter = new RestaurantMenuRecyclerAdapter(
            this,
            restaurant_id,
            getIntent().getStringExtra("restaurant_name"),
            rlProceed,
            btnProceed,
            restaurantMenu
        );
        recyclerRestaurantmenu.setAdapter(recyclerAdapter);
        recyclerRestaurantmenu.setLayoutManager(layoutManager);
        progressLayout.setVisibility(View.GONE);
    }

    private void showNoInternetDialog() {
        new AlertDialog.Builder(this)
            .setTitle("Error")
            .setMessage("Internet Connection Not Found")
            .setPositiveButton("Open Settings", (dialog, which) -> {
                startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                finish();
            })
            .setNegativeButton("Exit", (dialog, which) -> {
                ActivityCompat.finishAffinity(this);
            })
            .create()
            .show();
    }

    @Override
    public void onBackPressed() {
        if (recyclerAdapter != null && recyclerAdapter.getItemCount() > 0) {
            new AlertDialog.Builder(this)
                .setTitle("Alert!")
                .setMessage("Going back will remove everything from cart")
                .setPositiveButton("Okay", (dialog, which) -> {
                    recyclerAdapter.getItemCount();
                    new DBAsyncTask(this, 0).execute();
                    super.onBackPressed();
                })
                .setNegativeButton("No", null)
                .show();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_sort, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.actionSort) {
            showSortDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showSortDialog() {
        new AlertDialog.Builder(this)
            .setTitle("Sort By?")
            .setPositiveButton("Price(Low to High)", (dialog, which) -> {
                restaurantMenu.sort((o1, o2) -> Double.compare(
                    Double.parseDouble(o1.getFoodAmount()),
                    Double.parseDouble(o2.getFoodAmount())
                ));
                recyclerAdapter.notifyDataSetChanged();
            })
            .setNegativeButton("Price(High to Low)", (dialog, which) -> {
                restaurantMenu.sort((o1, o2) -> Double.compare(
                    Double.parseDouble(o2.getFoodAmount()),
                    Double.parseDouble(o1.getFoodAmount())
                ));
                recyclerAdapter.notifyDataSetChanged();
            })
            .setIcon(R.drawable.ic_sort2_foreground)
            .show();
    }

    private static class DBAsyncTask extends AsyncTask<Void, Void, Boolean> {
        private final Context context;
        private final int mode;
        private FoodDatabase db;

        DBAsyncTask(Context context, int mode) {
            this.context = context;
            this.mode = mode;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            db = Room.databaseBuilder(context, FoodDatabase.class, "food-db").build();
            try {
                switch (mode) {
                    case 0: // Remove all items from cart
                        db.FoodDao().clearCart();
                        return true;
                    case 1: // Check if cart is empty or not
                        return db.FoodDao().getAllFoods().size() > 0;
                }
            } finally {
                db.close();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (mode == 0) {
                String message = result ? "Cart database cleared" : "Cart database not cleared";
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            }
        }
    }
}