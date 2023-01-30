package net.onyx.module.modules.combat;

import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.onyx.onyx;
import net.onyx.event.EventManager;
import net.onyx.event.events.ItemUseListener;
import net.onyx.event.events.PlayerTickListener;
import net.onyx.module.Category;
import net.onyx.module.Module;
import net.onyx.module.setting.BlockUtils2;
import net.onyx.module.setting.BooleanSetting;
import net.onyx.module.setting.IntegerSetting;
import net.onyx.util.CrystalUtils;
import net.onyx.util.RotationUtils;
import org.lwjgl.glfw.GLFW;

import static net.onyx.onyx.mc;

public class MarlowCrystal extends Module implements PlayerTickListener, ItemUseListener {

    private final IntegerSetting breakInterval = IntegerSetting.Builder.newInstance()
            .setName("breakInterval")
            .setDescription("the interval between breaking crystals (in tick)")
            .setModule(this)
            .setValue(0)
            .setMin(0)
            .setMax(20)
            .setAvailability(() -> true)
            .build();

    private final BooleanSetting activateOnRightClick = BooleanSetting.Builder.newInstance()
            .setName("activateOnRightClick")
            .setDescription("will only activate on right click when enabled")
            .setModule(this)
            .setValue(false)
            .setAvailability(() -> true)
            .build();

    private final BooleanSetting stopOnKill = BooleanSetting.Builder.newInstance()
            .setName("stopOnKill")
            .setDescription("automatically stops crystalling when someone close to you dies")
            .setModule(this)
            .setValue(false)
            .setAvailability(() -> true)
            .build();

    public MarlowCrystal() {
        super("MarlowCrystal", "crystal like Marlowww!", false, Category.COMBAT);
    }

    private int crystalBreakClock = 0;

    @Override
    public void onEnable() {
        super.onEnable();
        eventManager.add(PlayerTickListener.class, this);
        eventManager.add(ItemUseListener.class, this);
        crystalBreakClock = 0;
    }

    @Override
    public void onDisable() {
        super.onDisable();
        EventManager eventManager = onyx.INSTANCE.getEventManager();
        eventManager.remove(PlayerTickListener.class, this);
        eventManager.remove(ItemUseListener.class, this);
    }

    @Override
    public void ItemUseListener(ItemUseEvent event) {

    }

    private boolean isDeadBodyNearby() {
        return mc.world.getPlayers().parallelStream()
                .filter(e -> mc.player != e)
                .filter(e -> e.squaredDistanceTo(mc.player) < 36)
                .anyMatch(LivingEntity::isDead);
    }

    @Override
    public void onItemUse(ItemUseEvent event) {
        ItemStack mainHandStack = mc.player.getMainHandStack();
        if (mc.crosshairTarget.getType() == HitResult.Type.BLOCK) {
            BlockHitResult hit = (BlockHitResult) mc.crosshairTarget;
            if (mainHandStack.isOf(Items.END_CRYSTAL) && BlockUtils2.isBlock(Blocks.OBSIDIAN, hit.getBlockPos()))
                event.cancel();
        }
    }

    @Override
    public void onPlayerTick() {
        boolean dontBreakCrystal = crystalBreakClock != 0;
        if (dontBreakCrystal)
            crystalBreakClock--;
        if (activateOnRightClick.get() && GLFW.glfwGetMouseButton(mc.getWindow().getHandle(), GLFW.GLFW_MOUSE_BUTTON_2) != GLFW.GLFW_PRESS)
            return;
        ItemStack mainHandStack = mc.player.getMainHandStack();
        if (!mainHandStack.isOf(Items.END_CRYSTAL))
            return;
        if (stopOnKill.get() && isDeadBodyNearby())
            return;
        Vec3d camPos = mc.player.getEyePos();
        BlockHitResult blockHit = mc.world.raycast(new RaycastContext(camPos, camPos.add(RotationUtils.getClientLookVec().multiply(4.5)), RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.NONE, mc.player));
        if (mc.crosshairTarget instanceof EntityHitResult hit) {
            if (!dontBreakCrystal && hit.getEntity() instanceof EndCrystalEntity crystal) {
                crystalBreakClock = breakInterval.get();
                mc.interactionManager.attackEntity(mc.player, crystal);
                mc.player.swingHand(Hand.MAIN_HAND);
                onyx.INSTANCE.getCrystalDataTracker().recordAttack(crystal);
            }
        }
        if (BlockUtils2.isBlock(Blocks.OBSIDIAN, blockHit.getBlockPos())) {
            if (CrystalUtils.canPlaceCrystalServer(blockHit.getBlockPos())) {
                ActionResult result = mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, blockHit);
                if (result.isAccepted() && result.shouldSwingHand())
                    mc.player.swingHand(Hand.MAIN_HAND);
            }
        }
    }
}