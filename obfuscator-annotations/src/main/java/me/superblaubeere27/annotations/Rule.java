/*
 * Copyright (c) 2017-2019 superblaubeere27, Sam Sun, MarcoMC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package me.superblaubeere27.annotations;

import static me.superblaubeere27.annotations.ObfuscationTransformer.*;

public @interface Rule {
    Action value();

    ObfuscationTransformer[] processors() default {FLOW_OBFUSCATION,
            LINE_NUMBER_REMOVER,
            NUMBER_OBFUSCATION,
            STRING_ENCRYPTION,
            HWID_PROTECTION,
            PEEPHOLE_OPTIMIZER,
            CRASHER,
            INVOKE_DYNAMIC,
            REFERENCE_PROXY,
            SHUFFLE_MEMBERS,
            INNER_CLASS_REMOVER,
            NAME_OBFUSCATION,
            HIDE_MEMBERS,
            INLINING};

    enum Action {
        ALLOW, DISALLOW
    }
}
