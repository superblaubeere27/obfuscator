package me.superblaubeere27.jobf.util.values;

public class StringValue extends Value<String> {

    public StringValue(String owner, String name, DeprecationLevel deprecated, String object) {
        super(owner, name, deprecated, object);
    }

    @Override
    public String toString() {
        return String.format("%s::%s = \"%s\"", getOwner(), getName(), getObject());
    }
}
