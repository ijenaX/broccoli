package com.flauschcode.broccoli.groceryList;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class GroceryIngredientRepository {

    private GroceryIngredientDAO groceryIngredientDAO;
    private LiveData<List<GroceryIngredient>> allGroceryIngredients;

    @Inject
    public GroceryIngredientRepository(Application application, GroceryIngredientDAO groceryIngredientDAO) {
        this.groceryIngredientDAO = groceryIngredientDAO;
        allGroceryIngredients = groceryIngredientDAO.findAll();
    }

    public LiveData<List<GroceryIngredient>> findAll() {
        return allGroceryIngredients;
    }

    public void insertOrUpdate(GroceryIngredient groceryIngredient) {
        CompletableFuture.runAsync(() -> {
            if (groceryIngredient.getId() == 0) {
                groceryIngredientDAO.insert(groceryIngredient);
            } else {
                groceryIngredientDAO.update(groceryIngredient);
            }
        });
    }
    public void delete(GroceryIngredient groceryIngredient) {
        CompletableFuture.runAsync(() -> groceryIngredientDAO.delete(groceryIngredient));
    }
}