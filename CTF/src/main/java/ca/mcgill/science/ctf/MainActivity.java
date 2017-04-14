package ca.mcgill.science.ctf;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.core.CrashlyticsCore;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.typeface.IIcon;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;

import ca.allanwang.capsule.library.changelog.ChangelogDialog;
import ca.allanwang.capsule.library.interfaces.CDrawerItem;
import ca.allanwang.capsule.library.item.DrawerItem;
import ca.allanwang.capsule.library.logging.CLog;
import ca.allanwang.capsule.library.logging.CallbackLogTree;
import ca.allanwang.capsule.library.permissions.CPermissionCallback;
import ca.mcgill.science.ctf.activities.SearchActivity;
import ca.mcgill.science.ctf.api.TEPIDAPI;
import ca.mcgill.science.ctf.auth.AccountUtil;
import ca.mcgill.science.ctf.fragments.AccountFragment;
import ca.mcgill.science.ctf.fragments.BaseFragment;
import ca.mcgill.science.ctf.fragments.DashboardFragment;
import ca.mcgill.science.ctf.fragments.PreTicketFragment;
import ca.mcgill.science.ctf.fragments.RoomsViewPagerFragment;
import ca.mcgill.science.ctf.fragments.SettingsFragment;
import ca.mcgill.science.ctf.utils.Preferences;
import ca.mcgill.science.ctf.utils.Utils;
import io.fabric.sdk.android.Fabric;
import timber.log.Timber;

public class MainActivity extends SearchActivity {

    private String mToken;

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        if (!isDebug()) {
            disableCLog(); //Make things cleaner; don't log unless specified
            //Add crashlytics
            CrashlyticsCore core = new CrashlyticsCore.Builder().build();
            Fabric.with(this, new Crashlytics.Builder().core(core).build(), new Answers(), new Crashlytics());
            Timber.plant(new CallbackLogTree((priority, tag, message, t) -> {
                if (priority == Log.ERROR)
                    if (t == null) Crashlytics.logException(new Exception(message));
                    else Crashlytics.logException(t);
                else Crashlytics.log(priority, tag, message);
            }));
        }
//        super.onCreate(savedInstanceState);
        Preferences prefs = new Preferences(this);
        if (prefs.isDarkMode()) setTheme(R.style.AppTheme_Dark_NoActionBar);

        preCapsuleOnCreate(savedInstanceState);
//        checkAccountPermission(new CPermissionCallback() {
//            @Override
//            public void onResult(PermissionResult result) {
//                if (result.isAllGranted()) {
        AccountUtil.initAccount(MainActivity.this);
//                } else {
//                    //Stop app?
//                }
//            }
//        });
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String lang = preferences.getString("pref_language", "en");
        SettingsFragment.setLocale(this, lang); //todo put setlocale somewhere else? CTFApp maybe?

        if (AccountUtil.isSignedIn()) {
            AccountUtil.requestToken(this, new AccountUtil.TokenRequestCallback() {
                @Override
                public void onReceived(@NonNull String token) {
                    mToken = token;
                    //initialize TEPID API
                    TEPIDAPI.Companion.setInstance(token, MainActivity.this); //to be sure, set api instance here
                    onLogin(savedInstanceState);
                    CLog.d("Token received %s", mToken);
                }

                @Override
                public void onFailed() {
                    requestAccount();
                }
            });
        } else if (isNetworkAvailable())
            requestAccount();
        else
            setContentView(R.layout.no_wifi);
    }

    private void requestAccount() {
        AccountUtil.requestAccount(this, future -> {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        });
    }

    private void onLogin(final Bundle savedInstanceState) {
        capsuleOnCreate(savedInstanceState);
        capsuleFrameOnCreate(savedInstanceState);
        onVersionUpdate(BuildConfig.VERSION_CODE, () -> ChangelogDialog.show(MainActivity.this, R.xml.changelog));
        cFab.hide(); //we don't use the fab for now
        cCoordinatorLayout.setScrollAllowed(false); //scrolling is currently not being used
        setSearchView(mToken);
        selectDrawerItem(getLastDrawerPosition()); //Go to dashboard by default TODO fix commitAllowingStateLoss ->   java.lang.IllegalStateException: Can not perform this action after onSaveInstanceState
    }

    //TODO use this?
    public void checkAccountPermission(@NonNull CPermissionCallback callback) {
        requestPermission(callback, 42, Manifest.permission.GET_ACCOUNTS);
    }

    /**
     * Sets up account header
     * will not be added if null
     *
     * @return desired header
     */
    @Nullable
    @Override
    protected AccountHeaderBuilder getAccountHeaderBuilder() {
        return new AccountHeaderBuilder().withActivity(this)
                .withHeaderBackground(R.color.colorPrimary)
                .withSelectionFirstLine(s(R.string.app_name))
                .withSelectionSecondLine(BuildConfig.VERSION_NAME)
                .withProfileImagesClickable(false)
                .withResetDrawerOnProfileListClick(false)
                .addProfiles(new ProfileDrawerItem().withIcon(ContextCompat.getDrawable(this, R.drawable.ctf)))
                .withSelectionListEnabled(false)
                .withSelectionListEnabledForSingleProfile(false);
    }

    /**
     * Sets up array of drawer items
     *
     * @return array of drawer items
     */
    @Override
    protected CDrawerItem[] getDrawerItems() {
        return new CDrawerItem[]{
                new TepidDrawerItem(R.string.dashboard, GoogleMaterial.Icon.gmd_dashboard, DashboardFragment::new),
                new TepidDrawerItem(R.string.roominfo, GoogleMaterial.Icon.gmd_weekend, RoomsViewPagerFragment::new),
                new TepidDrawerItem(R.string.userinfo, GoogleMaterial.Icon.gmd_person, AccountFragment::new),
                new TepidDrawerItem(R.string.ticket, GoogleMaterial.Icon.gmd_bug_report, PreTicketFragment::new),
                new TepidDrawerItem(R.string.settings, GoogleMaterial.Icon.gmd_settings, SettingsFragment::new)
//                new TepidDrawerItem(R.string.reportproblem, GoogleMaterial.Icon.gmd_error, ReportProblemFragment::new)
        };
    }

    //create drawer item and pass the token
    private class TepidDrawerItem extends DrawerItem {

        TepidDrawerItem(@StringRes int titleId, IIcon icon, DrawerFragment drawerFragment) {
            super(titleId, icon, true, drawerFragment);
        }

        @Nullable
        @Override
        public Fragment getFragment() {
            return BaseFragment.getFragment(mToken, null, super.getFragment());
        }
    }

    @Override
    protected void onResume() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String lang = preferences.getString("pref_language", "en");
        SettingsFragment.setLocale(this, lang);
        //prefs.setTheme();
        super.onResume();
    }

    /**
     * helper method - checks if wifi is enabled and if the phone is connected to a network
     * we'll need wifi to talk to TEPID (although we are caching the responses, they could be very stale)
     *
     * @return true if we have wifi, false otherwise
     */
    private boolean isWifiConnected() {
        WifiManager wm = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wm.isWifiEnabled()) {
            WifiInfo wi = wm.getConnectionInfo();
            if (null != wi && wi.getNetworkId() != -1) {
                return true;
            }
        }
        return false;
    }

    private boolean isNetworkAvailable() {
        return Utils.isNetworkAvailable(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        menu.findItem(R.id.action_search).setIcon(new IconicsDrawable(this, GoogleMaterial.Icon.gmd_search).color(Color.WHITE).sizeDp(24).respectFontBounds(true));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                mSearchView.open(true, item);
                return true;
            case R.id.action_changelog:
                ChangelogDialog.show(this, R.xml.changelog);
                return true;
            case R.id.action_settings:
                selectDrawerItemFromId(R.string.settings); //Switch to settings; note that CDrawer for settings has this as the titleId
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
