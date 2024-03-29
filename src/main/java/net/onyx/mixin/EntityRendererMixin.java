package net.onyx.mixin;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Matrix4f;
import net.onyx.Onyx;
import net.onyx.module.modules.render.nameTagPing;
import net.onyx.util.PlayerUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;


@Mixin(EntityRenderer.class)
public class EntityRendererMixin {

    @Redirect(method = "renderLabelIfPresent", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;draw(Lnet/minecraft/text/Text;FFIZLnet/minecraft/util/math/Matrix4f;Lnet/minecraft/client/render/VertexConsumerProvider;ZII)I"))
    private int shouldDisplayPingInName(TextRenderer instance, Text text, float x, float y, int color, boolean shadow, Matrix4f matrix, VertexConsumerProvider vertexConsumers, boolean seeThrough, int backgroundColor, int light, Entity entity) {
        if (entity.isPlayer() && Onyx.INSTANCE.getModuleManager().getModule(nameTagPing.class).isEnabled()) {
            text = Text.of(text.getString() + " (" + PlayerUtils.getPing(entity) + "ms)");
            x -= 15;
        }
        instance.draw(text, x, (float) y, color, false, matrix, vertexConsumers, seeThrough, backgroundColor, light);
        return color;
    }
}