package ca.mcgill.science.ctf.fragments;

import java.util.Map;

import ca.allanwang.capsule.library.logging.CLog;
import ca.mcgill.science.ctf.R;
import ca.mcgill.science.ctf.api.PrinterInfo;
import ca.mcgill.science.ctf.api.TEPIDAPI;
import ca.mcgill.science.ctf.iitems.RoomInfoItem;
import retrofit2.Call;

public class DashboardFragment extends BaseFragment<RoomInfoItem, Map<String, PrinterInfo>> {

//    @BindView(R.id.dashboard_username)
//    TextView usernameView;
//    @BindView(R.id.dashboard_quota)
//    TextView quotaView;
//    @BindView(R.id.dashboard_last_print_job)
//    TextView lastJobView;
//    @BindView(R.id.dashboard_container)
//    LinearLayout parentLayout;

    @Override
    public int getTitleId() {
        return R.string.dashboard;
    }

    @Override
    protected Call<Map<String, PrinterInfo>> getAPICall(TEPIDAPI api) {
        return api.getPrinterInfo();
    }

    @Override
    protected boolean onResponseReceived(Object body) {
        Map<String, PrinterInfo> data = ((Map<String, PrinterInfo>) body);
//        CLog.e("MSP %s", data.keySet());
        mAdapter.add(RoomInfoItem.getItems(data.values()));
        return false;
    }
}
