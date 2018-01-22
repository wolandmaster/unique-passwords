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

package com.github.wolandmaster.uniquepasswords.hashers;


import com.github.wolandmaster.uniquepasswords.interfaces.Hasher;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static java.nio.charset.StandardCharsets.UTF_8;

public class ShaHasher implements Hasher {

    public enum Algorithm {
        SHA1("SHA-1"),
        SHA224("SHA-224"),
        SHA256("SHA-256"),
        SHA384("SHA-384"),
        SHA512("SHA-512");

        private final String value;

        Algorithm(final String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    private final MessageDigest sha;

    public ShaHasher(final Algorithm algorithm) throws NoSuchAlgorithmException {
        sha = MessageDigest.getInstance(algorithm.getValue());
    }

    @Override
    public byte[] hash(final String password, final String salt) {
        final byte[] bytes = (salt + password).getBytes(UTF_8);
        synchronized (sha) {
            sha.update(bytes, 0, bytes.length);
            return sha.digest();
        }
    }

}
