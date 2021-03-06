/*
 * Copyright (c) 2016-2018, Christoph Engelbert (aka noctarius) and
 * contributors. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.noctarius.borabora;

import org.junit.Test;
import sun.misc.Unsafe;

import static org.junit.Assert.assertEquals;

public class UnsafeByteOutputTestCase {

    private static final Unsafe UNSAFE = UnsafeUtils.getUnsafe();

    @Test(expected = NoSuchByteException.class)
    public void test_write_offset_less_than_0() {
        UnsafeByteOutput output = new UnsafeByteOutput(0, 0);
        output.write(-1, (byte) 1);
    }

    @Test(expected = NoSuchByteException.class)
    public void test_write_offset_greater_than_array_length() {
        UnsafeByteOutput output = new UnsafeByteOutput(0, 1);
        output.write(2, (byte) 1);
    }

    @Test
    public void test_write_verify_returned_offset() {
        long address = allocate(16);
        UnsafeByteOutput output = new UnsafeByteOutput(address, 16);
        long offset = output.write(5, (byte) 0x1);
        assertEquals(6, offset);
    }

    @Test
    public void test_write_offset() {
        long address = allocate(1);
        UnsafeByteOutput output = new UnsafeByteOutput(address, 1);
        output.write(0, (byte) 1);
        byte actual = UNSAFE.getByte(address);
        assertEquals((byte) 1, actual);
    }

    @Test(expected = NoSuchByteException.class)
    public void test_write_array_offset_less_than_0() {
        UnsafeByteOutput output = new UnsafeByteOutput(0, 0);
        output.write(new byte[0], -1, 1);
    }

    @Test(expected = NoSuchByteException.class)
    public void test_write_array_length_less_than_0() {
        UnsafeByteOutput output = new UnsafeByteOutput(0, 0);
        output.write(new byte[0], 0, -1);
    }

    @Test(expected = NoSuchByteException.class)
    public void test_write_array_offset_less_than_array_length() {
        UnsafeByteOutput output = new UnsafeByteOutput(0, 0);
        output.write(new byte[0], 0, 1);
    }

    @Test(expected = NoSuchByteException.class)
    public void test_write_array_offset_plus_length_less_than_array_length() {
        UnsafeByteOutput output = new UnsafeByteOutput(0, 1);
        output.write(new byte[0], 0, 2);
    }

    @Test
    public void test_write_array_offset() {
        long address = allocate(1);
        byte[] v = new byte[]{(byte) 1};
        UnsafeByteOutput output = new UnsafeByteOutput(address, 1);
        output.write(v, 0, 1);
        byte actual = UNSAFE.getByte(address);
        assertEquals(v[0], actual);
    }

    private long allocate(long size) {
        return UNSAFE.allocateMemory(size);
    }

}
