package com.example.ctfdemo.auth;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Base64;

import com.example.ctfdemo.CTFApp;
import com.example.ctfdemo.R;
import com.example.ctfdemo.tepid.Session;

import java.io.IOException;

/*
import com.fasterxml.jackson.databind.ObjectMapper;*/

public class AccountUtil {
    private static Account account;
    private static Session session;
    public static final String accountType = CTFApp.getAppContext().getResources().getString(R.string.authenticator_account_type);
    public static final String tokenType = CTFApp.getAppContext().getResources().getString(R.string.authenticator_token_type);
    public static final String tepidURL = "https://tepid.sus.mcgill.ca:8443/tepid/";

    /**
     *  method to init variables needed for other components to request auth token
     *  assumes an account is already present on device. components that use this should
     *  do a null check on getAccount() and call am.addAccount() themselves as necessary
     * @param context
     */
    public static void init(final Context context) {
        AccountManager am = AccountManager.get(context);

        if (am.getAccountsByType(accountType).length > 0) {

            // init account
            account = am.getAccountsByType(accountType)[0];

            // get auth token
            final AccountManagerFuture<Bundle> future = am.getAuthToken(account, tokenType, null, (Activity) context, null, null);
            //todo should we pass the the given "context" parameter, or get CTFApp.getAppContext()?
            String token = "";
            try {
                future.getResult().getString(AccountManager.KEY_AUTHTOKEN);
            } catch (OperationCanceledException | IOException | AuthenticatorException e) {
                //todo better exception handling, what causes authenticator exception, what to do if cancelled?
            }


/*            // init session
            ObjectMapper om = new ObjectMapper();
            try {
                session = om.readValue(token, Session.class);
            } catch (IOException e) {
                e.printStackTrace();
            }*/
        }
    }

    public static Account getAccount() {
        return account;
    }

    public static String getUserName() {
        if (session == null) {
            init(CTFApp.getAppContext());
        }
        return session.getUser().shortUser;
    }

    /**
     * the token used in get requests to tepid server
     * @return the hashed authToken
     */
    public static String getAuthTokenHash() {
        if (session == null) {
            init(CTFApp.getAppContext());
        }

        return Base64.encodeToString((session.getUser().shortUser + ":" + session.getId()).getBytes(), Base64.CRLF);
    }

    public static void removeAccount() {

        AccountManager.get(CTFApp.getAppContext()).removeAccountExplicitly(account);
    }
}