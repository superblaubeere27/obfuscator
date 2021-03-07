/*
 * Copyright (c) 2017-2019 superblaubeere27, Sam Sun, MarcoMC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package me.superblaubeere27.jobf.utils;

import java.util.Random;

public class StringManipulationUtils
{

    private static final Random random = new Random();

    public static String makeUnreadable(final String input) {
        final StringBuilder builder = new StringBuilder();
        for (final char c : input.toCharArray())
            builder.append((char) (c + '\u7159'));
        return builder.toString();
    }

    public static String generateString(int lenght) {
        final String s = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        final StringBuilder stringBuilder = new StringBuilder(lenght);

        for (int i = 0; i < lenght; i++)
            stringBuilder.append(s.charAt(random.nextInt(s.length())));
        return stringBuilder.toString();
    }

    public static String generateUnicodeString(int lenght) {
        final StringBuilder stringBuilder = new StringBuilder(lenght);

        for (int i = 0; i < lenght; i++)
            stringBuilder.append((char) random.nextInt(255));
        return stringBuilder.toString();
    }
}
