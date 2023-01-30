package net.pugware.module.modules.render;

import net.pugware.event.events.ItemUseListener;
import net.pugware.module.Category;
import net.pugware.module.Module;

public class UpsideDownPlayers extends Module {


    public UpsideDownPlayers() {
        super("UpsideDownPlayers", "Render everyone upside-down!", false, Category.RENDER);
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @Override
    public void ItemUseListener(ItemUseListener.ItemUseEvent event) {
        
    }
}