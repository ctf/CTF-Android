package ca.mcgill.science.ctf.fragments;

import java.util.List;

import ca.mcgill.science.ctf.R;
import ca.mcgill.science.ctf.api.ITEPID;
import ca.mcgill.science.ctf.api.PrintData;
import retrofit2.Call;

public class AccountFragment extends BasePrintJobFragment {

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
    protected Call<List<PrintData>> getAPICall(ITEPID api) {
        return api.getUserPrintJobs(getShortUser());
    }

    @Override
    public int getTitleId() {
        return R.string.userinfo;
    }

}

