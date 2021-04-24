package dev.walshy.sfmobdrops;

import org.bukkit.entity.EntityType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Drop {

    private final EntityType dropsFrom;
    private final String slimefunId;
    private final double chance;

    private final String entityName;
    private final String entityNbtTag;
    private final int amount;

    public Drop(@Nonnull EntityType dropsFrom, @Nonnull String slimefunId, double chance,
                @Nullable String entityName, @Nullable String entityNbtTag, int amount
    ) {
        this.dropsFrom = dropsFrom;
        this.slimefunId = slimefunId;
        this.chance = chance;

        this.entityName = entityName;
        this.entityNbtTag = entityNbtTag;
        this.amount = amount;
    }

    @Nonnull
    public EntityType getDropsFrom() {
        return dropsFrom;
    }

    @Nonnull
    public String getSlimefunId() {
        return slimefunId;
    }

    public double getChance() {
        return chance;
    }

    @Nullable
    public String getEntityName() {
        return entityName;
    }

    @Nullable
    public String getEntityNbtTag() {
        return entityNbtTag;
    }

    public int getAmount() {
        return amount;
    }
}
