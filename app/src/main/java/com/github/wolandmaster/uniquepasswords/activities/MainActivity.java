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

package com.github.wolandmaster.uniquepasswords.activities;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.wolandmaster.uniquepasswords.R;
import com.github.wolandmaster.uniquepasswords.beans.Account;
import com.github.wolandmaster.uniquepasswords.beans.CachedAccounts;
import com.github.wolandmaster.uniquepasswords.beans.UsedCharacters;
import com.github.wolandmaster.uniquepasswords.interfaces.SimpleTextWatcher;
import com.github.wolandmaster.uniquepasswords.utils.HashUtils;
import com.github.wolandmaster.uniquepasswords.widgets.LockableScrollView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static android.R.layout.select_dialog_item;
import static android.text.InputType.TYPE_CLASS_TEXT;
import static android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD;
import static android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.github.wolandmaster.uniquepasswords.R.integer.default_password_length;
import static com.github.wolandmaster.uniquepasswords.R.string.default_char_lowercase;
import static com.github.wolandmaster.uniquepasswords.R.string.default_char_number;
import static com.github.wolandmaster.uniquepasswords.R.string.default_char_special;
import static com.github.wolandmaster.uniquepasswords.R.string.default_char_uppercase;
import static com.github.wolandmaster.uniquepasswords.R.string.key_cached_accounts;
import static com.github.wolandmaster.uniquepasswords.R.string.key_char_lowercase;
import static com.github.wolandmaster.uniquepasswords.R.string.key_char_number;
import static com.github.wolandmaster.uniquepasswords.R.string.key_char_special;
import static com.github.wolandmaster.uniquepasswords.R.string.key_char_uppercase;
import static com.github.wolandmaster.uniquepasswords.R.string.key_hash_algorithm;
import static com.github.wolandmaster.uniquepasswords.R.string.key_password_length;
import static com.github.wolandmaster.uniquepasswords.R.string.key_randomize_input;
import static com.github.wolandmaster.uniquepasswords.R.string.key_randomize_output;
import static com.github.wolandmaster.uniquepasswords.R.string.title_hash_algorithm;
import static com.github.wolandmaster.uniquepasswords.R.string.title_used_characters;
import static com.github.wolandmaster.uniquepasswords.utils.PreferenceUtils.matchKey;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final int RANDOMIZE_STRING_REQUEST_CODE = 1;

    private LockableScrollView mScrollView;
    private AutoCompleteTextView mDomain;
    private AutoCompleteTextView mUsername;
    private TextInputLayout mMasterPasswordLayout;
    private EditText mMasterPassword;
    private TextView mAdvancedOptionsShow;
    private RelativeLayout mAdvancedOptionsLayout;
    private EditText mPasswordLength;
    private EditText mHashAlgorithm;
    private EditText mUsedCharacters;
    private TextView mPassword;

    private SharedPreferences mSharedPreferences;
    private CachedAccounts mCachedAccounts;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (mSharedPreferences.getBoolean(getString(R.string.key_first_run), true)) {
            final Intent intent = new Intent(this, RandomizeStringActivity.class);
            intent.putStringArrayListExtra(getString(key_randomize_input), new ArrayList<>(Arrays.asList(
                    getString(default_char_lowercase),
                    getString(default_char_uppercase),
                    getString(default_char_number),
                    getString(default_char_special))));
            startActivityForResult(intent, RANDOMIZE_STRING_REQUEST_CODE);
        }

        mScrollView = (LockableScrollView) findViewById(R.id.scroll_view);
        mDomain = (AutoCompleteTextView) findViewById(R.id.domain);
        mUsername = (AutoCompleteTextView) findViewById(R.id.username);
        mMasterPasswordLayout = (TextInputLayout) findViewById(R.id.master_password_text_layout);
        mMasterPassword = (EditText) findViewById(R.id.master_password);
        mAdvancedOptionsShow = (TextView) findViewById(R.id.advanced_options_show);
        mAdvancedOptionsLayout = (RelativeLayout) findViewById(R.id.advanced_options_layout);
        mPasswordLength = (EditText) findViewById(R.id.password_length);
        mHashAlgorithm = (EditText) findViewById(R.id.hash_algorithm);
        mUsedCharacters = (EditText) findViewById(R.id.used_characters);
        mPassword = (TextView) findViewById(R.id.password);

        mSharedPreferences.registerOnSharedPreferenceChangeListener(this);
        mCachedAccounts = new CachedAccounts();

        setupDomain();
        setupUsername();
        setupMasterPassword();
        setupAdvancedOptionsShow();
        setupPasswordLength();
        setupHashAlgorithm();
        setupUsedCharacters();
        setupPassword();
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        final int id = item.getItemId();
        if (id == R.id.settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSharedPreferenceChanged(final SharedPreferences sharedPreferences, final String key) {
        if (matchKey(this, key,
                key_password_length)) {
            onDefaultPasswordLengthChanged();
        } else if (matchKey(this, key,
                key_hash_algorithm)) {
            onDefaultHashAlgorithmChanged();
        } else if (matchKey(this, key,
                key_cached_accounts)) {
            onCachedAccountsChanged();
        }
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        if (requestCode == RANDOMIZE_STRING_REQUEST_CODE && resultCode == RESULT_OK) {
            final List<String> result = data.getStringArrayListExtra(getString(key_randomize_output));
            new AlertDialog.Builder(this)
                    .setTitle("Random")
                    .setMessage(TextUtils.join("\n", result))
                    .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    })
                    .setNegativeButton(android.R.string.cancel, null)
                    .show();

            final SharedPreferences.Editor sharedPreferencesEditor = mSharedPreferences.edit();
            sharedPreferencesEditor.putString(getString(key_char_lowercase), result.get(0));
            sharedPreferencesEditor.putString(getString(key_char_uppercase), result.get(1));
            sharedPreferencesEditor.putString(getString(key_char_number), result.get(2));
            sharedPreferencesEditor.putString(getString(key_char_special), result.get(3));
            sharedPreferencesEditor.putBoolean(getString(R.string.key_first_run), false);
            sharedPreferencesEditor.commit();
        }
    }

    private void onDefaultPasswordLengthChanged() {
        mPasswordLength.setText(String.valueOf(mSharedPreferences.getInt(
                getString(key_password_length), getResources().getInteger(default_password_length))));
    }

    private void onDefaultHashAlgorithmChanged() {
        mHashAlgorithm.setText(mSharedPreferences.getString(
                getString(key_hash_algorithm), HashUtils.getDefaultHasher()));
    }

    private void onDefaultUsedCharactersChanged() {
        mUsedCharacters.setText(new UsedCharacters(this).toString());
//        mUsedCharacters.setText(TextUtils.join("", Arrays.asList(
//                mSharedPreferences.getString(getString(key_char_lowercase), getString(default_char_lowercase)),
//                mSharedPreferences.getString(getString(key_char_number), getString(default_char_number)),
//                mSharedPreferences.getString(getString(key_char_uppercase), getString(default_char_uppercase)),
//                mSharedPreferences.getString(getString(key_char_special), getString(default_char_special)))));
    }

    private void onCachedAccountsChanged() {
        mCachedAccounts
                .load(this)
                .clone()
                .distinct(Account::getDomain)
                .applyTo(mDomain);
    }

    private void setupDomain() {
        mDomain.setThreshold(1);
        onCachedAccountsChanged();
        mDomain.setOnItemClickListener((parent, view, position, id) -> mCachedAccounts
                .clone()
                .filter(account -> account.getDomain().equals((
                        (Account) mDomain.getAdapter().getItem(position)).getDomain()))
                .forEach(account -> account.setStringRepresentation(account::getUsername))
                .applyTo(mUsername)
                .onEmpty(() -> {
                    mUsername.setText("");
                    mUsername.requestFocus();
                })
                .onSingle(account -> {
                    mUsername.setText(account.getUsername());
                    mPasswordLength.setText(account.getPasswordLength());
                    mHashAlgorithm.setText(account.getHashAlgorithm());
                    mUsedCharacters.setText(account.getUsedCharacters());
                    mMasterPassword.requestFocus();
                })
                .onMultiple(accounts -> {
                    mUsername.showDropDown();
                    mUsername.requestFocus();
                }));
        mDomain.addTextChangedListener((SimpleTextWatcher) (s, start, before, count) -> {
            mUsername.setAdapter(new ArrayAdapter<>(this, select_dialog_item, Collections.emptyList()));
            onDefaultPasswordLengthChanged();
            onDefaultHashAlgorithmChanged();
            onDefaultUsedCharactersChanged();
        });
    }

    private void setupUsername() {
        mUsername.setThreshold(1);
        mUsername.setOnItemClickListener((parent, view, position, id) -> mCachedAccounts
                .clone()
                .filter(account -> account.getDomain().equals(mDomain.getText().toString()))
                .filter(account -> account.getUsername().equals((
                        (Account) mUsername.getAdapter().getItem(position)).getUsername()))
                .onSingle(account -> {
                    mPasswordLength.setText(account.getPasswordLength());
                    mHashAlgorithm.setText(account.getHashAlgorithm());
                    mUsedCharacters.setText(account.getUsedCharacters());
                    mMasterPassword.requestFocus();
                }));
    }

    private void setupMasterPassword() {
        findViewById(R.id.master_password_show).setOnTouchListener((view, event) -> {
            mMasterPassword.requestFocus();
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mScrollView.setScrollingEnabled(false);
                    mMasterPassword.setInputType(TYPE_CLASS_TEXT | TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    break;
                case MotionEvent.ACTION_UP:
                    mMasterPassword.setInputType(TYPE_CLASS_TEXT | TYPE_TEXT_VARIATION_PASSWORD);
                    mScrollView.setScrollingEnabled(true);
                    view.performClick();
                    break;
            }
            return true;
        });
        mMasterPassword.addTextChangedListener((SimpleTextWatcher) (s, start, before, count) -> {
            mMasterPasswordLayout.setError(null);
            mMasterPasswordLayout.setErrorEnabled(false);
        });
    }

    private void setupAdvancedOptionsShow() {
        mAdvancedOptionsShow.setOnClickListener(view -> {
            mAdvancedOptionsShow.setVisibility(GONE);
            mAdvancedOptionsLayout.setVisibility(VISIBLE);
        });
    }

    private void setupPasswordLength() {
        onDefaultPasswordLengthChanged();
        final View dialogView = getLayoutInflater().inflate(R.layout.dialog_number_picker, null);
        final NumberPicker numberPicker = (NumberPicker) dialogView.findViewById(R.id.password_length);
        final AlertDialog numberPickerDialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .setTitle(R.string.title_password_length)
                .setPositiveButton(android.R.string.ok, (dialog, which) ->
                        mPasswordLength.setText(String.valueOf(numberPicker.getValue())))
                .setNegativeButton(android.R.string.cancel, null)
                .create();
        mPasswordLength.setKeyListener(null);
        mPasswordLength.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) view.callOnClick();
        });
        mPasswordLength.setOnClickListener(view -> {
            hideKeyboard();
            numberPicker.setValue(Integer.parseInt(mPasswordLength.getText().toString()));
            numberPickerDialog.show();
        });
    }

    private void setupHashAlgorithm() {
        onDefaultHashAlgorithmChanged();
        final AlertDialog hashAlgorithmDialog = new AlertDialog.Builder(this)
                .setSingleChoiceItems(HashUtils.getHashers(), 0, null)
                .setTitle(title_hash_algorithm)
                .setPositiveButton(android.R.string.ok, (dialog, itemIndex) -> {
                    mHashAlgorithm.setText(HashUtils.getHashers()[
                            ((AlertDialog) dialog).getListView().getCheckedItemPosition()]);
                })
                .setNegativeButton(android.R.string.cancel, null)
                .create();
        mHashAlgorithm.setKeyListener(null);
        mHashAlgorithm.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) view.callOnClick();
        });
        mHashAlgorithm.setOnClickListener(view -> {
            hideKeyboard();
            hashAlgorithmDialog.getListView().setItemChecked(Arrays.asList(HashUtils.getHashers())
                    .indexOf(mHashAlgorithm.getText().toString()), true);
            hashAlgorithmDialog.show();
        });
    }

    private void setupUsedCharacters() {
        onDefaultUsedCharactersChanged();
        final UsedCharacters characters = new UsedCharacters(this);
        final AlertDialog usedCharactersDialog = new AlertDialog.Builder(this)
                .setMultiChoiceItems(characters.getItems(), characters.getCheckedItems(), (dialog, itemIndex, checked) ->
                        characters.setChecked(itemIndex, checked))
                .setTitle(title_used_characters)
                .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    mUsedCharacters.setText(characters.toString());
                })
                .setNegativeButton(android.R.string.cancel, null)
                .create();
        mUsedCharacters.setKeyListener(null);
        mUsedCharacters.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) view.callOnClick();
        });
        mUsedCharacters.setOnClickListener(view -> {
            hideKeyboard();
            characters.fromString(mUsedCharacters.getText().toString()).applyTo(usedCharactersDialog);
            usedCharactersDialog.show();
        });
    }

    private void setupPassword() {
        mPassword.setOnClickListener(view -> {
            if (TextUtils.isEmpty(mMasterPassword.getText())) {
                mScrollView.scrollTo(0, mMasterPasswordLayout.getBottom());
                mMasterPasswordLayout.setErrorEnabled(true);
                mMasterPasswordLayout.setError(getString(R.string.error_field_empty));
                mMasterPassword.requestFocus();
                mPassword.setText("");
            } else {
                mMasterPasswordLayout.setError(null);
                hideKeyboard();
                generatePassword();
                saveAccountToCache();
            }
        });
        mPassword.setOnLongClickListener(view -> {
            mPassword.callOnClick();
            final ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            clipboard.setPrimaryClip(ClipData.newPlainText("text", mPassword.getText().toString()));
            Toast.makeText(MainActivity.this, R.string.msg_copied_to_clipboard, Toast.LENGTH_SHORT).show();
            return true;
        });
    }

    private void generatePassword() {
        final byte[] hash = HashUtils.getHasher(mHashAlgorithm.getText().toString())
                .hash(mMasterPassword.getText().toString(), mDomain.getText().toString() + mUsername.getText());

        final char[] charTable = new UsedCharacters(this)
                .fromString(mUsedCharacters.getText().toString()).toCharTable();
        final int passwordLength = Integer.parseInt(mPasswordLength.getText().toString());
        mPassword.setText(HashUtils.hash2chars(hash, charTable, passwordLength));
    }

    private void hideKeyboard() {
        final View currentFocus = getCurrentFocus();
        if (currentFocus != null) {
            final InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
        }
    }

    private void saveAccountToCache() {
        if (mDomain.getText().toString().matches(".+\\.\\w{2,3}$")) {
            mCachedAccounts
                    .filter(account -> !account.getDomain().equals(mDomain.getText().toString())
                            || !account.getUsername().equals(mUsername.getText().toString()))
                    .add(new Account()
                            .setDomain(mDomain.getText().toString())
                            .setUsername(mUsername.getText().toString())
                            .setPasswordLength(mPasswordLength.getText().toString())
                            .setHashAlgorithm(mHashAlgorithm.getText().toString())
                            .setUsedCharacters(mUsedCharacters.getText().toString()))
                    .sort()
                    .save(this);
        }
    }

}
