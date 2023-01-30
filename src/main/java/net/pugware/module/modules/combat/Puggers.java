/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Blocks
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.decoration.EndCrystalEntity
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.Items
 *  net.minecraft.util.ActionResult
 *  net.minecraft.util.Hand
 *  net.minecraft.util.hit.BlockHitResult
 *  net.minecraft.util.hit.EntityHitResult
 *  net.minecraft.util.hit.HitResult
 *  net.minecraft.util.hit.HitResult$Type
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.world.RaycastContext
 *  net.minecraft.world.RaycastContext$FluidHandling
 *  net.minecraft.world.RaycastContext$ShapeType
 *  org.lwjgl.glfw.GLFW
 */
package net.pugware.module.modules.combat;

import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.pugware.Pugware;
import net.pugware.event.events.ItemUseListener;
import net.pugware.event.events.PlayerTickListener;
import net.pugware.module.Category;
import net.pugware.module.Module;
import net.pugware.module.setting.BlockUtils2;
import net.pugware.module.setting.BooleanSetting;
import net.pugware.module.setting.IntegerSetting;
import net.pugware.util.CrystalUtils;
import net.pugware.util.RotationUtils;
import org.lwjgl.glfw.GLFW;

import static net.pugware.Pugware.MC;

public class Puggers
extends Module
implements PlayerTickListener, ItemUseListener {
    private int CrystalCounter = 0;
    private int CrystalCounterClock = 0;
    private int CrystalOnceClock = 0;
    private int CrystalTwiceClock = 0;
    private final IntegerSetting breakInterval = IntegerSetting.Builder.newInstance().setName("Crystal Speed").setDescription("the interval between breaking crystals (in tick)").setModule(this).setValue(0).setMin(0).setMax(20).setAvailability(() -> true).build();
    private final IntegerSetting Cooldown = IntegerSetting.Builder.newInstance().setName("Cooldown").setDescription("Cooldown after Crystalling twice").setModule(this).setValue(20).setMin(1).setMax(200).setAvailability(() -> true).build();
    private final BooleanSetting activateOnRightClick = BooleanSetting.Builder.newInstance().setName("Use Right Click").setDescription("will only activate on right click when enabled").setModule(this).setValue(true).setAvailability(() -> true).build();
    private final IntegerSetting BreakTwice = IntegerSetting.Builder.newInstance().setName("BreakTwice").setDescription("Breaking the crystal twice").setModule(this).setValue(20).setMin(1).setMax(50).setAvailability(() -> true).build();
    private final BooleanSetting stopOnKill = BooleanSetting.Builder.newInstance().setName("Anti Loot Blow Up").setDescription("automatically stops crystalling when someone close to you dies").setModule(this).setValue(true).setAvailability(() -> true).build();
    private int crystalBreakClock = 0;

    public Puggers() {
        super("MarlowCrystalReWrite", "MarlowCrystalReWrite is a rewrite of marlowcrystal", false, Category.COMBAT);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        eventManager.add(PlayerTickListener.class, this);
        eventManager.add(ItemUseListener.class, this);
        this.crystalBreakClock = 0;
        this.CrystalCounter = 0;
        this.CrystalCounterClock = 0;
        this.CrystalOnceClock = 0;
        this.CrystalTwiceClock = 0;
    }

    @Override
    public void onDisable() {
        super.onDisable();
        eventManager = Pugware.INSTANCE.getEventManager();
        eventManager.remove(PlayerTickListener.class, this);
        eventManager.remove(ItemUseListener.class, this);
    }

    private boolean isDeadBodyNearby() {
        return Pugware.MC.world.getPlayers().parallelStream().filter(e -> Pugware.MC.player != e).filter(e -> e.squaredDistanceTo((Entity)Pugware.MC.player) < 36.0).anyMatch(LivingEntity::isDead);
    }

    @Override
    public void ItemUseListener(ItemUseEvent event) {
        ItemStack mainHandStack = Pugware.MC.player.getMainHandStack();
        if (Pugware.MC.crosshairTarget.getType() == HitResult.Type.BLOCK) {
            BlockHitResult hit = (BlockHitResult)Pugware.MC.crosshairTarget;
            if (mainHandStack.isOf(Items.END_CRYSTAL) && BlockUtils2.isBlock(Blocks.OBSIDIAN, hit.getBlockPos())) {
                event.cancel();
            }
        }
    }

    @Override
    public void onPlayerTick() {
        boolean dontBreakCrystal;
        boolean bl = dontBreakCrystal = this.crystalBreakClock != 0;
        if (this.CrystalCounter == 3) {
            ++this.CrystalCounterClock;
            if (this.CrystalCounterClock == this.Cooldown.get()) {
                this.CrystalCounter = 0;
                this.CrystalCounterClock = 0;
                return;
            }
            return;
        }
        if (this.CrystalCounter == 1) {
            ++this.CrystalOnceClock;
            if (this.CrystalOnceClock == this.Cooldown.get()) {
                this.CrystalCounter = 0;
                this.CrystalOnceClock = 0;
                return;
            }
        }
        if (this.CrystalCounter == 2) {
            ++this.CrystalTwiceClock;
            if (this.CrystalTwiceClock == this.Cooldown.get()) {
                this.CrystalCounter = 0;
                this.CrystalTwiceClock = 0;
                return;
            }
        }
        if (dontBreakCrystal) {
            --this.crystalBreakClock;
        }
        if (this.activateOnRightClick.get().booleanValue() && GLFW.glfwGetMouseButton((long) MC.getWindow().getHandle(), (int)1) != 1) {
            return;
        }
        ItemStack mainHandStack = MC.player.getMainHandStack();
        if (!mainHandStack.isOf(Items.END_CRYSTAL)) {
            return;
        }
        if (this.stopOnKill.get().booleanValue() && this.isDeadBodyNearby()) {
            return;
        }
        Vec3d camPos = MC.player.getEyePos();
        BlockHitResult blockHit = MC.world.raycast(new RaycastContext(camPos, camPos.add(RotationUtils.getClientLookVec().multiply(4.5)), RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.NONE, (Entity) MC.player));
        HitResult hitResult = MC.crosshairTarget;
        if (hitResult instanceof EntityHitResult) {
            Entity entity;
            EntityHitResult hit = (EntityHitResult)hitResult;
            if (!dontBreakCrystal && (entity = hit.getEntity()) instanceof EndCrystalEntity) {
                EndCrystalEntity crystal = (EndCrystalEntity)entity;
                this.crystalBreakClock = this.breakInterval.get();
                MC.interactionManager.attackEntity((PlayerEntity) MC.player, (Entity)crystal);
                MC.player.swingHand(Hand.MAIN_HAND);
                Pugware.INSTANCE.getCrystalDataTracker().recordAttack((Entity)crystal);
            }
        }
        if (BlockUtils2.isBlock(Blocks.OBSIDIAN, blockHit.getBlockPos()) && CrystalUtils.canPlaceCrystalServer(blockHit.getBlockPos())) {
            ActionResult result = MC.interactionManager.interactBlock(MC.player, Hand.MAIN_HAND, blockHit);
            ++this.CrystalCounter;
            if (result.isAccepted() && result.shouldSwingHand()) {
                MC.player.swingHand(Hand.MAIN_HAND);
            }
        }
    }

        @Override
        public void onItemUse(ItemUseEvent event) {

        }

}


