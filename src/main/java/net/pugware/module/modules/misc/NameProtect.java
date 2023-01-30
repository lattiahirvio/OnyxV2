package net.pugware.module.modules.misc;

import net.pugware.event.events.ItemUseListener;
import net.pugware.module.Category;
import net.pugware.module.Module;

import static net.pugware.Pugware.MC;

public class NameProtect extends Module {
    private static String replacementUsername = "LilNotMaster";
    private static boolean isEnabledStatic = false;


    public NameProtect() {
        super("NameProtect", "Hides your name in chat.", false, Category.MISC);
    }

    @Override
    public void onEnable() {
        isEnabledStatic = true;
    }

    @Override
    public void onDisable() {
        isEnabledStatic = false;
    }

    @Override
    public void ItemUseListener(ItemUseListener.ItemUseEvent event) {

    }

    public static int setReplacementUsername(String newReplacementUsername) {
        replacementUsername = newReplacementUsername;
        return 0;
    }

    public static String replaceName(String string) {
        if (string != null && isEnabledStatic) {
            return string.replace(MC.getSession().getUsername(), replacementUsername);
        }
        return string;
    }
}

