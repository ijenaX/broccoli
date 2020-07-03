package org.flauschhaus.broccoli.recipe;

import org.flauschhaus.broccoli.category.CategoryRepository;
import org.flauschhaus.broccoli.recipe.images.RecipeImageService;
import org.flauschhaus.broccoli.recipe.sharing.ShareService;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

import static org.mockito.Mockito.mock;

@Module
public class MockRecipeModule extends RecipeModule {

    @Provides
    @Singleton
    RecipeImageService recipeImageService() {
        return mock(RecipeImageService.class);
    }

    @Provides
    @Singleton
    RecipeRepository recipeRepository() {
        return mock(RecipeRepository.class);
    }

    @Provides
    @Singleton
    CategoryRepository categoryRepository() { //TODO move to other mock module
        return mock(CategoryRepository.class);
    }

    @Provides
    @Singleton
    ShareService shareService() {
        return mock(ShareService.class);
    }
}
