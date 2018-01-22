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

import org.junit.Test;

import java.math.BigInteger;

import static com.github.wolandmaster.uniquepasswords.hashers.ShaHasher.Algorithm.SHA1;
import static com.github.wolandmaster.uniquepasswords.hashers.ShaHasher.Algorithm.SHA224;
import static com.github.wolandmaster.uniquepasswords.hashers.ShaHasher.Algorithm.SHA256;
import static com.github.wolandmaster.uniquepasswords.hashers.ShaHasher.Algorithm.SHA384;
import static com.github.wolandmaster.uniquepasswords.hashers.ShaHasher.Algorithm.SHA512;
import static org.junit.Assert.assertEquals;

public class HasherTest {

    @Test
    public void shouldSha1HashingSucceed() throws Exception {
        assertEquals("59b3e8d637cf97edbe2384cf59cb7453dfe30789",
                toHex(new ShaHasher(SHA1).hash("password", "salt")));
    }

    @Test
    public void shouldSha224HashingSucceed() throws Exception {
        assertEquals("5fe31e9aab92219c047273219ab12eba400c9312ae74258706f144e1",
                toHex(new ShaHasher(SHA224).hash("password", "salt")));
    }

    @Test
    public void shouldSha256HashingSucceed() throws Exception {
        assertEquals("13601bda4ea78e55a07b98866d2be6be0744e3866f13c00c811cab608a28f322",
                toHex(new ShaHasher(SHA256).hash("password", "salt")));
    }

    @Test
    public void shouldSha384HashingSucceed() throws Exception {
        assertEquals("f4bdac9860c0ceea69fb29efbce24addca5cf1f808925d9433b"
                        + "668528290d5d2c9080f32342175b5124895684db8ba4f",
                toHex(new ShaHasher(SHA384).hash("password", "salt")));
    }

    @Test
    public void shouldSha512HashingSucceed() throws Exception {
        assertEquals("2908d2c28dfc047741fc590a026ffade237ab2ba7e1266f010fe49bde548b5987a"
                        + "534a86655a0d17f336588e540cd66f67234b152bbb645b4bb85758a1325d64",
                toHex(new ShaHasher(SHA512).hash("password", "salt")));
    }

    @Test
    public void shouldMd5HashingSucceed() throws Exception {
        assertEquals("67a1e09bb1f83f5007dc119c14d663aab305cadbb3bce54f3aa59c64fec00dea",
                toHex(new Md5Hasher().hash("password", "salt")));
    }

    @Test
    public void shouldBCryptHashingSucceed() throws Exception {
        assertEquals("37c26d5f3bcb1965a622914742db20e4908881f0e81f0b69",
                toHex(new BCryptHasher().hash("password", "salt")));
    }

    private String toHex(final byte[] hash) {
        return new BigInteger(1, hash).toString(16);
    }

}