package com.yyong.middleware.account;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.zero.support.common.AppGlobal;
import com.zero.support.common.util.Preferences;
import com.zero.support.common.util.Singleton;
import com.zero.support.work.AppExecutor;
import com.zero.support.work.Observable;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class AccountManager {
    private static Singleton<AccountManager> singleton = new Singleton<AccountManager>() {
        @Override
        protected AccountManager create() {
            return new AccountManager(AppGlobal.getApplication());
        }
    };

    public static AccountManager getDefault() {
        return singleton.get();
    }

    private File root;
    private File dataDir;
    private final Preferences preferences;
    private final Map<String, Account> accounts = new HashMap<>();
    private static final Account EMPTY_ACCOUNT = new Account();

    public AccountManager(Context context) {
        root = new File(context.getFilesDir(), "account");
        dataDir = new File(root, "data");
        preferences = new Preferences(new File(root, "accounts.json"), false, false);
        init();
    }

    private void init() {
        AppExecutor.async().execute(new Runnable() {
            @Override
            public void run() {
                Set<String> list = preferences.getStringSet("list", Collections.<String>emptySet());
                String current = preferences.getString("current", null);
                Gson gson = new Gson();
                synchronized (accounts) {
                    for (String json : list) {
                        Account account = gson.fromJson(json, Account.class);
                        accounts.put(account.rid, account);
                    }
                    if (current != null) {
                        Account account = accounts.get(current);
                        if (account != null) {
                            AccountManager.this.current.setValue(account);
                        } else {
                            AccountManager.this.current.setValue(EMPTY_ACCOUNT);
                        }
                    } else {
                        AccountManager.this.current.setValue(EMPTY_ACCOUNT);
                    }
                }


            }
        });
    }

    private final Observable<Account> current = new Observable<>();

    public Observable<Account> currentAccount() {
        return current;
    }

    public void clear() {
        synchronized (accounts) {
            current.setValue(EMPTY_ACCOUNT);
            accounts.clear();
            preferences.edit().clear().apply();
        }
    }

    public final Observable<Account> loginOut() {
        return loginOut(EMPTY_ACCOUNT);
    }

    final Observable<Account> loginOut(Account account) {
        synchronized (accounts) {
            SharedPreferences.Editor editor = preferences.edit();
            current.setValue(EMPTY_ACCOUNT);
            editor.putString("current", null).apply();
        }
        return current;
    }

    public final Observable<Account> login(Account account) {
        synchronized (accounts) {
            String current;
            SharedPreferences.Editor editor = preferences.edit();
            if (account == null) {
                current = null;
            } else {
                current = account.rid;
                if (accounts.containsKey(account.rid) && accounts.get(account.rid) == account) {

                } else {
                    accounts.put(account.rid, account);
                    Set<String> set = new LinkedHashSet<>();
                    Gson gson = new Gson();
                    for (Account a : accounts.values()) {
                        set.add(gson.toJson(a));
                    }
                    editor.putStringSet("list", set);
                }
            }
            editor.putString("current", current).apply();
        }
        current.setValue(account);
        return current;
    }
}
