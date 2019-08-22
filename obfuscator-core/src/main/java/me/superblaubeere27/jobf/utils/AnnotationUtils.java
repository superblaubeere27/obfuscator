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

public class AnnotationUtils {

//    public static boolean isExcluded(ClassNode node, ObfuscationTransformer type) {
//        if (node.visibleAnnotations != null && isExcluded(node.invisibleAnnotations, type)) {
//            return true;
//        }
//        return node.invisibleAnnotations != null && isExcluded(node.invisibleAnnotations, type);
//    }
//    public static boolean isExcluded(List<AnnotationNode> annotations, ObfuscationTransformer type) {
//        for (AnnotationNode annotation : annotations) {
//            if (annotation.desc.equals("L" + ObfuscatorRules.class.getName().replace('.', '/') + ";")) {
//                if (annotation.values.size() < 2 || !annotation.values.get(0).equals("value")) {
//                    continue;
//                }
//                List<AnnotationNode> rules;
//
//                if (annotation.values.get(1) instanceof List) {
//                    rules = (List<AnnotationNode>) annotation.values.get(1);
//                } else if (annotation.values.get(1) instanceof AnnotationNode) {
//                    rules = Collections.singletonList((AnnotationNode) annotation.values.get(1));
//                } else {
//                    continue;
//                }
//
//
//                for (AnnotationNode rule : rules) {
//                    Rule.Action action = null;
//
//                    for (int i = 0; i < rule.values.size(); i++) {
//                        Object o = rule.values.get(i);
//
//                        if (o instanceof ) {
//
//                        }
//                    }
//
//                }
//            }
//        }
//        return false;
//    }
}
