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
import net.minecraft.util.math.BlockPos;
import net.pugware.Pugware;
import net.pugware.event.EventManager;
import net.pugware.event.events.ItemUseListener;
import net.pugware.event.events.PlayerTickListener;
import net.pugware.module.Category;
import net.pugware.module.Module;
import net.pugware.module.setting.BooleanSetting;
import net.pugware.module.setting.IntegerSetting;
import net.pugware.util.BlockUtils;
import net.pugware.util.CrystalUtils;
import org.lwjgl.glfw.GLFW;

import static net.pugware.Pugware.MC;


public class AutoDtap extends Module implements PlayerTickListener, ItemUseListener
{

    private final IntegerSetting placeInterval = IntegerSetting.Builder.newInstance()
            .setName("placeInterval")
            .setDescription("the interval between placing crystals (in tick)")
            .setModule(this)
            .setValue(4)
            .setMin(0)
            .setMax(20)
            .setAvailability(() -> true)
            .build();
    private final IntegerSetting MaxCrystals = IntegerSetting.Builder.newInstance()
            .setName("MaxCrystals")
            .setDescription("the interval between breaking crystals (in tick)")
            .setModule(this)
            .setValue(2)
            .setMin(1)
            .setMax(2)
            .setAvailability(() -> false)
            .build();

    private final IntegerSetting breakInterval = IntegerSetting.Builder.newInstance()
            .setName("breakInterval")
            .setDescription("the interval between breaking crystals (in tick)")
            .setModule(this)
            .setValue(3)
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

    private int crystalPlaceClock = 0;
    private int crystalBreakClock = 0;

    public AutoDtap()
    {
        super("AutoDtap", "Double pop like commanderducko", false, Category.COMBAT);
    }

    @Override
    public void onEnable()
    {
        super.onEnable();
        eventManager.add(PlayerTickListener.class, this);
        eventManager.add(ItemUseListener.class, this);
        crystalPlaceClock = 0;
        crystalBreakClock = 0;
    }

    @Override
    public void onDisable()
    {
        super.onDisable();
        EventManager eventManager = Pugware.INSTANCE.getEventManager();
        eventManager.remove(PlayerTickListener.class, this);
        eventManager.remove(ItemUseListener.class, this);
    }

    @Override
    public void ItemUseListener(ItemUseEvent event) {

    }

    private boolean isDeadBodyNearby()
    {
        return MC.world.getPlayers().parallelStream()
                .filter(e -> MC.player != e)
                .filter(e -> e.squaredDistanceTo(MC.player) < 36)
                .anyMatch(LivingEntity::isDead);
    }

    @Override
    public void onPlayerTick()
    {
        boolean dontPlaceCrystal = crystalPlaceClock != 0;
        boolean dontBreakCrystal = crystalBreakClock != 0;
        if (dontPlaceCrystal)
            crystalPlaceClock--;
        if (dontBreakCrystal)
            crystalBreakClock--;
        if (activateOnRightClick.get() && GLFW.glfwGetMouseButton(MC.getWindow().getHandle(), GLFW.GLFW_MOUSE_BUTTON_2) != GLFW.GLFW_PRESS)
            return;
        ItemStack mainHandStack = MC.player.getMainHandStack();
        if (!mainHandStack.isOf(Items.END_CRYSTAL))
            return;
        if (stopOnKill.get() && isDeadBodyNearby())
            return;

        if (MC.crosshairTarget instanceof EntityHitResult hit)
        {
            if (!dontBreakCrystal && hit.getEntity() instanceof EndCrystalEntity crystal)
            {
                crystalBreakClock = breakInterval.get();
                MC.interactionManager.attackEntity(MC.player, crystal);
                MC.player.swingHand(Hand.MAIN_HAND);
                Pugware.INSTANCE.getCrystalDataTracker().recordAttack(crystal);
            }
        }
        if (MC.crosshairTarget instanceof BlockHitResult hit)
        {
            BlockPos block = hit.getBlockPos();
            if (!dontPlaceCrystal && CrystalUtils.canPlaceCrystalServer(block))
            {
                crystalPlaceClock = placeInterval.get();
                ActionResult result = MC.interactionManager.interactBlock(MC.player, Hand.MAIN_HAND, hit);
                if (result.isAccepted() && result.shouldSwingHand())
                    MC.player.swingHand(Hand.MAIN_HAND);
            }
        }
    }

    @Override
    public void onItemUse(ItemUseEvent event)
    {
        ItemStack mainHandStack = MC.player.getMainHandStack();
        if (MC.crosshairTarget.getType() == HitResult.Type.BLOCK)
        {
            BlockHitResult hit = (BlockHitResult) MC.crosshairTarget;
            if (mainHandStack.isOf(Items.END_CRYSTAL) && BlockUtils.isBlock(Blocks.OBSIDIAN, hit.getBlockPos()))
                event.cancel();
        }
    }
}