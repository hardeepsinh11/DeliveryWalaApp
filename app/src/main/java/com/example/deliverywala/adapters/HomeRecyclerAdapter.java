package com.example.deliverywala.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import com.example.deliverywala.R;
import com.example.deliverywala.activities.RestaurantMenuActivity;
import com.example.deliverywala.model.Restaurants;
import com.example.deliverywala.database.RestaurantDatabase;
import com.example.deliverywala.database.RestaurantEntity;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;

public class HomeRecyclerAdapter extends RecyclerView.Adapter<HomeRecyclerAdapter.HomeViewHolder> {
    private Context context;
    private ArrayList<Restaurants> itemList;

    public HomeRecyclerAdapter(Context context, ArrayList<Restaurants> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public HomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_home_single_row, parent, false);
        return new HomeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeViewHolder holder, int position) {
        Restaurants restaurant = itemList.get(position);
        holder.txtRestaurantName.setText(restaurant.getFoodName());
        holder.txtAddress.setText(restaurant.getFoodDescription());
        holder.txtRestaurantRating.setText(restaurant.getFoodAmount());
        Picasso.get().load(restaurant.getFoodImage()).error(R.drawable.non).into(holder.imgFood);

        holder.content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, RestaurantMenuActivity.class);
                intent.putExtra("restaurant_id", restaurant.getFoodId());
                intent.putExtra("restaurant_name", restaurant.getFoodName());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class HomeViewHolder extends RecyclerView.ViewHolder {
        TextView txtRestaurantName, txtRestaurantRating, txtAddress;
        ImageView imgFood, imgFav;
        RelativeLayout content;

        public HomeViewHolder(@NonNull View itemView) {
            super(itemView);
            txtRestaurantName = itemView.findViewById(R.id.txtRestaurantName);
            txtRestaurantRating = itemView.findViewById(R.id.txtRestaurantRating);
            txtAddress = itemView.findViewById(R.id.txtAddress);
            imgFood = itemView.findViewById(R.id.imgFood);
            imgFav = itemView.findViewById(R.id.imgFav);
            content = itemView.findViewById(R.id.content);
        }
    }

    public static class DBAsyncTask extends AsyncTask<Void, Void, Boolean> {
        private Context context;
        private RestaurantEntity restaurantEntity;
        private int mode;
        private RestaurantDatabase db;

        public DBAsyncTask(Context context, RestaurantEntity restaurantEntity, int mode) {
            this.context = context;
            this.restaurantEntity = restaurantEntity;
            this.mode = mode;
            this.db = Room.databaseBuilder(context, RestaurantDatabase.class, "restaurant-db").build();
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            switch (mode) {
                case 1:
                    RestaurantEntity restaurant = db.RestaurantDao().getRestaurantsById(String.valueOf(restaurantEntity.restaurant_id));
                    db.close();
                    return restaurant != null;
                case 2:
                    db.RestaurantDao().insertRestaurant(restaurantEntity);
                    db.close();
                    return true;
                case 3:
                    db.RestaurantDao().deleteRestaurant(restaurantEntity);
                    db.close();
                    return true;
                default:
                    return false;
            }
        }
    }
}