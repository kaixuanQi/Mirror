package com.zero.support.common.widget.recycler.tools;

import androidx.recyclerview.widget.RecyclerView;

import com.zero.support.common.widget.recycler.CellAdapter;
import com.zero.support.common.widget.recycler.ItemViewBound;
import com.zero.support.common.widget.recycler.ItemViewHolder;

public abstract class ListCellViewBinder<T extends ListCell> extends ItemViewBound {

    public ListCellViewBinder(int layoutId) {
        super(layoutId);
    }



    @Override
    public void bindItem(ItemViewHolder holder) {
        super.bindItem(holder);
        ListCell cell = holder.getItem();
        RecyclerView recyclerView = onBindRecyclerView(holder);
        CellAdapter adapter = (CellAdapter) recyclerView.getAdapter();
        if (adapter == null) {
            adapter = new CellAdapter(cell.getItems());
            recyclerView.setAdapter(adapter);
        } else if (adapter.getItems() != cell.getItems()) {
            adapter.setItems(cell.getItems());
        }


    }

    public abstract RecyclerView onBindRecyclerView(ItemViewHolder holder);
}
