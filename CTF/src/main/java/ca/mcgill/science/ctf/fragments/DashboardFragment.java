package ca.mcgill.science.ctf.fragments;

import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import ca.allanwang.swiperecyclerview.library.interfaces.ISwipeRecycler;
import ca.mcgill.science.ctf.R;
import ca.mcgill.science.ctf.api.ITEPID;
import ca.mcgill.science.ctf.api.TEPIDAPI;
import ca.mcgill.science.ctf.iitems.RoomInfoItem;
import retrofit2.Response;

public class DashboardFragment extends BaseFragment<RoomInfoItem> {

    @BindView(R.id.dashboard_username)
    TextView usernameView;
    @BindView(R.id.dashboard_quota)
    TextView quotaView;
    @BindView(R.id.dashboard_last_print_job)
    TextView lastJobView;
    @BindView(R.id.dashboard_container)
    LinearLayout parentLayout;

    @Override
    public int getTitleId() {
        return R.string.dashboard;
    }

    @Override
    public void onRefresh(ISwipeRecycler.OnRefreshStatus onRefreshStatus, TEPIDAPI api) throws IOException {
        Response response = api.getPrinterInfo().execute();
        if (response.isSuccessful()) {
            List<ITEPID.PrinterInfo> data = ((ITEPID.PrinterInfoList) response.body()).getList();
            mAdapter.add(RoomInfoItem.getItems(data));
        } else onRefreshStatus.onFailure();
    }
}
