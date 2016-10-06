package com.example.ctfdemo.requests;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;

import com.example.ctfdemo.auth.AccountUtil;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.SpiceRequest;

/**
 * request to get the stored token from android's account manager,
 * we do this in the robospice service because it is not safe to do on main thread
 */
public class TokenRequest extends SpiceRequest<String> {

    private Account account;
    private Context context;

    public TokenRequest(Account account, Context context) {
        super(String.class);
        this.account = account;
        this.context = context; // the context from which we launch the LoginActivity if no token is found, should normally be MainActivity
    }

    @Override
    public String loadDataFromNetwork() throws Exception {
        // todo this throws OperationCancelledException, AuthenticatorException, IOException, under what conditions - see AccountManager docs, fine-tune exception handling
        String token = AccountManager
                .get(context) //get an account manager
                .getAuthToken(account, AccountUtil.tokenType, null, (Activity) context, null, null) // request the saved auth token
                .getResult() // wait for result
                .getString(AccountManager.KEY_AUTHTOKEN); // retrieve the auth token from the returned Bundle
        if (token == null || token.isEmpty()) {
            throw new SpiceException("No token found on device.");
        }
        return token.trim();
    }
}
