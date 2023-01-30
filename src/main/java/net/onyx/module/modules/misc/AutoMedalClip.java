package net.onyx.module.modules.misc;

import net.minecraft.entity.LivingEntity;
import net.onyx.event.events.ItemUseListener;
import net.onyx.event.events.PlayerTickListener;
import net.onyx.keybind.Keybind;
import net.onyx.module.Category;
import net.onyx.module.Module;
import net.onyx.module.setting.IntegerSetting;
import net.onyx.module.setting.KeybindSetting;
import org.lwjgl.glfw.GLFW;

import java.awt.*;

import static net.onyx.onyx.mc;


public class AutoMedalClip extends Module implements PlayerTickListener {
    private final Keybind activateKeybind = new Keybind(
            "AutoMedalClip_medalKeybind",
            GLFW.GLFW_KEY_C,
            false,
            false,
            null
    );

    private KeybindSetting medalKeybind = new KeybindSetting.Builder()
            .setName("medalKeybind")
            .setDescription("the key to activate it")
            .setModule(this)
            .setValue(activateKeybind)
            .build();

    private IntegerSetting clipDelay = new IntegerSetting.Builder()
            .setName("clipDelayInSeconds")
            .setDescription("delay in seconds to clip after kill")
            .setModule(this)
            .setValue(0)
            .setMin(0)
            .setMax(7)
            .setAvailability(() -> true)
            .build();

    private int clipCooldown = 0;
    private int clipDelayTimer = 0;
    private boolean isClipQueued = false;


    public AutoMedalClip() {
        super("AutoMedalClip", "Automatically clips kills for you.", false, Category.MISC);
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

    private boolean isDeadBodyNearby() {
        return mc.world.getPlayers().parallelStream()

                .filter(e -> mc.player != e)
                .filter(e -> e.squaredDistanceTo(mc.player) < 72)
                .filter(e -> e.getArmor() > 10)
                .anyMatch(LivingEntity::isDead);
    }

    @Override
    public void onPlayerTick() {
        if (clipCooldown > 0) {
            clipCooldown--;
        }
        if (isDeadBodyNearby() && clipCooldown == 0) {
            isClipQueued = true;
            clipDelayTimer = clipDelay.get() * 20;
        }
        if (isClipQueued) {
            try {
                if (clipDelayTimer == 0) {
                    Robot robot = new Robot();
                    robot.keyPress(activateKeybind.getKey());
                    robot.keyRelease(activateKeybind.getKey());
                    clipCooldown = 50;
                    isClipQueued = false;
                } else {
                    clipDelayTimer--;
                }
            } catch (AWTException e) {
                e.printStackTrace();
            }
        }
    }
}
