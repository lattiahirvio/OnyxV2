package net.onyx.module.modules.misc;

import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.onyx.event.events.ItemUseListener;
import net.onyx.event.events.PlayerTickListener;
import net.onyx.module.Category;
import net.onyx.module.Module;
import net.onyx.module.setting.BooleanSetting;
import org.lwjgl.glfw.GLFW;

import static net.onyx.onyx.mc;
import static net.onyx.util.ChatUtils.plainMessageWithPrefix;
import static net.onyx.util.ChatUtils.sendPlainMessage;

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


        HitResult hit = mc.crosshairTarget;
        if (hit.getType() != HitResult.Type.ENTITY)
            return;
        Entity target = ((EntityHitResult) hit).getEntity();
        if (!(target instanceof PlayerEntity))
            return;
        if (GLFW.glfwGetMouseButton(mc.getWindow().getHandle(), GLFW.GLFW_MOUSE_BUTTON_3) == GLFW.GLFW_PRESS && !isMiddleClicking) {
            isMiddleClicking = true;
            if (includePrefix.get()) {
                plainMessageWithPrefix(target.getEntityName() + "'s ping is " + getPing(target));
            } else {
                sendPlainMessage(target.getEntityName() + "'s ping is " + getPing(target));
            }
        }
        if (GLFW.glfwGetMouseButton(mc.getWindow().getHandle(), GLFW.GLFW_MOUSE_BUTTON_3) == GLFW.GLFW_RELEASE && isMiddleClicking) {
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
