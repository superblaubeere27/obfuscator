/*
 * Copyright (c) 2017-2019 superblaubeere27, Sam Sun, MarcoMC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package me.superblaubeere27;

import java.lang.invoke.*;

public class Test {
    private static String[] references;
    private static Class[] types;

    static {
        references = new String[]{"Test:a:0:   "};
        types = new Class[]{String[].class};
    }

    public static void main(String[] args) {
        System.out.println();
    }

    private static CallSite bootstrap(final MethodHandles.Lookup lookup, final String s, final MethodType methodType) throws NoSuchMethodException, IllegalAccessException {
        try {
            final String[] split = Test.references[Integer.parseInt(s)].split(":");
            final Class<?> classIn = Class.forName(split[0]);
            final String name = split[1];
            MethodHandle methodHandle;

            int length = split[3].length();

            if (length <= 2) {
                final MethodType methodDesc = MethodType.fromMethodDescriptorString(split[2], Test.class.getClassLoader());

                if (length == 2) {
                    methodHandle = lookup.findVirtual(classIn, name, methodDesc);
                } else {
                    methodHandle = lookup.findStatic(classIn, name, methodDesc);
                }
            } else {
                Class typeLookup = types[Integer.parseInt(split[2])];

                if (length == 3) {
                    methodHandle = lookup.findGetter(classIn, name, typeLookup);
                } else if (length == 4) {
                    methodHandle = lookup.findStaticGetter(classIn, name, typeLookup);
                } else if (length == 5) {
                    methodHandle = lookup.findSetter(classIn, name, typeLookup);
                } else {
                    methodHandle = lookup.findStaticSetter(classIn, name, typeLookup);
                }
            }

            return new ConstantCallSite(methodHandle);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }


}
