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

import com.github.wolandmaster.uniquepasswords.R;
import com.github.wolandmaster.uniquepasswords.interfaces.Supplier;

import java.util.UUID;

public class Account {

    private String mId = UUID.randomUUID().toString();
    private String mDomain = "";
    private String mUsername = "";
    private String mPasswordLength = "";
    private String mHashAlgorithm = "";
    private String mUsedCharacters = "";

    private Supplier<String> mStringRepresentation = this::getDomain;

    public Account() {
    }

    public Account(final Account account) {
        setId(account.getId());
        setDomain(account.getDomain());
        setUsername(account.getUsername());
        setPasswordLength(account.getPasswordLength());
        setHashAlgorithm(account.getHashAlgorithm());
        setUsedCharacters(account.getUsedCharacters());
        setStringRepresentation(account.getStringRepresentation());
    }

    public String getId() {
        return mId;
    }

    public Account setId(final String id) {
        mId = id;
        return this;
    }

    public String getDomain() {
        return mDomain;
    }

    public Account setDomain(final String domain) {
        mDomain = domain;
        return this;
    }

    public String getUsername() {
        return mUsername;
    }

    public Account setUsername(final String username) {
        mUsername = username;
        return this;
    }

    public String getPasswordLength() {
        return mPasswordLength;
    }

    public Account setPasswordLength(final String passwordLength) {
        mPasswordLength = passwordLength;
        return this;
    }

    public String getHashAlgorithm() {
        return mHashAlgorithm;
    }

    public Account setHashAlgorithm(final String hashAlgorithm) {
        mHashAlgorithm = hashAlgorithm;
        return this;
    }

    public String getUsedCharacters() {
        return mUsedCharacters;
    }

    public Account setUsedCharacters(final String usedCharacters) {
        mUsedCharacters = usedCharacters;
        return this;
    }

    public Supplier<String> getStringRepresentation() {
        return mStringRepresentation;
    }

    public Account setStringRepresentation(final Supplier<String> stringRepresentation) {
        mStringRepresentation = stringRepresentation;
        return this;
    }

    public Account setByKey(final int key, final String value) {
        switch (key) {
            case R.string.key_account_id:
                return setId(value);
            case R.string.key_domain:
                return setDomain(value);
            case R.string.key_username:
                return setUsername(value);
            case R.string.key_password_length:
                return setPasswordLength(value);
            case R.string.key_hash_algorithm:
                return setHashAlgorithm(value);
            case R.string.key_used_characters:
                return setUsedCharacters(value);
            default:
                return this;
        }
    }

    @Override
    public String toString() {
        return mStringRepresentation.get();
    }

}
