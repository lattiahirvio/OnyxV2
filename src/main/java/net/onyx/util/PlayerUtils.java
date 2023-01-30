package net.onyx.util;

import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.GameMode;

import static net.onyx.onyx.mc;


public enum PlayerUtils {
    ;

    public static GameMode getGameMode(PlayerEntity player) {
        PlayerListEntry playerListEntry = mc.getNetworkHandler().getPlayerListEntry(player.getUuid());
        if (playerListEntry == null) return GameMode.SPECTATOR;
        return playerListEntry.getGameMode();
    }

    public static int getPing(Entity player) {
        if (mc.getNetworkHandler() == null) return 0;
        PlayerListEntry playerListEntry = mc.getNetworkHandler().getPlayerListEntry(player.getUuid());
        if (playerListEntry == null) return 0;
        return playerListEntry.getLatency();
    }
}

