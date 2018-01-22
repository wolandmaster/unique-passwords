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

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;

import com.github.wolandmaster.uniquepasswords.R;
import com.github.wolandmaster.uniquepasswords.beans.CachedAccounts;
import com.github.wolandmaster.uniquepasswords.utils.HashUtils;

import java.util.Arrays;

import static com.github.wolandmaster.uniquepasswords.R.string.key_cached_accounts;
import static com.github.wolandmaster.uniquepasswords.R.string.key_char_lowercase;
import static com.github.wolandmaster.uniquepasswords.R.string.key_char_number;
import static com.github.wolandmaster.uniquepasswords.R.string.key_char_special;
import static com.github.wolandmaster.uniquepasswords.R.string.key_char_uppercase;
import static com.github.wolandmaster.uniquepasswords.R.string.key_hash_algorithm;
import static com.github.wolandmaster.uniquepasswords.R.string.key_password_length;
import static com.github.wolandmaster.uniquepasswords.utils.PreferenceUtils.matchKey;

public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final Preference.OnPreferenceClickListener ADD_ACCOUNT_ON_CLICK = preference -> {
        // onPreferenceStartFragment(null, newAccount);

//        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(preference.getContext());
//        new CachedAccounts()
//                .load(preference.getContext())
//                .add(new Account()
//                        .setDomain("")
//                        .setUsername("")
//                        .setPasswordLength(String.valueOf(sharedPreferences.getInt(preference.getContext().getString(key_password_length), preference.getContext().getResources().getInteger(R.integer.default_password_length))))
//                        .setHashAlgorithm(sharedPreferences.getString(preference.getContext().getString(key_hash_algorithm), preference.getContext().getString(default_hash_algorithm)))
//                        .setUsedCharacters(TextUtils.join("", Arrays.asList(
//                                sharedPreferences.getString(preference.getContext().getString(key_char_lowercase), "?"),
//                                sharedPreferences.getString(preference.getContext().getString(key_char_number), "?"),
//                                sharedPreferences.getString(preference.getContext().getString(key_char_uppercase), "?"),
//                                sharedPreferences.getString(preference.getContext().getString(key_char_special), "?")))))
//                .sort()
//                .save(preference.getContext());

        return true;
    };

    private Context mContext;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_settings);

        final SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();
        for (final int keyId : Arrays.asList(key_password_length, key_hash_algorithm, key_char_lowercase,
                key_char_uppercase, key_char_number, key_char_special, key_cached_accounts)) {
            onSharedPreferenceChanged(sharedPreferences, getString(keyId));
        }
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);

        final ListPreference hashAlgorithm = (ListPreference) findPreference(getString(key_hash_algorithm));
        final String[] hashers = HashUtils.getHashers();
        hashAlgorithm.setEntries(hashers);
        hashAlgorithm.setEntryValues(hashers);
        hashAlgorithm.setValueIndex(0);
    }

    @Override
    public void onSharedPreferenceChanged(final SharedPreferences sharedPreferences, final String key) {
        if (isAdded()) {
            if (matchKey(mContext, key,
                    key_password_length)) {
                findPreference(key).setSummary(sharedPreferences.getInt(key, 0)
                        + " " + getString(R.string.text_characters));
            } else if (matchKey(mContext, key,
                    key_hash_algorithm,
                    key_char_lowercase,
                    key_char_uppercase,
                    key_char_number,
                    key_char_special)) {
                findPreference(key).setSummary(sharedPreferences.getString(key, ""));
            } else if (matchKey(mContext, key,
                    key_cached_accounts)) {
                onCachedAccountsChanged();
            }
        }
    }

    @Override
    public void onAttach(final Context context) {
        super.onAttach(context);
        mContext = context;
    }

    private void onCachedAccountsChanged() {
        final PreferenceCategory cachedAccountsPreference = (PreferenceCategory)
                findPreference(getString(key_cached_accounts));
        cachedAccountsPreference.removeAll();
        new CachedAccounts()
                .load(mContext)
                .forEach(account -> {
                    final Preference preference = new Preference(mContext);
                    preference.setTitle(account.getDomain());
                    preference.setSummary(account.getUsername());
                    preference.getExtras().putString(mContext.getString(R.string.key_account_id), account.getId());
                    preference.setFragment(CachedAccountFragment.class.getName());
                    cachedAccountsPreference.addPreference(preference);
//                    if ("".equals(account.getDomain())) {
//                    ((PreferenceActivity) getActivity()).onPreferenceStartFragment(null, preference);
//                    }
                });
//        final Preference addAccountPreference = new Preference(mContext);
//        addAccountPreference.setTitle(action_add_account);
//        addAccountPreference.setOnPreferenceClickListener(preference -> {
//            final Preference newAccount = new Preference(preference.getContext());
//            newAccount.getExtras().putString(mContext.getString(R.string.key_account_id), "");
//            newAccount.setFragment(CachedAccountFragment.class.getName());
//            ((PreferenceActivity) getActivity()).onPreferenceStartFragment(null, newAccount);
//            return false;
//        });
//        cachedAccountsPreference.addPreference(addAccountPreference);
    }

}
