// 
// Decompiled by Procyon v0.5.36
// 

package net.onyx.module.modules.combat;


import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.SwordItem;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.onyx.onyx;
import net.onyx.event.events.ItemUseListener;
import net.onyx.event.events.PlayerTickListener;
import net.onyx.module.Category;
import net.onyx.module.Module;
import net.onyx.module.setting.BooleanSetting;
import net.onyx.module.setting.DecimalSetting;
import org.lwjgl.glfw.GLFW;

import java.util.Objects;

public class TB extends Module implements PlayerTickListener
{
    private final DecimalSetting cooldown;
    private final BooleanSetting activateOnLeftClick;
    private final BooleanSetting attackInAir;
    private final BooleanSetting attackOnJump;
    
    public TB() {
        super("TriggerBot2", "automatically attacks players for you", false, Category.COMBAT);
        this.cooldown = DecimalSetting.Builder.newInstance().setName("Cooldown").setDescription("the cooldown threshold").setModule(this).setValue(1.0).setMin(0.0).setMax(1.0).setStep(0.1).setAvailability(() -> true).build();
        this.activateOnLeftClick = BooleanSetting.Builder.newInstance().setName("Use Left Click").setDescription("will only activate on right click when enabled").setModule(this).setValue(false).setAvailability(() -> true).build();
        this.attackInAir = BooleanSetting.Builder.newInstance().setName("Attack In Air").setDescription("whether or not to attack in mid air").setModule(this).setValue(true).setAvailability(() -> true).build();
        final BooleanSetting.Builder setValue = BooleanSetting.Builder.newInstance().setName("Attack On Jump").setDescription("whether or not to attack when going upwards").setModule(this).setValue(true);
        final BooleanSetting attackInAir = this.attackInAir;
        Objects.requireNonNull(attackInAir);
        this.attackOnJump = setValue.setAvailability(attackInAir::get).build();
    }
    
    @Override
    public void onEnable() {
        super.onEnable();
        TB.eventManager.add(PlayerTickListener.class, this);
    }
    
    @Override
    public void onDisable() {
        super.onDisable();
        TB.eventManager.remove(PlayerTickListener.class, this);
    }

    @Override
    public void ItemUseListener(ItemUseListener.ItemUseEvent event) {

    }

    @Override
    public void onPlayerTick() {
        if (onyx.mc.player.isUsingItem()) {
            return;
        }
        if (this.activateOnLeftClick.get() && GLFW.glfwGetMouseButton(onyx.mc.getWindow().getHandle(), 0) != 1) {
            return;
        }
        if (!(onyx.mc.player.getMainHandStack().getItem() instanceof SwordItem)) {
            return;
        }
        HitResult hit = onyx.mc.crosshairTarget;
        if (hit.getType() !=HitResult.Type.ENTITY) {
            return;
        }
        if (onyx.mc.player.getAttackCooldownProgress(0.0f) < this.cooldown.get()) {
            return;
        }
        final Entity target = ((EntityHitResult)hit).getEntity();
        if (!(target instanceof PlayerEntity)) {
            return;
        }
        if (!target.isOnGround() && !this.attackInAir.get()) {
            return;
        }
        if (onyx.mc.player.getY() > onyx.mc.player.prevY && !this.attackOnJump.get()) {
            return;
        }
        onyx.mc.interactionManager.attackEntity((PlayerEntity) onyx.mc.player, target);
        onyx.mc.player.swingHand(Hand.MAIN_HAND);
    }
}
