package com.flauschcode.broccoli;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.flauschcode.broccoli.category.Category;
import com.flauschcode.broccoli.category.CategoryDAO;
import com.flauschcode.broccoli.groceryList.GroceryIngredient;
import com.flauschcode.broccoli.groceryList.GroceryIngredientDAO;
import com.flauschcode.broccoli.groceryList.IngredientTypeConverter;
import com.flauschcode.broccoli.recipe.CoreRecipe;
import com.flauschcode.broccoli.recipe.CoreRecipeFts;
import com.flauschcode.broccoli.recipe.RecipeCategoryAssociation;
import com.flauschcode.broccoli.recipe.RecipeDAO;

@Database(entities = {CoreRecipe.class, Category.class, RecipeCategoryAssociation.class, CoreRecipeFts.class, GroceryIngredient.class}, version = 2)
@TypeConverters({IngredientTypeConverter.class})
public abstract class BroccoliDatabase extends RoomDatabase {

    private static BroccoliDatabase broccoliDatabase;

    public abstract RecipeDAO getRecipeDAO();
    public abstract CategoryDAO getCategoryDAO();
    public abstract GroceryIngredientDAO getGroceryIngredientDAO();

    public static synchronized BroccoliDatabase get(Context context) {
        if (broccoliDatabase == null) {
            broccoliDatabase = Room.databaseBuilder(context.getApplicationContext(), BroccoliDatabase.class, "broccoli")
                                    .build();
        }
        return broccoliDatabase;
    }
}

/*
static final Migration MIGRATION_1_2 = new Migration(1, 2) {
    @Override
    public void migrate(SupportSQLiteDatabase database) {
        // SQL to create the new grocery_ingredients table (adjust according to your schema)
        database.execSQL("CREATE TABLE IF NOT EXISTS grocery_ingredients (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, ingredient TEXT, recipeId INTEGER, inCart INTEGER NOT NULL, FOREIGN KEY(recipeId) REFERENCES recipes(recipeId))");
    }
};
 */