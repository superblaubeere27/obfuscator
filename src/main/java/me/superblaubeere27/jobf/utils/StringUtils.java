package me.superblaubeere27.jobf.utils;

import java.util.Random;

/**
 * Copyright © 2015 - 2017 | CCBlueX | All rights reserved.
 * <p>
 * Fume-Obfuscator - By CCBlueX(Marco)
 */
public class StringUtils {

    private static final Random random = new Random();

    public static String makeUnreadable(final String input) {
        final StringBuilder builder = new StringBuilder();
        for (final char c : input.toCharArray())
            builder.append((char) (c + '煙'));
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
