package ca.mcgill.science.ctf.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import ca.allanwang.capsule.library.event.CFabEvent;
import ca.allanwang.capsule.library.event.SnackbarEvent;
import ca.allanwang.capsule.library.interfaces.CFragmentCore;
import ca.mcgill.science.ctf.MainActivity;
import ca.mcgill.science.ctf.R;
import ca.mcgill.science.ctf.api.TicketData;
import ca.mcgill.science.ctf.api.User;
import ca.mcgill.science.ctf.auth.AccountUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Allan Wang on 29/03/2017.
 */

public class PreTicketFragment extends Fragment implements CFragmentCore {

    private Unbinder unbinder;
    private User user;
    private TicketData.PrinterId printer;
    @BindView(R.id.btn_printer)
    AppCompatButton printerButton;
    @BindView(R.id.btn_ticket)
    AppCompatButton ticketButton;
    @BindView(R.id.problem_text)
    AutoCompleteTextView problemText;
    private CharSequence[] printerOptions;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BaseFragment.getAPI(this).getUser(AccountUtil.getShortUser()).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                user = response.body();
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                EventBus.getDefault().post(new SnackbarEvent("Failed to get user data"));
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_ticket, container, false);
        bindButterKnife(v);
        printerButton.setOnClickListener(btn -> {
            new MaterialDialog.Builder(getContext())
                    .items(getPrinterOptions())
                    .itemsCallbackSingleChoice(getPrinterChoiceIndex(), (dialog, itemView, which, text) -> {
                        if (which == 0) printer = null;
                        else printer = TicketData.PrinterId.values()[which - 1];
                        //update text
                        printerButton.setText(text);
                        return true;
                    })
                    .show();
        });
        ticketButton.setOnClickListener(btn -> {
            new MaterialDialog.Builder(getContext())
                    .items(R.array.ticket_preset_names)
                    .itemsCallbackSingleChoice(-1, (dialog, itemView, which, text) -> {
                        problemText.setText(getResources().getStringArray(R.array.ticket_preset_full)[which]);
                        return true;
                    })
                    .show();
        });
        return v;
    }

    //generates list choices for the printer options; do not hardcode as this is needed in many places
    private CharSequence[] getPrinterOptions() {
        if (printerOptions != null) return printerOptions;
        TicketData.PrinterId[] printers = TicketData.PrinterId.values();
        printerOptions = new CharSequence[printers.length + 1];
        printerOptions[0] = getString(R.string.bracket_none);
        for (int i = 0; i < printers.length; i++)
            printerOptions[i + 1] = printers[i].getName();
        return printerOptions;
    }

    //list contains [none] followed by list of ordinals
    private int getPrinterChoiceIndex() {
        if (printer == null) return 0;
        return printer.ordinal() + 1;
    }

    protected void bindButterKnife(View view) {
        unbinder = ButterKnife.bind(this, view);
    }

    @Override
    @CallSuper
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null) unbinder.unbind();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        EventBus.getDefault().post(new CFabEvent(GoogleMaterial.Icon.gmd_send, v -> {
            ((MainActivity) getContext()).switchFragment(TicketFragment.getInstance(user, problemText.getText().toString(), printer));
        }));
    }

    @Override
    public int getTitleId() {
        return R.string.ticket;
    }
}
