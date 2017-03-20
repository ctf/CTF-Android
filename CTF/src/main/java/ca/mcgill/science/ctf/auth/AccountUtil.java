package ca.mcgill.science.ctf.auth;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.google.gson.Gson;

import ca.mcgill.science.ctf.CTFApp;
import ca.mcgill.science.ctf.R;
import ca.mcgill.science.ctf.api.Session;
import ca.mcgill.science.ctf.api.User;

/**
 * utility class for operations relating to the CTFAccount stored in android's account manager
 */
public class AccountUtil {

    private static AccountManager am;
    private static Account account;
    public static final String accountType = CTFApp.getAppContext().getResources().getString(R.string.authenticator_account_type),
            tokenType = CTFApp.getAppContext().getResources().getString(R.string.authenticator_token_type),
            KEY_SESSION = "SESSION";

    /**
     * called on startup to keep a global reference to the user's account
     *
     * @param context likely MainActivity or CTFApp
     */
    public static void initAccount(Context context) {
        am = AccountManager.get(context);
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
     * @param nick
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
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
    public static void removeAccount() {
        am.removeAccountExplicitly(account);
    }
}