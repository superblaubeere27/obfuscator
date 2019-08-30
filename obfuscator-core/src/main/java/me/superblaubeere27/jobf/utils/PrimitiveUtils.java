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

import org.objectweb.asm.Opcodes;

import java.util.HashMap;
import java.util.Map;

public class PrimitiveUtils {
    private static final Map<String, Class<?>> nameToPrimitive = new HashMap<>();
    private static final Map<Class<?>, Object> defaultPrimitiveValues = new HashMap<>();

    static {
        defaultPrimitiveValues.put(Integer.TYPE, 0);
        defaultPrimitiveValues.put(Long.TYPE, 0L);
        defaultPrimitiveValues.put(Double.TYPE, 0D);
        defaultPrimitiveValues.put(Float.TYPE, 0F);
        defaultPrimitiveValues.put(Boolean.TYPE, false);
        defaultPrimitiveValues.put(Character.TYPE, '\0');
        defaultPrimitiveValues.put(Byte.TYPE, (byte) 0);
        defaultPrimitiveValues.put(Short.TYPE, (short) 0);
        defaultPrimitiveValues.put(Object.class, null);
        nameToPrimitive.put("int", Integer.TYPE);
        nameToPrimitive.put("long", Long.TYPE);
        nameToPrimitive.put("double", Double.TYPE);
        nameToPrimitive.put("float", Float.TYPE);
        nameToPrimitive.put("boolean", Boolean.TYPE);
        nameToPrimitive.put("char", Character.TYPE);
        nameToPrimitive.put("byte", Byte.TYPE);
        nameToPrimitive.put("short", Short.TYPE);
        nameToPrimitive.put("void", Void.TYPE);
    }

    public static Class<?> getPrimitiveByName(String name) {
        return nameToPrimitive.get(name);
    }

    public static Object getDefaultValue(Class<?> primitive) {
        return defaultPrimitiveValues.get(primitive);
    }

    public static Class<?> getPrimitiveByNewArrayId(int id) {
        switch (id) {
            case Opcodes.T_BOOLEAN:
                return boolean.class;
            case Opcodes.T_CHAR:
                return char.class;
            case Opcodes.T_FLOAT:
                return float.class;
            case Opcodes.T_DOUBLE:
                return double.class;
            case Opcodes.T_BYTE:
                return byte.class;
            case Opcodes.T_SHORT:
                return short.class;
            case Opcodes.T_INT:
                return int.class;
            case Opcodes.T_LONG:
                return long.class;
        }
        throw new IllegalArgumentException("Unknown type " + id);
    }
}
