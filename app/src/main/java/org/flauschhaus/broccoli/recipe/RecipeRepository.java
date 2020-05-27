package org.flauschhaus.broccoli.recipe;

import androidx.lifecycle.LiveData;

import org.flauschhaus.broccoli.recipe.images.RecipeImageService;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class RecipeRepository {

    private RecipeDAO recipeDAO;
    private RecipeImageService recipeImageService;

    private LiveData<List<Recipe>> allRecipes;

    public enum InsertionType {
        INSERT, UPDATE
    }

    @Inject
    RecipeRepository(RecipeDAO recipeDAO, RecipeImageService recipeImageService) {
        this.recipeDAO = recipeDAO;
        this.recipeImageService = recipeImageService;
        allRecipes = recipeDAO.findAll();
    }

    public LiveData<List<Recipe>> findAll() {
        return allRecipes;
    }

    public CompletableFuture<InsertionType> insertOrUpdate(Recipe recipe) {
        return CompletableFuture.supplyAsync(() -> {
            if (recipe.getId() == 0) {
                recipeDAO.insert(recipe);
                return InsertionType.INSERT;
            } else {
                recipeDAO.update(recipe);
                return InsertionType.UPDATE;
            }
        });
    }

    public CompletableFuture<Void> delete(Recipe recipe) {
        return CompletableFuture.allOf(
                recipeImageService.deleteImage(recipe.getImageName()),
                CompletableFuture.runAsync(() -> recipeDAO.delete(recipe))
        );
    }

}