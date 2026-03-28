package com.example.deliverywala.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.*;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import com.example.deliverywala.R;
import com.example.deliverywala.adapters.HomeRecyclerAdapter;
import com.example.deliverywala.model.Restaurants;
import com.example.deliverywala.database.RestaurantDatabase;
import com.example.deliverywala.database.RestaurantEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class FavouritesFragment extends Fragment {
    private RecyclerView recyclerFav;
    private RecyclerView.LayoutManager layoutManager;
    private HomeRecyclerAdapter recyclerAdapter;
    private RelativeLayout progresslayout;
    private ProgressBar progressBar;
    private ArrayList<Restaurants> dbRestaurantList = new ArrayList<>();

    private final Comparator<Restaurants> ratingComparator = new Comparator<Restaurants>() {
        @Override
        public int compare(Restaurants restaurant1, Restaurants restaurant2) {
            if (restaurant1.getFoodAmount().compareToIgnoreCase(restaurant2.getFoodAmount()) == 0) {
                return restaurant1.getFoodName().compareToIgnoreCase(restaurant2.getFoodName());
            } else {
                return restaurant1.getFoodAmount().compareToIgnoreCase(restaurant2.getFoodAmount());
            }
        }
    };

    private final Comparator<Restaurants> alphabeticalComparator = new Comparator<Restaurants>() {
        @Override
        public int compare(Restaurants restaurant1, Restaurants restaurant2) {
            return restaurant1.getFoodName().compareToIgnoreCase(restaurant2.getFoodName());
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, 
                           @Nullable ViewGroup container,
                           @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favourites, container, false);
        setHasOptionsMenu(true);
        
        recyclerFav = view.findViewById(R.id.recyclerFav);
        progresslayout = view.findViewById(R.id.progressLayout);
        progressBar = view.findViewById(R.id.progressBar);
        progresslayout.setVisibility(View.VISIBLE);
        layoutManager = new LinearLayoutManager(getActivity());

        try {
            List<RestaurantEntity> restaurantList = new RetrieveFavourites(getActivity()).execute().get();
            for (RestaurantEntity i : restaurantList) {
                dbRestaurantList.add(new Restaurants(
                        String.valueOf(i.restaurant_id),
                    i.restaurantName,
                    i.address,
                    i.restaurantRating,
                    i.foodImage
                ));
            }

            if (dbRestaurantList.isEmpty()) {
                Toast.makeText(getContext(), "You have not selected any restaurant as your favourite", Toast.LENGTH_SHORT).show();
            }

            if (getActivity() != null) {
                progresslayout.setVisibility(View.GONE);
                recyclerAdapter = new HomeRecyclerAdapter(getActivity(), dbRestaurantList);
                recyclerFav.setAdapter(recyclerAdapter);
                recyclerFav.setLayoutManager(layoutManager);
            }
        } catch (Exception e) {
            e.printStackTrace();
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
            AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
            dialog.setTitle("Sort By?");
            dialog.setIcon(R.drawable.ic_sort2_foreground);
            
            dialog.setPositiveButton("A-Z", (dialogInterface, which) -> {
                Collections.sort(dbRestaurantList, alphabeticalComparator);
                recyclerAdapter.notifyDataSetChanged();
            });
            
            dialog.setNegativeButton("Z-A", (dialogInterface, which) -> {
                Collections.sort(dbRestaurantList, alphabeticalComparator);
                Collections.reverse(dbRestaurantList);
                recyclerAdapter.notifyDataSetChanged();
            });
            
            dialog.setNeutralButton("Rating(High to Low)", (dialogInterface, which) -> {
                Collections.sort(dbRestaurantList, ratingComparator);
                recyclerAdapter.notifyDataSetChanged();
            });
            
            dialog.create().show();
        }
        return super.onOptionsItemSelected(item);
    }

    private static class RetrieveFavourites extends AsyncTask<Void, Void, List<RestaurantEntity>> {
        private Context context;

        public RetrieveFavourites(Context context) {
            this.context = context;
        }

        @Override
        protected List<RestaurantEntity> doInBackground(Void... voids) {
            RestaurantDatabase db = Room.databaseBuilder(context, 
                RestaurantDatabase.class, "restaurant-db").build();
            return db.RestaurantDao().getAllRestaurants();
        }
    }
}