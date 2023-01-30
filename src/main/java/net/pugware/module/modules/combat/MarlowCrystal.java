package net.pugware.module.modules.combat;

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
import net.pugware.Pugware;
import net.pugware.event.EventManager;
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
        EventManager eventManager = Pugware.INSTANCE.getEventManager();
        eventManager.remove(PlayerTickListener.class, this);
        eventManager.remove(ItemUseListener.class, this);
    }

    @Override
    public void ItemUseListener(ItemUseEvent event) {

    }

    private boolean isDeadBodyNearby() {
        return MC.world.getPlayers().parallelStream()
                .filter(e -> MC.player != e)
                .filter(e -> e.squaredDistanceTo(MC.player) < 36)
                .anyMatch(LivingEntity::isDead);
    }

    @Override
    public void onItemUse(ItemUseEvent event) {
        ItemStack mainHandStack = MC.player.getMainHandStack();
        if (MC.crosshairTarget.getType() == HitResult.Type.BLOCK) {
            BlockHitResult hit = (BlockHitResult) MC.crosshairTarget;
            if (mainHandStack.isOf(Items.END_CRYSTAL) && BlockUtils2.isBlock(Blocks.OBSIDIAN, hit.getBlockPos()))
                event.cancel();
        }
    }

    @Override
    public void onPlayerTick() {
        boolean dontBreakCrystal = crystalBreakClock != 0;
        if (dontBreakCrystal)
            crystalBreakClock--;
        if (activateOnRightClick.get() && GLFW.glfwGetMouseButton(MC.getWindow().getHandle(), GLFW.GLFW_MOUSE_BUTTON_2) != GLFW.GLFW_PRESS)
            return;
        ItemStack mainHandStack = MC.player.getMainHandStack();
        if (!mainHandStack.isOf(Items.END_CRYSTAL))
            return;
        if (stopOnKill.get() && isDeadBodyNearby())
            return;
        Vec3d camPos = MC.player.getEyePos();
        BlockHitResult blockHit = MC.world.raycast(new RaycastContext(camPos, camPos.add(RotationUtils.getClientLookVec().multiply(4.5)), RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.NONE, MC.player));
        if (MC.crosshairTarget instanceof EntityHitResult hit) {
            if (!dontBreakCrystal && hit.getEntity() instanceof EndCrystalEntity crystal) {
                crystalBreakClock = breakInterval.get();
                MC.interactionManager.attackEntity(MC.player, crystal);
                MC.player.swingHand(Hand.MAIN_HAND);
                Pugware.INSTANCE.getCrystalDataTracker().recordAttack(crystal);
            }
        }
        if (BlockUtils2.isBlock(Blocks.OBSIDIAN, blockHit.getBlockPos())) {
            if (CrystalUtils.canPlaceCrystalServer(blockHit.getBlockPos())) {
                ActionResult result = MC.interactionManager.interactBlock(MC.player, Hand.MAIN_HAND, blockHit);
                if (result.isAccepted() && result.shouldSwingHand())
                    MC.player.swingHand(Hand.MAIN_HAND);
            }
        }
    }
}