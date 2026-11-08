package com.example.randomloot;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.slf4j.Logger;

@Mod(RandomLootMod.MOD_ID)
public class RandomLootMod {

    public static final String MOD_ID = "randomloot";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final DeferredRegister<Codec<? extends IGlobalLootModifier>> LOOT_MODIFIERS =
            DeferredRegister.create(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, MOD_ID);

    static {
        LOOT_MODIFIERS.register("randomize", RandomizeLootModifier.CODEC);
    }

    public RandomLootMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        LOOT_MODIFIERS.register(modEventBus);

        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, Config.SPEC);

        modEventBus.addListener((FMLCommonSetupEvent e) -> e.enqueueWork(Net::register));
        MinecraftForge.EVENT_BUS.addListener(this::onServerStarting);
        MinecraftForge.EVENT_BUS.addListener(this::onLogin);
    }

    private void onServerStarting(final ServerStartingEvent event) {
        // Rebuild the random pool for each world/server start so it matches the installed mods and config.
        ItemPool.reset();
        State.loadFromConfig();
    }

    private void onLogin(final PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer sp) {
            Net.sendTo(sp);
        }
    }
}
