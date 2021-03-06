package dev.walshy.sfmobdrops;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public final class Guis implements Listener {

    private static final String TITLE = ChatColor.DARK_PURPLE + "Mob Drops";

    protected Guis() {}

    // TODO: Add pages support
    public static void openMobDropList(Player player) {
        final Set<Drop> drops = SfMobDrops.getInstance().getDrops();
        final int size = (drops.size() + 8) / 9 * 9;

        final Inventory inv = Bukkit.createInventory(null, size, TITLE);
        for (Drop drop : drops) {
            final SlimefunItem item = drop.getSlimefunItem();
            if (item == null) continue;

            final ItemStack is = new ItemStack(getMaterialForMob(drop.getDropsFrom()));
            final ItemMeta im = is.getItemMeta();

            final int amount = drop.getAmount();
            im.setDisplayName(ChatColor.GRAY + "" + amount + "x " + item.getItemName());

            final List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + "Drops from " + getEntity(drop.getDropsFrom()));
            lore.add("");
            lore.add(ChatColor.LIGHT_PURPLE + "" + drop.getChance() + "% " + ChatColor.GRAY + "chance");

            if (drop.getEntityName() != null) {
                lore.add(ChatColor.GRAY + "Requires name: " + Constants.color(drop.getEntityName()));
            }
            if (drop.getEntityNbtTag() != null) {
                lore.add(ChatColor.GRAY + "Requires tag: " + ChatColor.LIGHT_PURPLE + drop.getEntityNbtTag());
            }
            im.setLore(lore);
            is.setItemMeta(im);

            inv.addItem(is);
        }

        player.openInventory(inv);
    }

    // TODO: Get some mob heads
    private static Material getMaterialForMob(@Nonnull EntityType type) {
        if (type == EntityType.ENDER_DRAGON) {
            return Material.DRAGON_HEAD;
        } else if (type == EntityType.ZOMBIE) {
            return Material.ZOMBIE_HEAD;
        } else if (type == EntityType.CREEPER) {
            return Material.CREEPER_HEAD;
        } else {
            final Material mat = Material.getMaterial(type + "_SPAWN_EGG");
            if (mat != null) {
                return mat;
            } else {
                return Material.SPAWNER;
            }
        }
    }

    private static String getEntity(@Nonnull EntityType type) {
        return ChatColor.LIGHT_PURPLE + capitalise(type.name().charAt(0) + type.name().substring(1)
            .replace('_', ' ')
            .toLowerCase(Locale.ROOT));
    }

    private static String capitalise(String str) {
        if (str.isEmpty()) return str;

        final char[] chars = str.toCharArray();
        boolean capitalizeNext = true;
        for (int i = 0; i < chars.length; i++) {
            final char ch = chars[i];
            if (ch == ' ') {
                capitalizeNext = true;
            } else if (capitalizeNext) {
                chars[i] = Character.toTitleCase(ch);
                capitalizeNext = false;
            }
        }
        return new String(chars);
    }

    @EventHandler
    public void onInvClick(@Nonnull InventoryClickEvent e) {
        if (e.getView().getTitle().equals(TITLE)) {
            e.setCancelled(true);
            e.setResult(Event.Result.DENY);
        }
    }
}
