package ca.mcgill.science.ctf;

import android.Manifest;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
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
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.core.CrashlyticsCore;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import ca.allanwang.capsule.library.activities.CapsuleActivityFrame;
import ca.allanwang.capsule.library.changelog.ChangelogDialog;
import ca.allanwang.capsule.library.interfaces.CDrawerItem;
import ca.allanwang.capsule.library.item.DrawerItem;
import ca.allanwang.capsule.library.logging.CLog;
import ca.allanwang.capsule.library.logging.CallbackLogTree;
import ca.allanwang.capsule.library.permissions.CPermissionCallback;
import ca.mcgill.science.ctf.api.ITEPID;
import ca.mcgill.science.ctf.api.SingleCallRequest;
import ca.mcgill.science.ctf.api.TEPIDAPI;
import ca.mcgill.science.ctf.api.UserQuery;
import ca.mcgill.science.ctf.auth.AccountUtil;
import ca.mcgill.science.ctf.fragments.BaseFragment;
import ca.mcgill.science.ctf.fragments.DashboardFragment;
import ca.mcgill.science.ctf.fragments.MyAccountFragment;
import ca.mcgill.science.ctf.fragments.ReportProblemFragment;
import ca.mcgill.science.ctf.fragments.SettingsFragment;
import ca.mcgill.science.ctf.utils.Preferences;
import ca.mcgill.science.ctf.utils.Utils;
import io.fabric.sdk.android.Fabric;
import retrofit2.Call;
import timber.log.Timber;

public class MainActivity extends CapsuleActivityFrame {

    private String mToken;
    private UserSearch mUserSearch;

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        if (!BuildConfig.DEBUG && !BuildConfig.BUILD_TYPE.equals("releaseTest")) {
            disableCLog(); //Make things cleaner; don't log unless specified
            //Add crashlytics
            CrashlyticsCore core = new CrashlyticsCore.Builder().build();
            Fabric.with(this, new Crashlytics.Builder().core(core).build(), new Answers(), new Crashlytics());
            Timber.plant(new CallbackLogTree(new CallbackLogTree.Callback() {
                @Override
                public void log(int priority, String tag, String message, Throwable t) {
                    if (priority == Log.ERROR) {
                        if (t == null) {
                            Crashlytics.logException(new Exception(message));
                        } else {
                            Crashlytics.logException(t);
                        }
                    } else {
                        Crashlytics.log(priority, tag, message);
                    }
                }
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
                    mUserSearch = new UserSearch(MainActivity.this, mToken);
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
        AccountUtil.requestAccount(this, new AccountManagerCallback<Bundle>() {
            @Override
            public void run(AccountManagerFuture<Bundle> future) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void onLogin(final Bundle savedInstanceState) {
        capsuleOnCreate(savedInstanceState);
        capsuleFrameOnCreate(savedInstanceState);
        cFab.hide(); //we don't use the fab for now
        cCoordinatorLayout.setScrollAllowed(false); //scrolling is currently not being used
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
        return generateDrawerItems(
                new TepidDrawerItem(R.string.dashboard, GoogleMaterial.Icon.gmd_dashboard, new DashboardFragment()),
//                new TepidDrawerItem(R.string.roominfo, GoogleMaterial.Icon.gmd_weekend, new RoomMapFragment()),
                new TepidDrawerItem(R.string.userinfo, GoogleMaterial.Icon.gmd_person, new MyAccountFragment()),
                new TepidDrawerItem(R.string.settings, GoogleMaterial.Icon.gmd_settings, new SettingsFragment()),
                new TepidDrawerItem(R.string.reportproblem, GoogleMaterial.Icon.gmd_error, new ReportProblemFragment())
        );
    }

    //create drawer item and pass the token
    private class TepidDrawerItem extends ShortCDrawerItem {

        TepidDrawerItem(@StringRes int titleId, GoogleMaterial.Icon icon, Fragment fragment) {
            super(titleId, icon, fragment);
        }

        public CDrawerItem getCDrawerItem() {
            return new DrawerItem(titleId, icon, true) {
                @Nullable
                @Override
                public Fragment getFragment() {
                    return BaseFragment.getFragment(mToken, fragment);
                }
            };
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
        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchItem.setIcon(new IconicsDrawable(this, GoogleMaterial.Icon.gmd_search).color(Color.WHITE).sizeDp(24).respectFontBounds(true));
        final SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                search(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                search(newText);
                return true;
            }


        });
        return true;
    }

    private void search(String s) {
        if (s == null || s.length() < 3) return;
        mUserSearch.request(s);
    }

    private class UserSearch extends SingleCallRequest<String, List<UserQuery>> {

        private ITEPID api;

        private UserSearch(@NotNull Context c, @NotNull String token) {
            super(c, token);
            api = TEPIDAPI.Companion.getInstance(token, c);
        }

        @NotNull
        @Override
        protected Call<List<UserQuery>> getAPICall(String input) {
            return api.getUserQuery(input);
        }

        @Override
        protected void onSuccess(List<UserQuery> result) {
            CLog.d("Search Success %d", result.size());
        }

        @Override
        protected void onFail(@NotNull Throwable t) {
            CLog.e("Search fail %s", t.getMessage());
        }

        @Override
        protected void onEnd(int flag) {

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
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
