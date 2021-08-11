package com.zero.support.common.widget.recycler.tools;


import com.zero.support.common.component.ResourceViewModel;
import com.zero.support.common.vo.Resource;
import com.zero.support.common.widget.recycler.Cell;
import com.zero.support.common.widget.recycler.CellAdapter;

import java.util.List;

public abstract class ListViewModel extends ResourceViewModel<List<Cell>> {
    private CellAdapter cellAdapter;

    public ListViewModel() {

    }
//
//    public void setupRecyclerView(CellAdapter adapter) {
//        adapter.setItems(getViewPage().getItems());
//    }

    @Override
    public Resource<List<Cell>> onCreateResource() {
        return super.onCreateResource();
    }

//    @Override
//    protected void onSubmit(Resource<List<Cell>> resource) {
//        super.onSubmit(resource);
//        getViewPage().postResource(resource);
//    }

//    public abstract ListViewPage getViewPage();
}
