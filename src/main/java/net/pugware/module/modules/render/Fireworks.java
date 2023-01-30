package net.pugware.module.modules.render;

import net.minecraft.entity.LivingEntity;
import net.pugware.event.events.ItemUseListener;
import net.pugware.event.events.PlayerTickListener;
import net.pugware.module.Category;
import net.pugware.module.Module;
import net.pugware.module.setting.IntegerSetting;

import static net.pugware.Pugware.MC;


public class Fireworks extends Module implements PlayerTickListener {
    private IntegerSetting rColorSetting = new IntegerSetting.Builder()
            .setName("clipDelayInSeconds")
            .setDescription("delay in seconds to clip after kill")
            .setModule(this)
            .setValue(0)
            .setMin(0)
            .setMax(255)
            .setAvailability(() -> true)
            .build();

    private IntegerSetting gColorSetting = new IntegerSetting.Builder()
            .setName("clipDelayInSeconds")
            .setDescription("delay in seconds to clip after kill")
            .setModule(this)
            .setValue(131)
            .setMin(0)
            .setMax(255)
            .setAvailability(() -> true)
            .build();

    private IntegerSetting bColorSetting = new IntegerSetting.Builder()
            .setName("clipDelayInSeconds")
            .setDescription("delay in seconds to clip after kill")
            .setModule(this)
            .setValue(255)
            .setMin(0)
            .setMax(255)
            .setAvailability(() -> true)
            .build();



    public Fireworks() {
        super("Fireworks", "Spawns a firework when you kill someone.", false, Category.MISC);
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

    private boolean isPlayerKilled() {
        return MC.world.getPlayers().parallelStream()

                .filter(e -> MC.player != e)
                .filter(e -> e.deathTime==1)
                .anyMatch(LivingEntity::isDead);
    }

    @Override
    public void onPlayerTick() {
        if (isPlayerKilled()) {

        }

    }
}
