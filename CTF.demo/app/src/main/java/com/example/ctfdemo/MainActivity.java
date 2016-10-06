package com.example.ctfdemo;

import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.example.ctfdemo.auth.AccountUtil;
import com.example.ctfdemo.fragments.DashboardFragment;
import com.example.ctfdemo.fragments.MyAccountFragment;
import com.example.ctfdemo.fragments.ReportProblemFragment;
import com.example.ctfdemo.fragments.RoomFragment;
import com.example.ctfdemo.fragments.SettingsFragment;
import com.example.ctfdemo.requests.CTFSpiceService;
import com.example.ctfdemo.requests.TokenRequest;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private String token;
    private SpiceManager requestManager = new SpiceManager(CTFSpiceService.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AccountUtil.initAccount(this);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String lang = preferences.getString("pref_language", "en");
        SettingsFragment.setLocale(this, lang); //todo put setlocale somewhere else? CTFApp maybe?

        if (isWifiConnected()) {
            if (AccountUtil.isSignedIn()) {
                setContentView(R.layout.activity_main);

                Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
                setSupportActionBar(toolbar);

                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
                drawer.addDrawerListener(toggle);
                toggle.syncState();
                NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
                navigationView.setNavigationItemSelectedListener(this);

            } else {
                AccountManager.get(this).addAccount(AccountUtil.accountType, AccountUtil.tokenType, null, null, this, new AccountManagerCallback<Bundle>() {
                    @Override
                    public void run(AccountManagerFuture<Bundle> accountManagerFuture) {
                        // switch back to main activity after user signs in
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                    }
                }, null);
            }
        } else {
            setContentView(R.layout.no_wifi);

            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
        }
    }

    @Override
    protected void onStart() {
        if (AccountUtil.isSignedIn()) {
            requestManager.start(this);
            requestManager.execute(new TokenRequest(AccountUtil.getAccount(), this), new RequestListener<String>() {
                @Override
                public void onRequestFailure(SpiceException spiceException) {
                    // todo wat do if we can't get token?!
                }

                @Override
                public void onRequestSuccess(String str) {
                    token = str;
                    FragmentManager fm = getSupportFragmentManager();
                    fm.beginTransaction().replace(R.id.content_frame, DashboardFragment.newInstance(token)).commit();
                }
            });
        }

        super.onStart();
    }

    @Override
    protected void onResume() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String lang = preferences.getString("pref_language", "en");
        SettingsFragment.setLocale(this, lang);
        //prefs.setTheme();
        super.onResume();
    }

    @Override
    protected void onStop() {
        if (requestManager.isStarted()) {
            requestManager.shouldStop();
        }
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * this is where we handle click events in the navigation drawer
     * @param item the item that was clicked
     * @return
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        // close the navigation drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        FragmentManager fm = getSupportFragmentManager();
        int id = item.getItemId();

        //todo null check token before passing to frags
        switch (id) {
            case R.id.dashboard:
                fm.beginTransaction().replace(R.id.content_frame, DashboardFragment.newInstance(token), DashboardFragment.TAG).commit();
                getSupportActionBar().setTitle(R.string.dashboard);
                break;
            case R.id.room_info:
                fm.beginTransaction().replace(R.id.content_frame, RoomFragment.newInstance(token), RoomFragment.TAG).commit();
                getSupportActionBar().setTitle(R.string.roominfo);
                break;
            case R.id.user_info:
                fm.beginTransaction().replace(R.id.content_frame, MyAccountFragment.newInstance(token), MyAccountFragment.TAG).commit();
                getSupportActionBar().setTitle(R.string.userinfo);
                break;
            case R.id.settings:
                fm.beginTransaction().replace(R.id.content_frame, SettingsFragment.newInstance(token), SettingsFragment.TAG).commit();
                getSupportActionBar().setTitle(R.string.settings);
                break;
            case R.id.report_problem:
                fm.beginTransaction().replace(R.id.content_frame, new ReportProblemFragment(), ReportProblemFragment.TAG).commit();
                getSupportActionBar().setTitle(R.string.reportproblem);
                break;
        }

        return true;
    }

    /**
     * helper method - checks if wifi is enabled and if the phone is connected to a network
     * we'll need wifi to talk to TEPID (although we are caching the responses, they could be very stale)
     * @return true if we have wifi, false otherwise
     */
    private boolean isWifiConnected() {
        WifiManager wm = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        if (wm.isWifiEnabled()) {
            WifiInfo wi = wm.getConnectionInfo();
            if (null != wi && wi.getNetworkId() != -1) {
                return true;
            }
        }
        return false;
    }

}
