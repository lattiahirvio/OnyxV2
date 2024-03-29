package net.onyx.module.modules.combat;

import net.minecraft.block.Blocks;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.item.Items;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.onyx.event.events.PlayerTickListener;
import net.onyx.module.Category;
import net.onyx.module.Module;
import net.onyx.util.BlockUtils2;
import net.onyx.module.setting.BooleanSetting;
import net.onyx.module.setting.DecimalSetting;
import net.onyx.util.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class AutoDoubleHand extends Module implements PlayerTickListener {

    private final BooleanSetting checkPlayersAround = BooleanSetting.Builder.newInstance()
            .setName("checkPlayersAround")
            .setDescription("if on, AutoDoubleHand will only activate when players are around")
            .setModule(this)
            .setValue(false)
            .setAvailability(() -> true)
            .build();

    private final DecimalSetting distance = DecimalSetting.Builder.newInstance()
            .setName("distance")
            .setDescription("the distance for your enemy to activate")
            .setModule(this)
            .setValue(6)
            .setMin(2)
            .setMax(10)
            .setStep(0.1)
            .setAvailability(checkPlayersAround::get)
            .build();

    private final BooleanSetting predictCrystals = BooleanSetting.Builder.newInstance()
            .setName("predictCrystals")
            .setDescription("whether or not to predict crystal placements")
            .setModule(this)
            .setValue(false)
            .setAvailability(() -> true)
            .build();

    private final BooleanSetting checkEnemiesAim = BooleanSetting.Builder.newInstance()
            .setName("checkEnemiesAim")
            .setDescription("when enabled, crystal prediction will only activate when someone is pointing at an obsidian")
            .setModule(this)
            .setValue(false)
            .setAvailability(predictCrystals::get)
            .build();

    private final BooleanSetting checkHoldingItems = BooleanSetting.Builder.newInstance()
            .setName("checkHoldingItems")
            .setDescription("when enabled, crystal prediction will only activate when someone is pointing at an obsidian with crystals out")
            .setModule(this)
            .setValue(false)
            .setAvailability(() -> predictCrystals.get() && checkEnemiesAim.get())
            .build();

    private final DecimalSetting activatesAbove = DecimalSetting.Builder.newInstance()
            .setName("activatesAbove")
            .setDescription("AutoDoubleHand will only activate when you are above this height, set to 0 to disable")
            .setModule(this)
            .setValue(0.5)
            .setMin(0)
            .setMax(4)
            .setStep(0.1)
            .setAvailability(() -> true)
            .build();

    public AutoDoubleHand() {
        super("AutoDoubleHand", "Automatically double hand when you appear to be in a predicament", false, Category.COMBAT);
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

    private List<EndCrystalEntity> getNearByCrystals() {
        Vec3d pos = mc.player.getPos();
        return mc.world.getEntitiesByClass(EndCrystalEntity.class, new Box(pos.add(-6, -6, -6), pos.add(6, 6, 6)), a -> true);
    }

    @Override
    public void onPlayerTick() {
        double distanceSq = distance.get() * distance.get();
        if (checkPlayersAround.get() && mc.world.getPlayers().parallelStream()
                .filter(e -> e != mc.player)
                .noneMatch(player -> mc.player.squaredDistanceTo(player) <= distanceSq))
            return;

        double activatesAboveV = activatesAbove.get();
        int f = (int) Math.floor(activatesAboveV);
        for (int i = 1; i <= f; i++)
            if (BlockUtils2.hasBlock(mc.player.getBlockPos().add(0, -i, 0)))
                return;
        if (BlockUtils2.hasBlock(new BlockPos(mc.player.getPos().add(0, -activatesAboveV, 0))))
            return;

        List<EndCrystalEntity> crystals = getNearByCrystals();
        ArrayList<Vec3d> crystalsPos = new ArrayList<>();
        crystals.forEach(e -> crystalsPos.add(e.getPos()));

        if (predictCrystals.get()) {
            Stream<BlockPos> stream =
                    BlockUtils2.getAllInBoxStream(mc.player.getBlockPos().add(-6, -8, -6), mc.player.getBlockPos().add(6, 2, 6))
                            .filter(e -> BlockUtils2.isBlock(Blocks.OBSIDIAN, e) || BlockUtils2.isBlock(Blocks.BEDROCK, e))
                            .filter(CrystalUtils::canPlaceCrystalClient);
            if (checkEnemiesAim.get()) {
                if (checkHoldingItems.get())
                    stream = stream.filter(this::arePeopleAimingAtBlockAndHoldingCrystals);
                else
                    stream = stream.filter(this::arePeopleAimingAtBlock);
            }
            stream.forEachOrdered(e -> crystalsPos.add(Vec3d.ofBottomCenter(e).add(0, 1, 0)));
        }

        for (Vec3d pos : crystalsPos) {
            double damage =
                    DamageUtils.crystalDamage(mc.player, pos, true, null, false);
            if (damage >= mc.player.getHealth() + mc.player.getAbsorptionAmount()) {
                InventoryUtils.selectItemFromHotbar(Items.TOTEM_OF_UNDYING);
                break;
            }
        }
    }

    private boolean arePeopleAimingAtBlock(BlockPos block) {
        return mc.world.getPlayers().parallelStream()
                .filter(e -> e != mc.player)
                .anyMatch(e ->
                {
                    Vec3d eyesPos = RotationUtils.getEyesPos(e);
                    BlockHitResult hitResult = mc.world.raycast(new RaycastContext(eyesPos, eyesPos.add(RotationUtils.getPlayerLookVec(e).multiply(4.5)), RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, e));
                    return hitResult != null && hitResult.getBlockPos().equals(block);
                });
    }

    private boolean arePeopleAimingAtBlockAndHoldingCrystals(BlockPos block) {
        return mc.world.getPlayers().parallelStream()
                .filter(e -> e != mc.player)
                .filter(e -> e.isHolding(Items.END_CRYSTAL))
                .anyMatch(e ->
                {
                    Vec3d eyesPos = RotationUtils.getEyesPos(e);
                    BlockHitResult hitResult = mc.world.raycast(new RaycastContext(eyesPos, eyesPos.add(RotationUtils.getPlayerLookVec(e).multiply(4.5)), RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, e));
                    return hitResult != null && hitResult.getBlockPos().equals(block);
                });
    }
}
