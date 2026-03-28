package com.example.deliverywala.database;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;


@Database(entities = {FoodEntity.class}, version = 1, exportSchema = false)
public abstract class FoodDatabase extends RoomDatabase {
    public abstract FoodDao FoodDao();

    private static volatile FoodDatabase INSTANCE;

    public static FoodDatabase getDbInstant(Context context) {
        if (INSTANCE == null) {
            synchronized (FoodDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            FoodDatabase.class,
                            "food-db"
                    ).allowMainThreadQueries()
                     .fallbackToDestructiveMigration()
                     .setJournalMode(JournalMode.TRUNCATE)
                     .build();
                }
            }
        }
        return INSTANCE;
    }

    static final Migration MIGRATION_1 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL(
                "ALTER TABLE MainTable" +
                " ADD COLUMN totalQuestions INTEGER NOT NULL DEFAULT 0 "
            );
        }
    };

    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE users ADD COLUMN age INTEGER");
        }
    };

    static final Migration MIGRATION_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL(
                "ALTER TABLE MainTable" +
                " ADD COLUMN totalQuestions INTEGER"
            );
        }
    };
}