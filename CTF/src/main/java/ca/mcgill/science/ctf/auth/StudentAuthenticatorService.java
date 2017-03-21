package ca.mcgill.science.ctf.auth;

import android.accounts.AccountManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

public class StudentAuthenticatorService extends Service {

    private StudentAuthenticator authenticator = null;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        if (intent.getAction().equals(AccountManager.ACTION_AUTHENTICATOR_INTENT))
            return getAuthenticator().getIBinder();
        return null;
    }

    private synchronized StudentAuthenticator getAuthenticator() {
        if (authenticator == null)
            authenticator = new StudentAuthenticator(this);
        return authenticator;
    }
}
