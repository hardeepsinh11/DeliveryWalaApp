package com.example.deliverywala.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "foods")
public class FoodEntity {
    @PrimaryKey
    public final int food_id;
    
    @ColumnInfo(name = "food_name")
    public final String foodName;
    
    @ColumnInfo(name = "food_price")
    public final String foodPrice;
    
    @ColumnInfo(name = "restaurant_name")
    public final String restaurantName;
    
    @ColumnInfo(name = "restaurantId")
    public final String restaurantId;
    
    @ColumnInfo(name = "qty")
    public final String quantity;

    public FoodEntity(int food_id, String foodName, String foodPrice, 
                     String restaurantName, String restaurantId, String quantity) {
        this.food_id = food_id;
        this.foodName = foodName;
        this.foodPrice = foodPrice;
        this.restaurantName = restaurantName;
        this.restaurantId = restaurantId;
        this.quantity = quantity;
    }
}