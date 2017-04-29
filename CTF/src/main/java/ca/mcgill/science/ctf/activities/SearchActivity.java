package ca.mcgill.science.ctf.activities;

import android.content.Context;
import android.widget.TextView;

import com.jakewharton.rxbinding2.widget.RxTextView;
import com.jakewharton.rxbinding2.widget.TextViewTextChangeEvent;
import com.lapism.searchview.SearchAdapter;
import com.lapism.searchview.SearchItem;
import com.lapism.searchview.SearchView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import ca.allanwang.capsule.library.event.SnackbarEvent;
import ca.allanwang.capsule.library.logging.CLog;
import ca.mcgill.science.ctf.R;
import ca.mcgill.science.ctf.api.ITEPID;
import ca.mcgill.science.ctf.api.SingleCallRequest;
import ca.mcgill.science.ctf.api.TEPIDAPI;
import ca.mcgill.science.ctf.api.User;
import ca.mcgill.science.ctf.api.UserBarcode;
import ca.mcgill.science.ctf.api.UserQuery;
import ca.mcgill.science.ctf.fragments.AccountJobFragment;
import ca.mcgill.science.ctf.fragments.base.BaseFragment;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Allan Wang on 2017-03-24.
 * <p>
 * Portion of {@link ca.mcgill.science.ctf.MainActivity} with the search menu logic
 */

public abstract class SearchActivity extends BaseActivity {

    protected SearchView mSearchView;
    private SearchAdapter mSearchAdapter;
    private List<UserQuery> mQueryResults;
    private UserSearch mUserSearch;
    private Call<UserBarcode> mBarcodeScanner;
    private String token;

    //custom activity to add SearchView
    @Override
    protected int getContentViewId() {
        return R.layout.activity_main;
    }

    protected void setSearchView(String token) {
        this.token = token;
        mUserSearch = new UserSearch(token, this);
        RxTextView.textChangeEvents((TextView) findViewById(com.lapism.searchview.R.id.searchEditText_input))
                .debounce(500, TimeUnit.MILLISECONDS)
                .filter(changes -> !changes.text().toString().isEmpty() && changes.text().toString().length() >= 2)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(getSearchObserver());

        mSearchView = (SearchView) findViewById(R.id.searchView);
        mSearchView.setVersion(SearchView.VERSION_MENU_ITEM)
                .setVersionMargins(SearchView.VERSION_MARGINS_MENU_ITEM)
                .setHint(R.string.search_users)
                .setVoice(false)
                .setArrowOnly(false) //we don't want a menu button
                .setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        return true;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        mSearchAdapter.setData(new ArrayList<>()); //so when you go from a big search to no characters, it doesn't blink the old data
                        return true;
                    }
                })
                .setOnOpenCloseListener(new SearchView.OnOpenCloseListener() {
                    @Override
                    public boolean onClose() {
                        if (mBarcodeScanner == null) return false;
                        mBarcodeScanner.cancel();
                        mBarcodeScanner = null;
                        return false;
                    }

                    @Override
                    public boolean onOpen() {
                        mBarcodeScanner = TEPIDAPI.Companion.getInstance(token, SearchActivity.this).scanBarcode();
                        mBarcodeScanner.enqueue(new Callback<UserBarcode>() {
                            @Override
                            public void onResponse(Call<UserBarcode> call, Response<UserBarcode> response) {
                                if (!call.isCanceled()) {
                                    if (mSearchView.isSearchOpen())
                                        mSearchView.close(true);
                                    jumpToUser(response.body().getCode());
                                }
                            }

                            @Override
                            public void onFailure(Call<UserBarcode> call, Throwable t) {

                            }
                        });
                        return false;
                    }
                });

        mSearchAdapter = new SearchAdapter(this);
        mSearchAdapter.addOnItemClickListener((view, position) -> {
            if (mQueryResults == null || mQueryResults.size() <= position)
                return; //no results actually exist
            UserQuery query = mQueryResults.get(position);
            jumpToUser(query.getShortUser());
            //TextView textView = (TextView) view.findViewById(R.id.textView_item_text);
            //TODO reroute to actual fragment rather than dialog; this is just for display
//                new MaterialDialog.Builder(SearchActivity.this)
//                        .title(query.getDisplayName())
//                        .content(query.getShortUser() + "\n" + query.getEmail() + "\nColor printing: " + (query.getColorPrinting() ? "Enabled" : "Disabled"))
//                        .show();
            mSearchView.close(false);
        });
        mSearchView.setAdapter(mSearchAdapter);
    }

    private void jumpToUser(long studentId) {
        TEPIDAPI.Companion.getInstanceDangerously().getUser(studentId).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (!call.isCanceled())
                    jumpToUser(response.body().getShortUser());
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {

            }
        });
    }

    private void jumpToUser(String shortUser) {
        switchFragment(BaseFragment.getFragment(token, shortUser, new AccountJobFragment()));
    }

    private DisposableObserver<TextViewTextChangeEvent> getSearchObserver() {
        return new DisposableObserver<TextViewTextChangeEvent>() {
            @Override
            public void onComplete() {
            }

            @Override
            public void onError(Throwable e) {
                postEventDebug(new SnackbarEvent("SearchActivity failed"));
            }

            @Override
            public void onNext(TextViewTextChangeEvent onTextChangeEvent) {
                mUserSearch.request(onTextChangeEvent.text().toString());
            }
        };
    }

    private void postSearchResults(List<UserQuery> results) {
        List<SearchItem> searchItems = new ArrayList<>();
        if (results == null || results.isEmpty())
            searchItems.add(new SearchItem(getString(R.string.no_suggestions)));
        else
            for (int i = 0; i < results.size(); i++)
                searchItems.add(new SearchItem(results.get(i).getDisplayName()));
        mQueryResults = results;
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
    public void onBackPressed() {
        if (mSearchView.isSearchOpen())
            mSearchView.close(true);
        else
            super.onBackPressed();
    }
}
