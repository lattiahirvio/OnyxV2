package net.onyx.module.modules.hud;

import net.onyx.Onyx;
import net.onyx.event.events.ItemUseListener;
import net.onyx.module.Module;
import net.onyx.module.Category;

public class SelfDestruct extends Module {

    public SelfDestruct() {
        super("SelfDestruct", "SelfDestruct", false, Category.HUD);
    }


    @Override
    public void onEnable() {
        Onyx.INSTANCE.onDestruct();
    }

}

