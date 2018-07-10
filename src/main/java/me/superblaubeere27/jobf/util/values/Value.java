package me.superblaubeere27.jobf.util.values;

public abstract class Value<T> {
    private String owner;
    private String name;
    private T object;
    private DeprecationLevel deprecation;

    public Value(String owner, String name, DeprecationLevel deprecation, T object) {
        this.owner = owner;
        this.name = name;
        this.deprecation = deprecation;
        this.object = object;
    }

    public String getOwner() {
        return owner;
    }

    public String getName() {
        return name;
    }

    public T getObject() {
        return object;
    }

    public void setObject(T object) {
        this.object = object;
    }

    public DeprecationLevel getDeprecation() {
        return deprecation;
    }

    @Override
    public String toString() {
        return String.format("%s::%s = %s", owner, name, object);
    }
}
