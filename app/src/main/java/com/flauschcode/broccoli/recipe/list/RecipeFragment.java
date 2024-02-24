package com.flauschcode.broccoli.recipe.list;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.flauschcode.broccoli.BR;
import com.flauschcode.broccoli.OnSelectionStateChangeListener;
import com.flauschcode.broccoli.R;
import com.flauschcode.broccoli.SelectableRecyclerViewAdapter;
import com.flauschcode.broccoli.category.Category;
import com.flauschcode.broccoli.groceryList.GroceryIngredient;
import com.flauschcode.broccoli.groceryList.GroceryIngredientRepository;
import com.flauschcode.broccoli.recipe.Recipe;
import com.flauschcode.broccoli.recipe.crud.CreateAndEditRecipeActivity;
import com.flauschcode.broccoli.recipe.details.RecipeDetailsActivity;
import com.flauschcode.broccoli.recipe.ingredients.Ingredient;
import com.flauschcode.broccoli.recipe.ingredients.IngredientBuilder;
import com.flauschcode.broccoli.seasons.SeasonalFood;
import com.google.android.material.chip.Chip;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;

public class RecipeFragment extends Fragment implements OnSelectionStateChangeListener, AdapterView.OnItemSelectedListener, SearchView.OnQueryTextListener {

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    @Inject
    GroceryIngredientRepository groceryIngredientRepository;
    private RecipeViewModel viewModel;

    private MenuItem searchItem;
    private SearchView searchView;
    private Spinner spinner;
    private Chip seasonalIngredientChip;
    private List<Recipe> selectedRecipes = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        AndroidSupportInjection.inject(this);

        View root = inflater.inflate(R.layout.fragment_recipes, container, false);

        RecyclerView recyclerView = root.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);

        View emptyMessageLayout = root.findViewById(R.id.recipes_empty);
        SelectableRecyclerViewAdapter<Recipe> adapter = new SelectableRecyclerViewAdapter<Recipe>() {
            @Override
            protected int getLayoutResourceId() {
                return R.layout.recipe_item;
            }

            @Override
            protected int getBindingVariableId() {
                return BR.recipe;
            }

            @Override
            protected void onItemClick(Recipe item, int position) {
                if (selectedRecipes.isEmpty()) {
                    onListInteraction(item);
                } else {
                    toggleSelection(position);
                }
            }

            @Override
            protected void onItemLongClick(Recipe item, int position) {
                toggleSelection(position);
                //Toast.makeText(getContext(), "Long clicked: " + item.getTitle().substring(0, 10) + " position: " + position, Toast.LENGTH_SHORT).show();
                //Toast.makeText(getContext(), "Multiselect mode: " + RecipeFragment.this.isMultiselectMode, Toast.LENGTH_SHORT).show();
            }

            @Override
            protected void onAdapterDataChanged(int itemCount) {
                emptyMessageLayout.setVisibility(itemCount == 0? View.VISIBLE : View.GONE);
            }
        };
        adapter.setOnSelectionStateChangeListener(this);
        recyclerView.setAdapter(adapter);

        FloatingActionButton fab = root.findViewById(R.id.fab_recipes);
        setUpFloatingActionButton(fab);

        viewModel = new ViewModelProvider(this, viewModelFactory).get(RecipeViewModel.class);
        viewModel.getRecipes().observe(getViewLifecycleOwner(), adapter::submitList);

        Toolbar toolbar = root.findViewById(R.id.toolbar_recipes);
        setUpMenu(toolbar);

        spinner = root.findViewById(R.id.spinner);
        setUpSpinner();

        seasonalIngredientChip = root.findViewById(R.id.chip);
        getSeasonalFoodArgument().ifPresent(seasonalFood -> {
            resetCategory();

            seasonalIngredientChip.setText(seasonalFood.getName());
            seasonalIngredientChip.setOnClickListener(view -> {
                NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
                navController.popBackStack(R.id.nav_seasons, true);
                resetCategoryAndArguments();
                seasonalIngredientChip.setVisibility(View.GONE);
                spinner.setVisibility(View.VISIBLE);
            });

            spinner.post(() -> {
                viewModel.setSeasonalTerms(seasonalFood.getTerms());
                viewModel.setFilterName(seasonalFood.getName());
            });
        });

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (!searchView.isIconified()) {
                    toolbar.collapseActionView();
                } else {
                    if (isEnabled()) {
                        setEnabled(false);
                        requireActivity().onBackPressed();
                    }
                }

            }
        });

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (getSeasonalFoodArgument().isPresent()) {
            safeSetVisibility(seasonalIngredientChip, View.VISIBLE);
            safeSetVisibility(spinner, View.GONE);
        } else {
            safeSetVisibility(seasonalIngredientChip, View.GONE);
            safeSetVisibility(spinner, View.VISIBLE);
        }
    }

    private void safeSetVisibility(View view, int visibility) {
        if (view != null) {
            view.setVisibility(visibility);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Category category = (Category) parent.getItemAtPosition(position);
        viewModel.setFilterCategory(category);
        viewModel.setFilterName(category.getName());
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // intentionally empty
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        viewModel.setSearchTerm(newText);
        return false;
    }

    @Override
    public void onSelectionStateChanged(List<?> selectedItems) {
        this.selectedRecipes = (List<Recipe>) selectedItems; // Assuming selectedRecipes is a member variable
        Toolbar toolbar = requireActivity().findViewById(R.id.toolbar_recipes);
        if (toolbar != null) {
            MenuItem addToGroceryList = toolbar.getMenu().findItem(R.id.action_add_to_grocery_list);
            MenuItem deleteRecipes = toolbar.getMenu().findItem(R.id.action_delete);
            boolean isMultiSelectMode = !selectedItems.isEmpty();
            addToGroceryList.setVisible(isMultiSelectMode);
            deleteRecipes.setVisible(isMultiSelectMode);
        }
    }

    ActivityResultLauncher<Intent> detailsResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData().hasExtra("hashtag")) {
                    resetCategoryAndArguments();
                    searchItem.expandActionView();
                    searchView.post(() -> searchView.setQuery(result.getData().getStringExtra("hashtag"), false));
                } else if (result.getResultCode() == Activity.RESULT_OK && result.getData().getBooleanExtra("navigateToSupportPage", false)) {
                    NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
                    navController.navigate(R.id.nav_support);
                }
            });

    private void onListInteraction(Recipe recipe) {
        Intent intent = new Intent(getContext(), RecipeDetailsActivity.class);
        intent.putExtra(Recipe.class.getName(), recipe);
        detailsResultLauncher.launch(intent);
    }

    private void resetCategory() {
        spinner.setSelection(0);
        Category categoryAll = viewModel.getCategoryAll();
        viewModel.setFilterCategory(categoryAll);
        viewModel.setFilterName(categoryAll.getName());
    }

    private void resetCategoryAndArguments() {
        resetCategory();
        if (getArguments() != null) {
            getArguments().clear();
        }
    }

    private void setUpFloatingActionButton(FloatingActionButton fab) {
        ActivityResultLauncher<Intent> crudResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        resetCategoryAndArguments();
                        Recipe recipe = (Recipe) result.getData().getSerializableExtra(Recipe.class.getName());
                        onListInteraction(recipe);
                    }
                });
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), CreateAndEditRecipeActivity.class);
            crudResultLauncher.launch(intent);
        });

    }

    private void setUpMenu(Toolbar toolbar) {
        toolbar.inflateMenu(R.menu.recipes);
        searchItem = toolbar.getMenu().findItem(R.id.action_search);
        MenuItem addToGroceryList = toolbar.getMenu().findItem(R.id.action_add_to_grocery_list);
        MenuItem deleteRecipes = toolbar.getMenu().findItem(R.id.action_delete);


        // Set up search view
        searchView = new SearchView(toolbar.getContext());
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setMinimumHeight(requireContext().getResources().getDimensionPixelSize(R.dimen.min_height_for_accessibility));
        searchItem.setActionView(searchView);
        viewModel.getFilterName().observe(getViewLifecycleOwner(), filterName -> searchView.setQueryHint(getString(R.string.search_in, filterName.toUpperCase())));
        searchView.setOnQueryTextListener(this);

        // Update kebab menu visibility based on multi-select mode
        addToGroceryList.setVisible(!selectedRecipes.isEmpty());
        deleteRecipes.setVisible(!selectedRecipes.isEmpty());

        // Set onClick Listener for "Add to grocery list"
        addToGroceryList.setOnMenuItemClickListener(item -> {
            List<GroceryIngredient> groceryIngredients = new ArrayList<>();
            for (Recipe recipe : selectedRecipes) {
                // Assuming Recipe has a method getIngredients() that returns a list of Ingredient objects
                List<Ingredient> ingredients = IngredientBuilder.from(recipe.getIngredients());
                for (Ingredient ingredient : ingredients) {
                    groceryIngredients.add(new GroceryIngredient(ingredient, recipe.getRecipeId(), false));
                }
            }

            // Store in database
            groceryIngredients.forEach(groceryIngredient ->
                    groceryIngredientRepository.insertOrUpdate(groceryIngredient)
            );

            // groceryIngredientRepository.findAll().observe(getViewLifecycleOwner(), new Observer<List<GroceryIngredient>>() {
            //    @Override
            //    public void onChanged(List<GroceryIngredient> groceryIngredients) {
            //        for(GroceryIngredient groceryIngredient: groceryIngredients){
            //            groceryIngredientRepository.delete(groceryIngredient);
            //        }
            //    }
            // });

            Toast.makeText(getContext(), "Added to grocery list: " + groceryIngredients.size(), Toast.LENGTH_LONG).show();
            return true;
        });

        // Set onClick Listener for "Delete"
        deleteRecipes.setOnMenuItemClickListener(item -> {
            Toast.makeText(getContext(), "Selected recipes: " + selectedRecipes.size(), Toast.LENGTH_LONG).show();
            return true;
        });
    }

    private void setUpSpinner() {
        ArrayAdapter<Category> arrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item);
        arrayAdapter.add(viewModel.getCategoryAll());
        arrayAdapter.add(viewModel.getCategorySeasonal());
        arrayAdapter.add(viewModel.getCategoryUnassigned());
        arrayAdapter.add(viewModel.getCategoryFavorites());
        viewModel.getCategories().observe(getViewLifecycleOwner(), categories -> categories.forEach(arrayAdapter::add));
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(this);

        Category preferredCategory = getPreferredCategory();
        int position = arrayAdapter.getPosition(preferredCategory);
        spinner.setSelection(position, false);
        viewModel.setFilterName(preferredCategory.getName());
    }

    private Category getPreferredCategory() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireActivity());
        String preferredCategoryId = sharedPreferences.getString("preferred-category", "-1");
        switch (preferredCategoryId)  {
            case "-2":
                return viewModel.getCategoryFavorites();
            case "-4":
                return viewModel.getCategorySeasonal();
            default:
                return viewModel.getCategoryAll();
        }
    }

    private Optional<SeasonalFood> getSeasonalFoodArgument() {
        return Optional.ofNullable(RecipeFragmentArgs.fromBundle(getArguments()).getSeasonalFood());
    }

}