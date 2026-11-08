package com.example.randomloot;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.Optional;
import java.util.function.Supplier;

/** Tiny network layer: the menu asks the server to flip a switch, the server tells everyone the new state. */
public final class Net {

    private static final String PROTOCOL = "1";

    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(RandomLootMod.MOD_ID, "main"),
            () -> PROTOCOL, v -> true, v -> true);

    private Net() {
    }

    public static void register() {
        CHANNEL.registerMessage(0, ToggleMsg.class, ToggleMsg::encode, ToggleMsg::decode, ToggleMsg::handle,
                Optional.of(NetworkDirection.PLAY_TO_SERVER));
        CHANNEL.registerMessage(1, SyncMsg.class, SyncMsg::encode, SyncMsg::decode, SyncMsg::handle,
                Optional.of(NetworkDirection.PLAY_TO_CLIENT));
    }

    public static void broadcast() {
        CHANNEL.send(PacketDistributor.ALL.noArg(), new SyncMsg(State.blocks, State.mobs, State.chaos));
    }

    public static void sendTo(ServerPlayer player) {
        CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new SyncMsg(State.blocks, State.mobs, State.chaos));
    }

    /** Client -> server: "please set switch #which to value". */
    public record ToggleMsg(int which, boolean value) {
        void encode(FriendlyByteBuf buf) {
            buf.writeByte(which);
            buf.writeBoolean(value);
        }

        static ToggleMsg decode(FriendlyByteBuf buf) {
            return new ToggleMsg(buf.readByte(), buf.readBoolean());
        }

        void handle(Supplier<NetworkEvent.Context> ctxSupplier) {
            NetworkEvent.Context ctx = ctxSupplier.get();
            ctx.enqueueWork(() -> {
                ServerPlayer sender = ctx.getSender();
                if (sender == null) {
                    return;
                }
                boolean allowed = sender.hasPermissions(2)
                        || sender.server.isSingleplayerOwner(sender.getGameProfile());
                if (!allowed) {
                    sender.sendSystemMessage(Component.literal("Random Loot: only operators can change these settings."));
                    sendTo(sender);
                    return;
                }
                if (which < 0 || which > 2) {
                    return;
                }
                State.set(which, value);
                State.saveToConfig();
                broadcast();
            });
            ctx.setPacketHandled(true);
        }
    }

    /** Server -> client: the current state of all three switches. */
    public record SyncMsg(boolean blocks, boolean mobs, boolean chaos) {
        void encode(FriendlyByteBuf buf) {
            buf.writeBoolean(blocks);
            buf.writeBoolean(mobs);
            buf.writeBoolean(chaos);
        }

        static SyncMsg decode(FriendlyByteBuf buf) {
            return new SyncMsg(buf.readBoolean(), buf.readBoolean(), buf.readBoolean());
        }

        void handle(Supplier<NetworkEvent.Context> ctxSupplier) {
            NetworkEvent.Context ctx = ctxSupplier.get();
            ctx.enqueueWork(() -> {
                State.blocks = blocks;
                State.mobs = mobs;
                State.chaos = chaos;
            });
            ctx.setPacketHandled(true);
        }
    }
}
