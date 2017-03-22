package ca.mcgill.science.ctf.fragments;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import ca.allanwang.swiperecyclerview.library.interfaces.ISwipeRecycler;
import ca.allanwang.swiperecyclerview.library.items.PairItem;
import ca.mcgill.science.ctf.R;
import ca.mcgill.science.ctf.api.ITEPID;
import ca.mcgill.science.ctf.api.PrintData;
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

    @Override
    public int getTitleId() {
        return R.string.userinfo;
    }

    @Override
    protected Call<List<PrintData>> getAPICall(ITEPID api) {
        return api.getUserPrintJobs(getShortUser());
    }

    @Override
    protected void onResponseReceived(@NonNull List<PrintData> body, ISwipeRecycler.OnRefreshStatus onRefreshStatus) {
        List<PairItem> items = new ArrayList<>();
        for (PrintData print : body)
            items.add(print.getPairData());
        mAdapter.add(items);
    }

}

