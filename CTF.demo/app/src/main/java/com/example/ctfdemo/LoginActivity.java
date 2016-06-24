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
import com.android.volley.toolbox.RequestFuture;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

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

    // checks if user is logging in with an email, that it is a mcgill email
    private boolean isEmailValid(String email) {
        if (email.contains("@") && !(email.endsWith("@mail.mcgill.ca") || email.endsWith("@mcgill.ca")))
            return false;
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
     * Represents an asynchronous login task used to authenticate the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Intent> {

        private final String mUsername;
        private final String mPassword;
        private String PARAM_USER_PASS = "";
        private String KEY_ERROR_MESSAGE = "...";

        UserLoginTask(String username, String password) {
            mUsername = username;
            mPassword = password;
        }

        @Override
        protected Intent doInBackground(Void... params) {
            final Intent res = new Intent();
            String authToken;

            try {
                authToken = getAuthTokenFromCredentials();
                System.out.println(authToken);

                res.putExtra(AccountManager.KEY_ACCOUNT_NAME, mAccountName);
                res.putExtra(AccountManager.KEY_ACCOUNT_TYPE, mAccountType);
                res.putExtra(AccountManager.KEY_AUTHTOKEN, authToken);
                res.putExtra(PARAM_USER_PASS, mPassword);

            } catch (Exception e) {
                res.putExtra(KEY_ERROR_MESSAGE, e.getMessage());
            }

            return res;
        }

        /**
         * submits a session request object to the loginURL with the provided credentials
         * and waits for the server to respond with a session object or an error
         *
         * @return a json string representing a session object
         * @throws ExecutionException if an error occurs waiting for the server response
         * @throws InterruptedException if an error occurs waiting for the server response
         * @throws JsonProcessingException if an error occurs turning the session request into a json object
         * @throws JSONException if an error occurs turning the session request into a json object
         */
        private String getAuthTokenFromCredentials() throws ExecutionException, InterruptedException, JSONException, JsonProcessingException {
            final String loginUrl = "https://tepid.sus.mcgill.ca:8443/tepid/sessions/";
            RequestQueue requestQueue = VolleySingleton.getInstance().getRequestQueue();

            // build a session request object with the provided login credentials
            SessionRequest sr = new SessionRequest()
                    .withUsername(mUsername)
                    .withPassword(mPassword)
                    .withPersistent(true)
                    .withPermanent(true);

            // convert the session request to a json object
            ObjectMapper om = new ObjectMapper();
            JSONObject requestPayload = new JSONObject(om.writeValueAsString(sr));


            // make an future for the response object so we can block while waiting for server reply
            RequestFuture<JSONObject> responseListener = RequestFuture.newFuture();

            // make an error listener for the request
            Response.ErrorListener errorListener = new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            };

            // build and send the request
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, loginUrl, requestPayload, responseListener, errorListener);
            requestQueue.add(request);

            // wait for the response with the session object and return it as a json string, or the empty string if something goes wrong
            JSONObject serverResponse = responseListener.get();

            return serverResponse.toString();
        }

        @Override
        protected void onPostExecute(final Intent intent) {
            mAuthTask = null;
            showProgress(false);

            if (intent.hasExtra(KEY_ERROR_MESSAGE)) {
                mPasswordView.setError(intent.getStringExtra(KEY_ERROR_MESSAGE));
                mPasswordView.requestFocus();
            } else {
                finishLogin(intent);
                finish();
            }
        }
        
        private void finishLogin(Intent intent) {
            String accountPassword = intent.getStringExtra(PARAM_USER_PASS);
            String authToken = intent.getStringExtra(AccountManager.KEY_AUTHTOKEN);
            final Account account = new Account(
                    mAccountName,
                    intent.getStringExtra(AccountManager.KEY_ACCOUNT_TYPE));

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
            mAccountManager.setAuthToken(account, mAuthType, authToken);

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

