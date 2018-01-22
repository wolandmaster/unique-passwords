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

package com.github.wolandmaster.uniquepasswords.utils;

import android.util.Log;

import com.github.wolandmaster.uniquepasswords.hashers.BCryptHasher;
import com.github.wolandmaster.uniquepasswords.hashers.Md5Hasher;
import com.github.wolandmaster.uniquepasswords.hashers.ShaHasher;
import com.github.wolandmaster.uniquepasswords.interfaces.Hasher;

import java.security.NoSuchAlgorithmException;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.github.wolandmaster.uniquepasswords.hashers.ShaHasher.Algorithm.SHA1;
import static com.github.wolandmaster.uniquepasswords.hashers.ShaHasher.Algorithm.SHA224;
import static com.github.wolandmaster.uniquepasswords.hashers.ShaHasher.Algorithm.SHA256;
import static com.github.wolandmaster.uniquepasswords.hashers.ShaHasher.Algorithm.SHA384;
import static com.github.wolandmaster.uniquepasswords.hashers.ShaHasher.Algorithm.SHA512;

public final class HashUtils {

    private static final Map<String, Hasher> HASHERS = new LinkedHashMap<>();

    static {
        try {
            HASHERS.put("sha-1", new ShaHasher(SHA1));
            HASHERS.put("sha-224", new ShaHasher(SHA224));
            HASHERS.put("sha-256", new ShaHasher(SHA256));
            HASHERS.put("sha-384", new ShaHasher(SHA384));
            HASHERS.put("sha-512", new ShaHasher(SHA512));
            HASHERS.put("md5", new Md5Hasher());
            HASHERS.put("bcrypt", new BCryptHasher());

        } catch (final NoSuchAlgorithmException e) {
            Log.i(HashUtils.class.getSimpleName(), "Not supported hasher", e);
        }
    }

    private HashUtils() {
    }

    public static String[] getHashers() {
        return HASHERS.keySet().toArray(new String[HASHERS.keySet().size()]);
    }

    public static String getDefaultHasher() {
        return HASHERS.entrySet().iterator().next().getKey();
    }

    public static Hasher getHasher(final String hasher) {
        return HASHERS.get(hasher);
    }

    public static String hash2chars(final byte[] hash, final char[] charTable, final int passwordLength) {
        final StringBuilder chars = new StringBuilder();
        for (int i = 0; i < passwordLength; i++) {
            chars.append(charTable[unsigned(hash[i]) % charTable.length]);
        }
        return chars.toString();
    }

    private static int unsigned(final byte x) {
        return ((int) x) & 0xff;
    }

}
