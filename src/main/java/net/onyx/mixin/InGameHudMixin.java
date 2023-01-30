package net.onyx.mixin;

import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import net.onyx.event.EventManager;
import net.onyx.event.events.RenderHudListener;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.onyx.onyx.mc;

@Mixin(InGameHud.class)
public class InGameHudMixin {
    @Inject(
            at = {@At(value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/systems/RenderSystem;enableBlend()V",
                    ordinal = 4)},
            method = {"render(Lnet/minecraft/client/util/math/MatrixStack;F)V"})
    private void onRender(MatrixStack matrixStack, float partialTicks,
                          CallbackInfo ci) {
        if (mc.options.debugEnabled)
            return;

        boolean blend = GL11.glGetBoolean(GL11.GL_BLEND);

        RenderHudListener.RenderHudEvent event = new RenderHudListener.RenderHudEvent(matrixStack, partialTicks);
        EventManager.fire(event);

        if (blend)
            GL11.glEnable(GL11.GL_BLEND);
        else
            GL11.glDisable(GL11.GL_BLEND);
    }
}
