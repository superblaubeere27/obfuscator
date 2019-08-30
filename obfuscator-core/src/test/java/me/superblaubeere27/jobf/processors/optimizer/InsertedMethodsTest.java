/*
 * Copyright (c) 2017-2019 superblaubeere27, Sam Sun, MarcoMC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package me.superblaubeere27.jobf.processors.optimizer;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class InsertedMethodsTest {

    @Test
    public void testStartsWith() {
        assertTrue(InsertedMethods.startsWith("ich hasse Menschen", "ich hasse ".length(), "ich hasse ".hashCode()));
        assertTrue(InsertedMethods.startsWith("ich hasse ", "ich hasse ".length(), "ich hasse ".hashCode()));
        assertFalse(InsertedMethods.startsWith("cancer", "aids".length(), "aids".hashCode()));
    }

    @Test
    public void testEndsWith() {
        assertTrue(InsertedMethods.endsWith("ich hasse Menschen", "Menschen".length(), "Menschen".hashCode()));
        assertTrue(InsertedMethods.endsWith("hasse", "hasse".length(), "hasse".hashCode()));
        assertFalse(InsertedMethods.endsWith("cancer", "aidsfgd".length(), "aidsfgd".hashCode()));
    }

}