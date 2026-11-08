package com.example.randomloot;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** Adds the "Random" panel (title + three labelled buttons) to the right side of the Escape menu. */
@Mod.EventBusSubscriber(modid = RandomLootMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public final class PauseMenuHook {

    private PauseMenuHook() {
    }

    private static Component label(String name, boolean on) {
        return Component.literal(name + ": ")
                .append(Component.literal(on ? "ON" : "OFF").withStyle(on ? ChatFormatting.GREEN : ChatFormatting.RED));
    }

    private static void addToggle(ScreenEvent.Init.Post event, int x, int y, int w, String name, int which) {
        Button button = Button.builder(label(name, State.get(which)), b -> {
            boolean next = !State.get(which);
            State.set(which, next);                       // show the change immediately
            b.setMessage(label(name, next));
            Net.CHANNEL.sendToServer(new Net.ToggleMsg(which, next)); // server applies it, saves it, tells everyone
        }).bounds(x, y, w, 20).build();
        event.addListener(button);
    }

    @SubscribeEvent
    public static void onPauseInit(ScreenEvent.Init.Post event) {
        if (!(event.getScreen() instanceof PauseScreen pause)) {
            return;
        }
        int w = 124;
        int x = Math.max(pause.width / 2 + 108, pause.width - w - 8);
        int y = pause.height / 4 + 24;

        StringWidget title = new StringWidget(x, y - 18, w, 14,
                Component.literal("Random").withStyle(ChatFormatting.BOLD), Minecraft.getInstance().font);
        event.addListener(title);

        addToggle(event, x, y, w, "Random Blocks", 0);
        addToggle(event, x, y + 24, w, "Random Mobs", 1);
        addToggle(event, x, y + 48, w, "Random Random", 2);
    }
}
