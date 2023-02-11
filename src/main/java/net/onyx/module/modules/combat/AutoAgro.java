package net.onyx.module.modules.combat;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.Items;
import net.onyx.event.Listener;
import net.onyx.event.events.BlockPlaceListener;
import net.onyx.module.Category;
import net.onyx.module.Module;
import net.onyx.util.InventoryUtils;

public class AutoAgro extends Module implements BlockPlaceListener {
    public AutoAgro() {
        super("AutoAgro", "Idk", false, Category.COMBAT);
    }

    @Override
    public void onBlockPlace(BlockState state) {
        if(state.getBlock() == Blocks.RESPAWN_ANCHOR) {
            InventoryUtils.selectItemFromHotbar(Items.GLOWSTONE);
        }
    }
}
