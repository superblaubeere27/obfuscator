package me.superblaubeere27.jobf.util.values;

import joptsimple.internal.Strings;

public class ModeValue extends Value<Integer> {
    private String[] possibleValues;

    public ModeValue(String owner, String name, DeprecationLevel deprecated, Integer object, String[] possibleValues) {
        super(owner, name, deprecated, object);
        this.possibleValues = possibleValues;
    }

    public String[] getPossibleValues() {
        return possibleValues;
    }


    @Override
    public String toString() {
        return String.format("%s::%s<%s> = %s", getOwner(), getName(), Strings.join(getPossibleValues(), ", "), getObject());
    }
}
