package com.example.deliverywala.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.example.deliverywala.R;
import com.example.deliverywala.activities.CartActivity;
import com.example.deliverywala.database.FoodDatabase;
import com.example.deliverywala.database.FoodEntity;
import com.example.deliverywala.model.FoodDetails;
import com.squareup.picasso.Picasso;
import java.util.List;

public class RestaurantMenuRecyclerAdapter extends RecyclerView.Adapter<RestaurantMenuRecyclerAdapter.RestaurantMenuViewHolder> {
    private Context context;
    private String restaurantId;
    private String restaurantName;
    private RelativeLayout proceedPassed;
    private Button btnProceed;
    private List<FoodDetails> itemList;
    private int itemSelectedCount = 0;
    private RelativeLayout proceedToCart;
    private Integer totalCost = 0;
    private FoodDatabase db;

    public RestaurantMenuRecyclerAdapter(Context context, String restaurantId, String restaurantName,
                                       RelativeLayout proceedPassed, Button btnProceed,
                                       List<FoodDetails> itemList) {
        this.context = context;
        this.restaurantId = restaurantId;
        this.restaurantName = restaurantName;
        this.proceedPassed = proceedPassed;
        this.btnProceed = btnProceed;
        this.itemList = itemList;
        this.db = FoodDatabase.getDbInstant(context);
        recalculateCartValues();
        this.btnProceed.setOnClickListener(v -> {
            Intent intent = new Intent(context, CartActivity.class);
            intent.putExtra("restaurantId", restaurantId);
            intent.putExtra("restaurantName", restaurantName);
            intent.putExtra("total_cost", totalCost); // અહીં ગ્લોબલ totalCost વપરાશે
            context.startActivity(intent);
        });
    }
    public void recalculateCartValues() {
        new Thread(() -> {
            List<FoodEntity> cartItems = db.FoodDao().getAllFoods();
            itemSelectedCount = cartItems.size();
            totalCost = 0;
            for (FoodEntity item : cartItems) {
                totalCost += Integer.parseInt(item.foodPrice);
            }

            // UI અપડેટ કરો
            ((android.app.Activity) context).runOnUiThread(() -> {
                if (itemSelectedCount > 0) {
                    proceedPassed.setVisibility(View.VISIBLE);
                } else {
                    proceedPassed.setVisibility(View.GONE);
                }
            });
        }).start();
    }

    @NonNull
    @Override
    public RestaurantMenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_restaurant_menu_single_row, parent, false);
        return new RestaurantMenuViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RestaurantMenuViewHolder holder, int position) {
        FoodDetails menu = itemList.get(position);
        proceedToCart = proceedPassed;
        holder.txtFoodName.setText(menu.getFoodName());
        Picasso.get().load(menu.getFoodImage()).error(R.drawable.ic_logo_b).into(holder.imgFood);
        holder.txtPrice.setText("Rs." + menu.getFoodAmount());

        FoodEntity foodEntity = new FoodEntity(
                Integer.parseInt(menu.getFoodId()),
                menu.getFoodName(),
                menu.getFoodAmount(),
                menu.getFoodName(),
                restaurantId,
                String.valueOf(menu.getQty())
        );



        new Thread(() -> {
            FoodEntity check = db.FoodDao().getFoodById(foodEntity.food_id);
            ((android.app.Activity) context).runOnUiThread(() -> {
                if (check != null) {
                    holder.btnAdd.setText("Remove");
                    holder.btnAdd.setBackgroundColor(ContextCompat.getColor(context, R.color.yellow));
                } else {
                    holder.btnAdd.setText("Add");
                    holder.btnAdd.setBackgroundColor(ContextCompat.getColor(context, R.color.app_color));
                }
            });
        }).start();

        holder.btnAdd.setOnClickListener(v -> {
            new Thread(() -> {
                FoodEntity checkExists = db.FoodDao().getFoodById(foodEntity.food_id);

                if (checkExists == null) {
                    // ADD LOGIC
                    db.FoodDao().insertFood(foodEntity);
                    totalCost += Integer.parseInt(menu.getFoodAmount());
                    itemSelectedCount++;

                    updateUI(holder, "Remove", R.color.yellow);
                } else {
                    // REMOVE LOGIC
                    db.FoodDao().deleteFood(foodEntity);
                    totalCost -= Integer.parseInt(menu.getFoodAmount());
                    itemSelectedCount--;

                    updateUI(holder, "Add", R.color.app_color);
                }

                // પ્રોસીડ બટન કંટ્રોલ કરો
                ((android.app.Activity) context).runOnUiThread(() -> {
                    if (itemSelectedCount > 0) proceedPassed.setVisibility(View.VISIBLE);
                    else proceedPassed.setVisibility(View.GONE);
                });
            }).start();
        });
    }

    private void updateUI(RestaurantMenuViewHolder holder, String text, int color) {
        ((android.app.Activity) context).runOnUiThread(() -> {
            holder.btnAdd.setText(text);
            holder.btnAdd.setBackgroundColor(ContextCompat.getColor(context, color));
        });

    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class RestaurantMenuViewHolder extends RecyclerView.ViewHolder {
        ImageView imgFood;
        TextView txtFoodName;
        TextView txtPrice;
        Button btnAdd;

        public RestaurantMenuViewHolder(View view) {
            super(view);
            imgFood = view.findViewById(R.id.imgFood);
            txtFoodName = view.findViewById(R.id.txtFoodname);
            txtPrice = view.findViewById(R.id.txtPrice);
            btnAdd = view.findViewById(R.id.btnAdd);
        }
    }

    public static class DBAsyncTaskCart extends AsyncTask<Void, Void, Boolean> {
        private Context context;
        private FoodEntity foodEntity;
        private int mode;
        private FoodDatabase db;

        public DBAsyncTaskCart(Context context, FoodEntity foodEntity, int mode) {
            this.context = context;
            this.foodEntity = foodEntity;
            this.mode = mode;
            this.db = FoodDatabase.getDbInstant(context);
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            switch (mode) {
                case 1:
                    FoodEntity food = db.FoodDao().getFoodById(Integer.parseInt(String.valueOf(foodEntity.food_id)));
                    return food != null;
                case 2:
                    db.FoodDao().insertFood(foodEntity);
                    return true;
                case 3:
                    db.FoodDao().deleteFood(foodEntity);
                    return true;
            }
            return false;
        }
    }
}