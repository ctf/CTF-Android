package ca.mcgill.science.ctf;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;

import butterknife.BindView;
import butterknife.ButterKnife;
import ca.mcgill.science.ctf.api.ITepid;
import ca.mcgill.science.ctf.api.Session;
import ca.mcgill.science.ctf.api.SessionRequest;
import ca.mcgill.science.ctf.api.TepidApi;
import ca.mcgill.science.ctf.auth.AccountUtil;
import ca.mcgill.science.ctf.utils.Preferences;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * The main login screen users will see
 * the account manager starts this activity whenever it needs to get a new auth token
 */
public class LoginActivity extends AccountAuthenticatorActivity {

    /**
     * This is the worker thread started by the LOGIN button, use this reference to cancel
     * the login if requested
     */
    //private UserLoginTask mAuthTask = null;

    private AccountManager mAccountManager;
    public static String ARG_ACCOUNT_TYPE = "ACCOUNT_TYPE",
            ARG_ACCOUNT_NAME = "ACCOUNT_NAME",
            ARG_TOKEN_TYPE = "TOKEN_TYPE",
            ARG_IS_ADDING_NEW_ACCOUNT = "BOOL_IS_NEW_ACCOUNT";

    // UI references.
    @BindView(R.id.username)
    public EditText mUsernameField;
    @BindView(R.id.password)
    public EditText mPasswordField;
    @BindView(R.id.login_progress)
    public View mProgressView;
    @BindView(R.id.login_form)
    public View mLoginFormView;
    private ITepid mAPI;
    private Call<Session> mRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Preferences.setTheme(this);
        setContentView(R.layout.activity_student_login);
        mAccountManager = AccountManager.get(getBaseContext());
        mAPI = TepidApi.Companion.getInstance(null, this);
        ButterKnife.bind(this);
        mPasswordField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        findViewById(R.id.sign_in_button).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });
    }

    /**
     * tries to sign in with username and password entered in the form,
     * if there are form errors (invalid email, missing fields, etc.), no login
     * attempt is made and the errors are highlighted for the user
     */
    private void attemptLogin() {
/*        if (mAuthTask != null) { // means another login attempt is already in progress
            return;
        }*/

        // Reset any previous errors before trying again
        mUsernameField.setError(null);
        mPasswordField.setError(null);

        // Store values at the time of the login attempt.
        String username = mUsernameField.getText().toString();
        String password = mPasswordField.getText().toString();

        // cancel is set to true if the credentials are empty/invalid
        // and the offending field is focused
        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password)) {
            mPasswordField.setError(getString(R.string.error_field_required));
            focusView = mPasswordField;
            cancel = true;
        }
        // Check for a valid email address.
        if (TextUtils.isEmpty(username)) {
            mUsernameField.setError(getString(R.string.error_field_required));
            focusView = mUsernameField;
            cancel = true;
        } else if (!isEmailValid(username)) {
            mUsernameField.setError(getString(R.string.error_invalid_email));
            focusView = mUsernameField;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            //mAuthTask = new UserLoginTask(email, password);
            //mAuthTask.execute((Void) null);


            mRequest = mAPI.getSession(new SessionRequest(username, password));
            mRequest.enqueue(new Callback<Session>() {
                @Override
                public void onResponse(Call<Session> call, Response<Session> response) {
                    if (response.body() == null || !response.isSuccessful()) {
                        mPasswordField.setError("Empty body returned");
                        showProgress(false);
                        mPasswordField.requestFocus();
                    } else {
                        Session responseData = response.body();
                        final Account account = new Account(responseData.getUser().getShortUser(), AccountUtil.getAccountType(LoginActivity.this));

                        if (getIntent().getBooleanExtra(ARG_IS_ADDING_NEW_ACCOUNT, false)) {
                            Bundle userData = new Bundle();
                            userData.putString(AccountUtil.KEY_SESSION, new Gson().toJson(responseData));
                            // password is optional here, we won't keep it
                            mAccountManager.addAccountExplicitly(account, null, userData);
                        }

                        // set the auth token using the session we received
                        mAccountManager.setAuthToken(account, AccountUtil.getTokenType(LoginActivity.this),
                                Base64.encodeToString((responseData.getUser().getShortUser() + ":" + responseData.get_id()).getBytes(), Base64.CRLF));
                        finish();
                    }
                }

                @Override
                public void onFailure(Call<Session> call, Throwable t) {
                    if (!call.isCanceled()) mPasswordField.setError("Could not Log in");
                    showProgress(false);
                    mPasswordField.requestFocus();
                }
            });

        }
    }

    // checks if user is logging in with an email, that it is a mcgill email
    private boolean isEmailValid(String email) {
        return !email.contains("@") || (email.endsWith("@mail.mcgill.ca") || email.endsWith("@mcgill.ca"));
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mProgressView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }
}

