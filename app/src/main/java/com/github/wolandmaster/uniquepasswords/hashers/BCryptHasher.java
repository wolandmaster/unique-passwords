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

import org.mindrot.jbcrypt.BCrypt;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.mindrot.jbcrypt.BCrypt.bf_crypt_ciphertext;

public class BCryptHasher implements Hasher {

    private static final int BCRYPT_LOG2_ROUNDS = 4;

    private final MessageDigest md5;
    private final BCrypt bcrypt = new BCrypt();

    public BCryptHasher() throws NoSuchAlgorithmException {
        md5 = MessageDigest.getInstance("MD5");
    }

    @Override
    public byte[] hash(final String password, final String salt) {
        final byte[] passwordBytes = password.getBytes(UTF_8);
        final byte[] saltBytes = salt.getBytes(UTF_8);
        synchronized (md5) {
            md5.update(saltBytes, 0, saltBytes.length);
            return bcrypt.crypt_raw(passwordBytes, md5.digest(), BCRYPT_LOG2_ROUNDS,
                    (int[]) bf_crypt_ciphertext.clone());
        }
    }

}
