package com.example.ctfdemo;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.AccountManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by erasmas on 5/4/16.
 */
public class StudentAuthenticatorService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        if (intent.getAction().equals(AccountManager.ACTION_AUTHENTICATOR_INTENT)) {
            return null;
        }

        AbstractAccountAuthenticator authenticator = new StudentAuthenticator(this);
        return authenticator.getIBinder();
    }
}
