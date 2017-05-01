package ca.mcgill.science.ctf.views;

import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.afollestad.materialdialogs.internal.MDTintHelper;
import com.afollestad.materialdialogs.util.DialogUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import ca.allanwang.capsule.library.event.RefreshEvent;
import ca.allanwang.capsule.library.event.SnackbarEvent;
import ca.allanwang.capsule.library.logging.CLog;
import ca.mcgill.science.ctf.R;
import ca.mcgill.science.ctf.api.PrinterInfo;
import ca.mcgill.science.ctf.api.PrinterTicket;
import ca.mcgill.science.ctf.api.PrinterTicketSubmission;
import ca.mcgill.science.ctf.api.TepidApi;
import ca.mcgill.science.ctf.utils.Preferences;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Allan Wang on 01/05/2017.
 */

public class TicketView extends LinearLayout {

    @BindView(R.id.ticket_text)
    AppCompatEditText ticketText;
    @BindView(R.id.sender)
    AppCompatTextView sender;

    public TicketView(Context context) {
        super(context);
        init();
    }

    public TicketView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TicketView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public TicketView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.view_ticket, this);
        ButterKnife.bind(this);
    }

    public static TicketView create(Context context, @Nullable PrinterInfo info) {
        return new TicketView(context).bind(context, info);
    }

    public TicketView bind(Context context, @Nullable PrinterInfo info) {
        if (info == null) return this; //no data at all; precaution
        PrinterTicket ticket = info.getTicket();
        if (ticket != null) {
            ticketText.setText(ticket.getReason());
            sender.setText(String.format(Locale.CANADA, "\u2014 %s\n%s", ticket.getUser().getRealName(), ticket.getReportedDate()));
            sender.setVisibility(VISIBLE);
        } else {
            ticketText.clearFocus();
        }
        int accentRes = info.getUp() ? R.color.enabled_green : R.color.disabled_red;
        int accentColor = ContextCompat.getColor(context, accentRes);
        MDTintHelper.setTint(ticketText, accentColor);
        MaterialDialog.Builder builder = new MaterialDialog.Builder(context)
                .title(info.getName())
                .theme(new Preferences(context).isDarkMode() ? Theme.DARK : Theme.LIGHT)
                .titleColorRes(accentRes)
                .negativeText(R.string.close)
                .customView(this, false)
                .negativeColorAttr(R.attr.material_drawer_primary_text)
                .positiveText(info.getUp() ? R.string.disable : R.string.enable)
                .positiveColorAttr(R.attr.material_drawer_primary_text)
                .autoDismiss(false)
                .widgetColorRes(accentRes)
                .onNegative((dialog, which) -> dialog.dismiss())
                .onPositive((dialog, which) -> {
                    if (info.getUp()) { //disabling printer
                        if (!ticketText.getText().toString().isEmpty()) {
                            sendTicket(info, false);
                            dialog.dismiss();
                        } else {
                            ticketText.setError(context.getString(R.string.error_field_required));
                        }
                    } else {
                        sendTicket(info, true);
                        dialog.dismiss();
                    }
                });

        if (!info.getUp()) {
            builder.neutralColorAttr(R.attr.material_drawer_primary_text)
                    .neutralText(R.string.modify)
                    .onNeutral((dialog, which) -> {
                        if (!ticketText.getText().toString().isEmpty()) {
                            sendTicket(info, false);
                            dialog.dismiss();
                        } else {
                            ticketText.setError(context.getString(R.string.error_field_required));
                        }
                    });
        }
        builder.show();
        return this;
    }

    private void sendTicket(final PrinterInfo printer, final boolean isUp) {
        String ticket = ticketText.getText() == null ? null : ticketText.getText().toString();
        TepidApi.Companion.getInstanceDangerously().setPrinterStatus(printer.get_id(), new PrinterTicketSubmission(isUp, ticket)).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    snackbar(String.format("%s successfully marked %s.", printer.getName(), isUp ? "up" : "down"));
                    EventBus.getDefault().post(new RefreshEvent(R.string.dashboard, true));
                } else {
                    CLog.e("Unsuccessful printer ticket %s", response.message());
                    snackbar("Unsuccessful response");
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                CLog.e("Failed printer ticket %s", t.getMessage());
                snackbar("Ticket failed to send");
            }
        });

    }

    private void snackbar(String s) {
        EventBus.getDefault().post(new SnackbarEvent(s));
    }

}
