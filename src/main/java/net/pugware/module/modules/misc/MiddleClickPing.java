package net.pugware.module.modules.misc;

import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.pugware.event.events.ItemUseListener;
import net.pugware.event.events.PlayerTickListener;
import net.pugware.module.Category;
import net.pugware.module.Module;
import net.pugware.module.setting.BooleanSetting;
import org.lwjgl.glfw.GLFW;

import static net.pugware.Pugware.MC;
import static net.pugware.util.ChatUtils.plainMessageWithPrefix;
import static net.pugware.util.ChatUtils.sendPlainMessage;

public class MiddleClickPing extends Module implements PlayerTickListener {
    private final BooleanSetting includePrefix = BooleanSetting.Builder.newInstance()
            .setName("includePrefix")
            .setDescription("whether or not to include the prefix in the ping message")
            .setModule(this)
            .setValue(true)
            .setAvailability(() -> true)
            .build();
    private boolean isMiddleClicking = false;

    public MiddleClickPing() {
        super("MiddleClickPing", "Middle Click a player to get their ping.", false, Category.MISC);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        eventManager.add(PlayerTickListener.class, this);
    }

    @Override
    public void onDisable() {
        super.onDisable();
        eventManager.remove(PlayerTickListener.class, this);
    }

    @Override
    public void ItemUseListener(ItemUseListener.ItemUseEvent event) {

    }

    @Override
    public void onPlayerTick() {


        HitResult hit = MC.crosshairTarget;
        if (hit.getType() != HitResult.Type.ENTITY)
            return;
        Entity target = ((EntityHitResult) hit).getEntity();
        if (!(target instanceof PlayerEntity))
            return;
        if (GLFW.glfwGetMouseButton(MC.getWindow().getHandle(), GLFW.GLFW_MOUSE_BUTTON_3) == GLFW.GLFW_PRESS && !isMiddleClicking) {
            isMiddleClicking = true;
            if (includePrefix.get()) {
                plainMessageWithPrefix(target.getEntityName() + "'s ping is " + getPing(target));
            } else {
                sendPlainMessage(target.getEntityName() + "'s ping is " + getPing(target));
            }
        }
        if (GLFW.glfwGetMouseButton(MC.getWindow().getHandle(), GLFW.GLFW_MOUSE_BUTTON_3) == GLFW.GLFW_RELEASE && isMiddleClicking) {
            isMiddleClicking = false;
        }
    }

    public static int getPing(Entity player) {
        if (mc.getNetworkHandler() == null) return 0;

        PlayerListEntry playerListEntry = mc.getNetworkHandler().getPlayerListEntry(player.getUuid());
        if (playerListEntry == null) return 0;
        return playerListEntry.getLatency();
    }
}
