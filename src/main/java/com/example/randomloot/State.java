package com.example.randomloot;

/**
 * Live on/off switches. The server's loot code reads these, and the in-game menu changes them.
 * Index: 0 = block drops, 1 = mob drops, 2 = "random random" (chaos) mode.
 */
public final class State {
    public static volatile boolean blocks = true;
    public static volatile boolean mobs = true;
    public static volatile boolean chaos = false;

    private State() {
    }

    public static boolean get(int which) {
        return switch (which) {
            case 0 -> blocks;
            case 1 -> mobs;
            default -> chaos;
        };
    }

    public static void set(int which, boolean value) {
        switch (which) {
            case 0 -> blocks = value;
            case 1 -> mobs = value;
            default -> chaos = value;
        }
    }

    /** Called when a world/server starts: take the saved values from the config file. */
    public static void loadFromConfig() {
        try {
            blocks = Config.RANDOMIZE_BLOCKS.get();
            mobs = Config.RANDOMIZE_MOBS.get();
            chaos = Config.CHAOS_MODE.get();
        } catch (Throwable t) {
            RandomLootMod.LOGGER.warn("[RandomLoot] Could not read config, using defaults.", t);
        }
    }

    /** Saves the current switches back to the config file so they survive a restart. */
    public static void saveToConfig() {
        try {
            Config.RANDOMIZE_BLOCKS.set(blocks);
            Config.RANDOMIZE_MOBS.set(mobs);
            Config.CHAOS_MODE.set(chaos);
            Config.SPEC.save();
        } catch (Throwable t) {
            RandomLootMod.LOGGER.warn("[RandomLoot] Could not save config.", t);
        }
    }
}
