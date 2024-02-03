package com.flauschcode.broccoli.groceryList;

import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.flauschcode.broccoli.BR;
import com.flauschcode.broccoli.OnSelectionStateChangeListener;
import com.flauschcode.broccoli.R;
import com.flauschcode.broccoli.SelectableRecyclerViewAdapter;

import java.util.List;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;

public class GroceryListFragment extends Fragment implements OnSelectionStateChangeListener {

    @Inject
    ViewModelProvider.Factory viewModelFactory;

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
                return BR.ingredient;
            }

            @Override
            protected void onItemClick(GroceryIngredient item, int position) {
                Holder viewHolder = (Holder) recyclerView.findViewHolderForAdapterPosition(position);

                if (viewHolder != null) {
                    TextView quantityTextView = viewHolder.itemView.findViewById(R.id.ingredient_quantity);
                    TextView textTextView = viewHolder.itemView.findViewById(R.id.ingredient_quantity);

                    if (textTextView == null) { return; }

                    if (item.isInCart()) {
                        quantityTextView.setPaintFlags(~Paint.STRIKE_THRU_TEXT_FLAG);
                        textTextView.setPaintFlags(~Paint.STRIKE_THRU_TEXT_FLAG);
                        item.setInCart(false);
                    } else {
                        quantityTextView.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                        textTextView.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                        item.setInCart(true);
                    }
                }
            }

            @Override
            protected void onItemLongClick(GroceryIngredient item, int position) {
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

        return root;
    }

    @Override
    public void onSelectionStateChanged(List<?> selectedItems) {

    }
}