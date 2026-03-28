package com.example.deliverywala.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "FoodItems")
public class RestaurantEntity {
    @PrimaryKey
    public final int restaurant_id;
    
    @ColumnInfo
    public final String restaurantName;
    
    @ColumnInfo
    public final String restaurantRating;
    
    @ColumnInfo
    public final String address;
    
    @ColumnInfo
    public final String foodImage;

    public RestaurantEntity(int restaurant_id, String restaurantName, 
                          String restaurantRating, String address, String foodImage) {
        this.restaurant_id = restaurant_id;
        this.restaurantName = restaurantName;
        this.restaurantRating = restaurantRating;
        this.address = address;
        this.foodImage = foodImage;
    }
}