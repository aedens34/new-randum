package com.example.randomloot;

import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.api.item.builder.GunItemBuilder;
import com.tacz.guns.api.item.gun.FireMode;
import com.tacz.guns.resource.index.CommonGunIndex;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Everything that touches TaCZ lives here, and this class is only used after
 * checking that TaCZ is loaded, so the mod still works without TaCZ installed.
 * Guns are read from TaCZ's own live index, so addon guns are included
 * automatically with no addon-specific code.
 */
public final class TaczCompat {

    private TaczCompat() {
    }

    public static List<ResourceLocation> gunIds() {
        List<ResourceLocation> ids = new ArrayList<>();
        try {
            Object result = TimelessAPI.getAllCommonGunIndex();
            if (result instanceof Map<?, ?> map) {
                for (Object key : map.keySet()) {
                    if (key instanceof ResourceLocation rl) {
                        ids.add(rl);
                    }
                }
            } else if (result instanceof Iterable<?> iterable) {
                for (Object item : iterable) {
                    if (item instanceof Map.Entry<?, ?> entry && entry.getKey() instanceof ResourceLocation rl) {
                        ids.add(rl);
                    } else if (item instanceof ResourceLocation rl) {
                        ids.add(rl);
                    }
                }
            }
        } catch (Throwable t) {
            RandomLootMod.LOGGER.error("[RandomLoot] Could not read TaCZ's gun list.", t);
        }
        return ids;
    }

    public static ItemStack buildGun(ResourceLocation gunId) {
        try {
            int magazine = magazineSize(gunId);
            return GunItemBuilder.create()
                    .setId(gunId)
                    .setFireMode(FireMode.SEMI)
                    .setAmmoCount(magazine)
                    .setAmmoInBarrel(true)
                    .build();
        } catch (Throwable t) {
            RandomLootMod.LOGGER.debug("[RandomLoot] Could not build gun {}", gunId, t);
            return ItemStack.EMPTY;
        }
    }

    private static int magazineSize(ResourceLocation gunId) {
        try {
            Optional<CommonGunIndex> index = TimelessAPI.getCommonGunIndex(gunId);
            if (index.isPresent()) {
                int amount = index.get().getGunData().getAmmoAmount();
                if (amount > 0) {
                    return amount;
                }
            }
        } catch (Throwable ignored) {
        }
        return 10;
    }
}
