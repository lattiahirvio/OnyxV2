package net.onyx.module.modules.combat;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.onyx.Onyx;
import net.onyx.event.events.PlayerTickListener;
import net.onyx.module.Category;
import net.onyx.module.Module;
import net.onyx.module.setting.BooleanSetting;
import net.onyx.module.setting.IntegerSetting;
import net.onyx.util.AnchorUtil;
import net.onyx.util.InventoryUtils;
import org.lwjgl.glfw.GLFW;

public class AnchorMacroRe extends Module implements PlayerTickListener
{
    private final IntegerSetting ExplodeSlot;
    private final BooleanSetting chargeOnly;
    private final IntegerSetting Cooldown;
    private boolean hasAnchored;
    private int clock;
    
    public AnchorMacroRe() {
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
        AnchorMacroRe.eventManager.add(PlayerTickListener.class, this);
        this.clock = 0;
        this.hasAnchored = false;
    }
    
    @Override
    public void onDisable() {
        super.onDisable();
        AnchorMacroRe.eventManager.remove(PlayerTickListener.class, this);
    }

    @Override
    public void onPlayerTick() {
        if (GLFW.glfwGetMouseButton(Onyx.mc.getWindow().getHandle(), 1) != 1) {
            return;
        }
        if (Onyx.mc.player.isUsingItem()) {
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
        final HitResult cr = Onyx.mc.crosshairTarget;
        if (cr instanceof BlockHitResult) {
            final BlockHitResult hit = (BlockHitResult)cr;
            final BlockPos pos = hit.getBlockPos();
            if (AnchorUtil.isAnchorUncharged(pos)) {
                if (Onyx.mc.player.isHolding(Items.GLOWSTONE)) {
                    final ActionResult actionResult = Onyx.mc.interactionManager.interactBlock(Onyx.mc.player, Hand.MAIN_HAND, hit);
                    if (actionResult.isAccepted() && actionResult.CONSUME.shouldSwingHand()) {
                        Onyx.mc.player.swingHand(Hand.MAIN_HAND);
                    }
                    return;
                }
                InventoryUtils.selectItemFromHotbar(Items.GLOWSTONE);
                final ActionResult actionResult = Onyx.mc.interactionManager.interactBlock(Onyx.mc.player, Hand.MAIN_HAND, hit);
                if (actionResult.isAccepted() && actionResult.CONSUME.shouldSwingHand()) {
                    Onyx.mc.player.swingHand(Hand.MAIN_HAND);
                }
            }
            else if (AnchorUtil.isAnchorCharged(pos) && !this.chargeOnly.get()) {
                final PlayerInventory inv = AnchorMacroRe.mc.player.getInventory();
                inv.selectedSlot = this.ExplodeSlot.get();
                final ActionResult actionResult2 = Onyx.mc.interactionManager.interactBlock(Onyx.mc.player, Hand.MAIN_HAND, hit);
                if (actionResult2.isAccepted() && actionResult2.CONSUME.shouldSwingHand()) {
                    Onyx.mc.player.swingHand(Hand.MAIN_HAND);
                }
                this.hasAnchored = true;
            }
        }
    }
}
