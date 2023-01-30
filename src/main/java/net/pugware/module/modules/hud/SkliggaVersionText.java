package net.pugware.module.modules.hud;

import net.minecraft.client.util.math.MatrixStack;
import net.pugware.event.events.ItemUseListener;
import net.pugware.event.events.RenderHudListener;
import net.pugware.module.Category;
import net.pugware.module.Module;

import static net.pugware.Pugware.MC;

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
        MC.textRenderer.drawWithShadow(matrices, "Onyx.ss", 0, 0, 0x351c75);
        matrices.pop();
    }
}
