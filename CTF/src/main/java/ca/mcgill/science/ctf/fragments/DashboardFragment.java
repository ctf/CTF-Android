package ca.mcgill.science.ctf.fragments;

import com.mikepenz.fastadapter.adapters.HeaderAdapter;

import java.util.Map;

import ca.allanwang.swiperecyclerview.library.SwipeRecyclerView;
import ca.allanwang.swiperecyclerview.library.interfaces.ISwipeRecycler;
import ca.mcgill.science.ctf.R;
import ca.mcgill.science.ctf.api.PrinterInfo;
import ca.mcgill.science.ctf.api.TEPIDAPI;
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
    protected Call<Map<String, PrinterInfo>> getAPICall(TEPIDAPI api) {
        return api.getPrinterInfo();
    }

    @Override
    protected void configSRV(SwipeRecyclerView srv) {
        super.configSRV(srv);
        mHeader = new HeaderAdapter<>();
        srv.setAdapter(mHeader.wrap(mAdapter));
    }

    @Override
    protected void onResponseReceived(Object body, final ISwipeRecycler.OnRefreshStatus onRefreshStatus) {
        Map<String, PrinterInfo> data = ((Map<String, PrinterInfo>) body);
        mAdapter.add(RoomInfoItem.getItems(data.values()));
    }
}
