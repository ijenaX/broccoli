package com.flauschcode.broccoli.groceryList;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.flauschcode.broccoli.BR;
import com.flauschcode.broccoli.R;
import com.flauschcode.broccoli.RecyclerViewAdapter;
import com.flauschcode.broccoli.category.Category;
import com.flauschcode.broccoli.category.CategoryDialog;
import com.flauschcode.broccoli.category.CategoryViewModel;
import com.flauschcode.broccoli.recipe.Recipe;
import com.flauschcode.broccoli.recipe.ingredients.Ingredient;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class GroceryListFragment extends Fragment {

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_grocery_list, container, false);

        RecyclerView recyclerView = root.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL));

        List<Ingredient> testData = generateTestData();

        View emptyMessageTextView = root.findViewById(R.id.text_view);

        RecyclerViewAdapter<Ingredient> adapter = new RecyclerViewAdapter<Ingredient>() {
            @Override
            protected int getLayoutResourceId() {
                return R.layout.grocery_item;
            }

            @Override
            protected int getBindingVariableId() {
                return BR.ingredient;
            }

            @Override
            protected void onItemClick(Ingredient item) {
            }

            @Override
            protected void onAdapterDataChanged(int itemCount) {
            }
        };
        recyclerView.setAdapter(adapter);
        //GroceryListViewModel viewModel = new ViewModelProvider(this, viewModelFactory).get(GroceryListViewModel.class);
        adapter.submitList(generateTestData());

        return root;
    }

    public void onListInteraction(Category category) {
        CategoryDialog.newInstance(category).show(getParentFragmentManager(), "CategoryDialogFragment");
    }

    private List<Ingredient> generateTestData() {
        // Hier kannst du deine Testdaten erstellen
        List<Ingredient> testData = new ArrayList<>();
        testData.add(new Ingredient("Zutat 1", "100 g"));
        testData.add(new Ingredient("Zutat 2", "200 g"));
        testData.add(new Ingredient("Zutat 3", "300 g"));
        // Füge weitere Testdaten hinzu, wie benötigt
        return testData;
    }
}
