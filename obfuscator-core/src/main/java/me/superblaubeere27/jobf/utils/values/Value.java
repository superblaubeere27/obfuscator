/*
 * Copyright (c) 2017-2019 superblaubeere27, Sam Sun, MarcoMC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package me.superblaubeere27.jobf.utils.values;

public abstract class Value<T> {
    private String owner;
    private String name;
    private String description;
    private T object;
    private DeprecationLevel deprecation;

    public Value(String owner, String name, DeprecationLevel deprecation, T object) {
        this(owner, name, "", deprecation, object);
    }

    public Value(String owner, String name, String description, DeprecationLevel deprecation, T object) {
        this.owner = owner;
        this.name = name;
        this.description = description;
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

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return String.format("%s::%s = %s", owner, name, object);
    }
}
