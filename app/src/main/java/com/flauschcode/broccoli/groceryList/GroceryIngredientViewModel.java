package com.flauschcode.broccoli.groceryList;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import javax.inject.Inject;

public class GroceryIngredientViewModel extends ViewModel {

    private final GroceryIngredientRepository groceryIngredientRepository;

    private final LiveData<List<GroceryIngredient>> groceryIngredients;

    @Inject
    public GroceryIngredientViewModel(GroceryIngredientRepository groceryIngredientRepository) {
        this.groceryIngredientRepository = groceryIngredientRepository;
        groceryIngredients = groceryIngredientRepository.findAll();
    }

    public LiveData<List<GroceryIngredient>> getGroceryIngredients() {
        return groceryIngredients;
    }

    public void insertOrUpdate(GroceryIngredient groceryIngredient) {
        groceryIngredientRepository.insertOrUpdate(groceryIngredient);
    }

    public void delete(GroceryIngredient groceryIngredient) {
        groceryIngredientRepository.delete(groceryIngredient);
    }
}