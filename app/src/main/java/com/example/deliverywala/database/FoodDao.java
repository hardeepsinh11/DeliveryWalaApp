package com.example.deliverywala.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

@Dao
public interface FoodDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertFood(FoodEntity foodEntity);


    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateFood(FoodEntity foodEntity);

    @Delete
    void deleteFood(FoodEntity foodEntity);

    @Query("DELETE FROM foods where food_id=:food_id AND food_name=:food_name")
    void deleteFoodByID(String food_id, String food_name);

    @Query("DELETE FROM foods")
    void clearCart();

    @Query("SELECT * FROM foods")
    List<FoodEntity> getAllFoods();

    @Query("SELECT * FROM foods where qty> 0")
    List<FoodEntity> getAllFoodsCart();

    @Query("SELECT * FROM foods WHERE food_id = :foodId LIMIT 1")
    FoodEntity getFoodById(int foodId);




}