package net.onyx.mixin;

import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Formatting;
import net.onyx.Onyx;
import net.onyx.module.modules.render.UpsideDownPlayers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LivingEntityRenderer.class)
public class LivingEntityRendererMixin {
    @Redirect(method = "setupTransforms", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/LivingEntityRenderer;shouldFlipUpsideDown(Lnet/minecraft/entity/LivingEntity;)Z"))
    public boolean forceFlipUpsideDownTransForms(LivingEntity entity) {
        if (Onyx.INSTANCE.getModuleManager().getModule(UpsideDownPlayers.class).isEnabled()) {
            return true;
        }
        if (entity instanceof PlayerEntity || entity.hasCustomName()) {
            String string = Formatting.strip(entity.getName().getString());
            if ("Dinnerbone".equals(string) || "Grumm".equals(string)) {
                return !(entity instanceof PlayerEntity) || ((PlayerEntity) entity).isPartVisible(PlayerModelPart.CAPE);
            }
        }
        return false;
    }

    @Redirect(method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/LivingEntityRenderer;shouldFlipUpsideDown(Lnet/minecraft/entity/LivingEntity;)Z"))
    public boolean forceFlipUpsideDown(LivingEntity entity) {
        if (Onyx.INSTANCE.getModuleManager().getModule(UpsideDownPlayers.class).isEnabled()) {
            return true;
        }
        if (entity instanceof PlayerEntity || entity.hasCustomName()) {
            String string = Formatting.strip(entity.getName().getString());
            if ("Dinnerbone".equals(string) || "Grumm".equals(string)) {
                return !(entity instanceof PlayerEntity) || ((PlayerEntity) entity).isPartVisible(PlayerModelPart.CAPE);
            }
        }
        return false;
    }
}
