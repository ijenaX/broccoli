package com.flauschcode.broccoli;

import android.annotation.SuppressLint;
import android.os.Debug;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashSet;

public abstract class SelectableRecyclerViewAdapter<T> extends ListAdapter<T, SelectableRecyclerViewAdapter<T>.Holder> {

    private HashSet<Integer> selectedItems = new HashSet<>();

    private OnSelectionStateChangeListener selectionStateChangeListener;

    protected SelectableRecyclerViewAdapter() {
        super(new DiffCallback<>());
        registerAdapterDataObserver(new ItemCountObserver());
    }

    @NonNull
    @Override
    public SelectableRecyclerViewAdapter<T>.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ViewDataBinding itemBinding = DataBindingUtil.inflate(layoutInflater, getLayoutResourceId(), parent, false);
        return new SelectableRecyclerViewAdapter<T>.Holder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull SelectableRecyclerViewAdapter.Holder holder, int position) {
        T currentItem = getItem(position);
        boolean isSelected = selectedItems.contains(position);
        holder.bind(currentItem, isSelected);

        // Set click listeners
        holder.itemView.setOnClickListener(v -> onItemClick(currentItem, position));
        holder.itemView.setOnLongClickListener(v -> {
            onItemLongClick(currentItem, position);
            return true;
        });
    }

    // Toggle selection state of the item
    public void toggleSelection(int position) {
        if (selectedItems.contains(position)) {
            selectedItems.remove(position);
        } else {
            selectedItems.add(position);
        }
        notifyItemChanged(position);

        if (selectionStateChangeListener != null) {
            selectionStateChangeListener.onSelectionStateChanged(!selectedItems.isEmpty());
        }
    }
    public HashSet<Integer> getSelectedItems() {
        return selectedItems;
    }

    protected abstract int getLayoutResourceId();
    protected abstract int getBindingVariableId();
    protected abstract void onItemClick(T item, int position);
    protected abstract void onItemLongClick(T item, int position);
    protected abstract void onAdapterDataChanged(int itemCount);

    public class Holder extends RecyclerView.ViewHolder {
        private final ViewDataBinding binding;
        private final CheckBox checkBoxSelected;

        Holder(ViewDataBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            this.checkBoxSelected = binding.getRoot().findViewById(R.id.checkBox_selected);
        }

        void bind(T obj, boolean isSelected) {
            binding.setVariable(getBindingVariableId(), obj);
            checkBoxSelected.setVisibility(isSelected ? View.VISIBLE : View.INVISIBLE);
            binding.executePendingBindings();
        }
    }

    public void setOnSelectionStateChangeListener(OnSelectionStateChangeListener listener) {
        this.selectionStateChangeListener = listener;
    }

    public static class DiffCallback<T> extends DiffUtil.ItemCallback<T> {
        @Override
        public boolean areItemsTheSame(@NonNull T oldItem, @NonNull T newItem) {
            return oldItem.hashCode() == newItem.hashCode();
        }

        @SuppressLint("DiffUtilEquals")
        @Override
        public boolean areContentsTheSame(@NonNull T oldItem, @NonNull T newItem) {
            return oldItem.equals(newItem);
        }
    }

    private class ItemCountObserver extends RecyclerView.AdapterDataObserver {
        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            super.onItemRangeChanged(positionStart, itemCount);
            notifyAdapterDataChanged();
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount, @Nullable Object payload) {
            super.onItemRangeChanged(positionStart, itemCount, payload);
            notifyAdapterDataChanged();
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            super.onItemRangeInserted(positionStart, itemCount);
            notifyAdapterDataChanged();
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            super.onItemRangeRemoved(positionStart, itemCount);
            notifyAdapterDataChanged();
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            super.onItemRangeMoved(fromPosition, toPosition, itemCount);
            notifyAdapterDataChanged();
        }

        private void notifyAdapterDataChanged() {
            SelectableRecyclerViewAdapter.this.onAdapterDataChanged(getItemCount());
        }
    }
}

