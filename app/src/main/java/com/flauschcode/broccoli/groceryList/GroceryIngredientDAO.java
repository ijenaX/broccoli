package com.flauschcode.broccoli.groceryList;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface GroceryIngredientDAO {

    @Query("SELECT * FROM grocery_ingredients")
    LiveData<List<GroceryIngredient>> findAll();

    @Insert
    void insert(GroceryIngredient groceryIngredient);

    @Update
    void update(GroceryIngredient groceryIngredient);

    @Delete
    void delete(GroceryIngredient groceryIngredient);
}
