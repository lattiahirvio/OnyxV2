package net.onyx.mixin;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.onyx.event.EventManager;
import net.onyx.event.events.PlayerTickListener;
import net.onyx.event.events.SendChatMessageListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.onyx.onyx.mc;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin {
    @Inject(at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/network/AbstractClientPlayerEntity;tick()V",
            ordinal = 0), method = "tick()V")
    private void onTick(CallbackInfo ci) {
        EventManager.fire(new PlayerTickListener.PlayerTickEvent());
    }

}
