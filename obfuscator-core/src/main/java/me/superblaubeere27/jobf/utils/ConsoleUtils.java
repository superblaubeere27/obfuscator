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

import java.util.List;

public class ConsoleUtils {

    public static String formatBox(String title, boolean center, List<String> lines) {
        int width = title.length() + 4;

        for (String line : lines) {
            int lineWidth = line.length() + 2;

            if (lineWidth > width) width = lineWidth;
        }

        int i = 0;

        StringBuilder sb = new StringBuilder();

        sb.append("+");

        centerString(sb, "[ " + title + " ]", "-", width);
        sb.append("+");

        sb.append("\n");

        for (String line : lines) {
            sb.append("|");

            if (center) {
                centerString(sb, line, " ", width);
            } else {
                sb.append(" ").append(line);

                addTimes(sb, width - line.length() - 1, " ");
            }


            sb.append("|");
            sb.append("\n");
        }

        sb.append("+");

        addTimes(sb, width, "-");

        sb.append("+");

        return sb.toString();
    }

    private static void centerString(StringBuilder stringBuilder, String stringToCenter, String fillChar, int width) {
        int sideOffset = width - stringToCenter.length();

        addTimes(stringBuilder, sideOffset / 2, fillChar);

        stringBuilder.append(stringToCenter);

        addTimes(stringBuilder, sideOffset - sideOffset / 2, fillChar);
    }

    private static void addTimes(StringBuilder sb, int times, String s) {
        for (int i = 0; i < times; i++) {
            sb.append(s);
        }
    }

}
