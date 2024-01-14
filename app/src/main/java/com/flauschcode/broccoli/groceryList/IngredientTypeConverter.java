package com.flauschcode.broccoli.groceryList;

import androidx.room.TypeConverter;

import com.flauschcode.broccoli.recipe.ingredients.Ingredient;
import com.flauschcode.broccoli.recipe.ingredients.IngredientBuilder;

public class IngredientTypeConverter {

    @TypeConverter
    public static Ingredient toIngredient(String ingredientString) {
        // Assuming the Ingredient class has a constructor that takes a string
        return IngredientBuilder.from(ingredientString).get(0);
    }

    @TypeConverter
    public static String fromIngredient(Ingredient ingredient) {
        return ingredient == null ? "" : ingredient.toString();
    }
}