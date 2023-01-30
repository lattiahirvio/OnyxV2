package net.pugware.module.modules.combat;

import net.pugware.event.events.ItemUseListener;
import net.pugware.event.events.PlayerTickListener;
import net.pugware.event.events.UpdateListener;
import net.pugware.module.Category;
import net.pugware.module.Module;
import net.pugware.module.setting.BlockUtils2;
import net.pugware.util.BlockUtils;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;

import static net.pugware.Pugware.MC;

public class MarlowAnchor extends Module implements PlayerTickListener
{
    public MarlowAnchor() {
        super("MarlowAnchor", "Anchor like marlow :3", false, Category.COMBAT);
    }

    @Override
    public void onEnable()
    {
        eventManager.add(PlayerTickListener.class, this);
    }

    @Override
    public void onDisable()
    {
        eventManager.remove(PlayerTickListener.class, this);
    }

    @Override
    public void ItemUseListener(ItemUseListener.ItemUseEvent event) {

    }

    @Override
    public void onPlayerTick()
    {
        if (MC.crosshairTarget instanceof BlockHitResult hit)
        {
            BlockPos pos = hit.getBlockPos();
            if (BlockUtils2.isAnchorCharged(pos))
            {
                if (!MC.player.isHolding(Items.GLOWSTONE))
                {
                    ActionResult actionResult = MC.interactionManager.interactBlock(MC.player, Hand.MAIN_HAND, hit);
                    if (actionResult.isAccepted() && actionResult.shouldSwingHand())
                        MC.player.swingHand(Hand.MAIN_HAND);
                }
            }
        }
    }
}
