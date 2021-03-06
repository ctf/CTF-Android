package ca.mcgill.science.ctf.auth;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.google.gson.Gson;

import ca.allanwang.capsule.library.logging.CLog;
import ca.mcgill.science.ctf.R;
import ca.mcgill.science.ctf.api.Session;
import ca.mcgill.science.ctf.api.User;

/**
 * utility class for operations relating to the CTFAccount stored in android's account manager
 */
public class AccountUtil {

    private static AccountManager am;
    private static Account account;
    public static final String KEY_SESSION = "SESSION";

    public static String getAccountType(Context context) {
        return context.getResources().getString(R.string.authenticator_account_type);
    }

    public static String getTokenType(Context context) {
        return context.getResources().getString(R.string.authenticator_token_type);
    }

    /**
     * called on startup to keep a global reference to the user's account
     *
     * @param context likely {@link ca.mcgill.science.ctf.StartActivity}
     */
    public static void initAccount(Context context) {
        am = AccountManager.get(context);
        String accountType = getAccountType(context);
        if (am.getAccountsByType(accountType).length > 0) { //TODO request account permission
            account = am.getAccountsByType(accountType)[0];
        }
    }

    public static Account getAccount() {
        return account;
    }

    /**
     * gets the serialized Session object the TEPID server gave us when we authenticated
     *
     * @return the user's current session on the TEPID server
     */
    public static Session getSession() {
        String s = am.getUserData(account, AccountUtil.KEY_SESSION);
        return new Gson().fromJson(s, Session.class);
    }

    /**
     * checks whether we successfully retrieved the account object from android's account manager
     *
     * @return
     */
    public static boolean isSignedIn() {
        return getAccount() != null;
    }

    /**
     * retrieves the short user from the stored Session object
     *
     * @return user's "short user (jdoe7)"
     */
    public static String getShortUser() {
        return getSession().getUser().getShortUser();
    }

    //TODO add ctf member check
    public static boolean isCTFer() {
        return false;
    }

    /**
     * gets the user's preferred nick from the Session object
     *
     * @return user's nick
     */
    public static String getNick() {
        return getSession().getUser().getNick();
    }

    /**
     * rather than reauth and get a new Session after a successfully changing the nick on TEPID
     * we just store the new nick in our current Session object
     *
     * @param nick nickname
     */
    public static void updateNick(String nick) {
        Session session = getSession();
        User user = session.getUser();
        user.setNick(nick);
        am.setUserData(account, KEY_SESSION, new Gson().toJson(session));
    }

    /**
     * removes the CTFAccount from android's account manager
     * used before logout to remove the account from the system
     */
    public static void removeAccount(final GeneralCallback callback) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            am.removeAccountExplicitly(account);
            callback.onFinish();
        } else {
            AccountManagerCallback<Boolean> handler = future -> callback.onFinish();
            am.removeAccount(account, handler, null);
        }
    }

    public static void requestAccount(Activity activity, AccountManagerCallback<Bundle> callback) {
        AccountManager.get(activity).addAccount(getAccountType(activity), getTokenType(activity), null, null, activity, callback, null);
    }

    public interface GeneralCallback {
        void onFinish();
    }

    public interface TokenRequestCallback {
        void onReceived(@NonNull String token);

        void onFailed();
    }

    public static void requestToken(Activity activity, @NonNull final TokenRequestCallback callback) {
        AccountManager.get(activity).getAuthToken(getAccount(), getTokenType(activity), null, activity, future -> {
            try {
                String token = future.getResult().getString(AccountManager.KEY_AUTHTOKEN);
                if (token == null || token.isEmpty()) {
                    CLog.e("empty token received");
                    callback.onFailed();
                } else
                    callback.onReceived(token.trim());
            } catch (Exception e) {
                CLog.e("Failed to request token %s", e.getMessage());
                callback.onFailed();
            }
        }, null);
    }
}