package com.example.ctfdemo;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * The main login screen users will see
 * the account manager starts this activity whenever it needs to get a new auth token
 */
public class LoginActivity extends AccountAuthenticatorActivity {

    /**
     * This is the task started in the background when the user presses the login button.
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // credential references
    private AccountManager mAccountManager;
    private String mAccountName, mAccountType, mAuthType;
    public static String ARG_ACCOUNT_NAME, ARG_ACCOUNT_TYPE, ARG_AUTH_TYPE, ARG_IS_ADDING_NEW_ACCOUNT;

    // UI references.
    private EditText mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    // connection references
    final String serverUrl = "https://tepid.sus.mcgill.ca:8443/tepid/sessions/";
    RequestQueue requestQueue = VolleySingleton.getInstance().getRequestQueue();
    JsonObjectRequest request = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_login);

        mAccountManager = AccountManager.get(getBaseContext());
        mAccountName = getIntent().getStringExtra(ARG_ACCOUNT_NAME);
        mAccountType = getIntent().getStringExtra(ARG_ACCOUNT_TYPE);
        mAuthType = getIntent().getStringExtra(ARG_AUTH_TYPE);

        // Set up the login form.
        mEmailView = (EditText) findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    /**
     * attempts to sign in using the credentials provided in the login form.
     * if there are form errors (invalid email, missing fields, etc.), no login
     * attempt is made and the errors are highlighted to the user
     */
    private void attemptLogin() {
        if (mAuthTask != null) { // means another login attempt is already in progress
            return;
        }

        // Reset any previous errors before trying again
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        // cancel is set to true if the credentials are empty or invalid and focusView
        // is set to the offending field
        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        } else if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
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
            mAuthTask = new UserLoginTask(email, password);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic, regex for a mcgill short user or email?
        return true;
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic, or scrap?
        return true;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
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
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Intent> {

        private final String mUsername;
        private final String mPassword;
        private String PARAM_USER_PASS = "";
        private String KEY_ERROR_MESSAGE = "";

        UserLoginTask(String username, String password) {
            mUsername = username;
            mPassword = password;
        }

        @Override
        protected Intent doInBackground(Void... params) {
            final Intent res = new Intent();
            String authToken;

            try {
                authToken = getAuthTokenFromCredentials(mAccountName, mPassword, mAuthType);

                res.putExtra(AccountManager.KEY_ACCOUNT_NAME, mAccountName);
                res.putExtra(AccountManager.KEY_ACCOUNT_TYPE, mAccountType);
                res.putExtra(AccountManager.KEY_AUTHTOKEN, authToken);
                res.putExtra(PARAM_USER_PASS, mPassword);
            } catch (Exception e) {
                res.putExtra(KEY_ERROR_MESSAGE, e.getMessage());
            }

            return res;
        }

        private String getAuthTokenFromCredentials(String mAccountName, String mPassword, String mAuthType) {
            final TextView errorBox = (TextView) findViewById(R.id.token);

            SessionRequest requestBody = new SessionRequest().withUsername(mUsername)
                                        .withPassword(mPassword)
                                        .withPersistent(true)
                                        .withPermanent(true);
            try {
                JSONObject requestString = new JSONObject("{username: \"" + mUsername + "\", password: \"" + mPassword + "\", persistent: false}");
                request = new JsonObjectRequest(Request.Method.POST, serverUrl, requestString,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                errorBox.setText(response.toString());
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                errorBox.setText(error.getMessage());
                            }
                        });
            } catch (JSONException e) {
                errorBox.setText(e.getMessage());
            }
            requestQueue.add(request);

            return "session.toString()";
        }

        @Override
        protected void onPostExecute(final Intent intent) {
            mAuthTask = null;
            showProgress(false);

            if (intent.hasExtra(KEY_ERROR_MESSAGE)) {
                //mPasswordView.setError(intent.getStringExtra(KEY_ERROR_MESSAGE));
                mPasswordView.requestFocus();
            } else {
                finishLogin(intent);
                finish();
            }
        }
        
        private void finishLogin(Intent intent) {
            String accountPassword = intent.getStringExtra(PARAM_USER_PASS);
            final Account account = new Account(
                    mAccountName,
                    intent.getStringExtra(AccountManager.KEY_ACCOUNT_TYPE));
            String authtoken = intent.getStringExtra(AccountManager.KEY_AUTHTOKEN);

            if (getIntent().getBooleanExtra(ARG_IS_ADDING_NEW_ACCOUNT, false)) {
                // Creating the account
                // Password is optional to this call, safer not to send it really.
                mAccountManager.addAccountExplicitly(account, accountPassword, null);
            } else {
                // Password change only
                mAccountManager.setPassword(account, accountPassword);
            }
            // set the auth token we got (Not setting the auth token will cause
            // another call to the server to authenticate the user)
            mAccountManager.setAuthToken(account, mAuthType, authtoken);

            // Our base class can do what Android requires with the
            // KEY_ACCOUNT_AUTHENTICATOR_RESPONSE extra that onCreate has
            // already grabbed
            setAccountAuthenticatorResult(intent.getExtras());
            // Tell the account manager settings page that all went well
            setResult(RESULT_OK, intent);
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}

