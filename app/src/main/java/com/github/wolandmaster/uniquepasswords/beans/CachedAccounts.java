/*
 * Copyright (C) 2017-2018 Sandor Balazsi <sandor.balazsi@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of
 * the GNU General Public License as published by the Free Software Foundation, either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 */

package com.github.wolandmaster.uniquepasswords.beans;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.android.internal.util.Predicate;
import com.github.wolandmaster.uniquepasswords.interfaces.Consumer;
import com.github.wolandmaster.uniquepasswords.interfaces.Function;
import com.github.wolandmaster.uniquepasswords.interfaces.Task;
import com.github.wolandmaster.uniquepasswords.utils.JSONUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static android.R.layout.select_dialog_item;
import static com.github.wolandmaster.uniquepasswords.R.string.key_account_id;
import static com.github.wolandmaster.uniquepasswords.R.string.key_cached_accounts;
import static com.github.wolandmaster.uniquepasswords.R.string.key_domain;
import static com.github.wolandmaster.uniquepasswords.R.string.key_hash_algorithm;
import static com.github.wolandmaster.uniquepasswords.R.string.key_password_length;
import static com.github.wolandmaster.uniquepasswords.R.string.key_used_characters;
import static com.github.wolandmaster.uniquepasswords.R.string.key_username;

public class CachedAccounts {

    private static final Comparator<Account> ACCOUNT_COMPARATOR = (left, right) -> {
        int result = left.getDomain().compareTo(right.getDomain());
        return (result == 0) ? left.getUsername().compareTo(right.getUsername()) : result;
    };

    private final List<Account> mAccounts = new ArrayList<>();

    public CachedAccounts() {
    }

    public CachedAccounts(final List<Account> accounts) {
        mAccounts.addAll(accounts);
    }

    public CachedAccounts load(final Context context) {
        synchronized (mAccounts) {
            try {
                mAccounts.clear();
                final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                final JSONArray cachedAccounts = new JSONArray(sharedPreferences.getString(
                        context.getString(key_cached_accounts), new JSONArray().toString()));
                for (int i = 0; i < cachedAccounts.length(); i++) {
                    final JSONObject account = cachedAccounts.getJSONObject(i);
                    mAccounts.add(new Account()
                            .setId(account.getString(context.getString(key_account_id)))
                            .setDomain(account.getString(context.getString(key_domain)))
                            .setUsername(account.getString(context.getString(key_username)))
                            .setPasswordLength(account.getString(context.getString(key_password_length)))
                            .setHashAlgorithm(account.getString(context.getString(key_hash_algorithm)))
                            .setUsedCharacters(account.getString(context.getString(key_used_characters))));
                }
            } catch (final JSONException e) {
                JSONUtils.handleException(context, e);
            }
        }
        return this;
    }

    public CachedAccounts save(final Context context) {
        synchronized (mAccounts) {
            try {
                final JSONArray cachedAccounts = new JSONArray();
                for (final Account account : mAccounts) {
                    cachedAccounts.put(new JSONObject()
                            .put(context.getString(key_account_id), account.getId())
                            .put(context.getString(key_domain), account.getDomain())
                            .put(context.getString(key_username), account.getUsername())
                            .put(context.getString(key_password_length), account.getPasswordLength())
                            .put(context.getString(key_hash_algorithm), account.getHashAlgorithm())
                            .put(context.getString(key_used_characters), account.getUsedCharacters()));
                }
                final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                final SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
                sharedPreferencesEditor.putString(context.getString(key_cached_accounts), cachedAccounts.toString());
                sharedPreferencesEditor.apply();
            } catch (final JSONException e) {
                JSONUtils.handleException(context, e);
            }
        }
        return this;
    }

    public CachedAccounts sort() {
        synchronized (mAccounts) {
            Collections.sort(mAccounts, ACCOUNT_COMPARATOR);
        }
        return this;
    }

    public CachedAccounts add(final Account account) {
        synchronized (mAccounts) {
            for (final Iterator<Account> iterator = mAccounts.iterator(); iterator.hasNext(); ) {
                final Account existingAccount = iterator.next();
                if (existingAccount.getId().equals(account.getId())) {
                    iterator.remove();
                }
            }
            mAccounts.add(account);
        }
        return this;
    }

    @Override
    public CachedAccounts clone() {
        final List<Account> clonedAccounts = new ArrayList<>();
        synchronized (mAccounts) {
            for (final Account account : mAccounts) {
                clonedAccounts.add(new Account(account));
            }
        }
        return new CachedAccounts(clonedAccounts);
    }

    public CachedAccounts filter(final Predicate<Account> filter) {
        synchronized (mAccounts) {
            final List<Account> filteredAccounts = new ArrayList<>();
            for (final Account account : mAccounts) {
                if (filter.apply(account)) {
                    filteredAccounts.add(account);
                }
            }
            mAccounts.clear();
            mAccounts.addAll(filteredAccounts);
        }
        return this;
    }

    public CachedAccounts distinct(final Function<Account, String> byField) {
        synchronized (mAccounts) {
            final Map<String, Account> distinctAccounts = new HashMap<>();
            for (final Account account : mAccounts) {
                final String fieldValue = byField.apply(account);
                if (!"".equals(fieldValue)) {
                    distinctAccounts.put(fieldValue, account);
                }
            }
            mAccounts.clear();
            mAccounts.addAll(distinctAccounts.values());
        }
        return this;
    }

    public CachedAccounts forEach(final Consumer<Account> action) {
        synchronized (mAccounts) {
            for (final Account account : mAccounts) {
                action.accept(account);
            }
        }
        return this;
    }

    public CachedAccounts onSpecific(final Predicate<Account> filter, final Consumer<Account> action) {
        synchronized (mAccounts) {
            for (final Account account : mAccounts) {
                if (filter.apply(account)) {
                    action.accept(account);
                }
            }
        }
        return this;
    }

    public CachedAccounts onEmpty(final Task task) {
        synchronized (mAccounts) {
            if (mAccounts.isEmpty()) {
                task.perform();
            }
        }
        return this;
    }

    public CachedAccounts onSingle(final Consumer<Account> account) {
        synchronized (mAccounts) {
            if (mAccounts.size() == 1) {
                account.accept(mAccounts.get(0));
            }
        }
        return this;
    }

    public CachedAccounts onMultiple(final Consumer<List<Account>> accounts) {
        synchronized (mAccounts) {
            if (mAccounts.size() > 1) {
                accounts.accept(mAccounts);
            }
        }
        return this;
    }

    public CachedAccounts applyTo(final AutoCompleteTextView textView) {
        synchronized (mAccounts) {
            textView.setAdapter(new ArrayAdapter<>(textView.getContext(), select_dialog_item, mAccounts));
        }
        return this;
    }

}
