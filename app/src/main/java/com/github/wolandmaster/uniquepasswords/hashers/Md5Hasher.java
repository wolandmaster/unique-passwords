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

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static java.nio.charset.StandardCharsets.UTF_8;

public class Md5Hasher implements Hasher {

    private final MessageDigest md5;

    public Md5Hasher() throws NoSuchAlgorithmException {
        md5 = MessageDigest.getInstance("MD5");
    }

    @Override
    public byte[] hash(final String password, final String salt) {
        final byte[] inputBytes1 = (salt + password).getBytes(UTF_8);
        final byte[] inputBytes2 = (password + salt).getBytes(UTF_8);
        synchronized (md5) {
            md5.update(inputBytes1, 0, inputBytes1.length);
            final byte[] hashBytes1 = md5.digest();
            md5.update(inputBytes2, 0, inputBytes2.length);
            final byte[] hashBytes2 = md5.digest();
            return ByteBuffer.wrap(new byte[hashBytes1.length + hashBytes2.length])
                    .put(hashBytes1).put(hashBytes2).array();
        }
    }

}
