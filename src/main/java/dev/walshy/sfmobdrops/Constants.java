package dev.walshy.sfmobdrops;

import java.util.regex.Pattern;

public final class Constants {

    public static final Pattern CONSTANT = Pattern.compile("[A-Z_]+");
    public static final Pattern NAMESPACE = Pattern.compile("[a-z_]+:[a-z_]+");

    private Constants() {}
}
