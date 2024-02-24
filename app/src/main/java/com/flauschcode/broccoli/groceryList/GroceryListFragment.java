package com.flauschcode.broccoli.groceryList;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.flauschcode.broccoli.BR;
import com.flauschcode.broccoli.OnSelectionStateChangeListener;
import com.flauschcode.broccoli.R;
import com.flauschcode.broccoli.SelectableRecyclerViewAdapter;
import com.flauschcode.broccoli.recipe.Recipe;
import com.flauschcode.broccoli.recipe.details.RecipeDetailsActivity;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;

public class GroceryListFragment extends Fragment implements OnSelectionStateChangeListener {

    @Inject
    ViewModelProvider.Factory viewModelFactory;
    private List<Recipe> selectedGroceries = new ArrayList<>();

    //GroceryIngredientViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        AndroidSupportInjection.inject(this);

        View root = inflater.inflate(R.layout.fragment_grocery_list, container, false);

        RecyclerView recyclerView = root.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL));

        View emptyMessageTextView = root.findViewById(R.id.text_view);

        SelectableRecyclerViewAdapter<GroceryIngredient> adapter = new SelectableRecyclerViewAdapter<GroceryIngredient>() {
            @Override
            protected int getLayoutResourceId() {
                return R.layout.grocery_item;
            }

            @Override
            protected int getBindingVariableId() {
                return BR.groceryIngredient;
            }

            @Override
            protected void onItemClick(GroceryIngredient item, int position) {
                if (selectedGroceries.isEmpty()) {
                    toggleStrikeThrough(item, position, recyclerView);
                } else {
                    toggleSelection(position);
                }
            }


            @Override
            protected void onItemLongClick(GroceryIngredient item, int position) {
                toggleSelection(position);
            }

            @Override
            protected void onAdapterDataChanged(int itemCount) {
                emptyMessageTextView.setVisibility(itemCount == 0? View.VISIBLE : View.GONE);
            }
        };
        adapter.setOnSelectionStateChangeListener(this);
        recyclerView.setAdapter(adapter);

        GroceryIngredientViewModel viewModel = new ViewModelProvider(this, viewModelFactory).get(GroceryIngredientViewModel.class);
        viewModel.getGroceryIngredients().observe(getViewLifecycleOwner(), adapter::submitList);

        Toolbar toolbar = root.findViewById(R.id.toolbar_groceries);
        setUpMenu(toolbar);

        return root;
    }

    private void setUpMenu(Toolbar toolbar) {
        toolbar.inflateMenu(R.menu.groceries);
        MenuItem deleteRecipes = toolbar.getMenu().findItem(R.id.action_delete);

        // Update kebab menu visibility based on multi-select mode
        deleteRecipes.setVisible(!selectedGroceries.isEmpty());

        // Set onClick Listener for "Delete"
        deleteRecipes.setOnMenuItemClickListener(item -> {
            Toast.makeText(getContext(), "Selected recipes: " + selectedGroceries.size(), Toast.LENGTH_LONG).show();
            return true;
        });
    }

    private void toggleStrikeThrough(GroceryIngredient item, int position, RecyclerView recyclerView) {
        SelectableRecyclerViewAdapter.Holder viewHolder = (SelectableRecyclerViewAdapter.Holder) recyclerView.findViewHolderForAdapterPosition(position);

        if (viewHolder != null) {
            TextView quantity = viewHolder.itemView.findViewById(R.id.ingredient_quantity);
            TextView ingredient = viewHolder.itemView.findViewById(R.id.ingredient_text);

            if (ingredient == null) { return; }

            int greyColor = ContextCompat.getColor(viewHolder.itemView.getContext(), R.color.gray_600);
            int normalColor = ContextCompat.getColor(viewHolder.itemView.getContext(), R.color.text_black);

            if (item.isInCart()) {
                quantity.setPaintFlags(quantity.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                ingredient.setPaintFlags(ingredient.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                quantity.setTextColor(normalColor);
                ingredient.setTextColor(normalColor);
                item.setInCart(false);
            } else {
                quantity.setPaintFlags(quantity.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                ingredient.setPaintFlags(ingredient.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                quantity.setTextColor(greyColor);
                ingredient.setTextColor(greyColor);
                item.setInCart(true);
            }
        }
    }

    @Override
    public void onSelectionStateChanged(List<?> selectedItems) {
        this.selectedGroceries = (List<Recipe>) selectedItems; // Assuming selectedRecipes is a member variable
        Toolbar toolbar = requireActivity().findViewById(R.id.toolbar_groceries);
        if (toolbar != null) {
            MenuItem deleteGroceries = toolbar.getMenu().findItem(R.id.action_delete);
            boolean isMultiSelectMode = !selectedItems.isEmpty();
            deleteGroceries.setVisible(isMultiSelectMode);
        }
    }
}