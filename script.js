function isRemappingEnabledForClass(name) {
    if (name.toLowerCase().endsWith("main")) {
        print("Keeping Main-Class " + name);
        return false;
    }

    return true;
}