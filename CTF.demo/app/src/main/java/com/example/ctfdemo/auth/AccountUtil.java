package com.example.ctfdemo.auth;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;

import com.example.ctfdemo.CTFApp;
import com.example.ctfdemo.R;
import com.example.ctfdemo.tepid.LdapUser;
import com.example.ctfdemo.tepid.Session;
import com.google.gson.Gson;

// todo find a better way to pass around a Context in here. this will probs give npe or memory leaks eventually...
public class AccountUtil {
    private static AccountManager am;
    private static Account account;
    public static final String accountType = CTFApp.getAppContext().getResources().getString(R.string.authenticator_account_type);
    public static final String tokenType = CTFApp.getAppContext().getResources().getString(R.string.authenticator_token_type);
    public static final String KEY_SESSION = "SESSION";
    private static Context context;

    /**
     *  method to init variables needed for other components to request auth token
     *  assumes an account is already present on device. components that use this should
     *  do a null check on getAccount() and call am.addAccount() themselves as necessary
     */
    //todo should change this param to Activity
    public static void initAccount(Context context) {
        AccountUtil.context = context;
        am = AccountManager.get(context);
        if (am.getAccountsByType(accountType).length > 0) {
            // get account
            account = am.getAccountsByType(accountType)[0];
        }
    }

    public static Account getAccount() {
        return account;
    }

    public static boolean isSignedIn() {
        if (getAccount() != null) {
            return true;
        }
        return false;
    }

    public static String getUsername() {
        String s = am.getUserData(account, AccountUtil.KEY_SESSION);
        Session session = new Gson().fromJson(s, Session.class);
        return session.getUser().shortUser;
    }

    public static String getNick() {
        String s = am.getUserData(account, AccountUtil.KEY_SESSION);
        Session session = new Gson().fromJson(s, Session.class);
        return session.getUser().nick;
    }

    public static void updateNick(String nick) {
        Session session = new Gson().fromJson(am.getUserData(account, AccountUtil.KEY_SESSION), Session.class);
        LdapUser user = session.getUser();
        user.nick = nick;
        session.setUser(user);
        am.setUserData(account, KEY_SESSION, new Gson().toJson(session));
    }

    public static void removeAccount() {
        //todo LogoutRequest to invalidate token, need to save session id in LoginActivity and retrieve from am here
        am.removeAccountExplicitly(account);
    }
}