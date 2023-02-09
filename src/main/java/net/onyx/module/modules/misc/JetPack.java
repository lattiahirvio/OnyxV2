package net.onyx.module.modules.misc;

import net.onyx.event.events.PlayerTickListener;
import net.onyx.module.Module;
import net.onyx.module.Category;
import net.onyx.util.BlockUtils;
import net.minecraft.util.math.BlockPos;

public class JetPack extends Module implements PlayerTickListener {
    public JetPack() {
        super("Jetpack", "jumpy ig", false, Category.MISC);
    }


    private final BlockPos.Mutable bp = new BlockPos.Mutable();
    @Override
    public void onEnable(){
        super.onEnable();
        eventManager.add(PlayerTickListener.class, this);
    }
    public void onDisable(){
        super.onDisable();
        eventManager.remove(PlayerTickListener.class, this);
    }

    @Override
    public void onPlayerTick() {

        if (mc.options.jumpKey.isPressed() && !mc.options.sneakKey.isPressed()) {
            mc.player.setVelocity(0, 0.42f, 0);
        }
        if (BlockUtils.placeBlock(bp)) {
            // Render block if was placed
            // Move player down so they are on top of the placed block ready to jump again
            if (mc.options.jumpKey.isPressed() && !mc.options.sneakKey.isPressed() && !mc.player.isOnGround() && !mc.world.getBlockState(bp).isAir()) {
                mc.player.setVelocity(0, -0.28f, 0);
            }
        }


    }
}
