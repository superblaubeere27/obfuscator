package me.superblaubeere27.jobf.util.values;

public class NumberValue<T extends Number> extends Value<T> {

    public NumberValue(String owner, String name, DeprecationLevel deprecated, T object) {
        super(owner, name, deprecated, object);
    }

}
