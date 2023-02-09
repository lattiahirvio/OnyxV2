package net.onyx.module.modules.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.*;
import net.minecraft.world.RaycastContext;
import net.onyx.Onyx;
import net.onyx.core.Rotation;
import net.onyx.core.Rotator;
import net.onyx.event.events.AttackEntityListener;
import net.onyx.event.events.GameRenderListener;
import net.onyx.event.events.PlayerTickListener;
import net.onyx.module.Category;
import net.onyx.module.Module;
import net.onyx.util.*;
import org.lwjgl.opengl.GL11;

import java.util.Comparator;
import java.util.stream.Stream;

public class PlacementHighlight extends Module implements PlayerTickListener, AttackEntityListener, GameRenderListener {


    private int renderClock;
    private int placeObiClock;
    private BlockPos highlight;
    private Vec3d targetPredictedPos;

    public PlacementHighlight() {
        super("PlacementHighlight", "", false, Category.RENDER);
        this.renderClock = 0;
        this.placeObiClock = -1;
    }

    @Override
    public void onEnable() {
        super.onEnable();
        this.renderClock = 0;
        PlacementHighlight.eventManager.add(PlayerTickListener.class, this);
        PlacementHighlight.eventManager.add(AttackEntityListener.class, this);
        PlacementHighlight.eventManager.add(GameRenderListener.class, this);
    }

    @Override
    public void onDisable() {
        super.onDisable();
        PlacementHighlight.eventManager.remove(PlayerTickListener.class, this);
        PlacementHighlight.eventManager.remove(AttackEntityListener.class, this);
        PlacementHighlight.eventManager.remove(GameRenderListener.class, this);
    }

    @Override
    public void onPlayerTick() {
        if (this.renderClock > 0) {
            --this.renderClock;
        }
        if (this.placeObiClock == 0) {
            this.placeObiClock = -1;
            if (this.highlight != null) {
                InventoryUtils.selectItemFromHotbar(Items.OBSIDIAN);
                BlockUtils.placeBlock(this.highlight);
            }
        }
        else {
            --this.placeObiClock;
        }
    }

    @Override
    public void onAttackEntity(final AttackEntityEvent event) {
        if (mc.currentScreen != null)
            return;
        if (!(event.getTarget() instanceof PlayerEntity)) {
            return;
        }
        final PlayerEntity target = (PlayerEntity)event.getTarget();
        if (Onyx.mc.player.isTouchingWater() || Onyx.mc.player.isInLava()) {
            return;
        }
        if (!target.isOnGround()) {
            return;
        }
        final int placeCrystalAfter = 4;
        final int breakCrystalAfter = 8;
        final int placeObiAfter = 2;
        final Vec3d targetKB = this.calcTargetKB(target);
        final int floorY = Onyx.mc.player.getBlockY() - 1;
        final Vec3d targetPos = target.getPos();
        final Vec3d targetPosAtPlaceCrystal = this.simulatePos(targetPos, targetKB, placeCrystalAfter);
        final Vec3d targetPosAtBreakCrystal = this.simulatePos(targetPos, targetKB, breakCrystalAfter);
        final Vec3d targetPosAtPlaceObi = this.simulatePos(targetPos, targetKB, placeObiAfter);
        final Box targetBoxAtPlaceObi = target.getBoundingBox().offset(targetPosAtPlaceObi.subtract(target.getPos()));
        final Box targetBoxAtPlaceCrystal = target.getBoundingBox().offset(targetPosAtPlaceCrystal.subtract(target.getPos()));
        final BlockPos blockPos = Onyx.mc.player.getBlockPos();
        final Stream<BlockPos> blocks = BlockUtils.getAllInBoxStream(blockPos.add(-4, 0, -4), blockPos.add(4, 0, 4));
        final BlockPos placement = blocks.filter(b -> !BlockUtils.hasBlock(b)).filter(b -> BlockUtils.hasBlock(b.add(0, -1, 0))).filter(b -> !Box.of(Vec3d.ofCenter(b), 1.0, 1.0, 1.0).intersects(targetBoxAtPlaceObi)).filter(b -> {
            final Vec3d startP = RenderUtils.getCameraPos();
            final Vec3d endP = Vec3d.ofBottomCenter(b);
            if (endP.subtract(startP).lengthSquared() > 16.0) {
                return false;
            }
            else {
                final BlockHitResult result = Onyx.mc.world.raycast(new RaycastContext(RenderUtils.getCameraPos(), endP, RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.NONE, Onyx.mc.player));
                return result.getType() == HitResult.Type.MISS;
            }
        }).filter(b -> CrystalUtils.canPlaceCrystalClientAssumeObsidian(b, targetBoxAtPlaceCrystal)).max(Comparator.comparingDouble(b -> DamageUtils.crystalDamage(target, targetPosAtBreakCrystal, Vec3d.ofCenter(b, 1.0), b, false))).orElse(null);
        if (placement == null) {
            return;
        }
        final Rotator rotator = Rotator.INSTANCE;
        rotator.stepToward(Vec3d.ofBottomCenter(placement), placeObiAfter, () -> {
            InventoryUtils.selectItemFromHotbar(Items.OBSIDIAN);
            final BlockPos neighbor = placement.add(0, -1, 0);
            final Direction direction = Direction.UP;
            final Vec3d center = Vec3d.ofCenter(neighbor).add(Vec3d.of(direction.getVector()).multiply(0.5));
            final ActionResult result2 = Onyx.mc.interactionManager.interactBlock(Onyx.mc.player, Hand.MAIN_HAND, new BlockHitResult(center, Direction.UP, placement.add(0, -1, 0), false));
            if (result2 == ActionResult.SUCCESS) {
                Onyx.mc.player.swingHand(Hand.MAIN_HAND);
                rotator.stepToward(new Rotation(0.0f, true, Onyx.mc.player.getPitch() - 15.0f, false), 5, () -> InventoryUtils.selectItemFromHotbar(Items.END_CRYSTAL));
            }
            return;
        });
        this.placeObiClock = placeObiAfter;
        this.highlight = placement;
        this.targetPredictedPos = targetPosAtBreakCrystal;
        this.renderClock = 40;
    }

    private Vec3d simulatePos(Vec3d start, Vec3d velocity, final int ticks) {
        for (int i = 0; i < ticks; ++i) {
            double j = velocity.getX();
            double k = velocity.getY();
            double l = velocity.getZ();
            if (Math.abs(j) < 0.003) {
                j = 0.0;
            }
            if (Math.abs(k) < 0.003) {
                k = 0.0;
            }
            if (Math.abs(l) < 0.003) {
                l = 0.0;
            }
            velocity = new Vec3d(j, k, l);
            double g = 0.0;
            g -= 0.08;
            velocity = velocity.add(0.0, g * 0.98, 0.0);
            velocity = velocity.multiply(0.91, 1.0, 0.91);
            start = start.add(velocity);
        }
        return start;
    }

    private Vec3d calcTargetKB(final LivingEntity target) {
        final float h = Onyx.mc.player.getAttackCooldownProgress(0.5f);
        int i = EnchantmentHelper.getKnockback(Onyx.mc.player);
        if (Onyx.mc.player.isSprinting() && h > 0.9) {
            ++i;
        }
        double strength = i * 0.5f;
        final double x = MathHelper.sin(Onyx.mc.player.getYaw() * 0.017453292f);
        final double z = -MathHelper.cos(Onyx.mc.player.getYaw() * 0.017453292f);
        final Iterable<ItemStack> armors = target.getArmorItems();
        double kbRes = 0.0;
        for (final ItemStack e : armors) {
            final Item item = e.getItem();
            if (!(item instanceof ArmorItem)) {
                continue;
            }
            final ArmorItem armorItem = (ArmorItem)item;
            if (armorItem.getMaterial() != ArmorMaterials.NETHERITE) {
                continue;
            }
            kbRes += 0.1;
        }
        strength *= 1.0 - target.getAttributeValue(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE) - kbRes;
        Vec3d result = Vec3d.ZERO;
        if (strength > 0.0) {
            final Vec3d vec3d = new Vec3d(target.getX() - target.prevX, target.getY() - target.prevY, target.getZ() - target.prevZ);
            final Vec3d vec3d2 = new Vec3d(x, 0.0, z).normalize().multiply(strength);
            result = new Vec3d(vec3d.x / 2.0 - vec3d2.x, target.isOnGround() ? Math.min(0.4, vec3d.y / 2.0 + strength) : vec3d.y, vec3d.z / 2.0 - vec3d2.z);
        }
        return result;
    }

    @Override
    public void onGameRender(final MatrixStack matrixStack, final float tickDelta) {
        if (this.renderClock == 0 || this.highlight == null) {
            return;
        }
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(2848);
        GL11.glEnable(2884);
        GL11.glDisable(2929);
        RenderSystem.setShader(GameRenderer::getPositionShader);
        RenderSystem.setShaderColor(0.4f, 1.0f, 0.4f, 0.4f);
        matrixStack.push();
        RenderUtils.applyRegionalRenderOffset(matrixStack);
        final BlockPos blockPos = RenderUtils.getCameraBlockPos();
        final int regionX = (blockPos.getX() >> 9) * 512;
        final int regionZ = (blockPos.getZ() >> 9) * 512;
        matrixStack.push();
        matrixStack.translate(this.highlight.getX() - regionX, this.highlight.getY(), this.highlight.getZ() - regionZ);
        RenderUtils.drawSolidBox(new Box(BlockPos.ORIGIN), matrixStack);
        matrixStack.pop();
        matrixStack.push();
        matrixStack.translate(this.targetPredictedPos.getX() - regionX, this.targetPredictedPos.getY(), this.targetPredictedPos.getZ() - regionZ);
        RenderUtils.drawSolidBox(Onyx.mc.player.getBoundingBox().offset(Onyx.mc.player.getPos().multiply(-1.0)), matrixStack);
        matrixStack.pop();
        matrixStack.pop();
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glEnable(2929);
        GL11.glDisable(3042);
        GL11.glDisable(2848);
    }
}
