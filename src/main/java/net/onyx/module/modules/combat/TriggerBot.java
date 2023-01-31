package net.onyx.module.modules.combat;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.SwordItem;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.onyx.event.events.PlayerTickListener;
import net.onyx.module.Category;
import net.onyx.module.Module;
import net.onyx.module.setting.BooleanSetting;
import net.onyx.module.setting.DecimalSetting;

public class TriggerBot extends Module implements PlayerTickListener {

    private final DecimalSetting cooldown = DecimalSetting.Builder.newInstance()
            .setName("cooldown")
            .setDescription("the cooldown threshold")
            .setModule(this)
            .setValue(1)
            .setMin(0)
            .setMax(1)
            .setStep(0.1)
            .setAvailability(() -> true)
            .build();

    private final BooleanSetting attackInAir = BooleanSetting.Builder.newInstance()
            .setName("attackInAir")
            .setDescription("whether or not to attack in mid air")
            .setModule(this)
            .setValue(true)
            .setAvailability(() -> true)
            .build();

    private final BooleanSetting attackOnJump = BooleanSetting.Builder.newInstance()
            .setName("attackOnJump")
            .setDescription("whether or not to attack when going upwards")
            .setModule(this)
            .setValue(true)
            .setAvailability(attackInAir::get)
            .build();

    public TriggerBot() {
        super("TriggerBot", "automatically attacks players for you", false, Category.COMBAT);
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
    public void onPlayerTick() {
        if (mc.player.isUsingItem())
            return;
        if (!(mc.player.getMainHandStack().getItem() instanceof SwordItem))
            return;
        HitResult hit = mc.crosshairTarget;
        if (hit.getType() != HitResult.Type.ENTITY)
            return;
        if (mc.player.getAttackCooldownProgress(0) < cooldown.get())
            return;
        Entity target = ((EntityHitResult) hit).getEntity();
        if (!(target instanceof PlayerEntity))
            return;
        if (!target.isOnGround() && !attackInAir.get())
            return;
        if (mc.player.getY() > mc.player.prevY && !attackOnJump.get())
            return;
        mc.interactionManager.attackEntity(mc.player, target);
        mc.player.swingHand(Hand.MAIN_HAND);
    }
}
