package com.example.randomloot;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.List;

public class Config {

    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.BooleanValue RANDOMIZE_BLOCKS;
    public static final ForgeConfigSpec.BooleanValue RANDOMIZE_MOBS;
    public static final ForgeConfigSpec.BooleanValue RANDOMIZE_OTHER;
    public static final ForgeConfigSpec.BooleanValue CHAOS_MODE;
    public static final ForgeConfigSpec.IntValue SEED_OFFSET;
    public static final ForgeConfigSpec.BooleanValue INCLUDE_TACZ_GUNS;
    public static final ForgeConfigSpec.IntValue TACZ_GUN_PERCENT;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> EXCLUDED_ITEMS;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        builder.push("randomLoot");

        RANDOMIZE_BLOCKS = builder
                .comment("Randomize what blocks drop when broken.")
                .define("randomizeBlocks", true);

        RANDOMIZE_MOBS = builder
                .comment("Randomize what mobs/animals drop when killed. Player death drops are never touched.")
                .define("randomizeMobs", true);

        RANDOMIZE_OTHER = builder
                .comment("Also randomize chests, fishing, archaeology and other loot. Off by default.")
                .define("randomizeOther", false);

        CHAOS_MODE = builder
                .comment(
                        "false = MAPPED: each original item always turns into the same replacement in a given world",
                        "        (like a classic loot randomizer - stone always gives the same thing).",
                        "true  = CHAOS: every single drop rolls a fresh random item.")
                .define("chaosMode", false);

        SEED_OFFSET = builder
                .comment("Change this number to get a completely different mapping in the same world (mapped mode only).")
                .defineInRange("seedOffset", 0, Integer.MIN_VALUE, Integer.MAX_VALUE);

        INCLUDE_TACZ_GUNS = builder
                .comment("If TaCZ is installed, let drops turn into TaCZ guns (base mod and every addon).")
                .define("includeTaczGuns", true);

        TACZ_GUN_PERCENT = builder
                .comment("Percent of replacements (0-100) that become a TaCZ gun instead of a normal item.")
                .defineInRange("taczGunPercent", 15, 0, 100);

        EXCLUDED_ITEMS = builder
                .comment("Item ids that can never be a replacement. The three tacz entries are blank placeholder items without a gun/ammo/attachment id.")
                .defineList("excludedItems", List.of(
                        "minecraft:air",
                        "minecraft:barrier",
                        "minecraft:bedrock",
                        "minecraft:command_block",
                        "minecraft:chain_command_block",
                        "minecraft:repeating_command_block",
                        "minecraft:command_block_minecart",
                        "minecraft:structure_block",
                        "minecraft:structure_void",
                        "minecraft:jigsaw",
                        "minecraft:light",
                        "minecraft:debug_stick",
                        "minecraft:knowledge_book",
                        "minecraft:spawner",
                        "minecraft:end_portal_frame",
                        "tacz:modern_kinetic_gun",
                        "tacz:ammo",
                        "tacz:attachment"
                ), e -> e instanceof String);

        builder.pop();
        SPEC = builder.build();
    }

    private Config() {
    }
}
