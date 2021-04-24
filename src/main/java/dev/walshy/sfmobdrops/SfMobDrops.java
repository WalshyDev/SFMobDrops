package dev.walshy.sfmobdrops;

import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
import me.mrCookieSlime.Slimefun.cscorelib2.chat.ChatColors;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;

public class SfMobDrops extends JavaPlugin implements Listener {

    /*
     * TODO: Add a reload command
     */

    private final Set<Drop> drops = new HashSet<>();

    @Override
    public void onEnable() {
        if (!new File(getDataFolder(), "config.yml").exists()) {
            saveDefaultConfig();
        }

        loadConfig();
    }

    public void loadConfig() {
        final Set<Drop> newSet = new HashSet<>();

        //noinspection unchecked
        final List<Map<String, Object>> list = (List<Map<String, Object>>) getConfig().getList("drops");
        if (list == null || list.isEmpty()) return;

        for (Map<String, Object> map : list) {
            // If some arguments are invalid then just miss it.
            if (!validateArguments(map)) {
                continue;
            }

            final EntityType type = entityFromString((String) map.get("entity"));
            final String slimefunId = (String) map.get("slimefunItem");
            final double chance = getDouble(map.get("chance"));

            final String entityName = (String) map.get("name");
            final String nbtTag = (String) map.get("nbtTag");
            final int amount = map.get("amount") == null ? 1 : (int) map.get("amount");

            newSet.add(new Drop(type, slimefunId, chance, entityName, nbtTag, amount));
        }

        this.drops.clear();
        this.drops.addAll(newSet);
        getLogger().info("Loaded in " + this.drops.size() + " drops!");
    }

    @EventHandler
    public void onMobDeath(@Nonnull EntityDeathEvent e) {
        final Drop drop = findDropFromEntity(e.getEntity());

        if (drop != null && ThreadLocalRandom.current().nextDouble(100) <= drop.getChance()) {
            final SlimefunItem item = SlimefunItem.getByID(drop.getSlimefunId());

            if (item != null && !item.isDisabledIn(e.getEntity().getWorld())) {
                final ItemStack dropping = item.getItem().clone();
                dropping.setAmount(drop.getAmount());

                e.getDrops().add(dropping);
            }
        }
    }

    private boolean validateArguments(@Nonnull Map<String, Object> map) {
        final String entity = (String) map.get("entity");
        final String sfItem = (String) map.get("slimefunItem");
        final double chance = getDouble(map.get("chance"));

        final String nbtTag = (String) map.get("nbtTag");
        final int amount = map.get("amount") == null ? 1 : (int) map.get("amount");

        // Required
        if (entity == null || sfItem == null) {
            getLogger().warning("Required property missing! 'entity', 'slimefunItem' and 'chance' are required!");
            return false;
        }

        if (!Constants.CONSTANT.matcher(entity).matches()) {
            getLogger().warning("Entity should be in SCREAMING_SNAKE_CASE!");
            return false;
        }

        if (!Constants.CONSTANT.matcher(sfItem).matches()) {
            getLogger().warning("Slimefun ID should be in SCREAMING_SNAKE_CASE!");
            return false;
        }

        if (chance < 1 || chance > 100) {
            getLogger().warning("Chance is not a valid value! It needs to be between 0-100");
            return false;
        }

        // Not required
        if (nbtTag != null && !Constants.NAMESPACE.matcher(nbtTag).matches()) {
            getLogger().warning("The NBT Tag need to be in snake_case!");
            return false;
        }

        if (amount < 1 || amount > 64) {
            getLogger().warning("Amount needs to be between 0-64!");
            return false;
        }

        // Validate values
        if (entityFromString(entity) == null) {
            getLogger().warning("Invalid entity type value! Given: " + entity + " - valid values here: "
                + "https://hub.spigotmc.org/javadocs/spigot/org/bukkit/entity/EntityType.html");
            return false;
        }

        if (SlimefunItem.getByID(sfItem) == null) {
            getLogger().warning("Invalid Slimefun Item ID! Given: " + sfItem + " - valid values can be found "
                + "on the Discord: https://discord.gg/slimefun");
            return false;
        }

        return true;
    }

    @Nullable
    private EntityType entityFromString(@Nonnull String str) {
        try {
            return EntityType.valueOf(str);
        } catch (IllegalArgumentException e) {
            getLogger().log(Level.WARNING, "Invalid Entity Type given! " + str + " is not valid!", e);
            return null;
        }
    }

    @Nullable
    private Drop findDropFromEntity(@Nonnull LivingEntity entity) {
        for (Drop drop : this.drops) {
            if (entity.getType() == drop.getDropsFrom()) {
                if (drop.getEntityName() != null && entity.getCustomName() != null
                    && !ChatColors.color(drop.getEntityName()).equals(entity.getCustomName())
                ) {
                    continue;
                }

                if (drop.getEntityNbtTag() != null && entity.getPersistentDataContainer().getKeys().stream()
                    .noneMatch(key -> key.toString().equals(drop.getEntityNbtTag()))
                ) {
                    continue;
                }

                return drop;
            }
        }
        return null;
    }

    private double getDouble(@Nonnull Object obj) {
        return obj instanceof Integer ? (int) obj : (double) obj;
    }
}
