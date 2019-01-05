/*
 * Copyright (c) 2017-2019 superblaubeere27, Sam Sun, MarcoMC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package me.superblaubeere27.jobf;

import me.superblaubeere27.jobf.util.values.BooleanValue;
import me.superblaubeere27.jobf.util.values.DeprecationLevel;
import me.superblaubeere27.jobf.util.values.NumberValue;

public class JObfSettings {
    private static final String PROCESSOR_NAME = "General";

    private BooleanValue multithreading = new BooleanValue(PROCESSOR_NAME, "Multithreading", "Enables Multithreading", DeprecationLevel.GOOD, true);
    private NumberValue<Integer> threads = new NumberValue<>(PROCESSOR_NAME, "Threads", "Count of threads; Please don't use more threads than you have CPU cores.", DeprecationLevel.GOOD, 1);

    JObfSettings() {
        try {
            threads.setObject(Runtime.getRuntime().availableProcessors());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    int getThreads() {
        return multithreading.getObject() ? Math.max(threads.getObject(), 1) : 1;
    }

}
