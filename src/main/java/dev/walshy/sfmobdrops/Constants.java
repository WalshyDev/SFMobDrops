package dev.walshy.sfmobdrops;

import org.bukkit.ChatColor;

import javax.annotation.Nonnull;
import java.util.regex.Pattern;

public final class Constants {

    public static final Pattern CONSTANT = Pattern.compile("[A-Z0-9_]+");
    public static final Pattern NAMESPACE = Pattern.compile("[a-z0-9_]+:[a-z0-9_]+");

    private Constants() {}

    // TODO: Make an actual util at some point or use dough-chat
    @Nonnull
    public static String color(@Nonnull String str) {
        return ChatColor.translateAlternateColorCodes('&', str);
    }
}
