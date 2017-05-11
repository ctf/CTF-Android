package ca.mcgill.science.ctf.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.webkit.WebView;

import org.greenrobot.eventbus.EventBus;

import ca.allanwang.capsule.library.event.CFabEvent;
import ca.allanwang.capsule.library.logging.CLog;
import ca.mcgill.science.ctf.R;
import ca.mcgill.science.ctf.api.TicketData;
import ca.mcgill.science.ctf.api.User;
import ca.mcgill.science.ctf.fragments.base.BaseWebViewFragment;

/**
 * Created by Allan Wang on 29/03/2017.
 * <p>
 * Submit a ticket request to IT
 * https://www.mcgill.ca/it/node/27784/need-help/#page-title
 */

public class TicketFragment extends BaseWebViewFragment {

    public static final String EXTRA_STUDENT_NAME = "student_name",
            EXTRA_STUDENT_ID = "student_id",
            EXTRA_STUDENT_EMAIL = "student_email",
            EXTRA_PRINTER = "ticket_printer",
            EXTRA_PROBLEM = "ticket_problem";

    public static TicketFragment getInstance(User user, String problem, @Nullable TicketData.PrinterId printer) {
        TicketFragment fragment = new TicketFragment();
        Bundle args = new Bundle();
        if (user != null) {
            args.putString(EXTRA_STUDENT_NAME, user.getRealName());
            if (user.getStudentId() != null)
                args.putString(EXTRA_STUDENT_ID, Integer.toString(user.getStudentId()));
            args.putString(EXTRA_STUDENT_EMAIL, user.getEmail());
        }
        args.putSerializable(EXTRA_PRINTER, printer);
        args.putString(EXTRA_PROBLEM, problem);
        fragment.setArguments(args);
        return fragment;
    }

    private TicketData buildTicket() {
        Bundle args = getArguments();
        if (args == null)
            throw new RuntimeException("Args not set in ticket fragment; use getInstance");
        TicketData data = new TicketData();
        data.setName(args.getString(EXTRA_STUDENT_NAME));
        data.setStudentNum(args.getString(EXTRA_STUDENT_ID, ""));
        data.setEmail(args.getString(EXTRA_STUDENT_EMAIL));
        data.setPrinter((TicketData.PrinterId) args.getSerializable(EXTRA_PRINTER));
        data.setProblem(args.getString(EXTRA_PROBLEM));
        return data;
    }

    private static final String IT_URL = "https://www.mcgill.ca/it/node/27784/need-help/#page-title";
    private boolean injectForm = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().post(new CFabEvent(false));
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getWebView().loadUrl(IT_URL);
    }

    @Override
    protected void onPageFinished(WebView view, String url) {
        CLog.e("URL LOADED %s", url);
        if (injectForm) return;
        javascript(buildTicket().getInjector());
        injectForm = true; //retrieve only once
    }

    @Override
    public int getTitleId() {
        return R.string.ticket;
    }


}
