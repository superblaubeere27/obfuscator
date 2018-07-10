package me.superblaubeere27.jobf.util.values;

public class EnabledValue extends BooleanValue {

    public EnabledValue(String owner, DeprecationLevel deprecated, Boolean object) {
        super(owner, "Enabled", deprecated, object);
    }

    @Override
    public String toString() {
        return String.format("%s = %s", getOwner(), getObject());
    }
}
