package ca.mcgill.science.ctf;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.os.Build;
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
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import org.json.JSONException;

import java.util.concurrent.ExecutionException;

import ca.mcgill.science.ctf.auth.AccountUtil;
import ca.mcgill.science.ctf.requests.CTFSpiceService;
import ca.mcgill.science.ctf.requests.LoginRequest;
import ca.mcgill.science.ctf.tepid.Session;

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
    private EditText mUsernameField;
    private EditText mPasswordField;
    private View mProgressView;
    private View mLoginFormView;

    private SpiceManager spiceManager = new SpiceManager(CTFSpiceService.class);


    @Override
    protected void onStart() {
        spiceManager.start(this);
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_login);

        mAccountManager = AccountManager.get(getBaseContext());

        // Set up the login form.
        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
        mUsernameField = (EditText) findViewById(R.id.username);
        mPasswordField = (EditText) findViewById(R.id.password);
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

    @Override
    protected void onStop() {
        spiceManager.shouldStop();
        super.onStop();
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

            spiceManager.execute(new LoginRequest(username, password), "json", DurationInMillis.ALWAYS_EXPIRED, new LoginRequestListener());

        }
    }

    // checks if user is logging in with an email, that it is a mcgill email
    private boolean isEmailValid(String email) {
        return !(email.contains("@") && !(email.endsWith("@mail.mcgill.ca") || email.endsWith("@mcgill.ca")));
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

    private final class LoginRequestListener implements RequestListener<Session> {

        @Override
        public void onRequestFailure(SpiceException spiceException) {
            showProgress(false);
            mPasswordField.setError(spiceException.getMessage());//todo probs not a great idea to show user exception message eh?
            mPasswordField.requestFocus();
        }

        @Override
        public void onRequestSuccess(Session session) {
            showProgress(false);

            // create a new CTF account, will be displayed in settings under user's salutation
            final Account account = new Account(session.getUser().shortUser, AccountUtil.accountType);

            if (getIntent().getBooleanExtra(ARG_IS_ADDING_NEW_ACCOUNT, false)) {
                Bundle userData = new Bundle();
                userData.putString(AccountUtil.KEY_SESSION, new Gson().toJson(session)); //todo should we save the whole damn session object?
                // password is optional here, we won't keep it
                mAccountManager.addAccountExplicitly(account, null, userData);
            }

            // set the auth token using the session we received
            mAccountManager.setAuthToken(account, AccountUtil.tokenType,
                    Base64.encodeToString((session.getUser().shortUser + ":" + session.getId()).getBytes(), Base64.CRLF));

            finish();
        }
    }

    /**
     * Represents an asynchronous login task used to authenticate the user.
     */
    /*public class UserLoginTask extends AsyncTask<Void, Void, Intent> {

        private final String mUsername;
        private final String mPassword;
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
                authToken = getAuthTokenFromCredentials();
                //System.out.println(authToken);

                res.putExtra(ARG_ACCOUNT_TYPE, AccountUtil.accountType);
                res.putExtra(ARG_ACCOUNT_NAME, mUsername);
                res.putExtra(ARG_TOKEN_TYPE, AccountUtil.tokenType);
                res.putExtra(AccountManager.KEY_AUTHTOKEN, authToken);

            } catch (Exception e) {
                res.putExtra(KEY_ERROR_MESSAGE, e.toString());
            }

            return res;
        }

        *//**
         * submits a session request object to the loginURL with the provided credentials
         * and waits for the server to respond with a session object or an error
         *
         * @return a json string representing a session object
         * @throws ExecutionException if an error occurs waiting for the server response
         * @throws InterruptedException if an error occurs waiting for the server response
         * @throws JSONException if an error occurs turning the session request into a json object
         *//*
        private String getAuthTokenFromCredentials() throws ExecutionException, InterruptedException, JSONException {


*//*
            final String loginUrl = "https://tepid.sus.mcgill.ca:8443/tepid/";
            final WebTarget tepidServer = ClientBuilder.newBuilder().build().target(loginUrl);

            // build session request with the provided login credentials
            SessionRequest sr = new SessionRequest()
                    .withUsername(mUsername)
                    .withPassword(mPassword)
                    .withPersistent(true)
                    .withPermanent(true);

            return tepidServer.path("sessions")
                    .request(MediaType.APPLICATION_JSON)
                    .post(Entity.entity(sr, MediaType.APPLICATION_JSON)).toString();*//*
            return "";
        }

        @Override
        protected void onPostExecute(final Intent intent) {
            mAuthTask = null;
            showProgress(false);

            if (intent.hasExtra(KEY_ERROR_MESSAGE)) {
                mPasswordField.setError(intent.getStringExtra(KEY_ERROR_MESSAGE));
                mPasswordField.requestFocus();
            } else {
                finishLogin(intent);
                finish();
            }
        }
        
        private void finishLogin(Intent intent) {
            String authToken = intent.getStringExtra(AccountManager.KEY_AUTHTOKEN);
            final Account account = new Account(intent.getStringExtra(ARG_ACCOUNT_NAME), intent.getStringExtra(ARG_ACCOUNT_TYPE));

            if (getIntent().getBooleanExtra(ARG_IS_ADDING_NEW_ACCOUNT, false)) {
                // Creating the account
                // Password is optional to this call, safer not to send it really.
                mAccountManager.addAccountExplicitly(account, null, null);
            }
            // set the auth token we got (Not setting the auth token will cause
            // another call to the server to authenticate the user)
            mAccountManager.setAuthToken(account, intent.getStringExtra(ARG_TOKEN_TYPE), authToken);

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
    }*/
}

