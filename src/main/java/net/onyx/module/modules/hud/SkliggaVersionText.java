package net.onyx.module.modules.hud;

import net.minecraft.client.util.math.MatrixStack;
import net.onyx.event.events.ItemUseListener;
import net.onyx.event.events.RenderHudListener;
import net.onyx.module.Category;
import net.onyx.module.Module;

import static net.onyx.onyx.mc;

public class SkliggaVersionText extends Module implements RenderHudListener
{

    public SkliggaVersionText()
    {
        super("OnyxText", "useless really it is", false, Category.HUD);
    }

    @Override
    public void onEnable()
    {
        super.onEnable();
        eventManager.add(RenderHudListener.class, this);
    }

    @Override
    public void onDisable()
    {
        super.onDisable();
        eventManager.remove(RenderHudListener.class, this);
    }

    @Override
    public void ItemUseListener(ItemUseListener.ItemUseEvent event) {

    }

    @Override
    public void onRenderHud(MatrixStack matrices, double partialTicks)
    {
        matrices.push();
        matrices.translate(10, 60, 0);
        mc.textRenderer.drawWithShadow(matrices, "Onyx.ss", 0, 0, 0x351c75);
        matrices.pop();
    }
}
