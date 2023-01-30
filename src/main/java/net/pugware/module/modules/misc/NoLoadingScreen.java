package net.pugware.module.modules.misc;

import net.pugware.event.events.ItemUseListener;
import net.pugware.module.Category;
import net.pugware.module.Module;

public class NoLoadingScreen extends Module {
    public NoLoadingScreen() {
        super("NoLoadingScreen", "restore pre 1.18.2 loading screen behavior", false, Category.MISC);
    }

    @Override
    public void ItemUseListener(ItemUseListener.ItemUseEvent event) {

    }
}
