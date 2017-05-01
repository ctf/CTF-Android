package ca.mcgill.science.ctf.fragments;

import android.support.annotation.NonNull;

import com.mikepenz.fastadapter.adapters.HeaderAdapter;

import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import ca.allanwang.capsule.library.event.CClickEvent;
import ca.allanwang.capsule.library.swiperecyclerview.SwipeRecyclerView;
import ca.allanwang.capsule.library.swiperecyclerview.interfaces.ISwipeRecycler;
import ca.mcgill.science.ctf.R;
import ca.mcgill.science.ctf.api.ITepid;
import ca.mcgill.science.ctf.api.PrintData;
import ca.mcgill.science.ctf.fragments.base.BasePrintJobFragment;
import ca.mcgill.science.ctf.iitems.UserHeaderItem;
import retrofit2.Call;

public class AccountJobFragment extends BasePrintJobFragment {

    private HeaderAdapter<UserHeaderItem> headerAdapter = new HeaderAdapter<>();

    @Override
    protected void configSRV(SwipeRecyclerView srv) {
        super.configSRV(srv);
        srv.setAdapter(headerAdapter.wrap(mAdapter), getNumColumns());
        UserHeaderItem.config(headerAdapter.getFastAdapter(), this);
    }

    @Override
    protected Call<List<PrintData>> getAPICall(ITepid api) {
        return api.getUserPrintJobs(getShortUser());
    }

    @Override
    public int getTitleId() {
        return R.string.user_print_jobs;
    }

    @Override
    protected void onResponseReceived(@NonNull List<PrintData> body, ISwipeRecycler.OnRefreshStatus onRefreshStatus) {
        super.onResponseReceived(body, onRefreshStatus);
        UserHeaderItem.inject(headerAdapter, this); //retrieve data again and add item
    }

    //TODO colors mismatch on silent refresh since header item is added
    @Override
    protected void onSilentResponseReceived(@NonNull List<PrintData> body) {
        super.onSilentResponseReceived(body);
        UserHeaderItem.injectSilently(headerAdapter, this); //retrieve data again and reset item
    }

    @Override
    public void onRefresh(ISwipeRecycler.OnRefreshStatus onRefreshStatus) {
        if (headerAdapter.getFastAdapter() != null) headerAdapter.clear();
        super.onRefresh(onRefreshStatus);
    }

    @Subscribe
    public void onCClick(CClickEvent event) {
        if (event.view.getId() == R.id.toolbar)
            mSRV.smoothScrollToPosition(0);
    }

}

