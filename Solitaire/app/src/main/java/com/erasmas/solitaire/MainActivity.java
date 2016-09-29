package com.erasmas.solitaire;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.erasmas.fragment.FragmentDecrypt;
import com.erasmas.fragment.FragmentEncrypt;
import com.erasmas.fragment.FragmentKeys;
import com.erasmas.pojos.Key;
import com.erasmas.solitaire.R.layout;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!getPreferences(Context.MODE_PRIVATE).getAll().containsKey(Key.DEFAULT_LABEL)) {
            getPreferences(Context.MODE_PRIVATE)
                    .edit()
                    .putString(Key.DEFAULT_LABEL, (new Key()).toString())
                    .commit();
        }

        setContentView(layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction().replace(R.id.content_frame, new FragmentEncrypt()).commit();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        FragmentManager fm = getSupportFragmentManager();
        int id = item.getItemId();

        if (id == R.id.nav_encrypt) {
            fm.beginTransaction().replace(R.id.content_frame, new FragmentEncrypt()).commit();
        } else if (id == R.id.nav_decrypt) {
            fm.beginTransaction().replace(R.id.content_frame, new FragmentDecrypt()).commit();
        } else if (id == R.id.nav_keys) {
            fm.beginTransaction().replace(R.id.content_frame, new FragmentKeys()).commit();
        } else if (id == R.id.nav_reset) {
            getPreferences(Context.MODE_PRIVATE)
                    .edit()
                    .clear()
                    .commit();
            getPreferences(Context.MODE_PRIVATE)
                    .edit()
                    .putString(Key.DEFAULT_LABEL, (new Key()).toString())
                    .commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
