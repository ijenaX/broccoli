package com.flauschcode.broccoli.groceryList;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.flauschcode.broccoli.recipe.CoreRecipe;
import com.flauschcode.broccoli.recipe.Recipe;
import com.flauschcode.broccoli.recipe.ingredients.Ingredient;

import java.io.Serializable;

@Entity(tableName = "grocery_ingredients",
        foreignKeys = {
                @ForeignKey(entity = CoreRecipe.class,
                        parentColumns = "recipeId",
                        childColumns = "recipeId")
        })
@TypeConverters(IngredientTypeConverter.class)
public class GroceryIngredient implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private long id;
    private Ingredient ingredient;
    private long recipeId;
    private boolean inCart;

    public GroceryIngredient(Ingredient ingredient, long recipeId, boolean inCart) {
        this.ingredient = ingredient;
        this.recipeId = recipeId;
        this.inCart = inCart;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    public void setIngredient(Ingredient ingredient) {
        this.ingredient = ingredient;
    }

    public long getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(long recipeId) {
        this.recipeId = recipeId;
    }

    public boolean isInCart() {
        return inCart;
    }

    public void setInCart(boolean inCart) {
        this.inCart = inCart;
    }
}
