package ca.mcgill.science.ctf.fragments;

import java.util.ArrayList;
import java.util.List;

import ca.allanwang.swiperecyclerview.library.interfaces.ISwipeRecycler;
import ca.mcgill.science.ctf.R;
import ca.mcgill.science.ctf.api.PrintData;
import ca.mcgill.science.ctf.api.TEPIDAPI;
import ca.mcgill.science.ctf.iitems.PairItem;
import ca.mcgill.science.ctf.tepid.PrintJob;
import retrofit2.Call;

public class MyAccountFragment extends BaseFragment<PairItem, List<PrintData>> {

//    @BindView(R.id.my_account_quota)
//    TextView quotaView;
//    @BindView(R.id.my_account_username)
//    TextView usernameView;
//    @BindView(R.id.nick_field)
//    EditText nickView;
//    @BindView(R.id.change_nick)
//    Button changeNickView;
//    @BindView(R.id.my_account_color)
//    AppCompatCheckBox turnColor;
    private boolean rColor;
    private String rNickname, rQuota;
    private PrintJob[] rPrintJobs;

    @Override
    public int getTitleId() {
        return R.string.userinfo;
    }

    @Override
    protected Call<List<PrintData>> getAPICall(TEPIDAPI api) {
        return api.getUserPrintJobs(getShortUser());
    }

    @Override
    protected void onResponseReceived(Object body, ISwipeRecycler.OnRefreshStatus onRefreshStatus) {
        List<PrintData> data = ((List<PrintData>) body);
        List<PairItem> items = new ArrayList<>();
        for (PrintData print : data)
            items.add(print.getPairData());
        mAdapter.add(items);
    }
}

