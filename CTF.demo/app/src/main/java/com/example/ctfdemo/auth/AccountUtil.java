package com.example.ctfdemo.auth;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.util.Base64;

import com.example.ctfdemo.CTFApp;
import com.example.ctfdemo.R;
import com.example.ctfdemo.requests.CTFSpiceService;
import com.example.ctfdemo.requests.TokenRequest;
import com.example.ctfdemo.tepid.Session;
import com.google.gson.Gson;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import java.io.IOException;


public class AccountUtil {
    private static Account account;
    private static String token;
    public static final String accountType = CTFApp.getAppContext().getResources().getString(R.string.authenticator_account_type);
    public static final String tokenType = CTFApp.getAppContext().getResources().getString(R.string.authenticator_token_type);
    public static final String KEY_USERNAME = CTFApp.getAppContext().getResources().getString(R.string.key_username);
    public static final String tepidURL = "https://tepid.sus.mcgill.ca:8443/tepid/";
    private static SpiceManager spiceManager = new SpiceManager(CTFSpiceService.class);

    /**
     *  method to init variables needed for other components to request auth token
     *  assumes an account is already present on device. components that use this should
     *  do a null check on getAccount() and call am.addAccount() themselves as necessary
     */
    //todo should change this param to Activity
    public static void init(final Context context) {
        spiceManager.start(context);

        AccountManager am = AccountManager.get(CTFApp.getAppContext());

        if (am.getAccountsByType(accountType).length > 0) {

            // init account
            account = am.getAccountsByType(accountType)[0];

            // get auth token

            spiceManager.execute(new TokenRequest(account, context), new RequestListener<String>(){

                @Override
                public void onRequestFailure(SpiceException spiceException) {
                    AccountManager.get(context).addAccount(AccountUtil.accountType, AccountUtil.tokenType, null, null, (Activity) context, null, null);
                }

                @Override
                public void onRequestSuccess(String s) {
                    token = s;
                }
            });
            // todo will this still work using ctfapp.getappcontext?!
            // todo confirmed will not work. can't cast application to activity
            //final AccountManagerFuture<Bundle> future = am.getAuthToken(account, tokenType, null, (Activity) context, null, null);
            //todo should we pass the the given "context" parameter, or get CTFApp.getAppContext()?
            //token = "";
            //try {
            //    token = future.getResult().getString(AccountManager.KEY_AUTHTOKEN);
            //} catch (OperationCanceledException | IOException | AuthenticatorException e) {
                //todo better exception handling, what causes authenticator exception, what to do if cancelled?
            //}
        }

        spiceManager.shouldStop();
    }

    public static Account getAccount() {
        return account;
    }

    public static String getUserName() {
        if (account == null) {
            // todo this won't work
            init(CTFApp.getAppContext());
        }

        AccountManager am = AccountManager.get(CTFApp.getAppContext());
        return am.getUserData(account, AccountUtil.KEY_USERNAME);
    }

    /**
     * get the token for the currently signed in user
     * @return the hashed auth token used for requests to TEPID
     */
    public static String getAuthToken() {
        if (token == null) {
            init(CTFApp.getAppContext());
        }

        return token;
    }

    public static void removeAccount() {
        AccountManager.get(CTFApp.getAppContext()).removeAccountExplicitly(account);
    }
}