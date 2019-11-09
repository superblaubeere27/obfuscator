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

import me.superblaubeere27.jobf.utils.values.BooleanValue;
import me.superblaubeere27.jobf.utils.values.DeprecationLevel;
import me.superblaubeere27.jobf.utils.values.FilePathValue;
import me.superblaubeere27.jobf.utils.values.StringValue;

public class JObfSettings {
    private static final String PROCESSOR_NAME = "General Settings";

    private StringValue generatorChars = new StringValue(PROCESSOR_NAME, "Generator characters", DeprecationLevel.GOOD, "Il");
    private BooleanValue useCustomDictionary = new BooleanValue(PROCESSOR_NAME, "Custom dictionary", DeprecationLevel.GOOD, false);
    private FilePathValue classNameDictionary = new FilePathValue(PROCESSOR_NAME, "Class Name dictionary", DeprecationLevel.GOOD, "");
    private FilePathValue nameDictionary = new FilePathValue(PROCESSOR_NAME, "Name dictionary", DeprecationLevel.GOOD, "");
    private BooleanValue useStore = new BooleanValue(PROCESSOR_NAME, "Use STORE instead of DEFLATE (For e.g. SpringBoot)", DeprecationLevel.GOOD, false);

    public BooleanValue getUseCustomDictionary() {
        return useCustomDictionary;
    }

    public FilePathValue getClassNameDictionary() {
        return classNameDictionary;
    }

    public FilePathValue getNameDictionary() {
        return nameDictionary;
    }

    public StringValue getGeneratorChars() {
        return generatorChars;
    }

    public BooleanValue getUseStore() {
        return useStore;
    }
}

