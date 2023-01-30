package net.onyx.module.modules.combat;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.onyx.onyx;
import net.onyx.event.events.ItemUseListener;
import net.onyx.event.events.PlayerTickListener;
import net.onyx.module.Category;
import net.onyx.module.Module;
import net.onyx.module.setting.BooleanSetting;
import net.onyx.module.setting.IntegerSetting;
import net.onyx.util.AcrBlkUtl;
import net.onyx.util.InventoryUtils;
import org.lwjgl.glfw.GLFW;

public class AA extends Module implements PlayerTickListener
{
    private final IntegerSetting ExplodeSlot;
    private final BooleanSetting chargeOnly;
    private final IntegerSetting Cooldown;
    private boolean hasAnchored;
    private int clock;
    
    public AA() {
        super("AnchorMacroRewrite", "Automatically explodes Anchors you place", false, Category.COMBAT);
        this.ExplodeSlot = new IntegerSetting.Builder().setName("Exploding Slot").setDescription("which slot to switch to when exploding Anchors").setModule(this).setValue(0).setMin(0).setMax(8).setAvailability(() -> true).build();
        this.chargeOnly = BooleanSetting.Builder.newInstance().setName("Charge Only").setDescription("if on, It wont explode Anchors but just charge them").setModule(this).setValue(false).setAvailability(() -> true).build();
        this.Cooldown = new IntegerSetting.Builder().setName("Cooldown").setDescription("Cooldown").setModule(this).setValue(4).setMin(0).setMax(10).setAvailability(() -> true).build();
        this.hasAnchored = false;
        this.clock = 0;
    }
    
    @Override
    public void onEnable() {
        super.onEnable();
        AA.eventManager.add(PlayerTickListener.class, this);
        this.clock = 0;
        this.hasAnchored = false;
    }
    
    @Override
    public void onDisable() {
        super.onDisable();
        AA.eventManager.remove(PlayerTickListener.class, this);
    }

    @Override
    public void ItemUseListener(ItemUseListener.ItemUseEvent event) {

    }

    @Override
    public void onPlayerTick() {
        if (GLFW.glfwGetMouseButton(onyx.mc.getWindow().getHandle(), 1) != 1) {
            return;
        }
        if (onyx.mc.player.isUsingItem()) {
            return;
        }
        if (this.hasAnchored) {
            if (this.clock != 0) {
                --this.clock;
                return;
            }
            this.clock = this.Cooldown.get();
            this.hasAnchored = false;
        }
        final HitResult cr = onyx.mc.crosshairTarget;
        if (cr instanceof BlockHitResult) {
            final BlockHitResult hit = (BlockHitResult)cr;
            final BlockPos pos = hit.getBlockPos();
            if (AcrBlkUtl.isAnchorUncharged(pos)) {
                if (onyx.mc.player.isHolding(Items.GLOWSTONE)) {
                    final ActionResult actionResult = onyx.mc.interactionManager.interactBlock(onyx.mc.player, Hand.MAIN_HAND, hit);
                    if (actionResult.isAccepted() && actionResult.CONSUME.shouldSwingHand()) {
                        onyx.mc.player.swingHand(Hand.MAIN_HAND);
                    }
                    return;
                }
                InventoryUtils.selectItemFromHotbar(Items.GLOWSTONE);
                final ActionResult actionResult = onyx.mc.interactionManager.interactBlock(onyx.mc.player, Hand.MAIN_HAND, hit);
                if (actionResult.isAccepted() && actionResult.CONSUME.shouldSwingHand()) {
                    onyx.mc.player.swingHand(Hand.MAIN_HAND);
                }
            }
            else if (AcrBlkUtl.isAnchorCharged(pos) && !this.chargeOnly.get()) {
                final PlayerInventory inv = AA.mc.player.getInventory();
                inv.selectedSlot = this.ExplodeSlot.get();
                final ActionResult actionResult2 = onyx.mc.interactionManager.interactBlock(onyx.mc.player, Hand.MAIN_HAND, hit);
                if (actionResult2.isAccepted() && actionResult2.CONSUME.shouldSwingHand()) {
                    onyx.mc.player.swingHand(Hand.MAIN_HAND);
                }
                this.hasAnchored = true;
            }
        }
    }
}
