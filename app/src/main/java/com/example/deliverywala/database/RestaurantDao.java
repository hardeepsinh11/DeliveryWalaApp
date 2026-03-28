package com.example.deliverywala.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

@Dao
public interface RestaurantDao {
    @Insert
    void insertRestaurant(RestaurantEntity restaurantEntity);

    @Delete
    void deleteRestaurant(RestaurantEntity restaurantEntity);

    @Query("SELECT * FROM FoodItems")
    List<RestaurantEntity> getAllRestaurants();

    @Query("SELECT * FROM FoodItems WHERE restaurant_id=:restaurantId")
    RestaurantEntity getRestaurantsById(String restaurantId);
}