package com.flauschcode.broccoli;

import android.content.Context;
import android.util.Log;

import androidx.room.AutoMigration;
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

@Database(
        version = 2,
        entities = {CoreRecipe.class, Category.class, RecipeCategoryAssociation.class, CoreRecipeFts.class, GroceryIngredient.class}
)
@TypeConverters({IngredientTypeConverter.class})
public abstract class BroccoliDatabase extends RoomDatabase {

    private static BroccoliDatabase broccoliDatabase;

    public abstract RecipeDAO getRecipeDAO();
    public abstract CategoryDAO getCategoryDAO();
    public abstract GroceryIngredientDAO getGroceryIngredientDAO();

    public static synchronized BroccoliDatabase get(Context context) {
        if (broccoliDatabase == null) {
            broccoliDatabase = Room.databaseBuilder(context.getApplicationContext(), BroccoliDatabase.class, "broccoli")
                    //.addMigrations(DatabaseMigrations.MIGRATION_1_2) this doesn't work here for some reason and needs to be added in DatabaseModule
                    .build();
        }
        return broccoliDatabase;
    }
}