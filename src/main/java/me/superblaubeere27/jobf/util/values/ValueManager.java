package me.superblaubeere27.jobf.util.values;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class ValueManager {
    private static List<Value<?>> values = new ArrayList<>();

    private static void registerField(Field field, Object object) {
        field.setAccessible(true);

        try {
            Object obj = field.get(object);

            if (obj instanceof Value) {
                Value value = (Value) obj;
                values.add(value);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static void registerClass(Object obj) {
        Class<?> clazz = obj.getClass();

        for (Field field : clazz.getFields()) {
            registerField(field, obj);
        }
        for (Field field : clazz.getDeclaredFields()) {
            registerField(field, obj);
        }

    }

    public static List<Value<?>> getValues() {
        return values;
    }
}
