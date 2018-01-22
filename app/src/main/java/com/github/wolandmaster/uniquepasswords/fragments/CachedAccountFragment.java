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

package com.github.wolandmaster.uniquepasswords.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.github.wolandmaster.uniquepasswords.R;
import com.github.wolandmaster.uniquepasswords.beans.Account;
import com.github.wolandmaster.uniquepasswords.beans.CachedAccounts;
import com.github.wolandmaster.uniquepasswords.utils.HashUtils;
import com.github.wolandmaster.uniquepasswords.widgets.NumberPickerPreference;
import com.github.wolandmaster.uniquepasswords.widgets.ValidatableEditTextPreference;

import java.util.Arrays;

import static com.github.wolandmaster.uniquepasswords.R.string.key_account_element;
import static com.github.wolandmaster.uniquepasswords.R.string.key_account_id;
import static com.github.wolandmaster.uniquepasswords.R.string.key_char_lowercase;
import static com.github.wolandmaster.uniquepasswords.R.string.key_char_number;
import static com.github.wolandmaster.uniquepasswords.R.string.key_char_special;
import static com.github.wolandmaster.uniquepasswords.R.string.key_char_uppercase;
import static com.github.wolandmaster.uniquepasswords.R.string.key_delete_account;
import static com.github.wolandmaster.uniquepasswords.R.string.key_domain;
import static com.github.wolandmaster.uniquepasswords.R.string.key_hash_algorithm;
import static com.github.wolandmaster.uniquepasswords.R.string.key_password_length;
import static com.github.wolandmaster.uniquepasswords.R.string.key_used_characters;
import static com.github.wolandmaster.uniquepasswords.R.string.key_username;
import static com.github.wolandmaster.uniquepasswords.R.string.text_characters;

public class CachedAccountFragment extends PreferenceFragment {

    private static final Preference.OnPreferenceChangeListener SET_VALUE_ON_CHANGE = (preference, value) -> {
        new CachedAccounts()
                .load(preference.getContext())
                .onSpecific(
                        account -> account.getId().equals(
                                preference.getExtras().getString(preference.getContext().getString(key_account_id))),
                        account -> account.setByKey(
                                preference.getExtras().getInt(preference.getContext().getString(key_account_element)),
                                value.toString()))
                .save(preference.getContext());
        preference.setSummary(value.toString());
        return true;
    };

    private static final Preference.OnPreferenceClickListener DELETE_ACCOUNT_ON_CLICK = preference -> {
        new AlertDialog.Builder(preference.getContext())
                .setMessage(R.string.action_delete_account_confirm)
                .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    new CachedAccounts()
                            .load(preference.getContext())
                            .filter(account -> !account.getId().equals(
                                    preference.getExtras().getString(preference.getContext().getString(key_account_id))))
                            .save(preference.getContext());
                    ((Activity) preference.getContext()).finish();
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
        return true;
    };

    private Context mContext;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_cached_account);
        setHasOptionsMenu(true);
        final String accountId = getArguments().getString(mContext.getString(key_account_id));
        if (!"".equals(accountId)) {
            new CachedAccounts()
                    .load(mContext)
                    .filter(account -> account.getId().equals(accountId))
                    .onSingle(account -> {
                        setupDomain(account);
                        setupUsername(account);
                        setupPasswordLength(account);
                        setupHashAlgorithm(account);
                        setupUsedCharacters(account);
                        setupDeleteAccount(account);
                    });
        } else {
            final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
            final Account account = new Account()
                    .setPasswordLength(String.valueOf(sharedPreferences.getInt(mContext.getString(key_password_length), mContext.getResources().getInteger(R.integer.default_password_length))))
                    .setHashAlgorithm(sharedPreferences.getString(mContext.getString(key_hash_algorithm), HashUtils.getDefaultHasher()))
                    .setUsedCharacters(TextUtils.join("", Arrays.asList(
                            sharedPreferences.getString(mContext.getString(key_char_lowercase), "?"),
                            sharedPreferences.getString(mContext.getString(key_char_number), "?"),
                            sharedPreferences.getString(mContext.getString(key_char_uppercase), "?"),
                            sharedPreferences.getString(mContext.getString(key_char_special), "?"))));
            setupDomain(account);
            setupUsername(account);
            setupPasswordLength(account);
            setupHashAlgorithm(account);
            setupUsedCharacters(account);
            setupDeleteAccount(account);
        }
    }

    @Override
    public void onAttach(final Context context) {
        super.onAttach(context);
        mContext = context;
    }

    private void setupDomain(final Account account) {
        final ValidatableEditTextPreference domain = (ValidatableEditTextPreference) findPreference(mContext.getString(key_domain));
        domain.getExtras().putString(mContext.getString(key_account_id), account.getId());
        domain.getExtras().putInt(mContext.getString(key_account_element), key_domain);
        domain.setOnPreferenceChangeListener(SET_VALUE_ON_CHANGE);
        domain.setSummary(account.getDomain());
        domain.setText(account.getDomain());
        domain.setValidator(value -> !"".equals(value.trim()));
        domain.setOnDialogClosedListener(positiveResult -> {
            if (positiveResult) {
                new CachedAccounts()
                        .load(mContext)
                        .add(account)
                        .save(mContext);
            }
        });
        if ("".equals(account.getDomain().trim())) {
            domain.showDialog(null);
        }
    }

    private void setupUsername(final Account account) {
        final EditTextPreference username = (EditTextPreference) findPreference(mContext.getString(key_username));
        username.getExtras().putString(mContext.getString(key_account_id), account.getId());
        username.getExtras().putInt(mContext.getString(key_account_element), key_username);
        username.setOnPreferenceChangeListener(SET_VALUE_ON_CHANGE);
        username.setSummary(account.getUsername());
        username.setText(account.getUsername());
    }

    private void setupPasswordLength(final Account account) {
        final NumberPickerPreference passwordLength = (NumberPickerPreference) findPreference(mContext.getString(key_password_length));
        passwordLength.getExtras().putString(mContext.getString(key_account_id), account.getId());
        passwordLength.getExtras().putInt(mContext.getString(key_account_element), key_password_length);
        passwordLength.setOnPreferenceChangeListener(SET_VALUE_ON_CHANGE);
        passwordLength.setSummary(account.getPasswordLength() + " " + mContext.getString(text_characters));
        passwordLength.setValue(Integer.parseInt(account.getPasswordLength()));
    }

    private void setupHashAlgorithm(final Account account) {
        final ListPreference hashAlgorithm = (ListPreference) findPreference(mContext.getString(key_hash_algorithm));
        hashAlgorithm.getExtras().putString(mContext.getString(key_account_id), account.getId());
        hashAlgorithm.getExtras().putInt(mContext.getString(key_account_element), key_hash_algorithm);
        hashAlgorithm.setOnPreferenceChangeListener(SET_VALUE_ON_CHANGE);
        hashAlgorithm.setSummary(account.getHashAlgorithm());
        hashAlgorithm.setEntries(HashUtils.getHashers());
        hashAlgorithm.setEntryValues(HashUtils.getHashers());
        hashAlgorithm.setValue(account.getHashAlgorithm());
    }

    private void setupUsedCharacters(final Account account) {
        final EditTextPreference usedCharacters = (EditTextPreference) findPreference(mContext.getString(key_used_characters));
        usedCharacters.getExtras().putString(mContext.getString(key_account_id), account.getId());
        usedCharacters.getExtras().putInt(mContext.getString(key_account_element), key_used_characters);
        usedCharacters.setOnPreferenceChangeListener(SET_VALUE_ON_CHANGE);
        usedCharacters.setSummary(account.getUsedCharacters());
        usedCharacters.setText(account.getUsedCharacters());
    }

    private void setupDeleteAccount(final Account account) {
        final Preference deleteAccount = findPreference(mContext.getString(key_delete_account));
        deleteAccount.getExtras().putString(mContext.getString(key_account_id), account.getId());
        deleteAccount.setOnPreferenceClickListener(DELETE_ACCOUNT_ON_CLICK);
    }

}
