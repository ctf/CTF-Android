package ca.mcgill.science.ctf.fragments.base;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import ca.allanwang.capsule.library.swiperecyclerview.adapters.AnimationAdapter;
import ca.allanwang.capsule.library.swiperecyclerview.interfaces.ISwipeRecycler;
import ca.mcgill.science.ctf.api.PrintData;
import ca.mcgill.science.ctf.iitems.PrintJobItem;

/*
 * Fragment containing list of print jobs (used for user, room, etc)
 */
public abstract class BasePrintJobFragment extends BaseFragment<PrintJobItem, List<PrintData>> {

    @Override
    protected void configAdapter(AnimationAdapter<PrintJobItem> adapter) {
        super.configAdapter(adapter);
        adapter.withItemEvent(new PrintJobItem.PrintJobClickEvent());
    }

    @Override
    protected void onResponseReceived(@NonNull List<PrintData> body, ISwipeRecycler.OnRefreshStatus onRefreshStatus) {
        List<PrintJobItem> items = new ArrayList<>();
        for (PrintData print : body)
            items.add(new PrintJobItem(print));
        mAdapter.add(items);
    }

    @Override
    protected void onSilentResponseReceived(@NonNull List<PrintData> body) {
        List<PrintJobItem> items = new ArrayList<>();
        for (PrintData print : body)
            items.add(new PrintJobItem(print));
        mAdapter.setNewList(items);
    }

}

