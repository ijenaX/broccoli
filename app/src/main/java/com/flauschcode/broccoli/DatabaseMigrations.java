package com.flauschcode.broccoli;

import android.util.Log;

import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

public class DatabaseMigrations {
    public static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            Log.d("DatabaseMigration", "Starting migration from 1 to 2");
            try {
                database.execSQL("CREATE TABLE IF NOT EXISTS grocery_ingredients (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                        "ingredient TEXT, " + // Assuming IngredientTypeConverter converts to TEXT
                        "recipeId INTEGER, " +
                        "inCart INTEGER NOT NULL, " + // Representing boolean as INTEGER
                        "FOREIGN KEY(recipeId) REFERENCES recipes(recipeId))"); // Ensure 'recipes' is correct table name
            } catch (Exception e) {
                Log.e("DatabaseMigration", "Error during migration from 1 to 2", e);
                throw e; // Re-throw to ensure Room knows the migration failed
            }

        }
    };

    // Future migrations can be added here
}