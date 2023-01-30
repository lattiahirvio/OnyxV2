package net.pugware.module.modules.render;

import net.pugware.event.events.ItemUseListener;
import net.pugware.module.Category;
import net.pugware.module.Module;

public class nameTagPing extends Module {


    public nameTagPing() {
        super("NameTagPing", "Show ping in a player's name tag.", false, Category.RENDER);
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