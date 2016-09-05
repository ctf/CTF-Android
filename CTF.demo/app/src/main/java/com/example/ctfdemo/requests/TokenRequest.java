package com.example.ctfdemo.requests;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import com.example.ctfdemo.auth.AccountUtil;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.SpiceRequest;

public class TokenRequest extends SpiceRequest<String> {

    private static Account account;
    private static Context context;

    public TokenRequest(Account account, Context context) {
        super(String.class);
        this.account = account;
        this.context = context;
    }

    @Override
    public String loadDataFromNetwork() throws Exception {
        // todo this throws OperationCancelledException, AuthenticatorException, IOException, under what conditions, fine-tune exception handling
        String token = AccountManager
                .get(context)
                .getAuthToken(account, AccountUtil.tokenType, null, (Activity) context, null, null)
                .getResult()
                .getString(AccountManager.KEY_AUTHTOKEN);
        if (token == null || token.isEmpty()) {
            throw new SpiceException("No token found on device.");
        }
        return token.trim();
    }
}
