package com.flauschcode.broccoli.groceryList;

import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
import com.flauschcode.broccoli.recipe.ingredients.Ingredient;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class GroceryListFragment extends Fragment implements OnSelectionStateChangeListener {

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

        View emptyMessageTextView = root.findViewById(R.id.text_view);

        SelectableRecyclerViewAdapter<Ingredient> adapter = new SelectableRecyclerViewAdapter<Ingredient>() {
            @Override
            protected int getLayoutResourceId() {
                return R.layout.grocery_item;
            }

            @Override
            protected int getBindingVariableId() {
                return BR.ingredient;
            }

            @Override
            protected void onItemClick(Ingredient item, int position) {
                Holder viewHolder = (Holder) recyclerView.findViewHolderForAdapterPosition(position);

                if (viewHolder != null) {
                    TextView quantityTextView = viewHolder.itemView.findViewById(R.id.ingredient_quantity);
                    TextView textTextView = viewHolder.itemView.findViewById(R.id.ingredient_quantity);

                    if (textTextView == null) { return; }

//                    if (item.isInCart()) {
//                        quantityTextView.setPaintFlags(~Paint.STRIKE_THRU_TEXT_FLAG);
//                        textTextView.setPaintFlags(~Paint.STRIKE_THRU_TEXT_FLAG);
//                        item.setInCart(false);
//                    } else {
//                        quantityTextView.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
//                        textTextView.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
//                        item.setInCart(true);
//                    }
                }
            }

            @Override
            protected void onItemLongClick(Ingredient item, int position) {
            }

            @Override
            protected void onAdapterDataChanged(int itemCount) {
                emptyMessageTextView.setVisibility(itemCount == 0? View.VISIBLE : View.GONE);
            }
        };
        adapter.setOnSelectionStateChangeListener(this);
        recyclerView.setAdapter(adapter);
        adapter.submitList(generateTestData());

        return root;
    }

    private List<Ingredient> generateTestData() {
        List<Ingredient> testData = new ArrayList<>();
        testData.add(new Ingredient("100 g", "Zutat 1"));
        testData.add(new Ingredient("200 g", "Zutat 2"));
        testData.add(new Ingredient("300 g", "Zutat 3"));
        return testData;
    }

    @Override
    public void onSelectionStateChanged(List<?> selectedItems) {

    }
}