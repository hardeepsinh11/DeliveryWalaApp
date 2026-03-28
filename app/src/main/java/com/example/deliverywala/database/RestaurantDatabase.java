package com.example.deliverywala.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {RestaurantEntity.class}, version = 1,exportSchema = false)
public abstract class RestaurantDatabase extends RoomDatabase {
    public abstract RestaurantDao RestaurantDao();
}