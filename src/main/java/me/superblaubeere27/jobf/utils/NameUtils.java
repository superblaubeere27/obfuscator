package me.superblaubeere27.jobf.utils;

import me.superblaubeere27.jobf.util.Util;
import org.objectweb.asm.tree.ClassNode;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Copyright © 2015 - 2017 | CCBlueX | All rights reserved.
 * <p>
 * Fume - By CCBlueX(Marco)
 */
public class NameUtils {

    private static int classNames = 0;
    private static Map<String, HashMap<String, Integer>> USED_METHODNAMES = new HashMap<>();
    private static Map<String, Integer> USED_FIELDNAMES = new HashMap<>();

    //    private static boolean iL = true;
    private static int iL_uniqueInt = 0;

    private static Random random = new Random();
    private static int METHODS = 0;
    private static int FIELDS = 0;

    public static int randInt(int min, int max) {
        return random.nextInt(max - min) + min;
    }

    public static void setup(final String classCharacters, final String methodCharacters, final String fieldCharacters, boolean iL) {
        USED_METHODNAMES.clear();
        USED_FIELDNAMES.clear();

    }

    public static String generateSpaceString(int length) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            stringBuilder.append(" ");
        }
        return stringBuilder.toString();
    }

    public static String generateClassName() {
//        if(iL) {
//            return Integer.toBinaryString(iL_int++).replace('0', 'I').replace('1', 'l');
//        }
        return Util.toIl(classNames++);
//        final StringBuilder stringBuilder = new StringBuilder();
//        final int lenght = getLenght();
//        while(stringBuilder.length() < lenght || USED_CLASSNAMES.contains(stringBuilder.toString())
////                || Fume.fume.obfuscator.files.containsKey(stringBuilder.toString() + ".class")
//                ) {
//            final int randIndex = (int)(Math.random() * CLASS_CHARACTERS.length());
//            stringBuilder.append(CLASS_CHARACTERS.charAt(randIndex));
//        }
//        USED_CLASSNAMES.add(stringBuilder.toString());
//        return stringBuilder.toString();
    }

    public static String generateMethodName(final String className, String desc) {
//        if (!USED_METHODNAMES.containsKey(className)) {
//            USED_METHODNAMES.put(className, new HashMap<>());
//        }
//
//        HashMap<String, Integer> descMap = USED_METHODNAMES.get(className);
//
//        if (!descMap.containsKey(desc)) {
//            descMap.put(desc, 0);
//        }
////        System.out.println("0 " + className + "/" + desc + ":" + descMap);
//
//        int i = descMap.get(desc);
//        descMap.put(desc, i + 1);

//        System.out.println(USED_METHODNAMES);

//        System.out.println(className + "/" + desc + ":" + descMap);

//        return Util.toIl(i);
        return Util.toIl(METHODS++);
    }

    public static String generateMethodName(final ClassNode classNode, String desc) {
        return generateMethodName(classNode.name, desc);
    }

    public static String generateFieldName(final String className) {
//        if (!USED_FIELDNAMES.containsKey(className)) {
//            USED_FIELDNAMES.put(className, 0);
//        }
//
//        int i = USED_FIELDNAMES.get(className);
//        USED_FIELDNAMES.put(className, i + 1);
//
//        return Util.toIl(i);
        return Util.toIl(FIELDS++);
    }

    public static String generateFieldName(final ClassNode classNode) {
        return generateFieldName(classNode.name);
    }

    public static String generateLocalVariableName(final String className, final String methodName) {
        return generateLocalVariableName();
    }

    public static String generateLocalVariableName() {
        return Util.toIl((int) (Math.random() * Short.MAX_VALUE));
    }

    private static int getLenght() {
        return new Random().nextInt(20) + 6;
    }

    public static String unicodeString(int length) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            stringBuilder.append((char) randInt(128, 250));
        }
        return stringBuilder.toString();
    }

    public static void mapClass(String old, String newName) {
        if (USED_METHODNAMES.containsKey(old)) {
            USED_METHODNAMES.put(newName, USED_METHODNAMES.get(old));
        }
        if (USED_FIELDNAMES.containsKey(old)) {
            USED_FIELDNAMES.put(newName, USED_FIELDNAMES.get(old));
        }
    }
}
