package net.pugware.module.modules.hud;

import net.pugware.Pugware;
import net.pugware.event.events.ItemUseListener;
import net.pugware.module.Module;
import net.pugware.module.Category;

public class SelfDestruct extends Module {

    public SelfDestruct() {
        super("SelfDestruct", "SelfDestruct", false, Category.HUD);
    }


    @Override
    public void onEnable() {
        Pugware.INSTANCE.onDestruct();
    }

    @Override
    public void ItemUseListener(ItemUseListener.ItemUseEvent event) {

    }
}

