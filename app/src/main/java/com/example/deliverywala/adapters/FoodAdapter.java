package com.example.deliverywala.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.deliverywala.R;
import com.example.deliverywala.activities.RestaurantMenuActivity;
import com.example.deliverywala.model.FoodDetails;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.List;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.HomeViewHolder> {
    private Context context;
    private ArrayList<FoodDetails> itemList;



    public FoodAdapter(Context context, ArrayList<FoodDetails> itemList) {
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
        FoodDetails restaurant = itemList.get(position);
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
                Log.e("clicked", restaurant.getFoodName());
            }
        });
    }


    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class HomeViewHolder extends RecyclerView.ViewHolder {
        TextView txtRestaurantName;
        TextView txtRestaurantRating;
        TextView txtAddress;
        ImageView imgFood;
        ImageView imgFav;
        RelativeLayout content;

        public HomeViewHolder(View view) {
            super(view);
            txtRestaurantName = view.findViewById(R.id.txtRestaurantName);
            txtRestaurantRating = view.findViewById(R.id.txtRestaurantRating);
            txtAddress = view.findViewById(R.id.txtAddress);
            imgFood = view.findViewById(R.id.imgFood);
            imgFav = view.findViewById(R.id.imgFav);
            content = view.findViewById(R.id.content);
        }
    }

}
