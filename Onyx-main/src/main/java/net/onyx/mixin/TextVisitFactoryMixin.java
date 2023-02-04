package net.onyx.mixin;

import net.minecraft.client.font.TextVisitFactory;
import net.onyx.Onyx;
import net.onyx.module.modules.misc.NameProtect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(TextVisitFactory.class)
public class TextVisitFactoryMixin {
    @ModifyArg(at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/font/TextVisitFactory;visitFormatted(Ljava/lang/String;ILnet/minecraft/text/Style;Lnet/minecraft/text/Style;Lnet/minecraft/text/CharacterVisitor;)Z",
            ordinal = 0),
            method = {
                    "visitFormatted(Ljava/lang/String;ILnet/minecraft/text/Style;Lnet/minecraft/text/CharacterVisitor;)Z"},
            index = 0)
    private static String adjustText(String text) {
        if (Onyx.INSTANCE.getModuleManager().getModules() != null) {
            return NameProtect.replaceName(text);
        } else return text;
    }
}