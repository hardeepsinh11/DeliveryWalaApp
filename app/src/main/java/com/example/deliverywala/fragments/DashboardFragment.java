package com.example.deliverywala.fragments;

// Required Android imports

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.deliverywala.R;
import com.example.deliverywala.activities.CartActivity;
import com.example.deliverywala.adapters.DashboardAdapter;
import com.example.deliverywala.database.FoodDatabase;
import com.example.deliverywala.database.FoodEntity;
import com.example.deliverywala.model.FoodDetails;
import com.example.deliverywala.util.ConnectionManager;
import com.example.deliverywala.util.Constants;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DashboardFragment extends Fragment {


    // UI Components
    private RecyclerView recyclerRestaurantmenu;
    private RecyclerView.LayoutManager layoutManager;
    private DashboardAdapter recyclerAdapter;
    private ProgressBar progressBar;
    private RelativeLayout progressLayout;
    private Button btnProceed;
    private RelativeLayout rlProceed;
    private Toolbar toolbar;

    // Data variables
    private String restaurant_id = null;
    private final List<FoodDetails> restaurantMenu = new ArrayList<>();
    private FoodDatabase db;

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.dashboard_fragment, container, false);
        setHasOptionsMenu(true);

        // Initialize views
        recyclerRestaurantmenu = view.findViewById(R.id.recyclerRestrauntMenu);
        progressLayout = view.findViewById(R.id.progressLayout);
        progressBar = view.findViewById(R.id.progressBar);
        btnProceed = view.findViewById(R.id.btnProceed);
        rlProceed = view.findViewById(R.id.rlProceed);
        recyclerRestaurantmenu.setLayoutManager(layoutManager);
        // Setup RecyclerView
        layoutManager = new LinearLayoutManager(requireActivity());
        progressLayout.setVisibility(View.VISIBLE);
        btnProceed.setVisibility(View.GONE);

        // Check internet connectivity
        if (new ConnectionManager().checkConnectivity(requireActivity())) {
            // Initialize database instance
            db = FoodDatabase.getDbInstant(requireActivity());
            progressLayout.setVisibility(View.VISIBLE);
            loadFoodItems();
        } else {
            // No internet connection - show error dialog
            progressLayout.setVisibility(View.GONE);
            showNoInternetDialog();
        }
        btnProceed.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), CartActivity.class);
            startActivity(intent);
        });
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override

            public void handleOnBackPressed() {
                if (recyclerAdapter != null) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(requireContext());
                    dialog.setTitle("Alert!");
                    dialog.setMessage("Going back will remove everything from cart");
                    dialog.setPositiveButton("Okay", (text, listener) -> {

                        new Thread(() -> {
                            db.FoodDao().clearCart(); // જો નામ બદલ્યું હોય તો restaurantDao() કરજે

                            requireActivity().runOnUiThread(() -> {
                                for (FoodDetails food : restaurantMenu) {
                                    food.setQty(0);
                                }
                                recyclerAdapter.notifyDataSetChanged();
                                requireActivity().finish(); // onBackPressed ને બદલે સીધું ફિનિશ કરો
                            });
                        }).start();

                    });
                    dialog.setNegativeButton("No", (text, listener) -> {
                    });
                    dialog.create().show();
                } else {
                    requireActivity().onBackPressed();
                }
            }
        });
        return view;
    }
    private void loadFoodItems() {
        progressLayout.setVisibility(View.VISIBLE);
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference(Constants.DBItemName);

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                restaurantMenu.clear();

                for (DataSnapshot foodSnapshot : snapshot.getChildren()) {
                    try {
                        FoodDetails food = foodSnapshot.getValue(FoodDetails.class);
                        if (food != null) {
                            food.setQty(0);
                            restaurantMenu.add(food);
                        }
                    } catch (Exception e) {
                        Log.e("Dashboard", "Error loading food item: " + e.getMessage());
                    }
                }
                if (recyclerAdapter == null) {
                    recyclerAdapter = new DashboardAdapter(
                            requireActivity(),
                            restaurant_id,
                            rlProceed,
                            btnProceed,
                            restaurantMenu
                    );
                    recyclerRestaurantmenu.setAdapter(recyclerAdapter);
                    recyclerRestaurantmenu.setLayoutManager(layoutManager);
                } else {

                    recyclerAdapter.notifyDataSetChanged();
                }
                progressLayout.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                progressLayout.setVisibility(View.GONE);
                Log.e("Dashboard", "Database error: " + error.getMessage());
            }
        });
    }

    private void showNoInternetDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
        dialog.setTitle("Error");
        dialog.setMessage("Internet Connection Not Found");
        dialog.setPositiveButton("Open Settings", (text, listener) -> {
            Intent settingsIntent = new Intent(Settings.ACTION_WIFI_SETTINGS);
            startActivity(settingsIntent);
            getActivity().finish();
        });
        dialog.setNegativeButton("Exit", (text, listener) -> {
            ActivityCompat.finishAffinity(getActivity());
        });
        dialog.create();
        dialog.show();
    }
    private void syncMenuWithDatabase() {
        new Thread(() -> {
            // ૧. ડેટાબેઝમાંથી અત્યારે જે કાર્ટમાં છે એ બધું ઉપાડો
            List<FoodEntity> cartItems = db.FoodDao().getAllFoods();

            // ૨. પહેલા આખું મેનુ ૦ કરી નાખો (Reset)
            for (FoodDetails food : restaurantMenu) {
                food.setQty(0);
            }

            // ૩. હવે જે આઈટમ કાર્ટમાં છે, તેની સંખ્યા મેનુમાં અપડેટ કરો
            for (FoodEntity cartItem : cartItems) {
                for (FoodDetails menuItem : restaurantMenu) {
                    if (Integer.parseInt(menuItem.getFoodId()) == cartItem.food_id) {
                        menuItem.setQty(Integer.parseInt(cartItem.quantity));
                    }
                }
            }

            // ૪. UI રિફ્રેશ કરો
            requireActivity().runOnUiThread(() -> {
                if (recyclerAdapter != null) {
                    recyclerAdapter.notifyDataSetChanged();
                }
            });
        }).start();
    }
    @Override
    public void onResume() {
        super.onResume();
        // જેવું તું કાર્ટમાંથી પાછો આવશે, આ મેથડ રન થશે અને બધું '૦' કરી દેશે જો કાર્ટ ખાલી હશે તો!
        if (!restaurantMenu.isEmpty()) {
            syncMenuWithDatabase();
        }
    }




}

            // Fetch restaurant menu from Firebase
         /*   FirebaseDatabase.getInstance().getReference(Constants.DBItemName)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            // Clear existing data
                            restaurantMenu.clear();
                            db.FoodDao().clearCart();

                            // Process each food item from Firebase
                            for (DataSnapshot postsnapshot : snapshot.getChildren()) {
                                try {
                                    // Convert Firebase data to FoodDetails object
                                    FoodDetails user = postsnapshot.getValue(FoodDetails.class);
                                    Log.e("DashboardFragment", "Loaded food: " + user.getFoodName());
                                    user.setQty(0);  // Initialize quantity to 0
                                    restaurantMenu.add(user);
                                } catch (Exception e) {
                                    Log.e("DashboardFragment", "Error loading food item", e);
                                }
                            }

                            // Setup RecyclerView adapter with the retrieved data
                            recyclerAdapter = new DashboardAdapter(
                                    requireActivity(),
                                    restaurant_id,
                                    rlProceed,
                                    btnProceed,
                                    restaurantMenu
                            );
                            recyclerRestaurantmenu.setAdapter(recyclerAdapter);
                            recyclerRestaurantmenu.setLayoutManager(layoutManager);
                            progressLayout.setVisibility(View.GONE);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            // Hide progress bar on error
                            progressLayout.setVisibility(View.GONE);
                            Log.e("DashboardFragment", "Firebase error: " + error.getMessage());
                        }
                    });
        } else {
            // No internet connection - show error dialog
            progressLayout.setVisibility(View.GONE);
            showNoInternetDialog();
        }
        return view;
    }


    private void showNoInternetDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(requireActivity());
        dialog.setTitle("Error");
        dialog.setMessage("Internet Connection Not Found");

        // Open Settings button
        dialog.setPositiveButton("Open Settings", (dialogInterface, which) -> {
            Intent settingsIntent = new Intent(Settings.ACTION_WIFI_SETTINGS);
            startActivity(settingsIntent);
            requireActivity().finish();
        });

        // Exit button
        dialog.setNegativeButton("Exit", (dialogInterface, which) -> {
            ActivityCompat.finishAffinity(requireActivity());
        });

        dialog.create().show();
    }
}
/*
val dbRef = FirebaseDatabase.getInstance().getReference(Constants.DBItemName)
            dbRef.

addValueEventListener(object :ValueEventListener {
    override fun onDataChange(snapshot:DataSnapshot){
        restaurantMenu.clear()
        db !!.FoodDao().clearCart()
//                    val cartItems = db!!.FoodDao().getAllFoods()
//                    Log.e("cartItems_size",cartItems.size.toString())
        for (postsnapshot in snapshot.children) {
//                        val rid = postsnapshot.child("foodId").getValue()
//                        val name = postsnapshot.child("foodName").getValue()
//                        val rating = postsnapshot.child("foodAmount").getValue()
//                        val address = postsnapshot.child("foodDescription").getValue()
//                        val image = postsnapshot.child("foodImage").getValue()
            try {
                val user:FoodDetails = postsnapshot.getValue(FoodDetails:: class.java)!!
                        Log.e("name", user.foodName.toString())
                user.qty = 0
                restaurantMenu.add(user)

            } catch (e:Exception){
                Log.e("name", e.toString())

            }/*
                        val restaurantObject = Restaurants(
                            rid.toString(),
                            name.toString(),
                            rating.toString(),
                            address.toString(),
                            image.toString()
                        )
                        restaurantsList.add(restaurantObject)*/
        /*

        recyclerAdapter = DashboardAdapter(
                requireActivity(),
                restaurant_id,
//                        intent.getStringExtra("restaurant_name"),
                rlProceed,//pass the relative layout which has the button to enable it later
                btnProceed,
                restaurantMenu
        )
        recyclerRestaurantmenu.adapter = recyclerAdapter
        recyclerRestaurantmenu.layoutManager = layoutManager
        progressLayout.visibility = GONE

    }

    override fun onCancelled(error:DatabaseError){
        progressLayout.visibility = GONE

    }
})
        }else{
progressLayout.visibility =GONE
val dialog = AlertDialog.Builder(requireActivity())
            dialog.

setTitle("Error")
            dialog.

setMessage("Internet Connection Not Found")
            dialog.

setPositiveButton("Open Settings") {
    text, listener ->
            val settingsIntent = Intent(Settings.ACTION_WIFI_SETTINGS)
    startActivity(settingsIntent)
    requireActivity().finish()
}
            dialog.

setNegativeButton("Exit") {
    text, listener ->
            ActivityCompat.finishAffinity(requireActivity())
}
            dialog.

create()
            dialog.

show()
        }
                return view
    }
    /*
        fun onBackPressed() {
            if (recyclerAdapter.getSelectedItemCount() > 0) {
                val alterDialog = AlertDialog.Builder(requireActivity())
                alterDialog.setTitle("Alert!")
                alterDialog.setMessage("Going back will remove everything from cart")
                alterDialog.setPositiveButton("Okay")
                { _, _ ->
                    recyclerAdapter.itemSelectedCount = 0
                    if (DBAsyncTask(requireActivity(), 0).execute().get()) {
                        //cleared
                        Toast.makeText(
                            requireActivity(),
                            "Cart database cleared",
                            Toast.LENGTH_SHORT
                        ).show()
                        super.onBackPressed()
                    } else {
                        Toast.makeText(
                            requireActivity(),
                            "Cart database not cleared",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    super.onBackPressed()
                }
                alterDialog.setNegativeButton("No")
                { _, _ ->
                    //do nothing
                }
                alterDialog.show()
            } else {
                super.onBackPressed()
            }
        }*/

