package com.example.randomloot;

import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;

import java.util.function.Supplier;

/**
 * A Global Loot Modifier: Forge runs it on every loot table result, so it
 * covers blocks, mobs and anything else without touching individual tables.
 */
public class RandomizeLootModifier extends LootModifier {

    public static final Supplier<Codec<RandomizeLootModifier>> CODEC = Suppliers.memoize(() ->
            RecordCodecBuilder.create(inst -> codecStart(inst).apply(inst, RandomizeLootModifier::new)));

    public RandomizeLootModifier(LootItemCondition[] conditionsIn) {
        super(conditionsIn);
    }

    @Override
    protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        boolean isBlock = context.hasParam(LootContextParams.BLOCK_STATE);
        boolean isEntityLoot = !isBlock && context.hasParam(LootContextParams.DAMAGE_SOURCE);

        boolean enabled;
        if (isBlock) {
            enabled = State.blocks;
        } else if (isEntityLoot) {
            Entity victim = context.getParamOrNull(LootContextParams.THIS_ENTITY);
            if (victim instanceof Player) {
                return generatedLoot; // never scramble player death drops
            }
            enabled = State.mobs;
        } else {
            enabled = Config.RANDOMIZE_OTHER.get();
        }

        if (!enabled) {
            return generatedLoot;
        }

        ObjectArrayList<ItemStack> result = new ObjectArrayList<>();
        for (ItemStack original : generatedLoot) {
            if (original.isEmpty()) {
                continue;
            }
            result.add(ItemPool.replacement(original, context));
        }
        return result;
    }

    @Override
    public Codec<? extends IGlobalLootModifier> codec() {
        return CODEC.get();
    }
}
