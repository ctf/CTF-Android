package ca.mcgill.science.ctf.fragments;

import android.support.annotation.NonNull;

import com.mikepenz.fastadapter.adapters.HeaderAdapter;

import java.util.Map;

import ca.allanwang.capsule.library.swiperecyclerview.SwipeRecyclerView;
import ca.allanwang.capsule.library.swiperecyclerview.adapters.AnimationAdapter;
import ca.allanwang.capsule.library.swiperecyclerview.interfaces.ISwipeRecycler;
import ca.mcgill.science.ctf.R;
import ca.mcgill.science.ctf.api.ITEPID;
import ca.mcgill.science.ctf.api.PrinterInfo;
import ca.mcgill.science.ctf.fragments.base.BaseFragment;
import ca.mcgill.science.ctf.iitems.DashboardHeaderItem;
import ca.mcgill.science.ctf.iitems.RoomInfoItem;
import retrofit2.Call;

public class DashboardFragment extends BaseFragment<RoomInfoItem, Map<String, PrinterInfo>> {

    private HeaderAdapter<DashboardHeaderItem> mHeader;

    @Override
    public int getTitleId() {
        return R.string.dashboard;
    }

    @Override
    protected void configAdapter(AnimationAdapter<RoomInfoItem> adapter) {
        super.configAdapter(adapter);
        //TODO only show dialog if user is a CTF member
        adapter.withItemEvent(new RoomInfoItem.PrinterClickEvent());
    }

    @Override
    protected Call<Map<String, PrinterInfo>> getAPICall(ITEPID api) {
        return api.getPrinterInfo();
    }

    @Override
    protected void configSRV(SwipeRecyclerView srv) {
        super.configSRV(srv);
        mHeader = new HeaderAdapter<>();
        srv.setAdapter(mHeader.wrap(mAdapter));
    }

    @Override
    protected void onResponseReceived(@NonNull Map<String, PrinterInfo> body, final ISwipeRecycler.OnRefreshStatus onRefreshStatus) {
        mAdapter.add(RoomInfoItem.getItems(body.values()));
    }

    @Override
    protected void onSilentResponseReceived(@NonNull Map<String, PrinterInfo> body) {
        mAdapter.setNewList(RoomInfoItem.getItems(body.values()));
    }
}
