package com.example.deliverywala.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.*;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.deliverywala.R;
import com.example.deliverywala.adapters.FoodAdapter;
import com.example.deliverywala.model.FoodDetails;
import com.example.deliverywala.util.ConnectionManager;
import com.example.deliverywala.util.Constants;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class HomeFragment extends Fragment {
    private RecyclerView recyclerHome;
    private RecyclerView recyclerCategoryGrid, recyclerPopular;
   private SearchView searchView;

    private RecyclerView.LayoutManager layoutManager;
    private FoodAdapter recyclerAdapter;
    private RelativeLayout progresslayout;
    private ProgressBar progressBar;
    private final List<FoodDetails> restaurantsList = new ArrayList<>();

    private final Comparator<FoodDetails> ratingComparator = new Comparator<FoodDetails>() {
        @Override
        public int compare(FoodDetails restaurant1, FoodDetails restaurant2) {
            if (restaurant1.getFoodAmount().compareToIgnoreCase(restaurant2.getFoodAmount()) == 0) {
                return restaurant1.getFoodName().compareToIgnoreCase(restaurant2.getFoodName());
            } else {
                return restaurant1.getFoodAmount().compareToIgnoreCase(restaurant2.getFoodAmount());
            }
        }
    };

    private final Comparator<FoodDetails> alphabeticalComparator = new Comparator<FoodDetails>() {
        @Override
        public int compare(FoodDetails restaurant1, FoodDetails restaurant2) {
            return restaurant1.getFoodName().compareToIgnoreCase(restaurant2.getFoodName());
        }
    };

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, 
                           @Nullable ViewGroup container,
                           @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        setHasOptionsMenu(true);



        recyclerHome = view.findViewById(R.id.recyclerHome);
        progresslayout = view.findViewById(R.id.progressLayout);
        progressBar = view.findViewById(R.id.progressBar);
        layoutManager = new LinearLayoutManager(getActivity());
        progresslayout.setVisibility(View.VISIBLE);

       if (!new ConnectionManager().checkConnectivity(getActivity())) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
            dialog.setTitle("Error");
            dialog.setMessage("Internet connection is not found");
            dialog.setPositiveButton("Open settings", (dialogInterface, which) -> {
                Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                startActivity(intent);
                getActivity().finish();
            });
            dialog.setNegativeButton("Exit app", (dialogInterface, which) -> {
                ActivityCompat.finishAffinity(getActivity());
            });
            dialog.create().show();
        } else {
            progresslayout.setVisibility(View.GONE);
            FirebaseDatabase.getInstance().getReference(Constants.DBItemName)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot postsnapshot : snapshot.getChildren()) {
                            try {
                                FoodDetails user = postsnapshot.getValue(FoodDetails.class);
                                Log.e("name", user.getFoodName());
                                restaurantsList.add(user);
                            } catch (Exception e) {
                                Log.e("name", e.toString());
                            }
                        }
                        recyclerAdapter = new FoodAdapter(getActivity(), (ArrayList<FoodDetails>) restaurantsList);
                        recyclerHome.setAdapter(recyclerAdapter);
                        recyclerHome.setLayoutManager(layoutManager);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Error handling
                    }
                });
        }
        return view;
    }



    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_sort, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.actionSort) {
            AlertDialog.Builder dialog = getBuilder();

            dialog.setNeutralButton("Rating(High to Low)", (dialogInterface, which) -> {
                restaurantsList.sort(ratingComparator);
                Collections.reverse(restaurantsList);
                recyclerAdapter.notifyDataSetChanged();
            });
            
            dialog.create().show();
        }
        return super.onOptionsItemSelected(item);
    }

    private AlertDialog.Builder getBuilder() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setTitle("Sort By?");
        dialog.setIcon(R.drawable.ic_sort2_foreground);

        dialog.setPositiveButton("A-Z", (dialogInterface, which) -> {
            restaurantsList.sort(alphabeticalComparator);
            recyclerAdapter.notifyDataSetChanged();
        });

        dialog.setNegativeButton("Z-A", (dialogInterface, which) -> {
            restaurantsList.sort(alphabeticalComparator);
            Collections.reverse(restaurantsList);
            recyclerAdapter.notifyDataSetChanged();
        });
        return dialog;
    }
}