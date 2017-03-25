package ca.mcgill.science.ctf.activities;

import android.content.Context;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.text.TextUtils;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.lapism.searchview.SearchAdapter;
import com.lapism.searchview.SearchItem;
import com.lapism.searchview.SearchView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import ca.allanwang.capsule.library.activities.CapsuleActivityFrame;
import ca.allanwang.capsule.library.logging.CLog;
import ca.mcgill.science.ctf.R;
import ca.mcgill.science.ctf.api.ITEPID;
import ca.mcgill.science.ctf.api.SingleCallRequest;
import ca.mcgill.science.ctf.api.TEPIDAPI;
import ca.mcgill.science.ctf.api.UserQuery;
import retrofit2.Call;

/**
 * Created by Allan Wang on 2017-03-24.
 * <p>
 * Portion of {@link ca.mcgill.science.ctf.MainActivity} with the search menu logic
 */

public abstract class SearchActivity extends CapsuleActivityFrame {

    protected SearchView mSearchView;
    private UserSearch mUserSearch;
    private SearchAdapter mSearchAdapter;
    private List<UserQuery> mQueryResults;
    private long mDelay = 300;

    //custom activity to add SearchView
    @Override
    protected int getContentViewId() {
        return R.layout.activity_main;
    }

    protected void setSearchView(String token) {
        mSearchView = (SearchView) findViewById(R.id.searchView);
        mUserSearch = new UserSearch(token, this);
        mSearchView.setVersion(SearchView.VERSION_MENU_ITEM)
                .setVersionMargins(SearchView.VERSION_MARGINS_MENU_ITEM)
                .setHint(R.string.search_users)
                .setDelay(mDelay)
                .setArrowOnly(false) //we don't want a menu button
                .setOnQueryTextListener(new SearchView.OnQueryTextListener() {
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

        mSearchAdapter = new SearchAdapter(this);
        mSearchAdapter.addOnItemClickListener(new SearchAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (mQueryResults == null || mQueryResults.size() <= position)
                    return; //no results actually exist
                UserQuery query = mQueryResults.get(position);
                //TextView textView = (TextView) view.findViewById(R.id.textView_item_text);
                //TODO reroute to actual fragment rather than dialog; this is just for display
                new MaterialDialog.Builder(SearchActivity.this)
                        .title(query.getDisplayName())
                        .content(query.getShortUser() + "\n" + query.getEmail() + "\nColor printing: " + (query.getColorPrinting() ? "Enabled" : "Disabled"))
                        .show();
                mSearchView.close(false);
            }
        });
        mSearchView.setAdapter(mSearchAdapter);
    }

    private void search(String s) {
        if (s == null || s.length() < 3) return;
        mUserSearch.request(s);
    }

    private void postSearchResults(List<UserQuery> results) {
        mQueryResults = results;
        List<SearchItem> searchItems = new ArrayList<>();
        if (results == null || results.isEmpty())
            searchItems.add(new SearchItem(getString(R.string.no_suggestions)));
        else
            for (int i = 0; i < results.size(); i++)
                searchItems.add(new SearchItem(results.get(i).getDisplayName()));
        mSearchAdapter.setData(searchItems);
    }

    private class UserSearch extends SingleCallRequest<String, List<UserQuery>> {

        private ITEPID api;

        private UserSearch(@NotNull String token, @NotNull Context c) {
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
            postSearchResults(result);
        }

        @Override
        protected void onFail(@NotNull Throwable t) {
            CLog.e("Search fail %s", t.getMessage());
            postSearchResults(null);
        }

        @Override
        protected void onEnd(int flag) {

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SearchView.SPEECH_REQUEST_CODE && resultCode == RESULT_OK) {
            List<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (results != null && results.size() > 0) {
                String searchWrd = results.get(0);
                if (!TextUtils.isEmpty(searchWrd)) {
                    if (mSearchView != null) {
                        mSearchView.setQuery(searchWrd, true);
                    }
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        if (mSearchView.isSearchOpen())
            mSearchView.close(true);
        else
            super.onBackPressed();
    }
}
